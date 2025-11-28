package edu.univ.erp.data;

import edu.univ.erp.domain.Course;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for Course table operations
 */
public class CourseRepository {
    private static final Logger logger = LoggerFactory.getLogger(CourseRepository.class);

    /**
     * Find course by ID
     */
    public Course findById(int courseId) {
        String sql = "SELECT * FROM courses WHERE course_id = ?";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCourse(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding course by ID: {}", courseId, e);
        }

        return null;
    }

    /**
     * Find course by code
     */
    public Course findByCode(String code) {
        String sql = "SELECT * FROM courses WHERE code = ?";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCourse(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding course by code: {}", code, e);
        }

        return null;
    }

    /**
     * Create a new course
     */
    public int createCourse(Course course) {
        String sql = "INSERT INTO courses (code, title, credits, description, prerequisites) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, course.getCode());
            stmt.setString(2, course.getTitle());
            stmt.setInt(3, course.getCredits());
            stmt.setString(4, course.getDescription());
            stmt.setString(5, course.getPrerequisites());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int courseId = generatedKeys.getInt(1);
                        logger.info("Created course: {} with ID: {}", course.getCode(), courseId);
                        return courseId;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating course: {}", course.getCode(), e);
        }

        return -1;
    }

    /**
     * Update course information
     */
    public boolean updateCourse(Course course) {
        String sql = "UPDATE courses SET title = ?, credits = ?, description = ?, prerequisites = ?, " +
                "updated_at = CURRENT_TIMESTAMP WHERE course_id = ?";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, course.getTitle());
            stmt.setInt(2, course.getCredits());
            stmt.setString(3, course.getDescription());
            stmt.setString(4, course.getPrerequisites());
            stmt.setInt(5, course.getCourseId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Updated course ID: {}", course.getCourseId());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating course: {}", course.getCourseId(), e);
        }

        return false;
    }

    /**
     * Delete a course
     */
    public boolean deleteCourse(int courseId) {
        String sql = "DELETE FROM courses WHERE course_id = ?";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Deleted course ID: {}", courseId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error deleting course: {}", courseId, e);
        }

        return false;
    }

    /**
     * Get all courses
     */
    public List<Course> findAll() {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses ORDER BY code";

        try (Connection conn = DatabaseManager.getErpConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                courses.add(mapResultSetToCourse(rs));
            }

            logger.debug("Found {} courses", courses.size());
        } catch (SQLException e) {
            logger.error("Error finding all courses", e);
        }

        return courses;
    }

    /**
     * Search courses by keyword
     */
    public List<Course> searchCourses(String keyword) {
        List<Course> courses = new ArrayList<>();
        String sql = "SELECT * FROM courses WHERE code LIKE ? OR title LIKE ? ORDER BY code";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    courses.add(mapResultSetToCourse(rs));
                }
            }

            logger.debug("Found {} courses matching '{}'", courses.size(), keyword);
        } catch (SQLException e) {
            logger.error("Error searching courses", e);
        }

        return courses;
    }

    /**
     * Map ResultSet to Course object
     */
    private Course mapResultSetToCourse(ResultSet rs) throws SQLException {
        Course course = new Course();
        course.setCourseId(rs.getInt("course_id"));
        course.setCode(rs.getString("code"));
        course.setTitle(rs.getString("title"));
        course.setCredits(rs.getInt("credits"));
        course.setDescription(rs.getString("description"));
        course.setPrerequisites(rs.getString("prerequisites"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            course.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            course.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return course;
    }
}
