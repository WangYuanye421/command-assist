package com.wangyuanye.plugin.component;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.*;
import com.intellij.ui.scale.JBUIScale;
import com.intellij.ui.table.JBTable;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.TabInfo;
import com.wangyuanye.plugin.dao.CmdDataSave;
import com.wangyuanye.plugin.dao.SchemaDataSave;
import com.wangyuanye.plugin.dao.dto.MyCmd;
import com.wangyuanye.plugin.dao.dto.MySchema;
import com.wangyuanye.plugin.util.IdeaApiUtil;
import com.wangyuanye.plugin.util.MessagesUtil;
import com.wangyuanye.plugin.util.MyTableUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 插件窗口
 *
 * @author wangyuanye
 * @date 2024/8/20
 **/
public final class CommandTab implements Disposable {
    private static final Logger logger = Logger.getInstance(CommandTab.class);
    public static final String TAB_NAME = MessagesUtil.getMessage("cmd.tab.name");
    private MyCmdModel cmdModel;
    private List<MyCmd> myCmdList;
    private JBTable commandTable;
    // 加载数据
    private CmdDataSave cmdService;
    private SchemaDataSave schemaService;
    private MySchema defaultSchema;
    private List<MySchema> schemasFromFile;


    public CommandTab() {
        // 加载数据
        cmdService = IdeaApiUtil.getCmdService();
        schemaService = IdeaApiUtil.getSchemaService();
        defaultSchema = schemaService.getDefaultSchema();
        schemasFromFile = schemaService.list();
        myCmdList = cmdService.list(defaultSchema.getId());
        cmdModel = new MyCmdModel(myCmdList);
        commandTable = new JBTable(cmdModel);
        commandTable.setShowGrid(false);
        commandTable.setFocusable(false);
        commandTable.getTableHeader().setReorderingAllowed(false);// 禁止列拖动
        MyTableUtil.setEmptyText(commandTable);
    }

    public void refreshTable(String schemaId) {
        myCmdList = cmdService.list(schemaId);
        commandTable.removeAll();
        cmdModel = new MyCmdModel(myCmdList);
        commandTable.setModel(cmdModel);
    }

    public TabInfo buildCommandTab(JBTabs jbTabs, SchemaTab schemaTab) {

        // Column "name"
        TableColumn columnName = commandTable.getColumnModel().getColumn(0);
        JTableHeader tableHeader = commandTable.getTableHeader();
        FontMetrics headerFontMetrics = tableHeader.getFontMetrics(tableHeader.getFont());
        int nameColumnWidth = headerFontMetrics.stringWidth(commandTable.getColumnName(0) + JBUIScale.scale(20));
        columnName.setPreferredWidth(nameColumnWidth);
        columnName.setMinWidth(nameColumnWidth);
        // 命令行美化
//        columnName.setCellRenderer(new DefaultTableCellRenderer(){
//            @Override
//            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//
//                String cmd = (String) value;
//                JLabel c = new JLabel();
//                c.setText(beautyCmd(cmd));
//                return c;
//            }
//        });

        // Column "remark"
        TableColumn remark = commandTable.getColumnModel().getColumn(1);
        int remarkColumnWidth = headerFontMetrics.stringWidth(commandTable.getColumnName(1)) + JBUIScale.scale(20);
        remark.setPreferredWidth(remarkColumnWidth);
        remark.setMinWidth(remarkColumnWidth);

        JPanel commandsPanel = new JPanel(new BorderLayout());
        ActionRun actionRun = new ActionRun(commandTable);// 运行
        ActionSchemaComboBox schemasAction = new ActionSchemaComboBox(schemasFromFile, this);// 分类下拉框
        ActionManageSchema actionManageSchema = new ActionManageSchema(jbTabs, schemaTab, schemasAction);// 分类管理按钮
        schemasAction.onItemChange();
        commandsPanel.add(ToolbarDecorator.createDecorator(commandTable)
                .addExtraAction(schemasAction)
                .addExtraAction(actionManageSchema)
                .setAddAction(new AnActionButtonRunnable() {
                    @Override
                    public void run(AnActionButton button) {
                        stopEditing();
                        MySchema selectedItem = (MySchema) schemasAction.getComboBox().getSelectedItem();
                        if (selectedItem == null || selectedItem.getId() == null) {
                            IdeaApiUtil.myTips(MessagesUtil.getMessage("cmd.add.no_schema"));
                            return;
                        }
                        MyCmd myCmdAdd = new MyCmd(selectedItem.getId(), "", "");
                        DialogMyCmd dialog = new DialogMyCmd(commandTable, myCmdAdd, -1, myCmdList);
                        IdeaApiUtil.setRelatedLocation(dialog);
                        if (!dialog.showAndGet()) {
                            return;
                        }
                        logger.info("cmd add. cmd:" + myCmdAdd.toString());
                        myCmdList.add(myCmdAdd);
                        cmdService.addCmd(myCmdAdd);// db
                        int index = myCmdList.size() - 1;
                        cmdModel.fireTableRowsInserted(index, index);
                        commandTable.getSelectionModel().setSelectionInterval(index, index);
                        commandTable.scrollRectToVisible(commandTable.getCellRect(index, 0, true));
                    }
                })
                .setEditAction(new AnActionButtonRunnable() {
                    @Override
                    public void run(AnActionButton button) {
                        editSelectedCommand();
                    }
                })
                .setRemoveAction(new AnActionButtonRunnable() {
                    @Override
                    public void run(AnActionButton button) {
                        stopEditing();
                        int selectedIndex = commandTable.getSelectedRow();
                        if (selectedIndex < 0 || selectedIndex >= cmdModel.getRowCount()) {
                            return;
                        }
                        MyCmd myCmdToBeRemoved = myCmdList.get(selectedIndex);
                        logger.info("cmd remove. cmd:" + myCmdToBeRemoved.toString());
                        cmdService.deleteCmd(myCmdToBeRemoved.getCmdId());
                        TableUtil.removeSelectedItems(commandTable);
                    }
                })
                .addExtraAction(actionRun)
                .disableUpDownActions().createPanel(), BorderLayout.CENTER);

        // double-click in "Patterns" table should also start editing of selected pattern
        new DoubleClickListener() {
            @Override
            protected boolean onDoubleClick(@NotNull MouseEvent e) {
                editSelectedCommand();
                return true;
            }
        }.installOn(commandTable);
        //commandsPanel.setVisible(true);
        return new TabInfo(commandsPanel).setText(CommandTab.TAB_NAME);
    }

    private String beautyCmd(String cmd) {
        // lsof -i:{%Parm%}
        // 正则表达式来匹配 {%Parm%} 格式的占位符
        logger.info("cmd美化前: " + cmd);
        System.out.println("cmd美化前: " + cmd);
        Pattern pattern = Pattern.compile("\\{%([a-zA-Z0-9_]+)%\\}");
        Matcher matcher = pattern.matcher(cmd);
        int i = 0;
        while (matcher.find()) {
            // 获取占位符中的参数名
            String actual = "<i style='color:1C75CFFF;'>" + matcher.group(1) + "</i>";
            cmd = cmd.replace(matcher.group(0), actual);
        }
        cmd = cmd.replace("\\", "<br>");
        cmd = "<html>" + cmd + "</html>";
        logger.info("cmd美化后: " + cmd);
        System.out.println("cmd美化后: " + cmd);
        return cmd;
    }

    private void editSelectedCommand() {
        stopEditing();
        int selectedIndex = commandTable.getSelectedRow();
        if (selectedIndex < 0 || selectedIndex >= cmdModel.getRowCount()) {
            return;
        }
        MyCmd sourceMyCmd = myCmdList.get(selectedIndex);
        MyCmd myCmdEdit = sourceMyCmd.clone();
        DialogMyCmd dialog = new DialogMyCmd(commandTable, myCmdEdit, selectedIndex, myCmdList);
        IdeaApiUtil.setRelatedLocation(dialog);
        dialog.setTitle(MessagesUtil.getMessage("cmd.dialog.edit.title"));
        if (!dialog.showAndGet()) {
            return;
        }
        logger.info("cmd edit. cmd:" + myCmdEdit.toString());
        myCmdList.set(selectedIndex, myCmdEdit);
        cmdService.updateCmd(myCmdEdit);// db
        cmdModel.fireTableRowsUpdated(selectedIndex, selectedIndex);
        commandTable.getSelectionModel().setSelectionInterval(selectedIndex, selectedIndex);
    }

    protected void stopEditing() {
        if (commandTable.isEditing()) {
            TableCellEditor editor = commandTable.getCellEditor();
            if (editor != null) {
                editor.stopCellEditing();
            }
        }
        if (commandTable.isEditing()) {
            TableCellEditor editor = commandTable.getCellEditor();
            if (editor != null) {
                editor.stopCellEditing();
            }
        }
    }

    @Override
    public void dispose() {
        System.out.println("应用关闭，执行清理");
    }
}
