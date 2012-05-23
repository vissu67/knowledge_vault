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
  <link rel="stylesheet" type="text/css" href="css/colorpicker.css" />
  <script src="js/jquery-1.3.2.min.js" type="text/javascript"></script>
  <script src="js/vanadium-min.js" type="text/javascript"></script>
  <title>Image stamp edit</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <c:choose>
        <c:when test="${action == 'imageCreate'}"><h1>Create image stamp</h1></c:when>
        <c:when test="${action == 'imageEdit'}"><h1>Edit image stamp</h1></c:when>
        <c:when test="${action == 'imageDelete'}"><h1>Delete image stamp</h1></c:when>
      </c:choose>
      <form action="Stamp" method="post" enctype="multipart/form-data">
        <input type="hidden" name="action" value="${action}"/>
        <input type="hidden" name="persist" value="${persist}"/>
        <input type="hidden" name="si_id" value="${stamp.id}"/>
        <table class="form" width="420px">
          <tr>
            <td>Name</td>
            <td><input class=":required :only_on_blur" name="si_name" value="${stamp.name}"/></td>
          </tr>
          <tr>
            <td>Description</td>
            <td><textarea class="" name="si_description" cols="50">${stamp.description}</textarea></td>
          </tr>
          <tr>
            <td>Image</td>
            <td>
              <c:choose>
                <c:when test="${action == 'imageCreate'}">
                  <input class=":required :only_on_blur" type="file" name="image"/>
                </c:when>
                <c:otherwise>
                  <c:url value="Stamp" var="urlImage">
                    <c:param name="action" value="imageView"/>
                    <c:param name="si_id" value="${stamp.id}"/>
                  </c:url>
                  <table cellpadding="0" cellspacing="0"><tr><td><img src="${urlImage}"/></td></tr><tr><td><input type="file" name="image"/></td></tr></table>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td>Layer</td>
            <td>
              <select class=":required :only_on_blur" name="si_layer">
                <c:choose>
                  <c:when test="${stamp.layer == 0}">
                    <option value="0" selected="selected">Under content</option>
                  </c:when>
                  <c:otherwise>
                    <option value="0">Under content</option>
                  </c:otherwise>
                </c:choose>
                <c:choose>
                  <c:when test="${stamp.layer == 1}">
                    <option value="1" selected="selected">Over content</option>
                  </c:when>
                  <c:otherwise>
                    <option value="1">Over content</option>
                  </c:otherwise>
                </c:choose>
              </select>
            </td>
          </tr>
          <tr>
            <td>Opacity</td>
            <td><input class=":required :float :only_on_blur" name="si_opacity" size="4" value="${stamp.opacity}"/></td>
          </tr>
          <tr>
            <td>Expr. X</td>
            <td><input class=":required :only_on_blur" name="si_expr_x" size="36" value="${stamp.exprX}"/></td>
          </tr>
          <tr>
            <td>Expr. Y</td>
            <td><input class=":required :only_on_blur" name="si_expr_y" size="36" value="${stamp.exprY}"/></td>
          </tr>
          <tr>
            <td>Active</td>
            <td>
              <c:choose>
                <c:when test="${stamp.active}">
                  <input name="si_active" type="checkbox" checked="checked"/>
                </c:when>
                <c:otherwise>
                  <input name="si_active" type="checkbox"/>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td>Users</td>
            <td>
              <select multiple="multiple" name="si_users" size="10">
                <c:forEach var="user" items="${users}">
                  <c:choose>
                    <c:when test="${fn:contains(stamp.users, user.id)}">
                      <option value="${user.id}" selected="selected">${user.id}</option>
                    </c:when>
                    <c:otherwise>
                      <option value="${user.id}">${user.id}</option>
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