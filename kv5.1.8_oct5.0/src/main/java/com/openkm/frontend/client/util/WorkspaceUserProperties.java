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

package com.openkm.frontend.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTWorkspace;
import com.openkm.frontend.client.contants.service.RPCService;
import com.openkm.frontend.client.service.OKMRepositoryService;
import com.openkm.frontend.client.service.OKMRepositoryServiceAsync;
import com.openkm.frontend.client.service.OKMWorkspaceService;
import com.openkm.frontend.client.service.OKMWorkspaceServiceAsync;
import com.openkm.frontend.client.widget.startup.StartUp;

/**
 * Workspace user properties
 * 
 * @author jllort
 *
 */
public class WorkspaceUserProperties {
	
	private final OKMRepositoryServiceAsync repositoryService = (OKMRepositoryServiceAsync) GWT.create(OKMRepositoryService.class);
	private final OKMWorkspaceServiceAsync workspaceService = (OKMWorkspaceServiceAsync) GWT.create(OKMWorkspaceService.class);
	
	private GWTWorkspace workspace;
	private String user = "";
	private String applicationURL = "";
	private String msg = "";
	
	/**
	 * Workspace user properties
	 */
	public WorkspaceUserProperties () {
	}
	
	/**
	 * First time inits workspace
	 */
	public void init() {
		getUserWorkspace();
	}
	
	/**
	 * Call back to get remote update message
	 */
	final AsyncCallback<String> callbackGetUpdateMessage = new AsyncCallback<String>() {
		public void onSuccess(String result) {
			msg = result;
			Main.get().mainPanel.bottomPanel.userInfo.setUpdateMessage(msg);
		}

		public void onFailure(Throwable caught){
			Main.get().showError("RemoteUser", caught);
		}
	};
	
	/**
	 * Call back to get workspace user data
	 */
	final AsyncCallback<GWTWorkspace> callbackGetUserWorkspace = new AsyncCallback<GWTWorkspace>() {
		public void onSuccess(GWTWorkspace result) {
			workspace = result;
			user = result.getUser();
			applicationURL = result.getApplicationURL();
			
			// Changing the web skin
			Util.changeCss(workspace.getWebSkin());
			
			Main.get().mainPanel.bottomPanel.userInfo.setUser(user, result.isTabAdminVisible());
			if (result.isChatEnabled()) {
				Main.get().mainPanel.bottomPanel.userInfo.enableChat();
				if (result.isChatAutoLogin()) {
					Main.get().mainPanel.bottomPanel.userInfo.loginChat();
				}
			}
			if (result.isUserQuotaEnabled() && result.getUserQuotaLimit() > 0) {
				Main.get().mainPanel.bottomPanel.userInfo.enableUserQuota(workspace.getUserQuotaLimit());
			}
			Main.get().mainPanel.bottomPanel.userInfo.showExtensions();
			//Main.get().aboutPopup.setAppVersion(result.getAppVersion());	//commented by vissu on feb18
			getUserDocumentsSize(); // Refreshing user document size ( here is yet set userQuota limit )
			
			// Starting schedulers
			Main.get().startUp.startKeepAlive(workspace.getKeepAliveSchedule());
			Main.get().mainPanel.dashboard.startRefreshingDashboard(workspace.getDashboardSchedule());
			
			// Enabling advanced filters
			if (workspace.isAdvancedFilters()) {
				Main.get().securityPopup.enableAdvancedFilter();
				Main.get().fileUpload.enableAdvancedFilter();
				Main.get().notifyPopup.enableAdvancedFilter();
			}
			
			// Show / hide menus
			Main.get().mainPanel.topPanel.mainMenu.setFileMenuVisible(workspace.isMenuFileVisible());
			Main.get().mainPanel.topPanel.mainMenu.setEditMenuVisible(workspace.isMenuEditVisible());
			Main.get().mainPanel.topPanel.mainMenu.setToolsMenuVisible(workspace.isMenuToolsVisible());
			Main.get().mainPanel.topPanel.mainMenu.setBookmarkMenuVisible(workspace.isMenuBookmarksVisible());
			Main.get().mainPanel.topPanel.mainMenu.setHelpMenuVisible(workspace.isMenuHelpVisible());
			
			// Init available languages
			Main.get().mainPanel.topPanel.mainMenu.initAvailableLanguage(workspace.getLangs());
			
			// Enabling / disabling some actions
			Main.get().mainPanel.topPanel.toolBar.setAvailableOption(workspace.getAvailableOption());
			
			// Showing tabs
			boolean refreshTab = false;
			if (workspace.isTabDesktopVisible()) {
				Main.get().mainPanel.topPanel.tabWorkspace.showDesktop();
				refreshTab = true;
			}
			if (workspace.isTabSearchVisible()) {
				Main.get().mainPanel.topPanel.tabWorkspace.showSearh();
				refreshTab = true;
			}
			if (workspace.isTabDashboardVisible()) {
				Main.get().mainPanel.topPanel.tabWorkspace.showDashboard();
				refreshTab = true;
			}
			if (result.isTabAdminVisible()) {
				Main.get().mainPanel.topPanel.mainMenu.administration.setVisible(true);
				Main.get().mainPanel.topPanel.tabWorkspace.showAdministration();
				refreshTab = true;
			}
			refreshTab = Main.get().mainPanel.topPanel.tabWorkspace.showExtensionTabs() || refreshTab;
			if (refreshTab) {
				Main.get().mainPanel.topPanel.tabWorkspace.init();
			}
			
			
			// showing stack
			boolean refreshStack = false;
			if (workspace.isStackTaxonomy()) {
				Main.get().mainPanel.desktop.navigator.showTaxonomy();
				refreshStack = true;
			}
			if (workspace.isStackCategoriesVisible()) {
				Main.get().mainPanel.desktop.navigator.showCategories();
				refreshStack = true;
			}
			if (workspace.isStackThesaurusVisible()) {
				Main.get().mainPanel.desktop.navigator.showThesaurus();
				refreshStack = true;
			}
			if (workspace.isStackTemplatesVisible()) {
				Main.get().mainPanel.desktop.navigator.showTemplates();
				Main.get().mainPanel.desktop.navigator.taxonomyTree.folderSelectPopup.showTemplates();
				Main.get().mainPanel.desktop.navigator.categoriesTree.folderSelectPopup.showTemplates();
				Main.get().mainPanel.desktop.navigator.thesaurusTree.folderSelectPopup.showTemplates();
				Main.get().mainPanel.desktop.navigator.personalTree.folderSelectPopup.showTemplates();
				Main.get().mainPanel.desktop.navigator.templateTree.folderSelectPopup.showTemplates();
				Main.get().mainPanel.desktop.navigator.mailTree.folderSelectPopup.showTemplates();
				Main.get().mainPanel.desktop.navigator.trashTree.folderSelectPopup.showTemplates();
				Main.get().mainPanel.dashboard.keyMapDashboard.showTemplates();
				Main.get().mainPanel.search.searchBrowser.searchIn.showTemplates();
				refreshStack = true;
			}
			if (workspace.isStackPersonalVisible()) {
				Main.get().mainPanel.desktop.navigator.showPersonal();
				Main.get().mainPanel.desktop.navigator.taxonomyTree.folderSelectPopup.showPersonal();
				Main.get().mainPanel.desktop.navigator.categoriesTree.folderSelectPopup.showPersonal();
				Main.get().mainPanel.desktop.navigator.thesaurusTree.folderSelectPopup.showPersonal();
				Main.get().mainPanel.desktop.navigator.personalTree.folderSelectPopup.showPersonal();
				Main.get().mainPanel.desktop.navigator.templateTree.folderSelectPopup.showPersonal();
				Main.get().mainPanel.desktop.navigator.mailTree.folderSelectPopup.showPersonal();
				Main.get().mainPanel.desktop.navigator.trashTree.folderSelectPopup.showPersonal();
				Main.get().mainPanel.dashboard.keyMapDashboard.showPersonal();
				Main.get().mainPanel.search.searchBrowser.searchIn.showPersonal();
				refreshStack = true;
			}
			if (workspace.isStackMailVisible()) {
				Main.get().mainPanel.desktop.navigator.showMail();
				Main.get().mainPanel.desktop.navigator.taxonomyTree.folderSelectPopup.showMail();
				Main.get().mainPanel.desktop.navigator.categoriesTree.folderSelectPopup.showMail();
				Main.get().mainPanel.desktop.navigator.thesaurusTree.folderSelectPopup.showMail();
				Main.get().mainPanel.desktop.navigator.personalTree.folderSelectPopup.showMail();
				Main.get().mainPanel.desktop.navigator.templateTree.folderSelectPopup.showMail();
				Main.get().mainPanel.desktop.navigator.mailTree.folderSelectPopup.showMail();
				Main.get().mainPanel.desktop.navigator.trashTree.folderSelectPopup.showMail();
				Main.get().mainPanel.dashboard.keyMapDashboard.showMail();
				Main.get().mainPanel.search.searchBrowser.searchIn.showMail();
				refreshStack = true;
			}
			if (workspace.isStackTrashVisible()) {
				Main.get().mainPanel.desktop.navigator.showTrash();
				Main.get().mainPanel.dashboard.keyMapDashboard.showTrash();
				Main.get().mainPanel.search.searchBrowser.searchIn.showTrash();
				refreshStack = true;
			}
			if (refreshStack) {
				Main.get().mainPanel.desktop.navigator.refreshView();
			}
			
			// Documents tabs
			if (workspace.isTabDocumentPropertiesVisible()) {
				Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.showDocument();
			}
			if (workspace.isTabDocumentNotesVisible()) {
				Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.showNotes();
			}
			if (workspace.isTabDocumentSecurityVisible()) {
				Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.showSecurity();
			}
			if (workspace.isTabDocumentVersionVisible()) {
				Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.showVersion();
			}
			if (workspace.isTabDocumentPreviewVisible()) {
				Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.showPreview();
			}
			if (workspace.isTabDocumentPropertyGroupsVisible()) {
				Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.showPropertyGroups();
				Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.showPropertyGroups();
				Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.showPropertyGroups();
			}
			Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.setKeywordEnabled(workspace.isKeywordEnabled());
			Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.showExtensions();
			Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.init();
			
			// Folder tabs
			if (workspace.isTabFolderPropertiesVisible()) {
				Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.showProperties();
			}
			if (workspace.isTabFolderNotesVisible()) {
				Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.showNotes();
			}
			if (workspace.isTabFolderSecurityVisible()) {
				Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.showSecurity();
			}
			Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.showExtensions();
			Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.init();
			
			// Mail tabs
			if (workspace.isTabMailPropertiesVisible()) {
				Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.showProperties();
			}
			if (workspace.isTabMailSecurityVisible()) {
				Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.showSecurity();
			}
			Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.showExtensions();
			Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.init();
			
			// Show / hide dashboard tools
			if (workspace.isDashboardUserVisible()) {
				Main.get().mainPanel.dashboard.showUser();
			}
			if (workspace.isDashboardMailVisible()) {
				Main.get().mainPanel.dashboard.showMail();
			}
			if (workspace.isDashboardNewsVisible()) {
				Main.get().mainPanel.dashboard.showNews();
			}
			if (workspace.isDashboardGeneralVisible()) {
				Main.get().mainPanel.dashboard.showGeneral();
			}
			if (workspace.isDashboardWorkflowVisible()) {
				Main.get().mainPanel.dashboard.showWorkflow();
			}
			if (workspace.isDashboardKeywordsVisible()) {
				Main.get().mainPanel.dashboard.showKeywords();
			}
			Main.get().mainPanel.dashboard.init();
			
			Main.get().startUp.nextStatus(StartUp.STARTUP_GET_TAXONOMY_ROOT);
			
			// Getting update messages 
			getUpdateMessage();
		}

		public void onFailure(Throwable caught){
			Main.get().showError("getUserWorkspace", caught);
		}
	};
	
	/**
	 * Gets the users documents size 
	 */
	final AsyncCallback<Double> callbackGetUserDocumentsSize = new AsyncCallback<Double>() {
		public void onSuccess(Double result) {
			Main.get().mainPanel.bottomPanel.userInfo.setUserRepositorySize(result.longValue());
		}

		public void onFailure(Throwable caught){
			Main.get().showError("getUserDocumentsSize", caught);
		}
	};
	
	/**
	 * Gets the remote user
	 */
	private void getUpdateMessage() {
		ServiceDefTarget endPoint = (ServiceDefTarget) repositoryService;
		endPoint.setServiceEntryPoint(RPCService.RepositoryService);	
		repositoryService.getUpdateMessage(callbackGetUpdateMessage);
	}
	
	/**
	 * Gets the workspace user data
	 */
	public void getUserWorkspace() {
		ServiceDefTarget endPoint = (ServiceDefTarget) workspaceService;
		endPoint.setServiceEntryPoint(RPCService.WorkspaceService);	
		workspaceService.getUserWorkspace(callbackGetUserWorkspace);
	}
	
	/**
	 * refreshUserWorkspace
	 */
	public void refreshUserWorkspace() {
		ServiceDefTarget endPoint = (ServiceDefTarget) workspaceService;
		endPoint.setServiceEntryPoint(RPCService.WorkspaceService);	
		workspaceService.getUserWorkspace(new AsyncCallback<GWTWorkspace>() {
			@Override
			public void onSuccess(GWTWorkspace result) {
				workspace = result;
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Main.get().showError("getUserWorkspace", caught);
			}
		});
	}
	
	/**
	 * Gets the user documents size
	 */
	public void getUserDocumentsSize() {
		ServiceDefTarget endPoint = (ServiceDefTarget) workspaceService;
		endPoint.setServiceEntryPoint(RPCService.WorkspaceService);	
		workspaceService.getUserDocumentsSize(callbackGetUserDocumentsSize);
	}
	
	/**
	 * Gets the user
	 * 
	 * @return The user
	 */
	public String getUser() {
		return user;
	}
	
	/**
	 * Gets the application URL
	 *
	 * @return
	 */
	public String getApplicationURL() {
		return applicationURL;
	}
	
	/**
	 * Gets the workspace data
	 * 
	 * @return The workspace data
	 */
	public GWTWorkspace getWorkspace() {
		return workspace;
	}
	
	/**
	 * setAvailableAction
	 * 
	 * Some actions must be enabled at ends because some objects are not created since end startp up
	 */
	public void setAvailableAction() {
		Main.get().mainPanel.desktop.navigator.taxonomyTree.menuPopup.menu.setAvailableOption(workspace.getAvailableOption());
		//commented out by vissu on may18 for disable categories & thesaurus
		//Main.get().mainPanel.desktop.navigator.categoriesTree.menuPopup.menu.setAvailableOption(workspace.getAvailableOption());
		//Main.get().mainPanel.desktop.navigator.thesaurusTree.menuPopup.menu.setAvailableOption(workspace.getAvailableOption());
		Main.get().mainPanel.desktop.navigator.templateTree.menuPopup.menu.setAvailableOption(workspace.getAvailableOption());
		Main.get().mainPanel.desktop.navigator.personalTree.menuPopup.menu.setAvailableOption(workspace.getAvailableOption());
		//commented out by vissu on may14 for disable mail 
		//Main.get().mainPanel.desktop.navigator.mailTree.menuPopup.menu.setAvailableOption(workspace.getAvailableOption());
		Main.get().mainPanel.desktop.navigator.trashTree.menuPopup.menu.setAvailableOption(workspace.getAvailableOption());
		Main.get().mainPanel.desktop.browser.fileBrowser.taxonomyMenuPopup.menu.setAvailableOption(workspace.getAvailableOption());
		//commented out by vissu on may18 for disable categories & thesaurus
		//Main.get().mainPanel.desktop.browser.fileBrowser.categoriesMenuPopup.menu.setAvailableOption(workspace.getAvailableOption());
		//Main.get().mainPanel.desktop.browser.fileBrowser.thesaurusMenuPopup.menu.setAvailableOption(workspace.getAvailableOption());
		Main.get().mainPanel.desktop.browser.fileBrowser.templatesMenuPopup.menu.setAvailableOption(workspace.getAvailableOption());
		Main.get().mainPanel.desktop.browser.fileBrowser.personalMenuPopup.menu.setAvailableOption(workspace.getAvailableOption());
		//commented out by vissu on may14 for disable mail
		//Main.get().mainPanel.desktop.browser.fileBrowser.mailMenuPopup.menu.setAvailableOption(workspace.getAvailableOption());
		Main.get().mainPanel.desktop.browser.fileBrowser.trashMenuPopup.menu.setAvailableOption(workspace.getAvailableOption());
		Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.menuPopup.menu.setAvailableOption(workspace.getAvailableOption());
		Main.get().mainPanel.topPanel.mainMenu.setAvailableOption(workspace);
	}
}
