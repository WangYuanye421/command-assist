package com.wangyuanye.plugin.component.command;

import com.wangyuanye.plugin.util.UiUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;


/**
 * @author wangyuanye
 * @date 2024/8/19
 **/
public class CmdButtonRenderer extends DefaultTableCellRenderer {
    private final JPanel editedPanel;
    private final JPanel normalPanel;

    public CmdButtonRenderer() {
        // 初始化编辑状态下的面板
        editedPanel = new JPanel();
        editedPanel.add(UiUtil.getActionButton("ca_confirm_command", "tbl_confirm"));
        editedPanel.add(Box.createHorizontalStrut(7));
        editedPanel.add(UiUtil.getActionButton("ca_cancel_command", "tbl_cancel"));

        // 初始化正常状态下的面板
        normalPanel = new JPanel();
        normalPanel.add(UiUtil.getActionButton("ca_run_command", "tbl_col3"));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        CmdTableModel model = (CmdTableModel) table.getModel();
        Boolean isEdit = (Boolean) model.getValueAt(row, CmdTable.col_edit);
        if (isEdit) {
            return editedPanel;
        } else {
            return normalPanel;
        }
    }
}
