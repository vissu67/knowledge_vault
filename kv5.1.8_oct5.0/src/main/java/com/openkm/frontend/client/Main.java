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

package com.openkm.frontend.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.log.client.DivLogger;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.ClosingEvent;
import com.google.gwt.user.client.Window.ClosingHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.rpc.StatusCodeException;
import com.google.gwt.user.client.ui.RootPanel;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTUserConfig;
import com.openkm.frontend.client.bean.RepositoryContext;
import com.openkm.frontend.client.contants.service.RPCService;
import com.openkm.frontend.client.extension.event.HasLanguageEvent;
import com.openkm.frontend.client.extension.event.handler.LanguageHandlerExtension;
import com.openkm.frontend.client.extension.event.hashandler.HasLanguageHandlerExtension;
import com.openkm.frontend.client.panel.ExtendedDockPanel;
import com.openkm.frontend.client.service.OKMLanguageService;
import com.openkm.frontend.client.service.OKMLanguageServiceAsync;
import com.openkm.frontend.client.util.Location;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.util.WindowUtils;
import com.openkm.frontend.client.util.WorkspaceUserProperties;
import com.openkm.frontend.client.widget.AboutPopup;
import com.openkm.frontend.client.widget.ZohoPopup;	//added by vissu on feb19 for zohoapi
import com.openkm.frontend.client.widget.ConfirmPopup;
import com.openkm.frontend.client.widget.DebugConsolePopup;
import com.openkm.frontend.client.widget.Dragable;
import com.openkm.frontend.client.widget.ErrorPopup;
import com.openkm.frontend.client.widget.ExternalURLPopup;
import com.openkm.frontend.client.widget.PropertyGroupPopup;
import com.openkm.frontend.client.widget.LogoutPopup;
import com.openkm.frontend.client.widget.MsgPopup;
import com.openkm.frontend.client.widget.ReportPopup;
import com.openkm.frontend.client.widget.UserPopup;
import com.openkm.frontend.client.widget.WorkflowPopup;
import com.openkm.frontend.client.widget.chat.OnlineUsersPopup;
import com.openkm.frontend.client.widget.findfolder.FindFolderSelectPopup;
import com.openkm.frontend.client.widget.foldertree.FolderTree;
import com.openkm.frontend.client.widget.notify.NotifyPopup;
import com.openkm.frontend.client.widget.security.SecurityPopup;
import com.openkm.frontend.client.widget.startup.StartUp;
import com.openkm.frontend.client.widget.startup.StartUpPopup;
import com.openkm.frontend.client.widget.test.TestPopup;
import com.openkm.frontend.client.widget.upload.FileUploadPopup;
import com.openkm.frontend.client.widget.wizard.TemplateWizardPopup;
import com.openkm.frontend.client.widget.wizard.WizardPopup;

/**
 * Main entry point application
 * 
 * @author jllort
 *
 */
public final class Main implements EntryPoint, HasLanguageHandlerExtension, HasLanguageEvent {
	public static String CONTEXT = "/OpenKM";
	private static Main singleton;
	private final OKMLanguageServiceAsync languageService = (OKMLanguageServiceAsync) GWT.create(OKMLanguageService.class);
	private List<String> extensionUuidList = new ArrayList<String>();

	/**
	 * @return singleton Main instance 
	 */
	public static Main get() {
		return singleton;
	}
	
	// Main panel declaration
	public ExtendedDockPanel mainPanel;
	
	// Other panel declaration
	public StartUpPopup startUpPopup;
	public FileUploadPopup fileUpload;
	public ErrorPopup errorPopup;
	public ErrorPopup errorPopupLogout;
	public MsgPopup msgPopup;
	public ExternalURLPopup externalURLPopup;
	public LogoutPopup logoutPopup;
	public SecurityPopup securityPopup;
	public AboutPopup aboutPopup;
	public UserPopup userPopup;
	public ConfirmPopup confirmPopup;
	public Dragable dragable;
	public PropertyGroupPopup propertyGroupPopup;
	public WorkflowPopup workflowPopup;
	public NotifyPopup notifyPopup;
	public DebugConsolePopup debugConsolePopup;
	public FindFolderSelectPopup findFolderSelectPopup;
	public WizardPopup wizardPopup;
	public ReportPopup reportPopup;
	public TemplateWizardPopup templateWizardPopup;
	public OnlineUsersPopup onlineUsersPopup;
	public TestPopup testPopup;
	public ZohoPopup zohoPopup;		//added by vissu on feb19 for zohoapi
	
	// User workspace properties
	public WorkspaceUserProperties workspaceUserProperties;
	
	// Language declarations
	private String lang;
	private Map<String, String> hI18n;
	public Map<String,String> hPropertyGroupI18n;
	
	// The nodePath parameter
	public String fldPath = "";  // Used for folderTree because docPath is set to null by filebroeser on this case the refreshing
									// panels are not sincronized ( loading )
	public String docPath = "";  // Used for folderTree because docPath is set to null by filebroeser on this case the refreshing
									// panels are not sincronized ( loading )
	
	// Main root folders and user home general values for all app
	public GWTFolder taxonomyRootFolder;
	public GWTFolder categoriesRootFolder;
	public GWTFolder thesaurusRootFolder;
	public GWTFolder personalRootFolder;
	public GWTFolder templatesRootFolder;
	public GWTFolder mailRootFolder;
	public GWTFolder trashRootFolder;
	public GWTUserConfig userHome;
	public FolderTree activeFolderTree; // The active folder
		
	// The satartUp sequence
	public StartUp startUp;
	
	// The location ( url params )
	public Location loc;
	
	// Repository context
	public RepositoryContext repositoryContext;
	
	// Lnaguage widget handlers
	List<LanguageHandlerExtension> langHandlerExtensionList;
	
	/* (non-Javadoc)
	 * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
	 */
	public void onModuleLoad() {
		Log.getLogger(DivLogger.class).getWidget().setVisible(false);
		Log.setUncaughtExceptionHandler();
		Log.setCurrentLogLevel(Log.LOG_LEVEL_OFF);

		singleton = this;
		
		// All objects defined before singleton to use global reference.
		langHandlerExtensionList = new ArrayList<LanguageHandlerExtension>();
		
		// Saves repository context paths
		repositoryContext = new RepositoryContext();
		
		// Request parameter
		loc = WindowUtils.getLocation();
		
		// Capture web application context
		if (Cookies.getCookie("ctx") != null && !Cookies.getCookie("ctx").equals("")) {
			CONTEXT = Cookies.getCookie("ctx");
		} else {
			// Otherwise we try to guess
			CONTEXT = loc.getContext();
		}
		
		if (loc.getParameter("docPath") != null && !loc.getParameter("docPath").equals("")) {
			fldPath = loc.getParameter("docPath").substring(0, loc.getParameter("docPath").lastIndexOf("/")); 
			docPath = loc.getParameter("docPath"); 
		} else if (loc.getParameter("fldPath") != null && !loc.getParameter("fldPath").equals("")) {
			fldPath = loc.getParameter("fldPath"); 
		}
		
		// Try to capture lang parameter
		if (loc.getParameter("lang") != null && !loc.getParameter("lang").equals("")) {
			lang = loc.getParameter("lang");
		} else if (Cookies.getCookie("lang") != null && !Cookies.getCookie("lang").equals("")) {
			lang = Cookies.getCookie("lang");
		} else {
			// First we initialize language values
			lang = Util.getBrowserLanguage();
		}
		
		// Getting language
		ServiceDefTarget endPoint = (ServiceDefTarget) languageService;
		endPoint.setServiceEntryPoint(RPCService.LanguageService);
		languageService.getFrontEndTranslations(Main.get().getLang(), new AsyncCallback<Map<String,String>>() {
			@Override
			public void onSuccess(Map<String, String> result) {
				hI18n = result;
				onModuleLoad2(); // continues normal loading
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error getting translations: " + caught.getMessage());
			}
		});
	}
	
	/**
	 * onModuleLoad2
	 */
	public void onModuleLoad2() {
		// Initialize workspace properties
		workspaceUserProperties = new WorkspaceUserProperties();
		
		// Initialize user home
		userHome = new GWTUserConfig();

		// Initialize panels
		mainPanel = new ExtendedDockPanel();
		
		// Loading popup
		startUpPopup = new StartUpPopup();
		startUpPopup.setWidth("300px");
		startUpPopup.setHeight("220px");
		startUpPopup.setStyleName("okm-Popup");
		startUpPopup.addStyleName("okm-DisableSelect");
		
		// Initialize general panels
		fileUpload = new FileUploadPopup();
		fileUpload.setStyleName("okm-Popup");
		errorPopup = new ErrorPopup(false);
		errorPopup.setStyleName("okm-Popup-Error");
		errorPopup.setWidth("380px");
		errorPopup.setHeight("205px");
		errorPopupLogout = new ErrorPopup(true);
		errorPopupLogout.setStyleName("okm-Popup-Error");
		errorPopupLogout.setWidth("300px");
		errorPopupLogout.setHeight("205px");
		msgPopup = new MsgPopup();
		msgPopup.setStyleName("okm-Popup");
		msgPopup.setWidth("300px");
		msgPopup.setHeight("205px");
		externalURLPopup = new ExternalURLPopup();
		externalURLPopup.setStyleName("okm-Popup");
		logoutPopup = new LogoutPopup();
		logoutPopup.setWidth("250");
		logoutPopup.setHeight("110");
		logoutPopup.setStyleName("okm-Popup");
		logoutPopup.addStyleName("okm-DisableSelect");
		securityPopup = new SecurityPopup();
		securityPopup.setWidth("345");	//vissu touch to reduce width from 600 to 345
		securityPopup.setHeight("330");	//vissu touch to reduce Height from 400 to 330
		securityPopup.setStyleName("okm-Popup");
		securityPopup.addStyleName("okm-DisableSelect");
		aboutPopup = new AboutPopup();
		aboutPopup.setWidth("300px");
		aboutPopup.setHeight("220px");
		aboutPopup.setStyleName("okm-Popup");
		aboutPopup.addStyleName("okm-DisableSelect");
		
		//added by vissu on feb16 for zohoapi
		zohoPopup = new ZohoPopup();
		zohoPopup.setWidth("800px");
		zohoPopup.setHeight("500px");
		zohoPopup.setStyleName("okm-Popup");
		zohoPopup.addStyleName("okm-DisableSelect");
		
		userPopup = new UserPopup();
		userPopup.setWidth("400px");
		userPopup.setHeight("220px");
		userPopup.setStyleName("okm-Popup");
		//userPopup.addStyleName("okm-DisableSelect");
		confirmPopup = new ConfirmPopup();
		confirmPopup.setWidth("300px");
		confirmPopup.setHeight("125px");
		confirmPopup.setStyleName("okm-Popup");
		confirmPopup.addStyleName("okm-DisableSelect");
		dragable = new Dragable();
		propertyGroupPopup = new PropertyGroupPopup();
		propertyGroupPopup.setWidth("300px");
		propertyGroupPopup.setHeight("100px");
		propertyGroupPopup.setStyleName("okm-Popup");
		propertyGroupPopup.addStyleName("okm-DisableSelect");
		workflowPopup = new WorkflowPopup();
		workflowPopup.setWidth("300px");
		workflowPopup.setHeight("100px");
		workflowPopup.setStyleName("okm-Popup");
		workflowPopup.addStyleName("okm-DisableSelect");
		notifyPopup = new NotifyPopup();
		notifyPopup.setWidth("400px");
		notifyPopup.setHeight("100px");
		notifyPopup.setStyleName("okm-Popup");
		debugConsolePopup = new DebugConsolePopup();
		debugConsolePopup.setWidth("300px");
		debugConsolePopup.setHeight("100px");
		debugConsolePopup.setStyleName("okm-Popup");
		debugConsolePopup.addStyleName("okm-DisableSelect");
		findFolderSelectPopup = new FindFolderSelectPopup();
		findFolderSelectPopup.setWidth("400px");
		findFolderSelectPopup.setHeight("240px");
		findFolderSelectPopup.setStyleName("okm-Popup");
		findFolderSelectPopup.addStyleName("okm-DisableSelect");
		wizardPopup = new WizardPopup();
		wizardPopup.setWidth("400px");
		wizardPopup.setHeight("40px");
		wizardPopup.setStyleName("okm-Popup");
		wizardPopup.addStyleName("okm-DisableSelect");
		reportPopup = new ReportPopup();
		reportPopup.setWidth("250px");
		reportPopup.setHeight("40px");
		reportPopup.setStyleName("okm-Popup");
		reportPopup.addStyleName("okm-DisableSelect");
		templateWizardPopup = new TemplateWizardPopup();
		templateWizardPopup.setWidth("400px");
		templateWizardPopup.setHeight("40px");
		templateWizardPopup.setStyleName("okm-Popup");
		templateWizardPopup.addStyleName("okm-DisableSelect");
		onlineUsersPopup = new OnlineUsersPopup();
		onlineUsersPopup.setWidth("250px");
		onlineUsersPopup.setHeight("350px");
		onlineUsersPopup.setStyleName("okm-Popup");
		onlineUsersPopup.addStyleName("okm-DisableSelect");
		testPopup = new TestPopup();
		testPopup.setWidth("600px");
		testPopup.setHeight("500px");
		testPopup.setStyleName("okm-Popup");

		// Get grid of scrollbars, and clear out the window's built-in margin,
	    // because we want to take advantage of the entire client area.
	    Window.enableScrolling(false);
	    Window.setMargin("0px");
	    
	    RootPanel.get().add(mainPanel);
	    RootPanel.get().add(dragable);
	    
	    Window.addWindowClosingHandler(new ClosingHandler() {
			@Override
			public void onWindowClosing(ClosingEvent event) {
				startUp.keepAlive.cancel();
			}
		});
		
		// Sets the active folder tree, it'll be used to store the active folder 
		// every time switching stack panel
		activeFolderTree = mainPanel.desktop.navigator.taxonomyTree;
		
		// Initialize on startup when all objects are created sequentially
		startUpPopup.show();
		startUp = new StartUp();
		startUp.init();
		
		// Auto-publish the method into JS when the GWT module loads.
		initJavaScriptApi();
		mainPanel.topPanel.toolBar.initJavaScriptApi(mainPanel.topPanel.toolBar);
	}
	
	/**
	 * Refresh language
	 * 
	 * @param lang The language code
	 */
	public void refreshLang(String lang) {
		this.lang = lang;
		ServiceDefTarget endPoint = (ServiceDefTarget) languageService;
		endPoint.setServiceEntryPoint(RPCService.LanguageService);
		languageService.getFrontEndTranslations(lang, new AsyncCallback<Map<String,String>>() {
			@Override
			public void onSuccess(Map<String, String> result) {
				hI18n = result;
				fireEvent(HasLanguageEvent.LANGUAGE_CHANGED);
				mainPanel.desktop.navigator.langRefresh();
				mainPanel.topPanel.langRefresh();
				mainPanel.desktop.browser.langRefresh();
				mainPanel.search.historySearch.langRefresh();
				mainPanel.search.searchBrowser.langRefresh();
				mainPanel.bottomPanel.langRefresh();
				mainPanel.dashboard.langRefresh();
				fileUpload.langRefresh();
				logoutPopup.langRefresh();
				securityPopup.langRefresh();
				aboutPopup.langRefresh();
				zohoPopup.langRefresh();	//added by vissu on feb16 for zohoapi
				userPopup.langRefresh();
				confirmPopup.langRefresh();
				msgPopup.langRefresh();
				errorPopup.langRefresh();
				errorPopupLogout.langRefresh();
				externalURLPopup.langRefresh();
				propertyGroupPopup.langRefresh();
				workflowPopup.langRefresh();
				notifyPopup.langRefresh();
				debugConsolePopup.langRefresh();
				findFolderSelectPopup.langRefresh();
				wizardPopup.langRefresh();
				wizardPopup.langRefresh();
				reportPopup.langRefresh();
				onlineUsersPopup.langRefresh();
				// Refreshing all menus on tabs not only the active
				mainPanel.desktop.navigator.taxonomyTree.langRefresh();
				mainPanel.desktop.navigator.thesaurusTree.langRefresh();
				mainPanel.desktop.navigator.personalTree.langRefresh();
				mainPanel.desktop.navigator.templateTree.langRefresh();
				mainPanel.desktop.navigator.trashTree.langRefresh();
				mainPanel.desktop.navigator.thesaurusTree.thesaurusSelectPopup.langRefresh();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Main.get().showError("getFrontEndTranslations", caught);
			}
		});
		
		
	}
	
	/**
	 * Sets the lang map values
	 * 
	 * @param hI18n
	 */
	public void setLangMap(Map<String,String> hI18n) {
		this.hI18n = hI18n;
	}
	
	/**
	 * Gets the actual lang
	 * 
	 * @return lang The language code
	 */
	public String getLang() {
		return lang;
	}
	
	/**
	 * Shows popup error message ( unique entry point for error on all application )
	 * 
	 * @param okme The exception error
	 */
	public void showError(String callback, Throwable caught) {
		startUp.recoverFromError();
		if (caught instanceof OKMException) {
			OKMException okme = (OKMException) caught;
			Log.error("OKMException("+callback+"): "+okme.getCode());
			errorPopup.show(okme.getCode()+"("+callback+"): "+i18n(okme.getCode()) + "<br><br>" + okme.getMessage());
		} else if (caught instanceof InvocationException) {
			InvocationException ie = (InvocationException) caught;
			Log.error("InvocationException("+callback+"): "+ie);
			//errorPopupLogout.show(Main.i18n("error.invocation")+" ("+callback+")");
			errorPopup.show(Main.i18n("error.invocation")+" ("+callback+")");
		} else if (caught instanceof StatusCodeException) {
			StatusCodeException ie = (StatusCodeException) caught;
			Log.error("StatusCodeException("+callback+"): "+ie + " <br>HTTP status code error:"+ie.getStatusCode());
			//errorPopupLogout.show(Main.i18n("error.invocation")+" ("+callback+")");
			mainPanel.bottomPanel.setStatus("status.network.error.detected", true, ie.getStatusCode());
		} else {
			Log.error("UnknownException("+callback+"): "+caught.getMessage());
			//errorPopupLogout.show(callback+": "+caught.getMessage());
			errorPopup.show(callback+": "+caught.getMessage());
		}
	}
	
	/**
	 * Gets the i18n param translation
	 * 
	 * @param properties The propetie code locator
	 * @return The translated value
	 */
	public static String i18n(String property) {
		// All frontend properties starts with frontend.
		String ret = (String) Main.get().hI18n.get("frontend."+property);
		
		if (ret == null) {
			ret = property;
		}
		
		return ret;
	}
	
	/**
	 * Gets the i18n extension param translation
	 * 
	 * @param properties The propetier code locator
	 * @return The translated value
	 */
	public String i18nExtension(String property) {
		// All extension properties starts with extension.
		String ret = (String) Main.get().hI18n.get("extension."+property);
		
		if (ret == null) {
			ret = property;
		}
		
		return ret;
	}
	
	/**
	 * getExtensionUuidList
	 * 
	 * @return
	 */
	public List<String> getExtensionUuidList() {
		return extensionUuidList;
	}

	/**
	 * setExtensionUuidList
	 * 
	 * @param extensionUuidList
	 */
	public void setExtensionUuidList(List<String> extensionUuidList) {
		this.extensionUuidList = extensionUuidList;
	}
	
	@Override
	public void addLanguageHandlerExtension(LanguageHandlerExtension handlerExtension) {
		langHandlerExtensionList.add(handlerExtension);
	}
	
	@Override
	public void fireEvent(LanguageEventConstant event) {
		for (Iterator<LanguageHandlerExtension> it = langHandlerExtensionList.iterator(); it.hasNext();) {
			it.next().onChange(event);
		}
	}
	
	/**
	 * initJavaScriptApi
	 */
	native void initJavaScriptApi() /*-{
		// define a static JS function with a friendly name
		$wnd.i18n = function(s) {
		 	return @com.openkm.frontend.client.Main::i18n(Ljava/lang/String;)(s);
		};
	}-*/;
}