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

package com.openkm.frontend.client.widget;

import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTMail;

/**
 * Root menu popup
 * 
 * @author jllort
 *
 */
public class MenuPopup extends PopupPanel {
	
	public VerticalPanel panel;
	public MenuBase menu;
	
	public MenuPopup(MenuBase menu) {
		// Establishes auto-close when click outside
		super(true,true);
		
		this.menu = menu;
		panel = new VerticalPanel();
		panel.add(menu);
		setWidget(panel);
	}
	
	/**
	 * Refresh language values
	 */
	public void langRefresh() {
		menu.langRefresh();
	}
	
	/**
	 * Set enabled all menu options
	 */
	public void enableAllMenuOptions() {
		menu.enableAllMenuOptions();
	}
	
	/**
	 * Set enabled root menu options
	 */
	public void enableRootMenuOptions() {
		menu.enableRootMenuOptions();
	}
	
	/**
	 * Checks permissions associated to folder and menu options enabled actions
	 * 
	 * @param folder The folder
	 */
	public void checkMenuOptionPermissions(GWTFolder folder, GWTFolder folderParent) {
		menu.checkMenuOptionPermissions(folder, folderParent);
	}
	
	/**
	 * Evaluates menu options
	 */
	public void evaluateMenuOptions() {
		menu.evaluateMenuOptions();
	}
	
	/**
	 * Disables all menu options
	 */
	public void disableAllMenuOption(){
		menu.disableAllMenuOption();
	}
	
	/**
	 * Checks permissions associated to document and menu options enabled actions
	 * 
	 * @param doc The document
	 */
	public void checkMenuOptionPermissions(GWTDocument doc, GWTFolder folder ) {
		menu.checkMenuOptionPermissions(doc, folder);
	}
	
	/**
	 * Checks permissions associated to mail and menu options enabled actions
	 * 
	 * @param mail The mail
	 */
	public void checkMenuOptionPermissions(GWTMail mail, GWTFolder folder ) {
		menu.checkMenuOptionPermissions(mail, folder);
	}
}