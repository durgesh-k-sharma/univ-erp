package edu.univ.erp.util;

import edu.univ.erp.data.DatabaseManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Utility to apply SQL scripts (constraints, indexes, etc.)
 */
public class SqlScriptRunner {
    private static final Logger logger = LoggerFactory.getLogger(SqlScriptRunner.class);

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("SQL Script Runner - Database Enhancements");
        System.out.println("========================================");
        System.out.println();

        try {
            // Apply constraints
            System.out.println("Applying database constraints...");
            applySqlScript("sql/add_constraints.sql");
            System.out.println("✓ Constraints applied successfully");
            System.out.println();

            // Apply indexes
            System.out.println("Applying performance indexes...");
            applySqlScript("sql/add_indexes.sql");
            System.out.println("✓ Indexes applied successfully");
            System.out.println();

            System.out.println("========================================");
            System.out.println("✓ All database enhancements applied!");
            System.out.println("========================================");

        } catch (Exception e) {
            logger.error("Error applying SQL scripts", e);
            System.err.println("ERROR: " + e.getMessage());
            System.exit(1);
        } finally {
            DatabaseManager.shutdown();
        }
    }

    private static void applySqlScript(String filePath) throws Exception {
        Connection conn = DatabaseManager.getErpConnection();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath));
                Statement stmt = conn.createStatement()) {

            StringBuilder sql = new StringBuilder();
            String line;
            int lineNumber = 0;
            int statementsExecuted = 0;

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
                            statementsExecuted++;
                        } catch (Exception e) {
                            // Log warning but continue (constraint/index might already exist)
                            logger.warn("Statement at line {} failed (may already exist): {}",
                                    lineNumber, e.getMessage());
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
                    statementsExecuted++;
                } catch (Exception e) {
                    logger.warn("Final statement failed (may already exist): {}", e.getMessage());
                }
            }

            System.out.println("  Executed " + statementsExecuted + " statements from " + filePath);

        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
}
