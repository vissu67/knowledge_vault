<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" href="css/style.css" type="text/css" />
  <title>Workflow Process Definition Browser</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <c:url value="Workflow" var="urlProcessDefinitionList">
        <c:param name="action" value="processDefinitionList"/>
      </c:url>
      <table>
        <tr>
          <td><h1>Process Definitions</h1></td>
          <td> &nbsp; <a href="${urlProcessDefinitionList}"><img src="img/action/reload.png" alt="Reload" title="Reload"/></a></td>
        </tr>
      </table>
      <table class="results" width="90%">
        <tr><th>Process ID</th><th>Process Name</th><th>Version</th><th width="50px">Actions</th></tr>
        <c:forEach var="pd" items="${processDefinitions}" varStatus="row">
          <c:url value="Workflow" var="urlProcessDefinitionView">
            <c:param name="action" value="processDefinitionView"/>
            <c:param name="pdid" value="${pd.id}"/>
          </c:url>
          <c:url value="Workflow" var="urlProcessDefinitionDelete">
            <c:param name="action" value="processDefinitionDelete"/>
            <c:param name="pdid" value="${pd.id}"/>
          </c:url>
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <td>${pd.id}</td>
            <td>${pd.name}</td>
            <td>${pd.version}</td>
            <td>
              <a href="${urlProcessDefinitionView}"><img src="img/action/examine.png" alt="Examine" title="Examine"/></a>
              &nbsp;
              <a href="${urlProcessDefinitionDelete}" onclick="return confirm('Are you sure you want to delete?')"><img src="img/action/delete.png" alt="Delete" title="Delete"/></a>
            </td>
          </tr>
        </c:forEach>
        <tr class="fuzzy">
          <td colspan="5" align="right">
            <form action="RegisterWorkflow" method="post" enctype="multipart/form-data">
              <table>
                <tr>
                  <td><input class=":required :only_on_blur" type="file" name="definition"/></td>
                  <td><input type="submit" value="Register process definition"/></td>
                </tr>
              </table>
            </form>
          </td>
        </tr>
      </table>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>