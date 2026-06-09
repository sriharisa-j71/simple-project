package com.example.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public final class DuckDbConnector implements AutoCloseable {

    private final Connection connection;

    public DuckDbConnector() {
        this("jdbc:duckdb:");
    }

    public DuckDbConnector(String jdbcUrl) {
        try {
            this.connection = DriverManager.getConnection(jdbcUrl);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to DuckDB", e);
        }
    }

    public void execute(String sql) {
        try (var stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("DuckDB execute failed: " + sql, e);
        }
    }

    public ResultSet query(String sql) {
        try {
            var stmt = connection.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            throw new RuntimeException("DuckDB query failed: " + sql, e);
        }
    }

    public long querySingleLong(String sql) {
        try (var rs = query(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            throw new RuntimeException("No results from: " + sql);
        } catch (SQLException e) {
            throw new RuntimeException("DuckDB query failed: " + sql, e);
        }
    }

    public String querySingleString(String sql) {
        try (var rs = query(sql)) {
            if (rs.next()) {
                return rs.getString(1);
            }
            throw new RuntimeException("No results from: " + sql);
        } catch (SQLException e) {
            throw new RuntimeException("DuckDB query failed: " + sql, e);
        }
    }

    public void loadParquet(String tableName, String parquetPath) {
        execute("CREATE TABLE IF NOT EXISTS " + tableName + " AS SELECT * FROM read_parquet('" + parquetPath + "')");
    }

    public void loadCsv(String tableName, String csvPath) {
        execute("CREATE TABLE IF NOT EXISTS " + tableName + " AS SELECT * FROM read_csv_auto('" + csvPath + "')");
    }

    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to close DuckDB connection", e);
        }
    }
}
