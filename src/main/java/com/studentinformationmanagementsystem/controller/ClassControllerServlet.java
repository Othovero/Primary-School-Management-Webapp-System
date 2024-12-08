package com.studentinformationmanagementsystem.controller;


import com.studentinformationmanagementsystem.dao.ClassAssignmentDAO;
import com.studentinformationmanagementsystem.dao.ClassDAO;
import com.studentinformationmanagementsystem.dao.SubjectDAO;
import com.studentinformationmanagementsystem.model.ClassAssignment;
import com.studentinformationmanagementsystem.model.SchoolClass;
import com.studentinformationmanagementsystem.model.Subject;
import com.studentinformationmanagementsystem.model.Teacher;
import com.studentinformationmanagementsystem.dao.TeacherDAO;

import com.studentinformationmanagementsystem.util.DatabaseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;


@WebServlet("/class")
public class ClassControllerServlet extends HttpServlet {
    private ClassDAO classDAO;
    private SubjectDAO subjectDAO;
    private TeacherDAO teacherDAO;
    private ClassAssignmentDAO classAssignmentDAO;

    @Override
    public void init() throws ServletException {
        classDAO = new ClassDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String command = request.getParameter("command");
        if (command == null) {
            command = "LIST";
        }

        try {
            switch (command) {
                case "LIST":
                    listClasses(request, response);
                    break;
                case "ADD_FORM":
                    showAddForm(request, response);
                    break;
                case "LOAD":
                    loadClass(request, response);
                    break;
                case "VIEW":
                    viewClass(request, response);
                    break;
                default:
                    listClasses(request, response);
            }
        } catch (Exception e) {
            handleException(request, response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String command = request.getParameter("command");
        if (command == null) {
            command = "LIST";
        }

        try {
            switch (command) {
                case "ADD":
                    addClass(request, response);
                    break;
                case "UPDATE":
                    updateClass(request, response);
                    break;
                case "DELETE":
                    deleteClass(request, response);
                    break;
                default:
                    listClasses(request, response);
            }
        } catch (Exception e) {
            handleException(request, response, e);
        }
    }

    private void listClasses(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        try {
            // Get current academic year and classes
            int academicYear = getCurrentAcademicYear();
            System.out.println("Fetching classes for academic year: " + academicYear);

            List<SchoolClass> schoolClasses = classDAO.getClassesByAcademicYear(academicYear);
            System.out.println("Found " + schoolClasses.size() + " classes");

            // Get enrollment stats for each class
            Map<Integer, Integer> enrollmentStats = classDAO.getClassEnrollmentStats(academicYear);

            // Add enrollment data to each class
            for (SchoolClass schoolClass : schoolClasses) {
                int enrollment = enrollmentStats.getOrDefault(schoolClass.getClassId(), 0);
                schoolClass.setCurrentEnrollment(enrollment);
                System.out.println("Class: " + schoolClass.getClassName() +
                        ", Enrollment: " + enrollment + "/" + schoolClass.getCapacity());
            }

            // Get academic years for dropdown filter
            List<Integer> academicYears = new ArrayList<>();
            academicYears.add(academicYear - 1);
            academicYears.add(academicYear);
            academicYears.add(academicYear + 1);

            // Set attributes for JSP
            request.setAttribute("classes", schoolClasses); // Changed from "classes" to "schoolClasses"
            request.setAttribute("currentYear", academicYear);
            request.setAttribute("academicYears", academicYears);
            request.setAttribute("selectedYear", academicYear);

            // Get messages from session if any
            HttpSession session = request.getSession();
            if (session.getAttribute("success") != null) {
                request.setAttribute("success", session.getAttribute("success"));
                session.removeAttribute("success");
            }
            if (session.getAttribute("error") != null) {
                request.setAttribute("error", session.getAttribute("error"));
                session.removeAttribute("error");
            }

            System.out.println("Forwarding to list-class.jsp");
            request.getRequestDispatcher("/WEB-INF/list-class.jsp").forward(request, response);

        } catch (SQLException e) {
            System.err.println("Database error in listClasses: " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("error",
                    "Error loading classes: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error in listClasses: " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("error",
                    "An unexpected error occurred while loading classes");
            response.sendRedirect(request.getContextPath() + "/error.jsp");
        }
    }

    private int getCurrentAcademicYear() {
        return java.time.Year.now().getValue();
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Get current year for academic year options
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            List<Integer> academicYears = Arrays.asList(currentYear, currentYear + 1);

            request.setAttribute("academicYears", academicYears);
            request.setAttribute("currentYear", currentYear);

            request.getRequestDispatcher("/WEB-INF/add-class.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("Error in showAddForm: " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("error",
                    "Error loading add class form: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/class?command=LIST");
        }
    }

    private void loadClass(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        int classId = Integer.parseInt(request.getParameter("classId"));
        SchoolClass schoolClass = classDAO.getClassById(classId);
        request.setAttribute("class", schoolClass);
        request.getRequestDispatcher("/WEB-INF/views/class/edit-class.jsp").forward(request, response);
    }

    private void viewClass(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        int classId = Integer.parseInt(request.getParameter("classId"));
        SchoolClass schoolClass = classDAO.getClassById(classId);
        int currentEnrollment = classDAO.getCurrentEnrollment(classId);

        request.setAttribute("class", schoolClass);
        request.setAttribute("currentEnrollment", currentEnrollment);
        request.getRequestDispatcher("/WEB-INF/views/class/view-class.jsp").forward(request, response);
    }

    private void addClass(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        try {
            // Get form data
            String className = request.getParameter("className");
            int academicYear = Integer.parseInt(request.getParameter("academicYear"));
            int capacity = Integer.parseInt(request.getParameter("capacity"));

            // Validate data
            List<String> errors = new ArrayList<>();
            if (className == null || !className.matches("^[A-Za-z0-9\\s-]{3,50}$")) {
                errors.add("Invalid class name format");
            }
            if (capacity < 1 || capacity > 50) {
                errors.add("Capacity must be between 1 and 50");
            }
            if (academicYear < Calendar.getInstance().get(Calendar.YEAR)) {
                errors.add("Invalid academic year");
            }

            // Check if class name already exists for the academic year
            if (classDAO.isClassNameExists(className, academicYear)) {
                errors.add("A class with this name already exists for the selected academic year");
            }

            if (!errors.isEmpty()) {
                request.getSession().setAttribute("error",
                        String.join(", ", errors));
                response.sendRedirect(request.getContextPath() +
                        "/class?command=ADD_FORM");
                return;
            }

            // Create and save the class
            SchoolClass schoolClass = new SchoolClass();
            schoolClass.setClassName(className);
            schoolClass.setAcademicYear(academicYear);
            schoolClass.setCapacity(capacity);
            schoolClass.setStatus(true);  // Set as active by default

            classDAO.insertClass(schoolClass);

            request.getSession().setAttribute("success",
                    "Class '" + className + "' created successfully");
            response.sendRedirect(request.getContextPath() + "/class?command=LIST");

        } catch (NumberFormatException e) {
            request.getSession().setAttribute("error",
                    "Invalid number format in form submission");
            response.sendRedirect(request.getContextPath() +
                    "/class?command=ADD_FORM");
        } catch (Exception e) {
            System.err.println("Error adding class: " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("error",
                    "Error creating class: " + e.getMessage());
            response.sendRedirect(request.getContextPath() +
                    "/class?command=ADD_FORM");
        }
    }


    private void updateClass(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int classId = Integer.parseInt(request.getParameter("classId"));
        SchoolClass schoolClass = classDAO.getClassById(classId);

        schoolClass.setClassName(request.getParameter("className"));
        schoolClass.setAcademicYear(Integer.parseInt(request.getParameter("academicYear")));
        schoolClass.setCapacity(Integer.parseInt(request.getParameter("capacity")));
        schoolClass.setStatus(Boolean.parseBoolean(request.getParameter("status")));

        classDAO.updateClass(schoolClass);
        request.getSession().setAttribute("success", "Class updated successfully.");
        response.sendRedirect(request.getContextPath() + "/class?command=LIST");
    }

    private void deleteClass(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int classId = Integer.parseInt(request.getParameter("classId"));

        if (classDAO.canDeleteClass(classId)) {
            classDAO.deleteClass(classId);
            request.getSession().setAttribute("success", "Class deleted successfully.");
        } else {
            request.getSession().setAttribute("error",
                    "Cannot delete class. It has active enrollments or assignments.");
        }

        response.sendRedirect(request.getContextPath() + "/class?command=LIST");
    }

    private void handleException(HttpServletRequest request, HttpServletResponse response, Exception e)
            throws ServletException, IOException {
        e.printStackTrace();
        request.getSession().setAttribute("error", "An error occurred: " + e.getMessage());
        response.sendRedirect(request.getContextPath() + "/class?command=LIST");
    }


}
