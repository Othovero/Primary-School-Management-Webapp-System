package com.studentinformationmanagementsystem.dao;
import com.studentinformationmanagementsystem.model.Enrollment;
import com.studentinformationmanagementsystem.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {
    public void insertEnrollment(Enrollment enrollment) throws SQLException {
        String sql = "INSERT INTO enrollments (student_id, class_id, academic_year, enrollment_date, status) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, enrollment.getStudentId());
            stmt.setInt(2, enrollment.getClassId());
            stmt.setInt(3, enrollment.getAcademicYear());
            stmt.setDate(4, enrollment.getEnrollmentDate());
            stmt.setString(5, enrollment.getStatus());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    enrollment.setEnrollmentId(rs.getInt(1));
                }
            }
        }
    }

    public Enrollment getEnrollment(int enrollmentId) throws SQLException {
        String sql = "SELECT * FROM enrollments WHERE enrollment_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, enrollmentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEnrollment(rs);
                }
            }
        }
        return null;
    }

    public List<Enrollment> getEnrollmentsByStudent(int studentId) throws SQLException {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM enrollments WHERE student_id = ? ORDER BY academic_year DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(mapResultSetToEnrollment(rs));
                }
            }
        }
        return enrollments;
    }

    public List<Enrollment> getEnrollmentsByClass(int classId) throws SQLException {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM enrollments WHERE class_id = ? AND status = 'ACTIVE'";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, classId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(mapResultSetToEnrollment(rs));
                }
            }
        }
        return enrollments;
    }

    public void updateEnrollmentStatus(int enrollmentId, String status) throws SQLException {
        String sql = "UPDATE enrollments SET status = ? WHERE enrollment_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, enrollmentId);

            stmt.executeUpdate();
        }
    }

    public boolean isStudentEnrolledInClass(int studentId, int classId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE student_id = ? AND class_id = ? AND status = 'ACTIVE'";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, classId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public int getCurrentClassIdForStudent(int studentId) throws SQLException {
        String sql = "SELECT class_id FROM enrollments WHERE student_id = ? AND status = 'ACTIVE' " +
                "ORDER BY academic_year DESC, enrollment_date DESC LIMIT 1";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("class_id");
                }
            }
        }
        return 0;
    }

    public int getEnrollmentCount(int classId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE class_id = ? AND status = 'ACTIVE'";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, classId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        }
    }

    public void withdrawEnrollment(int studentId, int classId) throws SQLException {
        String sql = "UPDATE enrollments SET status = 'WITHDRAWN' " +
                "WHERE student_id = ? AND class_id = ? AND status = 'ACTIVE'";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, classId);

            stmt.executeUpdate();
        }
    }

    public List<Enrollment> getEnrollmentsByAcademicYear(int academicYear) throws SQLException {
        List<Enrollment> enrollments = new ArrayList<>();
        String sql = "SELECT * FROM enrollments WHERE academic_year = ? ORDER BY enrollment_date";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, academicYear);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    enrollments.add(mapResultSetToEnrollment(rs));
                }
            }
        }
        return enrollments;
    }

    private Enrollment mapResultSetToEnrollment(ResultSet rs) throws SQLException {
        Enrollment enrollment = new Enrollment();
        enrollment.setEnrollmentId(rs.getInt("enrollment_id"));
        enrollment.setStudentId(rs.getInt("student_id"));
        enrollment.setClassId(rs.getInt("class_id"));
        enrollment.setAcademicYear(rs.getInt("academic_year"));
        enrollment.setEnrollmentDate(rs.getDate("enrollment_date"));
        enrollment.setStatus(rs.getString("status"));
        return enrollment;
    }
}
