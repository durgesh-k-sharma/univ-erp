package edu.univ.erp.auth;

import edu.univ.erp.data.DatabaseManager;
import edu.univ.erp.domain.Role;
import edu.univ.erp.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;

/**
 * Repository for Auth DB operations
 * Handles user authentication data (NO business data)
 */
public class AuthRepository {
    private static final Logger logger = LoggerFactory.getLogger(AuthRepository.class);

    /**
     * Find user by username
     */
    public User findByUsername(String username) {
        String sql = "SELECT user_id, username, role, password_hash, status, " +
                "failed_login_attempts, last_login, created_at, updated_at " +
                "FROM users_auth WHERE username = ?";

        try (Connection conn = DatabaseManager.getAuthConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding user by username: {}", username, e);
        }

        return null;
    }

    /**
     * Find user by user ID
     */
    public User findById(int userId) {
        String sql = "SELECT user_id, username, role, password_hash, status, " +
                "failed_login_attempts, last_login, created_at, updated_at " +
                "FROM users_auth WHERE user_id = ?";

        try (Connection conn = DatabaseManager.getAuthConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding user by ID: {}", userId, e);
        }

        return null;
    }

    /**
     * Get password hash for a user
     */
    public String getPasswordHash(int userId) {
        String sql = "SELECT password_hash FROM users_auth WHERE user_id = ?";

        try (Connection conn = DatabaseManager.getAuthConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("password_hash");
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting password hash for user: {}", userId, e);
        }

        return null;
    }

    /**
     * Create a new user in Auth DB
     */
    public int createUser(String username, Role role, String passwordHash) {
        String sql = "INSERT INTO users_auth (username, role, password_hash, status) " +
                "VALUES (?, ?, ?, 'ACTIVE')";

        try (Connection conn = DatabaseManager.getAuthConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, username);
            stmt.setString(2, role.name());
            stmt.setString(3, passwordHash);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        logger.info("Created user: {} with ID: {}", username, userId);
                        return userId;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating user: {}", username, e);
        }

        return -1;
    }

    /**
     * Update user password
     */
    public boolean updatePassword(int userId, String newPasswordHash) {
        String sql = "UPDATE users_auth SET password_hash = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE user_id = ?";

        try (Connection conn = DatabaseManager.getAuthConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPasswordHash);
            stmt.setInt(2, userId);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Password updated for user ID: {}", userId);

                // Optionally save to password history
                savePasswordHistory(userId, newPasswordHash);

                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating password for user: {}", userId, e);
        }

        return false;
    }

    /**
     * Save password to history (bonus feature)
     */
    private void savePasswordHistory(int userId, String passwordHash) {
        String sql = "INSERT INTO password_history (user_id, password_hash) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getAuthConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, passwordHash);
            stmt.executeUpdate();

            logger.debug("Password history saved for user ID: {}", userId);
        } catch (SQLException e) {
            logger.warn("Error saving password history for user: {}", userId, e);
        }
    }

    /**
     * Increment failed login attempts
     */
    public void incrementFailedAttempts(int userId) {
        String sql = "UPDATE users_auth SET failed_login_attempts = failed_login_attempts + 1, " +
                "status = CASE WHEN failed_login_attempts + 1 >= 3 THEN 'LOCKED' ELSE status END " +
                "WHERE user_id = ?";

        try (Connection conn = DatabaseManager.getAuthConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();

            logger.info("Incremented failed login attempts for user ID: {}", userId);
        } catch (SQLException e) {
            logger.error("Error incrementing failed attempts for user: {}", userId, e);
        }
    }

    /**
     * Reset failed login attempts
     */
    public void resetFailedAttempts(int userId) {
        String sql = "UPDATE users_auth SET failed_login_attempts = 0 WHERE user_id = ?";

        try (Connection conn = DatabaseManager.getAuthConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();

            logger.debug("Reset failed login attempts for user ID: {}", userId);
        } catch (SQLException e) {
            logger.error("Error resetting failed attempts for user: {}", userId, e);
        }
    }

    /**
     * Update last login timestamp
     */
    public void updateLastLogin(int userId) {
        String sql = "UPDATE users_auth SET last_login = CURRENT_TIMESTAMP WHERE user_id = ?";

        try (Connection conn = DatabaseManager.getAuthConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.executeUpdate();

            logger.debug("Updated last login for user ID: {}", userId);
        } catch (SQLException e) {
            logger.error("Error updating last login for user: {}", userId, e);
        }
    }

    /**
     * Unlock a locked user account
     */
    public boolean unlockUser(int userId) {
        String sql = "UPDATE users_auth SET status = 'ACTIVE', failed_login_attempts = 0 " +
                "WHERE user_id = ?";

        try (Connection conn = DatabaseManager.getAuthConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Unlocked user ID: {}", userId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error unlocking user: {}", userId, e);
        }

        return false;
    }

    /**
     * Map ResultSet to User object
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setRole(Role.valueOf(rs.getString("role")));
        user.setStatus(rs.getString("status"));
        user.setFailedLoginAttempts(rs.getInt("failed_login_attempts"));

        Timestamp lastLogin = rs.getTimestamp("last_login");
        if (lastLogin != null) {
            user.setLastLogin(lastLogin.toLocalDateTime());
        }

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            user.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return user;
    }
}
