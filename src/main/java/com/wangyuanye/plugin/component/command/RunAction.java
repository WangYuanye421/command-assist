package com.wangyuanye.plugin.component.command;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.DefaultLogger;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.wangyuanye.plugin.component.toolWindow.MyToolWindow;
import com.wangyuanye.plugin.component.toolWindow.MyToolWindowFactory;
import com.wangyuanye.plugin.services.MyService;
import com.wangyuanye.plugin.services.MyServiceImpl;
import com.wangyuanye.plugin.util.MessagesUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wangyuanye
 * @date 2024/8/20
 **/
public class RunAction extends AnAction {
    public static Logger logger = new DefaultLogger("[Cmd Run Action]");
    private MyService myService;

    public RunAction() {
        super("run", "run cmd", AllIcons.Actions.Execute);
        this.myService = MyServiceImpl.instance;
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        String cmd = getSelectCmd();
        if (cmd == null || cmd.isEmpty()) return;
        cmd = processCmd(cmd);
        if (project != null) {
            // 获取终端
            ToolWindow openTerminal = MyToolWindowFactory.getOpenTerminal();
            runInTerminal(project, cmd, openTerminal);
        }
    }

    private String processCmd(String cmd) {
        String result = cmd;
        // lsof -i:{%Parm%}
        logger.info("原始命令 : " + result);
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
            ParamInputDialog inputDialog = new ParamInputDialog(map);
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
        logger.info("待执行命令 : " + result);
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
        JTable table = MyToolWindow.getTable();
        CmdTableModel model = (CmdTableModel) table.getModel();
        int selectedRow = table.getSelectedRow();
        return (String) model.getValueAt(selectedRow, CmdTable.col_cmd_name);
    }


}
