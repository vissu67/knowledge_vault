<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.openkm.core.Config" %>
<% 
	session.invalidate();
	response.sendRedirect("index.jsp");
	//response.sendRedirect(request.getContextPath()+"/index.jsp");
%>