package com.wangyuanye.plugin.component.command;

import com.intellij.openapi.actionSystem.impl.ActionButton;
import com.intellij.ui.table.JBTable;
import com.wangyuanye.plugin.component.toolWindow.MyToolWindowFactory;
import com.wangyuanye.plugin.util.UiUtil;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;

/**
 * @author wangyuanye
 * @date 2024/8/19
 **/
public class CmdButtonEditor extends AbstractCellEditor implements TableCellEditor {
    private final JPanel panel;
    private final ActionButton confirmBtn;
    private final ActionButton cancelBtn;
    private final ActionButton runBtn;

    public CmdButtonEditor(JBTable table) {
        panel = new JPanel();
        confirmBtn = UiUtil.getActionButton("ca_confirm_command", "tbl_confirm");
        cancelBtn = UiUtil.getActionButton("ca_cancel_command", "tbl_cancel");
        runBtn = UiUtil.getActionButton("ca_run_command", "tbl_col3");
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        panel.removeAll();
        CmdTableModel model = (CmdTableModel) table.getModel();
        Boolean isEdit = (Boolean) model.getValueAt(row, CmdTable.col_edit);
        if (isEdit) {
            panel.add(confirmBtn);
            panel.add(Box.createHorizontalStrut(7));
            panel.add(cancelBtn);
        } else {
            if (MyToolWindowFactory.TERMINAL_OPEN) {
                // 终端打开后才能执行
                panel.add(runBtn);
            }
        }

        return panel;
    }
}
