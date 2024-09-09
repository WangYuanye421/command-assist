package com.wangyuanye.plugin.component;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 参数输入框
 *
 * @author wangyuanye
 * 2024/8/23
 **/
public class DialogParamInput extends DialogWrapper {
    private final Map<String, JTextField> textFieldMap = new HashMap<>();
    private final Map<String, String> map;

    public DialogParamInput(Map<String, String> map) {
        super(true); // use current window as parent
        this.map = map;
        setResizable(false);
        init();
        setTitle("输入参数: ");
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = JBUI.insets(5, 0); // 设置组件之间的上下左右间距
        int row = 0; // 记录当前的行数

        for (String k : map.keySet()) {
            // 设置 label 占据第一列
            gbc.gridx = 0;
            gbc.gridy = row;
            gbc.weightx = 0.2; // label 占较小的空间
            JLabel label = new JLabel(k + (k.equals("sudo") ? " pwd:" : ":"));
            panel.add(label, gbc);
            // 设置 textField 占据第二列
            gbc.gridx = 1;
            gbc.weightx = 0.8; // textField 占较多的空间
            if ("sudo".equals(k)) {
                JPasswordField passwordField = new JPasswordField();
                textFieldMap.put(k, passwordField);
                panel.add(passwordField, gbc);
            } else {
                JTextField textField = new JTextField();
                textFieldMap.put(k, textField);
                panel.add(textField, gbc);
            }
            row++; // 每添加一行，行数增加
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
