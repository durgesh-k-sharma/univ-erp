package edu.univ.erp.util;

import edu.univ.erp.auth.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Audit logger for tracking sensitive operations
 */
public class AuditLogger {
    private static final Logger logger = LoggerFactory.getLogger("AUDIT");
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Log a user action
     */
    public static void logUserAction(String action, String details) {
        String username = SessionManager.getCurrentUsername();
        String role = SessionManager.getCurrentRole();
        Integer userId = SessionManager.getCurrentUserId();
        String timestamp = LocalDateTime.now().format(formatter);

        logger.info("AUDIT | Time={} | User={} | UserID={} | Role={} | Action={} | Details={}",
                timestamp, username, userId, role, action, details);
    }

    /**
     * Log user creation
     */
    public static void logUserCreation(String newUsername, String newRole) {
        logUserAction("CREATE_USER",
                String.format("Created user '%s' with role '%s'", newUsername, newRole));
    }

    /**
     * Log user deletion
     */
    public static void logUserDeletion(String deletedUsername) {
        logUserAction("DELETE_USER",
                String.format("Deleted user '%s'", deletedUsername));
    }

    /**
     * Log grade modification
     */
    public static void logGradeModification(int enrollmentId, String component, double score) {
        logUserAction("MODIFY_GRADE",
                String.format("Modified grade for enrollment %d, component '%s', score %.2f",
                        enrollmentId, component, score));
    }

    /**
     * Log final grade computation
     */
    public static void logFinalGradeComputation(int enrollmentId, String finalGrade) {
        logUserAction("COMPUTE_FINAL_GRADE",
                String.format("Computed final grade for enrollment %d: %s", enrollmentId, finalGrade));
    }

    /**
     * Log section assignment
     */
    public static void logSectionAssignment(int sectionId, int instructorId) {
        logUserAction("ASSIGN_INSTRUCTOR",
                String.format("Assigned instructor %d to section %d", instructorId, sectionId));
    }

    /**
     * Log maintenance mode toggle
     */
    public static void logMaintenanceModeToggle(boolean enabled) {
        logUserAction("TOGGLE_MAINTENANCE_MODE",
                String.format("Maintenance mode %s", enabled ? "ENABLED" : "DISABLED"));
    }

    /**
     * Log password change
     */
    public static void logPasswordChange(String targetUsername) {
        logUserAction("CHANGE_PASSWORD",
                String.format("Password changed for user '%s'", targetUsername));
    }

    /**
     * Log failed login attempt
     */
    public static void logFailedLogin(String username, String reason) {
        logger.warn("AUDIT | Time={} | Action=FAILED_LOGIN | Username={} | Reason={}",
                LocalDateTime.now().format(formatter), username, reason);
    }

    /**
     * Log successful login
     */
    public static void logSuccessfulLogin(String username, String role) {
        logger.info("AUDIT | Time={} | Action=SUCCESSFUL_LOGIN | Username={} | Role={}",
                LocalDateTime.now().format(formatter), username, role);
    }

    /**
     * Log logout
     */
    public static void logLogout(String username) {
        logger.info("AUDIT | Time={} | Action=LOGOUT | Username={}",
                LocalDateTime.now().format(formatter), username);
    }

    /**
     * Log access denied
     */
    public static void logAccessDenied(String action, String reason) {
        logUserAction("ACCESS_DENIED",
                String.format("Attempted action '%s' denied: %s", action, reason));
    }
}
