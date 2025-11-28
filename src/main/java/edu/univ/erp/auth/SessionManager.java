package edu.univ.erp.auth;

import edu.univ.erp.domain.User;

/**
 * Session manager to track the currently logged-in user
 * Simple in-memory session for desktop application
 */
public class SessionManager {
    private static User currentUser = null;

    /**
     * Set the current logged-in user
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Get the current logged-in user
     */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Get the current user's ID
     */
    public static Integer getCurrentUserId() {
        return currentUser != null ? currentUser.getUserId() : null;
    }

    /**
     * Get the current user's role
     */
    public static String getCurrentRole() {
        return currentUser != null ? currentUser.getRole().name() : null;
    }

    /**
     * Get the current user's username
     */
    public static String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : null;
    }

    /**
     * Check if a user is logged in
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Clear the current session (logout)
     */
    public static void clearSession() {
        currentUser = null;
    }

    /**
     * Check if current user has a specific role
     */
    public static boolean hasRole(String role) {
        if (currentUser == null) {
            return false;
        }
        return currentUser.getRole().name().equals(role);
    }

    /**
     * Check if current user is an admin
     */
    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * Check if current user is an instructor
     */
    public static boolean isInstructor() {
        return hasRole("INSTRUCTOR");
    }

    /**
     * Check if current user is a student
     */
    public static boolean isStudent() {
        return hasRole("STUDENT");
    }
}
