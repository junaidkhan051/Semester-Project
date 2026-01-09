package com.bugtracker.ui;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * Builds a lightweight app icon in code so we don't have to ship a binary asset.
 * The icon mirrors the laptop/bug/gear motif provided by design.
 */
public final class AppIcon {
    private static Image icon;

    private AppIcon() {
    }

    public static Image get() {
        if (icon == null) {
            icon = buildIcon();
        }
        return icon;
    }

    private static Image buildIcon() {
        int size = 256;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        // Gradient palette from yellow to deep orange
        Color start = new Color(255, 191, 0);
        Color end = new Color(255, 115, 0);
        GradientPaint gradient = new GradientPaint(0, 0, start, size, size, end);

        // Laptop body
        g.setPaint(gradient);
        Shape lid = new RoundRectangle2D.Float(44, 60, 168, 128, 28, 28);
        g.fill(lid);
        g.setStroke(new BasicStroke(6f));
        g.setColor(new Color(255, 140, 0, 180));
        g.draw(lid);

        // Screen cutout
        g.setColor(new Color(10, 20, 40, 210));
        g.fill(new RoundRectangle2D.Float(60, 80, 136, 88, 18, 18));

        // Keyboard base
        Shape base = new RoundRectangle2D.Float(24, 188, 208, 32, 12, 12);
        g.setPaint(gradient);
        g.fill(base);

        // Gear outline
        g.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.setColor(new Color(255, 200, 80));
        int cx = 128;
        int cy = 118;
        int rOuter = 46;
        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(i * 45);
            double x1 = cx + Math.cos(angle) * (rOuter - 10);
            double y1 = cy + Math.sin(angle) * (rOuter - 10);
            double x2 = cx + Math.cos(angle) * (rOuter + 10);
            double y2 = cy + Math.sin(angle) * (rOuter + 10);
            g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
        }
        g.drawOval(cx - rOuter, cy - rOuter, rOuter * 2, rOuter * 2);

        // Bug body
        g.setColor(new Color(25, 32, 48, 230));
        g.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.fillOval(cx - 18, cy - 12, 36, 40);
        g.setColor(new Color(255, 190, 90));
        g.drawOval(cx - 18, cy - 12, 36, 40);

        // Bug head
        g.setColor(new Color(25, 32, 48, 230));
        g.fillOval(cx - 14, cy - 26, 28, 20);
        g.setColor(new Color(255, 190, 90));
        g.drawOval(cx - 14, cy - 26, 28, 20);

        // Legs and antennae
        g.setColor(new Color(255, 200, 120));
        g.drawLine(cx - 22, cy - 2, cx - 38, cy - 12);
        g.drawLine(cx + 22, cy - 2, cx + 38, cy - 12);
        g.drawLine(cx - 22, cy + 10, cx - 38, cy + 20);
        g.drawLine(cx + 22, cy + 10, cx + 38, cy + 20);
        g.drawLine(cx - 10, cy - 34, cx - 18, cy - 48);
        g.drawLine(cx + 10, cy - 34, cx + 18, cy - 48);

        g.dispose();
        return img;
    }
}

