-- ============================================
-- Performance Indexes for Query Optimization
-- ============================================
-- Run this script to add indexes for better query performance

USE univ_erp;

-- ============================================
-- Sections Table Indexes
-- ============================================

-- Speed up course catalog queries by semester/year
CREATE INDEX idx_sections_semester_year 
ON sections(semester, year);

-- Speed up section lookups by course
CREATE INDEX idx_sections_course_id 
ON sections(course_id);

-- Speed up instructor section queries
CREATE INDEX idx_sections_instructor_id 
ON sections(instructor_id);

-- ============================================
-- Enrollments Table Indexes
-- ============================================

-- Speed up student enrollment queries
CREATE INDEX idx_enrollments_student_id 
ON enrollments(student_id);

-- Speed up section enrollment queries
CREATE INDEX idx_enrollments_section_id 
ON enrollments(section_id);

-- Speed up enrollment status queries
CREATE INDEX idx_enrollments_status 
ON enrollments(status);

-- Composite index for active enrollments
CREATE INDEX idx_enrollments_student_status 
ON enrollments(student_id, status);

-- ============================================
-- Grades Table Indexes
-- ============================================

-- Speed up grade lookups by enrollment
CREATE INDEX idx_grades_enrollment_id 
ON grades(enrollment_id);

-- Speed up grade component queries
CREATE INDEX idx_grades_component 
ON grades(component);

-- ============================================
-- Students Table Indexes
-- ============================================

-- Speed up student lookups by user_id
CREATE INDEX idx_students_user_id 
ON students(user_id);

-- Speed up student searches by roll number
CREATE INDEX idx_students_roll_no 
ON students(roll_no);

-- ============================================
-- Instructors Table Indexes
-- ============================================

-- Speed up instructor lookups by user_id
CREATE INDEX idx_instructors_user_id 
ON instructors(user_id);

-- Speed up instructor searches by employee_id
CREATE INDEX idx_instructors_employee_id 
ON instructors(employee_id);

-- ============================================
-- Courses Table Indexes
-- ============================================

-- Speed up course searches by code
CREATE INDEX idx_courses_code 
ON courses(code);

-- ============================================
-- Display Indexes
-- ============================================

SELECT 
    TABLE_NAME,
    INDEX_NAME,
    COLUMN_NAME,
    SEQ_IN_INDEX
FROM 
    INFORMATION_SCHEMA.STATISTICS
WHERE 
    TABLE_SCHEMA = 'univ_erp'
    AND INDEX_NAME != 'PRIMARY'
ORDER BY 
    TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;
