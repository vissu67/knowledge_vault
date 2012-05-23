<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/style.css" />
  <title>Document filters</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <h1>Document filters</h1>
      <table class="results" width="70%">
        <tr>
          <th>Type</th><th>Value</th><th>Active</th>
          <th width="75px">
            <c:url value="DocumentFilter" var="urlCreate">
              <c:param name="action" value="create"/>
            </c:url>
            <a href="${urlCreate}"><img src="img/action/new.png" alt="New filter" title="New filter"/></a>
          </th>
        </tr>
        <c:forEach var="df" items="${documentFilters}" varStatus="row">
          <c:url value="DocumentFilter" var="urlEdit">
            <c:param name="action" value="edit"/>
            <c:param name="df_id" value="${df.id}"/>
          </c:url>
          <c:url value="DocumentFilter" var="urlDelete">
            <c:param name="action" value="delete"/>
            <c:param name="df_id" value="${df.id}"/>
          </c:url>
          <c:url value="DocumentFilter" var="urlRule">
            <c:param name="action" value="ruleList"/>
            <c:param name="df_id" value="${df.id}"/>
          </c:url>
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <td>${df.type}</td><td>${df.value}</td>
            <td align="center">
              <c:choose>
                <c:when test="${df.active}">
                  <img src="img/true.png" alt="Active" title="Active"/>
                </c:when>
                <c:otherwise>
                  <img src="img/false.png" alt="Inactive" title="Inactive"/>
                </c:otherwise>
              </c:choose>
            </td>
            <td>
              <a href="${urlEdit}"><img src="img/action/edit.png" alt="Edit" title="Edit"/></a>
              &nbsp;
              <a href="${urlDelete}"><img src="img/action/delete.png" alt="Delete" title="Delete"/></a>
              &nbsp;
              <a href="${urlRule}"><img src="img/action/rule.png" alt="Rules" title="Rules"/></a>
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