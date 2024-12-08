<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Subject Management - School Administration System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body class="bg-light">
<jsp:include page="../common/navbar.jsp"/>

<div class="container mt-4">
    <!-- Header -->
    <div class="row mb-4">
        <div class="col">
            <h2><i class="bi bi-book"></i> Subject Management</h2>
        </div>
        <div class="col-auto">
            <a href="${pageContext.request.contextPath}/subject?command=ADD_FORM"
               class="btn btn-primary">
                <i class="bi bi-plus-circle"></i> Add New Subject
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

    <!-- Search Box -->
    <div class="card mb-4">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/subject" method="get" class="row g-3">
                <input type="hidden" name="command" value="SEARCH">
                <div class="col-md-8">
                    <input type="text" name="searchTerm" class="form-control"
                           placeholder="Search subjects..." value="${param.searchTerm}">
                </div>
                <div class="col-auto">
                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-search"></i> Search
                    </button>
                    <a href="${pageContext.request.contextPath}/subject?command=LIST"
                       class="btn btn-secondary">
                        <i class="bi bi-x-circle"></i> Clear
                    </a>
                </div>
            </form>
        </div>
    </div>

    <!-- Subjects Table -->
    <div class="card">
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-hover align-middle">
                    <thead>
                    <tr>
                        <th>Subject Name</th>
                        <th>Description</th>
                        <th>Teachers</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="subject" items="${subjects}">
                        <tr>
                            <td>${subject.subjectName}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${empty subject.description}">
                                        <span class="text-muted">No description available</span>
                                    </c:when>
                                    <c:when test="${fn:length(subject.description) > 100}">
                                        ${fn:substring(subject.description, 0, 100)}...
                                    </c:when>
                                    <c:otherwise>
                                        ${subject.description}
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                        <span class="badge bg-info">
                                            ${subject.teacherCount} Teachers
                                        </span>
                            </td>
                            <td>
                                        <span class="badge bg-${subject.status ? 'success' : 'danger'}">
                                                ${subject.status ? 'Active' : 'Inactive'}
                                        </span>
                            </td>
                            <td>
                                <div class="btn-group">
                                    <a href="${pageContext.request.contextPath}/subject?command=LOAD&subjectId=${subject.subjectId}"
                                       class="btn btn-sm btn-primary" title="Edit">
                                        <i class="bi bi-pencil"></i>
                                    </a>
                                    <button type="button" class="btn btn-sm btn-danger"
                                            onclick="confirmDelete(${subject.subjectId}, '${subject.subjectName}')"
                                            title="Delete">
                                        <i class="bi bi-trash"></i>
                                    </button>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>

                    <c:if test="${empty subjects}">
                        <tr>
                            <td colspan="5" class="text-center py-4">
                                <i class="bi bi-emoji-neutral fs-1 text-muted"></i>
                                <p class="text-muted mb-0">No subjects found</p>
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
                <p>Are you sure you want to delete the subject: <strong><span id="subjectName"></span></strong>?</p>
                <p class="text-danger">
                    <i class="bi bi-exclamation-triangle"></i>
                    This will remove the subject from all teacher qualifications.
                </p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                <form id="deleteForm" method="post">
                    <input type="hidden" name="command" value="DELETE">
                    <input type="hidden" name="subjectId" id="deleteSubjectId">
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
    function confirmDelete(subjectId, subjectName) {
        document.getElementById('subjectName').textContent = subjectName;
        document.getElementById('deleteSubjectId').value = subjectId;
        document.getElementById('deleteForm').action = '${pageContext.request.contextPath}/subject';
        new bootstrap.Modal(document.getElementById('deleteModal')).show();
    }
</script>
</body>
</html>
