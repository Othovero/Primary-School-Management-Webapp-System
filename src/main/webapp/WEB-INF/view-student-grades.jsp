<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Grades - School Administration System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body class="bg-light">
<jsp:include page="../common/navbar.jsp"/>

<div class="container mt-4">
    <!-- Header -->
    <div class="row mb-4">
        <div class="col">
            <h2><i class="bi bi-person-lines-fill"></i> Student Grades</h2>
            <p class="text-muted">
                Student: ${student.fullName} (${student.birthCertificateNo})
                <br>
                Class: ${selectedClass.className}
            </p>
        </div>
        <div class="col-auto">
            <a href="${pageContext.request.contextPath}/grades?command=LIST" class="btn btn-secondary">
                <i class="bi bi-arrow-left"></i> Back to List
            </a>
        </div>
    </div>

    <!-- Error Messages -->
    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${error}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <!-- Grade Summary Card -->
    <div class="row mb-4">
        <div class="col-md-4">
            <div class="card">
                <div class="card-body">
                    <h5 class="card-title">Overall Performance</h5>
                    <h2 class="display-4 mb-0 text-${student.currentGrade >= 75 ? 'success' :
                                                        student.currentGrade >= 50 ? 'warning' :
                                                        'danger'}">
                        <fmt:formatNumber value="${student.currentGrade}" maxFractionDigits="1"/>%
                    </h2>
                    <p class="text-muted">Average Grade</p>
                </div>
            </div>
        </div>
    </div>

    <!-- Grades Table -->
    <div class="card">
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-hover">
                    <thead>
                    <tr>
                        <th>Subject</th>
                        <th>Term 1</th>
                        <th>Term 2</th>
                        <th>Term 3</th>
                        <th>Average</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${empty gradesMap}">
                            <tr>
                                <td colspan="5" class="text-center">No grades recorded for this student</td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="subjectGrades" items="${gradesMap}">
                                <tr>
                                    <td>${subjectGrades.key}</td>
                                    <c:forEach var="grade" items="${subjectGrades.value}">
                                        <td>
                                            <c:if test="${not empty grade}">
                                                        <span class="badge bg-${grade >= 75 ? 'success' :
                                                                                grade >= 50 ? 'warning' :
                                                                                'danger'}">
                                                            <fmt:formatNumber value="${grade}" maxFractionDigits="1"/>%
                                                        </span>
                                            </c:if>
                                        </td>
                                    </c:forEach>
                                    <td>
                                        <c:if test="${not empty subjectGrades.value}">
                                            <c:set var="sum" value="0"/>
                                            <c:set var="count" value="0"/>
                                            <c:forEach var="grade" items="${subjectGrades.value}">
                                                <c:if test="${not empty grade}">
                                                    <c:set var="sum" value="${sum + grade}"/>
                                                    <c:set var="count" value="${count + 1}"/>
                                                </c:if>
                                            </c:forEach>
                                            <c:if test="${count > 0}">
                                                <strong class="text-${(sum/count) >= 75 ? 'success' :
                                                                            (sum/count) >= 50 ? 'warning' :
                                                                            'danger'}">
                                                    <fmt:formatNumber value="${sum/count}" maxFractionDigits="1"/>%
                                                </strong>
                                            </c:if>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Comments Section -->
    <div class="card mt-4">
        <div class="card-header">
            <h5 class="card-title mb-0">Teacher Comments</h5>
        </div>
        <div class="card-body">
            <div class="table-responsive">
                <table class="table">
                    <thead>
                    <tr>
                        <th>Subject</th>
                        <th>Term</th>
                        <th>Comment</th>
                        <th>Date</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:choose>
                        <c:when test="${empty comments}">
                            <tr>
                                <td colspan="4" class="text-center">No comments available</td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="comment" items="${comments}">
                                <tr>
                                    <td>${comment.subjectName}</td>
                                    <td>Term ${comment.term}</td>
                                    <td>${comment.comment}</td>
                                    <td>
                                        <fmt:formatDate value="${comment.date}" pattern="dd MMM yyyy"/>
                                    </td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>