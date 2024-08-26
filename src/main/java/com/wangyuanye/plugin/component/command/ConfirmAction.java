package com.wangyuanye.plugin.component.command;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.wangyuanye.plugin.component.toolWindow.MyToolWindow;
import com.wangyuanye.plugin.component.toolbar.SchemaComboBoxAction;
import com.wangyuanye.plugin.dao.dto.Cmd;
import com.wangyuanye.plugin.dao.dto.CmdDataSave;
import com.wangyuanye.plugin.dao.dto.CmdSchema;
import com.wangyuanye.plugin.util.IdeaApiUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

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
        MyToolWindow appService = IdeaApiUtil.getCurrentProjectToolWindow();
        JTable table = appService.getTable();
        CmdTableModel model = (CmdTableModel) table.getModel();

        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;
        int row = table.getEditingRow();
        if (row != -1) {
            model.setValueAt(false, row, CmdTable.col_edit);//修改编辑标识
        }
        table.clearSelection(); // 清除表格的选择状态
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing(); // 确保退出编辑模式
        }
        String cmdId = (String) model.getValueAt(selectedRow, CmdTable.col_cmd_id);
        String name = (String) model.getValueAt(selectedRow, CmdTable.col_cmd_name);
        String remark = (String) model.getValueAt(selectedRow, CmdTable.col_cmd_remark);

        SchemaComboBoxAction anAction = (SchemaComboBoxAction) ActionManager.getInstance().getAction("ca_schema_command");
        CmdSchema selectedItem = (CmdSchema) anAction.getComboBox().getSelectedItem();
        if (selectedItem == null) {
            IdeaApiUtil.myTips("请先创建或选择分类");
            return;
        }
        String schemaId = selectedItem.getId();
        CmdDataSave cmdService = IdeaApiUtil.getCmdService();
        Cmd cmd = new Cmd(schemaId, name, remark);
        if (cmdId == null || cmdId.isEmpty()) {
            // add
            cmdService.addCmd(cmd);
        } else {
            // update
            cmd.setCmdId(cmdId);
            cmdService.updateCmd(cmd);
        }
    }
}
