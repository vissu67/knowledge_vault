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

package com.openkm.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.module.ModuleManager;
import com.openkm.module.NotificationModule;

/**
 * @author pavila
 *
 */
public class OKMNotification implements NotificationModule {
	private static Logger log = LoggerFactory.getLogger(OKMNotification.class);
	private static OKMNotification instance = new OKMNotification();

	private OKMNotification() {}
	
	public static OKMNotification getInstance() {
		return instance;
	}
	
	@Override
	public void subscribe(String token, String nodePath) throws PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("subscribe({}, {})", token, nodePath);
		NotificationModule nm = ModuleManager.getNotificationModule();
		nm.subscribe(token, nodePath);
		log.debug("subscribe: void");
	}

	@Override
	public void unsubscribe(String token, String nodePath) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("unsubscribe({}, {})", token, nodePath);
		NotificationModule nm = ModuleManager.getNotificationModule();
		nm.unsubscribe(token, nodePath);
		log.debug("unsubscribe: void");
	}

	@Override
	public List<String> getSubscriptors(String token, String nodePath) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getSubscriptors({}, {})", token, nodePath);
		NotificationModule nm = ModuleManager.getNotificationModule();
		List<String> users = nm.getSubscriptors(token, nodePath);
		log.debug("getSubscriptors: {}", users);
		return users;
	}

	@Override
	public void notify(String token, String nodePath, List<String> users, String message, boolean attachment)
			throws PathNotFoundException, AccessDeniedException, RepositoryException {
		log.debug("notify({}, {}, {}, {}, {})", new Object[] { token, nodePath, users, message, attachment });
		NotificationModule nm = ModuleManager.getNotificationModule();
		nm.notify(token, nodePath, users, message, attachment);
		log.debug("notify: void");
	}
}
