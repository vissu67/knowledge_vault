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

import java.util.HashMap;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.gen2.table.client.ScrollTable;
import com.google.gwt.gen2.table.client.SelectionGrid;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Hyperlink;

import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.bean.GWTPermission;
import com.openkm.frontend.client.bean.GWTQueryResult;
import com.openkm.frontend.client.util.Util;

/**
 * Extends ScrollTable functionalities 
 * 
 * @author jllort
 *
 */
public class ExtendedScrollTable extends ScrollTable {
	
	// Holds the data rows of the table this is a list of RowData Object
	public Map<Integer, GWTQueryResult> data = new HashMap<Integer, GWTQueryResult>();
	private int mouseX = 0;
	private int mouseY = 0;
	private int dataIndexValue = 0;
	private boolean panelSelected = false; // Indicates if panel is selected
	private FixedWidthGrid dataTable;
	private FixedWidthFlexTable headerTable;
	
	/**
	 * ExtendedScrollTable
	 */
	public ExtendedScrollTable(FixedWidthGrid dataTable, FixedWidthFlexTable headerTable, ScrollTableImages scrollTableImages) {
		super(dataTable, headerTable, scrollTableImages);
		this.dataTable = dataTable;
		this.headerTable = headerTable;
		
		dataTable.setSelectionPolicy(SelectionGrid.SelectionPolicy.ONE_ROW);
		setResizePolicy(ResizePolicy.UNCONSTRAINED);
		setScrollPolicy(ScrollPolicy.BOTH);
		
		dataTable.setColumnSorter(new ExtendedColumnSorter());
		
		// Sets some events
		DOM.sinkEvents(getDataWrapper(),Event.ONDBLCLICK | Event.ONMOUSEDOWN );
	}
	
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.EventListener#onBrowserEvent(com.google.gwt.user.client.Event)
	 */
	public void onBrowserEvent(Event event) {
		boolean headerFired = false; // Controls when event is fired by header
		
		// Case targe event is header must disable drag & drop
		if (headerTable.getEventTargetCell(event)!=null) {
			headerFired = true;
		}
		
		// Selects the panel
		setSelectedPanel(true);
		
		// When de button mouse is released
		mouseX = DOM.eventGetClientX(event);
		mouseY = DOM.eventGetClientY(event);
		
		// On double click not sends event to onCellClicked across super.onBrowserEvent();
		if (DOM.eventGetType(event) == Event.ONDBLCLICK) {
			// Disables the event propagation the sequence is:
			// Two time entry onCellClicked before entry on onBrowserEvent and disbles the
			// Tree onCellClicked that produces inconsistence error refreshing
			DOM.eventCancelBubble(event, true);
			if ((isDocumentSelected() || isAttachmentSelected()) && Main.get().workspaceUserProperties.getWorkspace().getAvailableOption().isDownloadOption()) {
				Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.downloadDocument();
			}

		} else if (DOM.eventGetType(event) == Event.ONMOUSEDOWN) {
			switch (DOM.eventGetButton(event)) {
			case Event.BUTTON_RIGHT:
				if (!headerFired) {
					if (isDocumentSelected() || isAttachmentSelected()) {
						Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.menuPopup.menu.checkMenuOptionPermissions(getDocument());
					} else if (isFolderSelected()) {
						Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.menuPopup.menu.checkMenuOptionPermissions(getFolder());
					} else if (isMailSelected()) {
						Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.menuPopup.menu.checkMenuOptionPermissions(getMail());
					}
					Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.menuPopup.menu.evaluateMenuOptions();
					Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.showMenu();
					DOM.eventPreventDefault(event); // Prevent to fire event to browser
				}
				break;
			default:
				break;
			}
		}

		super.onBrowserEvent(event);
	}
	
	/**
	 * Sets the selected panel value
	 * 
	 * @param selected The selected panel value
	 */
	public void setSelectedPanel(boolean selected) {
		if (selected){
			Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.addStyleName("okm-PanelSelected");
			Main.get().mainPanel.search.historySearch.searchSaved.setSelectedPanel(false);
			Main.get().mainPanel.search.historySearch.userNews.setSelectedPanel(false);
		} else {
			Main.get().mainPanel.search.searchBrowser.searchResult.searchCompactResult.removeStyleName("okm-PanelSelected");
		}
		panelSelected = selected;
	}
	
	/**
	 * Is panel selected
	 * 
	 * @return The panel selected value
	 */
	public boolean isPanelSelected() {
		return panelSelected;
	}
	
	/**
	 * Gets the X position on mouse click
	 * 
	 * @return The x position on mouse click
	 */
	public int getMouseX() {
		return mouseX;
	}
	
	/**
	 * Gets the Y position on mouse click
	 * 
	 * @return The y position on mouse click
	 */
	public int getMouseY() {
		return mouseY;
	}	
	
	/**
	 * Gets the selected row
	 * 
	 * @return The selected row
	 */
	public int getSelectedRow() {
		int selectedRow = -1;
		
		if (!dataTable.getSelectedRows().isEmpty()) {
			selectedRow = ((Integer) dataTable.getSelectedRows().iterator().next()).intValue();
		} 
		
		Log.debug("ExtendedScrollPanel selectedRow:"+selectedRow);
		return selectedRow;
	}
	
	/**
	 * Resets the values
	 */
	public void reset() {
		mouseX = 0;
		mouseY = 0;
		dataIndexValue = 0;
		
		// Only resets rows table the header is never reset
		data = new HashMap<Integer, GWTQueryResult>();
	}
	
	/**
	 * Adds a document to the panel
	 * 
	 * @param doc The doc to add
	 */
	public void addRow(GWTQueryResult gwtQueryResult) {
		if (gwtQueryResult.getDocument()!=null || gwtQueryResult.getAttachment()!=null) {
			addDocumentRow(gwtQueryResult, new Score(gwtQueryResult.getScore()));
		} else if (gwtQueryResult.getFolder()!=null) {
			addFolderRow(gwtQueryResult, new Score(gwtQueryResult.getScore()));
		} else if (gwtQueryResult.getMail()!=null) {
			addMailRow(gwtQueryResult, new Score(gwtQueryResult.getScore()));
		}
	}
	
	/**
	 * Adding document row
	 * 
	 * @param gwtQueryResult Query result
	 * @param score Document score
	 */
	private void addDocumentRow(GWTQueryResult gwtQueryResult, Score score) {
		int rows = dataTable.getRowCount();
		dataTable.insertRow(rows);
		
		GWTDocument doc = new GWTDocument();
		if (gwtQueryResult.getDocument()!=null) {
			doc = gwtQueryResult.getDocument();
		} else if (gwtQueryResult.getAttachment()!=null) {
			doc = gwtQueryResult.getAttachment();
		}
		
		// Sets folder object
		data.put(new Integer(dataIndexValue), gwtQueryResult);
		
		dataTable.setHTML(rows, 0, score.getHTML());
		
		if(doc.isAttachment())  {
			dataTable.setHTML(rows, 1, Util.imageItemHTML("img/email_attach.gif"));
		} else {
			dataTable.setHTML(rows, 1, "&nbsp;");
		}
		dataTable.setHTML(rows, 1, dataTable.getHTML(rows,1) + Util.mimeImageHTML(doc.getMimeType()));
		Hyperlink hLink = new Hyperlink();
		hLink.setHTML(doc.getName());
		hLink.setStyleName("okm-Hyperlink");
		// On attachemt case must remove last folder path, because it's internal usage not for visualization
		if (doc.isAttachment()) {
			hLink.setTitle(doc.getParent().substring(0, doc.getParent().lastIndexOf("/")));
		} else {
			hLink.setTitle(doc.getParent());
		}
		
		dataTable.setWidget(rows, 2, hLink);
		dataTable.setHTML(rows, 3, Util.formatSize(doc.getActualVersion().getSize()));
		DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
		dataTable.setHTML(rows, 4, dtf.format(doc.getLastModified()));
		dataTable.setHTML(rows, 5, doc.getActualVersion().getAuthor());
		dataTable.setHTML(rows, 6, doc.getActualVersion().getName());
		dataTable.setHTML(rows, 7, ""+(dataIndexValue++));
		
		// Format
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 0, HasHorizontalAlignment.ALIGN_LEFT);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 1, HasHorizontalAlignment.ALIGN_RIGHT);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 2, HasHorizontalAlignment.ALIGN_LEFT);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 3, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 4, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 5, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 6, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setVisible(rows,7,false);
		
		for (int i=0; i<7; i++) {
			dataTable.getCellFormatter().addStyleName(rows, i, "okm-DisableSelect");
		}
	}
	
	/**
	 * Adding folder
	 * 
	 * @param gwtQueryResult Query result
	 * @param score The folder score
	 */
	private void addFolderRow(GWTQueryResult gwtQueryResult, Score score) {
		int rows = dataTable.getRowCount();
		dataTable.insertRow(rows);
		
		GWTFolder folder = gwtQueryResult.getFolder();
		
		// Sets folder object
		data.put(new Integer(dataIndexValue), gwtQueryResult);
		
		dataTable.setHTML(rows, 0, score.getHTML());
		
		// Looks if must change icon on parent if now has no childs and properties with user security atention
		if ( (folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE) {
			if (folder.getHasChilds()) {
				dataTable.setHTML(rows, 1, Util.imageItemHTML("img/menuitem_childs.gif"));
			} else {
				dataTable.setHTML(rows, 1, Util.imageItemHTML("img/menuitem_empty.gif"));
			}
		} else {
			if (folder.getHasChilds()) {
				dataTable.setHTML(rows, 1, Util.imageItemHTML("img/menuitem_childs_ro.gif"));
			} else {
				dataTable.setHTML(rows, 1, Util.imageItemHTML("img/menuitem_empty_ro.gif"));
			}
		}
		Hyperlink hLink = new Hyperlink();
		hLink.setHTML(folder.getName());
		hLink.setTitle(folder.getParentPath());
		hLink.setStyleName("okm-Hyperlink");
		dataTable.setWidget(rows, 2, hLink);
		dataTable.setHTML(rows, 3, "&nbsp;");
		DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
		dataTable.setHTML(rows, 4, dtf.format(folder.getCreated()));
		dataTable.setHTML(rows, 5, folder.getAuthor());
		dataTable.setHTML(rows, 6, "&nbsp;");
		dataTable.setHTML(rows, 7, ""+(dataIndexValue++));
		
		// Format
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 0, HasHorizontalAlignment.ALIGN_LEFT);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 1, HasHorizontalAlignment.ALIGN_RIGHT);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 2, HasHorizontalAlignment.ALIGN_LEFT);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 3, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 4, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 5, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 6, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setVisible(rows,7,false);
		
		for (int i=0; i<7; i++) {
			dataTable.getCellFormatter().addStyleName(rows, i, "okm-DisableSelect");
		}
	}
	
	/**
	 * Adding mail
	 * 
	 * @param gwtQueryResult Query result
	 * @param score The mail score
	 */
	private void addMailRow(GWTQueryResult gwtQueryResult, Score score) {
		int rows = dataTable.getRowCount();
		dataTable.insertRow(rows);
		
		GWTMail mail = gwtQueryResult.getMail();
		
		// Sets folder object
		data.put(new Integer(dataIndexValue), gwtQueryResult);
		
		dataTable.setHTML(rows, 0, score.getHTML());
		
		if (mail.getAttachments().size()>0) {
			dataTable.setHTML(rows, 1, Util.imageItemHTML("img/email_attach.gif"));
		} else {
			dataTable.setHTML(rows, 1, Util.imageItemHTML("img/email.gif"));
		}
		
		Hyperlink hLink = new Hyperlink();
		hLink.setHTML(mail.getSubject());
		hLink.setTitle(mail.getParent());
		hLink.setStyleName("okm-Hyperlink");
		dataTable.setWidget(rows, 2, hLink);
		dataTable.setHTML(rows, 3, Util.formatSize(mail.getSize()));
		DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
		dataTable.setHTML(rows, 4, dtf.format(mail.getReceivedDate()));
		dataTable.setHTML(rows, 5, mail.getFrom());
		dataTable.setHTML(rows, 6, "&nbsp;");
		dataTable.setHTML(rows, 7, ""+(dataIndexValue++));
		
		// Format
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 0, HasHorizontalAlignment.ALIGN_LEFT);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 1, HasHorizontalAlignment.ALIGN_RIGHT);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 2, HasHorizontalAlignment.ALIGN_LEFT);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 3, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 4, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 5, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows, 6, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setVisible(rows,7,false);
		
		for (int i=0; i<7; i++) {
			dataTable.getCellFormatter().addStyleName(rows, i, "okm-DisableSelect");
		}
	}
	
	/**
	 * Sets the selected row
	 * 
	 * @param row The row number
	 */
	public void setSelectedRow(int row) {
		Log.debug("ExtendedScrollPanel setSelectedRow:"+row);
		dataTable.selectRow(row,true);
	}
	
	/**
	 * Gets a actual document object row
	 * 
	 * @return
	 */
	public GWTDocument getDocument() {
		if (isDocumentSelected()) {
			return ((GWTQueryResult) data.get(Integer.parseInt(dataTable.getText(getSelectedRow(),7)))).getDocument();
		} else {
			return null;
		}
	}
	
	/**
	 * Gets a actual attachment object row
	 * 
	 * @return
	 */
	public GWTDocument getAttachment() {
		if (isAttachmentSelected()) {
			return ((GWTQueryResult) data.get(Integer.parseInt(dataTable.getText(getSelectedRow(),7)))).getAttachment();
		} else {
			return null;
		}
	}
	
	/**
	 * Gets a actual document object row
	 * 
	 * @return
	 */
	public GWTFolder getFolder() {
		if (isFolderSelected()) {
			return ((GWTQueryResult) data.get(Integer.parseInt(dataTable.getText(getSelectedRow(),7)))).getFolder();
		} else {
			return null;
		}
	}
	
	/**
	 * Gets a actual mail object row
	 * 
	 * @return
	 */
	public GWTMail getMail() {
		if (isMailSelected()) {
			return ((GWTQueryResult) data.get(Integer.parseInt(dataTable.getText(getSelectedRow(),7)))).getMail();
		} else {
			return null;
		}
	}
	
	/**
	 * Return true or false if actual selected row is document
	 * 
	 * @return True or False if actual row is document type
	 */
	public boolean isDocumentSelected() {
		if (!dataTable.getSelectedRows().isEmpty()) {
			if (((GWTQueryResult) data.get(Integer.parseInt(dataTable.getText(getSelectedRow(),7)))).getDocument()!=null ) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Return true or false if actual selected row is attachment
	 * 
	 * @return True or False if actual row is attachment type
	 */
	public boolean isAttachmentSelected() {
		if (!dataTable.getSelectedRows().isEmpty()) {
			if (((GWTQueryResult) data.get(Integer.parseInt(dataTable.getText(getSelectedRow(),7)))).getAttachment()!=null ) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Return true or false if actual selected row is mail
	 * 
	 * @return True or False if actual row is mail type
	 */
	public boolean isFolderSelected() {
		if (!dataTable.getSelectedRows().isEmpty()) {
			if (((GWTQueryResult) data.get(Integer.parseInt(dataTable.getText(getSelectedRow(),7)))).getFolder()!=null ) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Return true or false if actual selected row is mail
	 * 
	 * @return True or False if actual row is mail type
	 */
	public boolean isMailSelected() {
		if (!dataTable.getSelectedRows().isEmpty()) {
			if (((GWTQueryResult) data.get(Integer.parseInt(dataTable.getText(getSelectedRow(),7)))).getMail()!=null ) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}