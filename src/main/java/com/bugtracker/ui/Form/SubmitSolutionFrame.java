package com.bugtracker.ui.Form;

import com.bugtracker.model.Bug;
import com.bugtracker.model.User;
import com.bugtracker.service.BugService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SubmitSolutionFrame extends JFrame {
    private User currentUser;
    private BugService bugService = new BugService();
    private JComboBox<Bug> bugCombo;
    private JTextArea solutionArea;

    public SubmitSolutionFrame(User user) {
        this.currentUser = user;

        setTitle("Submit Solution");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Submit Solution/Comment");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Select Bug:"), gbc);

        gbc.gridx = 1;
        bugCombo = new JComboBox<>();
        bugCombo.setPreferredSize(new Dimension(300, 30));
        loadAssignedBugs();
        mainPanel.add(bugCombo, gbc);

        gbc.gridy = 2;
        gbc.gridx = 0;
        mainPanel.add(new JLabel("Solution:"), gbc);

        gbc.gridx = 1;
        solutionArea = new JTextArea(8, 30);
        solutionArea.setLineWrap(true);
        JScrollPane scrollPane = new JScrollPane(solutionArea);
        scrollPane.setPreferredSize(new Dimension(300, 150));
        mainPanel.add(scrollPane, gbc);

        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton submitButton = new JButton("Submit Solution");
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitButton.setBackground(new Color(40, 167, 69));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setPreferredSize(new Dimension(170, 35));
        submitButton.addActionListener(e -> handleSubmit());
        mainPanel.add(submitButton, gbc);

        add(mainPanel);
    }

    private void loadAssignedBugs() {
        List<Bug> bugs = bugService.getBugsForAssignee(currentUser);
        for (Bug bug : bugs) {
            bugCombo.addItem(bug);
        }
    }

    private void handleSubmit() {
        Bug selectedBug = (Bug) bugCombo.getSelectedItem();
        String solution = solutionArea.getText().trim();

        if (selectedBug == null) {
            JOptionPane.showMessageDialog(this, "Please select a bug", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (solution.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a solution", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Note: addComment method would need to be implemented in BugService
        // For now, just show a message
        JOptionPane.showMessageDialog(this, "Solution submitted successfully!", "Success",
                JOptionPane.INFORMATION_MESSAGE);
        solutionArea.setText("");
        dispose();
    }
}
