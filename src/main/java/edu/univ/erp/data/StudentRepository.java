package edu.univ.erp.data;

import edu.univ.erp.domain.Student;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for Student table operations
 */
public class StudentRepository {
    private static final Logger logger = LoggerFactory.getLogger(StudentRepository.class);

    /**
     * Find student by user ID
     */
    public Student findByUserId(int userId) {
        String sql = "SELECT s.*, u.username FROM students s " +
                "LEFT JOIN univ_erp_auth.users_auth u ON s.user_id = u.user_id " +
                "WHERE s.user_id = ?";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding student by user ID: {}", userId, e);
        }

        return null;
    }

    /**
     * Find student by student ID
     */
    public Student findById(int studentId) {
        String sql = "SELECT s.*, u.username FROM students s " +
                "LEFT JOIN univ_erp_auth.users_auth u ON s.user_id = u.user_id " +
                "WHERE s.student_id = ?";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding student by ID: {}", studentId, e);
        }

        return null;
    }

    /**
     * Find student by roll number
     */
    public Student findByRollNo(String rollNo) {
        String sql = "SELECT s.*, u.username FROM students s " +
                "LEFT JOIN univ_erp_auth.users_auth u ON s.user_id = u.user_id " +
                "WHERE s.roll_no = ?";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, rollNo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding student by roll number: {}", rollNo, e);
        }

        return null;
    }

    /**
     * Create a new student
     */
    public int createStudent(Student student) {
        String sql = "INSERT INTO students (user_id, roll_no, program, year, email, phone) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, student.getUserId());
            stmt.setString(2, student.getRollNo());
            stmt.setString(3, student.getProgram());
            stmt.setInt(4, student.getYear());
            stmt.setString(5, student.getEmail());
            stmt.setString(6, student.getPhone());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int studentId = generatedKeys.getInt(1);
                        logger.info("Created student: {} with ID: {}", student.getRollNo(), studentId);
                        return studentId;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating student: {}", student.getRollNo(), e);
        }

        return -1;
    }

    /**
     * Update student information
     */
    public boolean updateStudent(Student student) {
        String sql = "UPDATE students SET program = ?, year = ?, email = ?, phone = ?, " +
                "updated_at = CURRENT_TIMESTAMP WHERE student_id = ?";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, student.getProgram());
            stmt.setInt(2, student.getYear());
            stmt.setString(3, student.getEmail());
            stmt.setString(4, student.getPhone());
            stmt.setInt(5, student.getStudentId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Updated student ID: {}", student.getStudentId());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating student: {}", student.getStudentId(), e);
        }

        return false;
    }

    /**
     * Get all students
     */
    public List<Student> findAll() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.*, u.username FROM students s " +
                "LEFT JOIN univ_erp_auth.users_auth u ON s.user_id = u.user_id " +
                "ORDER BY s.roll_no";

        try (Connection conn = DatabaseManager.getErpConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                students.add(mapResultSetToStudent(rs));
            }

            logger.debug("Found {} students", students.size());
        } catch (SQLException e) {
            logger.error("Error finding all students", e);
        }

        return students;
    }

    /**
     * Map ResultSet to Student object
     */
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getInt("student_id"));
        student.setUserId(rs.getInt("user_id"));
        student.setRollNo(rs.getString("roll_no"));
        student.setProgram(rs.getString("program"));
        student.setYear(rs.getInt("year"));
        student.setEmail(rs.getString("email"));
        student.setPhone(rs.getString("phone"));
        student.setUsername(rs.getString("username"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            student.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            student.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return student;
    }
}
