<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add New Class - School Administration System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body class="bg-light">
<jsp:include page="../common/navbar.jsp"/>

<div class="container mt-4">
    <div class="row mb-4">
        <div class="col">
            <h2><i class="bi bi-diagram-2"></i> Add New Class</h2>
        </div>
        <div class="col-auto">
            <a href="${pageContext.request.contextPath}/class?command=LIST"
               class="btn btn-secondary">
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

    <div class="card">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/class" method="post"
                  class="needs-validation" novalidate>
                <input type="hidden" name="command" value="ADD">

                <div class="row mb-3">
                    <div class="col-md-6">
                        <label for="className" class="form-label">Class Name *</label>
                        <input type="text" class="form-control" id="className"
                               name="className" required pattern="^[A-Za-z0-9\s-]{3,50}$"
                               value="${param.className}">
                        <div class="invalid-feedback">
                            Please enter a valid class name (3-50 characters).
                        </div>
                        <div class="form-text">
                            Examples: Standard 1A, Grade 2B, Class 3C
                        </div>
                    </div>

                    <div class="col-md-6">
                        <label for="academicYear" class="form-label">Academic Year *</label>
                        <select class="form-select" id="academicYear" name="academicYear" required>
                            <option value="">Select Academic Year</option>
                            <c:set var="currentYear" value="<%= java.time.Year.now().getValue() %>" />
                            <option value="${currentYear}" selected>${currentYear}</option>
                            <option value="${currentYear + 1}">${currentYear + 1}</option>
                        </select>
                        <div class="invalid-feedback">
                            Please select an academic year.
                        </div>
                    </div>
                </div>

                <div class="row mb-3">
                    <div class="col-md-6">
                        <label for="capacity" class="form-label">Class Capacity *</label>
                        <input type="number" class="form-control" id="capacity"
                               name="capacity" min="1" max="50" required
                               value="${param.capacity != null ? param.capacity : '30'}">
                        <div class="invalid-feedback">
                            Please enter a valid capacity (1-50 students).
                        </div>
                        <div class="form-text">
                            Recommended: 25-35 students per class
                        </div>
                    </div>
                </div>
                    </div>
                </div>

                <!-- Submit Buttons -->
                <div class="mt-4">
                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-save"></i> Create Class
                    </button>
                    <button type="reset" class="btn btn-secondary">
                        <i class="bi bi-x-circle"></i> Clear Form
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Form validation
    (function() {
        'use strict';
        var forms = document.querySelectorAll('.needs-validation');
        Array.from(forms).forEach(function(form) {
            form.addEventListener('submit', function(event) {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            }, false);
        });
    })();
</script>
</body>
</html>