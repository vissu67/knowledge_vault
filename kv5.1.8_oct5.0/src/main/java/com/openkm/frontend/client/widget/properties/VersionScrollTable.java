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
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.gen2.table.client.ScrollTable;
import com.google.gwt.gen2.table.client.SelectionGrid;
import com.google.gwt.gen2.table.client.AbstractScrollTable.ResizePolicy;
import com.google.gwt.gen2.table.client.AbstractScrollTable.ScrollPolicy;
import com.google.gwt.gen2.table.client.AbstractScrollTable.ScrollTableImages;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTPermission;
import com.openkm.frontend.client.bean.GWTVersion;
import com.openkm.frontend.client.contants.service.RPCService;
import com.openkm.frontend.client.service.OKMDocumentService;
import com.openkm.frontend.client.service.OKMDocumentServiceAsync;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.ConfirmPopup;

/**
 * VersionScrollTable
 * 
 * @author jllort
 *
 */
public class VersionScrollTable extends Composite implements ClickHandler  {
	
	private final OKMDocumentServiceAsync documentService = (OKMDocumentServiceAsync) GWT.create(OKMDocumentService.class);
	
	// Number of columns
	public static final int NUMBER_OF_COLUMNS = 7;
	
	private GWTDocument doc;
	private ScrollTable table;
	private FixedWidthFlexTable headerTable;
	private FixedWidthGrid dataTable;
	public List<String> versions;
	private boolean visibleButtons = true;
	private Button purge;
	private List<Button> buttonView;
	private List<Button> buttonRestore;
	
	/**
	 * Version
	 */
	public VersionScrollTable() {
		versions = new ArrayList<String>();
		buttonView = new ArrayList<Button>();
		buttonRestore = new ArrayList<Button>();
		
		purge = new Button(Main.i18n("version.purge.document"),this);
		purge.setStyleName("okm-Button");
		purge.setEnabled(false);
		
		ScrollTableImages scrollTableImages = new ScrollTableImages(){
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
		
		table = new ScrollTable(dataTable, headerTable, scrollTableImages);
		table.setCellSpacing(0);
		table.setCellPadding(2);
		table.setSize("540", "140");
		table.setColumnWidth(0, 89);
	    table.setColumnWidth(1, 264);
	    table.setColumnWidth(2, 113);
	    table.setColumnWidth(3, 140);
	    table.setColumnWidth(4, 103);
    	table.setColumnWidth(5, 130);
    	table.setColumnWidth(6, 113);

    	table.setPreferredColumnWidth(0, 89);
		table.setPreferredColumnWidth(1, 264);
		
		table.setColumnSortable(5, false);
		
		// Level 1 headers
	    headerTable.setHTML(0, 0, Main.i18n("version.name"));
	    //by vissu nov 20 - Interchanged created to comment
	    headerTable.setHTML(0, 1, Main.i18n("version.comment"));
	    
	    headerTable.setHTML(0, 2, Main.i18n("version.created"));

	    headerTable.setHTML(0, 3, Main.i18n("version.author"));
	    headerTable.setHTML(0, 4, Main.i18n("version.size"));
	    headerTable.setHTML(0, 5, "&nbsp;");
	    headerTable.setWidget(0, 6, purge);
	    
	    headerTable.getCellFormatter().setHorizontalAlignment(0,5,HasAlignment.ALIGN_CENTER);
	    headerTable.getCellFormatter().setVerticalAlignment(0,5,HasAlignment.ALIGN_MIDDLE);
	    
	    // Table data
	    dataTable.setSelectionPolicy(SelectionGrid.SelectionPolicy.ONE_ROW);
	    table.setResizePolicy(ResizePolicy.UNCONSTRAINED);
	    table.setScrollPolicy(ScrollPolicy.BOTH);
	    
	    headerTable.addStyleName("okm-DisableSelect");
	    dataTable.addStyleName("okm-DisableSelect");
		
		initWidget(table);
	}
	
	/**
	 * Language refresh
	 */
	public void langRefresh() {
		headerTable.setHTML(0, 0, Main.i18n("version.name"));
	    //by vissu nov 20 - Interchanged created to comment
		headerTable.setHTML(0, 1, Main.i18n("version.comment"));
		headerTable.setHTML(0, 2, Main.i18n("version.created"));

		headerTable.setHTML(0, 3, Main.i18n("version.author"));
		headerTable.setHTML(0, 4, Main.i18n("version.size"));

		purge.setHTML(Main.i18n("version.purge.document"));
		
		// Translate all view buttons
		if (!buttonView.isEmpty()) {
			for (Iterator<Button> it = buttonView.iterator(); it.hasNext();) {
				Button button = it.next();
				button.setHTML(Main.i18n("button.view"));
			}
		}
		
		if (!buttonRestore.isEmpty()) {
			for (Iterator<Button> it = buttonRestore.iterator(); it.hasNext();) {
				Button button = it.next();
				button.setHTML(Main.i18n("button.restore"));
			}
		}
	}
	
	/**
	 * Sets the document
	 * 
	 * @param GWTDocument The document
	 */
	public void set(GWTDocument doc) {
		this.doc = doc;
	}
	
	/**
	 * Removes all rows except the first
	 */
	private void removeAllRows() {
		// Purge all rows except first
		while (dataTable.getRowCount() > 0) {
			dataTable.removeRow(0);
		}
		dataTable.resize(0, NUMBER_OF_COLUMNS);
		versions = new ArrayList<String>();
	}
	
	/**
	 * Adds a version to the history table
	 * 
	 * @param version The Version to add
	 */
	private void addRow(GWTVersion version) {
		final int rows = dataTable.getRowCount();
		dataTable.insertRow(rows);
		dataTable.setHTML(rows, 0, version.getName());
		DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
	    //by vissu nov 20 - Interchanged created to comment
		dataTable.setHTML(rows, 1, version.getComment());
		dataTable.setHTML(rows, 2, dtf.format(version.getCreated()));

		dataTable.setHTML(rows, 3, version.getAuthor());
		dataTable.setHTML(rows, 4, Util.formatSize(version.getSize()));
	    //by vissu nov 20 - Interchanged created to comment
		versions.add(version.getName());
		
		// Special case when visibleButtons are false, widget are on trash, must disable all buttons,
		// but must enable the actual version to view ( on default is not enabled because is active one )
		if (version.isActual() && visibleButtons) {
			dataTable.selectRow(rows, true);
		} else {
			
			// Only on trash widget it'll occurs
			if (version.isActual()) {
				dataTable.selectRow(rows, true);
			}
			
			Button restoreButton = new Button(Main.i18n("button.restore"), new ClickHandler() { 
				@Override
				public void onClick(ClickEvent event) {
					List<String> versions = Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.version.versions; 
					String ver = (String) versions.get(rows);
					Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_RESTORE_HISTORY_DOCUMENT);
					Main.get().confirmPopup.setValue(ver);
					Main.get().confirmPopup.show();
				}
			});
			
			restoreButton.setVisible(visibleButtons);
			
			if ((doc.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE && !doc.isCheckedOut() && !doc.isLocked()) {
				restoreButton.setEnabled(true);
			} else {
				restoreButton.setEnabled(false);
			}
			//vissu 6

			dataTable.setWidget(rows, 6, restoreButton);
			dataTable.getCellFormatter().setHorizontalAlignment(rows, 6, HorizontalPanel.ALIGN_CENTER);
			buttonRestore.add(restoreButton);
			restoreButton.setStyleName("okm-Button");
		}
		
		Button viewButton = new Button(Main.i18n("button.view"), new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				String path = doc.getPath();
				List<String> versions = Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.version.versions; 
				String ver = (String) versions.get(rows);
				Util.downloadFile(path, "ver=" + ver);
			}
		});
		//vissu 5
		dataTable.setWidget(rows, 5, viewButton);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 5, HorizontalPanel.ALIGN_CENTER);
		buttonView.add(viewButton);
		viewButton.setStyleName("okm-Button");
	}
	
	/**
	 * Refresh the version history
	 */
	final AsyncCallback<List<GWTVersion>> callbackGetVersionHistory = new AsyncCallback<List<GWTVersion>>() {
		public void onSuccess(List<GWTVersion> result) {
			removeAllRows();
			
			// Initializes buttons lists ( to make language translations )
			buttonView = new ArrayList<Button>();
			buttonRestore = new ArrayList<Button>();
			
			// When there's more than one version document can purge it
			if (result.size() > 1) {
				purge.setEnabled(true);
			} else {
				purge.setEnabled(false);
			}
			
			for (Iterator<GWTVersion> it = result.iterator(); it.hasNext();) {
				GWTVersion version = it.next();
				addRow(version);
			}
			
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetVersionHistory();
		}

		public void onFailure(Throwable caught) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetVersionHistory();
			Main.get().showError("GetVersionHistory", caught);
		}
	};
	
	/**
	 * Refresh the version history after restoring version
	 */
	final AsyncCallback<Object> callbackRestoreVersion = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetRestoreVersion();
			Main.get().mainPanel.topPanel.toolBar.executeRefresh();
		}

		public void onFailure(Throwable caught) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetRestoreVersion();
			Main.get().showError("GetVersionHistory", caught);
		}
	};
	
	/**
	 * Refresh the version history after purge version
	 */
	final AsyncCallback<Object> callbackPurgeVersionHistory = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetPurgeVersionHistory();
			Main.get().mainPanel.topPanel.toolBar.executeRefresh();
			Main.get().workspaceUserProperties.getUserDocumentsSize();
		}

		public void onFailure(Throwable caught) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetPurgeVersionHistory();
			Main.get().showError("urgeVersionHistory", caught);
		}
	};
	
	/**
	 * Gets the version history on the server
	 */
	public void getVersionHistory() {
		if (doc != null) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.setVersionHistory();
			ServiceDefTarget endPoint = (ServiceDefTarget) documentService;
			endPoint.setServiceEntryPoint(RPCService.DocumentService);	
			documentService.getVersionHistory(doc.getPath(), callbackGetVersionHistory);
		}
	}
	
	/**
	 * Revert to a history document
	 */
	public void restoreVersion(String versionId) {
		if (doc != null) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.setRestoreVersion();
			ServiceDefTarget endPoint = (ServiceDefTarget) documentService;
			endPoint.setServiceEntryPoint(RPCService.DocumentService);	
			documentService.restoreVersion(doc.getPath(), versionId, callbackRestoreVersion);
		}
	}
	
	/**
	 * Purges a version history
	 */
	public void purgeVersionHistory() {
		if (doc != null) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.setPurgeVersionHistory();
			ServiceDefTarget endPoint = (ServiceDefTarget) documentService;
			endPoint.setServiceEntryPoint(RPCService.DocumentService);	
			documentService.purgeVersionHistory(doc.getPath(), callbackPurgeVersionHistory);
		}
	}
	
	/**
	 * Sets visibility to buttons ( true / false )
	 * 
	 * @param visible The visible value
	 */
	public void setVisibleButtons(boolean visible) {
		visibleButtons = visible;
	}
	
	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
	 */
	public void onClick(ClickEvent event) {
		Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_PURGE_VERSION_HISTORY_DOCUMENT);
		Main.get().confirmPopup.show();
	}
	
	/**
	 * fillWidth
	 */
	public void fillWidth() {
		table.fillWidth();
	}
}
