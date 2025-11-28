package edu.univ.erp.ui.common;

import javax.swing.*;
import java.awt.*;

/**
 * Utility class for displaying messages to users
 */
public class MessageDialog {

    /**
     * Show a success message
     */
    public static void showSuccess(Component parent, String message) {
        showSuccess(parent, "Success", message);
    }

    /**
     * Show a success message with custom title
     */
    public static void showSuccess(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Show an error message
     */
    public static void showError(Component parent, String message) {
        showError(parent, "Error", message);
    }

    /**
     * Show an error message with custom title
     */
    public static void showError(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Show a warning message
     */
    public static void showWarning(Component parent, String message) {
        showWarning(parent, "Warning", message);
    }

    /**
     * Show a warning message with custom title
     */
    public static void showWarning(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Show an info message
     */
    public static void showInfo(Component parent, String message) {
        showInfo(parent, "Information", message);
    }

    /**
     * Show an info message with custom title
     */
    public static void showInfo(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Show a confirmation dialog
     * 
     * @return true if user clicked Yes, false otherwise
     */
    public static boolean showConfirm(Component parent, String message) {
        return showConfirm(parent, "Confirm", message);
    }

    /**
     * Show a confirmation dialog with custom title
     * 
     * @return true if user clicked Yes, false otherwise
     */
    public static boolean showConfirm(Component parent, String title, String message) {
        int result = JOptionPane.showConfirmDialog(parent, message, title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        return result == JOptionPane.YES_OPTION;
    }

    /**
     * Show an input dialog
     * 
     * @return user input or null if cancelled
     */
    public static String showInput(Component parent, String message) {
        return showInput(parent, "Input", message, "");
    }

    /**
     * Show an input dialog with default value
     * 
     * @return user input or null if cancelled
     */
    public static String showInput(Component parent, String title, String message, String defaultValue) {
        return (String) JOptionPane.showInputDialog(parent, message, title,
                JOptionPane.PLAIN_MESSAGE, null, null, defaultValue);
    }
}
