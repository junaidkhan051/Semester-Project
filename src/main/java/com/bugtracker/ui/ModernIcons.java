package com.bugtracker.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

public class ModernIcons {

    public static Icon getLoginIcon(int size, Color color) {
        return new ModernIcon(size, color, "LOGIN");
    }

    public static Icon getRegisterIcon(int size, Color color) {
        return new ModernIcon(size, color, "REGISTER");
    }

    public static Icon getProductsIcon(int size, Color color) {
        return new ModernIcon(size, color, "PRODUCTS");
    }

    public static Icon getHelpIcon(int size, Color color) {
        return new ModernIcon(size, color, "HELP");
    }

    private static class ModernIcon implements Icon {
        private final int size;
        private final Color color;
        private final String type;

        public ModernIcon(int size, Color color, String type) {
            this.size = size;
            this.color = color;
            this.type = type;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.translate(x, y);

            float s = size / 24.0f; // Base drawing on 24x24 grid
            g2.scale(s, s);

            g2.setColor(color);
            Stroke stroke = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            g2.setStroke(stroke);

            switch (type) {
                case "LOGIN":
                    // Person icon with key or lock? Let's do a simple user profile with a key
                    // User
                    g2.draw(new Ellipse2D.Float(8, 3, 8, 8)); // Head
                    g2.draw(new Arc2D.Float(4, 13, 16, 12, 0, 180, Arc2D.OPEN)); // Shoulders
                    break;
                case "REGISTER":
                    // Document with plus
                    g2.draw(new Rectangle2D.Float(5, 3, 14, 18));
                    // Plus sign
                    g2.drawLine(12, 8, 12, 14);
                    g2.drawLine(9, 11, 15, 11);
                    break;
                case "PRODUCTS":
                    // 3D Box
                    Path2D box = new Path2D.Float();
                    box.moveTo(4, 8);
                    box.lineTo(12, 4);
                    box.lineTo(20, 8);
                    box.lineTo(20, 18);
                    box.lineTo(12, 22);
                    box.lineTo(4, 18);
                    box.closePath();
                    g2.draw(box);
                    // Inner lines
                    g2.drawLine(4, 8, 12, 12);
                    g2.drawLine(20, 8, 12, 12);
                    g2.drawLine(12, 12, 12, 22);
                    break;
                case "HELP":
                    // Circle with Question Mark
                    g2.draw(new Ellipse2D.Float(2, 2, 20, 20));
                    // Simple question mark curve
                    // We can draw it manully for better control than font
                    Path2D q = new Path2D.Float();
                    q.moveTo(8, 10);
                    q.curveTo(8, 6, 16, 6, 16, 10);
                    q.curveTo(16, 14, 12, 14, 12, 17);
                    g2.draw(q);
                    g2.fill(new Ellipse2D.Float(11, 19, 2, 2)); // Dot
                    break;
            }

            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }
    }
}
