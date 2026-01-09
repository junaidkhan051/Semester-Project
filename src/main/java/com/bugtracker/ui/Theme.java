package com.bugtracker.ui;

import java.awt.*;

public class Theme {

    public static boolean isDarkMode = true; // Default to Dark Mode

    // Colors - Non-final to allow switching
    public static Color PRIMARY;
    public static Color ACCENT;

    // Modern Glassmorphism Colors
    public static Color GLASS_PANEL_BG;
    public static Color GLASS_PANEL_LIGHT;
    public static Color GLASS_BORDER;
    public static Color GLASS_SHADOW;
    public static Color TRANSPARENT = new Color(0, 0, 0, 0);

    // Background Gradient Colors
    public static Color GRADIENT_START;
    public static Color GRADIENT_MID;
    public static Color GRADIENT_END;

    // Interactive Elements
    public static Color ACCENT_GLOW;
    public static Color SUCCESS_COLOR = new Color(72, 199, 142);
    public static Color ERROR_COLOR = new Color(239, 71, 111);

    // Sidebar Colors
    public static Color SIDEBAR_BG;
    public static Color SIDEBAR_ITEM_HOVER;
    public static Color SIDEBAR_ITEM_ACTIVE;

    // Text Colors
    public static Color TEXT_WHITE;
    public static Color TEXT_GRAY;
    public static Color TEXT_DARK;
    public static Color FORM_LABEL_COLOR;

    // Fonts
    public static final Font FONT_TITLE = new Font("Britannic Bold", Font.BOLD, 32);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_ICON = new Font("Segoe UI Symbol", Font.PLAIN, 24);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);

    // Styling Constants
    public static final int RADIUS = 20;
    public static final int RADIUS_SMALL = 12;
    public static final int SHADOW_SIZE = 15;
    public static final int ANIMATION_DELAY = 16;

    static {
        loadTheme();
    }

    public static void toggle() {
        isDarkMode = !isDarkMode;
        loadTheme();
    }

    private static void loadTheme() {
        if (isDarkMode) {
            // DARK MODE - DEEP BLUE THEME
            PRIMARY = new Color(64, 156, 255); // Vibrant Blue
            ACCENT = new Color(100, 200, 255); // Cyan-ish Blue Accent

            // Glass Panel - Deep Navy tint
            GLASS_PANEL_BG = new Color(15, 23, 42, 210);
            GLASS_PANEL_LIGHT = new Color(255, 255, 255, 10);
            GLASS_BORDER = new Color(56, 189, 248, 30); // Subtle Blue border
            GLASS_SHADOW = new Color(0, 0, 0, 100);

            // Background Gradient - Rich "Cyber" Blue
            GRADIENT_START = new Color(10, 15, 30); // Almost black-blue
            GRADIENT_MID = new Color(15, 25, 50); // Deep Navy
            GRADIENT_END = new Color(25, 45, 80); // Lighter Navy

            ACCENT_GLOW = new Color(56, 189, 248, 80);

            // Sidebar Colors - Dark Mode
            SIDEBAR_BG = new Color(10, 18, 35, 240); // Darker than main background
            SIDEBAR_ITEM_HOVER = new Color(56, 189, 248, 30); // Subtle blue glow
            SIDEBAR_ITEM_ACTIVE = new Color(64, 156, 255, 50); // Brighter active state

            TEXT_WHITE = new Color(240, 248, 255); // AliceBlue (slightly cool white)
            TEXT_GRAY = new Color(40, 63, 14); // Slate Blue Gray
            TEXT_DARK = new Color(15, 23, 42);
            FORM_LABEL_COLOR = new Color(210, 230, 255); // Soft blue-white for labels
        } else {
            // LIGHT MODE
            PRIMARY = new Color(67, 97, 238); // Stronger Blue
            ACCENT = new Color(115, 103, 240); // Purple

            GLASS_PANEL_BG = new Color(255, 255, 255, 180); // White glass
            GLASS_PANEL_LIGHT = new Color(255, 255, 255, 100);
            GLASS_BORDER = new Color(0, 0, 0, 20); // Subtle dark border
            GLASS_SHADOW = new Color(0, 0, 0, 30);

            // Light Gradient (Soft Blue/White)
            GRADIENT_START = new Color(240, 244, 255);
            GRADIENT_MID = new Color(225, 235, 255);
            GRADIENT_END = new Color(210, 225, 255);

            ACCENT_GLOW = new Color(67, 97, 238, 50);

            // Sidebar Colors - Light Mode
            SIDEBAR_BG = new Color(255, 255, 255, 220); // Light glass
            SIDEBAR_ITEM_HOVER = new Color(67, 97, 238, 15); // Subtle purple glow
            SIDEBAR_ITEM_ACTIVE = new Color(67, 97, 238, 30); // Active state

            TEXT_WHITE = new Color(40, 40, 50); // In light mode, "primary text" is dark
            TEXT_GRAY = new Color(100, 100, 110);
            TEXT_DARK = new Color(240, 240, 240); // In light mode, "inverse text" is light
            FORM_LABEL_COLOR = new Color(45, 60, 90); // Dark readable label color
        }
    }
}
