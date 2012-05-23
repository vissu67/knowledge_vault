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

package com.openkm.frontend.client.widget.searchsaved;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.ConfirmPopup;

/**
 * Search saved menu
 * 
 * @author jllort
 *
 */
public class Menu extends Composite {
	private MenuBar searchSavedMenu;
	private MenuItem run;
	private MenuItem delete;
	
	/**
	 * Browser menu
	 */
	public Menu() {
		// The item selected must be called on style.css : .okm-MenuBar .gwt-MenuItem-selected
		
		// First initialize language values
		searchSavedMenu = new MenuBar(true);
		run = new MenuItem(Util.menuHTML("img/icon/actions/run.gif", Main.i18n("search.saved.run")), true, runSearch);
		run.addStyleName("okm-MenuItem");
		searchSavedMenu.addItem(run);
		delete = new MenuItem(Util.menuHTML("img/icon/actions/delete.gif", Main.i18n("search.saved.delete")), true, deleteSearch);
		delete.addStyleName("okm-MenuItem");
		searchSavedMenu.addItem(delete);
		searchSavedMenu.addStyleName("okm-MenuBar");
		initWidget(searchSavedMenu);
	}
	
	// Command menu to save file
	Command runSearch = new Command() {
		public void execute() {
			Main.get().mainPanel.search.historySearch.searchSaved.getSearch();
			hide();
		}
	};
	
	// Command menu to go directory file
	Command deleteSearch = new Command() {
		public void execute() {
			Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_DELETE_SAVED_SEARCH);
			Main.get().confirmPopup.show();
			hide();
		}
	};

	/**
	 *  Refresh language values
	 */
	public void langRefresh() {
		run.setHTML(Util.menuHTML("img/icon/actions/run.gif", Main.i18n("search.saved.run")));
		delete.setHTML(Util.menuHTML("img/icon/actions/delete.gif", Main.i18n("search.saved.delete")));
	}
	
	/**
	 * Hide popup menu
	 */
	public void hide() {
		Main.get().mainPanel.search.historySearch.searchSaved.menuPopup.hide();
	}
	
	/**
	 * Enables menu item
	 * 
	 * @param menuItem The menu item
	 */
	public void enable(MenuItem menuItem) {
		menuItem.addStyleName("okm-MenuItem");
		menuItem.removeStyleName("okm-MenuItem-strike");
	}
	
	/**
	 * Disable the menu item
	 * 
	 * @param menuItem The menu item
	 */
	public void disable(MenuItem menuItem) {
		menuItem.removeStyleName("okm-MenuItem");
		menuItem.addStyleName("okm-MenuItem-strike");
	}
}