package com.wangyuanye.plugin.component.command;

import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * @author wangyuanye
 * @date 2024/8/19
 **/
public class CustomCellEditor extends DefaultCellEditor {
    private JBTable table;

    public CustomCellEditor(JBTable table) {
        super(new JTextField());
        this.table = table;
        JTextField textField = (JTextField) getComponent();
        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                handleTextChange();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                handleTextChange();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                handleTextChange();
            }
        });
    }

    private void handleTextChange() {
        int row = table.getEditingRow();
        CmdTableModel model = (CmdTableModel) table.getModel();
        if (row != -1) {
            model.setValueAt(true, row, CmdTable.col_edit);//修改编辑标识
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        // 记录编辑前的值
        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

    @Override
    public Object getCellEditorValue() {
        return super.getCellEditorValue();
    }
}
