<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page errorPage="error.jsp"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="f" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <meta name="viewport" content="width=device-width, minimum-scale=1.0, maximum-scale=1.0"/>
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" href="css/style.css" type="text/css" />
  <title>OpenKM Mobile</title>
</head>
<body>
  <c:url value="Handler" var="urlBrowse">
    <c:set var="parent"><u:getParent path="${fld.path}"/></c:set>
    <c:param name="action" value="browse"/>
    <c:param name="path" value="${parent}"/>
  </c:url>
  <table class="results" >
    <tr><th colspan="2">Folder <span style="font-weight: normal">${path}</span></th></tr>
    <tr><th>Property</th><th>Value</th></tr>
    <tr class="even"><td><b>UUID</b></td><td>${fld.uuid}</td></tr>
    <tr class="odd"><td><b>Name</b></td><td><u:getName path="${fld.path}"/></td></tr>
    <tr class="even"><td><b>Parent</b></td><td><a href="${urlBrowse}"><u:getParent path="${fld.path}"/></a></td></tr>
    <tr class="odd"><td><b>Created</b></td><td><f:formatDate value="${fld.created.time}" type="both"/> by ${doc.author}</td></tr>
    <c:choose>
      <c:when test="${fld.subscribed}"><c:set var="subscribed" value="Yes"/></c:when>
      <c:otherwise><c:set var="subscribed" value="No"/></c:otherwise>
    </c:choose>
    <tr class="even"><td><b>Subscribed</b></td><td>${subscribed}</td></tr>
  </table>
  <table class="results">
    <tr><th>Subscribed users</th></tr>
    <c:forEach var="sub" items="${fld.subscriptors}" varStatus="row">
      <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}"><td>${sub}</td></tr>
    </c:forEach>
  </table>
</body>
</html>