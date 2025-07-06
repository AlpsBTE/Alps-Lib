/*
 *  The MIT License (MIT)
 *
 *  Copyright © 2021-2025, Alps BTE <bte.atchli@gmail.com>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

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
