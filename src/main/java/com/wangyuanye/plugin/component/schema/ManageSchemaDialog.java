package com.wangyuanye.plugin.component.schema;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.DefaultLogger;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.table.JBTable;
import com.wangyuanye.plugin.dao.dto.CmdSchema;
import com.wangyuanye.plugin.dao.dto.SchemaDataSave;
import com.wangyuanye.plugin.util.MessagesUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * @author wangyuanye
 * @date 2024/8/21
 **/
public class ManageSchemaDialog extends DialogWrapper {
    public static Logger logger = new DefaultLogger("[schema dialog]");
    private static JBTable table;

    public ManageSchemaDialog(@Nullable Project project) {
        super(project);
        setTitle(MessagesUtil.getMessage("manage_cate"));
        init();
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
            tableModel.addRow(new Object[]{null, null, null, null, true});
        });
        removeBtn.addActionListener(e -> {
            System.out.println("btn remove ");
        });
    }

    private void buildTable(List<CmdSchema> schemaList) {
        table = new SchemaTable().createTable(schemaList);
    }
}
