<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:if test="${not empty sessionScope.success}">
  <div class="alert alert-success alert-dismissible fade show">
    <i class="bi bi-check-circle-fill me-2"></i>
      ${sessionScope.success}
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
  </div>
  <c:remove var="success" scope="session" />
</c:if>

<c:if test="${not empty sessionScope.error}">
  <div class="alert alert-danger alert-dismissible fade show">
    <i class="bi bi-exclamation-triangle-fill me-2"></i>
      ${sessionScope.error}
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
  </div>
  <c:remove var="error" scope="session" />
</c:if>

<c:if test="${not empty sessionScope.warning}">
  <div class="alert alert-warning alert-dismissible fade show">
    <i class="bi bi-exclamation-circle-fill me-2"></i>
      ${sessionScope.warning}
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
  </div>
  <c:remove var="warning" scope="session" />
</c:if>

<c:if test="${not empty sessionScope.info}">
  <div class="alert alert-info alert-dismissible fade show">
    <i class="bi bi-info-circle-fill me-2"></i>
      ${sessionScope.info}
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
  </div>
  <c:remove var="info" scope="session" />
</c:if>
