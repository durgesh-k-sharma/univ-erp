package edu.univ.erp.ui.admin;

import edu.univ.erp.api.admin.AdminApi;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.MessageDialog;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for adding a new course
 */
public class AddCourseDialog extends JDialog {
    private final AdminApi adminApi;
    private boolean success = false;

    private JTextField courseCodeField;
    private JTextField titleField;
    private JSpinner creditsSpinner;
    private JTextArea descriptionArea;
    private JTextField prerequisitesField;

    public AddCourseDialog(Frame parent, AdminApi adminApi) {
        super(parent, "Add New Course", true);
        this.adminApi = adminApi;

        initComponents();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Course Code
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Course Code:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        courseCodeField = new JTextField(20);
        formPanel.add(courseCodeField, gbc);

        // Title
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Title:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        titleField = new JTextField(20);
        formPanel.add(titleField, gbc);

        // Credits
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        formPanel.add(new JLabel("Credits:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        creditsSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 6, 1));
        formPanel.add(creditsSpinner, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTH;
        formPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        formPanel.add(scrollPane, gbc);

        // Prerequisites
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(new JLabel("Prerequisites:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        prerequisitesField = new JTextField(20);
        prerequisitesField.setToolTipText("Comma-separated course codes (e.g. CS101, MATH101)");
        formPanel.add(prerequisitesField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        JButton saveButton = new JButton("Create Course");
        saveButton.setFont(new Font("Dialog", Font.BOLD, 13));
        saveButton.addActionListener(e -> createCourse());
        buttonPanel.add(saveButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setPreferredSize(new Dimension(450, 300));
    }

    private void createCourse() {
        String courseCode = courseCodeField.getText().trim();
        String title = titleField.getText().trim();
        int credits = (Integer) creditsSpinner.getValue();
        String description = descriptionArea.getText().trim();
        String prerequisites = prerequisitesField.getText().trim();

        if (courseCode.isEmpty() || title.isEmpty()) {
            MessageDialog.showError(this, "Please fill in Course Code and Title");
            return;
        }

        AdminService.CreateCourseResult result = adminApi.createCourse(
                courseCode, title, credits,
                description.isEmpty() ? null : description,
                prerequisites.isEmpty() ? null : prerequisites);

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
