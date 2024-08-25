package com.wangyuanye.plugin.component.schema;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.wangyuanye.plugin.dao.dto.CmdSchema;
import com.wangyuanye.plugin.dao.dto.SchemaDataSave;
import com.wangyuanye.plugin.services.MyService;
import com.wangyuanye.plugin.services.MyServiceImpl;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * @author wangyuanye
 * @date 2024/8/20
 **/
public class SchemaCancelAction extends AnAction {
    private MyService myService;

    public SchemaCancelAction() {
        super("Cancel", "Cancel cmd", AllIcons.Actions.Cancel);
        myService = MyServiceImpl.instance;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println("schema cancel");
        JTable table = ManageSchemaDialog.getTable();
        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing(); // 确保退出编辑模式
        }
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int selectedRow = table.getSelectedRow();
        String schemaId = (String) model.getValueAt(selectedRow, SchemaTable.col_id);
        SchemaDataSave schemaDataSave = ApplicationManager.getApplication().getService(SchemaDataSave.class);
        if (schemaId != null && !schemaId.isEmpty()) {
            // 表已经退出编辑模式,恢复原值
            CmdSchema schema = schemaDataSave.getById(schemaId);
            model.setValueAt(schema.getName(), selectedRow, SchemaTable.col_name);
            model.setValueAt(schema.getDefaultSchema(), selectedRow, SchemaTable.col_isdefault);
            model.setValueAt(false, selectedRow, SchemaTable.col_edit);//编辑标识false
            return;
        }
        if (selectedRow != -1) {
            model.removeRow(selectedRow);
            model.fireTableDataChanged();
            table.clearSelection(); // 清除表格的选择状态
            table.revalidate(); // 重新验证表格布局
            table.repaint(); // 重新绘制表格
        }
    }
}
