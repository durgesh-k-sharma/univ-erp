package edu.univ.erp.data;

import edu.univ.erp.domain.Enrollment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for Enrollment table operations
 */
public class EnrollmentRepository {
    private static final Logger logger = LoggerFactory.getLogger(EnrollmentRepository.class);

    /**
     * Find enrollment by ID with full details
     */
    public Enrollment findById(int enrollmentId) {
        String sql = "SELECT e.*, s.roll_no as student_roll_no, u.username as student_name, " +
                "c.code as course_code, c.title as course_title, c.credits as course_credits, sec.section_number, " +
                "sec.semester, sec.year, sec.day_time, sec.room, " +
                "inst_user.username as instructor_name " +
                "FROM enrollments e " +
                "JOIN students s ON e.student_id = s.student_id " +
                "JOIN univ_erp_auth.users_auth u ON s.user_id = u.user_id " +
                "JOIN sections sec ON e.section_id = sec.section_id " +
                "JOIN courses c ON sec.course_id = c.course_id " +
                "LEFT JOIN instructors inst ON sec.instructor_id = inst.instructor_id " +
                "LEFT JOIN univ_erp_auth.users_auth inst_user ON inst.user_id = inst_user.user_id " +
                "WHERE e.enrollment_id = ?";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, enrollmentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEnrollment(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding enrollment by ID: {}", enrollmentId, e);
        }

        return null;
    }

    /**
     * Find enrollments by student ID
     */
    public List<Enrollment> findByStudent(int studentId) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT e.*, s.roll_no as student_roll_no, u.username as student_name, " +
                "c.code as course_code, c.title as course_title, c.credits as course_credits, sec.section_number, " +
                "sec.semester, sec.year, sec.day_time, sec.room, " +
                "inst_user.username as instructor_name " +
                "FROM enrollments e " +
                "JOIN students s ON e.student_id = s.student_id " +
                "JOIN univ_erp_auth.users_auth u ON s.user_id = u.user_id " +
                "JOIN sections sec ON e.section_id = sec.section_id " +
                "JOIN courses c ON sec.course_id = c.course_id " +
                "LEFT JOIN instructors inst ON sec.instructor_id = inst.instructor_id " +
                "LEFT JOIN univ_erp_auth.users_auth inst_user ON inst.user_id = inst_user.user_id " +
                "WHERE e.student_id = ? " +
                "ORDER BY sec.year DESC, sec.semester, c.code";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(mapResultSetToEnrollment(rs));
                }
            }

            logger.debug("Found {} enrollments for student ID: {}", enrollments.size(), studentId);
        } catch (SQLException e) {
            logger.error("Error finding enrollments by student: {}", studentId, e);
        }

        return enrollments;
    }

    /**
     * Find enrollments by section ID
     */
    public List<Enrollment> findBySection(int sectionId) {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT e.*, s.roll_no as student_roll_no, u.username as student_name, " +
                "c.code as course_code, c.title as course_title, c.credits as course_credits, sec.section_number, " +
                "sec.semester, sec.year, sec.day_time, sec.room, " +
                "inst_user.username as instructor_name " +
                "FROM enrollments e " +
                "JOIN students s ON e.student_id = s.student_id " +
                "JOIN univ_erp_auth.users_auth u ON s.user_id = u.user_id " +
                "JOIN sections sec ON e.section_id = sec.section_id " +
                "JOIN courses c ON sec.course_id = c.course_id " +
                "LEFT JOIN instructors inst ON sec.instructor_id = inst.instructor_id " +
                "LEFT JOIN univ_erp_auth.users_auth inst_user ON inst.user_id = inst_user.user_id " +
                "WHERE e.section_id = ? " +
                "ORDER BY s.roll_no";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sectionId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(mapResultSetToEnrollment(rs));
                }
            }

            logger.debug("Found {} enrollments for section ID: {}", enrollments.size(), sectionId);
        } catch (SQLException e) {
            logger.error("Error finding enrollments by section: {}", sectionId, e);
        }

        return enrollments;
    }

    /**
     * Get all enrollments
     */
    public List<Enrollment> findAll() {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT e.*, s.roll_no as student_roll_no, u.username as student_name, " +
                "c.code as course_code, c.title as course_title, c.credits as course_credits, sec.section_number, " +
                "sec.semester, sec.year, sec.day_time, sec.room, " +
                "inst_user.username as instructor_name " +
                "FROM enrollments e " +
                "JOIN students s ON e.student_id = s.student_id " +
                "JOIN univ_erp_auth.users_auth u ON s.user_id = u.user_id " +
                "JOIN sections sec ON e.section_id = sec.section_id " +
                "JOIN courses c ON sec.course_id = c.course_id " +
                "LEFT JOIN instructors inst ON sec.instructor_id = inst.instructor_id " +
                "LEFT JOIN univ_erp_auth.users_auth inst_user ON inst.user_id = inst_user.user_id " +
                "ORDER BY e.enrollment_id";

        try (Connection conn = DatabaseManager.getErpConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                enrollments.add(mapResultSetToEnrollment(rs));
            }
        } catch (SQLException e) {
            logger.error("Error finding all enrollments", e);
        }

        return enrollments;
    }

    /**
     * Check if active enrollment already exists (prevent duplicates)
     * Only checks for ENROLLED status, not DROPPED enrollments
     */
    public boolean existsByStudentAndSection(int studentId, int sectionId) {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND section_id = ? AND status = 'ENROLLED'";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking duplicate enrollment", e);
        }

        return false;
    }

    /**
     * Find enrollment by student and section (any status)
     */
    public Enrollment findByStudentAndSection(int studentId, int sectionId) {
        String sql = "SELECT e.*, s.roll_no as student_roll_no, u.username as student_name, " +
                "c.code as course_code, c.title as course_title, c.credits as course_credits, sec.section_number, " +
                "sec.semester, sec.year, sec.day_time, sec.room, " +
                "inst_user.username as instructor_name " +
                "FROM enrollments e " +
                "JOIN students s ON e.student_id = s.student_id " +
                "JOIN univ_erp_auth.users_auth u ON s.user_id = u.user_id " +
                "JOIN sections sec ON e.section_id = sec.section_id " +
                "JOIN courses c ON sec.course_id = c.course_id " +
                "LEFT JOIN instructors inst ON sec.instructor_id = inst.instructor_id " +
                "LEFT JOIN univ_erp_auth.users_auth inst_user ON inst.user_id = inst_user.user_id " +
                "WHERE e.student_id = ? AND e.section_id = ?";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEnrollment(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding enrollment for student {} and section {}", studentId, sectionId, e);
        }

        return null;
    }

    /**
     * Enroll a student in a section
     */
    public int enrollStudent(int studentId, int sectionId, LocalDateTime dropDeadline) {
        String sql = "INSERT INTO enrollments (student_id, section_id, status, drop_deadline) " +
                "VALUES (?, ?, 'ENROLLED', ?)";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, sectionId);
            stmt.setTimestamp(3, Timestamp.valueOf(dropDeadline));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int enrollmentId = generatedKeys.getInt(1);
                        logger.info("Enrolled student {} in section {}, enrollment ID: {}",
                                studentId, sectionId, enrollmentId);
                        return enrollmentId;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error enrolling student {} in section {}", studentId, sectionId, e);
        }

        return -1;
    }

    /**
     * Reactivate a dropped enrollment
     */
    public boolean reactivateEnrollment(int enrollmentId, LocalDateTime newDropDeadline) {
        String sql = "UPDATE enrollments SET status = 'ENROLLED', " +
                "enrollment_date = CURRENT_TIMESTAMP, drop_date = NULL, drop_deadline = ? " +
                "WHERE enrollment_id = ?";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(newDropDeadline));
            stmt.setInt(2, enrollmentId);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Reactivated enrollment ID: {}", enrollmentId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error reactivating enrollment: {}", enrollmentId, e);
        }

        return false;
    }

    /**
     * Drop an enrollment
     */
    public boolean dropEnrollment(int enrollmentId) {
        String sql = "UPDATE enrollments SET status = 'DROPPED', drop_date = CURRENT_TIMESTAMP " +
                "WHERE enrollment_id = ?";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, enrollmentId);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Dropped enrollment ID: {}", enrollmentId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error dropping enrollment: {}", enrollmentId, e);
        }

        return false;
    }

    /**
     * Update enrollment status
     */
    public boolean updateStatus(int enrollmentId, String status) {
        String sql = "UPDATE enrollments SET status = ? WHERE enrollment_id = ?";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, enrollmentId);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Updated enrollment {} status to {}", enrollmentId, status);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating enrollment status", e);
        }

        return false;
    }

    /**
     * Map ResultSet to Enrollment object
     */
    private Enrollment mapResultSetToEnrollment(ResultSet rs) throws SQLException {
        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(rs.getInt("enrollment_id"));
        enrollment.setStudentId(rs.getInt("student_id"));
        enrollment.setSectionId(rs.getInt("section_id"));
        enrollment.setStatus(rs.getString("status"));

        Timestamp enrollmentDate = rs.getTimestamp("enrollment_date");
        if (enrollmentDate != null) {
            enrollment.setEnrollmentDate(enrollmentDate.toLocalDateTime());
        }

        Timestamp dropDate = rs.getTimestamp("drop_date");
        if (dropDate != null) {
            enrollment.setDropDate(dropDate.toLocalDateTime());
        }

        Timestamp dropDeadline = rs.getTimestamp("drop_deadline");
        if (dropDeadline != null) {
            enrollment.setDropDeadline(dropDeadline.toLocalDateTime());
        }

        // Joined data
        enrollment.setStudentRollNo(rs.getString("student_roll_no"));
        enrollment.setStudentName(rs.getString("student_name"));
        enrollment.setCourseCode(rs.getString("course_code"));
        enrollment.setCourseTitle(rs.getString("course_title"));
        enrollment.setCourseCredits(rs.getInt("course_credits"));
        enrollment.setSectionNumber(rs.getString("section_number"));
        enrollment.setSemester(rs.getString("semester"));
        enrollment.setYear(rs.getInt("year"));
        enrollment.setDayTime(rs.getString("day_time"));
        enrollment.setRoom(rs.getString("room"));
        enrollment.setInstructorName(rs.getString("instructor_name"));

        return enrollment;
    }
}
