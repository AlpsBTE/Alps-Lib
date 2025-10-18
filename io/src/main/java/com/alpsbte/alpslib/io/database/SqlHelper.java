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
     */
    @FunctionalInterface
    public interface SQLConsumer {
        void apply(PreparedStatement ps) throws SQLException;
    }

    /**
     * A functional interface representing an operation that supplies a result and may throw a {@link SQLException}.
     * <p>
     * This is similar to {@link java.util.function.Supplier}, but allows for SQL exceptions.
     * </p>
     *
     * @param <T> The type of result supplied
     */
    @FunctionalInterface
    public interface SQLCheckedSupplier<T> {
        T get() throws SQLException;
    }

    /**
     * A functional interface representing an operation that does not return a result and may throw a {@link SQLException}.
     * <p>
     * This is similar to {@link Runnable}, but allows for SQL exceptions.
     * </p>
     */
    @FunctionalInterface
    public interface SQLRunnable {
        void get() throws SQLException;
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
        try (Connection con = Objects.requireNonNull(DatabaseConnection.getConnection())) {
            return runQuery(query, con, action);
        }
    }

    /**
     * Executes a database query and returns a result using the provided {@link SQLFunction}.
     *
     * @param query  The SQL query string to execute
     * @param con    The {@link Connection} to use for the query execution
     * @param action A lambda or method reference that performs operations on the {@link PreparedStatement}
     * @param <T>    The type of result to return
     * @return The result returned by the {@code action}, or {@code null} if an exception occurs
     */
    public static <T> T runQuery(String query, Connection con, @NotNull SQLFunction<T> action) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(query)) {
            return action.apply(ps);
        }
    }

    /**
     * Executes a database query and returns a result using the provided {@link SQLFunction}.
     *
     * @param query  The SQL query string to execute
     * @param action A lambda or method reference that performs operations on the {@link PreparedStatement}
     */
    public static void runQuery(String query, @NotNull SQLConsumer action) throws SQLException {
        try (Connection con = Objects.requireNonNull(DatabaseConnection.getConnection()); PreparedStatement ps = con.prepareStatement(query)) {
            action.apply(ps);
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
    public static void runStatement(String sql, @NotNull SQLConsumer action) throws SQLException {
        try (Connection con = Objects.requireNonNull(DatabaseConnection.getConnection());
             PreparedStatement ps = con.prepareStatement(sql)) {
            action.apply(ps);
            ps.executeUpdate();
        }
    }
}
