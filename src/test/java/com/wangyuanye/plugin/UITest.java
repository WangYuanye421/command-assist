package com.wangyuanye.plugin;


import com.intellij.openapi.diagnostic.DefaultLogger;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.testFramework.HeavyPlatformTestCase;

/**
 *
 * LightPlatformTestCase: 适用于大多数插件测试，提供轻量级的环境。
 * HeavyPlatformTestCase: 提供更完整的 IDEA 环境，适合需要复杂环境的测试。
 * UsefulTestCase: 提供了一些常用的断言和帮助方法，适用于独立于 IDEA 环境的测试。
 */

public class UITest extends HeavyPlatformTestCase {
    public static Logger logger = new DefaultLogger("[Cmd Run Action]");
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // 在这里进行初始化操作
    }

    @Override
    protected void tearDown() throws Exception {
        try {
            // 在这里进行清理操作
        } finally {
            super.tearDown();
        }
    }


//    @Test
//    public void test01() {
//        String cmd = "pwd";
//        MessagesUtil.setClipboardContent(cmd);
//        System.out.println("剪切板内容: " + MessagesUtil.getClipboardContent());
//
//        ToolWindow terminal = ToolWindowManager.getInstance(getProject()).getToolWindow("Terminal");
//
//        if (terminal == null || !terminal.isVisible()) {
//            // 如果终端没有打开，打开终端
//            ToolWindowManager.getInstance(getProject()).getToolWindow("Terminal").activate(() -> {
//                // 打开终端后再执行粘贴操作
//                pasteCommandInTerminal();
//            });
//        } else {
//            // 如果终端已经打开，直接粘贴命令
//            terminal.activate(() -> pasteCommandInTerminal());
//        }
//    }


//    private void pasteCommandInTerminal() {
//        ContentManager contentManager = ToolWindowManager.getInstance(getProject()).getToolWindow("Terminal").getContentManager();
//        Content selectedContent = contentManager.getSelectedContent();
//        if (selectedContent != null) {
//            IdeFocusManager.getInstance(getProject()).doWhenFocusSettlesDown(() -> {
//                try {
//                    Robot robot = new Robot();
//                    robot.delay(500);  // 等待焦点稳定
//                    if (System.getProperty("os.name").toLowerCase().contains("mac")) {
//                        robot.keyPress(KeyEvent.VK_META);
//                        robot.keyPress(KeyEvent.VK_V);
//                        robot.keyRelease(KeyEvent.VK_V);
//                        robot.keyRelease(KeyEvent.VK_META);
//                    } else {
//                        robot.keyPress(KeyEvent.VK_CONTROL);
//                        robot.keyPress(KeyEvent.VK_V);
//                        robot.keyRelease(KeyEvent.VK_V);
//                        robot.keyRelease(KeyEvent.VK_CONTROL);
//                    }
//                } catch (AWTException e) {
//                    logger.error(e.getMessage());
//                }
//            });
//        }
//    }

}
