package edu.univ.erp.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Enrollment domain model
 */
@DisplayName("Enrollment Domain Tests")
class EnrollmentTest {

    @Test
    @DisplayName("Should create enrollment with all fields")
    void testEnrollmentCreation() {
        LocalDateTime dropDeadline = LocalDateTime.now().plusDays(14);
        Enrollment enrollment = new Enrollment(1, 1, 1, "ENROLLED");
        enrollment.setCourseCode("CS101");
        enrollment.setSectionNumber("1");
        enrollment.setSemester("Fall");
        enrollment.setYear(2024);
        enrollment.setDropDeadline(dropDeadline);

        assertEquals(1, enrollment.getEnrollmentId());
        assertEquals(1, enrollment.getStudentId());
        assertEquals(1, enrollment.getSectionId());
        assertEquals("CS101", enrollment.getCourseCode());
        assertEquals("ENROLLED", enrollment.getStatus());
    }

    @Test
    @DisplayName("Should allow drop before deadline")
    void testCanDropBeforeDeadline() {
        LocalDateTime futureDeadline = LocalDateTime.now().plusDays(7);
        Enrollment enrollment = new Enrollment(1, 1, 1, "ENROLLED");
        enrollment.setDropDeadline(futureDeadline);

        assertTrue(enrollment.canDrop());
    }

    @Test
    @DisplayName("Should not allow drop after deadline")
    void testCannotDropAfterDeadline() {
        LocalDateTime pastDeadline = LocalDateTime.now().minusDays(1);
        Enrollment enrollment = new Enrollment(1, 1, 1, "ENROLLED");
        enrollment.setDropDeadline(pastDeadline);

        assertFalse(enrollment.canDrop());
    }

    @Test
    @DisplayName("Should handle different enrollment statuses")
    void testEnrollmentStatuses() {
        LocalDateTime deadline = LocalDateTime.now().plusDays(14);

        Enrollment enrolled = new Enrollment(1, 1, 1, "ENROLLED");
        enrolled.setDropDeadline(deadline);

        Enrollment dropped = new Enrollment(2, 1, 2, "DROPPED");
        dropped.setDropDate(LocalDateTime.now());

        assertEquals("ENROLLED", enrolled.getStatus());
        assertEquals("DROPPED", dropped.getStatus());
        assertNotNull(dropped.getDropDate());
    }
}
