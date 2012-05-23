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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Document;
import com.openkm.bean.Folder;
import com.openkm.bean.Repository;
import com.openkm.jcr.JCRUtils;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;

/**
 * Data browser servlet
 */
public class DataBrowserServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(DataBrowserServlet.class);
	
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
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws 
			ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		updateSessionManager(request);
		Session session = null;
		
		try {
			session = JCRUtils.getSession();
			
			if (action.equals("fs")) {
				fileSystemList(request, response);
			} else if (action.equals("repo")) {
				repositoryList(session, request, response);
			}
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} finally {
			JCRUtils.logout(session);
		}
	}
	
	/**
	 * File system list
	 */
	private void fileSystemList(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("fileSystemList({}, {})", request, response);
		String path = WebUtils.getString(request, "path", System.getProperty("user.home"));
		String dst = WebUtils.getString(request, "dst");
		File dir = new File(path);
		List<Map<String, String>> folders = new ArrayList<Map<String, String>>();
		List<Map<String, String>> documents = new ArrayList<Map<String, String>>();
		
		// Add parent folder link
		if (!Arrays.asList(File.listRoots()).contains(dir)) {
			Map<String, String> item = new HashMap<String, String>();
			File parent = dir.getParentFile();
			item.put("name", "&lt;PARENT FOLDER&gt;");
			item.put("path", parent.getPath());
			folders.add(item);
		}
		
		for (File f : dir.listFiles()) {
			Map<String, String> item = new HashMap<String, String>();
			
			if (f.isDirectory() && !f.isHidden()) {
				item.put("name", f.getName());
				item.put("path", f.getPath());
				folders.add(item);
			} else if (f.isFile() && !f.isHidden()) {
				item.put("name", f.getName());
				item.put("path", f.getPath());
				documents.add(item);
			}
		}
		
		ServletContext sc = getServletContext();
		sc.setAttribute("action", "fs");
		sc.setAttribute("path", path);
		sc.setAttribute("dst", dst);
		sc.setAttribute("folders", folders);
		sc.setAttribute("documents", documents);
		sc.getRequestDispatcher("/admin/data_browser.jsp").forward(request, response);
		
		// Activity log
		UserActivity.log(request.getRemoteUser(), "ADMIN_FILESYSTEM_LIST", path, null);
		log.debug("fileSystemList: void");
	}
	
	/**
	 * File system list
	 */
	private void repositoryList(Session session, HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException, PathNotFoundException, RepositoryException {
		log.debug("repositoryList({}, {})", request, response);
		Node root = session.getRootNode();
		String path = WebUtils.getString(request, "path", root.getNode(Repository.ROOT).getPath());
		String dst = WebUtils.getString(request, "dst");
		List<Map<String, String>> folders = new ArrayList<Map<String, String>>();
		List<Map<String, String>> documents = new ArrayList<Map<String, String>>();
		Node base = null;
		
		if ("/".equals(path)) {
			base = session.getRootNode();
		} else {
			base = session.getRootNode().getNode(path.substring(1));
			
			// Add parent folder link
			Map<String, String> item = new HashMap<String, String>();
			item.put("name", "&lt;PARENT FOLDER&gt;");
			item.put("path", base.getParent().getPath());
			folders.add(item);
		}
		
		for (NodeIterator ni = base.getNodes(); ni.hasNext(); ) {
			Node child = ni.nextNode();
			
			if (child.isNodeType(Folder.TYPE)) {
				Map<String, String> item = new HashMap<String, String>();
				item.put("name", child.getName());
				item.put("path", child.getPath());
				folders.add(item);
			} else if (child.isNodeType(Document.TYPE)) {
				Map<String, String> item = new HashMap<String, String>();
				item.put("name", child.getName());
				item.put("path", child.getPath());
				documents.add(item);
			}
		}
		
		ServletContext sc = getServletContext();
		sc.setAttribute("action", "repo");
		sc.setAttribute("path", path);
		sc.setAttribute("dst", dst);
		sc.setAttribute("folders", folders);
		sc.setAttribute("documents", documents);
		sc.getRequestDispatcher("/admin/data_browser.jsp").forward(request, response);
		
		// Activity log
		UserActivity.log(request.getRemoteUser(), "ADMIN_REPOSITORY_LIST", path, null);
		log.debug("repositoryList: void");
	}
}
