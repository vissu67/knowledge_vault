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

import com.openkm.bean.Folder;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.core.AccessDeniedException;

public interface RepositoryModule {

	/**
	 * Obtain the root folder of the repository.
	 * 
	 * @return A folder object with the repository root node properties.
	 * @throws PathNotFoundException If there is no root folder node in the repository.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Folder getRootFolder(String token) throws PathNotFoundException, RepositoryException,
			DatabaseException;

	/**
	 * Obtains the user trash folder.
	 * 
	 * @return A folder object with the user trash node properties.
	 * @throws PathNotFoundException If there is no user trash folder node in the repository.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Folder getTrashFolder(String token) throws PathNotFoundException, RepositoryException,
			DatabaseException;
	
	public Folder getTrashFolderBase(String token) throws PathNotFoundException, RepositoryException,
			DatabaseException;

	/**
	 * Obtain the template folder of the repository.
	 * 
	 * @return A folder object with the templates node properties.
	 * @throws PathNotFoundException If there is no templates folder node in the repository.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Folder getTemplatesFolder(String token) throws PathNotFoundException, RepositoryException,
			DatabaseException;

	/**
	 * Obtain the personal documents folder of the repository.
	 * 
	 * @return A folder object with the user documents folder node properties.
	 * @throws PathNotFoundException If there is no user documents folder node in the repository.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Folder getPersonalFolder(String token) throws PathNotFoundException, RepositoryException,
			DatabaseException;
	
	public Folder getPersonalFolderBase(String token) throws PathNotFoundException, RepositoryException,
			DatabaseException;

	/**
	 * Obtain the personal mails folder of the repository.
	 * 
	 * @return A folder object with the user mails folder node properties.
	 * @throws PathNotFoundException If there is no user documents folder node in the repository.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Folder getMailFolder(String token) throws PathNotFoundException, RepositoryException,
			DatabaseException;
	
	public Folder getMailFolderBase(String token) throws PathNotFoundException, RepositoryException,
			DatabaseException;

	/**
	 * Obtain the thesaurus folder of the repository.
	 * 
	 * @return A folder object with the thesaurus folder node properties.
	 * @throws PathNotFoundException If there is no user documents folder node in the repository.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Folder getThesaurusFolder(String token) throws PathNotFoundException, RepositoryException,
			DatabaseException;

	/**
	 * Obtain the categories folder of the repository.
	 * 
	 * @return A folder object with the categories folder node properties.
	 * @throws PathNotFoundException If there is no user documents folder node in the repository.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Folder getCategoriesFolder(String token) throws PathNotFoundException, RepositoryException,
			DatabaseException;
	
	/**
	 * Remove all the items in the user trash folder for ever. You can't 
	 * recover this items any more.
	 * 
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't modify the user deleted folders and documents because 
	 * of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void purgeTrash(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException;
	
	/**
	 * Get the update message, if any.
	 * 
	 * @return A possible update message or simple info for the application.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public String getUpdateMessage(String token) throws RepositoryException;
	
	/**
	 * Get the unique repository identifier
	 * 
	 * @return The repository UUID
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public String getRepositoryUuid(String token) throws RepositoryException;
	
	/**
	 * Test if a node path exists
	 * 
	 * @param path The node path to test 
	 * @return true if the node exist or false if not
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public boolean hasNode(String token, String path) throws RepositoryException, DatabaseException;
	
	/**
	 * Obtain the node path with a given uuid.
	 * 
	 * @param uuid An unique node identifier
	 * @return The path of the node with the given uuid
	 * @throws PathNotFoundException If there is no user node in the repository with this uuid.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public String getNodePath(String token, String uuid) throws PathNotFoundException, RepositoryException,
			DatabaseException;
	
	/**
	 * Obtain the node uuid with a given path.
	 * 
	 * @param path An unique path node identifier
	 * @return The path of the node with the given uuid
	 * @throws PathNotFoundException If there is no user node in the repository with this uuid.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public String getNodeUuid(String token, String path) throws PathNotFoundException, RepositoryException,
	DatabaseException;
}
