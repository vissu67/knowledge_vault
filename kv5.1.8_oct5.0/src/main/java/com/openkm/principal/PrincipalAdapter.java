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

package com.openkm.principal;

import java.util.List;

public interface PrincipalAdapter {
	
	/**
	 * Method to retrieve all users from a authentication source.
	 * 
	 * @return A Collection with all the users.
	 * @throws PrincipalAdapterException If any error occurs.
	 */
	public List<String> getUsers() throws PrincipalAdapterException;
	
	/**
	 * Method to retrieve all roles from a authentication source.
	 * 
	 * @return A Collection with all the roles.
	 * @throws PrincipalAdapterException If any error occurs.
	 */
	public List<String> getRoles() throws PrincipalAdapterException;
	
	/**
	 * Method to retrieve all users from a role.
	 * 
	 * @return A Collection with all the users within a role.
	 * @throws PrincipalAdapterException If any error occurs.
	 */
	public List<String> getUsersByRole(String role) throws PrincipalAdapterException;
	
	/**
	 * Method to retrieve all roles from a user.
	 * 
	 * @return A Collection with all the roles of the user.
	 * @throws PrincipalAdapterException If any error occurs.
	 */
	public List<String> getRolesByUser(String user) throws PrincipalAdapterException;
	
	/**
	 * Method to retrieve the mail from a user.
	 * 
	 * @param users A user id.
	 * @return The email of the user.
	 * @throws PrincipalAdapterException If any error occurs.
	 */
	public String getMail(String user) throws PrincipalAdapterException;
	
	/**
	 * Method to retrieve the name from a user.
	 * 
	 * @param users A user id.
	 * @return The name of the user.
	 * @throws PrincipalAdapterException If any error occurs.
	 */
	public String getName(String user) throws PrincipalAdapterException;
}
