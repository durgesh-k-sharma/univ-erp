package edu.univ.erp.ui.instructor;

import edu.univ.erp.api.instructor.InstructorApi;
import edu.univ.erp.service.InstructorService;
import edu.univ.erp.ui.common.MessageDialog;

import javax.swing.*;
import java.awt.*;

/**
 * Dialog for entering grades
 */
public class GradeEntryDialog extends JDialog {
    private final InstructorApi instructorApi;
    private final int enrollmentId;
    private final String studentName;

    private JComboBox<String> componentCombo;
    private JTextField scoreField;
    private JTextField maxScoreField;
    private JTextField weightField;
    private JButton saveButton;
    private JButton cancelButton;

    public GradeEntryDialog(Frame parent, InstructorApi instructorApi, int enrollmentId, String studentName) {
        super(parent, "Enter Grade - " + studentName, true);
        this.instructorApi = instructorApi;
        this.enrollmentId = enrollmentId;
        this.studentName = studentName;

        initComponents();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Component
        formPanel.add(new JLabel("Component:"));
        componentCombo = new JComboBox<>(new String[] { "QUIZ", "MIDTERM", "FINAL" });
        formPanel.add(componentCombo);

        // Score
        formPanel.add(new JLabel("Score:"));
        scoreField = new JTextField();
        formPanel.add(scoreField);

        // Max Score
        formPanel.add(new JLabel("Max Score:"));
        maxScoreField = new JTextField("100");
        formPanel.add(maxScoreField);

        // Weight
        formPanel.add(new JLabel("Weight (%):"));
        weightField = new JTextField();
        // Set default weights
        componentCombo.addActionListener(e -> {
            String component = (String) componentCombo.getSelectedItem();
            switch (component) {
                case "QUIZ":
                    weightField.setText("20");
                    break;
                case "MIDTERM":
                    weightField.setText("30");
                    break;
                case "FINAL":
                    weightField.setText("50");
                    break;
            }
        });
        componentCombo.setSelectedIndex(0); // Trigger default weight
        formPanel.add(weightField);

        add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        saveButton = new JButton("Save Grade");
        saveButton.setFont(new Font("Dialog", Font.BOLD, 13));
        saveButton.addActionListener(e -> saveGrade());
        buttonPanel.add(saveButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Set preferred size
        setPreferredSize(new Dimension(400, 250));
    }

    private void saveGrade() {
        try {
            String component = (String) componentCombo.getSelectedItem();
            double score = Double.parseDouble(scoreField.getText().trim());
            double maxScore = Double.parseDouble(maxScoreField.getText().trim());
            double weight = Double.parseDouble(weightField.getText().trim());

            // Validate
            if (score < 0 || maxScore <= 0) {
                MessageDialog.showError(this, "Score and max score must be positive numbers");
                return;
            }

            if (score > maxScore) {
                MessageDialog.showError(this, "Score cannot exceed max score");
                return;
            }

            if (weight < 0 || weight > 100) {
                MessageDialog.showError(this, "Weight must be between 0 and 100");
                return;
            }

            // Save grade
            InstructorService.GradeEntryResult result = instructorApi.enterGrade(
                    enrollmentId, component, score, maxScore, weight);

            if (result.isSuccess()) {
                MessageDialog.showSuccess(this, result.getMessage());
                dispose();
            } else {
                MessageDialog.showError(this, result.getMessage());
            }

        } catch (NumberFormatException e) {
            MessageDialog.showError(this, "Please enter valid numbers for all fields");
        }
    }
}
