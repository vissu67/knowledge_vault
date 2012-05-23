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

package com.openkm.cache;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.security.auth.Subject;

import org.apache.jackrabbit.core.HierarchyManager;
import org.apache.jackrabbit.core.ItemId;
import org.apache.jackrabbit.core.NodeId;
import org.apache.jackrabbit.core.PropertyId;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.core.security.AMContext;
import org.apache.jackrabbit.core.security.AccessManager;
import org.apache.jackrabbit.core.security.authorization.AccessControlProvider;
import org.apache.jackrabbit.core.security.authorization.WorkspaceAccessManager;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Document;
import com.openkm.bean.Note;
import com.openkm.bean.Permission;
import com.openkm.bean.cache.NodePermissions;
import com.openkm.core.Config;
import com.openkm.module.direct.DirectRepositoryModule;

/**
 * @author pavila
 * 
 */
public class OKMAccessManager implements AccessManager {
	private static Logger log = LoggerFactory.getLogger(OKMAccessManager.class);
	private static final boolean DEBUG = true;
	private Subject subject = null;
	private HierarchyManager hierMgr = null;
	private String principalUser = null;
	private Set<String> principalRoles = null;

	@Override
	public void init(AMContext context) throws AccessDeniedException {
		log.info("init(" + context + ")");
		subject = context.getSubject();
		principalRoles = new HashSet<String>();
		log.info("##### " + subject.getPrincipals());

		log.info("##### ##### ##### ##### ##### ##### ##### ");
		for (Iterator<java.security.Principal> it = subject.getPrincipals().iterator(); it.hasNext();) {
			Object obj = it.next();
			log.info("##### " + obj.getClass());

			if (obj instanceof java.security.acl.Group) {
				java.security.acl.Group group = (java.security.acl.Group) obj;
				log.info("Group: " + group.getName());
				for (Enumeration<? extends java.security.Principal> groups = group.members(); groups.hasMoreElements();) {
					java.security.Principal rol = (java.security.Principal) groups.nextElement();
					log.info("Rol: " + rol.getName());
					principalRoles.add(rol.getName());
				}
			} else if (obj instanceof java.security.Principal) {
				java.security.Principal principal = (java.security.Principal) obj;
				principalUser = principal.getName();
				log.debug("Principal: " + principalUser);
			} else if (obj instanceof org.apache.jackrabbit.core.security.UserPrincipal) {
				// TODO Esto es sólo para que funcione en modo shell para el
				// desarrollo
				// de interfaz web.
				org.apache.jackrabbit.core.security.UserPrincipal userPrincipal = (org.apache.jackrabbit.core.security.UserPrincipal) obj;
				principalUser = userPrincipal.getName();
				principalRoles.add(Config.DEFAULT_USER_ROLE);
				log.debug("UserPrincipal: " + principalUser);
			}
		}

		log.debug("PrincipalUser: " + principalUser);
		log.debug("PrincipalRoles: " + principalRoles);
		log.debug("##### ##### ##### ##### ##### ##### ##### ");

		hierMgr = context.getHierarchyManager();
		log.debug("init: void");
	}

	@Override
	public void close() throws Exception {
		if (DEBUG) log.debug("close()");
	}

	@Override
	public void checkPermission(ItemId id, int permissions)
			throws AccessDeniedException, ItemNotFoundException,
			RepositoryException {
		if (DEBUG) log.debug("checkPermission()");
		// TODO Auto-generated method stub
		if (DEBUG) log.debug("checkPermission: void");
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isGranted(ItemId id, int permissions)
			throws ItemNotFoundException, RepositoryException {
		if (DEBUG) log.debug("isGranted("+ subject.getPrincipals()+ ", "+ id+ ", "
							+ (permissions == AccessManager.READ ? "READ"
									: (permissions == AccessManager.WRITE ? "WRITE"
											: (permissions == AccessManager.REMOVE ? "REMOVE"
													: "NONE"))) + ")");
		boolean access = false;

		if (principalRoles.contains(Config.DEFAULT_ADMIN_ROLE)) {
			// An user with AdminRole has total access
			access = true;
		} else {
			NodeId nodeId = null;
			if (DEBUG) log.debug(subject.getPrincipals() + " Item Id: " + id);

			// Workaround because of transiente node visibility
			try {
				if (DEBUG) log.debug(subject.getPrincipals() + " Item Path: " + hierMgr.getPath(id));
			} catch (ItemNotFoundException e) {
				access = true;
				if (DEBUG) log.debug(subject.getPrincipals() + " hierMgr.getPath() > ItemNotFoundException: " + e.getMessage());
			}

			// Check for node id or property id
			if (id instanceof NodeId) {
				nodeId = (NodeId) id;
				if (DEBUG) log.debug(subject.getPrincipals() + " This is a NODE");
			} else {
				PropertyId propertyId = (PropertyId) id;
				nodeId = propertyId.getParentId();
				if (DEBUG) log.debug(subject.getPrincipals() + " This is a PROPERTY");
			}

			if (access || hierMgr.getPath(nodeId).denotesRoot()) {
				// Root node has full access
				access = true;
			} else {
				NodePermissions nPerms = NodePermissionsManager.get(nodeId);

				if (nPerms == null) {
					Session systemSession = DirectRepositoryModule.getSystemSession();
					Node node = null;

					// Workaround because of transiente node visibility
					try {
						node = ((SessionImpl) systemSession).getNodeById(nodeId);
					} catch (ItemNotFoundException e1) {
						if (DEBUG)log.debug(subject.getPrincipals() + " systemSession.getNodeById() > ItemNotFoundException: " + e1.getMessage());
					}

					if (node == null) {
						access = true;
					} else {
						if (DEBUG) log.debug(subject.getPrincipals() + " Node Name: " + node.getPath());
						if (DEBUG) log.debug(subject.getPrincipals() + " Node Type: " + node.getPrimaryNodeType().getName());

						if (node.isNodeType(Document.CONTENT_TYPE)) {
							if (DEBUG) log.debug(subject.getPrincipals() + " Node is CONTENT_TYPE");
							node = node.getParent();
							if (DEBUG) log.debug(subject.getPrincipals() + " Real -> " + node.getPath());
						} else if (node.isNodeType(Note.LIST_TYPE)) {
							if (DEBUG) log.debug(subject.getPrincipals()+" Node is NOTE_LIST_TYPE");
							node = node.getParent();
							if (DEBUG) log.debug(subject.getPrincipals()+" Real -> "+node.getPath());
						} else if (node.isNodeType(Note.TYPE)) {
							if (DEBUG) log.debug(subject.getPrincipals()+" Node is NOTE_TYPE");
							node = node.getParent().getParent();
						} else if (node.isNodeType("nt:frozenNode")) {
							if (DEBUG) log.debug(subject.getPrincipals() + " Node is FROZEN_NODE");
							String realNodeId = node.getProperty("jcr:frozenUuid").getString();
							node = systemSession.getNodeByUUID(realNodeId).getParent();
							if (DEBUG) log.debug(subject.getPrincipals() + " Real -> " + node.getPath());
						} else if (node.isNodeType("nt:version")) {
							log.debug(subject.getPrincipals() + " Node is VERSION");
							Node frozenNode = node.getNode("jcr:frozenNode");
							log.debug(subject.getPrincipals() + " el congelado -> " + frozenNode.getPath());
							String realNodeId = frozenNode.getProperty("jcr:frozenUuid").getString();

							try {
								node = systemSession.getNodeByUUID(realNodeId).getParent();
								if (DEBUG) log.debug(subject.getPrincipals() + " Real -> " + node.getPath());
							} catch (javax.jcr.ItemNotFoundException e) {
								if (DEBUG) log.debug(subject.getPrincipals() + " **************");
								if (DEBUG) log.debug(subject.getPrincipals() + " -> " + e.getMessage());
							}
						} else if (node.isNodeType("nt:versionHistory")) {
							if (DEBUG) log.debug(subject.getPrincipals() + " Node is VERSION_HISTORY");
							String realNodeId = node.getProperty("jcr:versionableUuid").getString();

							try {
								node = systemSession.getNodeByUUID(realNodeId).getParent();
								if (DEBUG) log.debug(subject.getPrincipals() + " Real -> " + node.getPath());
							} catch (javax.jcr.ItemNotFoundException e) {
								if (DEBUG) log.debug(subject.getPrincipals() + " **************");
								if (DEBUG) log.debug(subject.getPrincipals() + " **************");
								if (DEBUG) log.debug(subject.getPrincipals() + " -> " + e.getMessage());
							}
						}
						
						// Put permissions into cache
						// READ
						HashSet<String> sUsersRead = new HashSet<String>();
						Value[] vUsersRead = node.getProperty(Permission.USERS_READ).getValues();
						for (int i = 0; i < vUsersRead.length; i++) sUsersRead.add(vUsersRead[i].getString());
						
						HashSet<String> sRolesRead = new HashSet<String>();
						Value[] vRolesRead = node.getProperty(Permission.ROLES_READ).getValues();
						for (int i = 0; i < vRolesRead.length; i++) sRolesRead.add(vRolesRead[i].getString());												
						
						// WRITE
						HashSet<String> sUsersWrite = new HashSet<String>();
						Value[] vUsersWrite = node.getProperty(Permission.USERS_WRITE).getValues();
						for (int i = 0; i < vUsersWrite.length; i++) sUsersWrite.add(vUsersWrite[i].getString());
						
						HashSet<String> sRolesWrite = new HashSet<String>();
						Value[] vRolesWrite = node.getProperty(Permission.ROLES_WRITE).getValues();
						for (int i = 0; i < vRolesWrite.length; i++) sRolesWrite.add(vRolesWrite[i].getString());
						
						// DELETE
						HashSet<String> sUsersDelete = new HashSet<String>();
						Value[] vUsersDelete = node.getProperty(Permission.USERS_DELETE).getValues();
						for (int i = 0; i < vUsersDelete.length; i++) sUsersDelete.add(vUsersDelete[i].getString());
						
						HashSet<String> sRolesDelete = new HashSet<String>();
						Value[] vRolesDelete = node.getProperty(Permission.ROLES_DELETE).getValues();
						for (int i = 0; i < vRolesDelete.length; i++) sRolesDelete.add(vRolesDelete[i].getString());
						
						// SECURITY
						HashSet<String> sUsersSecurity= new HashSet<String>();
						Value[] vUsersSecurity = node.getProperty(Permission.USERS_SECURITY).getValues();
						for (int i = 0; i < vUsersSecurity.length; i++) sUsersSecurity.add(vUsersSecurity[i].getString());
						
						HashSet<String> sRolesSecurity = new HashSet<String>();
						Value[] vRolesSecurity = node.getProperty(Permission.ROLES_SECURITY).getValues();
						for (int i = 0; i < vRolesSecurity.length; i++) sRolesSecurity.add(vRolesSecurity[i].getString());
						
						nPerms = new NodePermissions();
						nPerms.setUsersRead(sUsersRead);
						nPerms.setRolesRead(sRolesRead);
						nPerms.setUsersWrite(sUsersWrite);
						nPerms.setRolesWrite(sRolesWrite);
						nPerms.setUsersDelete(sUsersDelete);
						nPerms.setRolesDelete(sRolesDelete);
						nPerms.setUsersSecurity(sUsersSecurity);
						nPerms.setRolesSecurity(sRolesSecurity);
						
						NodePermissionsManager.put(nodeId, nPerms);
					}
				}
				
				if (permissions == AccessManager.READ) {
					access = checkRead(nPerms.getUsersRead(), nPerms.getRolesRead());
				} else if (permissions == AccessManager.WRITE || permissions == AccessManager.REMOVE) {
					access = checkWrite(nPerms.getUsersWrite(),	nPerms.getRolesWrite());
				}
			}
		}

		// Workaround because of transiente node visibility
		try {
			if (DEBUG) log.debug(subject.getPrincipals() + " Path: " + hierMgr.getPath(id));
		} catch (ItemNotFoundException e) {
			if (DEBUG) log.debug(subject.getPrincipals() + " hierMgr.getPath() > ItemNotFoundException: " + e.getMessage());
		}

		if (DEBUG) log.debug(subject.getPrincipals() + " isGranted "
							+ (permissions == AccessManager.READ ? "READ"
									: (permissions == AccessManager.WRITE ? "WRITE"
											: (permissions == AccessManager.REMOVE ? "REMOVE"
													: "NONE"))) + ": " + access);
		if (DEBUG) log.debug("-------------------------------------");
		return access;
	}

	@Override
	public boolean canAccess(String workspaceName)
			throws NoSuchWorkspaceException, RepositoryException {
		boolean access = true;
		if (DEBUG) log.debug("canAccess(" + workspaceName + ")");
		if (DEBUG) log.debug("canAccess: " + access);
		return access;
	}

	/**
	 *
	 */
	private boolean checkRead(Set<String> usersRead, Set<String> rolesRead) {
		if (DEBUG) log.debug("checkRead(" + usersRead + ", " + rolesRead + ")");
		boolean access = false;

		if (usersRead.contains(principalUser)) {
			access = true;
		} else {
			for (Iterator<String> it = principalRoles.iterator(); it.hasNext();) {
				if (rolesRead.contains(it.next())) {
					access = true;
					break;
				}
			}
		}

		if (DEBUG) log.debug("checkRead: " + access);
		return access;
	}

	/**
	 * 
	 */
	private boolean checkWrite(Set<String> usersWrite, Set<String> rolesWrite) {
		if (DEBUG) log.debug("checkWrite(" + usersWrite + ", " + rolesWrite + ")");
		boolean access = false;

		if (usersWrite.contains(principalUser)) {
			access = true;
		} else {
			for (Iterator<String> it = principalRoles.iterator(); it.hasNext();) {
				if (rolesWrite.contains(it.next())) {
					access = true;
					break;
				}
			}
		}

		if (DEBUG) log.debug("checkWrite: " + access);
		return access;
	}

	@Override
	public boolean canRead(Path arg0) throws RepositoryException {
		return false;
	}

	@Override
	public void init(AMContext arg0, AccessControlProvider arg1, WorkspaceAccessManager arg2)
			throws AccessDeniedException, Exception {
	}

	@Override
	public boolean isGranted(Path arg0, int arg1) throws RepositoryException {
		return false;
	}

	@Override
	public boolean isGranted(Path arg0, Name arg1, int arg2) throws RepositoryException {
		return false;
	}

	//@Override
	// TODO Enable @Override when use jackrabbit 1.6
	public void checkPermission(Path arg0, int arg1) throws AccessDeniedException, RepositoryException {
	}
}
