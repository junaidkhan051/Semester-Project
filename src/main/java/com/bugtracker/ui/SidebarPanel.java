package com.bugtracker.ui;

import com.bugtracker.model.User;
import com.bugtracker.service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class SidebarPanel extends JPanel {
    private final User user;
    private final AuthService authService = new AuthService();
    private final List<SidebarMenuItem> menuItems = new ArrayList<>();
    private final DashboardCallback callback;
    private SidebarMenuItem activeMenuItem = null;

    public interface DashboardCallback {
        void onReportBug();

        void onViewStatus();

        void onAssignBug();

        void onUpdateStatus();

        void onAddComment();

        void onViewComments();

        void onAddProduct();

        void onRemoveProduct();

        void onAddUser();

        void onRefresh();

        void onLogout();

        void onToggleTheme();
    }

    public SidebarPanel(User user, DashboardCallback callback) {
        this.user = user;
        this.callback = callback;

        setOpaque(false);
        setPreferredSize(new Dimension(280, 0));
        setLayout(new BorderLayout());

        initializeUI();
    }

    private void initializeUI() {
        // Main container with padding
        JPanel mainContainer = new JPanel();
        mainContainer.setOpaque(false);
        mainContainer.setLayout(new BorderLayout());
        mainContainer.setBorder(new EmptyBorder(20, 15, 20, 15));

        // Top Section - App Title & User Info
        JPanel topSection = createTopSection();
        mainContainer.add(topSection, BorderLayout.NORTH);

        // Middle Section - Navigation Menu
        JPanel menuSection = createMenuSection();
        mainContainer.add(menuSection, BorderLayout.CENTER);

        // Bottom Section - Theme Toggle & Logout
        JPanel bottomSection = createBottomSection();
        mainContainer.add(bottomSection, BorderLayout.SOUTH);

        add(mainContainer, BorderLayout.CENTER);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Solid sidebar background (dark navy)
        Color sidebarBg = Theme.isDarkMode ? new Color(20, 35, 60) : new Color(240, 244, 250);
        g2.setColor(sidebarBg);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Subtle right border
        g2.setColor(new Color(255, 255, 255, 30));
        g2.setStroke(new BasicStroke(1));
        g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());

        g2.dispose();
    }

    private JPanel createTopSection() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // App Title with icon
        Image iconImage = AppIcon.get();
        Image scaledIcon = iconImage.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        JLabel lblAppTitle = new JLabel("Bug Tracker", new ImageIcon(scaledIcon), JLabel.LEFT);
        lblAppTitle.setIconTextGap(10);
        lblAppTitle.setFont(Theme.FONT_TITLE.deriveFont(24f));
        lblAppTitle.setForeground(Theme.TEXT_WHITE);
        lblAppTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblAppTitle);

        panel.add(Box.createVerticalStrut(10));

        // Separator
        JSeparator separator = new JSeparator();
        separator.setForeground(Theme.GLASS_BORDER);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        panel.add(separator);

        panel.add(Box.createVerticalStrut(20));

        // User Info
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setOpaque(false);
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblUserName = new JLabel(user.getFullName());
        lblUserName.setFont(Theme.FONT_BOLD.deriveFont(16f));
        lblUserName.setForeground(Theme.TEXT_WHITE);
        lblUserName.setAlignmentX(Component.LEFT_ALIGNMENT);
        userInfoPanel.add(lblUserName);

        userInfoPanel.add(Box.createVerticalStrut(5));

        JLabel lblRole = new JLabel(formatRole(user.getRole()));
        lblRole.setFont(Theme.FONT_REGULAR.deriveFont(13f));
        lblRole.setForeground(Theme.ACCENT);
        lblRole.setAlignmentX(Component.LEFT_ALIGNMENT);
        userInfoPanel.add(lblRole);

        panel.add(userInfoPanel);
        panel.add(Box.createVerticalStrut(25));

        return panel;
    }

    private JPanel createMenuSection() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Customer actions
        if (authService.canReportBug(user)) {
            addMenuItem(panel, "📝", "Post Bug", e -> callback.onReportBug(), true);
        }
        if (authService.canViewStatus(user)) {
            addMenuItem(panel, "📊", "View Status", e -> callback.onViewStatus(), true);
        }

        // Add menu items based on user role
        if (authService.canAssignBug(user)) {
            addMenuItem(panel, "📋", "Assign Bug", e -> callback.onAssignBug(), true);
        }

        if (authService.canUpdateStatus(user)) {
            addMenuItem(panel, "✏️", "Update Status", e -> callback.onUpdateStatus(), true);
        }

        if (authService.canAddComment(user)) {
            addMenuItem(panel, "💬", "Add Comment", e -> callback.onAddComment(), true);
        }

        if (authService.canManageProducts(user)) {
            addMenuItem(panel, "🧩", "Add Product", e -> callback.onAddProduct(), true);
            addMenuItem(panel, "🗑️", "Remove Product", e -> callback.onRemoveProduct(), true);
        }

        if (authService.canViewComments(user)) {
            addMenuItem(panel, "👁️", "View Comments", e -> callback.onViewComments(), true);
        }

        if (authService.canAddProduct(user)) {
            addMenuItem(panel, "👥", "Add User", e -> callback.onAddUser(), true);
        }

        // Separator
        panel.add(Box.createVerticalStrut(10));
        JSeparator separator = new JSeparator();
        separator.setForeground(Theme.GLASS_BORDER);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        panel.add(separator);
        panel.add(Box.createVerticalStrut(10));

        // Refresh
        addMenuItem(panel, "🔄", "Refresh", e -> callback.onRefresh(), true);

        // Spacer to push content up
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createBottomSection() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        // Theme Toggle
        String themeIcon = Theme.isDarkMode ? "🌙" : "☀️";
        String themeLabel = Theme.isDarkMode ? "Dark Mode" : "Light Mode";
        addMenuItem(panel, themeIcon, themeLabel, e -> callback.onToggleTheme(), true);

        panel.add(Box.createVerticalStrut(10));

        // Logout
        addMenuItem(panel, "🚪", "Logout", e -> callback.onLogout(), true);

        return panel;
    }

    private void addMenuItem(JPanel parent, String icon, String label, ActionListener action, boolean enabled) {
        SidebarMenuItem item = new SidebarMenuItem(icon, label, enabled, this);
        item.addActionListener(e -> {
            setActiveMenuItem(item);
            action.actionPerformed(e);
        });
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuItems.add(item);
        parent.add(item);
        parent.add(Box.createVerticalStrut(3));
    }

    private void setActiveMenuItem(SidebarMenuItem item) {
        if (activeMenuItem != null) {
            activeMenuItem.setActive(false);
        }
        activeMenuItem = item;
        if (activeMenuItem != null) {
            activeMenuItem.setActive(true);
        }
    }

    private String formatRole(String role) {
        switch (role) {
            case "ADMIN":
                return "Administrator";
            case "MANAGER":
                return "Manager";
            case "TECH_PERSON":
                return "Developer";
            case "CUSTOMER":
                return "Customer";
            default:
                return role;
        }
    }

    // Inner class for sidebar menu items
    private static class SidebarMenuItem extends JButton {
        private boolean isHovered = false;
        private boolean isActive = false;
        private final String icon;
        private final String label;

        public SidebarMenuItem(String icon, String label, boolean enabled, SidebarPanel parent) {
            this.icon = icon;
            this.label = label;

            setEnabled(enabled);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setHorizontalAlignment(SwingConstants.LEFT);
            setBorder(new EmptyBorder(12, 20, 12, 20));

            // Mouse hover effects
            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    if (isEnabled()) {
                        isHovered = true;
                        repaint();
                    }
                }

                public void mouseExited(java.awt.event.MouseEvent evt) {
                    isHovered = false;
                    repaint();
                }
            });
        }

        public void setActive(boolean active) {
            this.isActive = active;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            // Active state - solid blue background
            if (isActive && isEnabled()) {
                Color activeColor = Theme.isDarkMode ? new Color(41, 98, 255, 180) : new Color(41, 98, 255, 120);
                g2.setColor(activeColor);
                g2.fillRoundRect(5, 0, width - 10, height, 8, 8);
            }
            // Hover state
            else if (isHovered && isEnabled()) {
                Color hoverColor = Theme.isDarkMode ? new Color(56, 189, 248, 40) : new Color(67, 97, 238, 30);
                g2.setColor(hoverColor);
                g2.fillRoundRect(5, 0, width - 10, height, 8, 8);
            }

            // Draw icon and label
            Color textColor = isEnabled() ? Theme.TEXT_WHITE : Theme.TEXT_GRAY;

            // Icon
            g2.setColor(textColor);
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
            g2.drawString(icon, 25, height / 2 + 6);

            // Label
            g2.setFont(Theme.FONT_REGULAR.deriveFont(14f));
            g2.setColor(textColor);
            g2.drawString(label, 55, height / 2 + 5);

            g2.dispose();
        }
    }
}
