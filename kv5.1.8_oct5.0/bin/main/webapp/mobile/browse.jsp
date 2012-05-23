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
  <c:set var="ctxKnowledgeVault" value="/okm:root"/>
  <c:set var="ctxTemplates" value="/okm:templates"/>
  <c:set var="ctxPersonal" value="/okm:personal/${userId}"/>
  <c:set var="ctxMail" value="/okm:mail/${userId}"/>
  <c:set var="ctxTrash" value="/okm:trash/${userId}"/>
  <table width="100%" cellpadding="2" cellspacing="2">
    <tr>
      <td>
        <form id="context" action="Handler">
          <input type="hidden" name="action" value="browse"/>
          <select name="path" onchange="document.getElementById('context').submit()">
            <c:set var="tmp"><u:startsWith string="${path}" prefix="${ctxTaxonomy}"/></c:set>
            <option value="${ctxTaxonomy}" <c:if test="${tmp}">selected</c:if>>Taxonomy</option>
            <c:set var="tmp"><u:startsWith string="${path}" prefix="${ctxTemplates}"/></c:set>
            <option value="${ctxTemplates}" <c:if test="${tmp}">selected</c:if>>Templates</option>
            <c:set var="tmp"><u:startsWith string="${path}" prefix="${ctxPersonal}"/></c:set>
            <option value="${ctxPersonal}" <c:if test="${tmp}">selected</c:if>>My documents</option>
            <c:set var="tmp"><u:startsWith string="${path}" prefix="${ctxMail}"/></c:set>
            <option value="${ctxMail}" <c:if test="${tmp}">selected</c:if>>E-mail</option>
            <c:set var="tmp"><u:startsWith string="${path}" prefix="${ctxTrash}"/></c:set>
            <option value="${ctxTrash}" <c:if test="${tmp}">selected</c:if>>Trash</option>
          </select>
        </form>
      </td>
      <td><form id="search" action="Handler"><input type="hidden" name="action" value="search"/><input name="query" type="text"/><input type="image" src="img/search.png" style="vertical-align: middle; border: 0;"/></form></td>
      <td></td>
      <td align="right"><a href="Handler?action=logout" onclick="return confirm('Really want to logout?')"><img src="img/logout.png" /></a></td>
    </tr>
  </table>
  <table class="results">
    <tr><th colspan="4">Path: <span style="font-weight: normal">${pathDisplay}</span></th></tr>
    <tr><th></th><th>Name</th><th style="width: 25px"></th><th>Size</th></tr>
    <!-- List folders -->
    <c:forEach var="fld" items="${folderChilds}" varStatus="row">
      <c:choose>
        <c:when test="${fld.hasChilds}"><c:set var="fldImg" value="menuitem_childs.gif"/></c:when>
        <c:otherwise><c:set var="fldImg" value="menuitem_empty.gif"/></c:otherwise>
      </c:choose>
      <c:url value="Handler" var="urlBrowse">
        <c:param name="path" value="${fld.path}"/>
      </c:url>
      <c:url value="Handler" var="urlProperties">
        <c:param name="action" value="fldprop"/>
        <c:param name="path" value="${fld.path}"/>
      </c:url>
      <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
        <td width="18px"><img src="../frontend/img/${fldImg}"/></td>
        <td width="100%" onclick="document.location='${urlBrowse}'">
          <u:getName path="${fld.path}"/>
        </td>
        <td><a href="${urlProperties}"><img src="img/properties.png"/></a></td>
        <td nowrap="nowrap"></td>
      </tr>
    </c:forEach>
    <!-- List documents -->
    <c:forEach var="doc" items="${documentChilds}" varStatus="row">
      <c:set var="size"><u:formatSize size="${doc.actualVersion.size}"/></c:set>
      <c:url value="/mime/${doc.mimeType}" var="urlIcon">
      </c:url>
      <c:url value="/frontend/Download" var="urlDownload">
        <c:if test="${doc.convertibleToPdf}">
          <c:param name="toPdf"/>
        </c:if>
        <c:param name="id" value="${doc.path}"/>
      </c:url>
      <c:url value="Handler" var="urlProperties">
        <c:param name="action" value="docprop"/>
        <c:param name="path" value="${doc.path}"/>
      </c:url>
      <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
        <td width="18px"><img src="${urlIcon}"/></td>
        <td width="100%" onclick="if (confirm('Download ${size} document?')) { document.location='${urlDownload}'; }">
          <u:getName path="${doc.path}"/>
        </td>
        <td><a href="${urlProperties}"><img src="img/properties.png"/></a></td>
        <td nowrap="nowrap">${size}</td>
      </tr>
    </c:forEach>
  </table>
</body>
</html>