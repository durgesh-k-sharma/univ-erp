package edu.univ.erp.api.student;

import edu.univ.erp.domain.Enrollment;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.Student;
import edu.univ.erp.service.StudentService;

import java.util.List;

/**
 * API for student operations
 */
public class StudentApi {
    private final StudentService studentService;

    public StudentApi() {
        this.studentService = new StudentService();
    }

    /**
     * Register for a section
     */
    public StudentService.RegistrationResult register(int sectionId) {
        return studentService.registerForSection(sectionId);
    }

    /**
     * Drop a section
     */
    public StudentService.DropResult drop(int enrollmentId) {
        return studentService.dropSection(enrollmentId);
    }

    /**
     * Get my registrations (timetable)
     */
    public List<Enrollment> getMyRegistrations() {
        return studentService.getMyRegistrations();
    }

    /**
     * Get my grades
     */
    public List<Grade> getMyGrades() {
        return studentService.getMyGrades();
    }

    /**
     * Get current student profile
     */
    public Student getMyProfile() {
        return studentService.getMyProfile();
    }

    /**
     * Get current student profile (alias for getMyProfile)
     */
    public Student getProfile() {
        return getMyProfile();
    }

    /**
     * Browse course catalog
     */
    public List<Section> browseCatalog(String semester, int year) {
        return studentService.browseCatalog(semester, year);
    }

    /**
     * Register for a section (alias for register)
     */
    public StudentService.RegistrationResult registerForSection(int sectionId) {
        return register(sectionId);
    }

    /**
     * Drop a section (alias for drop)
     */
    public StudentService.DropResult dropSection(int enrollmentId) {
        return drop(enrollmentId);
    }

    /**
     * Get my timetable (alias for getMyRegistrations)
     */
    public List<Enrollment> getMyTimetable() {
        return getMyRegistrations();
    }
}
