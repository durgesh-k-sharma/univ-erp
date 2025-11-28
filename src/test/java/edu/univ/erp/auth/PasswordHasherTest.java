package edu.univ.erp.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Unit tests for PasswordHasher utility
 */
@DisplayName("PasswordHasher Tests")
class PasswordHasherTest {

    @Test
    @DisplayName("Should hash password successfully")
    void testHashPassword() {
        String plainPassword = "mySecurePassword123";

        String hash = PasswordHasher.hashPassword(plainPassword);

        assertNotNull(hash, "Hash should not be null");
        assertThat(hash, startsWith("$2a$"));
        assertThat(hash.length(), greaterThan(50));
    }

    @Test
    @DisplayName("Should generate different hashes for same password")
    void testHashPasswordGeneratesDifferentHashes() {
        String plainPassword = "samePassword";

        String hash1 = PasswordHasher.hashPassword(plainPassword);
        String hash2 = PasswordHasher.hashPassword(plainPassword);

        assertNotEquals(hash1, hash2, "Two hashes of same password should be different due to salt");
    }

    @Test
    @DisplayName("Should throw exception for null password")
    void testHashPasswordWithNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordHasher.hashPassword(null);
        }, "Should throw IllegalArgumentException for null password");
    }

    @Test
    @DisplayName("Should throw exception for empty password")
    void testHashPasswordWithEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordHasher.hashPassword("");
        }, "Should throw IllegalArgumentException for empty password");
    }

    @Test
    @DisplayName("Should verify correct password")
    void testVerifyPasswordSuccess() {
        String plainPassword = "correctPassword";
        String hash = PasswordHasher.hashPassword(plainPassword);

        boolean result = PasswordHasher.verifyPassword(plainPassword, hash);

        assertTrue(result, "Should verify correct password");
    }

    @Test
    @DisplayName("Should reject incorrect password")
    void testVerifyPasswordFailure() {
        String plainPassword = "correctPassword";
        String wrongPassword = "wrongPassword";
        String hash = PasswordHasher.hashPassword(plainPassword);

        boolean result = PasswordHasher.verifyPassword(wrongPassword, hash);

        assertFalse(result, "Should reject incorrect password");
    }

    @Test
    @DisplayName("Should return false for null password in verification")
    void testVerifyPasswordWithNullPassword() {
        String hash = PasswordHasher.hashPassword("somePassword");

        boolean result = PasswordHasher.verifyPassword(null, hash);

        assertFalse(result, "Should return false for null password");
    }

    @Test
    @DisplayName("Should return false for null hash in verification")
    void testVerifyPasswordWithNullHash() {
        boolean result = PasswordHasher.verifyPassword("somePassword", null);

        assertFalse(result, "Should return false for null hash");
    }

    @Test
    @DisplayName("Should detect hash that needs rehashing")
    void testNeedsRehash() {
        // Create a hash with lower rounds (simulating old hash)
        String oldHash = "$2a$04$abcdefghijklmnopqrstuv1234567890123456789012";

        boolean result = PasswordHasher.needsRehash(oldHash);

        assertTrue(result, "Should detect hash with lower rounds needs rehashing");
    }

    @Test
    @DisplayName("Should detect current hash does not need rehashing")
    void testDoesNotNeedRehash() {
        String plainPassword = "currentPassword";
        String currentHash = PasswordHasher.hashPassword(plainPassword);

        boolean result = PasswordHasher.needsRehash(currentHash);

        assertFalse(result, "Current hash should not need rehashing");
    }

    @Test
    @DisplayName("Should return true for invalid hash format")
    void testNeedsRehashInvalidFormat() {
        boolean result = PasswordHasher.needsRehash("invalidhash");

        assertTrue(result, "Invalid hash should be flagged for rehashing");
    }

    @Test
    @DisplayName("Should return true for null hash")
    void testNeedsRehashNull() {
        boolean result = PasswordHasher.needsRehash(null);

        assertTrue(result, "Null hash should be flagged for rehashing");
    }
}
