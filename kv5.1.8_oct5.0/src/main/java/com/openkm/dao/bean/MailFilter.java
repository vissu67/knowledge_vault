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

public class MailFilter implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String path;
	private String uuid;
	private boolean grouping = true;
	private boolean active = false;
	private Set<MailFilterRule> filterRules = new HashSet<MailFilterRule>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public boolean isGrouping() {
		return grouping;
	}

	public void setGrouping(boolean grouping) {
		this.grouping = grouping;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
	
	public Set<MailFilterRule> getFilterRules() {
		return filterRules;
	}

	public void setFilterRules(Set<MailFilterRule> filterRules) {
		this.filterRules = filterRules;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id="); sb.append(id);
		sb.append(", path="); sb.append(path);
		sb.append(", uuid="); sb.append(uuid);
		sb.append(", grouping="); sb.append(grouping);
		sb.append(", active="); sb.append(active);
		sb.append(", filterRules="); sb.append(filterRules);
		sb.append("}");
		return sb.toString();
	}
}
