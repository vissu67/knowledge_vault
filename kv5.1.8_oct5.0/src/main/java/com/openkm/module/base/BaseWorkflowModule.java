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

package com.openkm.module.base;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Session;

import org.jbpm.JbpmContext;
import org.jbpm.JbpmException;
import org.jbpm.db.GraphSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.form.FormElement;
import com.openkm.bean.workflow.ProcessInstance;
import com.openkm.core.Config;
import com.openkm.util.WorkflowUtils;

public class BaseWorkflowModule {
	private static Logger log = LoggerFactory.getLogger(BaseWorkflowModule.class);

	public static ProcessInstance runProcessDefinition(Session session, JbpmContext jbpmContext, 
			long processDefinitionId, String uuid, List<FormElement> variables) throws JbpmException {
		log.debug("runProcessDefinition({}, {}, {}, {})", new Object[] { session, processDefinitionId, uuid, variables });
		ProcessInstance vo = new ProcessInstance();
		jbpmContext.setActorId(session.getUserID());
		GraphSession graphSession = jbpmContext.getGraphSession();
		Map<String, Object> hm = new HashMap<String, Object>();
		hm.put(Config.WORKFLOW_PROCESS_INSTANCE_VARIABLE_UUID, uuid);
		
		for (FormElement fe : variables) {
			hm.put(fe.getName(), fe);
		}
		
		org.jbpm.graph.def.ProcessDefinition pd = graphSession.getProcessDefinition(processDefinitionId);
		org.jbpm.graph.exe.ProcessInstance pi = pd.createProcessInstance(hm);
		
		if (pi != null) {
			org.jbpm.taskmgmt.exe.TaskMgmtInstance tmi = pi.getTaskMgmtInstance();
			
			// 	http://community.jboss.org/thread/115182
			if (tmi.getTaskMgmtDefinition().getStartTask() != null) {
				org.jbpm.taskmgmt.exe.TaskInstance ti = tmi.createStartTaskInstance();
				
				if (Config.WORKFLOW_START_TASK_AUTO_RUN) {
					ti.start();
					ti.end();
				}
			} else {
				pi.getRootToken().signal();
			}
			
			jbpmContext.save(pi);
			vo = WorkflowUtils.copy(pi);
		}
		
		return vo;
	}
}
