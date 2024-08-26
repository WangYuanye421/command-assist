package com.wangyuanye.plugin.component.toolbar;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.DefaultLogger;
import com.intellij.openapi.diagnostic.Logger;
import com.wangyuanye.plugin.component.command.CmdTable;
import com.wangyuanye.plugin.component.command.CmdTableModel;
import com.wangyuanye.plugin.dao.dto.Cmd;
import com.wangyuanye.plugin.util.IdeaApiUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * @author wangyuanye
 * @date 2024/8/19
 **/
public class AddAction extends AnAction {
    private final Logger logger = new DefaultLogger("addAction");
    private JTable table;
    private CmdTable cmdTable;

    public AddAction() {

    }

    public AddAction(CmdTable cmdTable) {
        super("add", "add data", AllIcons.General.Add);
        this.cmdTable = cmdTable;
        this.table = cmdTable.getTable();
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println("AddAction ........");
        SchemaComboBoxAction anAction = (SchemaComboBoxAction) ActionManager.getInstance().getAction("ca_schema_command");
        Object selectedItem = anAction.getComboBox().getSelectedItem();
        if (selectedItem == null) {
            IdeaApiUtil.myTips("请先创建分类");
            return;
        }
        CmdTableModel tableModel = (CmdTableModel) table.getModel();
        Vector<Vector> dataVector = tableModel.getDataVector();
        if (dataVector.isEmpty()) {
            // 首次添加,初始化tableCell
            Cmd cmd = new Cmd();
            cmd.setEdit(true);
            List<Cmd> list = new ArrayList<>();
            list.add(cmd);
            cmdTable.refreshTable(list);
        } else {
            tableModel.addRow(new Object[]{null, "", "", "", "", "", true});
        }
    }
}
