package com.alpsbte.alpslib.libpsterra.utils;

import com.alpsbte.alpslib.libpsterra.core.config.ConfigPaths;
import me.arcaniax.hdb.api.HeadDatabaseAPI;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Level;


public class Utils {

    /**
     * Prefix used for all command permissions.
     */
    public static final String permissionPrefix = "plotsystem";

    // Player Messages
    public static String messagePrefix(FileConfiguration config){
        return config.getString(ConfigPaths.MESSAGE_PREFIX) + " ";
    }
    public static String getInfoMessageFormat(String info, FileConfiguration config) {
        return messagePrefix(config) + config.getString(ConfigPaths.MESSAGE_INFO_COLOUR) + info;
    }

    public static String getErrorMessageFormat(String error, FileConfiguration config) {
        return messagePrefix(config) + config.getString(ConfigPaths.MESSAGE_ERROR_COLOUR) + error;
    }

    public static boolean hasPermission(CommandSender sender, String permissionNode) {
        return sender.hasPermission(permissionPrefix + "." + permissionNode);
    }

    public static void sendConsoleMessage(String message, boolean sendMessage){
        if(sendMessage)
            Bukkit.getConsoleSender().sendMessage(message);
    }

    public static void sendConsoleError(String error, Exception ex, boolean sendMessage){
        if(sendMessage)
            Bukkit.getLogger().log(Level.SEVERE, error, ex);
    }

    public static void sendConsoleWarning(String warning, Exception ex, boolean sendMessage){
        if(sendMessage)
            Bukkit.getLogger().log(Level.WARNING, warning, ex);
    }
}
