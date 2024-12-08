<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add Grades - School Administration System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body class="bg-light">
<jsp:include page="../common/navbar.jsp"/>

<div class="container mt-4">
    <!-- Header -->
    <div class="row mb-4">
        <div class="col">
            <h2><i class="bi bi-journal-plus"></i> Add Grades</h2>
        </div>
        <div class="col-auto">
            <a href="${pageContext.request.contextPath}/grades?command=LIST" class="btn btn-secondary">
                <i class="bi bi-arrow-left"></i> Back to List
            </a>
        </div>
    </div>

    <!-- Alert Messages -->
    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${sessionScope.error}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <c:remove var="error" scope="session" />
    </c:if>

    <!-- Selection Form -->
    <div class="card mb-4">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/grades" method="get" class="row g-3">
                <input type="hidden" name="command" value="SHOW_STUDENTS">

                <div class="col-md-4">
                    <label class="form-label">Class</label>
                    <select name="classId" class="form-select" required>
                        <option value="">Select Class...</option>
                        <c:forEach var="schoolClass" items="${classes}">
                            <option value="${schoolClass.classId}" ${param.classId == schoolClass.classId ? 'selected' : ''}>
                                    ${schoolClass.className}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="col-md-4">
                    <label class="form-label">Subject</label>
                    <select name="subjectId" class="form-select" required>
                        <option value="">Select Subject...</option>
                        <c:forEach var="subject" items="${subjects}">
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
                        <option value="">Select Term...</option>
                        <option value="1" ${param.term == '1' ? 'selected' : ''}>Term 1</option>
                        <option value="2" ${param.term == '2' ? 'selected' : ''}>Term 2</option>
                        <option value="3" ${param.term == '3' ? 'selected' : ''}>Term 3</option>
                    </select>
                </div>

                <div class="col-md-2">
                    <label class="form-label">&nbsp;</label>
                    <button type="submit" class="btn btn-primary w-100">
                        <i class="bi bi-search"></i> Show Students
                    </button>
                </div>
            </form>
        </div>
    </div>

    <!-- Grade Entry Form -->
    <c:if test="${not empty students}">
        <div class="card">
            <div class="card-header">
                <h5 class="card-title mb-0">
                    Enter Grades for ${selectedClass.className} - ${selectedSubject.subjectName} - Term ${param.term}
                </h5>
            </div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/grades" method="post">
                    <input type="hidden" name="command" value="ADD">
                    <input type="hidden" name="classId" value="${param.classId}">
                    <input type="hidden" name="subjectId" value="${param.subjectId}">
                    <input type="hidden" name="term" value="${param.term}">

                    <div class="table-responsive">
                        <table class="table table-hover">
                            <thead>
                            <tr>
                                <th>Student Name</th>
                                <th>Birth Certificate No</th>
                                <th>Grade</th>
                                <th>Comments</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="student" items="${students}" varStatus="status">
                                <tr>
                                    <td>${student.fullName}</td>
                                    <td>${student.birthCertificateNo}</td>
                                    <td style="width: 150px;">
                                        <input type="hidden" name="studentIds" value="${student.studentId}">
                                        <input type="number" class="form-control" name="grades"
                                               min="0" max="100" step="0.1" required>
                                    </td>
                                    <td>
                                        <input type="text" class="form-control" name="comments"
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
                </form>
            </div>
        </div>
    </c:if>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
