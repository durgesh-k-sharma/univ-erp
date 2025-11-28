package edu.univ.erp.ui.student;

import edu.univ.erp.api.auth.AuthApi;
import edu.univ.erp.api.maintenance.MaintenanceApi;
import edu.univ.erp.api.student.StudentApi;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.domain.Student;
import edu.univ.erp.ui.auth.LoginPanel;
import edu.univ.erp.ui.common.MaintenanceBanner;
import edu.univ.erp.ui.common.MessageDialog;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * Main dashboard for student users
 */
public class StudentDashboard extends JFrame {
    private final StudentApi studentApi;
    private final AuthApi authApi;
    private final MaintenanceApi maintenanceApi;
    private Student currentStudent;

    private JPanel contentPanel;
    private JPanel maintenanceBannerPanel;
    private CardLayout cardLayout;

    public StudentDashboard() {
        this.studentApi = new StudentApi();
        this.authApi = new AuthApi();
        this.maintenanceApi = new MaintenanceApi();

        // Load current student profile
        this.currentStudent = studentApi.getProfile();

        initComponents();
        checkMaintenanceMode();
        loadStudentInfo();
    }

    private void initComponents() {
        setTitle("University ERP - Student Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Maintenance banner panel
        maintenanceBannerPanel = new JPanel(new BorderLayout());
        add(maintenanceBannerPanel, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Content area
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        contentPanel.add(createWelcomePanel(), "welcome");
        contentPanel.add(new CourseCatalogPanel(), "catalog");
        contentPanel.add(new MyRegistrationsPanel(), "registrations");
        contentPanel.add(new TimetablePanel(), "timetable");
        contentPanel.add(new GradesPanel(), "grades");

        add(contentPanel, BorderLayout.CENTER);

        cardLayout.show(contentPanel, "welcome");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(45, 52, 54));
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        JLabel headerLabel = new JLabel("Student Portal");
        headerLabel.setFont(new Font("Dialog", Font.BOLD, 18));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(headerLabel);

        sidebar.add(Box.createVerticalStrut(10));

        JLabel userLabel = new JLabel(SessionManager.getCurrentUsername());
        userLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
        userLabel.setForeground(Color.LIGHT_GRAY);
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(userLabel);

        sidebar.add(Box.createVerticalStrut(30));

        addMenuButton(sidebar, "🏠 Home", "welcome");
        addMenuButton(sidebar, "📚 Course Catalog", "catalog");
        addMenuButton(sidebar, "📝 My Registrations", "registrations");
        addMenuButton(sidebar, "📅 Timetable", "timetable");
        addMenuButton(sidebar, "📊 Grades", "grades");

        sidebar.add(Box.createVerticalGlue());

        JButton changePasswordButton = createSidebarButton("🔒 Change Password");
        changePasswordButton.addActionListener(e -> showChangePasswordDialog());
        sidebar.add(changePasswordButton);

        sidebar.add(Box.createVerticalStrut(5));

        JButton logoutButton = createSidebarButton("🚪 Logout");
        logoutButton.addActionListener(e -> logout());
        sidebar.add(logoutButton);

        return sidebar;
    }

    private void addMenuButton(JPanel sidebar, String text, String panelName) {
        JButton button = createSidebarButton(text);
        button.addActionListener(e -> cardLayout.show(contentPanel, panelName));
        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(5));
    }

    private JButton createSidebarButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        button.setFont(new Font("Dialog", Font.PLAIN, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(45, 52, 54));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(99, 110, 114));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(45, 52, 54));
            }
        });

        return button;
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new MigLayout("fillx,wrap 1", "[grow,fill]", "[]20[]"));

        JLabel titleLabel = new JLabel("Welcome to Student Portal");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        panel.add(titleLabel);

        if (currentStudent != null) {
            JPanel infoPanel = new JPanel(new MigLayout("fillx,wrap 2", "[right]15[grow,fill]"));
            infoPanel.setBorder(BorderFactory.createTitledBorder("Student Information"));

            addInfoRow(infoPanel, "Roll Number:", currentStudent.getRollNo());
            addInfoRow(infoPanel, "Program:", currentStudent.getProgram());
            addInfoRow(infoPanel, "Year:", String.valueOf(currentStudent.getYear()));
            if (currentStudent.getEmail() != null) {
                addInfoRow(infoPanel, "Email:", currentStudent.getEmail());
            }

            panel.add(infoPanel, "grow");
        }

        JLabel instructionLabel = new JLabel(
                "<html><p style='margin-top:20px;'>Use the menu on the left to navigate:</p>" +
                        "<ul>" +
                        "<li><b>Course Catalog</b> - Browse and register for courses</li>" +
                        "<li><b>My Registrations</b> - View and drop registered courses</li>" +
                        "<li><b>Timetable</b> - View your weekly schedule</li>" +
                        "<li><b>Grades</b> - View your grades and download transcript</li>" +
                        "</ul></html>");
        panel.add(instructionLabel, "grow");

        return panel;
    }

    private void addInfoRow(JPanel panel, String label, String value) {
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Dialog", Font.BOLD, 12));
        panel.add(labelComp);

        JLabel valueComp = new JLabel(value);
        panel.add(valueComp);
    }

    private void loadStudentInfo() {
        if (currentStudent == null) {
            MessageDialog.showError(this, "Could not load student profile");
        }
    }

    private void checkMaintenanceMode() {
        maintenanceBannerPanel.removeAll();
        if (maintenanceApi.isMaintenanceMode()) {
            MaintenanceBanner.addTo(maintenanceBannerPanel);
        }
        maintenanceBannerPanel.revalidate();
        maintenanceBannerPanel.repaint();
    }

    private void showChangePasswordDialog() {
        edu.univ.erp.ui.common.ChangePasswordDialog dialog = new edu.univ.erp.ui.common.ChangePasswordDialog(this);
        dialog.setVisible(true);
    }

    private void logout() {
        boolean confirm = MessageDialog.showConfirm(this, "Are you sure you want to logout?");
        if (confirm) {
            authApi.logout();
            dispose();
            LoginPanel.showLoginWindow();
        }
    }
}
