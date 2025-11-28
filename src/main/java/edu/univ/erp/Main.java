package edu.univ.erp;

import com.formdev.flatlaf.FlatLightLaf;
import edu.univ.erp.data.DatabaseManager;
import edu.univ.erp.ui.auth.LoginPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * Main entry point for the University ERP application
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Starting University ERP System...");

        // Initialize Theme
        try {
            edu.univ.erp.ui.common.ThemeManager.initialize();
            logger.info("Theme initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize theme", e);
        }

        // Test database connections
        logger.info("Testing database connections...");
        if (!DatabaseManager.testConnections()) {
            logger.error("Database connection test failed!");
            JOptionPane.showMessageDialog(null,
                    "Failed to connect to databases.\nPlease check your database configuration and ensure MySQL is running.",
                    "Database Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        logger.info("Database connections successful!");

        // Launch the login window
        SwingUtilities.invokeLater(() -> {
            try {
                LoginPanel.showLoginWindow();
                logger.info("Login window displayed");
            } catch (Exception e) {
                logger.error("Error showing login window", e);
                JOptionPane.showMessageDialog(null,
                        "Error initializing application: " + e.getMessage(),
                        "Initialization Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Add shutdown hook to close database connections
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down University ERP System...");
            DatabaseManager.shutdown();
            logger.info("Shutdown complete");
        }));
    }
}
