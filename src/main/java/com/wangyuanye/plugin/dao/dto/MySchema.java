package com.wangyuanye.plugin.dao.dto;


import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Tag;
import com.wangyuanye.plugin.util.MessagesUtil;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.UUID;

/**
 * nameSpace
 *
 * @author wangyuanye
 * 2024/8/18
 **/
@Tag("schema")
public class MySchema implements Serializable, Cloneable {
    private static final Logger LOG = Logger.getInstance(MySchema.class);
    @Attribute("id")
    private String id;
    // 命名空间名称
    @Attribute("name")
    private String name;
    // 是否默认
    @Attribute("defaultSchema")
    private boolean defaultSchema;


    public MySchema() {
    }

    public static MySchema getEmptyObj() {
        MySchema mySchema = new MySchema();
        mySchema.setName(MessagesUtil.getMessage("schema.combobox.place"));
        return mySchema;
    }

    public MySchema(String name, boolean defaultSchema) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.defaultSchema = defaultSchema;
    }

    public MySchema(String id, String name, boolean defaultSchema) {
        this.id = id;
        this.name = name;
        this.defaultSchema = defaultSchema;
    }

    @Override
    public MySchema clone() {
        try {
            MySchema schema = (MySchema) super.clone();
            schema.setId(this.getId());
            schema.setName(this.getName());
            schema.setDefaultSchema(this.getDefaultSchema());
            return schema;
        } catch (CloneNotSupportedException e) {
            LOG.error(e);
            return null;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public @NotNull String getName() {
        if (name == null) {
            return "";
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getDefaultSchema() {
        return defaultSchema;
    }

    public void setDefaultSchema(boolean defaultSchema) {
        this.defaultSchema = defaultSchema;
    }

    @Override
    public String toString() {
        return "CmdSchema{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", defaultSchema='" + defaultSchema + '\'' +
                '}';
    }
}
