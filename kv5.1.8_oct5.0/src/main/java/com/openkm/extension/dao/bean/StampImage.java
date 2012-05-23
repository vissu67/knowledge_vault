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

package com.openkm.extension.dao.bean;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class StampImage implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private String description;
	private String imageContent;
	private String imageMime;
	private int layer;
	private float opacity;
	private String exprX;
	private String exprY;
	private boolean active;
	private Set<String> users = new HashSet<String>();

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		if (description.length() > 512) {
			this.description = description.substring(0, 512);
		} else {
			this.description = description;
		}
	}

	public String getImageContent() {
		return imageContent;
	}

	public void setImageContent(String imageContent) {
		this.imageContent = imageContent;
	}

	public String getImageMime() {
		return imageMime;
	}

	public void setImageMime(String imageMime) {
		this.imageMime = imageMime;
	}
	
	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}
	
	public float getOpacity() {
		return opacity;
	}

	public void setOpacity(float opacity) {
		this.opacity = opacity;
	}

	public String getExprX() {
		return exprX;
	}

	public void setExprX(String exprX) {
		this.exprX = exprX;
	}

	public String getExprY() {
		return exprY;
	}

	public void setExprY(String exprY) {
		this.exprY = exprY;
	}
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Set<String> getUsers() {
		return users;
	}

	public void setUsers(Set<String> users) {
		this.users = users;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id="); sb.append(id);
		sb.append(", name="); sb.append(name);
		sb.append(", description="); sb.append(description);
		sb.append(", imageMime="); sb.append(imageMime);
		sb.append(", imageContent="); sb.append("[BIG]");
		sb.append(", layer="); sb.append(layer);
		sb.append(", opacity="); sb.append(opacity);
		sb.append(", exprX="); sb.append(exprX);
		sb.append(", exprY="); sb.append(exprY);
		sb.append(", active="); sb.append(active);
		sb.append(", users="); sb.append(users);
		sb.append("}");
		return sb.toString();
	}
}
