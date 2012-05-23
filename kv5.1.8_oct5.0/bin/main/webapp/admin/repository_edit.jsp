<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/style.css" />
  <link rel="stylesheet" type="text/css" href="js/codemirror/lib/codemirror.css" />
  <link rel="stylesheet" type="text/css" href="js/codemirror/mode/clike/clike.css" />
  <style type="text/css">
    .CodeMirror { width: 700px; height: 400px; background-color: #f8f6c2; }
    .activeline { background: #f0fcff !important; }
  </style>
  <script type="text/javascript" src="js/codemirror/lib/codemirror.js"></script>
  <script type="text/javascript" src="js/codemirror/mode/clike/clike.js"></script>
  <script type="text/javascript" src="js/jquery-1.3.2.min.js"></script>
  <script type="text/javascript">
    $(document).ready(function() {
      if ("${property.name}" == "okm:scriptCode")
      var cm = CodeMirror.fromTextArea(document.getElementById('value'), {
          lineNumbers: true,
      	  matchBrackets: true,
          indentUnit: 4,
          mode: "text/x-java",
          onCursorActivity: function() {
        	cm.setLineClass(hlLine, null);
            hlLine = cm.setLineClass(cm.getCursor().line, "activeline");
          }
        }
      );
      var hlLine = cm.setLineClass(0, "activeline");
    });
  </script>
  <title>Repository Edit</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isMultipleInstancesAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <h1>Repository edit</h1>
      <form action="RepositoryView" method="post">
        <input type="hidden" name="action" value="save"/>
        <input type="hidden" name="path" value="${node.path}"/>
        <input type="hidden" name="property" value="${property.name}"/>
        <table class="form" width="350px">
          <tr><td>Node</td><td><i>${node.path}</i></td></tr>
          <tr><td>Property</td><td><i>${property.name}</i></td></tr>
          <c:choose>
            <c:when test="${multiple}">
              <tr>
                <td>Value</td>
                <td><textarea cols="75" rows="15" name="value" id="value">${value}</textarea></td>
              </tr>
            </c:when>
            <c:otherwise>
              <tr><td>Value</td><td><input size="64" type="text" name="value" value="${value}"/></td></tr>
            </c:otherwise>
          </c:choose>
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
</body>
</html>