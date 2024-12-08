<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Class Performance - School Administration System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        .top-performer {
            border-left: 4px solid #198754;
            transition: transform 0.2s;
        }
        .top-performer:hover {
            transform: translateX(10px);
        }
        .rank-1 {
            border-left-color: gold;
        }
        .rank-2 {
            border-left-color: silver;
        }
        .rank-3 {
            border-left-color: #cd7f32;
        }
    </style>
</head>
<body class="bg-light">
<jsp:include page="../common/navbar.jsp"/>

<div class="container mt-4">
    <!-- Header -->
    <div class="row mb-4">
        <div class="col">
            <h2><i class="bi bi-trophy"></i> Class Performance</h2>
        </div>
        <div class="col-auto">
            <select id="academicYear" class="form-select" onchange="this.form.submit()">
                <c:forEach var="year" items="${academicYears}">
                    <option value="${year}" ${year == selectedYear ? 'selected' : ''}>
                            ${year} Academic Year
                    </option>
                </c:forEach>
            </select>
        </div>
    </div>

    <!-- Alert Messages -->
    <jsp:include page="../common/messages.jsp"/>

    <!-- Class Selection -->
    <div class="card mb-4">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/performance" method="get" class="row g-3">
                <input type="hidden" name="command" value="VIEW">
                <input type="hidden" name="academicYear" value="${selectedYear}">

                <div class="col-md-4">
                    <label class="form-label">Select Class</label>
                    <select name="classId" class="form-select" required onchange="this.form.submit()">
                        <option value="">Choose a class...</option>
                        <c:forEach var="class" items="${schoolClasses}">
                            <option value="${class.classId}" ${class.classId == selectedClassId ? 'selected' : ''}>
                                    ${class.className}
                            </option>
                        </c:forEach>
                    </select>
                </div>
            </form>
        </div>
    </div>

    <c:if test="${not empty selectedClassId}">
        <!-- Class Overview -->
        <div class="row mb-4">
            <div class="col-md-4">
                <div class="card bg-primary text-white">
                    <div class="card-body">
                        <h5>Class Average</h5>
                        <h2>${classAverage}%</h2>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card bg-success text-white">
                    <div class="card-body">
                        <h5>Highest Score</h5>
                        <h2>${highestScore}%</h2>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card bg-info text-white">
                    <div class="card-body">
                        <h5>Total Students</h5>
                        <h2>${totalStudents}</h2>
                    </div>
                </div>
            </div>
        </div>

        <!-- Top 5 Students -->
        <div class="card">
            <div class="card-header">
                <h5 class="card-title mb-0"><i class="bi bi-star"></i> Top 5 Performing Students</h5>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-8">
                        <c:forEach var="student" items="${topPerformers}" varStatus="status">
                            <div class="card mb-3 top-performer rank-${status.index + 1}">
                                <div class="card-body">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <div>
                                            <h5 class="mb-1">${status.index + 1}. ${student.fullName}</h5>
                                            <p class="mb-0 text-muted">
                                                Birth Certificate: ${student.birthCertificateNo}
                                            </p>
                                        </div>
                                        <div class="text-end">
                                            <h4 class="mb-0 text-success">${student.averageGrade}%</h4>
                                            <small class="text-muted">Average Grade</small>
                                        </div>
                                    </div>

                                    <!-- Subject Breakdown -->
                                    <div class="mt-3">
                                        <p class="mb-2">Subject Performance:</p>
                                        <c:forEach var="grade" items="${student.subjectGrades}">
                                            <div class="d-flex justify-content-between align-items-center mb-2">
                                                <span>${grade.subjectName}</span>
                                                <span class="badge bg-${grade.score >= 75 ? 'success' :
                                                                            grade.score >= 50 ? 'warning' : 'danger'}">
                                                        ${grade.score}%
                                                    </span>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>

                        <c:if test="${empty topPerformers}">
                            <div class="text-center py-5">
                                <i class="bi bi-clipboard-data fs-1 text-muted"></i>
                                <p class="text-muted mt-2">No performance data available for this class yet.</p>
                            </div>
                        </c:if>
                    </div>

                    <!-- Performance Distribution -->
                    <div class="col-md-4">
                        <div class="card">
                            <div class="card-header">
                                <h6 class="card-title mb-0">Grade Distribution</h6>
                            </div>
                            <div class="card-body">
                                <div class="mb-3">
                                    <label class="d-flex justify-content-between">
                                        <span>Distinction (75-100%)</span>
                                        <span>${gradeDistribution.distinction}%</span>
                                    </label>
                                    <div class="progress">
                                        <div class="progress-bar bg-success"
                                             style="width: ${gradeDistribution.distinction}%"></div>
                                    </div>
                                </div>
                                <div class="mb-3">
                                    <label class="d-flex justify-content-between">
                                        <span>Merit (60-74%)</span>
                                        <span>${gradeDistribution.merit}%</span>
                                    </label>
                                    <div class="progress">
                                        <div class="progress-bar bg-primary"
                                             style="width: ${gradeDistribution.merit}%"></div>
                                    </div>
                                </div>
                                <div class="mb-3">
                                    <label class="d-flex justify-content-between">
                                        <span>Pass (50-59%)</span>
                                        <span>${gradeDistribution.pass}%</span>
                                    </label>
                                    <div class="progress">
                                        <div class="progress-bar bg-warning"
                                             style="width: ${gradeDistribution.pass}%"></div>
                                    </div>
                                </div>
                                <div>
                                    <label class="d-flex justify-content-between">
                                        <span>Fail (0-49%)</span>
                                        <span>${gradeDistribution.fail}%</span>
                                    </label>
                                    <div class="progress">
                                        <div class="progress-bar bg-danger"
                                             style="width: ${gradeDistribution.fail}%"></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </c:if>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
