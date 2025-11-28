package edu.univ.erp.ui.auth;

import com.formdev.flatlaf.FlatLightLaf;
import edu.univ.erp.api.auth.AuthApi;
import edu.univ.erp.domain.User;
import edu.univ.erp.service.AuthService;
import edu.univ.erp.ui.admin.AdminDashboard;
import edu.univ.erp.ui.common.MessageDialog;
import edu.univ.erp.ui.instructor.InstructorDashboard;
import edu.univ.erp.ui.student.StudentDashboard;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Login panel for authentication
 */
public class LoginPanel extends JPanel {
    private final AuthApi authApi;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;

    public LoginPanel() {
        this.authApi = new AuthApi();
        initComponents();
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        // setBackground(new Color(240, 242, 245)); // Removed to allow theme background

        JPanel loginPanel = new JPanel(new MigLayout("wrap 2", "[right]10[grow,fill]", "[]10[]10[]20[]"));
        // loginPanel.setBackground(Color.WHITE); // Removed to allow theme background
        loginPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)));

        // Theme Selector (Top Right)
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topPanel.setOpaque(false);

        JLabel themeLabel = new JLabel("Theme:");
        // themeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JComboBox<edu.univ.erp.ui.common.ThemeManager.Theme> themeCombo = new JComboBox<>(
                edu.univ.erp.ui.common.ThemeManager.Theme.values());
        themeCombo.setSelectedItem(edu.univ.erp.ui.common.ThemeManager.getCurrentTheme());
        themeCombo.addActionListener(e -> {
            edu.univ.erp.ui.common.ThemeManager.Theme selected = (edu.univ.erp.ui.common.ThemeManager.Theme) themeCombo
                    .getSelectedItem();
            if (selected != null) {
                edu.univ.erp.ui.common.ThemeManager.applyTheme(selected);
            }
        });

        topPanel.add(themeLabel);
        topPanel.add(themeCombo);

        // Add top panel to main layout
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.insets = new Insets(10, 0, 0, 10);
        add(topPanel, gbc);

        // Title
        JLabel titleLabel = new JLabel("University ERP");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(51, 102, 204)); // University Blue
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginPanel.add(titleLabel, "span 2,center,gapbottom 10");

        // Subtitle
        JLabel subtitleLabel = new JLabel("Academic Management System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(UIManager.getColor("Label.disabledForeground"));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginPanel.add(subtitleLabel, "span 2,center,gapbottom 30");

        // Username
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();
        usernameField.putClientProperty("JTextField.placeholderText", "Enter username");
        usernameField.setFont(new Font("Dialog", Font.PLAIN, 14));
        loginPanel.add(usernameLabel);
        loginPanel.add(usernameField);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        passwordField.putClientProperty("JTextField.placeholderText", "Enter password");
        passwordField.setFont(new Font("Dialog", Font.PLAIN, 14));
        loginPanel.add(passwordLabel);
        loginPanel.add(passwordField);

        // Status label
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginPanel.add(statusLabel, "span 2,center,gaptop 10");

        // Login button
        loginButton = new JButton("Login");
        loginButton.setFont(new Font("Dialog", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(150, 35));
        loginButton.addActionListener(e -> performLogin());
        loginPanel.add(loginButton, "span 2,center,gaptop 10");

        // Test accounts info
        JLabel infoLabel = new JLabel("<html><center><i>Test Accounts:</i><br>" +
                "admin1 / inst1 / stu1<br>" +
                "Password: password123</center></html>");
        infoLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
        infoLabel.setForeground(UIManager.getColor("Label.disabledForeground"));
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginPanel.add(infoLabel, "span 2,center,gaptop 20");

        // Add enter key listener
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        usernameField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);

        // Center the login panel
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;
        add(loginPanel, gbc);
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty()) {
            statusLabel.setText("Please enter username");
            usernameField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            statusLabel.setText("Please enter password");
            passwordField.requestFocus();
            return;
        }

        // Disable login button during authentication
        loginButton.setEnabled(false);
        statusLabel.setText("Authenticating...");
        statusLabel.setForeground(Color.BLUE);

        // Perform login in background
        SwingWorker<AuthService.LoginResult, Void> worker = new SwingWorker<>() {
            @Override
            protected AuthService.LoginResult doInBackground() {
                return authApi.login(username, password);
            }

            @Override
            protected void done() {
                try {
                    AuthService.LoginResult result = get();

                    if (result.isSuccess()) {
                        statusLabel.setText("Login successful!");
                        statusLabel.setForeground(new Color(0, 150, 0));

                        // Open appropriate dashboard
                        SwingUtilities.invokeLater(() -> openDashboard(result.getUser()));
                    } else {
                        statusLabel.setText(result.getMessage());
                        statusLabel.setForeground(Color.RED);
                        passwordField.setText("");
                        passwordField.requestFocus();
                        loginButton.setEnabled(true);
                    }
                } catch (Exception e) {
                    statusLabel.setText("Login failed: " + e.getMessage());
                    statusLabel.setForeground(Color.RED);
                    loginButton.setEnabled(true);
                }
            }
        };

        worker.execute();
    }

    private void openDashboard(User user) {
        // Get the main frame
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);

        // Create appropriate dashboard based on role
        JFrame dashboard;
        switch (user.getRole()) {
            case ADMIN:
                dashboard = new AdminDashboard();
                break;
            case INSTRUCTOR:
                dashboard = new InstructorDashboard();
                break;
            case STUDENT:
                dashboard = new StudentDashboard();
                break;
            default:
                MessageDialog.showError(this, "Unknown user role: " + user.getRole());
                return;
        }

        // Close login window and show dashboard
        dashboard.setVisible(true);
        if (frame != null) {
            frame.dispose();
        }
    }

    /**
     * Create and show login window
     */
    public static void showLoginWindow() {
        SwingUtilities.invokeLater(() -> {
            // Theme is already initialized in Main

            JFrame frame = new JFrame("University ERP - Login");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new LoginPanel());
            frame.setSize(500, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
