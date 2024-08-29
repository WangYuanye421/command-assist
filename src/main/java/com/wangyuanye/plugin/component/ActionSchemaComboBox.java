package com.wangyuanye.plugin.component;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.openapi.ui.ComboBox;
import com.wangyuanye.plugin.dao.dto.MySchema;
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
public class ActionSchemaComboBox extends AnAction implements CustomComponentAction {
    private static ComboBox<MySchema> comboBox;
    private CommandTab commandTab;


    public ActionSchemaComboBox() {
        if (comboBox == null) {
            comboBox = new ComboBox<>();
        }
        comboBox.setMinimumAndPreferredWidth(120);
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

    public ActionSchemaComboBox(List<MySchema> schemaNames, CommandTab commandTab) {
        this();
        if (schemaNames == null) {
            schemaNames = new ArrayList<>();
        }
        this.commandTab = commandTab;
        initComboBoxData(schemaNames, false);

    }


    public void onItemChange() {
        comboBox.addItemListener(e -> {
            // 当选中项发生变化时触发的代码
            MySchema mySchema = (MySchema) comboBox.getSelectedItem();
            if (mySchema != null) {
                this.commandTab.refreshTable(mySchema.getId());
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

    public void initComboBoxData(List<MySchema> schemas, Boolean freshMode) {
        MySchema oldSelect = (MySchema) comboBox.getSelectedItem();
        comboBox.setEnabled(true);
        comboBox.removeAllItems();
        // 加载数据
        if (schemas == null || schemas.isEmpty()) {// 空数据,直接返回占位符对象
            MySchema empty = MySchema.getEmptyObj();
            comboBox.addItem(empty);
            comboBox.setEnabled(false);
            return;
        }
        schemas.forEach(comboBox::addItem);
        MySchema defaultSchema = null;
        for (MySchema schema : schemas) {
            if (schema.getDefaultSchema()) {
                defaultSchema = schema;
                break;
            }
        }
        if (freshMode) {
            if (oldSelect == null || oldSelect.getId() == null) {// 刷新模式,如果之前是空对象,则设置默认分类
                if (defaultSchema != null) {
                    comboBox.setSelectedItem(defaultSchema);
                } else {
                    comboBox.setSelectedItem(schemas.get(0));
                }
            } else {
                schemas.forEach(e -> {
                    if (e.getId().equals(oldSelect.getId())) {// 如果不是空对象,则更新分类,不切换分类
                        oldSelect.setName(e.getName());
                        oldSelect.setDefaultSchema(e.getDefaultSchema());
                        comboBox.setSelectedItem(oldSelect);
                    }
                });
            }
        } else {// 切换分类
            if (defaultSchema != null) {
                comboBox.setSelectedItem(defaultSchema);
            } else {
                comboBox.setSelectedItem(schemas.get(0));
            }
        }
    }

    public ComboBox<MySchema> getComboBox() {
        return comboBox;
    }
}
