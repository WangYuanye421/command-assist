package com.wangyuanye.plugin.component.command;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author wangyuanye
 * @date 2024/8/20
 **/
public class CheckBoxAction extends AnAction implements CustomComponentAction {
    private final JCheckBox jCheckBox;

    public CheckBoxAction() {
        this.jCheckBox = new JCheckBox();
        this.jCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("xxxxxxxxxxxxxxxxx");
            }
        });
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println("CheckBoxAction ........");
        System.out.println("data : " + e.getDataContext().toString());
    }

    @Override
    public @NotNull JComponent createCustomComponent(@NotNull Presentation presentation, @NotNull String place) {
        return jCheckBox;
    }
}
