package edu.univ.erp.ui.student;

import edu.univ.erp.api.student.StudentApi;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.ui.common.MessageDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for viewing my registrations
 */
public class MyRegistrationsPanel extends JPanel {
    private final StudentApi studentApi;

    private JTable registrationsTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;
    private JButton dropButton;

    public MyRegistrationsPanel() {
        this.studentApi = new StudentApi();
        initComponents();
        loadRegistrations();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel titleLabel = new JLabel("My Registrations");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = { "Enrollment ID", "Course Code", "Course Title", "Section", "Instructor",
                "Day/Time", "Room", "Status", "Enrolled Date", "Drop Deadline", "Can Drop" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        registrationsTable = new JTable(tableModel);
        registrationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        registrationsTable.setRowHeight(25);
        registrationsTable.getTableHeader().setReorderingAllowed(false);

        // Hide Enrollment ID column
        registrationsTable.getColumnModel().getColumn(0).setMinWidth(0);
        registrationsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        registrationsTable.getColumnModel().getColumn(0).setWidth(0);

        // Set column widths
        registrationsTable.getColumnModel().getColumn(1).setPreferredWidth(80); // Course Code
        registrationsTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Course Title
        registrationsTable.getColumnModel().getColumn(3).setPreferredWidth(60); // Section
        registrationsTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Instructor
        registrationsTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Day/Time
        registrationsTable.getColumnModel().getColumn(6).setPreferredWidth(80); // Room
        registrationsTable.getColumnModel().getColumn(7).setPreferredWidth(80); // Status
        registrationsTable.getColumnModel().getColumn(8).setPreferredWidth(120); // Enrolled Date
        registrationsTable.getColumnModel().getColumn(9).setPreferredWidth(120); // Drop Deadline
        registrationsTable.getColumnModel().getColumn(10).setPreferredWidth(70); // Can Drop

        JScrollPane scrollPane = new JScrollPane(registrationsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadRegistrations());
        buttonPanel.add(refreshButton);

        dropButton = new JButton("Drop Selected Section");
        dropButton.setFont(new Font("Dialog", Font.BOLD, 13));
        dropButton.setForeground(new Color(200, 0, 0));
        dropButton.addActionListener(e -> dropSection());
        dropButton.setEnabled(false);
        buttonPanel.add(dropButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Enable drop button when row is selected
        registrationsTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = registrationsTable.getSelectedRow();
            if (selectedRow != -1) {
                int modelRow = registrationsTable.convertRowIndexToModel(selectedRow);
                String canDrop = (String) tableModel.getValueAt(modelRow, 10);
                dropButton.setEnabled("Yes".equals(canDrop));
            } else {
                dropButton.setEnabled(false);
            }
        });
    }

    private void loadRegistrations() {
        // Clear table
        tableModel.setRowCount(0);

        // Load registrations
        List<Enrollment> enrollments = studentApi.getMyRegistrations();

        for (Enrollment enrollment : enrollments) {
            String canDrop = enrollment.canDrop() ? "Yes" : "No";

            Object[] row = {
                    enrollment.getEnrollmentId(),
                    enrollment.getCourseCode(),
                    enrollment.getCourseTitle(),
                    enrollment.getSectionNumber(),
                    enrollment.getInstructorName() != null ? enrollment.getInstructorName() : "TBA",
                    enrollment.getDayTime(),
                    enrollment.getRoom(),
                    enrollment.getStatus(),
                    enrollment.getEnrollmentDate() != null ? enrollment.getEnrollmentDate().toLocalDate().toString()
                            : "",
                    enrollment.getDropDeadline() != null ? enrollment.getDropDeadline().toLocalDate().toString() : "",
                    canDrop
            };
            tableModel.addRow(row);
        }

        if (enrollments.isEmpty()) {
            MessageDialog.showInfo(this,
                    "You are not registered for any sections yet.\nUse the Course Catalog to register.");
        }
    }

    private void dropSection() {
        int selectedRow = registrationsTable.getSelectedRow();
        if (selectedRow == -1) {
            MessageDialog.showWarning(this, "Please select a section to drop");
            return;
        }

        // Convert view row to model row
        int modelRow = registrationsTable.convertRowIndexToModel(selectedRow);
        int enrollmentId = (Integer) tableModel.getValueAt(modelRow, 0);
        String courseCode = (String) tableModel.getValueAt(modelRow, 1);
        String sectionNumber = (String) tableModel.getValueAt(modelRow, 3);
        String canDrop = (String) tableModel.getValueAt(modelRow, 10);

        // Check if can drop
        if (!"Yes".equals(canDrop)) {
            MessageDialog.showError(this, "Drop deadline has passed for this section.");
            return;
        }

        // Confirm drop
        boolean confirm = MessageDialog.showConfirm(this,
                "Are you sure you want to drop " + courseCode + "-" + sectionNumber + "?\n\n" +
                        "This action cannot be undone.");

        if (confirm) {
            StudentService.DropResult result = studentApi.dropSection(enrollmentId);

            if (result.isSuccess()) {
                MessageDialog.showSuccess(this, result.getMessage());
                loadRegistrations(); // Refresh
            } else {
                MessageDialog.showError(this, result.getMessage());
            }
        }
    }

    /**
     * Refresh the registrations
     */
    public void refresh() {
        loadRegistrations();
    }
}
