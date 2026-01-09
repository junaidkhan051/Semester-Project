package com.bugtracker.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DataInitializer {
    public static void initialize() {
        createTables();
        ensureProduct("CUI Portal", "1.0", "Student and Faculty Portal");
        ensureProduct("Banking Application", "2.0", "Core Banking System");

        // Seed Users - Different Roles
        ensureUser("admin", "admin123", "Administrator", "admin@bugtracker.com", "ADMIN");
        ensureUser("manager", "manager123", "Project Manager", "manager@bugtracker.com", "MANAGER");
        ensureUser("dev1", "dev123", "Alice Developer", "alice@bugtracker.com", "TECH_PERSON");
        ensureUser("dev2", "dev123", "Bob Developer", "bob@bugtracker.com", "TECH_PERSON");
        ensureUser("tester1", "test123", "Charlie Tester", "charlie@bugtracker.com", "CUSTOMER");
        ensureUser("tester2", "test123", "Diana Tester", "diana@bugtracker.com", "CUSTOMER");
    }

    private static void createTables() {
        String[] sqls = {
                "IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'User') " +
                        "CREATE TABLE [User] (" +
                        "    id INT IDENTITY(1,1) PRIMARY KEY," +
                        "    username NVARCHAR(100) NOT NULL UNIQUE," +
                        "    password NVARCHAR(256) NOT NULL," +
                        "    full_name NVARCHAR(200)," +
                        "    email NVARCHAR(200)," +
                        "    role NVARCHAR(50)" +
                        ");",

                "IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Product') " +
                        "CREATE TABLE Product (" +
                        "    id INT IDENTITY(1,1) PRIMARY KEY," +
                        "    name NVARCHAR(200) NOT NULL," +
                        "    version NVARCHAR(50)," +
                        "    description NVARCHAR(MAX)" +
                        ");",

                "IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Bug') " +
                        "CREATE TABLE Bug (" +
                        "    id INT IDENTITY(1,1) PRIMARY KEY," +
                        "    title NVARCHAR(300) NOT NULL," +
                        "    description NVARCHAR(MAX)," +
                        "    steps NVARCHAR(MAX)," +
                        "    severity NVARCHAR(50)," +
                        "    status NVARCHAR(50)," +
                        "    created_at DATETIME2 DEFAULT SYSUTCDATETIME()," +
                        "    updated_at DATETIME2 DEFAULT SYSUTCDATETIME()," +
                        "    reporter_id INT FOREIGN KEY REFERENCES [User](id)," +
                        "    assignee_id INT NULL FOREIGN KEY REFERENCES [User](id)," +
                        "    product_id INT FOREIGN KEY REFERENCES Product(id)" +
                        ");"
        };

        try (Connection c = DBConnection.getConnection(); java.sql.Statement s = c.createStatement()) {
            for (String sql : sqls) {
                s.execute(sql);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void ensureProduct(String name, String version, String desc) {
        String checkSql = "SELECT COUNT(*) FROM Product WHERE name = ?";
        String insertSql = "INSERT INTO Product (name, version, description) VALUES (?, ?, ?)";

        try (Connection c = DBConnection.getConnection()) {
            // Check if exists
            try (PreparedStatement ps = c.prepareStatement(checkSql)) {
                ps.setString(1, name);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0)
                        return; // Already exists
                }
            }

            // Insert
            try (PreparedStatement ps = c.prepareStatement(insertSql)) {
                ps.setString(1, name);
                ps.setString(2, version);
                ps.setString(3, desc);
                ps.executeUpdate();
                System.out.println("Seeded product: " + name);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void ensureUser(String username, String password, String fullName, String email, String role) {
        String checkSql = "SELECT COUNT(*) FROM [User] WHERE username = ?";
        String updateSql = "UPDATE [User] SET password = ? WHERE username = ?";
        String insertSql = "INSERT INTO [User] (username, password, full_name, email, role) VALUES (?, ?, ?, ?, ?)";

        try (Connection c = DBConnection.getConnection()) {
            // Check if exists
            boolean exists = false;
            try (PreparedStatement ps = c.prepareStatement(checkSql)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0)
                        exists = true;
                }
            }

            if (exists) {
                // Update password to ensure it matches
                try (PreparedStatement ps = c.prepareStatement(updateSql)) {
                    ps.setString(1, password);
                    ps.setString(2, username);
                    ps.executeUpdate();
                }
                return;
            }

            // Insert
            try (PreparedStatement ps = c.prepareStatement(insertSql)) {
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, fullName);
                ps.setString(4, email);
                ps.setString(5, role);
                ps.executeUpdate();
                System.out.println("Seeded user: " + username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, "Error seeding user " + username + ": " + e.getMessage());
        }
    }
}
