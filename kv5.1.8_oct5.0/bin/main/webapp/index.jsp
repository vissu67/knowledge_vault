<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page errorPage="general-error.jsp"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="com.openkm.util.WebUtils"%>
<%@ page import="com.openkm.core.Config"%>
<%@ page import="com.openkm.util.FormatUtil"%>
<%
	request.setCharacterEncoding("UTF-8");
	String url = null;
	String docPath = WebUtils.getString(request, "docPath", null);
	
	if (FormatUtil.isMobile(request)) {
		url = Config.EXPERIMENTAL_MOBILE_CONTEXT + "/index.jsp";
	} else {
		url = "frontend/index.jsp";
	}
	
	if (docPath != null) {
		url += "?docPath=" + URLEncoder.encode(docPath, "UTF-8");
	}
	
	if (!Config.DEFAULT_LANG.equals("")) {
		if (docPath != null) {
			url += "&lang=" + Config.DEFAULT_LANG;
		} else {
			url += "?lang=" + Config.DEFAULT_LANG;
		}
	}
	
	// Go to party
	response.sendRedirect(url);
%>