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
  <script src="js/colorpicker.js" type="text/javascript"></script>
  <script type="text/javascript">
    $('#st_color').ColorPicker({
		onSubmit: function(hsb, hex, rgb) {
			$('#st_color').val(hex);
		},
		onBeforeShow: function () {
			$(this).ColorPickerSetColor(this.value);
		}
	}).bind('keyup', function() {
		$(this).ColorPickerSetColor(this.value);
	});
  </script>
  <title>Text stamp edit</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <c:choose>
        <c:when test="${action == 'textCreate'}"><h1>Create text stamp</h1></c:when>
        <c:when test="${action == 'textEdit'}"><h1>Edit text stamp</h1></c:when>
        <c:when test="${action == 'textDelete'}"><h1>Delete text stamp</h1></c:when>
      </c:choose>
      <form action="Stamp">
        <input type="hidden" name="action" value="${action}"/>
        <input type="hidden" name="persist" value="${persist}"/>
        <input type="hidden" name="st_id" value="${stamp.id}"/>
        <table class="form" width="420px">
          <tr>
            <td>Name</td>
            <td><input class=":required :only_on_blur" name="st_name" value="${stamp.name}"/></td>
          </tr>
          <tr>
            <td>Description</td>
            <td><textarea class="" name="st_description" cols="50">${stamp.description}</textarea></td>
          </tr>
          <tr>
            <td>Text</td>
            <td><input class=":required :only_on_blur" name="st_text" size="25" value="${stamp.text}"/></td>
          </tr>
          <tr>
            <td>Layer</td>
            <td>
              <select class=":required :only_on_blur" name="st_layer">
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
            <td><input class=":required :float :only_on_blur" name="st_opacity" size="4" value="${stamp.opacity}"/></td>
          </tr>
          <tr>
            <td>Size</td>
            <td><input class=":required :integer :only_on_blur" name="st_size" size="4" value="${stamp.size}"/></td>
          </tr>
          <tr>
            <td>Color</td>
            <td>
              <table cellpadding="0" cellspacing="0"><tr>
                <td>
                  <c:if test="${action != 'textCreate'}">
                    <c:url value="Stamp" var="urlImage">
                      <c:param name="action" value="textColor"/>
                      <c:param name="st_id" value="${stamp.id}"/>
                    </c:url>
                    <img src="${urlImage}"/>&nbsp;
                  </c:if>
                </td>
                <td>
                  <input class=":required :only_on_submit" name="st_color" id="st_color" readonly="readonly" size="7" value="${stamp.color}"/>
                </td>
              </tr></table>
            </td>
          </tr>
          <tr>
            <td>Align</td>
            <td>
              <select class=":required :only_on_blur" name="st_align">
                <c:choose>
                  <c:when test="${stamp.align == 0}">
                    <option value="0" selected="selected">Left</option>
                  </c:when>
                  <c:otherwise>
                    <option value="0">Left</option>
                  </c:otherwise>
                </c:choose>
                <c:choose>
                  <c:when test="${stamp.align == 1}">
                    <option value="1" selected="selected">Center</option>
                  </c:when>
                  <c:otherwise>
                    <option value="1">Center</option>
                  </c:otherwise>
                </c:choose>
                <c:choose>
                  <c:when test="${stamp.align == 2}">
                    <option value="2" selected="selected">Right</option>
                  </c:when>
                  <c:otherwise>
                    <option value="2">Right</option>
                  </c:otherwise>
                </c:choose>
              </select>
            </td>
          </tr>
          <tr>
            <td>Rotation</td>
            <td><input class=":required :integer :only_on_blur" name="st_rotation" size="4" value="${stamp.rotation}"/></td>
          </tr>
          <tr>
            <td>Expr. X</td>
            <td><input class=":required :only_on_blur" name="st_expr_x" size="36" value="${stamp.exprX}"/></td>
          </tr>
          <tr>
            <td>Expr. Y</td>
            <td><input class=":required :only_on_blur" name="st_expr_y" size="36" value="${stamp.exprY}"/></td>
          </tr>
          <tr>
            <td>Active</td>
            <td>
              <c:choose>
                <c:when test="${stamp.active}">
                  <input name="st_active" type="checkbox" checked="checked"/>
                </c:when>
                <c:otherwise>
                  <input name="st_active" type="checkbox"/>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td>Users</td>
            <td>
              <select multiple="multiple" name="st_users" size="7">
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
  <script type="text/javascript">
    $('#st_color').ColorPicker({
		onSubmit: function(hsb, hex, rgb) {
			$('#st_color').val("#"+hex);
		},
		onBeforeShow: function () {
			$(this).ColorPickerSetColor(this.value);
		}
	}).bind('keyup', function() {
		$(this).ColorPickerSetColor(this.value);
	});
  </script>
</body>
</html>