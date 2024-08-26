package com.wangyuanye.plugin.dao.dto;


import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Tag;
import com.intellij.util.xmlb.annotations.Transient;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * nameSpace
 *
 * @author wangyuanye
 * @date 2024/8/18
 **/
@Tag("schema")
public class CmdSchema implements Serializable {
    @Attribute("id")
    private String id;
    // 命名空间名称
    @Attribute("name")
    private String name;
    // 是否默认
    @Attribute("defaultSchema")
    private boolean defaultSchema;
    // 命令集
    @Transient
    private List<Cmd> cmdList;
    @Transient
    private boolean isEdit;


    public CmdSchema() {
        this.id = UUID.randomUUID().toString();
    }

    public CmdSchema(String name, boolean defaultSchema) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.defaultSchema = defaultSchema;
    }

    public CmdSchema(String id, String name, boolean defaultSchema) {
        this.id = id;
        this.name = name;
        this.defaultSchema = defaultSchema;
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


    public List<Cmd> getCmdList() {
        return cmdList;
    }

    public void setCmdList(List<Cmd> cmdList) {
        this.cmdList = cmdList;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    @Override
    public String toString() {
        return "CmdSchema{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", defaultSchema='" + defaultSchema + '\'' +
                ", cmdList=" + cmdList +
                ", isEdit=" + isEdit +
                '}';
    }
}
