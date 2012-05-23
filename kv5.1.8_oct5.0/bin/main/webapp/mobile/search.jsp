<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page errorPage="error.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
  <table class="results">
    <tr><th colspan="4">Query: <span style="font-weight: normal">${query}</span></th></tr>
    <tr><th></th><th>Name</th><th></th><th>Size</th></tr>
    <c:forEach var="qr" items="${queryResult}" varStatus="row">
      <c:url value="../OKMDownloadServlet" var="urlDownload">
        <c:if test="${qr.document.convertibleToPdf}">
            <c:param name="toPdf"/>
        </c:if>
        <c:param name="id" value="${qr.document.path}"/>
      </c:url>
      <c:url value="doc-properties.jsp" var="urlProperties">
        <c:param name="path" value="${qr.document.path}"/>
      </c:url>
      <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
        <td width="18px"><img src="../frontend/img/icon/mime/${qr.document.mimeType}.gif"/></td>
        <td width="100%" onclick="if (confirm('Download ${size} document?')) { document.location='${urlDownload}'; }">
          <u:getName path="${qr.document.path}"/>
        </td>
        <td><a href="${urlProperties}"><img src="img/properties.png"/></a></td>
        <td nowrap="nowrap"><u:formatSize size="${qr.document.actualVersion.size}"/></td>
      </tr>
    </c:forEach>
  </table>
</body>
</html>