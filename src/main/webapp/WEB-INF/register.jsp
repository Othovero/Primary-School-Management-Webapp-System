<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Parent Registration - School Administration System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
    <style>
        .registration-container {
            max-width: 800px;
            margin: 50px auto;
            padding: 20px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
            border-radius: 8px;
        }
        .select2-container .select2-selection--single {
            height: 38px !important;
        }
    </style>
</head>
<body class="bg-light">

<div class="container">
    <div class="registration-container bg-white">
        <h2 class="text-center mb-4">
            <i class="bi bi-person-plus"></i> Parent Registration
        </h2>

        <!-- Alert Messages -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <i class="bi bi-exclamation-triangle-fill"></i> ${error}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/register" method="post" class="needs-validation" novalidate>
            <!-- Personal Information Section -->
            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="mb-0">Personal Information</h5>
                </div>
                <div class="card-body">
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="firstName" class="form-label">First Name *</label>
                            <input type="text" class="form-control" id="firstName" name="firstName" required>
                            <div class="invalid-feedback">Please enter your first name.</div>
                        </div>
                        <div class="col-md-6">
                            <label for="lastName" class="form-label">Last Name *</label>
                            <input type="text" class="form-control" id="lastName" name="lastName" required>
                            <div class="invalid-feedback">Please enter your last name.</div>
                        </div>
                    </div>

                    <div class="row mb-3">
                        <div class="col-md-6">
                            <label for="email" class="form-label">Email Address *</label>
                            <input type="email" class="form-control" id="email" name="email" required>
                            <div class="invalid-feedback">Please enter a valid email address.</div>
                        </div>
                        <div class="col-md-6">
                            <label for="phoneNumber" class="form-label">Phone Number *</label>
                            <input type="tel" class="form-control" id="phoneNumber" name="phoneNumber"
                                   pattern="[0-9]{10}" required>
                            <div class="invalid-feedback">Please enter a valid 10-digit phone number.</div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Child Information Section -->
            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="mb-0">Child Information</h5>
                </div>
                <div class="card-body">
                    <div class="mb-3">
                        <label for="childId" class="form-label">Select Your Child *</label>
                        <select class="form-select" id="childId" name="childId" required>
                            <option value="">Choose...</option>
                            <c:forEach var="student" items="${unassignedStudents}">
                                <option value="${student.studentId}">
                                        ${student.birthCertificateNo} - ${student.firstName} ${student.lastName}
                                    <c:if test="${not empty student.className}">
                                        (${student.className})
                                    </c:if>
                                </option>
                            </c:forEach>
                        </select>
                        <div class="form-text">
                            Please select your child from the list. If you don't see your child,
                            please contact the school administration.
                        </div>
                        <div class="invalid-feedback">
                            Please select your child.
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="relationship" class="form-label">Relationship to Child *</label>
                        <select class="form-select" id="relationship" name="relationship" required>
                            <option value="">Choose...</option>
                            <option value="FATHER">Father</option>
                            <option value="MOTHER">Mother</option>
                            <option value="GUARDIAN">Legal Guardian</option>
                        </select>
                        <div class="invalid-feedback">
                            Please select your relationship to the child.
                        </div>
                    </div>
                </div>
            </div>

            <!-- Account Security Section -->
            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="mb-0">Account Security</h5>
                </div>
                <div class="card-body">
                    <div class="mb-3">
                        <label for="password" class="form-label">Password *</label>
                        <input type="password" class="form-control" id="password" name="password"
                               pattern="^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$" required>
                        <div class="form-text">
                            Password must be at least 8 characters long and include letters and numbers.
                        </div>
                        <div class="invalid-feedback">
                            Password must meet the requirements.
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="confirmPassword" class="form-label">Confirm Password *</label>
                        <input type="password" class="form-control" id="confirmPassword"
                               name="confirmPassword" required>
                        <div class="invalid-feedback">Passwords must match.</div>
                    </div>

                    <div class="mb-3 form-check">
                        <input type="checkbox" class="form-check-input" id="terms" name="terms" required>
                        <label class="form-check-label" for="terms">
                            I agree to the terms and conditions *
                        </label>
                        <div class="invalid-feedback">You must agree before submitting.</div>
                    </div>
                </div>
            </div>

            <div class="d-grid gap-2">
                <button type="submit" class="btn btn-primary">
                    <i class="bi bi-person-plus"></i> Complete Registration
                </button>
                <a href="${pageContext.request.contextPath}/login" class="btn btn-link text-center">
                    Already have an account? Login
                </a>
            </div>
        </form>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/jquery@3.6.0/dist/jquery.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
<script>
    $(document).ready(function() {
        // Initialize Select2 for better dropdown experience
        $('#childId').select2({
            placeholder: "Search by name or birth certificate number"
        });

        // Form validation
        const forms = document.querySelectorAll('.needs-validation');
        const password = document.getElementById("password");
        const confirmPassword = document.getElementById("confirmPassword");

        function validatePassword(){
            if(password.value != confirmPassword.value) {
                confirmPassword.setCustomValidity("Passwords Don't Match");
            } else {
                confirmPassword.setCustomValidity('');
            }
        }

        password.onchange = validatePassword;
        confirmPassword.onkeyup = validatePassword;

        Array.from(forms).forEach(form => {
            form.addEventListener('submit', event => {
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            }, false);
        });
    });
</script>
</body>
</html>