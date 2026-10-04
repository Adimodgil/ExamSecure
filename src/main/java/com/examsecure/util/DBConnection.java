package com.examsecure.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Reads database settings from db.properties (on the classpath),
 * so the password is never hard-coded in source code.
 * Copy db.properties.example to db.properties and fill in your values.
 */
public class DBConnection {

    private static final Properties CONFIG = load();

    private static Properties load() {
        Properties p = new Properties();
        try (InputStream in = DBConnection.class.getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (in != null) {
                p.load(in);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return p;
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL driver jar missing in WEB-INF/lib", e);
        }

        String url = CONFIG.getProperty("db.url");
        String user = CONFIG.getProperty("db.user");
        String password = CONFIG.getProperty("db.password");

        if (url == null || user == null || password == null) {
            throw new SQLException(
                "db.properties not found. Copy db.properties.example to "
              + "src/main/resources/db.properties and set your MySQL password.");
        }

        return DriverManager.getConnection(url, user, password);
    }
}
