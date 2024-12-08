<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add New Student - School Administration System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body class="bg-light">
<jsp:include page="../common/navbar.jsp"/>

<div class="container mt-4">
    <!-- Header -->
    <div class="row mb-4">
        <div class="col">
            <h2><i class="bi bi-person-plus"></i> Add New Student</h2>
        </div>
        <div class="col-auto">
            <a href="${pageContext.request.contextPath}/student?command=LIST"
               class="btn btn-secondary">
                <i class="bi bi-arrow-left"></i> Back to List
            </a>
        </div>
    </div>

    <!-- Alert Messages -->
    <jsp:include page="../common/messages.jsp"/>

    <!-- Student Registration Form -->
    <div class="card">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/student" method="post"
                  class="needs-validation" novalidate>
                <input type="hidden" name="command" value="ADD">

                <!-- Personal Information -->
                <h5 class="mb-3">Personal Information</h5>
                <div class="row mb-4">
                    <div class="col-md-4">
                        <label for="birthCertificateNo" class="form-label">Birth Certificate No *</label>
                        <input type="text" class="form-control" id="birthCertificateNo"
                               name="birthCertificateNo" pattern="^\d{9}$" required>
                        <div class="invalid-feedback">
                            Please enter a valid 9-digit birth certificate number.
                        </div>
                    </div>

                    <div class="col-md-4">
                        <label for="firstName" class="form-label">First Name *</label>
                        <input type="text" class="form-control" id="firstName"
                               name="firstName" required>
                        <div class="invalid-feedback">
                            First name is required.
                        </div>
                    </div>

                    <div class="col-md-4">
                        <label for="lastName" class="form-label">Last Name *</label>
                        <input type="text" class="form-control" id="lastName"
                               name="lastName" required>
                        <div class="invalid-feedback">
                            Last name is required.
                        </div>
                    </div>
                </div>

                <div class="row mb-4">
                    <div class="col-md-4">
                        <label for="gender" class="form-label">Gender *</label>
                        <select class="form-select" id="gender" name="gender" required>
                            <option value="">Select gender...</option>
                            <option value="MALE">Male</option>
                            <option value="FEMALE">Female</option>
                        </select>
                        <div class="invalid-feedback">
                            Please select a gender.
                        </div>
                    </div>

                    <div class="col-md-4">
                        <label for="dateOfBirth" class="form-label">Date of Birth *</label>
                        <input type="date" class="form-control" id="dateOfBirth"
                               name="dateOfBirth" required>
                        <div class="invalid-feedback">
                            Date of birth is required.
                        </div>
                    </div>
                </div>

                <div class="mb-4">
                    <label for="address" class="form-label">Address *</label>
                    <textarea class="form-control" id="address" name="address"
                              rows="3" required></textarea>
                    <div class="invalid-feedback">
                        Address is required.
                    </div>
                </div>

                <!-- Guardian Information -->
                <h5 class="mb-3">Guardian Information</h5>
                <div class="row mb-4">
                    <div class="col-md-6">
                        <label for="guardianName" class="form-label">Guardian Full Name *</label>
                        <input type="text" class="form-control" id="guardianName"
                               name="guardianName" required>
                        <div class="invalid-feedback">
                            Guardian name is required.
                        </div>
                    </div>

                    <div class="col-md-6">
                        <label for="guardianContact" class="form-label">Guardian Contact No *</label>
                        <input type="tel" class="form-control" id="guardianContact"
                               name="guardianContact" pattern="^\d{10}$" required>
                        <div class="invalid-feedback">
                            Please enter a valid 10-digit contact number.
                        </div>
                    </div>
                </div>

                <div class="mb-4">
                    <label for="guardianEmail" class="form-label">Guardian Email</label>
                    <input type="email" class="form-control" id="guardianEmail"
                           name="guardianEmail">
                    <div class="invalid-feedback">
                        Please enter a valid email address.
                    </div>
                </div>

                <!-- Class Assignment -->
                <div class="mb-4">
                    <label for="classId" class="form-label">Assign to Class</label>
                    <select class="form-select" id="classId" name="currentClassId">
                        <option value="">Select class...</option>
                        <c:forEach var="schoolClass" items="${schoolClasses}">
                            <option value="${schoolClass.classId}">
                                    ${schoolClass.className} (Capacity: ${schoolClass.capacity})
                            </option>
                        </c:forEach>
                    </select>
                    <div class="form-text">
                        You can also assign the class later.
                    </div>
                </div>

                <!-- Submit Buttons -->
                <div class="mt-4">
                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-save"></i> Save Student
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
    (function () {
        'use strict'
        var forms = document.querySelectorAll('.needs-validation')
        Array.prototype.slice.call(forms)
            .forEach(function (form) {
                form.addEventListener('submit', function (event) {
                    if (!form.checkValidity()) {
                        event.preventDefault()
                        event.stopPropagation()
                    }
                    form.classList.add('was-validated')
                }, false)
            })
    })()
</script>
</body>
</html>
