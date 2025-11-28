-- ============================================
-- University ERP - Auth Database Seed Data
-- ============================================
-- Sample users with bcrypt hashed passwords
-- Default password for all users: "password123"
-- Hash: $2a$10$YQ7z8Z8Z8Z8Z8Z8Z8Z8Z8eFKvYxGxGxGxGxGxGxGxGxGxGxGxGxGxG
-- ============================================

USE univ_erp_auth;

-- Clear existing data
SET SQL_SAFE_UPDATES = 0;
DELETE FROM password_history;
DELETE FROM users_auth;
SET SQL_SAFE_UPDATES = 1;

-- Reset auto-increment
ALTER TABLE users_auth AUTO_INCREMENT = 1;
ALTER TABLE password_history AUTO_INCREMENT = 1;

-- Insert sample users
-- Password for all: "password123"
-- BCrypt hash: $2a$10$N9qo8uLOickgx2ZMRZoMye1IVI564JCbSJwGwLhLU7dEz8QQeKy4e

INSERT INTO users_auth (username, role, password_hash, status) VALUES
-- Admin users
('admin1', 'ADMIN', '$2a$10$zl/sgaN2tK.1yxSJnaN6eeLRtC6dpZSrBfKNULgCYr7Eamk0po5HK', 'ACTIVE'),

-- Instructor users
('inst1', 'INSTRUCTOR', '$2a$10$zl/sgaN2tK.1yxSJnaN6eeLRtC6dpZSrBfKNULgCYr7Eamk0po5HK', 'ACTIVE'),
('inst2', 'INSTRUCTOR', '$2a$10$zl/sgaN2tK.1yxSJnaN6eeLRtC6dpZSrBfKNULgCYr7Eamk0po5HK', 'ACTIVE'),

-- Student users
('stu1', 'STUDENT', '$2a$10$zl/sgaN2tK.1yxSJnaN6eeLRtC6dpZSrBfKNULgCYr7Eamk0po5HK', 'ACTIVE'),
('stu2', 'STUDENT', '$2a$10$zl/sgaN2tK.1yxSJnaN6eeLRtC6dpZSrBfKNULgCYr7Eamk0po5HK', 'ACTIVE'),
('stu3', 'STUDENT', '$2a$10$zl/sgaN2tK.1yxSJnaN6eeLRtC6dpZSrBfKNULgCYr7Eamk0po5HK', 'ACTIVE');

-- Display created users
SELECT user_id, username, role, status, created_at FROM users_auth ORDER BY user_id;
