package edu.univ.erp.domain;

import java.time.LocalDateTime;

/**
 * Instructor entity representing instructor profile data
 * Linked to ERP DB
 */
public class Instructor {
    private int instructorId;
    private int userId;
    private String employeeId;
    private String department;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // For display purposes (joined with User)
    private String username;
    private String fullName;

    public Instructor() {
    }

    public Instructor(int instructorId, int userId, String employeeId, String department) {
        this.instructorId = instructorId;
        this.userId = userId;
        this.employeeId = employeeId;
        this.department = department;
    }

    // Getters and Setters
    public int getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
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
        return fullName != null ? fullName : username;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public String toString() {
        return "Instructor{" +
                "instructorId=" + instructorId +
                ", userId=" + userId +
                ", employeeId='" + employeeId + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
