<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page errorPage="error.jsp" %>
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
  <table class="results">
    <tr><th colspan="2">Document <span style="font-weight: normal">${path}</span></th></tr>
    <tr><th>Property</th><th>Value</th></tr>
    <tr class="even"><td><b>UUID</b></td><td>${doc.uuid}</td></tr>
    <tr class="odd">
      <td><b>Name</b></td>
      <td>
        <u:getName path="${doc.path}"/>
        &nbsp;
        <img align="bottom" src="img/download.png" onclick="if (confirm('Download ${size} document?')) { document.location='${urlDownload}'; }"/>
        &nbsp;
        <c:if test="${doc.convertibleToPdf}">
          <img src="img/download_pdf.png" onclick="if (confirm('Download ${size} document as PDF?')) { document.location='${urlDownloadPdf}'; }"/>
        </c:if>
      </td>
    </tr>
    <tr class="even">
      <td><b>Folder</b></td>
      <td><a href="${urlBrowse}"><u:getParent path="${doc.path}"/></a></td>
    </tr>
    <tr class="odd"><td><b>Size</b></td><td><u:formatSize size="${doc.actualVersion.size}"/></td></tr>
    <tr class="even"><td><b>Created</b></td><td><f:formatDate value="${doc.actualVersion.created.time}" type="both"/> by ${doc.actualVersion.author}</td></tr>
    <tr class="odd"><td><b>Modified</b></td><td><f:formatDate value="${doc.lastModified.time}" type="both"/> by ${doc.author}</td></tr>
    <tr class="even"><td><b>MIME</b></td><td>${doc.mimeType}</td></tr>
    <tr class="odd"><td><b>Keywords</b></td>
      <td>
        <c:forEach var="keyword" items="${doc.keywords}">
          ${keyword}
        </c:forEach>
      </td>
    </tr>
    <tr class="even"><td><b>Categories</b></td>
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
    <tr class="odd"><td><b>Status</b></td><td>${status}</td></tr>
    <c:choose>
      <c:when test="${doc.subscribed}"><c:set var="subscribed" value="Yes"/></c:when>
      <c:otherwise><c:set var="subscribed" value="No"/></c:otherwise>
    </c:choose>
    <tr class="even"><td><b>Subscribed</b></td><td>${subscribed}</td></tr>
  </table>
  <table class="results">
    <tr><th>Subscribed users</th></tr>
    <c:forEach var="sub" items="${doc.subscriptors}" varStatus="row">
      <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}"><td>${sub}</td></tr>
    </c:forEach>
  </table>
</body>
</html>