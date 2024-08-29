package com.wangyuanye.plugin.component;

import com.intellij.util.ui.ItemRemovable;
import com.wangyuanye.plugin.dao.dto.MyCmd;
import com.wangyuanye.plugin.util.MessagesUtil;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * @author wangyuanye
 * @date 2024/8/28
 **/
public class MyCmdModel extends AbstractTableModel implements ItemRemovable {
    private final String[] ourColumnNames = new String[]{
            MessagesUtil.getMessage("cmd.table.col_name"),
            MessagesUtil.getMessage("cmd.table.col_remark")
    };
    private final Class[] ourColumnClasses = new Class[]{String.class, String.class};

    private final List<MyCmd> myCmdList;

    MyCmdModel(List<MyCmd> myCmdList) {
        this.myCmdList = myCmdList;
    }

    @Override
    public String getColumnName(int column) {
        return ourColumnNames[column];
    }

    @Override
    public Class getColumnClass(int column) {
        return ourColumnClasses[column];
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public int getRowCount() {
        return myCmdList.size();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int row, int column) {
        MyCmd myCmd = myCmdList.get(row);
        return switch (column) {
            case 0 -> myCmd.getName();
            case 1 -> myCmd.getRemark();
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        MyCmd myCmd = myCmdList.get(row);
        switch (column) {
            case 0 -> myCmd.setName((String) value);
            case 1 -> myCmd.setRemark((String) value);
            default -> throw new IllegalArgumentException();
        }
    }

    @Override
    public void removeRow(int index) {
        myCmdList.remove(index);
        fireTableRowsDeleted(index, index);
    }
}
