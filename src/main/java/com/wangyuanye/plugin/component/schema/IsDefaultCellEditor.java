package com.wangyuanye.plugin.component.schema;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author wangyuanye
 * @date 2024/8/25
 **/
public class IsDefaultCellEditor extends DefaultCellEditor {
    private JTable table;


    public IsDefaultCellEditor(JTable table) {
        super(new JCheckBox());
        this.table = table;
        JCheckBox jCheckBox = (JCheckBox) getComponent();
        jCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
        jCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent itemEvent) {
                boolean selected = ((JCheckBox) itemEvent.getItem()).isSelected();

                if (selected) {
                    int row = table.getEditingRow();
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    for (int i = 0; i < table.getRowCount(); i++) {
                        if (i != row) {
                            // 将其他默认取消掉
                            model.setValueAt(false, i, SchemaTable.col_isdefault);
                        }
                    }
                }
                System.out.println(itemEvent.getItem());
                handleTextChange();
            }
        });
    }

    private void handleTextChange() {
        int row = table.getEditingRow();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        if (row != -1) {
            model.setValueAt(true, row, SchemaTable.col_edit);//修改编辑标识
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        Component component = super.getTableCellEditorComponent(table, value, isSelected, row, column);
        component.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
        return component;
    }

}
