package edu.univ.erp.service;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.*;
import edu.univ.erp.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for student operations
 */
public class StudentService {
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    private final StudentRepository studentRepository;
    private final SectionRepository sectionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final GradeRepository gradeRepository;
    private final CourseRepository courseRepository;
    private final SettingsRepository settingsRepository;

    public StudentService() {
        this.studentRepository = new StudentRepository();
        this.sectionRepository = new SectionRepository();
        this.enrollmentRepository = new EnrollmentRepository();
        this.gradeRepository = new GradeRepository();
        this.courseRepository = new CourseRepository();
        this.settingsRepository = new SettingsRepository();
    }

    /**
     * Get student profile for current user
     */
    public Student getMyProfile() {
        Integer userId = SessionManager.getCurrentUserId();
        if (userId == null) {
            logger.warn("Cannot get profile: No user logged in");
            return null;
        }

        return studentRepository.findByUserId(userId);
    }

    /**
     * Browse course catalog (all available sections)
     */
    public List<Section> browseCatalog(String semester, int year) {
        logger.debug("Browsing catalog for {} {}", semester, year);
        return sectionRepository.findBySemesterYear(semester, year);
    }

    /**
     * Register for a section
     */
    public RegistrationResult registerForSection(int sectionId) {
        // Check if user can modify (not in maintenance mode)
        if (!AccessControl.canModify()) {
            return new RegistrationResult(false, AccessControl.getAccessDeniedMessage());
        }

        // Get current student
        Student student = getMyProfile();
        if (student == null) {
            return new RegistrationResult(false, "Student profile not found");
        }

        // Get section details
        Section section = sectionRepository.findById(sectionId);
        if (section == null) {
            return new RegistrationResult(false, "Section not found");
        }

        // Check if already actively enrolled
        if (enrollmentRepository.existsByStudentAndSection(student.getStudentId(), sectionId)) {
            return new RegistrationResult(false, "You are already enrolled in this section");
        }

        // Check if there's a dropped enrollment that can be reused
        Enrollment existingEnrollment = enrollmentRepository.findByStudentAndSection(student.getStudentId(), sectionId);

        // Check capacity
        if (section.isFull()) {
            return new RegistrationResult(false, "Section is full. No seats available.");
        }

        // Check prerequisites
        Course course = courseRepository.findById(section.getCourseId());
        if (course != null && course.getPrerequisites() != null && !course.getPrerequisites().isEmpty()) {
            String prereqResult = checkPrerequisites(student, course.getPrerequisites());
            if (prereqResult != null) {
                return new RegistrationResult(false, "Prerequisite not met: " + prereqResult);
            }
        }

        // Calculate drop deadline
        int dropDeadlineDays = settingsRepository.getDropDeadlineDays();
        LocalDateTime dropDeadline = LocalDateTime.now().plusDays(dropDeadlineDays);

        int enrollmentId;

        // If there's a dropped enrollment, reactivate it
        if (existingEnrollment != null && "DROPPED".equals(existingEnrollment.getStatus())) {
            boolean reactivated = enrollmentRepository.reactivateEnrollment(existingEnrollment.getEnrollmentId(),
                    dropDeadline);
            if (reactivated) {
                enrollmentId = existingEnrollment.getEnrollmentId();
                logger.info("Student {} re-enrolled in section {} (reactivated enrollment {})",
                        student.getRollNo(), sectionId, enrollmentId);
            } else {
                return new RegistrationResult(false, "Failed to re-enroll. Please try again.");
            }
        } else {
            // Create new enrollment
            enrollmentId = enrollmentRepository.enrollStudent(student.getStudentId(), sectionId, dropDeadline);
            if (enrollmentId > 0) {
                logger.info("Student {} enrolled in section {}", student.getRollNo(), sectionId);
            } else {
                return new RegistrationResult(false, "Failed to register. Please try again.");
            }
        }

        if (enrollmentId > 0) {
            return new RegistrationResult(true,
                    "Successfully registered for " + section.getCourseCode() + "-" + section.getSectionNumber());
        } else {
            return new RegistrationResult(false, "Failed to register. Please try again.");
        }
    }

    /**
     * Drop a section
     */
    public DropResult dropSection(int enrollmentId) {
        // Check if user can modify (not in maintenance mode)
        if (!AccessControl.canModify()) {
            return new DropResult(false, AccessControl.getAccessDeniedMessage());
        }

        // Get enrollment details
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId);
        if (enrollment == null) {
            return new DropResult(false, "Enrollment not found");
        }

        // Verify this enrollment belongs to current student
        Student student = getMyProfile();
        if (student == null || enrollment.getStudentId() != student.getStudentId()) {
            return new DropResult(false, "You can only drop your own enrollments");
        }

        // Check if already dropped
        if ("DROPPED".equals(enrollment.getStatus())) {
            return new DropResult(false, "This section has already been dropped");
        }

        // Check drop deadline
        if (!enrollment.canDrop()) {
            return new DropResult(false, "Drop deadline has passed for this section");
        }

        // Drop the enrollment
        boolean dropped = enrollmentRepository.dropEnrollment(enrollmentId);

        if (dropped) {
            logger.info("Student {} dropped enrollment {}", student.getRollNo(), enrollmentId);
            return new DropResult(true,
                    "Successfully dropped " + enrollment.getCourseCode() + "-" + enrollment.getSectionNumber());
        } else {
            return new DropResult(false, "Failed to drop section. Please try again.");
        }
    }

    /**
     * Get my registrations (enrolled sections)
     */
    public List<Enrollment> getMyRegistrations() {
        Student student = getMyProfile();
        if (student == null) {
            return List.of();
        }

        return enrollmentRepository.findByStudent(student.getStudentId());
    }

    /**
     * Get my timetable (enrolled sections with schedule)
     */
    public List<Enrollment> getMyTimetable() {
        return getMyRegistrations().stream()
                .filter(e -> "ENROLLED".equals(e.getStatus()))
                .toList();
    }

    /**
     * Get my grades (all courses)
     */
    public List<Grade> getMyGrades() {
        Student student = getMyProfile();
        if (student == null) {
            return List.of();
        }

        return gradeRepository.findByStudent(student.getStudentId());
    }

    /**
     * Get grades for a specific enrollment
     */
    public List<Grade> getGradesForEnrollment(int enrollmentId) {
        // Verify enrollment belongs to current student
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId);
        Student student = getMyProfile();

        if (student == null || enrollment == null || enrollment.getStudentId() != student.getStudentId()) {
            logger.warn("Access denied: Student trying to view grades for enrollment {}", enrollmentId);
            return List.of();
        }

        return gradeRepository.findByEnrollment(enrollmentId);
    }

    /**
     * Check if student meets prerequisites
     * Returns null if met, otherwise returns the missing prerequisite message
     */
    private String checkPrerequisites(Student student, String prerequisites) {
        List<Grade> studentGrades = gradeRepository.findByStudent(student.getStudentId());
        String[] requiredCourses = prerequisites.split(",");

        for (String reqCode : requiredCourses) {
            reqCode = reqCode.trim();
            boolean met = false;

            for (Grade grade : studentGrades) {
                if (reqCode.equalsIgnoreCase(grade.getCourseCode())) {
                    // Check if passed (Final grade exists and is not F)
                    String finalGrade = grade.getFinalGrade();
                    if (finalGrade != null && !finalGrade.equals("F")) {
                        met = true;
                        break;
                    }
                }
            }

            if (!met) {
                return "You must complete " + reqCode + " before registering for this course.";
            }
        }

        return null;
    }

    // Result classes
    public static class RegistrationResult {
        private final boolean success;
        private final String message;

        public RegistrationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class DropResult {
        private final boolean success;
        private final String message;

        public DropResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}
