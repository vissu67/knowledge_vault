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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.RepositoryException;
import javax.security.auth.Subject;

import org.apache.jackrabbit.core.HierarchyManager;
import org.apache.jackrabbit.core.ItemId;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.XASessionImpl;
import org.apache.jackrabbit.core.config.WorkspaceConfig;
import org.apache.jackrabbit.core.security.AMContext;
import org.apache.jackrabbit.core.security.AccessManager;
import org.apache.jackrabbit.core.security.SystemPrincipal;
import org.apache.jackrabbit.core.security.authorization.AccessControlProvider;
import org.apache.jackrabbit.core.security.authorization.WorkspaceAccessManager;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A <code>SystemSession</code> ...
 */
public class OKMSystemSession extends XASessionImpl {

    private static Logger log = LoggerFactory.getLogger(OKMSystemSession.class);
    private static final boolean DEBUG = false;

    /**
     * Package private factory method
     *
     * @param rep
     * @param wspConfig
     * @return
     * @throws RepositoryException
     */
    public static OKMSystemSession create(RepositoryImpl rep, WorkspaceConfig wspConfig)
            throws RepositoryException {
    	if (DEBUG) log.debug("create()");
        // create subject with SystemPrincipal
        Set<SystemPrincipal> principals = new HashSet<SystemPrincipal>();
        principals.add(new SystemPrincipal());
        Subject subject = new Subject(true, principals, Collections.EMPTY_SET, Collections.EMPTY_SET);
        OKMSystemSession oss = new OKMSystemSession(rep, subject, wspConfig);
        if (DEBUG) log.debug("create: "+oss);
        return oss;
    }

    /**
     * private constructor
     *
     * @param rep
     * @param wspConfig
     */
    private OKMSystemSession(RepositoryImpl rep, Subject subject, WorkspaceConfig wspConfig)
            throws RepositoryException {
        super(rep, subject, wspConfig);
    }
    
    /* (non-Javadoc)
     * @see javax.jcr.Session#logout()
     */
    public synchronized void logout() {
    	if (DEBUG) log.warn("logout()");
    	super.logout();
    	if (DEBUG) log.warn("logout: void");
    }

    @Override
    protected AccessManager createAccessManager(Subject subject, HierarchyManager hierMgr)
            throws AccessDeniedException, RepositoryException {
        /**
         * use own AccessManager implementation rather than relying on
         * configurable AccessManager to handle SystemPrincipal privileges
         * correctly
         */
        return new SystemAccessManager();
        //return super.createAccessManager(subject, hierMgr);
    }

    //--------------------------------------------------------< inner classes >
    private class SystemAccessManager implements AccessManager {

        SystemAccessManager() {
        }

        //----------------------------------------------------< AccessManager >
        @Override
        public void init(AMContext context) throws AccessDeniedException, Exception {
            // none
        }
        
		@Override
		public void init(AMContext arg0, AccessControlProvider arg1, WorkspaceAccessManager arg2)
				throws AccessDeniedException, Exception {
			// none
		}

        @Override
        public void close() throws Exception {
        	// none
        }

        @Override
        public void checkPermission(ItemId id, int permissions)
                throws AccessDeniedException, ItemNotFoundException, RepositoryException {
            // allow everything
        }

        @Override
        public boolean isGranted(ItemId id, int permissions)
                throws ItemNotFoundException, RepositoryException {
            // allow everything
            return true;
        }

        @Override
        public boolean canAccess(String workspaceName)
                throws NoSuchWorkspaceException, RepositoryException {
        	// allow everything
            return true;
        }

		@Override
		public boolean canRead(Path itemPath) throws RepositoryException {
			// allow everything
			return true;
		}

		@Override
		public boolean isGranted(Path absPath, int permissions) throws RepositoryException {
			// allow everything
			return true;
		}

		@Override
		public boolean isGranted(Path parentPath, Name childName, int permissions) throws RepositoryException {
			// allow everything
			return true;
		}

		// @Override
		// TODO Enable @Override when use jackrabbit 1.6
		public void checkPermission(Path absPath, int permissions) throws AccessDeniedException, RepositoryException {
			// allow everything
		}
    }
}
