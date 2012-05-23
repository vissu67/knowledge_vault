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

package com.openkm.bean;

import java.io.Serializable;

public class Permission implements Serializable {
	private static final long serialVersionUID = -6594786775079108975L;

	public static final String USERS_READ = "okm:authUsersRead";
	public static final String USERS_WRITE = "okm:authUsersWrite";
	public static final String USERS_DELETE = "okm:authUsersDelete";
	public static final String USERS_SECURITY = "okm:authUsersSecurity";
	public static final String ROLES_READ = "okm:authRolesRead";
	public static final String ROLES_WRITE = "okm:authRolesWrite";
	public static final String ROLES_DELETE = "okm:authRolesDelete";
	public static final String ROLES_SECURITY = "okm:authRolesSecurity";
	
	public static final byte NONE = 0;
	public static final byte READ = 1;
	public static final byte WRITE = 2;
	public static final byte DELETE = 4;
	public static final byte SECURITY = 8; 
	
	private String item;
	private int permissions;
	
	public String getItem() {
		return item;
	}
	
	public void setItem(String item) {
		this.item = item;
	}
	
	public int getPermissions() {
		return permissions;
	}
	
	public void setPermissions(int permissions) {
		this.permissions = permissions;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("item="); sb.append(item);
		sb.append(", permissions="); sb.append(permissions);
		sb.append("}");
		return sb.toString();
	}
}
