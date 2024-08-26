package com.wangyuanye.plugin.component.command;

import com.intellij.icons.AllIcons;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.JBUI;
import com.wangyuanye.plugin.dao.dto.Cmd;
import com.wangyuanye.plugin.util.MessagesUtil;
import com.wangyuanye.plugin.util.TableUtil;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.util.ArrayList;
import java.util.List;

import static com.intellij.ui.SimpleTextAttributes.STYLE_ITALIC;

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
    private CmdTableModel tableModel;
    private Object[] header;

    public CmdTable() {
        this.header = new Object[]{
                null,
                MessagesUtil.getMessage("tbl_col1"),
                MessagesUtil.getMessage("tbl_col2"),
                MessagesUtil.getMessage("tbl_col3"),
                null,
                null,
                false//是否编辑状态
        };
        this.table = new JBTable();
        this.tableModel = new CmdTableModel();
        table.setModel(tableModel);
    }

    /**
     * 创建table
     *
     * @param cmdList
     * @return
     */
    public JTable createTable(List<Cmd> cmdList) {
        if (cmdList == null) {
            cmdList = new ArrayList<>();
        }
        buildTableModel(cmdList);
        configTable(table, cmdList);
        return table;
    }

    /**
     * 刷新表格数据
     *
     * @param cmdList
     * @return
     */
    public void refreshTable(List<Cmd> cmdList) {
        buildTableModel(cmdList);
        configTable(table, cmdList);
    }

    //构建数据模型
    private void buildTableModel(List<Cmd> cmdList) {
        emptyTableTip();// tip for no data
        Object[][] arr = new Object[cmdList.size()][6];
        for (int i = 0; i < cmdList.size(); i++) {
            Cmd cmd = cmdList.get(i);
            arr[i] = new Object[]{null, cmd.getName(), cmd.getRemark(), "", cmd.getCmdId(), cmd.getSchemaId(),
                    cmd.getEdit() == null ? false : cmd.getEdit()};
        }
        tableModel.setDataVector(arr, header);
    }

    private void emptyTableTip() {// todo i18n replace
        // 设置空数据时的提示信息
        table.getEmptyText().setText(MessagesUtil.getMessage("no_data"));
        // 你还可以设置多行提示
        table.getEmptyText().appendLine(AllIcons.Modules.EditFolder, MessagesUtil.getMessage("no_data_tip1"),
                new SimpleTextAttributes(STYLE_ITALIC, JBColor.darkGray), null);
        table.getEmptyText().appendLine(AllIcons.General.Add, MessagesUtil.getMessage("no_data_tip2"),
                new SimpleTextAttributes(STYLE_ITALIC, JBColor.darkGray), null);
        // 设置空数据提示的样式
        table.getEmptyText().setFont(JBUI.Fonts.label(16f)); // 设置字体大小

    }

    private void configTable(JBTable table, List<?> list) {
        // 通用渲染
        TableUtil.headerSetting(table, true);
        if (!list.isEmpty()) {
            TableUtil.cellSetting(table, true);
            // 设置编辑器
            TableColumnModel columnModel = table.getColumnModel();
            columnModel.getColumn(col_cmd_name).setCellEditor(new CustomCellEditor(table));// cmd name
            columnModel.getColumn(col_cmd_remark).setCellEditor(new CustomCellEditor(table));// cmd description
            columnModel.getColumn(col_ope).setCellRenderer(new CmdButtonRenderer());// operation
            columnModel.getColumn(col_ope).setCellEditor(new CmdButtonEditor(table));
        }
        TableColumnModel headerColumn = table.getTableHeader().getColumnModel();
        // 视图上移除
        setColumnDisappear(headerColumn, col_cmd_id, col_schema_id, col_edit);// cmdId,schemaId,isEdit
    }

    private void setColumnDisappear(TableColumnModel columnModel, int... args) {
        for (int arg : args) {
            columnModel.getColumn(arg).setMinWidth(0);
            columnModel.getColumn(arg).setMaxWidth(0);
        }
    }

    public JBTable getTable() {
        return table;
    }
}
