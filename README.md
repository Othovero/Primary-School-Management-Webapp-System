# Primary School Management Webapp System

A comprehensive school management system built with Java Servlets, JSP, and MySQL - featuring student registration, grade tracking, teacher management, and academic progress monitoring. By Bakang Raditedu cse22-006 

## Key Features

### Student Management
- Complete student registration with personal and guardian details
- Track academic progress and enrollment history
- Search functionality for student records
- Student class assignment and transfer management

### Teacher Management
- Teacher registration and profile management
- Subject qualification tracking
- Class assignment system
- Grade entry and management

### Academic Management
- Class creation and management
- Subject management
- Grade tracking and reporting
- Top performers identification
- Term-wise academic progress monitoring

### User Management
- Role-based access control (Admin, Teacher, Parent)
- Secure authentication system
- Session management
- "Remember Me" functionality

## Technologies Used

### Backend
- Java Servlets
- Java Server Pages (JSP)
- JSTL
- MySQL Database
- Connection Pooling

### Frontend
- HTML5
- CSS3/Bootstrap 5
- JavaScript

### Architecture
- MVC (Model-View-Controller) Pattern
- DAO (Data Access Object) Pattern

## Project Structure
src/
├── main/
│   ├── java/
│   │   └── com/studentinformationmanagementsystem/
│   │       ├── controller/    # Servlet controllers
│   │       ├── dao/           # Database operations
│   │       ├── model/         # Java beans
│   │       └── util/          # Utility classes
│   └── webapp/
│       ├── WEB-INF/
│       │   ├── views/        # JSP files
│       │   └── web.xml
│       └── resources/        # Static resources

## Setup and Installation

1. **Prerequisites**
   - JDK 11 or higher
   - Apache Tomcat 10
   - MySQL 8.0
   - Maven

2. **Database Setup**
   ```sql
   CREATE DATABASE primary_school_db;
   Run the SQL scripts from the database folder

## Security Features
* Password hashing using BCrypt
* Session management
* SQL injection prevention
* Input validation and sanitization
* Role-based access control

## Core Functionalities

### For Administrators
* Manage student registrations
* Handle teacher assignments
* Create and manage classes
* Monitor academic progress

### For Teachers
* Record and manage grades
* View class rosters
* Track student performance
* Generate academic reports

### For Parents
* View student grades
* Track academic progress
* Access student information

## Contributing
1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request


## Acknowledgments
* Built using Bootstrap for responsive UI
* BCrypt for password hashing
* MySQL Community Edition
