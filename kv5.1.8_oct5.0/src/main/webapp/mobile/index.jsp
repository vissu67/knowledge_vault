<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.core.HttpSessionManager" %>
<%@ page errorPage="error.jsp" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<% HttpSessionManager.getInstance().add(request); %>
<% com.openkm.api.OKMAuth.getInstance().login(); %>
<c:redirect url="Handler">
  <c:if test="${not empty param.docPath}">
    <c:param name="action" value="docprop"/>
    <c:param name="path" value="${param.docPath}"/>
  </c:if>
</c:redirect>
