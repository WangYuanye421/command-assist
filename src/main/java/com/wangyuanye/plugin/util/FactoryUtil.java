package com.wangyuanye.plugin.util;

import com.wangyuanye.plugin.dao.CmdDao;
import com.wangyuanye.plugin.dao.CmdDaoFileImpl;
import com.wangyuanye.plugin.dao.CmdDaoTestImpl;

/**
 * 工厂工具类
 *
 * @author wangyuanye
 * @date 2024/8/19
 **/
public class FactoryUtil {

    // 获取测试dao
    public static CmdDao getTestDao() {
        return CmdDaoTestImpl.getInstance();
    }

    // 获取文件存储dao
    public static CmdDao getFileDao() {
        return CmdDaoFileImpl.getInstance();
    }


}
