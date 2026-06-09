package com.example.fixtures;

import com.example.common.DuckDbConnector;

public class DuckDbQueryFixture {

    private final DuckDbConnector duckdb = new DuckDbConnector();

    public long rowCountFromParquet(String parquetPath) {
        duckdb.execute("CREATE TABLE IF NOT EXISTS parquet_events AS SELECT * FROM read_parquet('" + parquetPath + "')");
        return duckdb.querySingleLong("SELECT COUNT(*) FROM parquet_events");
    }

    public long rowCountFromCsv(String csvPath) {
        duckdb.execute("CREATE TABLE IF NOT EXISTS csv_events AS SELECT * FROM read_csv_auto('" + csvPath + "')");
        return duckdb.querySingleLong("SELECT COUNT(*) FROM csv_events");
    }

    public long queryCount(String sql) {
        return duckdb.querySingleLong(sql);
    }

    public void runSql(String sql) {
        duckdb.execute(sql);
    }
}
