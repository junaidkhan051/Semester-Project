package com.bugtracker.repository;

import com.bugtracker.db.DBConnection;
import com.bugtracker.model.Bug;
import com.bugtracker.model.Product;
import com.bugtracker.model.User;
import com.bugtracker.model.enums.Severity;
import com.bugtracker.model.enums.Status;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BugRepository {
    private final UserRepository userRepo = new UserRepository();
    private final ProductRepository productRepo = new ProductRepository();

    public void save(Bug bug) {
        String sql = "INSERT INTO Bug(title, description, steps, severity, status, reporter_id, assignee_id, product_id) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection c = DBConnection.getConnection();
                PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, bug.getTitle());
            ps.setString(2, bug.getDescription());
            ps.setString(3, bug.getSteps());
            ps.setString(4, bug.getSeverity().name());
            ps.setString(5, bug.getStatus().name());
            ps.setInt(6, bug.getReporter().getId());
            if (bug.getAssignee() != null)
                ps.setInt(7, bug.getAssignee().getId());
            else
                ps.setNull(7, Types.INTEGER);
            ps.setInt(8, bug.getProduct().getId());
            ps.executeUpdate();
            try (ResultSet g = ps.getGeneratedKeys()) {
                if (g.next())
                    bug.setId(g.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStatus(int bugId, Status status) {
        String sql = "UPDATE Bug SET status = ?, updated_at = SYSUTCDATETIME() WHERE id = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, bugId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Bug findById(int id) {
        String sql = "SELECT * FROM Bug WHERE id = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return map(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Bug> findAll() {
        List<Bug> list = new ArrayList<>();
        String sql = "SELECT * FROM Bug ORDER BY created_at DESC";
        try (Connection c = DBConnection.getConnection();
                Statement s = c.createStatement();
                ResultSet rs = s.executeQuery(sql)) {
            while (rs.next())
                list.add(map(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Bug> findByAssignee(int userId) {
        List<Bug> list = new ArrayList<>();
        String sql = "SELECT * FROM Bug WHERE assignee_id = ? ORDER BY created_at DESC";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    list.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Bug> findByReporter(int userId) {
        List<Bug> list = new ArrayList<>();
        String sql = "SELECT * FROM Bug WHERE reporter_id = ? ORDER BY created_at DESC";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    list.add(map(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void updateAssignee(int bugId, int assigneeId) {
        String sql = "UPDATE Bug SET assignee_id = ?, updated_at = SYSUTCDATETIME() WHERE id = ?";
        try (Connection c = DBConnection.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, assigneeId);
            ps.setInt(2, bugId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Bug map(ResultSet rs) throws SQLException {
        Bug b = new Bug();
        b.setId(rs.getInt("id"));
        b.setTitle(rs.getString("title"));
        b.setDescription(rs.getString("description"));
        b.setSteps(rs.getString("steps"));
        String sev = rs.getString("severity");
        String st = rs.getString("status");
        if (sev != null)
            b.setSeverity(Severity.valueOf(sev));
        if (st != null)
            b.setStatus(Status.valueOf(st));
        Timestamp ca = rs.getTimestamp("created_at");
        Timestamp ua = rs.getTimestamp("updated_at");
        if (ca != null)
            b.setCreatedAt(ca.toLocalDateTime());
        if (ua != null)
            b.setUpdatedAt(ua.toLocalDateTime());

        int reporterId = rs.getInt("reporter_id");
        b.setReporter(userRepo.findById(reporterId));
        int assigneeId = rs.getInt("assignee_id");
        if (!rs.wasNull())
            b.setAssignee(userRepo.findById(assigneeId));
        int productId = rs.getInt("product_id");
        b.setProduct(productRepo.findById(productId));
        return b;
    }
}
