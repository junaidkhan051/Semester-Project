package com.bugtracker.repository;

import com.bugtracker.db.DBConnection;
import com.bugtracker.model.BugAttachment;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BugAttachmentRepository {

    public void save(BugAttachment attachment) {
        String sql = "INSERT INTO BugAttachment (bug_id, file_name, file_path, file_size) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, attachment.getBugId());
            ps.setString(2, attachment.getFileName());
            ps.setString(3, attachment.getFilePath());
            ps.setLong(4, attachment.getFileSize());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                attachment.setId(rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<BugAttachment> findByBugId(int bugId) {
        List<BugAttachment> attachments = new ArrayList<>();
        String sql = "SELECT * FROM BugAttachment WHERE bug_id = ? ORDER BY uploaded_at DESC";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bugId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                BugAttachment attachment = new BugAttachment();
                attachment.setId(rs.getInt("id"));
                attachment.setBugId(rs.getInt("bug_id"));
                attachment.setFileName(rs.getString("file_name"));
                attachment.setFilePath(rs.getString("file_path"));
                attachment.setFileSize(rs.getLong("file_size"));

                Timestamp ts = rs.getTimestamp("uploaded_at");
                if (ts != null) {
                    attachment.setUploadedAt(ts.toLocalDateTime());
                }

                attachments.add(attachment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return attachments;
    }

    public void delete(int attachmentId) {
        String sql = "DELETE FROM BugAttachment WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, attachmentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
