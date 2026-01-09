package com.bugtracker.ui;

import com.bugtracker.model.User;
import com.bugtracker.ui.Form.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardFrame extends JFrame implements SidebarPanel.DashboardCallback {
    private final User user;
    private BugListPanel bugListPanel;

    public DashboardFrame(User user) {
        this.user = user;
        setTitle("Bug Tracker - Dashboard");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setIconImage(AppIcon.get());

        // Set custom content pane for background
        JPanel contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Dynamic Theme Gradient
                GradientPaint gradient = new GradientPaint(
                        0, 0, Theme.GRADIENT_START,
                        0, getHeight(), Theme.GRADIENT_END);
                g2.setPaint(gradient);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        // Create SIDEBAR panel
        SidebarPanel sidebarPanel = new SidebarPanel(user, this);
        add(sidebarPanel, BorderLayout.WEST);

        // Create main content area with header
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setOpaque(false);

        // Add subtle header with user badge
        JPanel headerPanel = createContentHeader();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Create bug list panel
        bugListPanel = new BugListPanel(user);
        bugListPanel.setOpaque(false);
        mainPanel.add(bugListPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createContentHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(15, 20, 10, 20));

        // User badge on the right
        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        badgePanel.setOpaque(false);

        JLabel badgeLabel = new JLabel(formatRole(user.getRole()));
        badgeLabel.setFont(Theme.FONT_BOLD.deriveFont(13f));
        badgeLabel.setForeground(Theme.isDarkMode ? new Color(30, 50, 80) : new Color(40, 60, 100));
        badgeLabel.setBackground(Color.WHITE);
        badgeLabel.setOpaque(true);
        badgeLabel.setBorder(new EmptyBorder(8, 16, 8, 16));

        // Rounded border for badge
        badgeLabel.setUI(new javax.swing.plaf.basic.BasicLabelUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(c.getBackground());
                g2.fillRoundRect(0, 0, c.getWidth(), c.getHeight(), 8, 8);

                super.paint(g2, c);
                g2.dispose();
            }
        });

        badgePanel.add(badgeLabel);
        header.add(badgePanel, BorderLayout.EAST);

        return header;
    }

    private String formatRole(String role) {
        switch (role) {
            case "ADMIN":
                return "Administrator";
            case "MANAGER":
                return "Manager";
            case "TECH_PERSON":
                return "Technical_Person";
            case "CUSTOMER":
                return "Customer";
            default:
                return role;
        }
    }

    // ========== SIDEBAR CALLBACK IMPLEMENTATIONS ==========

    @Override
    public void onReportBug() {
        if (!"CUSTOMER".equalsIgnoreCase(user.getRole())) {
            JOptionPane.showMessageDialog(this,
                    "Only Customers can report bugs.",
                    "Access Denied",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        new BugReportDialog(this, user).setVisible(true);
        refreshBugList();
    }

    @Override
    public void onViewStatus() {
        refreshBugList();
    }

    @Override
    public void onAssignBug() {
        new AssignBugFrame(user).setVisible(true);
        refreshBugList();
    }

    @Override
    public void onUpdateStatus() {
        new UpdateBugStatusFrame(user).setVisible(true);
        refreshBugList();
    }

    @Override
    public void onAddComment() {
        new AddCommentFrame(user).setVisible(true);
    }

    @Override
    public void onViewComments() {
        new ViewCommentsFrame().setVisible(true);
    }

    @Override
    public void onAddProduct() {
        new AddProductFrame().setVisible(true);
    }

    @Override
    public void onRemoveProduct() {
        new RemoveProductFrame().setVisible(true);
    }

    @Override
    public void onAddUser() {
        showAddUserDialog();
    }

    @Override
    public void onRefresh() {
        refreshBugList();
    }

    @Override
    public void onLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }

    @Override
    public void onToggleTheme() {
        Theme.toggle();
        // Re-open dashboard to apply new theme
        new DashboardFrame(user).setVisible(true);
        dispose();
    }

    // ========== HELPER METHODS ==========

    private void showAddUserDialog() {
        String[] options = { "Manager", "Developer", "Tester","Network_Team","Db_Team", "Cancel" };
        int choice = JOptionPane.showOptionDialog(this,
                "Select user role to add:",
                "Add User",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        String role = null;
        switch (choice) {
            case 0:
                role = "MANAGER";
                break;
            case 1:
                role = "TECH_PERSON";
                break;
            case 2:
                role = "TESTER";
                break;
            case 3:
                role = "NETWORK_TEAM";
                break;
            case 4:
                role = "DB_TEAM";
        }

        if (role != null) {
            new AddUserFrame(role).setVisible(true);
        }
    }

    private void refreshBugList() {
        bugListPanel.loadTable();
    }
}
