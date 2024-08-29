package com.wangyuanye.plugin.component;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.*;
import com.intellij.ui.scale.JBUIScale;
import com.intellij.ui.table.JBTable;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBTabsImpl;
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

    public TabInfo buildCommandTab(JBTabsImpl jbTabs, SchemaTab schemaTab) {
        // Column "name"
        TableColumn columnName = commandTable.getColumnModel().getColumn(0);
        JTableHeader tableHeader = commandTable.getTableHeader();
        FontMetrics headerFontMetrics = tableHeader.getFontMetrics(tableHeader.getFont());
        int nameColumnWidth = headerFontMetrics.stringWidth(commandTable.getColumnName(0) + JBUIScale.scale(20));
        columnName.setPreferredWidth(nameColumnWidth);
        columnName.setMinWidth(nameColumnWidth);

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
                        MyCmdDialog dialog = new MyCmdDialog(commandsPanel, myCmdAdd, -1, myCmdList);
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
        return new TabInfo(commandsPanel).setText(CommandTab.TAB_NAME);
    }

    private void editSelectedCommand() {
        stopEditing();
        int selectedIndex = commandTable.getSelectedRow();
        if (selectedIndex < 0 || selectedIndex >= cmdModel.getRowCount()) {
            return;
        }
        MyCmd sourceMyCmd = myCmdList.get(selectedIndex);
        MyCmd myCmdEdit = sourceMyCmd.clone();
        MyCmdDialog dialog = new MyCmdDialog(commandTable, myCmdEdit, selectedIndex, myCmdList);
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
