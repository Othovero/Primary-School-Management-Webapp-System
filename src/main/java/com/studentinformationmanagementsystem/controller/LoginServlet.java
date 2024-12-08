package com.studentinformationmanagementsystem.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import com.studentinformationmanagementsystem.dao.UserDAO;
import com.studentinformationmanagementsystem.model.User;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login", ""})
public class LoginServlet extends HttpServlet {
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check for logout command
        String command = request.getParameter("command");
        if ("LOGOUT".equals(command)) {
            handleLogout(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            response.sendRedirect(request.getContextPath() + "/main-dashboard.jsp");
            return;
        }

        // Check for remember-me cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            String rememberToken = null;
            String username = null;

            for (Cookie cookie : cookies) {
                if ("rememberToken".equals(cookie.getName())) {
                    rememberToken = cookie.getValue();
                } else if ("username".equals(cookie.getName())) {
                    username = cookie.getValue();
                }
            }

            if (username != null && rememberToken != null) {
                try {
                    if (userDAO.validateRememberToken(username, rememberToken)) {
                        User user = userDAO.getUserByUsername(username);
                        if (user != null) {
                            createUserSession(request, user);
                            userDAO.updateLastLogin(user.getUserId());
                            response.sendRedirect(request.getContextPath() + "/main-dashboard.jsp");
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check for logout command in POST as well
        String command = request.getParameter("command");
        if ("LOGOUT".equals(command)) {
            handleLogout(request, response);
            return;
        }

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String remember = request.getParameter("remember");

        try {
            System.out.println("Login attempt - Username: " + username);

            User user = userDAO.authenticateUser(username, password);

            if (user != null) {
                System.out.println("User authenticated successfully - UserType: " + user.getUserType());
                createUserSession(request, user);

                if ("on".equals(remember)) {
                    String rememberToken = userDAO.generateRememberToken(user.getUserId());
                    Cookie usernameCookie = new Cookie("username", username);
                    Cookie tokenCookie = new Cookie("rememberToken", rememberToken);
                    usernameCookie.setMaxAge(30 * 24 * 60 * 60);
                    tokenCookie.setMaxAge(30 * 24 * 60 * 60);
                    response.addCookie(usernameCookie);
                    response.addCookie(tokenCookie);
                }

                request.getRequestDispatcher("/main-dashboard.jsp").forward(request, response);
            } else {
                System.out.println("Authentication failed");
                request.setAttribute("error", "Invalid username or password");
                request.setAttribute("username", username);
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            e.printStackTrace();
            request.setAttribute("error", "An error occurred during login: " + e.getMessage());
            request.setAttribute("username", username);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                String username = (String) session.getAttribute("username");
                System.out.println("Logging out user: " + username);

                // Clear all session attributes
                session.removeAttribute("userId");
                session.removeAttribute("username");
                session.removeAttribute("userType");
                session.removeAttribute("email");
                session.invalidate();
            }

            // Clear remember-me cookies
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("rememberToken".equals(cookie.getName()) ||
                            "username".equals(cookie.getName())) {
                        cookie.setValue("");
                        cookie.setMaxAge(0);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    }
                }
            }

            // Set success message in new session
            HttpSession newSession = request.getSession();
            newSession.setAttribute("success", "You have been successfully logged out.");

            // Redirect to login page
            response.sendRedirect(request.getContextPath() + "/login.jsp");

        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        }
    }

    private void createUserSession(HttpServletRequest request, User user) {
        HttpSession session = request.getSession();
        session.setAttribute("userId", user.getUserId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("userType", user.getUserType());
        session.setAttribute("email", user.getEmail());
        System.out.println("Session created for user: " + user.getUsername());
    }
}