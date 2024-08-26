package com.wangyuanye.plugin.component.schema;

import com.wangyuanye.plugin.util.UiUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;


/**
 * @author wangyuanye
 * @date 2024/8/19
 **/
public class SchemaButtonRenderer extends DefaultTableCellRenderer {
    private final JPanel editedPanel;


    public SchemaButtonRenderer() {
        // 初始化编辑状态下的面板
        editedPanel = new JPanel();
        editedPanel.add(UiUtil.getActionButton("ca_confirm_command", "tbl_confirm"));
        editedPanel.add(Box.createHorizontalStrut(7));
        editedPanel.add(UiUtil.getActionButton("ca_cancel_command", "tbl_cancel"));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        Boolean isEdit = (Boolean) model.getValueAt(row, SchemaTable.col_edit);
        if (isEdit) {
            return editedPanel;
        }
        return new JPanel();
    }
}
