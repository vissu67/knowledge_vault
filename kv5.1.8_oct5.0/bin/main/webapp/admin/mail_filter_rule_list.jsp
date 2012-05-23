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
  <title>Mail filter rules</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <c:url value="MailAccount" var="urlFilterList">
        <c:param name="action" value="filterList"/>
        <c:param name="ma_user" value="${ma.user}"/>
        <c:param name="ma_id" value="${ma_id}"/>
      </c:url>
      <h1>Mail filters rules <span style="font-size: 10px;">(<a href="${urlFilterList}">Mail filters</a>)</span></h1>
      <table class="results" width="70%">
        <tr>
          <th>Field</th><th>Operation</th><th>Value</th><th>Active</th>
          <th width="50px">
            <c:url value="MailAccount" var="urlCreate">
              <c:param name="action" value="ruleCreate"/>
              <c:param name="ma_user" value="${ma_user}"/>
              <c:param name="ma_id" value="${ma_id}"/>
              <c:param name="mf_id" value="${mf_id}"/>
            </c:url>
            <a href="${urlCreate}"><img src="img/action/new.png" alt="New rule" title="New rule"/></a>
          </th>
        </tr>
        <c:forEach var="mfr" items="${filterRules}" varStatus="row">
          <c:url value="MailAccount" var="urlEdit">
            <c:param name="action" value="ruleEdit"/>
            <c:param name="ma_user" value="${ma_user}"/>
            <c:param name="ma_id" value="${ma_id}"/>
            <c:param name="mf_id" value="${mf_id}"/>
            <c:param name="mfr_id" value="${mfr.id}"/>
          </c:url>
          <c:url value="MailAccount" var="urlDelete">
            <c:param name="action" value="ruleDelete"/>
            <c:param name="ma_user" value="${ma_user}"/>
            <c:param name="ma_id" value="${ma_id}"/>
            <c:param name="mf_id" value="${mf_id}"/>
            <c:param name="mfr_id" value="${mfr.id}"/>
          </c:url>
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <td>${mfr.field}</td>
            <td>${mfr.operation}</td>
            <td>${mfr.value}</td>
            <td align="center">
              <c:choose>
                <c:when test="${mfr.active}">
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