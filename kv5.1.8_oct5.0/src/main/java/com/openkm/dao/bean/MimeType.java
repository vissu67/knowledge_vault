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

public class MimeType implements Serializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String name;
	private String imageContent;
	private String imageMime;
	private Set<String> extensions = new HashSet<String>();
	
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
	
	public Set<String> getExtensions() {
		return extensions;
	}

	public void setExtensions(Set<String> extensions) {
		this.extensions = extensions;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("id="); sb.append(id);
		sb.append(", name="); sb.append(name);
		sb.append(", imageMime="); sb.append(imageMime);
		sb.append(", imageContent="); sb.append("[BIG]");
		sb.append(", extensions="); sb.append(extensions);
		sb.append("}");
		return sb.toString();
	}
}
