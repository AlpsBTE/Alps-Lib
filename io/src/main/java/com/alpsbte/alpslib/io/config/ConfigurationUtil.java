package com.alpsbte.alpslib.io.config;

import com.alpsbte.alpslib.io.YamlFileFactory;
import com.alpsbte.alpslib.io.YamlFile;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class ConfigurationUtil extends YamlFileFactory {
    private static final String CONFIG_VERSION_PATH = "config-version";
    public ConfigFile[] configs;

    public ConfigurationUtil(ConfigFile[] configs) throws ConfigNotImplementedException {
        super(configs);
        this.configs = configs;

        for (ConfigFile file : configs) {
            if (!file.getFile().exists() && createFile(file) && file.isMustBeConfigured()) {
                throw new ConfigNotImplementedException("The config file must be configured!");
            } else if (reloadFile(file) && file.getDouble(CONFIG_VERSION_PATH) != file.getVersion()) {
                updateConfigFile(file);
            }
        }

        reloadFiles();
    }

    /**
     * Updates the config file to the latest version.
     * This method is used to override.
     * @param file The config file to update.
     */
    public void updateConfigFile(ConfigFile file) {
        updateFile(file);
    }

    public static class ConfigFile extends YamlFile {
        private final boolean mustBeConfigured;

        public ConfigFile(Path fileName, double version, boolean mustBeConfigured) {
            super(fileName, version);
            this.mustBeConfigured = mustBeConfigured;
        }

        @Override
        public String getString(@NotNull String path) {
            return super.getString(path);
        }

        @Override
        public int getMaxConfigWidth() {
            return 400;
        }

        public boolean isMustBeConfigured() {
            return mustBeConfigured;
        }
    }
}