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
  <title>Check Email</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isMultipleInstancesAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <h1>Check email</h1>
      <form action="CheckEmail">
        <input type="hidden" name="action" value="send"/>
        <table class="form" width="250px">
          <tr><td>From</td><td><input type="text" name="from" value="${from}"/></td></tr>
          <tr><td>To</td><td><input type="text" name="to" value="${to}"/></td></tr>
          <tr><td>Subject</td><td><input type="text" name="subject" size="30" value="${subject}"/></td></tr>
          <tr><td colspan="2">Content</td></tr>
          <tr><td colspan="2"><textarea name="content" cols="40">${content}</textarea></td></tr>
          <tr><td colspan="2" align="right"><input type="submit" value="Send"/></td></tr>
        </table>
      </form>
      <br/>
      <div class="warn" style="text-align: center;">${error}</div>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>