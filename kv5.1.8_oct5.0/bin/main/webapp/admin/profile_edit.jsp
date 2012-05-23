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
  <title>User Profile</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <c:choose>
        <c:when test="${action == 'create'}"><h1>Create profile</h1></c:when>
        <c:when test="${action == 'edit'}"><h1>Edit profile</h1></c:when>
        <c:when test="${action == 'delete'}"><h1>Delete profile</h1></c:when>
      </c:choose>
      <form action="Profile">
        <input type="hidden" name="action" value="${action}"/>
        <input type="hidden" name="persist" value="${persist}"/>
        <input type="hidden" name="prf_id" value="${prf.id}"/>
        <table class="form" width="765px" border="0">
          <tr>
            <td valign="top" colspan="2" width="50%">
              <!-- GENERAL -->
              <jsp:include page="profile_general.jsp"/>
            </td>
            <td valign="top" rowspan="3" width="25%">
              <!-- STACKS -->
              <jsp:include page="profile_stacks.jsp"/>
              <!-- CHAT -->
              <jsp:include page="profile_chat_edit.jsp"/>
              <!-- TAB DOCUMENT -->
              <jsp:include page="profile_tab_document.jsp"/>
            </td>
            <td valign="top" rowspan="3" width="25%">
              <!-- DASHBOARD -->
              <jsp:include page="profile_dashboard.jsp"/>
              <!-- TAB -->
              <jsp:include page="profile_tab.jsp"/>
              <!-- TAB FOLDER -->
              <jsp:include page="profile_tab_folder.jsp"/>
              <!-- TAB MAIL -->
              <jsp:include page="profile_tab_mail.jsp"/>
            </td>
          </tr>
          <tr>
            <td valign="top" colspan="2">
              <!-- MISC -->
              <jsp:include page="profile_misc.jsp"/>
            </td>
          </tr>
          <tr>
            <td valign="top" colspan="2">
              <!-- WIZARD -->
              <jsp:include page="profile_wizard.jsp"/>
            </td>
          </tr>
          <tr>
            <td valign="top">
              <!-- MENU -->
              <jsp:include page="profile_menu.jsp"/>
              <!-- MENU BOOKMARK -->
              <jsp:include page="profile_menu_bookmark.jsp"/>
            </td>
            <td valign="top">
              <!-- MENU TOOL -->
              <jsp:include page="profile_menu_tool.jsp"/>
              <!-- MENU HELP -->
              <jsp:include page="profile_menu_help.jsp"/>
            </td>
            <td valign="top">
              <!-- MENU FILE -->
              <jsp:include page="profile_menu_file.jsp"/>
            </td>
            <td valign="top">
              <!-- MENU EDIT -->
              <jsp:include page="profile_menu_edit.jsp"/>             
            </td>
          </tr>
          <tr>
            <td colspan="4" align="right">
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