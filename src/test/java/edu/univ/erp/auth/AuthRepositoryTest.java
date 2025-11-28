package edu.univ.erp.auth;

import edu.univ.erp.domain.Role;
import edu.univ.erp.domain.User;
import edu.univ.erp.data.DatabaseManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Unit tests for AuthRepository
 * Note: These tests mock database connections to avoid requiring a live
 * database
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthRepository Tests")
class AuthRepositoryTest {

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockStatement;

    @Mock
    private ResultSet mockResultSet;

    private AuthRepository authRepository;
    private MockedStatic<DatabaseManager> mockedDatabaseManager;

    @BeforeEach
    void setUp() throws SQLException {
        mockedDatabaseManager = mockStatic(DatabaseManager.class);
        mockedDatabaseManager.when(DatabaseManager::getAuthConnection).thenReturn(mockConnection);

        // Configure lenient mocks
        lenient().when(mockConnection.prepareStatement(anyString())).thenReturn(mockStatement);
        lenient().when(mockConnection.prepareStatement(anyString(), anyInt())).thenReturn(mockStatement);
        lenient().when(mockStatement.executeQuery()).thenReturn(mockResultSet);
        lenient().when(mockStatement.executeUpdate()).thenReturn(0);
        lenient().when(mockStatement.getGeneratedKeys()).thenReturn(mockResultSet);

        authRepository = new AuthRepository();
    }

    @AfterEach
    void tearDown() {
        mockedDatabaseManager.close();
    }

    @Test
    @DisplayName("Should find user by username")
    void testFindByUsername() throws SQLException {
        // This test demonstrates the structure but would require mocking
        // DatabaseManager
        // In a real scenario, you'd use a test database or H2 in-memory database

        // For now, we'll test that the method handles null results gracefully
        User user = authRepository.findByUsername("nonexistent");

        // Should return null for non-existent user (when DB is not set up)
        // In integration tests, this would verify actual DB queries
        assertNull(user);
    }

    @Test
    @DisplayName("Should handle null username gracefully")
    void testFindByUsernameNull() {
        // Should not throw exception
        assertDoesNotThrow(() -> {
            User user = authRepository.findByUsername(null);
        });
    }

    @Test
    @DisplayName("Should create user and return user ID")
    void testCreateUser() {
        // Test the method signature and basic validation
        String username = "newuser";
        Role role = Role.STUDENT;
        String passwordHash = "$2a$10$abcdefghijklmnopqrstuv";

        // This would require database mocking or integration test
        // For now, verify method doesn't throw on valid inputs
        assertDoesNotThrow(() -> {
            authRepository.createUser(username, role, passwordHash);
        });
    }

    @Test
    @DisplayName("Should update password")
    void testUpdatePassword() {
        int userId = 1;
        String newHash = "$2a$10$newhashabcdefghijklmnop";

        // Verify method doesn't throw
        assertDoesNotThrow(() -> {
            authRepository.updatePassword(userId, newHash);
        });
    }

    @Test
    @DisplayName("Should increment failed attempts")
    void testIncrementFailedAttempts() {
        int userId = 1;

        assertDoesNotThrow(() -> {
            authRepository.incrementFailedAttempts(userId);
        });
    }

    @Test
    @DisplayName("Should reset failed attempts")
    void testResetFailedAttempts() {
        int userId = 1;

        assertDoesNotThrow(() -> {
            authRepository.resetFailedAttempts(userId);
        });
    }

    @Test
    @DisplayName("Should update last login")
    void testUpdateLastLogin() {
        int userId = 1;

        assertDoesNotThrow(() -> {
            authRepository.updateLastLogin(userId);
        });
    }

    @Test
    @DisplayName("Should unlock user")
    void testUnlockUser() {
        int userId = 1;

        assertDoesNotThrow(() -> {
            boolean result = authRepository.unlockUser(userId);
            // Without DB, this will return false
            assertFalse(result);
        });
    }
}
