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

package com.openkm.module;

public class ModuleManager {
	private static AuthModule authModule = null;
	private static RepositoryModule repositoryModule = null;
	private static FolderModule folderModule = null;
	private static DocumentModule documentModule = null;
	private static NoteModule noteModule = null;
	private static SearchModule searchModule = null;
	private static PropertyGroupModule propertyGroupModule= null;
	private static NotificationModule notificationModule = null;
	private static BookmarkModule bookmarkModule = null;
	private static DashboardModule dashboardModule = null;
	private static WorkflowModule workflowModule = null;
	private static ScriptingModule scriptingModule = null;
	private static StatsModule statsModule = null;
	private static MailModule mailModule = null;
	private static PropertyModule propertyModule = null;
	private static UserConfigModule userConfigModule = null;
	
	/**
	 * 
	 */
	public static synchronized AuthModule getAuthModule() {
		if (authModule == null) {
			authModule = new com.openkm.module.direct.DirectAuthModule();
			//authModule = new com.openkm.module.ejb.EJBAuthModule(); 
		}
		
		return authModule;
	}

	/**
	 * 
	 */
	public static synchronized RepositoryModule getRepositoryModule() {
		if (repositoryModule == null) {
			repositoryModule = new com.openkm.module.direct.DirectRepositoryModule();
			//repositoryModule = new com.openkm.module.ejb.EJBRepositoryModule(); 
		}
		
		return repositoryModule;
	}

	/**
	 * 
	 */
	public static synchronized FolderModule getFolderModule() {
		if (folderModule == null) {
			folderModule = new com.openkm.module.direct.DirectFolderModule();
			//folderModule = new com.openkm.module.ejb.EJBFolderModule(); 
		}
		
		return folderModule;
	}

	/**
	 * 
	 */
	public static synchronized DocumentModule getDocumentModule() {
		if (documentModule == null) {
			documentModule = new com.openkm.module.direct.DirectDocumentModule();
			//documentModule = new com.openkm.module.ejb.EJBDocumentModule(); 
		}
		
		return documentModule;
	}
	
	/**
	 * 
	 */
	public static synchronized NoteModule getNoteModule() {
		if (noteModule == null) {
			noteModule = new com.openkm.module.direct.DirectNoteModule();
			//noteModule = new com.openkm.module.ejb.EJBNoteModule(); 
		}
		
		return noteModule;
	}

	/**
	 * 
	 */
	public static synchronized SearchModule getSearchModule() {
		if (searchModule == null) {
			searchModule = new com.openkm.module.direct.DirectSearchModule();
			//searchModule = new com.openkm.module.ejb.EJBSearchModule(); 
		}
		
		return searchModule;
	}
	
	/**
	 * 
	 */
	public static synchronized PropertyGroupModule getPropertyGroupModule() {
		if (propertyGroupModule == null) {
			propertyGroupModule = new com.openkm.module.direct.DirectPropertyGroupModule();
			//propertyGroupModule = new com.openkm.module.ejb.EJBPropertyGroupModule(); 
		}
		
		return propertyGroupModule;
	}	

	/**
	 * 
	 */
	public static synchronized NotificationModule getNotificationModule() {
		if (notificationModule == null) {
			notificationModule = new com.openkm.module.direct.DirectNotificationModule();
			//notificationModule = new com.openkm.module.ejb.EJBNotificationModule();
		}
		
		return notificationModule;
	}
	
	/**
	 * 
	 */
	public static synchronized BookmarkModule getBookmarkModule() {
		if (bookmarkModule == null) {
			bookmarkModule = new com.openkm.module.direct.DirectBookmarkModule();
			//bookmarkModule = new com.openkm.module.ejb.EJBBookmarkModule();
		}
		
		return bookmarkModule;
	}
	
	/**
	 * 
	 */
	public static synchronized DashboardModule getDashboardModule() {
		if (dashboardModule == null) {
			dashboardModule = new com.openkm.module.direct.DirectDashboardModule();
			//dashboardModule = new com.openkm.module.ejb.EJBDashboardModule();
		}
		
		return dashboardModule;
	}
	
	/**
	 * 
	 */
	public static synchronized WorkflowModule getWorkflowModule() {
		if (workflowModule == null) {
			workflowModule = new com.openkm.module.direct.DirectWorkflowModule();
			//workflowModule = new com.openkm.module.ejb.EJBWorkflowModule();
		}
		
		return workflowModule;
	}
	
	/**
	 * 
	 */
	public static synchronized ScriptingModule getScriptingModule() {
		if (scriptingModule == null) {
			scriptingModule = new com.openkm.module.direct.DirectScriptingModule();
			//scriptingModule = new com.openkm.module.ejb.EJBScriptingModule();
		}
		
		return scriptingModule;
	}

	/**
	 * 
	 */
	public static synchronized StatsModule getStatsModule() {
		if (statsModule == null) {
			statsModule = new com.openkm.module.direct.DirectStatsModule();
			//scriptingModule = new com.openkm.module.ejb.EJBStatsModule();
		}
		
		return statsModule;
	}

	/**
	 * 
	 */
	public static synchronized MailModule getMailModule() {
		if (mailModule == null) {
			mailModule = new com.openkm.module.direct.DirectMailModule();
			//mailModule = new com.openkm.module.ejb.EJBMailModule();
		}
		
		return mailModule;
	}
	
	/**
	 * 
	 */
	public static synchronized PropertyModule getPropertyModule() {
		if (propertyModule == null) {
			propertyModule = new com.openkm.module.direct.DirectPropertyModule();
			//propertyModule = new com.openkm.module.ejb.EJBPropertyModule();
		}
		
		return propertyModule;
	}
	
	/**
	 * 
	 */
	public static synchronized UserConfigModule getUserConfigModule() {
		if (userConfigModule == null) {
			userConfigModule = new com.openkm.module.direct.DirectUserConfigModule();
			//propertyModule = new com.openkm.module.ejb.EJBPropertyModule();
		}
		
		return userConfigModule;
	}
}
