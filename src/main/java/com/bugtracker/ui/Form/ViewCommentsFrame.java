package com.bugtracker.ui.Form;

import com.bugtracker.db.DBConnection;

import com.bugtracker.ui.GlassPanel;
import com.bugtracker.ui.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewCommentsFrame extends JFrame {

    public ViewCommentsFrame() {
        setTitle("View Comments & Solutions");
        setSize(900, 600);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setBackground(new Color(0, 0, 0, 0));

        // Glass Panel
        GlassPanel glassPanel = new GlassPanel();
        glassPanel.setLayout(new BorderLayout(0, 20));
        glassPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        setContentPane(glassPanel);

        // Header
        glassPanel.add(createHeader(), BorderLayout.NORTH);

        // Table
        glassPanel.add(createTablePanel(), BorderLayout.CENTER);

    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel title = new JLabel("Comments & Solutions");
        title.setFont(Theme.FONT_TITLE.deriveFont(24f));
        title.setForeground(Theme.TEXT_WHITE);
        header.add(title, BorderLayout.WEST);

        JButton closeBtn = new JButton("X");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 18));
        closeBtn.setForeground(Theme.TEXT_WHITE);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());
        header.add(closeBtn, BorderLayout.EAST);

        return header;
    }

    private JScrollPane createTablePanel() {
        String[] columns = { "ID", "Bug ID", "Title", "Comment", "Author", "Date" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        loadComments(model);

        JTable table = new JTable(model);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(new Color(0, 0, 0, 0));

        return scrollPane;
    }

    private void styleTable(JTable table) {
        table.setFont(Theme.FONT_REGULAR);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setOpaque(false);
        table.setBackground(new Color(0, 0, 0, 0));
        table.setForeground(Theme.TEXT_WHITE);
        table.setSelectionBackground(new Color(255, 255, 255, 30));
        table.setSelectionForeground(Theme.TEXT_WHITE);

        JTableHeader header = table.getTableHeader();
        header.setFont(Theme.FONT_BOLD);
        header.setBackground(new Color(0, 0, 0, 60));
        header.setForeground(Theme.TEXT_DARK.brighter());
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setPreferredSize(new Dimension(0, 40));

        // Custom Renderer for glass look
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(new Color(255, 255, 255, 40));
                } else {
                    c.setBackground(row % 2 == 0 ? new Color(0, 0, 0, 30) : new Color(0, 0, 0, 10));
                }
                c.setForeground(Color.yellow);
                ((JComponent) c).setBorder(new EmptyBorder(0, 10, 0, 10)); // Padding
                return c;
            }
        });
    }

    private void loadComments(DefaultTableModel model) {
        String sql = "SELECT c.id, c.bug_id, b.title, c.text, u.username, c.created_at " +
                "FROM BugComment c " +
                "JOIN Bug b ON c.bug_id = b.id " +
                "JOIN [User] u ON c.author_id = u.id " +
                "ORDER BY c.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[] {
                        rs.getInt("id"),
                        rs.getInt("bug_id"),
                        rs.getString("title"),
                        rs.getString("text"),
                        rs.getString("username"),
                        rs.getTimestamp("created_at")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading comments: " + e.getMessage());
        }
    }
}
