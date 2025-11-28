package edu.univ.erp.ui.student;

import edu.univ.erp.api.student.StudentApi;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.ui.common.MessageDialog;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Panel for browsing course catalog and registering for courses
 */
public class CourseCatalogPanel extends JPanel {
    private final StudentApi studentApi;

    private JTable catalogTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JComboBox<String> semesterCombo;
    private JSpinner yearSpinner;
    private JTextField searchField;
    private JButton registerButton;

    public CourseCatalogPanel() {
        this.studentApi = new StudentApi();
        initComponents();
        loadCatalog();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Top panel with title and filters
        JPanel topPanel = new JPanel(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("Course Catalog");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        // Filter panel
        topPanel.add(createFilterPanel(), BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = { "Section ID", "Course Code", "Course Title", "Section", "Instructor",
                "Day/Time", "Room", "Credits", "Enrolled", "Capacity", "Available" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        catalogTable = new JTable(tableModel);
        catalogTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        catalogTable.setRowHeight(25);
        catalogTable.getTableHeader().setReorderingAllowed(false);

        // Hide Section ID column
        catalogTable.getColumnModel().getColumn(0).setMinWidth(0);
        catalogTable.getColumnModel().getColumn(0).setMaxWidth(0);
        catalogTable.getColumnModel().getColumn(0).setWidth(0);

        // Set column widths
        catalogTable.getColumnModel().getColumn(1).setPreferredWidth(80); // Course Code
        catalogTable.getColumnModel().getColumn(2).setPreferredWidth(200); // Course Title
        catalogTable.getColumnModel().getColumn(3).setPreferredWidth(60); // Section
        catalogTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Instructor
        catalogTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Day/Time
        catalogTable.getColumnModel().getColumn(6).setPreferredWidth(80); // Room
        catalogTable.getColumnModel().getColumn(7).setPreferredWidth(60); // Credits
        catalogTable.getColumnModel().getColumn(8).setPreferredWidth(70); // Enrolled
        catalogTable.getColumnModel().getColumn(9).setPreferredWidth(70); // Capacity
        catalogTable.getColumnModel().getColumn(10).setPreferredWidth(80); // Available

        // Add sorter
        sorter = new TableRowSorter<>(tableModel);
        catalogTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(catalogTable);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        registerButton = new JButton("Register for Selected Section");
        registerButton.setFont(new Font("Dialog", Font.BOLD, 13));
        registerButton.addActionListener(e -> registerForSection());
        registerButton.setEnabled(false);
        buttonPanel.add(registerButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Enable register button when row is selected
        catalogTable.getSelectionModel().addListSelectionListener(e -> {
            registerButton.setEnabled(catalogTable.getSelectedRow() != -1);
        });
    }

    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new MigLayout("fillx", "[]10[]10[]10[grow]10[]", "[]"));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Filter Courses"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Semester
        panel.add(new JLabel("Semester:"));
        semesterCombo = new JComboBox<>(new String[] { "FALL", "SPRING", "SUMMER" });
        semesterCombo.setSelectedItem(getCurrentSemester());
        semesterCombo.addActionListener(e -> loadCatalog());
        panel.add(semesterCombo);

        // Year
        panel.add(new JLabel("Year:"));
        int currentYear = LocalDate.now().getYear();
        yearSpinner = new JSpinner(new SpinnerNumberModel(currentYear, currentYear - 1, currentYear + 1, 1));
        yearSpinner.addChangeListener(e -> loadCatalog());
        panel.add(yearSpinner);

        // Search
        panel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        searchField.addActionListener(e -> applyFilter());
        panel.add(searchField, "growx");

        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> applyFilter());
        panel.add(searchButton);

        return panel;
    }

    private String getCurrentSemester() {
        int month = LocalDate.now().getMonthValue();
        if (month >= 1 && month <= 5)
            return "SPRING";
        if (month >= 6 && month <= 8)
            return "SUMMER";
        return "FALL";
    }

    private void loadCatalog() {
        String semester = (String) semesterCombo.getSelectedItem();
        int year = (Integer) yearSpinner.getValue();

        // Clear table
        tableModel.setRowCount(0);

        // Load sections
        List<Section> sections = studentApi.browseCatalog(semester, year);

        for (Section section : sections) {
            int available = section.getCapacity() - section.getEnrolledCount();
            String availableStr = available > 0 ? String.valueOf(available) : "FULL";

            Object[] row = {
                    section.getSectionId(),
                    section.getCourseCode(),
                    section.getCourseTitle(),
                    section.getSectionNumber(),
                    section.getInstructorName() != null ? section.getInstructorName() : "TBA",
                    section.getDayTime(),
                    section.getRoom(),
                    section.getCourseCredits(),
                    section.getEnrolledCount(),
                    section.getCapacity(),
                    availableStr
            };
            tableModel.addRow(row);
        }

        if (sections.isEmpty()) {
            MessageDialog.showInfo(this, "No sections found for " + semester + " " + year);
        }
    }

    private void applyFilter() {
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
    }

    private void registerForSection() {
        int selectedRow = catalogTable.getSelectedRow();
        if (selectedRow == -1) {
            MessageDialog.showWarning(this, "Please select a section to register");
            return;
        }

        // Convert view row to model row
        int modelRow = catalogTable.convertRowIndexToModel(selectedRow);
        int sectionId = (Integer) tableModel.getValueAt(modelRow, 0);
        String courseCode = (String) tableModel.getValueAt(modelRow, 1);
        String sectionNumber = (String) tableModel.getValueAt(modelRow, 3);
        String available = (String) tableModel.getValueAt(modelRow, 10);

        // Check if full
        if ("FULL".equals(available)) {
            MessageDialog.showError(this, "This section is full. Please choose another section.");
            return;
        }

        // Confirm registration
        boolean confirm = MessageDialog.showConfirm(this,
                "Register for " + courseCode + "-" + sectionNumber + "?");

        if (confirm) {
            StudentService.RegistrationResult result = studentApi.registerForSection(sectionId);

            if (result.isSuccess()) {
                MessageDialog.showSuccess(this, result.getMessage());
                loadCatalog(); // Refresh to show updated enrollment
            } else {
                MessageDialog.showError(this, result.getMessage());
            }
        }
    }

    /**
     * Refresh the catalog
     */
    public void refresh() {
        loadCatalog();
    }
}
