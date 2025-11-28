package edu.univ.erp.api.instructor;

import edu.univ.erp.domain.Instructor;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.InstructorService;

import java.util.List;

/**
 * API for instructor operations
 */
public class InstructorApi {
    private final InstructorService instructorService;

    public InstructorApi() {
        this.instructorService = new InstructorService();
    }

    /**
     * Get my sections
     */
    public List<Section> getMySections() {
        return instructorService.getMySections();
    }

    /**
     * Get enrolled students for a section
     */
    public InstructorService.EnrollmentListResult getEnrolledStudents(int sectionId) {
        return instructorService.getEnrolledStudents(sectionId);
    }

    /**
     * Enter grade
     */
    public InstructorService.GradeEntryResult enterGrade(int enrollmentId, String component, double score,
            double maxScore, double weight) {
        return instructorService.enterGrade(enrollmentId, component, score, maxScore, weight);
    }

    /**
     * Compute final grade
     */
    public InstructorService.ComputeGradeResult computeFinalGrade(int enrollmentId) {
        return instructorService.computeFinalGrade(enrollmentId);
    }

    /**
     * Get class statistics
     */
    public InstructorService.ClassStatsResult getClassStatistics(int sectionId) {
        return instructorService.getClassStatistics(sectionId);
    }

    /**
     * Get my profile
     */
    public Instructor getMyProfile() {
        return instructorService.getMyProfile();
    }

    /**
     * Get my profile (alias for getMyProfile)
     */
    public Instructor getProfile() {
        return getMyProfile();
    }
}
