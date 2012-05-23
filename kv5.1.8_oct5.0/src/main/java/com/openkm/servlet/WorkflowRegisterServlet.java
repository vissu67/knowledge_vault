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
import java.util.List;
import java.util.zip.ZipInputStream;

import javax.jcr.ItemNotFoundException;
import javax.jcr.LoginException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.jbpm.JbpmContext;
import org.jbpm.file.def.FileDefinition;
import org.jbpm.graph.def.ProcessDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.Config;
import com.openkm.util.FormUtils;
import com.openkm.util.JBPMUtils;

/**
 * Workflow Register Servlet
 */
public class WorkflowRegisterServlet extends BasicSecuredServlet {
	private static Logger log = LoggerFactory.getLogger(WorkflowRegisterServlet.class);
	private static final long serialVersionUID = 1L;
	
	/**
	 * Handle GET and POST
	 */
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("service({}, {}", request, response);
		request.setCharacterEncoding("UTF-8");
		String action = request.getPathInfo();
		PrintWriter out = response.getWriter();
		Session session = null;
		log.info("action: {}", action);
		
		try {
			if (action != null && action.length() > 1 && action.indexOf(':') > 0) {
				String[] usrpass = action.substring(1).split(":");
				log.info("UserPass: {}, Pass: {}", usrpass[0], usrpass[1]);
				
				if (Config.ADMIN_USER.equals(usrpass[0])) {
					session = getSession(usrpass[0], usrpass[1]);
				}
			} else {
				session = getSession(request);
			}
			
			if (session != null) {
				String msg = handleRequest(request);
				log.info("Status: {}", msg);
				out.print(msg);
				out.flush();
			} else {
				log.warn("Missing user credentials");
				response.setHeader("WWW-Authenticate", "Basic realm=\"OpenKM Worflow Register Server\"");
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			}
		} catch (LoginException e) {
			log.warn("LoginException", e);
			response.setHeader("WWW-Authenticate", "Basic realm=\"OpenKM Worflow Register Server\"");
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
		} catch (FileUploadException e) {
			log.warn(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "FileUploadException: "+e.getMessage());
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "IOException: "+e.getMessage());
		} catch (Exception e) {
			log.warn(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} finally {
			IOUtils.closeQuietly(out);
			
			if (session != null) {
				session.logout();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private String handleRequest(HttpServletRequest request) throws FileUploadException, IOException, Exception {
		log.warn("handleRequest({})", request);
		
		if (ServletFileUpload.isMultipartContent(request)) {
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			List<FileItem> items = upload.parseRequest(request);
			
			if (items.isEmpty()) {
				log.warn("No process file in the request");
		        return "No process file in the request";
			} else {
				FileItem fileItem = (FileItem) items.get(0);
				
				if (fileItem.getContentType().indexOf("application/x-zip-compressed") == -1) {
					log.warn("Not a process archive");
					throw new Exception("Not a process archive");
				} else {
					log.debug("Deploying process archive: {}", fileItem.getName());
					JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
					InputStream isForms = null;
					ZipInputStream zis = null;
					
					try {
						zis = new ZipInputStream(fileItem.getInputStream());
						ProcessDefinition processDefinition = ProcessDefinition.parseParZipInputStream(zis);
						
						// Check XML form definition
						FileDefinition fileDef = processDefinition.getFileDefinition();
						isForms = fileDef.getInputStream("forms.xml");
						FormUtils.parseWorkflowForms(isForms);
						
						log.debug("Created a processdefinition: {}", processDefinition.getName());
						jbpmContext.deployProcessDefinition(processDefinition);
						return "Deployed process " + processDefinition.getName() + " successfully";
					} finally {
						IOUtils.closeQuietly(isForms);
						IOUtils.closeQuietly(zis);
						jbpmContext.close();
					}
				}
			}
		} else {
			log.warn("Not a multipart request");
			return "Not a multipart request";
		}
	}
}
