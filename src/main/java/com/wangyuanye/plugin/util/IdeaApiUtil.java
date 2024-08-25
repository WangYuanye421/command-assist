package com.wangyuanye.plugin.util;

import com.intellij.ide.ui.UISettings;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.wangyuanye.plugin.component.toolWindow.MyToolWindow;
import com.wangyuanye.plugin.dao.dto.CmdDataSave;
import com.wangyuanye.plugin.dao.dto.SchemaDataSave;

/**
 * 插件开发常用api
 *
 * @author wangyuanye
 * @date 2024/8/20
 **/
public class IdeaApiUtil {

    public static MyToolWindow getAppService() {
        return ApplicationManager.getApplication().getService(MyToolWindow.class);
    }

    public static SchemaDataSave getSchemaService() {
        SchemaDataSave service = ApplicationManager.getApplication().getService(SchemaDataSave.class);
        return service;
    }

    public static CmdDataSave getCmdService() {
        return ApplicationManager.getApplication().getService(CmdDataSave.class);
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
