<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<nav class="navbar navbar-expand-lg navbar-dark bg-primary">
    <div class="container">
        <a class="navbar-brand" href="main-dashboard.jsp">
            <i class="bi bi-book"></i> School Admin
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto">
                <li class="nav-item">
                    <a class="nav-link" href="main-dashboard.jsp">
                        <i class="bi bi-speedometer2"></i> Dashboard
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="student?command=LIST">
                        <i class="bi bi-mortarboard"></i> Students
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="teacher?command=LIST">
                        <i class="bi bi-person-workspace"></i> Teachers
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="class?command=LIST">
                        <i class="bi bi-diagram-3"></i> Classes
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/subject?command=LIST">
                        <i class="bi bi-journal-text"></i> Subjects
                    </a>
                </li>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" role="button"
                       data-bs-toggle="dropdown">
                        <i class="bi bi-gear"></i> Administration
                    </a>
                    <ul class="dropdown-menu">
                        <li>
                            <a class="dropdown-item" href="user?command=LIST">
                                <i class="bi bi-people"></i> User Management
                            </a>
                        </li>
                        <li>
                            <a class="dropdown-item" href="settings">
                                <i class="bi bi-sliders"></i> System Settings
                            </a>
                        </li>
                    </ul>
                </li>
            </ul>

            <ul class="navbar-nav">
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" role="button"
                       data-bs-toggle="dropdown">
                        <i class="bi bi-person-circle"></i> ${sessionScope.username}
                    </a>
                    <ul class="dropdown-menu dropdown-menu-end">
                        <li>
                            <a class="dropdown-item" href="profile">
                                <i class="bi bi-person"></i> Profile
                            </a>
                        </li>
                        <li>
                            <a class="dropdown-item" href="changePassword">
                                <i class="bi bi-key"></i> Change Password
                            </a>
                        </li>
                        <li><hr class="dropdown-divider"></li>
                        <!-- Replace the existing logout form with this link -->
                        <li>
                            <a class="dropdown-item" href="${pageContext.request.contextPath}/login?command=LOGOUT">
                                <i class="bi bi-box-arrow-right"></i> Logout
                            </a>
                        </li>
                    </ul>
                </li>
            </ul>
        </div>
    </div>
</nav>