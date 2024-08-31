package com.wangyuanye.plugin.listeners;

import com.intellij.ide.AppLifecycleListener;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.wangyuanye.plugin.dao.CmdDataSave;
import com.wangyuanye.plugin.dao.SchemaDataSave;
import com.wangyuanye.plugin.dao.dto.MyCmd;
import com.wangyuanye.plugin.dao.dto.MySchema;
import com.wangyuanye.plugin.util.IdeaApiUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * wangyunaye
 */
public class MyProjectManagerListener implements AppLifecycleListener, ProjectManagerListener {
    private static final Logger logger = Logger.getInstance(MyProjectManagerListener.class);
    private final SchemaDataSave schemaService;
    private final CmdDataSave cmdService;

    public MyProjectManagerListener() {
        schemaService = IdeaApiUtil.getSchemaService();
        cmdService = IdeaApiUtil.getCmdService();
    }

    @Override
    public void welcomeScreenDisplayed() {
        AppLifecycleListener.super.welcomeScreenDisplayed();
        cleanDirtyData();
    }

    private void cleanDirtyData() {
        List<MySchema> schemas = schemaService.list();
        Map<String, MySchema> schemaMap = schemas.stream().collect(Collectors.toMap(MySchema::getId, e1 -> e1, (v1, v2) -> v1));
        List<MyCmd> cmds = cmdService.list();
        List<MyCmd> toBeRemove = new ArrayList<>();
        for (MyCmd cmd : cmds) {
            MySchema mySchema = schemaMap.get(cmd.getSchemaId());
            if (mySchema == null) {
                toBeRemove.add(cmd);
            }
        }
        cmds.removeAll(toBeRemove);
    }

    @Override
    public void projectClosingBeforeSave(@NotNull Project project) {
        logger.info("[ProjectManagerListener] Project Closing...");
        if (schemaService.getState() != null) {
            logger.info("[ProjectManagerListener] do save before. schemas FlushFlag: " + schemaService.getState().getFlushFlag());
            schemaService.getState().setFlushFlag(true);
            logger.info("[ProjectManagerListener] do save after. schemas FlushFlag: " + schemaService.getState().getFlushFlag());
        } else {
            logger.warn("[ProjectManagerListener] Can not get schemaService instance");
        }

        if (cmdService.getState() != null) {
            logger.info("[ProjectManagerListener] do save before. cmds FlushFlag: " + cmdService.getState().getFlushFlag());
            cmdService.getState().setFlushFlag(true);
            logger.info("[ProjectManagerListener] do save after. cmds FlushFlag: " + cmdService.getState().getFlushFlag());
        } else {
            logger.warn("[ProjectManagerListener] Can not get cmdService instance");
        }
    }

    // 应用关闭
    @Override
    public void appWillBeClosed(boolean isRestart) {
        logger.info("[AppLifecycleListener] Application Closing...");
        if (schemaService.getState() != null) {
            Boolean flushFlag = schemaService.getState().getFlushFlag();
            logger.info("[AppLifecycleListener] do save before. schemas FlushFlag: " + flushFlag);
            if (!flushFlag) { // if projectManager do save unsuccessful
                schemaService.getState().setFlushFlag(true);
                logger.info("[AppLifecycleListener] do save after. schemas FlushFlag: " + schemaService.getState().getFlushFlag());
            }
        } else {
            logger.warn("[AppLifecycleListener] Can not get schemaService instance");
        }

        if (cmdService.getState() != null) {
            Boolean flushFlag = cmdService.getState().getFlushFlag();
            logger.info("[AppLifecycleListener] do save before. cmds FlushFlag: " + flushFlag);
            if (!flushFlag) {// if projectManager do save unsuccessful
                cmdService.getState().setFlushFlag(true);
                logger.info("[AppLifecycleListener] do save after. cmds FlushFlag: " + cmdService.getState().getFlushFlag());
            }
        } else {
            logger.warn("[AppLifecycleListener] Can not get cmdService instance");
        }

    }
}
