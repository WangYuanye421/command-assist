package com.wangyuanye.plugin.dao;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.annotations.Tag;
import com.intellij.util.xmlb.annotations.XCollection;
import com.wangyuanye.plugin.dao.dto.MyCmd;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * command持久层
 *
 * @author wangyuanye
 * 2024/8/23
 **/
@Service(Service.Level.APP)
@State(name = "command", storages = {@Storage("command_assist/commands.xml")})
public final class CmdDataSave implements PersistentStateComponent<CmdDataSave.MyState> {
    private CmdDataSave.MyState myState = new CmdDataSave.MyState();

    Notification notification = new Notification(
            "CommandAssistNotificationGroup", // 通知组ID
            "Command Assist 通知",        // 通知标题
            NotificationType.INFORMATION // 通知类型 (INFORMATION, WARNING, ERROR)
    );


    // 保存所有的schema
    public static class MyState {
        @Tag("cmds")
        @XCollection(elementName = "cmd")
        public List<MyCmd> cmds;

        @Tag("flushFlag")
        public Boolean flushFlag;

        public @NotNull List<MyCmd> getCmds() {
            return cmds == null ? new ArrayList<>() : cmds;
        }

        public void setCmds(List<MyCmd> cmds) {
            this.cmds = cmds;
        }

        public Boolean getFlushFlag() {
            return flushFlag;
        }

        public void setFlushFlag(Boolean flushFlag) {
            this.flushFlag = flushFlag;
        }
    }

    @Override
    public @Nullable CmdDataSave.MyState getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull MyState state) {
        myState = state;
        myState.setFlushFlag(false);
    }

    //列表
    public @NotNull List<MyCmd> list() {
        return this.getState().getCmds();
    }

    public @NotNull List<MyCmd> list(String schemaId) {
        if (schemaId == null || schemaId.isEmpty()) return new ArrayList<>();
        List<MyCmd> list = list();
        return list.stream().filter(e -> schemaId.equals(e.getSchemaId())).collect(Collectors.toList());
    }

    /**
     * 获取单个
     *
     * @param schemaId
     * @param cmdId
     * @return
     */
    public @Nullable MyCmd getById(@NotNull String schemaId, @NotNull String cmdId) {
        List<MyCmd> list = list();
        Optional<MyCmd> first = list.stream().filter(e -> schemaId.equals(e.getSchemaId()) && cmdId.equals(e.getCmdId()))
                .findFirst();
        return first.orElse(null);
    }

    // 新增
    public void addCmd(@NotNull MyCmd myCmd) {
        List<MyCmd> myCmdList = this.getState().getCmds();
        myCmdList.add(myCmd);
        this.getState().setCmds(myCmdList);
    }

    //更新
    public void updateCmd(@NotNull MyCmd myCmd) {
        List<MyCmd> myCmdList = this.getState().getCmds();
        for (MyCmd ele : myCmdList) {
            if (ele.getCmdId().equals(myCmd.getCmdId())) {
                ele.setName(myCmd.getName());// 更新
                ele.setRemark(myCmd.getRemark());
            }
        }
        this.getState().setCmds(myCmdList);
    }

    /**
     * 删除整个分类的cmd
     *
     * @param schemaId
     */
    public void deleteCmdBySchemaId(@NotNull String schemaId) {
        List<MyCmd> deleteList = list(schemaId);
        if (deleteList.isEmpty()) return;
        List<MyCmd> all = list();
        all.removeAll(deleteList);
    }

    public void deleteCmdList(@NotNull List<String> cmdIds) {
        for (String id : cmdIds) {
            deleteCmd(id);
        }
    }

    // 删除
    public void deleteCmd(@NotNull String cmdId) {
        List<MyCmd> myCmdList = this.getState().getCmds();
        MyCmd delete = null;
        for (MyCmd ele : myCmdList) {
            if (cmdId.equals(ele.getCmdId())) {
                delete = ele;
            }
        }
        if (delete != null) {
            myCmdList.remove(delete);
            this.getState().setCmds(myCmdList);
        }
    }
}
