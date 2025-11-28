package edu.univ.erp.ui.admin;

import edu.univ.erp.api.admin.AdminApi;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.MessageDialog;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for adding a new instructor
 */
public class AddInstructorDialog extends JDialog {
    private final AdminApi adminApi;
    private boolean success = false;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField employeeIdField;
    private JTextField departmentField;
    private JTextField emailField;
    private JTextField phoneField;

    public AddInstructorDialog(Frame parent, AdminApi adminApi) {
        super(parent, "Add New Instructor", true);
        this.adminApi = adminApi;

        initComponents();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        formPanel.add(new JLabel("Employee ID:"));
        employeeIdField = new JTextField();
        formPanel.add(employeeIdField);

        formPanel.add(new JLabel("Department:"));
        departmentField = new JTextField();
        formPanel.add(departmentField);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        formPanel.add(new JLabel("Phone:"));
        phoneField = new JTextField();
        formPanel.add(phoneField);

        add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        JButton saveButton = new JButton("Create Instructor");
        saveButton.setFont(new Font("Dialog", Font.BOLD, 13));
        saveButton.addActionListener(e -> createInstructor());
        buttonPanel.add(saveButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setPreferredSize(new Dimension(450, 300));
    }

    private void createInstructor() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String employeeId = employeeIdField.getText().trim();
        String department = departmentField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || employeeId.isEmpty() || department.isEmpty()) {
            MessageDialog.showError(this,
                    "Please fill in all required fields (Username, Password, Employee ID, Department)");
            return;
        }

        AdminService.CreateUserResult result = adminApi.createInstructor(
                username, password, employeeId, department,
                email.isEmpty() ? null : email,
                phone.isEmpty() ? null : phone);

        if (result.isSuccess()) {
            MessageDialog.showSuccess(this, result.getMessage());
            success = true;
            dispose();
        } else {
            MessageDialog.showError(this, result.getMessage());
        }
    }

    public boolean isSuccess() {
        return success;
    }
}
