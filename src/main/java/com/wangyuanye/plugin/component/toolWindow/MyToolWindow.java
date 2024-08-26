package com.wangyuanye.plugin.component.toolWindow;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.wangyuanye.plugin.component.command.CmdTable;
import com.wangyuanye.plugin.component.schema.SchemaLabelAction;
import com.wangyuanye.plugin.component.toolbar.*;
import com.wangyuanye.plugin.dao.dto.Cmd;
import com.wangyuanye.plugin.dao.dto.CmdDataSave;
import com.wangyuanye.plugin.dao.dto.CmdSchema;
import com.wangyuanye.plugin.dao.dto.SchemaDataSave;
import com.wangyuanye.plugin.util.IdeaApiUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 插件窗口
 *
 * @author wangyuanye
 * @date 2024/8/20
 **/
@Service
public final class MyToolWindow {
    private JTable table;
    private CmdTable cmdTable;
    private JPanel mainPanel;
    private JPanel toolbar;
    private JPanel toolbarLeft;
    private JPanel toolbarRight;
    private JPanel tablePanel;
    private DefaultActionGroup actionGroup;
    private SchemaComboBoxAction schemaComboBoxAction;

    public Project currentProject;

    public MyToolWindow() {
        this.cmdTable = new CmdTable();
        this.mainPanel = new JPanel(new BorderLayout());
        this.tablePanel = new JPanel(new BorderLayout());
        this.toolbar = new JPanel(new BorderLayout());
        this.toolbarLeft = new JPanel();
        this.toolbarRight = new JPanel();
        // 创建工作栏
        buildToolbar(new ArrayList<>());
        // 创建表格
        buildTable(new ArrayList<>());
    }

    private void buildToolbar(List<CmdSchema> schemas) {
        DefaultActionGroup actionGroup1 = (DefaultActionGroup) ActionManager.getInstance().getAction("command_group1");
        DefaultActionGroup actionGroup2 = (DefaultActionGroup) ActionManager.getInstance().getAction("command_group2");
        actionGroup1.add(new SchemaLabelAction());//分类label

        SchemaComboBoxAction action = new SchemaComboBoxAction(schemas);
        action.onItemChange(this);
        actionGroup1.add(action);//分类下拉框

        actionGroup1.add(new ManageSchemaAction());// 管理分类
//        actionGroup1.add(Separator.getInstance());
//
//        actionGroup1.add(new FindAction());//搜索
//
//        actionGroup1.add(Separator.getInstance());
        actionGroup2.add(new CmdLabelAction());
        actionGroup2.add(new AddAction(cmdTable));//新增命令
        actionGroup2.add(new DeleteAction(table));//删除命令
        // 创建工具栏
        ActionToolbar actionToolbar1 = ActionManager.getInstance().createActionToolbar("My_command_group1", actionGroup1, true);
        ActionToolbar actionToolbar2 = ActionManager.getInstance().createActionToolbar("My_command_group2", actionGroup2, true);
        // 工具栏作用的窗口
        actionToolbar1.setTargetComponent(mainPanel);
        actionToolbar2.setTargetComponent(mainPanel);
        toolbarLeft.add(actionToolbar1.getComponent());
        toolbarRight.add(actionToolbar2.getComponent());
        toolbar.add(toolbarLeft, BorderLayout.WEST);
        toolbar.add(toolbarRight, BorderLayout.EAST);
        mainPanel.add(toolbar, BorderLayout.NORTH);
    }

    // 构建table
    private void buildTable(List<Cmd> cmdList) {
        table = cmdTable.createTable(cmdList);
        JPanel createTable = new JPanel(new BorderLayout());
        createTable.add(new JBScrollPane(table), BorderLayout.CENTER);
        tablePanel.add(createTable, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
    }

    /**
     * 初始化数据
     *
     * @param toolWindow
     * @param project
     */
    public void initToolWindow(@NotNull ToolWindow toolWindow, @NotNull Project project) {
        this.currentProject = project;
        //toolWindow.setHelpId("");
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(null, null, false);
        // 持久化对象
        CmdDataSave cmdService = IdeaApiUtil.getCmdService();
        SchemaDataSave schemaService = IdeaApiUtil.getSchemaService();

        CmdSchema defaultSchema = new CmdSchema();
        List<CmdSchema> schemas = new ArrayList<>();
        List<Cmd> cmdList = new ArrayList<>();
        // 数据未完成加载,使用空集合
        if (schemaService != null && schemaService.getSchemaDataLoaded()) {
            schemas = schemaService.list();
            defaultSchema = schemaService.getDefaultSchema();
        }
        if (cmdService != null && cmdService.getCmdDataLoaded()) {
            if (defaultSchema != null) {
                cmdList = cmdService.list(defaultSchema.getId());
            }
        }
        // 填充工具栏数据
        SchemaComboBoxAction anAction = (SchemaComboBoxAction) ActionManager.getInstance().getAction("ca_schema_command");
        anAction.initComboBoxData(schemas);
        // 填充table
        table = cmdTable.createTable(cmdList);
        ContentManager myContentManager = toolWindow.getContentManager();
        content.setComponent(mainPanel);
        myContentManager.addContent(content);
    }

    // 刷新table数据
    public void refreshTable(String currentName) {
        CmdDataSave cmdService = IdeaApiUtil.getCmdService();
        List<Cmd> cmdList = cmdService.list(currentName);
        cmdTable.refreshTable(cmdList);
    }


    public JTable getTable() {
        return this.table;
    }

    public @NotNull Project getCurrentProject() {
        return this.currentProject;
    }


}
