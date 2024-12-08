<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Grade Management - School Administration System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body class="bg-light">
<jsp:include page="../common/navbar.jsp"/>

<div class="container mt-4">
    <!-- Header -->
    <div class="row mb-4">
        <div class="col">
            <h2><i class="bi bi-mortarboard"></i> Grade Management</h2>
        </div>
    </div>

    <!-- Quick Actions -->
    <div class="row mb-4">
        <div class="col-md-4">
            <div class="card h-100">
                <div class="card-body text-center">
                    <i class="bi bi-plus-circle fs-1 text-primary mb-3"></i>
                    <h5>Enter Grades</h5>
                    <p class="text-muted">Add new grades for a class</p>
                    <a href="${pageContext.request.contextPath}/grades?command=ADD_FORM"
                       class="btn btn-primary">
                        Add Grades
                    </a>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <div class="card h-100">
                <div class="card-body text-center">
                    <i class="bi bi-trophy fs-1 text-warning mb-3"></i>
                    <h5>Top Performers</h5>
                    <p class="text-muted">View top performing students</p>
                    <a href="${pageContext.request.contextPath}/grades?command=TOP_PERFORMERS"
                       class="btn btn-warning">
                        View Top Performers
                    </a>
                </div>
            </div>
        </div>
    </div>

    <!-- Class Selection -->
    <div class="card mb-4">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/grades" method="get" class="row g-3">
                <input type="hidden" name="command" value="LIST">

                <div class="col-md-4">
                    <select name="classId" class="form-select" required>
                        <option value="">Select Class to View Grades...</option>
                        <c:forEach var="schoolClass" items="${classes}">
                            <option value="${schoolClass.classId}" ${param.classId == schoolClass.classId ? 'selected' : ''}>
                                    ${schoolClass.className}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="col-auto">
                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-search"></i> View Class Grades
                    </button>
                </div>
            </form>
        </div>
    </div>

    <!-- Class Grades Table (shown when class is selected) -->
    <c:if test="${not empty students}">
        <div class="card">
            <div class="card-header">
                <h5 class="card-title mb-0">
                        ${selectedClass.className} - Grade Overview
                </h5>
            </div>
            <div class="card-body">
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead>
                        <tr>
                            <th>Student Name</th>
                            <th>Birth Certificate No</th>
                            <th>Overall Average</th>
                            <th>Actions</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="student" items="${students}">
                            <tr>
                                <td>${student.fullName}</td>
                                <td>${student.birthCertificateNo}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${student.currentGrade > 0}">
<span class="badge bg-${student.currentGrade >= 75 ? 'success' :
                     student.currentGrade >= 50 ? 'warning' :
                                                                    'danger'}">
<fmt:formatNumber value="${student.currentGrade}" maxFractionDigits="1"/>%
</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-muted">No grades yet</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/grades?command=VIEW_STUDENT&studentId=${student.studentId}"
                                       class="btn btn-sm btn-info">
                                        <i class="bi bi-eye"></i> View Details
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </c:if>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>