package com.wangyuanye.plugin.component.toolWindow;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.wangyuanye.plugin.component.command.CmdTable;
import com.wangyuanye.plugin.component.toolbar.SchemaBomboAction;
import com.wangyuanye.plugin.component.schema.SchemaLabelAction;
import com.wangyuanye.plugin.component.toolbar.*;
import com.wangyuanye.plugin.dao.dto.Cmd;
import com.wangyuanye.plugin.dao.dto.CmdDataSave;
import com.wangyuanye.plugin.dao.dto.CmdSchema;
import com.wangyuanye.plugin.dao.dto.SchemaDataSave;
import com.wangyuanye.plugin.services.MyService;
import com.wangyuanye.plugin.services.MyServiceImpl;
import com.wangyuanye.plugin.util.IdeaApiUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 插件窗口
 *
 * @author wangyuanye
 * @date 2024/8/20
 **/
@Service
public final class MyToolWindow {
    private ContentManager myContentManager;
    private MyService myService;
    private static JTable table;
    private ToolWindow toolWindows;
    private CmdTable cmdTable;
    private JPanel mainPanel;
    private JPanel tablePanel;

    public MyToolWindow() {
        this.myService = MyServiceImpl.instance;
        this.cmdTable = new CmdTable();
        this.mainPanel = new JPanel(new BorderLayout());
        this.tablePanel = new JPanel(new BorderLayout());
    }

    public static JTable getTable() {
        return table;
    }

    public void initToolWindow(@NotNull ToolWindow toolWindow) {
        toolWindows = toolWindow;
        //toolWindow.setHelpId("");
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(null, null, false);
        // todo 如何保证数据服务已加载
        CmdDataSave cmdService = IdeaApiUtil.getCmdService();
        SchemaDataSave schemaDataSave = IdeaApiUtil.getSchemaService();
        CmdSchema defaultSchema = schemaDataSave.getDefaultSchema();
        List<String> schemas = schemaDataSave.list().stream().map(CmdSchema::getName).collect(Collectors.toList());
        List<Cmd> cmdList = cmdService.list(defaultSchema.getId());
        // 创建操作组
        DefaultActionGroup actionGroup = (DefaultActionGroup) ActionManager.getInstance().getAction("command_group");
        // table处理
        buildTable(cmdList);
        // 工具栏处理
        buildToolbar(actionGroup, defaultSchema.getName(), schemas);
        myContentManager = toolWindow.getContentManager();
        content.setComponent(mainPanel);
        myContentManager.addContent(content);
    }


    // 刷新table数据
    public void refreshTable(String currentName) {
        ArrayList<Cmd> cmdList = myService.list(currentName);
        cmdTable.refreshTable(cmdList);
    }

    // 构建toolbar
    private void buildToolbar(DefaultActionGroup group, String currentName, List<String> schemas) {
        group.add(new SchemaLabelAction());//分类label

        SchemaBomboAction action = new SchemaBomboAction(currentName, schemas);
        action.onItemChange(this);
        group.add(action);//分类下拉框

        group.add(new ManageSchemaAction());// 管理分类
//        group.add(Separator.getInstance());
//
//        group.add(new FindAction());//搜索
//
//        group.add(Separator.getInstance());
        group.add(new CmdLabelAction());
        group.add(new AddAction(table));//新增命令
        group.add(new DeleteAction(table));//删除命令
        // 创建工具栏
        ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("My_command_group", group, true);
        // 工具栏作用的窗口
        actionToolbar.setTargetComponent(mainPanel);
        mainPanel.add(actionToolbar.getComponent(), BorderLayout.NORTH);
    }

    // 构建table
    private void buildTable(List<Cmd> cmdList) {
        table = cmdTable.createTable(cmdList);
        JPanel createTable = new JPanel(new BorderLayout());
        createTable.add(new JBScrollPane(table), BorderLayout.CENTER);
        tablePanel.add(createTable, BorderLayout.CENTER);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
    }

}
