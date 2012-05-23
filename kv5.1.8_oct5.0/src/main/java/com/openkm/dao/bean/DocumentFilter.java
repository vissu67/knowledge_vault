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
import java.util.HashSet;
import java.util.Set;

public class DocumentFilter implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static final String TYPE_PATH = "PATH";
	public static final String TYPE_MIME_TYPE = "MIME_TYPE";
	
	private int id;
	private String type;
	private String value;
	private boolean active = false;
	private Set<DocumentFilterRule> filterRules = new HashSet<DocumentFilterRule>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
	
	public Set<DocumentFilterRule> getFilterRules() {
		return filterRules;
	}

	public void setFilterRules(Set<DocumentFilterRule> filterRules) {
		this.filterRules = filterRules;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id="); sb.append(id);
		sb.append(", type="); sb.append(type);
		sb.append(", value="); sb.append(value);
		sb.append(", active="); sb.append(active);
		sb.append(", filterRules="); sb.append(filterRules);
		sb.append("}");
		return sb.toString();
	}
}
