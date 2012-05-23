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

package com.openkm.core;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.security.auth.Subject;

import org.apache.jackrabbit.core.ItemId;
import org.apache.jackrabbit.core.NodeId;
import org.apache.jackrabbit.core.PropertyId;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.core.security.AMContext;
import org.apache.jackrabbit.core.security.AccessManager;
import org.apache.jackrabbit.core.security.authorization.AccessControlProvider;
import org.apache.jackrabbit.core.security.authorization.Permission;
import org.apache.jackrabbit.core.security.authorization.WorkspaceAccessManager;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Document;
import com.openkm.bean.Note;
import com.openkm.module.direct.DirectRepositoryModule;

/**
 * @author pavila
 * 
 */
public class OKMAccessManager implements AccessManager {
	private static Logger log = LoggerFactory.getLogger(OKMAccessManager.class);
	private AMContext context;
	private Subject subject = null;
	private String principalUser = null;
	private Set<String> principalRoles = null;

	@SuppressWarnings("unused")
	@Override
	public void init(AMContext context) throws AccessDeniedException, Exception {
		log.debug("init({})", context);
		this.context = context;
		subject = context.getSubject();
		principalRoles = new HashSet<String>();

		for (Iterator<java.security.Principal> it = subject.getPrincipals().iterator(); it.hasNext();) {
			Object obj = it.next();
			log.debug("##### {}", obj.getClass());

			if (obj instanceof org.apache.jackrabbit.core.security.principal.EveryonePrincipal) {
				// Needed for test.
				log.debug("o.a.j.c.s.p.EveryonePrincipal: {}", obj);
				org.apache.jackrabbit.core.security.principal.EveryonePrincipal everyonePrincipal = (org.apache.jackrabbit.core.security.principal.EveryonePrincipal) obj;
			} else if (obj instanceof org.apache.jackrabbit.core.security.UserPrincipal) {
				// Needed for test.
				log.debug("o.a.j.c.s.UserPrincipal: {}", obj);
				org.apache.jackrabbit.core.security.UserPrincipal userPrincipal = (org.apache.jackrabbit.core.security.UserPrincipal) obj;
				principalUser = userPrincipal.getName();
				principalRoles.add(Config.DEFAULT_USER_ROLE);
			} else if (obj instanceof java.security.acl.Group) {
				log.debug("j.s.a.Group: {}", obj);
				java.security.acl.Group group = (java.security.acl.Group) obj;
				for (Enumeration<? extends java.security.Principal> groups = group.members(); groups
						.hasMoreElements();) {
					java.security.Principal rol = (java.security.Principal) groups.nextElement();
					log.debug("Rol: {}", rol.getName());
					principalRoles.add(rol.getName());
				}
			} else if (obj instanceof java.security.Principal) {
				log.debug("j.s.Principal: {}", obj);
				java.security.Principal principal = (java.security.Principal) obj;
				principalUser = principal.getName();
			}
		}

		log.debug("PrincipalRoles: " + principalRoles);
		log.debug("init: void");
	}

	@Override
	public void init(AMContext context, AccessControlProvider acProvider, WorkspaceAccessManager wspAccessMgr)
			throws AccessDeniedException, Exception {
		log.debug("init({}, {}, {}", new Object[] { context, acProvider, wspAccessMgr });
		init(context);
	}

	@Override
	public void close() throws Exception {
		log.debug("close()");
	}

	@Override
	public boolean canAccess(String workspaceName) throws RepositoryException {
		//log.info("canAccess({})", workspaceName);
		return true;
	}

	@Override
	public boolean canRead(Path itemPath) throws RepositoryException {
		//log.info("canRead({})", itemPath);
		//return isGranted(itemPath, Permission.READ);
		return true;
	}

	@Override
	// This method is deprecated in Jackrabbit 1.5.0
	public void checkPermission(ItemId id, int permissions) throws AccessDeniedException,
			ItemNotFoundException, RepositoryException {
		//log.info("deprecated - checkPermission({}, {})", id, permissionsToString(permissions));
		//if (isGranted(id, deprecatedPermissionsToNewApi(permissions))) {
			//return;
		//}
		//throw new AccessDeniedException("Permission denied!");
	}

	@Override
	public void checkPermission(Path absPath, int permissions) throws AccessDeniedException,
			RepositoryException {
		//log.info("used in jackrabbit 1.6 - checkPermission({}, {})", absPath, permissions);
		//if (isGranted(absPath, permissions)) {
			//return;
		//}
		//throw new AccessDeniedException("Permission denied!");
	}

	@Override
	// This method is deprecated in Jackrabbit 1.5.0
	public boolean isGranted(ItemId id, int permissions) throws ItemNotFoundException, RepositoryException {
		log.debug("deprecated - isGranted({}, {} => {})", new Object[] { id, permissions,
				permissionsToString(deprecatedPermissionsToNewApi(permissions)) });
		Path path = context.getHierarchyManager().getPath(id);
		boolean access = isGranted(path, deprecatedPermissionsToNewApi(permissions));
		log.debug("deprecated - isGranted: {}", access);
		return access;
	}

	@Override
	public boolean isGranted(Path parentPath, Name childName, int permissions) throws RepositoryException {
		//log.info("isGranted({}, {}, {} => {})", new Object[] { parentPath, childName, permissions,
				//permissionsToString(permissions) });
		//Path p = PathFactoryImpl.getInstance().create(parentPath, childName, true);
		//boolean access = isGranted(p, permissions);
		//log.info("deprecated - isGranted: {}", access);
		return true;
	}

	@Override
	public boolean isGranted(Path absPath, int permissions) throws RepositoryException {
		log.debug("isGranted({}, {} => {})", new Object[] { absPath, permissions, permissionsToString(permissions) });
		boolean access = checkAccess(absPath, permissions);
		log.debug("isGranted: {}", access);
		return access;
	}

	/**
	 * Check access
	 */
	private boolean checkAccess(Path absPath, int permissions) throws RepositoryException {
		log.debug("checkAccess({}, {} => {})", new Object[] { absPath, permissions, permissionsToString(permissions) });
		Session systemSession = DirectRepositoryModule.getSystemSession();
		boolean access = false;

		if (principalRoles.contains(Config.DEFAULT_ADMIN_ROLE)) {
			// An user with AdminRole has total access
			access = true;
		} else {
			log.debug("{} Path: {}", subject.getPrincipals(), absPath);
			NodeId nodeId = context.getHierarchyManager().resolveNodePath(absPath);
			
			if (nodeId != null) {
				log.debug("{} This is a NODE", subject.getPrincipals());
			} else {
				PropertyId propertyId = context.getHierarchyManager().resolvePropertyPath(absPath);
				
				if (propertyId != null) {
					log.debug("{} This is a PROPERTY", subject.getPrincipals());
					nodeId = propertyId.getParentId();
				} else {
					// Seems to be a just-removed property
					log.debug("{} This is a UNKNOWN: {}", subject.getPrincipals(), absPath);
					Path ancestor = absPath.getAncestor(1);
					log.debug("UNKNOWN ancestor: {}", ancestor);
					nodeId = context.getHierarchyManager().resolveNodePath(ancestor);
				}
			}

			if (access || absPath.denotesRoot() || nodeId == null) {
				// Root node has full access
				access = true;
			} else {
				try {
					Node node = ((SessionImpl) systemSession).getNodeById(nodeId);

					if (node == null) {
						access = true;
					} else {
						log.debug("{} Node Name: {}", subject.getPrincipals(), node.getPath());
						log.debug("{} Node Type: {}", subject.getPrincipals(), node.getPrimaryNodeType()
								.getName());

						if (node.isNodeType(Document.CONTENT_TYPE)) {
							log.debug("{} Node is CONTENT_TYPE", subject.getPrincipals());
							node = node.getParent();
							log.debug("{} Real -> {}", subject.getPrincipals(), node.getPath());
						} else if (node.isNodeType(Note.LIST_TYPE)) {
							log.debug("{} Node is NOTE_LIST_TYPE", subject.getPrincipals());
							node = node.getParent();
							log.debug("{} Real -> {}", subject.getPrincipals(), node.getPath());
						} else if (node.isNodeType(Note.TYPE)) {
							log.debug("{} Node is NOTE_TYPE", subject.getPrincipals());
							node = node.getParent().getParent();
							log.debug("{} Real -> {}", subject.getPrincipals(), node.getPath());
						} else if (node.isNodeType("nt:frozenNode")) {
							log.debug("{} Node is FROZEN_NODE", subject.getPrincipals());
							String realNodeId = node.getProperty("jcr:frozenUuid").getString();
							node = systemSession.getNodeByUUID(realNodeId).getParent();
							log.debug("{} Real -> {}", subject.getPrincipals(), node.getPath());
						} else if (node.isNodeType("nt:version")) {
							log.debug("{} Node is VERSION", subject.getPrincipals());
							Node frozenNode = node.getNode("jcr:frozenNode");
							log.debug("{} Frozen node -> {}", subject.getPrincipals(), frozenNode.getPath());
							String realNodeId = frozenNode.getProperty("jcr:frozenUuid").getString();
							node = systemSession.getNodeByUUID(realNodeId).getParent();
							log.debug("{} Real -> {}", subject.getPrincipals(), node.getPath());
						} else if (node.isNodeType("nt:versionHistory")) {
							log.debug("{} Node is VERSION_HISTORY", subject.getPrincipals());
							String realNodeId = node.getProperty("jcr:versionableUuid").getString();
							node = systemSession.getNodeByUUID(realNodeId).getParent();
							log.debug("{} Real -> {}", subject.getPrincipals(), node.getPath());
						}

						if ((permissions & Permission.READ) != 0) {
							// Check for READ permissions
							try {
								access = checkProperties(node, 
										com.openkm.bean.Permission.USERS_READ,
										com.openkm.bean.Permission.ROLES_READ);
							} catch (PathNotFoundException e) {
								log.warn("{} PathNotFoundException({}) in {}", new Object[] {
										subject.getPrincipals(), e.getMessage(),
										node.getPrimaryNodeType().getName() });
								access = true;
							}
						} else if ((permissions & Permission.ADD_NODE) != 0 || 
								(permissions & Permission.SET_PROPERTY) != 0) {
							// Check for WRITE permissions
							try {
								access = checkProperties(node, 
										com.openkm.bean.Permission.USERS_WRITE,
										com.openkm.bean.Permission.ROLES_WRITE);
							} catch (PathNotFoundException e) {
								log.debug("{} PropertyNotFoundException({}) in {}", new Object[] {
										subject.getPrincipals(), e.getMessage(),
										node.getPrimaryNodeType().getName() });
								access = true;
							}
						} else if ((permissions & Permission.REMOVE_NODE) != 0 ||
								(permissions & Permission.REMOVE_PROPERTY) != 0) {
							// Check for DELETE permissions
							try {
								access = checkProperties(node, 
										com.openkm.bean.Permission.USERS_DELETE,
										com.openkm.bean.Permission.ROLES_DELETE);
							} catch (PathNotFoundException e) {
								log.debug("{} PropertyNotFoundException({}) in {}", new Object[] {
										subject.getPrincipals(), e.getMessage(),
										node.getPrimaryNodeType().getName() });
								access = true;
							}
						} else if ((permissions & Permission.MODIFY_AC) != 0) {
							// Check for PERMISSION permissions
							try {
								access = checkProperties(node, 
										com.openkm.bean.Permission.USERS_SECURITY,
										com.openkm.bean.Permission.ROLES_SECURITY);
							} catch (PathNotFoundException e) {
								log.debug("{} PropertyNotFoundException({}) in {}", new Object[] {
										subject.getPrincipals(), e.getMessage(),
										node.getPrimaryNodeType().getName() });
								access = true;
							}
						}
					}
				} catch (ItemNotFoundException e) {
					log.debug("{} systemSession.getNodeById() > ItemNotFoundException: {}", subject
							.getPrincipals(), e.getMessage());
					access = true;
				}
			}
		}

		log.debug("checkAccess: "+access);
		return access;
	}
	
	/**
	 * Check access properties
	 */
	private boolean checkProperties(Node node, String userProperty, String roleProperty) throws 
			ValueFormatException, RepositoryException, PathNotFoundException {
		log.debug("checkWrite({})", node);
		// Propiedad no definida en nt:versionHistory, nt:version y okm:resource
		Value[] users = node.getProperty(userProperty).getValues();
		boolean access = false;

		for (int i = 0; i < users.length; i++) {
			log.debug("{} User: {}", userProperty, users[i].getString());

			if (principalUser.equals(users[i].getString())) {
				access = true;
				break;
			}
		}

		// If there is no user specific access, try with roles
		if (!access) {
			// Propiedad no definida en nt:versionHistory, nt:version y okm:resource
			Value[] roles = node.getProperty(roleProperty).getValues();

			for (int i = 0; i < roles.length; i++) {
				log.debug("{} Rol: {}", roleProperty, roles[i].getString());

				if (principalRoles.contains(roles[i].getString())) {
					access = true;
					break;
				}
			}
		}

		log.debug("checkWrite: {}", access);
		return access;
	}

	/**
	 * 
	 */
	@SuppressWarnings("deprecation")
	private int deprecatedPermissionsToNewApi(int permissions) {
		boolean read = (permissions & READ) != 0;
		boolean write = (permissions & WRITE) != 0;
		boolean remove = (permissions & REMOVE) != 0;
		int result = 0;

		if (read) {
			result = result | Permission.READ;
		}

		if (write) {
			result = result | Permission.ADD_NODE;
			result = result | Permission.SET_PROPERTY;
		}

		if (remove) {
			result = result | Permission.REMOVE_NODE;
			result = result | Permission.REMOVE_PROPERTY;
		}

		return result;
	}

	/**
	 * 
	 */
	private String permissionsToString(int permissions) {
		StringBuilder sb = new StringBuilder();

		if (!(permissions == Permission.NONE)) {
			// if ((actions & Permission.ALL) != 0) {
			// sb.append("all ");
			// }
			if ((permissions & Permission.ADD_NODE) != 0) {
				sb.append("add_node ");
			}
			if ((permissions & Permission.READ) != 0) {
				sb.append("read ");
			}
			if ((permissions & Permission.REMOVE_NODE) != 0) {
				sb.append("remove_node ");
			}
			if ((permissions & Permission.REMOVE_PROPERTY) != 0) {
				sb.append("remove_property ");
			}
			if ((permissions & Permission.SET_PROPERTY) != 0) {
				sb.append("set_property ");
			} 
			if ((permissions & Permission.READ_AC) != 0) {
				sb.append("read_ac ");
			}
			if ((permissions & Permission.MODIFY_AC) != 0) {
				sb.append("modify_ac ");
			}
		}

		return sb.toString();
	}
}
