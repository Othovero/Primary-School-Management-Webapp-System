<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Student Details - School Administration System</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css" rel="stylesheet">
  <style>
    .info-card {
      border-left: 4px solid #0d6efd;
    }
    .grades-card {
      border-left: 4px solid #198754;
    }
    .enrollment-card {
      border-left: 4px solid #dc3545;
    }
  </style>
</head>
<body class="bg-light">
<jsp:include page="../common/navbar.jsp"/>

<div class="container mt-4">
  <!-- Header -->
  <div class="row mb-4 align-items-center">
    <div class="col">
      <h2>
        <i class="bi bi-person-badge"></i>
        Student Details: ${student.firstName} ${student.lastName}
      </h2>
      <p class="text-muted mb-0">
        Birth Certificate: ${student.birthCertificateNo}
      </p>
    </div>
    <div class="col-auto">
      <div class="btn-group">
        <a href="${pageContext.request.contextPath}/student?command=LOAD&studentId=${student.studentId}"
           class="btn btn-primary">
          <i class="bi bi-pencil"></i> Edit
        </a>
        <a href="${pageContext.request.contextPath}/student?command=LIST"
           class="btn btn-secondary">
          <i class="bi bi-arrow-left"></i> Back
        </a>
      </div>
    </div>
  </div>

  <!-- Alert Messages -->
  <jsp:include page="../common/messages.jsp"/>

  <div class="row">
    <!-- Personal Information -->
    <div class="col-md-4 mb-4">
      <div class="card info-card h-100">
        <div class="card-header">
          <h5 class="card-title mb-0">Personal Information</h5>
        </div>
        <div class="card-body">
          <div class="mb-3">
            <h6 class="text-muted mb-1">Full Name</h6>
            <p class="mb-0">${student.firstName} ${student.lastName}</p>
          </div>
          <div class="mb-3">
            <h6 class="text-muted mb-1">Gender</h6>
            <p class="mb-0">${student.gender}</p>
          </div>
          <div class="mb-3">
            <h6 class="text-muted mb-1">Date of Birth</h6>
            <p class="mb-0">
              <fmt:formatDate value="${student.dateOfBirth}" pattern="dd MMMM yyyy"/>
            </p>
          </div>
          <div class="mb-3">
            <h6 class="text-muted mb-1">Registration Date</h6>
            <p class="mb-0">
              <fmt:formatDate value="${student.registrationDate}" pattern="dd MMMM yyyy"/>
            </p>
          </div>
          <div class="mb-3">
            <h6 class="text-muted mb-1">Status</h6>
            <span class="badge bg-${student.status == 'ACTIVE' ? 'success' :
                                                  student.status == 'INACTIVE' ? 'danger' :
                                                  'warning'}">
              ${student.status}
            </span>
          </div>
          <div>
            <h6 class="text-muted mb-1">Address</h6>
            <p class="mb-0">${student.address}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Guardian Information -->
    <div class="col-md-4 mb-4">
      <div class="card h-100">
        <div class="card-header">
          <h5 class="card-title mb-0">Guardian Information</h5>
        </div>
        <div class="card-body">
          <div class="mb-3">
            <h6 class="text-muted mb-1">Guardian Name</h6>
            <p class="mb-0">${student.guardianName}</p>
          </div>
          <div class="mb-3">
            <h6 class="text-muted mb-1">Contact Number</h6>
            <p class="mb-0">
              <i class="bi bi-telephone"></i> ${student.guardianContact}
            </p>
          </div>
          <div>
            <h6 class="text-muted mb-1">Email</h6>
            <p class="mb-0">
              <i class="bi bi-envelope"></i> ${student.guardianEmail}
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- Current Enrollment -->
    <div class="col-md-4 mb-4">
      <div class="card enrollment-card h-100">
        <div class="card-header d-flex justify-content-between align-items-center">
          <h5 class="card-title mb-0">Current Enrollment</h5>
          <c:if test="${empty student.currentClassId && student.status == 'ACTIVE'}">
            <button class="btn btn-sm btn-primary"
                    onclick="showEnrollmentModal()">
              <i class="bi bi-plus-circle"></i> Enroll
            </button>
          </c:if>
        </div>
        <div class="card-body">
          <c:choose>
            <c:when test="${not empty student.currentClassId}">
              <div class="mb-3">
                <h6 class="text-muted mb-1">Current Class</h6>
                <p class="mb-0">${student.currentClass}</p>
              </div>
              <div class="mb-3">
                <h6 class="text-muted mb-1">Academic Year</h6>
                <p class="mb-0">${currentAcademicYear}</p>
              </div>
              <div>
                <h6 class="text-muted mb-1">Enrollment Date</h6>
                <p class="mb-0">
                  <fmt:formatDate value="${student.enrollmentDate}"
                                  pattern="dd MMMM yyyy"/>
                </p>
              </div>
            </c:when>
            <c:otherwise>
              <div class="text-center py-4">
                <i class="bi bi-mortarboard fs-1 text-muted"></i>
                <p class="text-muted mt-2">Not currently enrolled in any class</p>
              </div>
            </c:otherwise>
          </c:choose>
        </div>
      </div>
    </div>
  </div>

  <!-- Academic Performance -->
  <div class="card grades-card mb-4">
    <div class="card-header">
      <h5 class="card-title mb-0">Academic Performance</h5>
    </div>
    <div class="card-body">
      <div class="table-responsive">
        <table class="table table-hover">
          <thead>
          <tr>
            <th>Subject</th>
            <th>Grade</th>
            <th>Teacher</th>
            <th>Last Updated</th>
            <th>Comments</th>
          </tr>
          </thead>
          <tbody>
          <c:forEach var="grade" items="${studentGrades}">
            <tr>
              <td>${grade.subjectName}</td>
              <td>
                                        <span class="badge bg-${grade.score >= 75 ? 'success' :
                                                               grade.score >= 50 ? 'warning' :
                                                               'danger'}">
                                            ${grade.score}%
                                        </span>
              </td>
              <td>${grade.teacherName}</td>
              <td>
                <fmt:formatDate value="${grade.lastUpdated}"
                                pattern="dd MMM yyyy"/>
              </td>
              <td>${grade.comments}</td>
            </tr>
          </c:forEach>
          <c:if test="${empty studentGrades}">
            <tr>
              <td colspan="5" class="text-center py-4">
                <i class="bi bi-clipboard-data fs-1 text-muted"></i>
                <p class="text-muted mb-0">No grades available</p>
              </td>
            </tr>
          </c:if>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</div>

<!-- Enrollment Modal -->
<div class="modal fade" id="enrollmentModal" tabindex="-1">
  <div class="modal-dialog">
    <div class="modal-content">
      <form action="${pageContext.request.contextPath}/student" method="post">
        <input type="hidden" name="command" value="ENROLL">
        <input type="hidden" name="studentId" value="${student.studentId}">

        <div class="modal-header">
          <h5 class="modal-title">Enroll Student in Class</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
        </div>
        <div class="modal-body">
          <div class="mb-3">
            <label class="form-label">Select Class</label>
            <select name="classId" class="form-select" required>
              <option value="">Choose a class...</option>
              <c:forEach var="class" items="${availableClasses}">
                <option value="${class.classId}">
                    ${class.className} (${class.currentEnrollment}/${class.capacity})
                </option>
              </c:forEach>
            </select>
          </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
          <button type="submit" class="btn btn-primary">
            <i class="bi bi-check-circle"></i> Enroll
          </button>
        </div>
      </form>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
  function showEnrollmentModal() {
    new bootstrap.Modal(document.getElementById('enrollmentModal')).show();
  }
</script>
</body>
</html>
