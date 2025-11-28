package edu.univ.erp.ui.admin;

import edu.univ.erp.api.admin.AdminApi;
import edu.univ.erp.domain.Course;
import edu.univ.erp.ui.common.MessageDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing courses
 */
public class CourseManagementPanel extends JPanel {
    private final AdminApi adminApi;

    private JTable coursesTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton refreshButton;

    public CourseManagementPanel() {
        this.adminApi = new AdminApi();
        initComponents();
        loadCourses();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel titleLabel = new JLabel("Course Management");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = { "Course ID", "Course Code", "Course Title", "Credits", "Description" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        coursesTable = new JTable(tableModel);
        coursesTable.setRowHeight(25);
        coursesTable.getTableHeader().setReorderingAllowed(false);

        // Hide Course ID column
        coursesTable.getColumnModel().getColumn(0).setMinWidth(0);
        coursesTable.getColumnModel().getColumn(0).setMaxWidth(0);
        coursesTable.getColumnModel().getColumn(0).setWidth(0);

        // Set column widths
        coursesTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Course Code
        coursesTable.getColumnModel().getColumn(2).setPreferredWidth(250); // Course Title
        coursesTable.getColumnModel().getColumn(3).setPreferredWidth(60); // Credits
        coursesTable.getColumnModel().getColumn(4).setPreferredWidth(300); // Description

        JScrollPane scrollPane = new JScrollPane(coursesTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadCourses());
        buttonPanel.add(refreshButton);

        addButton = new JButton("Add New Course");
        addButton.setFont(new Font("Dialog", Font.BOLD, 13));
        addButton.addActionListener(e -> showAddCourseDialog());
        buttonPanel.add(addButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadCourses() {
        // Clear table
        tableModel.setRowCount(0);

        // Load courses
        List<Course> courses = adminApi.getAllCourses();

        for (Course course : courses) {
            Object[] row = {
                    course.getCourseId(),
                    course.getCourseCode(),
                    course.getTitle(),
                    course.getCredits(),
                    course.getDescription()
            };
            tableModel.addRow(row);
        }

        if (courses.isEmpty()) {
            MessageDialog.showInfo(this, "No courses found");
        }
    }

    private void showAddCourseDialog() {
        AddCourseDialog dialog = new AddCourseDialog((Frame) SwingUtilities.getWindowAncestor(this), adminApi);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            loadCourses();
        }
    }

    /**
     * Refresh the courses
     */
    public void refresh() {
        loadCourses();
    }
}
