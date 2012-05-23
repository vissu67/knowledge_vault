<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ page import="com.openkm.util.WarUtils" %>
<%@ page import="com.openkm.api.OKMRepository" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" href="css/style.css" type="text/css" />
  <title>Statistics</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <h1>Statistics</h1>
      <%-- <h2>Repository</h2> --%>
      <table align="center">
        <tr>
          <td><img src="StatsGraph?t=0"/></td>
          <td><img src="StatsGraph?t=1"/></td>
          <td><img src="StatsGraph?t=2"/></td>
        </tr>
      </table>
      
      <%-- <h2>System</h2> --%>
      <table align="center">
        <tr>
          <td><img src="StatsGraph?t=3"/></td>
          <td><img src="StatsGraph?t=4"/></td>
        </tr>
      </table>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>