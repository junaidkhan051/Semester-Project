package com.bugtracker.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Modern glass card component for form inputs
 */
public class GlassCard extends JPanel {
    private boolean isDarkMode;

    public GlassCard() {
        this(false);
    }

    public GlassCard(boolean isDarkMode) {
        this.isDarkMode = isDarkMode;
        setOpaque(false);
        setBackground(isDarkMode ? new Color(40, 40, 50, 180) : // Dark glass card
                new Color(255, 255, 255, 100)); // Light glass card
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int width = getWidth();
        int height = getHeight();

        // Paint glass card background
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, width, height, Theme.RADIUS_SMALL, Theme.RADIUS_SMALL);

        // Add subtle light overlay at top
        GradientPaint lightOverlay = new GradientPaint(
                0, 0, new Color(255, 255, 255, 30),
                0, height / 2, Theme.TRANSPARENT);
        g2.setPaint(lightOverlay);
        g2.fillRoundRect(0, 0, width, height, Theme.RADIUS_SMALL, Theme.RADIUS_SMALL);

        // Subtle border
        g2.setColor(new Color(255, 255, 255, 60));
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawRoundRect(0, 0, width - 1, height - 1, Theme.RADIUS_SMALL, Theme.RADIUS_SMALL);

        g2.dispose();
        super.paintComponent(g);
    }
}
