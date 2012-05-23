<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ page import="com.openkm.util.WarUtils"%>
<%@ page import="com.openkm.api.OKMRepository"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" href="css/style.css" type="text/css" />
  <title>Main</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <h1>OpenKM Administration</h1>
      <table width="215px" class="form" style="margin-top: 25px">
        <tr><td><b>OpenKM - Knowledge Management</b></td></tr>
        <tr><td>Version: <%=WarUtils.getAppVersion() %></td></tr>
        <tr><td>&nbsp;</td></tr>
        <tr><td>&copy; 2006-2011  OpenKM</td></tr>
        <tr><td>&nbsp;</td></tr>
        <tr><td><b>Support</b></td></tr>
        <tr><td><a href="mailto:support@openkm.com">support@openkm.com</a></td></tr>
        <tr><td>&nbsp;</td></tr>
        <tr><td><b>Installation ID</b></td></tr>
        <tr><td><%=OKMRepository.getInstance().getRepositoryUuid(null)%></td></tr>
      </table>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>
