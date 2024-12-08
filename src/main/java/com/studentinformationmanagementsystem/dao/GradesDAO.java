package com.studentinformationmanagementsystem.dao;

import com.studentinformationmanagementsystem.model.Grade;

import com.studentinformationmanagementsystem.model.Student;

import com.studentinformationmanagementsystem.util.DatabaseUtil;

import java.sql.*;

import java.util.*;

public class GradesDAO {

    public void saveGrade(Grade grade) throws SQLException {

        String sql = "INSERT INTO grades (student_id, subject_id, class_id, academic_year, " +

                "term, grade, grading_date, teacher_id, comments) " +

                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +

                "ON DUPLICATE KEY UPDATE " +

                "grade = VALUES(grade), " +

                "grading_date = VALUES(grading_date), " +

                "comments = VALUES(comments)";

        try (Connection conn = DatabaseUtil.getConnection();

             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, grade.getStudentId());

            stmt.setInt(2, grade.getSubjectId());

            stmt.setInt(3, grade.getClassId());

            stmt.setInt(4, grade.getAcademicYear());

            stmt.setInt(5, grade.getTerm());

            stmt.setDouble(6, grade.getGrade());

            stmt.setDate(7, grade.getGradingDate());

            stmt.setInt(8, grade.getTeacherId());

            stmt.setString(9, grade.getComments());

            stmt.executeUpdate();

        }

    }

    public Grade getGrade(int studentId, int subjectId, int classId, int term, int academicYear) throws SQLException {

        String sql = "SELECT * FROM grades WHERE student_id = ? AND subject_id = ? " +

                "AND class_id = ? AND term = ? AND academic_year = ?";

        try (Connection conn = DatabaseUtil.getConnection();

             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);

            stmt.setInt(2, subjectId);

            stmt.setInt(3, classId);

            stmt.setInt(4, term);

            stmt.setInt(5, academicYear);

            try (ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    return mapResultSetToGrade(rs);

                }

            }

        }

        return null;

    }

    public Map<String, List<Double>> getStudentGrades(int studentId, int classId) throws SQLException {
        Map<String, List<Double>> gradesMap = new LinkedHashMap<>(); // Using LinkedHashMap to maintain order

        String sql = "SELECT g.*, s.subject_name " +
                "FROM subjects s " +
                "LEFT JOIN grades g ON s.subject_id = g.subject_id " +
                "AND g.student_id = ? " +
                "AND g.class_id = ? " +
                "AND g.academic_year = YEAR(CURDATE()) " +
                "ORDER BY s.subject_name, g.term";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, classId);

            System.out.println("Fetching grades for student: " + studentId + ", class: " + classId); // Debug log

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String subjectName = rs.getString("subject_name");
                    double grade = rs.getDouble("grade");
                    int term = rs.getInt("term");

                    // Initialize list for subject if not exists
                    List<Double> termGrades = gradesMap.computeIfAbsent(subjectName, k -> Arrays.asList(null, null, null));

                    // Convert List to ArrayList if it's not already
                    if (!(termGrades instanceof ArrayList)) {
                        termGrades = new ArrayList<>(termGrades);
                        gradesMap.put(subjectName, termGrades);
                    }

                    // Set grade for appropriate term (term - 1 because array is 0-based)
                    if (term > 0 && term <= 3) {
                        ((ArrayList<Double>) termGrades).set(term - 1, grade);
                    }

                    System.out.println("Found grade: " + grade + " for subject: " + subjectName + ", term: " + term); // Debug log
                }
            }
        }

        System.out.println("Returning gradesMap with " + gradesMap.size() + " subjects"); // Debug log
        return gradesMap;
    }


    public List<Map.Entry<Student, Double>> getTopPerformers(int classId, int limit) throws SQLException {
        List<Map.Entry<Student, Double>> topPerformers = new ArrayList<>();

        String sql = "SELECT s.*, AVG(g.grade) as average_grade " +
                "FROM students s " +
                "JOIN enrollments e ON s.student_id = e.student_id " +
                "JOIN grades g ON s.student_id = g.student_id " +
                "WHERE e.class_id = ? " +
                "AND e.status = 'ACTIVE' " +
                "AND g.class_id = ? " +
                "AND g.academic_year = YEAR(CURDATE()) " +
                "GROUP BY s.student_id " +
                "ORDER BY average_grade DESC " +
                "LIMIT ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, classId);
            stmt.setInt(2, classId);
            stmt.setInt(3, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Student student = new Student();
                    student.setStudentId(rs.getInt("student_id"));
                    student.setBirthCertificateNo(rs.getString("birth_certificate_no"));
                    student.setFirstName(rs.getString("first_name"));
                    student.setLastName(rs.getString("last_name"));

                    double averageGrade = rs.getDouble("average_grade");

                    // Create a Map.Entry for the student and their average grade
                    topPerformers.add(new AbstractMap.SimpleEntry<>(student, averageGrade));

                    System.out.println("Added top performer: " + student.getFullName() +
                            " with average: " + averageGrade); // Debug log
                }
            }
        }

        return topPerformers;
    }

    public Map<String, Map.Entry<Student, Double>> getTopPerformersBySubject(int classId) throws SQLException {

        Map<String, Map.Entry<Student, Double>> topPerformers = new HashMap<>();

        String sql = "SELECT s.student_id, s.first_name, s.last_name, s.birth_certificate_no, " +

                "sub.subject_name, g.grade " +

                "FROM grades g " +

                "JOIN students s ON g.student_id = s.student_id " +

                "JOIN subjects sub ON g.subject_id = sub.subject_id " +

                "WHERE g.class_id = ? " +

                "AND g.grade = (" +

                "    SELECT MAX(grade) " +

                "    FROM grades g2 " +

                "    WHERE g2.subject_id = g.subject_id " +

                "    AND g2.class_id = g.class_id" +

                ")";

        try (Connection conn = DatabaseUtil.getConnection();

             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, classId);

            try (ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    Student student = new Student();

                    student.setStudentId(rs.getInt("student_id"));

                    student.setFirstName(rs.getString("first_name"));

                    student.setLastName(rs.getString("last_name"));

                    student.setBirthCertificateNo(rs.getString("birth_certificate_no"));

                    String subjectName = rs.getString("subject_name");

                    double grade = rs.getDouble("grade");

                    topPerformers.put(subjectName, new AbstractMap.SimpleEntry<>(student, grade));

                }

            }

        }

        return topPerformers;

    }

    public List<Map<String, Object>> getStudentComments(int studentId, int classId) throws SQLException {
        List<Map<String, Object>> comments = new ArrayList<>();

        String sql = "SELECT g.comments, g.grading_date, g.term, s.subject_name " +
                "FROM grades g " +
                "JOIN subjects s ON g.subject_id = s.subject_id " +
                "WHERE g.student_id = ? AND g.class_id = ? " +
                "AND g.comments IS NOT NULL AND g.comments != '' " +
                "ORDER BY g.grading_date DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, classId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> comment = new HashMap<>();
                    comment.put("comment", rs.getString("comments"));
                    comment.put("date", rs.getDate("grading_date"));
                    comment.put("term", rs.getInt("term"));
                    comment.put("subjectName", rs.getString("subject_name"));
                    comments.add(comment);
                }
            }
        }
        return comments;
    }


    public double getStudentAverage(int studentId, int classId) throws SQLException {
        String sql = "SELECT AVG(grade) as average " +
                "FROM grades " +
                "WHERE student_id = ? AND class_id = ? AND academic_year = YEAR(CURDATE())";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, classId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double avg = rs.getDouble("average");
                    // Debug log
                    System.out.println("Student ID: " + studentId + ", Average: " + avg);
                    return avg;
                }
            }
        }
        return 0.0;
    }
    public List<Grade> getStudentGradesForClass(int studentId, int classId) throws SQLException {
        List<Grade> grades = new ArrayList<>();
        String sql = "SELECT g.*, s.subject_name " +
                "FROM grades g " +
                "JOIN subjects s ON g.subject_id = s.subject_id " +
                "WHERE g.student_id = ? AND g.class_id = ? AND g.academic_year = YEAR(CURDATE()) " +
                "ORDER BY s.subject_name, g.term";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, classId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Grade grade = mapResultSetToGrade(rs);
                    grades.add(grade);
                    // Debug log
                    System.out.println("Found grade: " + grade.getGrade() +
                            " for subject: " + rs.getString("subject_name"));
                }
            }
        }
        return grades;
    }

    private Grade mapResultSetToGrade(ResultSet rs) throws SQLException {

        Grade grade = new Grade();

        grade.setGradeId(rs.getInt("grade_id"));

        grade.setStudentId(rs.getInt("student_id"));

        grade.setSubjectId(rs.getInt("subject_id"));

        grade.setClassId(rs.getInt("class_id"));

        grade.setAcademicYear(rs.getInt("academic_year"));

        grade.setTerm(rs.getInt("term"));

        grade.setGrade(rs.getDouble("grade"));

        grade.setGradingDate(rs.getDate("grading_date"));

        grade.setTeacherId(rs.getInt("teacher_id"));

        grade.setComments(rs.getString("comments"));

        return grade;

    }

}
