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

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyPrincipalAdapter implements PrincipalAdapter {
	private static Logger log = LoggerFactory.getLogger(DummyPrincipalAdapter.class);
	
	@Override
	public List<String> getUsers() throws PrincipalAdapterException {
		log.debug("getUsers()");
		List<String> list = new ArrayList<String>();
		list.add("okmAdmin");
		list.add("monkiki");
		log.debug("getUsers: {}", list);
		return list;
	}

	@Override
	public List<String> getRoles() throws PrincipalAdapterException {
		log.debug("getRoles()");
		List<String> list = new ArrayList<String>();
		list.add("AdminRole");
		list.add("UserRole");
		log.debug("getRoles: {}", list);
		return list;
	}
	
	@Override
	public List<String> getUsersByRole(String role) throws PrincipalAdapterException {
		List<String> list = new ArrayList<String>();
		
		if (role.equals("AdminRole")) {
			list.add("okmAdmin");
		} else if (role.equals("UserRole")) {
			list.add("okmAdmin");
			list.add("monkiki");
		}
		
		return list;
	}
	
	@Override
	public List<String> getRolesByUser(String user) throws PrincipalAdapterException {
		List<String> list = new ArrayList<String>();
		
		if (user.equals("okmAdmin")) {
			list.add("AdminRole");
			list.add("UserRole");
		} else if (user.equals("monkiki")) {
			list.add("UserRole");
		}
		
		return list;
	}

	@Override
	public String getMail(String user) throws PrincipalAdapterException {
		String mail = null;
		
		if (user.equals("okmAdmin")) {
			mail = "admin@openkm.com";
		}
		
		return mail;
	}

	@Override
	public String getName(String user) throws PrincipalAdapterException {
		String name = null;
		
		if (user.equals("okmAdmin")) {
			name = "Administrator";
		}
		
		return name;
	}
}
