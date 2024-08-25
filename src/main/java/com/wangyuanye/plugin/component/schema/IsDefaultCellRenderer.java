package com.wangyuanye.plugin.component.schema;

import com.wangyuanye.plugin.component.command.CmdTable;
import com.wangyuanye.plugin.component.command.CmdTableModel;
import com.wangyuanye.plugin.util.UiUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author wangyuanye
 * @date 2024/8/25
 **/
public class IsDefaultCellRenderer implements TableCellRenderer, ItemListener {
    private JTable table;
    private JCheckBox checkBox;


    public IsDefaultCellRenderer(JTable table) {

        checkBox = new JCheckBox();
        checkBox.setHorizontalAlignment(SwingConstants.CENTER);
        checkBox.setOpaque(true);
        checkBox.addItemListener(this);
        this.table = table;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        Boolean isDefault = (Boolean) model.getValueAt(row, SchemaTable.col_isdefault);
        checkBox.setSelected(isDefault);
        return checkBox;
    }

    @Override
    public void itemStateChanged(ItemEvent itemEvent) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int row = table.getEditingRow();
        if (row != -1) {
            model.setValueAt(true, row, SchemaTable.col_edit);//修改编辑标识
        }
    }
}
