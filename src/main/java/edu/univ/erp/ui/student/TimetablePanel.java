package edu.univ.erp.ui.student;

import edu.univ.erp.api.student.StudentApi;
import edu.univ.erp.domain.Enrollment;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for viewing student timetable
 */
public class TimetablePanel extends JPanel {
    private final StudentApi studentApi;

    private JTable timetableTable;
    private DefaultTableModel tableModel;

    public TimetablePanel() {
        this.studentApi = new StudentApi();
        initComponents();
        loadTimetable();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Info panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("My Timetable");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        infoPanel.add(titleLabel, BorderLayout.NORTH);

        add(infoPanel, BorderLayout.NORTH);

        // Table
        String[] columns = { "Course", "Section", "Day/Time", "Room", "Instructor", "Credits" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        timetableTable = new JTable(tableModel);
        timetableTable.setRowHeight(30);
        timetableTable.getTableHeader().setReorderingAllowed(false);

        // Set column widths
        timetableTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Course
        timetableTable.getColumnModel().getColumn(1).setPreferredWidth(60); // Section
        timetableTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Day/Time
        timetableTable.getColumnModel().getColumn(3).setPreferredWidth(80); // Room
        timetableTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Instructor
        timetableTable.getColumnModel().getColumn(5).setPreferredWidth(60); // Credits

        // Center align some columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        timetableTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        timetableTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(timetableTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadTimetable());
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadTimetable() {
        // Clear table
        tableModel.setRowCount(0);

        // Load enrolled sections
        List<Enrollment> enrollments = studentApi.getMyTimetable();

        for (Enrollment enrollment : enrollments) {
            Object[] row = {
                    enrollment.getCourseCode(),
                    enrollment.getSectionNumber(),
                    enrollment.getDayTime(),
                    enrollment.getRoom(),
                    enrollment.getInstructorName() != null ? enrollment.getInstructorName() : "TBA",
                    enrollment.getCourseCredits() != null ? enrollment.getCourseCredits() : ""
            };
            tableModel.addRow(row);
        }

        if (enrollments.isEmpty()) {
            // Add a message row
            Object[] row = { "No enrolled sections", "", "", "", "", "" };
            tableModel.addRow(row);
        }
    }

    /**
     * Refresh the timetable
     */
    public void refresh() {
        loadTimetable();
    }
}
