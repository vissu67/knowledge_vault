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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTKeyword;
import com.openkm.frontend.client.bean.GWTPermission;
import com.openkm.frontend.client.contants.service.RPCService;
import com.openkm.frontend.client.contants.ui.UIDesktopConstants;
import com.openkm.frontend.client.extension.event.HasDocumentEvent;
import com.openkm.frontend.client.service.OKMDocumentService;
import com.openkm.frontend.client.service.OKMDocumentServiceAsync;
import com.openkm.frontend.client.service.OKMPropertyService;
import com.openkm.frontend.client.service.OKMPropertyServiceAsync;
import com.openkm.frontend.client.util.CommonUI;
import com.openkm.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.dashboard.ImageHover;
import com.openkm.frontend.client.widget.dashboard.keymap.TagCloud;
import com.openkm.frontend.client.widget.thesaurus.ThesaurusSelectPopup;

/**
 * Document
 * 
 * @author jllort
 *
 */
public class Document extends Composite {
	private final OKMDocumentServiceAsync documentService = (OKMDocumentServiceAsync) GWT.create(OKMDocumentService.class);
	private final OKMPropertyServiceAsync propertyService = (OKMPropertyServiceAsync) GWT.create(OKMPropertyService.class);
	
	private FlexTable tableProperties;
	private FlexTable tableSubscribedUsers;
	private FlexTable tableSubscribedCategories;
	private FlexTable table;
	private GWTDocument document;
	private HorizontalPanel keywordPanel;
	private SimplePanel sp;
	private ScrollPanel scrollPanel;
	private SuggestBox suggestKey;
	private MultiWordSuggestOracle multiWordkSuggestKey; 
	private List<String> keywordList;
	private FlowPanel hKeyPanel;
	private Map<String,Widget> keywordMap;
	private TagCloud keywordsCloud;
	private boolean visible = true;
	private HTML subcribedUsersText;
	private HTML keywordsCloudText;
	private Image categoriesImage;
	private Image thesaurusImage;
	private HTML categoriesText;
	private boolean remove = true;
	private List<String> keyWordsListPending; // Keyword list pending to be added ( each one is added sequentially )
	HorizontalPanel hPanelSubscribedUsers;
	
	public Document() {
		keywordMap = new HashMap<String,Widget>();
		keyWordsListPending = new ArrayList<String>();
		document = new GWTDocument();
		table = new FlexTable();
		tableProperties = new FlexTable();
		tableSubscribedUsers = new FlexTable();
		tableSubscribedCategories = new FlexTable();
		keywordsCloud = new TagCloud();
		scrollPanel = new ScrollPanel(table);
		keywordPanel = new HorizontalPanel();
		sp = new SimplePanel();
		sp.setWidth("16px");

		multiWordkSuggestKey = new MultiWordSuggestOracle();
		keywordList = new ArrayList<String>();
		suggestKey = new SuggestBox(multiWordkSuggestKey);
		suggestKey.setHeight("20");
		suggestKey.setText(Main.i18n("dashboard.keyword.suggest"));
		suggestKey.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if ((char)KeyCodes.KEY_ENTER == event.getNativeKeyCode() && keyWordsListPending.isEmpty()) {
					Main.get().mainPanel.enableKeyShorcuts(); 			// Enables general keys applications
					String keys[] = suggestKey.getText().split(" "); 	// Separates keywords by space
					for (int i=0;i<keys.length;i++) {
						keyWordsListPending.add(keys[i]);
					}
					addPendingKeyWordsList();
					suggestKey.setText("");
				}
			}
		});
		suggestKey.getTextBox().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (suggestKey.getText().equals(Main.i18n("dashboard.keyword.suggest"))) {
					suggestKey.setText("");
				}
				Main.get().mainPanel.disableKeyShorcuts(); // Disables key shortcuts while updating
			}
		});
		
		thesaurusImage = new Image(OKMBundleResources.INSTANCE.bookOpenIcon());
		thesaurusImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Main.get().mainPanel.desktop.navigator.thesaurusTree.thesaurusSelectPopup.show(ThesaurusSelectPopup.DOCUMENT_PROPERTIES);
			}
		});
		
		VerticalPanel vPanel = new VerticalPanel();
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.add(suggestKey);
		//vissu touch to hide thesaurus image on oct'9th
		//hPanel.add(new HTML("&nbsp;"));
		//hPanel.add(thesaurusImage);
		hKeyPanel = new FlowPanel();
		HTML space = new HTML("");
		vPanel.add(hPanel);
		vPanel.add(space);
		vPanel.add(hKeyPanel);
		
		hKeyPanel.setWidth("250");
		vPanel.setCellHeight(space, "5");
		
		keywordPanel.add(vPanel);
		keywordPanel.add(sp);
		
		tableProperties.setHTML(0, 0, "<b>"+Main.i18n("document.uuid")+"</b>");
		tableProperties.setHTML(0, 1, "");
		tableProperties.setHTML(1, 0, "<b>"+Main.i18n("document.name")+"</b>");
		tableProperties.setHTML(1, 1, "");
		tableProperties.setHTML(2, 0, "<b>"+Main.i18n("document.folder")+"</b>");
		tableProperties.setHTML(3, 1, "");
		tableProperties.setHTML(3, 0, "<b>"+Main.i18n("document.size")+"</b>");
		tableProperties.setHTML(4, 1, "");
		tableProperties.setHTML(4, 0, "<b>"+Main.i18n("document.created")+"</b>");
		tableProperties.setHTML(5, 1, "");
		tableProperties.setHTML(5, 0, "<b>"+Main.i18n("document.lastmodified")+"</b>");
		tableProperties.setHTML(5, 1, "");
		tableProperties.setHTML(6, 0, "<b>"+Main.i18n("document.mimetype")+"</b>");
		tableProperties.setHTML(6, 1, "");
		tableProperties.setHTML(7, 0, "<b>"+Main.i18n("document.keywords")+"</b>");
		tableProperties.setHTML(7, 1, "");
		tableProperties.setHTML(8, 0, "<b>"+Main.i18n("document.status")+"</b>");
		tableProperties.setHTML(8, 1, "");
		tableProperties.setHTML(9, 0, "<b>"+Main.i18n("document.subscribed")+"</b>");
		tableProperties.setHTML(9, 1, "");
		tableProperties.setHTML(10, 0, "<b>"+Main.i18n("document.history.size")+"</b>");
		tableProperties.setHTML(10, 1, "");
		//vissu touch hide below on oct'9th
		/*tableProperties.setHTML(11, 0, "<b>"+Main.i18n("document.url")+"</b>");
		tableProperties.setWidget(11, 1, new HTML(""));
		tableProperties.setHTML(12, 0, "<b>"+Main.i18n("document.webdav")+"</b>");
		tableProperties.setWidget(12, 1, new HTML(""));	*/
		
		tableProperties.getCellFormatter().setVerticalAlignment(7, 0, HasAlignment.ALIGN_TOP);
		
		// Sets the tagcloud
		keywordsCloud.setWidth("350");
		
		VerticalPanel vPanel2 = new VerticalPanel();
		
		hPanelSubscribedUsers = new HorizontalPanel();
		subcribedUsersText = new HTML("<b>"+Main.i18n("document.subscribed.users")+"<b>");
		hPanelSubscribedUsers.add(subcribedUsersText);
		hPanelSubscribedUsers.add(new HTML("&nbsp;"));
		hPanelSubscribedUsers.setCellVerticalAlignment(subcribedUsersText, HasAlignment.ALIGN_MIDDLE);

		keywordsCloudText = new HTML("<b>"+Main.i18n("document.keywords.cloud")+"</b>");
		
		HorizontalPanel hPanelCategories = new HorizontalPanel();
		categoriesText = new HTML("<b>"+Main.i18n("document.categories")+"</b>");
		categoriesImage = new Image(OKMBundleResources.INSTANCE.tableKeyIcon());
		categoriesImage.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Main.get().mainPanel.desktop.navigator.categoriesTree.categoriesSelectPopup.show();
			}
		});
		
		//vissu touch to hide below on oct'9th
		/*hPanelCategories.add(categoriesText);
		hPanelCategories.add(new HTML("&nbsp;"));
		hPanelCategories.add(categoriesImage);	*/
		
		hPanelCategories.setCellVerticalAlignment(categoriesText, HasAlignment.ALIGN_MIDDLE);
		
		vPanel2.add(hPanelSubscribedUsers);
		vPanel2.add(tableSubscribedUsers);
		HTML space2 = new HTML("");
		vPanel2.add(space2);
		vPanel2.add(keywordsCloudText);
		vPanel2.add(keywordsCloud);
		HTML space3 = new HTML("");
		vPanel2.add(space3);
		vPanel2.add(hPanelCategories);
		vPanel2.add(tableSubscribedCategories);
		
		vPanel2.setCellHeight(space2, "10");
		vPanel2.setCellHeight(space3, "10");
		
		table.setWidget(0, 0, tableProperties);
		table.setHTML(0, 1, "");
		table.setWidget(0, 2, vPanel2);
		
		// The hidden column extends table to 100% width
		CellFormatter cellFormatter = table.getCellFormatter();
		cellFormatter.setWidth(0, 1, "25");
		cellFormatter.setVerticalAlignment(0, 0, HasAlignment.ALIGN_TOP);
		cellFormatter.setVerticalAlignment(0, 2, HasAlignment.ALIGN_TOP);
		
		// Sets wordWrap for al rows
		for (int i=0; i<11; i++) {
			setRowWordWarp(i, 0, true, tableProperties);
		}

		setRowWordWarp(0, 0, true, tableSubscribedUsers);
		setRowWordWarp(0, 0,true, tableSubscribedCategories);
		
		tableProperties.setStyleName("okm-DisableSelect");
		tableSubscribedUsers.setStyleName("okm-DisableSelect");
		tableSubscribedCategories.setStyleName("okm-DisableSelect");
		suggestKey.setStyleName("okm-KeyMap-Suggest");
		suggestKey.addStyleName("okm-Input");
		hKeyPanel.setStylePrimaryName("okm-cloudWrap");
		keywordsCloud.setStylePrimaryName("okm-cloudWrap");
		categoriesImage.addStyleName("okm-Hyperlink");
		thesaurusImage.addStyleName("okm-Hyperlink");
		
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
			cellFormatter.setWordWrap(row, i, warp);
		}
	}
	
	/**
	 * Sets the document values
	 * 
	 * @param doc The document object
	 */
	public void set(GWTDocument doc) {
		keywordMap = new HashMap<String,Widget>();
		keyWordsListPending = new ArrayList<String>();
		this.document = doc;
		

		//vissu touch to add below on oct'9th
		String parentPath = doc.getParentId();
		parentPath = parentPath.replaceAll("okm:root","Knowledge Vault");
		tableProperties.setHTML(2, 1, parentPath);

		// URL clipboard button
		String url = Main.get().workspaceUserProperties.getApplicationURL();
		url += "?docPath=" + URL.encodeQueryString(document.getPath());
		//vissu touch commented to hide on oct'9th
		//tableProperties.setWidget(11, 1, new HTML("<div id=\"urlclipboardcontainer\"></div>\n"));
		Util.createURLClipboardButton(url);
		
		// Webdav button
		String webdavUrl = Main.get().workspaceUserProperties.getApplicationURL();
		String webdavPath = document.getPath();
		// Replace only in case webdav fix is enabled
		if (Main.get().workspaceUserProperties.getWorkspace().isWebdavFix()) {
			webdavPath.replace("okm:", "okm_");
		}
		
		webdavUrl = webdavUrl.substring(0, webdavUrl.lastIndexOf('/')) + "/repository/default" + webdavPath;
		//vissu touch commented to hide on oct'9th
		//tableProperties.setWidget(12, 1, new HTML("<div id=\"webdavclipboardcontainer\"></div>\n"));
		Util.createWebDavClipboardButton(webdavUrl);
		
		tableProperties.setHTML(0, 1, doc.getUuid());
		tableProperties.setHTML(1, 1, doc.getName());
		//hide parent id vissu touch oct 9th
		//tableProperties.setHTML(2, 1, doc.getParentId());
		tableProperties.setHTML(3, 1, Util.formatSize(doc.getActualVersion().getSize()));
		DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
		tableProperties.setHTML(4, 1, dtf.format(doc.getCreated())+" "+Main.i18n("document.by")+" "+doc.getAuthor());
		tableProperties.setHTML(5, 1, dtf.format(doc.getLastModified())+" "+Main.i18n("document.by")+" "+doc.getActualVersion().getAuthor());
		tableProperties.setHTML(6, 1, doc.getMimeType());
		tableProperties.setWidget(7, 1, keywordPanel);
		hKeyPanel.clear();
		
		remove = ((doc.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE && !doc.isCheckedOut() && 
				  !(doc.isLocked() && !doc.getLockInfo().getOwner().equals(Main.get().workspaceUserProperties.getUser()))) && visible;
		
		for (Iterator<String> it = doc.getKeywords().iterator(); it.hasNext();) {
			// First adds only new keywords
			final String keyword = it.next();
			Widget keywordButton = getKeyWidget(keyword, remove);
			keywordMap.put(keyword, keywordButton);
			hKeyPanel.add(keywordButton);
		}
		
		// Reloading keyword list
		multiWordkSuggestKey.clear();
		keywordList = new ArrayList<String>();
		for (Iterator<GWTKeyword> it = Main.get().mainPanel.dashboard.keyMapDashboard.getAllKeywordList().iterator(); it.hasNext();) {
			String keyword = it.next().getKeyword();
			multiWordkSuggestKey.add(keyword);
			keywordList.add(keyword);
		}
		
		if (doc.isCheckedOut()) {
			tableProperties.setHTML(8, 1, Main.i18n("document.status.checkout")+" "+doc.getLockInfo().getOwner());
		} else if (doc.isLocked()) {
			tableProperties.setHTML(8, 1, Main.i18n("document.status.locked")+" "+doc.getLockInfo().getOwner());
		} else {
			tableProperties.setHTML(8, 1, Main.i18n("document.status.normal"));
		}
		
		if (doc.isSubscribed()) {
			tableProperties.setHTML(9, 1, Main.i18n("document.subscribed.yes"));
		} else {
			tableProperties.setHTML(9, 1, Main.i18n("document.subscribed.no"));
		}
		
		// Enables or disables change keywords with user permissions and document is not check-out or locked
		if (remove)  {
			suggestKey.setVisible(true);
			categoriesImage.setVisible(true);
			thesaurusImage.setVisible(true);
		} else {
			suggestKey.setVisible(false);
			categoriesImage.setVisible(false);
			thesaurusImage.setVisible(false);
		}
		
		getVersionHistorySize();
		
		// Sets wordWrap for al rows
		for (int i=0; i<11; i++) {
			setRowWordWarp(i, 1, true, tableProperties);
		}
		
		// Remove all table rows >= 1
		while (tableSubscribedUsers.getRowCount()>0) {
			tableSubscribedUsers.removeRow(0);
		}
		while(tableSubscribedCategories.getRowCount()>0) {
			tableSubscribedCategories.removeRow(0);
		}
		
		
		// Sets the document subscribers
		for (Iterator<String> it= doc.getSubscriptors().iterator(); it.hasNext(); ) {
			tableSubscribedUsers.setHTML(tableSubscribedUsers.getRowCount(), 0, it.next());
			setRowWordWarp(tableSubscribedUsers.getRowCount()-1, 0, true, tableSubscribedUsers);
		}
		
		// Sets the document categories
		for (Iterator<GWTFolder> it = doc.getCategories().iterator(); it.hasNext();) {
			drawCategory(it.next(),remove);
		}
		
		drawTagCloud(doc.getKeywords());
		
		// Some preoperties only must be visible on taxonomy or trash view
		int actualView = Main.get().mainPanel.desktop.navigator.getStackIndex();
		if (actualView==UIDesktopConstants.NAVIGATOR_TRASH) {
			tableProperties.getCellFormatter().setVisible(7,0,false);
			tableProperties.getCellFormatter().setVisible(7,1,false);
			tableProperties.getCellFormatter().setVisible(9,0,false);
			tableProperties.getCellFormatter().setVisible(9,1,false);
		} else {
			tableProperties.getCellFormatter().setVisible(7,0,true);
			tableProperties.getCellFormatter().setVisible(7,1,true);
			tableProperties.getCellFormatter().setVisible(9,0,true);
			tableProperties.getCellFormatter().setVisible(9,1,true);
		}
		keywordsCloudText.setVisible(true);
		keywordsCloud.setVisible(true);
		
		// Some data must not be visible on personal view
		if (actualView==UIDesktopConstants.NAVIGATOR_PERSONAL) {
			subcribedUsersText.setVisible(false);
			tableSubscribedUsers.setVisible(false);
			tableSubscribedCategories.setVisible(false);
		} else {
			subcribedUsersText.setVisible(true);
			tableSubscribedUsers.setVisible(true);
			tableSubscribedCategories.setVisible(true);
		}
	}
	
	/**
	 * Lang refresh
	 */
	public void langRefresh() {
		tableProperties.setHTML(0, 0, "<b>"+Main.i18n("document.uuid")+"</b>");
		tableProperties.setHTML(1, 0, "<b>"+Main.i18n("document.name")+"</b>");
		tableProperties.setHTML(2, 0, "<b>"+Main.i18n("document.folder")+"</b>");
		tableProperties.setHTML(3, 0, "<b>"+Main.i18n("document.size")+"</b>");
		tableProperties.setHTML(4, 0, "<b>"+Main.i18n("document.created")+"</b>");
		tableProperties.setHTML(5, 0, "<b>"+Main.i18n("document.lastmodified")+"</b>");
		tableProperties.setHTML(6, 0, "<b>"+Main.i18n("document.mimetype")+"</b>");
		tableProperties.setHTML(7, 0, "<b>"+Main.i18n("document.keywords")+"</b>");
		tableProperties.setHTML(8, 0, "<b>"+Main.i18n("document.status")+"</b>");
		tableProperties.setHTML(9, 0, "<b>"+Main.i18n("document.subscribed")+"</b>");
		tableProperties.setHTML(10, 0, "<b>"+Main.i18n("document.history.size")+"</b>");
		tableProperties.setHTML(11, 0, "<b>"+Main.i18n("document.url")+"</b>");
		tableProperties.setHTML(12, 0, "<b>"+Main.i18n("document.webdav")+"</b>");
		keywordsCloudText.setHTML("<b>"+Main.i18n("document.keywords.cloud")+"</b>");
		subcribedUsersText.setHTML("<b>"+Main.i18n("document.subscribed.users")+"<b>");
		categoriesText.setHTML("<b>"+Main.i18n("document.categories")+"</b>");
		
		if (document != null) {
			DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
			if (document.getCreated() != null) {
				tableProperties.setHTML(4, 1, dtf.format(document.getCreated())+" "+Main.i18n("document.by")+" "+document.getAuthor());
			}
			
			if (document.getLastModified() != null) {
				tableProperties.setHTML(5, 1, dtf.format(document.getLastModified())+" "+Main.i18n("document.by")+" "+document.getActualVersion().getAuthor());
			}

			if (document.isCheckedOut()) {
				tableProperties.setHTML(8, 1, Main.i18n("document.status.checkout")+" "+document.getLockInfo().getOwner());
			} else if (document.isLocked()) {
				tableProperties.setHTML(8, 1, Main.i18n("document.status.locked")+" "+document.getLockInfo().getOwner());
			} else {
				tableProperties.setHTML(8, 1, Main.i18n("document.status.normal"));
			}
			
			if (document.isSubscribed()) {
				tableProperties.setHTML(9, 1, Main.i18n("document.subscribed.yes"));
			} else {
				tableProperties.setHTML(9, 1, Main.i18n("document.subscribed.no"));
			}
		}
	}	
	
	/**
	 * Callback GetVersionHistorySize document
	 */
	final AsyncCallback<Long> callbackGetVersionHistorySize = new AsyncCallback<Long>() {
		public void onSuccess(Long result) {	
			tableProperties.setHTML(10, 1, Util.formatSize(result.longValue()));
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetGetVersionHistorySize();
		}

		public void onFailure(Throwable caught) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetGetVersionHistorySize();
			Main.get().showError("GetVersionHistorySize", caught);
		}
	};
	
	/**
	 * Callback addKeyword document
	 */
	final AsyncCallback<Object> callbackAddKeywords = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {	
			if (keyWordsListPending.isEmpty()) {
				Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetKeywords();
				drawTagCloud(document.getKeywords());
				Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.fireEvent(HasDocumentEvent.KEYWORD_ADDED);
			} else {
				addPendingKeyWordsList();
			}
			
		}

		public void onFailure(Throwable caught) {
			if (keyWordsListPending.isEmpty()) {
				Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetKeywords();
				drawTagCloud(document.getKeywords());
			} else {
				addPendingKeyWordsList();
			}
			Main.get().showError("AddKeyword", caught);
		}
	};
	
	/**
	 * Callback removeKeyword document
	 */
	final AsyncCallback<Object> callbackRemoveKeywords = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {	
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetKeywords();
			Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.fireEvent(HasDocumentEvent.KEYWORD_REMOVED);
		}

		public void onFailure(Throwable caught) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetKeywords();
			Main.get().showError("RemoveKeyword", caught);
		}
	};
	
	/**
	 * Callback addCategory document
	 */
	final AsyncCallback<Object> callbackAddCategory = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {	
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetCategories();
			Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.fireEvent(HasDocumentEvent.CATEGORY_ADDED);
		}

		public void onFailure(Throwable caught) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetCategories();
			Main.get().showError("AddCategory", caught);
		}
	};
	
	/**
	 * Callback removeCategory document
	 */
	final AsyncCallback<Object> callbackRemoveCategory = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {	
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetCategories();
			Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.fireEvent(HasDocumentEvent.CATEGORY_REMOVED);
		}

		public void onFailure(Throwable caught) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetCategories();
			Main.get().showError("RemoveCategory", caught);
		}
	};
	
	/**
	 * getVersionHistorySize document
	 */
	public void getVersionHistorySize() {
		Main.get().mainPanel.desktop.browser.tabMultiple.status.setGetVersionHistorySize();
		ServiceDefTarget endPoint = (ServiceDefTarget) documentService;
		endPoint.setServiceEntryPoint(RPCService.DocumentService);
		documentService.getVersionHistorySize(document.getPath(), callbackGetVersionHistorySize);
	}
	
	/**
	 * addKeyword document
	 */
	public void addKeyword(String keyword) {
		ServiceDefTarget endPoint = (ServiceDefTarget) propertyService;
		endPoint.setServiceEntryPoint(RPCService.PropertyService);
		propertyService.addKeyword(document.getPath(), keyword, callbackAddKeywords);
	}
	
	/**
	 * removeKeyword document
	 */
	public void removeKeyword(String keyword) {
		Main.get().mainPanel.desktop.browser.tabMultiple.status.setKeywords();
		ServiceDefTarget endPoint = (ServiceDefTarget) propertyService;
		endPoint.setServiceEntryPoint(RPCService.PropertyService);
		propertyService.removeKeyword(document.getPath(), keyword, callbackRemoveKeywords);
	}
	
	/**
	 * addCategory document
	 */
	public void addCategory(GWTFolder category) {
		if (!existCategory(category.getUuid())) {
			document.getCategories().add(category);
			drawCategory(category,remove);
			Main.get().mainPanel.desktop.browser.tabMultiple.status.setCategories();
			ServiceDefTarget endPoint = (ServiceDefTarget) propertyService;
			endPoint.setServiceEntryPoint(RPCService.PropertyService);
			propertyService.addCategory(document.getPath(), category.getUuid(), callbackAddCategory);
		}
	}
	
	/**
	 * removeCategory document
	 */
	public void removeCategory(String UUID) {
		Main.get().mainPanel.desktop.browser.tabMultiple.status.setCategories();
		ServiceDefTarget endPoint = (ServiceDefTarget) propertyService;
		endPoint.setServiceEntryPoint(RPCService.PropertyService);
		propertyService.removeCategory(document.getPath(), UUID, callbackRemoveCategory);
	}
	
	/**
	 * Sets visibility to buttons ( true / false )
	 * 
	 * @param visible The visible value
	 */
	public void setVisibleButtons(boolean visible) {
		this.visible = visible;
		suggestKey.setVisible(visible);
		categoriesImage.setVisible(visible);
		thesaurusImage.setVisible(visible);
	}
	
	/**
	 * Removes a key
	 * 
	 * @param keyword The key to be removed
	 */
	public void removeKey(String keyword) {
		if (keywordMap.containsKey(keyword)) {
			keywordMap.remove(keyword);
			document.getKeywords().remove(keyword);
			removeKeyword(keyword);
			Main.get().mainPanel.dashboard.keyMapDashboard.decreaseKeywordRate(keyword);
			drawTagCloud(document.getKeywords());
			if (Main.get().mainPanel.desktop.navigator.getStackIndex()==UIDesktopConstants.NAVIGATOR_THESAURUS) {
				GWTFolder folder = ((GWTFolder) Main.get().activeFolderTree.actualItem.getUserObject());
				// When remove the keyword for which are browsing must refreshing filebrowser view
				if (folder.getPath().substring(folder.getPath().lastIndexOf("/")+1).replace(" ", "_").equals(keyword)) {
					Main.get().mainPanel.desktop.browser.fileBrowser.refresh(folder.getPath());	
				}
			}
		}
	}
	
	/**
	 * addKeywordToPendinList
	 * 
	 * @param key
	 */
	public void addKeywordToPendinList(String key) {
		keyWordsListPending.add(key);
	}
	
	/**
	 * Adds keywords sequentially
	 * 
	 */
	public void addPendingKeyWordsList() {
		if (!keyWordsListPending.isEmpty()) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.setKeywords();
			String keyword = keyWordsListPending.remove(0);
			if (!keywordMap.containsKey(keyword) && keyword.length()>0) {
				for (Iterator<String> it = keywordMap.keySet().iterator(); it.hasNext();) {
					String key = it.next();
					if (!keywordList.contains(key)) {
						multiWordkSuggestKey.add(key);
						keywordList.add(key);
					}
				}
				Widget keywordButton = getKeyWidget(keyword, remove);
				keywordMap.put(keyword, keywordButton);
				hKeyPanel.add(keywordButton);
				document.getKeywords().add(keyword);
				addKeyword(keyword);
				Main.get().mainPanel.dashboard.keyMapDashboard.increaseKeywordRate(keyword);
			} else if (keyWordsListPending.isEmpty()) {
				Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetKeywords();
				drawTagCloud(document.getKeywords());
			}	
		}
	}
	
	
	/**
	 * Get a new widget keyword
	 * 
	 * @param keyword The keyword
	 * 
	 * @return The widget
	 */
	private HorizontalPanel getKeyWidget(final String keyword, boolean remove) {
		final HorizontalPanel externalPanel = new HorizontalPanel();
		HorizontalPanel hPanel = new HorizontalPanel();
		HTML space = new HTML();
		ImageHover delete = new ImageHover("img/icon/actions/delete_disabled.gif","img/icon/actions/delete.gif");
		delete.addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.document.removeKey(keyword);
				hKeyPanel.remove(externalPanel);
			}
		});
		delete.setStyleName("okm-KeyMap-ImageHover");
		hPanel.add(new HTML(keyword));
		hPanel.add(space);
		if (remove) {
			hPanel.add(delete);
		}
		hPanel.setCellWidth(space, "6");
		hPanel.setStyleName("okm-KeyMap-Gray");
		HTML space1 = new HTML();
		externalPanel.add(hPanel);
		externalPanel.add(space1);
		externalPanel.setCellWidth(space1, "6");
		externalPanel.setStylePrimaryName("okm-cloudTags");  
		return externalPanel;
	}
	
	/**
	 * Draws a tag cloud
	 */
	private void drawTagCloud(Collection<String> keywords) {
		// Deletes all tag clouds keys
		keywordsCloud.clear();
		keywordsCloud.setMinFrequency(Main.get().mainPanel.dashboard.keyMapDashboard.getTotalMinFrequency());
		keywordsCloud.setMaxFrequency(Main.get().mainPanel.dashboard.keyMapDashboard.getTotalMaxFrequency());
		
		for (Iterator<String> it = keywords.iterator(); it.hasNext();) {
			String keyword = it.next();
			HTML tagKey = new HTML(keyword);
			tagKey.setStyleName("okm-cloudTags");
			Style linkStyle = tagKey.getElement().getStyle();
			int fontSize = keywordsCloud.getLabelSize(Main.get().mainPanel.dashboard.keyMapDashboard.getKeywordRate(keyword));
			linkStyle.setProperty("fontSize", fontSize+"pt");
			linkStyle.setProperty("color", keywordsCloud.getColor(fontSize));
			if (fontSize>0) {
				linkStyle.setProperty("top", (keywordsCloud.getMaxFontSize()-fontSize)/2+"px" );
			} 
			keywordsCloud.add(tagKey);
		}
	}
	
	/**
	 * existCategory
	 * 
	 * @param Uuid
	 * @return
	 */
	private boolean existCategory(String Uuid) {
		boolean found = false;
		for (Iterator<GWTFolder> it = document.getCategories().iterator(); it.hasNext();) {
			if (it.next().getUuid().equals(Uuid)) {
				found = true;
				break;
			}
		}
		return found;
	}
	
	/**
	 * drawCategory
	 * 
	 * @param category
	 */
	private void drawCategory(final GWTFolder category, boolean remove) {
		int row = tableSubscribedCategories.getRowCount();
		Anchor anchor = new Anchor();
		// Looks if must change icon on parent if now has no childs and properties with user security atention
		String path = category.getPath().substring(16); // Removes /okm:categories
		if (category.getHasChilds()) {
			anchor.setHTML(Util.imageItemHTML("img/menuitem_childs.gif", path, "top"));
		} else {
			anchor.setHTML(Util.imageItemHTML("img/menuitem_empty.gif", path, "top"));
		}
		
		anchor.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				CommonUI.openAllFolderPath(category.getPath(), null);
			}
		});
		anchor.setStyleName("okm-KeyMap-ImageHover");
		
		Image delete = new Image(OKMBundleResources.INSTANCE.deleteIcon());
		delete.setStyleName("okm-KeyMap-ImageHover");
		delete.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				document.getCategories().remove(category);
				removeCategory(category.getUuid());
				tableSubscribedCategories.removeRow(tableSubscribedCategories.getCellForEvent(event).getRowIndex());
			}
		});
		
		tableSubscribedCategories.setWidget(row, 0, anchor);
		if (remove) {
			tableSubscribedCategories.setWidget(row, 1, delete);
		} else {
			tableSubscribedCategories.setWidget(row, 1, new HTML(""));
		}
		setRowWordWarp(row, 1, true, tableSubscribedCategories);
	}
	
	/**
	 * getKeywords
	 * 
	 * @return
	 */
	public Collection<String>  getKeywords() {
		return document.getKeywords();
	}
	
	/**
	 * @param enabled
	 */
	public void setKeywordEnabled(boolean enabled) {
		suggestKey.getTextBox().setEnabled(enabled);
	}
}
