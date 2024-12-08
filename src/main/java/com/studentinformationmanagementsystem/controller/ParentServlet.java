package com.studentinformationmanagementsystem.controller;


import com.studentinformationmanagementsystem.dao.*;
import com.studentinformationmanagementsystem.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

@WebServlet("/parent")  // Change from "/parent/" to "/parent/*" to handle sub-paths
public class ParentServlet extends HttpServlet {
    private ParentDAO parentDAO;
    private StudentDAO studentDAO;
    private GradesDAO gradesDAO;
    private ClassDAO classDAO;

    @Override
    public void init() throws ServletException {
        parentDAO = new ParentDAO();
        studentDAO = new StudentDAO();
        gradesDAO = new GradesDAO();
        classDAO = new ClassDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Check if user is logged in and is a parent
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("userId") == null ||
                    !"PARENT".equals(session.getAttribute("userType"))) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            String action = request.getParameter("action");
            if (action == null) {
                action = "dashboard";
            }

            switch (action) {
                case "dashboard":
                    showDashboard(request, response);
                    break;
                case "grades":
                    viewGrades(request, response);
                    break;
                case "profile":
                    viewProfile(request, response);
                    break;
                case "attendance":
                    viewAttendance(request, response);
                    break;
                case "comments":
                    viewComments(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/parent?action=dashboard");
            }
        } catch (Exception e) {
            handleException(request, response, e);
        }
    }

    private void showDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        int parentId = (int) request.getSession().getAttribute("userId");

        try {
            Map<String, Object> dashboardData = parentDAO.getDashboardData(parentId);

            if (dashboardData != null && !dashboardData.isEmpty()) {
                for (Map.Entry<String, Object> entry : dashboardData.entrySet()) {
                    request.setAttribute(entry.getKey(), entry.getValue());
                }

                // Get student and class info
                Student student = (Student) dashboardData.get("student");
                if (student != null && student.getCurrentClassId() != null) {
                    SchoolClass schoolClass = classDAO.getClassById(student.getCurrentClassId());
                    request.setAttribute("schoolClass", schoolClass);

                    Map<String, List<Double>> grades = gradesDAO.getStudentGrades(
                            student.getStudentId(),
                            student.getCurrentClassId()
                    );
                    request.setAttribute("grades", grades);

                    // Get comments
                    List<Map<String, Object>> comments = gradesDAO.getStudentComments(
                            student.getStudentId(),
                            student.getCurrentClassId()
                    );
                    request.setAttribute("comments", comments);
                }

                request.getRequestDispatcher("/WEB-INF/dashboard.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "No student information found for this account.");
                request.getRequestDispatcher("/WEB-INF/dashboard.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            System.err.println("Error loading parent dashboard: " + e.getMessage());
            throw e;
        }
    }

    private void viewGrades(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        int parentId = (int) request.getSession().getAttribute("userId");
        Student student = parentDAO.getChildByParentId(parentId);

        if (student != null && student.getCurrentClassId() != null) {
            Map<String, List<Double>> grades = gradesDAO.getStudentGrades(
                    student.getStudentId(),
                    student.getCurrentClassId()
            );
            request.setAttribute("grades", grades);
            request.setAttribute("student", student);

            SchoolClass schoolClass = classDAO.getClassById(student.getCurrentClassId());
            request.setAttribute("schoolClass", schoolClass);

            Map<Integer, Double> termAverages = calculateTermAverages(grades);
            request.setAttribute("termAverages", termAverages);
        }

        request.getRequestDispatcher("/WEB-INF/grades.jsp").forward(request, response);
    }

    private void viewProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        int parentId = (int) request.getSession().getAttribute("userId");
        Student student = parentDAO.getChildByParentId(parentId);

        if (student != null) {
            request.setAttribute("student", student);
            if (student.getCurrentClassId() != null) {
                SchoolClass schoolClass = classDAO.getClassById(student.getCurrentClassId());
                request.setAttribute("schoolClass", schoolClass);
            }
        }

        request.getRequestDispatcher("/WEB-INF/parent/profile.jsp").forward(request, response);
    }

    private void viewComments(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        int parentId = (int) request.getSession().getAttribute("userId");
        Student student = parentDAO.getChildByParentId(parentId);

        if (student != null && student.getCurrentClassId() != null) {
            List<Map<String, Object>> comments = gradesDAO.getStudentComments(
                    student.getStudentId(),
                    student.getCurrentClassId()
            );
            request.setAttribute("comments", comments);
            request.setAttribute("student", student);
        }

        request.getRequestDispatcher("/WEB-INF/comments.jsp").forward(request, response);
    }

    private void viewAttendance(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/attendance.jsp").forward(request, response);
    }

    private Map<Integer, Double> calculateTermAverages(Map<String, List<Double>> grades) {
        Map<Integer, Double> termAverages = new HashMap<>();
        if (grades == null || grades.isEmpty()) {
            return termAverages;
        }

        for (int term = 0; term < 3; term++) {
            double sum = 0;
            int count = 0;
            for (List<Double> subjectGrades : grades.values()) {
                if (subjectGrades.size() > term && subjectGrades.get(term) != null) {
                    sum += subjectGrades.get(term);
                    count++;
                }
            }
            if (count > 0) {
                termAverages.put(term + 1, sum / count);
            }
        }
        return termAverages;
    }

    private void handleException(HttpServletRequest request, HttpServletResponse response, Exception e)
            throws ServletException, IOException {
        e.printStackTrace();
        request.setAttribute("error", "An error occurred: " + e.getMessage());
        request.getRequestDispatcher("/WEB-INF/dashboard.jsp").forward(request, response);
    }
}