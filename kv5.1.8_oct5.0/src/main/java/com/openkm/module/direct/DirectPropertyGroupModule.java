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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.NodeTypeIterator;
import javax.jcr.nodetype.NodeTypeManager;
import javax.jcr.nodetype.PropertyDefinition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.PropertyGroup;
import com.openkm.bean.form.CheckBox;
import com.openkm.bean.form.FormElement;
import com.openkm.bean.form.Input;
import com.openkm.bean.form.Option;
import com.openkm.bean.form.Select;
import com.openkm.bean.form.SuggestBox;
import com.openkm.bean.form.TextArea;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.JcrSessionManager;
import com.openkm.core.LockException;
import com.openkm.core.NoSuchGroupException;
import com.openkm.core.NoSuchPropertyException;
import com.openkm.core.ParseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.jcr.JCRUtils;
import com.openkm.module.PropertyGroupModule;
import com.openkm.module.base.BasePropertyGroupModule;
import com.openkm.util.FormUtils;
import com.openkm.util.UserActivity;

public class DirectPropertyGroupModule implements PropertyGroupModule {
	private static Logger log = LoggerFactory.getLogger(DirectPropertyGroupModule.class);

	@Override
	public void addGroup(String token, String nodePath, String grpName) throws NoSuchGroupException,
			LockException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("addGroup({}, {}, {})", new Object[] { token, nodePath, grpName });
		Node documentNode = null;
		Session session = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			documentNode = session.getRootNode().getNode(nodePath.substring(1));
			BasePropertyGroupModule.addGroup(session, documentNode, grpName);
			
			// Activity log
			UserActivity.log(session.getUserID(), "ADD_PROPERTY_GROUP", documentNode.getUUID(), grpName+", "+nodePath);
		} catch (javax.jcr.nodetype.NoSuchNodeTypeException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(documentNode);
			throw new NoSuchGroupException(e.getMessage(), e);
		} catch (javax.jcr.lock.LockException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(documentNode);
			throw new LockException(e.getMessage(), e);
		} catch (javax.jcr.PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(documentNode);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(documentNode);
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(documentNode);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}
		
		log.debug("addGroup: void");
	}

	@Override
	public void removeGroup(String token, String nodePath, String grpName) throws AccessDeniedException, 
			NoSuchGroupException, LockException, PathNotFoundException, RepositoryException, 
			DatabaseException {
		log.debug("removeGroup({}, {}, {})", new Object[] { token, nodePath, grpName });
		Node documentNode = null;
		Session session = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			documentNode = session.getRootNode().getNode(nodePath.substring(1));
			
			synchronized (documentNode) {
				documentNode.removeMixin(grpName);
				documentNode.save();				
			}
			
			// Activity log
			UserActivity.log(session.getUserID(), "REMOVE_PROPERTY_GROUP", documentNode.getUUID(), grpName+", "+nodePath);
		} catch (javax.jcr.nodetype.NoSuchNodeTypeException e) {
			log.error(e.getMessage(), e);
			throw new NoSuchGroupException(e.getMessage(), e);
		} catch (javax.jcr.lock.LockException e) {
			log.error(e.getMessage(), e);
			throw new LockException(e.getMessage(), e);
		} catch (javax.jcr.PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}
		
		log.debug("removeGroup: void");
	}

	@Override
	public List<PropertyGroup> getGroups(String token, String nodePath) throws IOException, ParseException, 
			PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getGroups({}, {})", token, nodePath);
		ArrayList<PropertyGroup> ret = new ArrayList<PropertyGroup>();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Node documentNode = session.getRootNode().getNode(nodePath.substring(1));
			NodeType[] nt = documentNode.getMixinNodeTypes();
			Map<PropertyGroup, List<FormElement>> pgf = FormUtils.parsePropertyGroupsForms(Config.PROPERTY_GROUPS_XML);
			
			// Only return registered property definitions
			for (int i=0; i<nt.length; i++) {
				if (nt[i].getName().startsWith(PropertyGroup.GROUP+":")) {
					for (Iterator<PropertyGroup> it = pgf.keySet().iterator(); it.hasNext(); ) {
						PropertyGroup pg = it.next();
						
						if (pg.getName().equals(nt[i].getName())) {
							ret.add(pg);
						}
					}
				}
			}
		} catch (javax.jcr.PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}
		
		log.debug("getGroups: {}", ret);
		return ret;
	}

	@Override
	public List<PropertyGroup> getAllGroups(String token) throws IOException, ParseException,
			RepositoryException, DatabaseException {
		log.debug("getAllGroups({})", token);
		ArrayList<PropertyGroup> ret = new ArrayList<PropertyGroup>();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			NodeTypeManager ntm = session.getWorkspace().getNodeTypeManager();
			Map<PropertyGroup, List<FormElement>> pgf = FormUtils.parsePropertyGroupsForms(Config.PROPERTY_GROUPS_XML);
			
			// Only return registered property definitions
			for (NodeTypeIterator nti = ntm.getMixinNodeTypes(); nti.hasNext();) {
				NodeType nt = nti.nextNodeType();
				
				if (nt.getName().startsWith(PropertyGroup.GROUP + ":")) {
					for (Iterator<PropertyGroup> it = pgf.keySet().iterator(); it.hasNext(); ) {
						PropertyGroup pg = it.next();
						
						if (pg.getName().equals(nt.getName())) {
							ret.add(pg);
						}
					}
				}
			}
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}
		
		log.debug("getAllGroups: {}", ret);
		return ret;
	}

	@Override
	public List<FormElement> getProperties(String token, String nodePath, String grpName) throws IOException,
			ParseException, NoSuchGroupException, PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("getProperties({}, {}, {})", new Object[] { token, nodePath, grpName });
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Map<PropertyGroup, List<FormElement>> pgfs = FormUtils.parsePropertyGroupsForms(Config.PROPERTY_GROUPS_XML);
			List<FormElement> pgf = FormUtils.getPropertyGroupForms(pgfs, grpName);
			Node documentNode = session.getRootNode().getNode(nodePath.substring(1));
			NodeTypeManager ntm = session.getWorkspace().getNodeTypeManager();
			NodeType nt = ntm.getNodeType(grpName);
			PropertyDefinition[] pd = nt.getDeclaredPropertyDefinitions();
			
			for (FormElement fe : pgf) {
				for (int i=0; i < pd.length; i++) {
					// Only return registered property definitions
					if (fe.getName().equals(pd[i].getName())) {
						try {
							Property prop = documentNode.getProperty(pd[i].getName());
		 					
							if (fe instanceof Select && ((Select) fe).getType().equals(Select.TYPE_MULTIPLE) 
									&& pd[i].isMultiple()) {
								Value[] values = prop.getValues();
								
								for (int j=0; j<values.length; j++) {
									for (Option opt : ((Select) fe).getOptions()) {
										if (opt.getValue().equals(values[j].getString())) {
											opt.setSelected(true);
										}
									}
								}						
							} else if (!pd[i].isMultiple()) {
								Value value = prop.getValue();
								
								if (fe instanceof Input) {
									((Input) fe).setValue(value.getString());
								} else if (fe instanceof SuggestBox) {
									((SuggestBox) fe).setValue(value.getString());
								} else if (fe instanceof CheckBox) {
									((CheckBox) fe).setValue(Boolean.parseBoolean(value.getString()));
								} else if (fe instanceof TextArea) {
									((TextArea) fe).setValue(value.getString());
								} else if (fe instanceof Select) {
									for (Option opt : ((Select) fe).getOptions()) {
										if (opt.getValue().equals(value.getString())) {
											opt.setSelected(true);
										}
									}
								} else {
									throw new ParseException("Unknown property definition: " + pd[i].getName());
								}
							} else {
								throw new ParseException("Inconsistent property definition: " + pd[i].getName());
							}
						} catch (javax.jcr.PathNotFoundException e) {
							// Maybe the property is not found because was added after the assignment
							// throw new RepositoryException("Requested property not found: "+e.getMessage());
						}
					}
				}
			}
			
			// Activity log
			UserActivity.log(session.getUserID(), "GET_PROPERTY_GROUP_PROPERTIES", documentNode.getUUID(), grpName+", "+pgf+", "+nodePath);
			
			log.debug("getProperties: {}", pgf);
			return pgf;
		} catch (javax.jcr.nodetype.NoSuchNodeTypeException e) {
			log.error(e.getMessage(), e);
			throw new NoSuchGroupException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}
	}

	@Override
	public void setProperties(String token, String nodePath, String grpName, List<FormElement> properties)
			throws IOException, ParseException, NoSuchPropertyException, NoSuchGroupException, LockException,
			PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("setProperties({}, {}, {}, {})", new Object[] { token, nodePath, grpName, properties });
		Node documentNode = null;
		Session session = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			NodeTypeManager ntm = session.getWorkspace().getNodeTypeManager();
			NodeType nt = ntm.getNodeType(grpName);
			PropertyDefinition[] pd = nt.getDeclaredPropertyDefinitions();
			documentNode = session.getRootNode().getNode(nodePath.substring(1));
			
			synchronized (documentNode) {
				// Maybe the property is not found because was added after the assignment,
				// so we check if there are any missing node property, and then will remove
				// and add the mixing again
				for (FormElement fe : properties) {
					for (int i=0; i < pd.length; i++) {
						if (fe.getName().equals(pd[i].getName())) {
							if (documentNode.isNodeType(grpName) && !documentNode.hasProperty(pd[i].getName())) {
								documentNode.removeMixin(grpName);
								documentNode.addMixin(grpName);
							}
						}
					}
				}
				
				// Now we can safely set all property values.
				for (FormElement fe : properties) {
					for (int i=0; i < pd.length; i++) {
						// Only return registered property definitions
						if (fe.getName().equals(pd[i].getName())) {
							try {
								BasePropertyGroupModule.setPropertyValue(documentNode, pd[i], fe);
							} catch (javax.jcr.PathNotFoundException e) {
								throw new RepositoryException("Requested property not found: "+e.getMessage());
							}
						}
					}
				}
			}
			
			documentNode.save();
			
			// Activity log
			UserActivity.log(session.getUserID(), "SET_PROPERTY_GROUP_PROPERTIES", documentNode.getUUID(), grpName+", "+properties+", "+nodePath);
		} catch (javax.jcr.nodetype.NoSuchNodeTypeException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(documentNode);
			throw new NoSuchPropertyException(e.getMessage(), e);
		} catch (javax.jcr.lock.LockException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(documentNode);
			throw new LockException(e.getMessage(), e);
		} catch (javax.jcr.PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(documentNode);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(documentNode);
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(documentNode);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}
		
		log.debug("setProperties: void");
	}
	
	@Override
	public List<FormElement> getPropertyGroupForm(String token, String grpName) throws IOException,
			ParseException, RepositoryException, DatabaseException {
		log.debug("getPropertyGroupForm({}, {})", token, grpName);
		List<FormElement> ret = new ArrayList<FormElement>();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			NodeTypeManager ntm = session.getWorkspace().getNodeTypeManager();
			NodeType nt = ntm.getNodeType(grpName);
			PropertyDefinition[] pd = nt.getDeclaredPropertyDefinitions();
			Map<PropertyGroup, List<FormElement>> pgf = FormUtils.parsePropertyGroupsForms(Config.PROPERTY_GROUPS_XML);
			List<FormElement> tmp = FormUtils.getPropertyGroupForms(pgf, grpName);
			
			// Only return registered property definitions
			for (FormElement fe : tmp) {
				for (int i=0; i < pd.length; i++) {
					if (fe.getName().equals(pd[i].getName())) {
						ret.add(fe);
					}
				}
			}
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}
		
		log.debug("getPropertyGroupForm: {}", ret);
		return ret;
	}

	@Override
	public boolean hasGroup(String token, String nodePath, String grpName) throws IOException,
			ParseException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("hasGroup({}, {}, {})", new Object[] { token, nodePath, grpName });
		boolean ret = false;
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Node documentNode = session.getRootNode().getNode(nodePath.substring(1));
			NodeType[] nt = documentNode.getMixinNodeTypes();
			Map<PropertyGroup, List<FormElement>> pgf = FormUtils.parsePropertyGroupsForms(Config.PROPERTY_GROUPS_XML);
			
			// Only return registered property definitions
			for (int i=0; i<nt.length; i++) {
				if (nt[i].getName().startsWith(PropertyGroup.GROUP+":")) {
					for (Iterator<PropertyGroup> it = pgf.keySet().iterator(); it.hasNext(); ) {
						PropertyGroup pg = it.next();
						
						if (pg.getName().equals(nt[i].getName())) {
							ret = true;
							break;
						}
					}
				}
			}
		} catch (javax.jcr.PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}
		
		log.debug("hasGroup: {}", ret);
		return ret;
	}
}
