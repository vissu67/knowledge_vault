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

package com.openkm.module.direct;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;
import javax.jcr.Session;

import org.apache.commons.io.IOUtils;
import org.jbpm.JbpmContext;
import org.jbpm.JbpmException;
import org.jbpm.db.GraphSession;
import org.jbpm.db.TaskMgmtSession;
import org.jbpm.file.def.FileDefinition;
import org.jbpm.taskmgmt.exe.TaskMgmtInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.form.FormElement;
import com.openkm.bean.workflow.ProcessDefinition;
import com.openkm.bean.workflow.ProcessInstance;
import com.openkm.bean.workflow.TaskInstance;
import com.openkm.bean.workflow.Token;
import com.openkm.core.DatabaseException;
import com.openkm.core.JcrSessionManager;
import com.openkm.core.ParseException;
import com.openkm.core.RepositoryException;
import com.openkm.core.WorkflowException;
import com.openkm.jcr.JCRUtils;
import com.openkm.module.WorkflowModule;
import com.openkm.module.base.BaseWorkflowModule;
import com.openkm.util.FormUtils;
import com.openkm.util.JBPMUtils;
import com.openkm.util.UserActivity;
import com.openkm.util.WorkflowUtils;

public class DirectWorkflowModule implements WorkflowModule {
	private static Logger log = LoggerFactory.getLogger(DirectWorkflowModule.class);

	@Override
	public void registerProcessDefinition(String token, InputStream is) throws ParseException, 
			RepositoryException, WorkflowException, DatabaseException, IOException {
		log.debug("registerProcessDefinition({}, {})", token, is);
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		InputStream isForms = null;
		ZipInputStream zis = null;
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			zis = new ZipInputStream(is);
			org.jbpm.graph.def.ProcessDefinition processDefinition = org.jbpm.graph.def.ProcessDefinition.parseParZipInputStream(zis);
									
			// Check xml form definition  
			FileDefinition fileDef = processDefinition.getFileDefinition();
			isForms = fileDef.getInputStream("forms.xml");
			FormUtils.parseWorkflowForms(isForms);
						
			// If it is ok, deploy it
			jbpmContext.deployProcessDefinition(processDefinition);
			
			// Activity log
			UserActivity.log(session.getUserID(), "REGISTER_PROCESS_DEFINITION", null, null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			IOUtils.closeQuietly(isForms);
			IOUtils.closeQuietly(zis);
			jbpmContext.close();
		}
		
		log.debug("registerProcessDefinition: void");
	}

	@Override
	public void deleteProcessDefinition(String token, long processDefinitionId) throws RepositoryException, 
			DatabaseException, WorkflowException {
		log.debug("deleteProcessDefinition({}, {})", token, processDefinitionId);
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			GraphSession graphSession = jbpmContext.getGraphSession();
			graphSession.deleteProcessDefinition(processDefinitionId);
			jbpmContext.getSession().flush();
			
			// Activity log
			UserActivity.log(session.getUserID(), "DELETE_PROCESS_DEFINITION", ""+processDefinitionId, null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("deleteProcessDefinition: void");
	}

	@Override
	public ProcessDefinition getProcessDefinition(String token, long processDefinitionId) throws 
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("getProcessDefinition({}, {})", token, processDefinitionId);
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		ProcessDefinition vo = new ProcessDefinition();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			GraphSession graphSession = jbpmContext.getGraphSession();
			org.jbpm.graph.def.ProcessDefinition pd = graphSession.getProcessDefinition(processDefinitionId);
			vo = WorkflowUtils.copy(pd);
			
			// Activity log
			UserActivity.log(session.getUserID(), "GET_PROCESS_DEFINITION", ""+processDefinitionId, null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("getProcessDefinition: {}", vo);
		return vo;
	}

	@Override
	public byte[] getProcessDefinitionImage(String token, long processDefinitionId, String node) throws 
			RepositoryException, DatabaseException, WorkflowException  {
		log.debug("getProcessDefinitionImage({}, {}, {})", new Object[] { token, processDefinitionId, node });
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		byte[] image = null;
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			GraphSession graphSession = jbpmContext.getGraphSession();
			org.jbpm.graph.def.ProcessDefinition pd = graphSession.getProcessDefinition(processDefinitionId);
			FileDefinition fileDef = pd.getFileDefinition();
			
			WorkflowUtils.DiagramInfo dInfo = WorkflowUtils.getDiagramInfo(fileDef.getInputStream("gpd.xml"));
			WorkflowUtils.DiagramNodeInfo dNodeInfo = dInfo.getNodeMap().get(node);
			BufferedImage img = ImageIO.read(fileDef.getInputStream("processimage.jpg"));
			
			// Obtain all nodes Y
			List<Integer> ordenadas = new ArrayList<Integer>();
			
			for (WorkflowUtils.DiagramNodeInfo nodeInfo : dInfo.getNodeMap().values()) {
				ordenadas.add(nodeInfo.getY());
			}
			
			// Calculate minimal Y
			Collections.sort(ordenadas);
			int fix = ordenadas.get(0);
			
			if (dNodeInfo != null) {
				// Select node
				log.debug("DiagramNodeInfo: {}", dNodeInfo);
				Graphics g = img.getGraphics();
				Graphics2D g2d = (Graphics2D) g;
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25F));
				g2d.setColor(Color.blue);
				g2d.fillRect(dNodeInfo.getX(), dNodeInfo.getY() - fix, dNodeInfo.getWidth(), dNodeInfo.getHeight());
				g.dispose();
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(img, "jpg", baos);
			image = baos.toByteArray();
			baos.flush();
			baos.close();

			// Activity log
			UserActivity.log(session.getUserID(), "GET_PROCESS_DEFINITION_IMAGE", ""+processDefinitionId, null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} catch (IOException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("getProcessDefinitionImage: {}", image);
		return image;
	}

	@Override
	public Map<String, List<FormElement>> getProcessDefinitionForms(String token, long processDefinitionId)
			throws ParseException, RepositoryException, DatabaseException, WorkflowException {
		log.debug("getProcessDefinitionForms({}, {})", token, processDefinitionId);
		//long begin = Calendar.getInstance().getTimeInMillis();
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		Map<String, List<FormElement>> forms = new HashMap<String, List<FormElement>>();
		InputStream is = null;
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			GraphSession graphSession = jbpmContext.getGraphSession();
			org.jbpm.graph.def.ProcessDefinition pd = graphSession.getProcessDefinition(processDefinitionId);
			FileDefinition fileDef = pd.getFileDefinition();
			is = fileDef.getInputStream("forms.xml");
			
			if (is != null) {
				forms = FormUtils.parseWorkflowForms(is);
				is.close();
			} else {
				log.warn("Process definition '{}' has no forms.xml file", processDefinitionId);
			}
			
			// Activity log
			UserActivity.log(session.getUserID(), "GET_PROCESS_DEFINITION_FORMS", processDefinitionId+"", null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} catch (IOException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			IOUtils.closeQuietly(is);
			jbpmContext.close();
		}
		
		log.debug("getProcessDefinitionForms: {}", forms);
		//log.info("Time: "+(Calendar.getInstance().getTimeInMillis()-begin)+" ms");
		return forms;
	}

	@Override
	public ProcessInstance runProcessDefinition(String token, long processDefinitionId, String uuid, 
			List<FormElement> variables) throws RepositoryException, DatabaseException, WorkflowException {
		log.debug("runProcessDefinition({}, {}, {})", new Object[] { token, processDefinitionId, variables });
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		ProcessInstance vo = new ProcessInstance();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			vo = BaseWorkflowModule.runProcessDefinition(session, jbpmContext, processDefinitionId, uuid, variables);
			
			// Activity log
			UserActivity.log(session.getUserID(), "RUN_PROCESS_DEFINITION", ""+processDefinitionId, variables.toString());
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("runProcessDefinition: {}", vo);
		return vo;
	}

	@Override
	public ProcessInstance sendProcessInstanceSignal(String token, long processInstanceId, 
			String transitionName) throws RepositoryException, DatabaseException, WorkflowException {
		log.debug("sendProcessInstanceSignal({}, {}, {})", new Object[] { token, processInstanceId, transitionName });
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		ProcessInstance vo = new ProcessInstance();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			GraphSession graphSession = jbpmContext.getGraphSession();
			org.jbpm.graph.exe.ProcessInstance pi = graphSession.getProcessInstance(processInstanceId);
			org.jbpm.graph.exe.Token t = pi.getRootToken();
			
			if (transitionName != null && !transitionName.equals("")) {
				t.signal(transitionName);
			} else {
				t.signal();
			}

			jbpmContext.getSession().flush();
			vo = WorkflowUtils.copy(pi);
			
			// Activity log
			UserActivity.log(session.getUserID(), "SEND_PROCESS_INSTANCE_SIGNAL", ""+processInstanceId, transitionName);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("sendProcessInstanceSignal: {}", vo);
		return vo;
	}

	@Override
	public void endProcessInstance(String token, long processInstanceId) throws RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("endProcessInstance({}, {})", token, processInstanceId);
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			GraphSession graphSession = jbpmContext.getGraphSession();
			graphSession.getProcessInstance(processInstanceId).end();
			jbpmContext.getSession().flush();
			
			// Activity log
			UserActivity.log(session.getUserID(), "END_PROCESS_INSTANCE", ""+processInstanceId, null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("endProcessInstance: void");
	}
	
	@Override
	public void deleteProcessInstance(String token, long processInstanceId) throws RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("deleteProcessInstance({}, {})", token, processInstanceId);
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			GraphSession graphSession = jbpmContext.getGraphSession();
			graphSession.deleteProcessInstance(processInstanceId);
			jbpmContext.getSession().flush();
			
			// Activity log
			UserActivity.log(session.getUserID(), "DELETE_PROCESS_INSTANCE", ""+processInstanceId, null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("deleteProcessInstance: void");
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List<ProcessInstance> findProcessInstances(String token, long processDefinitionId) throws 
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("findProcessInstances({}, {})", token, processDefinitionId);
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		List<ProcessInstance> al = new ArrayList<ProcessInstance>();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			GraphSession graphSession = jbpmContext.getGraphSession();
			
			for (Iterator it = graphSession.findProcessInstances(processDefinitionId).iterator(); it.hasNext(); ) {
				al.add(WorkflowUtils.copy((org.jbpm.graph.exe.ProcessInstance) it.next()));
			}
			
			// Activity log
			UserActivity.log(session.getUserID(), "FIND_PROCESS_INSTANCES", ""+processDefinitionId, null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("findProcessInstances: {}", al);
		return al;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List<ProcessDefinition> findAllProcessDefinitions(String token) throws RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("findAllProcessDefinitions({})", token);
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		List<ProcessDefinition> al = new ArrayList<ProcessDefinition>();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			GraphSession graphSession = jbpmContext.getGraphSession();
			
			for (Iterator it = graphSession.findAllProcessDefinitions().iterator(); it.hasNext(); ){
				al.add(WorkflowUtils.copy((org.jbpm.graph.def.ProcessDefinition) it.next()));
			}
			
			// Activity log
			UserActivity.log(session.getUserID(), "FIND_ALL_PROCESS_DEFINITIONS", null, null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("findAllProcessDefinitions: {}", al);
		return al;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List<ProcessDefinition> findLatestProcessDefinitions(String token) throws RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("findLatestProcessDefinitions({})", token);
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		List<ProcessDefinition> al = new ArrayList<ProcessDefinition>();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			GraphSession graphSession = jbpmContext.getGraphSession();
			
			for (Iterator it = graphSession.findLatestProcessDefinitions().iterator(); it.hasNext(); ) {
				al.add(WorkflowUtils.copy((org.jbpm.graph.def.ProcessDefinition) it.next()));
			}
			
			// Activity log
			UserActivity.log(session.getUserID(), "FIND_LATEST_PROCESS_DEFINITIONS", null, null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("findLatestProcessDefinitions: {}", al);
		return al;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List<ProcessDefinition> findAllProcessDefinitionVersions(String token, String name) throws 
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("findAllProcessDefinitionVersions({}, {})", token, name);
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		List<ProcessDefinition> al = new ArrayList<ProcessDefinition>();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			GraphSession graphSession = jbpmContext.getGraphSession();
			
			for (Iterator it = graphSession.findAllProcessDefinitionVersions(name).iterator(); it.hasNext(); ){
				al.add(WorkflowUtils.copy((org.jbpm.graph.def.ProcessDefinition) it.next()));
			}
			
			// Activity log
			UserActivity.log(session.getUserID(), "FIND_ALL_PROCESS_DEFINITION_VERSIONS", name, null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("findAllProcessDefinitionVersions: {}", al);
		return al;
	}

	@Override
	public ProcessInstance getProcessInstance(String token, long processInstanceId) throws 
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("getProcessInstance({}, {})", token, processInstanceId);
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		ProcessInstance vo = new ProcessInstance();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			GraphSession graphSession = jbpmContext.getGraphSession();
			org.jbpm.graph.exe.ProcessInstance pi = graphSession.getProcessInstance(processInstanceId);
			vo = WorkflowUtils.copy(pi);
			
			// Activity log
			UserActivity.log(session.getUserID(), "GET_PROCESS_INSTANCE", ""+processInstanceId, null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("getProcessInstance: {}", vo);
		return vo;
	}

	@Override
	public void suspendProcessInstance(String token, long processInstanceId) throws RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("suspendProcessInstance({}, {})", token, processInstanceId);
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			org.jbpm.graph.exe.ProcessInstance pi = jbpmContext.getProcessInstance(processInstanceId);
			pi.suspend();
			jbpmContext.getSession().flush();
			
			// Activity log
			UserActivity.log(session.getUserID(), "SUSPEND_PROCESS_INSTANCE", ""+processInstanceId, null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("suspendProcessInstance: void");
	}

	@Override
	public void resumeProcessInstance(String token, long processInstanceId) throws RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("resumeProcessInstance({}, {})", token, processInstanceId);
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			org.jbpm.graph.exe.ProcessInstance pi = jbpmContext.getProcessInstance(processInstanceId);
			pi.resume();
			jbpmContext.getSession().flush();
			
			// Activity log
			UserActivity.log(session.getUserID(), "RESUME_PROCESS_INSTANCE", ""+processInstanceId, null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("resumeProcessInstance: void");
	}

	@Override
	public void addProcessInstanceVariable(String token, long processInstanceId, String name, Object value)
			throws RepositoryException, DatabaseException, WorkflowException{
		log.debug("addProcessInstanceVariable({}, {}, {}, {})", new Object[] { token, processInstanceId, name, value });
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			org.jbpm.graph.exe.ProcessInstance pi = jbpmContext.getProcessInstance(processInstanceId);
			pi.getContextInstance().setVariable(name, value);
			jbpmContext.getSession().flush();
			
			// Activity log
			UserActivity.log(session.getUserID(), "ADD_PROCESS_INSTANCE_VARIABLE", ""+processInstanceId, name+", "+value);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("addProcessInstanceVariable: void");
	}

	@Override
	public void deleteProcessInstanceVariable(String token, long processInstanceId, String name) throws
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("deleteProcessInstanceVariable({}, {}, {})", new Object[] { token, processInstanceId, name });
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			org.jbpm.graph.exe.ProcessInstance pi = jbpmContext.getProcessInstance(processInstanceId);
			pi.getContextInstance().deleteVariable(name);
			jbpmContext.getSession().flush();
			
			// Activity log
			UserActivity.log(session.getUserID(), "DELETE_PROCESS_INSTANCE_VARIABLE", ""+processInstanceId, name);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("deleteProcessInstanceVariable: void");
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List<TaskInstance> findUserTaskInstances(String token) throws RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("findUserTaskInstances({})", token);
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		List<TaskInstance> al = new ArrayList<TaskInstance>();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			TaskMgmtSession taskMgmtSession = jbpmContext.getTaskMgmtSession();
			
			for (Iterator it = taskMgmtSession.findTaskInstances(session.getUserID()).iterator(); it.hasNext(); ) {
				org.jbpm.taskmgmt.exe.TaskInstance ti = (org.jbpm.taskmgmt.exe.TaskInstance) it.next();
				al.add(WorkflowUtils.copy(ti));
			}
			
			// Sort
			Collections.sort(al);
			
			// Activity log
			UserActivity.log(session.getUserID(), "FIND_USER_TASK_INSTANCES", null, null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("findUserTaskInstances: {}", al);
		return al;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List<TaskInstance> findPooledTaskInstances(String token) throws RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("findPooledTaskInstances({})", token);
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		ArrayList<TaskInstance> al = new ArrayList<TaskInstance>();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			TaskMgmtSession taskMgmtSession = jbpmContext.getTaskMgmtSession();
			
			for (Iterator it = taskMgmtSession.findPooledTaskInstances(session.getUserID()).iterator(); it.hasNext(); ) {
				org.jbpm.taskmgmt.exe.TaskInstance ti = (org.jbpm.taskmgmt.exe.TaskInstance) it.next();
				al.add(WorkflowUtils.copy(ti));
			}
			
			// Sort
			Collections.sort(al);
			
			// Activity log
			UserActivity.log(session.getUserID(), "FIND_POOLED_TASK_INSTANCES", null, null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("findPooledTaskInstances: {}", al);
		return al;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public List<TaskInstance> findTaskInstances(String token, long processInstanceId) throws
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("findTaskInstances({})", token);
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		ArrayList<TaskInstance> al = new ArrayList<TaskInstance>();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			GraphSession graphSession = jbpmContext.getGraphSession();
			org.jbpm.graph.exe.ProcessInstance pi = graphSession.getProcessInstance(processInstanceId);
			TaskMgmtInstance taskMgmtInstance = pi.getTaskMgmtInstance();
			
			if (taskMgmtInstance.getTaskInstances() != null) {
				for (Iterator it = taskMgmtInstance.getTaskInstances().iterator(); it.hasNext(); ) {
					org.jbpm.taskmgmt.exe.TaskInstance ti = (org.jbpm.taskmgmt.exe.TaskInstance) it.next();
					al.add(WorkflowUtils.copy(ti));
				}
			}
			
			// Sort
			Collections.sort(al);
			
			// Activity log
			UserActivity.log(session.getUserID(), "FIND_TASK_INSTANCES", ""+processInstanceId, null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("findTaskInstances: {}", al);
		return al;
	}

	@Override
	public void setTaskInstanceValues(String token, long taskInstanceId, String transitionName,
			List<FormElement> values) throws RepositoryException, DatabaseException, WorkflowException {
		log.debug("setTaskInstanceValues({}, {}, {}, {})", new Object[] { token, taskInstanceId, transitionName, values });
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			TaskMgmtSession taskMgmtSession = jbpmContext.getTaskMgmtSession();
			Map<String, FormElement> hm = new HashMap<String, FormElement>();
			
			for (FormElement fe : values) {
				hm.put(fe.getName(), fe);
			}
			
			org.jbpm.taskmgmt.exe.TaskInstance ti = taskMgmtSession.getTaskInstance(taskInstanceId);
			ti.setVariables(hm);
			
			if (transitionName != null && !transitionName.equals("")) {
				if (ti.getStart() == null) {
					ti.start();
				}
				
				ti.end(transitionName);
			} else {
				if (ti.getStart() == null) {
					ti.start();
				}
				
				ti.end();
			}
			
			// Activity log
			UserActivity.log(session.getUserID(), "SET_TASK_INSTANCE_VALUES", ""+taskInstanceId, transitionName);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("setTaskInstanceValues: void");
	}

	@Override
	public void addTaskInstanceComment(String token, long taskInstanceId, String message) throws
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("addTaskInstanceComment({}, {}, {})", new Object[] { token, taskInstanceId, message });
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			TaskMgmtSession taskMgmtSession = jbpmContext.getTaskMgmtSession();
			org.jbpm.taskmgmt.exe.TaskInstance ti = taskMgmtSession.getTaskInstance(taskInstanceId);
			ti.addComment(new org.jbpm.graph.exe.Comment(session.getUserID(), message));
			jbpmContext.getSession().flush();
			
			// Activity log
			UserActivity.log(session.getUserID(), "ADD_TASK_INSTANCE_COMMENT", ""+taskInstanceId, message);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("addTaskInstanceComment: void");
	}

	@Override
	public TaskInstance getTaskInstance(String token, long taskInstanceId) throws RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("getTaskInstance({}, {})", token, taskInstanceId);
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		TaskInstance vo = new TaskInstance();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			TaskMgmtSession taskMgmtSession = jbpmContext.getTaskMgmtSession();
			org.jbpm.taskmgmt.exe.TaskInstance ti = taskMgmtSession.getTaskInstance(taskInstanceId);
			vo = WorkflowUtils.copy(ti);
						
			// Activity log
			UserActivity.log(session.getUserID(), "GET_TASK_INSTANCE", ""+taskInstanceId, null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("getTaskInstance: {}", vo);
		return vo;
	}

	@Override
	public void setTaskInstanceActorId(String token, long taskInstanceId, String actorId) throws
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("setTaskInstanceActorId({}, {}, {})", new Object[] { token, taskInstanceId, actorId });
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			org.jbpm.taskmgmt.exe.TaskInstance ti = jbpmContext.getTaskInstance(taskInstanceId);
			ti.setActorId(actorId);
			jbpmContext.getSession().flush();
			
			// Activity log
			UserActivity.log(session.getUserID(), "SET_TASK_INSTANCE_ACTOR_ID", ""+taskInstanceId, actorId);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("setTaskInstanceActorId: void");
	}

	@Override
	// TODO Esto creo que sobra pq no se puede hacer
	public void addTaskInstanceVariable(String token, long taskInstanceId, String name, Object value) throws
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("addTaskInstanceVariable({}, {}, {}, {})", new Object[] { token, taskInstanceId, name, value });
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			org.jbpm.taskmgmt.exe.TaskInstance ti = jbpmContext.getTaskInstance(taskInstanceId);
			ti.setVariable(name, value);
			jbpmContext.getSession().flush();
			
			// Activity log
			UserActivity.log(session.getUserID(), "ADD_TASK_INSTANCE_VARIABLE", ""+taskInstanceId, name+", "+value);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("addTaskInstanceVariable: void");
	}

	@Override
	public void deleteTaskInstanceVariable(String token, long taskInstanceId, String name) throws
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("deleteTaskInstanceVariable({}, {}, {})", new Object[] { token, taskInstanceId, name });
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			org.jbpm.taskmgmt.exe.TaskInstance ti = jbpmContext.getTaskInstance(taskInstanceId);
			ti.deleteVariable(name);
			jbpmContext.getSession().flush();
			
			// Activity log
			UserActivity.log(session.getUserID(), "DELETE_TASK_INSTANCE_VARIABLE", ""+taskInstanceId, name);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("deleteTaskInstanceVariable: void");
	}

	@Override
	public void startTaskInstance(String token, long taskInstanceId) throws RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("startTaskInstance({}, {})", token, taskInstanceId);
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			TaskMgmtSession taskMgmtSession = jbpmContext.getTaskMgmtSession();
			org.jbpm.taskmgmt.exe.TaskInstance ti = taskMgmtSession.getTaskInstance(taskInstanceId);
			ti.start();
			jbpmContext.getSession().flush();
			
			// Activity log
			UserActivity.log(session.getUserID(), "START_TASK_INSTANCE", ""+taskInstanceId, null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("startTaskInstance: void");
	}

	@Override
	public void endTaskInstance(String token, long taskInstanceId, String transitionName) throws
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("endTaskInstance({}, {}, {})", new Object[] { token, taskInstanceId, transitionName });
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			TaskMgmtSession taskMgmtSession = jbpmContext.getTaskMgmtSession();
			org.jbpm.taskmgmt.exe.TaskInstance ti = taskMgmtSession.getTaskInstance(taskInstanceId);
			
			if (transitionName != null && !transitionName.equals("")) {
				if (ti.getStart() == null) {
					ti.start();
				}
				
				ti.end(transitionName);
			} else {
				if (ti.getStart() == null) {
					ti.start();
				}
				
				ti.end();
			}
			
			jbpmContext.getSession().flush();
			
			// Activity log
			UserActivity.log(session.getUserID(), "END_TASK_INSTANCE", ""+taskInstanceId, null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("endTaskInstance: void");
	}

	@Override
	public void suspendTaskInstance(String token, long taskInstanceId) throws RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("suspendTaskInstance({}, {})", token, taskInstanceId);
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			TaskMgmtSession taskMgmtSession = jbpmContext.getTaskMgmtSession();
			org.jbpm.taskmgmt.exe.TaskInstance ti = taskMgmtSession.getTaskInstance(taskInstanceId);
			ti.suspend();
			jbpmContext.getSession().flush();
			
			// Activity log
			UserActivity.log(session.getUserID(), "SUSPEND_TASK_INSTANCE", ""+taskInstanceId, null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("suspendTaskInstance: void");
	}

	@Override
	public void resumeTaskInstance(String token, long taskInstanceId) throws RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("resumeTaskInstance({}, {})", token, taskInstanceId);
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			TaskMgmtSession taskMgmtSession = jbpmContext.getTaskMgmtSession();
			org.jbpm.taskmgmt.exe.TaskInstance ti = taskMgmtSession.getTaskInstance(taskInstanceId);
			ti.resume();
			jbpmContext.getSession().flush();
			
			// Activity log
			UserActivity.log(session.getUserID(), "RESUME_TASK_INSTANCE", ""+taskInstanceId, null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("resumeTaskInstance: void");
	}

	@Override
	public Token getToken(String token, long tokenId) throws RepositoryException, DatabaseException,
			WorkflowException {
		log.debug("getToken({}, {})", token, tokenId);
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		Token vo = new Token();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			org.jbpm.graph.exe.Token t = jbpmContext.getToken(tokenId);
			vo = WorkflowUtils.copy(t);
			vo.setProcessInstance(WorkflowUtils.copy(t.getProcessInstance())); // Avoid recursion
			
			// Activity log
			UserActivity.log(session.getUserID(), "GET_TOKEN", ""+tokenId, null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("getToken: "+vo);
		return vo;
	}

	@Override
	public void addTokenComment(String token, long tokenId, String message) throws RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("addTokenComment({}, {}, {})", new Object[] { token, tokenId, message });
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			org.jbpm.graph.exe.Token t = jbpmContext.getToken(tokenId);
			t.addComment(new org.jbpm.graph.exe.Comment(session.getUserID(), message));
			jbpmContext.getSession().flush();
			
			// Activity log
			UserActivity.log(session.getUserID(), "ADD_TOKEN_COMMENT", ""+tokenId, message);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("addTokenComment: void");
	}

	@Override
	public void suspendToken(String token, long tokenId) throws RepositoryException, DatabaseException,
			WorkflowException {
		log.debug("suspendToken({}, {})", token, tokenId);
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			org.jbpm.graph.exe.Token t = jbpmContext.getToken(tokenId);
			t.suspend();
			jbpmContext.getSession().flush();
			
			// Activity log
			UserActivity.log(session.getUserID(), "SUSPEND_TOKEN", ""+tokenId, null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("suspendToken: void");
	}

	@Override
	public void resumeToken(String token, long tokenId) throws RepositoryException, DatabaseException,
			WorkflowException {
		log.debug("resumeToken({}, {})", token, tokenId);
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			org.jbpm.graph.exe.Token t = jbpmContext.getToken(tokenId);
			t.resume();
			jbpmContext.getSession().flush();
			
			// Activity log
			UserActivity.log(session.getUserID(), "RESUME_TOKEN", ""+tokenId, null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("resumeToken: void");
	}

	@Override
	public Token sendTokenSignal(String token, long tokenId, String transitionName) throws 
			RepositoryException, DatabaseException, WorkflowException {
		log.debug("sendTokenSignal({}, {}, {})", new Object[] { token, tokenId, transitionName });
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		Token vo = new Token();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			org.jbpm.graph.exe.Token t = jbpmContext.getToken(tokenId);

			if (transitionName != null && !transitionName.equals("")) {
				t.signal(transitionName);
			} else {
				t.signal();
			}

			jbpmContext.getSession().flush();
			vo = WorkflowUtils.copy(t);
			vo.setProcessInstance(WorkflowUtils.copy(t.getProcessInstance())); // Avoid recursion
			
			// Activity log
			UserActivity.log(session.getUserID(), "SEND_TOKEN_SIGNAL", ""+tokenId, transitionName);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("sendTokenSignal: {}", vo);
		return vo;
	}

	@Override
	public void setTokenNode(String token, long tokenId, String nodeName) throws RepositoryException,
			DatabaseException, WorkflowException {
		log.debug("setTokenNode({}, {}, {})", new Object[] { token, tokenId, nodeName });
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			org.jbpm.graph.exe.Token t = jbpmContext.getToken(tokenId);
			org.jbpm.graph.def.Node node = t.getProcessInstance().getProcessDefinition().getNode(nodeName);
			t.setNode(node);
			jbpmContext.getSession().flush();
			
			// Activity log
			UserActivity.log(session.getUserID(), "SEND_TOKEN_NODE", ""+tokenId, nodeName);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("setTokenNode: void");
	}
	
	@Override
	public void endToken(String token, long tokenId) throws RepositoryException, DatabaseException,
			WorkflowException {
		log.debug("endToken({}, {})", token, tokenId);
		JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			jbpmContext.getToken(tokenId).end();
			jbpmContext.getSession().flush();
			
			// Activity log
			UserActivity.log(session.getUserID(), "END_TOKEN", ""+tokenId, null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (JbpmException e) {
			throw new WorkflowException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
			jbpmContext.close();
		}
		
		log.debug("endToken: void");
	}
}
