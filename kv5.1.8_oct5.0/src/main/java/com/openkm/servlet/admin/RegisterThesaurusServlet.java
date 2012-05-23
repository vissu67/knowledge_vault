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
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.openkm.core.Config;
import com.openkm.kea.tree.KEATree;

/**
 * Register thesaurus servlet
 */
public class RegisterThesaurusServlet extends BaseServlet {
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
		int level = (request.getParameter("level") != null && !request.getParameter("level").equals("")) ? Integer
				.parseInt(request.getParameter("level"))
				: 0;
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
		out.println("<h1>Register thesaurus</h1>");
		out.flush();

		if (!Config.KEA_THESAURUS_OWL_FILE.equals("")) {
			out.println("<b>Starting thesaurus creation, this could take some hours.</b><br>");
			out.println("<b>Don't close this window meanwhile OpenKM is creating thesaurus.</b><br>");
			out.println("It'll be displayed creation information while creating nodes until level "
					+ (level + 1) + ", please be patient because tree deep level could be big.<br><br>");
			out.flush();
			KEATree.generateTree(level, "/okm:thesaurus", new Vector<String>(), out);
			out.println("<br><b>Finished thesaurus creation.</b><br>");
		} else {
			out.println("<b>Error - there's no thesaurus file defined in OpenKM.cfg</b>");
		}

		try {
			// Dummy
		} catch (Exception e) {
			e.printStackTrace();
		}

		out.println("</body>");
		out.println("</html>");
		out.flush();
	}
}
