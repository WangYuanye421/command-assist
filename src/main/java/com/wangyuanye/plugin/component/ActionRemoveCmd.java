package com.wangyuanye.plugin.component;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.TableUtil;
import com.intellij.ui.table.JBTable;
import com.wangyuanye.plugin.dao.CmdDataSave;
import com.wangyuanye.plugin.dao.dto.MyCmd;
import com.wangyuanye.plugin.util.MessagesUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author yuanye.wang
 * @since
 **/
public class ActionRemoveCmd extends AnAction {
    private static final Logger logger = Logger.getInstance(ActionRemoveCmd.class);
    private JBTable commandTable;
    private MyCmdModel cmdModel;
    private List<MyCmd> myCmdList;
    private CmdDataSave cmdService;

    public ActionRemoveCmd() {
        super(MessagesUtil.getMessage("cmd.toolbar.remove.text"), MessagesUtil.getMessage("cmd.toolbar.remove.text"), AllIcons.General.Remove);
    }

    public ActionRemoveCmd(JBTable commandTable,
                           MyCmdModel cmdModel,
                           List<MyCmd> myCmdList,
                           CmdDataSave cmdService) {
        super(MessagesUtil.getMessage("cmd.toolbar.remove.text"), MessagesUtil.getMessage("cmd.toolbar.remove.text"), AllIcons.General.Remove);
        this.commandTable = commandTable;
        this.cmdModel = cmdModel;
        this.myCmdList = myCmdList;
        this.cmdService = cmdService;
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT; // fix `ActionUpdateThread.OLD_EDT` is deprecated
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        CommandTab.stopEditing(commandTable);
        int selectedIndex = commandTable.getSelectedRow();
        if (selectedIndex < 0 || selectedIndex >= cmdModel.getRowCount()) {
            return;
        }
        MyCmd myCmdToBeRemoved = myCmdList.get(selectedIndex);
        logger.info("cmd remove. cmd:" + myCmdToBeRemoved.toString());
        cmdService.deleteCmd(myCmdToBeRemoved.getCmdId());
        TableUtil.removeSelectedItems(commandTable);
    }

    public void reset(JBTable commandTable,
                      MyCmdModel cmdModel,
                      List<MyCmd> myCmdList) {
        this.commandTable = commandTable;
        this.cmdModel = cmdModel;
        this.myCmdList = myCmdList;
    }
}
