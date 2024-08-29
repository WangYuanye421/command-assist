package com.wangyuanye.plugin.util;

import com.intellij.ide.ui.UISettings;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.project.Project;
import com.wangyuanye.plugin.dao.CmdDataSave;
import com.wangyuanye.plugin.dao.SchemaDataSave;
import com.wangyuanye.plugin.toolWindow.PluginWindow;
import org.jetbrains.annotations.NotNull;


/**
 * 插件开发常用api
 *
 * @author wangyuanye
 * @date 2024/8/20
 **/
public class IdeaApiUtil {

    /**
     * 获取当前项目
     *
     * @return
     */
    public static @NotNull Project getProject() {
        PluginWindow projectToolWindow = getToolWindow();
        return projectToolWindow.getCurrentProject();
    }

    public static PluginWindow getToolWindow() {
        return ApplicationManager.getApplication().getService(PluginWindow.class);
    }

    public static SchemaDataSave getSchemaService() {
        return ApplicationManager.getApplication().getService(SchemaDataSave.class);
    }

    public static CmdDataSave getCmdService() {
        return ApplicationManager.getApplication().getService(CmdDataSave.class);
    }

    /**
     * 获取通知对象
     *
     * @return Notification
     */
    public static Notification getNotification() {
        return new Notification(
                "CommandAssistNotificationGroup", // 通知组ID
                "Command Assist 通知",        // 通知标题
                NotificationType.INFORMATION // 通知类型 (INFORMATION, WARNING, ERROR)
        );
    }

    /**
     * 发送提示信息
     *
     * @param tip 提示信息
     */
    public static void myTips(String tip) {
        Notification notification = getNotification();
        notification.setContent(MessagesUtil.buildBalloon(tip));
        notification.notify(getProject());
    }

    /**
     * IDEA全局UI配置
     * Font globalFont = uiSettings.getFontFace();
     * int globalFontSize = uiSettings.getFontSize();
     *
     * @return
     */
    public static UISettings getGlobalUiSettings() {
        return UISettings.getInstance();
    }

    public static String getUiFont() {
        String fontFace = UISettings.getInstance().getFontFace();
        return fontFace.isEmpty() ? "JetBrains Mono" : fontFace;
    }

    public static int getUiFontSize() {
        int fontSize = UISettings.getInstance().getFontSize();
        return fontSize == 0 ? 14 : fontSize;
    }

    /**
     * IDEA全局编辑器配置
     * String fontName = scheme.getEditorFontName();
     * int fontSize = scheme.getEditorFontSize();
     *
     * @return
     */
    public static EditorColorsScheme getGlobalEditorSettings() {
        return EditorColorsManager.getInstance().getGlobalScheme();
    }

    public static String getEditorFont() {
        EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();
        String fontName = scheme.getEditorFontName();
        return fontName.isEmpty() ? "JetBrains Mono" : fontName;
    }

    public static int getEditorFontSize() {
        EditorColorsScheme scheme = EditorColorsManager.getInstance().getGlobalScheme();
        int fontSize = scheme.getEditorFontSize();
        return fontSize == 0 ? 14 : fontSize;
    }

}
