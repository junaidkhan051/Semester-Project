package com.bugtracker.ui;

import com.bugtracker.model.Bug;
import com.bugtracker.model.User;
import com.bugtracker.model.enums.Severity;
import com.bugtracker.model.enums.Status;
import com.bugtracker.service.BugService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class BugListPanel extends JPanel {
    private final JTable table = new JTable();
    private final BugService bugService = new BugService();
    private final User user;

    // Sort Controls
    private final JComboBox<BugService.SortOption> sortCriteriaCombo = new JComboBox<>(BugService.SortOption.values());
    private final JComboBox<String> sortOrderCombo = new JComboBox<>(new String[] { "Ascending", "Descending" });

    // Search Controls
    private final JTextField searchField = new JTextField(15);
    private final JComboBox<String> statusFilterCombo = new JComboBox<>();
    private final JComboBox<String> severityFilterCombo = new JComboBox<>();
    private final JButton searchButton = new JButton("Search");

    public BugListPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout());
        setOpaque(false);

        // --- SORT PANEL ---
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sortPanel.setOpaque(false);
        sortPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));

        JLabel sortLabel = new JLabel("Sort By: ");
        sortLabel.setFont(Theme.FONT_BOLD);
        sortLabel.setForeground(Theme.TEXT_WHITE);

        styleComboBox(sortCriteriaCombo);
        styleComboBox(sortOrderCombo);

        sortCriteriaCombo.addActionListener(e -> loadTable());
        sortOrderCombo.addActionListener(e -> loadTable());

        sortPanel.add(sortLabel);
        sortPanel.add(sortCriteriaCombo);
        sortPanel.add(sortOrderCombo);

        // --- SEARCH PANEL ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 10));

        JLabel searchLabel = new JLabel("ID/Title: ");
        searchLabel.setFont(Theme.FONT_BOLD);
        searchLabel.setForeground(Theme.TEXT_WHITE);

        styleTextField(searchField);

        // Status Filter
        statusFilterCombo.addItem("All Status");
        for (Status s : Status.values()) {
            statusFilterCombo.addItem(s.name());
        }
        styleComboBox(statusFilterCombo);

        // Severity Filter
        severityFilterCombo.addItem("All Severities");
        for (Severity s : Severity.values()) {
            severityFilterCombo.addItem(s.name());
        }
        styleComboBox(severityFilterCombo);

        styleButton(searchButton);

        // Add listeners
        searchButton.addActionListener(e -> loadTable());
        searchField.addActionListener(e -> loadTable());
        statusFilterCombo.addActionListener(e -> loadTable());
        severityFilterCombo.addActionListener(e -> loadTable());

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("  Status: ")); // Spacer
        searchPanel.getComponent(searchPanel.getComponentCount() - 1).setForeground(Theme.TEXT_WHITE);
        searchPanel.add(statusFilterCombo);
        searchPanel.add(new JLabel("  Severity: ")); // Spacer
        searchPanel.getComponent(searchPanel.getComponentCount() - 1).setForeground(Theme.TEXT_WHITE);
        searchPanel.add(severityFilterCombo);
        searchPanel.add(searchButton);

        // Combine Sort and Search panels
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(sortPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Style the table
        table.setFont(Theme.FONT_REGULAR);
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setOpaque(true); // Make table opaque to show row colors
        table.setBackground(new Color(25, 40, 65)); // Match row background
        table.setForeground(new Color(220, 225, 235));

        // Dynamic selection color
        Color selectionColor = new Color(Theme.PRIMARY.getRed(), Theme.PRIMARY.getGreen(), Theme.PRIMARY.getBlue(), 50);
        table.setSelectionBackground(selectionColor);
        table.setSelectionForeground(Theme.TEXT_WHITE);

        // Style the header with light gray background
        JTableHeader header = table.getTableHeader();
        header.setFont(Theme.FONT_BOLD.deriveFont(13f));
        header.setBackground(new Color(180, 190, 200)); // Light gray header
        header.setForeground(new Color(50, 60, 70)); // Dark text for contrast
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 42));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(150, 160, 170)));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);
        ((DefaultTableCellRenderer) header.getDefaultRenderer())
                .setBorder(new javax.swing.border.EmptyBorder(0, 10, 0, 10));

        // Apply custom renderers
        table.setDefaultRenderer(Object.class, new CustomCellRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getViewport().setBackground(Theme.TRANSPARENT);

        // Apply neon scrollbar
        NeonScrollBarUI.applyToScrollPane(scrollPane);

        add(scrollPane, BorderLayout.CENTER);

        // Set default sort
        sortCriteriaCombo.setSelectedItem(BugService.SortOption.ID);
        sortOrderCombo.setSelectedItem("Ascending");

        loadTable();

        // Add Mouse Listener for Double Click
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        int bugId = (int) table.getValueAt(row, 0);
                        Bug bug = bugService.findById(bugId);
                        if (bug != null) {
                            new BugDetailDialog(SwingUtilities.getWindowAncestor(BugListPanel.this), bug)
                                    .setVisible(true);
                        }
                    }
                }
            }
        });
    }

    private void styleTextField(JTextField field) {
        field.setFont(Theme.FONT_REGULAR);
        field.setBackground(new Color(25, 40, 65));
        field.setForeground(Theme.TEXT_WHITE);
        field.setCaretColor(Theme.TEXT_WHITE);
        field.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 100)));
    }

    private void styleButton(JButton btn) {
        btn.setFont(Theme.FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(Theme.PRIMARY);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void styleComboBox(JComboBox<?> box) {
        box.setFont(Theme.FONT_REGULAR);
        box.setBackground(new Color(25, 40, 65));
        box.setForeground(Theme.TEXT_WHITE);
        // Basic styling, more complex styling would require custom UI
    }

    public void loadTable() {
        List<Bug> bugs;
        if ("CUSTOMER".equalsIgnoreCase(user.getRole())) {
            // Customers should only see bugs they reported
            bugs = bugService.getBugsForReporter(user);
        } else {
            // Staff roles can see all bugs
            bugs = bugService.getAllBugs();
        }

        // Apply Filters
        String query = searchField.getText().trim().toLowerCase();
        String statusFilter = (String) statusFilterCombo.getSelectedItem();
        String severityFilter = (String) severityFilterCombo.getSelectedItem();

        java.util.List<Bug> filteredBugs = new java.util.ArrayList<>();

        for (Bug b : bugs) {
            boolean matchesIdOrTitle = true;
            if (!query.isEmpty()) {
                matchesIdOrTitle = String.valueOf(b.getId()).contains(query) ||
                        (b.getTitle() != null && b.getTitle().toLowerCase().contains(query));
            }

            boolean matchesStatus = true;
            if (statusFilter != null && !"All Status".equals(statusFilter)) {
                matchesStatus = b.getStatus().name().equals(statusFilter);
            }

            boolean matchesSeverity = true;
            if (severityFilter != null && !"All Severities".equals(severityFilter)) {
                matchesSeverity = b.getSeverity().name().equals(severityFilter);
            }

            if (matchesIdOrTitle && matchesStatus && matchesSeverity) {
                filteredBugs.add(b);
            }
        }
        bugs = filteredBugs;

        // Apply Sort
        BugService.SortOption option = (BugService.SortOption) sortCriteriaCombo.getSelectedItem();
        boolean ascending = "Ascending".equals(sortOrderCombo.getSelectedItem());
        if (option != null) {
            bugService.sortBugs(bugs, option, ascending);
        }

        DefaultTableModel m = new DefaultTableModel(
                new Object[] { "ID", "Title", "Status", "Severity", "Reporter", "Assignee", "Product" },
                0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        for (Bug b : bugs) {
            m.addRow(new Object[] {
                    b.getId(),
                    b.getTitle(),
                    b.getStatus(),
                    b.getSeverity(),
                    b.getReporter() != null ? b.getReporter().getFullName() : "-",
                    b.getAssignee() != null ? b.getAssignee().getFullName() : "Unassigned",
                    b.getProduct() != null ? b.getProduct().getName() : "-"
            });
        }

        table.setModel(m);

        // Adjust column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(250);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(150);
        table.getColumnModel().getColumn(5).setPreferredWidth(150);
        table.getColumnModel().getColumn(6).setPreferredWidth(150);
    }

    // Custom cell renderer for color-coded status and severity
    class CustomCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Improved row background with better contrast
            if (!isSelected) {
                // Dark navy background for all rows
                Color baseColor = new Color(25, 40, 65); // Dark navy from reference
                if (row % 2 == 0) {
                    c.setBackground(baseColor);
                } else {
                    // Slightly lighter for alternating rows
                    c.setBackground(new Color(30, 45, 70));
                }
            } else {
                // Selection color
                c.setBackground(new Color(41, 98, 255, 100));
            }
            c.setForeground(new Color(220, 225, 235)); // Lighter text color
            ((JComponent) c).setBorder(new javax.swing.border.EmptyBorder(8, 10, 8, 10));

            // Color code Status column
            if (column == 2 && value instanceof Status) {
                Status status = (Status) value;
                // Use a label to paint a rounded badge
                JLabel label = new JLabel(status.name()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        Color badgeColor = Color.GRAY;
                        switch (status) {
                            case NEW:
                                badgeColor = new Color(64, 196, 255);
                                break;
                            case OPEN:
                                badgeColor = new Color(255, 167, 38);
                                break;
                            case IN_PROGRESS:
                                badgeColor = new Color(255, 238, 88);
                                break;
                            case RESOLVED:
                                badgeColor = new Color(102, 187, 106);
                                break;
                            case CLOSED:
                                badgeColor = new Color(189, 189, 189);
                                break;
                            case ASSIGNED: // Handle the ASSIGNED case
                                badgeColor = new Color(255, 238, 88); // Same as IN_PROGRESS for now
                                break;
                        }

                        // In light mode, maybe darken badges slightly? Stick to standard colors for
                        // now.
                        g2.setColor(badgeColor);

                        g2.fillRoundRect(0, 2, getWidth(), getHeight() - 4, 10, 10);
                        super.paintComponent(g2);
                        g2.dispose();
                    }
                };
                label.setOpaque(false);
                label.setHorizontalAlignment(CENTER);
                label.setForeground(Color.BLACK); // Text on badge should be readable (Black on color is safe)

                // Keep selection highlight behind the badge
                if (isSelected) {
                    label.setBackground(table.getSelectionBackground());
                    label.setOpaque(true);
                }
                return label;
            }
            // Color code Severity column
            else if (column == 3 && value instanceof Severity) {
                Severity severity = (Severity) value;
                JLabel label = new JLabel(severity.name()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        Color badgeColor = Color.GRAY;
                        switch (severity) {
                            case LOW:
                                badgeColor = new Color(102, 187, 106);
                                break;
                            case MEDIUM:
                                badgeColor = new Color(255, 238, 88);
                                break;
                            case HIGH:
                                badgeColor = new Color(255, 167, 38);
                                break;
                            case CRITICAL:
                                badgeColor = new Color(239, 83, 80);
                                break;
                        }

                        g2.setColor(badgeColor);
                        g2.fillRoundRect(0, 2, getWidth(), getHeight() - 4, 10, 10);
                        super.paintComponent(g2);
                        g2.dispose();
                    }
                };
                label.setOpaque(false);
                label.setHorizontalAlignment(CENTER);
                label.setForeground(Color.BLACK);
                if (isSelected) {
                    label.setBackground(table.getSelectionBackground());
                    label.setOpaque(true);
                }
                return label;
            }

            return c;
        }
    }
}
