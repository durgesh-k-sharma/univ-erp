package edu.univ.erp.api.catalog;

import edu.univ.erp.domain.Course;
import edu.univ.erp.domain.Section;
import edu.univ.erp.service.StudentService;
import edu.univ.erp.data.CourseRepository;
import edu.univ.erp.data.SectionRepository;

import java.util.List;

/**
 * API for browsing course catalog
 */
public class CatalogApi {
    private final StudentService studentService;
    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;

    public CatalogApi() {
        this.studentService = new StudentService();
        this.courseRepository = new CourseRepository();
        this.sectionRepository = new SectionRepository();
    }

    /**
     * Get all available courses
     */
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    /**
     * Get sections for a course
     */
    public List<Section> getSectionsForCourse(int courseId) {
        return sectionRepository.findByCourseId(courseId);
    }

    /**
     * Search courses
     */
    public List<Course> searchCourses(String query) {
        // Simple search implementation delegating to repository or service
        // For now, we can reuse findAll and filter, or add search to repo
        // Using existing service method if available, or direct repo access as
        // read-only
        return courseRepository.findAll(); // Placeholder for actual search logic if needed
    }

    /**
     * Get course details
     */
    public Course getCourse(int courseId) {
        return courseRepository.findById(courseId);
    }
}
