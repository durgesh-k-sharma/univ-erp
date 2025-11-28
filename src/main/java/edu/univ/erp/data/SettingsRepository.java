package edu.univ.erp.data;

import edu.univ.erp.domain.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

/**
 * Repository for Settings table operations
 */
public class SettingsRepository {
    private static final Logger logger = LoggerFactory.getLogger(SettingsRepository.class);

    /**
     * Get a setting value by key
     */
    public Settings getSetting(String key) {
        String sql = "SELECT setting_key, setting_value, description, updated_at " +
                "FROM settings WHERE setting_key = ?";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, key);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSettings(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting setting: {}", key, e);
        }

        return null;
    }

    /**
     * Update a setting value
     */
    public boolean updateSetting(String key, String value) {
        String sql = "UPDATE settings SET setting_value = ?, updated_at = CURRENT_TIMESTAMP " +
                "WHERE setting_key = ?";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, value);
            stmt.setString(2, key);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Updated setting: {} = {}", key, value);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating setting: {}", key, e);
        }

        return false;
    }

    /**
     * Check if maintenance mode is enabled
     */
    public boolean isMaintenanceMode() {
        Settings setting = getSetting("maintenance_mode");
        return setting != null && setting.getBooleanValue();
    }

    /**
     * Toggle maintenance mode
     */
    public boolean setMaintenanceMode(boolean enabled) {
        return updateSetting("maintenance_mode", String.valueOf(enabled));
    }

    /**
     * Get drop deadline days setting
     */
    public int getDropDeadlineDays() {
        Settings setting = getSetting("drop_deadline_days");
        return setting != null ? setting.getIntValue() : 14;
    }

    /**
     * Map ResultSet to Settings object
     */
    private Settings mapResultSetToSettings(ResultSet rs) throws SQLException {
        Settings settings = new Settings();
        settings.setSettingKey(rs.getString("setting_key"));
        settings.setSettingValue(rs.getString("setting_value"));
        settings.setDescription(rs.getString("description"));

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            settings.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return settings;
    }
}
