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

package com.openkm.frontend.client.extension.comunicator;

import java.util.Collection;

import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTNote;

/**
 * TabDocumentComunicator
 * 
 * @author jllort
 *
 */
public class TabDocumentComunicator {
	
	/**
	 * getSelectedTab
	 * 
	 * @return
	 */
	public static int getSelectedTab() {
		return Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.getSelectedTab();
	}
	
	/**
	 * getDocument
	 * 
	 * @return
	 */
	public static GWTDocument getDocument() {
		return Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.getDocument();
	}
	
	/**
	 * addKeyword
	 * 
	 * @param keyword
	 */
	public static void addKeyword(String keyword) {
		Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.document.addKeyword(keyword);
	}
	
	/**
	 * removeKeyword
	 * 
	 * @param keyword
	 */
	public static void removeKeyword(String keyword) {
		Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.document.removeKey(keyword);
	}
	
	/**
	 * addCategory
	 * 
	 * @param keyword
	 */
	public static void addCategory(GWTFolder category) {
		Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.document.addCategory(category);
	}
	
	/**
	 * removeKeyword
	 * 
	 * @param keyword
	 */
	public static void removeCategory(String UUID) {
		Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.document.removeCategory(UUID);
	}
	
	/**
	 * getKeywords
	 * 
	 * @return
	 */
	public static Collection<String> getKeywords() {
		return Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.document.getKeywords();
	}
	
	/**
	 * getNotes
	 * 
	 * @return
	 */
	public static Collection<GWTNote> getNotes() {
		return Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.notes.getNotes();
	}
	
	/**
	 * isVisibleButton
	 * 
	 * @return
	 */
	public static boolean isVisibleButton() {
		return Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.isVisibleButton();
	}
	
	/**
	 * setRefreshingStyle
	 */
	public static void setRefreshingStyle() {
		Main.get().mainPanel.desktop.browser.tabMultiple.setStyleName("okm-PanelRefreshing");
	}
	
	/**
	 * unsetRefreshingStyle
	 */
	public static void unsetRefreshingStyle() {
		Main.get().mainPanel.desktop.browser.tabMultiple.removeStyleName("okm-PanelRefreshing");
	}
}