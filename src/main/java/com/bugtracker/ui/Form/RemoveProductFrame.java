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
import java.util.List;

public class RemoveProductFrame extends JFrame {
    private final ProductService productService = new ProductService();
    private JComboBox<Product> productCombo;
    private Point initialClick;

    public RemoveProductFrame() {
        setTitle("Remove Product");
        setSize(450, 320);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
                getComponentAt(initialClick);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int thisX = getLocation().x;
                int thisY = getLocation().y;

                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;

                setLocation(thisX + xMoved, thisY + yMoved);
            }
        });

        initUI();
    }

    private void initUI() {
        setBackground(new Color(0, 0, 0, 0));

        GlassPanel mainPanel = new GlassPanel();
        mainPanel.setLayout(new BorderLayout(0, 20));
        mainPanel.setBorder(new EmptyBorder(30, 35, 30, 35));
        setContentPane(mainPanel);

        JPanel header = createHeader();
        mainPanel.add(header, BorderLayout.NORTH);

        JPanel form = createForm();
        mainPanel.add(form, BorderLayout.CENTER);

        JPanel buttons = createButtons();
        mainPanel.add(buttons, BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel title = new JLabel("Remove Product");
        title.setFont(Theme.FONT_TITLE.deriveFont(22f));
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

        form.add(createLabel("Select a product to remove"));
        form.add(Box.createVerticalStrut(8));

        productCombo = new JComboBox<>();
        productCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        productCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        productCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                JLabel renderer = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected,
                        cellHasFocus);
                renderer.setFont(Theme.FONT_REGULAR);
                renderer.setForeground(Theme.TEXT_DARK);
                renderer.setBackground(isSelected ? Theme.ACCENT : new Color(0, 0, 0, 40));
                if (value instanceof Product) {
                    Product p = (Product) value;
                    renderer.setText(p.getName() + " v" + p.getVersion());
                }
                return renderer;
            }
        });

        JScrollPane comboWrapper = new JScrollPane(productCombo);
        comboWrapper.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 30), 1, true));
        comboWrapper.setOpaque(false);
        comboWrapper.getViewport().setOpaque(false);
        comboWrapper.setPreferredSize(new Dimension(0, 60));
        form.add(comboWrapper);

        loadProducts();

        return form;
    }

    private JPanel createButtons() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttons.setOpaque(false);
        buttons.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnRemove = createStyledButton("Remove", Theme.SUCCESS_COLOR);
        btnRemove.addActionListener(e -> handleRemoveProduct());
        btnRemove.setPreferredSize(new Dimension(140, 45));
        buttons.add(btnRemove);

        return buttons;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Theme.FONT_BOLD);
        label.setForeground(Theme.FORM_LABEL_COLOR);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
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

    private void loadProducts() {
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No products found to remove.", "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
            return;
        }
        DefaultComboBoxModel<Product> model = new DefaultComboBoxModel<>();
        for (Product p : products) {
            model.addElement(p);
        }
        productCombo.setModel(model);
    }

    private void handleRemoveProduct() {
        Product selected = (Product) productCombo.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a product first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Remove product \"" + selected.getName() + "\"?", "Confirm Removal",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean success = productService.removeProduct(selected.getId());
        if (success) {
            JOptionPane.showMessageDialog(this, "Product removed successfully!", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to remove product.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

