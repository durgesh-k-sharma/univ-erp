package edu.univ.erp.service;

import edu.univ.erp.access.AccessControl;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;

/**
 * Unit tests for StudentService
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("StudentService Tests")
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private SectionRepository sectionRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private GradeRepository gradeRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private SettingsRepository settingsRepository;

    private StudentService studentService;

    @BeforeEach
    void setUp() throws Exception {
        studentService = new StudentService();

        // Inject mocks using reflection
        injectMock("studentRepository", studentRepository);
        injectMock("sectionRepository", sectionRepository);
        injectMock("enrollmentRepository", enrollmentRepository);
        injectMock("gradeRepository", gradeRepository);
        injectMock("courseRepository", courseRepository);
        injectMock("settingsRepository", settingsRepository);
    }

    private void injectMock(String fieldName, Object mock) throws Exception {
        Field field = StudentService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(studentService, mock);
    }

    @Test
    @DisplayName("Should get student profile successfully")
    void testGetMyProfile() {
        // Arrange
        Student mockStudent = TestDataFactory.createStudent(1, 1, "S001", "CS", 2023);

        try (MockedStatic<SessionManager> sessionManager = mockStatic(SessionManager.class)) {
            sessionManager.when(SessionManager::getCurrentUserId).thenReturn(1);
            when(studentRepository.findByUserId(1)).thenReturn(mockStudent);

            // Act
            Student result = studentService.getMyProfile();

            // Assert
            assertNotNull(result);
            assertEquals("S001", result.getRollNo());
        }
    }

    @Test
    @DisplayName("Should return null when no user logged in")
    void testGetMyProfileNoUser() {
        try (MockedStatic<SessionManager> sessionManager = mockStatic(SessionManager.class)) {
            sessionManager.when(SessionManager::getCurrentUserId).thenReturn(null);

            // Act
            Student result = studentService.getMyProfile();

            // Assert
            assertNull(result);
            verifyNoInteractions(studentRepository);
        }
    }

    @Test
    @DisplayName("Should browse catalog successfully")
    void testBrowseCatalog() {
        // Arrange
        Section section1 = TestDataFactory.createSection(1, 1, "CS101", 1, 1, "Fall", 2024, "MWF 10:00-11:00",
                "Room 101", 30, 25);
        Section section2 = TestDataFactory.createSection(2, 2, "CS102", 1, 1, "Fall", 2024, "TTh 14:00-15:30",
                "Room 102", 25, 20);
        List<Section> sections = Arrays.asList(section1, section2);

        when(sectionRepository.findBySemesterYear("Fall", 2024)).thenReturn(sections);

        // Act
        List<Section> result = studentService.browseCatalog("Fall", 2024);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertThat(result, hasItem(section1));
    }

    @Test
    @DisplayName("Should register for section successfully")
    void testRegisterForSectionSuccess() {
        // Arrange
        Student mockStudent = TestDataFactory.createStudent(1, 1, "S001", "CS", 2023);
        Section mockSection = TestDataFactory.createSection(1, 1, "CS101", 1, 1, "Fall", 2024, "MWF 10:00-11:00",
                "Room 101", 30, 25);
        Course mockCourse = TestDataFactory.createCourse(1, "CS101", "Intro to CS", 3);

        try (MockedStatic<SessionManager> sessionManager = mockStatic(SessionManager.class);
                MockedStatic<AccessControl> accessControl = mockStatic(AccessControl.class)) {

            sessionManager.when(SessionManager::getCurrentUserId).thenReturn(1);
            accessControl.when(AccessControl::canModify).thenReturn(true);

            when(studentRepository.findByUserId(1)).thenReturn(mockStudent);
            when(sectionRepository.findById(1)).thenReturn(mockSection);
            when(enrollmentRepository.existsByStudentAndSection(1, 1)).thenReturn(false);
            when(enrollmentRepository.findByStudentAndSection(1, 1)).thenReturn(null);
            when(courseRepository.findById(1)).thenReturn(mockCourse);
            when(settingsRepository.getDropDeadlineDays()).thenReturn(14);
            when(enrollmentRepository.enrollStudent(eq(1), eq(1), any(LocalDateTime.class))).thenReturn(1);

            // Act
            StudentService.RegistrationResult result = studentService.registerForSection(1);

            // Assert
            assertTrue(result.isSuccess());
            assertThat(result.getMessage(), containsString("Successfully registered"));
            verify(enrollmentRepository).enrollStudent(eq(1), eq(1), any(LocalDateTime.class));
        }
    }

    @Test
    @DisplayName("Should fail registration in maintenance mode")
    void testRegisterForSectionMaintenanceMode() {
        try (MockedStatic<AccessControl> accessControl = mockStatic(AccessControl.class)) {
            accessControl.when(AccessControl::canModify).thenReturn(false);
            accessControl.when(AccessControl::getAccessDeniedMessage).thenReturn("System in maintenance mode");

            // Act
            StudentService.RegistrationResult result = studentService.registerForSection(1);

            // Assert
            assertFalse(result.isSuccess());
            assertThat(result.getMessage(), containsString("maintenance"));
        }
    }

    @Test
    @DisplayName("Should fail registration when student not found")
    void testRegisterForSectionStudentNotFound() {
        try (MockedStatic<SessionManager> sessionManager = mockStatic(SessionManager.class);
                MockedStatic<AccessControl> accessControl = mockStatic(AccessControl.class)) {

            sessionManager.when(SessionManager::getCurrentUserId).thenReturn(1);
            accessControl.when(AccessControl::canModify).thenReturn(true);
            when(studentRepository.findByUserId(1)).thenReturn(null);

            // Act
            StudentService.RegistrationResult result = studentService.registerForSection(1);

            // Assert
            assertFalse(result.isSuccess());
            assertEquals("Student profile not found", result.getMessage());
        }
    }

    @Test
    @DisplayName("Should fail registration when section not found")
    void testRegisterForSectionNotFound() {
        Student mockStudent = TestDataFactory.createStudent(1, 1, "S001", "CS", 2023);

        try (MockedStatic<SessionManager> sessionManager = mockStatic(SessionManager.class);
                MockedStatic<AccessControl> accessControl = mockStatic(AccessControl.class)) {

            sessionManager.when(SessionManager::getCurrentUserId).thenReturn(1);
            accessControl.when(AccessControl::canModify).thenReturn(true);
            when(studentRepository.findByUserId(1)).thenReturn(mockStudent);
            when(sectionRepository.findById(1)).thenReturn(null);

            // Act
            StudentService.RegistrationResult result = studentService.registerForSection(1);

            // Assert
            assertFalse(result.isSuccess());
            assertEquals("Section not found", result.getMessage());
        }
    }

    @Test
    @DisplayName("Should fail registration when already enrolled")
    void testRegisterForSectionAlreadyEnrolled() {
        Student mockStudent = TestDataFactory.createStudent(1, 1, "S001", "CS", 2023);
        Section mockSection = TestDataFactory.createSection(1, 1, "CS101", 1, 1, "Fall", 2024, "MWF 10:00-11:00",
                "Room 101", 30, 25);

        try (MockedStatic<SessionManager> sessionManager = mockStatic(SessionManager.class);
                MockedStatic<AccessControl> accessControl = mockStatic(AccessControl.class)) {

            sessionManager.when(SessionManager::getCurrentUserId).thenReturn(1);
            accessControl.when(AccessControl::canModify).thenReturn(true);
            when(studentRepository.findByUserId(1)).thenReturn(mockStudent);
            when(sectionRepository.findById(1)).thenReturn(mockSection);
            when(enrollmentRepository.existsByStudentAndSection(1, 1)).thenReturn(true);

            // Act
            StudentService.RegistrationResult result = studentService.registerForSection(1);

            // Assert
            assertFalse(result.isSuccess());
            assertThat(result.getMessage(), containsString("already enrolled"));
        }
    }

    @Test
    @DisplayName("Should fail registration when section is full")
    void testRegisterForSectionFull() {
        Student mockStudent = TestDataFactory.createStudent(1, 1, "S001", "CS", 2023);
        Section fullSection = TestDataFactory.createSection(1, 1, "CS101", 1, 1, "Fall", 2024, "MWF 10:00-11:00",
                "Room 101", 30, 30);

        try (MockedStatic<SessionManager> sessionManager = mockStatic(SessionManager.class);
                MockedStatic<AccessControl> accessControl = mockStatic(AccessControl.class)) {

            sessionManager.when(SessionManager::getCurrentUserId).thenReturn(1);
            accessControl.when(AccessControl::canModify).thenReturn(true);
            when(studentRepository.findByUserId(1)).thenReturn(mockStudent);
            when(sectionRepository.findById(1)).thenReturn(fullSection);
            when(enrollmentRepository.existsByStudentAndSection(1, 1)).thenReturn(false);
            when(enrollmentRepository.findByStudentAndSection(1, 1)).thenReturn(null);

            // Act
            StudentService.RegistrationResult result = studentService.registerForSection(1);

            // Assert
            assertFalse(result.isSuccess());
            assertThat(result.getMessage(), containsString("full"));
        }
    }

    @Test
    @DisplayName("Should drop section successfully")
    void testDropSectionSuccess() {
        Student mockStudent = TestDataFactory.createStudent(1, 1, "S001", "CS", 2023);
        Enrollment mockEnrollment = TestDataFactory.createEnrollment(1, 1, 1, "ENROLLED");
        mockEnrollment.setCourseCode("CS101");
        mockEnrollment.setSectionNumber("1");
        mockEnrollment.setSemester("Fall");
        mockEnrollment.setYear(2024);
        mockEnrollment.setDropDeadline(LocalDateTime.now().plusDays(10));

        try (MockedStatic<SessionManager> sessionManager = mockStatic(SessionManager.class);
                MockedStatic<AccessControl> accessControl = mockStatic(AccessControl.class)) {

            sessionManager.when(SessionManager::getCurrentUserId).thenReturn(1);
            accessControl.when(AccessControl::canModify).thenReturn(true);
            when(studentRepository.findByUserId(1)).thenReturn(mockStudent);
            when(enrollmentRepository.findById(1)).thenReturn(mockEnrollment);
            when(enrollmentRepository.dropEnrollment(1)).thenReturn(true);

            // Act
            StudentService.DropResult result = studentService.dropSection(1);

            // Assert
            assertTrue(result.isSuccess());
            assertThat(result.getMessage(), containsString("Successfully dropped"));
            verify(enrollmentRepository).dropEnrollment(1);
        }
    }

    @Test
    @DisplayName("Should fail drop when enrollment not found")
    void testDropSectionNotFound() {
        try (MockedStatic<AccessControl> accessControl = mockStatic(AccessControl.class)) {
            accessControl.when(AccessControl::canModify).thenReturn(true);
            when(enrollmentRepository.findById(1)).thenReturn(null);

            // Act
            StudentService.DropResult result = studentService.dropSection(1);

            // Assert
            assertFalse(result.isSuccess());
            assertEquals("Enrollment not found", result.getMessage());
        }
    }

    @Test
    @DisplayName("Should fail drop when already dropped")
    void testDropSectionAlreadyDropped() {
        Student mockStudent = TestDataFactory.createStudent(1, 1, "S001", "CS", 2023);
        Enrollment droppedEnrollment = TestDataFactory.createEnrollment(1, 1, 1, "DROPPED");
        droppedEnrollment.setDropDeadline(LocalDateTime.now().plusDays(10));

        try (MockedStatic<SessionManager> sessionManager = mockStatic(SessionManager.class);
                MockedStatic<AccessControl> accessControl = mockStatic(AccessControl.class)) {

            sessionManager.when(SessionManager::getCurrentUserId).thenReturn(1);
            accessControl.when(AccessControl::canModify).thenReturn(true);
            when(studentRepository.findByUserId(1)).thenReturn(mockStudent);
            when(enrollmentRepository.findById(1)).thenReturn(droppedEnrollment);

            // Act
            StudentService.DropResult result = studentService.dropSection(1);

            // Assert
            assertFalse(result.isSuccess());
            assertThat(result.getMessage(), containsString("already been dropped"));
        }
    }

    @Test
    @DisplayName("Should get my registrations")
    void testGetMyRegistrations() {
        Student mockStudent = TestDataFactory.createStudent(1, 1, "S001", "CS", 2023);
        Enrollment enrollment1 = TestDataFactory.createEnrollment(1, 1, 1, "ENROLLED");
        Enrollment enrollment2 = TestDataFactory.createEnrollment(2, 1, 2, "ENROLLED");

        try (MockedStatic<SessionManager> sessionManager = mockStatic(SessionManager.class)) {
            sessionManager.when(SessionManager::getCurrentUserId).thenReturn(1);
            when(studentRepository.findByUserId(1)).thenReturn(mockStudent);
            when(enrollmentRepository.findByStudent(1)).thenReturn(Arrays.asList(enrollment1, enrollment2));

            // Act
            List<Enrollment> result = studentService.getMyRegistrations();

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
        }
    }

    @Test
    @DisplayName("Should get my grades")
    void testGetMyGrades() {
        Student mockStudent = TestDataFactory.createStudent(1, 1, "S001", "CS", 2023);
        Grade grade1 = TestDataFactory.createGrade(1, "Assignment 1", 95.0, 100.0, 20.0);
        Grade grade2 = TestDataFactory.createGrade(2, "Midterm", 88.0, 100.0, 30.0);

        try (MockedStatic<SessionManager> sessionManager = mockStatic(SessionManager.class)) {
            sessionManager.when(SessionManager::getCurrentUserId).thenReturn(1);
            when(studentRepository.findByUserId(1)).thenReturn(mockStudent);
            when(gradeRepository.findByStudent(1)).thenReturn(Arrays.asList(grade1, grade2));

            // Act
            List<Grade> result = studentService.getMyGrades();

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
        }
    }

    @Test
    @DisplayName("Should get timetable with only enrolled sections")
    void testGetMyTimetable() {
        Student mockStudent = TestDataFactory.createStudent(1, 1, "S001", "CS", 2023);
        Enrollment enrolled = TestDataFactory.createEnrollment(1, 1, 1, "ENROLLED");
        Enrollment dropped = TestDataFactory.createEnrollment(2, 1, 2, "DROPPED");

        try (MockedStatic<SessionManager> sessionManager = mockStatic(SessionManager.class)) {
            sessionManager.when(SessionManager::getCurrentUserId).thenReturn(1);
            when(studentRepository.findByUserId(1)).thenReturn(mockStudent);
            when(enrollmentRepository.findByStudent(1)).thenReturn(Arrays.asList(enrolled, dropped));

            // Act
            List<Enrollment> result = studentService.getMyTimetable();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals("ENROLLED", result.get(0).getStatus());
        }
    }
}
