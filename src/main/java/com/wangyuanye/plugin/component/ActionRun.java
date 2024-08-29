package com.wangyuanye.plugin.component;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.table.JBTable;
import com.wangyuanye.plugin.util.IdeaApiUtil;
import com.wangyuanye.plugin.util.MessagesUtil;
import com.wangyuanye.plugin.util.UiUtil;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wangyuanye
 * @date 2024/8/20
 **/
public class ActionRun extends AnAction {
    private JBTable commandTable;

    public ActionRun() {
        super("Run", "Run cmd", AllIcons.Actions.Execute);
    }

    public ActionRun(JBTable commandTable) {
        super("Run", "Run cmd", AllIcons.Actions.Execute);
        this.commandTable = commandTable;
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return super.getActionUpdateThread();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        if (commandTable == null) {
            e.getPresentation().setEnabled(false);
        } else {
            e.getPresentation().setEnabled(commandTable.getSelectedRow() != -1);
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        System.out.println("anaction run.....");
        System.out.println("run action");
        Project project = e.getProject();
        String cmd = getSelectCmd();
        if (cmd == null || cmd.isEmpty()) return;
        cmd = processCmd(cmd);
        if (project != null) {
            // 获取终端
            ToolWindow openTerminal = UiUtil.getOpenTerminal(IdeaApiUtil.getProject());
            runInTerminal(project, cmd, openTerminal);
        }
    }

    private String processCmd(String cmd) {
        String result = cmd;
        // lsof -i:{%Parm%}
        System.out.println("原始命令 : " + result);
        // 正则表达式来匹配 {%Parm%} 格式的占位符
        Pattern pattern = Pattern.compile("\\{%([a-zA-Z0-9_]+)%\\}");
        Matcher matcher = pattern.matcher(result);
        Map<String, String> map = new HashMap<>();
        Map<String, String> cmdKV = new HashMap<>();
        while (matcher.find()) {
            // 获取占位符中的参数名
            String parameterName = matcher.group(1);
            map.put(parameterName, "");
            cmdKV.put(parameterName, matcher.group(0));
        }
        if (map.size() > 0) {
            DialogParamInput inputDialog = new DialogParamInput(map);
            if (inputDialog.showAndGet()) {
                // 如果用户点击“OK”按钮，获取输入的值
                map = inputDialog.getMap();
            }
            for (String k : map.keySet()) {
                String value = map.get(k);
                String key = cmdKV.get(k);
                result = result.replace(key, value);
            }
        }
        System.out.println("待执行命令 : " + result);
        return result;
    }

    private void runInTerminal(@NotNull Project project, String cmd, ToolWindow openTerminal) {
        // 复制
        MessagesUtil.setClipboardContent(cmd);
        // 黏贴至终端
        MessagesUtil.pastToTerminal(project, openTerminal);
    }

    private String getSelectCmd() {
        MyCmdModel model = (MyCmdModel) commandTable.getModel();
        int selectedRow = commandTable.getSelectedRow();
        return (String) model.getValueAt(selectedRow, 0);
    }


}
