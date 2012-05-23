<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" href="css/style.css" type="text/css" />
  <title>Experimental</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isMultipleInstancesAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <h1>Experimental</h1>
      <c:url value="Language" var="urlAddTranslation">
      	<c:param name="action" value="addTranslation"/>
      </c:url>
      <center>
        <a href="LogCat">LogCat</a><br/>
        <a href="CheckEmail">Check email</a><br/>
        <a href="benchmark.jsp">Benchmark</a><br/>
        <a href="populate.jsp">Populate</a><br/>
        <a href="ActiveSessions">Active sessions</a><br/>
        <a href="repository_backup.jsp">Repository backup</a><br/>
        <a href="repository_checker.jsp">Repository checker</a><br/>
        <a href="installation_reset.jsp">Installation reset</a><br/>
        <a href="config.jsp">Configuration</a><br/>
        <a href="HibernateStats">Hibernate stats</a><br/>
        <a href="${urlAddTranslation}">Add translation term</a><br/>
      </center>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>
