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

public class ProfileMenu implements Serializable {
	private static final long serialVersionUID = 1L;
	private boolean fileVisible;
	private boolean editVisible;
	private boolean toolsVisible;
	private boolean bookmarksVisible;
	private boolean helpVisible;
	private ProfileMenuFile file = new ProfileMenuFile();
	private ProfileMenuBookmark bookmark = new ProfileMenuBookmark();
	private ProfileMenuTool tool = new ProfileMenuTool();
	private ProfileMenuEdit edit = new ProfileMenuEdit();
	private ProfileMenuHelp help = new ProfileMenuHelp();
	
	public boolean isFileVisible() {
		return fileVisible;
	}

	public void setFileVisible(boolean fileVisible) {
		this.fileVisible = fileVisible;
	}
	
	public boolean isEditVisible() {
		return editVisible;
	}

	public void setEditVisible(boolean editVisible) {
		this.editVisible = editVisible;
	}

	public boolean isToolsVisible() {
		return toolsVisible;
	}

	public void setToolsVisible(boolean toolsVisible) {
		this.toolsVisible = toolsVisible;
	}

	public boolean isBookmarksVisible() {
		return bookmarksVisible;
	}

	public void setBookmarksVisible(boolean bookmarksVisible) {
		this.bookmarksVisible = bookmarksVisible;
	}

	public boolean isHelpVisible() {
		return helpVisible;
	}

	public void setHelpVisible(boolean helpVisible) {
		this.helpVisible = helpVisible;
	}

	public ProfileMenuFile getFile() {
		return file;
	}

	public void setFile(ProfileMenuFile file) {
		this.file = file;
	}
	
	public ProfileMenuBookmark getBookmark() {
		return bookmark;
	}

	public void setBookmark(ProfileMenuBookmark bookmark) {
		this.bookmark = bookmark;
	}

	public ProfileMenuTool getTool() {
		return tool;
	}

	public void setTool(ProfileMenuTool tool) {
		this.tool = tool;
	}

	public ProfileMenuEdit getEdit() {
		return edit;
	}

	public void setEdit(ProfileMenuEdit edit) {
		this.edit = edit;
	}

	public ProfileMenuHelp getHelp() {
		return help;
	}

	public void setHelp(ProfileMenuHelp help) {
		this.help = help;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("fileVisible="); sb.append(fileVisible);
		sb.append(", editVisible="); sb.append(editVisible);
		sb.append(", toolsVisible="); sb.append(toolsVisible);
		sb.append(", bookmarksVisible="); sb.append(bookmarksVisible);
		sb.append(", helpVisible="); sb.append(helpVisible);
		sb.append(", file="); sb.append(file);
		sb.append(", bookmark="); sb.append(bookmark);
		sb.append(", tool="); sb.append(tool);
		sb.append(", edit="); sb.append(edit);
		sb.append(", help="); sb.append(help);
		sb.append("}");
		return sb.toString();
	}
}
