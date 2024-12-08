package com.studentinformationmanagementsystem.controller;

import com.studentinformationmanagementsystem.dao.StudentDAO;
import com.studentinformationmanagementsystem.dao.UserDAO;
import com.studentinformationmanagementsystem.model.Student;
import com.studentinformationmanagementsystem.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/register")
public class RegistrationServlet extends HttpServlet {
    private UserDAO userDAO;
    private StudentDAO studentDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        studentDAO = new StudentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Get list of unassigned students for the dropdown
            List<Student> unassignedStudents = studentDAO.getUnassignedStudents();
            request.setAttribute("unassignedStudents", unassignedStudents);

            request.getRequestDispatcher("/WEB-INF/register.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Error loading registration form: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/register.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            // Get form data
            String firstName = request.getParameter("firstName");
            String lastName = request.getParameter("lastName");
            String email = request.getParameter("email");
            String phoneNumber = request.getParameter("phoneNumber");
            String password = request.getParameter("password");
            int childId = Integer.parseInt(request.getParameter("childId"));
            String relationship = request.getParameter("relationship");

            // Validate input
            if (!password.equals(request.getParameter("confirmPassword"))) {
                throw new ValidationException("Passwords do not match");
            }

            // Check if email is already registered
            if (userDAO.isEmailTaken(email)) {
                throw new ValidationException("Email is already registered");
            }

            // Create parent user
            User user = new User();
            user.setUsername(email); // Use email as username
            user.setEmail(email);
            user.setUserType("PARENT");
            user.setStatus(true);

            // Start transaction
            userDAO.beginTransaction();
            try {
                // Create user account
                userDAO.createUser(user, password);

                // Assign child to parent
                studentDAO.assignParentToStudent(childId, user.getUserId());

                // Commit transaction
                userDAO.commitTransaction();

                // Set success message and redirect to login
                request.getSession().setAttribute("success",
                        "Registration successful! Please login with your email and password.");
                response.sendRedirect(request.getContextPath() + "/login");

            } catch (Exception e) {
                userDAO.rollbackTransaction();
                throw e;
            }

        } catch (ValidationException e) {
            request.setAttribute("error", e.getMessage());
            doGet(request, response); // Reload form with error
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Registration failed: " + e.getMessage());
            doGet(request, response); // Reload form with error
        }
    }

    private static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }
}