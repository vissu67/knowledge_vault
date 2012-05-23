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
  <title>Text Stamp List</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <c:url value="Stamp" var="urlImageList">
        <c:param name="action" value="imageList"/>
      </c:url>
      <h1>Text stamp list <span style="font-size: 10px;">(<a href="${urlImageList}">Image stamps</a>)</span></h1>
      <table class="results" width="80%">
        <tr>
          <th>Name</th><th>Description</th><th>Text</th><th>Color</th><th>Users</th><th width="25px">Active</th>
          <th width="75px">
            <c:url value="Stamp" var="urlCreate">
              <c:param name="action" value="textCreate"/>
            </c:url>
            <a href="${urlCreate}"><img src="img/action/new.png" alt="New stamp" title="New stamp"/></a>
          </th>
        </tr>
        <c:forEach var="stamp" items="${stamps}" varStatus="row">
          <c:url value="Stamp" var="urlEdit">
            <c:param name="action" value="textEdit"/>
            <c:param name="st_id" value="${stamp.id}"/>
          </c:url>
          <c:url value="Stamp" var="urlDelete">
            <c:param name="action" value="textDelete"/>
            <c:param name="st_id" value="${stamp.id}"/>
          </c:url>
          <c:url value="Stamp" var="urlTest">
            <c:param name="action" value="textTest"/>
            <c:param name="st_id" value="${stamp.id}"/>
          </c:url>
          <c:url value="Stamp" var="urlActive">
            <c:param name="action" value="textActive"/>
            <c:param name="st_id" value="${stamp.id}"/>
            <c:param name="st_active" value="${!stamp.active}"/>
          </c:url>
          <c:url value="Stamp" var="urlImage">
            <c:param name="action" value="textColor"/>
            <c:param name="st_id" value="${stamp.id}"/>
          </c:url>
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <td>${stamp.name}</td><td>${stamp.description}</td><td>${stamp.text}</td>
            <td align="center"><img src="${urlImage}"/></td>
            <td>
              <c:forEach var="user" items="${stamp.users}">
                ${user}
              </c:forEach>
            </td>
            <td align="center">
              <c:choose>
                <c:when test="${stamp.active}">
                  <a href="${urlActive}"><img src="img/true.png" alt="Active" title="Active"/></a>
                </c:when>
                <c:otherwise>
                  <a href="${urlActive}"><img src="img/false.png" alt="Inactive" title="Inactive"/></a>
                </c:otherwise>
              </c:choose>
            </td>
            <td align="center">
              <a href="${urlEdit}"><img src="img/action/edit.png" alt="Edit" title="Edit"/></a>
              &nbsp;
              <a href="${urlDelete}"><img src="img/action/delete.png" alt="Delete" title="Delete"/></a>
              &nbsp;
              <a href="${urlTest}"><img src="img/action/pdf.png" alt="Test" title="Test"/></a>
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