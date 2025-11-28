package edu.univ.erp.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

/**
 * Grade entity representing assessment scores and final grades
 */
public class Grade {
    private int gradeId;
    private int enrollmentId;
    private String component;
    private BigDecimal score;
    private BigDecimal maxScore;
    private BigDecimal weight;
    private String finalGrade;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // For display purposes
    private String courseCode;
    private String courseTitle;
    private String sectionNumber;

    public Grade() {
    }

    public Grade(int enrollmentId, String component, BigDecimal score, BigDecimal maxScore, BigDecimal weight) {
        this.enrollmentId = enrollmentId;
        this.component = component;
        this.score = score;
        this.maxScore = maxScore;
        this.weight = weight;
    }

    // Getters and Setters
    public int getGradeId() {
        return gradeId;
    }

    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
    }

    public int getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(int enrollmentId) {
        this.enrollmentId = enrollmentId;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public BigDecimal getMaxScore() {
        return maxScore;
    }

    public void setMaxScore(BigDecimal maxScore) {
        this.maxScore = maxScore;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    public String getFinalGrade() {
        return finalGrade;
    }

    public void setFinalGrade(String finalGrade) {
        this.finalGrade = finalGrade;
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

    public String getSectionNumber() {
        return sectionNumber;
    }

    public void setSectionNumber(String sectionNumber) {
        this.sectionNumber = sectionNumber;
    }

    // Placeholder methods for TranscriptGenerator
    // In a full implementation, these would be populated from the Course/Section
    // tables
    public int getCredits() {
        return 3; // Default to 3 credits for now
    }

    public String getSemester() {
        return "Fall 2023"; // Default placeholder
    }

    /**
     * Calculate percentage score
     */
    public BigDecimal getPercentage() {
        if (score == null || maxScore == null || maxScore.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return score.divide(maxScore, 4, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    /**
     * Calculate weighted contribution to final grade
     */
    public BigDecimal getWeightedScore() {
        if (score == null || maxScore == null || weight == null) {
            return BigDecimal.ZERO;
        }
        return getPercentage().multiply(weight).divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        return "Grade{" +
                "component='" + component + '\'' +
                ", score=" + score +
                ", maxScore=" + maxScore +
                ", weight=" + weight +
                '}';
    }
}
