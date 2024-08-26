package com.wangyuanye.plugin.component.command;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.wangyuanye.plugin.component.toolWindow.MyToolWindow;
import com.wangyuanye.plugin.dao.dto.Cmd;
import com.wangyuanye.plugin.dao.dto.CmdDataSave;
import com.wangyuanye.plugin.util.IdeaApiUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @author wangyuanye
 * @date 2024/8/20
 **/
public class CancelAction extends AnAction {

    public CancelAction() {
        super("Cancel", "Cancel cmd", AllIcons.Actions.Cancel);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        MyToolWindow appService = IdeaApiUtil.getCurrentProjectToolWindow();
        JTable table = appService.getTable();
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing(); // 确保退出编辑模式
        }
        CmdTableModel model = (CmdTableModel) table.getModel();
        int selectedRow = table.getSelectedRow();
        String cmdId = (String) model.getValueAt(selectedRow, CmdTable.col_cmd_id);
        String schemaId = (String) model.getValueAt(selectedRow, CmdTable.col_schema_id);
        if (cmdId != null && !cmdId.isEmpty()) {
            // 表已经退出编辑模式
            resetRowData(cmdId, schemaId, selectedRow, model);
            model.setValueAt(false, selectedRow, CmdTable.col_edit);//编辑标识false
            return;
        }
        if (selectedRow != -1) {
            model.removeRow(selectedRow);
            model.fireTableDataChanged();
            table.clearSelection(); // 清除表格的选择状态
            table.revalidate(); // 重新验证表格布局
            table.repaint(); // 重新绘制表格
        }
    }

    private void resetRowData(String cmdId, String schemaId, int selectedRow, CmdTableModel model) {
        CmdDataSave cmdService = IdeaApiUtil.getCmdService();
        Cmd cmd = cmdService.getById(schemaId, cmdId);
        if (cmd != null) {
            model.setValueAt(cmd.getName(), selectedRow, CmdTable.col_cmd_name);
            model.setValueAt(cmd.getRemark(), selectedRow, CmdTable.col_cmd_remark);
        }
    }
}
