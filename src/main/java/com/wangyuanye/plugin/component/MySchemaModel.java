package com.wangyuanye.plugin.component;

import com.intellij.util.ui.ItemRemovable;
import com.wangyuanye.plugin.dao.dto.MySchema;
import com.wangyuanye.plugin.util.MessagesUtil;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * @author wangyuanye
 * @date 2024/8/28
 **/
public class MySchemaModel extends AbstractTableModel implements ItemRemovable {
    private final String[] ourColumnNames = new String[]{
            MessagesUtil.getMessage("schema.table.col_name"),
            MessagesUtil.getMessage("schema.table.col_default")
    };
    private final Class[] ourColumnClasses = new Class[]{String.class, Boolean.class};

    private final List<MySchema> schemaList;

    MySchemaModel(List<MySchema> schemaList) {
        this.schemaList = schemaList;

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
        return schemaList.size();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        //return columnIndex == 1;勾选框也禁用,避免频繁点击,省去"唯一默认"的界面校验
        return false;
    }

    @Override
    public Object getValueAt(int row, int column) {
        MySchema schema = schemaList.get(row);
        return switch (column) {
            case 0 -> schema.getName();
            case 1 -> schema.getDefaultSchema();
            default -> throw new IllegalArgumentException();
        };
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        MySchema schema = schemaList.get(row);
        switch (column) {
            case 0 -> schema.setName((String) value);
            case 1 -> schema.setDefaultSchema((Boolean) value);
            default -> throw new IllegalArgumentException();
        }
    }

    @Override
    public void removeRow(int index) {
        schemaList.remove(index);
        fireTableRowsDeleted(index, index);
    }
}
