package com.agrotech.config;

import java.sql.Connection;
import java.sql.Statement;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;

public class DataSourceConfig {

    public static DataSource createAndInit() throws Exception {
        JdbcDataSource ds = new JdbcDataSource();
        ds.setURL("jdbc:h2:file:./database/agrotech.db;AUTO_SERVER=TRUE");
        ds.setUser("sa");
        ds.setPassword("");

        try (Connection c = ds.getConnection(); Statement st = c.createStatement()) {
            st.execute("""
                CREATE TABLE IF NOT EXISTS lecturas (
                  id_sensor   VARCHAR(10),
                  fecha       VARCHAR(32),
                  humedad     DOUBLE,
                  temperatura DOUBLE,
                  CONSTRAINT pk_lecturas PRIMARY KEY (id_sensor, fecha)
                )
            """);
        }
        return ds;
    }
}
