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

package com.openkm.dao.bean;

import java.io.Serializable;

public class DocumentFilterRule implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String ACTION_WIZARD_PROPERTY_GROUP = "WIZARD_PROPERTY_GROUP";
	public static final String ACTION_WIZARD_WORKFLOW = "WIZARD_WORKFLOW";
	public static final String ACTION_WIZARD_CATEGORY = "WIZARD_CATEGORY";
	public static final String ACTION_WIZARD_KEYWORD = "WIZARD_KEYWORD";
	public static final String ACTION_ASSIGN_PROPERTY_GROUP = "ASSIGN_PROPERTY_GROUP";
	public static final String ACTION_ASSIGN_WORKFLOW = "ASSIGN_WORKFLOW";
	public static final String ACTION_ADD_CATEGORY = "ADD_CATEGORY";
	public static final String ACTION_ADD_KEYWORD = "ADD_KEYWORD";
	public static final String ACTION_EXTRACT_METADATA = "EXTRACT_METADATA";
	
	private int id;
	private String action;
	private String value;
	private boolean active;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id="); sb.append(id);
		sb.append(", action="); sb.append(action);
		sb.append(", value="); sb.append(value);
		sb.append(", active="); sb.append(active);
		sb.append("}");
		return sb.toString();
	}
}
