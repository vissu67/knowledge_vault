<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ page import="com.openkm.util.UserActivity"%>
<%@ page import="com.openkm.util.SecureStore"%>
<%@ page import="com.openkm.util.WebUtils"%>
<%@ page import="bsh.Interpreter"%>
<%@ page import="java.io.ByteArrayOutputStream"%>
<%@ page import="java.io.PrintStream"%>
<%@ page import="java.util.UUID"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
    .CodeMirror { width: 550px; height: 250px; background-color: #f8f6c2; }
    .activeline { background: #f0fcff !important; }
  </style>
  <script type="text/javascript" src="js/codemirror/lib/codemirror.js"></script>
  <script type="text/javascript" src="js/codemirror/mode/clike/clike.js"></script>
  <script type="text/javascript" src="js/jquery-1.3.2.min.js"></script>
  <script type="text/javascript">
    $(document).ready(function() {
      var cm = CodeMirror.fromTextArea(document.getElementById('script'), {
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
  <title>Scripting</title>
</head>
<body>
<%
	if (BaseServlet.isMultipleInstancesAdmin(request)) {
		request.setCharacterEncoding("UTF-8");
		String script = WebUtils.getString(request, "script");
		String reqCsrft = (String) session.getAttribute("csrft");
		StringBuffer scriptOutput = new StringBuffer();
		Object scriptResult = null;
		Exception scriptError = null;
		
		if (!script.equals("")) {
			if (WebUtils.getString(request, "csrft").equals(reqCsrft)) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PrintStream pout = new PrintStream(baos);
				Interpreter bsh = new Interpreter(null, pout, pout, false);
				
				// set up interpreter
				bsh.set("bsh.httpServletRequest", request);
				bsh.set("bsh.httpServletResponse", response);
				
				try {
					scriptResult = bsh.eval(script);
				} catch (Exception e) {
					scriptError = e;
				}
				
				pout.flush();
				scriptOutput.append(baos.toString());
				
				// Activity log
				UserActivity.log(request.getRemoteUser(), "SCRIPTING", null, request.getRemoteHost() + ", " + script);
			} else {
				out.println("<div class=\"error\"><h3>Security risk detected</h3></div>");
				
				// Activity log
				UserActivity.log(request.getRemoteUser(), "SECURITY_RISK", null, request.getRemoteHost() + ", " + script);
			}
		} else {
			script = "print(\"Hola, mundo!\");";		
		}
		
		String genCsrft = SecureStore.md5Encode(UUID.randomUUID().toString().getBytes());
		session.setAttribute("csrft", genCsrft);
		out.println("<h1>Scripting</h1>");
		out.println("<h2>Results</h2>");
		out.println("<table class=\"results\" width=\"95%\">");
		out.println("<tr><th>Script error</th></tr><tr class=\"odd\"><td>"+(scriptError==null?"":scriptError)+"</td></tr>");
		out.println("<tr><th>Script result</th></tr><tr class=\"odd\"><td>"+(scriptResult==null?"":scriptResult)+"</td></tr>");
		out.println("<tr><th>Script output</th></tr><tr class=\"odd\"><td>"+scriptOutput+"</td></tr>");
		out.println("</table>");
		out.println("<hr>");
		out.println("<form action=\"scripting.jsp\" method=\"post\">");
		out.println("<input type=\"hidden\" name=\"csrft\" value=\"" + genCsrft + "\">");
		out.println("<table class=\"form\" align=\"center\">");
		out.println("<tr><td><textarea cols=\"80\" rows=\"25\" name=\"script\" id=\"script\">"+script+"</textarea></td></tr>");
		out.println("<tr><td align=\"right\"><input type=\"submit\" value=\"Evaluate\"></td></tr>");
		out.println("</table>");
		out.println("</form>");
	} else {
		out.println("<div class=\"error\"><h3>Only admin users allowed</h3></div>");
	}
%>
</body>
</html>