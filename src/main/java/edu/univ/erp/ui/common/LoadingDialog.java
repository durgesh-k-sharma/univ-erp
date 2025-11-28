package edu.univ.erp.ui.common;

import javax.swing.*;
import java.awt.*;

/**
 * Loading dialog to show during long operations
 */
public class LoadingDialog extends JDialog {
    private JLabel messageLabel;
    private JProgressBar progressBar;

    public LoadingDialog(Frame parent, String message) {
        super(parent, "Please Wait", true);
        initComponents(message);
    }

    private void initComponents(String message) {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        messageLabel = new JLabel(message);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(messageLabel);

        panel.add(Box.createVerticalStrut(15));

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(progressBar);

        add(panel);
        pack();
        setLocationRelativeTo(getParent());
    }

    /**
     * Update the message
     */
    public void setMessage(String message) {
        SwingUtilities.invokeLater(() -> messageLabel.setText(message));
    }

    /**
     * Show loading dialog and execute task in background
     */
    public static void showWhileExecuting(Frame parent, String message, Runnable task) {
        LoadingDialog dialog = new LoadingDialog(parent, message);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                task.run();
                return null;
            }

            @Override
            protected void done() {
                dialog.dispose();
            }
        };

        worker.execute();
        dialog.setVisible(true);
    }
}
