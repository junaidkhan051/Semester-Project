package com.bugtracker.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Animated background panel with gradient and floating particles
 */
public class AnimatedBackgroundPanel extends JPanel {
    private List<Particle> particles;
    private Timer animationTimer;
    private final Random random = new Random();

    public AnimatedBackgroundPanel() {
        setLayout(new BorderLayout());
        particles = new ArrayList<>();
        initParticles();
        startAnimation();
    }

    private void initParticles() {
        // Create 25 floating particles
        for (int i = 0; i < 25; i++) {
            particles.add(new Particle(
                    random.nextInt(1000),
                    random.nextInt(800),
                    10 + random.nextInt(40), // size
                    random.nextFloat() * 0.5f + 0.3f, // speed
                    random.nextFloat() * 360 // direction
            ));
        }
    }

    private void startAnimation() {
        animationTimer = new Timer(Theme.ANIMATION_DELAY, e -> {
            updateParticles();
            repaint();
        });
        animationTimer.start();
    }

    private void updateParticles() {
        for (Particle p : particles) {
            p.update(getWidth(), getHeight());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int width = getWidth();
        int height = getHeight();

        // Draw gradient background
        GradientPaint gradient = new GradientPaint(
                0, 0, Theme.GRADIENT_START,
                width, height, Theme.GRADIENT_END);
        g2.setPaint(gradient);
        g2.fillRect(0, 0, width, height);

        // Add secondary gradient for depth
        GradientPaint secondaryGradient = new GradientPaint(
                width / 2, 0, new Color(Theme.GRADIENT_MID.getRed(),
                        Theme.GRADIENT_MID.getGreen(),
                        Theme.GRADIENT_MID.getBlue(), 100),
                width / 2, height, Theme.TRANSPARENT);
        g2.setPaint(secondaryGradient);
        g2.fillRect(0, 0, width, height);

        // Draw particles
        for (Particle p : particles) {
            p.draw(g2);
        }

        g2.dispose();
    }

    /**
     * Inner class representing a floating particle
     */
    private class Particle {
        float x, y;
        float size;
        float speed;
        float direction;
        float opacity;
        Color color;

        public Particle(float x, float y, float size, float speed, float direction) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.speed = speed;
            this.direction = direction;
            this.opacity = random.nextFloat() * 0.3f + 0.1f;

            // Random subtle colors
            int colorChoice = random.nextInt(3);
            switch (colorChoice) {
                case 0:
                    color = new Color(255, 255, 255, (int) (opacity * 255));
                    break;
                case 1:
                    color = new Color(138, 180, 248, (int) (opacity * 255));
                    break;
                default:
                    color = new Color(162, 155, 254, (int) (opacity * 255));
                    break;
            }
        }

        public void update(int maxWidth, int maxHeight) {
            // Update position
            x += Math.cos(Math.toRadians(direction)) * speed;
            y += Math.sin(Math.toRadians(direction)) * speed;

            // Wrap around screen
            if (x < -size)
                x = maxWidth + size;
            if (x > maxWidth + size)
                x = -size;
            if (y < -size)
                y = maxHeight + size;
            if (y > maxHeight + size)
                y = -size;

            // Subtle direction change for organic movement
            direction += (random.nextFloat() - 0.5f) * 2;
        }

        public void draw(Graphics2D g2) {
            g2.setColor(color);

            // Draw circle with gradient for glow effect
            RadialGradientPaint radial = new RadialGradientPaint(
                    x, y, size / 2,
                    new float[] { 0f, 1f },
                    new Color[] {
                            new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()),
                            Theme.TRANSPARENT
                    });
            g2.setPaint(radial);

            Ellipse2D circle = new Ellipse2D.Float(x - size / 2, y - size / 2, size, size);
            g2.fill(circle);
        }
    }

    public void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }
}
