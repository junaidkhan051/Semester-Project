package com.bugtracker.ui;

import com.bugtracker.model.Product;
import com.bugtracker.model.User;
import com.bugtracker.service.ProductService;
import com.bugtracker.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class LoginFrame extends JFrame {
    private final UserService userService = new UserService();
    private final ProductService productService = new ProductService();
    private Image bgImage;

    private JPanel mainPanel;
    private CardLayout cardLayout;

    public LoginFrame() {
        setTitle("Bug Tracker System");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setIconImage(AppIcon.get());

        bgImage = ImageUtils.load(
                "C:/Users/CC/.gemini/antigravity/brain/3625d386-186f-4d18-a8d0-bbbe707f1618/tech_background_1765723672263.png");

        JPanel contentPane = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (bgImage != null) {
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        contentPane.setLayout(new GridBagLayout());
        setContentPane(contentPane);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(0, 0, 30, 0);

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel lblWelcome = new JLabel("WELCOME");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 48));
        lblWelcome.setForeground(Color.WHITE);
        lblWelcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("BUG TRACKING SYSTEM");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSub.setForeground(new Color(200, 200, 255));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(lblWelcome);
        header.add(Box.createVerticalStrut(10));
        header.add(lblSub);

        contentPane.add(header, c);

        c.gridy = 1;
        c.insets = new Insets(0, 0, 0, 0);

        GlassPanel glassCard = new GlassPanel();
        glassCard.setLayout(new BorderLayout());
        glassCard.setPreferredSize(new Dimension(600, 400));

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setOpaque(false);

        mainPanel.add(createWelcomePanel(), "WELCOME");
        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createRegisterPanel(), "REGISTER");
        mainPanel.add(createProductsPanel(), "PRODUCTS");
        mainPanel.add(createHelpPanel(), "HELP");

        glassCard.add(mainPanel, BorderLayout.CENTER);

        contentPane.add(glassCard, c);

        c.gridy = 2;
        c.insets = new Insets(30, 0, 0, 0);
        JLabel lblFooter = new JLabel("© 2025 BugTracker");
        lblFooter.setForeground(new Color(150, 150, 150));
        contentPane.add(lblFooter, c);
    }

    private JPanel createWelcomePanel() {
        JPanel p = new JPanel(new GridLayout(2, 2, 10, 10));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(30, 30, 30, 30));

        p.add(createIconButton("Login", "Login to your account", ModernIcons.getLoginIcon(64, new Color(64, 224, 208)),
                () -> cardLayout.show(mainPanel, "LOGIN")));
        p.add(createIconButton("Register (Customer)", "Create new account",
                ModernIcons.getRegisterIcon(64, new Color(255, 105, 180)),
                () -> cardLayout.show(mainPanel, "REGISTER")));
        p.add(createIconButton("Products", "View products", ModernIcons.getProductsIcon(64, new Color(50, 205, 50)),
                () -> cardLayout.show(mainPanel, "PRODUCTS")));
        p.add(createIconButton("Help", "Get support", ModernIcons.getHelpIcon(64, new Color(255, 165, 0)),
                () -> cardLayout.show(mainPanel, "HELP")));

        return p;
    }

    private JButton createIconButton(String title, String sub, Icon icon, Runnable action) {
        JButton btn = new JButton(
                "<html><center><b>" + title + "</b><br><span style='font-size:8px'>" + sub + "</span></center></html>");
        btn.setIcon(icon);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setIconTextGap(15);
        btn.setFont(Theme.FONT_REGULAR);
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(e -> action.run());

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(Theme.ACCENT);
            }

            public void mouseExited(MouseEvent e) {
                btn.setForeground(Color.WHITE);
            }
        });

        return btn;
    }

    private JPanel createLoginPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 10, 5, 10);
        c.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtUser = new JTextField(15);
        JPasswordField txtPass = new JPasswordField(15);
        styleField(txtUser);
        styleField(txtPass);

        c.gridx = 0;
        c.gridy = 0;
        JLabel l1 = new JLabel("Username");
        l1.setForeground(Color.WHITE);
        p.add(l1, c);

        c.gridy = 1;
        p.add(txtUser, c);

        c.gridy = 2;
        JLabel l2 = new JLabel("Password");
        l2.setForeground(Color.WHITE);
        p.add(l2, c);

        c.gridy = 3;
        p.add(txtPass, c);

        c.gridy = 4;
        c.fill = GridBagConstraints.NONE;
        JButton btn = new JButton("ENTER");
        styleButton(btn);
        btn.addActionListener(e -> {
            User user = userService.authenticate(txtUser.getText(), new String(txtPass.getPassword()));
            if (user != null) {
                SwingUtilities.invokeLater(() -> {
                    new DashboardFrame(user).setVisible(true);
                    dispose();
                });
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Login");
            }
        });
        p.add(btn, c);

        c.gridy = 5;
        JButton btnBack = new JButton("Back");
        styleLinkButton(btnBack);
        btnBack.addActionListener(e -> cardLayout.show(mainPanel, "WELCOME"));
        p.add(btnBack, c);

        return p;
    }

    private JPanel createRegisterPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 10, 5, 10);
        c.fill = GridBagConstraints.HORIZONTAL;

        JTextField txtUser = new JTextField(15);
        JPasswordField txtPass = new JPasswordField(15);
        JTextField txtFull = new JTextField(15);
        JTextField txtEmail = new JTextField(15);

        styleField(txtUser);
        styleField(txtPass);
        styleField(txtFull);
        styleField(txtEmail);

        c.gridx = 0;
        c.gridy = 0;
        p.add(createLabel("Username"), c);
        c.gridy = 1;
        p.add(txtUser, c);
        c.gridy = 2;
        p.add(createLabel("Password"), c);
        c.gridy = 3;
        p.add(txtPass, c);
        c.gridy = 4;
        p.add(createLabel("Full Name"), c);
        c.gridy = 5;
        p.add(txtFull, c);
        c.gridy = 6;
        p.add(createLabel("Email"), c);
        c.gridy = 7;
        p.add(txtEmail, c);

        c.gridy = 8;
        c.fill = GridBagConstraints.NONE;
        JButton btn = new JButton("REGISTER");
        styleButton(btn);
        btn.addActionListener(e -> {
            User u = new User();
            u.setUsername(txtUser.getText());
            u.setPassword(new String(txtPass.getPassword()));
            u.setFullName(txtFull.getText());
            u.setEmail(txtEmail.getText());
            u.setRole("CUSTOMER"); // Default role

            if (userService.register(u)) {
                JOptionPane.showMessageDialog(this, "Registration Successful! Please login.");
                cardLayout.show(mainPanel, "LOGIN");
            } else {
                JOptionPane.showMessageDialog(this, "Registration Failed (Username might be taken)");
            }
        });
        p.add(btn, c);

        c.gridy = 9;
        JButton btnBack = new JButton("Back");
        styleLinkButton(btnBack);
        btnBack.addActionListener(e -> cardLayout.show(mainPanel, "WELCOME"));
        p.add(btnBack, c);

        return p;
    }

    private JPanel createProductsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Our Products");
        title.setFont(Theme.FONT_HEADER);
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(title, BorderLayout.NORTH);

        JPanel carouselPanel = new JPanel(new CardLayout());
        carouselPanel.setOpaque(false);

        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            JLabel lblNoProd = new JLabel("No products available", SwingConstants.CENTER);
            lblNoProd.setForeground(Color.WHITE);
            carouselPanel.add(lblNoProd, "EMPTY");
        } else {
            for (int i = 0; i < products.size(); i++) {
                carouselPanel.add(createProductCard(products.get(i)), "PROD_" + i);
            }
        }

        p.add(carouselPanel, BorderLayout.CENTER);

        // Navigation Buttons
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        navPanel.setOpaque(false);

        JButton btnPrev = new JButton("❮");
        styleNavButton(btnPrev);

        JButton btnNext = new JButton("❯");
        styleNavButton(btnNext);

        CardLayout cl = (CardLayout) carouselPanel.getLayout();
        btnPrev.addActionListener(e -> cl.previous(carouselPanel));
        btnNext.addActionListener(e -> cl.next(carouselPanel));

        navPanel.add(btnPrev);

        JButton btnBack = new JButton("Back to Home");
        styleLinkButton(btnBack);
        btnBack.addActionListener(e -> cardLayout.show(mainPanel, "WELCOME"));
        navPanel.add(btnBack);

        navPanel.add(btnNext);

        p.add(navPanel, BorderLayout.SOUTH);

        return p;
    }

    private JPanel createProductCard(Product product) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(10, 10, 10, 10);

        // Product Icon/Image Placeholder
        JLabel iconLabel = new JLabel("📦"); // Placeholder icon
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
        iconLabel.setForeground(Theme.ACCENT);
        card.add(iconLabel, c);

        c.gridy++;
        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        nameLabel.setForeground(Color.WHITE);
        card.add(nameLabel, c);

        c.gridy++;
        JTextArea descArea = new JTextArea(product.getDescription());
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setOpaque(false);
        descArea.setEditable(false);
        descArea.setFocusable(false);
        descArea.setForeground(new Color(220, 220, 220));
        descArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        descArea.setPreferredSize(new Dimension(400, 80));
        descArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Center text in area
        // Note: usage of JTextPane with StyledDocument would allow centering,
        // but for simplicity we keep JTextArea or use a JLabel with HTML.
        // Let's use JLabel with HTML for better centering support

        JLabel descLabel = new JLabel(
                "<html><div style='text-align: center; width: 300px;'>" + product.getDescription() + "</div></html>");
        descLabel.setForeground(new Color(220, 220, 220));
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        card.add(descLabel, c);

        return card;
    }

    private void styleNavButton(JButton btn) {
        btn.setFont(new Font("Arial", Font.BOLD, 20));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(Theme.ACCENT);
            }

            public void mouseExited(MouseEvent e) {
                btn.setForeground(Color.WHITE);
            }
        });
    }

    private JPanel createHelpPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Help & Support");
        title.setFont(Theme.FONT_HEADER);
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(title, BorderLayout.NORTH);

        JTextArea txt = new JTextArea();
        txt.setText("Bug Tracker System Help\n\n" +
                "1. Login: Use your credentials to access the dashboard.\n" +
                "2. Register: Create a new account if you don't have one.\n" +
                "3. Products: View the list of supported products.\n" +
                "4. Reporting Bugs: Log in and click 'Report Bug'.\n\n" +
                "For technical support, contact admin@bugtracker.com");
        txt.setOpaque(false);
        txt.setForeground(Color.darkGray);
        txt.setEditable(false);
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);

        p.add(new JScrollPane(txt) {
            {
                setOpaque(false);
                getViewport().setOpaque(false);
                setBorder(null);
            }
        }, BorderLayout.CENTER);

        JButton btnBack = new JButton("Back");
        styleLinkButton(btnBack);
        btnBack.addActionListener(e -> cardLayout.show(mainPanel, "WELCOME"));
        p.add(btnBack, BorderLayout.SOUTH);

        return p;
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Color.WHITE);
        return l;
    }

    private void styleField(JTextField f) {
        f.setBackground(new Color(255, 255, 255, 30));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 50)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    }

    private void styleButton(JButton btn) {
        btn.setBackground(Theme.PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleLinkButton(JButton btn) {
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setForeground(Color.GRAY);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
}
