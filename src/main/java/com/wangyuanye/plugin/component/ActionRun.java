package com.wangyuanye.plugin.component;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.JBColor;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.table.JBTable;
import com.wangyuanye.plugin.util.IdeaApiUtil;
import com.wangyuanye.plugin.util.MessagesUtil;
import com.wangyuanye.plugin.util.UiUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wangyuanye
 * @date 2024/8/20
 **/
public class ActionRun extends AnAction {
    private static final Logger logger = Logger.getInstance(ActionRun.class);
    private JBTable commandTable;

    public ActionRun() {
        super(MessagesUtil.getMessage("cmd.run_btn.text"), MessagesUtil.getMessage("cmd.run_btn.desc"), AllIcons.Actions.Execute);
    }

    public ActionRun(JBTable commandTable) {
        super(MessagesUtil.getMessage("cmd.run_btn.text"), MessagesUtil.getMessage("cmd.run_btn.desc"), AllIcons.Actions.Execute);
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
        Project project = IdeaApiUtil.getProject();
        run(project);
    }


    // 方式一: 使用默认终端
    private void run(Project project) {
        String cmd = getSelectCmd();
        if (cmd == null || cmd.isEmpty()) return;
        cmd = processCmd(cmd);
        doRun(project, cmd);
    }

    private void doRun(Project project, String cmd) {
        // 获取终端
        ToolWindow openTerminal = UiUtil.getOpenTerminal(IdeaApiUtil.getProject());
        runInTerminal(project, cmd, openTerminal);
    }

    private String processCmd(String cmd) {
        String result = cmd;
        // lsof -i:{%Parm%}
        logger.info("原始命令 : " + result);
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
        if (!map.isEmpty()) {
            DialogParamInput inputDialog = new DialogParamInput(map);
            IdeaApiUtil.setRelatedLocation(inputDialog);
            if (inputDialog.showAndGet()) {
                // 如果用户点击“OK”按钮，获取输入的值
                map = inputDialog.getMap();
            } else {
                return cmd;
            }
            for (String k : map.keySet()) {
                String value = map.get(k);
                String key = cmdKV.get(k);
                result = result.replace(key, value);
            }
        }
        logger.info("待执行命令 : " + result);
        return result;
    }

    private void runInTerminal(@NotNull Project project, String cmd, ToolWindow openTerminal) {
        // 复制
        MessagesUtil.setClipboardContent(cmd);
        // 粘贴至终端
        MessagesUtil.pastToTerminal(project, openTerminal);
    }

    private String getSelectCmd() {
        MyCmdModel model = (MyCmdModel) commandTable.getModel();
        int selectedRow = commandTable.getSelectedRow();
        return (String) model.getValueAt(selectedRow, 0);
    }

    // 使用consoleView
    private void doRun2(Project project, String cmd) {
        String basePath = project.getBasePath();
        if (basePath == null) {
            logger.error("Project basePath is null");
            return;
        }
        String shellPath = "/bin/sh";
        String os = System.getProperty("os.name");
        logger.info("当前用户os : " + os);
        if (os.toLowerCase().contains("mac")) {
            shellPath = "/bin/zsh";
        } else if (os.toLowerCase().contains("windows")) {
            shellPath = "cmd.exe";
        }

        GeneralCommandLine commandLine = new GeneralCommandLine(shellPath, "-c", "cd " + basePath + " && " + cmd);
        // 默认当前项目
        commandLine.setWorkDirectory(new File(basePath));

        commandLine.withCharset(StandardCharsets.UTF_8);
        OSProcessHandler processHandler = null;
        try {
            processHandler = new OSProcessHandler(commandLine);
        } catch (ExecutionException e) {
            logger.error(e.toString());
            throw new RuntimeException(e);
        }
        ConsoleView consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
        processHandler.addProcessListener(new ProcessAdapter() {
            @Override
            public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                consoleView.print(event.getText(), ConsoleViewContentType.NORMAL_OUTPUT);
            }
        });

        ToolWindow terminal = ToolWindowManager.getInstance(project).getToolWindow("Terminal");
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(consoleView.getComponent(), buildTabName(cmd), false);
        content.setTabColor(new JBColor(0x96F61D1D, 0x96F61D1D));
        if (terminal != null) {
            terminal.getContentManager().addContent(content);
            terminal.activate(null);
        } else {
            logger.error("terminal 为null");
        }
        processHandler.startNotify();
    }

    private String buildTabName(String cmd) {
        if (cmd.length() <= 10) {
            return cmd;
        } else {
            return cmd.substring(0, 7) + "...";
        }
    }
}

