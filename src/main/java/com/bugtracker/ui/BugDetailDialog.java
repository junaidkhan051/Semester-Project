package com.bugtracker.ui;

import com.bugtracker.model.Bug;
import com.bugtracker.model.BugComment;
import com.bugtracker.service.BugService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BugDetailDialog extends JDialog {

    private final Bug bug;
    private final BugService bugService = new BugService();

    public BugDetailDialog(Window owner, Bug bug) {
        super(owner, "Bug Details - #" + bug.getId(), ModalityType.APPLICATION_MODAL);
        this.bug = bug;

        setSize(800, 700);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        // Use GlassPanel for background
        GlassPanel mainPanel = new GlassPanel();
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        setContentPane(mainPanel);

        // Header Section
        mainPanel.add(createHeader(), BorderLayout.NORTH);

        // Content Section (Scrollable)
        mainPanel.add(createContentPanel(), BorderLayout.CENTER);

        // Footer / Close Button
        mainPanel.add(createFooter(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(10, 10));
        header.setOpaque(false);

        // Title and ID
        JLabel titleLabel = new JLabel("#" + bug.getId() + ": " + bug.getTitle());
        titleLabel.setFont(Theme.FONT_TITLE.deriveFont(24f));
        titleLabel.setForeground(Theme.TEXT_WHITE);
        header.add(titleLabel, BorderLayout.CENTER);

        // Status Badge (Simplified version of what's in CustomCellRenderer)
        JLabel statusLabel = new JLabel(bug.getStatus().name());
        statusLabel.setFont(Theme.FONT_BOLD.deriveFont(14f));
        statusLabel.setForeground(Color.BLACK);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(getStatusColor(bug.getStatus()));
        statusLabel.setBorder(new EmptyBorder(5, 15, 5, 15));
        // Simple rounded border workaround or just standard label

        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        badgePanel.setOpaque(false);
        badgePanel.add(statusLabel);

        // Severity Badge
        JLabel severityLabel = new JLabel(bug.getSeverity().name());
        severityLabel.setFont(Theme.FONT_BOLD.deriveFont(14f));
        severityLabel.setForeground(Color.BLACK);
        severityLabel.setOpaque(true);
        severityLabel.setBackground(getSeverityColor(bug.getSeverity()));
        severityLabel.setBorder(new EmptyBorder(5, 15, 5, 15));
        badgePanel.add(severityLabel);

        header.add(badgePanel, BorderLayout.EAST);

        // Sub-header info
        JPanel subInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        subInfo.setOpaque(false);
        JLabel reporterLabel = new JLabel(
                "Reporter: " + (bug.getReporter() != null ? bug.getReporter().getFullName() : "Unknown"));
        formatMetaLabel(reporterLabel);

        JLabel assigneeLabel = new JLabel(
                "Assignee: " + (bug.getAssignee() != null ? bug.getAssignee().getFullName() : "Unassigned"));
        formatMetaLabel(assigneeLabel);

        JLabel productLabel = new JLabel("Product: " + (bug.getProduct() != null ? bug.getProduct().getName() : "-"));
        formatMetaLabel(productLabel);

        subInfo.add(reporterLabel);
        subInfo.add(assigneeLabel);
        subInfo.add(productLabel);

        header.add(subInfo, BorderLayout.SOUTH);

        return header;
    }

    private void formatMetaLabel(JLabel label) {
        label.setFont(Theme.FONT_REGULAR.deriveFont(14f));
        label.setForeground(new Color(200, 200, 200));
    }

    private JScrollPane createContentPanel() {
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Description Section
        JLabel descHeader = new JLabel("Description");
        descHeader.setFont(Theme.FONT_BOLD.deriveFont(18f));
        descHeader.setForeground(Theme.PRIMARY);
        descHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(descHeader);
        content.add(Box.createVerticalStrut(10));

        JTextArea descArea = new JTextArea(bug.getDescription());
        styleReadOnlyTextArea(descArea);
        content.add(descArea);

        content.add(Box.createVerticalStrut(20));

        // Comments Section
        JLabel commentsHeader = new JLabel("Comments & Activity");
        commentsHeader.setFont(Theme.FONT_BOLD.deriveFont(18f));
        commentsHeader.setForeground(Theme.PRIMARY);
        commentsHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(commentsHeader);
        content.add(Box.createVerticalStrut(10));

        List<BugComment> comments = bugService.getComments(bug.getId());
        if (comments.isEmpty()) {
            JLabel noComments = new JLabel("No comments yet.");
            noComments.setFont(Theme.FONT_REGULAR);
            noComments.setForeground(Theme.TEXT_GRAY);
            content.add(noComments);
        } else {
            for (BugComment c : comments) {
                content.add(createCommentPanel(c));
                content.add(Box.createVerticalStrut(10));
            }
        }

        JScrollPane scroll = new JScrollPane(content);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        NeonScrollBarUI.applyToScrollPane(scroll);

        return scroll;
    }

    private JPanel createCommentPanel(BugComment c) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(true);
        panel.setBackground(new Color(255, 255, 255, 15)); // Slight highlight
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100)); // Flex height would be better but simple for now

        // Header: Author - Date
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel author = new JLabel(c.getAuthor() != null ? c.getAuthor().getFullName() : "Unknown");
        author.setFont(Theme.FONT_BOLD);
        author.setForeground(Theme.TEXT_WHITE);

        String dateStr = c.getCreatedAt() != null
                ? c.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
                : "-";
        JLabel date = new JLabel(dateStr);
        date.setFont(Theme.FONT_REGULAR.deriveFont(12f));
        date.setForeground(Theme.TEXT_GRAY);

        header.add(author, BorderLayout.WEST);
        header.add(date, BorderLayout.EAST);

        panel.add(header, BorderLayout.NORTH);

        JTextArea text = new JTextArea(c.getText());
        text.setFont(Theme.FONT_REGULAR);
        text.setForeground(Theme.TEXT_WHITE);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setOpaque(false);
        text.setEditable(false);

        panel.add(text, BorderLayout.CENTER);

        // Restricting height is tricky with BoxLayout, so wrapping in a container that
        // allows expansion
        // For simple usage in BoxLayout, we set alignment
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        return panel;
    }

    private void styleReadOnlyTextArea(JTextArea area) {
        area.setFont(Theme.FONT_REGULAR.deriveFont(15f));
        area.setForeground(Theme.TEXT_WHITE);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setOpaque(false);
        area.setEditable(false);
        area.setBorder(null);
        area.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);

        JButton closeBtn = new JButton("Close");
        styleButton(closeBtn);
        closeBtn.addActionListener(e -> dispose());

        footer.add(closeBtn);
        return footer;
    }

    private void styleButton(JButton btn) {
        btn.setFont(Theme.FONT_BOLD);
        btn.setForeground(Color.WHITE);
        btn.setBackground(Theme.PRIMARY);
        btn.setBorder(new EmptyBorder(8, 20, 8, 20));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private Color getStatusColor(com.bugtracker.model.enums.Status status) {
        if (status == null)
            return Color.GRAY;
        switch (status) {
            case NEW:
                return new Color(64, 196, 255);
            case OPEN:
                return new Color(255, 167, 38);
            case IN_PROGRESS:
                return new Color(255, 238, 88);
            case RESOLVED:
                return new Color(102, 187, 106);
            case CLOSED:
                return new Color(189, 189, 189);
            default:
                return Color.GRAY;
        }
    }

    private Color getSeverityColor(com.bugtracker.model.enums.Severity severity) {
        if (severity == null)
            return Color.GRAY;
        switch (severity) {
            case LOW:
                return new Color(102, 187, 106);
            case MEDIUM:
                return new Color(255, 238, 88);
            case HIGH:
                return new Color(255, 167, 38);
            case CRITICAL:
                return new Color(239, 83, 80);
            default:
                return Color.GRAY;
        }
    }
}
