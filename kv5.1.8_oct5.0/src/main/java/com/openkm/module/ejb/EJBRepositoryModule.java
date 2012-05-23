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

package com.openkm.module.ejb;

import com.openkm.bean.Folder;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;

public class EJBRepositoryModule implements com.openkm.module.RepositoryModule {

	@Override
	public Folder getRootFolder(String token) throws PathNotFoundException, RepositoryException,
			DatabaseException {
		return null;
	}

	@Override
	public Folder getTrashFolder(String token) throws PathNotFoundException, RepositoryException,
			DatabaseException {
		return null;
	}

	@Override
	public Folder getTrashFolderBase(String token) throws PathNotFoundException, RepositoryException,
			DatabaseException {
		return null;
	}

	@Override
	public Folder getTemplatesFolder(String token) throws PathNotFoundException, RepositoryException,
			DatabaseException {
		return null;
	}

	@Override
	public Folder getPersonalFolder(String token) throws PathNotFoundException, RepositoryException,
			DatabaseException {
		return null;
	}

	@Override
	public Folder getPersonalFolderBase(String token) throws PathNotFoundException, RepositoryException,
			DatabaseException {
		return null;
	}

	@Override
	public Folder getMailFolder(String token) throws PathNotFoundException, RepositoryException,
			DatabaseException {
		return null;
	}

	@Override
	public Folder getMailFolderBase(String token) throws PathNotFoundException, RepositoryException,
			DatabaseException {
		return null;
	}

	@Override
	public Folder getThesaurusFolder(String token) throws PathNotFoundException, RepositoryException,
			DatabaseException {
		return null;
	}

	@Override
	public Folder getCategoriesFolder(String token) throws PathNotFoundException, RepositoryException,
			DatabaseException {
		return null;
	}

	@Override
	public void purgeTrash(String token) throws AccessDeniedException, RepositoryException, DatabaseException {
	}

	@Override
	public String getUpdateMessage(String token) throws RepositoryException {
		return null;
	}

	@Override
	public String getRepositoryUuid(String token) throws RepositoryException {
		return null;
	}

	@Override
	public boolean hasNode(String token, String path) throws RepositoryException, DatabaseException {
		return false;
	}

	@Override
	public String getNodePath(String token, String uuid) throws PathNotFoundException, RepositoryException,
			DatabaseException {
		return null;
	}

	@Override
	public String getNodeUuid(String token, String path) throws PathNotFoundException, RepositoryException,
			DatabaseException {
		return null;
	}
}
