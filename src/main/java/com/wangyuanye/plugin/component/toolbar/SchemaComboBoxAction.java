package com.wangyuanye.plugin.component.toolbar;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.openapi.ui.ComboBox;
import com.wangyuanye.plugin.component.toolWindow.MyToolWindow;
import com.wangyuanye.plugin.dao.dto.CmdSchema;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 下拉框
 *
 * @author wangyuanye
 * @date 2024/8/19
 **/
public class SchemaComboBoxAction extends AnAction implements CustomComponentAction {
    private static ComboBox<CmdSchema> comboBox;


    public SchemaComboBoxAction() {
        if (comboBox == null) {
            comboBox = new ComboBox<>();
        }
        comboBox.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel();
            if (value != null) {
                label.setText(value.getName());
            }
            if (isSelected) {
                label.setBackground(list.getSelectionBackground());
                label.setForeground(list.getSelectionForeground());
            } else {
                label.setBackground(list.getBackground());
                label.setForeground(list.getForeground());
            }
            label.setOpaque(true);
            return label;
        });
    }

    public SchemaComboBoxAction(List<CmdSchema> schemaNames) {
        this();
        if (schemaNames == null) {
            schemaNames = new ArrayList<>();
        }
        initComboBoxData(schemaNames);

    }


    public void onItemChange(MyToolWindow myToolWindow) {
        comboBox.addItemListener(e -> {
            // 当选中项发生变化时触发的代码
            CmdSchema cmdSchema = (CmdSchema) comboBox.getSelectedItem();
            if (cmdSchema != null) {
                myToolWindow.refreshTable(cmdSchema.getId());
            }
        });
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
    }

    @Override
    public @NotNull JComponent createCustomComponent(@NotNull Presentation presentation, @NotNull String place) {
        return comboBox;
    }

    public void initComboBoxData(List<CmdSchema> schemas) {
        comboBox.removeAllItems();
        // 加载数据
        schemas.forEach(comboBox::addItem);
        if (schemas.size() > 0 && comboBox.getSelectedItem() == null) {
            comboBox.setSelectedItem(schemas.get(0));
        }
        comboBox.repaint();
    }

    public ComboBox<CmdSchema> getComboBox() {
        return comboBox;
    }
}
