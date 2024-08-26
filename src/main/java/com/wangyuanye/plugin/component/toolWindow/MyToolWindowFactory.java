package com.wangyuanye.plugin.component.toolWindow;


import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
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
    private static MyToolWindow myToolWindow;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        if (myToolWindow == null) {
            myToolWindow = ApplicationManager.getApplication().getService(MyToolWindow.class);
        }
        System.out.println("project: " + project.getName());
        System.out.println("myToolWindow" + myToolWindow);
        //执行插件
        myToolWindow.initToolWindow(toolWindow, project);
    }
}
