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

import java.util.List;
import java.util.Map;

import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.principal.PrincipalAdapterException;

public class EJBAuthModule implements com.openkm.module.AuthModule {

	@Override
	public void login() throws RepositoryException, DatabaseException {
	}

	@Override
	public String login(String user, String password) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		return null;
	}

	@Override
	public void logout(String token) throws RepositoryException, DatabaseException {
	}

	@Override
	public void grantUser(String token, String nodePath, String user, int permissions, boolean recursive)
			throws PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
	}

	@Override
	public void revokeUser(String token, String nodePath, String user, int permissions, boolean recursive)
			throws PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
	}

	@Override
	public Map<String, Byte> getGrantedUsers(String token, String nodePath) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		return null;
	}

	@Override
	public void grantRole(String token, String nodePath, String role, int permissions, boolean recursive)
			throws PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
	}

	@Override
	public void revokeRole(String token, String nodePath, String role, int permissions, boolean recursive)
			throws PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
	}

	@Override
	public Map<String, Byte> getGrantedRoles(String token, String nodePath) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		return null;
	}

	@Override
	public List<String> getUsers(String token) throws PrincipalAdapterException {
		return null;
	}

	@Override
	public List<String> getRoles(String token) throws PrincipalAdapterException {
		return null;
	}

	@Override
	public List<String> getUsersByRole(String token, String role) throws PrincipalAdapterException {
		return null;
	}

	@Override
	public List<String> getRolesByUser(String token, String user) throws PrincipalAdapterException {
		return null;
	}

	@Override
	public String getMail(String token, String user) throws PrincipalAdapterException {
		return null;
	}

	@Override
	public String getName(String token, String user) throws PrincipalAdapterException {
		return null;
	}
}
