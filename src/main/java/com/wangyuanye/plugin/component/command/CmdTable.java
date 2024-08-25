package com.wangyuanye.plugin.component.command;

import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import com.wangyuanye.plugin.dao.dto.Cmd;
import com.wangyuanye.plugin.util.MessagesUtil;
import com.wangyuanye.plugin.util.TableUtil;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wangyuanye
 * @date 2024/8/19
 **/
public class CmdTable {
    public static final int col_check = 0;
    public static final int col_cmd_name = 1;
    public static final int col_cmd_remark = 2;
    public static final int col_ope = 3;
    public static final int col_cmd_id = 4;
    public static final int col_schema_id = 5;
    public static final int col_edit = 6;

    private JBTable table;
    private Object[] header;

    public CmdTable() {
        this.table = new JBTable();
        this.header = new Object[]{
                null,
                MessagesUtil.getMessage("tbl_col1"),
                MessagesUtil.getMessage("tbl_col2"),
                MessagesUtil.getMessage("tbl_col3"),
                null,
                null,
                false//是否编辑状态
        };
    }

    /**
     * 创建table
     *
     * @param cmdList
     * @return
     */
    public JTable createTable(List<Cmd> cmdList) {
        if (cmdList == null) return table;
        buildTableModel(cmdList);
        configTable(table);
        return table;
    }

    /**
     * 刷新表格数据
     *
     * @param cmdList
     * @return
     */
    public void refreshTable(ArrayList<Cmd> cmdList) {
        buildTableModel(cmdList);
        configTable(table);
    }

    //构建数据模型
    private void buildTableModel(List<Cmd> cmdList) {
        if (cmdList == null || cmdList.isEmpty()) {
            emptyTableTip();
            return;
        }
        // tip for no data
        Object[][] arr = new Object[cmdList.size()][6];
        for (int i = 0; i < cmdList.size(); i++) {
            Cmd cmd = cmdList.get(i);
            arr[i] = new Object[]{null, cmd.getName(), cmd.getRemark(), "", cmd.getCmdId(), cmd.getSchemaId(), false};
        }
        CmdTableModel tableModel = new CmdTableModel(arr, header);
        table.setModel(tableModel);
    }

    private void emptyTableTip() {// todo i18n replace
        // 设置空数据时的提示信息
        table.getEmptyText().setText("No data available");
        // 你还可以设置多行提示
        table.getEmptyText().appendLine("Please add some data to see the table content.");
        // 设置空数据提示的样式
        table.getEmptyText().setFont(JBUI.Fonts.label(16f)); // 设置字体大小

    }

    private void configTable(JBTable table) {
        // 通用渲染
        TableUtil.cellSetting(table, true);
        TableUtil.headerSetting(table, true);
        // 设置编辑器
        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(col_cmd_name).setCellEditor(new CustomCellEditor(table));// cmd name
        columnModel.getColumn(col_cmd_remark).setCellEditor(new CustomCellEditor(table));// cmd description
        columnModel.getColumn(col_ope).setCellRenderer(new CmdButtonRenderer());// operation
        columnModel.getColumn(col_ope).setCellEditor(new CmdButtonEditor(table));
        // 视图上移除
        setColumnDisappear(col_cmd_id, col_schema_id, col_edit);// cmdId,schemaId,isEdit
    }

    private void setColumnDisappear(int... args) {
        for (int arg : args) {
            table.getColumnModel().getColumn(arg).setMinWidth(0);
            table.getColumnModel().getColumn(arg).setMaxWidth(0);
        }
    }
}
