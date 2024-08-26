package com.wangyuanye.plugin.component.command;

import javax.swing.table.DefaultTableModel;

public class CmdTableModel extends DefaultTableModel {

    public CmdTableModel() {
    }

    public CmdTableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return true;
    }
}
