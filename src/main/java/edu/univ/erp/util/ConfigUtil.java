package edu.univ.erp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Configuration utility to load application properties
 */
public class ConfigUtil {
    private static final Logger logger = LoggerFactory.getLogger(ConfigUtil.class);
    private static Properties properties;

    static {
        loadProperties();
    }

    private static void loadProperties() {
        properties = new Properties();
        try (InputStream input = ConfigUtil.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input == null) {
                logger.warn("Unable to find application.properties, using defaults");
                setDefaults();
                return;
            }
            properties.load(input);
            logger.info("Configuration loaded successfully");
        } catch (IOException e) {
            logger.error("Error loading configuration", e);
            setDefaults();
        }
    }

    private static void setDefaults() {
        properties.setProperty("db.auth.url", "jdbc:mysql://localhost:3306/univ_erp_auth");
        properties.setProperty("db.auth.username", "root");
        properties.setProperty("db.auth.password", "root");
        properties.setProperty("db.erp.url", "jdbc:mysql://localhost:3306/univ_erp");
        properties.setProperty("db.erp.username", "root");
        properties.setProperty("db.erp.password", "root");
        properties.setProperty("db.pool.maxPoolSize", "10");
        properties.setProperty("db.pool.minIdle", "2");
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer value for key {}: {}", key, value);
            return defaultValue;
        }
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = properties.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    // Database configuration getters
    public static String getAuthDbUrl() {
        return get("db.auth.url");
    }

    public static String getAuthDbUsername() {
        return get("db.auth.username");
    }

    public static String getAuthDbPassword() {
        return get("db.auth.password");
    }

    public static String getErpDbUrl() {
        return get("db.erp.url");
    }

    public static String getErpDbUsername() {
        return get("db.erp.username");
    }

    public static String getErpDbPassword() {
        return get("db.erp.password");
    }

    public static int getMaxPoolSize() {
        return getInt("db.pool.maxPoolSize", 10);
    }

    public static int getMinIdle() {
        return getInt("db.pool.minIdle", 2);
    }
}
