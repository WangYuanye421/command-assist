package com.wangyuanye.plugin.component;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.ui.table.JBTable;
import com.wangyuanye.plugin.dao.CmdDataSave;
import com.wangyuanye.plugin.dao.dto.MyCmd;
import com.wangyuanye.plugin.dao.dto.MySchema;
import com.wangyuanye.plugin.util.IdeaApiUtil;
import com.wangyuanye.plugin.util.MessagesUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author yuanye.wang
 * @since
 **/
public class ActionAddCmd extends AnAction {
    private static final Logger logger = Logger.getInstance(ActionAddCmd.class);
    private JBTable commandTable;
    ActionSchemaComboBox schemaComboBox;
    private MyCmdModel cmdModel;
    private List<MyCmd> myCmdList;
    private CmdDataSave cmdService;

    public ActionAddCmd() {
        super(MessagesUtil.getMessage("cmd.toolbar.add.text"), MessagesUtil.getMessage("cmd.toolbar.add.text"), AllIcons.General.Add);
    }

    public ActionAddCmd(JBTable commandTable,
                        ActionSchemaComboBox schemaComboBox,
                        MyCmdModel cmdModel,
                        List<MyCmd> myCmdList,

                        CmdDataSave cmdService) {
        super(MessagesUtil.getMessage("cmd.toolbar.add.text"), MessagesUtil.getMessage("cmd.toolbar.add.text"), AllIcons.General.Add);
        this.commandTable = commandTable;
        this.cmdModel = cmdModel;
        this.myCmdList = myCmdList;
        this.cmdService = cmdService;
        this.schemaComboBox = schemaComboBox;
    }

    public void setSchemaComboBox(ActionSchemaComboBox schemaComboBox) {
        this.schemaComboBox = schemaComboBox;
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.EDT; // fix `ActionUpdateThread.OLD_EDT` is deprecated
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        CommandTab.stopEditing(commandTable);
        MySchema selectedItem = (MySchema) schemaComboBox.getComboBox().getSelectedItem();
        if (selectedItem == null || selectedItem.getId() == null) {
            IdeaApiUtil.myTips(MessagesUtil.getMessage("cmd.add.no_schema"));
            return;
        }
        MyCmd myCmdAdd = new MyCmd(selectedItem.getId(), "", "");
        DialogMyCmd dialog = new DialogMyCmd(commandTable, myCmdAdd, -1, myCmdList);
        IdeaApiUtil.setRelatedLocation(dialog);
        if (!dialog.showAndGet()) {
            return;
        }
        logger.info("cmd add. cmd:" + myCmdAdd.toString());
        myCmdList.add(myCmdAdd);
        cmdService.addCmd(myCmdAdd);// db
        int index = myCmdList.size() - 1;
        cmdModel.fireTableRowsInserted(index, index);
        commandTable.getSelectionModel().setSelectionInterval(index, index);
        commandTable.scrollRectToVisible(commandTable.getCellRect(index, 0, true));
    }

    public void reset(JBTable commandTable,
                      MyCmdModel cmdModel,
                      List<MyCmd> myCmdList) {
        this.commandTable = commandTable;
        this.cmdModel = cmdModel;
        this.myCmdList = myCmdList;
    }
}
