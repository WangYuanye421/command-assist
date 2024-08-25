package com.wangyuanye.plugin.setting;


import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.util.NlsContexts;
import com.wangyuanye.plugin.persistence.PluginSettingState;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * 将插件设置界面集成到IDEA的设置界面中
 *
 * @author wangyuanye
 * @date 2024/8/15
 **/
public class MyPluginConfigurable implements Configurable {

    private PluginSettingState settings;


    @Override
    public @NlsContexts.ConfigurableName String getDisplayName() {
        return "xxxxx";
    }

    @Override
    public @Nullable JComponent createComponent() {

        return new JPanel();
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
        System.out.println("apply ..............");
    }


}
