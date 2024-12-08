<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<jsp:useBean id="now" class="java.util.Date"/>
<fmt:formatDate var="currentYear" value="${now}" pattern="yyyy"/>

<footer class="footer mt-auto py-3 bg-light border-top">
  <div class="container">
    <div class="row">
      <div class="col-md-6 text-center text-md-start">
        <span class="text-muted">© ${currentYear} School Administration System</span>
      </div>
      <div class="col-md-6 text-center text-md-end">
        <span class="text-muted">Cse22-006</span>
      </div>
    </div>
  </div>
</footer>