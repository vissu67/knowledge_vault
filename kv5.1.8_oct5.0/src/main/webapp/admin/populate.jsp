<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ page import="com.openkm.util.Populate"%>
<%@ page import="java.util.Collection" %>
<%@ page import="java.util.Iterator" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/style.css" />
  <title>Populate</title>
</head>
<body>
<%
	if (BaseServlet.isMultipleInstancesAdmin(request)) {
		request.setCharacterEncoding("UTF-8");
		String token = (String) session.getAttribute("token");
		String seedWord = request.getParameter("seedWord")==null?"":request.getParameter("seedWord");
		String numDocs = request.getParameter("numDocs")==null?"":request.getParameter("numDocs");

		if (seedWord != null && !seedWord.equals("")) {
			seedWord = new String(seedWord.getBytes("ISO-8859-1"), "UTF-8");
		}
		
		out.println("<h1>Populate</h1>");
		out.println("<form action=\"populate.jsp\">");
		out.println("<table class=\"form\">");
		out.println("<tr><td>Seed</td><td><input name=\"seedWord\" value=\""+seedWord+"\"></td></tr>");
		out.println("<tr><td>Num docs</td><td><select name=\"numDocs\">");
		out.println("<option "+(numDocs.equals("5")?"selected":"")+" value=\"5\">5</option>");
		out.println("<option "+(numDocs.equals("10")?"selected":"")+" value=\"10\">10</option>");
		out.println("<option "+(numDocs.equals("25")?"selected":"")+" value=\"25\">25</option>");
		out.println("<option "+(numDocs.equals("50")?"selected":"")+" value=\"50\">50</option>");
		out.println("<option "+(numDocs.equals("100")?"selected":"")+" value=\"100\">100</option>");
		out.println("<option "+(numDocs.equals("250")?"selected":"")+" value=\"250\">250</option>");
		out.println("<option "+(numDocs.equals("500")?"selected":"")+" value=\"500\">500</option>");
		out.println("</select></td></tr>");
		out.println("<tr><td colspan=\"2\" align=\"right\"><input type=\"submit\" value=\"Send\"></td></tr>");
		out.println("</table>");
		out.println("<br/>");
				
		try {
			out.println("<table class=\"results\" width=\"80%\">");
			out.println("<tr><th>#</th><th width=\"90%\">URL</th><th>Status</th></tr>");
			
			if (!seedWord.equals("") && !numDocs.equals("")) {
				seedWord = new String(seedWord.getBytes("ISO-8859-1"), "UTF-8");
				Populate.massiveImport(token, seedWord, Integer.parseInt(numDocs), Populate.DEFAULT_TYPES, out);
			}
			
			out.println("</table>");
		} catch (Exception e) {
			out.println("<div class=\"error\">"+e.getMessage()+"</div>");
		}
	} else {
		out.println("<div class=\"error\"><h3>Only admin users allowed</h3></div>");
	}
%>
</body>
</html>
