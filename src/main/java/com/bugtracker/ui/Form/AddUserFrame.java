package com.bugtracker.ui.Form;

import com.bugtracker.model.User;
import com.bugtracker.service.UserService;

import com.bugtracker.ui.GlassPanel;
import com.bugtracker.ui.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AddUserFrame extends JFrame {
    private UserService userService = new UserService();
    private String role;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField fullNameField;
    private JTextField emailField;

    public AddUserFrame(String role) {
        this.role = role;

        setTitle("Add " + role);
        setSize(450, 550);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setBackground(new Color(50, 50, 50));

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

        JLabel title = new JLabel("Add " + toTitleCase(role));
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

        // Username
        form.add(createLabel("Username"));
        form.add(Box.createVerticalStrut(5));
        usernameField = createStyledTextField();
        form.add(usernameField);

        form.add(Box.createVerticalStrut(15));

        // Password
        form.add(createLabel("Password"));
        form.add(Box.createVerticalStrut(5));
        passwordField = createStyledPasswordField();
        form.add(passwordField);

        form.add(Box.createVerticalStrut(15));

        // Full Name
        form.add(createLabel("Full Name"));
        form.add(Box.createVerticalStrut(5));
        fullNameField = createStyledTextField();
        form.add(fullNameField);

        form.add(Box.createVerticalStrut(15));

        // Email
        form.add(createLabel("Email"));
        form.add(Box.createVerticalStrut(5));
        emailField = createStyledTextField();
        form.add(emailField);

        return form;
    }

    private JPanel createButtons() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.setOpaque(false);

        JButton addButton = new JButton("Create User");
        styleButton(addButton, Theme.SUCCESS_COLOR);
        addButton.addActionListener(e -> handleAddUser());
        buttons.add(addButton);

        return buttons;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.FONT_BOLD);
        label.setForeground(Theme.FORM_LABEL_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        styleField(field);
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        styleField(field);
        return field;
    }

    private void styleField(JTextField field) {
        field.setFont(Theme.FONT_REGULAR);
        field.setBackground(new Color(0, 0, 0, 60));
        field.setForeground(Theme.TEXT_WHITE);
        field.setCaretColor(Theme.TEXT_WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 30), 1, true),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
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

    private String toTitleCase(String input) {
        if (input == null || input.isEmpty())
            return "";
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase().replace("_", " ");
    }

    private void handleAddUser() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || fullName.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Invalid email format", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = new User(0, username, password, fullName, email, role);
        boolean success = userService.register(user);

        if (success) {
            JOptionPane.showMessageDialog(this, role + " added successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            usernameField.setText("");
            passwordField.setText("");
            fullNameField.setText("");
            emailField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add user. Username may already exist.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
