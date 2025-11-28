package edu.univ.erp.test;

import edu.univ.erp.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Test utility class for creating test objects with correct constructors
 */
public class TestDataFactory {

    public static User createUser(int id, String username, Role role, String status) {
        User user = new User(id, username, role);
        user.setStatus(status);
        return user;
    }

    public static Student createStudent(int studentId, int userId, String rollNo, String program, int year) {
        Student student = new Student(studentId, userId, rollNo, program, year);
        return student;
    }

    public static Section createSection(int sectionId, int courseId, String courseCode, int sectionNumber,
            Integer instructorId, String semester, int year, String dayTime,
            String room, int capacity, int enrolled) {
        Section section = new Section(sectionId, courseId, String.valueOf(sectionNumber), semester, year, capacity);
        section.setCourseCode(courseCode);
        section.setInstructorId(instructorId);
        section.setDayTime(dayTime);
        section.setRoom(room);
        section.setEnrolledCount(enrolled);
        return section;
    }

    public static Instructor createInstructor(int instructorId, int userId, String employeeId, String department) {
        Instructor instructor = new Instructor(instructorId, userId, employeeId, department);
        return instructor;
    }

    public static Enrollment createEnrollment(int enrollmentId, int studentId, int sectionId, String status) {
        Enrollment enrollment = new Enrollment(enrollmentId, studentId, sectionId, status);
        return enrollment;
    }

    public static Course createCourse(int courseId, String courseCode, String title, int credits) {
        Course course = new Course(courseId, courseCode, title, credits);
        return course;
    }

    public static Grade createGrade(int enrollmentId, String component, double score, double maxScore, double weight) {
        Grade grade = new Grade(enrollmentId, component,
                BigDecimal.valueOf(score),
                BigDecimal.valueOf(maxScore),
                BigDecimal.valueOf(weight));
        return grade;
    }
}
