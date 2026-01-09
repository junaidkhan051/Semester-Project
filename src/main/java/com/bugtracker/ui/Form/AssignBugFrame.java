package com.bugtracker.ui.Form;

import com.bugtracker.model.Bug;
import com.bugtracker.model.User;
import com.bugtracker.service.BugService;
import com.bugtracker.service.UserService;

import com.bugtracker.ui.GlassPanel;
import com.bugtracker.ui.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class AssignBugFrame extends JFrame {
    private BugService bugService = new BugService();
    private UserService userService = new UserService();
    private JComboBox<Bug> bugCombo;
    private JComboBox<User> techCombo;
    private User currentUser;

    public AssignBugFrame(User currentUser) {
        this.currentUser = currentUser;
        setTitle("Assign Bug");
        setSize(500, 400);
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

        JLabel title = new JLabel("Assign Bug");
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
        loadBugs();
        form.add(bugCombo);

        form.add(Box.createVerticalStrut(20));

        // Select Developer
        form.add(createLabel("Assign To Technical Person"));
        form.add(Box.createVerticalStrut(5));
        techCombo = new JComboBox<>();
        styleComboBox(techCombo);
        loadTechnicalPersons();
        form.add(techCombo);

        return form;
    }

    private JPanel createButtons() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.setOpaque(false);

        JButton assignButton = new JButton("Assign Bug");
        styleButton(assignButton, Theme.PRIMARY);
        assignButton.addActionListener(e -> handleAssign());
        buttons.add(assignButton);

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

    private void loadBugs() {
        List<Bug> bugs = bugService.getAllBugs();
        for (Bug bug : bugs) {
            bugCombo.addItem(bug);
        }
    }

    private void loadTechnicalPersons() {
        List<User> developers = userService.getUsersByRole("TECH_PERSON");
        for (User user : developers) {
            techCombo.addItem(user);
        }
    }

    private void handleAssign() {
        Bug selectedBug = (Bug) bugCombo.getSelectedItem();
        User selectedTech = (User) techCombo.getSelectedItem();

        if (selectedBug == null || selectedTech == null) {
            JOptionPane.showMessageDialog(this, "Please select both bug and developer", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            bugService.assignBug(selectedBug.getId(), currentUser, selectedTech);
            JOptionPane.showMessageDialog(this, "Bug assigned successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error assigning bug: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
