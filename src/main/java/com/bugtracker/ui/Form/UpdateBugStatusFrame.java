package com.bugtracker.ui.Form;

import com.bugtracker.model.Bug;
import com.bugtracker.model.User;
import com.bugtracker.model.enums.Status;
import com.bugtracker.service.BugService;

import com.bugtracker.ui.GlassPanel;
import com.bugtracker.ui.Theme;
//import com.bugtracker.ui.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class UpdateBugStatusFrame extends JFrame {
    private User currentUser;
    private BugService bugService = new BugService();
    private JComboBox<Bug> bugCombo;
    private JComboBox<String> statusCombo;

    public UpdateBugStatusFrame(User user) {
        this.currentUser = user;

        setTitle("Update Bug Status");
        setSize(450, 420);
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

        // Form
        glassPanel.add(createForm(), BorderLayout.CENTER);

        // Buttons
        glassPanel.add(createButtons(), BorderLayout.SOUTH);

    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel title = new JLabel("Update Status");
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

    private JPanel createForm() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);

        // Select Bug
        form.add(createLabel("Select Bug"));
        form.add(Box.createVerticalStrut(5));
        bugCombo = new JComboBox<>();
        styleComboBox(bugCombo);
        loadAssignedBugs();
        form.add(bugCombo);

        form.add(Box.createVerticalStrut(20));

        // New Status
        form.add(createLabel("New Status"));
        form.add(Box.createVerticalStrut(5));
        statusCombo = new JComboBox<>(new String[] { "NEW", "OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED" });
        styleComboBox(statusCombo);
        form.add(statusCombo);

        return form;
    }

    private JPanel createButtons() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.setOpaque(false);

        JButton updateButton = new JButton("Update Status");
        styleButton(updateButton, new Color(251, 140, 0));
        updateButton.addActionListener(e -> handleUpdate());
        buttons.add(updateButton);

        return buttons;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.FONT_BOLD);
        label.setForeground(Theme.FORM_LABEL_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private void styleComboBox(JComboBox<?> combo) {
        combo.setFont(Theme.FONT_REGULAR);
        combo.setBackground(new Color(255, 255, 255, 20));
        combo.setForeground(Theme.TEXT_DARK);
        combo.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 50)));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        combo.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void styleButton(JButton btn, Color bgColor) {
        btn.setFont(Theme.FONT_BOLD);
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(150, 45));
    }

    private void loadAssignedBugs() {
        // Allow updating status for any bug
        List<Bug> bugs = bugService.getAllBugs();
        for (Bug bug : bugs) {
            bugCombo.addItem(bug);
        }
    }

    private void handleUpdate() {
        Bug selectedBug = (Bug) bugCombo.getSelectedItem();
        String selectedStatus = (String) statusCombo.getSelectedItem();

        if (selectedBug == null) {
            JOptionPane.showMessageDialog(this, "Please select a bug", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            bugService.updateStatus(selectedBug.getId(), Status.valueOf(selectedStatus), currentUser);
            JOptionPane.showMessageDialog(this, "Bug status updated successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error updating status: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
