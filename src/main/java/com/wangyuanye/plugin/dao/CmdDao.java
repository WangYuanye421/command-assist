package com.wangyuanye.plugin.dao;


import com.wangyuanye.plugin.dao.dto.Cmd;
import com.wangyuanye.plugin.dao.dto.CmdSchema;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * cmd数据操作层接口
 *
 * @author wangyuanye
 * @date 2024/8/18
 **/
public interface CmdDao {

    /**
     * 加载数据源
     *
     * @return 封装后的 List<CmdSchema>
     */
    List<CmdSchema> load();

    // 返回所有命名空间
    default List<String> getSchemaNames() {
        List<CmdSchema> schemas = load();
        return schemas.stream().map(CmdSchema::getName).collect(Collectors.toList());
    }

    // 获取默认命名空间
    default String getDefaultSchema() {
        String name = "default";
        List<CmdSchema> schemas = load();
        for (CmdSchema e : schemas) {
            if (e.getDefaultSchema()) {
                name = e.getName();
                break;
            }
        }
        return name;

    }

    // 获取默认列表
    default ArrayList<Cmd> getDefaultCmdList() {
        return list(getDefaultSchema());
    }

    // 查询指定列表
    ArrayList<Cmd> list(String schemaName);

    // 新增
    void addCmd(Cmd cmd);

    // 修改
    void editCmd(Cmd cmd);

    // 删除
    void deleteCmd(String cmdId, String schemaName);
}
