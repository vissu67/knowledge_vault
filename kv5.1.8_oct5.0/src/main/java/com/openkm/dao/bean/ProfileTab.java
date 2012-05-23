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

public class ProfileTab implements Serializable {
	private static final long serialVersionUID = 1L;
	private boolean desktopVisible;
	private boolean searchVisible;
	private boolean dashboardVisible;
	private boolean administrationVisible;
	private ProfileTabFolder folder = new ProfileTabFolder();
	private ProfileTabDocument document = new ProfileTabDocument();
	private ProfileTabMail mail = new ProfileTabMail();

	public boolean isDesktopVisible() {
		return desktopVisible;
	}

	public void setDesktopVisible(boolean desktopVisible) {
		this.desktopVisible = desktopVisible;
	}

	public boolean isSearchVisible() {
		return searchVisible;
	}

	public void setSearchVisible(boolean searchVisible) {
		this.searchVisible = searchVisible;
	}

	public boolean isDashboardVisible() {
		return dashboardVisible;
	}

	public void setDashboardVisible(boolean dashboardVisible) {
		this.dashboardVisible = dashboardVisible;
	}
	
	public boolean isAdministrationVisible() {
		return administrationVisible;
	}

	public void setAdministrationVisible(boolean administrationVisible) {
		this.administrationVisible = administrationVisible;
	}

	public ProfileTabFolder getFolder() {
		return folder;
	}

	public void setFolder(ProfileTabFolder folder) {
		this.folder = folder;
	}

	public ProfileTabDocument getDocument() {
		return document;
	}

	public void setDocument(ProfileTabDocument document) {
		this.document = document;
	}

	public ProfileTabMail getMail() {
		return mail;
	}

	public void setMail(ProfileTabMail mail) {
		this.mail = mail;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("desktopVisible="); sb.append(desktopVisible);
		sb.append(", searchVisible="); sb.append(searchVisible);
		sb.append(", dashboardVisible="); sb.append(dashboardVisible);
		sb.append(", administrationVisible="); sb.append(administrationVisible);
		sb.append(", document="); sb.append(document);
		sb.append(", folder="); sb.append(folder);
		sb.append(", mail="); sb.append(mail);
		sb.append("}");
		return sb.toString();
	}
}
