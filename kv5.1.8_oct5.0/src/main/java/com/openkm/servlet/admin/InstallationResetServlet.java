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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.Config;
import com.openkm.dao.HibernateUtil;
import com.openkm.servlet.RepositoryStartupServlet;
import com.openkm.util.FileUtils;
import com.openkm.util.JBPMUtils;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;

/**
 * Repository reset servlet
 */
public class InstallationResetServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(InstallationResetServlet.class);
	
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
		request.setCharacterEncoding("UTF-8");
		String confirmation = WebUtils.getString(request, "confirmation");
		updateSessionManager(request);
		
		if (confirmation.equals("Yes")) {
			reset(request, response);
		} else {
			ServletContext sc = getServletContext();
			sc.getRequestDispatcher("/admin/installation_reset.jsp").forward(request, response);
		}
	}
	
	/**
	 * Perform installation reset (repository and database)
	 */
	private void reset(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		header(out, "Installation reset");
		out.flush();
		out.println("<h1>Repository reset</h1>");
		out.println("<ul>");
		out.flush();
		
		Config.SYSTEM_MAINTENANCE = true;
		out.println("<li>System into maintenance mode</li>");
		
		try {
			// Stop
			out.println("<li>Stop repository</li>");
			out.flush();
			RepositoryStartupServlet.stop(null);
			
			// Delete repository
			out.println("<li>Delete repository</li>");
			out.flush();
			File repoHome = new File(Config.REPOSITORY_HOME);
			log.info("Delete repository home: {}", repoHome.getPath());
			FileUtils.deleteQuietly(repoHome);
			
			// Init database
			out.println("<li>Initialize databases</li>");
			out.flush();
			JBPMUtils.closeConfig();
			HibernateUtil.closeSessionFactory();
			HibernateUtil.getSessionFactory(HibernateUtil.HBM2DDL_CREATE);
			
			// Start again
			out.println("<li>Start repository</li>");
			out.flush();
			RepositoryStartupServlet.start();
			
			// Finalized
			out.println("<li>Installation reset completed!</li>");
			out.flush();
			
			Config.SYSTEM_MAINTENANCE = false;
			out.println("<li>System out of maintenance mode</li>");
			out.println("</ul>");
			out.flush();
			
			// Activity log
			UserActivity.log(request.getRemoteUser(), "ADMIN_INSTALLATION_RESET", null, null);
		} catch (Exception e) {
			out.println("<div class=\"warn\">Exception: "+e.getMessage()+"</div>");
			out.flush();
		} finally {
			footer(out);
			out.flush();
			out.close();
		}
	}
}
