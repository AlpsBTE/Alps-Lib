package com.alpsbte.alpslib.io.database;

import lombok.Value;

/**
 * Configuration data for the database connection.
 * <p>
 * This class contains all necessary parameters to establish and configure a connection
 * to a database using HikariCP.
 * </p>
 */
@Value
public class DatabaseSection {
    /**
     * The JDBC URL of the database (without database name).
     */
    String url;
    /** The name of the database. */
    String dbName;
    /** The username for authentication. */
    String username;
    /** The password for authentication. */
    String password;
    /** Maximum lifetime of a connection in the pool (milliseconds). */
    long maxLifetime;
    /** Timeout for establishing a connection (milliseconds). */
    long connectionTimeout;
    /** Interval for keepalive pings (milliseconds). */
    long keepaliveTime;
    /** Maximum number of connections in the pool. */
    int maximumPoolSize;
    /** Threshold for leak detection (milliseconds). */
    long leakDetectionThreshold;
    /** Name of the connection pool. */
    String poolName;
}
