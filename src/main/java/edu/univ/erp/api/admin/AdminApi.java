package edu.univ.erp.api.admin;

import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Section;
import edu.univ.erp.domain.Student;
import edu.univ.erp.service.AdminService;

import java.util.List;

/**
 * API for admin operations
 */
public class AdminApi {
    private final AdminService adminService;

    public AdminApi() {
        this.adminService = new AdminService();
    }

    /**
     * Create student
     */
    public AdminService.CreateUserResult createStudent(String username, String password, String rollNo, String program,
            int year, String email, String phone) {
        return adminService.createStudent(username, password, rollNo, program, year, email, phone);
    }

    /**
     * Create instructor
     */
    public AdminService.CreateUserResult createInstructor(String username, String password, String employeeId,
            String department, String email, String phone) {
        return adminService.createInstructor(username, password, employeeId, department, email, phone);
    }

    /**
     * Create admin
     */
    public AdminService.CreateUserResult createAdmin(String username, String password) {
        return adminService.createAdmin(username, password);
    }

    /**
     * Create course
     */
    public AdminService.CreateCourseResult createCourse(String code, String title, int credits, String description,
            String prerequisites) {
        return adminService.createCourse(code, title, credits, description, prerequisites);
    }

    /**
     * Create section
     */
    public AdminService.CreateSectionResult createSection(int courseId, String sectionNumber, String dayTime,
            String room, int capacity, String semester, int year) {
        return adminService.createSection(courseId, sectionNumber, dayTime, room, capacity, semester, year);
    }

    /**
     * Assign instructor
     */
    public AdminService.AssignInstructorResult assignInstructor(int sectionId, int instructorId) {
        return adminService.assignInstructor(sectionId, instructorId);
    }

    /**
     * Backup data
     */
    public boolean backupData(String directoryPath) {
        return adminService.backupData(directoryPath);
    }

    /**
     * Restore data
     */
    public boolean restoreData(String directoryPath) {
        return adminService.restoreData(directoryPath);
    }

    /**
     * Get all students
     */
    public List<Student> getAllStudents() {
        return adminService.getAllStudents();
    }

    /**
     * Get all instructors
     */
    public List<Instructor> getAllInstructors() {
        return adminService.getAllInstructors();
    }

    /**
     * Get all courses
     */
    public List<Course> getAllCourses() {
        return adminService.getAllCourses();
    }

    /**
     * Get all sections
     */
    public List<Section> getAllSections() {
        return adminService.getAllSections();
    }
}
