package edu.univ.erp.data;

import edu.univ.erp.domain.Grade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for Grade table operations
 */
public class GradeRepository {
    private static final Logger logger = LoggerFactory.getLogger(GradeRepository.class);

    /**
     * Find grades by enrollment ID
     */
    public List<Grade> findByEnrollment(int enrollmentId) {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT g.*, c.code as course_code, c.title as course_title, sec.section_number " +
                "FROM grades g " +
                "JOIN enrollments e ON g.enrollment_id = e.enrollment_id " +
                "JOIN sections sec ON e.section_id = sec.section_id " +
                "JOIN courses c ON sec.course_id = c.course_id " +
                "WHERE g.enrollment_id = ? " +
                "ORDER BY g.component";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, enrollmentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(mapResultSetToGrade(rs));
                }
            }

            logger.debug("Found {} grades for enrollment ID: {}", grades.size(), enrollmentId);
        } catch (SQLException e) {
            logger.error("Error finding grades by enrollment: {}", enrollmentId, e);
        }

        return grades;
    }

    /**
     * Find grades by student ID (all enrollments)
     */
    public List<Grade> findByStudent(int studentId) {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT g.*, c.code as course_code, c.title as course_title, sec.section_number " +
                "FROM grades g " +
                "JOIN enrollments e ON g.enrollment_id = e.enrollment_id " +
                "JOIN sections sec ON e.section_id = sec.section_id " +
                "JOIN courses c ON sec.course_id = c.course_id " +
                "WHERE e.student_id = ? " +
                "ORDER BY sec.year DESC, sec.semester, c.code, g.component";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    grades.add(mapResultSetToGrade(rs));
                }
            }

            logger.debug("Found {} grades for student ID: {}", grades.size(), studentId);
        } catch (SQLException e) {
            logger.error("Error finding grades by student: {}", studentId, e);
        }

        return grades;
    }

    /**
     * Get all grades
     */
    public List<Grade> findAll() {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT g.*, c.code as course_code, c.title as course_title, sec.section_number " +
                "FROM grades g " +
                "JOIN enrollments e ON g.enrollment_id = e.enrollment_id " +
                "JOIN sections sec ON e.section_id = sec.section_id " +
                "JOIN courses c ON sec.course_id = c.course_id " +
                "ORDER BY g.grade_id";

        try (Connection conn = DatabaseManager.getErpConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                grades.add(mapResultSetToGrade(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all grades", e);
        }

        return grades;
    }

    /**
     * Save or update a grade component
     */
    public boolean saveGrade(Grade grade) {
        // Check if grade already exists for this enrollment and component
        String checkSql = "SELECT grade_id FROM grades WHERE enrollment_id = ? AND component = ?";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, grade.getEnrollmentId());
            checkStmt.setString(2, grade.getComponent());

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    // Update existing grade
                    int gradeId = rs.getInt("grade_id");
                    return updateGrade(gradeId, grade);
                } else {
                    // Insert new grade
                    return insertGrade(grade);
                }
            }
        } catch (SQLException e) {
            logger.error("Error saving grade", e);
        }

        return false;
    }

    /**
     * Insert a new grade
     */
    private boolean insertGrade(Grade grade) {
        String sql = "INSERT INTO grades (enrollment_id, component, score, max_score, weight) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, grade.getEnrollmentId());
            stmt.setString(2, grade.getComponent());
            stmt.setBigDecimal(3, grade.getScore());
            stmt.setBigDecimal(4, grade.getMaxScore());
            stmt.setBigDecimal(5, grade.getWeight());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int gradeId = generatedKeys.getInt(1);
                        logger.info("Inserted grade ID: {} for enrollment {}", gradeId, grade.getEnrollmentId());
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error inserting grade", e);
        }

        return false;
    }

    /**
     * Update an existing grade
     */
    private boolean updateGrade(int gradeId, Grade grade) {
        String sql = "UPDATE grades SET score = ?, max_score = ?, weight = ?, " +
                "updated_at = CURRENT_TIMESTAMP WHERE grade_id = ?";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBigDecimal(1, grade.getScore());
            stmt.setBigDecimal(2, grade.getMaxScore());
            stmt.setBigDecimal(3, grade.getWeight());
            stmt.setInt(4, gradeId);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Updated grade ID: {}", gradeId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating grade: {}", gradeId, e);
        }

        return false;
    }

    /**
     * Update final grade for an enrollment
     */
    public boolean updateFinalGrade(int enrollmentId, String finalGrade) {
        String sql = "UPDATE grades SET final_grade = ? WHERE enrollment_id = ? LIMIT 1";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, finalGrade);
            stmt.setInt(2, enrollmentId);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Updated final grade for enrollment {}: {}", enrollmentId, finalGrade);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating final grade for enrollment: {}", enrollmentId, e);
        }

        return false;
    }

    /**
     * Delete a grade
     */
    public boolean deleteGrade(int gradeId) {
        String sql = "DELETE FROM grades WHERE grade_id = ?";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gradeId);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Deleted grade ID: {}", gradeId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error deleting grade: {}", gradeId, e);
        }

        return false;
    }

    /**
     * Map ResultSet to Grade object
     */
    private Grade mapResultSetToGrade(ResultSet rs) throws SQLException {
        Grade grade = new Grade();
        grade.setGradeId(rs.getInt("grade_id"));
        grade.setEnrollmentId(rs.getInt("enrollment_id"));
        grade.setComponent(rs.getString("component"));

        BigDecimal score = rs.getBigDecimal("score");
        if (score != null) {
            grade.setScore(score);
        }

        BigDecimal maxScore = rs.getBigDecimal("max_score");
        if (maxScore != null) {
            grade.setMaxScore(maxScore);
        }

        BigDecimal weight = rs.getBigDecimal("weight");
        if (weight != null) {
            grade.setWeight(weight);
        }

        grade.setFinalGrade(rs.getString("final_grade"));

        // Joined data
        grade.setCourseCode(rs.getString("course_code"));
        grade.setCourseTitle(rs.getString("course_title"));
        grade.setSectionNumber(rs.getString("section_number"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            grade.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            grade.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return grade;
    }
}
