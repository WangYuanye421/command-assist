package com.wangyuanye.plugin.component.schema;

import com.intellij.openapi.actionSystem.impl.ActionButton;
import com.intellij.ui.table.JBTable;
import com.wangyuanye.plugin.util.UiUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import java.awt.*;

/**
 * @author wangyuanye
 * @date 2024/8/19
 **/
public class SchemaButtonEditor extends AbstractCellEditor implements TableCellEditor {
    private final JPanel panel;
    private final ActionButton confirmBtn;
    private final ActionButton cancelBtn;

    public SchemaButtonEditor(JBTable table) {
        panel = new JPanel();
        confirmBtn = UiUtil.getActionButton("ca_confirm_schema", "tbl_confirm");
        cancelBtn = UiUtil.getActionButton("ca_cancel_schema", "tbl_cancel");
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        panel.removeAll();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        Boolean isEdit = (Boolean) model.getValueAt(row, SchemaTable.col_edit);
        if (isEdit) {
            panel.add(confirmBtn);
            panel.add(Box.createHorizontalStrut(7));
            panel.add(cancelBtn);
        }
        return panel;
    }
}
