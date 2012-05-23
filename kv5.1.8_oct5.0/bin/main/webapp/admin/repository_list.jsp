<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ page import="com.openkm.core.Config" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://www.openkm.com/tags/utils" prefix="u" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" href="css/style.css" type="text/css" />
  <link rel="stylesheet" type="text/css" href="js/codemirror/lib/codemirror.css" />
  <link rel="stylesheet" type="text/css" href="js/codemirror/mode/clike/clike.css" />
  <style type="text/css">
    .CodeMirror { width: auto; height: auto; background-color: #f8f6c2; }
  </style>
  <script type="text/javascript" src="js/codemirror/lib/codemirror.js"></script>
  <script type="text/javascript" src="js/codemirror/mode/clike/clike.js"></script>
  <script type="text/javascript" src="js/jquery-1.3.2.min.js"></script>
  <script type="text/javascript">
    $(document).ready(function() {
      var cm = CodeMirror.fromTextArea(document.getElementById('value'), {
    	  lineNumbers: true,
      	  matchBrackets: true,
          indentUnit: 4,
          mode: "text/x-java",
          readOnly: true
      });
    });
  </script>
  <title>Repository View</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isMultipleInstancesAdmin(request)%></c:set>
  <c:set var="experimentalTextExtraction"><%=Config.EXPERIMENTAL_TEXT_EXTRACTION%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <h1>Repository view</h1>
      <h2>Info</h2>
      <ul>
        <li><b>Path</b>: ${breadcrumb}</li>
        <li><b>Depth</b>: ${node.depth}</li>
        <li><b>Type</b>: ${fn:toUpperCase(node.primaryNodeType.name)}</li>        
        <c:if test="${node.depth > 0}">
          <li><b>Actions</b>:
            <c:url value="RepositoryView" var="urlRemoveContent">
              <c:param name="path" value="${node.path}"/>
              <c:param name="action" value="remove_content"/>
            </c:url>
            <c:url value="RepositoryView" var="urlRemoveCurrent">
              <c:param name="path" value="${node.path}"/>
              <c:param name="action" value="remove_current"/>
            </c:url>
            <a href="${urlRemoveContent}">Remove contents</a> -
            <a href="${urlRemoveCurrent}">Remove current</a>
            <c:if test="${node.locked && holdsLock}">
              <c:url value="RepositoryView" var="urlUnlock">
                <c:param name="path" value="${node.path}"/>
                <c:param name="action" value="unlock"/>
              </c:url>
              - <a href="${urlUnlock}">Unlock</a>
            </c:if>
            <c:if test="${isDocumentContent && node.checkedOut}">
              <c:url value="RepositoryView" var="urlCheckin">
                <c:param name="path" value="${node.path}"/>
                <c:param name="action" value="checkin"/>
              </c:url>
              - <a href="${urlCheckin}">Checkin</a>
            </c:if>
            <c:if test="${experimentalTextExtraction && (isFolder || isDocument || isDocumentContent) && !node.locked}">
              <c:url value="RepositoryView" var="urlTextExtraction">
                <c:param name="path" value="${node.path}"/>
                <c:param name="action" value="textExtraction"/>
              </c:url>
              - <a href="${urlTextExtraction}">Text Extraction</a>
            </c:if>
            <c:if test="${isFolder}">
              <c:choose>
                <c:when test="${contentInfo != null}">
                  <c:url value="RepositoryView" var="urlDeactivate">
                    <c:param name="path" value="${node.path}"/>
                    <c:param name="stats" value="0"/>
                  </c:url>
                  - <a href="${urlDeactivate}">Disable statistics</a>
                </c:when>
                <c:otherwise>
                  <c:url value="RepositoryView" var="urlActivate">
                    <c:param name="path" value="${node.path}"/>
                    <c:param name="stats" value="1"/>
                  </c:url>
                  - <a href="${urlActivate}">Enable statistics</a>
                </c:otherwise>
              </c:choose>
            </c:if>
            <c:if test="${isDocument || isFolder}">
              <li>
                <b>Scripting</b>:
                <c:choose>
                  <c:when test="${isScripting}">
                    <c:url value="RepositoryView" var="urlRemoveScript">
                      <c:param name="path" value="${node.path}"/>
                      <c:param name="action" value="remove_script"/>
                    </c:url>
                    <a href="${urlRemoveScript}">Remove script</a>
                  </c:when>
                  <c:otherwise>
                  <c:url value="RepositoryView" var="urlSetScript">
                      <c:param name="path" value="${node.path}"/>
                      <c:param name="action" value="set_script"/>
                    </c:url>
                    <a href="${urlSetScript}">Set script</a>
                  </c:otherwise>
                </c:choose> 
              </li>
            </c:if>
          </li>
          <c:if test="${node.locked}"><li><b>Locked</b></li></c:if>
        </c:if>
      </ul>
      <c:if test="${fn:length(node.mixinNodeTypes) > 0}">
        <h2>Mixin</h2>
        <ul>
          <c:forEach var="mixin" items="${node.mixinNodeTypes}">
            <c:url value="RepositoryView" var="urlRemoveMixin">
              <c:param name="path" value="${node.path}"/>
              <c:param name="mixin" value="${mixin.name}"/>
              <c:param name="action" value="remove_mixin"/>
            </c:url>
            <li>${fn:toUpperCase(mixin.name)} <a href="${urlRemoveMixin}"><img src="img/action/delete.png" alt="Delete" title="Delete"/></a></li>
          </c:forEach>
        </ul>
      </c:if>
      <c:if test="${contentInfo != null}">
        <h2>Statistics</h2>
        <ul>
          <li><b>Size</b>: <u:formatSize size="${contentInfo.size}"/></li>
          <li><b>Folders</b>: ${contentInfo.folders} </li>
          <li><b>Documents</b>: ${contentInfo.documents}</li>
          <li><b>Mails</b>: ${contentInfo.mails}</li>
        </ul>
      </c:if>
      <h2>Properties</h2>
      <table class="results" width="90%">
        <tr><th>Type</th><th>Multiple</th><th>Protected</th><th>Name</th><th>Value</th><th>Action</th></tr>
        <c:forEach var="property" items="${properties}" varStatus="row">
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <td>${property.pType}</td>
            <td align="center"><c:if test="${property.pMultiple}"><img src="img/true.png"/></c:if></td>
            <td align="center"><c:if test="${property.pProtected}"><img src="img/true.png"/></c:if></td>
            <td>${property.pName}</td>
            <td>
              <c:choose>
                <c:when test="${property.pName == 'okm:scriptCode'}">
                  <textarea id="value">${property.pValue}</textarea>
                </c:when>
                <c:otherwise>${property.pValue}</c:otherwise>
              </c:choose>
            </td>
            <td align="center">
              <c:if test="${property.pType == 'STRING' && !property.pProtected}">
                <c:url value="RepositoryView" var="urlEdit">
                  <c:param name="path" value="${node.path}"/>
                  <c:param name="property" value="${property.pName}"/>
                  <c:param name="action" value="edit"/>
                </c:url>
                <a href="${urlEdit}"><img src="img/action/edit.png" title="Edit" alt="Edit"/></a>
              </c:if>
            </td>
          </tr>
        </c:forEach>
      </table>
      <h2>Children</h2>
      <table class="results" width="90%">
        <tr><th>Type</th><th>Locked</th><th>CheckedOut</th><th>Name</th></tr>
        <c:forEach var="child" items="${children}" varStatus="row">
          <c:url value="RepositoryView" var="urlList">
            <c:param name="path" value="${child.path}"/>
          </c:url>
          <tr class="${row.index % 2 == 0 ? 'even' : 'odd'}">
            <td>${fn:toUpperCase(child.primaryNodeType)}</td>
            <td align="center"><c:if test="${child.locked}"><img src="img/true.png"/></c:if></td>
            <td align="center"><c:if test="${child.checkedOut}"><img src="img/true.png"/></c:if></td>
            <td><a href="${urlList}">${child.name}</a></td>
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