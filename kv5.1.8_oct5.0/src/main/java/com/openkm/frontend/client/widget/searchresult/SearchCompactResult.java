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

package com.openkm.frontend.client.widget.searchresult;

import com.google.gwt.gen2.table.client.AbstractScrollTable.ScrollTableImages;
import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.bean.GWTQueryParams;
import com.openkm.frontend.client.bean.GWTQueryResult;
import com.openkm.frontend.client.util.CommonUI;
import com.openkm.frontend.client.util.Util;

/**
 * SearchCompactResult
 * 
 * @author jllort
 *
 */
public class SearchCompactResult extends Composite {
	
	// Number of columns
	public static final int NUMBER_OF_COLUMNS	= 8;
	
	public ExtendedScrollTable table;
	private FixedWidthFlexTable headerTable;
	private FixedWidthGrid dataTable;
	public MenuPopup menuPopup;
	
	/**
	 * SearchCompactResult
	 */
	public SearchCompactResult() {
		menuPopup = new MenuPopup();
		menuPopup.setStyleName("okm-SearchResult-MenuPopup");	
		
		ScrollTableImages scrollTableImages = new ScrollTableImages() {
			public AbstractImagePrototype scrollTableAscending() {
				return new AbstractImagePrototype() {
					public void applyTo(Image image) {
						image.setUrl("img/sort_asc.gif");
					}
					public Image createImage() {
						return  new Image("img/sort_asc.gif");
					}
					public String getHTML(){
						return "<img border=\"0\" src=\"img/sort_asc.gif\"/>";
					}
				};
			}
			
			public AbstractImagePrototype scrollTableDescending() {
				return new AbstractImagePrototype() {
					public void applyTo(Image image) {
						image.setUrl("img/sort_desc.gif");
					}
					public Image createImage() {
						return  new Image("img/sort_desc.gif");
					}
					public String getHTML(){
						return "<img border=\"0\" src=\"img/sort_desc.gif\"/>";
					}
				};
			}

			public AbstractImagePrototype scrollTableFillWidth() {
				return new AbstractImagePrototype() {
					public void applyTo(Image image) {
						image.setUrl("img/fill_width.gif");
					}
					public Image createImage() {
						return  new Image("img/fill_width.gif");
					}
					public String getHTML(){
						return "<img border=\"0\" src=\"img/fill_width.gif\"/>";
					}
				};
			}
		};
		
		headerTable = new FixedWidthFlexTable();
		dataTable = new FixedWidthGrid();
		table = new ExtendedScrollTable(dataTable,headerTable,scrollTableImages);
		table.setCellSpacing(0);
		table.setCellPadding(2);
		table.setSize("540","140");
		
		// Level 1 headers
	    headerTable.setHTML(0, 0, Main.i18n("search.result.score"));
	    headerTable.setHTML(0, 1, "&nbsp;");
	    headerTable.setHTML(0, 2, Main.i18n("search.result.name"));
	    headerTable.setHTML(0, 3, Main.i18n("search.result.size"));
	    headerTable.setHTML(0, 4, Main.i18n("search.result.date.update"));
	    headerTable.setHTML(0, 5, Main.i18n("search.result.author"));
	    headerTable.setHTML(0, 6, Main.i18n("search.result.version"));
		
		// Format    
	    table.setColumnWidth(0,70);
	    table.setColumnWidth(1,25);
	    table.setColumnWidth(2,150);
	    table.setColumnWidth(3,100);
	    table.setColumnWidth(4,150);
	    table.setColumnWidth(5,110);
	    table.setColumnWidth(6,90);
	    
	    table.setPreferredColumnWidth(0, 70);
		table.setPreferredColumnWidth(1, 25);
		table.setPreferredColumnWidth(4, 150);
		
		table.addStyleName("okm-DisableSelect");
		table.addStyleName("okm-Input");
		
		initWidget(table);
	}
	
	/**
	 * Refreshing lang
	 */
	public void langRefresh() {
		headerTable.setHTML(0, 0, Main.i18n("search.result.score"));
		headerTable.setHTML(0, 2, Main.i18n("search.result.name"));
		headerTable.setHTML(0, 3, Main.i18n("search.result.size"));
		headerTable.setHTML(0, 4, Main.i18n("search.result.date.update"));
		headerTable.setHTML(0, 5, Main.i18n("search.result.author"));
		headerTable.setHTML(0, 6, Main.i18n("search.result.version"));
		menuPopup.langRefresh();
	}
	
	/**
	 * Removes all rows except the first
	 */
	public void removeAllRows() {
		// Purge all rows 
		while (dataTable.getRowCount() > 0) {
			dataTable.removeRow(0);
		}
		
		table.reset();
		table.getDataTable().resize(0, NUMBER_OF_COLUMNS);
	}
	
	/**
	 * Adds a document to the panel
	 * 
	 * @param doc The doc to add
	 */
	public void addRow(GWTQueryResult gwtQueryResult) {
		 table.addRow(gwtQueryResult);
	 }
	
	/**
	 * Show the browser menu
	 */
	public void showMenu() {
		// The browser menu depends on actual view
		// Must substract top position from Y Screen Position
		menuPopup.setPopupPosition(table.getMouseX(), table.getMouseY());
		menuPopup.show();		
	}
	
	/**
	 * Download document
	 */
	public void downloadDocument() {
		if (!dataTable.getSelectedRows().isEmpty()) {
			if (table.isDocumentSelected()) {
				Util.downloadFile(getDocument().getPath(), "");
			} else if (table.isAttachmentSelected()) {
				Util.downloadFile(getAttachment().getPath(), "");
			}
		}
	}
	
	/**
	 * Open all folder path
	 */
	public void openAllFolderPath() {
		String docPath = "";
		String path = "";
		if (table.isDocumentSelected() || table.isAttachmentSelected()) {
			if (table.isAttachmentSelected()) {
				docPath = getAttachment().getParent();
			} else {
				docPath = getDocument().getPath();
			}
			path = docPath.substring(0,docPath.lastIndexOf("/"));
			
		} else if (table.isFolderSelected()) {
			path = getFolder().getPath();
			
		} else if (table.isMailSelected()) {
			docPath = getMail().getPath();
			path = docPath.substring(0,docPath.lastIndexOf("/"));
		}
		CommonUI.openAllFolderPath(path, docPath);
		menuPopup.hide();
	}
	
	/**
	 * Gets a actual document object row
	 * 
	 * @return The Document object value
	 */
	public GWTDocument getDocument() {
		//Row selected must be on table documents
		return table.getDocument();
	}
	
	/**
	 * Gets a actual attachment object row
	 * 
	 * @return The Attachment object value
	 */
	public GWTDocument getAttachment() {
		//Row selected must be on table documents
		return table.getAttachment();
	}
	
	/**
	 * Gets a actual folder object row
	 * 
	 * @return The folder object value
	 */
	public GWTFolder getFolder() {
		//Row selected must be on table documents
		return table.getFolder();
	}
	
	/**
	 * Gets a actual mail object row
	 * 
	 * @return The mail object value
	 */
	public GWTMail getMail() {
		//Row selected must be on table documents
		return table.getMail();
	}
	
	/**
	 * Call Back get search
	 */
	final AsyncCallback<GWTQueryParams> callbackGetSearch = new AsyncCallback<GWTQueryParams>() {
		public void onSuccess(GWTQueryParams result){
			GWTQueryParams gWTParams = result;	
			Main.get().mainPanel.search.searchBrowser.searchIn.setSavedSearch(gWTParams);
			removeAllRows();
		}
		
		public void onFailure(Throwable caught) {
			Main.get().showError("getSearch", caught);
		}
	};
	
	/**
	 * Indicates if panel is selected
	 * 
	 * @return The value of panel ( selected )
	 */
	public boolean isPanelSelected() {
		return table.isPanelSelected();
	}
	
	/**
	 * Sets the selected panel value
	 * 
	 * @param selected The select panel value
	 */
	public void setSelectedPanel(boolean selected) {
		table.setSelectedPanel(selected);
	}
	
	/**
	 * Fix width
	 */
	public void fixWidth() {
		table.fillWidth();
	}
}