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

package com.openkm.frontend.client.widget.trash;

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
	
	private boolean restoreFolderOption 	= false; 
	private boolean purgeFolderOption 		= false;
	private boolean purgeTrashFolderOption 	= true;
	
	private MenuBar dirMenu;
	private MenuItem restore;
	private MenuItem purge;
	private MenuItem purgeTrash;
	
	public TrashMenu() {
		// The item selected must be called on style.css : .okm-MenuBar .gwt-MenuItem-selected
		
		// First initialize language values
		dirMenu = new MenuBar(true);
		restore = new MenuItem(Util.menuHTML("img/icon/actions/restore.gif", Main.i18n("trash.menu.directory.restore")), true, restoreFolder);
		restore.addStyleName("okm-MenuItem");
		dirMenu.addItem(restore);
		purge = new MenuItem(Util.menuHTML("img/icon/actions/purge.gif", Main.i18n("trash.menu.directory.purge")), true, purgeFolder);
		purge.addStyleName("okm-MenuItem");
		dirMenu.addItem(purge);
		purgeTrash = new MenuItem(Util.menuHTML("img/icon/actions/purge_trash.gif", Main.i18n("trash.menu.directory.purge.trash")), true, purgeTrashFolder);
		purgeTrash.addStyleName("okm-MenuItem");
		dirMenu.addItem(purgeTrash);
		dirMenu.setStyleName("okm-MenuBar");
		initWidget(dirMenu);
	}
	
	// Command menu to restore Directory
	Command restoreFolder = new Command() {
		public void execute() {
			if(restoreFolderOption) {
				Main.get().activeFolderTree.restore();
				Main.get().activeFolderTree.hideMenuPopup();
			}
		}
	};
	
	// Command menu to remove a Directory
	Command purgeFolder = new Command() {
		public void execute() {
			if (purgeFolderOption) {
				Main.get().activeFolderTree.confirmPurge();
				Main.get().activeFolderTree.hideMenuPopup();
			}
		}
	};
	
	// Command menu to remove all trash folder
	Command purgeTrashFolder = new Command() {
		public void execute() {
			if (purgeTrashFolderOption) {
				Main.get().activeFolderTree.confirmPurgeTrash();
				Main.get().activeFolderTree.hideMenuPopup();
			}
		}
	};
	
	/**
	 *  Refresh language values
	 */
	public void langRefresh() {
		restore.setHTML(Util.menuHTML("img/icon/actions/restore.gif", Main.i18n("trash.menu.directory.restore")));
		purge.setHTML(Util.menuHTML("img/icon/actions/purge.gif", Main.i18n("trash.menu.directory.purge")));
		purgeTrash.setHTML(Util.menuHTML("img/icon/actions/purge_trash.gif", Main.i18n("trash.menu.directory.purge.trash")));
	}
	
	/**
	 * Set enabled all menu options
	 */
	public void enableAllMenuOptions(){
		restoreFolderOption		= true; 
		purgeFolderOption		= true;
		purgeTrashFolderOption 	= true;
	}
	
	/**
	 * Set enabled root menu options
	 */
	public void enableRootMenuOptions(){
		restoreFolderOption		= false; 
		purgeFolderOption		= false;
		purgeTrashFolderOption 	= true;
	}
	
	/**
	 * Evaluates menu options
	 */
	public void evaluateMenuOptions(){
		if (restoreFolderOption) {enable(restore);} else {disable(restore);}
		if (purgeFolderOption) {enable(purge);} else {disable(purge);}
		if (purgeTrashFolderOption) {enable(purgeTrash);} else {disable(purgeTrash);}
	}
	
	@Override
	public void setAvailableOption(GWTAvailableOption option) {
		restore.setVisible(option.isRestoreOption());
		purge.setVisible(option.isPurgeOption());
		purgeTrash.setVisible(option.isPurgeTrashOption());
	}

	public void checkMenuOptionPermissions(GWTFolder folder, GWTFolder folderParent) {
		// TODO Auto-generated method stub	
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