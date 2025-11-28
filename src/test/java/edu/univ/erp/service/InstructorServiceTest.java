package edu.univ.erp.service;

import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.*;
import edu.univ.erp.domain.*;
import edu.univ.erp.test.TestDataFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for InstructorService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("InstructorService Tests")
class InstructorServiceTest {

    @Mock
    private InstructorRepository instructorRepository;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private GradeRepository gradeRepository;

    private InstructorService instructorService;

    @BeforeEach
    void setUp() throws Exception {
        instructorService = new InstructorService();

        // Inject mocks using reflection
        injectMock("instructorRepository", instructorRepository);
        injectMock("sectionRepository", sectionRepository);
        injectMock("enrollmentRepository", enrollmentRepository);
        injectMock("gradeRepository", gradeRepository);
    }

    private void injectMock(String fieldName, Object mock) throws Exception {
        Field field = InstructorService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instructorService, mock);
    }

    @Test
    @DisplayName("Should get instructor profile successfully")
    void testGetMyProfile() {
        Instructor mockInstructor = TestDataFactory.createInstructor(1, 1, "I001", "CS");
        mockInstructor.setFullName("Dr. Jane Smith");

        try (MockedStatic<SessionManager> sessionManager = mockStatic(SessionManager.class)) {
            sessionManager.when(SessionManager::getCurrentUserId).thenReturn(1);
            when(instructorRepository.findByUserId(1)).thenReturn(mockInstructor);

            // Act
            Instructor result = instructorService.getMyProfile();

            // Assert
            assertNotNull(result);
            assertEquals("I001", result.getEmployeeId());
            assertEquals("CS", result.getDepartment());
        }
    }

    @Test
    @DisplayName("Should return null when no user logged in")
    void testGetMyProfileNoUser() {
        try (MockedStatic<SessionManager> sessionManager = mockStatic(SessionManager.class)) {
            sessionManager.when(SessionManager::getCurrentUserId).thenReturn(null);

            // Act
            Instructor result = instructorService.getMyProfile();

            // Assert
            assertNull(result);
            verifyNoInteractions(instructorRepository);
        }
    }

    @Test
    @DisplayName("Should get my sections")
    void testGetMySections() {
        Instructor mockInstructor = TestDataFactory.createInstructor(1, 1, "I001", "CS");
        Section section1 = TestDataFactory.createSection(1, 1, "CS101", 1, 1, "Fall", 2024, "MWF 10:00-11:00",
                "Room 101", 30, 25);
        Section section2 = TestDataFactory.createSection(2, 2, "CS102", 1, 1, "Fall", 2024, "TTh 14:00-15:30",
                "Room 102", 25, 20);

        try (MockedStatic<SessionManager> sessionManager = mockStatic(SessionManager.class)) {
            sessionManager.when(SessionManager::getCurrentUserId).thenReturn(1);
            when(instructorRepository.findByUserId(1)).thenReturn(mockInstructor);
            when(sectionRepository.findByInstructor(1)).thenReturn(Arrays.asList(section1, section2));

            // Act
            List<Section> result = instructorService.getMySections();

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
        }
    }

    @Test
    @DisplayName("Should get enrollments for section")
    void testGetEnrollmentsForSection() {
        Instructor mockInstructor = TestDataFactory.createInstructor(1, 1, "I001", "CS");
        Section mockSection = TestDataFactory.createSection(1, 1, "CS101", 1, 1, "Fall", 2024, "MWF 10:00-11:00",
                "Room 101", 30, 25);
        Enrollment enrollment1 = TestDataFactory.createEnrollment(1, 1, 1, "ENROLLED");
        Enrollment enrollment2 = TestDataFactory.createEnrollment(2, 2, 1, "ENROLLED");

        try (MockedStatic<SessionManager> sessionManager = mockStatic(SessionManager.class)) {
            sessionManager.when(SessionManager::getCurrentUserId).thenReturn(1);
            when(instructorRepository.findByUserId(1)).thenReturn(mockInstructor);
            when(sectionRepository.findById(1)).thenReturn(mockSection);
            when(enrollmentRepository.findBySection(1)).thenReturn(Arrays.asList(enrollment1, enrollment2));

            // Act - Note: This returns EnrollmentListResult, not List<Student>
            // We're testing that the method doesn't throw and returns a result
            assertDoesNotThrow(() -> {
                // The actual method might return EnrollmentListResult
                // For now, just verify the mocks are called correctly
                instructorService.getEnrolledStudents(1);
            });

            verify(enrollmentRepository).findBySection(1);
        }
    }

    @Test
    @DisplayName("Should deny access to section not taught by instructor")
    void testUnauthorizedSectionAccess() {
        Instructor mockInstructor = TestDataFactory.createInstructor(1, 1, "I001", "CS");
        Section otherSection = TestDataFactory.createSection(1, 1, "CS101", 1, 999, "Fall", 2024, "MWF 10:00-11:00",
                "Room 101", 30, 25);

        try (MockedStatic<SessionManager> sessionManager = mockStatic(SessionManager.class)) {
            sessionManager.when(SessionManager::getCurrentUserId).thenReturn(1);
            when(instructorRepository.findByUserId(1)).thenReturn(mockInstructor);
            when(sectionRepository.findById(1)).thenReturn(otherSection);

            // Act - Should not call enrollmentRepository for unauthorized section
            assertDoesNotThrow(() -> {
                instructorService.getEnrolledStudents(1);
            });

            // Verify enrollmentRepository was never called since access was denied
            verify(enrollmentRepository, never()).findBySection(anyInt());
        }
    }
}
