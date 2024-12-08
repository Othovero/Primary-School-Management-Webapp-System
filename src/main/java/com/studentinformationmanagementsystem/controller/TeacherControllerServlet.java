package com.studentinformationmanagementsystem.controller;


import com.studentinformationmanagementsystem.dao.TeacherDAO;
import com.studentinformationmanagementsystem.dao.SubjectDAO;
import com.studentinformationmanagementsystem.model.Teacher;
import com.studentinformationmanagementsystem.model.Subject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/teacher")
public class TeacherControllerServlet extends HttpServlet {
    private TeacherDAO teacherDAO;
    private SubjectDAO subjectDAO;
    private SimpleDateFormat dateFormat;

    @Override
    public void init() throws ServletException {
        teacherDAO = new TeacherDAO();
        subjectDAO = new SubjectDAO();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd");
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
                    listTeachers(request, response);
                    break;
                case "ADD_FORM":
                    showAddForm(request, response);
                    break;
                case "LOAD":
                    loadTeacher(request, response);
                    break;
                case "SEARCH":
                    searchTeachers(request, response);
                    break;
                case "VIEW":
                    viewTeacher(request, response);
                    break;
                default:
                    listTeachers(request, response);
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
                    addTeacher(request, response);
                    break;
                case "UPDATE":
                    updateTeacher(request, response);
                    break;
                case "DELETE":
                    deleteTeacher(request, response);
                    break;
                case "ASSIGN_SUBJECTS":
                    assignSubjects(request, response);
                    break;
                case "ASSIGN_CLASS":
                    assignClass(request, response);
                    break;
                default:
                    listTeachers(request, response);
            }
        } catch (Exception e) {
            handleException(request, response, e);
        }
    }

    private void listTeachers(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<Teacher> teachers = teacherDAO.getAllTeachers();
        List<Subject> subjects = subjectDAO.getAllSubjects(); // Add this line
        request.setAttribute("teachers", teachers);
        request.setAttribute("subjects", subjects); // Add this line
        request.getRequestDispatcher("/WEB-INF/list-teacher.jsp")
                .forward(request, response);
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<Subject> subjects = subjectDAO.getAllSubjects();
        request.setAttribute("subjects", subjects);
        request.getRequestDispatcher("/WEB-INF/add-teacher.jsp")
                .forward(request, response);
    }

    private void loadTeacher(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        int teacherId = Integer.parseInt(request.getParameter("teacherId"));
        Teacher teacher = teacherDAO.getTeacherById(teacherId);
        List<Subject> allSubjects = subjectDAO.getAllSubjects();

        request.setAttribute("teacher", teacher);
        request.setAttribute("subjects", allSubjects);
        request.getRequestDispatcher("/WEB-INF/add-teacher.jsp")
                .forward(request, response);
    }

    private void searchTeachers(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String searchTerm = request.getParameter("searchTerm");
        List<Teacher> teachers = teacherDAO.searchTeachers(searchTerm);
        request.setAttribute("teachers", teachers);
        request.setAttribute("searchTerm", searchTerm);
        request.getRequestDispatcher("/WEB-INF/list-teacher.jsp")
                .forward(request, response);
    }

    private void viewTeacher(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        int teacherId = Integer.parseInt(request.getParameter("teacherId"));
        Teacher teacher = teacherDAO.getTeacherById(teacherId);
        request.setAttribute("teacher", teacher);
        request.getRequestDispatcher("/WEB-INF/views/teacher/view-teacher.jsp")
                .forward(request, response);
    }

    private void addTeacher(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ParseException, ServletException {
        try {
            // Get basic teacher information
            Teacher teacher = new Teacher();
            teacher.setOmangPassportNo(request.getParameter("omangPassportNo"));
            teacher.setFirstName(request.getParameter("firstName"));
            teacher.setLastName(request.getParameter("lastName"));
            teacher.setGender(request.getParameter("gender"));
            // Handle date of birth
            String dobString = request.getParameter("dateOfBirth");
            if (dobString != null && !dobString.trim().isEmpty()) {
                try {
                    teacher.setDateOfBirth(Date.valueOf(dobString));
                } catch (IllegalArgumentException e) {
                    throw new ValidationException("Invalid date format for date of birth");
                }
            }
            teacher.setAddress(request.getParameter("address"));
            teacher.setContactNo(request.getParameter("contactNo"));
            teacher.setEmail(request.getParameter("email"));
            teacher.setQualifications(request.getParameter("qualifications"));
            teacher.setJoinDate(new Date(System.currentTimeMillis()));
            teacher.setStatus(true);

            // Get selected subjects
            String[] subjectIds = request.getParameterValues("subjects");
            List<Subject> selectedSubjects = new ArrayList<>();
            if (subjectIds != null) {
                for (String id : subjectIds) {
                    try {
                        Subject subject = subjectDAO.getSubjectById(Integer.parseInt(id));
                        if (subject != null) {
                            selectedSubjects.add(subject);
                            System.out.println("Added subject: " + subject.getSubjectName());
                        }
                    } catch (SQLException | NumberFormatException e) {
                        System.err.println("Error processing subject ID: " + id);
                        e.printStackTrace();
                    }
                }
            }
            teacher.setQualifiedSubjects(selectedSubjects);

            // Validate teacher data
            validateTeacher(teacher);

            // Check if teacher already exists
            if (teacherDAO.exists(teacher.getOmangPassportNo())) {
                request.getSession().setAttribute("error",
                        "A teacher with this Omang/Passport number already exists.");
                // Forward instead of redirect to preserve form data
                request.setAttribute("teacher", teacher);
                request.setAttribute("subjects", subjectDAO.getAllSubjects());
                request.getRequestDispatcher("/WEB-INF/add-teacher.jsp").forward(request, response);
                return;
            }

            // Generate initial password (in production, should be randomly generated and sent to email)
            String initialPassword = "Teacher@123";

            try {
                // Add teacher to database
                teacherDAO.insertTeacher(teacher, initialPassword);
                System.out.println("Teacher inserted successfully with ID: " + teacher.getTeacherId());

                request.getSession().setAttribute("success",
                        "Teacher added successfully: " + teacher.getFullName());
                response.sendRedirect(request.getContextPath() + "/teacher?command=LIST");
                return;

            } catch (SQLException e) {
                System.err.println("Error inserting teacher: " + e.getMessage());
                e.printStackTrace();
                // Forward back to form with error
                request.getSession().setAttribute("error", "Database error: " + e.getMessage());
                request.setAttribute("teacher", teacher);
                request.setAttribute("subjects", subjectDAO.getAllSubjects());
                request.getRequestDispatcher("/WEB-INF/add-teacher.jsp").forward(request, response);
            }

        } catch (ValidationException e) {
            System.err.println("Validation error: " + e.getMessage());
            // Forward back to form with error
            request.getSession().setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/WEB-INF/add-teacher.jsp").forward(request, response);
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("error", "An unexpected error occurred");
            response.sendRedirect(request.getContextPath() + "/teacher?command=ADD_FORM");
        }
    }

    private void updateTeacher(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ParseException {
        int teacherId = Integer.parseInt(request.getParameter("teacherId"));

        try {
            Teacher teacher = teacherDAO.getTeacherById(teacherId);
            if (teacher == null) {
                request.getSession().setAttribute("error", "Teacher not found.");
                response.sendRedirect(request.getContextPath() + "/teacher?command=LIST");
                return;
            }

            // Update teacher information
            teacher.setOmangPassportNo(request.getParameter("omangPassportNo"));
            teacher.setFirstName(request.getParameter("firstName"));
            teacher.setLastName(request.getParameter("lastName"));
            teacher.setGender(request.getParameter("gender"));
            teacher.setDateOfBirth(dateFormat.parse(request.getParameter("dateOfBirth")));
            teacher.setAddress(request.getParameter("address"));
            teacher.setContactNo(request.getParameter("contactNo"));
            teacher.setEmail(request.getParameter("email"));
            teacher.setQualifications(request.getParameter("qualifications"));

            // Update subject qualifications
            String[] subjectIds = request.getParameterValues("subjects");
            if (subjectIds != null) {
                List<Subject> selectedSubjects = Arrays.stream(subjectIds)
                        .map(id -> {
                            try {
                                return subjectDAO.getSubjectById(Integer.parseInt(id));
                            } catch (SQLException e) {
                                return null;
                            }
                        })
                        .filter(subject -> subject != null)
                        .collect(Collectors.toList());
                teacher.setQualifiedSubjects(selectedSubjects);
            } else {
                teacher.setQualifiedSubjects(new ArrayList<>());
            }

            validateTeacher(teacher);
            teacherDAO.updateTeacher(teacher);

            request.getSession().setAttribute("success",
                    "Teacher updated successfully: " + teacher.getFullName());
            response.sendRedirect(request.getContextPath() + "/teacher?command=LIST");

        } catch (ValidationException e) {
            request.getSession().setAttribute("error", e.getMessage());
            response.sendRedirect(request.getContextPath() +
                    "/teacher?command=LOAD&teacherId=" + teacherId);
        }
    }

    private void deleteTeacher(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int teacherId = Integer.parseInt(request.getParameter("teacherId"));
        teacherDAO.deleteTeacher(teacherId);
        request.getSession().setAttribute("success", "Teacher deleted successfully.");
        response.sendRedirect(request.getContextPath() + "/teacher?command=LIST");
    }

    private void assignSubjects(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int teacherId = Integer.parseInt(request.getParameter("teacherId"));
        String[] subjectIds = request.getParameterValues("subjects");

        if (subjectIds != null) {
            List<Integer> subjectIdList = Arrays.stream(subjectIds)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            teacherDAO.assignSubjects(teacherId, subjectIdList);
        }

        request.getSession().setAttribute("success", "Subjects assigned successfully.");
        response.sendRedirect(request.getContextPath() +
                "/teacher?command=VIEW&teacherId=" + teacherId);
    }

    private void assignClass(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int teacherId = Integer.parseInt(request.getParameter("teacherId"));
        int classId = Integer.parseInt(request.getParameter("classId"));
        int subjectId = Integer.parseInt(request.getParameter("subjectId"));
        int academicYear = Integer.parseInt(request.getParameter("academicYear"));

        if (teacherDAO.hasMaximumLoad(teacherId, academicYear)) {
            request.getSession().setAttribute("error",
                    "Teacher has reached maximum teaching load for this academic year.");
        } else {
            teacherDAO.assignClass(teacherId, classId, subjectId, academicYear);
            request.getSession().setAttribute("success", "Class assigned successfully.");
        }

        response.sendRedirect(request.getContextPath() +
                "/teacher?command=VIEW&teacherId=" + teacherId);
    }

    private void validateTeacher(Teacher teacher) throws ValidationException {
        if (teacher.getOmangPassportNo() == null ||
                !teacher.getOmangPassportNo().matches("\\d{9}")) {
            throw new ValidationException(
                    "Invalid Omang/Passport number. Must be 9 digits.");
        }
        if (teacher.getFirstName() == null || teacher.getFirstName().trim().isEmpty()) {
            throw new ValidationException("First name is required.");
        }
        if (teacher.getLastName() == null || teacher.getLastName().trim().isEmpty()) {
            throw new ValidationException("Last name is required.");
        }
        if (teacher.getDateOfBirth() == null) {
            throw new ValidationException("Date of birth is required.");
        }
        if (teacher.getContactNo() == null ||
                !teacher.getContactNo().matches("\\d{10}")) {
            throw new ValidationException(
                    "Invalid contact number. Must be 10 digits.");
        }
        if (!teacher.isValidEmail()) {
            throw new ValidationException("Invalid email format.");
        }
        if (teacher.getQualifications() == null ||
                teacher.getQualifications().trim().isEmpty()) {
            throw new ValidationException("Qualifications are required.");
        }
    }

    private void handleException(HttpServletRequest request,
                                 HttpServletResponse response, Exception e)
            throws ServletException, IOException {
        e.printStackTrace();
        request.getSession().setAttribute("error",
                "An error occurred: " + e.getMessage());
        response.sendRedirect(request.getContextPath() + "/teacher?command=LIST");
    }

    private static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }
}
