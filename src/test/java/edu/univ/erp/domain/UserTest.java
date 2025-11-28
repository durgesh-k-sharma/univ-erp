package edu.univ.erp.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Unit tests for User domain model
 */
@DisplayName("User Domain Tests")
class UserTest {

    @Test
    @DisplayName("Should create user with all fields")
    void testUserCreation() {
        User user = new User(1, "student1", Role.STUDENT);
        user.setStatus("ACTIVE");

        assertEquals(1, user.getUserId());
        assertEquals("student1", user.getUsername());
        assertEquals(Role.STUDENT, user.getRole());
        assertEquals("ACTIVE", user.getStatus());
    }

    @Test
    @DisplayName("Should handle different roles")
    void testUserRoles() {
        User student = new User(1, "student1", Role.STUDENT);
        User instructor = new User(2, "instructor1", Role.INSTRUCTOR);
        User admin = new User(3, "admin1", Role.ADMIN);

        assertEquals(Role.STUDENT, student.getRole());
        assertEquals(Role.INSTRUCTOR, instructor.getRole());
        assertEquals(Role.ADMIN, admin.getRole());
    }

    @Test
    @DisplayName("Should handle different statuses")
    void testUserStatuses() {
        User activeUser = new User(1, "user1", Role.STUDENT);
        activeUser.setStatus("ACTIVE");

        User inactiveUser = new User(2, "user2", Role.STUDENT);
        inactiveUser.setStatus("INACTIVE");

        User lockedUser = new User(3, "user3", Role.STUDENT);
        lockedUser.setStatus("LOCKED");

        assertEquals("ACTIVE", activeUser.getStatus());
        assertEquals("INACTIVE", inactiveUser.getStatus());
        assertEquals("LOCKED", lockedUser.getStatus());
    }
}
