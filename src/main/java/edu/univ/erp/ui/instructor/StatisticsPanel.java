package edu.univ.erp.ui.instructor;

import edu.univ.erp.api.instructor.InstructorApi;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.InstructorService;
import edu.univ.erp.ui.common.MessageDialog;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Panel for viewing section statistics
 */
public class StatisticsPanel extends JPanel {
    private final InstructorApi instructorApi;

    private JComboBox<SectionItem> sectionCombo;
    private JLabel totalStudentsLabel;
    private JLabel enrolledStudentsLabel;
    private JLabel droppedStudentsLabel;
    private JPanel gradeDistributionPanel;

    public StatisticsPanel() {
        this.instructorApi = new InstructorApi();
        initComponents();
        loadSections();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        // Title
        JLabel titleLabel = new JLabel("Class Statistics");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));

        // Selector panel
        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectorPanel.add(new JLabel("Select Section:"));
        sectionCombo = new JComboBox<>();
        sectionCombo.setPreferredSize(new Dimension(300, 30));
        sectionCombo.addActionListener(e -> loadStatistics());
        selectorPanel.add(sectionCombo);

        contentPanel.add(selectorPanel, BorderLayout.NORTH);

        // Statistics panel
        JPanel statsPanel = new JPanel(new MigLayout("fillx,wrap 1", "[grow,fill]", "[]10[]"));

        // Enrollment statistics
        JPanel enrollmentPanel = new JPanel(new MigLayout("fillx,wrap 2", "[right]15[grow,fill]"));
        enrollmentPanel.setBorder(BorderFactory.createTitledBorder("Enrollment Statistics"));

        enrollmentPanel.add(new JLabel("Total Students:"));
        totalStudentsLabel = new JLabel("-");
        totalStudentsLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        enrollmentPanel.add(totalStudentsLabel);

        enrollmentPanel.add(new JLabel("Currently Enrolled:"));
        enrolledStudentsLabel = new JLabel("-");
        enrolledStudentsLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        enrolledStudentsLabel.setForeground(new Color(0, 150, 0));
        enrollmentPanel.add(enrolledStudentsLabel);

        enrollmentPanel.add(new JLabel("Dropped:"));
        droppedStudentsLabel = new JLabel("-");
        droppedStudentsLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        droppedStudentsLabel.setForeground(new Color(200, 0, 0));
        enrollmentPanel.add(droppedStudentsLabel);

        statsPanel.add(enrollmentPanel, "grow");

        // Grade distribution
        gradeDistributionPanel = new JPanel(new MigLayout("fillx,wrap 2", "[right]15[grow,fill]"));
        gradeDistributionPanel.setBorder(BorderFactory.createTitledBorder("Grade Distribution"));
        statsPanel.add(gradeDistributionPanel, "grow");

        contentPanel.add(statsPanel, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refresh());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
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

    private void loadStatistics() {
        SectionItem selectedItem = (SectionItem) sectionCombo.getSelectedItem();
        if (selectedItem == null) {
            return;
        }

        int sectionId = selectedItem.getSection().getSectionId();
        InstructorService.ClassStatsResult result = instructorApi.getClassStatistics(sectionId);

        if (result.isSuccess()) {
            Map<String, Object> stats = result.getStatistics();

            // Update enrollment stats
            totalStudentsLabel.setText(String.valueOf(stats.get("totalStudents")));
            enrolledStudentsLabel.setText(String.valueOf(stats.get("enrolledStudents")));
            droppedStudentsLabel.setText(String.valueOf(stats.get("droppedStudents")));

            // Update grade distribution
            gradeDistributionPanel.removeAll();

            @SuppressWarnings("unchecked")
            Map<String, Long> gradeDistribution = (Map<String, Long>) stats.get("gradeDistribution");

            if (gradeDistribution != null && !gradeDistribution.isEmpty()) {
                String[] gradeOrder = { "A+", "A", "B+", "B", "C", "F" };

                for (String grade : gradeOrder) {
                    Long count = gradeDistribution.getOrDefault(grade, 0L);
                    if (count > 0) {
                        JLabel gradeLabel = new JLabel(grade + ":");
                        gradeLabel.setFont(new Font("Dialog", Font.BOLD, 12));
                        gradeDistributionPanel.add(gradeLabel);

                        JLabel countLabel = new JLabel(String.valueOf(count));
                        gradeDistributionPanel.add(countLabel);
                    }
                }
            } else {
                gradeDistributionPanel.add(new JLabel("No final grades assigned yet"), "span 2");
            }

            gradeDistributionPanel.revalidate();
            gradeDistributionPanel.repaint();

        } else {
            MessageDialog.showError(this, result.getMessage());
        }
    }

    /**
     * Refresh the statistics
     */
    public void refresh() {
        loadSections();
        loadStatistics();
    }
}
