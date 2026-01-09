package com.bugtracker.ui;

import javax.swing.*;
import java.awt.*;

public class GlassPanel extends JPanel {
    private boolean isHovered = false;
    private Color customBackground;

    public GlassPanel() {
        this(Theme.GLASS_PANEL_BG);
    }

    public GlassPanel(Color bg) {
        this.customBackground = bg;
        setOpaque(false);
        setBackground(bg);

        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                isHovered = true;
                repaint();
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                isHovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int width = getWidth();
        int height = getHeight();

        g2.setColor(Theme.GLASS_SHADOW);
        g2.fillRoundRect(Theme.SHADOW_SIZE / 2, Theme.SHADOW_SIZE / 2,
                width - Theme.SHADOW_SIZE, height - Theme.SHADOW_SIZE,
                Theme.RADIUS, Theme.RADIUS);

        Color topColor = customBackground;
        Color bottomColor = new Color(
                Math.max(0, customBackground.getRed() - 20),
                Math.max(0, customBackground.getGreen() - 20),
                Math.max(0, customBackground.getBlue() - 20),
                customBackground.getAlpha());

        GradientPaint gradient = new GradientPaint(
                0, 0, topColor,
                0, height, bottomColor);
        g2.setPaint(gradient);
        g2.fillRoundRect(0, 0, width, height, Theme.RADIUS, Theme.RADIUS);

        GradientPaint lightOverlay = new GradientPaint(
                0, 0, Theme.GLASS_PANEL_LIGHT,
                0, height / 3, Theme.TRANSPARENT);
        g2.setPaint(lightOverlay);
        g2.fillRoundRect(0, 0, width, height, Theme.RADIUS, Theme.RADIUS);

        if (isHovered) {
            g2.setStroke(new BasicStroke(2.5f));
            GradientPaint borderGradient = new GradientPaint(
                    0, 0, Theme.GLASS_BORDER,
                    width, height, Theme.ACCENT_GLOW);
            g2.setPaint(borderGradient);
        } else {
            g2.setStroke(new BasicStroke(1.5f));
            g2.setColor(Theme.GLASS_BORDER);
        }
        g2.drawRoundRect(1, 1, width - 2, height - 2, Theme.RADIUS, Theme.RADIUS);

        g2.dispose();
        super.paintComponent(g);
    }
}
