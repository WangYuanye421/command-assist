package com.wangyuanye.plugin.dao;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.annotations.Tag;
import com.intellij.util.xmlb.annotations.XCollection;
import com.wangyuanye.plugin.dao.dto.MySchema;
import com.wangyuanye.plugin.util.IdeaApiUtil;
import com.wangyuanye.plugin.util.MessagesUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 命名空间持久层
 *
 * @author wangyuanye
 * 2024/8/23
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
        public List<MySchema> schemas;

        @Tag("flushFlag")
        public Boolean flushFlag;// 标记数据是否已保存,用于监听器判断

        public @NotNull List<MySchema> getSchemas() {
            if (schemas == null) {
                return new ArrayList<>();
            }
            return schemas;
        }

        public void setSchemas(List<MySchema> schemas) {
            this.schemas = schemas;
        }

        public Boolean getFlushFlag() {
            return flushFlag;
        }

        public void setFlushFlag(Boolean flushFlag) {
            this.flushFlag = flushFlag;
        }
    }

    @Override
    public @Nullable SchemaDataSave.MyState getState() {
        return myState;
    }

    @Override
    public void loadState(@NotNull MyState state) {
        myState = state;
        myState.setFlushFlag(false);
    }


    public @NotNull MySchema getDefaultSchema() {
        MySchema result = new MySchema();
        List<MySchema> list = list();
        for (MySchema schema : list) {
            if (schema.getDefaultSchema()) {
                result = schema;
            }
        }
        if (result.getId() == null && !list.isEmpty()) {
            result = list.get(0);
        }
        return result;
    }

    //列表
    public @NotNull List<MySchema> list() {
        List<MySchema> schemas = this.getState().getSchemas();
        return schemas;
    }

    // 获取单个
    public @Nullable MySchema getById(String schemaId) {
        List<MySchema> list = list();
        for (MySchema schema : list) {
            if (schemaId.equals(schema.getId())) {
                return schema;
            }
        }
        return null;
    }

    // 新增
    public void addSchema(@NotNull MySchema schema) {
        List<MySchema> schemas = list();
        boolean nameExist = checkSchemaExist(schema.getName(), schema.getId(), schemas);
        if (nameExist) {
            notification.setContent(MessagesUtil.buildBalloon("[" + schema.getName() + "] 已存在"));
            notification.notify(IdeaApiUtil.getProject());
            return;
        }
        cancelOtherDefault(schema.getId(), schema.getDefaultSchema(), schemas);
        schemas.add(schema);
        this.getState().setSchemas(schemas);
    }

    //更新
    public void updateSchema(@NotNull MySchema schema) {
        List<MySchema> schemas = list();
        boolean nameExist = checkSchemaExist(schema.getName(), schema.getId(), schemas);
        if (nameExist) {
            notification.setContent(MessagesUtil.buildBalloon("[" + schema.getName() + "] 已存在"));
            notification.notify(IdeaApiUtil.getProject());
            return;
        }
        for (MySchema ele : schemas) {
            if (ele.getId().equals(schema.getId())) {
                ele.setName(schema.getName());// 更新
                ele.setDefaultSchema(schema.getDefaultSchema());
            }
        }
        cancelOtherDefault(schema.getId(), schema.getDefaultSchema(), schemas);
        this.getState().setSchemas(schemas);
    }

    private void cancelOtherDefault(String id, boolean defaultSchema, @NotNull List<MySchema> schemas) {
        if (defaultSchema) {
            for (MySchema schema : schemas) {
                if (schema.getDefaultSchema() && !schema.getId().equals(id)) {
                    schema.setDefaultSchema(false);
                }
            }
        }
    }

    public boolean checkSchemaExist(@NotNull String name, String schemaId, @NotNull List<MySchema> schemas) {
        boolean flag = false;
        MySchema sameName = null;
        for (MySchema schema : schemas) {
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

    public void deleteSchemaList(@NotNull List<String> ids) {
        for (String id : ids) {
            deleteSchema(id);
        }
    }

    // 删除
    public void deleteSchema(@NotNull String schemaId) {
        List<MySchema> schemas = list();
        MySchema delete = null;
        for (MySchema ele : schemas) {
            if (schemaId.equals(ele.getId())) {
                delete = ele;
            }
        }
        if (delete != null) {
            CmdDataSave cmdService = IdeaApiUtil.getCmdService();
            // 删除cmd
            cmdService.deleteCmdBySchemaId(delete.getId());
            schemas.remove(delete);
            this.getState().setSchemas(schemas);
        }
    }
}
