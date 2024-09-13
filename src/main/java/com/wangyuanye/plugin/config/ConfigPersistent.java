package com.wangyuanye.plugin.config;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.annotations.Tag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 配置数据的持久化
 *
 * @author yuanyewang515@gmail.com
 * @since v1.0
 **/
@Service(Service.Level.APP)
@State(name = "config", storages = {@Storage("command_assist/config.xml")})
public final class ConfigPersistent implements PersistentStateComponent<ConfigPersistent.CommandAssistConfig> {
    private ConfigPersistent.CommandAssistConfig state = new ConfigPersistent.CommandAssistConfig();

    /**
     * 配置对象
     */
    public static class CommandAssistConfig {
        @Tag("shellPath")
        private String shellPath;
        @Tag("path")
        private String path;

        public @NotNull String getShellPath() {
            return shellPath == null ? "" : shellPath;
        }

        public void setShellPath(@NotNull String shellPath) {
            this.shellPath = shellPath;
        }

        public @NotNull String getPath() {
            return path == null ? "" : path;
        }

        public void setPath(@NotNull String path) {
            this.path = path;
        }
    }

    @Override
    public @Nullable ConfigPersistent.CommandAssistConfig getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull CommandAssistConfig state) {
        this.state = state;
    }
}


