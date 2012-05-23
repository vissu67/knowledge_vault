<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" href="css/style.css" type="text/css" />
  <title>Active Sessions</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <c:url value="ActiveSessions" var="urlReload">
      </c:url>
      <table align="center">
        <tr>
          <td><h1>Active sessions</h1></td>
          <td> &nbsp; <a href="${urlReload}"><img src="img/action/reload.png" alt="Reload" title="Reload"/></a></td>
        </tr>
      </table>
      <table class="results" width="70%">
        <tr><th>Token</th><th>User</th><th>Creation</th><th>Last access</th><th>Action</th></tr>
        <c:forEach var="se" items="${sessions}" varStatus="row">
          <c:url value="ActiveSessions" var="urlLogout">
            <c:param name="action" value="logout"/>
            <c:param name="token" value="${se.key}"/>
          </c:url>
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <td>${se.key}</td><td>${se.value.session.userID}</td>
            <td><fmt:formatDate value="${se.value.creation.time}" type="both"/></td>
            <td><fmt:formatDate value="${se.value.lastAccess.time}" type="both"/></td>
            <td align="center">
              <c:if test="${se.value.session.userID != 'system'}">
                <a href="${urlLogout}"><img src="img/action/logout.png" alt="Logout" title="Logout"/></a>
              </c:if>
            </td>
          </tr>
        </c:forEach>
      </table>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>