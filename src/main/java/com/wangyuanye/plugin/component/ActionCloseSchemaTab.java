package com.wangyuanye.plugin.component;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.TabInfo;
import com.wangyuanye.plugin.util.MessagesUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * 关闭分类tab
 *
 * @author wangyuanye
 * 2024/8/29
 **/
public class ActionCloseSchemaTab extends AnAction {
    private JBTabs jbTabs;

    public ActionCloseSchemaTab() {
        super("Close Tab", MessagesUtil.getMessage("schema.tab.close"), AllIcons.Actions.Cancel);
    }

    public ActionCloseSchemaTab(JBTabs jbTabs) {
        super("Close Tab", MessagesUtil.getMessage("schema.tab.close"), AllIcons.Actions.Cancel);
        this.jbTabs = jbTabs;
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        List<TabInfo> tabs = jbTabs.getTabs();
        Optional<TabInfo> first = tabs.stream().filter(t -> SchemaTab.TAB_NAME.equals(t.getText())).findFirst();
        first.ifPresent(tabInfo -> jbTabs.removeTab(tabInfo));

    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT; // fix `ActionUpdateThread.OLD_EDT` is deprecated
    }
}
