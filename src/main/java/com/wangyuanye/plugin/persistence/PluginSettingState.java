package com.wangyuanye.plugin.persistence;

import com.intellij.openapi.components.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author wangyuanye
 * @date 2024/8/19
 **/

@State(name = "PluginSetting", storages = {@Storage("Command_settings.xml")})
@Service(Service.Level.APP)
public final class PluginSettingState implements PersistentStateComponent<PluginSettingState.State> {
    private State state = new State();

    static class State extends BaseState {
        public boolean dirCheck = false;
        public String storagePath = "";
    }

    @Nullable
    @Override
    public State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    // 你可以添加其他的业务逻辑方法
    public boolean isDirCheck() {
        return state.dirCheck;
    }

    public void setDirCheck(boolean dirCheck) {
        state.dirCheck = dirCheck;
    }
}
