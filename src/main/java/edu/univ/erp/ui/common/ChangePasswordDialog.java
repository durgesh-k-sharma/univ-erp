package edu.univ.erp.ui.common;

import edu.univ.erp.service.AuthService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for changing user password
 */
public class ChangePasswordDialog extends JDialog {
    private final AuthService authService;
    private boolean success = false;

    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JLabel statusLabel;
    private JButton changeButton;
    private JButton cancelButton;

    public ChangePasswordDialog(Frame owner) {
        super(owner, "Change Password", true);
        this.authService = new AuthService();
        initComponents();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new MigLayout("fillx, wrap 2", "[right]10[grow,fill]", "[]10[]10[]10[]"));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Current Password
        formPanel.add(new JLabel("Current Password:"));
        currentPasswordField = new JPasswordField(20);
        formPanel.add(currentPasswordField);

        // New Password
        formPanel.add(new JLabel("New Password:"));
        newPasswordField = new JPasswordField(20);
        formPanel.add(newPasswordField);

        // Confirm Password
        formPanel.add(new JLabel("Confirm Password:"));
        confirmPasswordField = new JPasswordField(20);
        formPanel.add(confirmPasswordField);

        // Status Label
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        formPanel.add(statusLabel, "span 2, center");

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        changeButton = new JButton("Change Password");
        changeButton.addActionListener(e -> changePassword());
        buttonPanel.add(changeButton);

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void changePassword() {
        String currentPass = new String(currentPasswordField.getPassword());
        String newPass = new String(newPasswordField.getPassword());
        String confirmPass = new String(confirmPasswordField.getPassword());

        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            statusLabel.setText("All fields are required");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            statusLabel.setText("New passwords do not match");
            return;
        }

        changeButton.setEnabled(false);
        statusLabel.setText("Updating...");
        statusLabel.setForeground(Color.BLUE);

        SwingWorker<AuthService.ChangePasswordResult, Void> worker = new SwingWorker<>() {
            @Override
            protected AuthService.ChangePasswordResult doInBackground() {
                return authService.changePassword(currentPass, newPass, confirmPass);
            }

            @Override
            protected void done() {
                try {
                    AuthService.ChangePasswordResult result = get();
                    if (result.isSuccess()) {
                        success = true;
                        MessageDialog.showSuccess(ChangePasswordDialog.this, result.getMessage());
                        dispose();
                    } else {
                        statusLabel.setText(result.getMessage());
                        statusLabel.setForeground(Color.RED);
                        changeButton.setEnabled(true);
                    }
                } catch (Exception e) {
                    statusLabel.setText("Error: " + e.getMessage());
                    statusLabel.setForeground(Color.RED);
                    changeButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    public boolean isSuccess() {
        return success;
    }
}
