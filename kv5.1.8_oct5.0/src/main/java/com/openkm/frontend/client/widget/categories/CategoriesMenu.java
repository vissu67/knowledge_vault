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

package com.openkm.frontend.client.widget.categories;

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
 * CategoriesMenu menu
 * 
 * @author jllort
 *
 */
public class CategoriesMenu extends MenuBase {
	
	private boolean createOption 		= true;
	private boolean renameOption 		= false;
	private boolean moveOption 			= false;
	
	private boolean rootNode 			= true;  // Indicates root node selected ( option menu are specific on this case ).
	
	private MenuBar dirMenu;
	
	private MenuItem create;
	private MenuItem rename;
	private MenuItem move;
	
	public CategoriesMenu() {
		// The item selected must be called on style.css : .okm-MenuBar .gwt-MenuItem-selected
		
		// First initialize language values
		dirMenu = new MenuBar(true);
		create = new MenuItem(Util.menuHTML("img/icon/actions/add_folder.gif", Main.i18n("tree.menu.directory.create")), true, addFolder);
		create.addStyleName("okm-MenuItem");
		dirMenu.addItem(create);
		rename = new MenuItem(Util.menuHTML("img/icon/actions/rename.gif", Main.i18n("tree.menu.directory.rename")), true, renFolder);
		rename.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(rename);
		move = new MenuItem(Util.menuHTML("img/icon/actions/move_folder.gif", Main.i18n("tree.menu.directory.move")), true, moveFolder);
		move.addStyleName("okm-MenuItem");
		dirMenu.addItem(move);
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
	
	// Command menu to rename a new Directory
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
	
	@Override
	public void langRefresh() {
		create.setHTML(Util.menuHTML("img/icon/actions/add_folder.gif", Main.i18n("tree.menu.directory.create")));
		rename.setHTML(Util.menuHTML("img/icon/actions/delete.gif", Main.i18n("tree.menu.directory.remove")));
		move.setHTML(Util.menuHTML("img/icon/actions/move_folder.gif", Main.i18n("tree.menu.directory.move")));
	}
	
	@Override
	public void evaluateMenuOptions() {
		if (createOption) {enable(create);} else {disable(create);}
		if (renameOption) {enable(rename);} else {disable(rename);}
		if (moveOption) {enable(move);} else {disable(move);}
	}
	
	@Override
	public void enableRootMenuOptions() {
		rootNode	 = true;
		createOption = true;
		renameOption = false;
		moveOption	 = false;
	}
	
	@Override
	public void enableAllMenuOptions() {
		rootNode		= false;
		createOption 	= true;
		renameOption 	= true;
		moveOption		= true;
	}
	
	@Override
	public void disableAllMenuOption() {
		createOption 	= false;
		renameOption 	= false;
		moveOption		= false;
	}
	
	@Override
	public void checkMenuOptionPermissions(GWTFolder folder, GWTFolder folderParent) {
		if ( (folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE)  {	
			createOption = true;
			
			// Evaluates root node case
			if (rootNode) {
				renameOption = false;
				moveOption	 = false;
			} else if ((folderParent.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE){
				renameOption = true;
				moveOption	 = true;
			} else {
				renameOption = false;
				moveOption	 = false;
			}
		} else {
			createOption = false;
			renameOption = false;
			moveOption	 = false;
		}
	}
	
	@Override
	public void setAvailableOption(GWTAvailableOption option) {
		create.setVisible(option.isCreateFolderOption());
		rename.setVisible(option.isRenameOption());
		move.setVisible(option.isMoveOption());
	}
	
	@Override
	public void checkMenuOptionPermissions(GWTMail mail, GWTFolder folder) {}
	
	@Override
	public void checkMenuOptionPermissions(GWTDocument doc, GWTFolder folder) {}
}