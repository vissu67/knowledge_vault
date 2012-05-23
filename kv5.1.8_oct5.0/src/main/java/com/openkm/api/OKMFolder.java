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

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import com.openkm.module.FolderModule;
import com.openkm.module.ModuleManager;

/**
 * @author pavila
 *
 */
public class OKMFolder implements FolderModule {
	private static Logger log = LoggerFactory.getLogger(OKMFolder.class);
	private static OKMFolder instance = new OKMFolder();
	
	private OKMFolder() {}
	
	public static OKMFolder getInstance() {
		return instance;
	}
	
	@Override
	public Folder create(String token, Folder fldPath) throws PathNotFoundException, ItemExistsException, 
			AccessDeniedException, RepositoryException, DatabaseException, ExtensionException {
		log.debug("create({}, {})", token, fldPath);
		FolderModule fm = ModuleManager.getFolderModule();
		Folder newFld = fm.create(token, fldPath);
		log.debug("create: {}", newFld);
		return newFld;
	}
		
	public Folder createSimple(String token, String fldPath) throws PathNotFoundException,
			ItemExistsException, AccessDeniedException, RepositoryException, DatabaseException,
			ExtensionException {
		log.debug("createSimple({}, {})", token, fldPath);
		FolderModule fm = ModuleManager.getFolderModule();
		Folder fld = new Folder();
		fld.setPath(fldPath);
		Folder newFolder = fm.create(token, fld);
		log.debug("createSimple: {}", newFolder);
		return newFolder;
	}
	
	@Override
	public Folder getProperties(String token, String fldPath) throws PathNotFoundException,
			RepositoryException, DatabaseException {
		log.debug("getProperties({}, {})", token, fldPath);
		FolderModule fm = ModuleManager.getFolderModule();
		Folder fld = fm.getProperties(token, fldPath);
		log.debug("getProperties: {}", fld);
		return fld;
	}
	
	@Override
	public void delete(String token, String fldPath) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("delete({}, {})", token, fldPath);
		FolderModule fm = ModuleManager.getFolderModule();
		fm.delete(token, fldPath);
		log.debug("delete: void");
	}

	@Override
	public void purge(String token, String fldPath) throws PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("purge({}, {})", token, fldPath);
		FolderModule fm = ModuleManager.getFolderModule();
		fm.purge(token, fldPath);
		log.debug("purge: void");
	}
	
	@Override
	public Folder rename(String token, String fldPath, String newName) throws PathNotFoundException,
			ItemExistsException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("rename({}, {}, {})", new Object[] { token, fldPath, newName });
		FolderModule fm = ModuleManager.getFolderModule();
		Folder renamedFolder = fm.rename(token, fldPath, newName);
		log.debug("rename: {}", renamedFolder);
		return renamedFolder;
	}
	
	@Override
	public void move(String token, String fldPath, String dstPath) throws PathNotFoundException,
			ItemExistsException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("move({}, {}, {})", new Object[] { token, fldPath, dstPath });
		FolderModule fm = ModuleManager.getFolderModule();
		fm.move(token, fldPath, dstPath);
		log.debug("move: void");
	}

	@Override
	public void copy(String token, String fldPath, String dstPath) throws PathNotFoundException,
			ItemExistsException, AccessDeniedException, RepositoryException, IOException, DatabaseException,
			UserQuotaExceededException {
		log.debug("copy({}, {}, {})", new Object[] { token, fldPath, dstPath });
		FolderModule fm = ModuleManager.getFolderModule();
		fm.copy(token, fldPath, dstPath);
		log.debug("copy: void");
	}
	
	@Override
	public List<Folder> getChilds(String token, String fldPath) throws PathNotFoundException, 
			RepositoryException, DatabaseException {
		log.debug("getChilds({}, {})", token, fldPath);
		FolderModule fm = ModuleManager.getFolderModule();
		List<Folder> childs = fm.getChilds(token, fldPath);
		log.debug("getChilds: {}", childs);
		return childs;
	}

	@Override
	public ContentInfo getContentInfo(String token, String fldPath) throws AccessDeniedException,
			RepositoryException, PathNotFoundException, DatabaseException {
		log.debug("getContentInfo({}, {})", token, fldPath);
		FolderModule fm = ModuleManager.getFolderModule();
		ContentInfo contentInfo = fm.getContentInfo(token, fldPath);
		log.debug("getContentInfo: {}", contentInfo);
		return contentInfo;
	}
	
	@Override
	public boolean isValid(String token, String fldPath) throws PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("isValid({}, {})", token, fldPath);
		FolderModule fm = ModuleManager.getFolderModule();
		boolean valid = fm.isValid(token, fldPath);
		log.debug("isValid: {}", valid);
		return valid;
	}

	@Override
	public String getPath(String token, String uuid) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getPath({})", uuid);
		FolderModule fm = ModuleManager.getFolderModule();
		String path = fm.getPath(token, uuid);
		log.debug("getPath: {}", path);
		return path;
	}
}
