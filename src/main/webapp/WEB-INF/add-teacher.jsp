<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Add New Teacher - School Administration System</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body class="bg-light">
<jsp:include page="../common/navbar.jsp"/>

<div class="container mt-4">
  <!-- Header -->
  <div class="row mb-4">
    <div class="col">
      <h2><i class="bi bi-person-plus"></i> Add New Teacher</h2>
    </div>
    <div class="col-auto">
      <a href="${pageContext.request.contextPath}/teacher?command=LIST"
         class="btn btn-secondary">
        <i class="bi bi-arrow-left"></i> Back to List
      </a>
    </div>
  </div>

  <!-- Alert Messages -->
  <c:if test="${not empty error}">
    <div class="alert alert-danger alert-dismissible fade show" role="alert">
        ${error}
      <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
  </c:if>

  <div class="card">
    <div class="card-body">
      <form action="${pageContext.request.contextPath}/teacher" method="post"
            class="needs-validation" novalidate>
        <input type="hidden" name="command" value="ADD">

        <!-- Personal Information -->
        <h5 class="mb-3">Personal Information</h5>
        <div class="row mb-3">
          <div class="col-md-4">
            <label for="omangPassportNo" class="form-label">Omang/Passport No *</label>
            <input type="text" class="form-control" id="omangPassportNo"
                   name="omangPassportNo" pattern="\d{9}" required>
            <div class="invalid-feedback">
              Please enter a valid 9-digit Omang/Passport number.
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

        <div class="row mb-3">
          <div class="col-md-4">
            <label for="gender" class="form-label">Gender *</label>
            <select class="form-select" id="gender" name="gender" required>
              <option value="">Select gender...</option>
              <option value="MALE">Male</option>
              <option value="FEMALE">Female</option>
            </select>
            <div class="invalid-feedback">
              Please select gender.
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

          <div class="col-md-4">
            <label for="email" class="form-label">Email *</label>
            <input type="email" class="form-control" id="email"
                   name="email" required>
            <div class="invalid-feedback">
              Please enter a valid email address.
            </div>
          </div>
        </div>

        <div class="row mb-3">
          <div class="col-md-6">
            <label for="contactNo" class="form-label">Contact Number *</label>
            <input type="tel" class="form-control" id="contactNo"
                   name="contactNo" pattern="^\d{10}$" required>
            <div class="invalid-feedback">
              Please enter a valid 10-digit contact number.
            </div>
          </div>

          <div class="col-md-6">
            <label for="address" class="form-label">Address *</label>
            <textarea class="form-control" id="address"
                      name="address" rows="1" required></textarea>
            <div class="invalid-feedback">
              Address is required.
            </div>
          </div>
        </div>

        <!-- Professional Information -->
        <h5 class="mb-3">Professional Information</h5>
        <div class="mb-3">
          <label for="qualifications" class="form-label">Qualifications *</label>
          <textarea class="form-control" id="qualifications"
                    name="qualifications" rows="3" required></textarea>
          <div class="invalid-feedback">
            Please enter qualifications.
          </div>
        </div>

        <div class="mb-4">
          <label class="form-label">Qualified to Teach *</label>
          <div class="row g-3">
            <c:forEach var="subject" items="${subjects}">
              <div class="col-md-3">
                <div class="form-check">
                  <input class="form-check-input" type="checkbox"
                         name="subjects" value="${subject.subjectId}"
                         id="subject${subject.subjectId}">
                  <label class="form-check-label" for="subject${subject.subjectId}">
                      ${subject.subjectName}
                  </label>
                </div>
              </div>
            </c:forEach>
          </div>
          <div class="form-text">
            Select at least one subject.
          </div>
        </div>

        <!-- Submit Buttons -->
        <div class="mt-4">
          <button type="submit" class="btn btn-primary">
            <i class="bi bi-save"></i> Save Teacher
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
        // Check if at least one subject is selected
        var subjects = form.querySelectorAll('input[name="subjects"]:checked');
        if (subjects.length === 0) {
          event.preventDefault();
          event.stopPropagation();
          alert('Please select at least one subject to teach.');
          return;
        }

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