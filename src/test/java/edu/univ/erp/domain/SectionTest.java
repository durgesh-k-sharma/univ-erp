package edu.univ.erp.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Section domain model
 */
@DisplayName("Section Domain Tests")
class SectionTest {

    @Test
    @DisplayName("Should create section with all fields")
    void testSectionCreation() {
        Section section = new Section(1, 1, "1", "Fall", 2024, 30);
        section.setCourseCode("CS101");
        section.setDayTime("MWF 10:00-11:00");
        section.setRoom("Room 101");
        section.setEnrolledCount(25);

        assertEquals(1, section.getSectionId());
        assertEquals("CS101", section.getCourseCode());
        assertEquals("1", section.getSectionNumber());
        assertEquals("Fall", section.getSemester());
        assertEquals(2024, section.getYear());
        assertEquals(30, section.getCapacity());
        assertEquals(25, section.getEnrolledCount());
    }

    @Test
    @DisplayName("Should detect when section is full")
    void testSectionIsFull() {
        Section fullSection = new Section(1, 1, "1", "Fall", 2024, 30);
        fullSection.setEnrolledCount(30);

        assertTrue(fullSection.isFull());
    }

    @Test
    @DisplayName("Should detect when section is not full")
    void testSectionIsNotFull() {
        Section notFullSection = new Section(1, 1, "1", "Fall", 2024, 30);
        notFullSection.setEnrolledCount(25);

        assertFalse(notFullSection.isFull());
    }

    @Test
    @DisplayName("Should calculate available seats")
    void testAvailableSeats() {
        Section section = new Section(1, 1, "1", "Fall", 2024, 30);
        section.setEnrolledCount(25);

        assertEquals(5, section.getAvailableSeats());
    }
}
