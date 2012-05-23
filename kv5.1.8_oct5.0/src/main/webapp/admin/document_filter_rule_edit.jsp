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
  <title>Document filter rule</title>
</head>
<body>
  <c:set var="isAdmin"><%=BaseServlet.isAdmin(request)%></c:set>
  <c:choose>
    <c:when test="${isAdmin}">
      <c:choose>
        <c:when test="${action == 'ruleCreate'}"><h1>Create document filter rule</h1></c:when>
        <c:when test="${action == 'ruleEdit'}"><h1>Edit document filter rule</h1></c:when>
        <c:when test="${action == 'ruleDelete'}"><h1>Delete document filter rule</h1></c:when>
      </c:choose>
      <form action="DocumentFilter" id="form">
        <input type="hidden" name="action" id="action" value="${action}"/>
        <input type="hidden" name="persist" value="${persist}"/>
        <input type="hidden" name="df_id" value="${df_id}"/>
        <input type="hidden" name="dfr_id" value="${dfr.id}"/>
        <table class="form" width="345px" align="center">
          <tr>
            <td nowrap="nowrap">Action</td>
            <td>
              <select name="dfr_action" id="dfr_action">
                <c:forEach var="act" items="${actions}">
                  <c:choose>
                    <c:when test="${act == dfr.action}">
                      <option value="${act}" selected="selected">${act}</option>
                    </c:when>
                    <c:otherwise>
                      <option value="${act}">${act}</option>
                    </c:otherwise>
                  </c:choose>
                </c:forEach>
              </select>
            </td>
          </tr>
          <tr>
            <td>Value</td>
            <td>
              <input name="dfr_value_str" id="dfr_value_str" value="${dfr.value}"/>
              <input name="dfr_value_bool" id="dfr_value_bool" type="checkbox"/>
              <select name="dfr_value_pg" id="dfr_value_pg">
                <c:forEach var="pg" items="${pgroups}">
                  <c:choose>
                    <c:when test="${pg.name == dfr.value}">
                      <option value="${pg.name}" selected="selected">${pg.label}</option>
                    </c:when>
                    <c:otherwise>
                      <option value="${pg.name}">${pg.label}</option>
                    </c:otherwise>
                  </c:choose>
                </c:forEach>
              </select>
              <select name="dfr_value_wf" id="dfr_value_wf">
                <c:forEach var="wf" items="${wflows}">
                  <c:choose>
                    <c:when test="${wf.id == dfr.id}">
                      <option value="${wf.id}" selected="selected">${wf.name}</option>
                    </c:when>
                    <c:otherwise>
                      <option value="${wf.id}">${wf.name}</option>
                    </c:otherwise>
                  </c:choose>
                </c:forEach>
              </select>
            </td>
          </tr>
          <tr>
            <td>Active</td>
            <td>
              <c:choose>
                <c:when test="${dfr.active}">
                  <input name="dfr_active" type="checkbox" checked="checked"/>
                </c:when>
                <c:otherwise>
                  <input name="dfr_active" type="checkbox"/>
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
    function valueType(action) {
      if (action == 'WIZARD_PROPERTY_GROUP' || action == 'ASSIGN_PROPERTY_GROUP') {
        $("#dfr_value_str").hide();
        $("#dfr_value_bool").hide();
        $("#dfr_value_pg").show();
        $("#dfr_value_wf").hide();
      } else if (action == 'WIZARD_WORKFLOW' || action == 'ASSIGN_WORKFLOW') {
        $("#dfr_value_str").hide();
        $("#dfr_value_bool").hide();
        $("#dfr_value_pg").hide();
        $("#dfr_value_wf").show();
      } else if (action == 'WIZARD_CATEGORY' || action == 'WIZARD_KEYWORD') {
        $("#dfr_value_str").hide();
        $("#dfr_value_bool").show();
        $("#dfr_value_pg").hide();
        $("#dfr_value_wf").hide();
      } else if (action == 'ADD_CATEGORY' || action == 'ADD_KEYWORD') {
        $("#dfr_value_str").show();
        $("#dfr_value_bool").hide();
        $("#dfr_value_pg").hide();
        $("#dfr_value_wf").hide();
      } else if (action == 'EXTRACT_METADATA') {
        $("#dfr_value_str").hide();
        $("#dfr_value_bool").show();
        $("#dfr_value_pg").hide();
        $("#dfr_value_wf").hide();
      }
    }
    
    // Set value type by selected default type 
    valueType($("#dfr_action").attr('value'));
    
    // Set value type when change type
    $("#dfr_action").bind("change", function() {
    	valueType($(this).attr('value'));
    });
  </script>
</body>
</html>