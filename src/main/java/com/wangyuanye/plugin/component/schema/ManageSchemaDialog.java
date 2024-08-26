package com.wangyuanye.plugin.component.schema;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.DefaultLogger;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.wangyuanye.plugin.component.command.CmdTableModel;
import com.wangyuanye.plugin.component.toolWindow.MyToolWindow;
import com.wangyuanye.plugin.component.toolbar.SchemaComboBoxAction;
import com.wangyuanye.plugin.dao.dto.CmdSchema;
import com.wangyuanye.plugin.dao.dto.SchemaDataSave;
import com.wangyuanye.plugin.util.IdeaApiUtil;
import com.wangyuanye.plugin.util.MessagesUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * @author wangyuanye
 * @date 2024/8/21
 **/
public class ManageSchemaDialog extends DialogWrapper {
    public static Logger logger = new DefaultLogger("[schema dialog]");
    private static JBTable table;
    private Project project;

    public ManageSchemaDialog(@Nullable Project project) {
        super(project);
        setTitle(MessagesUtil.getMessage("manage_cate"));
        init();
        this.project = project;
    }

    public static JTable getTable() {
        return table;
    }

    @Override
    protected Action @NotNull [] createActions() {
        // Remove OK button by only returning the CancelAction
        Action cancelAction = getCancelAction();
        cancelAction.putValue(Action.NAME, "关闭"); // 修改按钮文本
        return new Action[]{cancelAction};
    }

    @Override
    public void setOKActionEnabled(boolean isEnabled) {
        super.setOKActionEnabled(false);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        SchemaDataSave schemaDataSave = ApplicationManager.getApplication().getService(SchemaDataSave.class);
        List<CmdSchema> schemaList = schemaDataSave.list();
        // 主面板，使用BorderLayout布局
        JPanel mainPanel = new JPanel(new BorderLayout());
        // 上方按钮面板
        JPanel topPanel = topBtnPanel();
        // 表格
        buildTable(schemaList);
        JScrollPane tabledPanel = new JBScrollPane(table);
        // 将上方按钮面板、表格和下方按钮面板添加到主面板中
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tabledPanel, BorderLayout.CENTER);
        mainPanel.setPreferredSize(new Dimension(400, 200));
        return mainPanel;
    }


    private JPanel topBtnPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn = new JButton(AllIcons.General.Add);
        JButton removeBtn = new JButton(AllIcons.General.Remove);
        btnSettings(addBtn);
        btnSettings(removeBtn);
        topPanel.add(addBtn);
        topPanel.add(removeBtn);
        topBtnAction(addBtn, removeBtn);// 添加事件
        return topPanel;
    }

    private void btnSettings(JButton btn) {
        btn.setMinimumSize(new Dimension(30, 30));
        btn.setMaximumSize(new Dimension(30, 30));
    }

    private void topBtnAction(JButton addBtn, JButton removeBtn) {
        addBtn.addActionListener(e -> {
            System.out.println("btn add ");
            DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
            tableModel.addRow(new Object[]{false, "", false, "", false, ""});
        });
        removeBtn.addActionListener(e -> {
            System.out.println("btn remove ");
            removeSchema();
        });
    }

    private void removeSchema() {
        Map<Integer, String> idMap = new HashMap<>();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        for (int i = 0; i < table.getRowCount(); i++) {
            Boolean isSelected = (Boolean) model.getValueAt(i, SchemaTable.col_checked);  // 获取第一列的值
            if (isSelected) {
                // 获取被勾选行的id
                idMap.put(i, (String) model.getValueAt(i, SchemaTable.col_id));
            }
        }
        if (!idMap.isEmpty()) {
            System.out.println("idMap.size: " + idMap.size());
            // todo 提示
//            ConfirmationDialog messageDialog = new ConfirmationDialog(project,"分类下的所有命令将被删除", "确认删除?", null,null);
//            if (!messageDialog.isOK()) {
//                return;
//            }
            System.out.println("点击了确认");
            // 删除schema,包括关联的cmd
            SchemaDataSave schemaDataSave = IdeaApiUtil.getSchemaService();
            schemaDataSave.deleteSchemaList(idMap.values().stream().toList());
            SchemaComboBoxAction anAction = (SchemaComboBoxAction) ActionManager.getInstance().getAction("ca_schema_command");
            CmdSchema selectedItem = (CmdSchema) anAction.getComboBox().getSelectedItem();
            // 移除table
            Set<Integer> indexSet = idMap.keySet();
            // 将 Set 转换为 List
            List<Integer> indexList = new ArrayList<>(indexSet);
            // 对 List 进行降序排序, 防止table索引混乱
            Collections.sort(indexList, Collections.reverseOrder());
            for (Integer id : indexList) {
                model.removeRow(id);
                // 如果删除的是当前分类,移除cmdTableModel的数据
                String schemaId = idMap.get(id);
                if (schemaId.equals(selectedItem.getId())) {
                    MyToolWindow toolWindow = IdeaApiUtil.getCurrentProjectToolWindow();
                    JTable cmdTable = toolWindow.getTable();
                    CmdTableModel cmdTableModel = (CmdTableModel) cmdTable.getModel();
                    for (int i = 0; i < cmdTableModel.getRowCount(); i++) {
                        cmdTableModel.removeRow(i);
                    }
                }
            }
            model.fireTableDataChanged();
            // 更新工具类combobox
            anAction.initComboBoxData(schemaDataSave.list());
            table.revalidate();
            table.repaint();
        }
    }

    private void buildTable(List<CmdSchema> schemaList) {
        table = new SchemaTable().createTable(schemaList);
    }
}
