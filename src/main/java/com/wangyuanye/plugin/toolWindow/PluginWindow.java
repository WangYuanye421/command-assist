package com.wangyuanye.plugin.toolWindow;

import com.intellij.ide.ui.LafManager;
import com.intellij.ide.ui.LafManagerListener;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.JBTabsFactory;
import com.intellij.ui.tabs.TabInfo;
import com.wangyuanye.plugin.component.CommandTab;
import com.wangyuanye.plugin.component.SchemaTab;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * 插件窗口
 *
 * @author wangyuanye
 * @date 2024/8/20
 **/
@Service
public final class PluginWindow implements Disposable {
    private static final Logger logger = Logger.getInstance(PluginWindow.class);
    private static Boolean CHEATSHEET_SHOW = false;
    private JBTabs jbTabs;
    public Project currentProject;
    private CommandTab commandTab;
    private SchemaTab schemaTab;

    public PluginWindow() {
        commandTab = new CommandTab();
        schemaTab = new SchemaTab();
    }

    public CommandTab getCommandTab() {
        return commandTab;
    }


    public void initToolWindow(@NotNull ToolWindow toolWindow, @NotNull Project project) {
        this.currentProject = project;
        jbTabs = JBTabsFactory.createTabs(project);
        // 监听UI主题变化
        ApplicationManager.getApplication().getMessageBus().connect().subscribe(LafManagerListener.TOPIC, new LafManagerListener() {
            @Override
            public void lookAndFeelChanged(LafManager source) {
                // 当主题发生变化时调用此方法
                for (TabInfo tab : jbTabs.getTabs()) {
                    tab.getComponent().repaint();
                }
            }
        });

        TabInfo cmdTab = commandTab.buildCommandTab(jbTabs, schemaTab);
        //cmdTab.setTabColor(new JBColor(new Color(98, 163, 103), new Color(98, 163, 103)));
        jbTabs.addTab(cmdTab);
        if (CHEATSHEET_SHOW) {
            jbTabs.addTab(cheatsheetTab());
        }

        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(null, null, false);
        content.setComponent(jbTabs.getComponent());
        ContentManager myContentManager = toolWindow.getContentManager();
        myContentManager.addContent(content);
    }

    private TabInfo cheatsheetTab() {
        JLabel jLabel = new JLabel("xxxxxxxxx");
        return new TabInfo(jLabel).setText("cheatsheet");
    }

    public @NotNull Project getCurrentProject() {
        return this.currentProject;
    }

    @Override
    public void dispose() {
        commandTab = null;
        schemaTab = null;
    }
}
