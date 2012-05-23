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
import com.openkm.frontend.client.util.CommonUI;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.MenuBase;

/**
 * ThesaurusMenu menu
 * 
 * @author jllort
 *
 */
public class ThesaurusMenu extends MenuBase {
	
	private boolean checkoutOption 			= false;
	private boolean checkinOption 			= false;
	private boolean deleteOption 			= false;
	private boolean renameOption 			= false;
	private boolean cancelCheckoutOption 	= false;
	private boolean downloadOption 			= false;
	private boolean lockOption 				= false;
	private boolean unlockOption 			= false;
	private boolean addPropertyGroupOption  = false;
	private boolean goOption				= false;
	
	private MenuBar dirMenu;
	private MenuItem checkout;
	private MenuItem checkin;
	private MenuItem cancelCheckout;
	private MenuItem delete;
	private MenuItem rename;
	private MenuItem download;
	private MenuItem lock;
	private MenuItem unlock;
	private MenuItem go;
	
	/**
	 * Thesaurus menu
	 */
	public ThesaurusMenu() {
		// The item selected must be called on style.css : .okm-MenuBar .gwt-MenuItem-selected
		
		// First initialize language values
		dirMenu = new MenuBar(true);
		download = new MenuItem(Util.menuHTML("img/icon/actions/download.gif", Main.i18n("filebrowser.menu.download")), true, downloadFile);
		download.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(download);
		checkout = new MenuItem(Util.menuHTML("img/icon/actions/checkout.gif", Main.i18n("filebrowser.menu.checkout")), true, checkoutFile);
		checkout.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(checkout);
		checkin = new MenuItem(Util.menuHTML("img/icon/actions/checkin.gif", Main.i18n("filebrowser.menu.checkin")), true, checkinFile);
		checkin.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(checkin);
		cancelCheckout = new MenuItem(Util.menuHTML("img/icon/actions/cancel_checkout.gif", Main.i18n("filebrowser.menu.checkout.cancel")), true, cancelCheckinFile);
		cancelCheckout.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(cancelCheckout);
		lock = new MenuItem(Util.menuHTML("img/icon/actions/lock.gif", Main.i18n("filebrowser.menu.lock")), true, lockFile);
		lock.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(lock);
		unlock = new MenuItem(Util.menuHTML("img/icon/actions/unlock.gif", Main.i18n("filebrowser.menu.unlock")), true, unlockFile);
		unlock.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(unlock);
		delete = new MenuItem(Util.menuHTML("img/icon/actions/delete.gif", Main.i18n("filebrowser.menu.delete")), true, deleteFile);
		delete.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(delete);
		rename = new MenuItem(Util.menuHTML("img/icon/actions/rename.gif", Main.i18n("filebrowser.menu.rename")), true, renameFile);
		rename.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(rename);
		go = new MenuItem(Util.menuHTML("img/icon/actions/goto_folder.gif", Main.i18n("search.result.menu.go.folder")), true, goDirectory);
		go.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(go);
		
		dirMenu.setStyleName("okm-MenuBar");
		initWidget(dirMenu);
	}
	
	// Command menu to download file
	Command downloadFile = new Command() {
		public void execute() {		
			if (downloadOption) {
				Main.get().mainPanel.desktop.browser.fileBrowser.table.downloadDocument(false);
				hide();
			}
		}
	};
	
	// Command menu to checkout file
	Command checkoutFile = new Command() {
		public void execute() {
			if (checkoutOption) {
				Main.get().mainPanel.desktop.browser.fileBrowser.checkout();
				hide();
			}
		}
	};
	
	// Command menu to checkin file
	Command checkinFile = new Command() {
		public void execute() {
			if (checkinOption) {
				FileToUpload fileToUpload = new FileToUpload();
				fileToUpload.setFileUpload(new FileUpload());
				fileToUpload.setPath(Main.get().mainPanel.desktop.browser.fileBrowser.getPath());
				fileToUpload.setAction(UIFileUploadConstants.ACTION_UPDATE);
				fileToUpload.setEnableAddButton(false);
				fileToUpload.setEnableImport(false);
				Main.get().fileUpload.enqueueFileToUpload(new ArrayList<FileToUpload>(Arrays.asList(fileToUpload)));
				hide();
			}
		}
	};
	
	// Command menu to cancel checkin file
	Command cancelCheckinFile = new Command() {
		public void execute() {
			if (cancelCheckoutOption) {
				Main.get().mainPanel.desktop.browser.fileBrowser.cancelCheckout();
				hide();
			}
		}
	};
	
	// Command menu to lock file
	Command lockFile = new Command() {
		public void execute() {
			if (lockOption) {
				Main.get().mainPanel.desktop.browser.fileBrowser.lock();
				hide();
			}
		}
	};
	
	// Command menu to unlock file
	Command unlockFile = new Command() {
		public void execute() {
			if (unlockOption) {
				Main.get().mainPanel.desktop.browser.fileBrowser.unlock();
				hide();
			}
		}
	};
	
	// Command menu to lock file
	Command deleteFile = new Command() {
		public void execute() {
			if (deleteOption) {
				Main.get().mainPanel.desktop.browser.fileBrowser.confirmDelete();
				hide();
			}
		}
	};
	
	// Command menu to rename file
	Command renameFile = new Command() {
		public void execute() {
			if (renameOption) {
				Main.get().mainPanel.desktop.browser.fileBrowser.rename();
				hide();
			}
		}
	};
	
	// Command menu to rename file
	Command addPropertyGroup = new Command() {
		public void execute() {
			if (addPropertyGroupOption) {
				Main.get().propertyGroupPopup.show();
				hide();
			}
		}
	};
	
	// Command menu to go directory file
	Command goDirectory = new Command() {
		public void execute() {
			if (goOption) {
				String docPath = "";
				String path = "";
				if (Main.get().mainPanel.desktop.browser.fileBrowser.isDocumentSelected()) {
					docPath = Main.get().mainPanel.desktop.browser.fileBrowser.getDocument().getPath();
					path = docPath.substring(0,docPath.lastIndexOf("/"));
				}
				CommonUI.openAllFolderPath(path, docPath);
				hide();
			}
		}
	};
	
	/**
	 *  Refresh language values
	 */
	public void langRefresh() {
		checkout.setHTML(Util.menuHTML("img/icon/actions/checkout.gif", Main.i18n("filebrowser.menu.checkout")));
		checkin.setHTML(Util.menuHTML("img/icon/actions/checkin.gif", Main.i18n("filebrowser.menu.checkin")));
		delete.setHTML(Util.menuHTML("img/icon/actions/delete.gif", Main.i18n("filebrowser.menu.delete")));
		rename.setHTML(Util.menuHTML("img/icon/actions/rename.gif", Main.i18n("filebrowser.menu.rename")));
		cancelCheckout.setHTML(Util.menuHTML("img/icon/actions/cancel_checkout.gif", Main.i18n("filebrowser.menu.checkout.cancel")));
		lock.setHTML(Util.menuHTML("img/icon/actions/lock.gif", Main.i18n("filebrowser.menu.lock")));
		unlock.setHTML(Util.menuHTML("img/icon/actions/unlock.gif", Main.i18n("filebrowser.menu.unlock")));
		download.setHTML(Util.menuHTML("img/icon/actions/download.gif", Main.i18n("filebrowser.menu.download")));
		go.setHTML(Util.menuHTML("img/icon/actions/goto_folder.gif", Main.i18n("search.result.menu.go.folder")));
	}
	
	/**
	 * Checks permissions associated to folder and menu options enabled actions
	 * 
	 * @param folder The folder
	 */
	public void checkMenuOptionPermissions(GWTFolder folder, GWTFolder folderParent) {
	}
	
	/**
	 * Checks permissions associated to document and menu options enabled actions
	 * 
	 * @param doc The document
	 */
	public void checkMenuOptionPermissions(GWTDocument doc, GWTFolder folder) {	
		String user = Main.get().workspaceUserProperties.getUser();
		
		downloadOption		    = true;
		checkinOption 	     	= false;
		cancelCheckoutOption 	= false;
		unlockOption 		 	= false;	
		goOption				= true;
		
		if ( (doc.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE)  {
			lockOption				= true;
			checkoutOption 			= true;
			
			if ( (folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE)  {
				renameOption 			= true;
				deleteOption 			= true;
				addPropertyGroupOption 	= true;
			} else {
				renameOption 			= false;
				deleteOption 			= false;
				addPropertyGroupOption 	= false;
			}
		} else {
			lockOption				= false;
			deleteOption 			= false;
			renameOption 			= false;
			checkoutOption 			= false;
			addPropertyGroupOption 	= false;
		}
		
		if (doc.isCheckedOut()){
			lockOption 			= false;
			unlockOption		= false;
			checkoutOption		= false;
			if (doc.getLockInfo().getOwner().equals(user)) {
				checkinOption		 	= true;
				cancelCheckoutOption 	= true;
				addPropertyGroupOption 	= true;
			} else {
				checkinOption		 	= false;
				cancelCheckoutOption 	= false;
				addPropertyGroupOption 	= false;
			}
			deleteOption		= false;
			renameOption		= false;
			
		} else if (doc.isLocked()){
			lockOption			= false;
			if (doc.getLockInfo().getOwner().equals(user)) {
				unlockOption	= true;
			} else {
				unlockOption	= false;
			}
			checkoutOption	 	 	= false;
			checkinOption		 	= false;
			cancelCheckoutOption 	= false;
			deleteOption		 	= false;
			renameOption		 	= false;
			addPropertyGroupOption 	= false;
		} else {
			unlockOption			= false;
			checkinOption			= false;
			cancelCheckoutOption	= false;
		}
		
	}
	
	/**
	 * Checks permissions associated to mail and menu options enabled actions
	 * 
	 * @param mail The mail
	 */
	public void checkMenuOptionPermissions(GWTMail mail, GWTFolder folder) {	
	}
	
	/**
	 * Disables all menu options
	 */
	public void disableAllMenuOption() {
		downloadOption 			= false;
		deleteOption 		 	= false; 
		renameOption 		 	= false; 
		checkoutOption 		 	= false;
		checkinOption 			= false;
		cancelCheckoutOption 	= false;
		lockOption 			 	= false;
		unlockOption 		 	= false;
		addPropertyGroupOption 	= false;
		goOption				= false;
	}
	
	/**
	 * Evaluates menu options
	 */
	public void evaluateMenuOptions(){
		if (downloadOption){enable(download);} else {disable(download);}
		if (deleteOption){enable(delete);} else {disable(delete);}
		if (renameOption){enable(rename);} else {disable(rename);}
		if (checkoutOption){enable(checkout);} else {disable(checkout);}
		if (checkinOption){enable(checkin);} else {disable(checkin);}
		if (cancelCheckoutOption){enable(cancelCheckout);} else {disable(cancelCheckout);}
		if (lockOption){enable(lock);} else {disable(lock);}
		if (unlockOption){enable(unlock);} else {disable(unlock);}
		if (goOption){enable(go);} else {disable(go);}
	}
	
	@Override
	public void setAvailableOption(GWTAvailableOption option) {
		download.setVisible(option.isDownloadOption());
		delete.setVisible(option.isDeleteOption());
		rename.setVisible(option.isRenameOption());
		checkout.setVisible(option.isCheckoutOption());
		checkin.setVisible(option.isCheckinOption());
		cancelCheckout.setVisible(option.isCancelCheckoutOption());
		lock.setVisible(option.isLockOption());
		unlock.setVisible(option.isUnLockOption());
		go.setVisible(option.isGotoFolderOption());
	}
	
	/**
	 * Hide popup menu
	 */
	public void hide() {
		Main.get().mainPanel.desktop.browser.fileBrowser.thesaurusMenuPopup.hide();
	}
	
	/* (non-Javadoc)
	 * @see com.openkm.frontend.client.widget.MenuBase#enableAllMenuOptions()
	 */
	public void enableAllMenuOptions(){
	}
	
	/* (non-Javadoc)
	 * @see com.openkm.frontend.client.widget.MenuBase#enableRootMenuOptions()
	 */
	public void enableRootMenuOptions(){
	}
}