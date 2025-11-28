package edu.univ.erp.ui.common;

import javax.swing.*;
import java.awt.*;

/**
 * Common UI theme constants and utilities
 */
public class ThemeManager {
    // Fonts
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font SUBHEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font REGULAR_FONT = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font BOLD_FONT = new Font("Segoe UI", Font.BOLD, 12);

    // Colors
    public static final Color PRIMARY_COLOR = new Color(52, 152, 219);
    public static final Color SECONDARY_COLOR = new Color(44, 62, 80);
    public static final Color SUCCESS_COLOR = new Color(46, 204, 113);
    public static final Color DANGER_COLOR = new Color(231, 76, 60);
    public static final Color WARNING_COLOR = new Color(241, 196, 15);
    public static final Color TEXT_COLOR = new Color(44, 62, 80);
    public static final Color BACKGROUND_COLOR = new Color(236, 240, 241);

    // Theme Management
    private static final String PREF_THEME = "app_theme";
    private static final java.util.prefs.Preferences prefs = java.util.prefs.Preferences
            .userNodeForPackage(ThemeManager.class);

    public enum Theme {
        LIGHT("Light", "com.formdev.flatlaf.themes.FlatMacLightLaf"),
        DARK("Dark", "com.formdev.flatlaf.themes.FlatMacDarkLaf");

        private final String displayName;
        private final String className;

        Theme(String displayName, String className) {
            this.displayName = displayName;
            this.className = className;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getClassName() {
            return className;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    public static void initialize() {
        String savedThemeName = prefs.get(PREF_THEME, Theme.LIGHT.name());
        Theme theme;
        try {
            theme = Theme.valueOf(savedThemeName);
        } catch (IllegalArgumentException e) {
            theme = Theme.LIGHT;
        }
        applyTheme(theme);
    }

    public static void applyTheme(Theme theme) {
        try {
            UIManager.setLookAndFeel(theme.getClassName());

            // Update UI for all open windows
            for (Window window : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(window);
            }

            saveTheme(theme);
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback to default if failed
            try {
                UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static void saveTheme(Theme theme) {
        prefs.put(PREF_THEME, theme.name());
    }

    public static Theme getCurrentTheme() {
        String savedThemeName = prefs.get(PREF_THEME, Theme.LIGHT.name());
        try {
            return Theme.valueOf(savedThemeName);
        } catch (IllegalArgumentException e) {
            return Theme.LIGHT;
        }
    }
}
