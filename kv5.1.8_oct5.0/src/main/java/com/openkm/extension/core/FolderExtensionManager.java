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

package com.openkm.extension.core;

import java.util.Collections;
import java.util.List;
import java.util.ServiceConfigurationError;

import javax.jcr.Node;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Folder;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.ItemExistsException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.Ref;
import com.openkm.core.RepositoryException;

public class FolderExtensionManager {
	private static Logger log = LoggerFactory.getLogger(FolderExtensionManager.class);
	private static FolderExtensionManager service = null;
	
	private FolderExtensionManager() {}
	
	public static synchronized FolderExtensionManager getInstance() {
		if (service == null) {
			service = new FolderExtensionManager();
		}
		
		return service;
	}
	
	/**
	 * Handle PRE create extensions
	 */
	public void preCreate(Session session, Ref<Node> parentNode, Ref<Folder> fld) throws 
			AccessDeniedException, RepositoryException, PathNotFoundException,
			ItemExistsException, DatabaseException, ExtensionException {
		log.debug("preCreate({}, {}, {})", new Object[] { session, parentNode, fld });
		
		try {
			ExtensionManager em = ExtensionManager.getInstance();
			List<FolderExtension> col = em.getPlugins(FolderExtension.class);
			Collections.sort(col, new OrderComparator<FolderExtension>());
			
			for (FolderExtension ext : col) {
				log.debug("Extension class: {}", ext.getClass().getCanonicalName());
				ext.preCreate(session, parentNode, fld);
			}
		} catch (ServiceConfigurationError e) {
			log.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Handle POST create extensions
	 */
	public void postCreate(Session session, Ref<Node> parentNode, Ref<Node> fldNode) throws
			AccessDeniedException, RepositoryException, PathNotFoundException,
			ItemExistsException, DatabaseException, ExtensionException {
		log.debug("postCreate({}, {}, {})", new Object[] { session, parentNode, fldNode });
		
		try {
			ExtensionManager em = ExtensionManager.getInstance();
			List<FolderExtension> col = em.getPlugins(FolderExtension.class);
			Collections.sort(col, new OrderComparator<FolderExtension>());
			
			for (FolderExtension ext : col) {
				log.debug("Extension class: {}", ext.getClass().getCanonicalName());
				ext.postCreate(session, parentNode, fldNode);
			}
		} catch (ServiceConfigurationError e) {
			log.error(e.getMessage(), e);
		}
	}
}
