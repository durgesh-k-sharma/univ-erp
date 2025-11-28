package edu.univ.erp.data;

import edu.univ.erp.domain.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for Section table operations
 */
public class SectionRepository {
    private static final Logger logger = LoggerFactory.getLogger(SectionRepository.class);

    /**
     * Find section by ID with full details
     */
    public Section findById(int sectionId) {
        String sql = "SELECT s.*, c.code as course_code, c.title as course_title, c.credits as course_credits, " +
                "i.username as instructor_name, " +
                "(SELECT COUNT(*) FROM enrollments WHERE section_id = s.section_id AND status = 'ENROLLED') as enrolled_count "
                +
                "FROM sections s " +
                "JOIN courses c ON s.course_id = c.course_id " +
                "LEFT JOIN instructors inst ON s.instructor_id = inst.instructor_id " +
                "LEFT JOIN univ_erp_auth.users_auth i ON inst.user_id = i.user_id " +
                "WHERE s.section_id = ?";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sectionId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSection(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding section by ID: {}", sectionId, e);
        }

        return null;
    }

    /**
     * Find sections by instructor ID
     */
    public List<Section> findByInstructor(int instructorId) {
        List<Section> sections = new ArrayList<>();
        String sql = "SELECT s.*, c.code as course_code, c.title as course_title, c.credits as course_credits, " +
                "i.username as instructor_name, " +
                "(SELECT COUNT(*) FROM enrollments WHERE section_id = s.section_id AND status = 'ENROLLED') as enrolled_count "
                +
                "FROM sections s " +
                "JOIN courses c ON s.course_id = c.course_id " +
                "LEFT JOIN instructors inst ON s.instructor_id = inst.instructor_id " +
                "LEFT JOIN univ_erp_auth.users_auth i ON inst.user_id = i.user_id " +
                "WHERE s.instructor_id = ? " +
                "ORDER BY s.semester, s.year DESC, c.code";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, instructorId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sections.add(mapResultSetToSection(rs));
                }
            }

            logger.debug("Found {} sections for instructor ID: {}", sections.size(), instructorId);
        } catch (SQLException e) {
            logger.error("Error finding sections by instructor: {}", instructorId, e);
        }

        return sections;
    }

    /**
     * Find sections by semester and year
     */
    public List<Section> findBySemesterYear(String semester, int year) {
        List<Section> sections = new ArrayList<>();
        String sql = "SELECT s.*, c.code as course_code, c.title as course_title, c.credits as course_credits, " +
                "i.username as instructor_name, " +
                "(SELECT COUNT(*) FROM enrollments WHERE section_id = s.section_id AND status = 'ENROLLED') as enrolled_count "
                +
                "FROM sections s " +
                "JOIN courses c ON s.course_id = c.course_id " +
                "LEFT JOIN instructors inst ON s.instructor_id = inst.instructor_id " +
                "LEFT JOIN univ_erp_auth.users_auth i ON inst.user_id = i.user_id " +
                "WHERE s.semester = ? AND s.year = ? " +
                "ORDER BY c.code, s.section_number";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, semester);
            stmt.setInt(2, year);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sections.add(mapResultSetToSection(rs));
                }
            }

            logger.debug("Found {} sections for {} {}", sections.size(), semester, year);
        } catch (SQLException e) {
            logger.error("Error finding sections by semester/year: {} {}", semester, year, e);
        }

        return sections;
    }

    // Find sections by course ID
    public List<Section> findByCourseId(int courseId) {
        List<Section> sections = new ArrayList<>();
        String sql = "SELECT s.*, c.code as course_code, c.title as course_title, c.credits as course_credits, " +
                "i.username as instructor_name, " +
                "(SELECT COUNT(*) FROM enrollments WHERE section_id = s.section_id AND status = 'ENROLLED') as enrolled_count "
                +
                "FROM sections s " +
                "JOIN courses c ON s.course_id = c.course_id " +
                "LEFT JOIN instructors inst ON s.instructor_id = inst.instructor_id " +
                "LEFT JOIN univ_erp_auth.users_auth i ON inst.user_id = i.user_id " +
                "WHERE s.course_id = ? " +
                "ORDER BY s.semester, s.year DESC, s.section_number";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, courseId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    sections.add(mapResultSetToSection(rs));
                }
            }

            logger.debug("Found {} sections for course ID: {}", sections.size(), courseId);
        } catch (SQLException e) {
            logger.error("Error finding sections by course ID: {}", courseId, e);
        }

        return sections;
    }

    /**
     * Create a new section
     */
    public int createSection(Section section) {
        String sql = "INSERT INTO sections (course_id, instructor_id, section_number, day_time, room, capacity, semester, year) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, section.getCourseId());
            if (section.getInstructorId() != null) {
                stmt.setInt(2, section.getInstructorId());
            } else {
                stmt.setNull(2, Types.INTEGER);
            }
            stmt.setString(3, section.getSectionNumber());
            stmt.setString(4, section.getDayTime());
            stmt.setString(5, section.getRoom());
            stmt.setInt(6, section.getCapacity());
            stmt.setString(7, section.getSemester());
            stmt.setInt(8, section.getYear());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int sectionId = generatedKeys.getInt(1);
                        logger.info("Created section ID: {}", sectionId);
                        return sectionId;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating section", e);
        }

        return -1;
    }

    /**
     * Update section information
     */
    public boolean updateSection(Section section) {
        String sql = "UPDATE sections SET instructor_id = ?, day_time = ?, room = ?, capacity = ?, " +
                "updated_at = CURRENT_TIMESTAMP WHERE section_id = ?";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (section.getInstructorId() != null) {
                stmt.setInt(1, section.getInstructorId());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            stmt.setString(2, section.getDayTime());
            stmt.setString(3, section.getRoom());
            stmt.setInt(4, section.getCapacity());
            stmt.setInt(5, section.getSectionId());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Updated section ID: {}", section.getSectionId());
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error updating section: {}", section.getSectionId(), e);
        }

        return false;
    }

    /**
     * Assign instructor to section
     */
    public boolean assignInstructor(int sectionId, int instructorId) {
        String sql = "UPDATE sections SET instructor_id = ?, updated_at = CURRENT_TIMESTAMP WHERE section_id = ?";

        try (Connection conn = DatabaseManager.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, instructorId);
            stmt.setInt(2, sectionId);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                logger.info("Assigned instructor {} to section {}", instructorId, sectionId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error assigning instructor to section", e);
        }

        return false;
    }

    /**
     * Check section capacity
     */
    public boolean hasAvailableSeats(int sectionId) {
        Section section = findById(sectionId);
        return section != null && !section.isFull();
    }

    /**
     * Get all sections
     */
    public List<Section> findAll() {
        List<Section> sections = new ArrayList<>();
        String sql = "SELECT s.*, c.code as course_code, c.title as course_title, c.credits as course_credits, " +
                "i.username as instructor_name, " +
                "(SELECT COUNT(*) FROM enrollments WHERE section_id = s.section_id AND status = 'ENROLLED') as enrolled_count "
                +
                "FROM sections s " +
                "JOIN courses c ON s.course_id = c.course_id " +
                "LEFT JOIN instructors inst ON s.instructor_id = inst.instructor_id " +
                "LEFT JOIN univ_erp_auth.users_auth i ON inst.user_id = i.user_id " +
                "ORDER BY s.year DESC, s.semester, c.code";

        try (Connection conn = DatabaseManager.getErpConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                sections.add(mapResultSetToSection(rs));
            }

            logger.debug("Found {} sections", sections.size());
        } catch (SQLException e) {
            logger.error("Error finding all sections", e);
        }

        return sections;
    }

    /**
     * Map ResultSet to Section object
     */
    private Section mapResultSetToSection(ResultSet rs) throws SQLException {
        Section section = new Section();
        section.setSectionId(rs.getInt("section_id"));
        section.setCourseId(rs.getInt("course_id"));

        int instructorId = rs.getInt("instructor_id");
        if (!rs.wasNull()) {
            section.setInstructorId(instructorId);
        }

        section.setSectionNumber(rs.getString("section_number"));
        section.setDayTime(rs.getString("day_time"));
        section.setRoom(rs.getString("room"));
        section.setCapacity(rs.getInt("capacity"));
        section.setSemester(rs.getString("semester"));
        section.setYear(rs.getInt("year"));

        // Joined data
        section.setCourseCode(rs.getString("course_code"));
        section.setCourseTitle(rs.getString("course_title"));
        section.setCourseCredits(rs.getInt("course_credits"));
        section.setInstructorName(rs.getString("instructor_name"));
        section.setEnrolledCount(rs.getInt("enrolled_count"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            section.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            section.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return section;
    }
}
