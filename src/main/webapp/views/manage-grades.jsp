<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Manage Grades - School Administration System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body class="bg-light">
<jsp:include page="../common/navbar.jsp"/>

<div class="container mt-4">
    <div class="row mb-4">
        <div class="col">
            <h2><i class="bi bi-card-checklist"></i> Manage Grades</h2>
        </div>
    </div>

    <!-- Class and Subject Selection -->
    <div class="card mb-4">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/grades" method="get" class="row g-3">
                <input type="hidden" name="command" value="VIEW">

                <div class="col-md-4">
                    <label class="form-label">Class</label>
                    <select name="classId" class="form-select" required>
                        <option value="">Select Class...</option>
                        <c:forEach var="class" items="${teacherClasses}">
                            <option value="${class.classId}"
                                ${param.classId == class.classId ? 'selected' : ''}>
                                    ${class.className}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="col-md-4">
                    <label class="form-label">Subject</label>
                    <select name="subjectId" class="form-select" required>
                        <option value="">Select Subject...</option>
                        <c:forEach var="subject" items="${teacherSubjects}">
                            <option value="${subject.subjectId}"
                                ${param.subjectId == subject.subjectId ? 'selected' : ''}>
                                    ${subject.subjectName}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="col-md-2">
                    <label class="form-label">Term</label>
                    <select name="term" class="form-select" required>
                        <option value="1" ${param.term == '1' ? 'selected' : ''}>Term 1</option>
                        <option value="2" ${param.term == '2' ? 'selected' : ''}>Term 2</option>
                        <option value="3" ${param.term == '3' ? 'selected' : ''}>Term 3</option>
                    </select>
                </div>

                <div class="col-md-2">
                    <label class="form-label">&nbsp;</label>
                    <button type="submit" class="btn btn-primary w-100">
                        <i class="bi bi-search"></i> View Class
                    </button>
                </div>
            </form>
        </div>
    </div>

    <!-- Grade Entry Form -->
    <c:if test="${not empty students}">
        <form action="${pageContext.request.contextPath}/grades" method="post">
            <input type="hidden" name="command" value="SAVE">
            <input type="hidden" name="classId" value="${param.classId}">
            <input type="hidden" name="subjectId" value="${param.subjectId}">
            <input type="hidden" name="term" value="${param.term}">

            <div class="card">
                <div class="card-body">
                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                            <tr>
                                <th>Student Name</th>
                                <th>Birth Certificate No</th>
                                <th>Current Grade</th>
                                <th>New Grade</th>
                                <th>Comments</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="student" items="${students}" varStatus="status">
                                <tr>
                                    <td>${student.fullName}</td>
                                    <td>${student.birthCertificateNo}</td>
                                    <td>
                                        <c:if test="${not empty student.currentGrade}">
                                                    <span class="badge bg-${student.currentGrade >= 75 ? 'success' :
                                                                     student.currentGrade >= 50 ? 'warning' :
                                                                     'danger'}">
                                                        ${student.currentGrade}%
                                                    </span>
                                        </c:if>
                                    </td>
                                    <td style="width: 150px;">
                                        <input type="hidden" name="studentIds"
                                               value="${student.studentId}">
                                        <input type="number" class="form-control"
                                               name="grades"
                                               value="${student.currentGrade}"
                                               min="0" max="100" step="0.1">
                                    </td>
                                    <td>
                                        <input type="text" class="form-control"
                                               name="comments"
                                               value="${student.comments}"
                                               placeholder="Optional comments">
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <div class="mt-3">
                        <button type="submit" class="btn btn-primary">
                            <i class="bi bi-save"></i> Save Grades
                        </button>
                    </div>
                </div>
            </div>
        </form>
    </c:if>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
