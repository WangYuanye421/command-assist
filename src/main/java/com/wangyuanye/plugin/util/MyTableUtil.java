package com.wangyuanye.plugin.util;

import com.intellij.icons.AllIcons;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.table.JBTable;

import static com.intellij.ui.SimpleTextAttributes.STYLE_ITALIC;

/**
 * @author wangyuanye
 * @date 2024/8/19
 **/
public class MyTableUtil {

    public static void setEmptyText(JBTable table) {
        table.getEmptyText().setText(MessagesUtil.getMessage("no_data"));
        table.getEmptyText().appendLine(AllIcons.Modules.EditFolder, MessagesUtil.getMessage("no_data_tip1"),
                new SimpleTextAttributes(STYLE_ITALIC, JBColor.darkGray), null);
        table.getEmptyText().appendLine(AllIcons.General.Add, MessagesUtil.getMessage("no_data_tip2"),
                new SimpleTextAttributes(STYLE_ITALIC, JBColor.darkGray), null);
    }
}
