package com.bugtracker.ui;

import com.bugtracker.model.Bug;
import com.bugtracker.model.Product;
import com.bugtracker.model.User;
import com.bugtracker.model.enums.Severity;
import com.bugtracker.model.enums.Status;
import com.bugtracker.service.BugService;
import com.bugtracker.service.ProductService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

import java.util.List;

public class BugReportDialog extends JDialog {
    private final JTextField txtTitle = new JTextField(30);
    private final JTextArea txtDesc = new JTextArea(5, 30);
    private final JTextArea txtSteps = new JTextArea(4, 30);
    private final JComboBox<Severity> cmbSeverity = new JComboBox<>(Severity.values());
    private final JComboBox<Product> cmbProduct = new JComboBox<>();
    private final FileUploadPanel fileUploadPanel = new FileUploadPanel();

    private final BugService bugService = new BugService();
    private final ProductService productService = new ProductService();

    public BugReportDialog(Frame owner, User reporter) {
        super(owner, "Report Bug", true);
        setSize(650, 700);
        setLocationRelativeTo(owner);
        setUndecorated(true);

        initUI(reporter);
        loadProducts();
    }

    private void initUI(User reporter) {
        setBackground(new Color(0, 0, 0, 0));

        // Glass Panel
        GlassPanel glassPanel = new GlassPanel();
        glassPanel.setLayout(new BorderLayout(0, 20));
        glassPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        setContentPane(glassPanel);
        // FIX: Reduced height to fit within 700px dialog and preventing collapse
        glassPanel.setPreferredSize(new Dimension(550, 650));
        glassPanel.setMinimumSize(new Dimension(550, 650));

        // Header
        JPanel header = createHeader();
        glassPanel.add(header, BorderLayout.NORTH);

        // Form in scroll pane
        JPanel form = createForm();
        JScrollPane scrollPane = new JScrollPane(form);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Apply neon scrollbar
        NeonScrollBarUI.applyToScrollPane(scrollPane);

        glassPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttons = createButtons(reporter);
        glassPanel.add(buttons, BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel title = new JLabel("🐛 Report Bug");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Theme.TEXT_WHITE);
        header.add(title, BorderLayout.WEST);

        // Close button
        JButton closeBtn = new JButton("X");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 20));
        closeBtn.setForeground(Theme.TEXT_WHITE);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dispose());
        header.add(closeBtn, BorderLayout.EAST);

        return header;
    }

    private JPanel createForm() {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Title field
        form.add(createLabel("Title"));
        form.add(Box.createVerticalStrut(5));
        form.add(createStyledTextField(txtTitle));
        form.add(Box.createVerticalStrut(20));

        // Product and Severity
        JPanel row1 = new JPanel(new GridLayout(1, 2, 20, 0));
        row1.setOpaque(false);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel pnlProduct = new JPanel(new BorderLayout());
        pnlProduct.setOpaque(false);
        pnlProduct.add(createLabel("Product"), BorderLayout.NORTH);
        pnlProduct.add(createStyledComboBox(cmbProduct), BorderLayout.CENTER);

        JPanel pnlSeverity = new JPanel(new BorderLayout());
        pnlSeverity.setOpaque(false);
        pnlSeverity.add(createLabel("Severity"), BorderLayout.NORTH);
        pnlSeverity.add(createStyledComboBox(cmbSeverity), BorderLayout.CENTER);

        row1.add(pnlProduct);
        row1.add(pnlSeverity);
        form.add(row1);
        form.add(Box.createVerticalStrut(20));

        // Description
        form.add(createLabel("Description"));
        form.add(Box.createVerticalStrut(5));
        form.add(createStyledTextArea(txtDesc, 4));
        form.add(Box.createVerticalStrut(20));

        // Steps to Reproduce
        form.add(createLabel("Steps to Reproduce"));
        form.add(Box.createVerticalStrut(5));
        form.add(createStyledTextArea(txtSteps, 3));
        form.add(Box.createVerticalStrut(20));

        // File Upload
        form.add(createLabel("Attachments (Optional)"));
        form.add(Box.createVerticalStrut(5));
        form.add(fileUploadPanel);

        return form;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.FONT_BOLD);
        label.setForeground(new Color(220, 220, 220)); // Slightly softer white for labels
        label.setBorder(new EmptyBorder(0, 0, 5, 0));
        return label;
    }

    private JTextField createStyledTextField(JTextField textField) {
        textField.setFont(Theme.FONT_REGULAR);
        textField.setBackground(new Color(0, 0, 0, 60)); // Darker background for contrast
        textField.setForeground(Theme.TEXT_WHITE);
        textField.setCaretColor(Theme.TEXT_WHITE);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 30), 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        textField.setPreferredSize(new Dimension(0, 45));
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        return textField;
    }

    private JScrollPane createStyledTextArea(JTextArea textArea, int rows) {
        textArea.setFont(Theme.FONT_REGULAR);
        textArea.setBackground(new Color(0, 0, 0, 60));
        textArea.setForeground(Theme.TEXT_WHITE);
        textArea.setCaretColor(Theme.TEXT_WHITE);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setRows(rows);
        textArea.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 30), 1, true));
        scrollPane.setPreferredSize(new Dimension(0, rows * 25 + 30));

        // Apply neon scrollbar
        NeonScrollBarUI.applyToScrollPane(scrollPane);

        return scrollPane;
    }

    private <T> JComboBox<T> createStyledComboBox(JComboBox<T> comboBox) {
        comboBox.setFont(Theme.FONT_REGULAR);
        comboBox.setBackground(new Color(0, 0, 0, 60));
        comboBox.setForeground(Theme.TEXT_WHITE);
        comboBox.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 30), 1, true));
        comboBox.setPreferredSize(new Dimension(0, 45));
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        // Style the renderer
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(isSelected ? new Color(100, 149, 237) : new Color(30, 30, 40));
                setForeground(Color.WHITE);
                setBorder(new EmptyBorder(8, 12, 8, 12));
                return this;
            }
        });

        return comboBox;
    }

    private JPanel createButtons(User reporter) {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        buttons.setOpaque(false);
        buttons.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnCancel = createStyledButton("Cancel", Theme.ERROR_COLOR);
        btnCancel.addActionListener(e -> dispose());
        buttons.add(btnCancel);

        JButton btnSubmit = createStyledButton("Submit Bug", Theme.SUCCESS_COLOR);
        btnSubmit.setPreferredSize(new Dimension(140, 45));
        btnSubmit.addActionListener(e -> doSubmit(reporter));
        buttons.add(btnSubmit);

        return buttons;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Button background
                g2.setColor(getModel().isRollover() ? bgColor.brighter() : bgColor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), Theme.RADIUS_SMALL, Theme.RADIUS_SMALL);

                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(Theme.FONT_BOLD);
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(110, 45));

        return button;
    }

    private void loadProducts() {
        List<Product> products = productService.getAllProducts();
        DefaultComboBoxModel<Product> model = new DefaultComboBoxModel<>();
        for (Product p : products)
            model.addElement(p);
        cmbProduct.setModel(model);
    }

    private void doSubmit(User reporter) {
        if (txtTitle.getText().trim().isEmpty()) {
            showError("Title is required");
            return;
        }
        if (cmbProduct.getSelectedItem() == null) {
            showError("Please select a product");
            return;
        }

        Bug b = new Bug();
        b.setTitle(txtTitle.getText().trim());
        b.setDescription(txtDesc.getText());
        b.setSteps(txtSteps.getText());
        b.setSeverity((Severity) cmbSeverity.getSelectedItem());
        b.setStatus(Status.NEW);
        b.setReporter(reporter);
        b.setProduct((Product) cmbProduct.getSelectedItem());

        // Save bug and get the generated ID
        int bugId = bugService.reportBug(b, fileUploadPanel.getSelectedFiles());

        if (bugId > 0) {
            showSuccess("Bug reported successfully!");
            dispose();
        } else {
            showError("Failed to report bug. Please try again.");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}
