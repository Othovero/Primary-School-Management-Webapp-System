package com.studentinformationmanagementsystem.dao;


import com.studentinformationmanagementsystem.model.*;
import com.studentinformationmanagementsystem.util.DatabaseUtil;
import java.sql.*;
import java.util.*;

public class ParentDAO {

    public Map<String, Object> getDashboardData(int parentId) throws SQLException {
        Map<String, Object> data = new HashMap<>();

        String sql = "SELECT s.*, e.class_id, c.class_name, c.academic_year, " +  // Added e.class_id
                "psr.relationship_type " +
                "FROM parent_student_relation psr " +
                "JOIN students s ON psr.student_id = s.student_id " +
                "LEFT JOIN enrollments e ON s.student_id = e.student_id " +
                "AND e.status = 'ACTIVE' " +
                "LEFT JOIN classes c ON e.class_id = c.class_id " +
                "WHERE psr.parent_id = ? AND s.status = 'ACTIVE'";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, parentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Student student = extractStudentFromResultSet(rs);

                // Get class ID from enrollment
                int classId = rs.getInt("class_id");
                if (!rs.wasNull()) {
                    student.setCurrentClassId(classId);
                }

                data.put("student", student);
                data.put("className", rs.getString("class_name"));
                data.put("academicYear", rs.getInt("academic_year"));
                data.put("relationshipType", rs.getString("relationship_type"));

                // Get grades if student has a class
                if (student.getCurrentClassId() != null) {
                    GradesDAO gradesDAO = new GradesDAO();
                    Map<String, List<Double>> grades = gradesDAO.getStudentGrades(
                            student.getStudentId(),
                            student.getCurrentClassId()
                    );
                    data.put("studentGrades", grades);

                    List<Map<String, Object>> comments = gradesDAO.getStudentComments(
                            student.getStudentId(),
                            student.getCurrentClassId()
                    );
                    data.put("teacherComments", comments);
                }
            }
        }
        return data;
    }

    public Student getChildByParentId(int parentId) throws SQLException {
        String sql = "SELECT s.* FROM students s " +
                "JOIN parent_student_relation psr ON s.student_id = psr.student_id " +
                "WHERE psr.parent_id = ? AND s.status = 'ACTIVE'";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, parentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return extractStudentFromResultSet(rs);
            }
        }
        return null;
    }

    private Student extractStudentFromResultSet(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setStudentId(rs.getInt("student_id"));
        student.setFirstName(rs.getString("first_name"));
        student.setLastName(rs.getString("last_name"));
        student.setBirthCertificateNo(rs.getString("birth_certificate_no"));
        student.setStatus(rs.getString("status"));
        return student;
    }
}