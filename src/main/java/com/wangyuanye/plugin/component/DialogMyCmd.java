package com.wangyuanye.plugin.component;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.wangyuanye.plugin.dao.dto.MyCmd;
import com.wangyuanye.plugin.util.MessagesUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Collections;
import java.util.List;

/**
 * @author wangyuanye
 * 2024/8/28
 **/
public class DialogMyCmd extends DialogWrapper {
    private final MyCmd myCmd;
    private final JScrollPane scrollPane;
    private final JTextArea cmdField;
    private final JBTextField remarkField;
    private final int myCmdIndex;
    private final List<MyCmd> myExistingMyCmdList;
    private final JLabel helpIcon;

    DialogMyCmd(Component parent, MyCmd myCmd, int cmdIndex, List<MyCmd> existingMyCmdList) {
        super(parent, true);
        myCmdIndex = cmdIndex;
        myExistingMyCmdList = existingMyCmdList;
        setTitle(MessagesUtil.getMessage("cmd.dialog.add.title"));
        setResizable(false);
        this.myCmd = myCmd;

        // 初始化组件
        cmdField = new JTextArea();
        cmdField.setLineWrap(true);
        cmdField.setWrapStyleWord(true);
        cmdField.setText(myCmd.getName());

        // 使用 JScrollPane 包裹 JTextArea
        scrollPane = new JBScrollPane(cmdField);
        scrollPane.setPreferredSize(new Dimension(300, 100)); // 设置尺寸

        remarkField = new JBTextField();
        remarkField.setText(myCmd.getRemark());
        cmdField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                cmdField.setBorder(BorderFactory.createLineBorder(new JBColor(
                        new Color(156, 198, 243),
                        new Color(71, 105, 139)), 2));  // 焦点时的边框样式
            }

            @Override
            public void focusLost(FocusEvent e) {
                cmdField.setBorder(remarkField.getBorder());  // 失去焦点后恢复默认边框
            }
        });

        helpIcon = new JLabel(AllIcons.General.ContextHelp);
        helpIcon.setToolTipText(MessagesUtil.getMessage("cmd.name_label_tip"));
        init();
    }


    // 打开dialog后，光标聚焦组件
    @Override
    public JComponent getPreferredFocusedComponent() {
        return cmdField;
    }

    @Override
    protected void doOKAction() {
        myCmd.setName(cmdField.getText().trim());
        myCmd.setRemark(remarkField.getText().trim());
        super.doOKAction();
    }

    @NotNull
    @Override
    protected List<ValidationInfo> doValidateAll() {
        String cmdText = cmdField.getText().trim();
        if (cmdText.isEmpty()) {
            return Collections.singletonList(new ValidationInfo(MessagesUtil.getMessage("cmd.add.error_name"), cmdField));
        }
        for (int i = 0; i < myExistingMyCmdList.size(); i++) {
            MyCmd pattern = myExistingMyCmdList.get(i);
            if (myCmdIndex != i && cmdText.equals(pattern.getName())) {
                return Collections.singletonList(new ValidationInfo(MessagesUtil.getMessage("cmd.add.error_name_exist"), cmdField));
            }
        }
        return super.doValidateAll();
    }

    @Override
    protected JComponent createCenterPanel() {
        // 使用 FormBuilder 来布局，确保每个组件有合适的标签和位置
        return FormBuilder.createFormBuilder()
                .addLabeledComponent("命令:", scrollPane) // 带滚动条的文本区域
                .addLabeledComponent("备注:", remarkField) // 普通文本框
                .getPanel();
    }
}
