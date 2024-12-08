package com.studentinformationmanagementsystem.dao;

import com.studentinformationmanagementsystem.model.ClassAssignment;
import com.studentinformationmanagementsystem.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClassAssignmentDAO {

    public void insertAssignment(ClassAssignment assignment) throws SQLException {
        String sql = "INSERT INTO class_assignments (teacher_id, class_id, subject_id, academic_year) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, assignment.getTeacherId());
            stmt.setInt(2, assignment.getClassId());
            stmt.setInt(3, assignment.getSubjectId());
            stmt.setInt(4, assignment.getAcademicYear());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    assignment.setAssignmentId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public List<ClassAssignment> getAssignmentsByTeacher(int teacherId, int academicYear) throws SQLException {
        List<ClassAssignment> assignments = new ArrayList<>();
        String sql = "SELECT ca.*, c.class_name, s.subject_name, " +
                "CONCAT(t.first_name, ' ', t.last_name) as teacher_name " +
                "FROM class_assignments ca " +
                "JOIN classes c ON ca.class_id = c.class_id " +
                "JOIN subjects s ON ca.subject_id = s.subject_id " +
                "JOIN teachers t ON ca.teacher_id = t.teacher_id " +
                "WHERE ca.teacher_id = ? AND ca.academic_year = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, teacherId);
            stmt.setInt(2, academicYear);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    assignments.add(mapResultSetToClassAssignment(rs));
                }
            }
        }
        return assignments;
    }

    public List<ClassAssignment> getAssignmentsByClass(int classId, int academicYear) throws SQLException {
        List<ClassAssignment> assignments = new ArrayList<>();
        String sql = "SELECT ca.*, c.class_name, s.subject_name, " +
                "CONCAT(t.first_name, ' ', t.last_name) as teacher_name " +
                "FROM class_assignments ca " +
                "JOIN classes c ON ca.class_id = c.class_id " +
                "JOIN subjects s ON ca.subject_id = s.subject_id " +
                "JOIN teachers t ON ca.teacher_id = t.teacher_id " +
                "WHERE ca.class_id = ? AND ca.academic_year = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, classId);
            stmt.setInt(2, academicYear);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    assignments.add(mapResultSetToClassAssignment(rs));
                }
            }
        }
        return assignments;
    }

    public void deleteAssignment(int assignmentId) throws SQLException {
        String sql = "DELETE FROM class_assignments WHERE assignment_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, assignmentId);
            stmt.executeUpdate();
        }
    }

    public boolean isTeacherAssigned(int teacherId, int classId, int academicYear) throws SQLException {
        String sql = "SELECT COUNT(*) FROM class_assignments " +
                "WHERE teacher_id = ? AND class_id = ? AND academic_year = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, teacherId);
            stmt.setInt(2, classId);
            stmt.setInt(3, academicYear);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private ClassAssignment mapResultSetToClassAssignment(ResultSet rs) throws SQLException {
        ClassAssignment assignment = new ClassAssignment();
        assignment.setAssignmentId(rs.getInt("assignment_id"));
        assignment.setTeacherId(rs.getInt("teacher_id"));
        assignment.setClassId(rs.getInt("class_id"));
        assignment.setSubjectId(rs.getInt("subject_id"));
        assignment.setAcademicYear(rs.getInt("academic_year"));
        assignment.setClassName(rs.getString("class_name"));
        assignment.setSubjectName(rs.getString("subject_name"));
        assignment.setTeacherName(rs.getString("teacher_name"));
        return assignment;
    }
}
