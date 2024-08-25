package com.wangyuanye.plugin.services;

import com.wangyuanye.plugin.dao.CmdDao;
import com.wangyuanye.plugin.dao.dto.Cmd;
import com.wangyuanye.plugin.dao.dto.CmdSchema;
import com.wangyuanye.plugin.util.FactoryUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务层
 *
 * @author wangyuanye
 * @date 2024/8/17
 **/

public final class MyServiceImpl implements MyService {

    private CmdDao testDao = FactoryUtil.getTestDao();
    private CmdDao fileDao = FactoryUtil.getFileDao();
    private CmdDao dao = fileDao;
    public static final MyServiceImpl instance = Inner.getInstance();

    private MyServiceImpl() {
    }

    static class Inner {
        private static MyServiceImpl getInstance() {
            return new MyServiceImpl();
        }
    }

    // todo 添加缓存层?


    @Override
    public List<CmdSchema> load() {
        return dao.load();
    }

    @Override
    public ArrayList<Cmd> list(String schemaName) {
        return dao.list(schemaName);
    }

    @Override
    public Cmd getById(String cmdId, String schemaName) {
        ArrayList<Cmd> list = list(schemaName);
        Cmd cmd = null;
        for (Cmd e : list) {
            if (cmdId.equals(e.getCmdId())) {
                cmd = e;
                break;
            }
        }
        return cmd;
    }

    @Override
    public void addCmd(Cmd cmd) {
        dao.addCmd(cmd);
    }

    @Override
    public void editCmd(Cmd cmd) {
        dao.editCmd(cmd);
    }

    @Override
    public void deleteCmd(String cmdId, String schemaId) {
        dao.deleteCmd(cmdId, schemaId);
        // todo 脏数据处理,比如没有id
    }
}
