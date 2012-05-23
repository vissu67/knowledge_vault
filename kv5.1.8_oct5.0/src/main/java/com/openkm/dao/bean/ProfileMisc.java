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

public class ProfileMisc implements Serializable {
	private static final long serialVersionUID = 1L;
	private boolean advancedFilters;
	private long userQuota;
	private String webSkin;
	private boolean printPreview;
	private boolean keywordsEnabled;
	private Set<String> extensions = new HashSet<String>();
	private Set<Integer> reports = new HashSet<Integer>();

	public boolean isAdvancedFilters() {
		return advancedFilters;
	}

	public void setAdvancedFilters(boolean advancedFilters) {
		this.advancedFilters = advancedFilters;
	}

	public long getUserQuota() {
		return userQuota;
	}

	public void setUserQuota(long userQuota) {
		this.userQuota = userQuota;
	}
	
	public String getWebSkin() {
		return webSkin;
	}

	public void setWebSkin(String webSkin) {
		this.webSkin = webSkin;
	}
	
	public boolean isPrintPreview() {
		return printPreview;
	}

	public void setPrintPreview(boolean printPreview) {
		this.printPreview = printPreview;
	}
	
	public boolean isKeywordsEnabled() {
		return keywordsEnabled;
	}

	public void setKeywordsEnabled(boolean keywordsEnabled) {
		this.keywordsEnabled = keywordsEnabled;
	}
	
	public Set<String> getExtensions() {
		return extensions;
	}

	public void setExtensions(Set<String> extensions) {
		this.extensions = extensions;
	}
	
	public Set<Integer> getReports() {
		return reports;
	}

	public void setReports(Set<Integer> reports) {
		this.reports = reports;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("userQuota="); sb.append(userQuota);
		sb.append(", advancedFilters="); sb.append(advancedFilters);
		sb.append(", webSkin="); sb.append(webSkin);
		sb.append(", printPreview="); sb.append(printPreview);
		sb.append(", keywordsEnabled="); sb.append(keywordsEnabled);
		sb.append(", extensions="); sb.append(extensions);
		sb.append(", reports="); sb.append(reports);
		sb.append("}");
		return sb.toString();
	}
}
