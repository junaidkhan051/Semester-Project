package com.bugtracker.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:sqlserver://localhost;" +
            "instanceName=SQLEXPRESS;" +
            "databaseName=BugTracker;" +
            "integratedSecurity=true;" +
            "trustServerCertificate=true;";

    static {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // No username/password required for Windows Authentication
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
