<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Top Performers - School Administration System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body class="bg-light">
<jsp:include page="../common/navbar.jsp"/>

<div class="container mt-4">
    <!-- Header -->
    <div class="row mb-4">
        <div class="col">
            <h2><i class="bi bi-trophy"></i> Top Performing Students</h2>
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

    <!-- Class Selection -->
    <div class="card mb-4">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/grades" method="get" class="row g-3">
                <input type="hidden" name="command" value="TOP_PERFORMERS">

                <div class="col-md-4">
                    <select name="classId" class="form-select" required>
                        <option value="">Select Class...</option>
                        <c:forEach var="schoolClass" items="${classes}">
                            <option value="${schoolClass.classId}"
                                ${param.classId == schoolClass.classId ? 'selected' : ''}>
                                    ${schoolClass.className}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="col-md-2">
                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-search"></i> View Top Performers
                    </button>
                </div>
            </form>
        </div>
    </div>

    <!-- Top Performers List -->
    <c:if test="${not empty selectedClass}">
        <div class="card">
            <div class="card-header">
                <h5 class="card-title mb-0">Top 5 Students in ${selectedClass.className}</h5>
            </div>
            <div class="card-body">
                <div class="row">
                    <c:forEach var="performer" items="${topPerformers}" varStatus="status">
                        <div class="col-md-4 mb-4">
                            <div class="card h-100 ${status.index == 0 ? 'border-warning' : ''}">
                                <div class="card-body">
                                    <div class="d-flex align-items-center mb-3">
                                        <div class="display-4 me-3 ${status.index == 0 ? 'text-warning' : ''}">
                                            #${status.index + 1}
                                        </div>
                                        <div>
                                            <h5 class="card-title mb-0">${performer.key.fullName}</h5>
                                            <small class="text-muted">${performer.key.birthCertificateNo}</small>
                                        </div>
                                    </div>
                                    <h3 class="text-center mb-3">
                                           <span class="badge bg-${performer.value >= 75 ? 'success' :
                                                                   performer.value >= 50 ? 'warning' :
                                                                   'danger'}">
                                               <fmt:formatNumber value="${performer.value}" maxFractionDigits="1"/>%
                                           </span>
                                    </h3>
                                    <div class="d-grid">
                                        <a href="${pageContext.request.contextPath}/grades?command=VIEW_STUDENT&studentId=${performer.key.studentId}"
                                           class="btn btn-outline-primary">
                                            View Details
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <!-- Subject-wise Top Performers -->
                <h5 class="mt-4 mb-3">Top Performers by Subject</h5>
                <div class="table-responsive">
                    <table class="table table-hover">
                        <thead>
                        <tr>
                            <th>Subject</th>
                            <th>Top Student</th>
                            <th>Grade</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${empty topPerformersBySubject}">
                                <tr>
                                    <td colspan="3" class="text-center">No subject data available</td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="entry" items="${topPerformersBySubject}">
                                    <tr>
                                        <td>${entry.key}</td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/grades?command=VIEW_STUDENT&studentId=${entry.value.key.studentId}"
                                               class="text-decoration-none">
                                                    ${entry.value.key.fullName}
                                            </a>
                                        </td>
                                        <td>
                                                   <span class="badge bg-success">
                                                       <fmt:formatNumber value="${entry.value.value}" maxFractionDigits="1"/>%
                                                   </span>
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
    </c:if>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>