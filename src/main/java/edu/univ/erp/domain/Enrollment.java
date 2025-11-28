package edu.univ.erp.domain;

import java.time.LocalDateTime;

/**
 * Enrollment entity linking a student to a section
 */
public class Enrollment {
    private int enrollmentId;
    private int studentId;
    private int sectionId;
    private String status;
    private LocalDateTime enrollmentDate;
    private LocalDateTime dropDate;
    private LocalDateTime dropDeadline;

    // For display purposes (joined data)
    private String studentRollNo;
    private String studentName;
    private String courseCode;
    private String courseTitle;
    private String sectionNumber;
    private String semester;
    private int year;
    private String instructorName;
    private String dayTime;
    private String room;
    private Integer courseCredits;

    public Enrollment() {
    }

    public Enrollment(int enrollmentId, int studentId, int sectionId, String status) {
        this.enrollmentId = enrollmentId;
        this.studentId = studentId;
        this.sectionId = sectionId;
        this.status = status;
    }

    // Getters and Setters
    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDateTime enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public LocalDateTime getDropDate() {
        return dropDate;
    }

    public void setDropDate(LocalDateTime dropDate) {
        this.dropDate = dropDate;
    }

    public LocalDateTime getDropDeadline() {
        return dropDeadline;
    }

    public void setDropDeadline(LocalDateTime dropDeadline) {
        this.dropDeadline = dropDeadline;
    }

    public String getStudentRollNo() {
        return studentRollNo;
    }

    public void setStudentRollNo(String studentRollNo) {
        this.studentRollNo = studentRollNo;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getSectionNumber() {
        return sectionNumber;
    }

    public void setSectionNumber(String sectionNumber) {
        this.sectionNumber = sectionNumber;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public String getDayTime() {
        return dayTime;
    }

    public void setDayTime(String dayTime) {
        this.dayTime = dayTime;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public Integer getCourseCredits() {
        return courseCredits;
    }

    public void setCourseCredits(Integer courseCredits) {
        this.courseCredits = courseCredits;
    }

    public boolean canDrop() {
        return dropDeadline != null && LocalDateTime.now().isBefore(dropDeadline)
                && "ENROLLED".equals(status);
    }

    @Override
    public String toString() {
        return "Enrollment{" +
                "enrollmentId=" + enrollmentId +
                ", studentId=" + studentId +
                ", sectionId=" + sectionId +
                ", status='" + status + '\'' +
                '}';
    }
}
