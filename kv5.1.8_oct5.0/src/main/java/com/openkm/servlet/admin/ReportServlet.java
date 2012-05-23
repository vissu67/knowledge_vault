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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bsh.EvalError;

import com.openkm.bean.form.FormElement;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.ParseException;
import com.openkm.dao.ReportDAO;
import com.openkm.dao.bean.Report;
import com.openkm.jcr.JCRUtils;
import com.openkm.util.FormUtils;
import com.openkm.util.ReportUtils;
import com.openkm.util.SecureStore;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;

/**
 * Execute report servlet
 */
public class ReportServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(ReportServlet.class);
	private static Map<String, String> types = new LinkedHashMap<String, String>();
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("doGet({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		Session session = null;
		updateSessionManager(request);
		
		try {
			session = JCRUtils.getSession();
			
			if (action.equals("create")) {
				ServletContext sc = getServletContext();
				Report rp = new Report();
				sc.setAttribute("action", action);
				sc.setAttribute("types", types);
				sc.setAttribute("rp", rp);
				sc.getRequestDispatcher("/admin/report_edit.jsp").forward(request, response);
			} else if (action.equals("edit")) {
				ServletContext sc = getServletContext();
				int rpId = WebUtils.getInt(request, "rp_id");
				Report rp = ReportDAO.findByPk(rpId);
				sc.setAttribute("action", action);
				sc.setAttribute("types", types);
				sc.setAttribute("rp", rp);
				sc.getRequestDispatcher("/admin/report_edit.jsp").forward(request, response);
			} else if (action.equals("delete")) {
				ServletContext sc = getServletContext();
				int rpId = WebUtils.getInt(request, "rp_id");
				Report rp = ReportDAO.findByPk(rpId);
				sc.setAttribute("action", action);
				sc.setAttribute("types", types);
				sc.setAttribute("rp", rp);
				sc.getRequestDispatcher("/admin/report_edit.jsp").forward(request, response);
			} else if (action.equals("paramList")) {
				paramList(session, request, response);
			} else if (action.equals("getParams")) {
				getParams(session, request, response);
			} else if (action.equals("execute")) {
				execute(session, request, response);
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
		} catch (JRException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (EvalError e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request, response, e);
		} finally {
			JCRUtils.logout(session);
		}
	}

	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("doPost({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String action = "";
		Session session = null;
		updateSessionManager(request);
		
		try {
			if (ServletFileUpload.isMultipartContent(request)) {
				session = JCRUtils.getSession();
				InputStream is = null;
				FileItemFactory factory = new DiskFileItemFactory(); 
				ServletFileUpload upload = new ServletFileUpload(factory);
				List<FileItem> items = upload.parseRequest(request);
				Report rp = new Report();
				
				for (Iterator<FileItem> it = items.iterator(); it.hasNext();) {
					FileItem item = it.next();
					
					if (item.isFormField()) {
						if (item.getFieldName().equals("action")) {
							action = item.getString("UTF-8");
						} else if (item.getFieldName().equals("rp_id")) {
							rp.setId(Integer.parseInt(item.getString("UTF-8")));
						} else if (item.getFieldName().equals("rp_name")) {
							rp.setName(item.getString("UTF-8"));
						} else if (item.getFieldName().equals("rp_active")) {
							rp.setActive(true);
						}
					} else {
						is = item.getInputStream();
						rp.setFileName(FilenameUtils.getName(item.getName()));
						rp.setFileMime(Config.mimeTypes.getContentType(item.getName()));
						rp.setFileContent(SecureStore.b64Encode(IOUtils.toByteArray(is)));
						is.close();
					}
				}
			
				if (action.equals("create")) {
					int id = ReportDAO.create(rp);
					
					// Activity log
					UserActivity.log(session.getUserID(), "ADMIN_REPORT_CREATE", Integer.toString(id), rp.toString());
					list(session, request, response);
				} else if (action.equals("edit")) {
					Report tmp = ReportDAO.findByPk(rp.getId());
					tmp.setActive(rp.isActive());
					tmp.setFileContent(rp.getFileContent());
					tmp.setFileMime(rp.getFileMime());
					tmp.setFileName(rp.getFileName());
					tmp.setName(rp.getName());
					ReportDAO.update(tmp);
					
					// Activity log
					UserActivity.log(session.getUserID(), "ADMIN_REPORT_EDIT", Integer.toString(rp.getId()), rp.toString());
					list(session, request, response);
				} else if (action.equals("delete")) {
					ReportDAO.delete(rp.getId());
					
					// Activity log
					UserActivity.log(session.getUserID(), "ADMIN_REPORT_DELETE", Integer.toString(rp.getId()), null);
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
			sendErrorRedirect(request, response, e);
		} finally {
			JCRUtils.logout(session);
		}
	}
	
	/**
	 * List registered reports
	 */
	private void list(Session session, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException {
		log.debug("list({}, {}, {})", new Object[] { session, request, response });
		ServletContext sc = getServletContext();
		List<Report> list = ReportDAO.findAll();
		sc.setAttribute("reports", list);
		sc.getRequestDispatcher("/admin/report_list.jsp").forward(request, response);
		log.debug("list: void");
	}
	
	/**
	 * Show report parameters, previous step to execution
	 */
	private void getParams(Session session, HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException, DatabaseException, ParseException {	
		log.debug("getParams({}, {}, {})", new Object[] { session, request, response });
		ServletContext sc = getServletContext();
		int rpId = WebUtils.getInt(request, "rp_id");
		List<FormElement> params = ReportUtils.getReportParameters(rpId);
		
		sc.setAttribute("rp_id", rpId);
		sc.setAttribute("params", params);
		sc.setAttribute("ReportUtil", new ReportUtils());
		sc.getRequestDispatcher("/admin/report_get_params.jsp").forward(request, response);
		log.debug("getParams: void");
	}
	
	/**
	 * Execute report
	 */
	private void execute(Session session, HttpServletRequest request, HttpServletResponse response) throws 
			IOException, DatabaseException, JRException, EvalError, ParseException {
		log.debug("execute({}, {}, {})", new Object[] { session, request, response });
		int rpId = WebUtils.getInt(request, "rp_id");
		int format = WebUtils.getInt(request, "format",  ReportUtils.OUTPUT_PDF);
		Report rp = ReportDAO.findByPk(rpId);
		
		// Set file name
		String fileName = rp.getFileName().substring(0, rp.getFileName().indexOf('.')) + ReportUtils.FILE_EXTENSION[format];
		
		// Set default report parameters
		Map<String, String> params = new HashMap<String, String>();
		String host = com.openkm.core.Config.APPLICATION_URL;
		params.put("host", host.substring(0, host.lastIndexOf("/") + 1));
		
		for (FormElement fe : ReportUtils.getReportParameters(rpId)) {
			params.put(fe.getName(), WebUtils.getString(request, fe.getName()));
		}
		
		ByteArrayOutputStream baos = null;	
		ByteArrayInputStream bais = null;
		
		try {
			baos = ReportUtils.execute(rp, params, format);
			bais = new ByteArrayInputStream(baos.toByteArray());
			WebUtils.sendFile(request, response, fileName, ReportUtils.FILE_MIME[format], false, bais);
		} finally {
			IOUtils.closeQuietly(bais);
			IOUtils.closeQuietly(baos);
		}
		
		// Activity log
		UserActivity.log(request.getRemoteUser(), "ADMIN_REPORT_EXECUTE", Integer.toString(rpId), rp.toString());
		log.debug("execute: void");
	}
	
	/**
	 * List reports parameters
	 */
	private void paramList(Session session, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException, ParseException {
		log.debug("paramList({}, {}, {})", new Object[] { session, request, response });
		ServletContext sc = getServletContext();
		int rpId = WebUtils.getInt(request, "rp_id");
		List<FormElement> params = ReportUtils.getReportParameters(rpId);
		List<Map<String, String>> fMaps = new ArrayList<Map<String,String>>();
		
		for (FormElement fe : params) {
			fMaps.add(FormUtils.toString(fe));
		}
		
		sc.setAttribute("rp_id", rpId);
		sc.setAttribute("params", fMaps);
		sc.getRequestDispatcher("/admin/report_param_list.jsp").forward(request, response);
		log.debug("paramList: void");
	}
}
