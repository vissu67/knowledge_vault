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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.module.ModuleManager;
import com.openkm.module.ScriptingModule;

/**
 * @author pavila
 * 
 */
public class OKMScripting implements ScriptingModule {
	private static Logger log = LoggerFactory.getLogger(OKMScripting.class);
	private static OKMScripting instance = new OKMScripting();

	private OKMScripting() {
	}

	public static OKMScripting getInstance() {
		return instance;
	}

	@Override
	public void setScript(String token, String nodePath, String code) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("setScript({}, {}, {})", new Object[] { token, nodePath, code });
		ScriptingModule sm = ModuleManager.getScriptingModule();
		sm.setScript(token, nodePath, code);
		log.debug("setScript: void");
	}

	@Override
	public void removeScript(String token, String nodePath) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("removeScript({}, {})", token, nodePath);
		ScriptingModule sm = ModuleManager.getScriptingModule();
		sm.removeScript(token, nodePath);
		log.debug("removeScript: void");
	}

	@Override
	public String getScript(String token, String nodePath) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getScript({}, {})", token, nodePath);
		ScriptingModule sm = ModuleManager.getScriptingModule();
		String code = sm.getScript(token, nodePath);
		log.debug("getScript: {}", code);
		return code;
	}
}
