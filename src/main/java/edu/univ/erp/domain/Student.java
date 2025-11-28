package edu.univ.erp.domain;

import java.time.LocalDateTime;

/**
 * Student entity representing student profile data
 * Linked to ERP DB
 */
public class Student {
    private int studentId;
    private int userId;
    private String rollNo;
    private String program;
    private int year;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // For display purposes (joined with User)
    private String username;

    public Student() {
    }

    public Student(int studentId, int userId, String rollNo, String program, int year) {
        this.studentId = studentId;
        this.userId = userId;
        this.rollNo = rollNo;
        this.program = program;
        this.year = year;
    }

    // Getters and Setters
    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        // Since we don't have separate first/last name fields in this schema,
        // we'll return the username or a placeholder.
        // In a real app, we'd query the users table for the name.
        return username != null ? username : "Student " + rollNo;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", userId=" + userId +
                ", rollNo='" + rollNo + '\'' +
                ", program='" + program + '\'' +
                ", year=" + year +
                '}';
    }
}
