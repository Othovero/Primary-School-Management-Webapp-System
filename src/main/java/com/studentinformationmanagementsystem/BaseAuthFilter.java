package com.studentinformationmanagementsystem;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class BaseAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);

        String requestURI = httpRequest.getRequestURI();
        String command = httpRequest.getParameter("command");

        // Define public resources
        boolean isPublicResource = requestURI.endsWith("login.jsp") ||
                requestURI.endsWith("/login") ||
                requestURI.endsWith("/register") ||
                requestURI.endsWith("register.jsp") ||
                requestURI.contains("/resources/") ||
                requestURI.endsWith(".css") ||
                requestURI.endsWith(".js");

        // Special handling for logout
        boolean isLogoutRequest = requestURI.endsWith("/login") && "LOGOUT".equals(command);

        // Check if user is logged in
        boolean isLoggedIn = (session != null && session.getAttribute("userId") != null);

        if (isPublicResource || isLoggedIn || isLogoutRequest) {
            // For logout requests, clear browser cache to prevent back button issues
            if (isLogoutRequest) {
                httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
                httpResponse.setHeader("Pragma", "no-cache"); // HTTP 1.0
                httpResponse.setHeader("Expires", "0"); // Proxies
            }

            chain.doFilter(request, response);
        } else {
            // Store the requested URL
            httpRequest.getSession().setAttribute("redirectURL", requestURI);

            // Add security headers
            httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            httpResponse.setHeader("Pragma", "no-cache");
            httpResponse.setHeader("Expires", "0");

            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code if needed
    }

    @Override
    public void destroy() {
        // Cleanup code if needed
    }
}