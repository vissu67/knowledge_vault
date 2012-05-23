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
  <script src="js/jquery-1.3.2.min.js" type="text/javascript"></script>
  <script src="js/vanadium-min.js" type="text/javascript"></script>
  <title>Document filter</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <c:choose>
        <c:when test="${action == 'create'}"><h1>Create document filter</h1></c:when>
        <c:when test="${action == 'edit'}"><h1>Edit document filter</h1></c:when>
        <c:when test="${action == 'delete'}"><h1>Delete document filter</h1></c:when>
      </c:choose>
      <form action="DocumentFilter" id="form">
        <input type="hidden" name="action" id="action" value="${action}"/>
        <input type="hidden" name="persist" value="${persist}"/>
        <input type="hidden" name="df_id" value="${df.id}"/>
        <table class="form" width="345px" align="center">
          <tr>
            <td style="width: 32px;">Type</td>
            <td>
              <select name="df_type" id="df_type">
                <c:forEach var="tp" items="${types}">
                  <c:choose>
                    <c:when test="${tp == df.type}">
                      <option value="${tp}" selected="selected">${tp}</option>
                    </c:when>
                    <c:otherwise>
                      <option value="${tp}">${tp}</option>
                    </c:otherwise>
                  </c:choose>
                </c:forEach>
              </select>
            </td>
          </tr>
          <tr>
            <td style="width: 32px;">Value</td>
             <td>
               <input name="df_value_path" id="df_value_path" value="${df.value}" size="45"/>
               <select name="df_value_mime" id="df_value_mime">
                <c:forEach var="mt" items="${mimes}">
                  <c:choose>
                    <c:when test="${mt.name == df.value}">
                      <option value="${mt.name}" selected="selected">${mt.name}</option>
                    </c:when>
                    <c:otherwise>
                      <option value="${mt.name}">${mt.name}</option>
                    </c:otherwise>
                  </c:choose>
                </c:forEach>
              </select>
             </td>
          </tr>
          <tr>
            <td style="width: 32px;">Active</td>
            <td>
              <c:choose>
                <c:when test="${df.active}">
                  <input name="df_active" type="checkbox" checked="checked"/>
                </c:when>
                <c:otherwise>
                  <input name="df_active" type="checkbox"/>
                </c:otherwise>
              </c:choose>
            </td>
          </tr>
          <tr>
            <td colspan="2" align="right">
              <input type="button" onclick="javascript:window.history.back()" value="Cancel"/>
              <input type="submit" value="Send"/>
            </td>
          </tr>
        </table>
      </form>
      <br/>
      <div class="warn" style="text-align: center;" id="dest"></div>
    </c:when>
    <c:otherwise>
      <div class="error"><h3>Only admin users allowed</h3></div>
    </c:otherwise>
  </c:choose>
  <script type="text/javascript">
    function valueType(type) {
      if (type == 'MIME_TYPE') {
        $("#df_value_path").hide();
        $("#df_value_mime").show();
      } else {
        $("#df_value_path").show();
        $("#df_value_mime").hide();
      }
    }
    
    // Set value type by selected default type 
    valueType($("#df_type").attr('value'));
    
    // Set value type when change type
    $("#df_type").bind("change", function() {
    	valueType($(this).attr('value'));
    });
  </script>
</body>
</html>