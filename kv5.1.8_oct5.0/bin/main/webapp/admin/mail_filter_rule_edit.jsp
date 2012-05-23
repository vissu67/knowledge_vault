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
  <script src="js/jquery-1.3.2.min.js" type="text/javascript"></script>
  <script src="js/vanadium-min.js" type="text/javascript"></script>
  <title>Mail filter rule</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <c:choose>
        <c:when test="${action == 'ruleCreate'}"><h1>Create mail filter rule</h1></c:when>
        <c:when test="${action == 'ruleEdit'}"><h1>Edit mail filter rule</h1></c:when>
        <c:when test="${action == 'ruleDelete'}"><h1>Delete mail filter rule</h1></c:when>
      </c:choose>
      <form action="MailAccount" id="form">
        <input type="hidden" name="action" id="action" value="${action}"/>
        <input type="hidden" name="persist" value="${persist}"/>
        <input type="hidden" name="ma_id" value="${ma_id}"/>
        <input type="hidden" name="mf_id" value="${mf_id}"/>
        <input type="hidden" name="mfr_id" value="${mfr.id}"/>
        <table class="form" width="345px" align="center">
          <tr>
            <td nowrap="nowrap">Field</td>
            <td>
              <select name="mfr_field">
                <c:forEach var="fld" items="${fields}">
                  <c:choose>
                    <c:when test="${fld == mfr.field}">
                      <option value="${fld}" selected="selected">${fld}</option>
                    </c:when>
                    <c:otherwise>
                      <option value="${fld}">${fld}</option>
                    </c:otherwise>
                  </c:choose>
                </c:forEach>
              </select>
            </td>
          </tr>
          <tr>
            <td nowrap="nowrap">Operation</td>
            <td>
              <select name="mfr_operation">
                <c:forEach var="ope" items="${operations}">
                  <c:choose>
                    <c:when test="${ope == mfr.operation}">
                      <option value="${ope}" selected="selected">${ope}</option>
                    </c:when>
                    <c:otherwise>
                      <option value="${ope}">${ope}</option>
                    </c:otherwise>
                  </c:choose>
                </c:forEach>
              </select>
            </td>
          </tr>
          <tr>
            <td nowrap="nowrap">Value</td>
            <td><input name="mfr_value" value="${mfr.value}"/></td>
          </tr>
          <tr>
            <td>Active</td>
            <td>
              <c:choose>
                <c:when test="${mfr.active}">
                  <input name="mfr_active" type="checkbox" checked="checked"/>
                </c:when>
                <c:otherwise>
                  <input name="mfr_active" type="checkbox"/>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td colspan="2" align="right">
              <input type="button" onclick="javascript:window.history.back()" value="Cancel"/>
              <input type="submit" value="Send"/>
            </td>
          </tr>
        </table>
      </form>
      <br/>
      <div class="warn" style="text-align: center;" id="dest"></div>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>