package edu.univ.erp.access;

import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.SettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Access control utility to check permissions and maintenance mode
 */
public class AccessControl {
    private static final Logger logger = LoggerFactory.getLogger(AccessControl.class);
    private static final SettingsRepository settingsRepo = new SettingsRepository();

    /**
     * Check if the current user can perform an action
     */
    public static boolean canAccess(String role, String action) {
        String currentRole = SessionManager.getCurrentRole();

        if (currentRole == null) {
            logger.warn("Access denied: No user logged in");
            return false;
        }

        // Admin can do everything
        if ("ADMIN".equals(currentRole)) {
            return true;
        }

        // Role must match
        if (!currentRole.equals(role)) {
            logger.warn("Access denied: User role {} cannot perform {} action", currentRole, action);
            return false;
        }

        return true;
    }

    /**
     * Check if system is in maintenance mode
     */
    public static boolean isMaintenanceMode() {
        try {
            return settingsRepo.isMaintenanceMode();
        } catch (Exception e) {
            logger.error("Error checking maintenance mode", e);
            return false;
        }
    }

    /**
     * Check if the current user can modify data
     * Returns false if maintenance mode is ON and user is not admin
     */
    public static boolean canModify() {
        String currentRole = SessionManager.getCurrentRole();

        if (currentRole == null) {
            logger.warn("Modification denied: No user logged in");
            return false;
        }

        // Admin can modify even in maintenance mode
        if ("ADMIN".equals(currentRole)) {
            return true;
        }

        // Check maintenance mode for non-admin users
        if (isMaintenanceMode()) {
            logger.info("Modification denied: System is in maintenance mode");
            return false;
        }

        return true;
    }

    /**
     * Validate that a student can only access their own data
     */
    public static boolean validateStudentAccess(int studentUserId) {
        Integer currentUserId = SessionManager.getCurrentUserId();

        if (currentUserId == null) {
            return false;
        }

        // Admin can access any student data
        if (SessionManager.isAdmin()) {
            return true;
        }

        // Student can only access their own data
        if (SessionManager.isStudent()) {
            return currentUserId == studentUserId;
        }

        return false;
    }

    /**
     * Validate that an instructor can only access their own data
     */
    public static boolean validateInstructorAccess(int instructorUserId) {
        Integer currentUserId = SessionManager.getCurrentUserId();

        if (currentUserId == null) {
            return false;
        }

        // Admin can access any instructor data
        if (SessionManager.isAdmin()) {
            return true;
        }

        // Instructor can only access their own data
        if (SessionManager.isInstructor()) {
            return currentUserId == instructorUserId;
        }

        return false;
    }

    /**
     * Get a user-friendly message for access denial
     */
    public static String getAccessDeniedMessage() {
        if (!SessionManager.isLoggedIn()) {
            return "You must be logged in to perform this action.";
        }

        if (isMaintenanceMode() && !SessionManager.isAdmin()) {
            return "System is in maintenance mode. You can view data but cannot make changes.";
        }

        return "You do not have permission to perform this action.";
    }
}
