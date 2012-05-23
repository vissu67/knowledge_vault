<%@ page import="java.io.FileNotFoundException"%>
<%@ page import="java.io.IOException"%>
<%@ page import="java.io.File" %>
<%@ page import="com.openkm.core.Config" %>
<%@ page import="com.openkm.servlet.admin.BaseServlet" %>
<%@ page import="com.openkm.core.HttpSessionManager" %>
<%@ page import="com.openkm.util.FileUtils" %>
<%@ page import="com.openkm.util.WebUtils"%>
<%@ page import="com.openkm.util.FormatUtil"%>
<%@ page import="com.openkm.util.impexp.RepositoryImporter" %>
<%@ page import="com.openkm.util.impexp.HTMLInfoDecorator" %>
<%@ page import="com.openkm.util.impexp.ImpExpStats"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <link rel="Shortcut icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/style.css" />
  <script type="text/javascript" src="js/jquery-1.3.2.min.js"></script>
  <script type="text/javascript" src="js/jquery.DOMWindow.js"></script>
  <script type="text/javascript">
    $(document).ready(function() {
      $dm = $('.ds').openDOMWindow({
        height:200, width:300,
        eventType:'click',
        overlayOpacity: '57',
        windowSource:'iframe', windowPadding:0
      });
	});
    
    function dialogClose() {
    	$dm.closeDOMWindow();
    }
  </script>
  <title>Repository Import</title>
</head>
<body>
<%
	if (BaseServlet.isMultipleInstancesAdmin(request)) {
		request.setCharacterEncoding("UTF-8");
		String repoPath = WebUtils.getString(request, "repoPath", "/okm:root");
		String fsPath = WebUtils.getString(request, "fsPath");
		boolean metadata = WebUtils.getBoolean(request, "metadata");
		
		out.println("<h1>Repository import</h1>");
		out.println("<form action=\"repository_import.jsp\">");
		out.println("<table class=\"form\" align=\"center\">");
		out.println("<tr>");
		out.println("<td>Filesystem path</td>");
		out.println("<td><input type=\"text\" size=\"50\" name=\"fsPath\" id=\"fsPath\" value=\""+fsPath+"\" ></td>");
		out.println("<td><a class=\"ds\" href=\"DataBrowser?action=fs&dst=fsPath\"><img src=\"img/action/browse_fs.png\"/></a></td>");
		out.println("</tr>");
		out.println("<tr>");
		out.println("<td>Repository path</td>");
		out.println("<td><input type=\"text\" size=\"50\" name=\"repoPath\" id=\"repoPath\" value=\""+repoPath+"\" ></td>");
		out.println("<td><a class=\"ds\" href=\"DataBrowser?action=repo&dst=repoPath\"><img src=\"img/action/browse_repo.png\"/></a></td>");
		out.println("</tr>");
		out.println("<tr><td>Metadata</td><td><input type=\"checkbox\" name=\"metadata\"/></td></tr>");
		out.println("<tr><td colspan=\"3\" align=\"right\">");
		out.println("<input type=\"submit\" value=\"Send\">");
		out.println("</td></tr>");
		out.println("</table>");
		out.println("</form>");

		try {
			if (repoPath != null && !repoPath.equals("") && fsPath != null && !fsPath.equals("")) {
				out.println("<hr/>");
				File dir = new File(Config.INSTANCE_CHROOT_PATH + fsPath);
				int files = FileUtils.countFiles(dir);
				out.println("<b>Files & directories to import:</b> "+files+"<br/>");
				long begin = System.currentTimeMillis();
				ImpExpStats stats = RepositoryImporter.importDocuments(null, dir, repoPath, metadata, out, 
						new HTMLInfoDecorator(files));
				long end = System.currentTimeMillis();
				out.println("<hr/>");
				out.println("<div class=\"ok\">Filesystem '"+new File(fsPath).getAbsolutePath()+"' imported into '"+repoPath+"'</div>");
				out.println("<br/>");
				out.println("<b>Documents:</b> "+stats.getDocuments()+"<br/>");
				out.println("<b>Folders:</b> "+stats.getFolders()+"<br/>");
				out.println("<b>Size:</b> "+FormatUtil.formatSize(stats.getSize())+"<br/>");
				out.println("<b>Time:</b> "+FormatUtil.formatSeconds(end - begin)+"<br/>");
			}
		} catch (FileNotFoundException e) {
			out.println("<div class=\"error\">File Not Found: "+e.getMessage()+"<div>");
		} catch (IOException e) {
			out.println("<div class=\"error\">IO Error: "+e.getMessage()+"<div>");
		} catch (Exception e) {
			out.println("<div class=\"error\">Error: "+e.getMessage()+"<div>");
		}
	} else {
		out.println("<div class=\"error\"><h3>Only admin users allowed</h3></div>");
	}
%>
</body>
</html>