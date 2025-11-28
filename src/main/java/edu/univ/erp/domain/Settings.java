package edu.univ.erp.domain;

import java.time.LocalDateTime;

/**
 * Settings entity for system configuration
 */
public class Settings {
    private String settingKey;
    private String settingValue;
    private String description;
    private LocalDateTime updatedAt;

    public Settings() {
    }

    public Settings(String settingKey, String settingValue) {
        this.settingKey = settingKey;
        this.settingValue = settingValue;
    }

    // Getters and Setters
    public String getSettingKey() {
        return settingKey;
    }

    public void setSettingKey(String settingKey) {
        this.settingKey = settingKey;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public void setSettingValue(String settingValue) {
        this.settingValue = settingValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean getBooleanValue() {
        return "true".equalsIgnoreCase(settingValue);
    }

    public int getIntValue() {
        try {
            return Integer.parseInt(settingValue);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "Settings{" +
                "settingKey='" + settingKey + '\'' +
                ", settingValue='" + settingValue + '\'' +
                '}';
    }
}
