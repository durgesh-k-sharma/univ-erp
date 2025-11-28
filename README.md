# University ERP System

A desktop application for university management built with Java and Swing.

## Project Overview

This is a comprehensive ERP system for managing university operations including:
- **Student Management**: Course registration, timetables, grades, transcripts
- **Instructor Management**: Section management, grade entry, class statistics
- **Admin Management**: User management, course/section creation, maintenance mode

## Features

### Role-Based Access Control
- **Admin**: Full system access, user management, maintenance mode control
- **Instructor**: Manage assigned sections, enter grades, view statistics
- **Student**: Register/drop courses, view timetable and grades, download transcript

### Security
- Dual database architecture (Auth DB + ERP DB)
- BCrypt password hashing (UNIX shadow style)
- Session management
- Account lockout after failed login attempts

### Maintenance Mode
- Admin can toggle maintenance mode
- When enabled, students and instructors can view but not modify data
- Visible banner displayed throughout the UI

## Technology Stack

- **Java 17**
- **Swing** with **FlatLaf** modern Look & Feel
- **MySQL** (dual databases)
- **HikariCP** for connection pooling
- **jBCrypt** for password hashing
- **OpenCSV** for CSV exports
- **OpenPDF** for PDF generation
- **SLF4J + Logback** for logging
- **Maven** for build management

## Prerequisites

- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6 or higher

## Quick Setup (Windows)

For a one-click setup experience, use the provided batch script:

```bash
./setup.bat
```

This script will:
1. Drop existing databases (if any)
2. Create fresh `univ_auth` and `univ_erp` databases
3. Load all schemas (auth, erp, constraints, indexes)
4. Load seed data with test accounts
5. Start the application

**Note**: You will be prompted for your MySQL root password.

## Database Setup

### 1. Create Databases

```sql
CREATE DATABASE univ_erp_auth;
CREATE DATABASE univ_erp;
```

### 2. Run Schema Scripts

```bash
# Auth DB schema
mysql -u root -p univ_erp_auth < sql/auth_schema.sql

# ERP DB schema
mysql -u root -p univ_erp < sql/erp_schema.sql
```

### 3. Load Seed Data

```bash
# Auth DB seed data (creates users with password: "password123")
mysql -u root -p univ_erp_auth < sql/auth_seed.sql

# ERP DB seed data
mysql -u root -p univ_erp < sql/erp_seed.sql
```

## Configuration

Edit `src/main/resources/application.properties` to configure database connections:

```properties
# Auth Database
db.auth.url=jdbc:mysql://localhost:3306/univ_erp_auth?useSSL=false&serverTimezone=UTC
db.auth.username=root
db.auth.password=your_password

# ERP Database
db.erp.url=jdbc:mysql://localhost:3306/univ_erp?useSSL=false&serverTimezone=UTC
db.erp.username=root
db.erp.password=your_password
```

## Build and Run

### Using Maven (Recommended)

```bash
# Clean, compile and run
mvn clean compile exec:java
```

### Using JAR

```bash
# Build JAR with dependencies
mvn clean package

# Run the JAR
java -jar target/univ-erp-1.0.0.jar
```

## Testing

### Run All Tests

```bash
mvn test
```

### Test Coverage

- **70 automated tests** covering:
  - Authentication (login, password hashing, account lockout)
  - Domain models (validation, business logic)
  - Services (student, instructor, admin operations)
  - Data repositories (CRUD operations)

### Test Data

- Tests use mocked data and in-memory fixtures
- No database required for unit tests
- Integration tests use test database connections

## Default Test Accounts

All default passwords are: **password123**

| Username | Role       | Description                    |
|----------|------------|--------------------------------|
| admin1   | ADMIN      | System administrator           |
| inst1    | INSTRUCTOR | Instructor with 2 sections     |
| inst2    | INSTRUCTOR | Instructor (Math department)   |
| stu1     | STUDENT    | Student with 3 enrollments     |
| stu2     | STUDENT    | Student with 2 enrollments     |
| stu3     | STUDENT    | Student with no enrollments    |

## Project Structure

```
src/main/java/edu/univ/erp/
├── Main.java                    # Application entry point
├── domain/                      # Domain models
├── auth/                        # Authentication layer
├── data/                        # Data access layer
├── access/                      # Access control
├── service/                     # Business logic
├── ui/                          # User interface
│   ├── admin/                   # Admin panels
│   ├── instructor/              # Instructor panels
│   ├── student/                 # Student panels
│   ├── auth/                    # Login UI
│   └── common/                  # Shared UI components
└── util/                        # Utilities
```

## Development Status

### Completed
- [x] Maven project setup
- [x] Database schema design (Auth DB + ERP DB)
- [x] Seed data scripts
- [x] Domain models
- [x] Database connection management (HikariCP)
- [x] Authentication system (BCrypt, session management)
- [x] Data repositories (all CRUD operations)
- [x] Access control system
- [x] Configuration management
- [x] Service layer (business logic)
- [x] Login UI
- [x] Student UI (catalog, registration, timetable, grades)
- [x] Instructor UI (sections, gradebook, statistics)
- [x] Admin UI (user/course/section management)
- [x] Maintenance mode UI
- [x] Input validation & Error handling
- [x] Testing & Documentation
- [x] Bonus features (CSV, Backup/Restore, Account Lockout)
        
## License

This is an academic project for educational purposes.

