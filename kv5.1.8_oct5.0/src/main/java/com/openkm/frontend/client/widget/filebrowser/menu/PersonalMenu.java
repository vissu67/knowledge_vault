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
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.MenuBase;

/**
 * Browser my documents menu
 * 
 * @author jllort
 *
 */
public class PersonalMenu extends MenuBase {
	
	private boolean checkoutOption 		= false;
	private boolean checkinOption 		= false;
	private boolean deleteOption 		= false;
	private boolean renameOption 		= false;
	
	private boolean zohoOption	 		= false;	//added by vissu on feb19 for zohoapi
	
	private boolean cancelCheckoutOption= false;
	private boolean downloadOption 		= false;
	private boolean lockOption 			= false;
	private boolean unlockOption 		= false;
	private boolean moveOption 			= false;
	private boolean copyOption			= false;
	private boolean exportOption		= false;
	
	private MenuBar dirMenu;
	private MenuItem checkout;
	private MenuItem checkin;
	private MenuItem cancelCheckout;
	
	private MenuItem zoho;		//added by vissu on feb19 for zohoapi
	
	private MenuItem delete;
	private MenuItem rename;
	private MenuItem download;
	private MenuItem lock;
	private MenuItem unlock;
	private MenuItem move;
	private MenuItem copy;
	private MenuItem export;
	
	/**
	 * Browser menu
	 */
	public PersonalMenu() {
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
		move = new MenuItem(Util.menuHTML("img/icon/actions/move_document.gif", Main.i18n("filebrowser.menu.move")), true, moveFile);
		move.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(move);
		copy = new MenuItem(Util.menuHTML("img/icon/actions/copy.gif", Main.i18n("filebrowser.menu.copy")), true, copyFile);
		copy.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(copy);
		export = new MenuItem(Util.menuHTML("img/icon/actions/export.gif", Main.i18n("filebrowser.menu.export")), true, exportToFile);
		export.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(export);
		
		//added by vissu on feb19 for zohoapi
		zoho = new MenuItem(Util.menuHTML("img/icon/actions/zoho.gif", Main.i18n("filebrowser.menu.zoho")), true, zohoFile);
		checkin.addStyleName("okm-MenuItem-strike");
		dirMenu.addItem(zoho);
		
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
	
	//added by vissu on feb19 for zohoapi
		// Command menu to checkout file
			Command zohoFile = new Command() {
				public void execute() {
					if (zohoOption) {
						Main.get().mainPanel.desktop.browser.fileBrowser.zoho();
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
	Command moveFile = new Command() {
		public void execute() {
			if (moveOption) {
				Main.get().mainPanel.desktop.browser.fileBrowser.move();
				hide();
			}
		}
	};
	
	// Command menu to rename file
	Command copyFile = new Command() {
		public void execute() {
			if (copyOption) {
				Main.get().mainPanel.desktop.browser.fileBrowser.copy();
				hide();
			}
		}
	};
	
	// Command menu to set default home
	Command exportToFile = new Command() {
		public void execute() {
			if (exportOption) {				
				Main.get().mainPanel.desktop.browser.fileBrowser.exportFolderToFile();
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
		move.setHTML(Util.menuHTML("img/icon/actions/move_document.gif", Main.i18n("filebrowser.menu.move")));
		copy.setHTML(Util.menuHTML("img/icon/actions/copy.gif", Main.i18n("filebrowser.menu.copy")));
		export.setHTML(Util.menuHTML("img/icon/actions/export.gif", Main.i18n("filebrowser.menu.export")));
		
		//added by vissu on feb19 for zohoapi
		zoho.setHTML(Util.menuHTML("img/icon/actions/zoho.gif", Main.i18n("filebrowser.menu.zoho")));
	}
	
	/**
	 * Checks permissions associated to folder and menu options enabled actions
	 * 
	 * @param folder The folder
	 */
	public void checkMenuOptionPermissions(GWTFolder folder, GWTFolder folderParent) {
		downloadOption 			= false;
		checkoutOption 		 	= false;
		zohoOption	 		 	= false;		//added by vissu on feb19 for zohoapi
		checkinOption 		 	= false;
		cancelCheckoutOption 	= false;
		lockOption 			 	= false;
		unlockOption 		 	= false;
		copyOption 				= true;
		exportOption			= true;
		
		if ( (folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE && 
			 (folderParent.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE )  {
			deleteOption 		= true;
			renameOption 		= true;
			moveOption 			= true;
		} else {
			deleteOption 		= false;
			renameOption 		= false;
			moveOption 			= false;
		}
	}
	
	/**
	 * Checks permissions associated to document and menu options enabled actions
	 * 
	 * @param doc The document
	 */
	public void checkMenuOptionPermissions(GWTDocument doc, GWTFolder folder) {	
		String user = Main.get().workspaceUserProperties.getUser();
		
		downloadOption		     = true;
		checkinOption 	     	= false;
		cancelCheckoutOption 	= false;
		unlockOption 		 	= false;	
		copyOption 				= true;
		exportOption			= false;
		
		if ( (doc.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE)  {
			lockOption				= true;
			checkoutOption 			= true;
			zohoOption				= true;		//added by vissu on feb19 for zohoapi
			
			if ( (folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE)  {
				renameOption 			= true;
				deleteOption 			= true;
				moveOption 				= true;
			} else {
				renameOption 			= false;
				deleteOption 			= false;
				moveOption 				= false;
			}
		} else {
			lockOption				= false;
			deleteOption 			= false;
			renameOption 			= false;
			checkoutOption 			= false;
			moveOption 				= false;
			
			zohoOption				= false;	//added by vissu on feb19 for zohoapi
		}
		
		if (doc.isCheckedOut()){
			lockOption 			= false;
			unlockOption		= false;
			checkoutOption		= false;
			if (doc.getLockInfo().getOwner().equals(user)) {
				checkinOption		 	= true;
				cancelCheckoutOption 	= true;
			} else {
				checkinOption		 	= false;
				cancelCheckoutOption 	= false;
			}
			deleteOption		= false;
			renameOption		= false;
			moveOption			= false;
			
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
			moveOption			 	= false;
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
		downloadOption		    = false;
		checkinOption 	     	= false;
		cancelCheckoutOption 	= false;
		unlockOption 		 	= false;	
		copyOption 				= true;
		exportOption			= false;
		lockOption				= false;
		checkoutOption 			= false;
		renameOption 			= false;
		
		if ( (mail.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE)  {
			if ( (folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE)  {
				deleteOption 			= true;
				moveOption 				= true;
			} else {
				deleteOption 			= false;
				moveOption 				= false;
			}
		} else {
			deleteOption 			= false;
			moveOption 				= false;
		}		
	}
	
	/**
	 * Disables all menu options
	 */
	public void disableAllMenuOption() {
		downloadOption 			= false;
		deleteOption 		 	= false; 
		renameOption 		 	= false; 
		checkoutOption 		 	= false;
		zohoOption				= false;	//added by vissu on feb19 for zohoapi
		checkinOption 			= false;
		cancelCheckoutOption 	= false;
		lockOption 			 	= false;
		unlockOption 		 	= false;
		moveOption 			 	= false;
		exportOption			= false;
	}
	
	/**
	 * Evaluates menu options
	 */
	public void evaluateMenuOptions() {
		if (downloadOption){enable(download);} else {disable(download);}
		if (deleteOption){enable(delete);} else {disable(delete);}
		if (renameOption){enable(rename);} else {disable(rename);}
		if (checkoutOption){enable(checkout);} else {disable(checkout);}
		if (zohoOption){enable(zoho);} else {disable(zoho);}	//added by vissu on feb19 for zohoapi
		if (checkinOption){enable(checkin);} else {disable(checkin);}
		if (cancelCheckoutOption){enable(cancelCheckout);} else {disable(cancelCheckout);}
		if (lockOption){enable(lock);} else {disable(lock);}
		if (unlockOption){enable(unlock);} else {disable(unlock);}
		if (moveOption){enable(move);} else {disable(move);}
		if (copyOption){enable(copy);} else {disable(copy);}
		if (exportOption){enable(export);} else {disable(export);}
	}
	
	@Override
	public void setAvailableOption(GWTAvailableOption option) {
		download.setVisible(option.isDownloadOption());
		delete.setVisible(option.isDeleteOption());
		rename.setVisible(option.isRenameOption());
		checkout.setVisible(option.isCheckoutOption());
		zoho.setVisible(option.isZohoOption());		//added by vissu on feb19 for zohoapi
		checkin.setVisible(option.isCheckinOption());
		cancelCheckout.setVisible(option.isCancelCheckoutOption());
		lock.setVisible(option.isLockOption());
		unlock.setVisible(option.isUnLockOption());
		move.setVisible(option.isMoveOption());
		copy.setVisible(option.isCopyOption());
		export.setVisible(option.isExportOption());
	}
	
	/**
	 * Hide popup menu
	 */
	public void hide() {
		Main.get().mainPanel.desktop.browser.fileBrowser.personalMenuPopup.hide();
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
