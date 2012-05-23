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

package com.openkm.module;

import java.util.List;

import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;

public interface NotificationModule {

	/**
	 * Add user subscription to a node.
	 * 
	 * @param nodePath The complete path to the node.
	 * @throws PathNotFoundException If the node defined by nodePath do not exists.
	 * @throws AccessDeniedException If the token authorization information is not valid.
	 * @throws RepositoryException If there is any error accessing to the repository.
	 */
	public void subscribe(String token, String nodePath) throws PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException;
	
	/**
	 * Remove an user subscription from a node.
	 * 
	 * @param nodePath The complete path to the node.
	 * @throws PathNotFoundException If the node defined by nodePath do not exists.
	 * @throws AccessDeniedException If the token authorization information is not valid.
	 * @throws RepositoryException If there is any error accessing to the repository.
	 */
	public void unsubscribe(String token, String nodePath) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException;
	
	/** 
	 * Get user subscriptions from am item (document or folder).
	 * 
	 * @param nodePath The complete path to the node.
	 * @return A Collection of subscribed users.
	 * @throws PathNotFoundException If the node defined by nodePath do not exists.
	 * @throws AccessDeniedException If the token authorization information is not valid.
	 * @throws RepositoryException If there is any error accessing to the repository.
	 */
	public List<String> getSubscriptors(String token, String nodePath) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Send a notification message to an user list.
	 * 
	 * @param nodePath The complete path to the node.
	 * @param users Array of users to notify.
	 * @param message An String with the notification message.
	 * @throws PathNotFoundException If the node defined by nodePath do not exists.
	 * @throws AccessDeniedException If the token authorization information is not valid.
	 * @throws RepositoryException If there is any error accessing to the repository.
	 */
	public void notify(String token, String nodePath, List<String> users, String message, boolean attachment)
			throws PathNotFoundException, AccessDeniedException, RepositoryException;
}
