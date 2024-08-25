package com.wangyuanye.plugin.component.toolbar;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.ui.SearchTextField;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author wangyuanye
 * @date 2024/8/19
 **/
public class FindAction extends AnAction implements CustomComponentAction {
    private final SearchTextField searchTextField;

    public FindAction() {
        this.searchTextField = new SearchTextField();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println("FindAction ........");
    }

    @Override
    public @NotNull JComponent createCustomComponent(@NotNull Presentation presentation, @NotNull String place) {
        return searchTextField;
    }
}
