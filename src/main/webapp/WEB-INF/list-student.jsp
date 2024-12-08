<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Management - School Administration System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body class="bg-light">
<jsp:include page="../common/navbar.jsp"/>

<div class="container mt-4">
    <!-- Header -->
    <div class="row mb-4">
        <div class="col">
            <h2><i class="bi bi-mortarboard"></i> Student Management</h2>
        </div>
        <div class="col-auto">
            <a href="${pageContext.request.contextPath}/student?command=ADD_FORM"
               class="btn btn-primary">
                <i class="bi bi-plus-circle"></i> Add New Student
            </a>
        </div>
    </div>

    <!-- Alert Messages -->
    <jsp:include page="../common/messages.jsp"/>

    <!-- Advanced Search Form -->
    <div class="card mb-4">
        <div class="card-header">
            <h5 class="card-title mb-0">Search Students</h5>
        </div>
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/student" method="get" class="row g-3">
                <input type="hidden" name="command" value="SEARCH">

                <!-- Basic Search -->
                <div class="col-md-4">
                    <label class="form-label">Search Term</label>
                    <input type="text" class="form-control" name="searchTerm"
                           placeholder="Name, Birth Certificate No, Guardian..."
                           value="${param.searchTerm}">
                </div>
                <!-- Class Filter -->
                <div class="col-md-3">
                    <label class="form-label">Class</label>
                    <select name="classId" class="form-select">
                        <option value="">All Classes</option>
                        <c:forEach var="schoolClass" items="${schoolClasses}">
                            <option value="${schoolClass.classId}" ${param.classId == schoolClass.classId ? 'selected' : ''}>
                                    ${schoolClass.className}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <!-- Status Filter -->
                <div class="col-md-3">
                    <label class="form-label">Status</label>
                    <select name="status" class="form-select">
                        <option value="">All Status</option>
                        <option value="ACTIVE" ${param.status == 'ACTIVE' ? 'selected' : ''}>Active</option>
                        <option value="INACTIVE" ${param.status == 'INACTIVE' ? 'selected' : ''}>Inactive</option>
                        <option value="TRANSFERRED" ${param.status == 'TRANSFERRED' ? 'selected' : ''}>Transferred</option>
                        <option value="GRADUATED" ${param.status == 'GRADUATED' ? 'selected' : ''}>Graduated</option>
                    </select>
                </div>

                <!-- Search Buttons -->
                <div class="col-md-2">
                    <label class="form-label">&nbsp;</label>
                    <div class="d-grid gap-2">
                        <button type="submit" class="btn btn-primary">
                            <i class="bi bi-search"></i> Search
                        </button>
                    </div>
                </div>

                <!-- Clear Button -->
                <div class="col-12">
                    <a href="${pageContext.request.contextPath}/student?command=LIST"
                       class="btn btn-secondary">
                        <i class="bi bi-x-circle"></i> Clear Filters
                    </a>
                </div>
            </form>
        </div>
    </div>

    <!-- Students Table -->
    <div class="card">
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-striped table-hover align-middle">
                    <thead class="table-light">
                    <tr>
                        <th>Birth Cert No</th>
                        <th>Name</th>
                        <th>Gender</th>
                        <th>Class</th>
                        <th>Guardian Details</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="student" items="${students}">
                        <tr>
                            <td>${student.birthCertificateNo}</td>
                            <td>${student.firstName} ${student.lastName}</td>
                            <td>${student.gender}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty student.className}">
                                        ${student.className}
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-warning">Not Assigned</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <strong>${student.guardianName}</strong><br>
                                <small class="text-muted">
                                    <i class="bi bi-telephone"></i> ${student.guardianContact}<br>
                                    <i class="bi bi-envelope"></i> ${student.guardianEmail}
                                </small>
                            </td>
                            <td>
                                        <span class="badge bg-${student.status eq 'ACTIVE' ? 'success' :
                                                              student.status eq 'INACTIVE' ? 'danger' :
                                                              student.status eq 'TRANSFERRED' ? 'warning' : 'info'}">
                                                ${student.status}
                                        </span>
                            </td>
                            <td>
                                <div class="btn-group">
                                    <!-- View Details -->
                                    <a href="${pageContext.request.contextPath}/student?command=VIEW&studentId=${student.studentId}"
                                       class="btn btn-sm btn-info" title="View Details">
                                        <i class="bi bi-eye"></i>
                                    </a>

                                    <!-- Edit -->
                                    <a href="${pageContext.request.contextPath}/student?command=LOAD&studentId=${student.studentId}"
                                       class="btn btn-sm btn-primary" title="Edit">
                                        <i class="bi bi-pencil"></i>
                                    </a>

                                    <!-- Enroll in Class (if not assigned) -->
                                    <c:if test="${empty student.currentClassId && student.status eq 'ACTIVE'}">
                                        <a href="${pageContext.request.contextPath}/student?command=ENROLL_FORM&studentId=${student.studentId}"
                                           class="btn btn-sm btn-success" title="Enroll in Class">
                                            <i class="bi bi-person-plus"></i>
                                        </a>
                                    </c:if>

                                    <!-- Delete -->
                                    <button type="button" class="btn btn-sm btn-danger"
                                            onclick="confirmDelete(${student.studentId}, '${student.firstName} ${student.lastName}')"
                                            title="Delete">
                                        <i class="bi bi-trash"></i>
                                    </button>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>

                    <c:if test="${empty students}">
                        <tr>
                            <td colspan="7" class="text-center py-4">
                                <i class="bi bi-inbox fs-1 text-muted"></i>
                                <p class="text-muted mb-0">No students found</p>
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
                <p>Are you sure you want to delete the student: <strong><span id="studentName"></span></strong>?</p>
                <p class="text-danger">
                    <i class="bi bi-exclamation-triangle"></i>
                    This action cannot be undone.
                </p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <form id="deleteForm" method="post">
                    <input type="hidden" name="command" value="DELETE">
                    <input type="hidden" name="studentId" id="deleteStudentId">
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
    function confirmDelete(studentId, studentName) {
        document.getElementById('studentName').textContent = studentName;
        document.getElementById('deleteStudentId').value = studentId;
        document.getElementById('deleteForm').action = '${pageContext.request.contextPath}/student';
        new bootstrap.Modal(document.getElementById('deleteModal')).show();
    }
</script>
</body>
</html>