package com.bugtracker.repository;

import com.bugtracker.db.DBConnection;
import com.bugtracker.model.BugComment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BugCommentRepository {
    public void save(BugComment c) {
        String sql = "INSERT INTO BugComment(text, author_id, bug_id) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getText());
            ps.setInt(2, c.getAuthor().getId());
            ps.setInt(3, c.getBug().getId());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<BugComment> findForBug(int bugId) {
        List<BugComment> list = new ArrayList<>();
        String sql = "SELECT * FROM BugComment WHERE bug_id = ? ORDER BY created_at";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bugId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BugComment c = new BugComment();
                    c.setId(rs.getInt("id"));
                    c.setText(rs.getString("text"));
                    c.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    // For simplicity we won't fully map author/bug here in this example
                    list.add(c);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}
