package edu.univ.erp.service;

import edu.univ.erp.auth.AuthRepository;
import edu.univ.erp.auth.PasswordHasher;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.domain.Role;
import edu.univ.erp.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Unit tests for AuthService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private AuthRepository authRepository;

    private AuthService authService;

    @BeforeEach
    void setUp() throws Exception {
        authService = new AuthService();

        // Use reflection to inject mock repository
        Field field = AuthService.class.getDeclaredField("authRepository");
        field.setAccessible(true);
        field.set(authService, authRepository);
    }

    private User createUser(int id, String username, Role role, String status) {
        User user = new User(id, username, role);
        user.setStatus(status);
        return user;
    }

    @Test
    @DisplayName("Should login successfully with correct credentials")
    void testLoginSuccess() {
        // Arrange
        String username = "student1";
        String password = "password123";
        String passwordHash = PasswordHasher.hashPassword(password);

        User mockUser = createUser(1, username, Role.STUDENT, "ACTIVE");

        when(authRepository.findByUsername(username)).thenReturn(mockUser);
        when(authRepository.getPasswordHash(1)).thenReturn(passwordHash);

        try (MockedStatic<SessionManager> sessionManager = mockStatic(SessionManager.class)) {
            // Act
            AuthService.LoginResult result = authService.login(username, password);

            // Assert
            assertTrue(result.isSuccess());
            assertEquals("Login successful", result.getMessage());
            assertNotNull(result.getUser());
            assertEquals(username, result.getUser().getUsername());

            verify(authRepository).resetFailedAttempts(1);
            verify(authRepository).updateLastLogin(1);
            sessionManager.verify(() -> SessionManager.setCurrentUser(mockUser));
        }
    }

    @Test
    @DisplayName("Should fail login with empty username")
    void testLoginEmptyUsername() {
        // Act
        AuthService.LoginResult result = authService.login("", "password");

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Username cannot be empty", result.getMessage());
        assertNull(result.getUser());

        verifyNoInteractions(authRepository);
    }

    @Test
    @DisplayName("Should fail login with null username")
    void testLoginNullUsername() {
        // Act
        AuthService.LoginResult result = authService.login(null, "password");

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Username cannot be empty", result.getMessage());
        assertNull(result.getUser());
    }

    @Test
    @DisplayName("Should fail login with empty password")
    void testLoginEmptyPassword() {
        // Act
        AuthService.LoginResult result = authService.login("student1", "");

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Password cannot be empty", result.getMessage());
        assertNull(result.getUser());
    }

    @Test
    @DisplayName("Should fail login with non-existent user")
    void testLoginUserNotFound() {
        // Arrange
        when(authRepository.findByUsername("nonexistent")).thenReturn(null);

        // Act
        AuthService.LoginResult result = authService.login("nonexistent", "password");

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Incorrect username or password", result.getMessage());
        assertNull(result.getUser());
    }

    @Test
    @DisplayName("Should fail login with locked account")
    void testLoginLockedAccount() {
        // Arrange
        User lockedUser = createUser(1, "student1", Role.STUDENT, "LOCKED");
        when(authRepository.findByUsername("student1")).thenReturn(lockedUser);

        // Act
        AuthService.LoginResult result = authService.login("student1", "password");

        // Assert
        assertFalse(result.isSuccess());
        assertThat(result.getMessage(), containsString("locked"));
        assertNull(result.getUser());
    }

    @Test
    @DisplayName("Should fail login with inactive account")
    void testLoginInactiveAccount() {
        // Arrange
        User inactiveUser = createUser(1, "student1", Role.STUDENT, "INACTIVE");
        when(authRepository.findByUsername("student1")).thenReturn(inactiveUser);

        // Act
        AuthService.LoginResult result = authService.login("student1", "password");

        // Assert
        assertFalse(result.isSuccess());
        assertThat(result.getMessage(), containsString("inactive"));
        assertNull(result.getUser());
    }

    @Test
    @DisplayName("Should fail login with incorrect password")
    void testLoginIncorrectPassword() {
        // Arrange
        String username = "student1";
        String correctPassword = "correct123";
        String wrongPassword = "wrong123";
        String passwordHash = PasswordHasher.hashPassword(correctPassword);

        User mockUser = createUser(1, username, Role.STUDENT, "ACTIVE");

        when(authRepository.findByUsername(username)).thenReturn(mockUser);
        when(authRepository.getPasswordHash(1)).thenReturn(passwordHash);

        // Act
        AuthService.LoginResult result = authService.login(username, wrongPassword);

        // Assert
        assertFalse(result.isSuccess());
        assertEquals("Incorrect username or password", result.getMessage());
        assertNull(result.getUser());

        verify(authRepository).incrementFailedAttempts(1);
        verify(authRepository, never()).resetFailedAttempts(anyInt());
    }

    @Test
    @DisplayName("Should fail login when password hash is null")
    void testLoginNullPasswordHash() {
        // Arrange
        User mockUser = createUser(1, "student1", Role.STUDENT, "ACTIVE");
        when(authRepository.findByUsername("student1")).thenReturn(mockUser);
        when(authRepository.getPasswordHash(1)).thenReturn(null);

        // Act
        AuthService.LoginResult result = authService.login("student1", "password");

        // Assert
        assertFalse(result.isSuccess());
        assertThat(result.getMessage(), containsString("Authentication error"));
        assertNull(result.getUser());
    }

    @Test
    @DisplayName("Should logout successfully")
    void testLogout() {
        // Arrange
        User mockUser = createUser(1, "student1", Role.STUDENT, "ACTIVE");

        try (MockedStatic<SessionManager> sessionManager = mockStatic(SessionManager.class)) {
            sessionManager.when(SessionManager::getCurrentUser).thenReturn(mockUser);

            // Act
            authService.logout();

            // Assert
            sessionManager.verify(SessionManager::clearSession);
        }
    }

    @Test
    @DisplayName("Should change password successfully")
    void testChangePasswordSuccess() {
        // Arrange
        String oldPassword = "oldPass123";
        String newPassword = "newPass456";
        String oldHash = PasswordHasher.hashPassword(oldPassword);

        User mockUser = createUser(1, "student1", Role.STUDENT, "ACTIVE");

        when(authRepository.getPasswordHash(1)).thenReturn(oldHash);
        when(authRepository.updatePassword(eq(1), anyString())).thenReturn(true);

        try (MockedStatic<SessionManager> sessionManager = mockStatic(SessionManager.class)) {
            sessionManager.when(SessionManager::getCurrentUser).thenReturn(mockUser);

            // Act
            AuthService.ChangePasswordResult result = authService.changePassword(oldPassword, newPassword, newPassword);

            // Assert
            assertTrue(result.isSuccess());
            assertEquals("Password changed successfully", result.getMessage());
            verify(authRepository).updatePassword(eq(1), anyString());
        }
    }

    @Test
    @DisplayName("Should fail change password when not logged in")
    void testChangePasswordNotLoggedIn() {
        try (MockedStatic<SessionManager> sessionManager = mockStatic(SessionManager.class)) {
            sessionManager.when(SessionManager::getCurrentUser).thenReturn(null);

            // Act
            AuthService.ChangePasswordResult result = authService.changePassword("old", "new", "new");

            // Assert
            assertFalse(result.isSuccess());
            assertEquals("No user logged in", result.getMessage());
        }
    }

    @Test
    @DisplayName("Should fail change password with empty old password")
    void testChangePasswordEmptyOldPassword() {
        User mockUser = createUser(1, "student1", Role.STUDENT, "ACTIVE");

        try (MockedStatic<SessionManager> sessionManager = mockStatic(SessionManager.class)) {
            sessionManager.when(SessionManager::getCurrentUser).thenReturn(mockUser);

            // Act
            AuthService.ChangePasswordResult result = authService.changePassword("", "newPass", "newPass");

            // Assert
            assertFalse(result.isSuccess());
            assertThat(result.getMessage(), containsString("Current password cannot be empty"));
        }
    }

    @Test
    @DisplayName("Should fail change password with short new password")
    void testChangePasswordTooShort() {
        User mockUser = createUser(1, "student1", Role.STUDENT, "ACTIVE");

        try (MockedStatic<SessionManager> sessionManager = mockStatic(SessionManager.class)) {
            sessionManager.when(SessionManager::getCurrentUser).thenReturn(mockUser);

            // Act
            AuthService.ChangePasswordResult result = authService.changePassword("oldPass", "123", "123");

            // Assert
            assertFalse(result.isSuccess());
            assertThat(result.getMessage(), containsString("at least 6 characters"));
        }
    }

    @Test
    @DisplayName("Should fail change password when passwords don't match")
    void testChangePasswordMismatch() {
        User mockUser = createUser(1, "student1", Role.STUDENT, "ACTIVE");

        try (MockedStatic<SessionManager> sessionManager = mockStatic(SessionManager.class)) {
            sessionManager.when(SessionManager::getCurrentUser).thenReturn(mockUser);

            // Act
            AuthService.ChangePasswordResult result = authService.changePassword("oldPass", "newPass1", "newPass2");

            // Assert
            assertFalse(result.isSuccess());
            assertThat(result.getMessage(), containsString("do not match"));
        }
    }

    @Test
    @DisplayName("Should fail change password when new password same as old")
    void testChangePasswordSameAsOld() {
        User mockUser = createUser(1, "student1", Role.STUDENT, "ACTIVE");

        try (MockedStatic<SessionManager> sessionManager = mockStatic(SessionManager.class)) {
            sessionManager.when(SessionManager::getCurrentUser).thenReturn(mockUser);

            // Act
            AuthService.ChangePasswordResult result = authService.changePassword("samePass", "samePass", "samePass");

            // Assert
            assertFalse(result.isSuccess());
            assertThat(result.getMessage(), containsString("must be different"));
        }
    }

    @Test
    @DisplayName("Should fail change password with incorrect old password")
    void testChangePasswordIncorrectOld() {
        String correctOldPassword = "correct123";
        String wrongOldPassword = "wrong123";
        String oldHash = PasswordHasher.hashPassword(correctOldPassword);

        User mockUser = createUser(1, "student1", Role.STUDENT, "ACTIVE");

        when(authRepository.getPasswordHash(1)).thenReturn(oldHash);

        try (MockedStatic<SessionManager> sessionManager = mockStatic(SessionManager.class)) {
            sessionManager.when(SessionManager::getCurrentUser).thenReturn(mockUser);

            // Act
            AuthService.ChangePasswordResult result = authService.changePassword(wrongOldPassword, "newPass123",
                    "newPass123");

            // Assert
            assertFalse(result.isSuccess());
            assertThat(result.getMessage(), containsString("Current password is incorrect"));
        }
    }

    @Test
    @DisplayName("Should check if user is logged in")
    void testIsLoggedIn() {
        try (MockedStatic<SessionManager> sessionManager = mockStatic(SessionManager.class)) {
            sessionManager.when(SessionManager::isLoggedIn).thenReturn(true);

            // Act
            boolean result = authService.isLoggedIn();

            // Assert
            assertTrue(result);
        }
    }

    @Test
    @DisplayName("Should get current user")
    void testGetCurrentUser() {
        User mockUser = createUser(1, "student1", Role.STUDENT, "ACTIVE");

        try (MockedStatic<SessionManager> sessionManager = mockStatic(SessionManager.class)) {
            sessionManager.when(SessionManager::getCurrentUser).thenReturn(mockUser);

            // Act
            User result = authService.getCurrentUser();

            // Assert
            assertNotNull(result);
            assertEquals(mockUser.getUsername(), result.getUsername());
        }
    }
}
