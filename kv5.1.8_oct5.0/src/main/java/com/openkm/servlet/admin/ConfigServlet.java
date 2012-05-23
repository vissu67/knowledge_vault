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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.openkm.bean.StoredFile;
import com.openkm.core.DatabaseException;
import com.openkm.dao.ConfigDAO;
import com.openkm.dao.bean.Config;
import com.openkm.jcr.JCRUtils;
import com.openkm.util.SecureStore;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;

/**
 * Execute config servlet
 */
public class ConfigServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(ConfigServlet.class);
	
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
		String action = WebUtils.getString(request, "action");
		Session session = null;
		updateSessionManager(request);
		
		try {
			session = JCRUtils.getSession();
			Map<String, String> types = new LinkedHashMap<String, String>();
			types.put(Config.STRING, "String");
			types.put(Config.TEXT, "Text");
			types.put(Config.BOOLEAN, "Boolean");
			types.put(Config.INTEGER, "Integer");
			types.put(Config.LONG, "Long");
			types.put(Config.FILE, "File");
			
			if (action.equals("create")) {
				create(session, types, request, response);
			} else if (action.equals("edit")) {
				edit(session, types, request, response);
			} else if (action.equals("delete")) {
				delete(session, types, request, response);
			} else if (action.equals("view")) {
				view(session, request, response);
			} else {
				list(session, request, response);
			}
		} catch (LoginException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} finally {
			JCRUtils.logout(session);
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("doPost({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		ServletContext sc = getServletContext();
		String action = WebUtils.getString(request, "action");
		Session session = null;
		updateSessionManager(request);
		
		try {
			if (ServletFileUpload.isMultipartContent(request)) {
				session = JCRUtils.getSession();
				InputStream is = null;
				FileItemFactory factory = new DiskFileItemFactory(); 
				ServletFileUpload upload = new ServletFileUpload(factory);
				List<FileItem> items = upload.parseRequest(request);
				Config cfg = new Config();
				StoredFile stFile = new StoredFile();
				
				for (Iterator<FileItem> it = items.iterator(); it.hasNext();) {
					FileItem item = it.next();
					
					if (item.isFormField()) {
						if (item.getFieldName().equals("action")) {
							action = item.getString("UTF-8");
						} else if (item.getFieldName().equals("cfg_key")) {
							cfg.setKey(item.getString("UTF-8"));
						} else if (item.getFieldName().equals("cfg_type")) {
							cfg.setType(item.getString("UTF-8"));
						} else if (item.getFieldName().equals("cfg_value")) {
							cfg.setValue(item.getString("UTF-8").trim());
						}
					} else {
						is = item.getInputStream();
						stFile.setName(item.getName());
						stFile.setMime(com.openkm.core.Config.mimeTypes.getContentType(item.getName()));
						stFile.setContent(SecureStore.b64Encode(IOUtils.toByteArray(is)));
						is.close();
					}
				}
			
				if (action.equals("create")) {
					if (Config.FILE.equals(cfg.getType())) {
						cfg.setValue(new Gson().toJson(stFile));
					} else if (Config.BOOLEAN.equals(cfg.getType())) {
						cfg.setValue(Boolean.toString(cfg.getValue() != null && !cfg.getValue().equals("")));
					}
					
					ConfigDAO.create(cfg);
					com.openkm.core.Config.reload(sc.getContextPath().substring(1), new Properties());
					
					// Activity log
					UserActivity.log(session.getUserID(), "ADMIN_CONFIG_CREATE", cfg.getKey(), cfg.toString());
					list(session, request, response);
				} else if (action.equals("edit")) {
					if (Config.FILE.equals(cfg.getType())) {
						cfg.setValue(new Gson().toJson(stFile));
					} else if (Config.BOOLEAN.equals(cfg.getType())) {
						cfg.setValue(Boolean.toString(cfg.getValue() != null && !cfg.getValue().equals("")));
					}
					
					ConfigDAO.update(cfg);
					com.openkm.core.Config.reload(sc.getContextPath().substring(1), new Properties());
										
					// Activity log
					UserActivity.log(session.getUserID(), "ADMIN_CONFIG_EDIT", cfg.getKey(), cfg.toString());
					list(session, request, response);
				} else if (action.equals("delete")) {
					ConfigDAO.delete(cfg.getKey());
					com.openkm.core.Config.reload(sc.getContextPath().substring(1), new Properties());
					
					// Activity log
					UserActivity.log(session.getUserID(), "ADMIN_CONFIG_DELETE", cfg.getKey(), null);
					list(session, request, response);
				}
			}
		} catch (LoginException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (FileUploadException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} finally {
			JCRUtils.logout(session);
		}
	}

	/**
	 * Create config
	 */
	private void create(Session session, Map<String, String> types, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException, DatabaseException {
		ServletContext sc = getServletContext();
		Config cfg = new Config();
		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("persist", true);
		sc.setAttribute("types", types);
		sc.setAttribute("cfg", cfg);
		sc.getRequestDispatcher("/admin/config_edit.jsp").forward(request, response);
	}
	
	/**
	 * Edit config
	 */
	private void edit(Session session, Map<String, String> types, HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException, DatabaseException {
		ServletContext sc = getServletContext();
		String cfgKey = WebUtils.getString(request, "cfg_key");
		Config cfg = ConfigDAO.findByPk(cfgKey);
		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("persist", true);
		sc.setAttribute("types", types);
		sc.setAttribute("cfg", cfg);
		sc.getRequestDispatcher("/admin/config_edit.jsp").forward(request, response);
	}

	/**
	 * Delete config
	 */
	private void delete(Session session, Map<String, String> types, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException, DatabaseException {
		ServletContext sc = getServletContext();
		String cfgKey = WebUtils.getString(request, "cfg_key");
		Config cfg = ConfigDAO.findByPk(cfgKey);
		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("persist", true);
		sc.setAttribute("types", types);
		sc.setAttribute("cfg", cfg);
		sc.getRequestDispatcher("/admin/config_edit.jsp").forward(request, response);
	}

	/**
	 * List config
	 */
	private void list(Session session, HttpServletRequest request, HttpServletResponse response) throws
			ServletException, IOException, DatabaseException {
		log.debug("list({}, {}, {})", new Object[] { session, request, response });
		ServletContext sc = getServletContext();
		List<Config> list = ConfigDAO.findAll();
		
		for (Config cfg : list) {
			if (Config.STRING.equals(cfg.getType())) {
				cfg.setType("String");
			} else if (Config.TEXT.equals(cfg.getType())) {
				cfg.setType("Text");
			} else if (Config.BOOLEAN.equals(cfg.getType())) {
				cfg.setType("Boolean");
			} else if (Config.INTEGER.equals(cfg.getType())) {
				cfg.setType("Integer");
			} else if (Config.LONG.equals(cfg.getType())) {
				cfg.setType("Long");
			} else if (Config.FILE.equals(cfg.getType())) {
				cfg.setType("File");
			}
		}
		
		sc.setAttribute("configs", list);
		sc.getRequestDispatcher("/admin/config_list.jsp").forward(request, response);
		log.debug("list: void");
	}
	
	/**
	 * Download file
	 */
	private void view(Session session, HttpServletRequest request, HttpServletResponse response) throws
			ServletException, IOException, DatabaseException {
		log.debug("view({}, {}, {})", new Object[] { session, request, response });
		String cfgKey = WebUtils.getString(request, "cfg_key");
		Config cfg = ConfigDAO.findByPk(cfgKey);
		
		if (cfg != null) {
			StoredFile stFile = new Gson().fromJson(cfg.getValue(), StoredFile.class);
			byte[] content = SecureStore.b64Decode(stFile.getContent());
			ByteArrayInputStream bais = new ByteArrayInputStream(content);
			WebUtils.sendFile(request, response, stFile.getName(), stFile.getMime(), true, bais);
		}
		
		log.debug("view: void");
	}
}
