/**
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

package com.openkm.frontend.client.widget.toolbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.FileToUpload;
import com.openkm.frontend.client.bean.GWTAvailableOption;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.bean.GWTPermission;
import com.openkm.frontend.client.bean.GWTPropertyGroup;
import com.openkm.frontend.client.bean.ToolBarOption;
import com.openkm.frontend.client.contants.service.ErrorCode;
import com.openkm.frontend.client.contants.service.RPCService;
import com.openkm.frontend.client.contants.ui.UIDesktopConstants;
import com.openkm.frontend.client.contants.ui.UIDockPanelConstants;
import com.openkm.frontend.client.contants.ui.UIFileUploadConstants;
import com.openkm.frontend.client.extension.event.HasToolBarEvent;
import com.openkm.frontend.client.extension.event.handler.ToolBarHandlerExtension;
import com.openkm.frontend.client.extension.event.hashandler.HasToolBarHandlerExtension;
import com.openkm.frontend.client.extension.widget.toolbar.ToolBarButtonExtension;
import com.openkm.frontend.client.service.OKMDocumentService;
import com.openkm.frontend.client.service.OKMDocumentServiceAsync;
import com.openkm.frontend.client.service.OKMFolderService;
import com.openkm.frontend.client.service.OKMFolderServiceAsync;
import com.openkm.frontend.client.service.OKMPropertyGroupService;
import com.openkm.frontend.client.service.OKMPropertyGroupServiceAsync;
import com.openkm.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.ConfirmPopup;
import com.openkm.frontend.client.widget.OriginPanel;
import com.openkm.frontend.client.widget.mainmenu.Bookmark;

/**
 * ToolBar
 * 
 * @author jllort
 *
 */
public class ToolBar extends Composite implements OriginPanel, HasToolBarEvent, HasToolBarHandlerExtension {
	
	private final OKMDocumentServiceAsync documentService = (OKMDocumentServiceAsync) GWT.create(OKMDocumentService.class);
	private final OKMFolderServiceAsync folderService = (OKMFolderServiceAsync) GWT.create(OKMFolderService.class);
	private final OKMPropertyGroupServiceAsync propertyGroupService = (OKMPropertyGroupServiceAsync) GWT.create(OKMPropertyGroupService.class);
		
	private HorizontalPanel panel;
	private ToolBarButton createFolder;
	private ToolBarButton findFolder;
	private ToolBarButton lock;
	private ToolBarButton unlock;
	private ToolBarButton addDocument;
	private ToolBarButton delete;
	private ToolBarButton checkout;
	private ToolBarButton checkin;
	private ToolBarButton zoho;		//added by vissu on feb23 for zohoapi
	private GWTDocument doc;		//added by vissu on feb27
	
	private ToolBarButton cancelCheckout;
	private ToolBarButton download;
	private ToolBarButton downloadPdf;
	private ToolBarButton addPropertyGroup;
	private ToolBarButton removePropertyGroup;
	private ToolBarButton startWorkflow;
	private ToolBarButton addSubscription;
	private ToolBarButton removeSubscription;
	private ToolBarButton home;
	private ToolBarButton refresh;
	private ToolBarButton scanner;
	private ToolBarButton uploader;
	private Object node;
		
	private boolean enabled = true;  // Indicates if toolbar is enabled or disabled
	private boolean propertyGroupEnabled = false; // Indicates if property group is enabled, used only on changing language
	private ToolBarOption toolBarOption;
	private int actualView;
	private HashMap<String, ToolBarOption> viewValues;
	private List<ToolBarButtonExtension> widgetExtensionList;
	private List<ToolBarHandlerExtension> toolBarHandlerExtensionList;
	
	private MouseOverHandler mouseOverHandler = new MouseOverHandler(){
		@Override
		public void onMouseOver(MouseOverEvent event) {
			Widget sender = (Widget) event.getSource();
			sender.addStyleName("okm-ToolBar-selected");
		}
	};
	
	private MouseOutHandler mouseOutHandler = new MouseOutHandler(){
		@Override
		public void onMouseOut(MouseOutEvent event) {
			Widget sender = (Widget) event.getSource();
			sender.removeStyleName("okm-ToolBar-selected");
		}
	};
	
	/**
	 * Folder listener
	 */
	ClickHandler createFolderHandler = new ClickHandler() { 
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.createFolderOption) {
				executeFolderDirectory();
				fireEvent(HasToolBarEvent.EXECUTE_CREATE_FOLDER);
			}
		}
	};
	
	/**
	 * Execute create folder
	 */
	public void executeFolderDirectory() {
		Main.get().activeFolderTree.addTmpFolderCreate();
	}
	
	/**
	 * Find folder Handler
	 */
	ClickHandler findFolderHandler = new ClickHandler() { 
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.findFolderOption) {
				Main.get().findFolderSelectPopup.show();
				fireEvent(HasToolBarEvent.EXECUTE_FIND_FOLDER);
			}
		}
	};
	
	/**
	 * Lock Handler
	 */
	ClickHandler lockHandler = new ClickHandler() { 
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.lockOption) {
				executeLock();
			}
		}
	};
	
	/**
	 * Execute unlock
	 */
	public void executeLock() {
		Main.get().mainPanel.desktop.browser.fileBrowser.lock();
		fireEvent(HasToolBarEvent.EXECUTE_LOCK);
	}
	
	/**
	 * Unlock Handler
	 */
	ClickHandler unLockHandler = new ClickHandler() { 
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.unLockOption) {
				GWTDocument doc = Main.get().mainPanel.desktop.browser.fileBrowser.getDocument();
				if (doc.getLockInfo().getOwner().equals(Main.get().workspaceUserProperties.getUser())) {
					executeUnlock();
				} else if (Main.get().workspaceUserProperties.getWorkspace().isAdminRole()) {
					Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_FORCE_UNLOCK); 
					Main.get().confirmPopup.show();
				}
			}
		}
	};
	
	/**
	 * Execute lock
	 */
	public void executeUnlock() {
		Main.get().mainPanel.desktop.browser.fileBrowser.unlock();
		fireEvent(HasToolBarEvent.EXECUTE_UNLOCK);
	}
	
	/**
	 * Add document Handler
	 */
	ClickHandler addDocumentHandler = new ClickHandler() { 
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.addDocumentOption) {
				if (Main.get().mainPanel.bottomPanel.userInfo.isQuotaExceed()) {
					Main.get().showError("UserQuotaExceed", 
				             new OKMException("OKM-"+ErrorCode.ORIGIN_OKMBrowser + ErrorCode.CAUSE_QuotaExceed,""));
				} else {
					executeAddDocument();
				}
			}
		}
	};
	
	/**
	 * Execute adds documents
	 */
	public void executeAddDocument() {
		FileToUpload fileToUpload = new FileToUpload();
		fileToUpload.setFileUpload(new FileUpload());
		fileToUpload.setPath((String) Main.get().activeFolderTree.getActualPath());
		fileToUpload.setAction(UIFileUploadConstants.ACTION_INSERT);
		Main.get().fileUpload.enqueueFileToUpload(new ArrayList<FileToUpload>(Arrays.asList(fileToUpload)));
		fireEvent(HasToolBarEvent.EXECUTE_ADD_DOCUMENT);
	}
	
	/**
	 * Delete Handler
	 */
	ClickHandler deleteHandler = new ClickHandler() { 
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.deleteOption) {
				executeDelete();
			}
		}
	};
	
	/**
	 * Executes delete option
	 */
	public void executeDelete() {
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
			Main.get().mainPanel.desktop.browser.fileBrowser.confirmDelete();
		} else if (Main.get().activeFolderTree.isPanelSelected()) {
			Main.get().activeFolderTree.confirmDelete();
		}
		fireEvent(HasToolBarEvent.EXECUTE_DELETE);
	}
	
	/**
	 * Executes delete option
	 */
	public void executeCopy() {
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
			Main.get().mainPanel.desktop.browser.fileBrowser.copy();
		} else if (Main.get().activeFolderTree.isPanelSelected()) {
			Main.get().activeFolderTree.copy();
		}
		fireEvent(HasToolBarEvent.EXECUTE_COPY);
	}
	
	/**
	 * Executes move option
	 */
	public void executeMove() {
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
			Main.get().mainPanel.desktop.browser.fileBrowser.move();
		} else if (Main.get().activeFolderTree.isPanelSelected()) {
			Main.get().activeFolderTree.move();
		}
		fireEvent(HasToolBarEvent.EXECUTE_MOVE);
	}
	
	/**
	 * Executes rename option
	 */
	public void executeRename() {
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
			Main.get().mainPanel.desktop.browser.fileBrowser.rename();
		} else if (Main.get().activeFolderTree.isPanelSelected()) {
			Main.get().activeFolderTree.rename();
		}
		fireEvent(HasToolBarEvent.EXECUTE_RENAME);
	}
	
	
	//added by vissu on feb19 for zohoapi
	
	/**
	 * zoho Handler
	 */
	ClickHandler zohoHandler = new ClickHandler() { 
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.zohoOption) {
					executeZoho();				
		}
		}
	};
	/**
	 * Executes zoho option
	 */
	public void executeZoho() {
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {

		Main.get().mainPanel.desktop.browser.fileBrowser.zoho();
		} else if (Main.get().activeFolderTree.isPanelSelected()) {
			//Main.get().activeFolderTree.zoho();
		}
		
		fireEvent(HasToolBarEvent.EXECUTE_ZOHO);
	}
	
	
	/**
	 * Edit Handler
	 */
	ClickHandler editHandler = new ClickHandler() { 
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.checkoutOption) {
				if (Main.get().mainPanel.bottomPanel.userInfo.isQuotaExceed()) {
					Main.get().showError("UserQuotaExceed", 
				             new OKMException("OKM-"+ErrorCode.ORIGIN_OKMBrowser + ErrorCode.CAUSE_QuotaExceed,""));
				} else {
					executeCheckout();
				}
			}
		}
	};
	
	/**
	 * Execute check out
	 */
	public void executeCheckout() {
		Main.get().mainPanel.desktop.browser.fileBrowser.checkout();
		fireEvent(HasToolBarEvent.EXECUTE_CHECKOUT);
	}
	
	
	
	/**
	 * Checkin Handler
	 */
	ClickHandler checkinHandler = new ClickHandler() { 
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.checkinOption) {
				exectuteCheckin();
			}
		}
	};
	
	/**
	 * Execute checkin
	 */
	public void exectuteCheckin() {
		FileToUpload fileToUpload = new FileToUpload();
		fileToUpload.setFileUpload(new FileUpload());
		fileToUpload.setPath(Main.get().mainPanel.desktop.browser.fileBrowser.getPath());
		fileToUpload.setAction(UIFileUploadConstants.ACTION_UPDATE);
		fileToUpload.setEnableAddButton(false);
		fileToUpload.setEnableImport(false);
		Main.get().fileUpload.enqueueFileToUpload(new ArrayList<FileToUpload>(Arrays.asList(fileToUpload)));
		fireEvent(HasToolBarEvent.EXECUTE_CHECKIN);
	}
	
	/**
	 * Checkout cancel Handler
	 */
	ClickHandler cancelCheckoutHandler = new ClickHandler() { 
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.cancelCheckoutOption) {
				GWTDocument doc = Main.get().mainPanel.desktop.browser.fileBrowser.getDocument();
				if (doc.getLockInfo().getOwner().equals(Main.get().workspaceUserProperties.getUser())) {
					executeCancelCheckout();
				} else if (Main.get().workspaceUserProperties.getWorkspace().isAdminRole()) {
					Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_FORCE_CANCEL_CHECKOUT); 
					Main.get().confirmPopup.show();
				}
			}
		}
	};
	
	/**
	 * Cancel the check out
	 */
	public void executeCancelCheckout() {
		Main.get().mainPanel.desktop.browser.fileBrowser.cancelCheckout();
		fireEvent(HasToolBarEvent.EXECUTE_CANCEL_CHECKOUT);
	}
	
	/**
	 * Download Handler
	 */
	ClickHandler downloadHandler = new ClickHandler() { 
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.downloadOption) {
				executeDownload();
			}
		}
	};

	/**
	 * Download as PDF Handler
	 */
	ClickHandler downloadPdfHandler = new ClickHandler() { 
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.downloadPdfOption) {
				executeDownloadPdf();
			}
		}
	};
	
	/**
	 * Download document
	 */
	public void executeDownload() {
		Main.get().mainPanel.desktop.browser.fileBrowser.table.downloadDocument(false);
		fireEvent(HasToolBarEvent.EXECUTE_DOWNLOAD_DOCUMENT);
	}

	/**
	 * Download document as PDF
	 */
	public void executeDownloadPdf() {
		Main.get().mainPanel.desktop.browser.fileBrowser.table.downloadDocumentPdf();
		fireEvent(HasToolBarEvent.EXECUTE_DOWNLOAD_PDF_DOCUMENT);
	}

	/**
	 * Add property group Handler
	 */
	ClickHandler addPropertyGroupHandler = new ClickHandler() { 
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.addPropertyGroupOption) {
				executeAddPropertyGroup();
			}
		}
	};
	
	/**
	 * Execute add property group
	 */
	public void executeAddPropertyGroup(){
		Main.get().propertyGroupPopup.show();
		fireEvent(HasToolBarEvent.EXECUTE_ADD_PROPERTY_GROUP);
	}
	
	/**
	 * Add workflowgroup
	 */
	public void executeAddWorkflow(){
		Main.get().workflowPopup.show();
		fireEvent(HasToolBarEvent.EXECUTE_ADD_WORKFLOW);
	}
	
	/**
	 * Remove property group Handler
	 */
	ClickHandler removePropertyGroupHandler = new ClickHandler() { 
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.removePropertyGroupOption && toolBarOption.firedRemovePropertyGroupOption) {
				executeRemovePropertyGroup();
			}
		}
	};
	
	/**
	 * Add workflow Handler
	 */
	ClickHandler startWorkflowHandler = new ClickHandler() { 
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.workflowOption) {
				executeAddWorkflow();
			}
		}
	};
	
	/**
	 * Execute remove property group
	 */
	public void executeRemovePropertyGroup() {
		Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_DELETE_PROPERTY_GROUP);
		Main.get().confirmPopup.show();
		fireEvent(HasToolBarEvent.EXECUTE_REMOVE_PROPERTY_GROUP);
	}
	
	/**
	 * Add subscription
	 */
	ClickHandler addSubscriptionHandler = new ClickHandler() { 
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.addSubscription) {
				executeAddSubscription();
			}
		}
	};
	
	/**
	 * Execute add subscription
	 */
	public void executeAddSubscription(){
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
			Main.get().mainPanel.desktop.browser.fileBrowser.addSubscription();
		} else if (Main.get().activeFolderTree.isPanelSelected()) {
			Main.get().activeFolderTree.addSubscription();
		}
		fireEvent(HasToolBarEvent.EXECUTE_ADD_SUBSCRIPTION);
	}
	
	/**
	 * Remove subscription
	 */
	ClickHandler removeSubscriptionHandler = new ClickHandler() { 
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.removeSubscription) {
				executeRemoveSubscription();
			}
		}
	};
	
	/**
	 * Execute remove property group
	 */
	public void executeRemoveSubscription(){
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
			Main.get().mainPanel.desktop.browser.fileBrowser.removeSubscription();
		} else if (Main.get().activeFolderTree.isPanelSelected()) {
			Main.get().activeFolderTree.removeSubscription();
		}
		fireEvent(HasToolBarEvent.EXECUTE_REMOVE_SUBSCRIPTION);
	}
	
	/**
	 * Arrow rotate clock wise Handler
	 */
	ClickHandler arrowRefreshHandler = new ClickHandler() { 
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.refreshOption) {
				executeRefresh();
			}
		}
	};
	
	/**
	 * Scanner Handler
	 */
	ClickHandler scannerHandler = new ClickHandler() { 
		@Override
		public void onClick(ClickEvent event) {
			executeScanner();
		}
	};
	
	/**
	 * Upload Handler
	 */
	ClickHandler uploaderHandler = new ClickHandler() { 
		@Override
		public void onClick(ClickEvent event) {
			executeUploader();
		}
	};
	
	/**
	 * executeScanner
	 */
	public void executeScanner() {
		if (toolBarOption.scannerOption ) {
			setScannerApplet(Main.get().workspaceUserProperties.getWorkspace().getToken(),
					  Main.get().activeFolderTree.getActualPath());
			fireEvent(HasToolBarEvent.EXECUTE_SCANNER);
		}
	}
	
	/**
	 * executeUploader
	 */
	public void executeUploader() {
		if (toolBarOption.uploaderOption ) {
			setUploaderApplet(Main.get().workspaceUserProperties.getWorkspace().getToken(),
					  Main.get().activeFolderTree.getActualPath());
			fireEvent(HasToolBarEvent.EXECUTE_UPLOADER);
		}

	}
	
	/**
	 * Refreshing workspace
	 */
	public void executeRefresh() {
		switch (Main.get().mainPanel.topPanel.tabWorkspace.getSelectedWorkspace()) {
			case UIDockPanelConstants.DESKTOP :
				int actualView = Main.get().mainPanel.desktop.navigator.stackPanel.getStackIndex();
				switch (actualView){
					case UIDesktopConstants.NAVIGATOR_TAXONOMY:
					case UIDesktopConstants.NAVIGATOR_CATEGORIES:
					case UIDesktopConstants.NAVIGATOR_THESAURUS:
					case UIDesktopConstants.NAVIGATOR_TEMPLATES:
					case UIDesktopConstants.NAVIGATOR_PERSONAL:
					case UIDesktopConstants.NAVIGATOR_MAIL:
						Main.get().activeFolderTree.refresh(false);
						break;
						
					case UIDesktopConstants.NAVIGATOR_TRASH:
						Main.get().activeFolderTree.refresh(false);
						break;
				}
				break;
				
			case UIDockPanelConstants.SEARCH :
				break;
				
			case UIDockPanelConstants.DASHBOARD :
				Main.get().mainPanel.dashboard.refreshAll();
				break;
		}
		fireEvent(HasToolBarEvent.EXECUTE_REFRESH);
	}
	
	/**
	 * Arrow rotate clock wise Handler
	 */
	ClickHandler arrowHomeHandler = new ClickHandler() { 
		@Override
		public void onClick(ClickEvent event) {
			if (toolBarOption.homeOption) {
				executeGoToUserHome();
			}
		}
	};
	
	/**
	 * Goes home
	 */
	public void executeGoToUserHome() {		
		// First must validate path is correct
		if (Main.get().userHome!=null && !Main.get().userHome.getHomePath().equals("")) {
			if (Main.get().userHome.getHomeType().equals(Bookmark.BOOKMARK_DOCUMENT)) {
				ServiceDefTarget endPoint = (ServiceDefTarget) documentService;
				endPoint.setServiceEntryPoint(RPCService.DocumentService);
				documentService.isValid( Main.get().userHome.getHomePath() ,callbackIsValidDocument);
			} else if (Main.get().userHome.getHomeType().equals(Bookmark.BOOKMARK_FOLDER)) {
				ServiceDefTarget endPoint = (ServiceDefTarget) folderService;
				endPoint.setServiceEntryPoint(RPCService.FolderService);	
				folderService.isValid(Main.get().userHome.getHomePath(), callbackIsValidFolder);
			}
		}
		fireEvent(HasToolBarEvent.EXECUTE_GO_HOME);
	}
	
	/**
	 * Execute add export
	 */
	public void executeExport(){
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
			Main.get().mainPanel.desktop.browser.fileBrowser.exportFolderToFile();
		} else if (Main.get().activeFolderTree.isPanelSelected()) {
			Main.get().activeFolderTree.exportFolderToFile();
		}
		fireEvent(HasToolBarEvent.EXECUTE_EXPORT_TO_ZIP);
	}

	/**
	 * Gets the HTML space code
	 * 
	 * @return Space tool bar code
	 */
	private HTML space() {
		HTML space = new HTML(" ");
		space.setStyleName("okm-ToolBar-space");
		return space;
	}

	/**
	 * Tool Bar
	 */
	public ToolBar() {
		actualView = UIDesktopConstants.NAVIGATOR_TAXONOMY;
		viewValues = new HashMap<String, ToolBarOption>();
		toolBarOption = getDefaultRootToolBar();
		widgetExtensionList = new ArrayList<ToolBarButtonExtension>();
		toolBarHandlerExtensionList = new ArrayList<ToolBarHandlerExtension>();
		
		// ONLY TO DEVELOPMENT TESTINGT
		//enableAllToolBarForTestingPurpose();
		createFolder = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.createFolder()), 
											   Main.i18n("tree.menu.directory.create"), createFolderHandler);
		
		findFolder = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.findFolder()), 
				  					   Main.i18n("tree.menu.directory.find.folder"), findFolderHandler);
			
		
		lock = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.lockDisabled()), 
				   				 Main.i18n("general.menu.file.lock"), lockHandler);
			
		unlock = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.lockDisabled()), 
  				 				   Main.i18n("general.menu.file.unlock"), unLockHandler);
			
		addDocument = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.addDocument()), 
				   						Main.i18n("general.menu.file.add.document"), addDocumentHandler);
			
			
		delete = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.deleteDisabled()), 
								   Main.i18n("general.menu.file.delete"), deleteHandler);
			
		checkout = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.checkoutDisabled()), 
				   				 Main.i18n("general.menu.file.checkout"), editHandler);
			
		checkin = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.checkinDisabled()), 
  				 					Main.i18n("general.menu.file.checkin"), checkinHandler);
		
		//added by vissu on feb23 for zohoapi
		zoho = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.zohoDisabled()), 
					Main.i18n("general.menu.file.zoho"), zohoHandler);
			
		cancelCheckout = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.cancelCheckoutDisabled()), 
											Main.i18n("general.menu.file.cancel.checkout"), cancelCheckoutHandler);
			
		download = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.downloadDisabled()),
									  Main.i18n("general.menu.file.download.document"), downloadHandler);
			
		downloadPdf = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.downloadPdfDisabled()),
				  						Main.i18n("general.menu.file.download.document.pdf"), downloadPdfHandler); 
			
		addPropertyGroup = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.addPropertyGroupDisabled()),
											 Main.i18n("filebrowser.menu.add.property.group"), addPropertyGroupHandler); 
			
		removePropertyGroup = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.removePropertyGroupDisabled()),
				 								Main.i18n("filebrowser.menu.remove.property.group"), removePropertyGroupHandler); 
			
		startWorkflow = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.startWorkflowDisabled()),
										  Main.i18n("filebrowser.menu.start.workflow"), startWorkflowHandler); 
			

		addSubscription = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.addSubscriptionDisabled()),
				  							Main.i18n("filebrowser.menu.add.subscription"), addSubscriptionHandler); 
			
		removeSubscription = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.removeSubscriptionDisabled()),
											   Main.i18n("filebrowser.menu.remove.subscription"), removeSubscriptionHandler); 

		home = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.home()),
				   				  Main.i18n("general.menu.bookmark.home"), arrowHomeHandler); 
			
		refresh = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.refresh()),
 				  					 Main.i18n("general.menu.file.refresh"), arrowRefreshHandler); 
			
		scanner = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.scanner()),
					 				Main.i18n("general.menu.file.scanner"), scannerHandler); 
			
		uploader  = new ToolBarButton(new Image(OKMBundleResources.INSTANCE.uploader()),
 				Main.i18n("general.menu.file.uploader"), uploaderHandler); 
		
		createFolder.addMouseOverHandler(mouseOverHandler);
		createFolder.addMouseOutHandler(mouseOutHandler);
		findFolder.addMouseOverHandler(mouseOverHandler);
		findFolder.addMouseOutHandler(mouseOutHandler);
		lock.addMouseOverHandler(mouseOverHandler);
		lock.addMouseOutHandler(mouseOutHandler);
		unlock.addMouseOverHandler(mouseOverHandler);
		unlock.addMouseOutHandler(mouseOutHandler);
		addDocument.addMouseOverHandler(mouseOverHandler);
		addDocument.addMouseOutHandler(mouseOutHandler);
		delete.addMouseOverHandler(mouseOverHandler);
		delete.addMouseOutHandler(mouseOutHandler);
		checkout.addMouseOverHandler(mouseOverHandler);
		checkout.addMouseOutHandler(mouseOutHandler);
		checkin.addMouseOverHandler(mouseOverHandler);
		checkin.addMouseOutHandler(mouseOutHandler);
		cancelCheckout.addMouseOverHandler(mouseOverHandler);
		cancelCheckout.addMouseOutHandler(mouseOutHandler);
		
		//added by vissu on feb27
		zoho.addMouseOverHandler(mouseOverHandler);
		zoho.addMouseOutHandler(mouseOutHandler);
		
		download.addMouseOverHandler(mouseOverHandler);
		download.addMouseOutHandler(mouseOutHandler);
		downloadPdf.addMouseOverHandler(mouseOverHandler);
		downloadPdf.addMouseOutHandler(mouseOutHandler);
		addPropertyGroup.addMouseOverHandler(mouseOverHandler);
		addPropertyGroup.addMouseOutHandler(mouseOutHandler);
		removePropertyGroup.addMouseOverHandler(mouseOverHandler);
		removePropertyGroup.addMouseOutHandler(mouseOutHandler);
		startWorkflow.addMouseOverHandler(mouseOverHandler);
		startWorkflow.addMouseOutHandler(mouseOutHandler);
		addSubscription.addMouseOverHandler(mouseOverHandler);
		addSubscription.addMouseOutHandler(mouseOutHandler);
		removeSubscription.addMouseOverHandler(mouseOverHandler);
		removeSubscription.addMouseOutHandler(mouseOutHandler);
		home.addMouseOverHandler(mouseOverHandler);
		home.addMouseOutHandler(mouseOutHandler);
		refresh.addMouseOverHandler(mouseOverHandler);
		refresh.addMouseOutHandler(mouseOutHandler);
		scanner.addMouseOverHandler(mouseOverHandler);
		scanner.addMouseOutHandler(mouseOutHandler);
		uploader.addMouseOverHandler(mouseOverHandler);
		uploader.addMouseOutHandler(mouseOutHandler);
		
		createFolder.setStyleName("okm-ToolBar-button");
		findFolder.setStyleName("okm-ToolBar-button");
		lock.setStyleName("okm-ToolBar-button");
		unlock.setStyleName("okm-ToolBar-button");
		addDocument.setStyleName("okm-ToolBar-button");
		delete.setStyleName("okm-ToolBar-button-disabled");
		checkout.setStyleName("okm-ToolBar-button-disabled");
		checkin.setStyleName("okm-ToolBar-button-disabled");
		cancelCheckout.setStyleName("okm-ToolBar-button-disabled");
		
		//added by vissu on feb27
		zoho.setStyleName("okm-ToolBar-button-disabled");
		
		download.setStyleName("okm-ToolBar-button-disabled");
		downloadPdf.setStyleName("okm-ToolBar-button-disabled");
		addPropertyGroup.setStyleName("okm-ToolBar-button-disabled");
		removePropertyGroup.setStyleName("okm-ToolBar-button-disabled");
		startWorkflow.setStyleName("okm-ToolBar-button-disabled");
		addSubscription.setStyleName("okm-ToolBar-button-disabled");
		removeSubscription.setStyleName("okm-ToolBar-button-disabled");
		home.setStyleName("okm-ToolBar-button-disabled");
		refresh.setStyleName("okm-ToolBar-button-disabled");
		scanner.setStyleName("okm-ToolBar-button-disabled");
		uploader.setStyleName("okm-ToolBar-button-disabled");
		
		panel = new HorizontalPanel();
		panel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		panel.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		panel.addStyleName("okm-ToolBar");
		panel.add(space());
		panel.add(createFolder);
		panel.add(space());
		panel.add(findFolder);
		panel.add(space());
		panel.add(download);
		panel.add(space());
		panel.add(downloadPdf);
		panel.add(space());
		panel.add(new Image(OKMBundleResources.INSTANCE.separator())); // pos 9
		panel.add(lock);
		panel.add(space());
		panel.add(unlock);
		panel.add(space());
		panel.add(new Image(OKMBundleResources.INSTANCE.separator()));
		panel.add(addDocument);
		panel.add(space());
		panel.add(checkout);
		panel.add(space());
		panel.add(checkin);
		panel.add(space());
		panel.add(cancelCheckout);
		panel.add(space());
		panel.add(delete);
		panel.add(space());
		panel.add(new Image(OKMBundleResources.INSTANCE.separator()));
		panel.add(addPropertyGroup);
		panel.add(space());
		panel.add(removePropertyGroup);
		panel.add(space());
		panel.add(new Image(OKMBundleResources.INSTANCE.separator()));
		panel.add(startWorkflow);
		panel.add(space());
		panel.add(new Image(OKMBundleResources.INSTANCE.separator()));
		panel.add(addSubscription);
		panel.add(space());
		panel.add(removeSubscription);
		panel.add(space());
		panel.add(new Image(OKMBundleResources.INSTANCE.separator()));
		panel.add(refresh);
		panel.add(space());
		panel.add(home);
		panel.add(space());
		panel.add(new Image(OKMBundleResources.INSTANCE.separator()));
		panel.add(scanner);
		panel.add(space());
		panel.add(uploader);
		panel.add(space());
		
		//added by vissu on feb27
		panel.add(zoho);
		panel.add(space());
		
		// Hide all buttons at startup
		for (int i=0; i<panel.getWidgetCount(); i++) {
			panel.getWidget(i).setVisible(false);
		}
		
		initWidget(panel);
	}
	
	/**
	 * Checks permissions associated to folder and tool button enabled actions
	 * 
	 * @param folder The folder
	 * @param folderParent the folder parent
	 * @param origin The Origin panel 
	 */
	public void checkToolButtonPermissions(GWTFolder folder, GWTFolder folderParent, int originPanel) {
		//folderParent.setPermissions((byte)(GWTPermission.DELETE | GWTPermission.READ | GWTPermission.SECURITY | GWTPermission.WRITE));
		// Only if toolbar is enabled must change tools icons values
		if (isEnabled()) {			
			disableDownload();
			disableDownloadPdf();
			disableSendDocumentLink();
			disableSendDocumentAttachment();
			disableCheckout();
			disableLock();
			disableCheckin();
			disableCancelCheckout();
			
			disableZoho();		//added by vissu on feb27
			
			disableUnlock();
			disableWorkflow();
			disableRename();
			disableCopy();
			disableMove();
			disableAddNote();
			disableScanner();
			disableUploader();
			
			boolean isRoot = Main.get().taxonomyRootFolder.getPath().equals(folder.getPath()) || 
							//commented by vissu on may18 for disable categories & thesaurus
							 //Main.get().thesaurusRootFolder.getPath().equals(folder.getPath()) ||
							 //Main.get().categoriesRootFolder.getPath().equals(folder.getPath()) ||
							 Main.get().templatesRootFolder.getPath().equals(folder.getPath()) ||
							 Main.get().personalRootFolder.getPath().equals(folder.getPath()) || 
							 Main.get().trashRootFolder.getPath().equals(folder.getPath()) ;
								//commented out by vissu may14 for disable mail
								//||  Main.get().mailRootFolder.getPath().equals(folder.getPath());
			
			// On folder parent don't enables subscription
			if (isRoot) {
				disableAddSubscription();
				disableRemoveSubscription();
				disableExport();
				
			} else if (folder.isSubscribed()) {
				disableAddSubscription();
				enableRemoveSubscription();
				
			} else {
				enableAddSubscription();
				disableRemoveSubscription();
			}
			
			// Enables or disables deleting ( in root is not enabled by default 
			if(!isRoot && ((folderParent.getPermissions() & GWTPermission.DELETE) == GWTPermission.DELETE) &&
			   ((folder.getPermissions() & GWTPermission.DELETE) == GWTPermission.DELETE)) {
				enableDelete();
			} else {
				disableDelete();
			}
			
			if ((folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE) {
				if (originPanel != FILE_BROWSER) {
					enableAddDocument();
					enableCreateDirectory();
				}
				// Evaluates special case root node that must not be deleted;
				if (!isRoot) {
					enableRename(); 
					enableCopy();
					enableMove();
					enableExport();
				}
				
				// Enable scanner button
				if (Main.get().mainPanel.desktop.navigator.getStackIndex()== UIDesktopConstants.NAVIGATOR_TAXONOMY ||
					Main.get().mainPanel.desktop.navigator.getStackIndex()== UIDesktopConstants.NAVIGATOR_TEMPLATES ||
					Main.get().mainPanel.desktop.navigator.getStackIndex()== UIDesktopConstants.NAVIGATOR_PERSONAL ) {
					enableScanner();
					enableUploader();
				}
				
				// Enable workflow
				if (Main.get().mainPanel.desktop.navigator.getStackIndex()!= UIDesktopConstants.NAVIGATOR_THESAURUS ||
					Main.get().mainPanel.desktop.navigator.getStackIndex()!= UIDesktopConstants.NAVIGATOR_CATEGORIES) {
					enableWorkflow();
				}
				
				// Enable property groups
				if ((folderParent.getPermissions() & GWTPermission.WRITE)==GWTPermission.WRITE && 
					(folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE) {
					enableRemovePropertyGroup(); // Always enable it ( not controls button, only boolean value )
					if (Main.get().mainPanel.desktop.navigator.getStackIndex()!= UIDesktopConstants.NAVIGATOR_CATEGORIES &&
						Main.get().mainPanel.desktop.navigator.getStackIndex()!= UIDesktopConstants.NAVIGATOR_THESAURUS &&
						Main.get().mainPanel.desktop.navigator.getStackIndex()!= UIDesktopConstants.NAVIGATOR_PERSONAL &&
						Main.get().mainPanel.desktop.navigator.getStackIndex()!= UIDesktopConstants.NAVIGATOR_TRASH && 
						Main.get().mainPanel.desktop.navigator.getStackIndex()!= UIDesktopConstants.NAVIGATOR_MAIL) {
						getAllGroups(folder); // Evaluates enable or disable property group buttons
					}
				} else {
					disableAddPropertyGroup();
					disableRemovePropertyGroup();
				}
				
			} else {
				if (originPanel != FILE_BROWSER) {
					disableCreateDirectory();
					disableAddDocument();
				}
				disableAddPropertyGroup();
				disableRemovePropertyGroup();
			}
			
			// Except taxonomy categories and thesaurus stack panels always disabling 
			if 	(Main.get().mainPanel.desktop.navigator.getStackIndex()== UIDesktopConstants.NAVIGATOR_PERSONAL || 
				Main.get().mainPanel.desktop.navigator.getStackIndex()== UIDesktopConstants.NAVIGATOR_TRASH || 
				Main.get().mainPanel.desktop.navigator.getStackIndex()== UIDesktopConstants.NAVIGATOR_MAIL) {
				disableAddPropertyGroup();
				disableRemovePropertyGroup();
				disableFiredRemovePropertyGroup();
				disableAddSubscription();
				disableRemoveSubscription();
			}
			
			// On templates disables subscription, but property group are enabled
			if (Main.get().mainPanel.desktop.navigator.getStackIndex()== UIDesktopConstants.NAVIGATOR_TEMPLATES) {
				disableAddSubscription();
				disableRemoveSubscription();
			}
			
			// Disables add document, delete and create directory from thesaurus view
			if (Main.get().mainPanel.desktop.navigator.getStackIndex()== UIDesktopConstants.NAVIGATOR_THESAURUS || 
				Main.get().mainPanel.desktop.navigator.getStackIndex()== UIDesktopConstants.NAVIGATOR_CATEGORIES ) {
				disableAddDocument();
				disableAddSubscription();
				if (Main.get().mainPanel.desktop.navigator.getStackIndex()== UIDesktopConstants.NAVIGATOR_THESAURUS) {
					disableCreateDirectory();
					disableDelete();
				}
			}
			
			// Enables find folder in Desktop view 
			if (Main.get().mainPanel.topPanel.tabWorkspace.getSelectedWorkspace()==UIDockPanelConstants.DESKTOP){
				enableFindFolder();
			} else {
				disableFindFolder();
			}
			
			// The remove property group is special case depends on tab property enabled, with this call we force to set false
			evaluateRemoveGroupProperty(false);
			
			// ONLY TO DEVELOPMENT TESTINGT
			//enableAllToolBarForTestingPurpose();
			
			// Sets the permission to main menu
			Main.get().mainPanel.topPanel.mainMenu.setOptions(toolBarOption);
			
			// Checking extension button 
			for (Iterator<ToolBarButtonExtension> it = widgetExtensionList.iterator(); it.hasNext();) {
				it.next().checkPermissions(folder, folderParent, originPanel);
			}
			
			fireEvent(HasToolBarEvent.EXECUTE_CHECK_FOLDER_PERMISSION);
		}
	}
	
	/**
	 * Checks permissions associated to document and tool button enabled actions
	 * 
	 * @param doc The document
	 */
	public void checkToolButtonPermissions(GWTDocument doc, GWTFolder folder) {
		// Only if toolbar is enabled must change tools icons values
		if (isEnabled()) {
			boolean disable = false;
			String user = Main.get().workspaceUserProperties.getUser();
			
			enableDownload();
			disableRename(); 
			disableCopy();
			disableMove();
			disableExport();
			disableAddNote();
			disableScanner();
			disableUploader();
			disableAddSubscription();
			disableRemoveSubscription();

			if (doc.isConvertibleToPdf()) {
				enableDownloadPdf();
			} else {
				disableDownloadPdf();
			}
			
			// Checking delete permissions
			if (((doc.getPermissions() & GWTPermission.DELETE) == GWTPermission.DELETE) && 
				((folder.getPermissions() & GWTPermission.DELETE) == GWTPermission.DELETE ) &&
				!doc.isCheckedOut() && !doc.isLocked() &&
				Main.get().mainPanel.desktop.navigator.getStackIndex()!= UIDesktopConstants.NAVIGATOR_THESAURUS &&
				Main.get().mainPanel.desktop.navigator.getStackIndex()!= UIDesktopConstants.NAVIGATOR_CATEGORIES) {
				enableDelete();
			} else {
				disableDelete();
			}
			
			// Enable scanner button
			if (((folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE ) && 
				 (Main.get().mainPanel.desktop.navigator.getStackIndex()== UIDesktopConstants.NAVIGATOR_TAXONOMY ||
				  Main.get().mainPanel.desktop.navigator.getStackIndex()== UIDesktopConstants.NAVIGATOR_TEMPLATES ||
				  Main.get().mainPanel.desktop.navigator.getStackIndex()== UIDesktopConstants.NAVIGATOR_PERSONAL) ) {
				enableScanner();
				enableUploader();
			}
			
			if ((doc.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE) {
				if (!doc.isCheckedOut() && !doc.isLocked()) {
					enableCheckout();
					
					enableZoho();		//added by vissu on feb27
					
					enableLock();
					disableCheckin();
					disableCancelCheckout();
					disableUnlock();
					enableAddNote();
					
					if (doc.isSubscribed()) {
						enableRemoveSubscription();
					} else {
						enableAddSubscription();
					} 
					
					// In thesaurus and categories view must not evaluate write folder permissions
					if ((folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE ||
						 Main.get().mainPanel.desktop.navigator.getStackIndex()!= UIDesktopConstants.NAVIGATOR_THESAURUS ||
						 Main.get().mainPanel.desktop.navigator.getStackIndex()!= UIDesktopConstants.NAVIGATOR_CATEGORIES) {
						enableRename();
						enableCopy();
						enableMove();
						enableRemovePropertyGroup(); // Always enable it ( not controls button, only boolean value )
						enableWorkflow();
						if (Main.get().mainPanel.desktop.navigator.getStackIndex()!= UIDesktopConstants.NAVIGATOR_CATEGORIES &&
							Main.get().mainPanel.desktop.navigator.getStackIndex()!= UIDesktopConstants.NAVIGATOR_THESAURUS &&
							Main.get().mainPanel.desktop.navigator.getStackIndex()!= UIDesktopConstants.NAVIGATOR_PERSONAL &&
							Main.get().mainPanel.desktop.navigator.getStackIndex()!= UIDesktopConstants.NAVIGATOR_TRASH && 
							Main.get().mainPanel.desktop.navigator.getStackIndex()!= UIDesktopConstants.NAVIGATOR_MAIL) {
							getAllGroups(doc); // Evaluates enable or disable property group buttons
						}
					} else {
						disableAddPropertyGroup();
						disableRemovePropertyGroup();
					}
				} else {
					if (doc.isCheckedOut()) {
						if (doc.getLockInfo().getOwner().equals(user) || Main.get().workspaceUserProperties.getWorkspace().isAdminRole()) {
							if (doc.getLockInfo().getOwner().equals(user)) {
								enableCheckin();
								enableAddPropertyGroup();
								enableRemovePropertyGroup();
								enableAddNote();
							} else {
								disableCheckin();
								disableAddPropertyGroup();
								disableRemovePropertyGroup();
								disableAddNote();
							}
							enableCancelCheckout();
							disableCheckout();
							disableZoho();		//added by vissu on feb27
							disableLock();
							disableUnlock();
							
						} else {
							disable = true;
						}
					} else {
						if (doc.getLockInfo().getOwner().equals(user) || Main.get().workspaceUserProperties.getWorkspace().isAdminRole()) {
							enableUnlock();
							disableCheckin();
							disableCancelCheckout();
							disableCheckout();
							
							disableZoho();	//added by vissu on feb27
							
							disableLock();
							enableAddPropertyGroup();
							enableRemovePropertyGroup();
							enableAddNote();
							
							if (doc.isSubscribed()) {
								enableRemoveSubscription();
							} else {
								enableAddSubscription();
							} 
						} else {
							disable = true;
						}
					}
				}
			} else {
				disable = true;
			}
			
			if (disable) {
				disableLock();
				disableUnlock();
				disableCheckout();
				
				disableZoho();		//added by vissu on feb27
				
				disableCheckin();
				disableCancelCheckout();
				disableAddPropertyGroup();
				disableRemovePropertyGroup();
				disableWorkflow();
			} 
			
			// Only on taxonomy, categories, and thesaurus enables to send document link by mail 
			if (Main.get().mainPanel.desktop.navigator.getStackIndex()== UIDesktopConstants.NAVIGATOR_TAXONOMY ||
				Main.get().mainPanel.desktop.navigator.getStackIndex()!= UIDesktopConstants.NAVIGATOR_THESAURUS ||
				Main.get().mainPanel.desktop.navigator.getStackIndex()!= UIDesktopConstants.NAVIGATOR_CATEGORIES) {
				enableSendDocumentLink();
				enableSendDocumentAttachment();
			} else {
				disableSendDocumentLink();
				disableSendDocumentAttachment();
			}
			
			// Excepts on taxonomy, categories and thesaurus panel always disabling 
			if (Main.get().mainPanel.desktop.navigator.getStackIndex()== UIDesktopConstants.NAVIGATOR_PERSONAL || 
				Main.get().mainPanel.desktop.navigator.getStackIndex()== UIDesktopConstants.NAVIGATOR_TRASH || 
				Main.get().mainPanel.desktop.navigator.getStackIndex()== UIDesktopConstants.NAVIGATOR_MAIL) {
				disableAddPropertyGroup();
				disableRemovePropertyGroup();
				disableFiredRemovePropertyGroup();
				disableAddSubscription();
				disableRemoveSubscription();
			}
			
			// On templates disables subscription, but property group are enabled
			if (Main.get().mainPanel.desktop.navigator.getStackIndex()== UIDesktopConstants.NAVIGATOR_TEMPLATES) {
				disableAddSubscription();
				disableRemoveSubscription();
			}
			
			// Enables find folder in Desktop view 
			if (Main.get().mainPanel.topPanel.tabWorkspace.getSelectedWorkspace()==UIDockPanelConstants.DESKTOP){
				enableFindFolder();
			} else {
				disableFindFolder();
			}
			
			// ONLY TO DEVELOPMENT TESTINGT
			//enableAllToolBarForTestingPurpose();
			
			// Sets the permission to main menu
			Main.get().mainPanel.topPanel.mainMenu.setOptions(toolBarOption);
			
			// Sets the visible values to note tab
			Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.notes.setVisibleAddNote(toolBarOption.addNoteOption);
			
			// Checking extension button 
			for (Iterator<ToolBarButtonExtension> it = widgetExtensionList.iterator(); it.hasNext();) {
				it.next().checkPermissions(doc, folder);
			}
			
			fireEvent(HasToolBarEvent.EXECUTE_CHECK_DOCUMENT_PERMISSION);
		}
	}
	
	/**
	 * Checks permissions associated to document and tool button enabled actions
	 * 
	 * @param mail The Mail
	 */
	public void checkToolButtonPermissions(GWTMail mail, GWTFolder folder) {
		// Only if toolbar is enabled must change tools icons values
		if (isEnabled()) {
			
			disableDownload();
			disableRename(); 
			disableCopy();
			disableMove();
			disableExport();
			disableDownloadPdf();
			disableAddSubscription();
			disableRemoveSubscription();
			disableCheckout();
			
			disableZoho();		//added by vissu on feb27
			
			disableLock();
			disableUnlock();
			disableCheckout();
			disableCheckin();
			disableCancelCheckout();
			disableWorkflow();
			disableAddDocument();
			disableAddNote();
			disableScanner();
			disableUploader();
			
			// Checking delete permissions
			if (((mail.getPermissions() & GWTPermission.DELETE) == GWTPermission.DELETE) && 
				((folder.getPermissions() & GWTPermission.DELETE) == GWTPermission.DELETE )) {
				enableDelete();
			} else {
				disableDelete();
			}
			
			// Enable scanner button
			if (((folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE ) && 
				 (Main.get().mainPanel.desktop.navigator.getStackIndex()== UIDesktopConstants.NAVIGATOR_TAXONOMY ||
				  Main.get().mainPanel.desktop.navigator.getStackIndex()== UIDesktopConstants.NAVIGATOR_TEMPLATES ||
				  Main.get().mainPanel.desktop.navigator.getStackIndex()== UIDesktopConstants.NAVIGATOR_PERSONAL) ) {
				enableScanner();
				enableUploader();
			}
			
			if (((mail.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE) && 
				((folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE)) {
				
				// In thesaurus and categories view must not evaluate write folder permissions
				if ((folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE ||
					 Main.get().mainPanel.desktop.navigator.getStackIndex()!= UIDesktopConstants.NAVIGATOR_THESAURUS ||
					 Main.get().mainPanel.desktop.navigator.getStackIndex()!= UIDesktopConstants.NAVIGATOR_CATEGORIES) {
					enableWorkflow();
				}
				
				enableRename();
				enableCopy();
				enableMove();
				enableRemovePropertyGroup(); // Always enable it ( not controls button, only boolean value )
				if (Main.get().mainPanel.desktop.navigator.getStackIndex()!= UIDesktopConstants.NAVIGATOR_CATEGORIES &&
					Main.get().mainPanel.desktop.navigator.getStackIndex()!= UIDesktopConstants.NAVIGATOR_THESAURUS &&
					Main.get().mainPanel.desktop.navigator.getStackIndex()!= UIDesktopConstants.NAVIGATOR_PERSONAL &&
					Main.get().mainPanel.desktop.navigator.getStackIndex()!= UIDesktopConstants.NAVIGATOR_TRASH && 
					Main.get().mainPanel.desktop.navigator.getStackIndex()!= UIDesktopConstants.NAVIGATOR_MAIL) {
					getAllGroups(mail); // Evaluates enable or disable property group buttons
				}
				// On mail panel is not able to uploading files
				if (Main.get().mainPanel.desktop.navigator.getStackIndex()!= UIDesktopConstants.NAVIGATOR_MAIL ) {
					enableAddDocument();
				} 		
			} else {
				disableAddPropertyGroup();
				disableRemovePropertyGroup();
			}
			
			// Onnly on taxonomy enables to send document link by mail 
			if (Main.get().mainPanel.desktop.navigator.getStackIndex()== UIDesktopConstants.NAVIGATOR_TAXONOMY) {
				enableSendDocumentLink();
				enableSendDocumentAttachment();
			} else {
				disableSendDocumentLink();
				disableSendDocumentAttachment();
			}
			
			// Excepts on taxonomy categories and thesaurus panel always disabling 
			if (Main.get().mainPanel.desktop.navigator.getStackIndex()== UIDesktopConstants.NAVIGATOR_PERSONAL || 
				Main.get().mainPanel.desktop.navigator.getStackIndex()== UIDesktopConstants.NAVIGATOR_TRASH || 
				Main.get().mainPanel.desktop.navigator.getStackIndex()== UIDesktopConstants.NAVIGATOR_MAIL) {
				disableAddPropertyGroup();
				disableRemovePropertyGroup();
				disableFiredRemovePropertyGroup();
				disableAddSubscription();
				disableRemoveSubscription();
			}
			
			// On templates disables subscription, but property group are enabled
			if (Main.get().mainPanel.desktop.navigator.getStackIndex()== UIDesktopConstants.NAVIGATOR_TEMPLATES) {
				disableAddSubscription();
				disableRemoveSubscription();
			}
			
			// ONLY TO DEVELOPMENT TESTINGT
			//enableAllToolBarForTestingPurpose();
			
			// Sets the permission to main menu
			Main.get().mainPanel.topPanel.mainMenu.setOptions(toolBarOption);
			
			// Checking extension button 
			for (Iterator<ToolBarButtonExtension> it = widgetExtensionList.iterator(); it.hasNext();) {
				it.next().checkPermissions(mail, folder);
			}
			
			fireEvent(HasToolBarEvent.EXECUTE_CHECK_MAIL_PERMISSION);
		}
	}
	
	/**
	 * Indicates if toolBar is enabled
	 * 
	 * @return The value of enabled / disabled toolbar
	 */
	public boolean isEnabled() {
		return enabled;
	}
	
	/**
	 * Enables create directory
	 */
	public void enableCreateDirectory() {
		toolBarOption.createFolderOption = true;
		createFolder.setStyleName("okm-ToolBar-button");
		createFolder.setResource(OKMBundleResources.INSTANCE.createFolder()); 
		createFolder.setTitle(Main.i18n("tree.menu.directory.create"));
	}
	
	/**
	 * Disables create directory
	 */
	public void disableCreateDirectory() {
		toolBarOption.createFolderOption = false;
		createFolder.setStyleName("okm-ToolBar-button-disabled");
		createFolder.setResource(OKMBundleResources.INSTANCE.createFolderDisabled()); 
		createFolder.setTitle(Main.i18n("tree.menu.directory.create"));
	}
	
	/**
	 * Enables find folder
	 */
	public void enableFindFolder() {
		toolBarOption.findFolderOption = true;
		findFolder.setStyleName("okm-ToolBar-button");
		findFolder.setResource(OKMBundleResources.INSTANCE.findFolder());
		findFolder.setTitle(Main.i18n("tree.menu.directory.find.folder"));
	}
	
	/**
	 * Disables create directory
	 */
	public void disableFindFolder() {
		toolBarOption.findFolderOption = false;
		findFolder.setStyleName("okm-ToolBar-button-disabled");
		findFolder.setResource(OKMBundleResources.INSTANCE.findFolderDisabled());
		findFolder.setTitle(Main.i18n("tree.menu.directory.find.folder"));
	}
	
	/**
	 * Enables edit button
	 */
	public void enableCheckout() {
		toolBarOption.checkoutOption = true;
		checkout.setStyleName("okm-ToolBar-button");
		checkout.setResource(OKMBundleResources.INSTANCE.checkout());
		checkout.setTitle(Main.i18n("general.menu.file.checkout"));
	}
	
	/**
	 * Disables edit button
	 */
	public void disableCheckout() {
		toolBarOption.checkoutOption = false;
		checkout.setStyleName("okm-ToolBar-button-disabled");
		checkout.setResource(OKMBundleResources.INSTANCE.checkoutDisabled());
		checkout.setTitle(Main.i18n("general.menu.file.checkout"));
	}
	
	//added by vissu on feb23 for zohoapi
	public void enableZoho() {
		toolBarOption.zohoOption = true;
		zoho.setStyleName("okm-ToolBar-button");
		zoho.setResource(OKMBundleResources.INSTANCE.zoho());
		zoho.setTitle(Main.i18n("general.menu.file.zoho"));
	}
	
	/**
	 * Disables edit button
	 */
	public void disableZoho() {
		toolBarOption.checkoutOption = false;
		zoho.setStyleName("okm-ToolBar-button-disabled");
		zoho.setResource(OKMBundleResources.INSTANCE.zohoDisabled());
		zoho.setTitle(Main.i18n("general.menu.file.zoho"));
	}
	/**
	 * Enables checkin button
	 */
	public void enableCheckin() {
		toolBarOption.checkinOption = true;
		checkin.setStyleName("okm-ToolBar-button");
		checkin.setResource(OKMBundleResources.INSTANCE.checkin());
		checkin.setTitle(Main.i18n("general.menu.file.checkin"));
	}
	
	/**
	 * Disables checkin button
	 */
	public void disableCheckin() {
		toolBarOption.checkinOption = false;
		checkin.setStyleName("okm-ToolBar-button-disabled");
		checkin.setResource(OKMBundleResources.INSTANCE.checkinDisabled());
		checkin.setTitle(Main.i18n("general.menu.file.checkin"));
	}
	
	/**
	 * Enables checkout cancel button
	 */
	public void enableCancelCheckout() {
		toolBarOption.cancelCheckoutOption = true;
		cancelCheckout.setStyleName("okm-ToolBar-button");
		cancelCheckout.setResource(OKMBundleResources.INSTANCE.cancelCheckout());
		cancelCheckout.setTitle(Main.i18n("general.menu.file.cancel.checkout"));
	}
	
	/**
	 * Disables checkout cancel button
	 */
	public void disableCancelCheckout() {
		toolBarOption.cancelCheckoutOption = false;
		cancelCheckout.setStyleName("okm-ToolBar-button-disabled");
		cancelCheckout.setResource(OKMBundleResources.INSTANCE.cancelCheckoutDisabled());
		cancelCheckout.setTitle(Main.i18n("general.menu.file.cancel.checkout"));
	}
	
	/**
	 * Disables lock button
	 */
	public void disableLock() {
		toolBarOption.lockOption = false;
		lock.setStyleName("okm-ToolBar-button-disabled");
		lock.setResource(OKMBundleResources.INSTANCE.lockDisabled());
		lock.setTitle(Main.i18n("general.menu.file.lock"));
	}
	
	/**
	 * Enables lock button
	 */
	public void enableLock() {
		toolBarOption.lockOption = true;
		lock.setStyleName("okm-ToolBar-button");
		lock.setResource(OKMBundleResources.INSTANCE.lock());
		lock.setTitle(Main.i18n("general.menu.file.lock"));
	}
	
	/**
	 * Disables delete lock button
	 */
	public void disableUnlock() {
		toolBarOption.unLockOption = false;
		unlock.setStyleName("okm-ToolBar-button-disabled");
		unlock.setResource(OKMBundleResources.INSTANCE.unLockDisabled());
		unlock.setTitle(Main.i18n("general.menu.file.unlock"));
	}
	
	/**
	 * Enables delete lock button
	 */
	public void enableUnlock() {
		toolBarOption.unLockOption = true;
		unlock.setStyleName("okm-ToolBar-button");
		unlock.setResource(OKMBundleResources.INSTANCE.unLock());
		unlock.setTitle(Main.i18n("general.menu.file.unlock"));
	}
	
	/**
	 * Disables download button
	 */
	public void disableDownload() {
		toolBarOption.downloadOption = false;
		download.setStyleName("okm-ToolBar-button-disabled");
		download.setResource(OKMBundleResources.INSTANCE.downloadDisabled());
		download.setTitle(Main.i18n("general.menu.file.download.document"));
	}
	
	/**
	 * Enables download button
	 */
	public void enableDownload() {
		toolBarOption.downloadOption = true;
		download.setStyleName("okm-ToolBar-button");
		download.setResource(OKMBundleResources.INSTANCE.download());
		download.setTitle(Main.i18n("general.menu.file.download.document"));
	}

	/**
	 * Disables download as PDF button
	 */
	public void disableDownloadPdf() {
		toolBarOption.downloadPdfOption = false;
		downloadPdf.setStyleName("okm-ToolBar-button-disabled");
		downloadPdf.setResource(OKMBundleResources.INSTANCE.downloadPdfDisabled());
		downloadPdf.setTitle(Main.i18n("general.menu.file.download.document.pdf"));
	}
	
	/**
	 * Enables download as PDF button
	 */
	public void enableDownloadPdf() {
		toolBarOption.downloadPdfOption = true;
		downloadPdf.setStyleName("okm-ToolBar-button");
		downloadPdf.setResource(OKMBundleResources.INSTANCE.downloadPdf());
		downloadPdf.setTitle(Main.i18n("general.menu.file.download.document.pdf"));
	}

	/**
	 * Disables send document link button
	 */
	public void disableSendDocumentLink() {
		toolBarOption.sendDocumentLinkOption = false;
	}
	
	/**
	 * Enables send document link button
	 */
	public void enableSendDocumentLink(){
		toolBarOption.sendDocumentLinkOption = true;
	}
	
	/**
	 * Disables send document attachment button
	 */
	public void disableSendDocumentAttachment() {
		toolBarOption.sendDocumentAttachmentOption = false;
	}
	
	/**
	 * Enables send document attachment button
	 */
	public void enableSendDocumentAttachment(){
		toolBarOption.sendDocumentAttachmentOption = true;
	}
	
	/**
	 * Disables delete button
	 */
	public void disableDelete() {
		toolBarOption.deleteOption = false;
		delete.setStyleName("okm-ToolBar-button-disabled");
		delete.setResource(OKMBundleResources.INSTANCE.deleteDisabled());
		delete.setTitle(Main.i18n("general.menu.file.delete"));
	}
	
	/**
	 * Enables delete button
	 */
	public void enableDelete() {
		toolBarOption.deleteOption = true;
		delete.setStyleName("okm-ToolBar-button");
		delete.setResource(OKMBundleResources.INSTANCE.delete());
		delete.setTitle(Main.i18n("general.menu.file.delete"));
	}
	
	/**
	 * Disable arrow rotate clockwise 
	 */
	public void disableRefresh() {
		toolBarOption.refreshOption = false;
		refresh.setStyleName("okm-ToolBar-button-disabled");
		refresh.setResource(OKMBundleResources.INSTANCE.refreshDisabled());
		refresh.setTitle(Main.i18n("general.menu.file.refresh"));
	}
	
	/**
	 * Enables Rotate ClockWise Arrow
	 */
	public void enableRefresh() {
		toolBarOption.refreshOption = true;
		refresh.setStyleName("okm-ToolBar-button");
		refresh.setResource(OKMBundleResources.INSTANCE.refresh());
		refresh.setTitle(Main.i18n("general.menu.file.refresh"));
	}
	
	/**
	 * Disables add document 
	 */
	public void disableAddDocument() {
		toolBarOption.addDocumentOption = false;
		addDocument.setStyleName("okm-ToolBar-button-disabled");
		addDocument.setResource(OKMBundleResources.INSTANCE.addDocumentDisabled());
		addDocument.setTitle(Main.i18n("general.menu.file.add.document"));	
	}
	
	/**
	 * Enables add document 
	 */
	public void enableAddDocument() {
		toolBarOption.addDocumentOption = true;
		addDocument.setStyleName("okm-ToolBar-button");
		addDocument.setResource(OKMBundleResources.INSTANCE.addDocument());
		addDocument.setTitle(Main.i18n("general.menu.file.add.document"));	
	}
	
	/**
	 * Disables add property group 
	 */
	public void disableAddPropertyGroup() {
		toolBarOption.addPropertyGroupOption = false;
		addPropertyGroup.setStyleName("okm-ToolBar-button-disabled");
		addPropertyGroup.setResource(OKMBundleResources.INSTANCE.addPropertyGroupDisabled());
		addPropertyGroup.setTitle(Main.i18n("filebrowser.menu.add.property.group"));
	}
	
	/**
	 * Enables add property group 
	 */
	public void enableAddPropertyGroup() {
		toolBarOption.addPropertyGroupOption = true;
		addPropertyGroup.setStyleName("okm-ToolBar-button");
		addPropertyGroup.setResource(OKMBundleResources.INSTANCE.addPropertyGroup());
		addPropertyGroup.setTitle(Main.i18n("filebrowser.menu.add.property.group"));
	}
	
	/**
	 * Disables add subscription 
	 */
	public void disableAddSubscription() {
		toolBarOption.addSubscription = false;
		addSubscription.setStyleName("okm-ToolBar-button-disabled");
		addSubscription.setResource(OKMBundleResources.INSTANCE.addSubscriptionDisabled());
		addSubscription.setTitle(Main.i18n("filebrowser.menu.add.subscription"));
	}
	
	/**
	 * Enables add subscription 
	 */
	public void enableAddSubscription() {
		toolBarOption.addSubscription = true;
		addSubscription.setStyleName("okm-ToolBar-button");
		addSubscription.setResource(OKMBundleResources.INSTANCE.addSubscription());
		addSubscription.setTitle(Main.i18n("filebrowser.menu.add.subscription"));
	}
	
	/**
	 * Disables remove subscription 
	 */
	public void disableRemoveSubscription() {
		toolBarOption.removeSubscription = false;
		removeSubscription.setStyleName("okm-ToolBar-button-disabled");
		removeSubscription.setResource(OKMBundleResources.INSTANCE.removeSubscriptionDisabled());
		removeSubscription.setTitle(Main.i18n("filebrowser.menu.remove.subscription"));
	}
	
	/**
	 * Enables remove subscription 
	 */
	public void enableRemoveSubscription() {
		toolBarOption.removeSubscription = true;
		removeSubscription.setStyleName("okm-ToolBar-button");
		removeSubscription.setResource(OKMBundleResources.INSTANCE.removeSubscription());
		removeSubscription.setTitle(Main.i18n("filebrowser.menu.remove.subscription"));
	}
	
	/**
	 * Disables remove subscription 
	 */
	public void disableHome() {
		toolBarOption.homeOption = false;
		home.setStyleName("okm-ToolBar-button-disabled");
		home.setResource(OKMBundleResources.INSTANCE.homeDisabled());
		home.setTitle(Main.i18n("general.menu.bookmark.home"));
	}
	
	/**
	 * Enables remove subscription 
	 */
	public void enableHome() {
		toolBarOption.homeOption = true;
		home.setStyleName("okm-ToolBar-button");
		home.setResource(OKMBundleResources.INSTANCE.home());
		home.setTitle(Main.i18n("general.menu.bookmark.home"));
	}
	
	/**
	 * Disables remove property group 
	 */
	public void disableRemovePropertyGroup() {
		toolBarOption.removePropertyGroupOption = false;
	}
	
	/**
	 * Enables remove property group 
	 */
	public void enableRemovePropertyGroup() {
		toolBarOption.removePropertyGroupOption = true;
	}
	
	/**
	 * Disables workflow
	 */
	public void disableWorkflow() {
		toolBarOption.workflowOption = false;
		startWorkflow.setStyleName("okm-ToolBar-button-disabled");
		startWorkflow.setResource(OKMBundleResources.INSTANCE.startWorkflowDisabled());
		startWorkflow.setTitle(Main.i18n("filebrowser.menu.start.workflow"));
	}
	
	/**
	 * Enables workflow 
	 */
	public void enableWorkflow() {
		toolBarOption.workflowOption = true;
		startWorkflow.setStyleName("okm-ToolBar-button");
		startWorkflow.setResource(OKMBundleResources.INSTANCE.startWorkflow());
		startWorkflow.setTitle(Main.i18n("filebrowser.menu.start.workflow"));
	}
	
	/**
	 * Disables scanner
	 */
	public void disableScanner() {
		toolBarOption.scannerOption = false;
		scanner.setStyleName("okm-ToolBar-button-disabled");
		scanner.setResource(OKMBundleResources.INSTANCE.scannerDisabled());
		scanner.setTitle(Main.i18n("general.menu.file.scanner"));
	}
	
	/**
	 * Enables scanner 
	 */
	public void enableScanner() {
		toolBarOption.scannerOption = true;
		scanner.setStyleName("okm-ToolBar-button");
		scanner.setResource(OKMBundleResources.INSTANCE.scanner());
		scanner.setTitle(Main.i18n("general.menu.file.scanner"));
	}
	
	/**
	 * Disables uploader
	 */
	public void disableUploader() {
		toolBarOption.uploaderOption = false;
		uploader.setStyleName("okm-ToolBar-button-disabled");
		uploader.setResource(OKMBundleResources.INSTANCE.uploaderDisabled());
		uploader.setTitle(Main.i18n("general.menu.file.uploader"));
	}
	
	/**
	 * Enables uploader 
	 */
	public void enableUploader() {
		toolBarOption.uploaderOption = true;
		uploader.setStyleName("okm-ToolBar-button");
		uploader.setResource(OKMBundleResources.INSTANCE.uploader());
		uploader.setTitle(Main.i18n("general.menu.file.uploader"));
	}
	
	/**
	 * Disables fired property group 
	 */
	public void disableFiredRemovePropertyGroup() {
		toolBarOption.firedRemovePropertyGroupOption = false;
	}
	
	/**
	 * Enables fired property group 
	 */
	public void enableFiredRemovePropertyGroup() {
		toolBarOption.firedRemovePropertyGroupOption = true;
	}
	
	/**
	 * Enables rename
	 */
	public void enableRename() {
		toolBarOption.renameOption = true;
	}
	
	/**
	 * Disable rename
	 */
	public void disableRename() {
		toolBarOption.renameOption = false;
	}
	
	/**
	 * Enables copy
	 */
	public void enableCopy() {
		toolBarOption.copyOption = true;
	}
	
	/**
	 * Disable copy
	 */
	public void disableCopy() {
		toolBarOption.copyOption = false;
	}
	
	/**
	 * Enables move
	 */
	public void enableMove() {
		toolBarOption.moveOption = true;
	}
	
	/**
	 * Disable move
	 */
	public void disableMove() {
		toolBarOption.moveOption = false;
	}
	
	/**
	 * Enables export
	 */
	public void enableExport() {
		toolBarOption.exportOption = true;
	}
	
	/**
	 * Disable export
	 */
	public void disableExport() {
		toolBarOption.exportOption = false;
	}
	
	/**
	 * Enables add note
	 */
	public void enableAddNote() {
		toolBarOption.addNoteOption = true;
	}
	
	/**
	 * Disable add note
	 */
	public void disableAddNote() {
		toolBarOption.addNoteOption = false;
	}
	
	
	/**
	 * Only used on developement to testing purposes
	 */
/*	private void enableAllToolBarForTestingPurpose() {
		enableCreateDirectory();
		enableFindFolder();
		enableAddDocument();
		enableCheckout();
		enableCheckin();
		enableCancelCheckout();
		enableLock();
		enableUnlock();
		enableDownload();
		enableDownloadPdf();
		enableDelete();
		enableAddPropertyGroup();
		enableRemovePropertyGroup();
		enableAddSubscription();
		enableRemoveSubscription();
		enableFiredRemovePropertyGroup();
		enableHome();
		enableRefresh();
		enableRename();
		enableCopy();
		enableMove();
		enableExport();
		enableWorkflow();
		enableAddNote();
		enableScanner();
		enableUploader();
	} */

	/**
	 * Gets the defatul Tool Bar object values for root
	 * 
	 * @return The default toolBarOption for init root
	 */
	public ToolBarOption getDefaultRootToolBar() {
		ToolBarOption tmpToolBarOption = new ToolBarOption();
		tmpToolBarOption.createFolderOption				= true;
		tmpToolBarOption.findFolderOption				= true;
		tmpToolBarOption.addDocumentOption 				= true;
		tmpToolBarOption.checkoutOption 				= false;
		tmpToolBarOption.checkinOption 					= false;
		tmpToolBarOption.cancelCheckoutOption 			= false;
		
		tmpToolBarOption.zohoOption 					= false;		//added by vissu on feb27

		
		tmpToolBarOption.lockOption						= false;
		tmpToolBarOption.unLockOption 					= false;
		tmpToolBarOption.downloadOption					= false;
		tmpToolBarOption.downloadPdfOption				= false;
		tmpToolBarOption.deleteOption					= false;
		tmpToolBarOption.addPropertyGroupOption 		= false;
		tmpToolBarOption.removePropertyGroupOption  	= false;
		tmpToolBarOption.addSubscription  				= false;
		tmpToolBarOption.removeSubscription 		 	= false;
		tmpToolBarOption.firedRemovePropertyGroupOption = false;
		tmpToolBarOption.homeOption						= true;
		tmpToolBarOption.refreshOption					= true;
		tmpToolBarOption.renameOption 					= false;
		tmpToolBarOption.copyOption 					= false;
		tmpToolBarOption.moveOption 					= false;
		tmpToolBarOption.exportOption					= false;
		tmpToolBarOption.workflowOption					= false;
		tmpToolBarOption.addNoteOption					= false;
		tmpToolBarOption.scannerOption					= true;
		tmpToolBarOption.uploaderOption					= true;
		return tmpToolBarOption;
	}
	
	/**
	 * Gets the defatul Tool Bar object values for categories
	 * 
	 * @return The default toolBarOption for templates
	 */
	public ToolBarOption getDefaultCategoriesToolBar() {
		ToolBarOption tmpToolBarOption = new ToolBarOption();
		tmpToolBarOption.createFolderOption				= true;
		tmpToolBarOption.findFolderOption				= true;
		tmpToolBarOption.addDocumentOption 				= false;
		tmpToolBarOption.checkoutOption 				= false;
		tmpToolBarOption.checkinOption 					= false;
		tmpToolBarOption.cancelCheckoutOption 			= false;
		
		tmpToolBarOption.zohoOption 					= false;		//added by vissu on feb27
		
		tmpToolBarOption.lockOption						= false;
		tmpToolBarOption.unLockOption 					= false;
		tmpToolBarOption.downloadOption					= false;
		tmpToolBarOption.downloadPdfOption				= false;
		tmpToolBarOption.deleteOption					= false;
		tmpToolBarOption.addPropertyGroupOption 		= false;
		tmpToolBarOption.removePropertyGroupOption  	= false;
		tmpToolBarOption.addSubscription  				= false;
		tmpToolBarOption.removeSubscription 		 	= false;
		tmpToolBarOption.firedRemovePropertyGroupOption = false;
		tmpToolBarOption.homeOption						= false;
		tmpToolBarOption.refreshOption					= true;
		tmpToolBarOption.renameOption 					= false;
		tmpToolBarOption.copyOption 					= false;
		tmpToolBarOption.moveOption 					= false;
		tmpToolBarOption.exportOption					= false;
		tmpToolBarOption.workflowOption					= false;
		tmpToolBarOption.addNoteOption					= false;
		tmpToolBarOption.scannerOption					= false;
		tmpToolBarOption.uploaderOption					= false;
		return tmpToolBarOption;
	}
	
	/**
	 * Gets the defatul Tool Bar object values for thesaurus
	 * 
	 * @return The default toolBarOption for templates
	 */
	public ToolBarOption getDefaultThesaurusToolBar() {
		ToolBarOption tmpToolBarOption = new ToolBarOption();
		tmpToolBarOption.createFolderOption				= false;
		tmpToolBarOption.findFolderOption				= true;
		tmpToolBarOption.addDocumentOption 				= false;
		tmpToolBarOption.checkoutOption 				= false;
		tmpToolBarOption.checkinOption 					= false;
		tmpToolBarOption.cancelCheckoutOption 			= false;
		
		tmpToolBarOption.zohoOption 					= false;		//added by vissu on feb27

		tmpToolBarOption.lockOption						= false;
		tmpToolBarOption.unLockOption 					= false;
		tmpToolBarOption.downloadOption					= false;
		tmpToolBarOption.downloadPdfOption				= false;
		tmpToolBarOption.deleteOption					= false;
		tmpToolBarOption.addPropertyGroupOption 		= false;
		tmpToolBarOption.removePropertyGroupOption  	= false;
		tmpToolBarOption.addSubscription  				= false;
		tmpToolBarOption.removeSubscription 		 	= false;
		tmpToolBarOption.firedRemovePropertyGroupOption = false;
		tmpToolBarOption.homeOption						= false;
		tmpToolBarOption.refreshOption					= true;
		tmpToolBarOption.renameOption 					= false;
		tmpToolBarOption.copyOption 					= false;
		tmpToolBarOption.moveOption 					= false;
		tmpToolBarOption.exportOption					= false;
		tmpToolBarOption.workflowOption					= false;
		tmpToolBarOption.addNoteOption					= false;
		tmpToolBarOption.scannerOption					= false;
		tmpToolBarOption.uploaderOption					= false;
		return tmpToolBarOption;
	}
	
	/**
	 * Gets the defatul Tool Bar object values for trash
	 * 
	 * @return The default toolBarOption for trash
	 */
	public ToolBarOption getDefaultTrashToolBar() {
		ToolBarOption tmpToolBarOption = new ToolBarOption();
		tmpToolBarOption.createFolderOption				= false;
		tmpToolBarOption.findFolderOption				= true;
		tmpToolBarOption.addDocumentOption 				= false;
		tmpToolBarOption.checkoutOption 				= false;
		tmpToolBarOption.checkinOption 					= false;
		tmpToolBarOption.cancelCheckoutOption 			= false;

		tmpToolBarOption.zohoOption 					= false;		//added by vissu on feb27

		tmpToolBarOption.lockOption						= false;
		tmpToolBarOption.unLockOption 					= false;
		tmpToolBarOption.downloadOption					= false;
		tmpToolBarOption.downloadPdfOption				= false;
		tmpToolBarOption.deleteOption					= false;
		tmpToolBarOption.addPropertyGroupOption 		= false;
		tmpToolBarOption.removePropertyGroupOption  	= false;
		tmpToolBarOption.addSubscription  				= false;
		tmpToolBarOption.removeSubscription 		 	= false;
		tmpToolBarOption.firedRemovePropertyGroupOption = false;
		tmpToolBarOption.homeOption						= false;
		tmpToolBarOption.refreshOption					= true;
		tmpToolBarOption.renameOption 					= false;
		tmpToolBarOption.copyOption 					= false;
		tmpToolBarOption.moveOption 					= false;
		tmpToolBarOption.exportOption					= false;
		tmpToolBarOption.workflowOption					= false;
		tmpToolBarOption.addNoteOption					= false;
		tmpToolBarOption.scannerOption					= false;
		tmpToolBarOption.uploaderOption					= false;
		return tmpToolBarOption;
	}
	
	/**
	 * Gets the defatul Tool Bar object values for templates
	 * 
	 * @return The default toolBarOption for templates
	 */
	public ToolBarOption getDefaultTemplatesToolBar() {
		ToolBarOption tmpToolBarOption = new ToolBarOption();
		tmpToolBarOption.createFolderOption				= true;
		tmpToolBarOption.findFolderOption				= true;
		tmpToolBarOption.addDocumentOption 				= true;
		tmpToolBarOption.checkoutOption 				= false;
		tmpToolBarOption.checkinOption 					= false;
		tmpToolBarOption.cancelCheckoutOption 			= false;
		
		tmpToolBarOption.zohoOption 					= false;		//added by vissu on feb27

		tmpToolBarOption.lockOption						= false;
		tmpToolBarOption.unLockOption 					= false;
		tmpToolBarOption.downloadOption					= false;
		tmpToolBarOption.downloadPdfOption				= false;
		tmpToolBarOption.deleteOption					= false;
		tmpToolBarOption.addPropertyGroupOption 		= false;
		tmpToolBarOption.removePropertyGroupOption  	= false;
		tmpToolBarOption.addSubscription  				= false;
		tmpToolBarOption.removeSubscription 		 	= false;
		tmpToolBarOption.firedRemovePropertyGroupOption = false;
		tmpToolBarOption.homeOption						= false;
		tmpToolBarOption.refreshOption					= true;
		tmpToolBarOption.renameOption 					= false;
		tmpToolBarOption.copyOption 					= false;
		tmpToolBarOption.moveOption 					= false;
		tmpToolBarOption.exportOption					= false;
		tmpToolBarOption.workflowOption					= false;
		tmpToolBarOption.addNoteOption					= false;
		tmpToolBarOption.scannerOption					= false;
		tmpToolBarOption.uploaderOption					= false;
		return tmpToolBarOption;
	}
	
	/**
	 * Gets the defatul Tool Bar object values for my documents
	 * 
	 * @return The default toolBarOption for templates
	 */
	public ToolBarOption getDefaultMyDocumentsToolBar() {
		ToolBarOption tmpToolBarOption = new ToolBarOption();
		tmpToolBarOption.createFolderOption				= true;
		tmpToolBarOption.findFolderOption				= true;
		tmpToolBarOption.addDocumentOption 				= true;
		tmpToolBarOption.checkoutOption 				= false;
		tmpToolBarOption.checkinOption 					= false;
		tmpToolBarOption.cancelCheckoutOption 			= false;
		
		tmpToolBarOption.zohoOption 					= false;		//added by vissu on feb27

		tmpToolBarOption.lockOption						= false;
		tmpToolBarOption.unLockOption 					= false;
		tmpToolBarOption.downloadOption					= false;
		tmpToolBarOption.downloadPdfOption				= false;
		tmpToolBarOption.deleteOption					= false;
		tmpToolBarOption.addPropertyGroupOption 		= false;
		tmpToolBarOption.removePropertyGroupOption  	= false;
		tmpToolBarOption.addSubscription  				= false;
		tmpToolBarOption.removeSubscription 		 	= false;
		tmpToolBarOption.firedRemovePropertyGroupOption = false;
		tmpToolBarOption.homeOption						= false;
		tmpToolBarOption.refreshOption					= true;
		tmpToolBarOption.renameOption 					= false;
		tmpToolBarOption.copyOption 					= false;
		tmpToolBarOption.moveOption 					= false;
		tmpToolBarOption.exportOption					= false;
		tmpToolBarOption.workflowOption					= false;
		tmpToolBarOption.addNoteOption					= false;
		tmpToolBarOption.scannerOption					= true;
		tmpToolBarOption.uploaderOption					= true;
		return tmpToolBarOption;
	}
	
	/**
	 * Gets the defatul Tool Bar object values for search
	 * 
	 * @return The default toolBarOption for search
	 */
	public ToolBarOption getDefaultSearchToolBar() {
		ToolBarOption tmpToolBarOption = new ToolBarOption();
		tmpToolBarOption.createFolderOption			= false;
		tmpToolBarOption.findFolderOption				= false;
		tmpToolBarOption.addDocumentOption 				= false;
		tmpToolBarOption.checkoutOption 				= false;
		tmpToolBarOption.checkinOption 					= false;
		tmpToolBarOption.cancelCheckoutOption 			= false;
		
		tmpToolBarOption.zohoOption 					= false;		//added by vissu on feb27

		tmpToolBarOption.lockOption						= false;
		tmpToolBarOption.unLockOption 					= false;
		tmpToolBarOption.downloadOption					= false;
		tmpToolBarOption.downloadPdfOption				= false;
		tmpToolBarOption.deleteOption					= false;
		tmpToolBarOption.addPropertyGroupOption 		= false;
		tmpToolBarOption.removePropertyGroupOption  	= false;
		tmpToolBarOption.addSubscription  				= false;
		tmpToolBarOption.removeSubscription 		 	= false;
		tmpToolBarOption.firedRemovePropertyGroupOption = false;
		tmpToolBarOption.homeOption						= false;
		tmpToolBarOption.refreshOption					= false;
		tmpToolBarOption.renameOption 					= false;
		tmpToolBarOption.copyOption 					= false;
		tmpToolBarOption.moveOption 					= false;
		tmpToolBarOption.exportOption					= false;
		tmpToolBarOption.workflowOption					= false;
		tmpToolBarOption.addNoteOption					= false;
		tmpToolBarOption.scannerOption					= false;
		tmpToolBarOption.uploaderOption					= false;
		return tmpToolBarOption;
	}
	
	/**
	 * Gets the defatul Tool Bar object values for dashboard
	 * 
	 * @return The default toolBarOption for search
	 */
	public ToolBarOption getDefaultDashboardToolBar() {
		ToolBarOption tmpToolBarOption = new ToolBarOption();
		tmpToolBarOption.createFolderOption				= false;
		tmpToolBarOption.findFolderOption				= false;
		tmpToolBarOption.addDocumentOption 				= false;
		tmpToolBarOption.checkoutOption 				= false;
		tmpToolBarOption.checkinOption 					= false;
		tmpToolBarOption.cancelCheckoutOption 			= false;
		
		tmpToolBarOption.zohoOption 					= false;		//added by vissu on feb27

		tmpToolBarOption.lockOption						= false;
		tmpToolBarOption.unLockOption 					= false;
		tmpToolBarOption.downloadOption					= false;
		tmpToolBarOption.downloadPdfOption				= false;
		tmpToolBarOption.deleteOption					= false;
		tmpToolBarOption.addPropertyGroupOption 		= false;
		tmpToolBarOption.removePropertyGroupOption  	= false;
		tmpToolBarOption.addSubscription  				= false;
		tmpToolBarOption.removeSubscription 		 	= false;
		tmpToolBarOption.firedRemovePropertyGroupOption = false;
		tmpToolBarOption.homeOption						= false;
		tmpToolBarOption.refreshOption					= true;
		tmpToolBarOption.renameOption 					= false;
		tmpToolBarOption.copyOption 					= false;
		tmpToolBarOption.moveOption 					= false;
		tmpToolBarOption.exportOption					= false;
		tmpToolBarOption.workflowOption					= false;
		tmpToolBarOption.addNoteOption					= false;
		tmpToolBarOption.scannerOption					= false;
		tmpToolBarOption.uploaderOption					= false;
		return tmpToolBarOption;
	}
	
	/**
	 * Gets the defatul Tool Bar object values for administration
	 * 
	 * @return The default toolBarOption for search
	 */
	public ToolBarOption getDefaultAdministrationToolBar() {
		ToolBarOption tmpToolBarOption = new ToolBarOption();
		tmpToolBarOption.createFolderOption				= false;
		tmpToolBarOption.findFolderOption				= false;
		tmpToolBarOption.addDocumentOption 				= false;
		tmpToolBarOption.checkoutOption 				= false;
		tmpToolBarOption.checkinOption 					= false;
		tmpToolBarOption.cancelCheckoutOption 			= false;
		
		tmpToolBarOption.zohoOption 					= false;		//added by vissu on feb27

		tmpToolBarOption.lockOption						= false;
		tmpToolBarOption.unLockOption 					= false;
		tmpToolBarOption.downloadOption					= false;
		tmpToolBarOption.downloadPdfOption				= false;
		tmpToolBarOption.deleteOption					= false;
		tmpToolBarOption.addPropertyGroupOption 		= false;
		tmpToolBarOption.removePropertyGroupOption  	= false;
		tmpToolBarOption.addSubscription  				= false;
		tmpToolBarOption.removeSubscription 		 	= false;
		tmpToolBarOption.firedRemovePropertyGroupOption = false;
		tmpToolBarOption.homeOption						= false;
		tmpToolBarOption.refreshOption					= false;
		tmpToolBarOption.renameOption 					= false;
		tmpToolBarOption.copyOption 					= false;
		tmpToolBarOption.moveOption 					= false;
		tmpToolBarOption.exportOption					= false;
		tmpToolBarOption.workflowOption					= false;
		tmpToolBarOption.addNoteOption					= false;
		tmpToolBarOption.scannerOption					= false;
		tmpToolBarOption.uploaderOption					= false;
		return tmpToolBarOption;
	}
	
	/**
	 * Gets the defatul Tool Bar object values for extensions
	 * 
	 * @return The default toolBarOption for search
	 */
	public ToolBarOption getDefaultExtensionsToolBar() {
		ToolBarOption tmpToolBarOption = new ToolBarOption();
		tmpToolBarOption.createFolderOption				= false;
		tmpToolBarOption.findFolderOption				= false;
		tmpToolBarOption.addDocumentOption 				= false;
		tmpToolBarOption.checkoutOption 				= false;
		tmpToolBarOption.checkinOption 					= false;
		tmpToolBarOption.cancelCheckoutOption 			= false;
		
		tmpToolBarOption.zohoOption 					= false;		//added by vissu on feb27

		tmpToolBarOption.lockOption						= false;
		tmpToolBarOption.unLockOption 					= false;
		tmpToolBarOption.downloadOption					= false;
		tmpToolBarOption.downloadPdfOption				= false;
		tmpToolBarOption.deleteOption					= false;
		tmpToolBarOption.addPropertyGroupOption 		= false;
		tmpToolBarOption.removePropertyGroupOption  	= false;
		tmpToolBarOption.addSubscription  				= false;
		tmpToolBarOption.removeSubscription 		 	= false;
		tmpToolBarOption.firedRemovePropertyGroupOption = false;
		tmpToolBarOption.homeOption						= false;
		tmpToolBarOption.refreshOption					= false;
		tmpToolBarOption.renameOption 					= false;
		tmpToolBarOption.copyOption 					= false;
		tmpToolBarOption.moveOption 					= false;
		tmpToolBarOption.exportOption					= false;
		tmpToolBarOption.workflowOption					= false;
		tmpToolBarOption.addNoteOption					= false;
		tmpToolBarOption.scannerOption					= false;
		tmpToolBarOption.uploaderOption					= false;
		return tmpToolBarOption;
	}
	
	/**
	 * Evalues show Icons based on toolBarOption values
	 * 
	 */
	public void evaluateShowIcons() {
		if (toolBarOption.createFolderOption) {enableCreateDirectory(); } else {disableCreateDirectory(); }
		if (toolBarOption.findFolderOption) {enableFindFolder(); } else {disableFindFolder(); }
		if (toolBarOption.addDocumentOption) {enableAddDocument(); } else {disableAddDocument(); }
		if (toolBarOption.checkoutOption) { enableCheckout(); } else { disableCheckout(); }
		if (toolBarOption.checkinOption) { enableCheckin(); } else { disableCheckin(); }
		if (toolBarOption.cancelCheckoutOption) { enableCancelCheckout(); } else { disableCancelCheckout(); }
		if (toolBarOption.lockOption) { enableLock(); } else { disableLock();}
		if (toolBarOption.unLockOption) { enableUnlock(); } else { disableUnlock(); }
		if (toolBarOption.downloadOption) { enableDownload(); } else { disableDownload(); }
		if (toolBarOption.downloadPdfOption) { enableDownloadPdf(); } else { disableDownloadPdf(); }
		if (toolBarOption.deleteOption) { enableDelete(); } else { disableDelete(); }
		if (toolBarOption.addPropertyGroupOption) { enableAddPropertyGroup(); } else { disableAddPropertyGroup(); }
		
		//added by vissu on feb27
		if (toolBarOption.zohoOption) {	enableZoho(); }  else { disableZoho(); }
		
		
		// Special case removePropertyGroupOption is only evaluated on TabDocument tab changing by evaluateRemoveGroupProperty method
		if (!toolBarOption.removePropertyGroupOption) { // We evaluate for changing panel desktop / search ( only disable option )
			removePropertyGroup.setStyleName("okm-ToolBar-button-disabled");
			removePropertyGroup.setResource(OKMBundleResources.INSTANCE.removePropertyGroupDisabled());
			removePropertyGroup.setTitle(Main.i18n("filebrowser.menu.remove.property.group"));
		}
		
		if (toolBarOption.workflowOption) { enableWorkflow(); } else { disableWorkflow();}
		if (toolBarOption.addSubscription) { enableAddSubscription(); } else { disableAddSubscription(); }
		if (toolBarOption.removeSubscription){ enableRemoveSubscription(); } else { disableRemoveSubscription(); }
		if (toolBarOption.homeOption){ enableHome(); } else { disableHome(); }
		if (toolBarOption.refreshOption){ enableRefresh(); } else { disableRefresh(); }
		if (toolBarOption.scannerOption){ enableScanner(); } else { disableScanner(); }
		if (toolBarOption.uploaderOption){ enableUploader(); } else { disableUploader(); }
		
		// Checking extension button 
		for (Iterator<ToolBarButtonExtension> it = widgetExtensionList.iterator(); it.hasNext();) {
			ToolBarButtonExtension button = it.next();
			button.enable(button.isEnabled());
		}
	}
	
	/**
	 * Evaluate the remove group property
	 * 
	 * @param propertyGroupEnabled
	 */
	public void evaluateRemoveGroupProperty(boolean propertyGroupEnabled) {
		// Show or hide removeGroupProperty depends on two cases, the property is enabled by security user and
		// must be one tab group selected
		
		// We save to used on changing language
		this.propertyGroupEnabled = propertyGroupEnabled;
		
		// Sets fired property
		if (propertyGroupEnabled) {
			enableFiredRemovePropertyGroup();
		} else {
			disableFiredRemovePropertyGroup();
		}
		
		// Show or hides button
		if (toolBarOption.removePropertyGroupOption && toolBarOption.firedRemovePropertyGroupOption) {
			removePropertyGroup.setStyleName("okm-ToolBar-button");
			removePropertyGroup.setResource(OKMBundleResources.INSTANCE.removePropertyGroup());
			removePropertyGroup.setTitle(Main.i18n("filebrowser.menu.remove.property.group"));
		} else {
			removePropertyGroup.setStyleName("okm-ToolBar-button-disabled");
			removePropertyGroup.setResource(OKMBundleResources.INSTANCE.removePropertyGroupDisabled());
			removePropertyGroup.setTitle(Main.i18n("filebrowser.menu.remove.property.group"));
		}
	}
	
	/**
	 * Save changes to the actual view
	 * Must be called after mainPanel actual view is changed
	 */
	public void changeView(int view, int newMainPanelView) {
		boolean toolBarEnabled = true;
		int mainPanelView = Main.get().mainPanel.getActualView();
		
		// Evaluates actual desktop view to put values
		switch(mainPanelView){
			case UIDockPanelConstants.DESKTOP:
				// Saves actual view values on hashMap
				switch (actualView) {
					case UIDesktopConstants.NAVIGATOR_TAXONOMY:
						viewValues.put("view_root:option", toolBarOption);
						break;
						
					case UIDesktopConstants.NAVIGATOR_CATEGORIES:
						viewValues.put("view_categories:option", toolBarOption);
						break;
						
					case UIDesktopConstants.NAVIGATOR_THESAURUS:
						viewValues.put("view_thesaurus:option", toolBarOption);
						break;
						
					case UIDesktopConstants.NAVIGATOR_TRASH:
						viewValues.put("view_trash:option", toolBarOption);
						break;
						
					case UIDesktopConstants.NAVIGATOR_TEMPLATES:
						viewValues.put("view_templates:option", toolBarOption);
						break;
					
					case UIDesktopConstants.NAVIGATOR_PERSONAL:
						viewValues.put("view_my_documents:option", toolBarOption);
						break;
					
					case UIDesktopConstants.NAVIGATOR_MAIL:
						viewValues.put("view_mail:option", toolBarOption);
						break;
				}
				break;
				
			case UIDockPanelConstants.SEARCH:
				viewValues.put("view_search:option", toolBarOption);
				break;
				
			case UIDockPanelConstants.DASHBOARD:
				viewValues.put("view_dashboard:option", toolBarOption);
				break;
				
			case UIDockPanelConstants.ADMINISTRATION:
				viewValues.put("view_administration:option", toolBarOption);
				break;
				
			case UIDockPanelConstants.EXTENSIONS:
				viewValues.put("view_extension:option", toolBarOption);
				break;
		}
		
		// Evaluates new desktop view to restore values 
		switch(newMainPanelView){
			case UIDockPanelConstants.DESKTOP:
				switch (view) {
					case UIDesktopConstants.NAVIGATOR_TAXONOMY:
						if (viewValues.containsKey("view_root:option")){
							toolBarOption = (ToolBarOption) viewValues.get("view_root:option");
						}
						toolBarEnabled = true;
						break;
						
					case UIDesktopConstants.NAVIGATOR_CATEGORIES:
						if (viewValues.containsKey("view_categories:option")){
							toolBarOption = (ToolBarOption) viewValues.get("view_categories:option");
						} else {
							toolBarOption = getDefaultCategoriesToolBar();
						}
						toolBarEnabled = true;
						break;
					
					case UIDesktopConstants.NAVIGATOR_THESAURUS:
						if (viewValues.containsKey("view_thesaurus:option")){
							toolBarOption = (ToolBarOption) viewValues.get("view_thesaurus:option");
						} else {
							toolBarOption = getDefaultThesaurusToolBar();
						}
						toolBarEnabled = true;
						break;
						
					case UIDesktopConstants.NAVIGATOR_TRASH:
						if (viewValues.containsKey("view_trash:option")){
							toolBarOption = (ToolBarOption) viewValues.get("view_trash:option");
						} else {
							toolBarOption = getDefaultTrashToolBar();
						}
						toolBarEnabled = false;
						break;
					
					case UIDesktopConstants.NAVIGATOR_TEMPLATES:
						if (viewValues.containsKey("view_templates:option")){
							toolBarOption = (ToolBarOption) viewValues.get("view_templates:option");
						} else {
							toolBarOption = getDefaultTemplatesToolBar();
						}
						toolBarEnabled = true;
						break;
					
					case UIDesktopConstants.NAVIGATOR_PERSONAL:
						if (viewValues.containsKey("view_my_documents:option")){
							toolBarOption = (ToolBarOption) viewValues.get("view_my_documents:option");
						} else {
							toolBarOption = getDefaultMyDocumentsToolBar();
						}
						toolBarEnabled = true;
						break;
				}
				break;
				
			case UIDockPanelConstants.SEARCH:
				if (viewValues.containsKey("view_search:option")){
					toolBarOption = (ToolBarOption) viewValues.get("view_search:option");
				} else {
					toolBarOption = getDefaultSearchToolBar();
				}
				toolBarEnabled = false;
				break;
			
			case UIDockPanelConstants.DASHBOARD:
				if (viewValues.containsKey("view_dashboard:option")){
					toolBarOption = (ToolBarOption) viewValues.get("view_dashboard:option");
				} else {
					toolBarOption = getDefaultDashboardToolBar();
				}
				toolBarEnabled = false;
				break;
				
			case UIDockPanelConstants.ADMINISTRATION:
				if (viewValues.containsKey("view_administration:option")){
					toolBarOption = (ToolBarOption) viewValues.get("view_administration:option");
				} else {
					toolBarOption = getDefaultAdministrationToolBar();
				}
				toolBarEnabled = false;
				break;
				
			case UIDockPanelConstants.EXTENSIONS:
				if (viewValues.containsKey("view_extension:option")){
					toolBarOption = (ToolBarOption) viewValues.get("view_extension:option");
				} else {
					toolBarOption = getDefaultExtensionsToolBar();
				}
				toolBarEnabled = false;
				break;

		}
		
		// Enables  before evaluate show icons, order is important because can evaluate
		// icons if enabled is false always before evaluate icons must be enabled
		enabled = true;
		evaluateShowIcons(); // Evalues icons to show
		enabled = toolBarEnabled; 
		actualView = view;   // Sets the new view active
		
		// Sets the permission to main menu
		Main.get().mainPanel.topPanel.mainMenu.setOptions(toolBarOption);
		
		fireEvent(HasToolBarEvent.EXECUTE_CHANGED_VIEW);
	}
	
	/**
	 * Call back opens document passed by url param
	 */
	final AsyncCallback<Boolean> callbackIsValidDocument = new AsyncCallback<Boolean>() {
		public void onSuccess(Boolean result){
			if (result.booleanValue()) {
				// Opens folder passed by parameter
				String path = Main.get().userHome.getHomePath().substring(0,Main.get().userHome.getHomePath().lastIndexOf("/"));
				Main.get().activeFolderTree.openAllPathFolder(path,Main.get().userHome.getHomePath());
			}
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("isValid", caught);
		}
	};
	
	/**
	 * Call back opens folder passed by url param
	 */
	final AsyncCallback<Boolean> callbackIsValidFolder = new AsyncCallback<Boolean>() {
		public void onSuccess(Boolean result){			
			if (result.booleanValue()) {
				// Opens document passed by parameter
				Main.get().activeFolderTree.openAllPathFolder(Main.get().userHome.getHomePath(),"");
			}
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("isValid", caught);
		}
	};
	
	
	
	/**
	 * Gets asyncronous to get all groups
	 */
	final AsyncCallback<List<GWTPropertyGroup>> callbackGetAllGroups = new AsyncCallback<List<GWTPropertyGroup>>() {
		public void onSuccess(List<GWTPropertyGroup> result){
			// List of groups to be added
			if (!result.isEmpty()) {
				enableAddPropertyGroup();
			} else {
				disableAddPropertyGroup();
			}
		}

		public void onFailure(Throwable caught) {
			disableAddPropertyGroup();
			Main.get().showError("GetAllGroups", caught);
		}
	};
	
	/**
	 * Gets all property groups
	 */
	private void getAllGroups(Object node) {
		this.node = node;
		String path = getActualNodePath();
		
		if (!path.equals("")) {
			//below line commented by vissu on may10
			//propertyGroupService.getAllGroups(path, callbackGetAllGroups);
		}
	}
	
	/**
	 * @return
	 */
	public String getActualNodePath() {
		String path = "";
		if (node instanceof GWTDocument) {
			path = ((GWTDocument) node).getPath();
		} else if (node instanceof GWTFolder) {
			path = ((GWTFolder) node).getPath();
		} else if (node instanceof GWTMail) {
			path = ((GWTMail) node).getPath();
		} 
		return path;
	}
	
	/**
	 * getActualNode
	 * 
	 * @return
	 */
	public Object getActualNode() {
		return node;
	}
	
	/**
	 * isNodeDocument
	 * 
	 * @return
	 */
	public boolean isNodeDocument() {
		return (node!=null && node instanceof GWTDocument);
	}
	
	/**
	 * isNodeFolder
	 * 
	 * @return
	 */
	public boolean isNodeFolder() {
		return (node!=null && node instanceof GWTFolder);
	}
	
	/**
	 * isNodeMail
	 * 
	 * @return
	 */
	public boolean isNodeMail() {
		return (node!=null && node instanceof GWTMail);
	}
	
	/**
	 * Sets the user home
	 * 
	 * @param user
	 * @param Uuid
	 * @param path
	 * @param type
	 */
	public void setUserHome(String user, String Uuid, String path, String type){
		Main.get().userHome.setHomeUuid(Uuid);
		Main.get().userHome.setUser(user);
		Main.get().userHome.setHomePath(path);
		Main.get().userHome.setHomeType(type);
		fireEvent(HasToolBarEvent.EXECUTE_SET_USER_HOME);
	}

	/**
	 * Create html scanner applet code 
	 */
	public void setScannerApplet(String token, String path) {
		if (Util.isJREInstalled()) {
			Widget scannerApplet = RootPanel.get("scannerApplet");
			scannerApplet.setSize("1", "1");
			panel.add(scannerApplet);
			scannerApplet.getElement().setInnerHTML("<applet code=\"com.openkm.applet.Scanner\" name=\"Scanner\" width=\"1\" height=\"1\" mayscript archive=\"../scanner.jar\">"+
					"<param name=\"token\" value=\""+token+"\">"+
					"<param name=\"path\" value=\""+path+"\">"+
					"<param name=\"lang\" value=\""+Main.get().getLang()+"\">"+
					"</applet>");
		} else {
			Main.get().showError("setScannerApplet", new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBrowser, ErrorCode.CAUSE_Configuration), "JRE support not detected in your browser"));
		}
	}
	
	/**
	 * Create html uploader applet code 
	 */
	public void setUploaderApplet(String token, String path) {
		if (Util.isJREInstalled()) {
			Widget uploaderApplet = RootPanel.get("uploaderApplet");
			uploaderApplet.setSize("1", "1");
			panel.add(uploaderApplet);
			uploaderApplet.getElement().setInnerHTML("<applet code=\"com.openkm.applet.Uploader\" name=\"Uploader\" width=\"1\" height=\"1\" mayscript archive=\"../uploader.jar\">"+
					"<param name=\"token\" value=\""+token+"\">"+
					"<param name=\"path\" value=\""+path+"\">"+
					"<param name=\"lang\" value=\""+Main.get().getLang()+"\">"+
					"</applet>");
		} else {
			Main.get().showError("setUploaderApplet", new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBrowser, ErrorCode.CAUSE_Configuration), "JRE support not detected in your browser"));
		}
	}

	/**
	 * destroyScannerApplet
	 */
	public void destroyScannerApplet() {
		Widget scannerApplet = RootPanel.get("scannerApplet");
		panel.remove(scannerApplet);
		scannerApplet.getElement().setInnerHTML("");
	}
	
	/**
	 * destroyUploaderApplet
	 */
	public void destroyUploaderApplet() {
		Widget uploadApplet = RootPanel.get("uploaderApplet");
		panel.remove(uploadApplet);
		uploadApplet.getElement().setInnerHTML("");
	}
	
	/**
	 * Lang refresh
	 */
	public void langRefresh() {
		evaluateShowIcons();
		evaluateRemoveGroupProperty(propertyGroupEnabled);
	}
	
	/**
	 * Gets the tool bar option
	 * 
	 * @return The actual toolBar Option
	 */
	public ToolBarOption getToolBarOption() {
		return toolBarOption;
	}
	
	/**
	 * setAvailableOption
	 * 
	 * @param option
	 */
	public void setAvailableOption(GWTAvailableOption option) {
		// FIRST
		createFolder.setVisible(option.isCreateFolderOption());
		panel.getWidget(2).setVisible(option.isCreateFolderOption()); // Hide space
		findFolder.setVisible(option.isCreateFolderOption());
		panel.getWidget(4).setVisible(option.isCreateFolderOption()); // Hide space
		download.setVisible(option.isDownloadOption());
		panel.getWidget(6).setVisible(option.isDownloadOption()); // hide space
		downloadPdf.setVisible(option.isDownloadPdfOption());
		panel.getWidget(8).setVisible(option.isDownloadPdfOption()); // hide space
		panel.getWidget(9).setVisible(option.isCreateFolderOption() || option.isFindFolderOption() ||
					                  option.isDownloadOption() || option.isDownloadPdfOption()); // hide separator
 
		
		// SECOND
		lock.setVisible(option.isLockOption());
		panel.getWidget(11).setVisible(option.isLockOption()); // hide space
		unlock.setVisible(option.isUnLockOption());
		panel.getWidget(13).setVisible(option.isUnLockOption()); // hide space
		panel.getWidget(14).setVisible(option.isLockOption() || option.isUnLockOption()); // hide separator
		
		// THIRD
		addDocument.setVisible(option.isAddDocumentOption());
		panel.getWidget(16).setVisible(option.isAddDocumentOption()); // hide space
		checkout.setVisible(option.isCheckoutOption());
		panel.getWidget(18).setVisible(option.isCheckoutOption()); // hide space
		checkin.setVisible(option.isCheckinOption());
		panel.getWidget(20).setVisible(option.isCheckinOption()); // hide space
		cancelCheckout.setVisible(option.isCancelCheckoutOption());
		panel.getWidget(22).setVisible(option.isCancelCheckoutOption()); // hide space
		delete.setVisible(option.isDeleteOption());
		panel.getWidget(24).setVisible(option.isDeleteOption()); // hide space
		panel.getWidget(25).setVisible(option.isAddDocumentOption() || option.isCheckoutOption() || 
									   option.isCheckinOption() || option.isCancelCheckoutOption() || 
									   option.isDeleteOption()); // hide separator
		
		// FOURTH
		addPropertyGroup.setVisible(option.isAddPropertyGroupOption());
		panel.getWidget(27).setVisible(option.isAddPropertyGroupOption()); // hide space
		removePropertyGroup.setVisible(option.isRemovePropertyGroupOption());
		panel.getWidget(29).setVisible(option.isRemovePropertyGroupOption()); // hide space
		panel.getWidget(30).setVisible(option.isAddPropertyGroupOption() || option.isRemovePropertyGroupOption()); // hide separator
		
		// FIFTH
		startWorkflow.setVisible(option.isWorkflowOption());
		panel.getWidget(32).setVisible(option.isWorkflowOption()); // hide space
		panel.getWidget(33).setVisible(option.isWorkflowOption()); // hide separator
		
		// SIXTH
		addSubscription.setVisible(option.isAddSubscription());
		panel.getWidget(35).setVisible(option.isAddSubscription()); // hide space
		removeSubscription.setVisible(option.isRemoveSubscription());
		panel.getWidget(37).setVisible(option.isRemoveSubscription()); // hide space
		panel.getWidget(38).setVisible(option.isAddSubscription() || option.isRemoveSubscription()); // hide separator
		
		// SEVENTH 
		home.setVisible(option.isHomeOption());
		panel.getWidget(40).setVisible(option.isHomeOption()); // hide space
		refresh.setVisible(option.isRefreshOption());
		panel.getWidget(42).setVisible(option.isRefreshOption()); // hide space
		panel.getWidget(43).setVisible(option.isHomeOption() || option.isRefreshOption()); // hide separator
		
		scanner.setVisible(option.isScannerOption());
		panel.getWidget(45).setVisible(option.isScannerOption()); // hide space
		uploader.setVisible(option.isUploaderOption());
		panel.getWidget(47).setVisible(option.isUploaderOption()); // hide space
		
		//added by vissu on feb27
		zoho.setVisible(option.isZohoOption());
		panel.getWidget(49).setVisible(option.isZohoOption()); // hide space
	}
	
	/**
	 * addToolBarButton
	 * 
	 * @param extension
	 */
	public void addToolBarButtonExtension(ToolBarButtonExtension extension) {
		extension.addMouseOverHandler(mouseOverHandler);
		extension.addMouseOutHandler(mouseOutHandler);
		extension.setStyleName("okm-ToolBar-button");
		widgetExtensionList.add(extension);
		panel.add(extension);
	}
	
	@Override
	public void addToolBarHandlerExtension(ToolBarHandlerExtension handlerExtension) {
		toolBarHandlerExtensionList.add(handlerExtension);
	}
	
	@Override
	public void fireEvent(ToolBarEventConstant event) {
		for (Iterator<ToolBarHandlerExtension> it = toolBarHandlerExtensionList.iterator(); it.hasNext();) {
			it.next().onChange(event);
		}
	}
	
	/**
	 * initJavaScriptApi
	 * 
	 * @param toolBar
	 */
	public native void initJavaScriptApi(ToolBar toolBar) /*-{
	    $wnd.destroyScannerApplet = toolBar.@com.openkm.frontend.client.widget.toolbar.ToolBar::destroyScannerApplet();
	    $wnd.destroyUploaderApplet = toolBar.@com.openkm.frontend.client.widget.toolbar.ToolBar::destroyUploaderApplet();
	    $wnd.refreshFolder = toolBar.@com.openkm.frontend.client.widget.toolbar.ToolBar::executeRefresh();
	}-*/;
}
