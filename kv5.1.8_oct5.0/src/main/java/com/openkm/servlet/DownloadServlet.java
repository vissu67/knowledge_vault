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
import java.io.InputStream;
import java.io.PrintWriter;

import javax.jcr.ItemNotFoundException;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Document;
import com.openkm.core.JcrSessionManager;
import com.openkm.jcr.JCRUtils;
import com.openkm.module.base.BaseDocumentModule;
import com.openkm.util.WebUtils;

/**
 * Download Servlet
 */
public class DownloadServlet extends BasicSecuredServlet {
	private static Logger log = LoggerFactory.getLogger(DownloadServlet.class);
	private static final long serialVersionUID = 1L;
	
	/**
	 * 
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		request.setCharacterEncoding("UTF-8");
		String path = WebUtils.getString(request, "path");
		String uuid = WebUtils.getString(request, "uuid");
		String token = WebUtils.getString(request, "token");
		Session session = null;
		
		try {
			if (token.equals("")) {
				session = getSession(request);
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			if (session != null) {
				Node node = null;
				
				if (!uuid.equals("")) {
					node = session.getNodeByUUID(uuid);
					path = node.getPath();
				} else if (!path.equals("")) {
					node = session.getRootNode().getNode(path.substring(1));
				}
				
				if (node != null) {
					Document doc = BaseDocumentModule.getProperties(session, node);
					String fileName = JCRUtils.getName(doc.getPath());
					log.info("Download {} by {}", path, session.getUserID());
					InputStream is = BaseDocumentModule.getContent(session, path, false);
					WebUtils.sendFile(request, response, fileName, doc.getMimeType(), true, is);
					is.close();
				} else {
					response.setContentType("text/plain; charset=UTF-8");
					PrintWriter out = response.getWriter();
					out.println("Missing document reference");
					out.close();
				}
			} else {
				response.setContentType("text/plain; charset=UTF-8");
				PrintWriter out = response.getWriter();
				out.println("Missing user credentials");
				out.close();
			}
		} catch (LoginException e) {
			log.warn(e.getMessage(), e);
			response.setHeader("WWW-Authenticate", "Basic realm=\"OpenKM Download Server\"");
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
		} catch (ItemNotFoundException e) {
			log.warn(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "ItemNotFoundException: "+e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "PathNotFoundException: "+e.getMessage());
		} catch (RepositoryException e) {
			log.warn(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "RepositoryException: "+e.getMessage());
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} finally {
			if (token.equals("") && session != null) {
				session.logout();
			}
		}
	}
}
