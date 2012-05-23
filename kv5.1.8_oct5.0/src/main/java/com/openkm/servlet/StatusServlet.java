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

package com.openkm.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.Workspace;
import javax.jcr.query.QueryResult;
import javax.jcr.query.Row;
import javax.jcr.query.RowIterator;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.dao.HibernateUtil;
import com.openkm.dao.bean.Activity;
import com.openkm.jcr.JCRUtils;
import com.openkm.util.UserActivity;

/**
 * Status Servlet
 */
public class StatusServlet extends BasicSecuredServlet {
	private static Logger log = LoggerFactory.getLogger(StatusServlet.class);
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		String action = request.getPathInfo();
		javax.jcr.Session jcrSession = null;
		org.hibernate.Session dbSession = null;
		log.debug("action: {}", action);
		
		try {
			jcrSession = getSession(request);
			dbSession = HibernateUtil.getSessionFactory().openSession();
			
			// Check database
			checkDatabase(dbSession);
			
			// Check repository
			checkRepository(jcrSession);
			
			response.setContentType("text/plain; charset=UTF-8");
			PrintWriter out = response.getWriter();
			out.println("OK");
			out.close();
			
			// Activity log
			UserActivity.log(jcrSession.getUserID(), "MISC_STATUS", null, "OK");
		} catch (LoginException e) {
			response.setHeader("WWW-Authenticate", "Basic realm=\"OpenKM Status Server\"");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
		} catch (RepositoryException e) {
			// Activity log
			UserActivity.log(request.getRemoteUser(), "MISC_STATUS", null, e.getMessage());
			log.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} catch (Exception e) {
			// Activity log
			UserActivity.log(request.getRemoteUser(), "MISC_STATUS", null, e.getMessage());
			log.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} finally {
			JCRUtils.logout(jcrSession);
			HibernateUtil.close(dbSession);
		}
	}
	
	/**
	 * Check database connection
	 */
	@SuppressWarnings("unchecked")
	private void checkDatabase(org.hibernate.Session session) throws Exception {
		String qs = "from Activity where action='MISC_STATUS'";
		org.hibernate.Query q = session.createQuery(qs);
		List<Activity> ret = q.list();
		
		for (Activity act : ret) {
			String txt = act.toString();
			log.debug("checkDatabase: {}", txt);
		}
	}
	
	/**
	 * Check repository connection
	 */
	private void checkRepository(javax.jcr.Session session) throws Exception {
		String st = "/jcr:root/okm:root//element(*, okm:document)[@okm:author='okmAdmin']";
		Workspace workspace = session.getWorkspace();
		javax.jcr.query.QueryManager queryManager = workspace.getQueryManager();
		javax.jcr.query.Query query = queryManager.createQuery(st, javax.jcr.query.Query.XPATH);
		QueryResult result = query.execute();
		String[] cols = result.getColumnNames();
		
		for (RowIterator it = result.getRows(); it.hasNext(); ) { 
			String txt = toString(cols, it.nextRow());
			log.debug("checkRepository: {}", txt);
		}
	}
	
	/**
	 * Convert repository search to string
	 */
	private String toString(String[] cols, Row row) throws RepositoryException {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		
		for (String col : cols) {
			Value val = row.getValue(col);
			sb.append(col).append("=");
			
			if (val == null) {
				sb.append(val).append(", ");
			} else {
				sb.append(val.getString()).append(", ");
			}
		}
		
		sb.append("}");
		return sb.toString();
	}
}
