package edu.univ.erp.ui.admin;

import edu.univ.erp.api.admin.AdminApi;
import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.MessageDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing sections
 */
public class SectionManagementPanel extends JPanel {
    private final AdminApi adminApi;

    private JTable sectionsTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton assignInstructorButton;
    private JButton refreshButton;

    public SectionManagementPanel() {
        this.adminApi = new AdminApi();
        initComponents();
        loadSections();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel titleLabel = new JLabel("Section Management");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = { "Section ID", "Course Code", "Section", "Instructor", "Day/Time",
                "Room", "Semester", "Year", "Capacity", "Enrolled" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        sectionsTable = new JTable(tableModel);
        sectionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        sectionsTable.setRowHeight(25);
        sectionsTable.getTableHeader().setReorderingAllowed(false);

        // Hide Section ID column
        sectionsTable.getColumnModel().getColumn(0).setMinWidth(0);
        sectionsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        sectionsTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(sectionsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadSections());
        buttonPanel.add(refreshButton);

        addButton = new JButton("Add New Section");
        addButton.setFont(new Font("Dialog", Font.BOLD, 13));
        addButton.addActionListener(e -> showAddSectionDialog());
        buttonPanel.add(addButton);

        assignInstructorButton = new JButton("Assign Instructor");
        assignInstructorButton.setFont(new Font("Dialog", Font.BOLD, 13));
        assignInstructorButton.addActionListener(e -> assignInstructor());
        assignInstructorButton.setEnabled(false);
        buttonPanel.add(assignInstructorButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Enable assign button when row is selected
        sectionsTable.getSelectionModel().addListSelectionListener(e -> {
            assignInstructorButton.setEnabled(sectionsTable.getSelectedRow() != -1);
        });
    }

    private void loadSections() {
        // Clear table
        tableModel.setRowCount(0);

        // Load sections
        List<Section> sections = adminApi.getAllSections();

        for (Section section : sections) {
            Object[] row = {
                    section.getSectionId(),
                    section.getCourseCode(),
                    section.getSectionNumber(),
                    section.getInstructorName() != null ? section.getInstructorName() : "Not Assigned",
                    section.getDayTime(),
                    section.getRoom(),
                    section.getSemester(),
                    section.getYear(),
                    section.getCapacity(),
                    section.getEnrolledCount()
            };
            tableModel.addRow(row);
        }

        if (sections.isEmpty()) {
            MessageDialog.showInfo(this, "No sections found");
        }
    }

    private void showAddSectionDialog() {
        List<Course> courses = adminApi.getAllCourses();

        if (courses.isEmpty()) {
            MessageDialog.showError(this, "No courses available. Please create courses first.");
            return;
        }

        AddSectionDialog dialog = new AddSectionDialog((Frame) SwingUtilities.getWindowAncestor(this), adminApi,
                courses);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            loadSections();
        }
    }

    private void assignInstructor() {
        int selectedRow = sectionsTable.getSelectedRow();
        if (selectedRow == -1) {
            MessageDialog.showWarning(this, "Please select a section");
            return;
        }

        // Convert view row to model row
        int modelRow = sectionsTable.convertRowIndexToModel(selectedRow);
        int sectionId = (Integer) tableModel.getValueAt(modelRow, 0);
        String courseCode = (String) tableModel.getValueAt(modelRow, 1);
        String sectionNumber = (String) tableModel.getValueAt(modelRow, 2);

        // Get list of instructors
        List<Instructor> instructors = adminApi.getAllInstructors();

        if (instructors.isEmpty()) {
            MessageDialog.showError(this, "No instructors available");
            return;
        }

        // Show selection dialog
        Instructor selected = (Instructor) JOptionPane.showInputDialog(
                this,
                "Select instructor for " + courseCode + "-" + sectionNumber + ":",
                "Assign Instructor",
                JOptionPane.QUESTION_MESSAGE,
                null,
                instructors.toArray(),
                instructors.get(0));

        if (selected != null) {
            AdminService.AssignInstructorResult result = adminApi.assignInstructor(sectionId,
                    selected.getInstructorId());

            if (result.isSuccess()) {
                MessageDialog.showSuccess(this, result.getMessage());
                loadSections();
            } else {
                MessageDialog.showError(this, result.getMessage());
            }
        }
    }

    /**
     * Refresh the sections
     */
    public void refresh() {
        loadSections();
    }
}
