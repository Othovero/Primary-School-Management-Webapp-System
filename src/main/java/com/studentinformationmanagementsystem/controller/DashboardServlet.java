package com.studentinformationmanagementsystem.controller;

import com.studentinformationmanagementsystem.dao.*;
import com.studentinformationmanagementsystem.model.*;
import com.studentinformationmanagementsystem.util.DatabaseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private StudentDAO studentDAO;
    private TeacherDAO teacherDAO;
    private ClassDAO classDAO;
    private SubjectDAO subjectDAO;
    private GradesDAO gradesDAO;
    private UserDAO userDAO;
    private DashboardDAO dashboardDAO;

    @Override
    public void init() throws ServletException {
        studentDAO = new StudentDAO();
        teacherDAO = new TeacherDAO();
        classDAO = new ClassDAO();
        subjectDAO = new SubjectDAO();
        gradesDAO = new GradesDAO();
        userDAO = new UserDAO();
        dashboardDAO = new DashboardDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("userId") == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            String userType = (String) session.getAttribute("userType");
            int userId = (int) session.getAttribute("userId");

            switch (userType) {
                case "ADMIN":
                    loadAdminDashboard(request);
                    break;
                case "TEACHER":
                    loadTeacherDashboard(request, userId);
                    break;
                case "PARENT":
                    response.sendRedirect(request.getContextPath() + "/parent?action=dashboard");
                    return;
                default:
                    request.setAttribute("error", "Invalid user type");
            }

            request.getRequestDispatcher("/main-dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "Error loading dashboard: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }

    private void loadAdminDashboard(HttpServletRequest request) throws SQLException {
        try {
            // Load statistics for admin dashboard
            request.setAttribute("totalStudents", studentDAO.getTotalCount());
            request.setAttribute("totalTeachers", teacherDAO.getTotalCount());
            request.setAttribute("totalClasses", classDAO.getTotalCount());
            request.setAttribute("totalSubjects", subjectDAO.getTotalCount());

            // Get recent enrollments or other relevant data
            List<Student> recentStudents = studentDAO.getRecentStudents(5);
            request.setAttribute("recentStudents", recentStudents);

            // Get all classes for filtering
            List<SchoolClass> schoolClasses = classDAO.getAllClasses();
            request.setAttribute("schoolClasses", schoolClasses);

            // Get current academic year
            int currentYear = java.time.Year.now().getValue();
            request.setAttribute("currentYear", currentYear);

        } catch (SQLException e) {
            throw new SQLException("Error loading admin dashboard: " + e.getMessage());
        }
    }

    private void loadTeacherDashboard(HttpServletRequest request, int userId) throws SQLException {
        try {
            // Get teacher information
            Teacher teacher = teacherDAO.getTeacherByUserId(userId);
            if (teacher != null) {
                request.setAttribute("teacher", teacher);

                // Get teacher's classes for current year
                List<SchoolClass> teacherClasses = classDAO.getClassesByTeacher(
                        teacher.getTeacherId(),
                        java.time.Year.now().getValue()
                );
                request.setAttribute("teacherClasses", teacherClasses);

                // Get subjects taught by teacher
                List<Subject> teacherSubjects = subjectDAO.getSubjectsByTeacher(teacher.getTeacherId());
                request.setAttribute("teacherSubjects", teacherSubjects);

                // Get class assignments
                List<ClassAssignment> classAssignments = teacher.getClassAssignments();
                if (classAssignments != null && !classAssignments.isEmpty()) {
                    Map<Integer, List<Student>> classStudents = new HashMap<>();
                    for (ClassAssignment assignment : classAssignments) {
                        List<Student> students = studentDAO.getStudentsByClass(assignment.getClassId());
                        classStudents.put(assignment.getClassId(), students);
                    }
                    request.setAttribute("classStudents", classStudents);
                }

                // Calculate teaching load
                int teachingLoad = teacher.getTeachingLoad();
                request.setAttribute("teachingLoad", teachingLoad);

            } else {
                request.setAttribute("error", "Teacher information not found.");
            }
        } catch (SQLException e) {
            throw new SQLException("Error loading teacher dashboard: " + e.getMessage());
        }
    }


    private double calculateOverallAverage(Map<String, List<Double>> gradesMap) {
        if (gradesMap == null || gradesMap.isEmpty()) {
            return 0.0;
        }

        double totalSum = 0.0;
        int totalGrades = 0;

        for (List<Double> grades : gradesMap.values()) {
            for (Double grade : grades) {
                if (grade != null) {
                    totalSum += grade;
                    totalGrades++;
                }
            }
        }

        return totalGrades > 0 ? totalSum / totalGrades : 0.0;
    }
}