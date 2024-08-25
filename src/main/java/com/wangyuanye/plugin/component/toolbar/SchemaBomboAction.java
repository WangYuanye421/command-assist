package com.wangyuanye.plugin.component.toolbar;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.openapi.ui.ComboBox;
import com.wangyuanye.plugin.component.toolWindow.MyToolWindow;
import com.wangyuanye.plugin.util.MessagesUtil;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;

/**
 * 下拉框
 *
 * @author wangyuanye
 * @date 2024/8/19
 **/
public class SchemaBomboAction extends AnAction implements CustomComponentAction {
    private ComboBox<String> comboBox;

    public SchemaBomboAction() {
    }

    public SchemaBomboAction(String currentName, List<String> schemaNames) {
        comboBox = new ComboBox<>();
        initComboBoxData(currentName, schemaNames);

    }

    public void onItemChange(MyToolWindow myToolWindow) {
        comboBox.addActionListener(e -> {
            // 当选中项发生变化时触发的代码
            String selectedValue = (String) comboBox.getSelectedItem();
            System.out.println("selectedValue : " + selectedValue);
            comboBox.setSelectedItem(selectedValue);
            myToolWindow.refreshTable(selectedValue);
        });
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
    }

    @Override
    public @NotNull JComponent createCustomComponent(@NotNull Presentation presentation, @NotNull String place) {
        return comboBox;
    }

    private void initComboBoxData(String defaultSchema, List<String> schemaNames) {
        // 加载数据
        if (!CollectionUtils.isEmpty(schemaNames)) {
            schemaNames.forEach(comboBox::addItem);
            comboBox.setSelectedItem(defaultSchema); // 当前选择值
        } else {
            comboBox.addItem(MessagesUtil.getMessage("box_place"));
            comboBox.setSelectedIndex(0); // 设置提示项为默认选项
        }
    }
}
