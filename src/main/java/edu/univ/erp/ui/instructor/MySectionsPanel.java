package edu.univ.erp.ui.instructor;

import edu.univ.erp.api.instructor.InstructorApi;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.InstructorService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for viewing instructor's sections
 */
public class MySectionsPanel extends JPanel {
    private final InstructorApi instructorApi;

    private JTable sectionsTable;
    private DefaultTableModel tableModel;
    private JButton refreshButton;

    public MySectionsPanel() {
        this.instructorApi = new InstructorApi();
        initComponents();
        loadSections();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel titleLabel = new JLabel("My Sections");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = { "Section ID", "Course Code", "Course Title", "Section", "Credits",
                "Day/Time", "Room", "Semester", "Year", "Enrolled", "Capacity" };
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

        // Set column widths
        sectionsTable.getColumnModel().getColumn(1).setPreferredWidth(80); // Course Code
        sectionsTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Course Title
        sectionsTable.getColumnModel().getColumn(3).setPreferredWidth(60); // Section
        sectionsTable.getColumnModel().getColumn(4).setPreferredWidth(60); // Credits
        sectionsTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Day/Time
        sectionsTable.getColumnModel().getColumn(6).setPreferredWidth(80); // Room
        sectionsTable.getColumnModel().getColumn(7).setPreferredWidth(80); // Semester
        sectionsTable.getColumnModel().getColumn(8).setPreferredWidth(60); // Year
        sectionsTable.getColumnModel().getColumn(9).setPreferredWidth(70); // Enrolled
        sectionsTable.getColumnModel().getColumn(10).setPreferredWidth(70); // Capacity

        JScrollPane scrollPane = new JScrollPane(sectionsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadSections());
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadSections() {
        // Clear table
        tableModel.setRowCount(0);

        // Load sections
        List<Section> sections = instructorApi.getMySections();

        for (Section section : sections) {
            Object[] row = {
                    section.getSectionId(),
                    section.getCourseCode(),
                    section.getCourseTitle(),
                    section.getSectionNumber(),
                    section.getCourseCredits(),
                    section.getDayTime(),
                    section.getRoom(),
                    section.getSemester(),
                    section.getYear(),
                    section.getEnrolledCount(),
                    section.getCapacity()
            };
            tableModel.addRow(row);
        }

        if (sections.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "You have no sections assigned yet.",
                    "No Sections",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Refresh the sections
     */
    public void refresh() {
        loadSections();
    }
}
