package com.wangyuanye.plugin.dao.dto;


import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.annotations.Attribute;
import com.intellij.util.xmlb.annotations.Tag;

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

    public Cmd() {
        this.cmdId = UUID.randomUUID().toString();
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
}
