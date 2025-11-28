package edu.univ.erp.util;

import edu.univ.erp.data.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Utility to load seed data from SQL files
 */
public class SeedDataLoader {
    private static final Logger logger = LoggerFactory.getLogger(SeedDataLoader.class);

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("University ERP - Loading Seed Data");
        System.out.println("========================================");
        System.out.println();

        try {
            // Load Auth DB seed data
            System.out.println("Loading Auth DB seed data...");
            loadSqlFile("sql/auth_seed.sql", true);
            System.out.println("✓ Auth DB seed data loaded successfully");
            System.out.println();

            // Load ERP DB seed data
            System.out.println("Loading ERP DB seed data...");
            loadSqlFile("sql/erp_seed.sql", false);
            System.out.println("✓ ERP DB seed data loaded successfully");
            System.out.println();

            System.out.println("========================================");
            System.out.println("✓ All seed data loaded successfully!");
            System.out.println("========================================");
            System.out.println();
            System.out.println("Default users created:");
            System.out.println("  Admin:      admin / password123");
            System.out.println("  Instructor: john.doe / password123");
            System.out.println("  Instructor: jane.smith / password123");
            System.out.println("  Student:    alice.smith / password123");
            System.out.println("  Student:    bob.jones / password123");
            System.out.println();

        } catch (Exception e) {
            logger.error("Error loading seed data", e);
            System.err.println("ERROR: " + e.getMessage());
            System.exit(1);
        } finally {
            DatabaseManager.shutdown();
        }
    }

    private static void loadSqlFile(String filePath, boolean isAuthDb) throws Exception {
        Connection conn = isAuthDb ? DatabaseManager.getAuthConnection() : DatabaseManager.getErpConnection();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
                Statement stmt = conn.createStatement()) {

            StringBuilder sql = new StringBuilder();
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                // Skip comments and empty lines
                if (line.isEmpty() || line.startsWith("--") || line.startsWith("#")) {
                    continue;
                }

                // Skip USE statements (we're already connected to the right database)
                if (line.toUpperCase().startsWith("USE ")) {
                    continue;
                }

                sql.append(line).append(" ");

                // Execute when we hit a semicolon at the end of a line
                if (line.endsWith(";")) {
                    String statement = sql.toString().trim();
                    if (!statement.isEmpty() && !statement.equals(";")) {
                        try {
                            stmt.execute(statement);
                        } catch (Exception e) {
                            logger.warn("Error executing statement at line {}: {}", lineNumber, e.getMessage());
                            // Continue with next statement
                        }
                    }
                    sql = new StringBuilder();
                }
            }

            // Execute any remaining SQL
            String remaining = sql.toString().trim();
            if (!remaining.isEmpty() && !remaining.equals(";")) {
                try {
                    stmt.execute(remaining);
                } catch (Exception e) {
                    logger.warn("Error executing final statement: {}", e.getMessage());
                }
            }

        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
}
