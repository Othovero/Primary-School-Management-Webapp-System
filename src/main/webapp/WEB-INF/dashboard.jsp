<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Parent Dashboard - School Administration System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body class="bg-light">
<jsp:include page="../common/navbar.jsp"/>

<div class="container mt-4">
    <!-- Student Information Card -->
    <div class="row">
        <div class="col-md-4 mb-4">
            <div class="card">
                <div class="card-header">
                    <h5 class="card-title mb-0">Student Information</h5>
                </div>
                <div class="card-body">
                    <c:if test="${not empty student}">
                        <h6>${student.firstName} ${student.lastName}</h6>
                        <p class="mb-1"><strong>Birth Certificate:</strong> ${student.birthCertificateNo}</p>
                        <p class="mb-1"><strong>Class:</strong> ${className}</p>
                        <p class="mb-1"><strong>Academic Year:</strong> ${academicYear}</p>
                        <p class="mb-0">
                            <strong>Status:</strong>
                            <span class="badge bg-success">${student.status}</span>
                        </p>
                    </c:if>
                </div>
            </div>
        </div>

        <!-- Grades Summary Card -->
        <div class="col-md-8 mb-4">
            <div class="card">
                <div class="card-header">
                    <h5 class="card-title mb-0">Academic Performance</h5>
                </div>
                <div class="card-body">
                    <c:if test="${not empty grades}">
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
                                <c:forEach items="${grades}" var="entry">
                                    <tr>
                                        <td>${entry.key}</td>
                                        <c:forEach items="${entry.value}" var="grade">
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
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:if>
                    <c:if test="${empty grades}">
                        <p class="text-muted text-center">No grades available yet.</p>
                    </c:if>
                </div>
            </div>
        </div>
    </div>

    <!-- Teacher Comments -->
    <div class="card">
        <div class="card-header">
            <h5 class="card-title mb-0">Teacher Comments</h5>
        </div>
        <div class="card-body">
            <c:if test="${not empty comments}">
                <div class="list-group">
                    <c:forEach items="${comments}" var="comment">
                        <div class="list-group-item">
                            <div class="d-flex justify-content-between">
                                <h6 class="mb-1">${comment.subjectName}</h6>
                                <small class="text-muted">
                                    <fmt:formatDate value="${comment.date}" pattern="dd MMM yyyy"/>
                                </small>
                            </div>
                            <p class="mb-1">${comment.comment}</p>
                            <small class="text-muted">Term ${comment.term}</small>
                        </div>
                    </c:forEach>
                </div>
            </c:if>
            <c:if test="${empty comments}">
                <p class="text-muted text-center">No teacher comments available.</p>
            </c:if>
        </div>
    </div>
</div>

<jsp:include page="../common/footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>