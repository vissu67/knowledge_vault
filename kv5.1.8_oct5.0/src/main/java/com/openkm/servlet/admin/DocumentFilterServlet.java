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

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.api.OKMPropertyGroup;
import com.openkm.api.OKMWorkflow;
import com.openkm.core.DatabaseException;
import com.openkm.core.ParseException;
import com.openkm.core.WorkflowException;
import com.openkm.dao.DocumentFilterDAO;
import com.openkm.dao.MimeTypeDAO;
import com.openkm.dao.bean.DocumentFilter;
import com.openkm.dao.bean.DocumentFilterRule;
import com.openkm.jcr.JCRUtils;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;

/**
 * Document filter servlet
 */
public class DocumentFilterServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(DocumentFilterServlet.class);
	String types [] = { DocumentFilter.TYPE_PATH, DocumentFilter.TYPE_MIME_TYPE };
	String actions[] = { DocumentFilterRule.ACTION_WIZARD_PROPERTY_GROUP, DocumentFilterRule.ACTION_WIZARD_WORKFLOW,
			DocumentFilterRule.ACTION_WIZARD_CATEGORY, DocumentFilterRule.ACTION_WIZARD_KEYWORD,
			DocumentFilterRule.ACTION_ASSIGN_PROPERTY_GROUP, DocumentFilterRule.ACTION_ASSIGN_WORKFLOW, 
			DocumentFilterRule.ACTION_ADD_CATEGORY, DocumentFilterRule.ACTION_ADD_KEYWORD,
			DocumentFilterRule.ACTION_EXTRACT_METADATA };
	
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
				create(session, request, response);
			} else if (action.equals("edit")) {
				edit(session, request, response);
			} else if (action.equals("delete")) {
				delete(session, request, response);
			} else if (action.equals("ruleList")) {
				ruleList(session, request, response);
			} else if (action.equals("ruleCreate")) {
				ruleCreate(session, request, response);
			} else if (action.equals("ruleEdit")) {
				ruleEdit(session, request, response);
			} else if (action.equals("ruleDelete")) {
				ruleDelete(session, request, response);			
			}
			
			if (action.equals("") || WebUtils.getBoolean(request, "persist")) {
				if (action.startsWith("rule")) {
					ruleList(session, request, response);
				} else {
					list(session, request, response);
				}
			}
		} catch (LoginException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (com.openkm.core.RepositoryException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (WorkflowException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} finally {
			JCRUtils.logout(session);
		}
	}
	
	/**
	 * List document filters
	 */
	private void list(Session session, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException {
		log.debug("list({}, {}, {})", new Object[] { session, request, response });
		ServletContext sc = getServletContext();
		sc.setAttribute("documentFilters", DocumentFilterDAO.findAll(false));
		sc.getRequestDispatcher("/admin/document_filter_list.jsp").forward(request, response);
		log.debug("list: void");
	}
	
	/**
	 * Create document filter 
	 */
	private void create(Session session, HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException, DatabaseException, RepositoryException {
		log.debug("create({}, {}, {})", new Object[] { session, request, response });
		
		if (WebUtils.getBoolean(request, "persist")) {
			DocumentFilter df = new DocumentFilter();
			df.setType(WebUtils.getString(request, "df_type"));
			df.setActive(WebUtils.getBoolean(request, "df_active"));
			
			if (DocumentFilter.TYPE_MIME_TYPE.equals(df.getType())) {
				df.setValue(WebUtils.getString(request, "df_value_mime"));
				
				if (MimeTypeDAO.findByName(df.getValue()) == null) {
					throw new DatabaseException("Mime type not registered");
				}
			} else if (DocumentFilter.TYPE_PATH.equals(df.getType())) {
				df.setValue(WebUtils.getString(request, "df_value_path"));
				
				if (!session.getRootNode().hasNode(df.getValue())) {
					throw new RepositoryException("Node path not found");
				}
			}
			
			DocumentFilterDAO.create(df);
			
			// Activity log
			UserActivity.log(session.getUserID(), "ADMIN_DOCUMENT_FILTER_CREATE", Integer.toString(df.getId()), df.toString());
		} else {
			ServletContext sc = getServletContext();
			DocumentFilter df = new DocumentFilter();
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("types", types);
			sc.setAttribute("mimes", MimeTypeDAO.findAll("mt.name"));
			sc.setAttribute("df", df);
			sc.getRequestDispatcher("/admin/document_filter_edit.jsp").forward(request, response);
		}
		
		log.debug("create: void");
	}
	
	/**
	 * Edit document filter
	 */
	private void edit(Session session, HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException, DatabaseException, RepositoryException {
		log.debug("edit({}, {}, {})", new Object[] { session, request, response });
		
		if (WebUtils.getBoolean(request, "persist")) {
			DocumentFilter df = new DocumentFilter();
			df.setId(WebUtils.getInt(request, "df_id"));
			df.setType(WebUtils.getString(request, "df_type"));
			df.setActive(WebUtils.getBoolean(request, "df_active"));
			
			if (DocumentFilter.TYPE_MIME_TYPE.equals(df.getType())) {
				df.setValue(WebUtils.getString(request, "df_value_mime"));
				
				if (MimeTypeDAO.findByName(df.getValue()) == null) {
					throw new DatabaseException("Mime type not registered");
				}
			} else if (DocumentFilter.TYPE_PATH.equals(df.getType())) {
				df.setValue(WebUtils.getString(request, "df_value_path"));
				
				if (!session.getRootNode().hasNode(df.getValue())) {
					throw new RepositoryException("Node path not found");
				}
			}
			
			DocumentFilterDAO.update(df);
			
			// Activity log
			UserActivity.log(session.getUserID(), "ADMIN_DOCUMENT_FILTER_EDIT", Integer.toString(df.getId()), df.toString());
		} else {
			ServletContext sc = getServletContext();
			int dfId = WebUtils.getInt(request, "df_id");
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("types", types);
			sc.setAttribute("mimes", MimeTypeDAO.findAll("mt.name"));
			sc.setAttribute("df", DocumentFilterDAO.findByPk(dfId));
			sc.getRequestDispatcher("/admin/document_filter_edit.jsp").forward(request, response);
		}
		
		log.debug("edit: void");
	}
	
	/**
	 * Delete filter rule
	 */
	private void delete(Session session, HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException, DatabaseException, RepositoryException {
		log.debug("delete({}, {}, {})", new Object[] { session, request, response });
		
		if (WebUtils.getBoolean(request, "persist")) {
			int dfId = WebUtils.getInt(request, "df_id");
			DocumentFilterDAO.delete(dfId);
			
			// Activity log
			UserActivity.log(session.getUserID(), "ADMIN_DOCUMENT_FILTER_DELETE", Integer.toString(dfId), null);
		} else {
			ServletContext sc = getServletContext();
			int dfId = WebUtils.getInt(request, "df_id");
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("types", types);
			sc.setAttribute("mimes", MimeTypeDAO.findAll("mt.name"));
			sc.setAttribute("df", DocumentFilterDAO.findByPk(dfId));
			sc.getRequestDispatcher("/admin/document_filter_edit.jsp").forward(request, response);
		}
		
		log.debug("delete: void");
	}
	
	/**
	 * List filter rules
	 */
	private void ruleList(Session session, HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException, DatabaseException {
		log.debug("ruleList({}, {}, {})", new Object[] { session, request, response });
		ServletContext sc = getServletContext();
		int dfId = WebUtils.getInt(request, "df_id");
		DocumentFilter df = DocumentFilterDAO.findByPk(dfId);
		sc.setAttribute("df_id", dfId);
		sc.setAttribute("filterRules", df.getFilterRules());
		sc.getRequestDispatcher("/admin/document_filter_rule_list.jsp").forward(request, response);
		log.debug("ruleList: void");
	}
	
	/**
	 * Create filter rule 
	 */
	private void ruleCreate(Session session, HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException, DatabaseException, RepositoryException, ParseException, 
			com.openkm.core.RepositoryException, WorkflowException {
		log.debug("ruleCreate({}, {}, {})", new Object[] { session, request, response });
		
		if (WebUtils.getBoolean(request, "persist")) {
			int df_id = WebUtils.getInt(request, "df_id");
			DocumentFilterRule dfr = new DocumentFilterRule();
			dfr.setAction(WebUtils.getString(request, "dfr_action"));
			dfr.setActive(WebUtils.getBoolean(request, "dfr_active"));
			
			if (DocumentFilterRule.ACTION_WIZARD_PROPERTY_GROUP.equals(dfr.getAction()) || 
					DocumentFilterRule.ACTION_ASSIGN_PROPERTY_GROUP.equals(dfr.getAction())) {
				dfr.setValue(WebUtils.getString(request, "dfr_value_pg"));
			} else if (DocumentFilterRule.ACTION_WIZARD_WORKFLOW.equals(dfr.getAction()) || 
					DocumentFilterRule.ACTION_ASSIGN_WORKFLOW.equals(dfr.getAction())) {
				dfr.setValue(WebUtils.getString(request, "dfr_value_wf"));
			} else if (DocumentFilterRule.ACTION_WIZARD_CATEGORY.equals(dfr.getAction()) || 
					DocumentFilterRule.ACTION_WIZARD_KEYWORD.equals(dfr.getAction()) ||
					DocumentFilterRule.ACTION_EXTRACT_METADATA.equals(dfr.getAction())) {
				dfr.setValue(Boolean.toString(WebUtils.getBoolean(request, "dfr_value_bool")));
			} else if (DocumentFilterRule.ACTION_ADD_CATEGORY.equals(dfr.getAction()) || 
					DocumentFilterRule.ACTION_ADD_KEYWORD.equals(dfr.getAction())) {
				dfr.setValue(WebUtils.getString(request, "dfr_value_str"));
			}
			
			DocumentFilter df = DocumentFilterDAO.findByPk(df_id);
			df.getFilterRules().add(dfr);
			DocumentFilterDAO.update(df);
			
			// Activity log
			UserActivity.log(session.getUserID(), "ADMIN_DOCUMENT_FILTER_RULE_CREATE", Integer.toString(df.getId()), df.toString());
		} else {
			ServletContext sc = getServletContext();
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("df_id", WebUtils.getInt(request, "df_id"));
			sc.setAttribute("dfr",  new DocumentFilterRule());
			sc.setAttribute("actions", actions);
			sc.setAttribute("pgroups", OKMPropertyGroup.getInstance().getAllGroups(null));
			sc.setAttribute("wflows", OKMWorkflow.getInstance().findAllProcessDefinitions(null));
			sc.getRequestDispatcher("/admin/document_filter_rule_edit.jsp").forward(request, response);
		}
		
		log.debug("ruleCreate: void");
	}
	
	/**
	 * Edit filter rule 
	 */
	private void ruleEdit(Session session, HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException, DatabaseException, RepositoryException, ParseException,
			com.openkm.core.RepositoryException, WorkflowException {
		log.debug("ruleEdit({}, {}, {})", new Object[] { session, request, response });
		
		if (WebUtils.getBoolean(request, "persist")) {
			int dfrId = WebUtils.getInt(request, "dfr_id");
			DocumentFilterRule dfr = DocumentFilterDAO.findRuleByPk(dfrId);
			
			if (dfr != null) {
				dfr.setAction(WebUtils.getString(request, "dfr_action"));
				dfr.setActive(WebUtils.getBoolean(request, "dfr_active"));
				
				if (DocumentFilterRule.ACTION_WIZARD_PROPERTY_GROUP.equals(dfr.getAction()) || 
						DocumentFilterRule.ACTION_ASSIGN_PROPERTY_GROUP.equals(dfr.getAction())) {
					dfr.setValue(WebUtils.getString(request, "dfr_value_pg"));
				} else if (DocumentFilterRule.ACTION_WIZARD_WORKFLOW.equals(dfr.getAction()) || 
						DocumentFilterRule.ACTION_ASSIGN_WORKFLOW.equals(dfr.getAction())) {
					dfr.setValue(WebUtils.getString(request, "dfr_value_wf"));
				} else if (DocumentFilterRule.ACTION_WIZARD_CATEGORY.equals(dfr.getAction()) || 
						DocumentFilterRule.ACTION_WIZARD_KEYWORD.equals(dfr.getAction()) ||
						DocumentFilterRule.ACTION_EXTRACT_METADATA.equals(dfr.getAction())) {
					dfr.setValue(Boolean.toString(WebUtils.getBoolean(request, "dfr_value_bool")));
				} else if (DocumentFilterRule.ACTION_ADD_CATEGORY.equals(dfr.getAction()) || 
						DocumentFilterRule.ACTION_ADD_KEYWORD.equals(dfr.getAction())) {
					dfr.setValue(WebUtils.getString(request, "dfr_value_str"));
				}
				
				DocumentFilterDAO.updateRule(dfr);
			}
			
			// Activity log
			UserActivity.log(session.getUserID(), "ADMIN_DOCUMENT_FILTER_RULE_EDIT", Integer.toString(dfr.getId()), dfr.toString());
		} else {
			ServletContext sc = getServletContext();
			int dfrId = WebUtils.getInt(request, "dfr_id");
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("df_id", WebUtils.getInt(request, "df_id"));
			sc.setAttribute("dfr", DocumentFilterDAO.findRuleByPk(dfrId));
			sc.setAttribute("actions", actions);
			sc.setAttribute("pgroups", OKMPropertyGroup.getInstance().getAllGroups(null));
			sc.setAttribute("wflows", OKMWorkflow.getInstance().findAllProcessDefinitions(null));
			sc.getRequestDispatcher("/admin/document_filter_rule_edit.jsp").forward(request, response);
		}
		
		log.debug("ruleEdit: void");
	}
	
	/**
	 * Delete filter rule 
	 */
	private void ruleDelete(Session session, HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException, DatabaseException, ParseException,
			com.openkm.core.RepositoryException, WorkflowException {
		log.debug("ruleDelete({}, {}, {})", new Object[] { session, request, response });
		
		if (WebUtils.getBoolean(request, "persist")) {
			int dfrId = WebUtils.getInt(request, "dfr_id");
			DocumentFilterDAO.deleteRule(dfrId);
			
			// Activity log
			UserActivity.log(session.getUserID(), "ADMIN_DOCUMENT_FILTER_RULE_DELETE", Integer.toString(dfrId), null);
		} else {
			ServletContext sc = getServletContext();
			int dfrId = WebUtils.getInt(request, "dfr_id");
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("df_id", WebUtils.getInt(request, "df_id"));
			sc.setAttribute("dfr", DocumentFilterDAO.findRuleByPk(dfrId));
			sc.setAttribute("actions", actions);
			sc.setAttribute("pgroups", OKMPropertyGroup.getInstance().getAllGroups(null));
			sc.setAttribute("wflows", OKMWorkflow.getInstance().findAllProcessDefinitions(null));
			sc.getRequestDispatcher("/admin/document_filter_rule_edit.jsp").forward(request, response);
		}
		
		log.debug("ruleDelete: void");
	}
}
