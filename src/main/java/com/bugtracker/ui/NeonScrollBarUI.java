package com.bugtracker.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class NeonScrollBarUI extends BasicScrollBarUI {

    private static final int THUMB_WIDTH = 8;
    private static final int THUMB_RADIUS = 4;
    private boolean isHovered = false;

    @Override
    protected void configureScrollBarColors() {
        // Transparent track
        this.thumbColor = new Color(0, 0, 0, 0);
        this.trackColor = new Color(0, 0, 0, 0);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    private JButton createZeroButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        return button;
    }

    @Override
    protected void installComponents() {
        super.installComponents();

        // Add mouse listener for hover effect
        scrollbar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                isHovered = true;
                scrollbar.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                isHovered = false;
                scrollbar.repaint();
            }
        });
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Subtle track background
        Color trackColor = Theme.isDarkMode
                ? new Color(255, 255, 255, 10)
                : new Color(0, 0, 0, 10);
        g2.setColor(trackColor);

        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            int centerX = trackBounds.x + trackBounds.width / 2;
            g2.fillRoundRect(centerX - 3, trackBounds.y, 6, trackBounds.height, 3, 3);
        } else {
            int centerY = trackBounds.y + trackBounds.height / 2;
            g2.fillRoundRect(trackBounds.x, centerY - 3, trackBounds.width, 6, 3, 3);
        }

        g2.dispose();
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Neon/Glow colors
        Color neonColor = Theme.isDarkMode
                ? new Color(100, 200, 255) // Cyan-blue
                : new Color(67, 97, 238); // Purple-blue

        Color glowColor = new Color(
                neonColor.getRed(),
                neonColor.getGreen(),
                neonColor.getBlue(),
                isHovered ? 80 : 40);

        if (scrollbar.getOrientation() == JScrollBar.VERTICAL) {
            int centerX = thumbBounds.x + thumbBounds.width / 2;
            int thumbX = centerX - THUMB_WIDTH / 2;

            // Glow effect (outer)
            if (isHovered) {
                g2.setColor(glowColor);
                g2.fillRoundRect(
                        thumbX - 2,
                        thumbBounds.y - 2,
                        THUMB_WIDTH + 4,
                        thumbBounds.height + 4,
                        THUMB_RADIUS + 2,
                        THUMB_RADIUS + 2);
            }

            // Main thumb with gradient
            GradientPaint gradient = new GradientPaint(
                    thumbX, thumbBounds.y, neonColor.brighter(),
                    thumbX + THUMB_WIDTH, thumbBounds.y, neonColor);
            g2.setPaint(gradient);
            g2.fillRoundRect(thumbX, thumbBounds.y, THUMB_WIDTH, thumbBounds.height, THUMB_RADIUS, THUMB_RADIUS);

            // Inner glow highlight
            Color highlightColor = new Color(255, 255, 255, isHovered ? 100 : 60);
            g2.setColor(highlightColor);
            g2.fillRoundRect(thumbX + 1, thumbBounds.y + 1, THUMB_WIDTH - 2, thumbBounds.height / 3, THUMB_RADIUS - 1,
                    THUMB_RADIUS - 1);

        } else {
            // Horizontal scrollbar
            int centerY = thumbBounds.y + thumbBounds.height / 2;
            int thumbY = centerY - THUMB_WIDTH / 2;

            // Glow effect (outer)
            if (isHovered) {
                g2.setColor(glowColor);
                g2.fillRoundRect(
                        thumbBounds.x - 2,
                        thumbY - 2,
                        thumbBounds.width + 4,
                        THUMB_WIDTH + 4,
                        THUMB_RADIUS + 2,
                        THUMB_RADIUS + 2);
            }

            // Main thumb with gradient
            GradientPaint gradient = new GradientPaint(
                    thumbBounds.x, thumbY, neonColor.brighter(),
                    thumbBounds.x, thumbY + THUMB_WIDTH, neonColor);
            g2.setPaint(gradient);
            g2.fillRoundRect(thumbBounds.x, thumbY, thumbBounds.width, THUMB_WIDTH, THUMB_RADIUS, THUMB_RADIUS);

            // Inner glow highlight
            Color highlightColor = new Color(255, 255, 255, isHovered ? 100 : 60);
            g2.setColor(highlightColor);
            g2.fillRoundRect(thumbBounds.x + 1, thumbY + 1, thumbBounds.width / 3, THUMB_WIDTH - 2, THUMB_RADIUS - 1,
                    THUMB_RADIUS - 1);
        }

        g2.dispose();
    }

    @Override
    protected Dimension getMinimumThumbSize() {
        return new Dimension(THUMB_WIDTH, 40);
    }

    /**
     * Apply neon scrollbar to a JScrollPane
     */
    public static void applyToScrollPane(JScrollPane scrollPane) {
        scrollPane.getVerticalScrollBar().setUI(new NeonScrollBarUI());
        scrollPane.getHorizontalScrollBar().setUI(new NeonScrollBarUI());

        // Make scrollbars more prominent
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);

        // Transparent background
        scrollPane.getVerticalScrollBar().setOpaque(false);
        scrollPane.getHorizontalScrollBar().setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        // Remove border
        scrollPane.setBorder(null);
    }
}
