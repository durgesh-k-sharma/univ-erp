package edu.univ.erp.ui.admin;

import edu.univ.erp.api.admin.AdminApi;
import edu.univ.erp.ui.common.MessageDialog;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Panel for Backup and Restore operations
 */
public class BackupRestorePanel extends JPanel {
        private final AdminApi adminApi;

        public BackupRestorePanel(AdminApi adminApi) {
                this.adminApi = adminApi;
                initComponents();
        }

        private void initComponents() {
                setLayout(new MigLayout("fill, insets 20", "[grow]", "[][grow]"));

                // Header
                add(new JLabel("<html><h2>Backup & Restore</h2></html>"), "wrap");

                // Content Panel
                JPanel contentPanel = new JPanel(new MigLayout("fillx, insets 20", "[grow]", "[]20[]"));
                contentPanel.setBorder(BorderFactory.createEtchedBorder());
                // contentPanel.setBackground(Color.WHITE); // Removed for theme compatibility

                // Backup Section
                JPanel backupPanel = new JPanel(new MigLayout("fillx", "[grow][]", "[]10[]"));
                backupPanel.setBorder(BorderFactory.createTitledBorder("Backup Data"));
                // backupPanel.setOpaque(false); // Removed for theme compatibility

                backupPanel.add(
                                new JLabel("<html>Export system data (Students, Instructors, Courses, Sections) to CSV files.<br>"
                                                +
                                                "Select a directory to save the backup files.</html>"),
                                "span, wrap");

                JButton backupButton = new JButton("Backup Data");
                backupButton.setIcon(UIManager.getIcon("FileView.floppyDriveIcon"));
                backupButton.addActionListener(e -> performBackup());
                backupPanel.add(backupButton, "tag right");

                contentPanel.add(backupPanel, "growx, wrap");

                // Restore Section
                JPanel restorePanel = new JPanel(new MigLayout("fillx", "[grow][]", "[]10[]"));
                restorePanel.setBorder(BorderFactory.createTitledBorder("Restore Data"));
                // restorePanel.setOpaque(false); // Removed for theme compatibility

                restorePanel.add(new JLabel("<html>Restore system data from CSV files.<br>" +
                                "Select a directory containing the backup files.<br>" +
                                "<b>Warning: This feature is currently experimental.</b></html>"), "span, wrap");

                JButton restoreButton = new JButton("Restore Data");
                restoreButton.addActionListener(e -> performRestore());
                restorePanel.add(restoreButton, "tag right");

                contentPanel.add(restorePanel, "growx");

                add(contentPanel, "grow");
        }

        private void performBackup() {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Select Backup Directory");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                        File selectedDir = chooser.getSelectedFile();

                        boolean success = adminApi.backupData(selectedDir.getAbsolutePath());

                        if (success) {
                                MessageDialog.showSuccess(this,
                                                "Backup completed successfully to:\n" + selectedDir.getAbsolutePath());
                        } else {
                                MessageDialog.showError(this, "Backup failed. Check logs for details.");
                        }
                }
        }

        private void performRestore() {
                boolean confirm = MessageDialog.showConfirm(this,
                                "Are you sure you want to restore data?\n\n" +
                                                "This operation is experimental and may overwrite existing data.");

                if (!confirm) {
                        return;
                }

                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Select Backup Directory");
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                        File selectedDir = chooser.getSelectedFile();

                        boolean success = adminApi.restoreData(selectedDir.getAbsolutePath());

                        if (success) {
                                MessageDialog.showSuccess(this,
                                                "Restore completed successfully from:\n"
                                                                + selectedDir.getAbsolutePath());
                        } else {
                                MessageDialog.showError(this, "Restore failed. Check logs for details.");
                        }
                }
        }
}
