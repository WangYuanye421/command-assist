package com.wangyuanye.plugin.dao;


import com.intellij.openapi.application.ApplicationManager;
import com.wangyuanye.plugin.dao.dto.Cmd;
import com.wangyuanye.plugin.dao.dto.CmdSchema;
import com.wangyuanye.plugin.dao.dto.SchemaDataSave;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 使用文件系统 存储
 */
public class CmdDaoFileImpl implements CmdDao {
    private SchemaDataSave schemaDataSave;
    private SchemaDataSave.MyState state;
    private static HashMap<String, ArrayList<Cmd>> cmdMap = new HashMap<>();

    private static class Inner {
        private static CmdDaoFileImpl getInstance() {
            return new CmdDaoFileImpl();
        }
    }

    public static CmdDaoFileImpl getInstance() {
        return Inner.getInstance();
    }

    private CmdDaoFileImpl() {
        schemaDataSave = ApplicationManager.getApplication().getService(SchemaDataSave.class);
        state = schemaDataSave.getState();
    }

    private void initMap(List<CmdSchema> initList) {
        for (CmdSchema ele : initList) {
            cmdMap.put(ele.getName(), new ArrayList<>(ele.getCmdList()));
        }
    }

    @Override
    public List<CmdSchema> load() {
        if (state != null) {
            List<CmdSchema> schemas = state.getSchemas();
            //initMap(schemas);
            return schemas;
        }
        return new ArrayList<>();
    }

    @Override
    public ArrayList<Cmd> list(String schemaName) {
        return cmdMap.get(schemaName);
    }

    @Override
    public void addCmd(Cmd cmd) {
        if (schemaDataSave.getState() != null) {
            List<CmdSchema> schemas = schemaDataSave.getState().getSchemas();
            for (CmdSchema schema : schemas) {
                if (schema.getId().equals(cmd.getSchemaId())) {
                    schema.getCmdList().add(cmd);
                    //dataSave.updateSchema(schemas);
                }
            }
        }
    }

    @Override
    public void editCmd(Cmd cmd) {
        if (schemaDataSave.getState() != null) {
            List<CmdSchema> schemas = schemaDataSave.getState().getSchemas();
            for (CmdSchema schema : schemas) {
                if (schema.getId().equals(cmd.getSchemaId())) {
                    List<Cmd> cmdList = schema.getCmdList();
                    for (Cmd ele : cmdList) {
                        if (ele.getCmdId().equals(cmd.getCmdId())) {
                            ele.setName(cmd.getName());
                            ele.setRemark(cmd.getRemark());
                            //dataSave.updateSchema(schemas);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void deleteCmd(String cmdId, String schemaId) {
        if (schemaDataSave.getState() != null) {
            List<CmdSchema> schemas = schemaDataSave.getState().getSchemas();
            for (CmdSchema schema : schemas) {
                if (schema.getId().equals(schemaId)) {
                    List<Cmd> cmdList = schema.getCmdList();
                    Cmd cmd = null;
                    for (Cmd ele : cmdList) {
                        if (ele.getCmdId().equals(cmdId)) {
                            cmd = ele;
                            break;
                        }
                    }
                    if (cmd != null) {
                        cmdList.remove(cmd);
                    }
                }
            }
        }
    }
}
