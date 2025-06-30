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

import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

/**
 * Helper class for executing SQL statements and queries.
 * <p>
 * Provides methods for safe execution of SQL operations with automatic resource management.
 * </p>
 */
@SuppressWarnings({"unused", "SqlSourceToSinkFlow"})
public class SqlHelper {
    private SqlHelper() {
        // Prevent instantiation
    }

    /**
     * A functional interface representing an operation that accepts a {@link PreparedStatement}
     * and returns a result, potentially throwing a {@link SQLException}.
     *
     * @param <T> The type of the result produced by the function
     */
    @FunctionalInterface
    public interface SQLFunction<T> {
        T apply(PreparedStatement ps) throws SQLException;
    }

    /**
     * A functional interface representing an operation that accepts a {@link PreparedStatement}
     * and returns no result, potentially throwing a {@link SQLException}.
     *
     * @param <T> The type of the input (usually {@link PreparedStatement})
     */
    @FunctionalInterface
    public interface SQLConsumer<T> {
        void accept(T t) throws SQLException;
    }

    /**
     * Executes a database query and returns a result using the provided {@link SQLFunction}.
     *
     * @param query  The SQL query string to execute
     * @param action A lambda or method reference that performs operations on the {@link PreparedStatement}
     * @param <T>    The type of result to return
     * @return The result returned by the {@code action}, or {@code null} if an exception occurs
     */
    public static <T> T runQuery(String query, @NotNull SQLFunction<T> action) throws SQLException {
        try (Connection con = Objects.requireNonNull(DatabaseConnection.getConnection());
             PreparedStatement ps = con.prepareStatement(query)) {
            return action.apply(ps);
        }
    }

    /**
     * Executes a database query and returns a result using the provided {@link SQLFunction}.
     * This method returns auto-generated keys.
     *
     * @param query  The SQL query string to execute
     * @param action A lambda or method reference that performs operations on the {@link PreparedStatement}
     * @param <T>    The type of result to return
     * @return The result returned by the {@code action}
     */
    public static <T> @NotNull T runInsertQuery(String query, @NotNull SQLFunction<T> action) throws SQLException {
        try (Connection con = Objects.requireNonNull(DatabaseConnection.getConnection())) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                T r = action.apply(ps);
                con.commit();
                return r; // Return the result after successful execution
            } catch (SQLException e) {
                con.rollback();
                throw e; // Re-throw the exception after rollback
            }
        }
    }

    /**
     * Executes a database update or mutation operation using the provided {@link SQLConsumer}.
     * This method is intended for operations that do not return a result (e.g. {@code UPDATE}, {@code INSERT}, {@code DELETE}).
     * It executes the SQL statement automatically.
     *
     * @param sql    The SQL statement to execute
     * @param action A lambda or method reference that performs the update using the {@link PreparedStatement}
     */
    public static void runStatement(String sql, @NotNull SQLConsumer<PreparedStatement> action) throws SQLException {
        try (Connection con = Objects.requireNonNull(DatabaseConnection.getConnection());
             PreparedStatement ps = con.prepareStatement(sql)) {
            action.accept(ps);
            ps.executeUpdate();
        }
    }
}
