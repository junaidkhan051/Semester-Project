package com.bugtracker.repository;

import com.bugtracker.db.DBConnection;
import com.bugtracker.model.BugHistory;

import java.sql.*;

public class BugHistoryRepository {
    public void save(BugHistory h) {
        String sql = "INSERT INTO BugHistory(action, bug_id) VALUES (?, ?)";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, h.getAction());
            ps.setInt(2, h.getBug().getId());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
