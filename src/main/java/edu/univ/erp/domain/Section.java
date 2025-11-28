package edu.univ.erp.domain;

import java.time.LocalDateTime;

/**
 * Section entity representing a specific offering of a course
 */
public class Section {
    private int sectionId;
    private int courseId;
    private Integer instructorId;
    private String sectionNumber;
    private String dayTime;
    private String room;
    private int capacity;
    private String semester;
    private int year;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // For display purposes (joined data)
    private String courseCode;
    private String courseTitle;
    private int courseCredits;
    private String instructorName;
    private int enrolledCount;

    public Section() {
    }

    public Section(int sectionId, int courseId, String sectionNumber, String semester, int year, int capacity) {
        this.sectionId = sectionId;
        this.courseId = courseId;
        this.sectionNumber = sectionNumber;
        this.semester = semester;
        this.year = year;
        this.capacity = capacity;
    }

    // Getters and Setters
    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    public int getCourseId() {
        return courseId;
    }

    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    public Integer getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(Integer instructorId) {
        this.instructorId = instructorId;
    }

    public String getSectionNumber() {
        return sectionNumber;
    }

    public void setSectionNumber(String sectionNumber) {
        this.sectionNumber = sectionNumber;
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

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
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

    public int getCourseCredits() {
        return courseCredits;
    }

    public void setCourseCredits(int courseCredits) {
        this.courseCredits = courseCredits;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public int getEnrolledCount() {
        return enrolledCount;
    }

    public void setEnrolledCount(int enrolledCount) {
        this.enrolledCount = enrolledCount;
    }

    public int getAvailableSeats() {
        return capacity - enrolledCount;
    }

    public boolean isFull() {
        return enrolledCount >= capacity;
    }

    @Override
    public String toString() {
        return courseCode + "-" + sectionNumber + " (" + semester + " " + year + ")";
    }
}
