package com.wangyuanye.plugin.component;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 参数输入框
 *
 * @author wangyuanye
 * @date 2024/8/23
 **/
public class DialogParamInput extends DialogWrapper {
    private final Map<String, JTextField> textFieldMap = new HashMap<>();
    private final Map<String, String> map;

    public DialogParamInput(Map<String, String> map) {
        super(true); // use current window as parent
        this.map = map;
        init();
        setTitle("输入参数: ");
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridLayout(map.size(), 2));
        for (String k : map.keySet()) {
            JLabel label = new JLabel(k + ":");
            JTextField textField = new JTextField();
            textFieldMap.put(k, textField); // 存储 JTextField 的引用
            panel.add(label);
            panel.add(textField);
        }
        return panel;
    }

    @Override
    protected void doOKAction() {
        // 在用户点击“确定”时，获取所有文本框的输入内容
        for (Map.Entry<String, JTextField> entry : textFieldMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().getText();
            map.put(key, value); // 将用户输入的值更新到原来的 map 中
        }
        super.doOKAction(); // 关闭对话框
    }

    public Map<String, String> getMap() {
        return map;
    }
}
