package com.wangyuanye.plugin.util;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.impl.ActionButton;
import com.intellij.util.ui.JBUI;

import java.awt.*;

/**
 * @author wangyuanye
 * @date 2024/8/20
 **/
public class UiUtil {
    private static ActionManager actionManager = ActionManager.getInstance();

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
}
