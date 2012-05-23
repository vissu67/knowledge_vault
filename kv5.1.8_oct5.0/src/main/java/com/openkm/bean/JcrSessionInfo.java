/**
 *  OpenKM, Open Document Management System (http://www.openkm.com)
 *  Copyright (C) 2006-2011  Paco Avila & Josep Llort
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
import java.util.Calendar;

import javax.jcr.Session;

public class JcrSessionInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private Calendar creation;
	private Calendar lastAccess;
	private Session session;
	
	public Calendar getLastAccess() {
		return lastAccess;
	}
	
	public void setLastAccess(Calendar lastAccess) {
		this.lastAccess = lastAccess;
	}
	
	public Calendar getCreation() {
		return creation;
	}
	
	public void setCreation(Calendar creation) {
		this.creation = creation;
	}
	
	public javax.jcr.Session getSession() {
		return session;
	}
	
	public void setSession(javax.jcr.Session session) {
		this.session = session;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append("session="); sb.append(session);
		sb.append(", creation="); sb.append(creation==null?null:creation.getTime());
		sb.append(", lastAccess="); sb.append(lastAccess==null?null:lastAccess.getTime());
		sb.append("]");
		return sb.toString();
	}
}
