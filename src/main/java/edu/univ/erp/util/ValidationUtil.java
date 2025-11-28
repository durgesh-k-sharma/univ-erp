package edu.univ.erp.util;

import java.time.Year;
import java.util.regex.Pattern;

/**
 * Centralized validation utility for enterprise-grade input validation
 */
public class ValidationUtil {

    // Regex patterns
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[0-9]{3}-[0-9]{4}$|^[0-9]{10}$");
    private static final Pattern ROLL_NO_PATTERN = Pattern.compile(
            "^[0-9]{4}[A-Z]{2,3}[0-9]{3}$");
    private static final Pattern COURSE_CODE_PATTERN = Pattern.compile(
            "^[A-Z]{3}[0-9]{3}$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile(
            "^[a-z][a-z0-9._]{2,19}$");

    /**
     * Validate capacity (must be positive)
     */
    public static ValidationResult validateCapacity(int capacity) {
        if (capacity <= 0) {
            return ValidationResult.error("Capacity must be a positive number (greater than 0).");
        }
        if (capacity > 500) {
            return ValidationResult.error("Capacity cannot exceed 500 students.");
        }
        return ValidationResult.success();
    }

    /**
     * Validate credits (typically 1-6 range)
     */
    public static ValidationResult validateCredits(int credits) {
        if (credits < 1 || credits > 6) {
            return ValidationResult.error("Credits must be between 1 and 6.");
        }
        return ValidationResult.success();
    }

    /**
     * Validate semester
     */
    public static ValidationResult validateSemester(String semester) {
        if (semester == null || semester.trim().isEmpty()) {
            return ValidationResult.error("Semester cannot be empty.");
        }

        String sem = semester.trim().toUpperCase();
        if (!sem.equals("FALL") && !sem.equals("SPRING") && !sem.equals("SUMMER")) {
            return ValidationResult.error("Semester must be FALL, SPRING, or SUMMER.");
        }
        return ValidationResult.success();
    }

    /**
     * Validate year (must be reasonable - within 2 years of current)
     */
    public static ValidationResult validateYear(int year) {
        int currentYear = Year.now().getValue();
        if (year < currentYear - 2 || year > currentYear + 2) {
            return ValidationResult.error(
                    String.format("Year must be between %d and %d.", currentYear - 2, currentYear + 2));
        }
        return ValidationResult.success();
    }

    /**
     * Validate semester and year combination
     */
    public static ValidationResult validateSemesterYear(String semester, int year) {
        ValidationResult semResult = validateSemester(semester);
        if (!semResult.isValid()) {
            return semResult;
        }

        ValidationResult yearResult = validateYear(year);
        if (!yearResult.isValid()) {
            return yearResult;
        }

        return ValidationResult.success();
    }

    /**
     * Validate username format
     */
    public static ValidationResult validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return ValidationResult.error("Username cannot be empty.");
        }

        if (!USERNAME_PATTERN.matcher(username).matches()) {
            return ValidationResult.error(
                    "Username must start with a letter, be 3-20 characters long, " +
                            "and contain only lowercase letters, numbers, dots, and underscores.");
        }
        return ValidationResult.success();
    }

    /**
     * Validate email format
     */
    public static ValidationResult validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return ValidationResult.error("Email cannot be empty.");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return ValidationResult.error("Please enter a valid email address (e.g., user@example.com).");
        }
        return ValidationResult.success();
    }

    /**
     * Validate phone format
     */
    public static ValidationResult validatePhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return ValidationResult.success(); // Phone is optional
        }

        if (!PHONE_PATTERN.matcher(phone).matches()) {
            return ValidationResult.error("Phone must be in format XXX-XXXX or XXXXXXXXXX.");
        }
        return ValidationResult.success();
    }

    /**
     * Validate roll number format
     */
    public static ValidationResult validateRollNo(String rollNo) {
        if (rollNo == null || rollNo.trim().isEmpty()) {
            return ValidationResult.error("Roll number cannot be empty.");
        }

        if (!ROLL_NO_PATTERN.matcher(rollNo).matches()) {
            return ValidationResult.error(
                    "Roll number must be in format YYYYPPNNN (e.g., 2023CS001).");
        }
        return ValidationResult.success();
    }

    /**
     * Validate course code format
     */
    public static ValidationResult validateCourseCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return ValidationResult.error("Course code cannot be empty.");
        }

        if (!COURSE_CODE_PATTERN.matcher(code).matches()) {
            return ValidationResult.error(
                    "Course code must be in format LLLNNN (e.g., CSE101).");
        }
        return ValidationResult.success();
    }

    /**
     * Validate password strength
     */
    public static ValidationResult validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return ValidationResult.error("Password cannot be empty.");
        }

        if (password.length() < 8) {
            return ValidationResult.error("Password must be at least 8 characters long.");
        }

        if (password.length() > 128) {
            return ValidationResult.error("Password cannot exceed 128 characters.");
        }

        // Check for at least one letter and one number
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*[0-9].*");

        if (!hasLetter || !hasDigit) {
            return ValidationResult.error("Password must contain at least one letter and one number.");
        }

        return ValidationResult.success();
    }

    /**
     * Validate score (must be between 0 and max_score)
     */
    public static ValidationResult validateScore(double score, double maxScore) {
        if (score < 0) {
            return ValidationResult.error("Score cannot be negative.");
        }

        if (score > maxScore) {
            return ValidationResult.error(
                    String.format("Score (%.2f) cannot exceed maximum score (%.2f).", score, maxScore));
        }

        return ValidationResult.success();
    }

    /**
     * Validate weight (must be between 0 and 100)
     */
    public static ValidationResult validateWeight(double weight) {
        if (weight < 0 || weight > 100) {
            return ValidationResult.error("Weight must be between 0 and 100.");
        }
        return ValidationResult.success();
    }

    /**
     * Validate that a string is not empty
     */
    public static ValidationResult validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            return ValidationResult.error(fieldName + " cannot be empty.");
        }
        return ValidationResult.success();
    }

    /**
     * Validation result class
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String message;

        private ValidationResult(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, null);
        }

        public static ValidationResult error(String message) {
            return new ValidationResult(false, message);
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }
}
