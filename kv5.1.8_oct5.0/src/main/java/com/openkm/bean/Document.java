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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author pavila
 * 
 */
public class Document implements Serializable {
	private static final long serialVersionUID = 4453338766237619444L;
	
	public static final String TYPE = "okm:document";
	public static final String CONTENT = "okm:content"; 
	public static final String CONTENT_TYPE = "okm:resource";
	public static final String SIZE = "okm:size";
	public static final String LANGUAGE = "okm:language";
	public static final String AUTHOR = "okm:author";
	public static final String VERSION_COMMENT = "okm:versionComment";
	public static final String NAME = "okm:name";
	public static final String TEXT = "okm:text";
	// public static final String TITLE = "okm:title";
	// public static final String TITLE = "okm:description";
	
	private String path;
	// private String title = "";
	// private String description = "";
	private String language = "";
	private String author;
	private Calendar created;
	private Calendar lastModified;
	private String mimeType;
	private boolean locked;
	private boolean checkedOut;
	private Version actualVersion;
	private byte permissions;
	private Lock lockInfo;
	private String uuid;
	private boolean subscribed;
	private boolean convertibleToPdf;
	private boolean convertibleToSwf;
	private boolean convertibleToDxf;
	private String cipherName;
	private Set<String> subscriptors = new HashSet<String>();
	private Set<String> keywords = new HashSet<String>();
	private Set<Folder> categories = new HashSet<Folder>();
	private List<Note> notes = new ArrayList<Note>();

	public Lock getLockInfo() {
		return lockInfo;
	}

	public void setLockInfo(Lock lockInfo) {
		this.lockInfo = lockInfo;
	}

	public Calendar getLastModified() {
		return lastModified;
	}

	public void setLastModified(Calendar lastModified) {
		this.lastModified = lastModified;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Calendar getCreated() {
		return created;
	}

	public void setCreated(Calendar created) {
		this.created = created;
	}

	public Set<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(Set<String> keywords) {
		this.keywords = keywords;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}
	
	// public String getTitle() {
	// 	return title;
	// }

	// public void setTitle(String title) {
	// 	this.title = title;
	// }
	
	public boolean isCheckedOut() {
		return checkedOut;
	}

	public void setCheckedOut(boolean checkedOut) {
		this.checkedOut = checkedOut;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	
	public Version getActualVersion() {
		return actualVersion;
	}

	public void setActualVersion(Version actualVersion) {
		this.actualVersion = actualVersion;
	}

	public byte getPermissions() {
		return permissions;
	}

	public void setPermissions(byte permissions) {
		this.permissions = permissions;
	}

	public boolean isSubscribed() {
		return subscribed;
	}

	public void setSubscribed(boolean subscribed) {
		this.subscribed = subscribed;
	}

	public Set<String> getSubscriptors() {
		return subscriptors;
	}

	public void setSubscriptors(Set<String> subscriptors) {
		this.subscriptors = subscriptors;
	}
	
	public Set<Folder> getCategories() {
		return categories;
	}

	public void setCategories(Set<Folder> categories) {
		this.categories = categories;
	}
	
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public boolean isConvertibleToPdf() {
		return convertibleToPdf;
	}

	public void setConvertibleToPdf(boolean convertibleToPdf) {
		this.convertibleToPdf = convertibleToPdf;
	}

	public boolean isConvertibleToSwf() {
		return convertibleToSwf;
	}

	public void setConvertibleToSwf(boolean convertibleToSwf) {
		this.convertibleToSwf = convertibleToSwf;
	}
	
	public boolean isConvertibleToDxf() {
		return convertibleToDxf;
	}

	public void setConvertibleToDxf(boolean convertibleToDxf) {
		this.convertibleToDxf = convertibleToDxf;
	}
	
	public void setCipherName(String cipherName) {
		this.cipherName = cipherName;
	}

	public String getCipherName() {
		return cipherName;
	}
	
	public List<Note> getNotes() {
		return notes;
	}

	public void setNotes(List<Note> notes) {
		this.notes = notes;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("path="); sb.append(path);
		// sb.append(", title="); sb.append(title);
		// sb.append(", description="); sb.append(description);
		sb.append(", mimeType="); sb.append(mimeType);
		sb.append(", author="); sb.append(author);
		sb.append(", permissions="); sb.append(permissions);
		sb.append(", created="); sb.append(created==null?null:created.getTime());
		sb.append(", lastModified="); sb.append(lastModified==null?null:lastModified.getTime());
		sb.append(", keywords="); sb.append(keywords);
		sb.append(", categories="); sb.append(categories);
		sb.append(", locked="); sb.append(locked);
		sb.append(", lockInfo="); sb.append(lockInfo);
		sb.append(", actualVersion="); sb.append(actualVersion);
		sb.append(", subscribed="); sb.append(subscribed);
		sb.append(", uuid="); sb.append(uuid);
		sb.append(", convertibleToPdf="); sb.append(convertibleToPdf);
		sb.append(", convertibleToSwf="); sb.append(convertibleToSwf);
		sb.append(", convertibleToDxf="); sb.append(convertibleToDxf);
		sb.append(", cipherName="); sb.append(cipherName);
		sb.append(", notes="); sb.append(notes);
		sb.append("}");
		return sb.toString();
	}
}
