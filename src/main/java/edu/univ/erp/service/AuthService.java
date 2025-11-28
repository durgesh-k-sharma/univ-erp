package edu.univ.erp.service;

import edu.univ.erp.auth.AuthRepository;
import edu.univ.erp.auth.PasswordHasher;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Authentication service for login, logout, and password management
 */
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private final AuthRepository authRepository;

    public AuthService() {
        this.authRepository = new AuthRepository();
    }

    /**
     * Login with username and password
     * 
     * @return User object if successful, null otherwise
     */
    public LoginResult login(String username, String password) {
        logger.info("Login attempt for username: {}", username);

        // Validate inputs
        if (username == null || username.trim().isEmpty()) {
            return new LoginResult(false, "Username cannot be empty", null);
        }

        if (password == null || password.isEmpty()) {
            return new LoginResult(false, "Password cannot be empty", null);
        }

        // Find user
        User user = authRepository.findByUsername(username.trim());

        if (user == null) {
            logger.warn("Login failed: User not found - {}", username);
            return new LoginResult(false, "Incorrect username or password", null);
        }

        // Check if account is locked
        if ("LOCKED".equals(user.getStatus())) {
            logger.warn("Login failed: Account locked - {}", username);
            return new LoginResult(false,
                    "Account is locked due to multiple failed login attempts. Please contact an administrator.", null);
        }

        // Check if account is inactive
        if ("INACTIVE".equals(user.getStatus())) {
            logger.warn("Login failed: Account inactive - {}", username);
            return new LoginResult(false, "Account is inactive. Please contact an administrator.", null);
        }

        // Get password hash and verify
        String passwordHash = authRepository.getPasswordHash(user.getUserId());

        if (passwordHash == null) {
            logger.error("Login failed: No password hash found for user - {}", username);
            return new LoginResult(false, "Authentication error. Please contact support.", null);
        }

        // Verify password
        boolean passwordMatches = PasswordHasher.verifyPassword(password, passwordHash);

        if (!passwordMatches) {
            // Increment failed attempts
            authRepository.incrementFailedAttempts(user.getUserId());
            logger.warn("Login failed: Incorrect password - {}", username);
            return new LoginResult(false, "Incorrect username or password", null);
        }

        // Successful login
        authRepository.resetFailedAttempts(user.getUserId());
        authRepository.updateLastLogin(user.getUserId());

        // Set session
        SessionManager.setCurrentUser(user);

        logger.info("Login successful for user: {} ({})", username, user.getRole());
        return new LoginResult(true, "Login successful", user);
    }

    /**
     * Logout the current user
     */
    public void logout() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser != null) {
            logger.info("User logged out: {}", currentUser.getUsername());
        }
        SessionManager.clearSession();
    }

    /**
     * Change password for the current user
     */
    public ChangePasswordResult changePassword(String oldPassword, String newPassword, String confirmPassword) {
        User currentUser = SessionManager.getCurrentUser();

        if (currentUser == null) {
            return new ChangePasswordResult(false, "No user logged in");
        }

        // Validate inputs
        if (oldPassword == null || oldPassword.isEmpty()) {
            return new ChangePasswordResult(false, "Current password cannot be empty");
        }

        if (newPassword == null || newPassword.isEmpty()) {
            return new ChangePasswordResult(false, "New password cannot be empty");
        }

        if (newPassword.length() < 6) {
            return new ChangePasswordResult(false, "New password must be at least 6 characters long");
        }

        if (!newPassword.equals(confirmPassword)) {
            return new ChangePasswordResult(false, "New password and confirmation do not match");
        }

        if (oldPassword.equals(newPassword)) {
            return new ChangePasswordResult(false, "New password must be different from current password");
        }

        // Verify old password
        String currentHash = authRepository.getPasswordHash(currentUser.getUserId());

        if (currentHash == null || !PasswordHasher.verifyPassword(oldPassword, currentHash)) {
            logger.warn("Change password failed: Incorrect current password for user {}", currentUser.getUsername());
            return new ChangePasswordResult(false, "Current password is incorrect");
        }

        // Hash new password
        String newHash = PasswordHasher.hashPassword(newPassword);

        // Update password
        boolean updated = authRepository.updatePassword(currentUser.getUserId(), newHash);

        if (updated) {
            logger.info("Password changed successfully for user: {}", currentUser.getUsername());
            return new ChangePasswordResult(true, "Password changed successfully");
        } else {
            logger.error("Failed to update password for user: {}", currentUser.getUsername());
            return new ChangePasswordResult(false, "Failed to update password. Please try again.");
        }
    }

    /**
     * Get current logged-in user
     */
    public User getCurrentUser() {
        return SessionManager.getCurrentUser();
    }

    /**
     * Check if a user is logged in
     */
    public boolean isLoggedIn() {
        return SessionManager.isLoggedIn();
    }

    // Result classes
    public static class LoginResult {
        private final boolean success;
        private final String message;
        private final User user;

        public LoginResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public User getUser() {
            return user;
        }
    }

    public static class ChangePasswordResult {
        private final boolean success;
        private final String message;

        public ChangePasswordResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}
