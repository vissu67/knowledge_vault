<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.openkm.bean.HttpSessionInfo" %>
<%@ page import="com.openkm.util.FormatUtil" %>
<%@ page import="com.openkm.core.HttpSessionManager" %>
<%@ page import="com.openkm.core.Config" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Iterator" %>
<table border="0" cellpadding="2" cellspacing="0" align="center" class="demo">
<%
  out.println("<tr><td class=\"demo_alert\">");
  out.println("This demo is available for testing purposes.<br>");
  out.println("These documents can be removed at any time, <br>");
  out.println("so don't expect your document to be here tomorrow.<br><br>");
  out.println("Please, report any issue on this site to <a href=\"http://twitter.com/openkm\">@openkm</a>.");
  out.println("</td></tr>");
  out.println("<tr><td class=\"demo_title\">- LOGGED USERS -</td></tr>");
  out.println("<tr><td>");

  HttpSessionManager sm = HttpSessionManager.getInstance();

  if (sm.getSessions().isEmpty()) {
    out.println("<b>No users logged, all demo users are available.</b>");
  } else {
    out.println("<table class=\"demo_list\" width=\"100%\" align=\"center\">");
    out.println("<tr><th>User</th><th>Login</th><th>Last action</th></tr>");
    for (Iterator<HttpSessionInfo> it = sm.getSessions().iterator(); it.hasNext(); ) {
      HttpSessionInfo si = it.next();
      out.print("<tr><td>"+si.getUser()+"</td><td>"+
        FormatUtil.formatDate(si.getCreation())+"</td><td>"+
        FormatUtil.formatDate(si.getLastAccess())+"</td</tr>");
    }
    out.println("</table>");
  }

  out.println("</td></tr>");
  out.println("<tr><td class=\"demo_title\">- AVAILABLE DEMO USERS -</td></tr>");
  out.println("<tr><td><b>If you need you own user, please <a href=\"http://www.openkm.com/Contact/\">contact us</a>.</b></td></tr>");
  out.println("<tr><td>");
  out.println("<table class=\"demo_list\" align=\"center\">");
  out.println("<tr><th>User</th><th>Password</th></tr>");
  for (int i=0; i<10; i++) {
    String userID = "user" + i;
    out.println("<tr><td>" + userID + "</td><td>pass" + i +"</td></tr>");
  }
  out.println("</table>");
  out.println("</td></tr>");
  %>
</table>
