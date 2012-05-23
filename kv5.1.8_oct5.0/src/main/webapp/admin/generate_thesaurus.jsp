<%@ page import="java.io.File" %>
<%@ page import="java.util.Vector" %>
<%@ page import="com.openkm.core.Config" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ page import="com.openkm.core.HttpSessionManager" %>
<%@ page import="com.openkm.kea.tree.KEATree" %>
<%@ page import="com.openkm.util.FormatUtil" %>
<%@ page import="com.openkm.util.impexp.RepositoryImporter" %>
<%@ page import="com.openkm.util.impexp.HTMLInfoDecorator" %>
<%@ page import="com.openkm.util.impexp.ImpExpStats" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@page import="com.openkm.bean.Repository"%><html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/style.css" />
  <title>Generate thesaurus</title>
</head>
<body>
<%
	if (BaseServlet.isMultipleInstancesAdmin(request)) {
		request.setCharacterEncoding("UTF-8");
		String strLevel = request.getParameter("level");
				
		out.println("<h1>Generate thesaurus</h1>");
		out.println("<form action=\"generate_thesaurus.jsp\">");
		out.println("<table class=\"form\">");
		out.println("<tr><td>");
		out.println("Show level <select name=\"level\">");
		
		for (int i=1; i<6; i++) {
			out.println("<option value=\""+i+"\" "+(String.valueOf(i).equals(strLevel)?"selected":"")+">"+i+"</option>");
		}
		
		out.println("</select>");
		out.println("</td></tr>");
		out.println("<tr><td align=\"right\">");
		out.println("<input type=\"submit\" value=\"Send\">");
		out.println("</td></tr>");
		out.println("</table>");
		out.println("</form>");
		out.println("<br/>");
		out.println("<table class=\"results\" width=\"100%\">");
		out.println("<tr><th>Parameter</th><th>Value</th></tr>");
		out.println("<tr class=\"odd\"><td><b>"+Config.PROPERTY_KEA_THESAURUS_SKOS_FILE+"</b></td><td>"+Config.KEA_THESAURUS_SKOS_FILE+"</td></tr>");
		out.println("<tr class=\"even\"><td><b>"+Config.PROPERTY_KEA_THESAURUS_OWL_FILE+"</b></td><td>"+Config.KEA_THESAURUS_OWL_FILE+"</td></tr>");
		out.println("<tr class=\"odd\"><td><b>"+Config.PROPERTY_KEA_THESAURUS_VOCABULARY_SERQL+"</b></td><td>"+Config.KEA_THESAURUS_VOCABULARY_SERQL+"</td></tr>");
		out.println("<tr class=\"even\"><td><b>"+Config.PROPERTY_KEA_THESAURUS_BASE_URL+"</b></td><td>"+Config.KEA_THESAURUS_BASE_URL+"</td></tr>");
		out.println("<tr class=\"odd\"><td><b>"+Config.PROPERTY_KEA_THESAURUS_TREE_ROOT+"</b></td><td>"+Config.KEA_THESAURUS_TREE_ROOT+"</td></tr>");
		out.println("<tr class=\"even\"><td><b>"+Config.PROPERTY_KEA_THESAURUS_TREE_CHILDS+"</b></td><td>"+Config.KEA_THESAURUS_TREE_CHILDS+"</td></tr>");
		out.println("<tr class=\"odd\"><td><b>"+Config.PROPERTY_KEA_MODEL_FILE+"</b></td><td>"+Config.KEA_MODEL_FILE+"</td></tr>");
		out.println("<tr class=\"even\"><td><b>"+Config.PROPERTY_KEA_AUTOMATIC_KEYWORD_EXTRACTION_NUMBER+"</b></td><td>"+Config.KEA_AUTOMATIC_KEYWORD_EXTRACTION_NUMBER+"</td></tr>");
		out.println("<tr class=\"odd\"><td><b>"+Config.PROPERTY_KEA_AUTOMATIC_KEYWORD_EXTRACTION_RESTRICTION+"</b></td><td>"+Config.KEA_AUTOMATIC_KEYWORD_EXTRACTION_RESTRICTION+"</td></tr>");
		out.println("<tr class=\"even\"><td><b>"+Config.PROPERTY_KEA_STOPWORDS_FILE+"</b></td><td>"+Config.KEA_STOPWORDS_FILE+"</td></tr>");
		out.println("</table>");

		try {
			if (!Config.KEA_THESAURUS_OWL_FILE.equals("")) {
				if (strLevel != null && !strLevel.equals("")) {
					out.println("<hr/>");
					int level = Integer.parseInt(strLevel);
					long begin = System.currentTimeMillis();
					KEATree.generateTree(level, "/"+Repository.THESAURUS, new Vector<String>(), out);
					long end = System.currentTimeMillis();
					out.println("<hr/>");
					out.println("<div class=\"ok\">Level '"+level+"'</div>");
				}
			} else {
				out.println("<hr/>");
				out.println("<div class=\"warn\">Warning: "+Config.PROPERTY_KEA_THESAURUS_OWL_FILE+" is empty</div>");
			}
		} catch (Exception e) {
			out.println("<div class=\"error\">"+e.getMessage()+"<div>");
		}
	} else {
		out.println("<div class=\"error\"><h3>Only admin users allowed</h3></div>");
	}
%>
</body>
</html>