package com.studentinformationmanagementsystem.dao;


import com.studentinformationmanagementsystem.model.Teacher;
import com.studentinformationmanagementsystem.model.Subject;
import com.studentinformationmanagementsystem.model.ClassAssignment;
import com.studentinformationmanagementsystem.util.DatabaseUtil;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class TeacherDAO {

    public void insertTeacher(Teacher teacher, String password) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            // First insert into users table
            String userSql = "INSERT INTO users (username, password, user_type, email) VALUES (?, ?, 'TEACHER', ?)";
            int userId;

            try (PreparedStatement stmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, teacher.getEmail());
                stmt.setString(2, password);
                stmt.setString(3, teacher.getEmail());

                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        userId = rs.getInt(1);
                    } else {
                        throw new SQLException("Creating user failed, no ID obtained.");
                    }
                }
            }

            // Set join date if not already set
            if (teacher.getJoinDate() == null) {
                teacher.setJoinDate(new Date(System.currentTimeMillis()));
            }

            // Then insert into teachers table
            String teacherSql = "INSERT INTO teachers (omang_passport_no, first_name, last_name, " +
                    "gender, date_of_birth, address, contact_no, email, qualifications, " +
                    "join_date, user_id, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(teacherSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, teacher.getOmangPassportNo());
                stmt.setString(2, teacher.getFirstName());
                stmt.setString(3, teacher.getLastName());
                stmt.setString(4, teacher.getGender());
                stmt.setDate(5, new java.sql.Date(teacher.getDateOfBirth().getTime()));
                stmt.setString(6, teacher.getAddress());
                stmt.setString(7, teacher.getContactNo());
                stmt.setString(8, teacher.getEmail());
                stmt.setString(9, teacher.getQualifications());
                stmt.setDate(10, new java.sql.Date(teacher.getJoinDate().getTime()));
                stmt.setInt(11, userId);
                stmt.setBoolean(12, true);

                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        teacher.setTeacherId(rs.getInt(1));
                        teacher.setUserId(userId);
                    }
                }
            }

            // Insert teacher's qualified subjects
            if (teacher.getQualifiedSubjects() != null && !teacher.getQualifiedSubjects().isEmpty()) {
                String subjectSql = "INSERT INTO teacher_subjects (teacher_id, subject_id) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(subjectSql)) {
                    for (Subject subject : teacher.getQualifiedSubjects()) {
                        stmt.setInt(1, teacher.getTeacherId());
                        stmt.setInt(2, subject.getSubjectId());
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }
            }

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Teacher getTeacherById(int teacherId) throws SQLException {
        String sql = "SELECT * FROM teachers WHERE teacher_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, teacherId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Teacher teacher = mapResultSetToTeacher(rs);
                    loadTeacherSubjects(teacher);
                    loadClassAssignments(teacher);
                    return teacher;
                }
            }
        }
        return null;
    }

    public Teacher getTeacherByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM teachers WHERE user_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Teacher teacher = mapResultSetToTeacher(rs);
                    loadTeacherSubjects(teacher);
                    loadClassAssignments(teacher);
                    return teacher;
                }
            }
        }
        return null;
    }



    public List<Teacher> getAllTeachers() throws SQLException {
        List<Teacher> teachers = new ArrayList<>();
        String sql = "SELECT DISTINCT t.*, GROUP_CONCAT(s.subject_name) as subject_names " +
                "FROM teachers t " +
                "LEFT JOIN teacher_subjects ts ON t.teacher_id = ts.teacher_id " +
                "LEFT JOIN subjects s ON ts.subject_id = s.subject_id " +
                "WHERE t.status = true " +
                "GROUP BY t.teacher_id " +
                "ORDER BY t.first_name, t.last_name";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Teacher teacher = mapResultSetToTeacher(rs);
                // Get teacher's subjects
                String subjectNames = rs.getString("subject_names");
                if (subjectNames != null) {
                    List<Subject> subjects = new ArrayList<>();
                    for (String subjectName : subjectNames.split(",")) {
                        Subject subject = new Subject();
                        subject.setSubjectName(subjectName.trim());
                        subjects.add(subject);
                    }
                    teacher.setQualifiedSubjects(subjects);
                } else {
                    teacher.setQualifiedSubjects(new ArrayList<>());
                }
                loadClassAssignments(teacher);
                teachers.add(teacher);
            }
        }
        return teachers;
    }

    public List<Teacher> searchTeachers(String searchTerm) throws SQLException {
        List<Teacher> teachers = new ArrayList<>();
        String sql = "SELECT * FROM teachers WHERE status = true AND " +
                "(first_name LIKE ? OR last_name LIKE ? OR " +
                "omang_passport_no LIKE ? OR email LIKE ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            for (int i = 1; i <= 4; i++) {
                stmt.setString(i, searchPattern);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Teacher teacher = mapResultSetToTeacher(rs);
                    loadTeacherSubjects(teacher);
                    loadClassAssignments(teacher);
                    teachers.add(teacher);
                }
            }
        }
        return teachers;
    }

    public void updateTeacher(Teacher teacher) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            // Update teacher information
            String sql = "UPDATE teachers SET omang_passport_no = ?, first_name = ?, last_name = ?, " +
                    "gender = ?, date_of_birth = ?, address = ?, contact_no = ?, " +
                    "email = ?, qualifications = ?, status = ? WHERE teacher_id = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, teacher.getOmangPassportNo());
                stmt.setString(2, teacher.getFirstName());
                stmt.setString(3, teacher.getLastName());
                stmt.setString(4, teacher.getGender());
                stmt.setDate(5, new java.sql.Date(teacher.getDateOfBirth().getTime()));
                stmt.setString(6, teacher.getAddress());
                stmt.setString(7, teacher.getContactNo());
                stmt.setString(8, teacher.getEmail());
                stmt.setString(9, teacher.getQualifications());
                stmt.setBoolean(10, teacher.isStatus());
                stmt.setInt(11, teacher.getTeacherId());

                stmt.executeUpdate();
            }

            // Update user email
            String updateUserSql = "UPDATE users SET email = ?, username = ? WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateUserSql)) {
                stmt.setString(1, teacher.getEmail());
                stmt.setString(2, teacher.getEmail());
                stmt.setInt(3, teacher.getUserId());
                stmt.executeUpdate();
            }

            // Update qualified subjects
            if (teacher.getQualifiedSubjects() != null) {
                // First, remove existing subject associations
                String deleteSubjectsSql = "DELETE FROM teacher_subjects WHERE teacher_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(deleteSubjectsSql)) {
                    stmt.setInt(1, teacher.getTeacherId());
                    stmt.executeUpdate();
                }

                // Then, insert new subject associations
                if (!teacher.getQualifiedSubjects().isEmpty()) {
                    String insertSubjectsSql = "INSERT INTO teacher_subjects (teacher_id, subject_id) VALUES (?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(insertSubjectsSql)) {
                        for (Subject subject : teacher.getQualifiedSubjects()) {
                            stmt.setInt(1, teacher.getTeacherId());
                            stmt.setInt(2, subject.getSubjectId());
                            stmt.addBatch();
                        }
                        stmt.executeBatch();
                    }
                }
            }

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void deleteTeacher(int teacherId) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            // Get user_id first
            int userId;
            String getUserIdSql = "SELECT user_id FROM teachers WHERE teacher_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(getUserIdSql)) {
                stmt.setInt(1, teacherId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Teacher not found");
                    }
                    userId = rs.getInt("user_id");
                }
            }

            // Delete teacher_subjects associations
            String deleteSubjectsSql = "DELETE FROM teacher_subjects WHERE teacher_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteSubjectsSql)) {
                stmt.setInt(1, teacherId);
                stmt.executeUpdate();
            }

            // Delete class assignments
            String deleteAssignmentsSql = "DELETE FROM class_assignments WHERE teacher_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteAssignmentsSql)) {
                stmt.setInt(1, teacherId);
                stmt.executeUpdate();
            }

            // Delete teacher
            String deleteTeacherSql = "DELETE FROM teachers WHERE teacher_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteTeacherSql)) {
                stmt.setInt(1, teacherId);
                stmt.executeUpdate();
            }

            // Delete user
            String deleteUserSql = "DELETE FROM users WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteUserSql)) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Utility methods
    private Teacher mapResultSetToTeacher(ResultSet rs) throws SQLException {
        Teacher teacher = new Teacher();
        teacher.setTeacherId(rs.getInt("teacher_id"));
        teacher.setOmangPassportNo(rs.getString("omang_passport_no"));
        teacher.setFirstName(rs.getString("first_name"));
        teacher.setLastName(rs.getString("last_name"));
        teacher.setGender(rs.getString("gender"));
        teacher.setDateOfBirth(rs.getDate("date_of_birth"));
        teacher.setAddress(rs.getString("address"));
        teacher.setContactNo(rs.getString("contact_no"));
        teacher.setEmail(rs.getString("email"));
        teacher.setQualifications(rs.getString("qualifications"));
        teacher.setJoinDate(rs.getDate("join_date"));
        teacher.setStatus(rs.getBoolean("status"));
        teacher.setUserId(rs.getInt("user_id"));
        return teacher;
    }

    private void loadTeacherSubjects(Teacher teacher) throws SQLException {
        String sql = "SELECT s.* FROM subjects s " +
                "JOIN teacher_subjects ts ON s.subject_id = ts.subject_id " +
                "WHERE ts.teacher_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, teacher.getTeacherId());
            List<Subject> subjects = new ArrayList<>();

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Subject subject = new Subject();
                    subject.setSubjectId(rs.getInt("subject_id"));
                    subject.setSubjectName(rs.getString("subject_name"));
                    subject.setDescription(rs.getString("description"));
                    subject.setStatus(rs.getBoolean("status"));
                    subjects.add(subject);
                }
            }

            teacher.setQualifiedSubjects(subjects);
        }
    }

    private void loadClassAssignments(Teacher teacher) throws SQLException {
        String sql = "SELECT ca.*, c.class_name, s.subject_name " +
                "FROM class_assignments ca " +
                "JOIN classes c ON ca.class_id = c.class_id " +
                "JOIN subjects s ON ca.subject_id = s.subject_id " +
                "WHERE ca.teacher_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, teacher.getTeacherId());
            List<ClassAssignment> assignments = new ArrayList<>();

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ClassAssignment assignment = new ClassAssignment();
                    assignment.setAssignmentId(rs.getInt("assignment_id"));
                    assignment.setTeacherId(rs.getInt("teacher_id"));
                    assignment.setClassId(rs.getInt("class_id"));
                    assignment.setSubjectId(rs.getInt("subject_id"));
                    assignment.setAcademicYear(rs.getInt("academic_year"));
                    assignment.setClassName(rs.getString("class_name"));
                    assignment.setSubjectName(rs.getString("subject_name"));
                    assignments.add(assignment);
                }
            }

            teacher.setClassAssignments(assignments);
        }
    }

    // Additional utility methods
    public int getTotalCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM teachers WHERE status = true";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
    public int getTeacherCountBySubject(int subjectId) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT teacher_id) FROM teacher_subjects WHERE subject_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, subjectId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        }
    }

    public int getTeacherIdForClassAndSubject(int classId, int subjectId) throws SQLException {
        String sql = "SELECT teacher_id FROM class_assignments " +
                "WHERE class_id = ? AND subject_id = ? AND academic_year = ? " +
                "LIMIT 1";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);

            stmt.setInt(1, classId);
            stmt.setInt(2, subjectId);
            stmt.setInt(3, currentYear);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("teacher_id");
                }
                // If no teacher is assigned, return a default value
                // You might want to handle this case differently based on your requirements
                return 1; // Assuming 1 is a valid teacher ID or admin ID
            }
        } catch (SQLException e) {
            System.err.println("Error getting teacher ID for class and subject: " + e.getMessage());
            throw e;
        }
    }

    public boolean exists(String omangPassportNo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM teachers WHERE omang_passport_no = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, omangPassportNo);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public List<Teacher> getTeachersBySubject(int subjectId) throws SQLException {
        List<Teacher> teachers = new ArrayList<>();
        String sql = "SELECT t.* FROM teachers t " +
                "JOIN teacher_subjects ts ON t.teacher_id = ts.teacher_id " +
                "WHERE ts.subject_id = ? AND t.status = true";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, subjectId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Teacher teacher = mapResultSetToTeacher(rs);
                    loadTeacherSubjects(teacher);
                    loadClassAssignments(teacher);
                    teachers.add(teacher);
                }
            }
        }
        return teachers;
    }

    public Map<Integer, Integer> getTeachingLoad() throws SQLException {
        Map<Integer, Integer> loadMap = new HashMap<>();
        String sql = "SELECT teacher_id, COUNT(*) as load FROM class_assignments " +
                "WHERE academic_year = YEAR(CURRENT_DATE) GROUP BY teacher_id";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                loadMap.put(rs.getInt("teacher_id"), rs.getInt("load"));
            }
        }
        return loadMap;
    }

    public void updateTeacherStatus(int teacherId, boolean status) throws SQLException {
        String sql = "UPDATE teachers SET status = ? WHERE teacher_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setBoolean(1, status);
            stmt.setInt(2, teacherId);
            stmt.executeUpdate();
        }
    }

    public List<Teacher> getAvailableTeachers(int subjectId, int academicYear) throws SQLException {
        List<Teacher> teachers = new ArrayList<>();
        String sql = "SELECT DISTINCT t.* FROM teachers t " +
                "JOIN teacher_subjects ts ON t.teacher_id = ts.teacher_id " +
                "WHERE ts.subject_id = ? " +
                "AND t.status = true " +
                "AND t.teacher_id NOT IN (" +
                "    SELECT teacher_id FROM class_assignments " +
                "    WHERE academic_year = ? " +
                "    GROUP BY teacher_id " +
                "    HAVING COUNT(*) >= 5" +  // Assuming max load is 5 classes
                ")";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, subjectId);
            stmt.setInt(2, academicYear);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Teacher teacher = mapResultSetToTeacher(rs);
                    loadTeacherSubjects(teacher);
                    loadClassAssignments(teacher);
                    teachers.add(teacher);
                }
            }
        }
        return teachers;
    }

    public void assignSubjects(int teacherId, List<Integer> subjectIds) throws SQLException {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            // First, remove existing subject assignments
            String deleteSql = "DELETE FROM teacher_subjects WHERE teacher_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteSql)) {
                stmt.setInt(1, teacherId);
                stmt.executeUpdate();
            }

            // Then, insert new subject assignments
            String insertSql = "INSERT INTO teacher_subjects (teacher_id, subject_id) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertSql)) {
                for (Integer subjectId : subjectIds) {
                    stmt.setInt(1, teacherId);
                    stmt.setInt(2, subjectId);
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void assignClass(int teacherId, int classId, int subjectId, int academicYear) throws SQLException {
        String sql = "INSERT INTO class_assignments (teacher_id, class_id, subject_id, academic_year) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, teacherId);
            stmt.setInt(2, classId);
            stmt.setInt(3, subjectId);
            stmt.setInt(4, academicYear);

            stmt.executeUpdate();
        }
    }

    public boolean hasMaximumLoad(int teacherId, int academicYear) throws SQLException {
        String sql = "SELECT COUNT(*) FROM class_assignments " +
                "WHERE teacher_id = ? AND academic_year = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, teacherId);
            stmt.setInt(2, academicYear);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) >= 5; // Assuming max load is 5 classes
            }
        }
    }
}