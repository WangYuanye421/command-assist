package com.wangyuanye.plugin.component.schema;

import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import com.wangyuanye.plugin.dao.dto.CmdSchema;
import com.wangyuanye.plugin.util.MessagesUtil;
import com.wangyuanye.plugin.util.TableUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.util.List;

/**
 * @author wangyuanye
 * @date 2024/8/19
 **/
public class SchemaTable {
    private JBTable table;
    private Object[] header;
    public static final int col_checked = 0;
    public static final int col_name = 1;
    public static final int col_isdefault = 2;
    public static final int col_ope = 3;
    public static final int col_edit = 4;
    public static final int col_id = 5;

    public SchemaTable() {
        this.table = new JBTable();
        this.header = new Object[]{
                null,
                MessagesUtil.getMessage("cate_col2"),
                MessagesUtil.getMessage("cate_col3"),
                MessagesUtil.getMessage("cate_col4"),
                null,
                null
        };
    }

    /**
     * 创建table
     *
     * @param schemaList
     * @return
     */
    public JBTable createTable(List<CmdSchema> schemaList) {
        buildTableModel(schemaList);
        configTable(table);
        return table;
    }

    /**
     * 刷新表格数据
     *
     * @param schemaList
     * @return
     */
    public void refreshTable(List<CmdSchema> schemaList) {
        createTable(schemaList);
        table.revalidate();
        table.repaint();
    }

    //构建数据模型
    private void buildTableModel(List<CmdSchema> schemaList) {
        // 设置空数据时的提示信息
        table.getEmptyText().setText("No data available");
        // 你还可以设置多行提示
        table.getEmptyText().appendLine("Please add some data to see the table content.");
        // 设置空数据提示的样式
        table.getEmptyText().setFont(JBUI.Fonts.label(16f)); // 设置字体大小
        Object[][] arr = new Object[schemaList.size()][];
        for (int i = 0; i < schemaList.size(); i++) {

            arr[i] = new Object[]{
                    false,
                    schemaList.get(i).getName(),
                    schemaList.get(i).getDefaultSchema(),
                    null,
                    false,
                    schemaList.get(i).getId()
            };
        }
        TableModel tableModel = new DefaultTableModel(arr, header);
        table.setModel(tableModel);
    }


    private void configTable(JBTable table) {
        // 通用渲染
        TableUtil.cellSetting(table, true);
        TableUtil.headerSetting(table, true);
        // 设置编辑器
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(col_name).setCellEditor(new SchemaNameCellEditor(table));
        columnModel.getColumn(col_name).setPreferredWidth(100);

        //columnModel.getColumn(col_isdefault).setCellRenderer(new IsDefaultCellRenderer(table));
        columnModel.getColumn(col_isdefault).setCellRenderer(table.getDefaultRenderer(Boolean.class));
        columnModel.getColumn(col_isdefault).setCellEditor(new IsDefaultCellEditor(table));
        columnModel.getColumn(col_isdefault).setMinWidth(60);
        columnModel.getColumn(col_isdefault).setMaxWidth(60);
        columnModel.getColumn(col_ope).setCellRenderer(new SchemaButtonRenderer());// operation
        columnModel.getColumn(col_ope).setCellEditor(new SchemaButtonEditor(table));
        table.getColumnModel().getColumn(col_ope).setMinWidth(120);
        table.getColumnModel().getColumn(col_ope).setMaxWidth(120);
        // 隐藏列
        columnModel.removeColumn(columnModel.getColumn(col_id));
        columnModel.removeColumn(columnModel.getColumn(col_edit));

    }
}
