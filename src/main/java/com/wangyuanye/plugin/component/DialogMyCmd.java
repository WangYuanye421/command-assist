package com.wangyuanye.plugin.component;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBTextArea;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.wangyuanye.plugin.dao.dto.MyCmd;
import com.wangyuanye.plugin.util.MessagesUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

/**
 * @author wangyuanye
 * @date 2024/8/28
 **/
public class DialogMyCmd extends DialogWrapper {
    private final MyCmd myCmd;
    private final JBTextField cmdField;
    private final JBTextField remarkField;
    private final int myCmdIndex;
    private final List<MyCmd> myExistingMyCmdList;
    private final JLabel helpIcon;
    private Component parent;

    DialogMyCmd(Component parent, MyCmd myCmd, int cmdIndex, List<MyCmd> existingMyCmdList) {
        super(parent, true);
        this.parent = parent;
        setSize(300, 120);
        myCmdIndex = cmdIndex;
        myExistingMyCmdList = existingMyCmdList;
        setTitle(MessagesUtil.getMessage("cmd.dialog.add.title"));
        setResizable(false);
        this.myCmd = myCmd;
        cmdField = new JBTextField(this.myCmd.getName());

        remarkField = new JBTextField(this.myCmd.getRemark());
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
        // 第一个文本框和提示标签
        JPanel panel1 = new JPanel(new BorderLayout());
        panel1.add(cmdField, BorderLayout.CENTER);
        panel1.add(helpIcon, BorderLayout.EAST);

        JPanel panel2 = new JPanel(new BorderLayout());
        panel2.add(remarkField, BorderLayout.CENTER);
        JLabel placeholder = new JLabel();
        placeholder.setPreferredSize(helpIcon.getPreferredSize());
        panel2.add(placeholder, BorderLayout.EAST);

        return FormBuilder.createFormBuilder()
                .addLabeledComponent(MessagesUtil.getMessage("cmd.dialog.label_name"), panel1)
                .addLabeledComponent(MessagesUtil.getMessage("cmd.dialog.label_remark"), panel2)
                .getPanel();
    }
}
