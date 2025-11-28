package edu.univ.erp.data;

import edu.univ.erp.domain.Instructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for Instructor table operations
 */
public class InstructorRepository {
    private static final Logger logger = LoggerFactory.getLogger(InstructorRepository.class);

    /**
     * Find instructor by user ID
     */
    public Instructor findByUserId(int userId) {
        String sql = "SELECT i.*, u.username FROM instructors i " +
                "LEFT JOIN univ_erp_auth.users_auth u ON i.user_id = u.user_id " +
                "WHERE i.user_id = ?";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInstructor(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding instructor by user ID: {}", userId, e);
        }

        return null;
    }

    /**
     * Find instructor by instructor ID
     */
    public Instructor findById(int instructorId) {
        String sql = "SELECT i.*, u.username FROM instructors i " +
                "LEFT JOIN univ_erp_auth.users_auth u ON i.user_id = u.user_id " +
                "WHERE i.instructor_id = ?";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, instructorId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInstructor(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding instructor by ID: {}", instructorId, e);
        }

        return null;
    }

    /**
     * Find instructor by employee ID
     */
    public Instructor findByEmployeeId(String employeeId) {
        String sql = "SELECT i.*, u.username FROM instructors i " +
                "LEFT JOIN univ_erp_auth.users_auth u ON i.user_id = u.user_id " +
                "WHERE i.employee_id = ?";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employeeId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToInstructor(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding instructor by employee ID: {}", employeeId, e);
        }

        return null;
    }

    /**
     * Create a new instructor
     */
    public int createInstructor(Instructor instructor) {
        String sql = "INSERT INTO instructors (user_id, employee_id, department, email, phone) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, instructor.getUserId());
            stmt.setString(2, instructor.getEmployeeId());
            stmt.setString(3, instructor.getDepartment());
            stmt.setString(4, instructor.getEmail());
            stmt.setString(5, instructor.getPhone());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int instructorId = generatedKeys.getInt(1);
                        logger.info("Created instructor: {} with ID: {}", instructor.getEmployeeId(), instructorId);
                        return instructorId;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating instructor: {}", instructor.getEmployeeId(), e);
        }

        return -1;
    }

    /**
     * Update instructor information
     */
    public boolean updateInstructor(Instructor instructor) {
        String sql = "UPDATE instructors SET department = ?, email = ?, phone = ?, " +
                "updated_at = CURRENT_TIMESTAMP WHERE instructor_id = ?";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, instructor.getDepartment());
            stmt.setString(2, instructor.getEmail());
            stmt.setString(3, instructor.getPhone());
            stmt.setInt(4, instructor.getInstructorId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Updated instructor ID: {}", instructor.getInstructorId());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating instructor: {}", instructor.getInstructorId(), e);
        }

        return false;
    }

    /**
     * Get all instructors
     */
    public List<Instructor> findAll() {
        List<Instructor> instructors = new ArrayList<>();
        String sql = "SELECT i.*, u.username FROM instructors i " +
                "LEFT JOIN univ_erp_auth.users_auth u ON i.user_id = u.user_id " +
                "ORDER BY i.employee_id";

        try (Connection conn = DatabaseManager.getErpConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                instructors.add(mapResultSetToInstructor(rs));
            }

            logger.debug("Found {} instructors", instructors.size());
        } catch (SQLException e) {
            logger.error("Error finding all instructors", e);
        }

        return instructors;
    }

    /**
     * Map ResultSet to Instructor object
     */
    private Instructor mapResultSetToInstructor(ResultSet rs) throws SQLException {
        Instructor instructor = new Instructor();
        instructor.setInstructorId(rs.getInt("instructor_id"));
        instructor.setUserId(rs.getInt("user_id"));
        instructor.setEmployeeId(rs.getString("employee_id"));
        instructor.setDepartment(rs.getString("department"));
        instructor.setEmail(rs.getString("email"));
        instructor.setPhone(rs.getString("phone"));
        instructor.setUsername(rs.getString("username"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            instructor.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            instructor.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return instructor;
    }
}
