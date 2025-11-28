package edu.univ.erp.ui.common;

import javax.swing.*;
import java.awt.*;

/**
 * Banner to display when system is in maintenance mode
 */
public class MaintenanceBanner extends JPanel {

    public MaintenanceBanner() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        // Colors are now handled by updateColors() called via updateUI()

        JLabel iconLabel = new JLabel("⚠");
        iconLabel.setFont(new Font("Dialog", Font.BOLD, 20));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        add(iconLabel, BorderLayout.WEST);

        JLabel messageLabel = new JLabel(
                "<html><b>MAINTENANCE MODE</b> - System is in read-only mode. You can view data but cannot make changes.</html>");
        messageLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        add(messageLabel, BorderLayout.CENTER);

        // Initial update to ensure colors are set if updateUI ran before this
        updateColors();
    }

    @Override
    public void updateUI() {
        super.updateUI();
        updateColors();
    }

    private void updateColors() {
        ThemeManager.Theme currentTheme = ThemeManager.getCurrentTheme();

        if (currentTheme == ThemeManager.Theme.DARK) {
            // Darker amber for dark mode to ensure white text is readable
            setBackground(new Color(100, 80, 0));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(2, 0, 2, 0, new Color(160, 120, 0)),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        } else {
            // Original bright yellow for light mode
            setBackground(new Color(255, 200, 0));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(2, 0, 2, 0, new Color(200, 150, 0)),
                    BorderFactory.createEmptyBorder(8, 15, 8, 15)));
        }
    }

    /**
     * Create and add maintenance banner to a container
     */
    public static MaintenanceBanner addTo(Container container) {
        MaintenanceBanner banner = new MaintenanceBanner();
        container.add(banner, BorderLayout.NORTH);
        return banner;
    }
}
