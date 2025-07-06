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

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Provides centralized management for the database connection.
 * <p>
 * This class initializes and manages a HikariCP connection pool
 * and offers methods to obtain and close database connections.
 * </p>
 */
public class DatabaseConnection {
    private DatabaseConnection() {}

    private static HikariDataSource hikari;
    private static Logger logger;

    /**
     * Initializes the connection pool with the given configuration data.
     *
     * @param config          The database configuration
     */
    public static void initializeDatabase(@NotNull DatabaseSection config, boolean enableLogging) throws ClassNotFoundException {
        Class.forName("org.mariadb.jdbc.Driver");
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.getUrl() + config.getDbName() + "?allowMultiQueries=true");
        hikariConfig.setUsername(config.getUsername());
        hikariConfig.setPassword(config.getPassword());
        hikariConfig.addDataSourceProperty("cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", "250");
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        hikariConfig.addDataSourceProperty("useServerPrepStmts", "true");
        hikariConfig.setMaxLifetime(config.getMaxLifetime());
        hikariConfig.setConnectionTimeout(config.getConnectionTimeout());
        hikariConfig.setKeepaliveTime(config.getKeepaliveTime());
        hikariConfig.setMaximumPoolSize(config.getMaximumPoolSize());
        hikariConfig.setLeakDetectionThreshold(config.getLeakDetectionThreshold());
        hikariConfig.setPoolName(config.getPoolName());

        hikari = new HikariDataSource(hikariConfig);

        if (enableLogging) {
            logger = LoggerFactory.getLogger(DatabaseConnection.class);
        }
    }

    /**
     * Returns a new connection from the connection pool.
     *
     * @return An open SQL connection
     * @throws SQLException If no connection is available
     */
    public static @NotNull Connection getConnection() throws SQLException {
        if (hikari == null) {
            throw new SQLException("Unable to get a connection from the pool. (hikari is null)");
        }

        Connection connection = hikari.getConnection();
        if (connection == null) {
            throw new SQLException("Unable to get a connection from the pool. (getConnection returned null)");
        }

        return connection;
    }

    /**
     * Closes the connection pool and releases all resources.
     * Logs success or a warning if no connection exists, if logger is set.
     */
    public static void shutdown() {
        if (hikari != null) {
            hikari.close();
            if (logger != null) logger.info("Database connection closed successfully.");
        } else {
            if (logger != null) logger.warn("No database connection to close.");
        }
    }
}
