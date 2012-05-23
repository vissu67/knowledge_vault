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

package com.openkm.frontend.client.widget.searchin;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.util.OKMBundleResources;

/**
 * @author jllort
 *
 */
public class SearchAdvanced extends Composite {
	
	private ScrollPanel scrollPanel;
	private FlexTable table;
	public HorizontalPanel pathExplorerPanel;
	public HorizontalPanel categoryExplorerPanel;
	public TextBox path;
	public Image pathExplorer;
	public Image categoryExplorer;
	public FolderSelectPopup folderSelectPopup;
	public TextBox categoryPath;
	public String categoryUuid = "";
	public HorizontalPanel typePanel;
	public CheckBox typeDocument;
	public CheckBox typeFolder;
	public CheckBox typeMail;
	public HTML document;
	public HTML folder;
	public HTML mail;
	public FlexTable tableMail;
	public ListBox mimeTypes;
	public TextBox from;
	public TextBox to;
	public TextBox subject;
	public HTML mailText;
	
	/**
	 * SearchAdvanced
	 */
	public SearchAdvanced() {
		table = new FlexTable();
		scrollPanel = new ScrollPanel(table);
		
		// Sets the folder explorer
		folderSelectPopup = new FolderSelectPopup();
		pathExplorerPanel = new HorizontalPanel();
		path = new TextBox();
		path.setReadOnly(true);
		pathExplorer =  new Image(OKMBundleResources.INSTANCE.folderExplorer());
		
		pathExplorerPanel.add(path);
		pathExplorerPanel.add(new HTML("&nbsp;"));
		pathExplorerPanel.add(pathExplorer);
		
		pathExplorer.addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				folderSelectPopup.show(false);
			}
		});
		
		pathExplorerPanel.setCellVerticalAlignment(pathExplorer, HasAlignment.ALIGN_MIDDLE);
		
		// Sets the category explorer
		categoryExplorerPanel = new HorizontalPanel();
		categoryPath = new TextBox();
		categoryUuid = "";
		categoryPath.setReadOnly(true);
		categoryExplorer =  new Image(OKMBundleResources.INSTANCE.folderExplorer());
		
		categoryExplorerPanel.add(categoryPath);
		categoryExplorerPanel.add(new HTML("&nbsp;"));
		categoryExplorerPanel.add(categoryExplorer);
		
		categoryExplorer.addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				folderSelectPopup.show(true);
			}
		});
		
		categoryExplorerPanel.setCellVerticalAlignment(categoryExplorer, HasAlignment.ALIGN_MIDDLE);
		
		// Sets type document
		tableMail = new FlexTable();
		typePanel = new HorizontalPanel();
		typeDocument = new CheckBox();
		typeDocument.setValue(true);
		document = new HTML(Main.i18n("search.type.document"));
		typeFolder = new CheckBox();
		typeFolder.setValue(false);
		folder = new HTML(Main.i18n("search.type.folder"));
		typeMail = new CheckBox();
		typeMail.setValue(false);
		typeMail.addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				if (typeMail.getValue()){
					mailText.setVisible(true);
					tableMail.setVisible(true);
				} else {
					mailText.setVisible(false);
					tableMail.setVisible(false);
				}
			}
		});
		mail = new HTML(Main.i18n("search.type.mail"));		
		typePanel.add(typeDocument);
		typePanel.add(document);
		typePanel.add(new HTML("&nbsp;"));
		typePanel.add(typeFolder);
		typePanel.add(folder);
		typePanel.add(new HTML("&nbsp;"));
		typePanel.add(typeMail);
		typePanel.add(mail);
		typePanel.add(new HTML("&nbsp;"));
		typePanel.setCellVerticalAlignment(document, HasAlignment.ALIGN_MIDDLE);
		typePanel.setCellVerticalAlignment(folder, HasAlignment.ALIGN_MIDDLE);
		typePanel.setCellVerticalAlignment(mail, HasAlignment.ALIGN_MIDDLE);
		
		// Sets mime types values
		mimeTypes = new ListBox();
		mimeTypes.addItem(" ", "");
		mimeTypes.addItem("HTML", "text/html");
		mimeTypes.addItem("MS Excel", "application/vnd.ms-excel");
		mimeTypes.addItem("MS PowerPoint", "application/vnd.ms-powerpoint");
		mimeTypes.addItem("MS Word", "application/msword");
		mimeTypes.addItem("OpenOffice.org Database", "application/vnd.oasis.opendocument.database");
		mimeTypes.addItem("OpenOffice.org Draw", "application/vnd.oasis.opendocument.graphics");
		mimeTypes.addItem("OpenOffice.org Presentation", "application/vnd.oasis.opendocument.presentation");
		mimeTypes.addItem("OpenOffice.org Spreadsheet", "application/vnd.oasis.opendocument.spreadsheet");
		mimeTypes.addItem("OpenOffice.org Word Processor", "application/vnd.oasis.opendocument.text");
		mimeTypes.addItem("PDF", "application/pdf");
		mimeTypes.addItem("RTF", "application/rtf");
		mimeTypes.addItem("TXT", "text/plain");
		mimeTypes.addItem("XML", "text/xml");
		
		mimeTypes.addChangeHandler(new ChangeHandler(){
			@Override
			public void onChange(ChangeEvent event) {
				Main.get().mainPanel.search.searchBrowser.searchIn.searchControl.evaluateSearchButtonVisible();							
			}
		});
		mailText = new HTML(Main.i18n("search.type.mail"));
		mailText.setVisible(false);
		table.setHTML(1, 0, Main.i18n("search.folder"));
		table.setWidget(1, 1, pathExplorerPanel);
		table.setHTML(2, 0, Main.i18n("search.category"));
		table.setWidget(2, 1, categoryExplorerPanel);
		table.setHTML(3, 0, Main.i18n("search.type"));
		table.setWidget(3, 1, typePanel);
		table.setHTML(4, 0, Main.i18n("search.mimetype"));
		table.setWidget(4, 1, mimeTypes);
		table.setWidget(5, 0, mailText);
		table.setWidget(5, 1, tableMail);
		table.getCellFormatter().setVerticalAlignment(5, 0, HasAlignment.ALIGN_TOP);
		
		// Adding mail search params
		from = new TextBox();
		to = new TextBox();
		subject = new TextBox();
		tableMail.setHTML(0, 0, Main.i18n("mail.from"));
		tableMail.setWidget(0, 1, from);
		tableMail.setHTML(1, 0, Main.i18n("mail.to"));
		tableMail.setWidget(1, 1, to);
		tableMail.setHTML(2, 0, Main.i18n("mail.subject"));
		tableMail.setWidget(2, 1, subject);
		setRowWordWarp(tableMail, 0, 2, false);
		setRowWordWarp(tableMail, 1, 2, false);
		setRowWordWarp(tableMail, 2, 2, false);
		setRowWordWarp(tableMail, 3, 2, false);
		tableMail.setVisible(false);
		
		path.setStyleName("okm-Input");
		categoryPath.setStyleName("okm-Input");
		folderSelectPopup.setStyleName("okm-Popup");
		folderSelectPopup.addStyleName("okm-DisableSelect");
		from.setStyleName("okm-Input");
		to.setStyleName("okm-Input");
		subject.setStyleName("okm-Input");
		document.addStyleName("okm-NoWrap");
		folder.addStyleName("okm-NoWrap");
		mail.addStyleName("okm-NoWrap");
		mimeTypes.setStyleName("okm-Select");
		
		initWidget(scrollPanel);
	}
	
	/**
	 * langRefresh
	 */
	public void langRefresh() {
		table.setHTML(1, 0, Main.i18n("search.folder"));
		table.setHTML(2, 0, Main.i18n("search.category"));
		table.setHTML(3, 0, Main.i18n("search.type"));
		table.setHTML(4, 0, Main.i18n("search.mimetype"));
		mailText.setHTML(Main.i18n("search.type.mail"));
		
		tableMail.setHTML(0, 0, Main.i18n("mail.from"));
		tableMail.setHTML(1, 0, Main.i18n("mail.to"));
		tableMail.setHTML(2, 0, Main.i18n("mail.subject"));
		
		document.setHTML(Main.i18n("search.type.document"));
		folder.setHTML(Main.i18n("search.type.folder"));
		mail.setHTML(Main.i18n("search.type.mail"));
		
		folderSelectPopup.langRefresh();
	}
	
	/**
	 * Set the WordWarp for all the row cells
	 * 
	 * @param row The row cell
	 * @param columns Number of row columns
	 * @param warp
	 */
	private void setRowWordWarp(FlexTable table, int row, int columns, boolean wrap) {
		CellFormatter cellFormatter = table.getCellFormatter();
		for (int i=0; i<columns; i++) {
			cellFormatter.setWordWrap(row, i, wrap);
		}
	}
}