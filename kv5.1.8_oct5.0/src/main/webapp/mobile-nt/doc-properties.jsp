<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page errorPage="error.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="f" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8" />  
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />  
  <title>OpenKM Mobile</title>
  <link rel="apple-touch-icon" href="img/condor.jpg" />
  <link rel="stylesheet" href="http://code.jquery.com/mobile/1.0b1/jquery.mobile-1.0b1.min.css" />
  <script src="http://code.jquery.com/jquery-1.5.2.min.js"></script>
  <script src="http://code.jquery.com/mobile/1.0b1/jquery.mobile-1.0b1.min.js"></script>
</head>
<body>
  <div data-role="page" data-theme="b" id="jkm-home">
    <div data-role="header">
      <h1>Document Properties</h1>
      <!-- <a href="create" data-icon="plus" class="ui-btn-right">Create</a> -->
    </div>
    <div data-role="content">
      <c:url value="/frontend/Download" var="urlDownload">
        <c:param name="id" value="${doc.path}"/>
      </c:url>
      <c:url value="/frontend/Download" var="urlDownloadPdf">
        <c:param name="toPdf"/>
        <c:param name="id" value="${doc.path}"/>
      </c:url>
      <c:url value="Handler" var="urlBrowse">
        <c:set var="parent"><u:getParent path="${doc.path}"/></c:set>
        <c:param name="action" value="browse"/>
        <c:param name="path" value="${parent}"/>
      </c:url>
      <table style="width: 100%">
        <tr><th scope="row">UUID:</th><td>${doc.uuid}</td></tr>
        <tr><th scope="row">Name:</th><td><u:getName path="${doc.path}"/></td></tr>
        <tr><th scope="row">Folder:</th><td><u:getParent path="${doc.path}"/></td></tr>
        <tr><th scope="row">Size:</th><td><u:formatSize size="${doc.actualVersion.size}"/></td></tr>
        <tr><th scope="row">Created:</th><td><f:formatDate value="${doc.actualVersion.created.time}" type="both"/> by ${doc.actualVersion.author}</td></tr>
        <tr><th scope="row">Modified:</th><td><f:formatDate value="${doc.lastModified.time}" type="both"/> by ${doc.author}</td></tr>
        <tr><th scope="row">MIME:</th><td>${doc.mimeType}</td></tr>
        <tr>
          <th scope="row">Keywords:</th>
          <td>
            <c:forEach var="keyword" items="${doc.keywords}">
              ${keyword}
            </c:forEach>
          </td>
        </tr>
        <tr>
          <th scope="row">Categories:</th>
          <td>
            <c:forEach var="category" items="${categories}">
              ${category}<br/>
            </c:forEach>
          </td>
        </tr>
        <c:choose>
          <c:when test="${doc.checkedOut}"><c:set var="status" value="Checkout by ${doc.lockInfo.owner}"/></c:when>
          <c:when test="${doc.locked}"><c:set var="status" value="Locked by ${doc.lockInfo.owner}"/></c:when>
          <c:otherwise><c:set var="status" value="Available"/></c:otherwise>
        </c:choose>
        <tr><th scope="row">Status:</th><td>${status}</td></tr>
        <c:choose>
          <c:when test="${doc.subscribed}"><c:set var="subscribed" value="Yes"/></c:when>
          <c:otherwise><c:set var="subscribed" value="No"/></c:otherwise>
        </c:choose>
        <tr><th scope="row">Subscribed:</th><td>${subscribed}</td></tr>
        <tr>
          <td colspan="2">
            <div data-role="collapsible" data-collapsed="true">
              <h3>Subscribed users</h3>
              <c:forEach var="sub" items="${doc.subscriptors}">
                <p>${sub}</p>
              </c:forEach>
            </div>
          </td>
        </tr>
      </table>
    </div>
    <!--
    <div data-role="footer" class="ui-bar">
      <a href="#jkm-home" data-role="button" data-icon="arrow-u">Up</a>      
    </div>
    -->
  </div>
</body>
</html>