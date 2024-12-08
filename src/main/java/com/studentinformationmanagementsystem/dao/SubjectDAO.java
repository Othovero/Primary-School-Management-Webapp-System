package com.studentinformationmanagementsystem.dao;

import com.studentinformationmanagementsystem.model.Subject;
import com.studentinformationmanagementsystem.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {

    public void insertSubject(Subject subject) throws SQLException {
        String sql = "INSERT INTO subjects (subject_name, description, status) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, subject.getSubjectName());
            stmt.setString(2, subject.getDescription());
            stmt.setBoolean(3, subject.isStatus());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    subject.setSubjectId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public Subject getSubjectById(int subjectId) throws SQLException {
        String sql = "SELECT * FROM subjects WHERE subject_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, subjectId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSubject(rs);
                }
            }
        }
        return null;
    }

    public List<Subject> getAllSubjects() throws SQLException {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT * FROM subjects ORDER BY subject_name";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                subjects.add(mapResultSetToSubject(rs));
            }
        }
        return subjects;
    }

    public List<Subject> searchSubjects(String searchTerm) throws SQLException {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT * FROM subjects WHERE subject_name LIKE ? OR description LIKE ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);

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
        }
        return subjects;
    }

    public boolean existsByName(String subjectName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM subjects WHERE subject_name = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, subjectName);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public List<Subject> getActiveSubjects() throws SQLException {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT * FROM subjects WHERE status = true ORDER BY subject_name";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                subjects.add(mapResultSetToSubject(rs));
            }
        }
        return subjects;
    }

    public void updateSubject(Subject subject) throws SQLException {
        String sql = "UPDATE subjects SET subject_name = ?, description = ?, status = ? WHERE subject_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, subject.getSubjectName());
            stmt.setString(2, subject.getDescription());
            stmt.setBoolean(3, subject.isStatus());
            stmt.setInt(4, subject.getSubjectId());

            stmt.executeUpdate();
        }
    }

    public void deleteSubject(int subjectId) throws SQLException {
        String sql = "DELETE FROM subjects WHERE subject_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, subjectId);
            stmt.executeUpdate();
        }
    }

    public List<Subject> getSubjectsByTeacher(int teacherId) throws SQLException {
        List<Subject> subjects = new ArrayList<>();
        String sql = "SELECT s.* FROM subjects s " +
                "JOIN teacher_subjects ts ON s.subject_id = ts.subject_id " +
                "WHERE ts.teacher_id = ? AND s.status = true";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, teacherId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    subjects.add(mapResultSetToSubject(rs));
                }
            }
        }
        return subjects;
    }

    public boolean isSubjectAssigned(int subjectId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM class_assignments WHERE subject_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, subjectId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private Subject mapResultSetToSubject(ResultSet rs) throws SQLException {
        Subject subject = new Subject();
        subject.setSubjectId(rs.getInt("subject_id"));
        subject.setSubjectName(rs.getString("subject_name"));
        subject.setDescription(rs.getString("description"));
        subject.setStatus(rs.getBoolean("status"));
        return subject;
    }

    public int getTotalCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM subjects WHERE status = true";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
}
