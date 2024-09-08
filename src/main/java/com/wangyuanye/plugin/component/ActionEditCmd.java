package com.wangyuanye.plugin.component;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.table.JBTable;
import com.wangyuanye.plugin.dao.CmdDataSave;
import com.wangyuanye.plugin.dao.dto.MyCmd;
import com.wangyuanye.plugin.util.IdeaApiUtil;
import com.wangyuanye.plugin.util.MessagesUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author yuanye.wang
 * @since
 **/
public class ActionEditCmd extends AnAction {
    private static final Logger logger = Logger.getInstance(ActionEditCmd.class);
    private JBTable commandTable;
    private MyCmdModel cmdModel;
    private List<MyCmd> myCmdList;
    private CmdDataSave cmdService;

    public ActionEditCmd() {
        super(MessagesUtil.getMessage("cmd.toolbar.edit.text"), MessagesUtil.getMessage("cmd.toolbar.edit.text"), AllIcons.Actions.Edit);
    }

    public ActionEditCmd(JBTable commandTable, MyCmdModel cmdModel, List<MyCmd> myCmdList,
                         CmdDataSave cmdService) {
        super(MessagesUtil.getMessage("cmd.toolbar.edit.text"), MessagesUtil.getMessage("cmd.toolbar.edit.text"), AllIcons.Actions.Edit);
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
        MyCmd sourceMyCmd = myCmdList.get(selectedIndex);
        MyCmd myCmdEdit = sourceMyCmd.clone();
        DialogMyCmd dialog = new DialogMyCmd(commandTable, myCmdEdit, selectedIndex, myCmdList);
        IdeaApiUtil.setRelatedLocation(dialog);
        dialog.setTitle(MessagesUtil.getMessage("cmd.dialog.edit.title"));
        if (!dialog.showAndGet()) {
            return;
        }
        logger.info("cmd edit. cmd:" + myCmdEdit.toString());
        myCmdList.set(selectedIndex, myCmdEdit);
        cmdService.updateCmd(myCmdEdit);// db
        cmdModel.fireTableRowsUpdated(selectedIndex, selectedIndex);
        commandTable.getSelectionModel().setSelectionInterval(selectedIndex, selectedIndex);
    }

    public void reset(JBTable commandTable,
                      MyCmdModel cmdModel,
                      List<MyCmd> myCmdList) {
        this.commandTable = commandTable;
        this.cmdModel = cmdModel;
        this.myCmdList = myCmdList;
    }
}
