<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Class Management - School Administration System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body class="bg-light">
<jsp:include page="../common/navbar.jsp"/>

<div class="container mt-4">
    <!-- Header -->
    <div class="row mb-4">
        <div class="col">
            <h2><i class="bi bi-diagram-3"></i> Class Management</h2>
        </div>
        <div class="col-auto">
            <a href="${pageContext.request.contextPath}/class?command=ADD_FORM"
               class="btn btn-primary">
                <i class="bi bi-plus-circle"></i> Add New Class
            </a>
        </div>
    </div>

    <!-- Alert Messages -->
    <c:if test="${not empty sessionScope.success}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${sessionScope.success}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <c:remove var="success" scope="session" />
    </c:if>
    <c:if test="${not empty sessionScope.error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${sessionScope.error}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
        <c:remove var="error" scope="session" />
    </c:if>

    <!-- Filters -->
    <div class="card mb-4">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/class" method="get" class="row g-3">
                <input type="hidden" name="command" value="LIST">

                <div class="col-md-4">
                    <select name="academicYear" class="form-select">
                        <option value="">Select Academic Year</option>
                        <c:forEach var="year" items="${academicYears}">
                            <option value="${year}" ${year == currentYear ? 'selected' : ''}>
                                    ${year}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="col-auto">
                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-search"></i> Filter
                    </button>
                    <a href="${pageContext.request.contextPath}/class?command=LIST"
                       class="btn btn-secondary">
                        <i class="bi bi-x-circle"></i> Clear
                    </a>
                </div>
            </form>
        </div>
    </div>

    <!-- Classes List -->
    <div class="card">
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead>
                    <tr>
                        <th>Class Name</th>
                        <th>Academic Year</th>
                        <th>Capacity</th>
                        <th>Current Enrollment</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="schoolClass" items="${classes}">
                        <tr>
                            <td>${schoolClass.className}</td>
                            <td>${schoolClass.academicYear}</td>
                            <td>${schoolClass.capacity}</td>
                            <td>
                                <div class="progress" style="height: 20px;">
                                    <div class="progress-bar ${schoolClass.currentEnrollment >= schoolClass.capacity ? 'bg-danger' : 'bg-success'}"
                                         role="progressbar"
                                         style="width: ${(schoolClass.currentEnrollment / schoolClass.capacity) * 100}%">
                                            ${schoolClass.currentEnrollment}/${schoolClass.capacity}
                                    </div>
                                </div>
                            </td>
                            <td>
                                        <span class="badge bg-${schoolClass.status ? 'success' : 'danger'}">
                                                ${schoolClass.status ? 'Active' : 'Inactive'}
                                        </span>
                            </td>
                            <td>
                                <div class="btn-group">
                                    <a href="${pageContext.request.contextPath}/class?command=LOAD&classId=${schoolClass.classId}"
                                       class="btn btn-sm btn-primary" title="Edit">
                                        <i class="bi bi-pencil"></i>
                                    </a>
                                    <button type="button" class="btn btn-sm btn-danger"
                                            onclick="confirmDelete(${schoolClass.classId}, '${schoolClass.className}')"
                                            title="Delete">
                                        <i class="bi bi-trash"></i>
                                    </button>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>

                    <c:if test="${empty classes}">
                        <tr>
                            <td colspan="6" class="text-center py-4">
                                <i class="bi bi-emoji-neutral fs-1 text-muted"></i>
                                <p class="text-muted mb-0">No classes found</p>
                            </td>
                        </tr>
                    </c:if>
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
                <p>Are you sure you want to delete the class: <strong><span id="className"></span></strong>?</p>
                <p class="text-danger">
                    <i class="bi bi-exclamation-triangle"></i>
                    This will remove all student enrollments in this class.
                </p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <form id="deleteForm" method="post">
                    <input type="hidden" name="command" value="DELETE">
                    <input type="hidden" name="classId" id="deleteClassId">
                    <button type="submit" class="btn btn-danger">
                        <i class="bi bi-trash"></i> Delete
                    </button>
                </form>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function confirmDelete(classId, className) {
        document.getElementById('className').textContent = className;
        document.getElementById('deleteClassId').value = classId;
        document.getElementById('deleteForm').action = '${pageContext.request.contextPath}/class';
        new bootstrap.Modal(document.getElementById('deleteModal')).show();
    }
</script>
</body>
</html>