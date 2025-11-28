-- ============================================
-- Database Constraints for Data Integrity
-- ============================================
-- Run this script to add enterprise-grade constraints
-- to ensure data integrity and prevent invalid data

USE univ_erp;

-- ============================================
-- Sections Table Constraints
-- ============================================

-- Ensure capacity is positive
ALTER TABLE sections 
ADD CONSTRAINT check_capacity 
CHECK (capacity > 0);
    
-- ============================================
-- Courses Table Constraints
-- ============================================

-- Ensure credits are in valid range (1-6)
ALTER TABLE courses 
ADD CONSTRAINT check_credits 
CHECK (credits BETWEEN 1 AND 6);

-- ============================================
-- Enrollments Table Constraints
-- ============================================

-- Ensure unique enrollments (student cannot register for same section twice)
ALTER TABLE enrollments 
ADD CONSTRAINT unique_enrollment 
UNIQUE (student_id, section_id);

-- Ensure enrollment date is before drop deadline
ALTER TABLE enrollments 
ADD CONSTRAINT check_enrollment_dates 
CHECK (enrollment_date < drop_deadline);

-- ============================================
-- Grades Table Constraints
-- ============================================

-- Ensure score is non-negative and doesn't exceed max_score
ALTER TABLE grades 
ADD CONSTRAINT check_score_range 
CHECK (score >= 0 AND score <= max_score);

-- Ensure max_score is positive
ALTER TABLE grades 
ADD CONSTRAINT check_max_score 
CHECK (max_score > 0);

-- Ensure weight is between 0 and 100
ALTER TABLE grades 
ADD CONSTRAINT check_weight_range 
CHECK (weight >= 0 AND weight <= 100);

-- ============================================
-- Students Table Constraints
-- ============================================

-- Ensure year is positive and reasonable (1-6 for undergrad/grad)
ALTER TABLE students 
ADD CONSTRAINT check_student_year 
CHECK (year BETWEEN 1 AND 6);

-- ============================================
-- Display Constraints
-- ============================================

SELECT 
    TABLE_NAME,
    CONSTRAINT_NAME,
    CONSTRAINT_TYPE
FROM 
    INFORMATION_SCHEMA.TABLE_CONSTRAINTS
WHERE 
    TABLE_SCHEMA = 'univ_erp'
    AND CONSTRAINT_TYPE IN ('CHECK', 'UNIQUE')
ORDER BY 
    TABLE_NAME, CONSTRAINT_TYPE;
