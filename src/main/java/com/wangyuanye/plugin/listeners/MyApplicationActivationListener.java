package com.wangyuanye.plugin.listeners;

import com.intellij.openapi.application.ApplicationActivationListener;
import com.intellij.openapi.wm.IdeFrame;
import org.jetbrains.annotations.NotNull;

public class MyApplicationActivationListener implements ApplicationActivationListener {

    @Override
    public void applicationActivated(@NotNull IdeFrame ideFrame) {
        System.out.println("MyApplicationActivationListener ============");
    }
}
