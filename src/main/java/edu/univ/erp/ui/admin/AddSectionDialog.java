package edu.univ.erp.ui.admin;

import edu.univ.erp.api.admin.AdminApi;
import edu.univ.erp.domain.Course;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.MessageDialog;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Dialog for adding a new section
 */
public class AddSectionDialog extends JDialog {
    private final AdminApi adminApi;
    private final List<Course> courses;
    private boolean success = false;

    private JComboBox<Course> courseCombo;
    private JTextField sectionNumberField;
    private JComboBox<String> semesterCombo;
    private JSpinner yearSpinner;
    private JTextField dayTimeField;
    private JTextField roomField;
    private JSpinner capacitySpinner;

    public AddSectionDialog(Frame parent, AdminApi adminApi, List<Course> courses) {
        super(parent, "Add New Section", true);
        this.adminApi = adminApi;
        this.courses = courses;

        initComponents();
        pack();
        setLocationRelativeTo(parent);
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        // Course
        formPanel.add(new JLabel("Course:"));
        courseCombo = new JComboBox<>(courses.toArray(new Course[0]));
        formPanel.add(courseCombo);

        // Section Number
        formPanel.add(new JLabel("Section Number:"));
        sectionNumberField = new JTextField();
        formPanel.add(sectionNumberField);

        // Semester
        formPanel.add(new JLabel("Semester:"));
        semesterCombo = new JComboBox<>(new String[] { "FALL", "SPRING", "SUMMER" });
        semesterCombo.setSelectedItem(getCurrentSemester());
        formPanel.add(semesterCombo);

        // Year
        formPanel.add(new JLabel("Year:"));
        int currentYear = LocalDate.now().getYear();
        yearSpinner = new JSpinner(new SpinnerNumberModel(currentYear, currentYear, currentYear + 2, 1));
        formPanel.add(yearSpinner);

        // Day/Time
        formPanel.add(new JLabel("Day/Time:"));
        dayTimeField = new JTextField();
        formPanel.add(dayTimeField);

        // Room
        formPanel.add(new JLabel("Room:"));
        roomField = new JTextField();
        formPanel.add(roomField);

        // Capacity
        formPanel.add(new JLabel("Capacity:"));
        capacitySpinner = new JSpinner(new SpinnerNumberModel(30, 1, 200, 5));
        formPanel.add(capacitySpinner);

        add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dispose());
        buttonPanel.add(cancelButton);

        JButton saveButton = new JButton("Create Section");
        saveButton.setFont(new Font("Dialog", Font.BOLD, 13));
        saveButton.addActionListener(e -> createSection());
        buttonPanel.add(saveButton);

        add(buttonPanel, BorderLayout.SOUTH);

        setPreferredSize(new Dimension(450, 350));
    }

    private String getCurrentSemester() {
        int month = LocalDate.now().getMonthValue();
        if (month >= 1 && month <= 5)
            return "SPRING";
        if (month >= 6 && month <= 8)
            return "SUMMER";
        return "FALL";
    }

    private void createSection() {
        Course selectedCourse = (Course) courseCombo.getSelectedItem();
        String sectionNumber = sectionNumberField.getText().trim();
        String semester = (String) semesterCombo.getSelectedItem();
        int year = (Integer) yearSpinner.getValue();
        String dayTime = dayTimeField.getText().trim();
        String room = roomField.getText().trim();
        int capacity = (Integer) capacitySpinner.getValue();

        if (selectedCourse == null || sectionNumber.isEmpty() || dayTime.isEmpty() || room.isEmpty()) {
            MessageDialog.showError(this, "Please fill in all fields");
            return;
        }

        AdminService.CreateSectionResult result = adminApi.createSection(
                selectedCourse.getCourseId(), sectionNumber, dayTime, room, capacity, semester, year);

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
