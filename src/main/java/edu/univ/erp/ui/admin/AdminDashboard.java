package edu.univ.erp.ui.admin;

import edu.univ.erp.api.admin.AdminApi;
import edu.univ.erp.api.auth.AuthApi;
import edu.univ.erp.api.maintenance.MaintenanceApi;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.auth.LoginPanel;
import edu.univ.erp.ui.common.MaintenanceBanner;
import edu.univ.erp.ui.common.MessageDialog;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * Main dashboard for admin users
 */
public class AdminDashboard extends JFrame {
    private final AdminApi adminApi;
    private final AuthApi authApi;
    private final MaintenanceApi maintenanceApi;

    private JPanel contentPanel;
    private JPanel maintenanceBannerPanel;
    private CardLayout cardLayout;

    public AdminDashboard() {
        this.adminApi = new AdminApi();
        this.authApi = new AuthApi();
        this.maintenanceApi = new MaintenanceApi();

        initComponents();
        checkMaintenanceMode();
    }

    private void initComponents() {
        setTitle("University ERP - Admin Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Maintenance banner panel
        maintenanceBannerPanel = new JPanel(new BorderLayout());
        add(maintenanceBannerPanel, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Content area
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        contentPanel.add(createWelcomePanel(), "welcome");
        contentPanel.add(new UserManagementPanel(), "users");
        contentPanel.add(new CourseManagementPanel(), "courses");
        contentPanel.add(new SectionManagementPanel(), "sections");
        contentPanel.add(new BackupRestorePanel(adminApi), "backup");
        contentPanel.add(createMaintenanceModePanel(), "maintenance");

        add(contentPanel, BorderLayout.CENTER);

        cardLayout.show(contentPanel, "welcome");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(44, 62, 80));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        JLabel headerLabel = new JLabel("Admin Portal");
        headerLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(headerLabel);

        sidebar.add(Box.createVerticalStrut(10));

        JLabel userLabel = new JLabel(SessionManager.getCurrentUsername());
        userLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        userLabel.setForeground(Color.LIGHT_GRAY);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(userLabel);

        sidebar.add(Box.createVerticalStrut(30));

        addMenuButton(sidebar, "🏠 Home", "welcome");
        addMenuButton(sidebar, "👥 User Management", "users");
        addMenuButton(sidebar, "📚 Course Management", "courses");
        addMenuButton(sidebar, "📝 Section Management", "sections");
        addMenuButton(sidebar, "💾 Backup & Restore", "backup");
        addMenuButton(sidebar, "⚙️ Maintenance Mode", "maintenance");

        sidebar.add(Box.createVerticalGlue());

        JButton changePasswordButton = createSidebarButton("🔒 Change Password");
        changePasswordButton.addActionListener(e -> showChangePasswordDialog());
        sidebar.add(changePasswordButton);

        sidebar.add(Box.createVerticalStrut(5));

        JButton logoutButton = createSidebarButton("🚪 Logout");
        logoutButton.addActionListener(e -> logout());
        sidebar.add(logoutButton);

        return sidebar;
    }

    private void addMenuButton(JPanel sidebar, String text, String panelName) {
        JButton button = createSidebarButton(text);
        button.addActionListener(e -> {
            cardLayout.show(contentPanel, panelName);
            if ("maintenance".equals(panelName)) {
                refreshMaintenancePanel();
            }
        });
        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(5));
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setFont(new Font("Dialog", Font.PLAIN, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(44, 62, 80));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(84, 102, 120));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(44, 62, 80));
            }
        });

        return button;
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new MigLayout("fillx,wrap 1", "[grow,fill]", "[]20[]"));

        JLabel titleLabel = new JLabel("Welcome to Admin Portal");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        panel.add(titleLabel);

        JLabel instructionLabel = new JLabel(
                "<html><p style='margin-top:20px;'>As an administrator, you have full access to:</p>" +
                        "<ul>" +
                        "<li><b>User Management</b> - Create students, instructors, and admins</li>" +
                        "<li><b>Course Management</b> - Create and manage courses</li>" +
                        "<li><b>Section Management</b> - Create sections and assign instructors</li>" +
                        "<li><b>Maintenance Mode</b> - Toggle system-wide read-only mode</li>" +
                        "</ul></html>");
        panel.add(instructionLabel, "grow");

        return panel;
    }

    private JPanel createMaintenanceModePanel() {
        JPanel panel = new JPanel(new MigLayout("fillx,wrap 1", "[grow,fill]", "[]20[]"));

        JLabel titleLabel = new JLabel("Maintenance Mode");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        panel.add(titleLabel);

        JPanel controlPanel = new JPanel(new MigLayout("fillx,wrap 2", "[][grow,fill]"));
        controlPanel.setBorder(BorderFactory.createTitledBorder("System Maintenance"));

        JLabel statusLabel = new JLabel("Current Status:");
        statusLabel.setFont(new Font("Dialog", Font.BOLD, 12));
        controlPanel.add(statusLabel);

        JLabel statusValueLabel = new JLabel();
        statusValueLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        controlPanel.add(statusValueLabel, "id statusValue");

        JLabel descLabel = new JLabel(
                "<html><p style='margin-top:10px;'>When maintenance mode is enabled, students and instructors " +
                        "can view data but cannot make any changes. Only administrators can modify data.</p></html>");
        controlPanel.add(descLabel, "span 2,gaptop 10");

        JButton toggleButton = new JButton();
        toggleButton.setFont(new Font("Dialog", Font.BOLD, 14));
        toggleButton.addActionListener(e -> toggleMaintenanceMode(toggleButton, statusValueLabel));
        controlPanel.add(toggleButton, "span 2,center,gaptop 20,id toggleBtn");

        panel.add(controlPanel, "grow");

        // Store components for later access
        panel.putClientProperty("statusLabel", statusValueLabel);
        panel.putClientProperty("toggleButton", toggleButton);

        return panel;
    }

    private void refreshMaintenancePanel() {
        // Find the maintenance panel
        for (Component comp : contentPanel.getComponents()) {
            if (comp instanceof JPanel) {
                JLabel statusLabel = (JLabel) ((JPanel) comp).getClientProperty("statusLabel");
                JButton toggleButton = (JButton) ((JPanel) comp).getClientProperty("toggleButton");

                if (statusLabel != null && toggleButton != null) {
                    boolean isMaintenanceMode = maintenanceApi.isMaintenanceMode();
                    boolean isDark = edu.univ.erp.ui.common.ThemeManager
                            .getCurrentTheme() == edu.univ.erp.ui.common.ThemeManager.Theme.DARK;

                    if (isMaintenanceMode) {
                        statusLabel.setText("ENABLED (Read-Only Mode)");
                        // Brighter red for dark mode text
                        statusLabel.setForeground(isDark ? new Color(255, 80, 80) : new Color(200, 0, 0));

                        toggleButton.setText("Disable Maintenance Mode");
                        // Green button
                        toggleButton.setBackground(new Color(0, 150, 0));
                        toggleButton.setForeground(Color.WHITE);
                    } else {
                        statusLabel.setText("DISABLED (Normal Operation)");
                        // Brighter green for dark mode text
                        statusLabel.setForeground(isDark ? new Color(80, 200, 80) : new Color(0, 150, 0));

                        toggleButton.setText("Enable Maintenance Mode");
                        // Red button
                        toggleButton.setBackground(new Color(200, 0, 0));
                        toggleButton.setForeground(Color.WHITE);
                    }
                    break;
                }
            }
        }
    }

    private void toggleMaintenanceMode(JButton button, JLabel statusLabel) {
        boolean currentMode = maintenanceApi.isMaintenanceMode();
        String action = currentMode ? "disable" : "enable";

        boolean confirm = MessageDialog.showConfirm(this,
                "Are you sure you want to " + action + " maintenance mode?\n\n" +
                        (currentMode ? "Students and instructors will be able to make changes again."
                                : "Students and instructors will only be able to view data."));

        if (confirm) {
            AdminService.MaintenanceModeResult result = maintenanceApi.toggleMaintenanceMode(!currentMode);

            if (result.isSuccess()) {
                MessageDialog.showSuccess(this, result.getMessage());
                refreshMaintenancePanel();
                checkMaintenanceMode();
            } else {
                MessageDialog.showError(this, result.getMessage());
            }
        }
    }

    private void checkMaintenanceMode() {
        maintenanceBannerPanel.removeAll();
        if (maintenanceApi.isMaintenanceMode()) {
            MaintenanceBanner.addTo(maintenanceBannerPanel);
        }
        maintenanceBannerPanel.revalidate();
        maintenanceBannerPanel.repaint();
    }

    private void showChangePasswordDialog() {
        edu.univ.erp.ui.common.ChangePasswordDialog dialog = new edu.univ.erp.ui.common.ChangePasswordDialog(this);
        dialog.setVisible(true);
    }

    private void logout() {
        boolean confirm = MessageDialog.showConfirm(this, "Are you sure you want to logout?");
        if (confirm) {
            authApi.logout();
            dispose();
            LoginPanel.showLoginWindow();
        }
    }
}
