package edu.univ.erp.ui.student;

import edu.univ.erp.api.student.StudentApi;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.ui.common.MessageDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for viewing grades
 */
public class GradesPanel extends JPanel {
    private final StudentApi studentApi;

    private JTable gradesTable;
    private DefaultTableModel tableModel;

    public GradesPanel() {
        this.studentApi = new StudentApi();
        initComponents();
        loadGrades();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel titleLabel = new JLabel("My Grades");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = { "Course Code", "Course Title", "Section", "Component", "Score", "Max Score", "Percentage",
                "Weight", "Final Grade" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        gradesTable = new JTable(tableModel);
        gradesTable.setRowHeight(25);
        gradesTable.getTableHeader().setReorderingAllowed(false);

        // Set column widths
        gradesTable.getColumnModel().getColumn(0).setPreferredWidth(80); // Course Code
        gradesTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Course Title
        gradesTable.getColumnModel().getColumn(2).setPreferredWidth(60); // Section
        gradesTable.getColumnModel().getColumn(3).setPreferredWidth(80); // Component
        gradesTable.getColumnModel().getColumn(4).setPreferredWidth(60); // Score
        gradesTable.getColumnModel().getColumn(5).setPreferredWidth(80); // Max Score
        gradesTable.getColumnModel().getColumn(6).setPreferredWidth(80); // Percentage
        gradesTable.getColumnModel().getColumn(7).setPreferredWidth(60); // Weight
        gradesTable.getColumnModel().getColumn(8).setPreferredWidth(80); // Final Grade

        // Center align numeric columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        gradesTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        gradesTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        gradesTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        gradesTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        gradesTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        gradesTable.getColumnModel().getColumn(7).setCellRenderer(centerRenderer);
        gradesTable.getColumnModel().getColumn(8).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(gradesTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadGrades());
        buttonPanel.add(refreshButton);

        JButton exportButton = new JButton("Download Transcript (PDF)");
        exportButton.setFont(new Font("Dialog", Font.BOLD, 13));
        exportButton.addActionListener(e -> downloadTranscript());
        exportButton.setEnabled(true);
        buttonPanel.add(exportButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadGrades() {
        // Clear table
        tableModel.setRowCount(0);

        // Load grades
        List<Grade> grades = studentApi.getMyGrades();

        for (Grade grade : grades) {
            String percentage = grade.getScore() != null && grade.getMaxScore() != null
                    ? String.format("%.1f%%", grade.getPercentage().doubleValue())
                    : "-";

            String weight = grade.getWeight() != null
                    ? String.format("%.0f%%", grade.getWeight().doubleValue())
                    : "-";

            Object[] row = {
                    grade.getCourseCode(),
                    grade.getCourseTitle(),
                    grade.getSectionNumber(),
                    grade.getComponent(),
                    grade.getScore() != null ? grade.getScore().toString() : "-",
                    grade.getMaxScore() != null ? grade.getMaxScore().toString() : "-",
                    percentage,
                    weight,
                    grade.getFinalGrade() != null ? grade.getFinalGrade() : "-"
            };
            tableModel.addRow(row);
        }

        if (grades.isEmpty()) {
            Object[] row = { "No grades available yet", "", "", "", "", "", "", "", "" };
            tableModel.addRow(row);
        }
    }

    private void downloadTranscript() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Transcript");
        fileChooser.setSelectedFile(new java.io.File("Transcript.pdf"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                if (!filePath.toLowerCase().endsWith(".pdf")) {
                    filePath += ".pdf";
                }

                edu.univ.erp.domain.Student student = studentApi.getProfile();
                List<Grade> grades = studentApi.getMyGrades();

                edu.univ.erp.util.TranscriptGenerator.generateTranscript(student, grades, filePath);

                MessageDialog.showSuccess(this, "Transcript saved successfully to:\n" + filePath);
            } catch (Exception e) {
                e.printStackTrace();
                MessageDialog.showError(this, "Failed to generate transcript: " + e.getMessage());
            }
        }
    }

    /**
     * Refresh the grades
     */
    public void refresh() {
        loadGrades();
    }
}
