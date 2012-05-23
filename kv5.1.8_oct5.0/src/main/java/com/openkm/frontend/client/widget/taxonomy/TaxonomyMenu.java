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

package com.openkm.frontend.client.widget.taxonomy;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.FileToUpload;
import com.openkm.frontend.client.bean.GWTAvailableOption;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.bean.GWTPermission;
import com.openkm.frontend.client.contants.ui.UIFileUploadConstants;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.MenuBase;

/**
 * Taxonomy menu
 * 
 * @author jllort
 *
 */
public class TaxonomyMenu extends MenuBase {
	
	private boolean createOption 		= true;
	private boolean removeOption 		= false;
	private boolean renameOption 		= false;
	private boolean moveOption 			= false;
	private boolean copyOption 			= false;
	private boolean addDocumentOption 	= true;
	private boolean addBookmarkOption	= false;
	private boolean setHomeOption		= false;
	private boolean exportOption		= false;
	
	private boolean rootNode 			= true;  // Indicates root node selected ( option menu are specific on this case ).
	
	private MenuBar dirMenu;
	private MenuItem create;
	private MenuItem remove;
	private MenuItem rename;
	private MenuItem move;
	private MenuItem copy;
	private MenuItem bookmark;
	private MenuItem home;
	private MenuItem addDocument;
	private MenuItem export;
	
	public TaxonomyMenu() {
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
		addDocument = new MenuItem(Util.menuHTML("img/icon/actions/add_document.gif", Main.i18n("tree.menu.directory.add.document")), true, addDocumentFolder);
		addDocument.addStyleName("okm-MenuItem");
		dirMenu.addItem(addDocument);
		bookmark = new MenuItem(Util.menuHTML("img/icon/actions/add_bookmark.gif", Main.i18n("tree.menu.add.bookmark")), true, addBookmark);
		bookmark.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(bookmark);
		home = new MenuItem(Util.menuHTML("img/icon/actions/bookmark.gif", Main.i18n("tree.menu.set.home")), true, setHome);
		home.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(home);
		export = new MenuItem(Util.menuHTML("img/icon/actions/export.gif", Main.i18n("tree.menu.export")), true, exportToFile);
		export.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(export);
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
	
	// Command menu to add Document to actual Directory
	Command addDocumentFolder = new Command() {
		public void execute() {
			if (addDocumentOption) {
				FileToUpload fileToUpload = new FileToUpload();
				fileToUpload.setFileUpload(new FileUpload());
				fileToUpload.setPath((String) Main.get().activeFolderTree.getActualPath());
				fileToUpload.setAction(UIFileUploadConstants.ACTION_INSERT);
				Main.get().fileUpload.enqueueFileToUpload(new ArrayList<FileToUpload>(Arrays.asList(fileToUpload)));
				Main.get().activeFolderTree.hideMenuPopup();
			}
		}
	};
	
	// Command menu to add bookmark
	Command addBookmark = new Command() {
		public void execute() {
			if (addBookmarkOption) {
				Main.get().activeFolderTree.addBookmark();
				Main.get().activeFolderTree.hideMenuPopup();
			}
		}
	};
	
	// Command menu to set default home
	Command setHome = new Command() {
		public void execute() {
			if (setHomeOption) {				
				Main.get().activeFolderTree.setHome();
				Main.get().activeFolderTree.hideMenuPopup();
			}
		}
	};
	
	// Command menu to set default home
	Command exportToFile = new Command() {
		public void execute() {
			if (exportOption) {				
				Main.get().activeFolderTree.exportFolderToFile();
				Main.get().activeFolderTree.hideMenuPopup();
			}
		}
	};
	
	/**
	 * Set enabled all menu options
	 */
	public void enableAllMenuOptions() {
		rootNode			= false;
		createOption 		= true;
		removeOption 		= true;
		renameOption 		= true;
		moveOption			= true;
		addBookmarkOption	= true;
		setHomeOption		= true;
		exportOption		= true;
	}
	
	/**
	 * Set enabled root menu options
	 */
	public void enableRootMenuOptions() {
		rootNode			= true;
		createOption 		= true;
		removeOption 		= false;
		renameOption 		= false;
		moveOption			= false; 
		addBookmarkOption	= true;
		setHomeOption		= true;
		exportOption		= false;
	}
	
	/**
	 * Checks permissions associated to folder and menu options enabled actions
	 * 
	 * @param folder The folder
	 */
	public void checkMenuOptionPermissions(GWTFolder folder, GWTFolder folderParent) {
		if ( (folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE)  {
			
			createOption 		= true;
			addDocumentOption 	= true;
			addBookmarkOption	= true;
			setHomeOption		= true;
			copyOption 			= true;
			exportOption		= true;
			
			// Evaluates root node case
			if (rootNode) {
				removeOption 		= false;
				renameOption 		= false;
				moveOption			= false;
				copyOption 			= false;
				exportOption		= false;
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
			addDocumentOption 	= false;
			moveOption			= false;
			if(rootNode) {
				copyOption 		= false;
				exportOption 	= false;
			} else {
				copyOption 		= true;
				exportOption 	= true;
			}
		}
	}
	
	/**
	 * Evaluates menu options
	 */
	public void evaluateMenuOptions() {
		if (createOption) {enable(create);} else {disable(create);}
		if (removeOption) {enable(remove);} else {disable(remove);}
		if (renameOption) {enable(rename);} else {disable(rename);}
		if (moveOption) {enable(move);} else {disable(move);}
		if (copyOption) {enable(copy);} else {disable(copy);}
		if (addDocumentOption) {enable(addDocument);} else {disable(addDocument);}
		if (addBookmarkOption){enable(bookmark);} else {disable(bookmark);}
		if (setHomeOption){enable(home);} else {disable(home);}
		if (exportOption){enable(export);} else {disable(export);}
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
		addDocument.setHTML(Util.menuHTML("img/icon/actions/add_document.gif", Main.i18n("tree.menu.directory.add.document")));
		bookmark.setHTML(Util.menuHTML("img/icon/actions/add_bookmark.gif", Main.i18n("tree.menu.add.bookmark")));
		home.setHTML(Util.menuHTML("img/icon/actions/bookmark.gif", Main.i18n("tree.menu.set.home")));
		export.setHTML(Util.menuHTML("img/icon/actions/export.gif", Main.i18n("tree.menu.export")));
	}
	
	@Override
	public void setAvailableOption(GWTAvailableOption option) {
		create.setVisible(option.isCreateFolderOption());
		remove.setVisible(option.isDeleteOption());
		rename.setVisible(option.isRenameOption());
		move.setVisible(option.isMoveOption());
		copy.setVisible(option.isCopyOption());
		addDocument.setVisible(option.isAddDocumentOption());
		bookmark.setVisible(option.isAddBookmarkOption());
		home.setVisible(option.isSetHomeOption());
		export.setVisible(option.isExportOption());
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