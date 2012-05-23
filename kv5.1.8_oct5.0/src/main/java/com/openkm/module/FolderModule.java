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

import java.io.IOException;
import java.util.List;

import com.openkm.bean.ContentInfo;
import com.openkm.bean.Folder;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.ItemExistsException;
import com.openkm.core.LockException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.core.UserQuotaExceededException;
import com.openkm.extension.core.ExtensionException;

public interface FolderModule {

	/**
	 * Create a new folder in the repository.
	 * 
	 * @param fld A folder object with the new folder properties.
	 * @return A folder object with the new created folder properties.
	 * @throws PathNotFoundException If the parent folder doesn't exist.
	 * @throws ItemExistsException If there is already a folder in the
	 * repository with the same name in the same path.
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't modify the parent folder because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Folder create(String token, Folder fld) throws PathNotFoundException, ItemExistsException, 
			AccessDeniedException, RepositoryException, DatabaseException, ExtensionException;

	/**
	 * Obtains properties from a previously created folder.
	 * 
	 * @param fldPath The path that identifies an unique folder. 
	 * @return A folder object with the selected folder properties.
	 * @throws PathNotFoundException If the indicated folder doesn't exist.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Folder getProperties(String token, String fldPath) throws PathNotFoundException, 
			RepositoryException, DatabaseException;
	
	/**
	 * Delete a folder the repository. It is a logical delete,
	 * so really is moved to the user trash and can be restored.
	 * 
	 * @param fldPath The path that identifies an unique folder.  
	 * @throws LockException Can't delete a folder with locked documents.
	 * @throws PathNotFoundException If there is no folder in the repository in this path.
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't modify the folder because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void delete(String token, String fldPath) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Deletes definitively a folder from the repository. It is a phisical delete, so
	 * the folder can't be restored.
	 * 
	 * @param fldPath The path that identifies an unique folder.  
	 * @throws LockException Can't delete a folder with locked documents.
	 * @throws PathNotFoundException If there is no folder in the repository in this path.
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't modify the folder because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void purge(String token, String fldPath) throws PathNotFoundException, AccessDeniedException, 
			RepositoryException, DatabaseException;

	/**
	 * Rename a folder in the repository.
	 * 
	 * @param fldPath The path that identifies an unique folder.  
	 * @param newName The new folder name.
	 * @return A folder object with the new folder properties.
	 * @throws PathNotFoundException If there is no folder in the repository in this path.
	 * @throws ItemExistsException If there is already a folder in the
	 * repository with the same name in the same path.
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't modify the folder because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public Folder rename(String token, String fldPath, String newName) throws PathNotFoundException,
			ItemExistsException, AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Move a folder to another location in the repository.
	 * 
	 * @param fldPath The path that identifies an unique folder.
	 * @param dstPath The path of the destination folder.
	 * @throws PathNotFoundException If the dstPath does not exists.
	 * @throws ItemExistsException If there is already a folder in the
	 * destination folder with the same name.
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't modify the parent folder or the destination folder
	 * because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void move(String token, String fldPath, String dstPath) throws PathNotFoundException,
			ItemExistsException, AccessDeniedException, RepositoryException, DatabaseException;

	/**
	 * Copy a folder to another location in the repository.
	 * 
	 * @param fldPath The path that identifies an unique folder.
	 * @param dstPath The path of the destination folder.
	 * @throws PathNotFoundException If the dstPath does not exists.
	 * @throws ItemExistsException If there is already a folder in the
	 * destination folder with the same name.
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't modify the parent folder or the destination folder
	 * because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public void copy(String token, String fldPath, String dstPath) throws PathNotFoundException,
			ItemExistsException, AccessDeniedException, RepositoryException, IOException, DatabaseException, 
			UserQuotaExceededException;

	/**
	 * Retrieve a list of child folders from an existing one.
	 * 
	 * @param fldPath The path that identifies an unique folder.
	 * @return A Collection with the child folders.
	 * @throws PathNotFoundException If there is no folder in the repository in this path
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public List<Folder> getChilds(String token, String fldPath) throws PathNotFoundException,
			RepositoryException, DatabaseException;

	/**
	 * Retrive the content info of the folder: number of folders, number of documents, and total size.
	 * 
	 * @param fldPath The path that identifies an unique folder.
	 * @return A ContentInfo with the number of folder, number of documents and total size.
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't access this folder because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 * @throws PathNotFoundException If there is no folder in the repository with this path.
	 */
	public ContentInfo getContentInfo(String token, String fldPath) throws AccessDeniedException,
			RepositoryException, PathNotFoundException, DatabaseException;
	
	/**
	 * Test if a folder path is valid.
	 * 
	 * @param fldPath The path that identifies an unique folder.
	 * @return True if is a valid folder path, otherwise false.
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't access this folder because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 * @throws PathNotFoundException If there is no folder in the repository with this path.
	 */
	public boolean isValid(String token, String fldPath) throws PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException;
	
	/**
	 * Get the folder path from a UUID
	 * 
	 * @param uuid The unique folder id.
	 * @return The folder path
	 * @throws AccessDeniedException If there is any security problem: 
	 * you can't access this folder because of lack of permissions.
	 * @throws RepositoryException If there is any problem.
	 */
	public String getPath(String token, String uuid) throws AccessDeniedException, RepositoryException,
			DatabaseException;
}
