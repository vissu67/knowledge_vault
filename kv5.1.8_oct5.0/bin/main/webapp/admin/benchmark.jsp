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
  <title>Benchmark</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isMultipleInstancesAdmin(request)%></c:set>
   <c:choose>
    <c:when test="${isAdmin}">
      <h1>Benchmark</h1>
      <form action="Benchmark" method="get">
        <table class="form">
          <tr>
            <td>Action</td>
            <td>
              <select name="action">
                <option value="okmImport">OpenKM import</option>
                <option value="okmCopy">OpenKM copy</option>
                <option value="okmApiHighGenerate">OpenKM generate (API HIGH)</option>
                <option value="okmApiLowGenerate">OpenKM generate (API LOW)</option>
                <option value="okmRawGenerate">OpenKM generate (RAW)</option>
                <option value="jcrGenerate">Jackrabbit generate</option>
              </select>
            </td>
          </tr>
          <tr><td>Param 1</td><td><input type="text" name="param1" size="32"/></td></tr>
          <tr><td>Param 2</td><td><input type="text" name="param2" size="32"/></td></tr>
          <tr><td>Param 3</td><td><input type="text" name="param3" size="32"/></td></tr>
          <tr><td>Param 4</td><td><input type="text" name="param4" size="32" value="1"/></td></tr>
          <tr><td colspan="2" align="right"><input type="submit" value="Send"/></td></tr>
        </table>
      </form>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>