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

package com.openkm.frontend.client.widget.properties;

import java.util.Iterator;

import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.contants.ui.UIDesktopConstants;
import com.openkm.frontend.client.util.Util;

public class Folder extends Composite {
	
	private ScrollPanel scrollPanel;
	private FlexTable tableProperties;
	private FlexTable tableSubscribedUsers;
	private FlexTable table;
	private GWTFolder folder;
	HorizontalPanel hPanelSubscribedUsers;
	private HTML subcribedUsersText;
	
	/**
	 * The folder
	 */
	public Folder() {
		table = new FlexTable();
		tableProperties = new FlexTable();
		tableSubscribedUsers = new FlexTable();
		scrollPanel = new ScrollPanel(table);
		
		tableProperties.setWidth("100%");
		tableProperties.setHTML(0, 0, "<b>"+Main.i18n("folder.uuid")+"</b>");
		tableProperties.setHTML(0, 1, "");
		tableProperties.setHTML(1, 0, "<b>"+Main.i18n("folder.name")+"</b>");
		tableProperties.setHTML(1, 1, "");
		tableProperties.setHTML(2, 0, "<b>"+Main.i18n("folder.parent")+"</b>");
		tableProperties.setHTML(2, 1, "");
		tableProperties.setHTML(3, 0, "<b>"+Main.i18n("folder.created")+"</b>");
		tableProperties.setHTML(3, 1, "");
		tableProperties.setHTML(4, 0, "<b>"+Main.i18n("folder.subscribed")+"</b>");
		tableProperties.setHTML(4, 1, "");
		tableProperties.setHTML(5, 0, "<b>"+Main.i18n("folder.number.folders")+"</b>");
		tableProperties.setHTML(5, 1, "");
		tableProperties.setHTML(6, 0, "<b>"+Main.i18n("folder.number.documents")+"</b>");
		tableProperties.setHTML(6, 1, "");
		//vissu touch to hide below on oct'5th
		/*tableProperties.setHTML(7, 0, "<b>"+Main.i18n("folder.number.mails")+"</b>");
		tableProperties.setHTML(7, 1, "");
		tableProperties.setHTML(8, 0, "<b>"+Main.i18n("folder.url")+"</b>");
		tableProperties.setWidget(8, 1, new HTML(""));
		tableProperties.setHTML(9, 0, "<b>"+Main.i18n("folder.webdav")+"</b>");
		tableProperties.setWidget(9, 1, new HTML(""));	*/
		
		hPanelSubscribedUsers = new HorizontalPanel();
		subcribedUsersText = new HTML("<b>"+Main.i18n("folder.subscribed.users")+"<b>");
		hPanelSubscribedUsers.add(subcribedUsersText);
		hPanelSubscribedUsers.add(new HTML("&nbsp;"));
		hPanelSubscribedUsers.setCellVerticalAlignment(subcribedUsersText, HasAlignment.ALIGN_MIDDLE);
		
		tableSubscribedUsers.setWidget(0, 0, hPanelSubscribedUsers);
				
		table.setWidget(0, 0, tableProperties);
		table.setHTML(0,1, "");
		table.setWidget(0,2,tableSubscribedUsers);

		// The hidden column extends table to 100% width
		CellFormatter cellFormatter = table.getCellFormatter();
		cellFormatter.setWidth(0, 1, "25");
		cellFormatter.setVerticalAlignment(0,0, HasAlignment.ALIGN_TOP);
		cellFormatter.setVerticalAlignment(0,2, HasAlignment.ALIGN_TOP);
		
		// Sets wordWrap for al rows
		setRowWordWarp(0, 0, true, tableProperties);
		setRowWordWarp(1, 0, true, tableProperties);
		setRowWordWarp(2, 0, true, tableProperties);
		setRowWordWarp(3, 0, true, tableProperties);
		setRowWordWarp(4, 0, true, tableProperties);
		setRowWordWarp(5, 0, true, tableProperties);
		setRowWordWarp(6, 0, true, tableProperties);
		setRowWordWarp(7, 0, true, tableProperties);
		setRowWordWarp(0, 0, true, tableSubscribedUsers);
		
		tableProperties.setStyleName("okm-DisableSelect");
		tableSubscribedUsers.setStyleName("okm-DisableSelect");
		
		initWidget(scrollPanel);
	}
	
	/**
	 * Set the WordWarp for all the row cells
	 * 
	 * @param row The row cell
	 * @param columns Number of row columns
	 * @param warp
	 * @param table The table to change word wrap
	 */
	private void setRowWordWarp(int row, int columns, boolean warp, FlexTable table) {
		CellFormatter cellFormatter = table.getCellFormatter();
		for (int i=0; i<columns; i++) {
			cellFormatter.setWordWrap(row, i, false);
		}
	}
	
	/**
	 * get
	 * 
	 * @return
	 */
	public GWTFolder get() {
		return folder;
	}
	
	/**
	 * Sets the folder values
	 * 
	 * @param folder The folder object
	 */
	public void set(GWTFolder folder) {
		this.folder = folder;
		
		// url
		String url = Main.get().workspaceUserProperties.getApplicationURL();
		url += "?fldPath=" + URL.encodeComponent(folder.getPath());
		//vissu touch commented to hide on oct'9th
		//tableProperties.setWidget(8, 1, new HTML("<div id=\"folderurlclipboardcontainer\"></div>\n"));
		Util.createFolderURLClipboardButton(url);
		
		// Webdav
		String webdavUrl = Main.get().workspaceUserProperties.getApplicationURL();
		if (webdavUrl.lastIndexOf('/')>0) {
			int idx = webdavUrl.lastIndexOf('/');
			String webdavPath = folder.getPath();
			// Replace only in case webdav fix is enabled
			if (Main.get().workspaceUserProperties.getWorkspace().isWebdavFix()) {
				webdavPath.replace("okm:", "okm_");
			}
			
			webdavUrl = webdavUrl.substring(0, webdavUrl.lastIndexOf('/', idx-1)) + "/repository/default" + webdavPath;
			//vissu touch commented to hide on oct'9th
			//tableProperties.setWidget(9, 1, new HTML("<div id=\"folderwebdavclipboardcontainer\"></div>\n"));
			Util.createFolderWebDavClipboardButton(webdavUrl);
		}
		
		tableProperties.setHTML(0, 1, folder.getUuid());
		tableProperties.setHTML(1, 1, folder.getName());
		//vissu touch commented below line on oct'9th
		//tableProperties.setHTML(2, 1, folder.getParentPath());

		//vissu touch to add below on oct'9th
		String parentPath = folder.getParentPath();
		parentPath = parentPath.replaceAll("okm:root","Knowledge Vault");
		tableProperties.setHTML(2, 1, parentPath);
		
		DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
		tableProperties.setHTML(3, 1, dtf.format(folder.getCreated())+" "+Main.i18n("folder.by")+" "+folder.getAuthor());
		if (folder.isSubscribed()) {
			tableProperties.setHTML(4, 1, Main.i18n("folder.subscribed.yes"));
		} else {
			tableProperties.setHTML(4, 1, Main.i18n("folder.subscribed.no"));
		}
		
		setRowWordWarp(0, 1, true, tableProperties);
		setRowWordWarp(1, 1, true, tableProperties);
		setRowWordWarp(2, 1, true, tableProperties);
		setRowWordWarp(3, 1, true, tableProperties);
		setRowWordWarp(4, 1, true, tableProperties);
		setRowWordWarp(5, 1, true, tableProperties);
		setRowWordWarp(6, 1, true, tableProperties);
		setRowWordWarp(7, 1, true, tableProperties);
		
		// Remove all table rows >= 1
		while (tableSubscribedUsers.getRowCount() > 1) {
			tableSubscribedUsers.removeRow(1);
		}
		
		// Sets the folder subscribers
		for (Iterator<String> it = folder.getSubscriptors().iterator(); it.hasNext(); ) {
			tableSubscribedUsers.setHTML(tableSubscribedUsers.getRowCount(), 0, it.next());
			setRowWordWarp(tableSubscribedUsers.getRowCount()-1, 0, true, tableSubscribedUsers);
		}
		
		// Some preoperties only must be visible on taxonomy or trash view
		int actualView = Main.get().mainPanel.desktop.navigator.getStackIndex();
		switch(actualView) {
			case UIDesktopConstants.NAVIGATOR_TAXONOMY:   // Some preperties only must be visible on taxonomy or trash view
			case UIDesktopConstants.NAVIGATOR_TRASH:
				tableSubscribedUsers.setVisible(true);
				tableProperties.getRowFormatter().setVisible(4, true); // Is user subscribed
				tableProperties.getRowFormatter().setVisible(5, true); // Number of folders
				tableProperties.getRowFormatter().setVisible(6, true); // Number of documents
				tableProperties.getRowFormatter().setVisible(7, true); // Number of e-mails
				break;
				
			case UIDesktopConstants.NAVIGATOR_THESAURUS:
			case UIDesktopConstants.NAVIGATOR_CATEGORIES:
				tableSubscribedUsers.setVisible(true);
				tableProperties.getRowFormatter().setVisible(4, false);
				tableProperties.getRowFormatter().setVisible(5, true);
				tableProperties.getRowFormatter().setVisible(6, true);
				tableProperties.getRowFormatter().setVisible(7, false);
				break;
			case UIDesktopConstants.NAVIGATOR_MAIL:
				tableSubscribedUsers.setVisible(false);
				tableProperties.getRowFormatter().setVisible(4, false);
				tableProperties.getRowFormatter().setVisible(5, true);
				tableProperties.getRowFormatter().setVisible(6, false);
				tableProperties.getRowFormatter().setVisible(7, false);
				break;
		
			case UIDesktopConstants.NAVIGATOR_PERSONAL:
				tableSubscribedUsers.setVisible(false); // Some data must not be visible on personal view
				tableProperties.getRowFormatter().setVisible(4, false);
				tableProperties.getRowFormatter().setVisible(5, true); // Number of folders
				tableProperties.getRowFormatter().setVisible(6, true); // Number of documents
				//vissu touch modified 'true' to 'false' to make number of mails invisible on oct'9th
				tableProperties.getRowFormatter().setVisible(7, false); // Number of e-mails
				break;
		}
	}

	/**
	 * resetNumericFolderValues
	 */
	public void resetNumericFolderValues() {
		tableProperties.setHTML(5, 1, "");
		tableProperties.setHTML(6, 1, "");
		tableProperties.setHTML(7, 1, "");
	}
	
	/**
	 * setNumberOfFolders
	 */
	public void setNumberOfFolders(int num) {
		tableProperties.setHTML(5, 1, ""+num);
	}
	
	/**
	 * setNumberOfDocuments
	 */
	public void setNumberOfDocuments(int num) {
		tableProperties.setHTML(6, 1, ""+num);
	}
	
	/**
	 * setNumberOfMails
	 */
	public void setNumberOfMails(int num) {
		//vissu touch comment to hide mail num on oct'9th
		//tableProperties.setHTML(7, 1, ""+num);
	}
		
	/**
	 * Language refresh
	 */
	public void langRefresh() {
		tableProperties.setHTML(0, 0, "<b>"+Main.i18n("folder.uuid")+"</b>");
		tableProperties.setHTML(1, 0, "<b>"+Main.i18n("folder.name")+"</b>");
		tableProperties.setHTML(2, 0, "<b>"+Main.i18n("folder.parent")+"</b>");
		tableProperties.setHTML(3, 0, "<b>"+Main.i18n("folder.created")+"</b>");
		tableProperties.setHTML(4, 0, "<b>"+Main.i18n("folder.subscribed")+"</b>");
		tableProperties.setHTML(5, 0, "<b>"+Main.i18n("folder.number.folders")+"</b>");
		tableProperties.setHTML(6, 0, "<b>"+Main.i18n("folder.number.documents")+"</b>");
		tableProperties.setHTML(7, 0, "<b>"+Main.i18n("folder.number.mails")+"</b>");
		tableProperties.setHTML(8, 0, "<b>"+Main.i18n("folder.url")+"</b>");
		tableProperties.setHTML(9, 0, "<b>"+Main.i18n("folder.webdav")+"</b>");
		
		if (folder!=null) {
			if (folder.isSubscribed()) {
				tableProperties.setHTML(4, 1, Main.i18n("folder.subscribed.yes"));
			} else {
				tableProperties.setHTML(4, 1, Main.i18n("folder.subscribed.no"));
			}
		}
		
		subcribedUsersText.setHTML("<b>"+Main.i18n("folder.subscribed.users")+"<b>");
	}
}