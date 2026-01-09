package com.bugtracker.ui.Form;

import com.bugtracker.model.Product;
import com.bugtracker.service.ProductService;

import com.bugtracker.ui.GlassPanel;
import com.bugtracker.ui.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class AddProductFrame extends JFrame {
    private ProductService productService = new ProductService();
    private JTextField nameField;
    private JTextField versionField;
    private JTextArea descriptionArea;
    private Point initialClick;

    public AddProductFrame() {
        setTitle("Add Product");
        setSize(500, 600);
        setUndecorated(true); // Remove default window decor
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Add drag functionality
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                getComponentAt(initialClick);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // get location of Window
                int thisX = getLocation().x;
                int thisY = getLocation().y;

                // Determine how much the mouse moved since the initial click
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;

                // Move window to this position
                int X = thisX + xMoved;
                int Y = thisY + yMoved;
                setLocation(X, Y);
            }
        });

        initUI();
    }

    private void initUI() {
        // Animated background
        setBackground(new Color(0, 0, 0, 0));

        // Main glass panel container
        GlassPanel mainPanel = new GlassPanel();
        mainPanel.setLayout(new BorderLayout(0, 20));
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        setContentPane(mainPanel);

        // Header
        JPanel header = createHeader();
        mainPanel.add(header, BorderLayout.NORTH);

        // Form
        JPanel form = createForm();
        mainPanel.add(form, BorderLayout.CENTER);

        // Buttons
        JPanel buttons = createButtons();
        mainPanel.add(buttons, BorderLayout.SOUTH);

        // Add main panel centered

    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel title = new JLabel("New Product");
        title.setFont(Theme.FONT_TITLE.deriveFont(24f));
        title.setForeground(Theme.TEXT_WHITE);
        header.add(title, BorderLayout.WEST);

        JButton closeBtn = new JButton("X");
        closeBtn.setFont(new Font("Arial", Font.BOLD, 18));
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

        // Name
        form.add(createLabel("Product Name"));
        form.add(Box.createVerticalStrut(5));
        nameField = createStyledTextField();
        form.add(nameField);
        form.add(Box.createVerticalStrut(20));

        // Version
        form.add(createLabel("Version"));
        form.add(Box.createVerticalStrut(5));
        versionField = createStyledTextField();
        form.add(versionField);
        form.add(Box.createVerticalStrut(20));

        // Description
        form.add(createLabel("Description"));
        form.add(Box.createVerticalStrut(5));
        descriptionArea = createStyledTextArea();
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 30), 1, true));
        scrollPane.setPreferredSize(new Dimension(0, 100));
        form.add(scrollPane);

        return form;
    }

    private JPanel createButtons() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttons.setOpaque(false);
        buttons.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnAdd = createStyledButton("Add Product", Theme.SUCCESS_COLOR);
        btnAdd.addActionListener(e -> handleAddProduct());

        // Make the button slightly wider
        btnAdd.setPreferredSize(new Dimension(160, 45));

        buttons.add(btnAdd);

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
        field.setFont(Theme.FONT_REGULAR);
        field.setBackground(new Color(0, 0, 0, 60));
        field.setForeground(Theme.TEXT_WHITE);
        field.setCaretColor(Theme.TEXT_WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 30), 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        return field;
    }

    private JTextArea createStyledTextArea() {
        JTextArea area = new JTextArea();
        area.setFont(Theme.FONT_REGULAR);
        area.setBackground(new Color(0, 0, 0, 60));
        area.setForeground(Theme.TEXT_WHITE);
        area.setCaretColor(Theme.TEXT_WHITE);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        return area;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
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
        button.setPreferredSize(new Dimension(120, 40));
        return button;
    }

    private void handleAddProduct() {
        String name = nameField.getText().trim();
        String version = versionField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (name.isEmpty() || version.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Version are required", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Product product = new Product(0, name, version, description);
        boolean success = productService.addProduct(product);

        if (success) {
            JOptionPane.showMessageDialog(this, "Product added successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add product", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
