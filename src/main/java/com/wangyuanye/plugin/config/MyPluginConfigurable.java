package com.wangyuanye.plugin.config;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import com.wangyuanye.plugin.util.MessagesUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * 插件配置类
 *
 * @author yuanyewang515@gmail.com
 * @since v1.0
 **/
public class MyPluginConfigurable implements SearchableConfigurable {
    private final ConfigPersistent configPersistent;
    private JPanel mainPanel;
    // 创建标签
    final JBLabel shellPathLabel;
    final JBLabel pathLabel;
    final TextFieldWithBrowseButton shellPathField;
    final JBTextField pathField;


    public MyPluginConfigurable() {
        shellPathLabel = new JBLabel("Shell path:");
        pathLabel = new JBLabel("Path:");

        shellPathField = new TextFieldWithBrowseButton();
        shellPathField.setToolTipText(MessagesUtil.getMessage("config.shell.chose.tip"));
        this.configPersistent = ApplicationManager.getApplication().getService(ConfigPersistent.class);
        pathField = new JBTextField();

        pathField.setToolTipText(MessagesUtil.getMessage("config.path.tip"));
        String shellPath = "";
        if (configPersistent.getState() != null) {
            shellPath = configPersistent.getState().getShellPath();
            shellPathField.setText(configPersistent.getState().getShellPath());
            pathField.setText(configPersistent.getState().getPath());
        }
        if (shellPath.isEmpty()) {
            String os = System.getProperty("os.name");
            if (os.toLowerCase().contains("windows")) {
                shellPath = "C:\\Windows\\System32";
            } else {
                shellPath = "/bin/zsh";
            }
        }
        // 添加浏览文件夹的监听器
        String finalShellPath = shellPath;
        // 设置默认打开路径,后台执行
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            VirtualFile defaultDirectory = LocalFileSystem.getInstance().findFileByPath(finalShellPath);
            // 创建 FileChooserDescriptor
            FileChooserDescriptor fileChooserDescriptor = FileChooserDescriptorFactory.createSingleFileOrFolderDescriptor();
            // 如果找到了默认路径，则将其设置为文件选择器的根路径
            if (defaultDirectory != null) {
                fileChooserDescriptor.setRoots(defaultDirectory);
            }
            // 使用新版方法添加浏览按钮监听器
            shellPathField.addBrowseFolderListener(
                    "选择路径",             // 对话框标题
                    "请选择一个文件夹",    // 对话框描述
                    null,                   // 项目，可以传入 null
                    fileChooserDescriptor   // 文件选择器描述符
            );
        });
    }

    @Override
    public @NotNull @NonNls String getId() {
        return "com.wangyuanye.plugin.ca.config";
    }

    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "Command Assist Setting";
    }

    @Override
    public @Nullable JComponent createComponent() {
        mainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(shellPathLabel, shellPathField)
                .addLabeledComponent(pathLabel, pathField)
                .getPanel();
        mainPanel.setPreferredSize(new Dimension(200, 90));
        mainPanel.setMinimumSize(new Dimension(100, 90));
        mainPanel.setMaximumSize(new Dimension(200, 90));
        return mainPanel;
    }

    @Override
    public boolean isModified() {
        String sp = shellPathField.getText();
        String shellPath = null;
        if (configPersistent.getState() != null) {
            shellPath = configPersistent.getState().getShellPath();
        }
        if(!sp.equals(shellPath)) {
            return true;
        }
        String p = pathField.getText();
        String path = null;
        if (configPersistent.getState() != null) {
            path = configPersistent.getState().getPath();
        }
        if(!p.equals(path)) {
            return true;
        }
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        if (configPersistent.getState() != null) {
            configPersistent.getState().setShellPath(shellPathField.getText());
            configPersistent.getState().setPath(pathField.getText());
        }
    }
}
