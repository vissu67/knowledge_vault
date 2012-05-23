<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page errorPage="error.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
  <script type="text/javascript">
    $(function() {
      $('li').bind('taphold', function(e) {
        var id = $(this).attr('id');
        var action = $(this).attr('data-action');
        //alert('You ' + e.type + " - '" + id + "'" + " => " + action);
        $.mobile.changePage("Handler?action="+action+"&uuid="+id, null, true, true);
        e.stopImmediatePropagation();
        return false;
      });
    });
  </script>  
</head>
<body>
  <div data-role="page" data-theme="b" id="jkm-home">
    <div data-role="header">
      <h1>Repository Browser</h1>
      <!-- <a href="create" data-icon="plus" class="ui-btn-right">Create</a> -->
    </div>
    <div data-role="content">
      <ul data-role="listview">
        <!-- List folders -->
        <c:forEach var="fld" items="${folderChilds}">
          <li id="${fld.uuid}" data-action="fldprop">
            <c:url value="Handler" var="urlBrowse">
              <c:param name="path" value="${fld.path}"/>
            </c:url>
            <c:url value="Handler" var="urlProperties">
              <c:param name="action" value="fldprop"/>
              <c:param name="uuid" value="${fld.uuid}"/>
            </c:url>
            <c:choose>
              <c:when test="${fld.hasChilds}"><c:set var="fldImg" value="menuitem_childs.gif"/></c:when>
              <c:otherwise><c:set var="fldImg" value="menuitem_empty.gif"/></c:otherwise>
            </c:choose>
            <img src="../frontend/img/${fldImg}" class="ui-li-icon"/>
            <a href="${urlBrowse}" data-transition="slide"><u:getName path="${fld.path}"/></a>
            <a href="${urlProperties}" data-role="button"></a>
          </li>
        </c:forEach>
        <!-- List documents -->
        <c:forEach var="doc" items="${documentChilds}">
          <li id="${doc.uuid}" data-action="docprop">
            <c:url value="/frontend/Download" var="urlDownload">
              <c:if test="${doc.convertibleToPdf}">
                <c:param name="toPdf"/>
              </c:if>
              <c:param name="id" value="${doc.path}"/>
            </c:url>
            <c:url value="Handler" var="urlProperties">
              <c:param name="action" value="docprop"/>
              <c:param name="uuid" value="${doc.uuid}"/>
            </c:url>
            <c:url value="/mime/${doc.mimeType}" var="urlIcon"></c:url>
            <c:set var="size"><u:formatSize size="${doc.actualVersion.size}"/></c:set>
            <img src="${urlIcon}" class="ui-li-icon"/>
            <a href="${urlDownload}" data-ajax="false"><u:getName path="${doc.path}"/></a>
            <span class="ui-li-count">${size}</span>
            <a href="${urlProperties}" data-role="button"></a>
          </li>
        </c:forEach>
      </ul>
    </div>
    <!--
    <div data-role="footer" class="ui-bar">
      <a href="#jkm-home" data-role="button" data-icon="arrow-u">Up</a>      
    </div>
    -->
  </div>
</body>
</html>