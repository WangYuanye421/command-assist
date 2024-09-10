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
import com.intellij.ui.content.ContentManagerEvent;
import com.intellij.ui.content.ContentManagerListener;
import com.intellij.ui.table.JBTable;
import com.wangyuanye.plugin.util.IdeaApiUtil;
import com.wangyuanye.plugin.util.MessagesUtil;
import com.wangyuanye.plugin.util.UiUtil;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wangyuanye
 * 2024/8/20
 **/
public class ActionRun extends AnAction {
    private static final Logger logger = Logger.getInstance(ActionRun.class);
    private JBTable commandTable;

    public ActionRun() {
        super(MessagesUtil.getMessage("cmd.toolbar.run.text"), MessagesUtil.getMessage("cmd.toolbar.run.text"), AllIcons.Actions.Execute);
    }

    public ActionRun(JBTable commandTable) {
        super(MessagesUtil.getMessage("cmd.toolbar.run.text"), MessagesUtil.getMessage("cmd.toolbar.run.text"), AllIcons.Actions.Execute);
        this.commandTable = commandTable;
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT; // fix `ActionUpdateThread.OLD_EDT` is deprecated
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
        logger.info("原始 cmd : " + cmd);
        AtomicBoolean sudoFlag = new AtomicBoolean(false);
        Map<String, String> cmdMap = processCmd(cmd, sudoFlag);
        logger.info("处理后 cmd : " + cmd);
        String cancel = cmdMap.get("cancel");
        if (cancel != null) {
            return;
        }
        doRun2(project, cmdMap, sudoFlag);
    }

    private void doRun(Project project, String cmd) {
        // 获取终端
        ToolWindow openTerminal = UiUtil.getOpenTerminal(IdeaApiUtil.getProject());
        runInTerminal(project, cmd, openTerminal);
    }

    private Map<String, String> processCmd(String cmd, AtomicBoolean sudoFlag) {
        Map<String, String> resultMap = new HashMap<>();
        String result = cmd;
        // lsof -i:{%Parm%}
        // 正则表达式来匹配 {%Parm%} 格式的占位符
        Pattern pattern = Pattern.compile("\\{%([a-zA-Z0-9_]+)%}");
        Matcher matcher = pattern.matcher(result);
        Map<String, String> map = new HashMap<>();
        Map<String, String> cmdKV = new HashMap<>();
        while (matcher.find()) {
            // 获取占位符中的参数名
            String parameterName = matcher.group(1);
            map.put(parameterName, "");
            cmdKV.put(parameterName, matcher.group(0));
        }
        // 是否包含sudo
        sudoFlag.set(false);
        if (cmd.contains("sudo")) {
            map.put("sudo", "");
            sudoFlag.set(true);
        }
        if (!map.isEmpty()) {
            DialogParamInput inputDialog = new DialogParamInput(map);
            IdeaApiUtil.setRelatedLocation(inputDialog);
            if (inputDialog.showAndGet()) {
                // 如果用户点击“OK”按钮，获取输入的值
                map = inputDialog.getMap();
            } else {
                resultMap.put("cancel", "cancel");// 取消执行
                return resultMap;
            }
            for (String k : map.keySet()) {
                if (!"sudo".equals(k)) {
                    String value = map.get(k);
                    String key = cmdKV.get(k);
                    result = result.replace(key, value);
                }
            }
            String temp = result;
            resultMap.put("no_pwd", temp.replace("\\n", " ").replace("\\", " "));
            // 处理sudo
            if (sudoFlag.get()) {

                String sudoAccess = "echo " + map.get("sudo") + "|" + " sudo -S -v 2>/dev/null && ";//缓存sudo权限
                result = result.replaceAll("sudo", sudoAccess);
            }
        }
        // 移除 \ 和 \n
        result = result.replace("\\n", " ").replace("\\", " ");
        resultMap.put("result", result);
        return resultMap;
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

    private String decideWinShellPath() {
        String shellPath = "cmd.exe";
        String path = System.getenv("PATH");
        String[] split = path.split(";");
        String powershell = "";
        for (int i = 0; i < split.length; i++) {
            if (split[i].contains("PowerShell")) {
                powershell = split[i];
            }
        }
        if (!powershell.isEmpty()) { // 是否包含powershell，优先使用
            shellPath = powershell + "powershell.exe";
        }
        return shellPath;
    }

    // 使用consoleView
    private void doRun2(Project project, Map<String, String> cmdMap, AtomicBoolean sudoFlag) {
        String cmd = cmdMap.get("result");
        String basePath = project.getBasePath();
        if (basePath == null) {
            logger.error("Project basePath is null");
            return;
        }
        GeneralCommandLine commandLine = new GeneralCommandLine();
        String shellPath = "/bin/bash";
        // 默认当前项目
        commandLine.setWorkDirectory(basePath);
        commandLine.setCharset(StandardCharsets.UTF_8);
        commandLine.withEnvironment("PATH", System.getenv("PATH"));
        System.out.println("charset  " + Charset.defaultCharset());
        String os = System.getProperty("os.name");
        logger.info("当前用户os : " + os);
        if (os.toLowerCase().contains("windows")) {
            shellPath = decideWinShellPath();// 获取执行程序的路径
            //shellPath = "cmd.exe";
            commandLine.addParameter("/c");
            commandLine.setCharset(Charset.forName("GBK"));
            commandLine.addParameter(cmd);
        } else {
            commandLine.addParameter("-c");
            commandLine.addParameter(cmd);
        }
        commandLine.setExePath(shellPath);
        OSProcessHandler processHandler;
        try {
            processHandler = new OSProcessHandler(commandLine);
        } catch (ExecutionException e) {
            logger.error(e.toString());
            throw new RuntimeException(e);
        }
        // 获取进程ID
        final Process process = processHandler.getProcess();
        long pid = process.pid();
        ConsoleView consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
        consoleView.print("Command Assist: \n", ConsoleViewContentType.SYSTEM_OUTPUT);
        consoleView.print("     dir: " + commandLine.getWorkDirectory() + "\n", ConsoleViewContentType.SYSTEM_OUTPUT);
        consoleView.print("     pid: " + pid + "\n", ConsoleViewContentType.SYSTEM_OUTPUT);
        if (sudoFlag.get()) {
            String noPwd = cmdMap.get("no_pwd");
            consoleView.print("     cmd: " + noPwd + "\n", ConsoleViewContentType.SYSTEM_OUTPUT);
        } else {
            consoleView.print("     cmd: " + cmd + "\n", ConsoleViewContentType.SYSTEM_OUTPUT);
        }
        consoleView.print("--------------------------------------------------------------",
                ConsoleViewContentType.SYSTEM_OUTPUT);
        consoleView.print(" \n", ConsoleViewContentType.SYSTEM_OUTPUT);
        processHandler.addProcessListener(new ProcessAdapter() {
            // 监听输出
            @Override
            public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                String text = event.getText();
                if (!text.startsWith(commandLine.getExePath())) {
                    consoleView.print(text, ConsoleViewContentType.SYSTEM_OUTPUT);
                }
            }

            // 监听终止事件
            @Override
            public void processTerminated(@NotNull ProcessEvent event) {
                // 当进程终止时，输出任务完成提示
                consoleView.print("\n\n\n", ConsoleViewContentType.SYSTEM_OUTPUT);
                consoleView.print("Command Assist: " + MessagesUtil.getMessage("terminal.run.complete") + "\n",
                        ConsoleViewContentType.SYSTEM_OUTPUT);
            }
        });

        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        ToolWindow terminal = toolWindowManager.getToolWindow("Terminal");
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(consoleView.getComponent(), buildTabName(cmd), false);
        content.setTabColor(new JBColor(0x769AE5DC, 0x769AE5DC));
        final long myGroupId = 9999L;
        content.setExecutionId(myGroupId);
        if (terminal != null) {
            terminal.getContentManager().addContent(content);
            // 确保新创建的标签页可见并选中
            terminal.getContentManager().setSelectedContent(content);
            terminal.activate(null);

            terminal.addContentManagerListener(new ContentManagerListener() {
                @Override
                public void contentRemoved(@NotNull ContentManagerEvent event) {
                    //  区分terminal是否是自己创建的
                    if (myGroupId != event.getContent().getExecutionId()) {
                        return;
                    }
                    if (process.isAlive()) {
                        process.destroyForcibly();
                        try {
                            boolean terminated = process.waitFor(5, TimeUnit.SECONDS);
                            if (terminated) {
                                IdeaApiUtil.myTips(MessagesUtil.getMessage("terminal.close.success"));
                            } else {
                                String html = "<html><br>"
                                        + buildTabName(cmd)
                                        + "<br>"
                                        + MessagesUtil.getMessage("terminal.close.fail")
                                        + "<br>"
                                        + "pid : " + pid
                                        + "</html>";
                                IdeaApiUtil.myWarn(html);
                            }
                        } catch (InterruptedException e) {
                            logger.error("终端页关闭异常. " + e);
                            throw new RuntimeException(e);
                        }
                    } else {
                        IdeaApiUtil.myTips(MessagesUtil.getMessage("terminal.close.success"));
                    }
                }
            });

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

