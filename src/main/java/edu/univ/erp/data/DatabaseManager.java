package edu.univ.erp.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import edu.univ.erp.util.ConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Database connection manager using HikariCP for connection pooling
 * Manages two separate connection pools: Auth DB and ERP DB
 */
public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);

    private static HikariDataSource authDataSource;
    private static HikariDataSource erpDataSource;

    static {
        initializeAuthPool();
        initializeErpPool();
    }

    private static void initializeAuthPool() {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(ConfigUtil.getAuthDbUrl());
            config.setUsername(ConfigUtil.getAuthDbUsername());
            config.setPassword(ConfigUtil.getAuthDbPassword());
            config.setMaximumPoolSize(ConfigUtil.getMaxPoolSize());
            config.setMinimumIdle(ConfigUtil.getMinIdle());
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            config.setPoolName("AuthDB-Pool");

            // Connection test query
            config.setConnectionTestQuery("SELECT 1");

            authDataSource = new HikariDataSource(config);
            logger.info("Auth DB connection pool initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize Auth DB connection pool", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    private static void initializeErpPool() {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(ConfigUtil.getErpDbUrl());
            config.setUsername(ConfigUtil.getErpDbUsername());
            config.setPassword(ConfigUtil.getErpDbPassword());
            config.setMaximumPoolSize(ConfigUtil.getMaxPoolSize());
            config.setMinimumIdle(ConfigUtil.getMinIdle());
            config.setConnectionTimeout(30000);
            config.setIdleTimeout(600000);
            config.setMaxLifetime(1800000);
            config.setPoolName("ErpDB-Pool");

            // Connection test query
            config.setConnectionTestQuery("SELECT 1");

            erpDataSource = new HikariDataSource(config);
            logger.info("ERP DB connection pool initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize ERP DB connection pool", e);
            throw new RuntimeException("Database initialization failed", e);
        }
    }

    /**
     * Get a connection to the Auth database
     */
    public static Connection getAuthConnection() throws SQLException {
        if (authDataSource == null || authDataSource.isClosed()) {
            logger.error("Auth DB connection pool is not available");
            throw new SQLException("Auth DB connection pool is not available");
        }
        return authDataSource.getConnection();
    }

    /**
     * Get a connection to the ERP database
     */
    public static Connection getErpConnection() throws SQLException {
        if (erpDataSource == null || erpDataSource.isClosed()) {
            logger.error("ERP DB connection pool is not available");
            throw new SQLException("ERP DB connection pool is not available");
        }
        return erpDataSource.getConnection();
    }

    /**
     * Close both connection pools
     */
    public static void shutdown() {
        logger.info("Shutting down database connection pools...");
        if (authDataSource != null && !authDataSource.isClosed()) {
            authDataSource.close();
            logger.info("Auth DB connection pool closed");
        }
        if (erpDataSource != null && !erpDataSource.isClosed()) {
            erpDataSource.close();
            logger.info("ERP DB connection pool closed");
        }
    }

    /**
     * Check if databases are accessible
     */
    public static boolean testConnections() {
        boolean authOk = false;
        boolean erpOk = false;

        try (Connection conn = getAuthConnection()) {
            authOk = conn.isValid(5);
            logger.info("Auth DB connection test: {}", authOk ? "SUCCESS" : "FAILED");
        } catch (SQLException e) {
            logger.error("Auth DB connection test failed", e);
        }

        try (Connection conn = getErpConnection()) {
            erpOk = conn.isValid(5);
            logger.info("ERP DB connection test: {}", erpOk ? "SUCCESS" : "FAILED");
        } catch (SQLException e) {
            logger.error("ERP DB connection test failed", e);
        }

        return authOk && erpOk;
    }
}
