package edu.univ.erp.ui.instructor;

import edu.univ.erp.api.instructor.InstructorApi;
import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.InstructorService;
import edu.univ.erp.ui.common.MessageDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Gradebook panel for entering and managing grades
 */
public class GradebookPanel extends JPanel {
    private final InstructorApi instructorApi;

    private JComboBox<SectionItem> sectionCombo;
    private JTable studentsTable;
    private DefaultTableModel tableModel;
    private JButton enterGradeButton;
    private JButton computeFinalButton;
    private JButton refreshButton;

    public GradebookPanel() {
        this.instructorApi = new InstructorApi();
        initComponents();
        loadSections();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel titleLabel = new JLabel("Gradebook");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Section selector panel
        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectorPanel.add(new JLabel("Select Section:"));
        sectionCombo = new JComboBox<>();
        sectionCombo.setPreferredSize(new Dimension(400, 25));
        sectionCombo.addActionListener(e -> loadStudents());
        selectorPanel.add(sectionCombo);
        add(selectorPanel, BorderLayout.NORTH);

        // Table
        String[] columns = { "Enrollment ID", "Roll No", "Student Name", "Status" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentsTable = new JTable(tableModel);
        studentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentsTable.setRowHeight(25);
        studentsTable.getTableHeader().setReorderingAllowed(false);

        // Hide Enrollment ID column
        studentsTable.getColumnModel().getColumn(0).setMinWidth(0);
        studentsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        studentsTable.getColumnModel().getColumn(0).setWidth(0);

        JScrollPane scrollPane = new JScrollPane(studentsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadStudents());
        buttonPanel.add(refreshButton);

        enterGradeButton = new JButton("Enter Grade for Selected Student");
        enterGradeButton.setFont(new Font("Dialog", Font.BOLD, 13));
        enterGradeButton.addActionListener(e -> enterGrade());
        enterGradeButton.setEnabled(false);
        buttonPanel.add(enterGradeButton);

        computeFinalButton = new JButton("Compute Final Grade");
        computeFinalButton.setForeground(new Color(0, 100, 0));
        computeFinalButton.addActionListener(e -> computeFinalGrade());
        computeFinalButton.setEnabled(false);
        buttonPanel.add(computeFinalButton);

        JButton exportButton = new JButton("Export CSV");
        exportButton.addActionListener(e -> exportGrades());
        buttonPanel.add(exportButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Enable buttons when row is selected
        studentsTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = studentsTable.getSelectedRow() != -1;
            enterGradeButton.setEnabled(hasSelection);
            computeFinalButton.setEnabled(hasSelection);
        });
    }

    private void loadSections() {
        sectionCombo.removeAllItems();
        List<Section> sections = instructorApi.getMySections();

        for (Section section : sections) {
            sectionCombo.addItem(new SectionItem(section));
        }

        if (sections.isEmpty()) {
            MessageDialog.showInfo(this, "You have no sections assigned yet.");
        }
    }

    private void loadStudents() {
        tableModel.setRowCount(0);

        SectionItem selectedItem = (SectionItem) sectionCombo.getSelectedItem();
        if (selectedItem == null) {
            return;
        }

        int sectionId = selectedItem.section.getSectionId();
        InstructorService.EnrollmentListResult result = instructorApi.getEnrolledStudents(sectionId);

        if (result.isSuccess()) {
            for (Enrollment enrollment : result.getEnrollments()) {
                Object[] row = {
                        enrollment.getEnrollmentId(),
                        enrollment.getStudentRollNo(),
                        enrollment.getStudentName(),
                        enrollment.getStatus()
                };
                tableModel.addRow(row);
            }
        } else {
            MessageDialog.showError(this, result.getMessage());
        }
    }

    private void enterGrade() {
        int selectedRow = studentsTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        int modelRow = studentsTable.convertRowIndexToModel(selectedRow);
        int enrollmentId = (Integer) tableModel.getValueAt(modelRow, 0);
        String studentName = (String) tableModel.getValueAt(modelRow, 2);

        // Show grade entry dialog
        GradeEntryDialog dialog = new GradeEntryDialog((Frame) SwingUtilities.getWindowAncestor(this),
                instructorApi, enrollmentId, studentName);
        dialog.setVisible(true);
    }

    private void computeFinalGrade() {
        int selectedRow = studentsTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        int modelRow = studentsTable.convertRowIndexToModel(selectedRow);
        int enrollmentId = (Integer) tableModel.getValueAt(modelRow, 0);
        String studentName = (String) tableModel.getValueAt(modelRow, 2);

        boolean confirm = MessageDialog.showConfirm(this,
                "Compute final grade for " + studentName + "?\n\n" +
                        "This will calculate the weighted average based on all grade components.");

        if (confirm) {
            InstructorService.ComputeGradeResult result = instructorApi.computeFinalGrade(enrollmentId);

            if (result.isSuccess()) {
                MessageDialog.showSuccess(this,
                        "Final grade computed successfully!\n\n" +
                                "Student: " + studentName + "\n" +
                                "Final Grade: " + result.getFinalGrade());
            } else {
                MessageDialog.showError(this, result.getMessage());
            }
        }
    }

    private void exportGrades() {
        if (studentsTable.getRowCount() == 0) {
            MessageDialog.showInfo(this, "No data to export");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Grades");
        fileChooser.setSelectedFile(new java.io.File("Grades.csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".csv")) {
                    filePath += ".csv";
                }

                String[] headers = { "Enrollment ID", "Roll No", "Student Name", "Status" };
                java.util.List<String[]> data = new java.util.ArrayList<>();

                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String[] row = new String[tableModel.getColumnCount()];
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        Object value = tableModel.getValueAt(i, j);
                        row[j] = value != null ? value.toString() : "";
                    }
                    data.add(row);
                }

                edu.univ.erp.util.CsvExportUtil.exportToCsv(filePath, headers, data);
                MessageDialog.showSuccess(this, "Grades exported successfully to:\n" + filePath);
            } catch (Exception e) {
                e.printStackTrace();
                MessageDialog.showError(this, "Failed to export grades: " + e.getMessage());
            }
        }
    }

    /**
     * Refresh the gradebook
     */
    public void refresh() {
        loadSections();
        loadStudents();
    }

    // Helper class for section combo box
    private static class SectionItem {
        final Section section;

        SectionItem(Section section) {
            this.section = section;
        }

        @Override
        public String toString() {
            return section.getCourseCode() + "-" + section.getSectionNumber() +
                    " (" + section.getSemester() + " " + section.getYear() + ")";
        }
    }
}
