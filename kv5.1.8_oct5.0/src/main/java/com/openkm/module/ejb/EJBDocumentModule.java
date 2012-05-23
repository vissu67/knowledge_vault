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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.openkm.bean.Document;
import com.openkm.bean.Lock;
import com.openkm.bean.Version;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.FileSizeExceededException;
import com.openkm.core.ItemExistsException;
import com.openkm.core.LockException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.core.UnsupportedMimeTypeException;
import com.openkm.core.UserQuotaExceededException;
import com.openkm.core.VersionException;
import com.openkm.core.VirusDetectedException;


public class EJBDocumentModule implements com.openkm.module.DocumentModule {

	@Override
	public Document create(String token, Document doc, InputStream is) throws UnsupportedMimeTypeException,
			FileSizeExceededException, UserQuotaExceededException, VirusDetectedException,
			ItemExistsException, PathNotFoundException, AccessDeniedException, RepositoryException,
			IOException, DatabaseException {
		return null;
	}

	@Override
	public void delete(String token, String docPath) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
	}

	@Override
	public Document rename(String token, String docPath, String newName) throws PathNotFoundException,
			ItemExistsException, AccessDeniedException, RepositoryException, DatabaseException {
		return null;
	}

	@Override
	public Document getProperties(String token, String docPath) throws PathNotFoundException,
			RepositoryException, DatabaseException {
		return null;
	}

	@Override
	public void setProperties(String token, Document doc) throws VersionException, LockException,
			PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
	}

	@Override
	public InputStream getContent(String token, String docPath, boolean checkout)
			throws PathNotFoundException, RepositoryException, IOException, DatabaseException {
		return null;
	}

	@Override
	public InputStream getContentByVersion(String token, String docPath, String versionId)
			throws RepositoryException, PathNotFoundException, IOException, DatabaseException {
		return null;
	}

	@Override
	public void setContent(String token, String docPath, InputStream is) throws FileSizeExceededException,
			UserQuotaExceededException, VirusDetectedException, VersionException, LockException,
			PathNotFoundException, AccessDeniedException, RepositoryException, IOException, DatabaseException {
	}

	@Override
	public List<Document> getChilds(String token, String fldPath) throws PathNotFoundException,
			RepositoryException, DatabaseException {
		return null;
	}

	@Override
	public void checkout(String token, String docPath) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
	}
	
	//added by vissu on feb20 for zohoapi
	@Override
	public String zoho(String token, String docPath) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		return null;
	}

	@Override
	public void cancelCheckout(String token, String docPath) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
	}

	@Override
	public boolean isCheckedOut(String token, String docPath) throws PathNotFoundException,
			RepositoryException, DatabaseException {
		return false;
	}

	@Override
	public Version checkin(String token, String docPath, String comment) throws LockException,
			VersionException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException {
		return null;
	}

	@Override
	public List<Version> getVersionHistory(String token, String docPath) throws PathNotFoundException,
			RepositoryException, DatabaseException {
		return null;
	}

	@Override
	public void lock(String token, String docPath) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
	}

	@Override
	public void unlock(String token, String docPath) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
	}

	@Override
	public boolean isLocked(String token, String docPath) throws RepositoryException, PathNotFoundException,
			DatabaseException {
		return false;
	}

	@Override
	public Lock getLock(String token, String docPath) throws RepositoryException, PathNotFoundException,
			LockException, DatabaseException {
		return null;
	}

	@Override
	public void purge(String token, String docPath) throws AccessDeniedException, RepositoryException,
			PathNotFoundException, DatabaseException {
	}

	@Override
	public void move(String token, String docPath, String fldPath) throws PathNotFoundException,
			ItemExistsException, AccessDeniedException, RepositoryException, DatabaseException {
	}

	@Override
	public void copy(String token, String docPath, String fldPath) throws ItemExistsException,
			PathNotFoundException, AccessDeniedException, RepositoryException, IOException,
			DatabaseException, UserQuotaExceededException {
	}

	@Override
	public void restoreVersion(String token, String docPath, String versionId) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
	}

	@Override
	public void purgeVersionHistory(String token, String docPath) throws AccessDeniedException,
			RepositoryException, PathNotFoundException, DatabaseException {
	}

	@Override
	public long getVersionHistorySize(String token, String docPath) throws RepositoryException,
			PathNotFoundException, DatabaseException {
		return 0;
	}

	@Override
	public boolean isValid(String token, String docPath) throws PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException {
		return false;
	}

	@Override
	public String getPath(String token, String uuid) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		return null;
	}

	@Override
	public void forceUnlock(String token, String docPath) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
	}

	@Override
	public void forceCancelCheckout(String token, String docPath) throws LockException,
			PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
	}
}
