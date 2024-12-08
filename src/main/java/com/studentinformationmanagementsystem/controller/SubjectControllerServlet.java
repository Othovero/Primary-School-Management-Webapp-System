package com.studentinformationmanagementsystem.controller;


import com.studentinformationmanagementsystem.dao.SubjectDAO;
import com.studentinformationmanagementsystem.dao.TeacherDAO;
import com.studentinformationmanagementsystem.model.Subject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/subject")
public class SubjectControllerServlet extends HttpServlet {
    private SubjectDAO subjectDAO;
    private TeacherDAO teacherDAO;

    @Override
    public void init() throws ServletException {
        subjectDAO = new SubjectDAO();
        teacherDAO = new TeacherDAO();
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
                    listSubjects(request, response);
                    break;
                case "ADD_FORM":
                    showAddForm(request, response);
                    break;
                case "LOAD":
                    loadSubject(request, response);
                    break;
                case "SEARCH":
                    searchSubjects(request, response);
                    break;
                default:
                    listSubjects(request, response);
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
                    addSubject(request, response);
                    break;
                case "UPDATE":
                    updateSubject(request, response);
                    break;
                case "DELETE":
                    deleteSubject(request, response);
                    break;
                default:
                    listSubjects(request, response);
            }
        } catch (Exception e) {
            handleException(request, response, e);
        }
    }

    private void listSubjects(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        try {
            List<Subject> subjects = subjectDAO.getAllSubjects();

            // Get teacher count for each subject
            for (Subject subject : subjects) {
                int teacherCount = teacherDAO.getTeacherCountBySubject(subject.getSubjectId());
                subject.setTeacherCount(teacherCount);
            }

            request.setAttribute("subjects", subjects);
            request.getRequestDispatcher("/WEB-INF/list-subject.jsp")
                    .forward(request, response);

        } catch (Exception e) {
            System.err.println("Error in listSubjects: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "Error loading subjects: " + e.getMessage());
            request.setAttribute("subjects", new ArrayList<>());
            request.getRequestDispatcher("/WEB-INF/list-subject.jsp")
                    .forward(request, response);
        }
    }

    private void showAddForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/add-subject.jsp").forward(request, response);
    }

    private void loadSubject(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        try {
            int subjectId = Integer.parseInt(request.getParameter("subjectId"));
            Subject subject = subjectDAO.getSubjectById(subjectId);

            if (subject == null) {
                request.getSession().setAttribute("error", "Subject not found");
                response.sendRedirect(request.getContextPath() + "/subject?command=LIST");
                return;
            }

            request.setAttribute("subject", subject);
            request.getRequestDispatcher("/WEB-INF/edit-subject.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("Error in loadSubject: " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("error",
                    "Error loading subject: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/subject?command=LIST");
        }
    }

    private void searchSubjects(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        try {
            String searchTerm = request.getParameter("searchTerm");
            List<Subject> subjects = subjectDAO.searchSubjects(searchTerm);

            // Get teacher count for each subject
            for (Subject subject : subjects) {
                int teacherCount = teacherDAO.getTeacherCountBySubject(subject.getSubjectId());
                subject.setTeacherCount(teacherCount);
            }

            request.setAttribute("subjects", subjects);
            request.setAttribute("searchTerm", searchTerm);
            request.getRequestDispatcher("/WEB-INF/list-subject.jsp").forward(request, response);

        } catch (Exception e) {
            System.err.println("Error in searchSubjects: " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("error",
                    "Error searching subjects: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/subject?command=LIST");
        }
    }

    private void addSubject(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        try {
            String subjectName = request.getParameter("subjectName");
            String description = request.getParameter("description");

            // Validate input
            if (subjectName == null || subjectName.trim().isEmpty()) {
                throw new IllegalArgumentException("Subject name is required");
            }

            // Check if subject already exists
            if (subjectDAO.existsByName(subjectName)) {
                request.getSession().setAttribute("error",
                        "A subject with this name already exists");
                response.sendRedirect(request.getContextPath() +
                        "/subject?command=ADD_FORM");
                return;
            }

            // Create and save subject
            Subject subject = new Subject();
            subject.setSubjectName(subjectName);
            subject.setDescription(description);
            subject.setStatus(true);

            subjectDAO.insertSubject(subject);

            request.getSession().setAttribute("success",
                    "Subject added successfully: " + subjectName);
            response.sendRedirect(request.getContextPath() + "/subject?command=LIST");

        } catch (Exception e) {
            System.err.println("Error in addSubject: " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("error",
                    "Error adding subject: " + e.getMessage());
            response.sendRedirect(request.getContextPath() +
                    "/subject?command=ADD_FORM");
        }
    }

    private void updateSubject(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException, ServletException {
        try {
            int subjectId = Integer.parseInt(request.getParameter("subjectId"));
            String subjectName = request.getParameter("subjectName");
            String description = request.getParameter("description");
            boolean status = Boolean.parseBoolean(request.getParameter("status"));

            // Validate input
            if (subjectName == null || subjectName.trim().isEmpty()) {
                throw new IllegalArgumentException("Subject name is required");
            }

            // Check if subject exists
            Subject subject = subjectDAO.getSubjectById(subjectId);
            if (subject == null) {
                request.getSession().setAttribute("error", "Subject not found");
                response.sendRedirect(request.getContextPath() + "/subject?command=LIST");
                return;
            }

            // Update subject
            subject.setSubjectName(subjectName);
            subject.setDescription(description);
            subject.setStatus(status);

            subjectDAO.updateSubject(subject);

            request.getSession().setAttribute("success",
                    "Subject updated successfully: " + subjectName);
            response.sendRedirect(request.getContextPath() + "/subject?command=LIST");

        } catch (Exception e) {
            System.err.println("Error in updateSubject: " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("error",
                    "Error updating subject: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/subject?command=LIST");
        }
    }

    private void deleteSubject(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        try {
            int subjectId = Integer.parseInt(request.getParameter("subjectId"));

            // Check if subject is assigned to any teachers
            int teacherCount = teacherDAO.getTeacherCountBySubject(subjectId);
            if (teacherCount > 0) {
                request.getSession().setAttribute("error",
                        "Cannot delete subject that is assigned to teachers");
                response.sendRedirect(request.getContextPath() + "/subject?command=LIST");
                return;
            }

            subjectDAO.deleteSubject(subjectId);

            request.getSession().setAttribute("success", "Subject deleted successfully");
            response.sendRedirect(request.getContextPath() + "/subject?command=LIST");

        } catch (Exception e) {
            System.err.println("Error in deleteSubject: " + e.getMessage());
            e.printStackTrace();
            request.getSession().setAttribute("error",
                    "Error deleting subject: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/subject?command=LIST");
        }
    }


    private void handleException(HttpServletRequest request, HttpServletResponse response,
                                 Exception e) throws ServletException, IOException {
        System.err.println("Error in SubjectControllerServlet: " + e.getMessage());
        e.printStackTrace();
        request.setAttribute("error", "An error occurred: " + e.getMessage());
        request.getRequestDispatcher("/WEB-INF/list-subject.jsp")
                .forward(request, response);
    }
}
