/**
 * 
 */
package com.openkm.test;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.core.ItemId;
import org.apache.jackrabbit.core.security.AMContext;
import org.apache.jackrabbit.core.security.AccessManager;
import org.apache.jackrabbit.core.security.authorization.AccessControlProvider;
import org.apache.jackrabbit.core.security.authorization.Permission;
import org.apache.jackrabbit.core.security.authorization.WorkspaceAccessManager;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.apache.jackrabbit.spi.commons.name.PathFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Paco Avila
 * 
 */
public class MyAccessManager implements AccessManager {
	private static Logger log = LoggerFactory.getLogger(MyAccessManager.class);
	private AMContext context;
	ThreadLocal<Boolean> alreadyInsideAccessManager = new ThreadLocal<Boolean>() {
		@Override
		protected Boolean initialValue() {
			return Boolean.FALSE;
		}
	};

	@Override
	public void init(AMContext context) throws AccessDeniedException, Exception {
		log.debug("init(" + context + ")");
		this.context = context;
	}

	@Override
	public void init(AMContext context, AccessControlProvider acProvider, WorkspaceAccessManager wspAccessMgr)
			throws AccessDeniedException, Exception {
		log.debug("init(" + context + ", " + acProvider + ", " + wspAccessMgr + ")");
		init(context);
	}

	@Override
	public void close() throws Exception {
		log.debug("close()");
	}

	@Override
	public boolean canAccess(String workspaceName) throws NoSuchWorkspaceException, RepositoryException {
		log.info("canAccess(" + workspaceName + ")");
		return true;
	}

	@Override
	public boolean canRead(Path itemPath) throws RepositoryException {
		log.info("canRead(" + itemPath + ")");
		return isGranted(itemPath, Permission.READ);
	}

	@Override
	// This method is deprecated in Jackrabbit 1.5.0
	public void checkPermission(ItemId id, int permissions) throws AccessDeniedException,
			ItemNotFoundException, RepositoryException {
		log.debug("checkPermission(" + id + ", " + permissions + ")");
		if (isGranted(id, permissions)) {
			return;
		}
		throw new AccessDeniedException("JCR permission denied!");
	}

	@Override
	// This method is deprecated in Jackrabbit 1.5.0
	public boolean isGranted(ItemId id, int permissions) throws ItemNotFoundException, RepositoryException {
		log.info("isGranted(" + id + ", " + permissions + ")");
		Path path = context.getHierarchyManager().getPath(id);
		return isGranted(path, deprecatedActionsToNewApi(permissions));
	}

	@Override
	public boolean isGranted(Path absPath, int permissions) throws RepositoryException {
		log.info("isGranted(" + absPath + ", " + permissions + ")");
		
		if (alreadyInsideAccessManager.get()) {
			log.debug("[YES inside]");
			return true;
		}

		log.debug("[NOT inside]");
		alreadyInsideAccessManager.set(Boolean.TRUE);
		
		alreadyInsideAccessManager.remove();
		
		return true;
	}

	@Override
	public boolean isGranted(Path parentPath, Name childName, int permissions) throws RepositoryException {
		log.info("isGranted(" + parentPath + ", " + childName + ", " + permissions + ")");
		Path p = PathFactoryImpl.getInstance().create(parentPath, childName, true);
		return isGranted(p, permissions);
	}

	/**
	 * 
	 */
	@SuppressWarnings("deprecation")
	private int deprecatedActionsToNewApi(int actions) {
		boolean read = (actions & READ) != 0;
		boolean write = (actions & WRITE) != 0;
		boolean remove = (actions & REMOVE) != 0;
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
	@SuppressWarnings("unused")
	private String actionsToString(int actions) {
		StringBuilder sb = new StringBuilder();

		if (!(actions == Permission.NONE)) {
			//if ((actions & Permission.ALL) != 0) {
				//sb.append("all ");
			//}
			if ((actions & Permission.ADD_NODE) != 0) {
				sb.append("add_node ");
			}
			if ((actions & Permission.READ) != 0) {
				sb.append("read ");
			}
			if ((actions & Permission.REMOVE_NODE) != 0) {
				sb.append("remove_node ");
			}
			if ((actions & Permission.REMOVE_PROPERTY) != 0) {
				sb.append("remove_property ");
			}
			if ((actions & Permission.SET_PROPERTY) != 0) {
				sb.append("set_property ");
			}
		}

		return sb.toString();
	}

	// @Override
	// TODO Enable when using jackrabbit 1.6
	public void checkPermission(Path arg0, int arg1) throws AccessDeniedException, RepositoryException {
	}
}
