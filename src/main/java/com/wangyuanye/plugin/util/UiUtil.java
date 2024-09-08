package com.wangyuanye.plugin.util;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.impl.ActionButton;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

/**
 * @author wangyuanye
 * 2024/8/20
 **/
public class UiUtil {
    private static ActionManager actionManager = ActionManager.getInstance();

    public static @Nullable ToolWindow getOpenTerminal(@NotNull Project project) {
        // 尝试获取终端
        ToolWindow terminalWindow = ToolWindowManager.getInstance(project).getToolWindow("Terminal");
        if (terminalWindow == null) {
            IdeaApiUtil.myTips("请先安装并启用终端");
            return null;
        }
        if (!terminalWindow.isAvailable()) {
            IdeaApiUtil.myTips("终端不可用,命令已复制到剪切板");
            return null;
        }
        if (terminalWindow.isVisible()) {// 不可见
            terminalWindow.show();
        }
        if (terminalWindow.isActive()) {// 未激活
            terminalWindow.activate(null);
        }
        return terminalWindow;
    }

    /**
     * 获取actionManager
     *
     * @return
     */
    public static ActionManager getActionManager() {
        if (actionManager == null) {
            actionManager = ActionManager.getInstance();
        }
        return actionManager;
    }

    /**
     * 获取actionButton
     *
     * @param actionId
     * @param i18nStr  国际化字符串
     * @return ActionButton
     */
    public static ActionButton getActionButton(String actionId, String i18nStr) {
        // 获取action
        AnAction action = getActionManager().getAction(actionId);
        // 创建ActionButton
        Presentation presentation = action.getTemplatePresentation().clone();
        Dimension buttonSize = JBUI.size(20); // 设置按钮的大小
        return new ActionButton(action, presentation, MessagesUtil.getMessage(i18nStr), buttonSize);
    }

    public static ActionButton getActionButton(AnAction action, String i18nStr) {
        // 创建ActionButton
        Presentation presentation = action.getTemplatePresentation().clone();
        Dimension buttonSize = JBUI.size(20); // 设置按钮的大小
        return new ActionButton(action, presentation, MessagesUtil.getMessage(i18nStr), buttonSize);
    }
}
