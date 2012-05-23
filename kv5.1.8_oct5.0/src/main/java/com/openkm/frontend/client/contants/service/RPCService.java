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

package com.openkm.frontend.client.contants.service;

import com.openkm.frontend.client.Main;

/**
 * RPC Service General configuration
 * 
 * @author jllort
 */
public class RPCService {
	
	// Service entry point 
	public static String FolderService = Main.CONTEXT + "/frontend/Folder";
	public static String DocumentService = Main.CONTEXT + "/frontend/Document";
	public static String DownloadServlet = Main.CONTEXT + "/frontend/Download";
	public static String ConverterServlet = Main.CONTEXT + "/frontend/Converter";
	public static String FileUploadService = Main.CONTEXT + "/frontend/FileUpload";
	public static String AuthService = Main.CONTEXT + "/frontend/Auth";
	public static String SearchService = Main.CONTEXT + "/frontend/Search";
	public static String PropertyGroupService = Main.CONTEXT + "/frontend/PropertyGroup";
	public static String NotifyService = Main.CONTEXT + "/frontend/Notify";
	public static String BookmarkService = Main.CONTEXT + "/frontend/Bookmark";
	public static String RepositoryService = Main.CONTEXT + "/frontend/Repository";
	public static String DashboardService = Main.CONTEXT + "/frontend/Dashboard";
	public static String WorkspaceService = Main.CONTEXT + "/frontend/Workspace";
	public static String WorkflowService = Main.CONTEXT + "/frontend/Workflow";
	public static String MailService = Main.CONTEXT + "/frontend/Mail";
	public static String GeneralService = Main.CONTEXT + "/frontend/General";
	public static String ThesaurusService = Main.CONTEXT + "/frontend/Thesaurus";
	public static String PropertyService = Main.CONTEXT + "/frontend/Property";
	public static String ChatService = Main.CONTEXT + "/frontend/Chat";
	public static String UserConfigService = Main.CONTEXT + "/frontend/UserConfig";
	public static String NoteService = Main.CONTEXT + "/frontend/Note";
	public static String LanguageService = Main.CONTEXT + "/frontend/Language";
	public static String FeedService = Main.CONTEXT + "/feed/";
	public static String StaplingService = Main.CONTEXT + "/extension/Stapling";
	public static String StaplingDownloadService = Main.CONTEXT + "/extension/StaplingDownload";
	public static String ProposeSubscriptionService = Main.CONTEXT + "/extension/ProposedSubscription";
	public static String ProposeQueryService = Main.CONTEXT + "/extension/ProposedQuery";
	public static String MessageService = Main.CONTEXT + "/extension/Message";
	public static String StampService = Main.CONTEXT + "/extension/Stamp";
	public static String ContactService = Main.CONTEXT + "/extension/Contact";
	public static String ActivityLogService = Main.CONTEXT + "/extension/ActivityLog";
	public static String TestService = Main.CONTEXT + "/frontend/Test";
	public static String ReportServlet = Main.CONTEXT + "/frontend/ExecuteReport";
}
