package com.wangyuanye.plugin.component;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.wangyuanye.plugin.dao.dto.MySchema;
import com.wangyuanye.plugin.util.MessagesUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author wangyuanye
 * 2024/8/28
 **/
public class DialogMySchema extends DialogWrapper {
    private final MySchema mySchema;
    private final JBTextField nameField;
    private final JBCheckBox isDefaultcheckBox;
    private final int mySchemaIndex;
    private final List<MySchema> myExistingSchemaList;

    DialogMySchema(Component parent, MySchema schema, int cmdIndex, List<MySchema> existingSchemaList) {
        super(parent, true);
        setSize(300, 120);
        mySchemaIndex = cmdIndex;
        myExistingSchemaList = existingSchemaList;
        setTitle(MessagesUtil.getMessage("schema.dialog.add.title"));
        setResizable(false);
        mySchema = schema;
        nameField = new JBTextField(mySchema.getName());
        isDefaultcheckBox = new JBCheckBox();
        isDefaultcheckBox.setSelected(mySchema.getDefaultSchema());
        init();
    }

    // 打开dialog后，光标聚焦组件
    @Override
    public JComponent getPreferredFocusedComponent() {
        return nameField;
    }

    @Override
    protected void doOKAction() {
        mySchema.setName(nameField.getText().trim());
        mySchema.setDefaultSchema(isDefaultcheckBox.isSelected());
        super.doOKAction();
    }

    // 不做校验
    @NotNull
    @Override
    protected List<ValidationInfo> doValidateAll() {
        String nameText = nameField.getText().trim();
        boolean selected = isDefaultcheckBox.isSelected();
        if (nameText.isEmpty()) {
            return Collections.singletonList(new ValidationInfo(MessagesUtil.getMessage("schema.add.error_name"), nameField));
        }
        for (int i = 0; i < myExistingSchemaList.size(); i++) {
            MySchema existSchema = myExistingSchemaList.get(i);
            if (mySchemaIndex != i && nameText.equals(existSchema.getName())) {
                return Collections.singletonList(new ValidationInfo(MessagesUtil.getMessage("schema.add.error_name_exist"), nameField));
            }
        }
        // 过滤出 默认为true的
        Optional<MySchema> first = myExistingSchemaList.stream().filter(MySchema::getDefaultSchema).findFirst();
        if (first.isPresent() && selected) {
            return Collections.singletonList(new ValidationInfo(MessagesUtil.getMessage("schema.add.error_default_exist"), isDefaultcheckBox));
        }
        return super.doValidateAll();
    }

    @Override
    protected JComponent createCenterPanel() {
        return FormBuilder.createFormBuilder()
                .addLabeledComponent(MessagesUtil.getMessage("schema.dialog.label_name"), nameField)
                .addLabeledComponent(MessagesUtil.getMessage("schema.dialog.label_default"), isDefaultcheckBox)
                .getPanel();
    }
}
