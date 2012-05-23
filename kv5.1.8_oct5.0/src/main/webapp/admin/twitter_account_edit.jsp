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
  <title>Twitter account</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <c:choose>
        <c:when test="${action == 'create'}"><h1>Create twitter account</h1></c:when>
        <c:when test="${action == 'edit'}"><h1>Edit twitter account</h1></c:when>
        <c:when test="${action == 'delete'}"><h1>Delete twitter account</h1></c:when>
      </c:choose>
      <form action="TwitterAccount">
        <input type="hidden" name="action" value="${action}"/>
        <input type="hidden" name="persist" value="${persist}"/>
        <input type="hidden" name="ta_id" value="${ta.id}"/>
        <input type="hidden" name="ta_user" value="${ta.user}"/>
        <table class="form" width="330px">
          <tr>
            <td nowrap="nowrap">Twitter user</td>
            <td><input class=":required :only_on_blur" name="ta_tuser" value="${ta.twitterUser}"/></td>
          </tr>
          <tr>
            <td>Active</td>
            <td>
              <c:choose>
                <c:when test="${ta.active}">
                  <input name="ta_active" type="checkbox" checked="checked"/>
                </c:when>
                <c:otherwise>
                  <input name="ta_active" type="checkbox"/>
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
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
</body>
</html>