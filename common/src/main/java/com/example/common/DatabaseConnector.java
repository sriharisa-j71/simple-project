package com.example.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseConnector implements AutoCloseable {

    private final Connection connection;

    public DatabaseConnector() {
        this("jdbc:postgresql://localhost:5432/eventstore", "testuser", "testpass");
    }

    public DatabaseConnector(String jdbcUrl, String username, String password) {
        try {
            this.connection = DriverManager.getConnection(jdbcUrl, username, password);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to PostgreSQL", e);
        }
    }

    public boolean recordExists(String table, String column, String value) {
        String sql = "SELECT COUNT(*) FROM " + table + " WHERE " + column + " = ?";
        try (var ps = connection.prepareStatement(sql)) {
            ps.setString(1, value);
            try (var rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Query failed", e);
        }
    }

    public void executeUpdate(String sql) {
        try (var stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Update failed", e);
        }
    }

    public ResultSet executeQuery(String sql) {
        try {
            var stmt = connection.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Query failed", e);
        }
    }

    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to close connection", e);
        }
    }
}
