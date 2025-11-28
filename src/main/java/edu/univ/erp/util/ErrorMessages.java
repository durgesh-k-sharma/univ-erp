package edu.univ.erp.util;

/**
 * Centralized error messages for consistent user feedback
 */
public class ErrorMessages {

    // ========================================
    // Access Control Messages
    // ========================================
    public static final String UNAUTHORIZED = "You are not authorized to perform this action.";
    public static final String MAINTENANCE_MODE = "System is in maintenance mode. Modifications are currently disabled.";
    public static final String NOT_YOUR_SECTION = "You can only modify sections assigned to you.";
    public static final String NOT_YOUR_DATA = "You can only access your own data.";
    public static final String ADMIN_ONLY = "This action requires administrator privileges.";
    public static final String INSTRUCTOR_ONLY = "This action is only available to instructors.";
    public static final String STUDENT_ONLY = "This action is only available to students.";
    public static final String NOT_LOGGED_IN = "Please log in to continue.";

    // ========================================
    // Registration & Enrollment Messages
    // ========================================
    public static final String SECTION_FULL = "This section is full. Please choose another section.";
    public static final String ALREADY_REGISTERED = "You are already registered for this section.";
    public static final String DEADLINE_PASSED = "The registration/drop deadline has passed.";
    public static final String PREREQUISITE_NOT_MET = "You have not completed the required prerequisite courses.";
    public static final String TIME_CONFLICT = "This section conflicts with your existing schedule.";
    public static final String NOT_ENROLLED = "You are not enrolled in this section.";
    public static final String CANNOT_DROP_COMPLETED = "Cannot drop a completed course.";

    // ========================================
    // Validation Messages
    // ========================================
    public static final String INVALID_CAPACITY = "Capacity must be a positive number.";
    public static final String INVALID_CREDITS = "Credits must be between 1 and 6.";
    public static final String INVALID_EMAIL = "Please enter a valid email address.";
    public static final String INVALID_PHONE = "Phone must be in format XXX-XXXX or XXXXXXXXXX.";
    public static final String INVALID_ROLL_NO = "Roll number must be in format YYYYPPNNN (e.g., 2023CS001).";
    public static final String INVALID_COURSE_CODE = "Course code must be in format LLLNNN (e.g., CSE101).";
    public static final String INVALID_USERNAME = "Username must be 3-20 characters, start with a letter, and contain only lowercase letters, numbers, dots, and underscores.";
    public static final String INVALID_PASSWORD = "Password must be at least 8 characters and contain at least one letter and one number.";
    public static final String INVALID_SEMESTER = "Semester must be FALL, SPRING, or SUMMER.";
    public static final String INVALID_YEAR = "Year must be within 2 years of the current year.";
    public static final String INVALID_SCORE = "Score must be between 0 and the maximum score.";
    public static final String INVALID_WEIGHT = "Weight must be between 0 and 100.";
    public static final String EMPTY_FIELD = "This field cannot be empty.";

    // ========================================
    // Data Integrity Messages
    // ========================================
    public static final String DUPLICATE_ENROLLMENT = "This enrollment already exists in the system.";
    public static final String DUPLICATE_USERNAME = "This username is already taken.";
    public static final String DUPLICATE_COURSE_CODE = "A course with this code already exists.";
    public static final String DUPLICATE_ROLL_NO = "This roll number is already assigned to another student.";
    public static final String DUPLICATE_EMPLOYEE_ID = "This employee ID is already assigned to another instructor.";
    public static final String SECTION_HAS_ENROLLMENTS = "Cannot delete section with enrolled students. Please drop all students first.";
    public static final String COURSE_HAS_SECTIONS = "Cannot delete course with existing sections. Please delete all sections first.";
    public static final String USER_HAS_DATA = "Cannot delete user with existing data in the system.";
    public static final String CAPACITY_BELOW_ENROLLED = "Cannot set capacity below the current number of enrolled students.";

    // ========================================
    // Grade Entry Messages
    // ========================================
    public static final String GRADE_ALREADY_EXISTS = "A grade for this component already exists.";
    public static final String MISSING_GRADE_COMPONENTS = "Cannot compute final grade. Some assessment components are missing.";
    public static final String INVALID_GRADE_WEIGHTS = "Grade component weights must sum to 100%.";
    public static final String GRADE_NOT_FOUND = "Grade record not found.";

    // ========================================
    // Authentication Messages
    // ========================================
    public static final String INVALID_CREDENTIALS = "Invalid username or password.";
    public static final String ACCOUNT_LOCKED = "Your account has been locked due to multiple failed login attempts. Please contact an administrator.";
    public static final String PASSWORD_MISMATCH = "Passwords do not match.";
    public static final String INCORRECT_OLD_PASSWORD = "The old password you entered is incorrect.";
    public static final String PASSWORD_CHANGED_SUCCESS = "Password changed successfully. Please log in with your new password.";

    // ========================================
    // Database & System Messages
    // ========================================
    public static final String DATABASE_ERROR = "A database error occurred. Please try again or contact support.";
    public static final String UNEXPECTED_ERROR = "An unexpected error occurred. Please try again or contact support.";
    public static final String CONNECTION_ERROR = "Unable to connect to the database. Please check your connection and try again.";
    public static final String OPERATION_FAILED = "The operation could not be completed. Please try again.";

    // ========================================
    // Success Messages
    // ========================================
    public static final String REGISTRATION_SUCCESS = "Successfully registered for the section.";
    public static final String DROP_SUCCESS = "Successfully dropped the section.";
    public static final String GRADE_SAVED_SUCCESS = "Grade saved successfully.";
    public static final String FINAL_GRADE_COMPUTED = "Final grade computed successfully.";
    public static final String USER_CREATED_SUCCESS = "User created successfully.";
    public static final String COURSE_CREATED_SUCCESS = "Course created successfully.";
    public static final String SECTION_CREATED_SUCCESS = "Section created successfully.";
    public static final String UPDATE_SUCCESS = "Update completed successfully.";
    public static final String DELETE_SUCCESS = "Deletion completed successfully.";
    public static final String MAINTENANCE_MODE_ENABLED = "Maintenance mode has been enabled. Students and instructors can view but cannot modify data.";
    public static final String MAINTENANCE_MODE_DISABLED = "Maintenance mode has been disabled. Normal operations have resumed.";

    // ========================================
    // Helper Methods
    // ========================================

    /**
     * Format a message with a parameter
     */
    public static String format(String template, Object... args) {
        return String.format(template, args);
    }

    /**
     * Get a custom "not found" message
     */
    public static String notFound(String entityType) {
        return format("%s not found.", entityType);
    }

    /**
     * Get a custom "already exists" message
     */
    public static String alreadyExists(String entityType) {
        return format("%s already exists.", entityType);
    }

    /**
     * Get a custom "cannot delete" message
     */
    public static String cannotDelete(String entityType, String reason) {
        return format("Cannot delete %s: %s", entityType, reason);
    }
}
