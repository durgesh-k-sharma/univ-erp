package edu.univ.erp.ui.admin;

import edu.univ.erp.api.admin.AdminApi;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Student;
import edu.univ.erp.service.AdminService;
import edu.univ.erp.ui.common.MessageDialog;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel for managing users (students, instructors, admins)
 */
public class UserManagementPanel extends JPanel {
    private final AdminApi adminApi;

    private JTabbedPane tabbedPane;
    private JTable studentsTable;
    private JTable instructorsTable;
    private DefaultTableModel studentsTableModel;
    private DefaultTableModel instructorsTableModel;

    private JTextField adminUsernameField;
    private JPasswordField adminPasswordField;

    public UserManagementPanel() {
        this.adminApi = new AdminApi();
        initComponents();
        loadUsers();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Tabbed pane for different user types
        tabbedPane = new JTabbedPane();

        // Students tab
        JPanel studentsPanel = createStudentsPanel();
        tabbedPane.addTab("Students", studentsPanel);

        // Instructors tab
        JPanel instructorsPanel = createInstructorsPanel();
        tabbedPane.addTab("Instructors", instructorsPanel);

        // Admins tab
        JPanel adminsPanel = createAdminsPanel();
        tabbedPane.addTab("Admins", adminsPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createStudentsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Table
        String[] columns = { "Student ID", "Roll No", "Username", "Program", "Year", "Email", "Phone" };
        studentsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        studentsTable = new JTable(studentsTableModel);
        studentsTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(studentsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadStudents());
        buttonPanel.add(refreshButton);

        JButton addButton = new JButton("Add New Student");
        addButton.setFont(new Font("Dialog", Font.BOLD, 13));
        addButton.addActionListener(e -> showAddStudentDialog());
        buttonPanel.add(addButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createInstructorsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Table
        String[] columns = { "Instructor ID", "Employee ID", "Username", "Department", "Email", "Phone" };
        instructorsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        instructorsTable = new JTable(instructorsTableModel);
        instructorsTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(instructorsTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadInstructors());
        buttonPanel.add(refreshButton);

        JButton addButton = new JButton("Add New Instructor");
        addButton.setFont(new Font("Dialog", Font.BOLD, 13));
        addButton.addActionListener(e -> showAddInstructorDialog());
        buttonPanel.add(addButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAdminsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JLabel infoLabel = new JLabel(
                "<html><p>Create new administrator accounts.</p>" +
                        "<p style='margin-top:10px;'><b>Note:</b> Admin accounts have full system access.</p></html>");
        panel.add(infoLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new MigLayout("fillx,wrap 2", "[right]15[300,fill]"));
        formPanel.setBorder(BorderFactory.createTitledBorder("Create New Admin"));

        formPanel.add(new JLabel("Username:"));
        adminUsernameField = new JTextField();
        formPanel.add(adminUsernameField);

        formPanel.add(new JLabel("Password:"));
        adminPasswordField = new JPasswordField();
        formPanel.add(adminPasswordField);

        JButton createButton = new JButton("Create Admin");
        createButton.setFont(new Font("Dialog", Font.BOLD, 13));
        createButton.addActionListener(e -> createAdmin());
        formPanel.add(createButton, "span 2,center,gaptop 20");

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        centerPanel.add(formPanel);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private void loadUsers() {
        loadStudents();
        loadInstructors();
    }

    private void loadStudents() {
        studentsTableModel.setRowCount(0);
        List<Student> students = adminApi.getAllStudents();
        for (Student s : students) {
            Object[] row = {
                    s.getStudentId(),
                    s.getRollNo(),
                    s.getUsername(),
                    s.getProgram(),
                    s.getYear(),
                    s.getEmail(),
                    s.getPhone()
            };
            studentsTableModel.addRow(row);
        }
    }

    private void loadInstructors() {
        instructorsTableModel.setRowCount(0);
        List<Instructor> instructors = adminApi.getAllInstructors();
        for (Instructor i : instructors) {
            Object[] row = {
                    i.getInstructorId(),
                    i.getEmployeeId(),
                    i.getUsername(),
                    i.getDepartment(),
                    i.getEmail(),
                    i.getPhone()
            };
            instructorsTableModel.addRow(row);
        }
    }

    private void showAddStudentDialog() {
        AddStudentDialog dialog = new AddStudentDialog((Frame) SwingUtilities.getWindowAncestor(this), adminApi);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            loadStudents();
        }
    }

    private void showAddInstructorDialog() {
        AddInstructorDialog dialog = new AddInstructorDialog((Frame) SwingUtilities.getWindowAncestor(this), adminApi);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            loadInstructors();
        }
    }

    private void createAdmin() {
        String username = adminUsernameField.getText().trim();
        String password = new String(adminPasswordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            MessageDialog.showError(this, "Please enter username and password");
            return;
        }

        AdminService.CreateUserResult result = adminApi.createAdmin(username, password);

        if (result.isSuccess()) {
            MessageDialog.showSuccess(this, result.getMessage());
            adminUsernameField.setText("");
            adminPasswordField.setText("");
        } else {
            MessageDialog.showError(this, result.getMessage());
        }
    }

    /**
     * Refresh the user lists
     */
    public void refresh() {
        loadUsers();
    }
}
