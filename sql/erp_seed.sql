-- ============================================
-- University ERP - Main Database Seed Data
-- ============================================
-- Sample business data linked to Auth DB users
-- user_id values match those from auth_seed.sql
-- ============================================

USE univ_erp;

-- Clear existing data (in reverse order of dependencies)
SET SQL_SAFE_UPDATES = 0;
DELETE FROM grades;
DELETE FROM enrollments;
DELETE FROM sections;
DELETE FROM courses;
DELETE FROM instructors;
DELETE FROM students;
SET SQL_SAFE_UPDATES = 1;

-- Reset auto-increment
ALTER TABLE students AUTO_INCREMENT = 1;
ALTER TABLE instructors AUTO_INCREMENT = 1;
ALTER TABLE courses AUTO_INCREMENT = 1;
ALTER TABLE sections AUTO_INCREMENT = 1;
ALTER TABLE enrollments AUTO_INCREMENT = 1;
ALTER TABLE grades AUTO_INCREMENT = 1;

-- ============================================
-- Insert Instructors (linked to Auth DB)
-- ============================================
INSERT INTO instructors (user_id, employee_id, department, email, phone) VALUES
(2, 'EMP001', 'Computer Science', 'john.doe@univ.edu', '555-0101'),
(3, 'EMP002', 'Mathematics', 'jane.smith@univ.edu', '555-0102');

-- ============================================
-- Insert Students (linked to Auth DB)
-- ============================================
INSERT INTO students (user_id, roll_no, program, year, email, phone) VALUES
(4, '2023CS001', 'B.Tech Computer Science', 2, 'alice.smith@univ.edu', '555-0201'),
(5, '2023CS002', 'B.Tech Computer Science', 2, 'bob.jones@univ.edu', '555-0202');

-- ============================================
-- Insert Courses
-- ============================================
INSERT INTO courses (code, title, credits, description) VALUES
('CSE101', 'Introduction to Programming', 4, 'Fundamentals of programming using Python'),
('CSE201', 'Data Structures', 4, 'Study of fundamental data structures and algorithms'),
('CSE301', 'Database Systems', 3, 'Relational databases, SQL, and database design'),
('MTH101', 'Calculus I', 4, 'Differential and integral calculus'),
('MTH201', 'Linear Algebra', 3, 'Matrices, vector spaces, and linear transformations');

-- ============================================
-- Insert Sections (Fall 2024)
-- ============================================
INSERT INTO sections (course_id, instructor_id, section_number, day_time, room, capacity, semester, year) VALUES
(1, 1, 'A', 'Mon/Wed 09:00-10:30', 'LH-101', 40, 'FALL', 2024),
(1, 1, 'B', 'Tue/Thu 09:00-10:30', 'LH-102', 40, 'FALL', 2024),
(2, 1, 'A', 'Mon/Wed 11:00-12:30', 'LH-201', 35, 'FALL', 2024),
(3, 1, 'A', 'Tue/Thu 14:00-15:30', 'LH-301', 30, 'FALL', 2024),
(4, 2, 'A', 'Mon/Wed 14:00-15:30', 'LH-401', 45, 'FALL', 2024),
(4, 2, 'B', 'Tue/Thu 11:00-12:30', 'LH-402', 45, 'FALL', 2024),
(5, 2, 'A', 'Mon/Wed 16:00-17:30', 'LH-403', 35, 'FALL', 2024);

-- ============================================
-- Insert Sections (Spring 2025)
-- ============================================
INSERT INTO sections (course_id, instructor_id, section_number, day_time, room, capacity, semester, year) VALUES
(1, 1, 'A', 'Mon/Wed 09:00-10:30', 'LH-101', 40, 'SPRING', 2025),
(2, 1, 'A', 'Tue/Thu 11:00-12:30', 'LH-201', 35, 'SPRING', 2025),
(3, 1, 'A', 'Mon/Wed 14:00-15:30', 'LH-301', 30, 'SPRING', 2025),
(4, 2, 'A', 'Tue/Thu 09:00-10:30', 'LH-401', 45, 'SPRING', 2025),
(5, 2, 'A', 'Mon/Wed 16:00-17:30', 'LH-403', 35, 'SPRING', 2025);

-- ============================================
-- Insert Sections (Fall 2025)
-- ============================================
INSERT INTO sections (course_id, instructor_id, section_number, day_time, room, capacity, semester, year) VALUES
(1, 1, 'A', 'Mon/Wed 09:00-10:30', 'LH-101', 40, 'FALL', 2025),
(1, 1, 'B', 'Tue/Thu 09:00-10:30', 'LH-102', 40, 'FALL', 2025),
(2, 1, 'A', 'Mon/Wed 11:00-12:30', 'LH-201', 35, 'FALL', 2025),
(3, 1, 'A', 'Tue/Thu 14:00-15:30', 'LH-301', 30, 'FALL', 2025),
(4, 2, 'A', 'Mon/Wed 14:00-15:30', 'LH-401', 45, 'FALL', 2025),
(4, 2, 'B', 'Tue/Thu 11:00-12:30', 'LH-402', 45, 'FALL', 2025),
(5, 2, 'A', 'Mon/Wed 16:00-17:30', 'LH-403', 35, 'FALL', 2025);

-- ============================================
-- Insert Enrollments
-- ============================================
INSERT INTO enrollments (student_id, section_id, status, enrollment_date, drop_deadline) VALUES
(1, 1, 'ENROLLED', '2024-08-15 10:00:00', '2024-08-29 23:59:59'),
(1, 3, 'ENROLLED', '2024-08-15 10:05:00', '2024-08-29 23:59:59'),
(1, 5, 'ENROLLED', '2024-08-15 10:10:00', '2024-08-29 23:59:59'),
(2, 2, 'ENROLLED', '2024-08-15 11:00:00', '2024-08-29 23:59:59'),
(2, 5, 'ENROLLED', '2024-08-15 11:05:00', '2024-08-29 23:59:59');

-- ============================================
-- Insert Sample Grades
-- ============================================
INSERT INTO grades (enrollment_id, component, score, max_score, weight) VALUES
(1, 'QUIZ', 18.0, 20.0, 20.0),
(1, 'MIDTERM', 75.0, 100.0, 30.0),
(1, 'FINAL', 85.0, 100.0, 50.0),
(2, 'QUIZ', 16.0, 20.0, 20.0),
(2, 'MIDTERM', 68.0, 100.0, 30.0),
(3, 'QUIZ', 19.0, 20.0, 20.0),
(3, 'MIDTERM', 88.0, 100.0, 30.0),
(3, 'FINAL', 92.0, 100.0, 50.0),
(4, 'QUIZ', 15.0, 20.0, 20.0),
(4, 'MIDTERM', 72.0, 100.0, 30.0);

-- ============================================
-- Display Summary
-- ============================================
SELECT 'Students' as Table_Name, COUNT(*) as Count FROM students
UNION ALL
SELECT 'Instructors', COUNT(*) FROM instructors
UNION ALL
SELECT 'Courses', COUNT(*) FROM courses
UNION ALL
SELECT 'Sections', COUNT(*) FROM sections
UNION ALL
SELECT 'Enrollments', COUNT(*) FROM enrollments
UNION ALL
SELECT 'Grades', COUNT(*) FROM grades;
