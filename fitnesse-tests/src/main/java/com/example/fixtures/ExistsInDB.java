package com.example.fixtures;

import com.example.common.DatabaseConnector;

public class ExistsInDB {

    private final DatabaseConnector db = new DatabaseConnector();
    private String table;
    private String column;

    public void setTable(String table) {
        this.table = table;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public boolean exists(String value) {
        for (int i = 0; i < 10; i++) {
            if (db.recordExists(table, column, value)) return true;
            try { Thread.sleep(2000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
        }
        return false;
    }
}
