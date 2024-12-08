package com.studentinformationmanagementsystem.dao;



import com.studentinformationmanagementsystem.model.SchoolClass;
import com.studentinformationmanagementsystem.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassDAO {

    public void insertClass(SchoolClass schoolClass) throws SQLException {
        String sql = "INSERT INTO classes (class_name, academic_year, capacity, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, schoolClass.getClassName());
            stmt.setInt(2, schoolClass.getAcademicYear());
            stmt.setInt(3, schoolClass.getCapacity());
            stmt.setBoolean(4, schoolClass.isStatus());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    schoolClass.setClassId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public SchoolClass getClassById(int classId) throws SQLException {
        String sql = "SELECT * FROM classes WHERE class_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, classId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToClass(rs);
                }
            }
        }
        return null;
    }

    public List<SchoolClass> getAllClasses() throws SQLException {
        List<SchoolClass> schoolClasses = new ArrayList<>();
        String sql = "SELECT * FROM classes ORDER BY academic_year DESC, class_name";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                schoolClasses.add(mapResultSetToClass(rs));
            }
        }
        return schoolClasses;
    }

    public List<SchoolClass> getClassesByAcademicYear(int academicYear) throws SQLException {
        List<SchoolClass> classes = new ArrayList<>();
        String sql = "SELECT * FROM classes WHERE academic_year = ? ORDER BY class_name";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, academicYear);
            System.out.println("Executing SQL: " + sql + " with year: " + academicYear);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SchoolClass schoolClass = mapResultSetToClass(rs);
                    classes.add(schoolClass);
                    System.out.println("Loaded class: " + schoolClass.getClassName());
                }
            }
        }

        System.out.println("Returning " + classes.size() + " classes");
        return classes;
    }

    public void updateClass(SchoolClass schoolClass) throws SQLException {
        String sql = "UPDATE classes SET class_name = ?, academic_year = ?, capacity = ?, status = ? " +
                "WHERE class_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, schoolClass.getClassName());
            stmt.setInt(2, schoolClass.getAcademicYear());
            stmt.setInt(3, schoolClass.getCapacity());
            stmt.setBoolean(4, schoolClass.isStatus());
            stmt.setInt(5, schoolClass.getClassId());

            stmt.executeUpdate();
        }
    }

    public void deleteClass(int classId) throws SQLException {
        String sql = "DELETE FROM classes WHERE class_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, classId);
            stmt.executeUpdate();
        }
    }

    public int getCurrentEnrollment(int classId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM enrollments WHERE class_id = ? AND status = 'ACTIVE'";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, classId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private SchoolClass mapResultSetToClass(ResultSet rs) throws SQLException {
        SchoolClass schoolClass = new SchoolClass();
        schoolClass.setClassId(rs.getInt("class_id"));
        schoolClass.setClassName(rs.getString("class_name"));
        schoolClass.setAcademicYear(rs.getInt("academic_year"));
        schoolClass.setCapacity(rs.getInt("capacity"));
        schoolClass.setStatus(rs.getBoolean("status"));
        schoolClass.setCreatedAt(rs.getTimestamp("created_at"));
        return schoolClass;
    }


    // Additional utility methods
    public boolean hasAvailableCapacity(int classId) throws SQLException {
        String sql = "SELECT c.capacity, COUNT(e.enrollment_id) as current_count " +
                "FROM classes c " +
                "LEFT JOIN enrollments e ON c.class_id = e.class_id AND e.status = 'ACTIVE' " +
                "WHERE c.class_id = ? " +
                "GROUP BY c.class_id, c.capacity";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, classId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int capacity = rs.getInt("capacity");
                    int currentCount = rs.getInt("current_count");
                    return currentCount < capacity;
                }
            }
        }
        return false;
    }

    public List<SchoolClass> getClassesByTeacher(int teacherId, int academicYear) throws SQLException {
        List<SchoolClass> schoolClasses = new ArrayList<>();
        String sql = "SELECT DISTINCT c.* FROM classes c " +
                "JOIN class_assignments ca ON c.class_id = ca.class_id " +
                "WHERE ca.teacher_id = ? AND ca.academic_year = ? " +
                "ORDER BY c.class_name";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, teacherId);
            stmt.setInt(2, academicYear);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    schoolClasses.add(mapResultSetToClass(rs));
                }
            }
        }
        return schoolClasses;
    }

    public int getTotalCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM classes WHERE status = true";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    public boolean isClassNameExists(String className, int academicYear) throws SQLException {
        String sql = "SELECT COUNT(*) FROM classes WHERE class_name = ? AND academic_year = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, className);
            stmt.setInt(2, academicYear);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public List<SchoolClass> getAvailableClasses(int academicYear) throws SQLException {
        List<SchoolClass> schoolClasses = new ArrayList<>();
        String sql = "SELECT c.*, " +
                "(SELECT COUNT(*) FROM enrollments e WHERE e.class_id = c.class_id AND e.status = 'ACTIVE') as enrolled " +
                "FROM classes c " +
                "WHERE c.academic_year = ? AND c.status = true " +
                "HAVING enrolled < c.capacity " +
                "ORDER BY c.class_name";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, academicYear);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    schoolClasses.add(mapResultSetToClass(rs));
                }
            }
        }
        return schoolClasses;
    }

    public void updateCapacity(int classId, int newCapacity) throws SQLException {
        String sql = "UPDATE classes SET capacity = ? WHERE class_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newCapacity);
            stmt.setInt(2, classId);
            stmt.executeUpdate();
        }
    }

    public List<SchoolClass> searchClasses(String searchTerm) throws SQLException {
        List<SchoolClass> schoolClasses = new ArrayList<>();
        String sql = "SELECT * FROM classes WHERE class_name LIKE ? ORDER BY academic_year DESC, class_name";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + searchTerm + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    schoolClasses.add(mapResultSetToClass(rs));
                }
            }
        }
        return schoolClasses;
    }

    public Map<Integer, Integer> getClassEnrollmentStats(int academicYear) throws SQLException {
        Map<Integer, Integer> stats = new HashMap<>();
        String sql = "SELECT c.class_id, COUNT(e.enrollment_id) as enrollment_count " +
                "FROM classes c " +
                "LEFT JOIN enrollments e ON c.class_id = e.class_id " +
                "AND e.status = 'ACTIVE' " +
                "WHERE c.academic_year = ? " +
                "GROUP BY c.class_id";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, academicYear);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    stats.put(rs.getInt("class_id"), rs.getInt("enrollment_count"));
                }
            }
        }
        return stats;
    }

    public boolean canDeleteClass(int classId) throws SQLException {
        // Check if there are any active enrollments or assignments
        String sql = "SELECT " +
                "(SELECT COUNT(*) FROM enrollments WHERE class_id = ? AND status = 'ACTIVE') + " +
                "(SELECT COUNT(*) FROM class_assignments WHERE class_id = ?) as total";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, classId);
            stmt.setInt(2, classId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt("total") == 0;
            }
        }
    }
}