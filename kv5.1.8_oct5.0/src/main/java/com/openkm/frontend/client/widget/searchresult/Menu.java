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

package com.openkm.frontend.client.widget.searchresult;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTAvailableOption;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.util.Util;

/**
 * Search result menu
 * 
 * @author jllort
 *
 */
public class Menu extends Composite {
	
	private boolean downloadOption 	= false;
	private boolean goOption 		= false;
	
	private MenuBar searchMenu;
	private MenuItem download;
	private MenuItem go;
	
	/**
	 * Browser menu
	 */
	public Menu() {
		// The item selected must be called on style.css : .okm-MenuBar .gwt-MenuItem-selected
		
		// First initialize language values
		searchMenu = new MenuBar(true);
		download = new MenuItem(Util.menuHTML("img/icon/actions/download.gif", Main.i18n("search.result.menu.download")), true, downloadFile);
		download.addStyleName("okm-MenuItem");
		searchMenu.addItem(download);
		go = new MenuItem(Util.menuHTML("img/icon/actions/goto_folder.gif", Main.i18n("search.result.menu.go.folder")), true, goDirectory);
		go.addStyleName("okm-MenuItem");
		searchMenu.addItem(go);
		searchMenu.setStyleName("okm-MenuBar");
		initWidget(searchMenu);
	}
	
	// Command menu to download file
	Command downloadFile = new Command() {
		public void execute() {
			if (downloadOption) {
				Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.downloadDocument();
			}
			hide();
		}
	};
	
	// Command menu to go directory file
	Command goDirectory = new Command() {
		public void execute() {
			if (goOption) {
				Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.openAllFolderPath();
			}
			hide();
		}
	};

	/**
	 *  Refresh language values
	 */
	public void langRefresh() {
		download.setHTML(Util.menuHTML("img/icon/actions/download.gif", Main.i18n("search.result.menu.download")));
		go.setHTML(Util.menuHTML("img/icon/actions/goto_folder.gif", Main.i18n("search.result.menu.go.folder")));
	}
	
	/**
	 * Hide popup menu
	 */
	public void hide() {
		Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.menuPopup.hide();
	}
	
	/**
	 * Checks permissions
	 * 
	 * @param folder
	 */
	public void checkMenuOptionPermissions(GWTFolder folder) {
		downloadOption 	= false;
		goOption 		= true;
	}
	
	/**
	 * Checks permissions
	 * 
	 * @param doc
	 */
	public void checkMenuOptionPermissions(GWTDocument doc) {
		downloadOption	= true;
		goOption 		= true;
	}
	
	/**
	 * Checks permissions
	 * 
	 * @param mail
	 */
	public void checkMenuOptionPermissions(GWTMail mail) {
		downloadOption 	= true;
		goOption 		= true;
	}
	
	/**
	 * Evaluates menu options
	 */
	public void evaluateMenuOptions() {
		if (downloadOption){enable(download);} else {disable(download);}
		if (goOption){enable(go);} else {disable(go);}
		if (Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.table.isFolderSelected() ) {
			go.setHTML(Util.menuHTML("img/icon/actions/goto_folder.gif", Main.i18n("search.result.menu.go.folder")));
		} else {
			go.setHTML(Util.menuHTML("img/icon/actions/goto_document.gif", Main.i18n("search.result.menu.go.document")));
		}
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
	
	/**
	 * setAvailableOption
	 * 
	 * @param option
	 */
	public void setAvailableOption(GWTAvailableOption option) {
		download.setVisible(option.isDownloadOption());
		go.setVisible(option.isGotoFolderOption());
	}
}