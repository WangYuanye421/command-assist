package com.wangyuanye.plugin.dao.dto;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.annotations.Tag;
import com.intellij.util.xmlb.annotations.XCollection;
import com.wangyuanye.plugin.component.toolWindow.MyToolWindowFactory;
import com.wangyuanye.plugin.util.MessagesUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * command持久层
 *
 * @author wangyuanye
 * @date 2024/8/23
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
        @Tag("commands")
        @XCollection(elementName = "command")
        public List<Cmd> cmds;

        public List<Cmd> getCmds() {
            return cmds;
        }

        public void setCmds(List<Cmd> cmds) {
            this.cmds = cmds;
        }
    }

    @Override
    public @Nullable CmdDataSave.MyState getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull MyState state) {
        myState = state;
    }

    //列表
    public @NotNull List<Cmd> list() {
        return this.getState().getCmds();
    }

    public @NotNull List<Cmd> list(@NotNull String schemaId) {
        List<Cmd> list = list();
        return list.stream().filter(e -> schemaId.equals(e.getSchemaId())).collect(Collectors.toList());
    }

    // 获取单个
    public @Nullable Cmd getByCondition(String name, String remark) {
        return null;
    }

    // 新增
    public void addCmd(@NotNull Cmd cmd) {
        List<Cmd> cmdList = this.getState().getCmds();
        cmdList.add(cmd);
        this.getState().setCmds(cmdList);
    }

    //更新
    public void updateCmd(@NotNull Cmd cmd) {
        List<Cmd> cmdList = this.getState().getCmds();
        for (Cmd ele : cmdList) {
            if (ele.getCmdId().equals(cmd.getCmdId())) {
                ele.setName(cmd.getName());// 更新
                ele.setRemark(cmd.getRemark());
            }
        }
        this.getState().setCmds(cmdList);
    }

    public void deleteCmdList(@NotNull List<String> cmdIds){
        for (String id : cmdIds) {
            deleteCmd(id);
        }
    }
    // 删除
    public void deleteCmd(@NotNull String cmdId) {
        List<Cmd> cmdList = this.getState().getCmds();
        // todo 查询是否有cmd,要求确认
        Cmd delete = null;
        for (Cmd ele : cmdList) {
            if (cmdId.equals(ele.getCmdId())) {
                delete = ele;
            }
        }
        if (delete != null) {
            cmdList.remove(delete);
            this.getState().setCmds(cmdList);
        }
    }
}
