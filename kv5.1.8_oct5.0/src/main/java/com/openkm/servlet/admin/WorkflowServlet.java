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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jcr.Session;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.api.OKMAuth;
import com.openkm.api.OKMDocument;
import com.openkm.api.OKMWorkflow;
import com.openkm.bean.form.FormElement;
import com.openkm.bean.workflow.ProcessInstance;
import com.openkm.bean.workflow.TaskInstance;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.ParseException;
import com.openkm.core.RepositoryException;
import com.openkm.core.WorkflowException;
import com.openkm.jcr.JCRUtils;
import com.openkm.principal.PrincipalAdapterException;
import com.openkm.util.FormUtils;
import com.openkm.util.FormatUtil;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;

/**
 * RepositoryView servlet
 */
public class WorkflowServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(WorkflowServlet.class);
	private static Map<String, String> statusFilterValues = new LinkedHashMap<String, String>();
	
	static {
		statusFilterValues.put("0", "All");
		statusFilterValues.put("1", "Running");
		statusFilterValues.put("2", "Ended");
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("doGet({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		Session session = null;
		updateSessionManager(request);
		
		try {
			session = JCRUtils.getSession();
			
			if (action.equals("processDefinitionDelete")) {
				processDefinitionDelete(session, request, response);
				processDefinitionList(session, request, response);
			} else if (action.equals("processDefinitionView")) {
				processDefinitionView(session, request, response);
			} else if (action.equals("processInstanceView")) {
				processInstanceView(session, request, response);
			} else if (action.equals("processInstanceDelete")) {
				processInstanceDelete(session, request, response);
				processDefinitionView(session, request, response);
			} else if (action.equals("processInstanceEnd")) {
				processInstanceEnd(session, request, response);
				processDefinitionView(session, request, response);
			} else if (action.equals("processInstanceResume")) {
				processInstanceResume(session, request, response);
				processDefinitionView(session, request, response);
			} else if (action.equals("processInstanceSuspend")) {
				processInstanceSuspend(session, request, response);
				processDefinitionView(session, request, response);
			} else if (action.equals("processInstanceVariableDelete")) {
				processInstanceVariableDelete(session, request, response);
				processInstanceView(session, request, response);
			} else if (action.equals("processInstanceVariableAdd")) {
				processInstanceVariableAdd(session, request, response);
				processInstanceView(session, request, response);
			} else if (action.equals("taskInstanceSetActor")) {
				taskInstanceSetActor(session, request, response);
				processInstanceView(session, request, response);
			} else if (action.equals("taskInstanceSuspend")) {
				taskInstanceSuspend(session, request, response);
				processInstanceView(session, request, response);
			} else if (action.equals("taskInstanceResume")) {
				taskInstanceResume(session, request, response);
				processInstanceView(session, request, response);
			} else if (action.equals("taskInstanceStart")) {
				taskInstanceStart(session, request, response);
				processInstanceView(session, request, response);
			} else if (action.equals("taskInstanceEnd")) {
				taskInstanceEnd(session, request, response);
				if (request.getParameter("transition") == null) {
					processInstanceView(session, request, response);
				} else {
					taskInstanceView(session, request, response);
				}
			} else if (action.equals("taskInstanceView")) {
				taskInstanceView(session, request, response);
			} else if (action.equals("taskInstanceAddComment")) {
				taskInstanceAddComment(session, request, response);
				taskInstanceView(session, request, response);
			} else if (action.equals("taskInstanceVariableDelete")) {
				taskInstanceVariableDelete(session, request, response);
				taskInstanceView(session, request, response);
			} else if (action.equals("taskInstanceVariableAdd")) {
				taskInstanceVariableAdd(session, request, response);
				taskInstanceView(session, request, response);
			} else if (action.equals("tokenView")) {
				tokenView(session, request, response);
			} else if (action.equals("tokenEnd")) {
				tokenEnd(session, request, response);
				processInstanceView(session, request, response);
			} else if (action.equals("tokenSuspend")) {
				tokenSuspend(session, request, response);
				processInstanceView(session, request, response);
			} else if (action.equals("tokenResume")) {
				tokenResume(session, request, response);
				processInstanceView(session, request, response);
			} else if (action.equals("processInstanceAddComment")) {
				processInstanceAddComment(session, request, response);
				processInstanceView(session, request, response);
			} else if (action.equals("tokenSignal")) {
				tokenSignal(session, request, response);
				tokenView(session, request, response);
			} else if (action.equals("tokenSetNode")) {
				tokenSetNode(session, request, response);
				tokenView(session, request, response);
			} else {
				processDefinitionList(session, request, response);
			}
		} catch (javax.jcr.LoginException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (WorkflowException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} finally {
			JCRUtils.logout(session);
		}
	}

	/**
	 * List all process definitions
	 */
	private void processDefinitionList(Session session, HttpServletRequest request, HttpServletResponse response) throws 
			ServletException, IOException, com.openkm.core.RepositoryException, DatabaseException, WorkflowException {
		log.debug("listProcessDefinition({}, {}, {})", new Object[] { session, request, response });
		ServletContext sc = getServletContext();
		sc.setAttribute("processDefinitions", OKMWorkflow.getInstance().findAllProcessDefinitions(null));
		sc.getRequestDispatcher("/admin/process_definition_list.jsp").forward(request, response);
		log.debug("listProcessDefinition: void");
	}
	
	/**
	 * Delete a process definition
	 */
	private void processDefinitionDelete(Session session, HttpServletRequest request, 
			HttpServletResponse response) throws RepositoryException, DatabaseException, WorkflowException {
		log.debug("deleteProcessDefinition({}, {}, {})", new Object[] { session, request, response });
		long pdid = WebUtils.getLong(request, "pdid");
		OKMWorkflow.getInstance().deleteProcessDefinition(null, pdid);
		
		// Activity log
		UserActivity.log(session.getUserID(), "ADMIN_PROCESS_DEFINITION_DELETE", Long.toString(pdid), null);
		log.debug("deleteProcessDefinition: void");
	}
	
	/**
	 * View process definition
	 */
	private void processDefinitionView(Session session, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, RepositoryException, DatabaseException, WorkflowException,
			ParseException {
		log.debug("viewProcessDefinition({}, {}, {})", new Object[] { session, request, response });
		ServletContext sc = getServletContext();
		long pdid = WebUtils.getLong(request, "pdid");
		int statusFilter = WebUtils.getInt(request, "statusFilter", 1);
		Map<String, List<FormElement>> procDefForms = OKMWorkflow.getInstance().getProcessDefinitionForms(null, pdid);
		Map<String, List<Map<String, String>>> pdf = new HashMap<String, List<Map<String,String>>>();
		
		for (String key : procDefForms.keySet()) {
			List<Map<String, String>> value = new ArrayList<Map<String, String>>();
			
			for (FormElement fe : procDefForms.get(key)) {
				value.add(FormUtils.toString(fe));
			}
			
			pdf.put(key, value);
		}
		
		// Filter process instances by status
		List<ProcessInstance> processInstances = new ArrayList<ProcessInstance>();
		
		for (ProcessInstance pi : OKMWorkflow.getInstance().findProcessInstances(null, pdid)) {
			if (statusFilter == 1) { // Running
				if (pi.getEnd() == null && !pi.isSuspended()) {
					processInstances.add(pi);
				}
			} else if (statusFilter == 2) { // Ended
				if (pi.getEnd() != null && !pi.isSuspended()) {
					processInstances.add(pi);
				}
			} else { // All
				processInstances.add(pi);
			}
		}
		
		sc.setAttribute("processDefinition", OKMWorkflow.getInstance().getProcessDefinition(null, pdid));
		sc.setAttribute("processInstances", processInstances);
		sc.setAttribute("processDefinitionForms", pdf);
		sc.setAttribute("statusFilterValues", statusFilterValues);
		sc.setAttribute("statusFilter", statusFilter);
		sc.getRequestDispatcher("/admin/process_definition_view.jsp").forward(request, response);
		log.debug("viewProcessDefinition: void");
	}
	
	/**
	 * View process instance
	 */
	private void processInstanceView(Session session, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, RepositoryException, DatabaseException, WorkflowException,
			PrincipalAdapterException, AccessDeniedException {
		log.debug("processInstanceView({}, {}, {})", new Object[] { session, request, response });
		ServletContext sc = getServletContext();
		long piid = WebUtils.getLong(request, "piid");
		ProcessInstance pi = OKMWorkflow.getInstance().getProcessInstance(null, piid);
		Map<String, String> vars = new HashMap<String, String>();
		
		for (Entry<String, Object> entry : pi.getVariables().entrySet()) {
			vars.put(entry.getKey(), FormatUtil.formatObject(entry.getValue()));
			
			if (entry.getKey().equals(Config.WORKFLOW_PROCESS_INSTANCE_VARIABLE_UUID)) {
				vars.put(Config.WORKFLOW_PROCESS_INSTANCE_VARIABLE_PATH, 
						OKMDocument.getInstance().getPath(null, entry.getValue().toString()));
			} 
		}
		
		sc.setAttribute("variables", vars);
		sc.setAttribute("processInstance", pi);
		sc.setAttribute("taskInstances", OKMWorkflow.getInstance().findTaskInstances(null, piid));
		sc.setAttribute("users", OKMAuth.getInstance().getUsers(null));
		sc.getRequestDispatcher("/admin/process_instance_view.jsp").forward(request, response);
		log.debug("processInstanceView: void");
	}
	
	/**
	 * Delete process instance
	 */
	private void processInstanceDelete(Session session, HttpServletRequest request, 
			HttpServletResponse response) throws RepositoryException, DatabaseException, WorkflowException {
		log.debug("processInstanceDelete({}, {}, {})", new Object[] { session, request, response });
		long piid = WebUtils.getLong(request, "piid");
		OKMWorkflow.getInstance().deleteProcessInstance(null, piid);
		
		// Activity log
		UserActivity.log(session.getUserID(), "ADMIN_PROCESS_INSTANCE_DELETE", Long.toString(piid), null);
		log.debug("processInstanceDelete: void");
	}
	
	/**
	 * End process instance
	 */
	private void processInstanceEnd(Session session, HttpServletRequest request, 
			HttpServletResponse response) throws RepositoryException, DatabaseException, WorkflowException {
		log.debug("processInstanceEnd({}, {}, {})", new Object[] { session, request, response });
		long piid = WebUtils.getLong(request, "piid");
		OKMWorkflow.getInstance().endProcessInstance(null, piid);
		
		// Activity log
		UserActivity.log(session.getUserID(), "ADMIN_PROCESS_INSTANCE_END", Long.toString(piid), null);
		log.debug("processInstanceEnd: void");
	}
	
	/**
	 * Resume process instance
	 */
	private void processInstanceResume(Session session, HttpServletRequest request, 
			HttpServletResponse response) throws RepositoryException, DatabaseException, WorkflowException {
		log.debug("processInstanceResume({}, {}, {})", new Object[] { session, request, response });
		long piid = WebUtils.getLong(request, "piid");
		OKMWorkflow.getInstance().resumeProcessInstance(null, piid);
		
		// Activity log
		UserActivity.log(session.getUserID(), "ADMIN_PROCESS_INSTANCE_RESUME", Long.toString(piid), null);
		log.debug("processInstanceResume: void");
	}
	
	/**
	 * Suspend process instance
	 */
	private void processInstanceSuspend(Session session, HttpServletRequest request, 
			HttpServletResponse response) throws RepositoryException, DatabaseException, WorkflowException {
		log.debug("processInstanceSuspend({}, {}, {})", new Object[] { session, request, response });
		long piid = WebUtils.getLong(request, "piid");
		OKMWorkflow.getInstance().suspendProcessInstance(null, piid);
		
		// Activity log
		UserActivity.log(session.getUserID(), "ADMIN_PROCESS_INSTANCE_SUSPEND", Long.toString(piid), null);
		log.debug("processInstanceSuspend: void");
	}
	
	/**
	 * Add comment to process instance
	 */
	private void processInstanceAddComment(Session session, HttpServletRequest request,
			HttpServletResponse response) throws DatabaseException, WorkflowException, RepositoryException {
		log.debug("processInstanceAddComment({}, {}, {})", new Object[] { session, request, response });
		long tid = WebUtils.getLong(request, "tid");
		String message = WebUtils.getString(request, "message");
		
		if (!message.equals("")) {
			OKMWorkflow.getInstance().addTokenComment(null, tid, message);
		}
		
		// Activity log
		UserActivity.log(session.getUserID(), "ADMIN_PROCESS_INSTANCE_ADD_COMMENT", Long.toString(tid), null);
		log.debug("processInstanceAddComment: void");
	}
	
	/**
	 * Delete process instance variable
	 */
	private void processInstanceVariableDelete(Session session, HttpServletRequest request,
			HttpServletResponse response) throws RepositoryException, DatabaseException, WorkflowException {
		log.debug("processInstanceVariableDelete({}, {}, {})", new Object[] { session, request, response });
		long piid = WebUtils.getLong(request, "piid");
		String name = WebUtils.getString(request, "name");
		OKMWorkflow.getInstance().deleteProcessInstanceVariable(null, piid, name);
		
		// Activity log
		UserActivity.log(session.getUserID(), "ADMIN_PROCESS_INSTANCE_VARIABLE_DELETE", Long.toString(piid), null);
		log.debug("processInstanceVariableDelete: void");
	}

	/**
	 * Add process instance variable
	 */
	private void processInstanceVariableAdd(Session session, HttpServletRequest request,
			HttpServletResponse response) throws RepositoryException, DatabaseException, WorkflowException {
		log.debug("processInstanceVariableAdd({}, {}, {})", new Object[] { session, request, response });
		long piid = WebUtils.getLong(request, "piid");
		String name = WebUtils.getString(request, "name");
		String value= WebUtils.getString(request, "value");
		OKMWorkflow.getInstance().addProcessInstanceVariable(null, piid, name, value);
		
		// Activity log
		UserActivity.log(session.getUserID(), "ADMIN_PROCESS_INSTANCE_VARIABLE_ADD", Long.toString(piid), 
				name + "=" + value);
		log.debug("processInstanceVariableAdd: void");
	}
	
	/**
	 * Set task instance actor
	 */
	private void taskInstanceSetActor(Session session, HttpServletRequest request, 
			HttpServletResponse response) throws RepositoryException, DatabaseException, WorkflowException {
		log.debug("taskInstanceSetActor({}, {}, {})", new Object[] { session, request, response });
		long tiid = WebUtils.getLong(request, "tiid");
		String actor = WebUtils.getString(request, "actor");
		OKMWorkflow.getInstance().setTaskInstanceActorId(null, tiid, actor);
		
		// Activity log
		UserActivity.log(session.getUserID(), "ADMIN_TASK_INSTANCE_SET_ACTOR", Long.toString(tiid), actor);
		log.debug("taskInstanceSetActor: void");
	}
	
	/**
	 * View task instance
	 */
	private void taskInstanceView(Session session, HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException, RepositoryException, DatabaseException, WorkflowException,
			ParseException, AccessDeniedException {
		log.debug("taskInstanceView({}, {}, {})", new Object[] { session, request, response });
		ServletContext sc = getServletContext();
		long tiid = WebUtils.getLong(request, "tiid");
		TaskInstance ti = OKMWorkflow.getInstance().getTaskInstance(null, tiid);
		Map<String, List<FormElement>> procDefForms = OKMWorkflow.getInstance().getProcessDefinitionForms(null, ti.getProcessInstance().getProcessDefinition().getId());
		List<Map<String, String>> pdf = new ArrayList<Map<String,String>>();
		Map<String, String> vars = new HashMap<String, String>();
		List<FormElement> fes = procDefForms.get(ti.getName());
		
		if (fes != null) {
			for (FormElement fe : fes) {
				pdf.add(FormUtils.toString(fe));
			}
		}
		
		for (Entry<String, Object> entry : ti.getVariables().entrySet()) {
			vars.put(entry.getKey(), FormatUtil.formatObject(entry.getValue()));
			
			if (entry.getKey().equals(Config.WORKFLOW_PROCESS_INSTANCE_VARIABLE_UUID)) {
				vars.put(Config.WORKFLOW_PROCESS_INSTANCE_VARIABLE_PATH, 
						OKMDocument.getInstance().getPath(null, entry.getValue().toString()));
			} 
		}
		
		sc.setAttribute("variables", vars);
		sc.setAttribute("taskInstance", ti);
		sc.setAttribute("taskInstanceForm", pdf);
		sc.getRequestDispatcher("/admin/task_instance_view.jsp").forward(request, response);
		log.debug("taskInstanceView: void");
	}

	/**
	 * Start task instance
	 */
	private void taskInstanceStart(Session session, HttpServletRequest request, HttpServletResponse response)
			throws RepositoryException, DatabaseException, WorkflowException {
		log.debug("taskInstanceStart({}, {}, {})", new Object[] { session, request, response });
		long tiid = WebUtils.getLong(request, "tiid");
		OKMWorkflow.getInstance().startTaskInstance(null, tiid);
		
		// Activity log
		UserActivity.log(session.getUserID(), "ADMIN_TASK_INSTANCE_START", Long.toString(tiid), null);
		log.debug("taskInstanceStart: void");
	}
	
	/**
	 * End task instance
	 */
	private void taskInstanceEnd(Session session, HttpServletRequest request, HttpServletResponse response) 
			throws RepositoryException, DatabaseException, WorkflowException {
		log.debug("taskInstanceEnd({}, {}, {})", new Object[] { session, request, response });
		long tiid = WebUtils.getLong(request, "tiid");
		String transition = WebUtils.getString(request, "transition", null);
		OKMWorkflow.getInstance().endTaskInstance(null, tiid, transition);
		
		// Activity log
		UserActivity.log(session.getUserID(), "ADMIN_TASK_INSTANCE_END", Long.toString(tiid), null);
		log.debug("taskInstanceEnd: void");
	}

	/**
	 * Suspend task instance
	 */
	private void taskInstanceSuspend(Session session, HttpServletRequest request, 
			HttpServletResponse response) throws RepositoryException, DatabaseException, WorkflowException {
		log.debug("taskInstanceSuspend({}, {}, {})", new Object[] { session, request, response });
		long tiid = WebUtils.getLong(request, "tiid");
		OKMWorkflow.getInstance().suspendTaskInstance(null, tiid);
		
		// Activity log
		UserActivity.log(session.getUserID(), "ADMIN_TASK_INSTANCE_SUSPEND", Long.toString(tiid), null);
		log.debug("taskInstanceSuspend: void");
	}
	
	/**
	 * Add comment to task instance
	 */
	private void taskInstanceAddComment(Session session, HttpServletRequest request,
			HttpServletResponse response) throws DatabaseException, WorkflowException, RepositoryException {
		log.debug("processInstanceAddComment({}, {}, {})", new Object[] { session, request, response });
		long tiid = WebUtils.getLong(request, "tiid");
		String message = WebUtils.getString(request, "message");
		
		if (!message.equals("")) {
			OKMWorkflow.getInstance().addTaskInstanceComment(null, tiid, message);
		}
		
		// Activity log
		UserActivity.log(session.getUserID(), "ADMIN_TASK_INSTANCE_ADD_COMMENT", Long.toString(tiid), null);
		log.debug("processInstanceAddComment: void");
	}
	
	/**
	 * Delete task instance variable
	 */
	private void taskInstanceVariableDelete(Session session, HttpServletRequest request,
			HttpServletResponse response) throws RepositoryException, DatabaseException, WorkflowException {
		log.debug("taskInstanceVariableDelete({}, {}, {})", new Object[] { session, request, response });
		long tiid = WebUtils.getLong(request, "tiid");
		String name = WebUtils.getString(request, "name");
		OKMWorkflow.getInstance().deleteTaskInstanceVariable(null, tiid, name);
		
		// Activity log
		UserActivity.log(session.getUserID(), "ADMIN_TASK_INSTANCE_VARIABLE_DELETE", Long.toString(tiid), null);
		log.debug("taskInstanceVariableDelete: void");
	}

	/**
	 * Add task instance variable
	 */
	private void taskInstanceVariableAdd(Session session, HttpServletRequest request,
			HttpServletResponse response) throws RepositoryException, DatabaseException, WorkflowException {
		log.debug("taskInstanceVariableAdd({}, {}, {})", new Object[] { session, request, response });
		long tiid = WebUtils.getLong(request, "tiid");
		String name = WebUtils.getString(request, "name");
		String value= WebUtils.getString(request, "value");
		OKMWorkflow.getInstance().addTaskInstanceVariable(null, tiid, name, value);
		
		// Activity log
		UserActivity.log(session.getUserID(), "ADMIN_TASK_INSTANCE_VARIABLE_ADD", Long.toString(tiid), 
				name + "=" + value);
		log.debug("taskInstanceVariableAdd: void");
	}
	
	/**
	 * Resume task instance
	 */
	private void taskInstanceResume(Session session, HttpServletRequest request, 
			HttpServletResponse response) throws RepositoryException, DatabaseException, WorkflowException {
		log.debug("taskInstanceResume({}, {}, {})", new Object[] { session, request, response });
		long tiid = WebUtils.getLong(request, "tiid");
		OKMWorkflow.getInstance().resumeTaskInstance(null, tiid);
		
		// Activity log
		UserActivity.log(session.getUserID(), "ADMIN_TASK_INSTANCE_RESUME", Long.toString(tiid), null);
		log.debug("taskInstanceResume: void");
	}
	
	/**
	 * Suspend token
	 */
	private void tokenSuspend(Session session, HttpServletRequest request, HttpServletResponse response) 
			throws RepositoryException, DatabaseException, WorkflowException {
		log.debug("tokenSuspend({}, {}, {})", new Object[] { session, request, response });
		long tid = WebUtils.getLong(request, "tid");
		OKMWorkflow.getInstance().suspendToken(null, tid);

		// Activity log
		UserActivity.log(session.getUserID(), "ADMIN_TOKEN_SUSPEND", Long.toString(tid), null);
		log.debug("tokenSuspend: void");
	}
	
	/**
	 * Resume token
	 */
	private void tokenResume(Session session, HttpServletRequest request, HttpServletResponse response) 
			throws RepositoryException, DatabaseException, WorkflowException {
		log.debug("tokenResume({}, {}, {})", new Object[] { session, request, response });
		long tid = WebUtils.getLong(request, "tid");
		OKMWorkflow.getInstance().resumeToken(null, tid);

		// Activity log
		UserActivity.log(session.getUserID(), "ADMIN_TOKEN_RESUME", Long.toString(tid), null);
		log.debug("tokenResume: void");
	}

	/**
	 * End token
	 */
	private void tokenEnd(Session session, HttpServletRequest request, HttpServletResponse response) throws 
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("tokenEnd({}, {}, {})", new Object[] { session, request, response });
		long tid = WebUtils.getLong(request, "tid");
		OKMWorkflow.getInstance().endToken(null, tid);
		
		// Activity log
		UserActivity.log(session.getUserID(), "ADMIN_TOKEN_END", Long.toString(tid), null);
		log.debug("tokenEnd: void");
	}
	
	/**
	 * Set token node
	 */
	private void tokenSetNode(Session session, HttpServletRequest request, HttpServletResponse response)
			throws DatabaseException, WorkflowException, RepositoryException {
		log.debug("tokenSetNode({}, {}, {})", new Object[] { session, request, response });
		long tid = WebUtils.getLong(request, "tid");
		String node = WebUtils.getString(request, "node");
		OKMWorkflow.getInstance().setTokenNode(null, tid, node);
				
		// Activity log
		UserActivity.log(session.getUserID(), "ADMIN_TOKEN_SET_NODE", Long.toString(tid), node);
		log.debug("tokenSetNode: void");
	}
	
	/**
	 * Send token signal
	 */
	private void tokenSignal(Session session, HttpServletRequest request, HttpServletResponse response) 
			throws DatabaseException, WorkflowException, RepositoryException {
		log.debug("tokenSignal({}, {}, {})", new Object[] { session, request, response });
		long tid = WebUtils.getLong(request, "tid");
		String transition = WebUtils.getString(request, "transition");
		OKMWorkflow.getInstance().sendTokenSignal(null, tid, transition);
				
		// Activity log
		UserActivity.log(session.getUserID(), "ADMIN_TOKEN_SIGNAL", Long.toString(tid), transition);
		log.debug("tokenSignal: void");
	}

	/**
	 * View token
	 */
	private void tokenView(Session session, HttpServletRequest request, HttpServletResponse response) throws
			ServletException, IOException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("tokenView({}, {}, {})", new Object[] { session, request, response });
		ServletContext sc = getServletContext();
		long tid = WebUtils.getLong(request, "tid");
		sc.setAttribute("token", OKMWorkflow.getInstance().getToken(null, tid));
		sc.getRequestDispatcher("/admin/token_view.jsp").forward(request, response);
		log.debug("tokenView: void");
	}
}
