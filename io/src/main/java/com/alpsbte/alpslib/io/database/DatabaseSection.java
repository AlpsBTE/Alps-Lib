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
