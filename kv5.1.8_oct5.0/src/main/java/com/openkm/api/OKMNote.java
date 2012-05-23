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

import com.openkm.bean.Note;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.LockException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.module.ModuleManager;
import com.openkm.module.NoteModule;

/**
 * @author pavila
 *
 */
public class OKMNote implements NoteModule {
	private static Logger log = LoggerFactory.getLogger(OKMNote.class);
	private static OKMNote instance = new OKMNote();

	private OKMNote() {}
	
	public static OKMNote getInstance() {
		return instance;
	}
			
	@Override
	public Note add(String token, String nodePath, String text) throws LockException,
			PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("add({}, {}, {})", new Object[] { token, nodePath, text });
		NoteModule nm = ModuleManager.getNoteModule();
		Note ret = nm.add(token, nodePath, text);
		log.debug("add: {}", ret);
		return ret;
	}
	
	@Override
	public Note get(String token, String notePath) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("get({}, {})", token, notePath);
		NoteModule nm = ModuleManager.getNoteModule();
		Note ret = nm.get(token, notePath);
		log.debug("get: {}", ret);
		return ret;
	}

	@Override
	public void remove(String token, String notePath) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("remove({}, {})", token, notePath);
		NoteModule nm = ModuleManager.getNoteModule();
		nm.remove(token, notePath);
		log.debug("remove: void");
	}

	@Override
	public void set(String token, String notePath, String text) throws LockException,
			PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("set({}, {}, {})", new Object[] { token, notePath, text });
		NoteModule nm = ModuleManager.getNoteModule();
		nm.set(token, notePath, text);
		log.debug("set: void");
	}
	
	@Override
	public List<Note> list(String token, String nodePath) throws PathNotFoundException,
			RepositoryException, DatabaseException {
		log.debug("list({}, {})", token, nodePath);
		NoteModule nm = ModuleManager.getNoteModule();
		List<Note> col = nm.list(token, nodePath);
		log.debug("list: {}", col);
		return col;
	}
}
