package edu.univ.erp.util;

import edu.univ.erp.auth.PasswordHasher;
import edu.univ.erp.data.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CheckUsers {
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CheckUsers.class);

    public static void main(String[] args) {
        try {
            logger.info("Checking users in database...");
            Connection conn = DatabaseManager.getAuthConnection();
            String sql = "SELECT username, role, status, password_hash FROM users_auth";

            try (PreparedStatement stmt = conn.prepareStatement(sql);
                    ResultSet rs = stmt.executeQuery()) {

                logger.info("--- USERS FOUND ---");
                // Using System.out for table format as it's a CLI tool output
                System.out.printf("%-15s %-15s %-10s %s%n", "USERNAME", "ROLE", "STATUS", "HASH START");
                System.out.println("------------------------------------------------------------");

                boolean found = false;
                while (rs.next()) {
                    found = true;
                    String username = rs.getString("username");
                    String hash = rs.getString("password_hash");
                    String hashStart = (hash != null && hash.length() > 10) ? hash.substring(0, 10) + "..." : hash;

                    System.out.printf("%-15s %-15s %-10s %s%n",
                            username,
                            rs.getString("role"),
                            rs.getString("status"),
                            hashStart);

                    // Verify password for admin1
                    if ("admin1".equals(username)) {
                        boolean match = PasswordHasher.verifyPassword("password123", hash);
                        logger.info("Password check for 'admin1' with 'password123': {}", (match ? "MATCH" : "FAIL"));
                        if (!match) {
                            String newHash = PasswordHasher.hashPassword("password123");
                            logger.info("FULL NEW HASH: {}", newHash);
                        }
                    }
                }

                if (!found) {
                    logger.warn("NO USERS FOUND! Did you run auth_seed.sql?");
                }
            }
            System.exit(0);
        } catch (Exception e) {
            logger.error("Error checking users", e);
            System.exit(1);
        }
    }
}
