package edu.univ.erp.ui.admin;

import edu.univ.erp.api.admin.AdminApi;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.MessageDialog;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for adding a new student
 */
public class AddStudentDialog extends JDialog {
    private final AdminApi adminApi;
    private boolean success = false;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField rollNoField;
    private JTextField programField;
    private JSpinner yearSpinner;
    private JTextField emailField;
    private JTextField phoneField;

    public AddStudentDialog(Frame parent, AdminApi adminApi) {
        super(parent, "Add New Student", true);
        this.adminApi = adminApi;

        initComponents();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        formPanel.add(usernameField);

        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        formPanel.add(new JLabel("Roll Number:"));
        rollNoField = new JTextField();
        formPanel.add(rollNoField);

        formPanel.add(new JLabel("Program:"));
        programField = new JTextField();
        formPanel.add(programField);

        formPanel.add(new JLabel("Year:"));
        yearSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
        formPanel.add(yearSpinner);

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

        JButton saveButton = new JButton("Create Student");
        saveButton.setFont(new Font("Dialog", Font.BOLD, 13));
        saveButton.addActionListener(e -> createStudent());
        buttonPanel.add(saveButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setPreferredSize(new Dimension(450, 350));
    }

    private void createStudent() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String rollNo = rollNoField.getText().trim();
        String program = programField.getText().trim();
        int year = (Integer) yearSpinner.getValue();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || rollNo.isEmpty() || program.isEmpty()) {
            MessageDialog.showError(this, "Please fill in all required fields (Username, Password, Roll No, Program)");
            return;
        }

        AdminService.CreateUserResult result = adminApi.createStudent(
                username, password, rollNo, program, year,
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
