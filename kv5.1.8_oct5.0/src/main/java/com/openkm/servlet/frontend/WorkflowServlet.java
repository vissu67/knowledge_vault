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

package com.openkm.servlet.frontend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.api.OKMWorkflow;
import com.openkm.bean.form.FormElement;
import com.openkm.bean.workflow.ProcessDefinition;
import com.openkm.bean.workflow.TaskInstance;
import com.openkm.core.DatabaseException;
import com.openkm.core.ParseException;
import com.openkm.core.RepositoryException;
import com.openkm.core.WorkflowException;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.GWTProcessDefinition;
import com.openkm.frontend.client.bean.GWTTaskInstance;
import com.openkm.frontend.client.bean.form.GWTFormElement;
import com.openkm.frontend.client.contants.service.ErrorCode;
import com.openkm.frontend.client.service.OKMWorkflowService;
import com.openkm.util.GWTUtil;

/**
 * Servlet Class
 */
public class WorkflowServlet extends OKMRemoteServiceServlet implements OKMWorkflowService {
	private static Logger log = LoggerFactory.getLogger(WorkflowServlet.class);
	private static final long serialVersionUID = 1L;
	
	@Override
	public List<GWTProcessDefinition> findLatestProcessDefinitions() throws OKMException {
		log.debug("findLatestProcessDefinitions()");
		List<GWTProcessDefinition> processDefinitionList = new ArrayList<GWTProcessDefinition>();
		updateSessionManager();
		
		try {
			for (Iterator<ProcessDefinition> it = OKMWorkflow.getInstance().findLatestProcessDefinitions(null).iterator(); it.hasNext();) {
				processDefinitionList.add(GWTUtil.copy(it.next()));
			}
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (WorkflowException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_Workflow), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("findLatestProcessDefinitions: {}", processDefinitionList);
		return processDefinitionList;
	}
	
	@Override
	public void runProcessDefinition(String UUID, double id, List<GWTFormElement> formElements) throws OKMException  {
		log.debug("runProcessDefinition()");
		updateSessionManager();
		
		try {
			List<FormElement> formElementList = new ArrayList<FormElement>();
			
			for (Iterator<GWTFormElement> it = formElements.iterator(); it.hasNext();) {
				formElementList.add(GWTUtil.copy(it.next()));
			}
			
			OKMWorkflow.getInstance().runProcessDefinition(null, new Double(id).longValue(), UUID, formElementList);
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (WorkflowException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_Workflow), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_General), e.getMessage());
		}
				
		log.debug("runProcessDefinition: void");
	}
	
	@Override
	public List<GWTTaskInstance> findUserTaskInstances() throws OKMException {
		log.debug("findUserTaskInstances()");
		List<GWTTaskInstance> taskInstances = new ArrayList<GWTTaskInstance>();
		updateSessionManager();
		
		try {
			for (Iterator<TaskInstance> it= OKMWorkflow.getInstance().findUserTaskInstances(null).iterator(); it.hasNext();) {
				taskInstances.add(GWTUtil.copy(it.next()));
			}
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (WorkflowException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_Workflow), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("findUserTaskInstances: {}", taskInstances);
		return taskInstances;
	}
	
	@Override
	public List<GWTTaskInstance> findPooledTaskInstances() throws OKMException {
		log.debug("findPooledTaskInstances()");
		List<GWTTaskInstance> taskInstances = new ArrayList<GWTTaskInstance>();
		updateSessionManager();
		
		try {
			for (Iterator<TaskInstance> it= OKMWorkflow.getInstance().findPooledTaskInstances(null).iterator(); it.hasNext();) {
				taskInstances.add(GWTUtil.copy(it.next()));
			}
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (WorkflowException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_Workflow), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("findPooledTaskInstances: {}", taskInstances);
		return taskInstances;
	}
	
	@Override
	public Map<String, List<GWTFormElement>> getProcessDefinitionForms(double id) throws OKMException {
		log.debug("getProcessDefinitionForms()");
		Map<String, List<GWTFormElement>> formElementList = new HashMap<String, List<GWTFormElement>>();
		updateSessionManager();
		
		try {
			Map<String, List<FormElement>> list = OKMWorkflow.getInstance().getProcessDefinitionForms(null, new Double(id).longValue());
			
			for (Iterator<String> it= list.keySet().iterator(); it.hasNext();) {
				String key = it.next();
				List<FormElement> col = list.get(key);
				List<GWTFormElement> gwtCol = new ArrayList<GWTFormElement>();
				
				for (Iterator<FormElement> itf= col.iterator(); itf.hasNext();) {
					gwtCol.add(GWTUtil.copy(itf.next()));
				}
				
				formElementList.put(key, gwtCol);
			}
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_Parse), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (WorkflowException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_Workflow), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("getProcessDefinitionForms: {}", formElementList);
		return formElementList;
	}
	
	@Override
	public void setTaskInstanceValues(double id, String transitionName, List<GWTFormElement> formElements) throws OKMException {
		log.debug("setTaskInstanceValues()");
		updateSessionManager();
		
		try {
			List<FormElement> formElementList = new ArrayList<FormElement>();
			
			for (Iterator<GWTFormElement> it = formElements.iterator(); it.hasNext();) {
				formElementList.add(GWTUtil.copy(it.next()));
			}
			
			OKMWorkflow.getInstance().setTaskInstanceValues(null, new Double(id).longValue(), transitionName, formElementList);
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (WorkflowException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_Workflow), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("setTaskInstanceValues: void");
	}
	
	@Override
	public void addComment(double tokenId, String message) throws OKMException {
		log.debug("addComment({}, {})", tokenId, message);
		updateSessionManager();

		try {
			OKMWorkflow.getInstance().addTokenComment(null, new Double(tokenId).longValue(), message);
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (WorkflowException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_Workflow), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("addComment: void");
	}
	
	@Override
	public void setTaskInstanceActorId(double id) throws OKMException {
		log.debug("setTaskInstanceActorId({})", id);
		updateSessionManager();
		
		try {
			OKMWorkflow.getInstance().setTaskInstanceActorId(null, new Double(id).longValue(), getThreadLocalRequest().getRemoteUser());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (WorkflowException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_Workflow), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("setTaskInstanceActorId: void");
	}

	@Override
	public void startTaskInstance(double id) throws OKMException {
		log.debug("startTaskInstance({})", id);
		updateSessionManager();
		
		try {
			OKMWorkflow okmWorkflow= OKMWorkflow.getInstance();
			long taskInstanceId = new Double(id).longValue();
			TaskInstance ti = okmWorkflow.getTaskInstance(null, taskInstanceId);
			
			if (ti.getStart() == null) {
				okmWorkflow.startTaskInstance(null, taskInstanceId);
			}
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (WorkflowException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_Workflow), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMWorkflowService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("startTaskInstance: void");
	}
}
