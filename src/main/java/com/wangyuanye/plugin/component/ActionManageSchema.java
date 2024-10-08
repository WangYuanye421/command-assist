package com.wangyuanye.plugin.component;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.tabs.JBTabs;
import com.intellij.ui.tabs.TabInfo;
import com.wangyuanye.plugin.util.IdeaApiUtil;
import com.wangyuanye.plugin.util.MessagesUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * 管理分类
 *
 * @author wangyuanye
 * 2024/8/19
 **/
public class ActionManageSchema extends AnAction {
    private JBTabs jbTabs;
    private SchemaTab schemaTab;
    private ActionSchemaComboBox actionSchemaComboBox;

    public ActionManageSchema() {
        super(MessagesUtil.getMessage("schema.manage_btn.text"), MessagesUtil.getMessage("schema.manage_btn.desc"), AllIcons.Modules.EditFolder);
    }

    public ActionManageSchema(JBTabs jbTabs, SchemaTab schemaTab, ActionSchemaComboBox combobox) {
        super(MessagesUtil.getMessage("schema.manage_btn.text"), MessagesUtil.getMessage("schema.manage_btn.desc"), AllIcons.Modules.EditFolder);
        this.jbTabs = jbTabs;
        this.schemaTab = schemaTab;
        this.actionSchemaComboBox = combobox;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        List<TabInfo> tabs = jbTabs.getTabs();
        Optional<TabInfo> first = tabs.stream().filter(t -> SchemaTab.TAB_NAME.equals(t.getText())).findFirst();
        if (first.isPresent()) {
            IdeaApiUtil.myTips(MessagesUtil.getMessage("schema.tab.opened"));
            return;
        }
        TabInfo tab = schemaTab.buildSchemaTab(jbTabs, actionSchemaComboBox);
        jbTabs.addTab(tab);
        jbTabs.select(tab, true);// 激活当前tab
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT; // fix `ActionUpdateThread.OLD_EDT` is deprecated
    }
}
