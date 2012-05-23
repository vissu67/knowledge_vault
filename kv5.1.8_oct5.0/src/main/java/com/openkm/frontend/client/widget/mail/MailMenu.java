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

package com.openkm.frontend.client.widget.mail;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTAvailableOption;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.bean.GWTPermission;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.MenuBase;

/**
 * Mail menu
 * 
 * @author jllort
 *
 */
public class MailMenu extends MenuBase {
	
	private boolean createOption 	= true;
	private boolean removeOption 	= false;
	private boolean renameOption 	= false;
	private boolean moveOption 		= false;
	private boolean copyOption 		= false;
	
	private boolean rootNode 		= true;  // Indicates root node selected ( option menu are specific on this case ).
	private MenuBar dirMenu;
	private MenuItem create;
	private MenuItem remove;
	private MenuItem rename;
	private MenuItem move;
	private MenuItem copy;
	
	public MailMenu() {
		// The item selected must be called on style.css : .okm-MenuBar .gwt-MenuItem-selected
		
		// First initialize language values
		dirMenu = new MenuBar(true);
		create = new MenuItem(Util.menuHTML("img/icon/actions/add_folder.gif", Main.i18n("tree.menu.directory.create")), true, addFolder);
		create.addStyleName("okm-MenuItem");
		dirMenu.addItem(create);
		remove = new MenuItem(Util.menuHTML("img/icon/actions/delete.gif", Main.i18n("tree.menu.directory.remove")), true, delFolder);
		remove.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(remove);
		rename = new MenuItem(Util.menuHTML("img/icon/actions/rename.gif", Main.i18n("tree.menu.directory.rename")), true, renFolder);
		rename.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(rename);
		move = new MenuItem(Util.menuHTML("img/icon/actions/move_folder.gif", Main.i18n("tree.menu.directory.move")), true, moveFolder);
		move.addStyleName("okm-MenuItem");
		dirMenu.addItem(move);
		copy = new MenuItem(Util.menuHTML("img/icon/actions/copy.gif", Main.i18n("tree.menu.directory.copy")), true, copyFolder);
		copy.addStyleName("okm-MenuItem");
		dirMenu.addItem(copy);
		dirMenu.setStyleName("okm-MenuBar");
		initWidget(dirMenu);
	}
	
	// Command menu to add a new Directory
	Command addFolder = new Command() {
		public void execute() {
			if (createOption) {
				Main.get().activeFolderTree.addTmpFolderCreate();
				Main.get().activeFolderTree.hideMenuPopup();
			}
		}
	};
	
	// Command menu to delete a new Directory
	Command delFolder = new Command() {
		public void execute() {
			if (removeOption){
				Main.get().activeFolderTree.confirmDelete();
				Main.get().activeFolderTree.hideMenuPopup();
			}
		}
	};
	
	// Command menu to delete a new Directory
	Command renFolder = new Command() {
		public void execute() {
			if (renameOption) {
				Main.get().activeFolderTree.rename();
				Main.get().activeFolderTree.hideMenuPopup();
			}
		}
	};
	
	// Command menu to refresh actual Directory
	Command moveFolder = new Command() {
		public void execute() {
			if (moveOption) {
				Main.get().activeFolderTree.move();
				Main.get().activeFolderTree.hideMenuPopup();
			}
		}
	};
	
	// Command menu to refresh actual Directory
	Command copyFolder = new Command() {
		public void execute() {
			if (copyOption) {
				Main.get().activeFolderTree.copy();
				Main.get().activeFolderTree.hideMenuPopup();
			}
		}
	};
	
	/**
	 * Set enabled all menu options
	 */
	public void enableAllMenuOptions(){
		rootNode			= false;
		createOption 		= true;
		removeOption 		= true;
		renameOption 		= true;
		moveOption			= true;
		copyOption			= true;
	}
	
	/**
	 * Set enabled root menu options
	 */
	public void enableRootMenuOptions(){
		rootNode			= true;
		createOption 		= true;
		removeOption 		= false;
		renameOption 		= false;
		moveOption			= false;
		copyOption			= false;
	}
	
	/**
	 * Checks permissions associated to folder and menu options enabled actions
	 * 
	 * @param folder The folder
	 */
	public void checkMenuOptionPermissions(GWTFolder folder, GWTFolder folderParent) {
		if ( (folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE)  {
			
			createOption 		= true;
			copyOption 			= true;
			
			// Evaluates root node case
			if (rootNode) {
				removeOption 		= false;
				renameOption 		= false;
				moveOption			= false;
				copyOption			= false;
				copyOption 			= false;
			} else if ((folderParent.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE){
				removeOption 		= true;
				renameOption 		= true;
				moveOption			= true;
			} else {
				removeOption 		= false;
				renameOption 		= false;
				moveOption			= false;
			}
		} else {
			createOption 		= false;
			removeOption 		= false;
			renameOption 		= false;
			moveOption			= false;
			if(rootNode) {
				copyOption 		= false;
			} else {
				copyOption 		= true;
			}
		}
	}
	
	/**
	 * Evaluates menu options
	 */
	public void evaluateMenuOptions(){
		if (createOption) {enable(create);} else {disable(create);}
		if (removeOption) {enable(remove);} else {disable(remove);}
		if (renameOption) {enable(rename);} else {disable(rename);}
		if (moveOption) {enable(move);} else {disable(move);}
		if (copyOption) {enable(copy);} else {disable(copy);}
	}
	
	/**
	 *  Refresh language values
	 */
	public void langRefresh() {
		create.setHTML(Util.menuHTML("img/icon/actions/add_folder.gif", Main.i18n("tree.menu.directory.create")));
		remove.setHTML(Util.menuHTML("img/icon/actions/delete.gif", Main.i18n("tree.menu.directory.remove")));
		rename.setHTML(Util.menuHTML("img/icon/actions/rename.gif", Main.i18n("tree.menu.directory.rename")));
		move.setHTML(Util.menuHTML("img/icon/actions/move_folder.gif", Main.i18n("tree.menu.directory.move")));
		copy.setHTML(Util.menuHTML("img/icon/actions/copy.gif", Main.i18n("tree.menu.directory.copy")));
	}
	
	@Override
	public void setAvailableOption(GWTAvailableOption option) {
		create.setVisible(option.isCreateFolderOption());
		remove.setVisible(option.isDeleteOption());
		rename.setVisible(option.isRenameOption());
		move.setVisible(option.isMoveOption());
		copy.setVisible(option.isCopyOption());
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