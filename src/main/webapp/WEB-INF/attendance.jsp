<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student Attendance - School Administration System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css" rel="stylesheet">
</head>
<body class="bg-light">
<jsp:include page="../common/navbar.jsp"/>

<div class="container mt-4">
    <div class="card">
        <div class="card-header">
            <h5 class="card-title mb-0">Attendance Record</h5>
        </div>
        <div class="card-body">
            <c:if test="${not empty attendance}">
                <div class="table-responsive">
                    <table class="table">
                        <thead>
                        <tr>
                            <th>Date</th>
                            <th>Status</th>
                            <th>Remarks</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${attendance}" var="record">
                            <tr>
                                <td><fmt:formatDate value="${record.date}" pattern="dd MMM yyyy"/></td>
                                <td>
                                            <span class="badge bg-${record.status eq 'PRESENT' ? 'success' : 'danger'}">
                                                    ${record.status}
                                            </span>
                                </td>
                                <td>${record.remarks}</td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:if>
            <c:if test="${empty attendance}">
                <p class="text-muted text-center">No attendance records available.</p>
            </c:if>
        </div>
    </div>
</div>

<jsp:include page="/common/footer.jsp"/>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
