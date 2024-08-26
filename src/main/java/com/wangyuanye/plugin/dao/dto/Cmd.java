package com.wangyuanye.plugin.dao.dto;


import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Tag;
import com.intellij.util.xmlb.annotations.Transient;

import java.io.Serializable;
import java.util.UUID;

/**
 * @author wangyuanye
 * @date 2024/8/17
 **/
@Tag("cmd")
public class Cmd implements Serializable {
    @Attribute("schemaId")
    private String schemaId;
    @Attribute("cmdId")
    private String cmdId;
    @Attribute("name")
    private String name;
    @Attribute("remark")
    private String remark;
    @Transient
    private Boolean isEdit = false;

    public Cmd() {
    }


    public Cmd(String schemaId, String name, String remark) {
        this.schemaId = schemaId;
        this.cmdId = UUID.randomUUID().toString();
        this.name = name;
        this.remark = remark;
    }

    public String getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    public String getCmdId() {
        return cmdId;
    }

    public void setCmdId(String cmdId) {
        this.cmdId = cmdId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getEdit() {
        return isEdit;
    }

    public void setEdit(Boolean edit) {
        isEdit = edit;
    }
}
