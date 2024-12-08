<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html class="h-100">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - School Administration System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        .card-hover:hover {
            transform: translateY(-5px);
            transition: all 0.3s ease;
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        }
    </style>
</head>
<body class="d-flex flex-column h-100">
<!-- Navbar -->
<jsp:include page="/common/navbar.jsp"/>

<!-- Main Content -->
<main class="flex-shrink-0">
<div class="container mt-4">
<!-- Welcome Section -->
<div class="row mb-4">
    <div class="col">
        <h2>Welcome, ${sessionScope.username}!</h2>
        <p class="text-muted">
            <i class="bi bi-clock"></i>
            Last login: <fmt:formatDate value="${sessionScope.lastLogin}" pattern="dd MMM yyyy HH:mm"/>
        </p>
    </div>
</div>

<!-- Role-based content -->
<c:choose>
    <%-- Admin Dashboard --%>
    <c:when test="${sessionScope.userType eq 'ADMIN'}">
        <!-- Statistics Cards -->
        <div class="row mb-4">
            <div class="col-md-3 mb-3">
                <div class="card bg-primary text-white h-100 card-hover">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <h6 class="card-title">Total Students</h6>
                                <h2 class="display-6 mb-0">${totalStudents}</h2>
                            </div>
                            <i class="bi bi-mortarboard fs-1"></i>
                        </div>
                        <a href="${pageContext.request.contextPath}/student?command=LIST"
                           class="text-white text-decoration-none">
                            View Details <i class="bi bi-arrow-right"></i>
                        </a>
                    </div>
                </div>
            </div>

            <div class="col-md-3 mb-3">
                <div class="card bg-success text-white h-100 card-hover">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <h6 class="card-title">Total Teachers</h6>
                                <h2 class="display-6 mb-0">${totalTeachers}</h2>
                            </div>
                            <i class="bi bi-person-workspace fs-1"></i>
                        </div>
                        <a href="${pageContext.request.contextPath}/teacher?command=LIST"
                           class="text-white text-decoration-none">
                            View Details <i class="bi bi-arrow-right"></i>
                        </a>
                    </div>
                </div>
            </div>

            <div class="col-md-3 mb-3">
                <div class="card bg-info text-white h-100 card-hover">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <h6 class="card-title">Total Classes</h6>
                                <h2 class="display-6 mb-0">${totalClasses}</h2>
                            </div>
                            <i class="bi bi-diagram-3 fs-1"></i>
                        </div>
                        <a href="${pageContext.request.contextPath}/class?command=LIST"
                           class="text-white text-decoration-none">
                            View Details <i class="bi bi-arrow-right"></i>
                        </a>
                    </div>
                </div>
            </div>

            <div class="col-md-3 mb-3">
                <div class="card bg-warning text-white h-100 card-hover">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <h6 class="card-title">Total Subjects</h6>
                                <h2 class="display-6 mb-0">${totalSubjects}</h2>
                            </div>
                            <i class="bi bi-book fs-1"></i>
                        </div>
                        <a href="${pageContext.request.contextPath}/subject?command=LIST"
                           class="text-white text-decoration-none">
                            View Details <i class="bi bi-arrow-right"></i>
                        </a>
                    </div>
                </div>
            </div>
        </div>

        <!-- Quick Actions -->
        <h4 class="mb-3">Quick Actions</h4>
        <div class="row">
            <!-- Student Management Card -->
            <div class="col-md-6 col-lg-3 mb-4">
                <div class="card h-100 card-hover">
                    <div class="card-body text-center">
                        <i class="bi bi-mortarboard fs-1 text-primary mb-3"></i>
                        <h5>Student Management</h5>
                        <div class="d-grid gap-2">
                            <a href="${pageContext.request.contextPath}/student?command=ADD_FORM"
                               class="btn btn-outline-primary">
                                Add Student
                            </a>
                            <a href="${pageContext.request.contextPath}/student?command=LIST"
                               class="btn btn-outline-primary">
                                View Students
                            </a>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Teacher Management Card -->
            <div class="col-md-6 col-lg-3 mb-4">
                <div class="card h-100 card-hover">
                    <div class="card-body text-center">
                        <i class="bi bi-person-workspace fs-1 text-success mb-3"></i>
                        <h5>Teacher Management</h5>
                        <div class="d-grid gap-2">
                            <a href="${pageContext.request.contextPath}/teacher?command=ADD_FORM"
                               class="btn btn-outline-success">
                                Add Teacher
                            </a>
                            <a href="${pageContext.request.contextPath}/teacher?command=LIST"
                               class="btn btn-outline-success">
                                View Teachers
                            </a>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Class Management Card -->
            <div class="col-md-6 col-lg-3 mb-4">
                <div class="card h-100 card-hover">
                    <div class="card-body text-center">
                        <i class="bi bi-diagram-3 fs-1 text-info mb-3"></i>
                        <h5>Class Management</h5>
                        <div class="d-grid gap-2">
                            <a href="${pageContext.request.contextPath}/class?command=ADD_FORM"
                               class="btn btn-outline-info">
                                Add Class
                            </a>
                            <a href="${pageContext.request.contextPath}/class?command=LIST"
                               class="btn btn-outline-info">
                                View Classes
                            </a>
                        </div>
                    </div>
                </div>
            </div>
            <!-- Add these cards after the existing Subject Management Card in the admin section -->

            <!-- Grade Management Card -->
            <div class="col-md-6 col-lg-3 mb-4">
                <div class="card h-100 card-hover">
                    <div class="card-body text-center">
                        <i class="bi bi-card-checklist fs-1 text-danger mb-3"></i>
                        <h5>Grade Management</h5>
                        <div class="d-grid gap-2">
                            <a href="${pageContext.request.contextPath}/grades?command=LIST"
                               class="btn btn-outline-danger">
                                <i class="bi bi-journal-check"></i> View Grades
                            </a>
                            <a href="${pageContext.request.contextPath}/grades?command=ADD_FORM"
                               class="btn btn-outline-danger">
                                <i class="bi bi-plus-circle"></i> Add Grades
                            </a>
                            <a href="${pageContext.request.contextPath}/grades?command=TOP_PERFORMERS"
                               class="btn btn-outline-danger">
                                <i class="bi bi-trophy"></i> Top Performers
                            </a>
                        </div>
                    </div>
                </div>
            </div>


            <!-- Subject Management Card -->
            <div class="col-md-6 col-lg-3 mb-4">
                <div class="card h-100 card-hover">
                    <div class="card-body text-center">
                        <i class="bi bi-journal-text fs-1 text-warning mb-3"></i>
                        <h5>Subject Management</h5>
                        <div class="d-grid gap-2">
                            <a href="${pageContext.request.contextPath}/subject?command=ADD_FORM"
                               class="btn btn-outline-warning">
                                Add Subject
                            </a>
                            <a href="${pageContext.request.contextPath}/subject?command=LIST"
                               class="btn btn-outline-warning">
                                View Subjects
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </c:when>

    <%-- Teacher Dashboard --%>
    <c:when test="${sessionScope.userType eq 'TEACHER'}">
        <div class="row">
            <!-- Teacher's Classes -->
            <div class="col-md-8 mb-4">
                <div class="card">
                    <div class="card-header">
                        <h5 class="card-title mb-0">My Classes</h5>
                    </div>
                    <div class="card-body">
                        <div class="list-group">
                            <c:forEach items="${teacherClasses}" var="schoolClass">
                                <a href="${pageContext.request.contextPath}/grades?command=VIEW&classId=${schoolClass.classId}"
                                   class="list-group-item list-group-item-action">
                                    <div class="d-flex w-100 justify-content-between">
                                        <h6 class="mb-1">${schoolClass.className}</h6>
                                        <small>${schoolClass.currentEnrollment} students</small>
                                    </div>
                                    <p class="mb-1">Academic Year: ${schoolClass.academicYear}</p>
                                    <small class="text-muted">Click to view/manage grades</small>
                                </a>
                            </c:forEach>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Quick Actions -->
            <div class="col-md-4 mb-4">
                <div class="card">
                    <div class="card-header">
                        <h5 class="card-title mb-0">Quick Actions</h5>
                    </div>
                    <div class="card-body">
                        <div class="d-grid gap-3">
                            <a href="${pageContext.request.contextPath}/grades?command=ADD_FORM"
                               class="btn btn-primary">
                                <i class="bi bi-plus-circle"></i> Enter Grades
                            </a>
                            <a href="${pageContext.request.contextPath}/grades?command=TOP_PERFORMERS"
                               class="btn btn-success">
                                <i class="bi bi-trophy"></i> View Top Performers
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </c:when>

    <%-- Parent Dashboard --%>
    <c:when test="${sessionScope.userType eq 'PARENT'}">
        <c:redirect url="${pageContext.request.contextPath}/parent?action=dashboard"/>
    </c:when>
    <%-- Default Case --%>
    <c:otherwise>
        <div class="alert alert-danger" role="alert">
            <i class="bi bi-exclamation-triangle-fill"></i>
            Invalid user type or session expired. Please try logging in again.
        </div>
    </c:otherwise>
</c:choose>

    <!-- Error Messages -->
    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <i class="bi bi-exclamation-triangle-fill"></i> ${error}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <!-- Success Messages -->
    <c:if test="${not empty success}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <i class="bi bi-check-circle-fill"></i> ${success}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
</div>
</main>

<!-- Footer -->
<jsp:include page="/common/footer.jsp"/>

<!-- Bootstrap Bundle with Popper -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>

<!-- Custom Scripts -->
<script>
    // Add any custom JavaScript here
    document.addEventListener('DOMContentLoaded', function() {
        // Enable all tooltips
        var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'))
        var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl)
        });

        // Auto-hide alerts after 5 seconds
        setTimeout(function() {
            var alerts = document.querySelectorAll('.alert');
            alerts.forEach(function(alert) {
                bootstrap.Alert.getOrCreateInstance(alert).close();
            });
        }, 5000);
    });
</script>
</body>
</html>