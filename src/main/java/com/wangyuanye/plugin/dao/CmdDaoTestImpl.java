package com.wangyuanye.plugin.dao;

import com.wangyuanye.plugin.dao.dto.Cmd;
import com.wangyuanye.plugin.dao.dto.CmdSchema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CmdDaoTestImpl implements CmdDao {
    private static HashMap<String, ArrayList<Cmd>> cmdMap = new HashMap<>();


    private static class Inner {
        private static CmdDaoTestImpl getInstance() {
            return new CmdDaoTestImpl();
        }
    }

    public static CmdDaoTestImpl getInstance() {
        return Inner.getInstance();
    }

    private CmdDaoTestImpl() {
        load();
    }


    private ArrayList<CmdSchema> initList() {
        ArrayList<CmdSchema> array = new ArrayList<CmdSchema>();
        ArrayList<Cmd> list = new ArrayList<Cmd>();
        list.add(new Cmd("default", "ls", "显示目录内容"));
        list.add(new Cmd("default", "ps -ef|grep", "显示进程"));
        list.add(new Cmd("default", "pwd", "显示当前路径"));
        list.add(new Cmd("default", "echo {%p1%} {%p2%}", "原样输出"));
        //array.add(new CmdSchema("default", "true", list));

        ArrayList<Cmd> list2 = new ArrayList<Cmd>();
        list2.add(new Cmd("java", "jps", "原样输出"));
        list2.add(new Cmd("java", "java -p", "原样输出"));
        list2.add(new Cmd("java", "javac", "原样输出"));
        //array.add(new CmdSchema("java", "false", list2));
        return array;
    }

    @Override
    public List<CmdSchema> load() {
        ArrayList<CmdSchema> initList = initList();
        initMap(initList);
        return initList;
    }

    private void initMap(ArrayList<CmdSchema> initList) {
        for (CmdSchema ele : initList) {
            cmdMap.put(ele.getName(), new ArrayList<>(ele.getCmdList()));
        }
    }

    @Override
    public ArrayList<Cmd> list(String schemaName) {
        return cmdMap.get(schemaName);
    }

    @Override
    public void addCmd(Cmd cmd) {
        cmdMap.get(cmd.getName()).add(cmd);
    }

    @Override
    public void editCmd(Cmd cmd) {
        ArrayList<Cmd> list = cmdMap.get(cmd.getSchemaId());
        for (Cmd ele : list) {
            if (ele.getCmdId().equals(cmd.getCmdId())) {
                ele.setName(cmd.getName());
                ele.setRemark(cmd.getRemark());
            }
        }
    }

    @Override
    public void deleteCmd(String cmdId, String schemaName) {
        ArrayList<Cmd> list = cmdMap.get(schemaName);
        Cmd cmd = null;
        for (Cmd ele : list) {
            if (ele.getCmdId().equals(cmdId)) {
                cmd = ele;
                break;
            }
        }
        if (cmd != null) {
            list.remove(cmd);
        }
    }
}
