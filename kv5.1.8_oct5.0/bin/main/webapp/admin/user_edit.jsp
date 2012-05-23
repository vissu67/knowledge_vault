<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/style.css" />
  <script src="js/jquery-1.3.2.min.js" type="text/javascript"></script>
  <script src="js/vanadium-min.js" type="text/javascript"></script>
  <title>User edit</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <c:choose>
        <c:when test="${action == 'userCreate'}"><h1>Create user</h1></c:when>
        <c:when test="${action == 'userEdit'}"><h1>Edit user</h1></c:when>
        <c:when test="${action == 'userDelete'}"><h1>Delete user</h1></c:when>
      </c:choose>
      <form action="Auth">
        <input type="hidden" name="action" value="${action}"/>
        <input type="hidden" name="persist" value="${persist}"/>
        <table class="form" width="372px">
          <tr>
            <td>Id</td>
            <td width="100%">
              <c:choose>
                <c:when test="${action != 'userCreate'}">
                  <input class=":required :only_on_blur" name="usr_id" value="${usr.id}" readonly="readonly"/>
                </c:when>
                <c:otherwise>
                  <input class=":required :only_on_blur" name="usr_id" value=""/>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td>Password</td>
            <td>
              <c:choose>
                <c:when test="${action == 'userCreate'}">
                  <input class=":required :only_on_blur" type="password" name="usr_password" id="usr_password" value="" autocomplete="off"/>
                </c:when>
                <c:otherwise>
                  <input class="" type="password" name="usr_password" id="usr_password" value="" autocomplete="off"/>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td nowrap="nowrap">Confirm password</td>
            <td><input class=":same_as;usr_password :only_on_blur" type="password" value="" autocomplete="off"/></td>
          </tr>
          <tr>
            <td>Name</td>
            <td><input class="" name="usr_name" value="${usr.name}"/></td>
          </tr>
          <tr>
            <td>Mail</td>
            <td><input class=":email :required :only_on_blur" name="usr_email" value="${usr.email}"/></td>
          </tr>
          <tr>
            <td>Active</td>
            <td>
              <c:choose>
                <c:when test="${usr.active}">
                  <input name="usr_active" type="checkbox" checked="checked"/>
                </c:when>
                <c:otherwise>
                  <input name="usr_active" type="checkbox"/>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td>Roles</td>
            <td>
              <select multiple="multiple" name="usr_roles" size="10">
                <c:forEach var="role" items="${roles}">
                  <c:choose>
                    <c:when test="${fn:contains(usr.roles, role)}">
                      <option value="${role.id}" selected="selected">${role.id}</option>
                    </c:when>
                    <c:otherwise>
                      <option value="${role.id}">${role.id}</option>
                    </c:otherwise>
                  </c:choose>
                </c:forEach>
              </select>
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