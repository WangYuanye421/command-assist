package com.wangyuanye.plugin.component;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionToolbarPosition;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.impl.ActionButton;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.DoubleClickListener;
import com.intellij.ui.ToolbarDecorator;
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
import javax.swing.table.DefaultTableCellRenderer;
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
 * 2024/8/20
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
    ActionButton actionAddBtn;
    ActionButton actionEditBtn;
    ActionButton actionDelBtn;
    ActionButton actionCopyBtn;
    ActionButton actionRunBtn;
    ActionAddCmd actionAddCmd;//新增
    ActionEditCmd actionEditCmd;// 修改
    ActionRemoveCmd actionRemoveCmd;// 删除
    ActionCopy actionCopy;// 复制
    ActionRun actionRun;// 运行
    List<ActionButton> buttonList;
    ActionSchemaComboBox schemaComboBox;
    ActionManageSchema actionManageSchema;


    public CommandTab() {
        // 加载数据
        cmdService = IdeaApiUtil.getCmdService();
        schemaService = IdeaApiUtil.getSchemaService();
        defaultSchema = schemaService.getDefaultSchema();
        schemasFromFile = schemaService.list();
        myCmdList = cmdService.list(defaultSchema.getId());
        cmdModel = new MyCmdModel(myCmdList);
        commandTable = new JBTable(cmdModel);
        tableConfig();
    }

    private void tableConfig() {
        commandTable.setShowGrid(false);
        commandTable.setFocusable(false);
        commandTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // 设置单行选中
        JTableHeader tableHeader = commandTable.getTableHeader();
        DefaultTableCellRenderer headRenderer = new DefaultTableCellRenderer();
        // 创建自定义渲染器，将内容居中
        headRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        tableHeader.setDefaultRenderer(headRenderer);
        tableHeader.setReorderingAllowed(false);// 禁止列拖动
        MyTableUtil.setEmptyText(commandTable);
    }

    public void refreshTable(String schemaId) {
        myCmdList = cmdService.list(schemaId);
        commandTable.removeAll();
        cmdModel = new MyCmdModel(myCmdList);
        commandTable.setModel(cmdModel);
        commandTable.getSelectionModel().clearSelection();

        actionAddCmd.reset(commandTable, cmdModel, myCmdList);
        actionEditCmd.reset(commandTable, cmdModel, myCmdList);
        actionRemoveCmd.reset(commandTable, cmdModel, myCmdList);

        // 按钮
        for (ActionButton button : buttonList) {
            button.setEnabled(false);
        }
        actionAddBtn.setEnabled(true);
    }

    public TabInfo buildCommandTab(JBTabs jbTabs, SchemaTab schemaTab) {
        // Column "name"
        TableColumn columnName = commandTable.getColumnModel().getColumn(0);
        columnName.setPreferredWidth(100);
        columnName.setMinWidth(100);
        columnName.setMaxWidth(500);
        columnName.setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    String text = value.toString();
                    // 限制文本长度，超过部分用省略号表示
                    if (text.length() > 30) {
                        label.setText(text.substring(0, 30) + "...");
                    }
                    // 悬浮显示完整文本
                    logger.info("cmd美化前: " + text);
                    String processed = beautyCmd(text);
                    logger.info("cmd美化后: " + processed);
                    label.setToolTipText(processed);
                }
                return label;
            }
        });

        // Column "remark"
        TableColumn remark = commandTable.getColumnModel().getColumn(1);
        remark.setPreferredWidth(100);
        remark.setMinWidth(100);
        remark.setMaxWidth(200);

        JPanel commandsPanel = new JPanel(new BorderLayout());
        this.schemaComboBox = new ActionSchemaComboBox(schemasFromFile, this);// 分类下拉框
        this.actionManageSchema = new ActionManageSchema(jbTabs, schemaTab, schemaComboBox);// 分类管理按钮
        schemaComboBox.onItemChange();

        JPanel topTool = ToolbarDecorator.createDecorator(commandTable)
                .addExtraAction(schemaComboBox)
                .addExtraAction(actionManageSchema)
                .disableUpDownActions()
                .setToolbarPosition(ActionToolbarPosition.TOP)
                .createPanel();

        // 手动创建右侧竖直工具栏
        this.actionAddCmd = new ActionAddCmd(commandTable, schemaComboBox, cmdModel, myCmdList, cmdService);//新增
        this.actionEditCmd = new ActionEditCmd(commandTable, cmdModel, myCmdList, cmdService);// 修改
        this.actionRemoveCmd = new ActionRemoveCmd(commandTable, cmdModel, myCmdList, cmdService);// 删除
        this.actionCopy = new ActionCopy(commandTable);// 复制
        this.actionRun = new ActionRun(commandTable);// 运行


        JPanel rightTool = new JPanel();
        rightTool.setLayout(new BoxLayout(rightTool, BoxLayout.Y_AXIS)); // 使用 BoxLayout 使按钮竖直排列
        Dimension buttonSize = new Dimension(30, 30);
        Presentation addPresentation = new Presentation();
        addPresentation.copyFrom(actionAddCmd.getTemplatePresentation());
        this.actionAddBtn = new ActionButton(actionAddCmd, addPresentation, "Toolbar", buttonSize);

        Presentation editPresentation = new Presentation();
        editPresentation.copyFrom(actionEditCmd.getTemplatePresentation());
        this.actionEditBtn = new ActionButton(actionEditCmd, editPresentation, "Toolbar", buttonSize);

        Presentation removePresentation = new Presentation();
        removePresentation.copyFrom(actionRemoveCmd.getTemplatePresentation());
        this.actionDelBtn = new ActionButton(actionRemoveCmd, removePresentation, "Toolbar", buttonSize);

        Presentation copyPresentation = new Presentation();
        copyPresentation.copyFrom(actionCopy.getTemplatePresentation());
        this.actionCopyBtn = new ActionButton(actionCopy, copyPresentation, "Toolbar", buttonSize);

        Presentation runPresentation = new Presentation();
        runPresentation.copyFrom(actionRun.getTemplatePresentation());
        this.actionRunBtn = new ActionButton(actionRun, runPresentation, "Toolbar", buttonSize);
        this.buttonList = List.of(actionAddBtn, actionEditBtn, actionDelBtn, actionCopyBtn, actionRunBtn);
        for (ActionButton button : buttonList) {
            button.setPreferredSize(buttonSize);   // 设置首选大小
            button.setMinimumSize(buttonSize);     // 设置最小大小
            button.setMaximumSize(buttonSize);     // 设置最大大小
            button.setEnabled(false);
            rightTool.add(button);  // 添加按钮到竖直工具栏
        }
        actionAddBtn.setEnabled(true);

        // 监听选中行
        commandTable.getSelectionModel().addListSelectionListener(e -> {
            if (commandTable.getSelectedRow() != -1) {
                actionEditBtn.setEnabled(true);
                actionDelBtn.setEnabled(true);
                actionCopyBtn.setEnabled(true);
                actionRunBtn.setEnabled(true);
            }
            if (myCmdList.isEmpty()) {
                actionEditBtn.setEnabled(false);
                actionDelBtn.setEnabled(false);
                actionCopyBtn.setEnabled(false);
                actionRunBtn.setEnabled(false);
            }
        });

        commandsPanel.add(topTool, BorderLayout.CENTER); // 上方工具栏
        commandsPanel.add(rightTool, BorderLayout.EAST); // 右侧竖型工具栏

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
        Pattern pattern = Pattern.compile("\\{%([a-zA-Z0-9_]+)%}");
        Matcher matcher = pattern.matcher(cmd);
        while (matcher.find()) {
            // 获取占位符中的参数名
            String actual = "<i style='color:1C75CFFF;'>" + matcher.group(1) + "</i>";
            cmd = cmd.replace(matcher.group(0), actual);
        }
        String[] split = cmd.split("&&");
        StringBuilder sb = new StringBuilder("<html><code>");
        for (int i = 0; i < split.length; i++) {
            if (i == 0) {
                sb.append(split[i]);
            } else {
                sb.append("<br>");
                sb.append("&&").append(split[i]);
            }
        }
        sb.append("</code></html>");
        return sb.toString();
    }

    private void editSelectedCommand() {
        stopEditing(commandTable);
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
        logger.info("cmd edit. cmd:" + myCmdEdit);
        myCmdList.set(selectedIndex, myCmdEdit);
        cmdService.updateCmd(myCmdEdit);// db
        cmdModel.fireTableRowsUpdated(selectedIndex, selectedIndex);
        commandTable.getSelectionModel().setSelectionInterval(selectedIndex, selectedIndex);
    }

    public static void stopEditing(JBTable commandTable) {
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
        this.cmdModel = null;
        this.myCmdList = null;
        this.commandTable = null;
        this.cmdService = null;
        this.schemaService = null;
        this.defaultSchema = null;
        this.schemasFromFile = null;
        this.actionAddCmd = null;
        this.actionEditCmd = null;
        this.actionRemoveCmd = null;
        this.actionCopy = null;
        this.actionRun = null;
        this.actionAddBtn = null;
        this.actionEditBtn = null;
        this.actionDelBtn = null;
        this.actionCopyBtn = null;
        this.actionRunBtn = null;
        this.buttonList = null;
        this.schemaComboBox = null;
        this.actionManageSchema = null;
    }
}
