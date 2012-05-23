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

package com.openkm.frontend.client.bean;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * @author jllort
 *
 */
public class GWTFolder implements IsSerializable {
	
	public static final String TYPE = "okm:folder";

	private String parentPath;
	private String path;
	private String name;
	private boolean hasChilds;
	private Date created;
	private String author;
	private byte permissions;
	private boolean subscribed;
	private String uuid;
	private boolean hasNotes = false;	
	private List<GWTNote> notes;
	private Set<String> subscriptors;
	
	/**
	 * @return Returns the subscriptors.
	 */
	public Set<String> getSubscriptors() {
		return subscriptors;
	}

	/**
	 * @param subscriptors The subscriptors to set.
	 */
	public void setSubscriptors(Set<String> subscriptors) {
		this.subscriptors = subscriptors;
	}

	public boolean getHasChilds() {
		return hasChilds;
	}

	public void setHasChilds(boolean parent) {
		this.hasChilds = parent;
	}
	
	public String getParentPath() {
		return parentPath;
	}
	
	public void setParentPath(String parentPath) {
		this.parentPath = parentPath;
	}

	public String getName() {
		//vissu touch to change left hand folder tree names on oct'9th
				name = name.replaceAll("okm:root","Knowledge Vault");
				name = name.replaceAll("okm:template","Template");
				name = name.replaceAll("okm:personal","Personal");
				name = name.replaceAll("okm:trash","Trash");
				
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	public void setCreated(Date created) {
		this.created = created;
	}
	
	public Date getCreated() {
		return created;
	}
	
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
	
	public byte getPermissions() {
		return permissions;
	}

	public void setPermissions(byte permissions) {
		this.permissions = permissions;
	}
	
	public String toString() {
		return "[path="+path+", name="+name+", hasChilds="+hasChilds+"]";
	}

	/**
	 * @return Returns the subscribed.
	 */
	public boolean isSubscribed() {
		return subscribed;
	}

	/**
	 * @param subscribed The subscribed to set.
	 */
	public void setSubscribed(boolean subscribed) {
		this.subscribed = subscribed;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public boolean isHasNotes() {
		return hasNotes;
	}

	public void setHasNotes(boolean hasNotes) {
		this.hasNotes = hasNotes;
	}
	
	public List<GWTNote> getNotes() {
		return notes;
	}

	public void setNotes(List<GWTNote> notes) {
		this.notes = notes;
	}
}