package com.bugtracker.ui.Form;

import com.bugtracker.model.Bug;
import com.bugtracker.model.User;
import com.bugtracker.service.BugService;
import com.bugtracker.ui.GlassPanel;
import com.bugtracker.ui.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class AddCommentFrame extends JFrame {
    private final User author;
    private final BugService bugService = new BugService();
    private JComboBox<Bug> bugCombo;
    private JTextArea commentArea;

    public AddCommentFrame(User author) {
        this.author = author;

        setTitle("Add Comment");
        setSize(500, 450);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(new Color(140, 20, 50, 100)); // Transparent for custom shape if needed

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Theme.GLASS_PANEL_BG); // Use theme color

        // Use GlassPanel for the look
        GlassPanel glassPanel = new GlassPanel();
        glassPanel.setLayout(new BorderLayout(0, 20));
        glassPanel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Add Comment");
        title.setFont(Theme.FONT_TITLE.deriveFont(22f));
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

        glassPanel.add(header, BorderLayout.NORTH);

        // Form
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);

        // Select Bug
        JLabel lblBug = new JLabel("Select Bug");
        lblBug.setFont(Theme.FONT_BOLD);
        lblBug.setForeground(Theme.FORM_LABEL_COLOR);
        lblBug.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lblBug);
        form.add(Box.createVerticalStrut(5));

        bugCombo = new JComboBox<>();
        styleComboBox(bugCombo);
        loadAssignedBugs();
        bugCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(bugCombo);

        form.add(Box.createVerticalStrut(20));

        // Comment Text
        JLabel lblComment = new JLabel("Comment");
        lblComment.setFont(Theme.FONT_BOLD);
        lblComment.setForeground(Theme.FORM_LABEL_COLOR);
        lblComment.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lblComment);
        form.add(Box.createVerticalStrut(5));

        commentArea = new JTextArea(5, 20);
        styleTextArea(commentArea);
        JScrollPane scrollPane = new JScrollPane(commentArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 30)));
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(scrollPane);

        glassPanel.add(form, BorderLayout.CENTER);

        // Footer Button
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);

        JButton btnSubmit = new JButton("Post Comment");
        styleButton(btnSubmit, Theme.SUCCESS_COLOR);
        btnSubmit.addActionListener(e -> handleSubmit());
        footer.add(btnSubmit);

        glassPanel.add(footer, BorderLayout.SOUTH);

        setContentPane(glassPanel);
    }

    private void styleComboBox(JComboBox<Bug> combo) {
        combo.setFont(Theme.FONT_REGULAR);
        combo.setBackground(new Color(255, 255, 255, 20));
        combo.setForeground(Theme.TEXT_DARK); // Combo defaults are tricky with dark themes, kept readable
        combo.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 50)));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
    }

    private void styleTextArea(JTextArea area) {
        area.setFont(Theme.FONT_REGULAR);
        area.setBackground(new Color(0, 0, 0, 50));
        area.setForeground(Theme.TEXT_WHITE);
        area.setCaretColor(Theme.TEXT_WHITE);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void styleButton(JButton btn, Color bgColor) {
        btn.setFont(Theme.FONT_BOLD);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(140, 40));
    }

    private void loadAssignedBugs() {
        // Tech person can comment on bugs assigned to them (or maybe all? Logic says
        // "Tech person can be add comments")
        // Usually devs comment on bugs they are working on, or any bug.
        // For now let's load ALL bugs, as a dev might comment on others' bugs.
        List<Bug> bugs = bugService.getAllBugs();
        for (Bug b : bugs) {
            bugCombo.addItem(b);
        }
    }

    private void handleSubmit() {
        Bug selected = (Bug) bugCombo.getSelectedItem();
        String text = commentArea.getText().trim();

        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a bug", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Comment cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            bugService.addComment(selected.getId(), text, author.getId());
            JOptionPane.showMessageDialog(this, "Comment added!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error adding comment: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
