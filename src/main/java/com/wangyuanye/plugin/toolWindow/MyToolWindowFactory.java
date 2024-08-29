package com.wangyuanye.plugin.toolWindow;


import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;


/**
 * 插件入口
 * 打开新的项目,就会执行一次
 * <p>
 * 基础功能: CRUD, 命令行执行
 * todo cheatsheet
 * todo 分享至github, star rank
 */
public class MyToolWindowFactory implements ToolWindowFactory {
    private static final Logger logger = Logger.getInstance(MyToolWindowFactory.class);
    private static PluginWindow pluginWindow;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        if (pluginWindow == null) {
            Application application = ApplicationManager.getApplication();
            pluginWindow = application.getService(PluginWindow.class);
        }
        logger.info("info current project : " + project.getName());
        logger.debug("debug current project : " + project.getName());
        //执行插件
        pluginWindow.initToolWindow(toolWindow, project);
    }


    /**
     * 工具类显示的图标
     *
     * @return AllIcons.General.ExternalTools
     */
    @Override
    public @Nullable Icon getIcon() {
        return AllIcons.Toolwindows.ToolWindowCommander;
    }
}
