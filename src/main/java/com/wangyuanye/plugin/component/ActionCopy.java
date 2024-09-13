package com.wangyuanye.plugin.component;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.ui.JBColor;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.table.JBTable;
import com.wangyuanye.plugin.util.MessagesUtil;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

/**
 * @author yuanye.wang
 * @since
 **/
public class ActionCopy extends AnAction {
    private static final Logger logger = Logger.getInstance(ActionCopy.class);
    private JBTable commandTable;

    public ActionCopy() {
        super(MessagesUtil.getMessage("cmd.toolbar.copy.text"), MessagesUtil.getMessage("cmd.toolbar.copy.text"),
                AllIcons.Actions.Copy);
    }

    public ActionCopy(JBTable commandTable) {
        super(MessagesUtil.getMessage("cmd.toolbar.copy.text"), MessagesUtil.getMessage("cmd.toolbar.copy.text"),
                AllIcons.Actions.Copy);
        this.commandTable = commandTable;
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT; // fix `ActionUpdateThread.OLD_EDT` is deprecated
    }


    @Override
    public void actionPerformed(AnActionEvent e) {
        // 获取鼠标事件
        InputEvent inputEvent = e.getInputEvent();

        if (inputEvent instanceof MouseEvent) {
            MouseEvent mouseEvent = (MouseEvent) inputEvent;
            MyCmdModel model = (MyCmdModel) commandTable.getModel();
            int selectedRow = commandTable.getSelectedRow();
            String cmd = (String) model.getValueAt(selectedRow, 0);
            // 复制
            MessagesUtil.setClipboardContent(cmd);
            showMsg(mouseEvent, MessagesUtil.getMessage("cmd.toolbar.copy.copied"));
        }

    }

    private void showMsg(MouseEvent e, String message) {
        // 创建气泡提示
        Balloon balloon = JBPopupFactory.getInstance()
                .createHtmlTextBalloonBuilder(message, null,
                        new JBColor(new Color(160, 229, 153), new Color(160, 229, 153)),
                        null)
                .setFadeoutTime(3000)  // 设置气泡提示显示时间为3秒
                .createBalloon();

        // 在鼠标指针处显示气泡提示
        balloon.show(new RelativePoint(e.getComponent(), e.getPoint()), Balloon.Position.below);
    }
}
