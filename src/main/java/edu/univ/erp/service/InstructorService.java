package edu.univ.erp.service;

import edu.univ.erp.access.AccessControl;
import edu.univ.erp.auth.SessionManager;
import edu.univ.erp.data.*;
import edu.univ.erp.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for instructor operations
 */
public class InstructorService {
    private static final Logger logger = LoggerFactory.getLogger(InstructorService.class);

    private final InstructorRepository instructorRepository;
    private final SectionRepository sectionRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final GradeRepository gradeRepository;

    public InstructorService() {
        this.instructorRepository = new InstructorRepository();
        this.sectionRepository = new SectionRepository();
        this.enrollmentRepository = new EnrollmentRepository();
        this.gradeRepository = new GradeRepository();
    }

    /**
     * Get instructor profile for current user
     */
    public Instructor getMyProfile() {
        Integer userId = SessionManager.getCurrentUserId();
        if (userId == null) {
            logger.warn("Cannot get profile: No user logged in");
            return null;
        }

        return instructorRepository.findByUserId(userId);
    }

    /**
     * Get my sections (sections assigned to current instructor)
     */
    public List<Section> getMySections() {
        Instructor instructor = getMyProfile();
        if (instructor == null) {
            return List.of();
        }

        return sectionRepository.findByInstructor(instructor.getInstructorId());
    }

    /**
     * Get students enrolled in a section
     */
    public EnrollmentListResult getEnrolledStudents(int sectionId) {
        // Verify this section belongs to current instructor
        if (!canAccessSection(sectionId)) {
            return new EnrollmentListResult(false, "You can only view students in your own sections", List.of());
        }

        List<Enrollment> enrollments = enrollmentRepository.findBySection(sectionId);
        return new EnrollmentListResult(true, "Success", enrollments);
    }

    /**
     * Enter or update a grade component for a student
     */
    public GradeEntryResult enterGrade(int enrollmentId, String component, double score, double maxScore,
            double weight) {
        // Check if user can modify (not in maintenance mode)
        if (!AccessControl.canModify()) {
            return new GradeEntryResult(false, AccessControl.getAccessDeniedMessage());
        }

        // Validate inputs
        if (component == null || component.trim().isEmpty()) {
            return new GradeEntryResult(false, "Component name cannot be empty");
        }

        if (score < 0 || maxScore <= 0) {
            return new GradeEntryResult(false, "Invalid score values");
        }

        if (score > maxScore) {
            return new GradeEntryResult(false, "Score cannot exceed maximum score");
        }

        if (weight < 0 || weight > 100) {
            return new GradeEntryResult(false, "Weight must be between 0 and 100");
        }

        // Verify enrollment belongs to instructor's section
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId);
        if (enrollment == null) {
            return new GradeEntryResult(false, "Enrollment not found");
        }

        if (!canAccessSection(enrollment.getSectionId())) {
            return new GradeEntryResult(false, "You can only enter grades for your own sections");
        }

        // Create grade object
        Grade grade = new Grade();
        grade.setEnrollmentId(enrollmentId);
        grade.setComponent(component.toUpperCase());
        grade.setScore(BigDecimal.valueOf(score));
        grade.setMaxScore(BigDecimal.valueOf(maxScore));
        grade.setWeight(BigDecimal.valueOf(weight));

        // Save grade
        boolean saved = gradeRepository.saveGrade(grade);

        if (saved) {
            logger.info("Grade entered for enrollment {}: {} = {}/{}", enrollmentId, component, score, maxScore);
            return new GradeEntryResult(true, "Grade saved successfully");
        } else {
            return new GradeEntryResult(false, "Failed to save grade. Please try again.");
        }
    }

    /**
     * Compute final grade for an enrollment based on weighted components
     */
    public ComputeGradeResult computeFinalGrade(int enrollmentId) {
        // Check if user can modify (not in maintenance mode)
        if (!AccessControl.canModify()) {
            return new ComputeGradeResult(false, AccessControl.getAccessDeniedMessage(), null);
        }

        // Verify enrollment belongs to instructor's section
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId);
        if (enrollment == null) {
            return new ComputeGradeResult(false, "Enrollment not found", null);
        }

        if (!canAccessSection(enrollment.getSectionId())) {
            return new ComputeGradeResult(false, "You can only compute grades for your own sections", null);
        }

        // Get all grade components
        List<Grade> grades = gradeRepository.findByEnrollment(enrollmentId);

        if (grades.isEmpty()) {
            return new ComputeGradeResult(false, "No grades entered yet", null);
        }

        // Calculate weighted total
        BigDecimal totalWeightedScore = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;

        for (Grade grade : grades) {
            if (grade.getScore() != null && grade.getMaxScore() != null && grade.getWeight() != null) {
                totalWeightedScore = totalWeightedScore.add(grade.getWeightedScore());
                totalWeight = totalWeight.add(grade.getWeight());
            }
        }

        // Check if all components are graded
        if (totalWeight.compareTo(BigDecimal.valueOf(100)) < 0) {
            return new ComputeGradeResult(false,
                    String.format("Not all components graded. Total weight: %.0f%% (need 100%%)",
                            totalWeight.doubleValue()),
                    null);
        }

        // Calculate final percentage
        BigDecimal finalPercentage = totalWeightedScore.setScale(2, RoundingMode.HALF_UP);

        // Convert to letter grade
        String letterGrade = convertToLetterGrade(finalPercentage.doubleValue());

        // Update final grade in database
        boolean updated = gradeRepository.updateFinalGrade(enrollmentId, letterGrade);

        if (updated) {
            logger.info("Final grade computed for enrollment {}: {} ({}%)", enrollmentId, letterGrade, finalPercentage);
            return new ComputeGradeResult(true, "Final grade computed successfully", letterGrade);
        } else {
            return new ComputeGradeResult(false, "Failed to save final grade", null);
        }
    }

    /**
     * Get class statistics for a section
     */
    public ClassStatsResult getClassStatistics(int sectionId) {
        // Verify this section belongs to current instructor
        if (!canAccessSection(sectionId)) {
            return new ClassStatsResult(false, "You can only view statistics for your own sections", null);
        }

        List<Enrollment> enrollments = enrollmentRepository.findBySection(sectionId);

        if (enrollments.isEmpty()) {
            return new ClassStatsResult(false, "No students enrolled in this section", null);
        }

        // Calculate statistics
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", enrollments.size());
        stats.put("enrolledStudents", enrollments.stream().filter(e -> "ENROLLED".equals(e.getStatus())).count());
        stats.put("droppedStudents", enrollments.stream().filter(e -> "DROPPED".equals(e.getStatus())).count());

        // Grade statistics (for enrolled students with final grades)
        List<Grade> allGrades = enrollments.stream()
                .filter(e -> "ENROLLED".equals(e.getStatus()))
                .flatMap(e -> gradeRepository.findByEnrollment(e.getEnrollmentId()).stream())
                .filter(g -> g.getFinalGrade() != null)
                .toList();

        if (!allGrades.isEmpty()) {
            Map<String, Long> gradeDistribution = new HashMap<>();
            for (Grade grade : allGrades) {
                String letterGrade = grade.getFinalGrade();
                gradeDistribution.put(letterGrade, gradeDistribution.getOrDefault(letterGrade, 0L) + 1);
            }
            stats.put("gradeDistribution", gradeDistribution);
        }

        return new ClassStatsResult(true, "Statistics retrieved successfully", stats);
    }

    /**
     * Check if current instructor can access a section
     */
    private boolean canAccessSection(int sectionId) {
        Instructor instructor = getMyProfile();
        if (instructor == null) {
            return false;
        }

        Section section = sectionRepository.findById(sectionId);
        if (section == null) {
            return false;
        }

        // Admin can access any section
        if (SessionManager.isAdmin()) {
            return true;
        }

        // Instructor can only access their own sections
        return section.getInstructorId() != null && section.getInstructorId() == instructor.getInstructorId();
    }

    /**
     * Convert percentage to letter grade
     */
    private String convertToLetterGrade(double percentage) {
        if (percentage >= 90)
            return "A+";
        if (percentage >= 80)
            return "A";
        if (percentage >= 70)
            return "B+";
        if (percentage >= 60)
            return "B";
        if (percentage >= 50)
            return "C";
        return "F";
    }

    // Result classes
    public static class EnrollmentListResult {
        private final boolean success;
        private final String message;
        private final List<Enrollment> enrollments;

        public EnrollmentListResult(boolean success, String message, List<Enrollment> enrollments) {
            this.success = success;
            this.message = message;
            this.enrollments = enrollments;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public List<Enrollment> getEnrollments() {
            return enrollments;
        }
    }

    public static class GradeEntryResult {
        private final boolean success;
        private final String message;

        public GradeEntryResult(boolean success, String message) {
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

    public static class ComputeGradeResult {
        private final boolean success;
        private final String message;
        private final String finalGrade;

        public ComputeGradeResult(boolean success, String message, String finalGrade) {
            this.success = success;
            this.message = message;
            this.finalGrade = finalGrade;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public String getFinalGrade() {
            return finalGrade;
        }
    }

    public static class ClassStatsResult {
        private final boolean success;
        private final String message;
        private final Map<String, Object> statistics;

        public ClassStatsResult(boolean success, String message, Map<String, Object> statistics) {
            this.success = success;
            this.message = message;
            this.statistics = statistics;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public Map<String, Object> getStatistics() {
            return statistics;
        }
    }
}
