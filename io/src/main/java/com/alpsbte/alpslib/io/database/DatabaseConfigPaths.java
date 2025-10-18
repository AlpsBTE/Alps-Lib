package com.alpsbte.alpslib.io.database;

import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class DatabaseConfigPaths {
    private DatabaseConfigPaths() {throw new IllegalStateException("Utility class");}

    private static final String DATABASE = "database.";
    public static final String DATABASE_URL = DATABASE + "url";
    public static final String DATABASE_NAME = DATABASE + "dbname";
    public static final String DATABASE_USERNAME = DATABASE + "username";
    public static final String DATABASE_PASSWORD = DATABASE + "password";
    public static final String DATABASE_MAX_LIFETIME = DATABASE + "max-lifetime";
    public static final String DATABASE_CONNECTION_TIMEOUT = DATABASE + "connection-timeout";
    public static final String DATABASE_KEEPALIVE_TIME = DATABASE + "keepalive-time";
    public static final String DATABASE_MAXIMUM_POOL_SIZE = DATABASE + "maximum-pool-size";
    public static final String DATABASE_LEAK_DETECTION_THRESHOLD = DATABASE + "leak-detection-threshold";
    public static final String DATABASE_POOL_NAME = DATABASE + "pool-name";

    @Contract("_ -> new")
    public static @NotNull DatabaseSection getConfig(@NotNull FileConfiguration config) {
        return new DatabaseSection(
                config.getString(DATABASE_URL),
                config.getString(DATABASE_NAME),
                config.getString(DATABASE_USERNAME),
                config.getString(DATABASE_PASSWORD),
                config.getLong(DATABASE_MAX_LIFETIME, 1800000L), // Default: 30 minutes
                config.getLong(DATABASE_CONNECTION_TIMEOUT, 30000L), // Default: 30 seconds
                config.getLong(DATABASE_KEEPALIVE_TIME, 120000L), // Default: 2 minutes
                config.getInt(DATABASE_MAXIMUM_POOL_SIZE, 10), // Default: 10 connections
                config.getLong(DATABASE_LEAK_DETECTION_THRESHOLD, 0L), // Default: no leak detection
                config.getString(DATABASE_POOL_NAME, "plotsystem-hikari")
        );
    }
}
