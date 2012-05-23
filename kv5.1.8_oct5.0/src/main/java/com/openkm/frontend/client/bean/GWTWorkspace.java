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

package com.openkm.frontend.client.bean;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * GWTWorkspace
 * 
 * @author jllort
 *
 */
public class GWTWorkspace implements IsSerializable {
	private String token = "";
	private String user = "";
	private List<String> roleList = new ArrayList<String>();
	private String applicationURL = "";
	private String imapHost = "";
	private String imapUser = "";
	private String imapPassword = "";
	private String imapFolder = "";
	private int imapID = -1;
	private String password = "";
	private boolean changePassword = true;
	private String email = "";
	private String webSkin = "";
	private boolean adminRole = false;
	private String previewer = "";
	private List<GWTReport> reports = new ArrayList<GWTReport>();

	// System wide
	private String appVersion = "";
	private String workflowRunConfigForm = "";
	private String workflowProcessIntanceVariableUUID = "";
	private String workflowProcessIntanceVariablePath = "";
	private long keepAliveSchedule;
	private long dashboardSchedule;
	
	// User Profile
	private boolean advancedFilters;
	private boolean chatEnabled;
	private boolean chatAutoLogin;
	private long userQuotaLimit;
	private boolean printPreview;
	private boolean keywordEnabled;
	private boolean userQuotaEnabled;
	private boolean webdavFix;
	private List<GWTPropertyGroup> wizardPropertyGroupsList = new ArrayList<GWTPropertyGroup>();
	private List<Double> wizardWorkflowsList = new ArrayList<Double>();
	private boolean wizardPropertyGroups;
	private boolean wizardWorkflows;
	private boolean wizardKeywords;
	private boolean wizardCategories;
	private boolean stackTaxonomy;
	private boolean stackCategoriesVisible;
	private boolean stackThesaurusVisible;
	private boolean stackTemplatesVisible;
	private boolean stackPersonalVisible;
	private boolean stackMailVisible;
	private boolean stackTrashVisible;
	private boolean menuFileVisible;
	private boolean menuEditVisible;
	private boolean menuToolsVisible;
	private boolean menuBookmarksVisible;
	private boolean menuHelpVisible;
	private boolean tabDesktopVisible;
	private boolean tabSearchVisible;
	private boolean tabDashboardVisible;
	private boolean tabAdminVisible;
	private boolean dashboardUserVisible;
	private boolean dashboardMailVisible;
	private boolean dashboardNewsVisible;
	private boolean dashboardGeneralVisible;
	private boolean dashboardWorkflowVisible;
	private boolean dashboardKeywordsVisible;
	private boolean tabDocumentPropertiesVisible;
	private boolean tabDocumentNotesVisible;
	private boolean tabDocumentSecurityVisible;
	private boolean tabDocumentVersionVisible;
	private boolean tabDocumentPreviewVisible;
	private boolean tabDocumentPropertyGroupsVisible;
	private boolean tabFolderPropertiesVisible;
	private boolean tabFolderSecurityVisible;
	private boolean tabFolderNotesVisible;
	private boolean tabMailPropertiesVisible;
	private boolean tabMailSecurityVisible;
	private GWTAvailableOption availableOption = new GWTAvailableOption();
	private List<GWTLanguage> langs = new ArrayList<GWTLanguage>();

	/**
	 * GWTWorkspace
	 */
	public GWTWorkspace() {
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getApplicationURL() {
		return applicationURL;
	}

	public void setApplicationURL(String applicationURL) {
		this.applicationURL = applicationURL;
	}

	public String getImapHost() {
		return imapHost;
	}

	public void setImapHost(String imapHost) {
		this.imapHost = imapHost;
	}

	public String getImapUser() {
		return imapUser;
	}

	public void setImapUser(String imapUser) {
		this.imapUser = imapUser;
	}

	public String getImapPassword() {
		return imapPassword;
	}

	public void setImapPassword(String imapPassword) {
		this.imapPassword = imapPassword;
	}

	public String getImapFolder() {
		return imapFolder;
	}

	public void setImapFolder(String imapFolder) {
		this.imapFolder = imapFolder;
	}

	public int getImapID() {
		return imapID;
	}

	public void setImapID(int imapID) {
		this.imapID = imapID;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isChangePassword() {
		return changePassword;
	}

	public void setChangePassword(boolean changePassword) {
		this.changePassword = changePassword;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	/**
	 * System Wide
	 */
	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getWorkflowRunConfigForm() {
		return workflowRunConfigForm;
	}

	public void setWorkflowRunConfigForm(String workflowRunConfigForm) {
		this.workflowRunConfigForm = workflowRunConfigForm;
	}

	public String getWorkflowProcessIntanceVariableUUID() {
		return workflowProcessIntanceVariableUUID;
	}

	public void setWorkflowProcessIntanceVariableUUID(String workflowProcessIntanceVariableUUID) {
		this.workflowProcessIntanceVariableUUID = workflowProcessIntanceVariableUUID;
	}

	public String getWorkflowProcessIntanceVariablePath() {
		return workflowProcessIntanceVariablePath;
	}

	public void setWorkflowProcessIntanceVariablePath(String workflowProcessIntanceVariablePath) {
		this.workflowProcessIntanceVariablePath = workflowProcessIntanceVariablePath;
	}

	public boolean isChatEnabled() {
		return chatEnabled;
	}

	public void setChatEnabled(boolean chatEnabled) {
		this.chatEnabled = chatEnabled;
	}

	public boolean isChatAutoLogin() {
		return chatAutoLogin;
	}

	public void setChatAutoLogin(boolean chatAutoLogin) {
		this.chatAutoLogin = chatAutoLogin;
	}
	
	public long getKeepAliveSchedule() {
		return keepAliveSchedule;
	}

	public void setKeepAliveSchedule(long keepAliveSchedule) {
		this.keepAliveSchedule = keepAliveSchedule;
	}

	public long getDashboardSchedule() {
		return dashboardSchedule;
	}

	public void setDashboardSchedule(long dashboardSchedule) {
		this.dashboardSchedule = dashboardSchedule;
	}
	
	/**
	 * User Profile
	 */
	public boolean isAdvancedFilters() {
		return advancedFilters;
	}

	public void setAdvancedFilters(boolean advancedFilters) {
		this.advancedFilters = advancedFilters;
	}

	public long getUserQuotaLimit() {
		return userQuotaLimit;
	}

	public void setUserQuotaLimit(long userQuotaLimit) {
		this.userQuotaLimit = userQuotaLimit;
	}
	
	public boolean isPrintPreview() {
		return printPreview;
	}

	public void setPrintPreview(boolean printPreview) {
		this.printPreview = printPreview;
	}
	
	public boolean isKeywordEnabled() {
		return keywordEnabled;
	}

	public void setKeywordEnabled(boolean keywordEnabled) {
		this.keywordEnabled = keywordEnabled;
	}

	public boolean isUserQuotaEnabled() {
		return userQuotaEnabled;
	}

	public void setUserQuotaEnabled(boolean userQuotaEnabled) {
		this.userQuotaEnabled = userQuotaEnabled;
	}
	
	public boolean isWebdavFix() {
		return webdavFix;
	}

	public void setWebdavFix(boolean webdavFix) {
		this.webdavFix = webdavFix;
	}

	public boolean isWizardPropertyGroups() {
		return wizardPropertyGroups;
	}

	public void setWizardPropertyGroups(boolean wizardPropertyGroups) {
		this.wizardPropertyGroups = wizardPropertyGroups;
	}
	
	public List<GWTPropertyGroup> getWizardPropertyGroupsList() {
		return wizardPropertyGroupsList;
	}

	public void setWizardPropertyGroupsList(List<GWTPropertyGroup> wizardPropertyGroupsList) {
		this.wizardPropertyGroupsList = wizardPropertyGroupsList;
	}
	
	public List<Double> getWizardWorkflowsList() {
		return wizardWorkflowsList;
	}

	public void setWizardWorkflowsList(List<Double> wizardWorkflowsList) {
		this.wizardWorkflowsList = wizardWorkflowsList;
	}

	public boolean isWizardWorkflows() {
		return wizardWorkflows;
	}

	public void setWizardWorkflows(boolean wizardWorkflows) {
		this.wizardWorkflows = wizardWorkflows;
	}

	public boolean isWizardKeywords() {
		return wizardKeywords;
	}

	public void setWizardKeywords(boolean wizardKeywords) {
		this.wizardKeywords = wizardKeywords;
	}

	public boolean isWizardCategories() {
		return wizardCategories;
	}

	public void setWizardCategories(boolean wizardCategories) {
		this.wizardCategories = wizardCategories;
	}
	
	public boolean isStackTaxonomy() {
		return stackTaxonomy;
	}

	public void setStackTaxonomy(boolean stackTaxonomy) {
		this.stackTaxonomy = stackTaxonomy;
	}

	public boolean isStackCategoriesVisible() {
		return stackCategoriesVisible;
	}

	public void setStackCategoriesVisible(boolean stackCategoriesVisible) {
		this.stackCategoriesVisible = stackCategoriesVisible;
	}

	public boolean isStackThesaurusVisible() {
		return stackThesaurusVisible;
	}

	public void setStackThesaurusVisible(boolean stackThesaurusVisible) {
		this.stackThesaurusVisible = stackThesaurusVisible;
	}
	
	public boolean isStackTemplatesVisible() {
		return stackTemplatesVisible;
	}

	public void setStackTemplatesVisible(boolean stackTemplatesVisible) {
		this.stackTemplatesVisible = stackTemplatesVisible;
	}

	public boolean isStackPersonalVisible() {
		return stackPersonalVisible;
	}

	public void setStackPersonalVisible(boolean stackPersonalVisible) {
		this.stackPersonalVisible = stackPersonalVisible;
	}

	public boolean isStackMailVisible() {
		return stackMailVisible;
	}

	public void setStackMailVisible(boolean stackMailVisible) {
		this.stackMailVisible = stackMailVisible;
	}
	
	public boolean isStackTrashVisible() {
		return stackTrashVisible;
	}

	public void setStackTrashVisible(boolean stackTrashVisible) {
		this.stackTrashVisible = stackTrashVisible;
	}
	
	public boolean isMenuFileVisible() {
		return menuFileVisible;
	}

	public void setMenuFileVisible(boolean menuFileVisible) {
		this.menuFileVisible = menuFileVisible;
	}

	public boolean isMenuEditVisible() {
		return menuEditVisible;
	}

	public void setMenuEditVisible(boolean menuEditVisible) {
		this.menuEditVisible = menuEditVisible;
	}

	public boolean isMenuToolsVisible() {
		return menuToolsVisible;
	}

	public void setMenuToolsVisible(boolean menuToolsVisible) {
		this.menuToolsVisible = menuToolsVisible;
	}

	public boolean isMenuBookmarksVisible() {
		return menuBookmarksVisible;
	}

	public void setMenuBookmarksVisible(boolean menuBookmarksVisible) {
		this.menuBookmarksVisible = menuBookmarksVisible;
	}

	public boolean isMenuHelpVisible() {
		return menuHelpVisible;
	}

	public void setMenuHelpVisible(boolean menuHelpVisible) {
		this.menuHelpVisible = menuHelpVisible;
	}

	public boolean isTabDesktopVisible() {
		return tabDesktopVisible;
	}

	public void setTabDesktopVisible(boolean tabDesktopVisible) {
		this.tabDesktopVisible = tabDesktopVisible;
	}

	public boolean isTabSearchVisible() {
		return tabSearchVisible;
	}

	public void setTabSearchVisible(boolean tabSearchVisible) {
		this.tabSearchVisible = tabSearchVisible;
	}

	public boolean isTabDashboardVisible() {
		return tabDashboardVisible;
	}

	public void setTabDashboardVisible(boolean tabDashboardVisible) {
		this.tabDashboardVisible = tabDashboardVisible;
	}
	
	public boolean isTabAdminVisible() {
		return tabAdminVisible;
	}

	public void setTabAdminVisible(boolean tabAdminVisible) {
		this.tabAdminVisible = tabAdminVisible;
	}

	public boolean isDashboardUserVisible() {
		return dashboardUserVisible;
	}

	public void setDashboardUserVisible(boolean dashboardUserVisible) {
		this.dashboardUserVisible = dashboardUserVisible;
	}

	public boolean isDashboardMailVisible() {
		return dashboardMailVisible;
	}

	public void setDashboardMailVisible(boolean dashboardMailVisible) {
		this.dashboardMailVisible = dashboardMailVisible;
	}

	public boolean isDashboardNewsVisible() {
		return dashboardNewsVisible;
	}

	public void setDashboardNewsVisible(boolean dashboardNewsVisible) {
		this.dashboardNewsVisible = dashboardNewsVisible;
	}

	public boolean isDashboardGeneralVisible() {
		return dashboardGeneralVisible;
	}

	public void setDashboardGeneralVisible(boolean dashboardGeneralVisible) {
		this.dashboardGeneralVisible = dashboardGeneralVisible;
	}

	public boolean isDashboardWorkflowVisible() {
		return dashboardWorkflowVisible;
	}

	public void setDashboardWorkflowVisible(boolean dashboardWorkflowVisible) {
		this.dashboardWorkflowVisible = dashboardWorkflowVisible;
	}

	public boolean isDashboardKeywordsVisible() {
		return dashboardKeywordsVisible;
	}

	public void setDashboardKeywordsVisible(boolean dashboardKeywordsVisible) {
		this.dashboardKeywordsVisible = dashboardKeywordsVisible;
	}

	public GWTAvailableOption getAvailableOption() {
		return availableOption;
	}

	public void setAvailableOption(GWTAvailableOption availableOption) {
		this.availableOption = availableOption;
	}
	
	public List<String> getRoleList() {
		return roleList;
	}

	public void setRoleList(List<String> roleList) {
		this.roleList = roleList;
	}
	
	public boolean isTabDocumentPropertiesVisible() {
		return tabDocumentPropertiesVisible;
	}

	public void setTabDocumentPropertiesVisible(boolean tabDocumentProperties) {
		this.tabDocumentPropertiesVisible = tabDocumentProperties;
	}

	public boolean isTabDocumentNotesVisible() {
		return tabDocumentNotesVisible;
	}

	public void setTabDocumentNotesVisible(boolean tabDocumentNotes) {
		this.tabDocumentNotesVisible = tabDocumentNotes;
	}

	public boolean isTabDocumentSecurityVisible() {
		return tabDocumentSecurityVisible;
	}

	public void setTabDocumentSecurityVisible(boolean tabDocumentSecurity) {
		this.tabDocumentSecurityVisible = tabDocumentSecurity;
	}

	public boolean isTabDocumentVersionVisible() {
		return tabDocumentVersionVisible;
	}

	public void setTabDocumentVersionVisible(boolean tabDocumentVersion) {
		this.tabDocumentVersionVisible = tabDocumentVersion;
	}

	public boolean isTabDocumentPreviewVisible() {
		return tabDocumentPreviewVisible;
	}

	public void setTabDocumentPreviewVisible(boolean tabDocumentPreview) {
		this.tabDocumentPreviewVisible = tabDocumentPreview;
	}

	public boolean isTabDocumentPropertyGroupsVisible() {
		return tabDocumentPropertyGroupsVisible;
	}

	public void setTabDocumentPropertyGroupsVisible(boolean tabDocumentPropertyGroups) {
		this.tabDocumentPropertyGroupsVisible = tabDocumentPropertyGroups;
	}
	
	public boolean isTabFolderPropertiesVisible() {
		return tabFolderPropertiesVisible;
	}

	public void setTabFolderPropertiesVisible(boolean tabFolderPropertiesVisible) {
		this.tabFolderPropertiesVisible = tabFolderPropertiesVisible;
	}

	public boolean isTabFolderSecurityVisible() {
		return tabFolderSecurityVisible;
	}

	public void setTabFolderSecurityVisible(boolean tabFolderSecurityVisible) {
		this.tabFolderSecurityVisible = tabFolderSecurityVisible;
	}
	
	public boolean isTabFolderNotesVisible() {
		return tabFolderNotesVisible;
	}

	public void setTabFolderNotesVisible(boolean tabFolderNotesVisible) {
		this.tabFolderNotesVisible = tabFolderNotesVisible;
	}
	
	public boolean isTabMailPropertiesVisible() {
		return tabMailPropertiesVisible;
	}

	public void setTabMailPropertiesVisible(boolean tabMailPropertiesVisible) {
		this.tabMailPropertiesVisible = tabMailPropertiesVisible;
	}

	public boolean isTabMailSecurityVisible() {
		return tabMailSecurityVisible;
	}

	public void setTabMailSecurityVisible(boolean tabMailSecurityVisible) {
		this.tabMailSecurityVisible = tabMailSecurityVisible;
	}
	
	public String getWebSkin() {
		return webSkin;
	}

	public void setWebSkin(String webSkin) {
		this.webSkin = webSkin;
	}
	
	public boolean isAdminRole() {
		return adminRole;
	}

	public void setAdminRole(boolean adminRole) {
		this.adminRole = adminRole;
	}
	
	public String getPreviewer() {
		return previewer;
	}

	public void setPreviewer(String previewer) {
		this.previewer = previewer;
	}
	
	public List<GWTLanguage> getLangs() {
		return langs;
	}

	public void setLangs(List<GWTLanguage> langs) {
		this.langs = langs;
	}
	
	public List<GWTReport> getReports() {
		return reports;
	}

	public void setReports(List<GWTReport> reports) {
		this.reports = reports;
	}
}
