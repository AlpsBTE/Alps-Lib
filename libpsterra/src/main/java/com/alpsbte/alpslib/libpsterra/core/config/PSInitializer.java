package com.alpsbte.alpslib.libpsterra.core.config;

public class PSInitializer {

    private final boolean isDefaultInitializer;

    private final boolean consoleOutput;
    private final boolean checkForUpdates;
    private final boolean devMode;
    private final boolean configMustBeConfigured;
    private final String configPath;
    private final String configFileName;
    private final String defaultConfigFileName;
    private final String APIHost;
    private final int APIPort;
    private final String APIKey;

    /**
     * Initializes a new PSInitializer with default values.
     */
    public PSInitializer(){
        this.isDefaultInitializer = true;

        this.consoleOutput = true;
        this.checkForUpdates = true;
        this.devMode = false;
        this.configMustBeConfigured = true;
        this.configPath = null;
        this.configFileName = null;
        this.defaultConfigFileName = null;
        this.APIHost = null;
        this.APIPort = 0;
        this.APIKey = null;
    }


    /**
     * Initializes a new PSInitializer with a given config file.
     */
    public PSInitializer(JavaPlugin plugin, FileConfiguration configFile){
        this.isDefaultInitializer = true;

        this.consoleOutput = true;
        this.checkForUpdates = configFile.getBoolean(ConfigPaths.CHECK_FOR_UPDATES);
        this.devMode = configFile.getBoolean(ConfigPaths.DEV_MODE);
        this.worldName = configFile.getString(ConfigPaths.WORLD_NAME);
        this.configMustBeConfigured = true;
        this.pluginConfigPath = "";
        this.absolutePluginConfigPath = plugin.getDataFolder().getAbsolutePath() + pluginConfigPath;
        this.configFileName = CONFIG_FILE_NAME;
        this.defaultConfigFileName = DEFAULT_CONFIG_FILE_NAME;
        this.APIHost = configFile.getString(ConfigPaths.API_URL);
        this.APIPort = configFile.getInt(ConfigPaths.API_PORT);
        this.APIKey = configFile.getString(ConfigPaths.API_KEY);
    }

    /**
     * Initializes a new PSInitializer with the given values.
     * Allows other plugins to initialize the plugin with different settings to allow embedding the plugin in other plugins.
     *
     * @param consoleOutput Whether the plugin should output messages to the console
     * @param checkForUpdates Whether the plugin should check for updates
     * @param devMode Whether the plugin should run in development mode
     * @param configPath The path to the config file
     * @param configFileName The name of the config file
     * @param defaultConfigFileName The name of the default config file
     * @param APIHost The URL of the API
     * @param APIPort The port of the API
     * @param APIKey The key of the API
     */
    public PSInitializer(boolean consoleOutput, boolean checkForUpdates, boolean devMode, boolean configMustBeConfigured, String configPath, String configFileName, String defaultConfigFileName, String APIHost, int APIPort, String APIKey) {
        this.isDefaultInitializer = false;

        this.consoleOutput = consoleOutput;
        this.checkForUpdates = checkForUpdates;
        this.devMode = devMode;
        this.configMustBeConfigured = configMustBeConfigured;
        this.configPath = configPath;
        this.configFileName = configFileName;
        this.defaultConfigFileName = defaultConfigFileName;
        this.APIHost = APIHost;
        this.APIPort = APIPort;
        this.APIKey = APIKey;
    }


    public boolean isDefaultInitializer() {
        return isDefaultInitializer;
    }

    public boolean isConsoleOutput() {
        return consoleOutput;
    }

    public boolean isCheckForUpdates() {
        return checkForUpdates;
    }

    public boolean isDevMode() {
        return devMode;
    }

    public boolean isConfigMustBeConfigured() {
        return configMustBeConfigured;
    }

    public String getConfigPath() {
        return configPath;
    }

    public String getConfigFileName() {
        return configFileName;
    }

    public String getDefaultConfigFileName() {
        return defaultConfigFileName;
    }

    public String getAPIHost() {
        return APIHost;
    }

    public int getAPIPort() {
        return APIPort;
    }

    public String getAPIKey() {
        return APIKey;
    }
}
