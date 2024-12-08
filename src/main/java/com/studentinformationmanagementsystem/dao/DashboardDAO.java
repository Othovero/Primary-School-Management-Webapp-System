package com.studentinformationmanagementsystem.dao;


import com.studentinformationmanagementsystem.model.*;
import com.studentinformationmanagementsystem.util.DatabaseUtil;
import java.sql.*;
import java.util.*;

public class DashboardDAO {
    private GradesDAO gradesDAO;

    public DashboardDAO() {
        this.gradesDAO = new GradesDAO();
    }

    public Map<String, Object> getParentDashboardData(int userId) throws SQLException {
        Map<String, Object> dashboardData = new HashMap<>();

        String sql = "SELECT s.*, c.class_name, c.academic_year, e.class_id, " +
                "psr.relationship_type " +
                "FROM parent_student_relation psr " +
                "JOIN students s ON psr.student_id = s.student_id " +
                "LEFT JOIN enrollments e ON s.student_id = e.student_id AND e.status = 'ACTIVE' " +
                "LEFT JOIN classes c ON e.class_id = c.class_id " +
                "WHERE psr.parent_id = ? AND s.status = 'ACTIVE'";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            System.out.println("Executing query for parent ID: " + userId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Student Information
                    Student student = new Student();
                    student.setStudentId(rs.getInt("student_id"));
                    student.setFirstName(rs.getString("first_name"));
                    student.setLastName(rs.getString("last_name"));
                    student.setBirthCertificateNo(rs.getString("birth_certificate_no"));
                    student.setStatus(rs.getString("status"));
                    Integer classId = rs.getInt("class_id");
                    if (!rs.wasNull()) {
                        student.setCurrentClassId(classId);
                    }

                    dashboardData.put("studentInfo", student);
                    dashboardData.put("className", rs.getString("class_name"));
                    dashboardData.put("academicYear", rs.getInt("academic_year"));

                    System.out.println("Found student: " + student.getFirstName() + " " + student.getLastName());
                    System.out.println("Class ID: " + classId);

                    // If student has a class, get grades and comments
                    if (classId != null && !rs.wasNull()) {
                        // Get grades
                        Map<String, List<Double>> gradesMap = gradesDAO.getStudentGrades(
                                student.getStudentId(),
                                classId
                        );
                        dashboardData.put("studentGrades", gradesMap);
                        System.out.println("Grades loaded: " + (gradesMap != null ? gradesMap.size() : "0") + " subjects");

                        // Calculate overall average
                        if (gradesMap != null && !gradesMap.isEmpty()) {
                            double totalSum = 0;
                            int totalGrades = 0;
                            for (List<Double> grades : gradesMap.values()) {
                                for (Double grade : grades) {
                                    if (grade != null) {
                                        totalSum += grade;
                                        totalGrades++;
                                    }
                                }
                            }
                            double overallAverage = totalGrades > 0 ? totalSum / totalGrades : 0;
                            dashboardData.put("overallAverage", overallAverage);
                            System.out.println("Overall average calculated: " + overallAverage);
                        }

                        // Get comments
                        List<Map<String, Object>> comments = gradesDAO.getStudentComments(
                                student.getStudentId(),
                                classId
                        );
                        dashboardData.put("teacherComments", comments);
                        System.out.println("Comments loaded: " + (comments != null ? comments.size() : "0") + " comments");
                    }
                } else {
                    System.out.println("No student found for parent ID: " + userId);
                }
            }
        } catch (Exception e) {
            System.err.println("Error in getParentDashboardData: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }

        return dashboardData;
    }
}
