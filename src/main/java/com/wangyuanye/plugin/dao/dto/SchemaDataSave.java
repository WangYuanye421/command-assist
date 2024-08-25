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

/**
 * 命名空间持久层
 *
 * @author wangyuanye
 * @date 2024/8/23
 **/
@Service(Service.Level.APP)
@State(name = "commandSchema", storages = {@Storage("command_assist/schemas.xml")})
public final class SchemaDataSave implements PersistentStateComponent<SchemaDataSave.MyState> {
    private SchemaDataSave.MyState myState = new SchemaDataSave.MyState();
    Notification notification = new Notification(
            "CommandAssistNotificationGroup", // 通知组ID
            "Command Assist 通知",        // 通知标题
            NotificationType.INFORMATION // 通知类型 (INFORMATION, WARNING, ERROR)
    );


    // 保存所有的schema
    public static class MyState {
        @Tag("schemas")
        @XCollection(elementName = "schema")
        public List<CmdSchema> schemas;

        public @NotNull List<CmdSchema> getSchemas() {
            if (schemas == null) {
                return new ArrayList<>();
            }
            return schemas;
        }

        public void setSchemas(List<CmdSchema> schemas) {
            this.schemas = schemas;
        }
    }

    @Override
    public @Nullable SchemaDataSave.MyState getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull MyState state) {
        myState = state;
    }

    public CmdSchema getDefaultSchema(){
        CmdSchema result = null;
        List<CmdSchema> list = list();
        for (CmdSchema schema : list) {
            if(schema.getDefaultSchema()){
                result = schema;
            }
        }
        if(result == null && !list.isEmpty()){
            result = list.get(0);
        }
        return result;
    }

    //列表
    public @NotNull List<CmdSchema> list() {
        return this.getState().getSchemas();
    }

    // 获取单个
    public @Nullable CmdSchema getById(String schemaId) {
        List<CmdSchema> list = list();
        for (CmdSchema schema : list) {
            if (schemaId.equals(schema.getId())) {
                return schema;
            }
        }
        return null;
    }

    // 新增
    public void addSchema(@NotNull CmdSchema schema) {
        List<CmdSchema> schemas = this.getState().getSchemas();
        boolean nameExist = checkSchemaExist(schema.getName(), schema.getId(), schemas);
        if (nameExist) {
            notification.setContent(MessagesUtil.buildBalloon("[" + schema.getName() + "] 已存在"));
            notification.notify(MyToolWindowFactory.getProject());
            return;
        }
        schemas.add(schema);
        cancelOtherDefault(schema.getId(), schema.getDefaultSchema(), schemas);
        this.getState().setSchemas(schemas);
    }

    //更新
    public void updateSchema(@NotNull CmdSchema schema) {
        List<CmdSchema> schemas = this.getState().getSchemas();
        boolean nameExist = checkSchemaExist(schema.getName(), schema.getId(), schemas);
        if (nameExist) {
            notification.setContent(MessagesUtil.buildBalloon("[" + schema.getName() + "] 已存在"));
            notification.notify(MyToolWindowFactory.getProject());
            return;
        }
        for (CmdSchema ele : schemas) {
            if (ele.getId().equals(schema.getId())) {
                ele.setName(schema.getName());// 更新
                ele.setDefaultSchema(schema.getDefaultSchema());
            }
        }
        cancelOtherDefault(schema.getId(), schema.getDefaultSchema(), schemas);
        this.getState().setSchemas(schemas);
    }

    private void cancelOtherDefault(String id, boolean defaultSchema,@NotNull List<CmdSchema> schemas){
        if(defaultSchema){
            for (CmdSchema schema : schemas) {
                if (schema.getDefaultSchema() && !schema.getId().equals(id)) {
                    schema.setDefaultSchema(false);
                }
            }
        }
    }

    public boolean checkSchemaExist(@NotNull String name, String schemaId, @NotNull List<CmdSchema> schemas) {
        boolean flag = false;
        CmdSchema sameName = null;
        for (CmdSchema schema : schemas) {
            if (name.equals(schema.getName())) {
                sameName = schema;
                break;
            }
        }
        if (sameName != null) {
            if (schemaId == null || schemaId.isEmpty()) {
                // 表示新增
                return true;
            } else {// 表示更新
                if (!sameName.getId().equals(schemaId)) {
                    return true;
                }
            }
        }
        return flag;
    }

    // 删除
    public void deleteSchema(@NotNull String schemaId) {
        List<CmdSchema> schemas = this.getState().getSchemas();
        // todo 查询是否有cmd,要求确认
        CmdSchema delete = null;
        for (CmdSchema ele : schemas) {
            if (schemaId.equals(ele.getId())) {
                delete = ele;
            }
        }
        if (delete != null) {
            schemas.remove(delete);
            this.getState().setSchemas(schemas);
        }
    }
}
