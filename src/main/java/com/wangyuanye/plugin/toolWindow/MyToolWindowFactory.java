package com.wangyuanye.plugin.toolWindow;


import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.wangyuanye.plugin.util.IdeaApiUtil;
import com.wangyuanye.plugin.util.UiUtil;
import org.jetbrains.annotations.NotNull;


/**
 * 插件入口
 * 打开新的项目,就会执行一次
 * <p>
 * 基础功能: CRUD, 命令行执行
 * todo cheatsheet
 * todo 分享至github, star rank
 */
public class MyToolWindowFactory implements ToolWindowFactory {
    public static final String myToolWindowId = "Command Assist";
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
//        ToolWindow openTerminal = UiUtil.getOpenTerminal(IdeaApiUtil.getProject());
//        if (openTerminal != null) {
//            openTerminal.activate(null);
//        }
    }
}
