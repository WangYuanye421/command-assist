package com.wangyuanye.plugin.component;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.ui.tabs.impl.JBTabsImpl;
import com.wangyuanye.plugin.util.IdeaApiUtil;
import com.wangyuanye.plugin.util.MessagesUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * 管理分类
 *
 * @author wangyuanye
 * @date 2024/8/19
 **/
public class ActionManageSchema extends AnAction {
    private JBTabsImpl jbTabs;
    private SchemaTab schemaTab;
    private ActionSchemaComboBox actionSchemaComboBox;

    public ActionManageSchema() {
        super("管理分类", "操作分类数据", AllIcons.Modules.EditFolder);
    }

    public ActionManageSchema(JBTabsImpl jbTabs, SchemaTab schemaTab, ActionSchemaComboBox combobox) {
        super("管理分类", "操作分类数据", AllIcons.Modules.EditFolder);
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
        TabInfo tabInfo = schemaTab.buildSchemaTab(jbTabs, actionSchemaComboBox);
        TabInfo tab = tabInfo;
        jbTabs.addTab(tab);
        jbTabs.select(tab, true);// 激活当前tab


    }
}
