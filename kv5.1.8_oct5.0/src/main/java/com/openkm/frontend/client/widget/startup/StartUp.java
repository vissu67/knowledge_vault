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

package com.openkm.frontend.client.widget.startup;

import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.openkm.extension.frontend.client.Customization;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTUserConfig;
import com.openkm.frontend.client.contants.service.RPCService;
import com.openkm.frontend.client.extension.ExtensionManager;
import com.openkm.frontend.client.service.OKMAuthService;
import com.openkm.frontend.client.service.OKMAuthServiceAsync;
import com.openkm.frontend.client.service.OKMGeneralService;
import com.openkm.frontend.client.service.OKMGeneralServiceAsync;
import com.openkm.frontend.client.service.OKMRepositoryService;
import com.openkm.frontend.client.service.OKMRepositoryServiceAsync;
import com.openkm.frontend.client.service.OKMUserConfigService;
import com.openkm.frontend.client.service.OKMUserConfigServiceAsync;

/**
 * @author jllort
 *
 */
public class StartUp {
	
	public static final int STARTUP_STARTING								= 0;
	public static final int STARTUP_GET_USER_VALUES							= 1;
	public static final int STARTUP_GET_TAXONOMY_ROOT						= 2;
	//public static final int STARTUP_GET_CATEGORIES_ROOT 					= 3;	//commented by vissu on may18 for disable categories & thesaurus
	//public static final int STARTUP_GET_THESAURUS_ROOT 					= 4;	//commented by vissu on may18 for disable categories & thesaurus
	public static final int STARTUP_GET_TEMPLATE_ROOT 						= 3;
	public static final int STARTUP_GET_PERSONAL 	  						= 4;
	//public static final int STARTUP_GET_MAIL 	  							= 7;	//commented by vissu on may14 for disable mail
	public static final int STARTUP_GET_TRASH 	 	  						= 5;
	public static final int STARTUP_GET_USER_HOME 	  						= 6;
	public static final int STARTUP_GET_BOOKMARKS							= 7;
	public static final int STARTUP_LOADING_TAXONOMY						= 8;
	public static final int STARTUP_LOADING_TAXONOMY_FOLDERS				= 9;
	public static final int STARTUP_LOADING_TAXONOMY_EVAL_PARAMS			= 10;
	public static final int STARTUP_LOADING_OPEN_PATH						= 11;
	public static final int STARTUP_LOADING_TAXONOMY_FILEBROWSER_FOLDERS	= 12;
	public static final int STARTUP_LOADING_TAXONOMY_FILEBROWSER_DOCUMENTS	= 13;
	//public static final int STARTUP_LOADING_TAXONOMY_FILEBROWSER_MAILS	= 17;	//commented by vissu on may14 for disable mail
	//public static final int STARTUP_LOADING_CATEGORIES					= 14;	//commented by vissu on may18 for disable categories & thesaurus
	//public static final int STARTUP_LOADING_THESAURUS						= 15;	//commented by vissu on may18 for disable categories & thesaurus
	public static final int STARTUP_LOADING_TEMPLATES						= 14;
	public static final int STARTUP_LOADING_PERSONAL						= 15;
	//public static final int STARTUP_LOADING_MAIL							= 22;	//commented by vissu on may14 for disable mail
	public static final int STARTUP_LOADING_TRASH							= 16;
	public static final int STARTUP_LOADING_HISTORY_SEARCH					= 17;
	
	private final OKMRepositoryServiceAsync repositoryService = (OKMRepositoryServiceAsync) GWT.create(OKMRepositoryService.class);
	private final OKMAuthServiceAsync authService = (OKMAuthServiceAsync) GWT.create(OKMAuthService.class);
	private final OKMUserConfigServiceAsync userConfigService = (OKMUserConfigServiceAsync) GWT.create(OKMUserConfigService.class);
	private final OKMGeneralServiceAsync generalService = (OKMGeneralServiceAsync) GWT.create(OKMGeneralService.class);
	
	private boolean enabled = true;
	private boolean error = false;
	private int status = -1;
	public Timer keepAlive;
	
	/**
	 * Inits on first load
	 */
	public void init(){
		ServiceDefTarget endPoint = (ServiceDefTarget) generalService;
		endPoint.setServiceEntryPoint(RPCService.GeneralService);
		generalService.getEnabledExtensions(new AsyncCallback<List<String>>() {
			@Override
			public void onSuccess(List<String> result) {
				Main.get().setExtensionUuidList(result);
				// Only show registered extensions
				ExtensionManager.start(Customization.getExtensionWidgets(result));
				nextStatus(STARTUP_STARTING);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Main.get().showError("getEnabledExtensions",caught);
				nextStatus(STARTUP_STARTING);
			}
		});
	}
	
	/**
	 * Gets asyncronous taxonomy root node
	 */
	final AsyncCallback<GWTFolder> callbackGetRootFolder = new AsyncCallback<GWTFolder>() {
		public void onSuccess(GWTFolder result) {
			//Only executes on initalization and evalues root Node permissions
			Main.get().taxonomyRootFolder = result;
			Main.get().mainPanel.desktop.browser.fileBrowser.table.fillWidth(); // Sets de columns size
			//below line commented by vissu on may18 for disable mail & disable categories & thesaurus
			//nextStatus(STARTUP_GET_CATEGORIES_ROOT);
			nextStatus(STARTUP_GET_TEMPLATE_ROOT);
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("GetRootFolder", caught);
		}
	};
	
	/**
	 * Gets asyncronous template root node
	 */
	final AsyncCallback<GWTFolder> callbackGetTemplatesFolder = new AsyncCallback<GWTFolder>() {
		public void onSuccess(GWTFolder result) {
			// Only executes on initalization
			Main.get().templatesRootFolder = result;
			nextStatus(STARTUP_GET_PERSONAL);
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("GetTemplatesFolder", caught);
		}
	};
	
	/**
	 * Gets asyncronous mail root node
	 */
	final AsyncCallback<GWTFolder> callbackGetMailFolder = new AsyncCallback<GWTFolder>() {
		public void onSuccess(GWTFolder result) {
			// Only executes on initalization
			Main.get().mailRootFolder = result;
			nextStatus(STARTUP_GET_TRASH);
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("GetMailFolder", caught);
		}
	};
	
	/**
	 * Gets asyncronous thesaurus root node
	 */
	final AsyncCallback<GWTFolder> callbackGetThesaurusFolder = new AsyncCallback<GWTFolder>() {
		public void onSuccess(GWTFolder result) {
			// Only executes on initalization
			Main.get().thesaurusRootFolder = result;
			nextStatus(STARTUP_GET_TEMPLATE_ROOT);
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("GetThesaurusFolder", caught);
		}
	};
	
	/**
	 * Gets asyncronous categories root node
	 */
	final AsyncCallback<GWTFolder> callbackGetCategoriesFolder = new AsyncCallback<GWTFolder>() {
		public void onSuccess(GWTFolder result) {
			// Only executes on initalization
			Main.get().categoriesRootFolder = result;
			//changed from STARTUP_GET_THESAURUS_ROOT to STARTUP_GET_TEMPLATE_ROOT by vissu on may18 for disable categories & thesaurus
			nextStatus(STARTUP_GET_TEMPLATE_ROOT);
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("GetCategoriesFolder", caught);
		}
	};

	/**
	 * Callback get user home
	 */
	final AsyncCallback<GWTUserConfig> callbackGetUserHome = new AsyncCallback<GWTUserConfig>() {
		public void onSuccess(GWTUserConfig result) {
			Main.get().userHome = result;
			nextStatus(STARTUP_GET_BOOKMARKS);
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("GetUserHome", caught);
		}
	};
	
	/**
	 * Gets asyncronous personal documents node
	 */
	final AsyncCallback<GWTFolder> callbackGetPersonalFolder = new AsyncCallback<GWTFolder>() {
		public void onSuccess(GWTFolder result) {
			Main.get().personalRootFolder = result;
			//below line commnted by vissu on may14 for disable mail
			//nextStatus(STARTUP_GET_MAIL);
			nextStatus(STARTUP_GET_TRASH);
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("GetPersonalFolder", caught);
		}
	};
	
	/**
	 * Gets asyncronous trash node
	 */
	final AsyncCallback<GWTFolder> callbackGetTrashFolder = new AsyncCallback<GWTFolder>() {
		public void onSuccess(GWTFolder result) {
			Main.get().trashRootFolder = result;
			nextStatus(STARTUP_GET_USER_HOME);
		}
		
		public void onFailure(Throwable caught) {
			Main.get().showError("GetTrashFolder", caught);
		}
	};
	
	/**
	 * Call back add new granted user
	 */
	final AsyncCallback<Object> callbackKeepAlive = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {
		}
			
		public void onFailure(Throwable caught) {
			Main.get().mainPanel.bottomPanel.setStatus("status.keep.alive.error",true);
		}
	};
	
	/**
	 * Gets asyncronous to add a group
	 */
	final AsyncCallback<Map<String,String>> callbackGetPropertyGroupTranslations = new AsyncCallback<Map<String,String>>() {
		public void onSuccess(Map<String,String> result){
			Main.get().hPropertyGroupI18n = result;
			
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("GetPropertyGroupTranslations", caught);
		}
	};
	
	/**
	 * Gets the trash
	 */
	public void getTrash() {
		ServiceDefTarget endPoint = (ServiceDefTarget) repositoryService;
		endPoint.setServiceEntryPoint(RPCService.RepositoryService);	
		repositoryService.getTrashFolder(callbackGetTrashFolder);
	}
	
	/**
	 * Gets the personal documents
	 */
	public void getPersonal() {
		ServiceDefTarget endPoint = (ServiceDefTarget) repositoryService;
		endPoint.setServiceEntryPoint(RPCService.RepositoryService);
		repositoryService.getPersonalFolder(callbackGetPersonalFolder);
	}
	
	/**
	 * Gets the user home
	 * 
	 */
	public void getUserHome() {
		ServiceDefTarget endPoint = (ServiceDefTarget) userConfigService;
		endPoint.setServiceEntryPoint(RPCService.UserConfigService);			
		userConfigService.getUserHome(callbackGetUserHome);
	}
	
	/**
	 * Gets the template
	 */
	public void getTemplate() {
		ServiceDefTarget endPoint = (ServiceDefTarget) repositoryService;
		endPoint.setServiceEntryPoint(RPCService.RepositoryService);
		repositoryService.getTemplatesFolder(callbackGetTemplatesFolder);
	}
	
	/**
	 * Gets the mail
	 */
	public void getMail() {
		ServiceDefTarget endPoint = (ServiceDefTarget) repositoryService;
		endPoint.setServiceEntryPoint(RPCService.RepositoryService);
		repositoryService.getMailFolder(callbackGetMailFolder);
	}
	
	/**
	 * Gets the thesaurus
	 */
	public void getThesaurus() {
		ServiceDefTarget endPoint = (ServiceDefTarget) repositoryService;
		endPoint.setServiceEntryPoint(RPCService.RepositoryService);
		repositoryService.getThesaurusFolder(callbackGetThesaurusFolder);
	}
	
	public void getCategories() {
		ServiceDefTarget endPoint = (ServiceDefTarget) repositoryService;
		endPoint.setServiceEntryPoint(RPCService.RepositoryService);
		repositoryService.getCategoriesFolder(callbackGetCategoriesFolder);
	}	
	
	
	/**
	 * Gets the taxonomy
	 */
	public void getRoot() {
		ServiceDefTarget endPoint = (ServiceDefTarget) repositoryService;
		endPoint.setServiceEntryPoint(RPCService.RepositoryService);	
		repositoryService.getRootFolder(callbackGetRootFolder);
	}
	
	public void startKeepAlive(double scheduleTime) {
		// KeepAlieve thread
	    ServiceDefTarget endPoint = (ServiceDefTarget) authService;
		endPoint.setServiceEntryPoint(RPCService.AuthService);
		keepAlive = new Timer() {
			public void run() {
				authService.keepAlive(callbackKeepAlive);
			}
		};
		
		keepAlive.scheduleRepeating(new Double(scheduleTime).intValue()); // 15 min
	}
	
	/**
	 * Sets the next status
	 * 
	 * @param status The new status 
	 */
	public void nextStatus( int status) {
		if (enabled) {
			// Status is always incremental
			if (this.status<status) {
				this.status = status;
				
				switch (status) {
					case STARTUP_STARTING:
						Main.get().startUpPopup.addStatus(Main.i18n("startup.starting.loading"), STARTUP_STARTING);
						nextStatus(STARTUP_GET_USER_VALUES);
						break;
						
					case STARTUP_GET_USER_VALUES:
						Main.get().startUpPopup.addStatus(Main.i18n("startup.loading.user.values"), STARTUP_GET_USER_VALUES);
						Main.get().workspaceUserProperties.init();
						break;	
						
					case STARTUP_GET_TAXONOMY_ROOT:
						Main.get().startUpPopup.addStatus(Main.i18n("startup.taxonomy"), STARTUP_GET_TAXONOMY_ROOT);
						getRoot();
						break;
						
					//below case commented by vissu on may16 for disable categories & thesaurus
					/*case STARTUP_GET_CATEGORIES_ROOT:
						Main.get().startUpPopup.addStatus(Main.i18n("startup.categories"), STARTUP_GET_CATEGORIES_ROOT);
						getCategories();
						break;
						
					case STARTUP_GET_THESAURUS_ROOT:
						Main.get().startUpPopup.addStatus(Main.i18n("startup.thesaurus"), STARTUP_GET_THESAURUS_ROOT);
						getThesaurus();
						break;*/
					
					case STARTUP_GET_TEMPLATE_ROOT:
						Main.get().startUpPopup.addStatus(Main.i18n("startup.template"), STARTUP_GET_TEMPLATE_ROOT);
						getTemplate();
						break;
						
					case STARTUP_GET_PERSONAL :
						Main.get().startUpPopup.addStatus(Main.i18n("startup.personal"), STARTUP_GET_PERSONAL);
						getPersonal();
						break;
					
					//below case commented by vissu on may14 for disable mail
					/*case STARTUP_GET_MAIL :
						Main.get().startUpPopup.addStatus(Main.i18n("startup.mail"), STARTUP_GET_MAIL);
						getMail();
						break;*/
					
					case STARTUP_GET_TRASH :
						Main.get().startUpPopup.addStatus(Main.i18n("startup.trash"), STARTUP_GET_TRASH);
						getTrash();
						break;
				
					case STARTUP_GET_USER_HOME :
						Main.get().startUpPopup.addStatus(Main.i18n("startup.user.home"), STARTUP_GET_USER_HOME);
						getUserHome();
						break;
					
					case STARTUP_GET_BOOKMARKS:
						Main.get().startUpPopup.addStatus(Main.i18n("startup.bookmarks"), STARTUP_GET_BOOKMARKS);
						Main.get().mainPanel.topPanel.mainMenu.bookmark.getAll(); 	// Initialize bookmarks
						Main.get().mainPanel.desktop.browser.tabMultiple.init();			// Initialize tab multiple
						break;
					
					case STARTUP_LOADING_TAXONOMY:
						Main.get().startUpPopup.addStatus(Main.i18n("startup.loading.taxonomy"), STARTUP_LOADING_TAXONOMY);
						Main.get().mainPanel.desktop.navigator.taxonomyTree.init();			// Initialize folder tree
						break;
					
					case STARTUP_LOADING_TAXONOMY_FOLDERS:
						Main.get().startUpPopup.addStatus(Main.i18n("startup.loading.taxonomy.getting.folders"), STARTUP_LOADING_TAXONOMY_FOLDERS);
						break;
					
					case STARTUP_LOADING_TAXONOMY_EVAL_PARAMS:
						Main.get().startUpPopup.addStatus(Main.i18n("startup.loading.taxonomy.eval.params"), STARTUP_LOADING_TAXONOMY_EVAL_PARAMS);
						break;
					
					case STARTUP_LOADING_OPEN_PATH:
						Main.get().startUpPopup.addStatus(Main.i18n("startup.loading.taxonomy.open.path"), STARTUP_LOADING_OPEN_PATH);
						break;
					
					case STARTUP_LOADING_TAXONOMY_FILEBROWSER_FOLDERS:
						Main.get().startUpPopup.addStatus(Main.i18n("startup.loading.taxonomy.getting.filebrowser.folders"),
														  STARTUP_LOADING_TAXONOMY_FILEBROWSER_FOLDERS);
						break;
					
					case STARTUP_LOADING_TAXONOMY_FILEBROWSER_DOCUMENTS:
						Main.get().startUpPopup.addStatus(Main.i18n("startup.loading.taxonomy.getting.filebrowser.documents"),
														  STARTUP_LOADING_TAXONOMY_FILEBROWSER_DOCUMENTS);
						break;
					
					//below case commented by vissu on may14 for disable mail
					/*case STARTUP_LOADING_TAXONOMY_FILEBROWSER_MAILS:
						Main.get().startUpPopup.addStatus(Main.i18n("startup.loading.taxonomy.getting.filebrowser.mails"),
														  STARTUP_LOADING_TAXONOMY_FILEBROWSER_MAILS);
						break;*/
						
					//below case commented by vissu on may18 for disable categories & thesaurus
				/*	case STARTUP_LOADING_CATEGORIES:
						Main.get().startUpPopup.addStatus(Main.i18n("startup.loading.categories"), STARTUP_LOADING_CATEGORIES);
						Main.get().mainPanel.desktop.navigator.categoriesTree.init();	  	// Initialize thesaurus
						break;
						
					case STARTUP_LOADING_THESAURUS:
						Main.get().startUpPopup.addStatus(Main.i18n("startup.loading.thesaurus"), STARTUP_LOADING_THESAURUS);
						Main.get().mainPanel.desktop.navigator.thesaurusTree.init();	  	// Initialize thesaurus
						break;*/
						
					case STARTUP_LOADING_TEMPLATES:
						Main.get().startUpPopup.addStatus(Main.i18n("startup.loading.templates"), STARTUP_LOADING_TEMPLATES);
						Main.get().mainPanel.desktop.navigator.templateTree.init();	   		// Initialize templates
						break;
					
					case STARTUP_LOADING_PERSONAL:
						Main.get().startUpPopup.addStatus(Main.i18n("startup.loading.personal"), STARTUP_LOADING_PERSONAL);
						Main.get().mainPanel.desktop.navigator.personalTree.init();			// Initialize my documents
						break;
					
					//below case commented by vissu on may14 for disable mail
					/*case STARTUP_LOADING_MAIL:
						Main.get().startUpPopup.addStatus(Main.i18n("startup.loading.mail"), STARTUP_LOADING_MAIL);
						Main.get().mainPanel.desktop.navigator.mailTree.init();				// Initialize mail
						break;*/

					case STARTUP_LOADING_TRASH:
						Main.get().startUpPopup.addStatus(Main.i18n("startup.loading.trash"), STARTUP_LOADING_TRASH);
						Main.get().mainPanel.desktop.navigator.trashTree.init();			// Initialize trash folder
						break;//below case commented by vissu on may14 for disable mai
					
					case STARTUP_LOADING_HISTORY_SEARCH:
						Main.get().startUpPopup.addStatus(Main.i18n("startup.loading.history.search"), STARTUP_LOADING_HISTORY_SEARCH);
						//Main.get().mainPanel.search.historySearch.searchSaved.init();		// Initialize history saved
						//Main.get().mainPanel.search.historySearch.userNews.init();
						Main.get().mainPanel.setVisible(true);
						Main.get().workspaceUserProperties.setAvailableAction(); // Some actions ( menus / etc ... ) must be set at ends startup
						  														 // After init widget methods ares all yet finished
						enabled = false;
						if (!error) {
							Main.get().startUpPopup.hide();
						}
						break;
				}			
			}
		}
	}
	
	/**
	 * Disable
	 */
	public void disable() {
		enabled = false;
	}
	
	/**
	 * Tries to recover after an error
	 */
	public void recoverFromError() {
		error = true;
		Main.get().startUpPopup.button.setVisible(true);
		
		if (status<STARTUP_LOADING_HISTORY_SEARCH) {	
			// This range are sequential calls
			if (status<STARTUP_LOADING_TAXONOMY) {
				nextStatus(status+1); // Tries to execute next initializing
			
			// This range must start with loading personal ( sequential in this range is break )
			//changed STARTUP_LOADING_CATEGORIES to STARTUP_LOADING_TEMPLATES on may18 for disable categories & thesaurus
			} else if (status<STARTUP_LOADING_TEMPLATES) {
				nextStatus(STARTUP_LOADING_TEMPLATES); // Tries to execute next initializing
			
            // This range are sequential calls
			} else {
				nextStatus(status+1); // Tries to execute next initializing
			}
		} else {
			enabled = false;
		}
	}
	
	public String getStatusMsg(int status) {
		String msg = "";
		
		switch (status) {
			case STARTUP_STARTING:
				msg = Main.i18n("startup.starting.loading");
				break;
				
			case STARTUP_GET_USER_VALUES:
				msg = Main.i18n("startup.loading.user.values");
				break;	
				
			case STARTUP_GET_TAXONOMY_ROOT:
				msg = Main.i18n("startup.taxonomy");
				break;
				
			//below case commented by vissu on may18 for disable categories & thesaurus
			/*case STARTUP_GET_CATEGORIES_ROOT:
				msg = Main.i18n("startup.categories");
				break;
				
			case STARTUP_GET_THESAURUS_ROOT:
				msg = Main.i18n("startup.thesaurus");
				break;*/
			
			case STARTUP_GET_TEMPLATE_ROOT:
				msg = Main.i18n("startup.template");
				break;
				
			case STARTUP_GET_PERSONAL :
				msg = Main.i18n("startup.personal");
				break;
			
			//below case commented by vissu on may14 for disable mail
			/*case STARTUP_GET_MAIL :
				msg = Main.i18n("startup.mail");
				break;*/
			
			case STARTUP_GET_TRASH :
				msg = Main.i18n("startup.trash");
				break;
		
			case STARTUP_GET_USER_HOME :
				msg = Main.i18n("startup.user.home");
				getUserHome();
				break;
			
			case STARTUP_GET_BOOKMARKS:
				msg = Main.i18n("startup.bookmarks");
				break;
			
			case STARTUP_LOADING_TAXONOMY:
				msg = Main.i18n("startup.loading.taxonomy");
				break;
			
			case STARTUP_LOADING_TAXONOMY_FOLDERS:
				msg = Main.i18n("startup.loading.taxonomy.getting.folders");
				break;
			
			case STARTUP_LOADING_TAXONOMY_EVAL_PARAMS:
				msg = Main.i18n("startup.loading.taxonomy.eval.params");
				break;
			
			case STARTUP_LOADING_OPEN_PATH:
				msg = Main.i18n("startup.loading.taxonomy.open.path");
				break;
			
			case STARTUP_LOADING_TAXONOMY_FILEBROWSER_FOLDERS:
				msg = Main.i18n("startup.loading.taxonomy.getting.filebrowser.folders");
				break;
			
			case STARTUP_LOADING_TAXONOMY_FILEBROWSER_DOCUMENTS:
				msg = Main.i18n("startup.loading.taxonomy.getting.filebrowser.documents");
				break;

			//below case commented by vissu on may14 for disable mail
			/*case STARTUP_LOADING_TAXONOMY_FILEBROWSER_MAILS:
				msg = Main.i18n("startup.loading.taxonomy.getting.filebrowser.mails");
				break;*/
				
			//below case commented by vissu on may18 for disable categories & thesaurus
			/*case STARTUP_LOADING_CATEGORIES:
				msg = Main.i18n("startup.loading.categories");
				break;
				
			case STARTUP_LOADING_THESAURUS:
				msg = Main.i18n("startup.loading.thesaurus");
				break;*/
				
			case STARTUP_LOADING_TEMPLATES:
				msg = Main.i18n("startup.loading.templates");
				break;
			
			case STARTUP_LOADING_PERSONAL:
				msg = Main.i18n("startup.loading.personal");
				break;
			
			//below case commented by vissu on may14 for disable mail
			/*case STARTUP_LOADING_MAIL:
				msg = Main.i18n("startup.loading.mail");
				break;*/
			
			case STARTUP_LOADING_TRASH:
				msg = Main.i18n("startup.loading.trash");
				break;
			
			case STARTUP_LOADING_HISTORY_SEARCH:
				msg = Main.i18n("startup.loading.history.search");
				break;			
		}			
		
		return msg;
	}
	
}