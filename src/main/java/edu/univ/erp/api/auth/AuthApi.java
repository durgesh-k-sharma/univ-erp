package edu.univ.erp.api.auth;

import edu.univ.erp.domain.User;
import edu.univ.erp.service.AuthService;
import edu.univ.erp.auth.SessionManager;

/**
 * API for authentication operations
 */
public class AuthApi {
    private final AuthService authService;

    public AuthApi() {
        this.authService = new AuthService();
    }

    /**
     * Login a user
     */
    public AuthService.LoginResult login(String username, String password) {
        return authService.login(username, password);
    }

    /**
     * Logout current user
     */
    public void logout() {
        authService.logout();
    }

    /**
     * Change password for current user
     */
    public AuthService.ChangePasswordResult changePassword(String currentPassword, String newPassword) {
        String username = SessionManager.getCurrentUsername();
        return authService.changePassword(username, currentPassword, newPassword);
    }

    /**
     * Get current username
     */
    public String getCurrentUsername() {
        return SessionManager.getCurrentUsername();
    }
}
