/**
 *  OpenKM, Open Document Management System (http://www.openkm.com)
 *  Copyright (c) 2006-2011  Paco Avila & Josep Llort
 *
 *  No bytes were intentionally harmed during the development of this application.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.servlet.admin;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.api.OKMFolder;
import com.openkm.bean.ContentInfo;
import com.openkm.util.FormatUtil;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;
import com.openkm.util.impexp.HTMLInfoDecorator;
import com.openkm.util.impexp.ImpExpStats;
import com.openkm.util.impexp.RepositoryChecker;

/**
 * Repository checker servlet
 */
public class RepositoryCheckerServlet extends BaseServlet {
	private static Logger log = LoggerFactory.getLogger(RepositoryCheckerServlet.class);
	private static final long serialVersionUID = 1L;
	
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		String method = request.getMethod();
		
		if (checkMultipleInstancesAccess(request, response)) {
			if (method.equals(METHOD_GET)) {
				doGet(request, response);
			} else if (method.equals(METHOD_POST)) {
				doPost(request, response);
			}
		}
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("doGet({}, {})", request, response);
		String repoPath = WebUtils.getString(request, "repoPath", "/okm:root");
		boolean versions = WebUtils.getBoolean(request, "versions");
		updateSessionManager(request);
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
		out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		out.println("<head>");
		out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
		out.println("<link rel=\"Shortcut icon\" href=\"favicon.ico\" />");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/style.css\" />");
		out.println("<body>");
		out.println("<h1>Repository checker</h1>");
		out.flush();
		
		try {
			if (!repoPath.equals("")) {
				ContentInfo cInfo = OKMFolder.getInstance().getContentInfo(null, repoPath);
				long begin = System.currentTimeMillis();
				ImpExpStats stats = RepositoryChecker.checkDocuments(null, repoPath, versions, out,
						new HTMLInfoDecorator((int)cInfo.getDocuments()));
				long end = System.currentTimeMillis();
				out.println("<hr/>");
				out.println("<div class=\"ok\">Path: "+repoPath+"</div>");
				out.println("<div class=\"ok\">Versions: "+versions+"</div>");
				out.println("<br/>");
				out.println("<b>Documents:</b> "+stats.getDocuments()+"<br/>");
				out.println("<b>Folders:</b> "+stats.getFolders()+"<br/>");
				out.println("<b>Size:</b> "+FormatUtil.formatSize(stats.getSize())+"<br/>");
				out.println("<b>Time:</b> "+FormatUtil.formatSeconds(end - begin)+"<br/>");
				
				// Activity log
				UserActivity.log(request.getRemoteUser(), "ADMIN_REPOSITORY_CHECKER", null,
						"Documents: " + stats.getDocuments() +
						", Folders: " + stats.getFolders() +
						", Size: " + FormatUtil.formatSize(stats.getSize()) +
						", Time: " + FormatUtil.formatSeconds(end - begin));
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		} finally {
			out.println("</body>");
			out.println("</html>");
			out.flush();
		}
	}
}
