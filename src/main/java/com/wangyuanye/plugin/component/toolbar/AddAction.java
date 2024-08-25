package com.wangyuanye.plugin.component.toolbar;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.DefaultLogger;
import com.intellij.openapi.diagnostic.Logger;
import com.wangyuanye.plugin.component.command.CmdTableModel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author wangyuanye
 * @date 2024/8/19
 **/
public class AddAction extends AnAction {
    private final Logger logger = new DefaultLogger("addAction");
    private JTable table;

    public AddAction() {

    }

    public AddAction(JTable table) {
        super("add", "add data", AllIcons.General.Add);
        this.table = table;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println("AddAction ........");
        CmdTableModel tableModel = (CmdTableModel) table.getModel();
        tableModel.addRow(new Object[]{null, "", "", "", "", "", true});

    }
}
