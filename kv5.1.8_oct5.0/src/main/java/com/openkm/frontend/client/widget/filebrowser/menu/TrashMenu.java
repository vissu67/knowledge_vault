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

package com.openkm.frontend.client.widget.filebrowser.menu;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTAvailableOption;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.MenuBase;

/**
 * Trash menu
 * 
 * @author jllort
 *
 */
public class TrashMenu extends MenuBase {
	
	private MenuBar dirMenu;
	private MenuItem restoreItem;
	private MenuItem purgeItem;
	
	public TrashMenu() {
		// The item selected must be called on style.css : .okm-MenuBar .gwt-MenuItem-selected
		
		// First initialize language values
		dirMenu = new MenuBar(true);
		restoreItem = new MenuItem(Util.menuHTML("img/icon/actions/restore.gif", Main.i18n("trash.menu.directory.restore")), true, restore);
		restoreItem.addStyleName("okm-MenuItem");
		dirMenu.addItem(restoreItem);
		purgeItem = new MenuItem(Util.menuHTML("img/icon/actions/purge.gif", Main.i18n("trash.menu.directory.purge")), true, purge);
		purgeItem.addStyleName("okm-MenuItem");
		dirMenu.addItem(purgeItem);
		dirMenu.addStyleName("okm-MenuBar");
		initWidget(dirMenu);
	}
	
	// Command menu to restore Directory or Document
	Command restore = new Command() {
		public void execute() {
			Main.get().mainPanel.desktop.browser.fileBrowser.trashMenuPopup.hide();
			Main.get().mainPanel.desktop.browser.fileBrowser.restore();
		}
	};
	
	// Command menu to remove a Directory or Document
	Command purge = new Command() {
		public void execute() {
			Main.get().mainPanel.desktop.browser.fileBrowser.trashMenuPopup.hide();
			Main.get().mainPanel.desktop.browser.fileBrowser.confirmPurge();
		}
	};
	
	
	/**
	 *  Refresh language values
	 */
	public void langRefresh() {
		restoreItem.setHTML(Util.menuHTML("img/icon/actions/restore.gif", Main.i18n("trash.menu.directory.restore")));
		purgeItem.setHTML(Util.menuHTML("img/icon/actions/purge.gif", Main.i18n("trash.menu.directory.purge")));
	}
	
	@Override
	public void setAvailableOption(GWTAvailableOption option) {
		restoreItem.setVisible(option.isRestoreOption());
		purgeItem.setVisible(option.isPurgeOption());
	}
	
	/* (non-Javadoc)
	 * @see com.openkm.frontend.client.widget.MenuBase#checkMenuOptionPermissions(com.openkm.frontend.client.bean.GWTFolder, com.openkm.frontend.client.bean.GWTFolder)
	 */
	public void checkMenuOptionPermissions(GWTFolder folder, GWTFolder folderParent) {
	}

	/* (non-Javadoc)
	 * @see com.openkm.frontend.client.widget.MenuBase#enableAllMenuOptions()
	 */
	public void enableAllMenuOptions() {
	}

	/* (non-Javadoc)
	 * @see com.openkm.frontend.client.widget.MenuBase#enableRootMenuOptions()
	 */
	public void enableRootMenuOptions() {
	}

	/* (non-Javadoc)
	 * @see com.openkm.frontend.client.widget.MenuBase#evaluateMenuOptions()
	 */
	public void evaluateMenuOptions() {
	}

	/* (non-Javadoc)
	 * @see com.openkm.frontend.client.widget.MenuBase#disableAllMenuOption()
	 */
	public void disableAllMenuOption() {
	}

	/* (non-Javadoc)
	 * @see com.openkm.frontend.client.widget.MenuBase#checkMenuOptionPermissions(com.openkm.frontend.client.bean.GWTDocument, com.openkm.frontend.client.bean.GWTFolder)
	 */
	public void checkMenuOptionPermissions(GWTDocument doc, GWTFolder folder) {
	}
	
	/* (non-Javadoc)
	 * @see com.openkm.frontend.client.widget.MenuBase#checkMenuOptionPermissions(com.openkm.frontend.client.bean.GWTMail, com.openkm.frontend.client.bean.GWTFolder)
	 */
	public void checkMenuOptionPermissions(GWTMail mail, GWTFolder folder) {
	}
}