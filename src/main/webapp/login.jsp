<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - School Administration System</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        .login-container {
            max-width: 400px;
            margin: 100px auto;
            padding: 20px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
            border-radius: 8px;
        }
        .school-logo {
            width: 100px;
            height: 100px;
            margin: 0 auto 20px;
            display: block;
        }
    </style>
</head>
<body class="bg-light">
<div class="container">
    <div class="login-container bg-white">
        <img src="${pageContext.request.contextPath}/resources/images/school-logo.png"
             alt="School Logo" class="school-logo">
        <h2 class="text-center mb-4">School Management System</h2>

        <c:if test="${not empty sessionScope.success}">
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                <i class="bi bi-check-circle-fill"></i> ${sessionScope.success}
                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
            </div>
            <c:remove var="success" scope="session" />
        </c:if>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">
                <i class="bi bi-exclamation-triangle-fill"></i> ${error}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post">
            <div class="mb-3">
                <label for="username" class="form-label">Username</label>
                <div class="input-group">
                        <span class="input-group-text">
                            <i class="bi bi-person"></i>
                        </span>
                    <input type="text" class="form-control" id="username"
                           name="username" required autofocus>
                </div>
            </div>

            <div class="mb-3">
                <label for="password" class="form-label">Password</label>
                <div class="input-group">
                        <span class="input-group-text">
                            <i class="bi bi-key"></i>
                        </span>
                    <input type="password" class="form-control" id="password"
                           name="password" required>
                </div>
            </div>

            <div class="mb-3 form-check">
                <input type="checkbox" class="form-check-input" id="remember"
                       name="remember">
                <label class="form-check-label" for="remember">Remember me</label>
            </div>

            <button type="submit" class="btn btn-primary w-100">
                <i class="bi bi-box-arrow-in-right"></i> Login
            </button>

            <div class="mt-3 text-center">
                <a href="${pageContext.request.contextPath}/forgot-password"
                   class="text-decoration-none">
                    <i class="bi bi-question-circle"></i> Forgot Password?
                </a>
            </div>
            <div class="mt-3 text-center">
                <a href="${pageContext.request.contextPath}/register" class="text-decoration-none">
                    <i class="bi bi-person-plus"></i> New Parent? Register here
                </a>
            </div>
        </form>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>