<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Teacher Management - School Administration System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body class="bg-light">
<jsp:include page="../common/navbar.jsp"/>

<div class="container mt-4">
    <div class="row mb-4">
        <div class="col">
            <h2><i class="bi bi-person-workspace"></i> Teacher Management</h2>
        </div>
        <div class="col-auto">
            <a href="${pageContext.request.contextPath}/teacher?command=ADD_FORM"
               class="btn btn-primary">
                <i class="bi bi-plus-circle"></i> Add New Teacher
            </a>
        </div>
    </div>

    <!-- Search Form -->
    <div class="card mb-4">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/teacher" method="get" class="row g-3">
                <input type="hidden" name="command" value="SEARCH">

                <div class="col-md-4">
                    <input type="text" name="searchTerm" class="form-control"
                           placeholder="Search by name, ID, or qualifications..."
                           value="${param.searchTerm}">
                </div>

                <div class="col-md-3">
                    <select name="subject" class="form-select">
                        <option value="">All Subjects</option>
                        <c:forEach var="subject" items="${subjects}">
                            <option value="${subject.subjectId}"
                                ${param.subject == subject.subjectId ? 'selected' : ''}>
                                    ${subject.subjectName}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="col-auto">
                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-search"></i> Search
                    </button>
                    <a href="${pageContext.request.contextPath}/teacher?command=LIST"
                       class="btn btn-secondary">
                        <i class="bi bi-x-circle"></i> Clear
                    </a>
                </div>
            </form>
        </div>
    </div>

    <!-- Teachers Table -->
    <div class="card">
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Name</th>
                        <th>Contact Info</th>
                        <th>Qualifications</th>
                        <th>Subjects</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="teacher" items="${teachers}">
                        <tr>
                            <td>${teacher.omangPassportNo}</td>
                            <td>${teacher.firstName} ${teacher.lastName}</td>
                            <td>
                                <div>${teacher.contactNo}</div>
                                <small class="text-muted">${teacher.email}</small>
                            </td>
                            <td>${teacher.qualifications}</td>
                            <td>
                                <c:forEach var="subject" items="${teacher.qualifiedSubjects}"
                                           varStatus="status">
                                            <span class="badge bg-info">
                                                    ${subject.subjectName}
                                            </span>
                                    ${!status.last ? ' ' : ''}
                                </c:forEach>
                            </td>
                            <td>
                                        <span class="badge bg-${teacher.status ? 'success' : 'danger'}">
                                                ${teacher.status ? 'Active' : 'Inactive'}
                                        </span>
                            </td>
                            <td>
                                <div class="btn-group">
                                    <a href="${pageContext.request.contextPath}/teacher?command=LOAD&teacherId=${teacher.teacherId}"
                                       class="btn btn-sm btn-primary" title="Edit">
                                        <i class="bi bi-pencil"></i>
                                    </a>
                                    <button type="button" class="btn btn-sm btn-danger"
                                            onclick="confirmDelete(${teacher.teacherId}, '${teacher.firstName} ${teacher.lastName}')"
                                            title="Delete">
                                        <i class="bi bi-trash"></i>
                                    </button>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<!-- Delete Confirmation Modal -->
<div class="modal fade" id="deleteModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title">Confirm Delete</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <div class="modal-body">
                <p>Are you sure you want to delete the teacher: <span id="teacherName"></span>?</p>
                <p class="text-danger">This will also remove all class assignments.</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <form id="deleteForm" method="post">
                    <input type="hidden" name="command" value="DELETE">
                    <input type="hidden" name="teacherId" id="deleteTeacherId">
                    <button type="submit" class="btn btn-danger">Delete</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function confirmDelete(teacherId, teacherName) {
        document.getElementById('teacherName').textContent = teacherName;
        document.getElementById('deleteTeacherId').value = teacherId;
        document.getElementById('deleteForm').action = '${pageContext.request.contextPath}/teacher';
        new bootstrap.Modal(document.getElementById('deleteModal')).show();
    }
</script>
</body>
</html>
