/**
 * 
 */
package com.openkm.test;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.security.auth.Subject;

import org.apache.jackrabbit.core.HierarchyManager;
import org.apache.jackrabbit.core.ItemId;
import org.apache.jackrabbit.core.NodeId;
import org.apache.jackrabbit.core.PropertyId;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.core.security.AMContext;
import org.apache.jackrabbit.core.security.AccessManager;
import org.apache.jackrabbit.core.security.UserPrincipal;
import org.apache.jackrabbit.core.security.authorization.AccessControlProvider;
import org.apache.jackrabbit.core.security.authorization.WorkspaceAccessManager;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Paco Avila
 * 
 */
public class MyAccessManagerLockAccessDenied implements AccessManager {
	private static Logger log = LoggerFactory.getLogger(MyAccessManagerLockAccessDenied.class);
	private Subject subject = null;
	private HierarchyManager hierMgr = null;
	private static final int READ = 1;
	private static final int WRITE = 2;
	private static final int REMOVE = 4;
	public static boolean CAN_WRITE = true;
	
	@Override
	public void init(AMContext context) throws AccessDeniedException, Exception {
		log.debug("init(" + context + ")");
		subject = context.getSubject();
		log.debug("##### "+subject.getPrincipals());
		hierMgr = context.getHierarchyManager();
		log.debug("init: void");
	}

	@Override
	public void close() throws Exception {
		log.debug("close()");
	}

	@Override
	public void checkPermission(ItemId id, int permissions)
			throws AccessDeniedException, ItemNotFoundException,
			RepositoryException {
		log.debug("checkPermission()");
	}

	@Override
	public boolean isGranted(ItemId id, int permissions)
			throws ItemNotFoundException, RepositoryException {
		log.debug("isGranted(" + subject.getPrincipals() + ", " + id + ", "
				+ (permissions == READ ? "READ"
						: (permissions == WRITE ? "WRITE"
								: (permissions == REMOVE ? "REMOVE"
										: "NONE"))) + ")");
		boolean access = false;
		Session systemSession = DummyLockAccessDenied.getSystemSession();
		
		if (subject.getPrincipals().contains(new UserPrincipal(systemSession.getUserID()))) {
			// Si es del tipo SYSTEM dar permisos totales
			// Es necesario para que no caiga en un bucle infinito
			access = true;
		} else {
			NodeId nodeId = null;
			log.debug(subject.getPrincipals()+" Item Id: "+id);
						
			// Workaround because of transiente node visibility
			try {
				log.debug(subject.getPrincipals()+" Item Path: "+hierMgr.getPath(id));
			} catch (ItemNotFoundException e) {
				log.warn(subject.getPrincipals()+" hierMgr.getPath() > ItemNotFoundException: "+e.getMessage());
			}

			if (id instanceof NodeId) {
				nodeId = (NodeId) id;
				log.debug(subject.getPrincipals()+" This is a NODE");
			} else {
				PropertyId propertyId = (PropertyId) id;
				nodeId = propertyId.getParentId();
				log.debug(subject.getPrincipals()+" This is a PROPERTY");
			}
				
			if (hierMgr.getPath(nodeId).denotesRoot()) {
				// Root node has full access
				access = true;
			} else {
				Node node = null;
				
				// Workaround because of transiente node visibility
				try {
					node = ((SessionImpl) systemSession).getNodeById(nodeId);
				} catch (ItemNotFoundException e1) {
					log.warn(subject.getPrincipals()+" systemSession.getNodeById() > ItemNotFoundException: "+e1.getMessage());
				}
				
				if (node == null) {
					access = true;
				} else {
					log.debug(subject.getPrincipals()+" Node Name: " + node.getPath());
					log.debug(subject.getPrincipals()+" Node Type: " + node.getPrimaryNodeType().getName());
					
					if (permissions == READ) {
						for (PropertyIterator pi = node.getProperties(); pi.hasNext(); ) {
							Property property = (Property) pi.nextProperty();
							log.debug("Property: " + property.getName());
						}
						access = true;
					} else if (permissions == WRITE || permissions == REMOVE) {
						for (PropertyIterator pi = node.getProperties(); pi.hasNext(); ) {
							Property property = (Property) pi.nextProperty();
							log.debug("Property: " + property.getName());
						}
						if (CAN_WRITE) {
							access = true;
						}
					}
				}
			}
		}

		// Workaround because of transiente node visibility
		try {
			log.debug(subject.getPrincipals()+" Path: " + hierMgr.getPath(id));
		} catch (ItemNotFoundException e) {
			log.warn(subject.getPrincipals()+" hierMgr.getPath() > ItemNotFoundException: "+e.getMessage());
		}
		
		log.debug(subject.getPrincipals()+" isGranted "+(permissions == READ ? "READ" 
				: (permissions == WRITE ? "WRITE" 
						: (permissions == REMOVE ? "REMOVE" 
								: "NONE")))+": " + access);
		log.debug("-------------------------------------");

		return access;
	}

	@Override
	public boolean canAccess(String workspaceName)
			throws NoSuchWorkspaceException, RepositoryException {
		boolean access = true;
		log.debug("canAccess(" + workspaceName + ")");
		log.debug("canAccess: " + access);
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

	// @Override
	// TODO Enable when using jackrabbit 1.6
	public void checkPermission(Path arg0, int arg1) throws AccessDeniedException, RepositoryException {
	}
}
