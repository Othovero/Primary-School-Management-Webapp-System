package com.studentinformationmanagementsystem.dao;


import com.studentinformationmanagementsystem.model.Student;
import com.studentinformationmanagementsystem.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    // Create operation
    public void insertStudent(Student student) throws SQLException {
        String sql = "INSERT INTO students (birth_certificate_no, first_name, last_name, " +
                "gender, date_of_birth, address, guardian_name, guardian_contact, " +
                "guardian_email, registration_date, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, student.getBirthCertificateNo());
            stmt.setString(2, student.getFirstName());
            stmt.setString(3, student.getLastName());
            stmt.setString(4, student.getGender());
            stmt.setDate(5, student.getDateOfBirth());  // No conversion needed now
            stmt.setString(6, student.getAddress());
            stmt.setString(7, student.getGuardianName());
            stmt.setString(8, student.getGuardianContact());
            stmt.setString(9, student.getGuardianEmail());
            stmt.setDate(10, student.getRegistrationDate());  // No conversion needed now
            stmt.setString(11, student.getStatus());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    student.setStudentId(generatedKeys.getInt(1));
                }
            }
        }
    }

    // Read operations
    public Student getStudentById(int studentId) throws SQLException {
        String sql = "SELECT s.*, e.class_id " +  // Added e.class_id
                "FROM students s " +
                "LEFT JOIN enrollments e ON s.student_id = e.student_id " +
                "AND e.status = 'ACTIVE' " +
                "WHERE s.student_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Student student = new Student();
                    student.setStudentId(rs.getInt("student_id"));
                    student.setBirthCertificateNo(rs.getString("birth_certificate_no"));
                    student.setFirstName(rs.getString("first_name"));
                    student.setLastName(rs.getString("last_name"));
                    student.setGender(rs.getString("gender"));
                    student.setDateOfBirth(rs.getDate("date_of_birth"));
                    student.setAddress(rs.getString("address"));
                    student.setGuardianName(rs.getString("guardian_name"));
                    student.setGuardianContact(rs.getString("guardian_contact"));
                    student.setGuardianEmail(rs.getString("guardian_email"));
                    student.setStatus(rs.getString("status"));

                    // Get class_id from the enrollments join
                    int classId = rs.getInt("class_id");
                    if (!rs.wasNull()) {
                        student.setCurrentClassId(classId);
                    }

                    return student;
                }
                return null;
            }
        }
    }

    public List<Student> getAllStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.*, e.class_id, c.class_name " +
                "FROM students s " +
                "LEFT JOIN enrollments e ON s.student_id = e.student_id " +
                "AND e.status = 'ACTIVE' " +
                "LEFT JOIN classes c ON e.class_id = c.class_id " +
                "WHERE s.status = 'ACTIVE' " +
                "ORDER BY s.first_name, s.last_name";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Student student = mapResultSetToStudent(rs);
                student.setCurrentClassId(rs.getInt("class_id"));// This will set the current class ID
                student.setClassName(rs.getString("class_name"));
                students.add(student);
            }
        }
        return students;
    }

    public List<Student> searchStudents(String searchTerm) throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE first_name LIKE ? OR last_name LIKE ? " +
                "OR birth_certificate_no LIKE ? OR guardian_name LIKE ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            for (int i = 1; i <= 4; i++) {
                stmt.setString(i, searchPattern);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSetToStudent(rs));
                }
            }
        }
        return students;
    }

    // Update operation
    public void updateStudent(Student student) throws SQLException {
        String sql = "UPDATE students SET birth_certificate_no = ?, first_name = ?, " +
                "last_name = ?, gender = ?, date_of_birth = ?, address = ?, " +
                "guardian_name = ?, guardian_contact = ?, guardian_email = ?, " +
                "status = ?, current_class_id = ?, additional_notes = ? " +
                "WHERE student_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, student.getBirthCertificateNo());
            stmt.setString(2, student.getFirstName());
            stmt.setString(3, student.getLastName());
            stmt.setString(4, student.getGender());
            stmt.setDate(5, new java.sql.Date(student.getDateOfBirth().getTime()));
            stmt.setString(6, student.getAddress());
            stmt.setString(7, student.getGuardianName());
            stmt.setString(8, student.getGuardianContact());
            stmt.setString(9, student.getGuardianEmail());
            stmt.setString(10, student.getStatus());
            if (student.getCurrentClassId() != null) {
                stmt.setInt(11, student.getCurrentClassId());
            } else {
                stmt.setNull(11, Types.INTEGER);
            }
            stmt.setString(12, student.getAdditionalNotes());
            stmt.setInt(13, student.getStudentId());

            stmt.executeUpdate();
        }
    }

    // Delete operation
    public void deleteStudent(int studentId) throws SQLException {
        String sql = "DELETE FROM students WHERE student_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.executeUpdate();
        }
    }

    // Utility methods
    private Student mapResultSetToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getInt("student_id"));
        student.setBirthCertificateNo(rs.getString("birth_certificate_no"));
        student.setFirstName(rs.getString("first_name"));
        student.setLastName(rs.getString("last_name"));
        student.setGender(rs.getString("gender"));
        student.setDateOfBirth(rs.getDate("date_of_birth"));
        student.setAddress(rs.getString("address"));
        student.setGuardianName(rs.getString("guardian_name"));
        student.setGuardianContact(rs.getString("guardian_contact"));
        student.setGuardianEmail(rs.getString("guardian_email"));
        student.setRegistrationDate(rs.getDate("registration_date"));
        student.setStatus(rs.getString("status"));
        int classId = rs.getInt("class_id");
        if (!rs.wasNull()) {
            student.setCurrentClassId(classId);
        }
        student.setAdditionalNotes(rs.getString("additional_notes"));
        return student;
    }

    // Additional utility methods
    public int getTotalCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM students";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    public List<Student> getStudentsByClass(int classId) throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.*, e.class_id " +  // Added e.class_id to select
                "FROM students s " +
                "JOIN enrollments e ON s.student_id = e.student_id " +
                "WHERE e.class_id = ? " +
                "AND e.status = 'ACTIVE' " +
                "AND s.status = 'ACTIVE' " +
                "ORDER BY s.first_name, s.last_name";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, classId);
            System.out.println("Executing SQL for class ID: " + classId); // Debug log

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Student student = new Student();
                    student.setStudentId(rs.getInt("student_id"));
                    student.setBirthCertificateNo(rs.getString("birth_certificate_no"));
                    student.setFirstName(rs.getString("first_name"));
                    student.setLastName(rs.getString("last_name"));
                    student.setGender(rs.getString("gender"));
                    student.setDateOfBirth(rs.getDate("date_of_birth"));
                    student.setAddress(rs.getString("address"));
                    student.setGuardianName(rs.getString("guardian_name"));
                    student.setGuardianContact(rs.getString("guardian_contact"));
                    student.setGuardianEmail(rs.getString("guardian_email"));
                    student.setStatus(rs.getString("status"));
                    student.setCurrentClassId(rs.getInt("class_id")); // Added this line
                    students.add(student);
                    System.out.println("Found student: " + student.getFullName());
                }
            }
        }
        return students;
    }

    public List<Student> getStudentsByStatus(String status) throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE status = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSetToStudent(rs));
                }
            }
        }
        return students;
    }

    public List<Student> getRecentStudents(int limit) throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY registration_date DESC LIMIT ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapResultSetToStudent(rs));
                }
            }
        }
        return students;
    }

    public boolean exists(String birthCertificateNo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM students WHERE birth_certificate_no = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, birthCertificateNo);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public void updateStudentStatus(int studentId, String newStatus) throws SQLException {
        String sql = "UPDATE students SET status = ? WHERE student_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newStatus);
            stmt.setInt(2, studentId);
            stmt.executeUpdate();
        }
    }
    // Add these methods to your existing StudentDAO class

    public List<Student> getUnassignedStudents() throws SQLException {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT s.*, c.class_id, c.class_name " +
                "FROM students s " +
                "LEFT JOIN enrollments e ON s.student_id = e.student_id AND e.status = 'ACTIVE' " +
                "LEFT JOIN classes c ON e.class_id = c.class_id " +
                "LEFT JOIN parent_student_relation psr ON s.student_id = psr.student_id " +
                "WHERE psr.parent_id IS NULL AND s.status = 'ACTIVE' " +
                "ORDER BY s.first_name, s.last_name";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Student student = new Student();
                student.setStudentId(rs.getInt("student_id"));
                student.setBirthCertificateNo(rs.getString("birth_certificate_no"));
                student.setFirstName(rs.getString("first_name"));
                student.setLastName(rs.getString("last_name"));
                student.setGender(rs.getString("gender"));
                student.setDateOfBirth(rs.getDate("date_of_birth"));
                student.setAddress(rs.getString("address"));
                student.setGuardianName(rs.getString("guardian_name"));
                student.setGuardianContact(rs.getString("guardian_contact"));
                student.setGuardianEmail(rs.getString("guardian_email"));
                student.setRegistrationDate(rs.getDate("registration_date"));
                student.setStatus(rs.getString("status"));

                // Get class information
                int classId = rs.getInt("class_id");
                if (!rs.wasNull()) {
                    student.setCurrentClassId(classId);
                    student.setClassName(rs.getString("class_name"));
                }

                students.add(student);
            }
        }
        return students;
    }

    public void assignParentToStudent(int studentId, int parentId) throws SQLException {
        String sql = "INSERT INTO parent_student_relation (student_id, parent_id) VALUES (?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, parentId);
            stmt.executeUpdate();
        }
    }

    public Student getStudentByParentId(int parentId) throws SQLException {
        String sql = "SELECT s.* FROM students s " +
                "JOIN parent_student_relation psr ON s.student_id = psr.student_id " +
                "WHERE psr.parent_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, parentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToStudent(rs);
                }
            }
        }
        return null;
    }
}