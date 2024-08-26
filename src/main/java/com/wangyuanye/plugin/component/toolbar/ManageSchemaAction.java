package com.wangyuanye.plugin.component.toolbar;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.ProjectManager;
import com.wangyuanye.plugin.component.schema.ManageSchemaDialog;
import org.jetbrains.annotations.NotNull;

/**
 * 管理分类
 *
 * @author wangyuanye
 * @date 2024/8/19
 **/
public class ManageSchemaAction extends AnAction {

    public ManageSchemaAction() {
        super("管理分类", "操作分类数据", AllIcons.Modules.EditFolder);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println("打开dialog");
        ManageSchemaDialog dialog = new ManageSchemaDialog(ProjectManager.getInstance().getDefaultProject());
        dialog.show();
    }
}
