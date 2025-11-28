package edu.univ.erp.auth;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Password hashing utility using BCrypt
 * Implements UNIX shadow-style password security
 */
public class PasswordHasher {
    private static final Logger logger = LoggerFactory.getLogger(PasswordHasher.class);
    private static final int BCRYPT_ROUNDS = 10;

    /**
     * Hash a plaintext password using BCrypt
     * 
     * @param plainPassword The plaintext password
     * @return BCrypt hash of the password
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }

        try {
            String hash = BCrypt.hashpw(plainPassword, BCrypt.gensalt(BCRYPT_ROUNDS));
            logger.debug("Password hashed successfully");
            return hash;
        } catch (Exception e) {
            logger.error("Error hashing password", e);
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    /**
     * Verify a plaintext password against a BCrypt hash
     * 
     * @param plainPassword  The plaintext password to verify
     * @param hashedPassword The BCrypt hash to verify against
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            logger.warn("Null password or hash provided for verification");
            return false;
        }

        try {
            boolean matches = BCrypt.checkpw(plainPassword, hashedPassword);
            logger.debug("Password verification: {}", matches ? "SUCCESS" : "FAILED");
            return matches;
        } catch (Exception e) {
            logger.error("Error verifying password", e);
            return false;
        }
    }

    /**
     * Check if a password needs rehashing (e.g., if BCrypt rounds have changed)
     * 
     * @param hashedPassword The current hash
     * @return true if rehashing is recommended
     */
    public static boolean needsRehash(String hashedPassword) {
        // BCrypt hashes start with $2a$<rounds>$
        if (hashedPassword == null || hashedPassword.length() < 7) {
            return true;
        }

        try {
            String roundsStr = hashedPassword.substring(4, 6);
            int rounds = Integer.parseInt(roundsStr);
            return rounds < BCRYPT_ROUNDS;
        } catch (Exception e) {
            logger.warn("Could not parse BCrypt rounds from hash", e);
            return true;
        }
    }
}
