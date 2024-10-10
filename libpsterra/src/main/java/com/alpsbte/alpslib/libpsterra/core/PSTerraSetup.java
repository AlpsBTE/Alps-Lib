package com.alpsbte.alpslib.libpsterra.core;

import java.util.ArrayList;
import java.util.List;

import com.alpsbte.alpslib.libpsterra.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.MenuFunctionListener;
import com.alpsbte.alpslib.libpsterra.commands.CMD_CreatePlot;
import com.alpsbte.alpslib.libpsterra.commands.CMD_PastePlot;
import com.alpsbte.alpslib.libpsterra.core.config.ConfigManager;
import com.alpsbte.alpslib.libpsterra.core.config.ConfigPaths;
import com.alpsbte.alpslib.libpsterra.core.config.DataMode;
import com.alpsbte.alpslib.libpsterra.core.plotsystem.PlotCreator;
import com.alpsbte.alpslib.libpsterra.core.plotsystem.PlotPaster;
import com.alpsbte.alpslib.libpsterra.utils.FTPManager;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import org.jetbrains.annotations.Nullable;

public class PSTerraSetup {
    public PlotCreator plotCreator;
    public PlotPaster plotPaster;
    public Connection connection;
    public ConfigManager configManager;

    /**
     * creates/instantiates all the necessary classes and event listeners required for plugin operation
     *
     * @param plugin The plugin instance
     * @throws Exception If an error occurs during setup
     */
    public static PSTerraSetup setupPlugin(JavaPlugin plugin, String version) throws Exception{
        return setupPlugin(plugin, version, true, null, null, null);
    }

    /**
     * creates/instantiates all the necessary classes and event listeners required for plugin operation
     *
     * @param plugin The plugin instance
     * @param version The version of the plugin
     * @param consoleOutput If console output should be enabled
     * @param configPath The path to the config file (null if default)
     * @param configFileName The name of the config file (null if default)
     * @param defaultConfigFileName The name of the default config file (null if default)
     * @throws Exception If an error occurs during setup
     */
    public static PSTerraSetup setupPlugin(JavaPlugin plugin, String version, boolean consoleOutput,
                                           @Nullable String configPath, @Nullable String configFileName, @Nullable String defaultConfigFileName) throws Exception{

        PSTerraSetup result = new PSTerraSetup();

        String successPrefix = ChatColor.DARK_GRAY + "[" + ChatColor.DARK_GREEN + "✔" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;
        String errorPrefix = ChatColor.DARK_GRAY + "[" + ChatColor.RED + "X" + ChatColor.DARK_GRAY + "] " + ChatColor.GRAY;

        Utils.sendConsoleMessage(ChatColor.GOLD + "--------------- Plot-System-Terra V" + version + " ----------------", consoleOutput);
        Utils.sendConsoleMessage(ChatColor.DARK_GREEN + "Starting plugin...", consoleOutput);
        Utils.sendConsoleMessage(" ", consoleOutput);

        // Check for required dependencies, if it returns false disable plugin
        if (!DependencyManager.checkForRequiredDependencies(plugin)) {
            Utils.sendConsoleMessage(errorPrefix + "Could not load required dependencies.", consoleOutput);
            Utils.sendConsoleMessage(ChatColor.YELLOW + "Missing Dependencies:", consoleOutput);

            DependencyManager.missingDependencies.forEach(dependency -> Utils.sendConsoleMessage(ChatColor.YELLOW + " - " + dependency, consoleOutput));
            throw new RuntimeException("Could not load required dependencies");
        }
        Utils.sendConsoleMessage(successPrefix + "Successfully loaded required dependencies.", consoleOutput);

        // Load config
        result.configManager = new ConfigManager(plugin, consoleOutput, configPath, configFileName, defaultConfigFileName);
        Utils.sendConsoleMessage(successPrefix + "Successfully loaded configuration file.", consoleOutput);
        result.configManager.reloadConfigs();
        FileConfiguration configFile = result.configManager.getConfig();

        // Initialize connection


        Utils.sendConsoleMessage(successPrefix + "datamode:"+ configFile.getString(ConfigPaths.DATA_MODE), consoleOutput);
        if(configFile.getString(ConfigPaths.DATA_MODE).equalsIgnoreCase(DataMode.DATABASE.toString())){

            String URL = configFile.getString(ConfigPaths.DATABASE_URL);
            String name = configFile.getString(ConfigPaths.DATABASE_NAME);
            String username = configFile.getString(ConfigPaths.DATABASE_USERNAME);
            String password = configFile.getString(ConfigPaths.DATABASE_PASSWORD);
            String teamApiKey = configFile.getString(ConfigPaths.API_KEY);
            
            result.connection = new DatabaseConnection(URL, name, username, password, teamApiKey);// DatabaseConnection.InitializeDatabase();
            Utils.sendConsoleMessage(successPrefix + "Successfully initialized database connection.", consoleOutput);
        }else{
            String teamApiKey = configFile.getString(ConfigPaths.API_KEY);
            String apiHost = configFile.getString(ConfigPaths.API_URL);
            
            int apiPort = configFile.getInt(ConfigPaths.API_KEY);

            result.connection = new NetworkAPIConnection(apiHost, apiPort, teamApiKey);
            // String name = configFile.getString(ConfigPaths.DATABASE_NAME);
            // String username = configFile.getString(ConfigPaths.DATABASE_USERNAME);
            // String password = configFile.getString(ConfigPaths.DATABASE_PASSWORD);
            Utils.sendConsoleMessage(successPrefix + "Successfully initialized API connection.", consoleOutput);
        }
        if (result.connection == null)
            throw new RuntimeException("Connection initialization failed");


        //check FTP
        FTPManager.testSFTPConnection_VFS2(result.connection, consoleOutput);
        Utils.sendConsoleMessage(successPrefix + "Successfully tested FTP connection.", consoleOutput);
            
        // Register event listeners
        plugin.getServer().getPluginManager().registerEvents(new EventListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new MenuFunctionListener(), plugin);
        Utils.sendConsoleMessage(successPrefix + "Successfully registered event listeners.", consoleOutput);

        result.plotCreator = new PlotCreator(plugin, configFile, result.connection);

        // Start checking for plots to paste
        result.plotPaster = new PlotPaster(plugin, configFile, result.connection, consoleOutput);
        result.plotPaster.start();



        // Register commands

        plugin.getCommand("createplot").setExecutor(new CMD_CreatePlot(result.plotCreator, result.connection, configFile));
        plugin.getCommand("pasteplot").setExecutor(new CMD_PastePlot(result.plotPaster, result.connection, configFile));

        Utils.sendConsoleMessage(successPrefix + "Successfully registered commands.", consoleOutput);


        return result;
    }

    public static class DependencyManager {

        // List with all missing dependencies
        private final static List<String> missingDependencies = new ArrayList<>();

        /**
         * Check for all required dependencies and inform in console about missing dependencies
         * @return True if all dependencies are present
         */
        private static boolean checkForRequiredDependencies(Plugin plugin) {
            PluginManager pluginManager = plugin.getServer().getPluginManager();

            if (!pluginManager.isPluginEnabled("WorldEdit")) {
                missingDependencies.add("WorldEdit (V6.1.9)");
            }

            if (!pluginManager.isPluginEnabled("HeadDatabase")) {
                missingDependencies.add("HeadDatabase");
            }

            return missingDependencies.isEmpty();
        }

        /**
         * @return World Edit instance
         */
        public static WorldEdit getWorldEdit() {
            return WorldEdit.getInstance();
        }

        /**
         * @return World Edit Plugin
         */
        public static WorldEditPlugin getWorldEditPlugin() {
            return (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        }
    }
};
