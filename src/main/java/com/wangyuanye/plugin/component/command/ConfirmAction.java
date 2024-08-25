package com.wangyuanye.plugin.component.command;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

/**
 * @author wangyuanye
 * @date 2024/8/20
 **/
public class ConfirmAction extends AnAction {

    public ConfirmAction() {
        super("Confirm", "Confirm cmd", AllIcons.Actions.Commit);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println("ConfirmAction ........");
        System.out.println("basePath : " + e.getProject().getBasePath());
    }
}
