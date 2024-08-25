package com.wangyuanye.plugin.component.schema;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.ui.components.JBLabel;
import com.wangyuanye.plugin.util.MessagesUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author wangyuanye
 * @date 2024/8/19
 **/
public class SchemaLabelAction extends AnAction implements CustomComponentAction {
    private JBLabel label;

    public SchemaLabelAction() {
        this.label = new JBLabel(MessagesUtil.getMessage("box_label"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
    }

    @Override
    public @NotNull JComponent createCustomComponent(@NotNull Presentation presentation, @NotNull String place) {
        return label;
    }
}
