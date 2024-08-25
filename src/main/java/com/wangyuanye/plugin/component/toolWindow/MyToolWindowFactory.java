package com.wangyuanye.plugin.component.toolWindow;


import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.openapi.wm.ToolWindowManager;
import com.wangyuanye.plugin.util.MessagesUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * 插件入口
 * <p>
 * todo 基础功能: CRUD, 命令行执行
 * todo cheatsheet
 * todo 分享至github, star rank
 */
public class MyToolWindowFactory implements ToolWindowFactory {
    public static boolean TERMINAL_OPEN = false;
    private static Project project;

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        MyToolWindowFactory.project = project;
        //执行插件
        ApplicationManager.getApplication().getService(MyToolWindow.class).initToolWindow(toolWindow);
        getOpenTerminal();
    }

    public static @Nullable ToolWindow getOpenTerminal() {
        Notification notification = new Notification(
                "CommandAssistNotificationGroup", // 通知组ID
                "Command Assist 通知",        // 通知标题
                NotificationType.INFORMATION // 通知类型 (INFORMATION, WARNING, ERROR)
        );
        // 尝试获取终端
        ToolWindow terminalWindow = ToolWindowManager.getInstance(getProject()).getToolWindow("Terminal");
        if (terminalWindow == null) {
            notification.setContent(MessagesUtil.buildBalloon("请先安装并启用终端"));
            notification.notify(getProject());
            return null;
        }
        if (!terminalWindow.isAvailable()) {
            notification.setContent(MessagesUtil.buildBalloon("终端不可用,命令已复制到剪切板"));
            notification.notify(getProject());
            return null;
        }
        if (terminalWindow.isVisible()) {// 不可见
            terminalWindow.show();
        }
        if (terminalWindow.isActive()) {// 未激活
            terminalWindow.activate(null);
        }
        // 终端已打开
        TERMINAL_OPEN = true;
        return terminalWindow;
    }

    public static Project getProject() {
        return MyToolWindowFactory.project;
    }

}
