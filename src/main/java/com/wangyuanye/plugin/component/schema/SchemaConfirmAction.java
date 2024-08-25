package com.wangyuanye.plugin.component.schema;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.wangyuanye.plugin.dao.dto.CmdSchema;
import com.wangyuanye.plugin.dao.dto.SchemaDataSave;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * @author wangyuanye
 * @date 2024/8/20
 **/
public class SchemaConfirmAction extends AnAction {

    public SchemaConfirmAction() {
        super("Confirm", "Confirm schema", AllIcons.Actions.Commit);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println("schema confirm ........");
        JTable table = ManageSchemaDialog.getTable();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int selectedRow = table.getSelectedRow();
        String schemaId = (String) model.getValueAt(selectedRow, SchemaTable.col_id);
        String name = (String) model.getValueAt(selectedRow, SchemaTable.col_name);
        Boolean isDefault = (Boolean) model.getValueAt(selectedRow, SchemaTable.col_isdefault);
        SchemaDataSave schemaDataSave = ApplicationManager.getApplication().getService(SchemaDataSave.class);
        if(schemaId == null || schemaId.isEmpty()){
            //新增
            CmdSchema cmdSchema = new CmdSchema(name, isDefault);
            schemaDataSave.addSchema(cmdSchema);
        }else{
            //修改
            CmdSchema cmdSchema = new CmdSchema(schemaId, name, isDefault);
            schemaDataSave.updateSchema(cmdSchema);
        }
        table.clearSelection(); // 清除表格的选择状态
        int row = table.getEditingRow();
        if (row != -1) {
            model.setValueAt(false, row, SchemaTable.col_edit);//修改编辑标识
        }
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing(); // 确保退出编辑模式
        }
    }
}
