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

package com.openkm.frontend.client.widget.mainmenu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTAvailableOption;
import com.openkm.frontend.client.bean.GWTLanguage;
import com.openkm.frontend.client.bean.GWTReport;
import com.openkm.frontend.client.bean.GWTWorkspace;
import com.openkm.frontend.client.bean.ToolBarOption;
import com.openkm.frontend.client.contants.ui.UIMenuConstants;
import com.openkm.frontend.client.extension.widget.menu.MenuItemExtension;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.ConfirmPopup;
import com.openkm.frontend.client.widget.notify.NotifyPopup;

/**
 * Main menu
 * 
 * @author jllort
 *
 */
public class MainMenu extends Composite {
	
	private static final int OUTPUT_PDF = 2;
	private static final int OUTPUT_RTF = 3;
	private static final int OUTPUT_CSV = 4;
	
	// URI CONSTANTS
	public static final String URI_HELP = "http://www.openkm.com";
	public static final String URI_BUG_REPORT = "http://issues.openkm.com";
	public static final String URI_SUPPORT_REQUEST = "http://www.openkm.com/Contact/";
	public static final String URI_PUBLIC_FORUM = "http://forum.openkm.com";
	public static final String URI_PROJECT_WEB = "http://www.openkm.com";
	public static final String URI_DOCUMENTATION = "http://wiki.openkm.com";
	public static final String URI_VERSION_CHANGES = "http://wiki.openkm.com/index.php/Changelog";

	private ToolBarOption mainMenuOption;
	public Bookmark bookmark;
	public BookmarkPopup bookmarkPopup;
	public ManageBookmarkPopup manageBookmarkPopup;
	public int reportOutput = OUTPUT_PDF;
	
	private MenuBar MainMenu;
	private MenuItem menuFile;
			private MenuBar subMenuFile;
			private MenuItem createDirectory;
			private MenuItem addDocument;
			private MenuItem download;
			private MenuItem downloadPdf;
			private MenuItem sendDocumentLink;
			private MenuItem sendDocumentAttachment;
			private MenuItem export;
			private MenuItem horizontalLineFile1;
			private MenuItem scanner;
			private MenuItem uploader;
			private MenuItem horizontalLineFile2;
			private MenuItem purgeTrash;
			private MenuItem horizontalLineFile3;
			private MenuItem exit;
	private MenuItem menuEdit;
		private MenuBar subMenuEdit;
			private MenuItem lock;
			private MenuItem unlock;
			private MenuItem checkout;
			private MenuItem checkin;
			private MenuItem cancelCheckout;
			private MenuItem delete;
			private MenuItem copy;
			private MenuItem move;
			private MenuItem rename;
			private MenuItem zoho;	//added by vissu on feb19 for zohoapi
	private MenuItem menuTools;
		private MenuBar subMenuTools;
			private MenuItem language;
				private MenuBar subMenuLanguage;
				private MenuBar subMenuSkin;
					private MenuItem skinDefault;
					private MenuItem skinTest;
					private MenuItem skinMediumFont;
					private MenuItem skinBigFont;
			private MenuItem skin;
			private MenuItem debugConsole;
			public MenuItem administration;
			//commented by vissu on oct'11th
			/*private MenuItem preferences;
				private MenuBar subMenuPreferences;*/
					private MenuItem userPreferences;
	private MenuItem menuBookmark;
		public MenuBar subMenuBookmark;
		private MenuItem home;
		private MenuItem defaultHome;
		private MenuItem manageBookmark;
		private MenuItem horizontalLineBookmark1;
	private MenuItem menuReports;
		private MenuItem reportFormat;
			private MenuBar subMenuReportFormat;
				private MenuItem reportFormatPdf;
				private MenuItem reportFormatRtf;
				private MenuItem reportFormatCsv;
		private MenuBar subMenuReports;
	private MenuItem menuHelp;
		private MenuBar subMenuHelp;
			private MenuItem help;
			private MenuItem documentation;
			private MenuItem bugReport;
			private MenuItem supportRequest;
			private MenuItem publicForum;
			private MenuItem versionChanges;
			private MenuItem projectWeb;
			private MenuItem about;
	
	public MainMenu() {
		
		// The bookmark
		bookmark = new Bookmark();
		bookmarkPopup = new BookmarkPopup();
		bookmarkPopup.setWidth("310px");
		bookmarkPopup.setHeight("100px");
		bookmarkPopup.setStyleName("okm-Popup");
		
		// The bookmark management
		manageBookmarkPopup = new ManageBookmarkPopup();
		manageBookmarkPopup.setWidth("400px");
		manageBookmarkPopup.setHeight("230px");
		manageBookmarkPopup.setStyleName("okm-Popup");
		
		// General menu
		MainMenu = new MenuBar(false);
		MainMenu.setStyleName("okm-TopMenuBar");
		
		// File menu
		// First we must create menus and submenus on inverse order 
				createDirectory = new MenuItem(Util.menuHTML("img/icon/actions/add_folder.gif", Main.i18n("general.menu.file.create.directory")), true, createDirectoryOKM);
				createDirectory.addStyleName("okm-MainMenuItem");
				addDocument = new MenuItem(Util.menuHTML("img/icon/actions/add_document.gif", Main.i18n("general.menu.file.add.document")), true, addDocumentOKM);
				addDocument.addStyleName("okm-MainMenuItem");
				download = new MenuItem(Util.menuHTML("img/icon/actions/download.gif", Main.i18n("general.menu.file.download.document")), true, downloadOKM);
				download.addStyleName("okm-MainMenuItem");
				downloadPdf = new MenuItem(Util.menuHTML("img/icon/actions/download_pdf.gif", Main.i18n("general.menu.file.download.document.pdf")), true, downloadPdfOKM);
				downloadPdf.addStyleName("okm-MainMenuItem");
				sendDocumentLink = new MenuItem(Util.menuHTML("img/icon/actions/send_document_link.gif", Main.i18n("general.menu.file.send.link")), true, sendDocumentLinkOKM);
				sendDocumentLink.addStyleName("okm-MainMenuItem");
				sendDocumentAttachment = new MenuItem(Util.menuHTML("img/icon/actions/send_document_attachment.gif", Main.i18n("general.menu.file.send.attachment")), true, sendDocumentAttachmentOKM);
				sendDocumentAttachment.addStyleName("okm-MainMenuItem");
				export = new MenuItem(Util.menuHTML("img/icon/actions/export.gif", Main.i18n("filebrowser.menu.export")), true, exportToFile);
				export.addStyleName("okm-MainMenuItem");
				horizontalLineFile1 = new MenuItem("", true, nullExecute);
				horizontalLineFile1.setStyleName("okm-MainMenuItem");
				horizontalLineFile1.addStyleName("okm-MainMenuItem-Base-HorizontalSeparator");
				horizontalLineFile1.setHeight("2");
				scanner = new MenuItem(Util.menuHTML("img/icon/actions/scanner.gif", Main.i18n("general.menu.file.scanner")), true, scanFile);
				scanner.addStyleName("okm-MainMenuItem");
				uploader = new MenuItem(Util.menuHTML("img/icon/actions/upload.gif", Main.i18n("general.menu.file.uploader")), true, uploadFile);
				uploader.addStyleName("okm-MainMenuItem");
				horizontalLineFile2 = new MenuItem("", true, nullExecute);
				horizontalLineFile2.setStyleName("okm-MainMenuItem");
				horizontalLineFile2.addStyleName("okm-MainMenuItem-Base-HorizontalSeparator");
				horizontalLineFile2.setHeight("2");
				purgeTrash = new MenuItem(Util.menuHTML("img/icon/actions/purge_trash.gif", Main.i18n("general.menu.file.purge.trash")), true, purgeTrashOKM);
				purgeTrash.addStyleName("okm-MainMenuItem");
				horizontalLineFile3 = new MenuItem("", true, nullExecute);
				horizontalLineFile3.setStyleName("okm-MainMenuItem");
				horizontalLineFile3.addStyleName("okm-MainMenuItem-Base-HorizontalSeparator");
				horizontalLineFile3.setHeight("2");
				exit = new MenuItem(Util.menuHTML("img/icon/menu/exit.gif", Main.i18n("general.menu.file.exit")), true, exitOKM);
				exit.addStyleName("okm-MainMenuItem");
			subMenuFile = new MenuBar(true);
			subMenuFile.setStyleName("okm-SubMenuBar");
			subMenuFile.setAutoOpen(true);
			subMenuFile.addItem(createDirectory);
			subMenuFile.addItem(addDocument);
			subMenuFile.addItem(download);
			subMenuFile.addItem(downloadPdf);
			subMenuFile.addItem(sendDocumentLink);
			subMenuFile.addItem(sendDocumentAttachment);
			subMenuFile.addItem(export);
			subMenuFile.addItem(horizontalLineFile1);
			subMenuFile.addItem(scanner);
			subMenuFile.addItem(uploader);
			subMenuFile.addItem(horizontalLineFile2);
			subMenuFile.addItem(purgeTrash);
			subMenuFile.addItem(horizontalLineFile3);
			subMenuFile.addItem(exit);
		menuFile = new MenuItem(Main.i18n("general.menu.file"), subMenuFile);
		menuFile.addStyleName("okm-MainMenuBar");
		
		// Edit menu
		// First we must create menus and submenus on inverse order
				lock = new MenuItem(Util.menuHTML("img/icon/actions/lock.gif", Main.i18n("general.menu.file.lock")), true, lockOKM);
				lock.addStyleName("okm-MainMenuItem");
				unlock = new MenuItem(Util.menuHTML("img/icon/actions/unlock.gif", Main.i18n("general.menu.file.unlock")), true, unlockOKM);
				unlock.addStyleName("okm-MainMenuItem");
				checkout = new MenuItem(Util.menuHTML("img/icon/actions/checkout.gif", Main.i18n("general.menu.file.checkout")), true, checkoutOKM);
				checkout.addStyleName("okm-MainMenuItem");
				checkin = new MenuItem(Util.menuHTML("img/icon/actions/checkin.gif", Main.i18n("general.menu.file.checkin")), true, checkinOKM);
				checkin.addStyleName("okm-MainMenuItem");
				cancelCheckout = new MenuItem(Util.menuHTML("img/icon/actions/cancel_checkout.gif", Main.i18n("general.menu.file.cancel.checkout")), true, cancelCheckoutOKM);
				cancelCheckout.addStyleName("okm-MainMenuItem");
				delete = new MenuItem(Util.menuHTML("img/icon/actions/delete.gif", Main.i18n("general.menu.file.delete")), true, deleteOKM);
				delete.addStyleName("okm-MainMenuItem");
				copy = new MenuItem(Util.menuHTML("img/icon/actions/copy.gif", Main.i18n("filebrowser.menu.copy")), true, copyOKM);
				copy.addStyleName("okm-MenuItem-strike");
				move = new MenuItem(Util.menuHTML("img/icon/actions/move_document.gif", Main.i18n("filebrowser.menu.move")), true, moveOKM);
				move.addStyleName("okm-MenuItem-strike");
				rename = new MenuItem(Util.menuHTML("img/icon/actions/rename.gif", Main.i18n("filebrowser.menu.rename")), true, renameOKM);
				rename.addStyleName("okm-MenuItem-strike");
				//added by vissu on feb19 for zohoapi
				zoho = new MenuItem(Util.menuHTML("img/icon/actions/zoho.gif", Main.i18n("general.menu.file.zoho")), true, zohoOKM);
				zoho.addStyleName("okm-MainMenuItem");	
				
			// Submenu edit
			subMenuEdit = new MenuBar(true);
			subMenuEdit.setStyleName("okm-SubMenuBar");
			subMenuEdit.setAutoOpen(true);
			subMenuEdit.addItem(lock);
			subMenuEdit.addItem(unlock);
			subMenuEdit.addItem(checkout);
			subMenuEdit.addItem(checkin);
			subMenuEdit.addItem(cancelCheckout);
			subMenuEdit.addItem(delete);
			subMenuEdit.addItem(copy);
			subMenuEdit.addItem(move);
			subMenuEdit.addItem(rename);
			subMenuEdit.addItem(zoho);	//added by vissu on feb19 for zohoapi
		
		// Menu edit
		menuEdit = new MenuItem(Main.i18n("general.menu.edit"), subMenuEdit);
		menuEdit.addStyleName("okm-MainMenuBar");
		
		// Tools menu
		// First we must create menus and submenus on inverse order 
					// Submenu Language options
					subMenuLanguage = new MenuBar(true);
					subMenuLanguage.setStyleName("okm-SubMenuBar");
				// Submenu language
				language = new MenuItem(Util.menuHTML("img/icon/menu/language.gif", Main.i18n("general.menu.tools.languages")), true, subMenuLanguage);
				language.addStyleName("okm-MainMenuItem");
				//language.addStyleName("okm-MainMenuItem-Base-Childs");
								
					// Submenu skin options
					subMenuSkin = new MenuBar(true);
					subMenuSkin.setStyleName("okm-SubMenuBar");
						skinDefault = new MenuItem(Util.menuHTML("img/icon/menu/skin_default.gif", Main.i18n("general.menu.tools.skin.default")), true, setSkinDefault);
						skinTest = new MenuItem(Util.menuHTML("img/icon/menu/skin_test.gif", Main.i18n("general.menu.tools.skin.default2")), true, setSkinDefault2);
						skinMediumFont = new MenuItem(Util.menuHTML("img/icon/menu/skin_test.gif", Main.i18n("general.menu.tools.skin.mediumfont")), true, setSkinMediumFont);
						skinBigFont = new MenuItem(Util.menuHTML("img/icon/menu/skin_test.gif", Main.i18n("general.menu.tools.skin.bigfont")), true, setSkinBigFont);
						skinDefault.addStyleName("okm-MainMenuItem");
						skinTest.addStyleName("okm-MainMenuItem");
						skinMediumFont.addStyleName("okm-MainMenuItem");
						skinBigFont.addStyleName("okm-MainMenuItem");
					subMenuSkin.addItem(skinDefault);
					subMenuSkin.addItem(skinTest);
					subMenuSkin.addItem(skinMediumFont);
					subMenuSkin.addItem(skinBigFont);
				
				// Submenu skin
				skin = new MenuItem(Util.menuHTML("img/icon/menu/skin.gif", Main.i18n("general.menu.tools.skin")), true, subMenuSkin);
				skin.addStyleName("okm-MainMenuItem");
				
				// Other tools options
				debugConsole = new MenuItem(Util.menuHTML("img/icon/menu/console.gif", Main.i18n("general.menu.debug.console")), true, setViewDebugConsole);
				debugConsole.addStyleName("okm-MainMenuItem");
				administration = new MenuItem(Util.menuHTML("img/icon/menu/administration.gif", Main.i18n("general.menu.administration")), true, showAdministration);
				administration.addStyleName("okm-MainMenuItem");
				administration.setVisible(false);
					
					// Submenu preferences opions
				//commented by vissu on oct'11th
				/*	subMenuPreferences = new MenuBar(true);
					subMenuPreferences.setStyleName("okm-SubMenuBar");	*/
						userPreferences = new MenuItem(Util.menuHTML("img/icon/menu/user_preferences.gif", Main.i18n("general.menu.tools.user.preferences")), true, setUserPreferences);
						userPreferences.addStyleName("okm-MainMenuItem");
					//commented by vissu on oct'11th		
					//subMenuPreferences.addItem(userPreferences);
				
				// Submenu preferences
				//commented by vissu on oct'11th		
			/*	preferences = new MenuItem(Util.menuHTML("img/icon/menu/preferences.gif", Main.i18n("general.menu.tools.preferences")), true, subMenuPreferences);
				preferences.addStyleName("okm-MainMenuItem");	*/
				
			// Submenu tools
			subMenuTools = new MenuBar(true);
			subMenuTools.setStyleName("okm-SubMenuBar");
			subMenuTools.setAutoOpen(true);
			subMenuTools.addItem(language);
			subMenuTools.addItem(skin);
			subMenuTools.addItem(debugConsole);
			subMenuTools.addItem(administration);
			//modifide as preferences to userPreferences by vissu on oct'11th		
			subMenuTools.addItem(userPreferences);
			
		
		// Menu tools
		menuTools = new MenuItem(Main.i18n("general.menu.tools"), subMenuTools);
		menuTools.addStyleName("okm-MainMenuBar");
		
				home = new MenuItem(Util.menuHTML("img/icon/actions/bookmark_go.gif", Main.i18n("general.menu.bookmark.home")), true, goToUserHome);
				home.addStyleName("okm-MainMenuItem");
				defaultHome = new MenuItem(Util.menuHTML("img/icon/actions/bookmark.gif", Main.i18n("general.menu.bookmark.default.home")), true, setDefaultHome);
				defaultHome.addStyleName("okm-MainMenuItem");
				manageBookmark = new MenuItem(Util.menuHTML("img/icon/actions/bookmark_edit.gif", Main.i18n("general.menu.bookmark.edit")), true, editBookmark);
				manageBookmark.addStyleName("okm-MainMenuItem");
				horizontalLineBookmark1 = new MenuItem("", true, nullExecute);
				horizontalLineBookmark1.setStyleName("okm-MainMenuItem");
				horizontalLineBookmark1.addStyleName("okm-MainMenuItem-Base-HorizontalSeparator");
				horizontalLineBookmark1.setHeight("2");
				
			// Submenu tools
			subMenuBookmark = new MenuBar(true);
			subMenuBookmark.setStyleName("okm-SubMenuBar");
			subMenuBookmark.setAutoOpen(true);
			subMenuBookmark.addItem(home);
			subMenuBookmark.addItem(defaultHome);
			subMenuBookmark.addItem(manageBookmark);
			subMenuBookmark.addItem(horizontalLineBookmark1);
		
		// Menu bookmark
		menuBookmark = new MenuItem(Main.i18n("general.menu.bookmark"), subMenuBookmark);
		menuBookmark.addStyleName("okm-MainMenuBar");	
				
				// Submenu help option 
				help = new MenuItem(Util.menuHTML("img/icon/menu/help.gif", Util.windowOpen(Main.i18n("general.menu.help"), URI_HELP) ), true, nullExecute);
				help.addStyleName("okm-MainMenuItem");
				documentation = new MenuItem(Util.menuHTML("img/icon/menu/documentation.gif", Util.windowOpen(Main.i18n("general.menu.documentation"), URI_DOCUMENTATION)), true, nullExecute);
				documentation.addStyleName("okm-MainMenuItem");
				bugReport = new MenuItem(Util.menuHTML("img/icon/menu/bugs.gif", Util.windowOpen(Main.i18n("general.menu.bug.report") ,URI_BUG_REPORT) ), true, nullExecute);
				bugReport.addStyleName("okm-MainMenuItem");
				supportRequest = new MenuItem(Util.menuHTML("img/icon/menu/support.gif", Util.windowOpen(Main.i18n("general.menu.support.request"), URI_SUPPORT_REQUEST) ), true, nullExecute);
				supportRequest.addStyleName("okm-MainMenuItem");
				publicForum = new MenuItem(Util.menuHTML("img/icon/menu/forum.gif", Util.windowOpen(Main.i18n("general.menu.public.forum"), URI_PUBLIC_FORUM)), true, nullExecute);
				publicForum.addStyleName("okm-MainMenuItem");
				versionChanges = new MenuItem(Util.menuHTML("img/icon/menu/brick.gif", Util.windowOpen(Main.i18n("general.menu.version.changes"), URI_VERSION_CHANGES)), true, nullExecute);
				versionChanges.addStyleName("okm-MainMenuItem");
				projectWeb = new MenuItem(Util.menuHTML("img/icon/menu/home.gif", Util.windowOpen(Main.i18n("general.menu.project.web"), URI_PROJECT_WEB)), true, nullExecute);
				projectWeb.addStyleName("okm-MainMenuItem");
				about = new MenuItem(Util.menuHTML("img/icon/menu/about.gif", Main.i18n("general.menu.about")), true, aboutOKM);
				about.addStyleName("okm-MainMenuItem");
			
			// Submenu preferences
				//commented by vissu on oct'11th
		/*	preferences = new MenuItem(Util.menuHTML("img/icon/menu/preferences.gif", Main.i18n("general.menu.tools.preferences")), true, subMenuPreferences);
			preferences.addStyleName("okm-MainMenuItem");	*/
				
				// Submenu report format
				subMenuReportFormat = new MenuBar(true);
				subMenuReportFormat.setStyleName("okm-SubMenuBar");
					reportFormatPdf = new MenuItem(Util.menuHTML("img/icon/security/yes.gif", Main.i18n("general.menu.report.format.pdf")), true, enablePdfReporFormat);
					reportFormatPdf.addStyleName("okm-MainMenuItem");
					reportFormatRtf = new MenuItem(Util.menuHTML("img/icon/security/no.gif", Main.i18n("general.menu.report.format.rtf")), true, enableTextReporFormat);
					reportFormatRtf.addStyleName("okm-MainMenuItem");
					reportFormatRtf.addStyleName("okm-MenuItem-strike");
					reportFormatCsv = new MenuItem(Util.menuHTML("img/icon/security/no.gif", Main.i18n("general.menu.report.format.csv")), true, enableCsvReporFormat);
					reportFormatCsv.addStyleName("okm-MainMenuItem");
					reportFormatCsv.addStyleName("okm-MenuItem-strike");
				subMenuReportFormat.addItem(reportFormatPdf);
				subMenuReportFormat.addItem(reportFormatRtf);
				subMenuReportFormat.addItem(reportFormatCsv);
				
			reportFormat = new MenuItem(Util.menuHTML("img/icon/menu/preferences.gif", Main.i18n("general.menu.report.format")), true, subMenuReportFormat);
			reportFormat.addStyleName("okm-MainMenuItem");
				
			// Submenu tools
			subMenuReports = new MenuBar(true);
			subMenuReports.setStyleName("okm-SubMenuBar");
			subMenuReports.setAutoOpen(true);
			subMenuReports.addItem(reportFormat);
				
		// Menu bookmark
		menuReports = new MenuItem(Main.i18n("general.menu.report"), subMenuReports);
		menuReports.addStyleName("okm-MainMenuBar");
				
			// Submenu help
			subMenuHelp = new MenuBar(true);
			subMenuHelp.setStyleName("okm-SubMenuBar");
			subMenuHelp.setAutoOpen(true);
			//subMenuHelp.addItem(help);
			subMenuHelp.addItem(documentation);
			subMenuHelp.addItem(bugReport);
			subMenuHelp.addItem(supportRequest);
			subMenuHelp.addItem(publicForum);
			subMenuHelp.addItem(versionChanges);
			subMenuHelp.addItem(projectWeb);
			subMenuHelp.addItem(about);
		
		// Help menu
		menuHelp  = new MenuItem(Main.i18n("general.menu.help"), subMenuHelp);
		menuHelp.addStyleName("okm-MainMenuBar");
		
		// Create final general menu adding cascade menus to it
		MainMenu.addItem(menuFile);
		MainMenu.addItem(menuEdit);
		MainMenu.addItem(menuTools);
		MainMenu.addItem(menuBookmark);
		MainMenu.addItem(menuReports);
		MainMenu.addItem(menuHelp);
		MainMenu.setAutoOpen(false);
		
		// By default hide menus
		menuFile.setVisible(false);
		menuEdit.setVisible(false);
		menuTools.setVisible(false);
		menuBookmark.setVisible(false);
		menuReports.setVisible(false);
		menuHelp.setVisible(false);
		
		initWidget(MainMenu);
	}
	
	// Lang refresh
	public void langRefresh() {
		bookmarkPopup.langRefresh(); // Refreshing popup
		manageBookmarkPopup.langRefresh(); // Refreshing management popup
		menuEdit.setText(Main.i18n("general.menu.edit"));
			createDirectory.setHTML(Util.menuHTML("img/icon/actions/add_folder.gif", Main.i18n("general.menu.file.create.directory")));
			download.setHTML(Util.menuHTML("img/icon/actions/download.gif", Main.i18n("general.menu.file.download.document")));
			downloadPdf.setHTML(Util.menuHTML("img/icon/actions/download_pdf.gif", Main.i18n("general.menu.file.download.document.pdf")));
			sendDocumentLink.setHTML(Util.menuHTML("img/icon/actions/send_document_link.gif", Main.i18n("general.menu.file.send.link")));
			sendDocumentAttachment.setHTML(Util.menuHTML("img/icon/actions/send_document_attachment.gif", Main.i18n("general.menu.file.send.attachment")));
			export.setHTML(Util.menuHTML("img/icon/actions/export.gif", Main.i18n("filebrowser.menu.export")));
			scanner.setHTML(Util.menuHTML("img/icon/actions/scanner.gif", Main.i18n("general.menu.file.scanner")));
			uploader.setHTML(Util.menuHTML("img/icon/actions/upload.gif", Main.i18n("general.menu.file.uploader")));
			lock.setHTML(Util.menuHTML("img/icon/actions/lock.gif", Main.i18n("general.menu.file.lock")));
			unlock.setHTML(Util.menuHTML("img/icon/actions/unlock.gif", Main.i18n("general.menu.file.unlock")));
			addDocument.setHTML(Util.menuHTML("img/icon/actions/add_document.gif", Main.i18n("general.menu.file.add.document")));
			checkout.setHTML(Util.menuHTML("img/icon/actions/checkout.gif", Main.i18n("general.menu.file.checkout")));
			checkin.setHTML(Util.menuHTML("img/icon/actions/checkin.gif", Main.i18n("general.menu.file.checkin")));
			cancelCheckout.setHTML(Util.menuHTML("img/icon/actions/cancel_checkout.gif", Main.i18n("general.menu.file.cancel.checkout")));
			delete.setHTML(Util.menuHTML("img/icon/actions/delete.gif", Main.i18n("general.menu.file.delete")));
			move.setHTML(Util.menuHTML("img/icon/actions/move_document.gif", Main.i18n("filebrowser.menu.move")));
			copy.setHTML(Util.menuHTML("img/icon/actions/copy.gif", Main.i18n("filebrowser.menu.copy")));
			rename.setHTML(Util.menuHTML("img/icon/actions/rename.gif", Main.i18n("filebrowser.menu.rename")));
			//added by vissu on feb19 for zohoapi
			zoho.setHTML(Util.menuHTML("img/icon/actions/zoho.gif", Main.i18n("general.menu.file.zoho")));
			
		menuFile.setText(Main.i18n("general.menu.file"));
			purgeTrash.setHTML(Util.menuHTML("img/icon/actions/purge_trash.gif", Main.i18n("general.menu.file.purge.trash")));
			exit.setHTML(Util.menuHTML("img/icon/menu/exit.gif", Main.i18n("general.menu.file.exit")));
		menuTools.setText(Main.i18n("general.menu.tools"));
			language.setHTML(Util.menuHTML("img/icon/menu/language.gif", Main.i18n("general.menu.tools.languages")));
			skin.setHTML(Util.menuHTML("img/icon/menu/skin.gif", Main.i18n("general.menu.tools.skin")));
				skinDefault.setHTML(Util.menuHTML("img/icon/menu/skin_default.gif", Main.i18n("general.menu.tools.skin.default")));
				skinTest.setHTML(Util.menuHTML("img/icon/menu/skin_test.gif", Main.i18n("general.menu.tools.skin.default2")));
				skinMediumFont.setHTML(Util.menuHTML("img/icon/menu/skin_test.gif", Main.i18n("general.menu.tools.skin.mediumfont")));
				skinBigFont.setHTML(Util.menuHTML("img/icon/menu/skin_test.gif", Main.i18n("general.menu.tools.skin.bigfont")));	
				skinTest.setHTML(Util.menuHTML("img/icon/menu/skin_test.gif", Main.i18n("general.menu.tools.skin.default2")));	
			debugConsole.setHTML(Util.menuHTML("img/icon/menu/console.gif", Main.i18n("general.menu.debug.console")));
			administration.setHTML(Util.menuHTML("img/icon/menu/administration.gif", Main.i18n("general.menu.administration")));
			//commented by vissu on oct'11th
			//preferences.setHTML(Util.menuHTML("img/icon/menu/preferences.gif", Main.i18n("general.menu.tools.preferences")));
				userPreferences.setHTML(Util.menuHTML("img/icon/menu/user_preferences.gif", Main.i18n("general.menu.tools.user.preferences")));
		menuBookmark.setText(Main.i18n("general.menu.bookmark"));
			home.setHTML(Util.menuHTML("img/icon/actions/bookmark_go.gif", Main.i18n("general.menu.bookmark.home")));
			defaultHome.setHTML(Util.menuHTML("img/icon/actions/bookmark.gif", Main.i18n("general.menu.bookmark.default.home")));
			manageBookmark.setHTML(Util.menuHTML("img/icon/actions/bookmark_edit.gif", Main.i18n("general.menu.bookmark.edit")));
		menuReports.setText(Main.i18n("general.menu.report"));
			reportFormat.setHTML(Util.menuHTML("img/icon/menu/preferences.gif", Main.i18n("general.menu.report.format")));
			refreshReportFormatMenu();
		menuHelp.setText(Main.i18n("general.menu.help"));
			help.setHTML(Util.menuHTML("img/icon/menu/help.gif", Util.windowOpen(Main.i18n("general.menu.help"), URI_HELP)));
			documentation.setHTML(Util.menuHTML("img/icon/menu/documentation.gif", Util.windowOpen(Main.i18n("general.menu.documentation"), URI_DOCUMENTATION)));
			bugReport.setHTML(Util.menuHTML("img/icon/menu/bugs.gif", Util.windowOpen(Main.i18n("general.menu.bug.report"), URI_BUG_REPORT)));
			supportRequest.setHTML(Util.menuHTML("img/icon/menu/support.gif", Util.windowOpen(Main.i18n("general.menu.support.request"), URI_SUPPORT_REQUEST)));
			publicForum.setHTML(Util.menuHTML("img/icon/menu/forum.gif", Util.windowOpen(Main.i18n("general.menu.public.forum"), URI_PUBLIC_FORUM)));
			versionChanges.setHTML(Util.menuHTML("img/icon/menu/brick.gif", Util.windowOpen(Main.i18n("general.menu.version.changes"), URI_VERSION_CHANGES)));
			projectWeb.setHTML(Util.menuHTML("img/icon/menu/home.gif", Util.windowOpen(Main.i18n("general.menu.project.web"), URI_PROJECT_WEB)));
			about.setHTML(Util.menuHTML("img/icon/menu/about.gif", Main.i18n("general.menu.about")));
	}
	
	/**
	 * refreshReportFormatMenu
	 */
	private void refreshReportFormatMenu(){
		switch (reportOutput) {
			case OUTPUT_PDF:
				reportFormatPdf.setHTML(Util.menuHTML("img/icon/security/yes.gif", Main.i18n("general.menu.report.format.pdf")));
				reportFormatRtf.setHTML(Util.menuHTML("img/icon/security/no.gif", Main.i18n("general.menu.report.format.rtf")));
				reportFormatCsv.setHTML(Util.menuHTML("img/icon/security/no.gif", Main.i18n("general.menu.report.format.csv")));
				enable(reportFormatPdf);
				disable(reportFormatRtf);
				disable(reportFormatCsv);
				break;
			
			case OUTPUT_RTF:
				reportFormatPdf.setHTML(Util.menuHTML("img/icon/security/no.gif", Main.i18n("general.menu.report.format.pdf")));
				reportFormatRtf.setHTML(Util.menuHTML("img/icon/security/yes.gif", Main.i18n("general.menu.report.format.rtf")));
				reportFormatCsv.setHTML(Util.menuHTML("img/icon/security/no.gif", Main.i18n("general.menu.report.format.csv")));
				disable(reportFormatPdf);
				enable(reportFormatRtf);
				disable(reportFormatCsv);
				break;
				
			case OUTPUT_CSV:
				reportFormatPdf.setHTML(Util.menuHTML("img/icon/security/no.gif", Main.i18n("general.menu.report.format.pdf")));
				reportFormatRtf.setHTML(Util.menuHTML("img/icon/security/no.gif", Main.i18n("general.menu.report.format.rtf")));
				reportFormatCsv.setHTML(Util.menuHTML("img/icon/security/yes.gif", Main.i18n("general.menu.report.format.csv")));
				disable(reportFormatPdf);
				disable(reportFormatRtf);
				enable(reportFormatCsv);
				break;
		}
	}
	
	/**
	 * Enables menu item
	 * 
	 * @param menuItem The menu item
	 */
	public void enable(MenuItem menuItem) {
		menuItem.removeStyleName("okm-MenuItem-strike");
	}
	
	/**
	 * Disables the menu item with and strike
	 * 
	 * @param menuItem The menu item
	 */
	public void disable(MenuItem menuItem) {
		menuItem.addStyleName("okm-MenuItem-strike");
	}
	
	/**
	 * Enables or disables menu option on privileges
	 */
	public void evaluateMenuOptions() {
		if (mainMenuOption.createFolderOption) {	enable(createDirectory); } else { disable(createDirectory);	}
		if (mainMenuOption.downloadOption) { enable(download); } else { disable(download); }
		if (mainMenuOption.downloadPdfOption) { enable(downloadPdf); } else { disable(downloadPdf); }
		if (mainMenuOption.sendDocumentLinkOption) { enable(sendDocumentLink); } else { disable(sendDocumentLink); }
		if (mainMenuOption.sendDocumentAttachmentOption) { enable(sendDocumentAttachment); } else { disable(sendDocumentAttachment); }
		if (mainMenuOption.exportOption) { enable(export); } else { disable(export); }
		if (mainMenuOption.scannerOption) { enable(scanner); } else { disable(scanner); }
		if (mainMenuOption.uploaderOption) { enable(uploader); } else { disable(uploader); }
		if (mainMenuOption.lockOption) { enable(lock); } else {	disable(lock); }
		if (mainMenuOption.unLockOption) { enable(unlock); } else { disable(unlock); }
		if (mainMenuOption.addDocumentOption) { enable(addDocument); } else { disable(addDocument); }
		if (mainMenuOption.checkoutOption) { enable(checkout); } else { disable(checkout); }
		if (mainMenuOption.checkinOption) { enable(checkin); } else { disable(checkin); }
		if (mainMenuOption.cancelCheckoutOption) { enable(cancelCheckout); } else { disable(cancelCheckout); }
		if (mainMenuOption.deleteOption) { enable(delete); } else { disable(delete); }
		if (mainMenuOption.copyOption) { enable(copy); } else { disable(copy); }
		if (mainMenuOption.renameOption) { enable(rename); } else { disable(rename); }

		//added by vissu on feb19 for zohoapi
		if (mainMenuOption.zohoOption) { enable(zoho); } else { disable(zoho); }

		if (mainMenuOption.moveOption) { enable(move); } else { disable(move); }
		if (mainMenuOption.homeOption) {
			enable(home);
			enable(defaultHome);
			bookmark.enableBookmarks();
		} else {
			disable(home);
			disable(defaultHome);
			bookmark.disableBookmarks();
		}
	}
	
	/**
	 * Sets the main menu options
	 * 
	 * @param mainMenuOption The manin Menu options
	 */
	public void setOptions(ToolBarOption mainMenuOption){
		this.mainMenuOption = mainMenuOption;
		evaluateMenuOptions();
	}
	
	// Command menu to purge trash 
	Command purgeTrashOKM = new Command() {
		public void execute() {
			Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_EMPTY_TRASH);
			Main.get().confirmPopup.show();
		}
	};
	
	// Command menu to create directory 
	Command createDirectoryOKM = new Command() {
		public void execute() {
			if (mainMenuOption.createFolderOption) {
				Main.get().mainPanel.topPanel.toolBar.executeFolderDirectory();
			}
		}
	};
	
	// Command menu to download
	Command downloadOKM = new Command() {
		public void execute() {
			if (mainMenuOption.downloadOption) {
				Main.get().mainPanel.topPanel.toolBar.executeDownload();
			}
		}
	};

	// Command menu to download as PDF
	Command downloadPdfOKM = new Command() {
		public void execute() {
			if (mainMenuOption.downloadPdfOption) {
				Main.get().mainPanel.topPanel.toolBar.executeDownloadPdf();
			}
		}
	};

	// Command menu to send document link
	Command sendDocumentLinkOKM = new Command() {
		public void execute() {
			if (mainMenuOption.sendDocumentLinkOption) {
				Main.get().notifyPopup.executeSendDocument(NotifyPopup.NOTIFY_WITH_LINK);
			}
		}
	};
	
	// Command menu to send document attachment
	Command sendDocumentAttachmentOKM = new Command() {
		public void execute() {
			if (mainMenuOption.sendDocumentAttachmentOption) {
				Main.get().notifyPopup.executeSendDocument(NotifyPopup.NOTIFY_WITH_ATTACHMENT);
			}
		}
	};
	
	
	// Command menu to export
	Command exportToFile = new Command() {
		public void execute() {
			if (mainMenuOption.exportOption) {
				Main.get().mainPanel.topPanel.toolBar.executeExport();
			}
		}
	};
	
	// Command menu to scan
	Command scanFile = new Command() {
		public void execute() {
			if (mainMenuOption.scannerOption) {
				Main.get().mainPanel.topPanel.toolBar.executeScanner();
			}
		}
	};
	
	// Command menu to upload
	Command uploadFile = new Command() {
		public void execute() {
			if (mainMenuOption.uploaderOption) {
				Main.get().mainPanel.topPanel.toolBar.executeUploader();
			}
		}
	};

	// Command menu to lock  
	Command lockOKM = new Command() {
		public void execute() {
			if (mainMenuOption.lockOption) {
				Main.get().mainPanel.topPanel.toolBar.executeLock();
			}
		}
	};
	
	// Command menu to unlock
	Command unlockOKM = new Command() {
		public void execute() {
			if (mainMenuOption.unLockOption) {
				Main.get().mainPanel.topPanel.toolBar.executeUnlock();
			}
		}
	};
	
	// Command menu to add documen
	Command addDocumentOKM = new Command() {
		public void execute() {
			if (mainMenuOption.addDocumentOption) {
				Main.get().mainPanel.topPanel.toolBar.executeAddDocument();
			}
		}
	};
	
	// Command menu to edit (checkout)
	Command checkoutOKM = new Command() {
		public void execute() {
			if (mainMenuOption.checkoutOption) {
				Main.get().mainPanel.topPanel.toolBar.executeCheckout();
			}
		}
	};
	
	// Command menu to checkin
	Command checkinOKM = new Command() {
		public void execute() {
			if (mainMenuOption.checkinOption) {
				Main.get().mainPanel.topPanel.toolBar.exectuteCheckin();
			}
		}
	};
	
	// Command menu to cancel checkout
	Command cancelCheckoutOKM = new Command() {
		public void execute() {
			if (mainMenuOption.cancelCheckoutOption) {
				Main.get().mainPanel.topPanel.toolBar.executeCancelCheckout();
			}
		}
	};
	
	// Command menu to copy
	Command copyOKM = new Command() {
		public void execute() {
			if (mainMenuOption.copyOption) {
				Main.get().mainPanel.topPanel.toolBar.executeCopy();
			}
		}
	};
	
	// Command menu to copy
	Command moveOKM = new Command() {
		public void execute() {
			Main.get().mainPanel.topPanel.toolBar.executeMove();
		}
	};
	
	// Command menu to copy
	Command renameOKM = new Command() {
		public void execute() {
			if (mainMenuOption.renameOption) {
				Main.get().mainPanel.topPanel.toolBar.executeRename();
			}
		}
	};
	
	//added by vissu on feb19 for zohoapi
	// Command menu to copy
		Command zohoOKM = new Command() {
			public void execute() {
				if (mainMenuOption.zohoOption) {
					Main.get().mainPanel.topPanel.toolBar.executeZoho();
					//Main.get().zohoPopup.show();
					
				}
			}
		};
	
	// Command menu to delete 
	Command deleteOKM = new Command() {
		public void execute() {
			if (mainMenuOption.deleteOption) {
				Main.get().mainPanel.topPanel.toolBar.executeDelete();
			}
		}
	};
	
	// Command menu to delete 
	Command refreshOKM = new Command() {
		public void execute() {
			if (mainMenuOption.refreshOption) {
				Main.get().mainPanel.topPanel.toolBar.executeRefresh();
			}
		}
	};
	
	// Command menu to exit application
	Command exitOKM = new Command() {
		public void execute() {
			Main.get().logoutPopup.logout();
		}
	};
	
	// Command menu to show about
	Command aboutOKM = new Command() {
		public void execute() {
			Main.get().aboutPopup.show();
		}
	};
	
	// Command menu to set default skin
	Command setSkinDefault = new Command() {
		public void execute() {
			Util.changeCss("default");
		}
	};
	
	// Command menu to set test skin
	Command setSkinDefault2 = new Command() {
		public void execute() {
			Util.changeCss("test");
		}
	};
	
	// Command menu to set test skin
	Command setSkinMediumFont = new Command() {
		public void execute() {
			Util.changeCss("mediumfont");
		}
	};
	
	// Command menu to set test skin
	Command setSkinBigFont = new Command() {
		public void execute() {
			Util.changeCss("bigfont");
		}
	};
	
	// Command menu to show debug console
	Command setViewDebugConsole = new Command() {
		public void execute() {
			Main.get().debugConsolePopup.center();
		}
	};
	
	// Command menu to show administration
	Command showAdministration = new Command() {
		public void execute() {
			Window.open(Main.CONTEXT + "/admin/index.jsp", "Administration", "");
		}
	};
	
	// Command menu to go to set user preferences
	Command setUserPreferences = new Command() {
		public void execute() {
			Main.get().userPopup.show();
		}
	};
	
	// Command enable pdf report format
	Command enablePdfReporFormat = new Command() {
		public void execute() {
			reportOutput = OUTPUT_PDF;
			refreshReportFormatMenu();
		}
	};
	
	// Command enable text report format
	Command enableTextReporFormat = new Command() {
		public void execute() {
			reportOutput = OUTPUT_RTF;
			refreshReportFormatMenu();
		}
	};
	
	// Command enable CSV report format
	Command enableCsvReporFormat = new Command() {
		public void execute() {
			reportOutput = OUTPUT_CSV;
			refreshReportFormatMenu();
		}
	};
	
	// Command menu to go to user home
	Command goToUserHome = new Command() {
		public void execute() {
			if (mainMenuOption.homeOption) {
				Main.get().mainPanel.topPanel.toolBar.executeGoToUserHome();
			}
		}
	};
	
	// Command menu to go to user home
	Command setDefaultHome = new Command() {
		public void execute() {
			if (mainMenuOption.homeOption) {
				if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
					Main.get().mainPanel.desktop.browser.fileBrowser.setHome();
				} else if (Main.get().activeFolderTree.isPanelSelected()) {
					Main.get().activeFolderTree.setHome();
				}
			}
		}
	};
	
	// Command menu to go to user home
	Command editBookmark = new Command() {
		public void execute() {
			if (mainMenuOption.homeOption) {
				manageBookmarkPopup.showPopup();
			}
		}
	};
	
	
	
	// Command menu that executes void
	Command nullExecute = new Command() {
		public void execute() {
		}
	};
	
	/**
	 * Gets the tools bar options
	 * @return The tool bar options values
	 */
	public ToolBarOption getToolBarOption() {
		return mainMenuOption;
	}
	
	/**
	 * setAvailableOption
	 * 
	 * @param option
	 */
	public void setAvailableOption(GWTWorkspace workspace) {
		GWTAvailableOption option = workspace.getAvailableOption();
		
		// FILE MENU
		createDirectory.setVisible(option.isCreateFolderOption());
		addDocument.setVisible(option.isAddDocumentOption());
		download.setVisible(option.isDownloadOption());
		downloadPdf.setVisible(option.isDownloadPdfOption());
		sendDocumentLink.setVisible(option.isSendDocumentLinkOption());
		sendDocumentAttachment.setVisible(option.isSendDocumentAttachmentOption());
		export.setVisible(option.isExportOption());
		horizontalLineFile1.setVisible(option.isCreateFolderOption() || option.isAddDocumentOption() || option.isDownloadOption() ||
									   option.isDownloadPdfOption() || option.isSendDocumentLinkOption() || 
									   option.isSendDocumentAttachmentOption() || option.isExportOption());
		scanner.setVisible(option.isScannerOption());
		uploader.setVisible(option.isUploaderOption());
		horizontalLineFile2.setVisible(option.isScannerOption() || option.isUploaderOption());
		purgeTrash.setVisible(option.isPurgeTrashOption());
		horizontalLineFile3.setVisible(option.isPurgeTrashOption());
		
		// EDIT MENU
		lock.setVisible(option.isLockOption());
		unlock.setVisible(option.isUnLockOption());
		checkout.setVisible(option.isCheckoutOption());
		checkin.setVisible(option.isCheckinOption());
		cancelCheckout.setVisible(option.isCancelCheckoutOption());
		delete.setVisible(option.isDeleteOption());
		copy.setVisible(option.isCopyOption());
		move.setVisible(option.isMoveOption());
		rename.setVisible(option.isRenameOption());
		
		zoho.setVisible(option.isZohoOption());	//added by vissu on feb19 for zohoapi
	
		// MENU TOOLS
		if (!option.isLanguagesOption()) {
			subMenuTools.removeItem(language);
		}
		if (!option.isSkinOption()) {
			subMenuTools.removeItem(skin);
		}
		debugConsole.setVisible(option.isDebugOption());
		administration.setVisible(option.isAdministrationOption());
		//commented by vissu on oct'11th
	/*	if (!option.isPreferencesOption()) {
			subMenuTools.removeItem(preferences);
		}	*/
		
		// MENU BOOKMARKS
		home.setVisible(option.isHomeOption());
		defaultHome.setVisible(option.isAddBookmarkOption());
		manageBookmark.setVisible(option.isManageBookmarkOption());
		horizontalLineBookmark1.setVisible(option.isHomeOption() || option.isAddBookmarkOption() || option.isAddBookmarkOption());
		
		// MENU REPORTS
		if (workspace.getReports().size()>0) {
			menuReports.setVisible(true);
			for (final GWTReport report : workspace.getReports()) {
				MenuItem reportMenuItem = new MenuItem(Util.menuHTML("img/icon/menu/report.png", report.getName()), true, new Command() {
					@Override
					public void execute() {
						if (report.getFormElements().size()>0) {
							Main.get().reportPopup.setReport(report);
							Main.get().reportPopup.center();
						} else {
							Map<String,String> parameters = new HashMap<String, String>();
							parameters.put("format", String.valueOf(reportOutput));
							Util.executeReport(report.getId(), parameters);
						}
					}
				});
				reportMenuItem.addStyleName("okm-MainMenuItem");
				subMenuReports.addItem(reportMenuItem);
			}
		} 		
		// MENU HELP
		help.setVisible(option.isHelpOption());
		documentation.setVisible(option.isDocumentationOption());
		bugReport.setVisible(option.isBugReportOption());
		supportRequest.setVisible(option.isSupportRequestOption());
		publicForum.setVisible(option.isPublicForumOption());
		versionChanges.setVisible(option.isVersionChangesOption());
		projectWeb.setVisible(option.isProjectWebOption());
		about.setVisible(option.isAboutOption());
	}
	
	/**
	 * setEditMenuVisible
	 * 
	 * @param visible
	 */
	public void setEditMenuVisible(boolean visible) {
		menuEdit.setVisible(visible);
	}
	
	/**
	 * setToolsMenuVisible
	 * 
	 * @param visible
	 */
	public void setToolsMenuVisible(boolean visible) {
		menuTools.setVisible(visible);
	}
	
	/**
	 * setBookmarkMenuVisible
	 * 
	 * @param visible
	 */
	public void setBookmarkMenuVisible(boolean visible) {
		menuBookmark.setVisible(visible);
	}
	
	/**
	 * setHelpMenuVisible
	 * 
	 * @param visible
	 */
	public void setHelpMenuVisible(boolean visible) {
		menuHelp.setVisible(visible);
	}
	
	/**
	 * setFileMenuVisible
	 * 
	 * @param visible
	 */
	public void setFileMenuVisible(boolean visible) {
		menuFile.setVisible(visible);
	}
	
	/**
	 * addMenu
	 * 
	 * @param extension
	 */
	public void addMenuExtension(MenuItemExtension extension) {
		switch (extension.getMenuLocation()) {
			case UIMenuConstants.NEW_MENU:
				MainMenu.addItem(extension);
				break;
			case UIMenuConstants.MAIN_MENU_FILE:
				subMenuFile.addItem(extension);
				break;
			case UIMenuConstants.MAIN_MENU_EDIT:
				subMenuEdit.addItem(extension);
				break;
			case UIMenuConstants.MAIN_MENU_TOOLS:
				subMenuTools.addItem(extension);
				break;
			case UIMenuConstants.MAIN_MENU_BOOKMARS:
				subMenuBookmark.addItem(extension);
				break;
			case UIMenuConstants.MAIN_MENU_REPORTS:
				subMenuReports.addItem(extension);
				menuReports.setVisible(true);
				break;
			case UIMenuConstants.MAIN_MENU_HELP:
				subMenuHelp.addItem(extension);
				break;
		}
		
	}
	
	/**
	 * getReportOutput
	 */
	public int getReportOutput() {
		return reportOutput;
	}
	
	/**
	 * initAvailableLanguage
	 * 
	 * @param langs
	 */
	public void initAvailableLanguage(List<GWTLanguage> langs) {
		for (final GWTLanguage lang : langs) {
			MenuItem menuItem = new MenuItem(Util.flagMenuHTML(lang.getId(), lang.getName()), true, new Command() {
				public void execute() {
					Main.get().refreshLang(lang.getId());
				}
			});
			menuItem.addStyleName("okm-MainMenuItem");
			subMenuLanguage.addItem(menuItem);
		}
	}
}
