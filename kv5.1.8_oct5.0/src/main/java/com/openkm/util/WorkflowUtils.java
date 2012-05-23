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

package com.openkm.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.hibernate.Hibernate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.openkm.bean.workflow.Comment;
import com.openkm.bean.workflow.ProcessDefinition;
import com.openkm.bean.workflow.ProcessInstance;
import com.openkm.bean.workflow.TaskInstance;
import com.openkm.bean.workflow.Token;
import com.openkm.bean.workflow.Transition;

public class WorkflowUtils {
	
	/**
	 * Copy process definition
	 */
	@SuppressWarnings("rawtypes")
	public static ProcessDefinition copy(org.jbpm.graph.def.ProcessDefinition pd) {
		ProcessDefinition vo = new ProcessDefinition();
		
		vo.setName(pd.getName());
		vo.setDescription(pd.getDescription());
		vo.setId(pd.getId());
		vo.setVersion(pd.getVersion());
		ArrayList<String> al = new ArrayList<String>();
		
		for (Iterator it = pd.getNodes().iterator(); it.hasNext(); ) {
			org.jbpm.graph.def.Node n = (org.jbpm.graph.def.Node) it.next();
			al.add(n.getName());
		}
		
		vo.setNodes(al);
			
		return vo;
	}
	
	/**
	 * Copy process instance
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ProcessInstance copy(org.jbpm.graph.exe.ProcessInstance pi) {
		ProcessInstance vo = new ProcessInstance();

		if (pi.getStart() != null) {
			Calendar start = Calendar.getInstance();
			start.setTime(pi.getStart());
			vo.setStart(start);
		}
		
		if (pi.getEnd() != null) {
			Calendar end = Calendar.getInstance();
			end.setTime(pi.getEnd());
			vo.setEnd(end);
		}
		
		vo.setId(pi.getId());
		vo.setVersion(pi.getVersion());
		vo.setKey(pi.getKey());
		vo.setEnded(pi.hasEnded());
		vo.setSuspended(pi.isSuspended());

		// TODO https://jira.jboss.org/jira/browse/JBPM-1778
		if (pi.getContextInstance().getVariables() != null) {
			vo.setVariables(pi.getContextInstance().getVariables());
			
			// Workaround for LazyInitializationException
			for (String key : vo.getVariables().keySet()) {
				Hibernate.initialize(vo.getVariables().get(key));
			}
		} else {
			vo.setVariables(new HashMap<String, Object>());
		}
		
		ArrayList<Token> al = new ArrayList<Token>();
		
		for (Iterator it = pi.findAllTokens().iterator(); it.hasNext(); ) {
			org.jbpm.graph.exe.Token tk = (org.jbpm.graph.exe.Token) it.next();
			al.add(copy(tk));
		}

		vo.setAllTokens(al);
		vo.setRootToken(copy(pi.getRootToken()));
		vo.setProcessDefinition(copy(pi.getProcessDefinition()));
		
		return vo;
	}
	
	/**
	 * Copy task instance
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static TaskInstance copy(org.jbpm.taskmgmt.exe.TaskInstance ti) {
		TaskInstance vo = new TaskInstance();

		if (ti.getCreate() != null) {
			Calendar create = Calendar.getInstance();
			create.setTime(ti.getCreate());
			vo.setCreate(create);
		}
		
		if (ti.getStart() != null) {
			Calendar start = Calendar.getInstance();
			start.setTime(ti.getStart());
			vo.setStart(start);
		}
		
		if (ti.getEnd() != null) {
			Calendar end = Calendar.getInstance();
			end.setTime(ti.getEnd());
			vo.setEnd(end);
		}
		
		if (ti.getDueDate() != null) {
			Calendar dueDate = Calendar.getInstance();
			dueDate.setTime(ti.getDueDate());
			vo.setDueDate(dueDate);
		}
		
		vo.setId(ti.getId());
		vo.setName(ti.getName());
		vo.setDescription(ti.getDescription());
		vo.setVariables(ti.getVariables());
		ArrayList<Comment> al = new ArrayList<Comment>();
		
		for (Iterator it = ti.getComments().iterator(); it.hasNext(); ) {
			org.jbpm.graph.exe.Comment c = (org.jbpm.graph.exe.Comment) it.next();
			Comment tc = new Comment();
			tc.setActorId(c.getActorId());
			tc.setMessage(c.getMessage());
			Calendar time = Calendar.getInstance();
			time.setTime(c.getTime());
			tc.setTime(time);
			al.add(tc);
		}
		
		vo.setComments(al);
		vo.setActorId(ti.getActorId());
		vo.setOpen(ti.isOpen());
		vo.setLast(ti.isLast());
		vo.setSuspended(ti.isSuspended());
		vo.setStartTaskInstance(ti.isStartTaskInstance());
		HashSet<String> hs = new HashSet<String>();
		
		for (Iterator it = ti.getPooledActors().iterator(); it.hasNext(); ) {
			hs.add(it.next().toString());
		}
		
		vo.setPooledActors(hs);
		ArrayList<Transition> alT = new ArrayList<Transition>();
		
		// TODO http://www.jboss.com/index.html?module=bb&op=viewtopic&t=144049
		if (ti.getEnd() == null) {
			for (Iterator it = ti.getAvailableTransitions().iterator(); it.hasNext(); ) {
				org.jbpm.graph.def.Transition tr = (org.jbpm.graph.def.Transition) it.next(); 
				alT.add(copy(tr));
			}
		}
		
		// Sort
		Collections.sort(alT);
		vo.setAvailableTransitions(alT);
		
		vo.setToken(copy(ti.getToken()));
		vo.setProcessInstance(copy(ti.getProcessInstance()));
		
		return vo;
	}
	
	/**
	 * Copy token
	 */
	@SuppressWarnings("rawtypes")
	public static Token copy(org.jbpm.graph.exe.Token t) {
		Token vo = null;
		
		if (t != null) {
			vo = new Token();
			vo.setName(t.getName());
			vo.setId(t.getId());
			vo.setSuspended(t.isSuspended());
			ArrayList<Comment> alC = new ArrayList<Comment>();
			
			if (t.getComments() != null) {
				for (Iterator it = t.getComments().iterator(); it.hasNext(); ) {
					org.jbpm.graph.exe.Comment c = (org.jbpm.graph.exe.Comment) it.next();
					alC.add(copy(c));
				}
			}
			
			vo.setComments(alC);
			vo.setParent(copy(t.getParent()));
			vo.setNode(t.getNode().getName());
			
			if (t.getStart() != null) {
				Calendar start = Calendar.getInstance();
				start.setTime(t.getStart());
				vo.setStart(start);
			}
			
			if (t.getEnd() != null) {
				Calendar end = Calendar.getInstance();
				end.setTime(t.getEnd());
				vo.setEnd(end);
			}
			
			ArrayList<Transition> alT = new ArrayList<Transition>();
			
			for (Iterator it = t.getAvailableTransitions().iterator(); it.hasNext(); ) {
				org.jbpm.graph.def.Transition tr = (org.jbpm.graph.def.Transition) it.next(); 
				alT.add(copy(tr));
			}
			
			// Sort
			Collections.sort(alT);
			vo.setAvailableTransitions(alT);
			
			// Causes recursion
			//vo.setProcessInstance(copy(t.getProcessInstance()));
		}
		
		return vo;
	}
	
	/**
	 * Copy comment
	 */
	public static Comment copy(org.jbpm.graph.exe.Comment c) {
		Comment vo = new Comment();
		
		vo.setActorId(c.getActorId());
		vo.setMessage(c.getMessage());
		Calendar time = Calendar.getInstance();
		time.setTime(c.getTime());
		vo.setTime(time);
		
		return vo;
	}

	/**
	 * Copy transition
	 */
	public static Transition copy(org.jbpm.graph.def.Transition t) {
		Transition vo = new Transition();
		
		vo.setId(t.getId());
		vo.setName(t.getName());
		vo.setFrom(t.getFrom().getName());
		vo.setTo(t.getTo().getName());
					
		return vo;
	}
	
	/**
	 * Get diagram info
	 */
	public static WorkflowUtils.DiagramInfo getDiagramInfo(InputStream is) {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setFeature("http://xml.org/sax/features/validation", false);
			dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			DocumentBuilder db = dbf.newDocumentBuilder();
						
			if (is != null) {
				Document doc = db.parse(is);
				doc.getDocumentElement().normalize();
				Element processDiagramElement = doc.getDocumentElement();
	            final String widthString = processDiagramElement.getAttribute("width");
	            final String heightString = processDiagramElement.getAttribute("height");
	            final List<DiagramNodeInfo> diagramNodeInfoList = new ArrayList<DiagramNodeInfo>();
	            final NodeList nodeNodeList = processDiagramElement.getElementsByTagName("node");
	            final int nodeNodeListLength = nodeNodeList.getLength();
	            
	            for (int i = 0; i < nodeNodeListLength; i ++) {
	                final Node nodeNode = nodeNodeList.item(i);
	                
	                if (nodeNode instanceof Node && nodeNode.getParentNode() == processDiagramElement) {
	                    final Element nodeElement = (Element) nodeNode;
	                    final String nodeName = nodeElement.getAttribute("name");
	                    final String nodeXString = nodeElement.getAttribute("x");
	                    final String nodeYString = nodeElement.getAttribute("y");
	                    final String nodeWidthString = nodeElement.getAttribute("width");
	                    final String nodeHeightString = nodeElement.getAttribute("height");
	                    final DiagramNodeInfo nodeInfo = new DiagramNodeInfo(
	                        nodeName,
	                        Integer.parseInt(nodeXString),
	                        Integer.parseInt(nodeYString),
	                        Integer.parseInt(nodeWidthString),
	                        Integer.parseInt(nodeHeightString)
	                    );
	                    diagramNodeInfoList.add(nodeInfo);
	                }
	            }
	            
	            final DiagramInfo diagramInfo = new DiagramInfo(
	                Integer.parseInt(heightString),
	                Integer.parseInt(widthString),
	                diagramNodeInfoList
	            );
	            
	            return diagramInfo;
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Diagram info helper
	 */
	public static final class DiagramInfo implements Serializable {
        private static final long serialVersionUID = 1L;

        private final int width;
        private final int height;
        private final Map<String,DiagramNodeInfo> nodeMap;

        public DiagramInfo(final int height, final int width, final List<DiagramNodeInfo> nodeList) {
            this.height = height;
            this.width = width;
            final LinkedHashMap<String, DiagramNodeInfo> map = new LinkedHashMap<String, DiagramNodeInfo>();
            for (DiagramNodeInfo nodeInfo : nodeList) {
                map.put(nodeInfo.getName(), nodeInfo);
            }
            nodeMap = Collections.unmodifiableMap(map);
        }

        public int getHeight() {
            return height;
        }

        public Map<String, DiagramNodeInfo> getNodeMap() {
            return nodeMap;
        }

        public List<DiagramNodeInfo> getNodes() {
            return Collections.unmodifiableList(new ArrayList<DiagramNodeInfo>(nodeMap.values()));
        }

        public int getWidth() {
            return width;
        }
    }

	/**
	 * Diagram node info
	 */
	public static final class DiagramNodeInfo implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String name;
        private final int x;
        private final int y;
        private final int width;
        private final int height;

        public DiagramNodeInfo(final String name, final int x, final int y, final int width, final int height) {
            this.height = height;
            this.name = name;
            this.width = width;
            this.x = x;
            this.y = y;
        }

        public int getHeight() {
            return height;
        }

        public String getName() {
            return name;
        }

        public int getWidth() {
            return width;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
        
        public String toString() {
        	StringBuilder sb = new StringBuilder();
    		sb.append("{");
    		sb.append("name="); sb.append(name);
    		sb.append(", x="); sb.append(x);
    		sb.append(", y="); sb.append(y);
    		sb.append(", width="); sb.append(width);
    		sb.append(", height="); sb.append(height);
    		sb.append("}");
    		return sb.toString();
        }
    }
}
