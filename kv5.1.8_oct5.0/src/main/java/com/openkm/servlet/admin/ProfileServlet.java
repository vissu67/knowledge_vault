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

package com.openkm.servlet.admin;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;

import javax.jcr.Session;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.api.OKMPropertyGroup;
import com.openkm.api.OKMWorkflow;
import com.openkm.core.DatabaseException;
import com.openkm.core.ParseException;
import com.openkm.core.RepositoryException;
import com.openkm.core.WorkflowException;
import com.openkm.dao.ProfileDAO;
import com.openkm.dao.ReportDAO;
import com.openkm.dao.bean.Profile;
import com.openkm.extension.dao.ExtensionDAO;
import com.openkm.jcr.JCRUtils;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;

/**
 * User profiles servlet
 */
public class ProfileServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(ProfileServlet.class);
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("doGet({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		Session session = null;
		updateSessionManager(request);
		
		try {
			session = JCRUtils.getSession();
			
			if (action.equals("create")) {
				create(session, request, response);
			} else if (action.equals("edit")) {
				edit(session, request, response);
			} else if (action.equals("delete")) {
				delete(session, request, response);
			}
			
			if (action.equals("") || WebUtils.getBoolean(request, "persist")) {
				list(session, request, response);
			}
		} catch (javax.jcr.LoginException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (WorkflowException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} finally {
			JCRUtils.logout(session);
		}
	}
	
	/**
	 * New user
	 */
	private void create(Session session, HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException, DatabaseException, RepositoryException, ParseException,
			WorkflowException {
		log.debug("create({}, {}, {})", new Object[] { session, request, response });
		
		if (WebUtils.getBoolean(request, "persist")) {
			Profile prf = getUserProfile(request);
			int id = ProfileDAO.create(prf);
			
			// Activity log
			UserActivity.log(session.getUserID(), "ADMIN_USER_PROFILE_CREATE", Integer.toString(id), prf.toString());
		} else {
			ServletContext sc = getServletContext();
			Profile prf = new Profile();
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("exts", ExtensionDAO.findAll());
			sc.setAttribute("reps", ReportDAO.findAll());
			sc.setAttribute("pgroups", OKMPropertyGroup.getInstance().getAllGroups(null));
			sc.setAttribute("wflows", OKMWorkflow.getInstance().findAllProcessDefinitions(null));
			sc.setAttribute("prf", prf);
			sc.getRequestDispatcher("/admin/profile_edit.jsp").forward(request, response);
		}
		
		log.debug("create: void");
	}
	
	/**
	 * Edit user
	 */
	private void edit(Session session, HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException, DatabaseException, NoSuchAlgorithmException,
			RepositoryException, ParseException, WorkflowException {
		log.debug("edit({}, {}, {})", new Object[] { session, request, response });
		
		if (WebUtils.getBoolean(request, "persist")) {
			Profile prf = getUserProfile(request);
			ProfileDAO.update(prf);
			
			// Activity log
			UserActivity.log(session.getUserID(), "ADMIN_USER_PROFILE_EDIT", Integer.toString(prf.getId()), prf.toString());
		} else {
			ServletContext sc = getServletContext();
			int prfId = WebUtils.getInt(request, "prf_id");
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("exts", ExtensionDAO.findAll());
			sc.setAttribute("reps", ReportDAO.findAll());
			sc.setAttribute("pgroups", OKMPropertyGroup.getInstance().getAllGroups(null));
			sc.setAttribute("wflows", OKMWorkflow.getInstance().findAllProcessDefinitions(null));
			sc.setAttribute("prf", ProfileDAO.findByPk(prfId));
			sc.getRequestDispatcher("/admin/profile_edit.jsp").forward(request, response);
		}
		
		log.debug("edit: void");
	}

	/**
	 * Update user
	 */
	private void delete(Session session, HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException, DatabaseException, NoSuchAlgorithmException,
			RepositoryException, ParseException, WorkflowException {
		log.debug("delete({}, {}, {})", new Object[] { session, request, response });
		
		if (WebUtils.getBoolean(request, "persist")) {
			int prfId = WebUtils.getInt(request, "prf_id");
			ProfileDAO.delete(prfId);
			
			// Activity log
			UserActivity.log(session.getUserID(), "ADMIN_USER_PROFILE_DELETE", Integer.toString(prfId), null);
		} else {
			ServletContext sc = getServletContext();
			int prfId = WebUtils.getInt(request, "prf_id");
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("exts", ExtensionDAO.findAll());
			sc.setAttribute("reps", ReportDAO.findAll());
			sc.setAttribute("pgroups", OKMPropertyGroup.getInstance().getAllGroups(null));
			sc.setAttribute("wflows", OKMWorkflow.getInstance().findAllProcessDefinitions(null));
			sc.setAttribute("prf", ProfileDAO.findByPk(prfId));
			sc.getRequestDispatcher("/admin/profile_edit.jsp").forward(request, response);
		}
		
		log.debug("delete: void");
	}

	/**
	 * List user profiles
	 */
	private void list(Session session, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException {
		log.debug("list({}, {}, {})", new Object[] { session, request, response });
		ServletContext sc = getServletContext();
		sc.setAttribute("userProfiles", ProfileDAO.findAll(false));
		sc.getRequestDispatcher("/admin/profile_list.jsp").forward(request, response);
		log.debug("list: void");
	}
	
	/**
	 * Fille user profile object
	 */
	private Profile getUserProfile(HttpServletRequest request) {
		Profile prf = new Profile();
		
		prf.setId(WebUtils.getInt(request, "prf_id"));
		prf.setName(WebUtils.getString(request, "prf_name"));
		prf.setActive(WebUtils.getBoolean(request, "prf_active"));
		
		// Misc
		prf.getMisc().setUserQuota(WebUtils.getLong(request, "prf_misc_user_quota"));
		prf.getMisc().setAdvancedFilters(WebUtils.getBoolean(request, "prf_misc_advanced_filter"));
		prf.getMisc().setWebSkin(WebUtils.getString(request, "prf_misc_web_skin"));
		prf.getMisc().setPrintPreview(WebUtils.getBoolean(request, "prf_misc_print_preview"));
		prf.getMisc().setKeywordsEnabled(WebUtils.getBoolean(request, "prf_misc_keywords_enabled"));
		prf.getMisc().setExtensions(new HashSet<String>(WebUtils.getStringList(request, "prf_misc_extensions")));
		prf.getMisc().setReports(new HashSet<Integer>(WebUtils.getIntList(request, "prf_misc_reports")));
		
		// Wizard
		prf.getWizard().setKeywordsEnabled(WebUtils.getBoolean(request, "prf_wizard_keywords"));
		prf.getWizard().setCategoriesEnabled(WebUtils.getBoolean(request, "prf_wizard_categories"));
		prf.getWizard().setPropertyGroups(new HashSet<String>(WebUtils.getStringList(request, "prf_wizard_property_groups")));
		prf.getWizard().setWorkflows(new HashSet<String>(WebUtils.getStringList(request, "prf_wizard_workflows")));
		
		// Chat
		prf.getChat().setChatEnabled(WebUtils.getBoolean(request, "prf_chat_enabled"));
		prf.getChat().setAutoLoginEnabled(WebUtils.getBoolean(request, "prf_chat_auto_login"));
		
		// Stack
		prf.getStack().setTaxonomyVisible(WebUtils.getBoolean(request, "prf_stack_taxonomy_visible"));
		prf.getStack().setCategoriesVisible(WebUtils.getBoolean(request, "prf_stack_categories_visible"));
		prf.getStack().setThesaurusVisible(WebUtils.getBoolean(request, "prf_stack_thesaurus_visible"));
		prf.getStack().setTemplatesVisible(WebUtils.getBoolean(request, "prf_stack_templates_visible"));
		prf.getStack().setPersonalVisible(WebUtils.getBoolean(request, "prf_stack_personal_visible"));
		prf.getStack().setMailVisible(WebUtils.getBoolean(request, "prf_stack_mail_visible"));
		prf.getStack().setTrashVisible(WebUtils.getBoolean(request, "prf_stack_trash_visible"));
		
		// Menu
		prf.getMenu().setFileVisible(WebUtils.getBoolean(request, "prf_menu_file_visible"));
		prf.getMenu().setEditVisible(WebUtils.getBoolean(request, "prf_menu_edit_visible"));
		prf.getMenu().setToolsVisible(WebUtils.getBoolean(request, "prf_menu_tools_visible"));
		prf.getMenu().setBookmarksVisible(WebUtils.getBoolean(request, "prf_menu_bookmarks_visible"));
		prf.getMenu().setHelpVisible(WebUtils.getBoolean(request, "prf_menu_help_visible"));
		
		// Menu File
		prf.getMenu().getFile().setCreateFolderVisible(WebUtils.getBoolean(request, "prf_menu_file_create_folder_visible"));
		prf.getMenu().getFile().setFindFolderVisible(WebUtils.getBoolean(request, "prf_menu_file_find_folder_visible"));
		prf.getMenu().getFile().setGoFolderVisible(WebUtils.getBoolean(request, "prf_menu_file_go_folder_visible"));
		prf.getMenu().getFile().setDownloadVisible(WebUtils.getBoolean(request, "prf_menu_file_download_visible"));
		prf.getMenu().getFile().setDownloadPdfVisible(WebUtils.getBoolean(request, "prf_menu_file_download_pdf_visible"));
		prf.getMenu().getFile().setAddDocumentVisible(WebUtils.getBoolean(request, "prf_menu_file_add_document_visible"));
		prf.getMenu().getFile().setStartWorkflowVisible(WebUtils.getBoolean(request, "prf_menu_file_start_workflow_visible"));
		prf.getMenu().getFile().setRefreshVisible(WebUtils.getBoolean(request, "prf_menu_file_refresh_visible"));
		prf.getMenu().getFile().setScannerVisible(WebUtils.getBoolean(request, "prf_menu_file_scanner_visible"));
		prf.getMenu().getFile().setUploaderVisible(WebUtils.getBoolean(request, "prf_menu_file_uploader_visible"));
		prf.getMenu().getFile().setExportVisible(WebUtils.getBoolean(request, "prf_menu_file_export_visible"));
		prf.getMenu().getFile().setCreateFromTemplateVisible(WebUtils.getBoolean(request, "prf_menu_file_create_from_template_visible"));
		prf.getMenu().getFile().setPurgeVisible(WebUtils.getBoolean(request, "prf_menu_file_purge_visible"));
		prf.getMenu().getFile().setPurgeTrashVisible(WebUtils.getBoolean(request, "prf_menu_file_purge_trash_visible"));
		prf.getMenu().getFile().setRestoreVisible(WebUtils.getBoolean(request, "prf_menu_file_restore_visible"));
		prf.getMenu().getFile().setSendDocumentLinkVisible(WebUtils.getBoolean(request, "prf_menu_file_send_document_link_visible"));
		prf.getMenu().getFile().setSendDocumentAttachmentVisible(WebUtils.getBoolean(request, "prf_menu_file_send_document_attachment_visible"));

		// Menu Bookmarks
		prf.getMenu().getBookmark().setManageBookmarksVisible(WebUtils.getBoolean(request, "prf_menu_bookmark_manage_bookmarks_visible"));
		prf.getMenu().getBookmark().setAddBookmarkVisible(WebUtils.getBoolean(request, "prf_menu_bookmark_add_bookmark_visible"));
		prf.getMenu().getBookmark().setSetHomeVisible(WebUtils.getBoolean(request, "prf_menu_bookmark_set_home_visible"));
		prf.getMenu().getBookmark().setGoHomeVisible(WebUtils.getBoolean(request, "prf_menu_bookmark_go_home_visible"));
		
		// Menu Tools
		prf.getMenu().getTool().setLanguagesVisible(WebUtils.getBoolean(request, "prf_menu_tool_languages_visible"));
		prf.getMenu().getTool().setSkinVisible(WebUtils.getBoolean(request, "prf_menu_tool_skin_visible"));
		prf.getMenu().getTool().setDebugVisible(WebUtils.getBoolean(request, "prf_menu_tool_debug_visible"));
		prf.getMenu().getTool().setAdministrationVisible(WebUtils.getBoolean(request, "prf_menu_tool_administration_visible"));
		prf.getMenu().getTool().setPreferencesVisible(WebUtils.getBoolean(request, "prf_menu_tool_preferences_visible"));
		
		// Menu Edit
		prf.getMenu().getEdit().setRenameVisible(WebUtils.getBoolean(request, "prf_menu_edit_rename_visible"));
		prf.getMenu().getEdit().setCopyVisible(WebUtils.getBoolean(request, "prf_menu_edit_copy_visible"));
		prf.getMenu().getEdit().setMoveVisible(WebUtils.getBoolean(request, "prf_menu_edit_move_visible"));
		prf.getMenu().getEdit().setLockVisible(WebUtils.getBoolean(request, "prf_menu_edit_lock_visible"));
		prf.getMenu().getEdit().setUnlockVisible(WebUtils.getBoolean(request, "prf_menu_edit_unlock_visible"));
		prf.getMenu().getEdit().setCheckInVisible(WebUtils.getBoolean(request, "prf_menu_edit_check_in_visible"));
		prf.getMenu().getEdit().setCheckOutVisible(WebUtils.getBoolean(request, "prf_menu_edit_check_out_visible"));
		
		//added by vissu on feb19 for zohoapi
		prf.getMenu().getEdit().setZohoVisible(WebUtils.getBoolean(request, "prf_menu_edit_zoho_visible"));
		
		prf.getMenu().getEdit().setCancelCheckOutVisible(WebUtils.getBoolean(request, "prf_menu_edit_cancel_check_out_visible"));
		prf.getMenu().getEdit().setDeleteVisible(WebUtils.getBoolean(request, "prf_menu_edit_delete_visible"));
		prf.getMenu().getEdit().setAddPropertyGroupVisible(WebUtils.getBoolean(request, "prf_menu_edit_add_property_group_visible"));
		prf.getMenu().getEdit().setRemovePropertyGroupVisible(WebUtils.getBoolean(request, "prf_menu_edit_remove_property_group_visible"));
		prf.getMenu().getEdit().setAddSubscriptionVisible(WebUtils.getBoolean(request, "prf_menu_edit_add_subscription_visible"));
		prf.getMenu().getEdit().setRemoveSubscriptionVisible(WebUtils.getBoolean(request, "prf_menu_edit_remove_subscription_visible"));
		
		// Menu Help
		prf.getMenu().getHelp().setHelpVisible(WebUtils.getBoolean(request, "prf_menu_help_help_visible"));
		prf.getMenu().getHelp().setDocumentationVisible(WebUtils.getBoolean(request, "prf_menu_help_documentation_visible"));
		prf.getMenu().getHelp().setBugTrackingVisible(WebUtils.getBoolean(request, "prf_menu_help_bug_tracking_visible"));
		prf.getMenu().getHelp().setSupportVisible(WebUtils.getBoolean(request, "prf_menu_help_support_visible"));
		prf.getMenu().getHelp().setForumVisible(WebUtils.getBoolean(request, "prf_menu_help_forum_visible"));
		prf.getMenu().getHelp().setChangelogVisible(WebUtils.getBoolean(request, "prf_menu_help_changelog_visible"));
		prf.getMenu().getHelp().setWebSiteVisible(WebUtils.getBoolean(request, "prf_menu_help_web_site_visible"));
		prf.getMenu().getHelp().setAboutVisible(WebUtils.getBoolean(request, "prf_menu_help_about_visible"));
		
		// Tab
		prf.getTab().setDesktopVisible(WebUtils.getBoolean(request, "prf_tab_desktop_visible"));
		prf.getTab().setSearchVisible(WebUtils.getBoolean(request, "prf_tab_search_visible"));
		prf.getTab().setDashboardVisible(WebUtils.getBoolean(request, "prf_tab_dashboard_visible"));
		prf.getTab().setAdministrationVisible(WebUtils.getBoolean(request, "prf_tab_administration_visible"));
		
		// Tab Document
		prf.getTab().getDocument().setPropertiesVisible(WebUtils.getBoolean(request, "prf_tab_document_properties_visible"));
		prf.getTab().getDocument().setSecurityVisible(WebUtils.getBoolean(request, "prf_tab_document_security_visible"));
		prf.getTab().getDocument().setNotesVisible(WebUtils.getBoolean(request, "prf_tab_document_notes_visible"));
		prf.getTab().getDocument().setVersionsVisible(WebUtils.getBoolean(request, "prf_tab_document_versions_visible"));
		prf.getTab().getDocument().setPreviewVisible(WebUtils.getBoolean(request, "prf_tab_document_preview_visible"));
		prf.getTab().getDocument().setPropertyGroupsVisible(WebUtils.getBoolean(request, "prf_tab_document_property_groups_visible"));

		// Tab Folder
		prf.getTab().getFolder().setPropertiesVisible(WebUtils.getBoolean(request, "prf_tab_folder_properties_visible"));
		prf.getTab().getFolder().setSecurityVisible(WebUtils.getBoolean(request, "prf_tab_folder_security_visible"));
		prf.getTab().getFolder().setNotesVisible(WebUtils.getBoolean(request, "prf_tab_folder_notes_visible"));

		// Tab Mail
		prf.getTab().getMail().setPropertiesVisible(WebUtils.getBoolean(request, "prf_tab_mail_properties_visible"));
		prf.getTab().getMail().setSecurityVisible(WebUtils.getBoolean(request, "prf_tab_mail_security_visible"));

		// Dashboard
		prf.getDashboard().setUserVisible(WebUtils.getBoolean(request, "prf_dashboard_user_visible"));
		prf.getDashboard().setMailVisible(WebUtils.getBoolean(request, "prf_dashboard_mail_visible"));
		prf.getDashboard().setNewsVisible(WebUtils.getBoolean(request, "prf_dashboard_news_visible"));
		prf.getDashboard().setGeneralVisible(WebUtils.getBoolean(request, "prf_dashboard_general_visible"));
		prf.getDashboard().setWorkflowVisible(WebUtils.getBoolean(request, "prf_dashboard_workflow_visible"));
		prf.getDashboard().setKeywordsVisible(WebUtils.getBoolean(request, "prf_dashboard_keywords_visible"));
				
		return prf;
	}
}
