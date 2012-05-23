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

package com.openkm.frontend.client.widget.filebrowser;

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
import com.openkm.frontend.client.contants.ui.UIDesktopConstants;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.OriginPanel;

/**
 * Extends ExtendedScrollTable functionalities 
 * 
 * @author jllort
 *
 */
public class ExtendedScrollTable extends ScrollTable implements OriginPanel {
	
	// Actions on rows
	public static final int ACTION_NONE	= 0;
	public static final int ACTION_RENAMING = 1;
	
	// Special event case
	private static final int EVENT_ONMOUSEDOWN_RIGHT = -2;
	
	// Holds the data rows of the table this is a list of RowData Object
	public Map<Integer, Object> data = new HashMap<Integer, Object>();
	private FixedWidthGrid dataTable;
	private FixedWidthFlexTable headerTable;
	
	private int selectedRow = -1;
	private int mouseX = 0;
	private int mouseY = 0;
	private int dataIndexValue = 0;
	private int rowAction = ACTION_NONE;
	
	private boolean dragged = false;
	
	/**
	 * @param dataTable
	 * @param headerTable
	 * @param scrollTableImages
	 */
	public ExtendedScrollTable(FixedWidthGrid dataTable, FixedWidthFlexTable headerTable, ScrollTableImages scrollTableImages) {
		super(dataTable, headerTable, scrollTableImages);
		
		this.dataTable = dataTable;
		this.headerTable = headerTable;
		
		// Table data  SortableFixedWidthGrid.HOVERING_POLICY_CELL
	    dataTable.setSelectionPolicy(SelectionGrid.SelectionPolicy.ONE_ROW);
	    setResizePolicy(ResizePolicy.UNCONSTRAINED);
	    setScrollPolicy(ScrollPolicy.BOTH);
	    	    
	    dataTable.setColumnSorter(new ExtendedColumnSorter());
		
		// Sets some events
		DOM.sinkEvents(getDataWrapper(), Event.ONCLICK | Event.ONDBLCLICK | Event.ONMOUSEDOWN |
				Event.ONMOUSEUP | Event.ONMOUSEMOVE | Event.ONMOUSEUP);
	}
	
	/**
	 * Resets the values
	 */
	public void reset() {
		selectedRow = -1;
		mouseX = 0;
		mouseY = 0;
		dataIndexValue = 0;
		
		// Reset rowAction
		rowAction = ACTION_NONE;
		
		// Only resets rows table the header is never reset
		data = new HashMap<Integer, Object>();
	}
	
	/**
	 * Sets the selected row
	 * 
	 * @param row The row number
	 */
	public void setSelectedRow(int row) {
		Log.debug("ExtendedScrollPanel setSelectedRow:"+row);
		dataTable.selectRow(row,true);
		selectedRow = row;
	}	
		
	/**
	 *  Sets the values in specifed row/column
	 *  Expects a Comparable Object for sorting
	 *  
	 * @param rows The actual table row
	 * @param GWTFolder The folder 
	 */
	public void addRow(GWTFolder folder) {
		int row = dataTable.getRowCount();
		dataTable.insertRow(row);
		
		// Sets folder object
		data.put(new Integer(dataIndexValue),folder);

		// Subscribed is a special case, must add icon with others
		if (folder.isSubscribed()) {
			dataTable.setHTML(row, 0, Util.imageItemHTML("img/icon/subscribed.gif"));
		} else {
			dataTable.setHTML(row, 0, "&nbsp;");
		}
		
		if (folder.isHasNotes()) {
			dataTable.setHTML(row, 0, dataTable.getHTML(row,0) + Util.imageItemHTML("img/icon/note.gif"));
		}
		
		// Looks if must change icon on parent if now has no childs and properties with user security atention
		if ( (folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE) {
			if (folder.getHasChilds()) {
				dataTable.setHTML(row, 1, Util.imageItemHTML("img/menuitem_childs.gif"));
			} else {
				dataTable.setHTML(row, 1, Util.imageItemHTML("img/menuitem_empty.gif"));
			}
		} else {
			if (folder.getHasChilds()) {
				dataTable.setHTML(row, 1, Util.imageItemHTML("img/menuitem_childs_ro.gif"));
			} else {
				dataTable.setHTML(row, 1, Util.imageItemHTML("img/menuitem_empty_ro.gif"));
			}
		}
		
		dataTable.setHTML(row, 2, folder.getName());		
		dataTable.setHTML(row, 3, "&nbsp;");
		DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
		dataTable.setHTML(row, 4, dtf.format(folder.getCreated()));
		dataTable.setHTML(row, 5, folder.getAuthor());
		dataTable.setHTML(row, 6, "&nbsp;");
		dataTable.setHTML(row, 7, ""+(dataIndexValue++));
		
		// Format
		dataTable.getCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		dataTable.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_LEFT);
		dataTable.getCellFormatter().setHorizontalAlignment(row, 3, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(row, 4, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(row, 5, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(row, 6, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setVisible(row,7,false);
		
		for (int i=0; i<7; i++) {
			dataTable.getCellFormatter().addStyleName(row, i, "okm-DisableSelect");
		}
	}
	
	/**
	 * addNoteIconToSelectedRow
	 */
	public void addNoteIconToSelectedRow() {
		dataTable.setHTML(selectedRow, 0, dataTable.getHTML(selectedRow,0) + Util.imageItemHTML("img/icon/note.gif"));
	}
	
	/**
	 * deleteNoteIconToSelectedRow
	 */
	public void deleteNoteIconToSelectedRow() {
		String htmlValue = dataTable.getHTML(selectedRow,0);
		String newHtmlIcons = "";
		
		if (htmlValue.indexOf("edit.gif")>=0) {
			newHtmlIcons = Util.imageItemHTML("img/icon/edit.gif");
		} else if (htmlValue.indexOf("lock.gif")>0) {
			newHtmlIcons = Util.imageItemHTML("img/icon/lock.gif");
		} else {
			newHtmlIcons = "&nbsp;";
		}
		
		if (htmlValue.indexOf("subscribed.gif")>=0) {
			newHtmlIcons += Util.imageItemHTML("img/icon/subscribed.gif");
		}
		
		dataTable.setHTML(selectedRow, 0, newHtmlIcons);
	}
	
	/**
	 * Sets the document to the row
	 * 
	 * @param rows The table row
	 * @param doc The document
	 */
	public void addRow(GWTDocument doc) {
		int row = dataTable.getRowCount();
		dataTable.insertRow(row);
		
		// Sets document object
		data.put(new Integer(dataIndexValue),doc);
		
		if (doc.isCheckedOut()) {
			dataTable.setHTML(row, 0, Util.imageItemHTML("img/icon/edit.gif"));
		} else if (doc.isLocked()) {
			dataTable.setHTML(row, 0, Util.imageItemHTML("img/icon/lock.gif"));
		} else {
			dataTable.setHTML(row, 0, "&nbsp;");
		}
		
		// Subscribed is a special case, must add icon with others
		if (doc.isSubscribed()) {
			dataTable.setHTML(row, 0, dataTable.getHTML(row,0) + Util.imageItemHTML("img/icon/subscribed.gif"));
		}
		// Document has notes
		if (doc.isHasNotes()) {
			dataTable.setHTML(row, 0, dataTable.getHTML(row,0) + Util.imageItemHTML("img/icon/note.gif"));
		}
		// Document encrypted
		if (doc.getCipherName()!=null && !doc.getCipherName().equals("")) {
			dataTable.setHTML(row, 0, dataTable.getHTML(row,0) + Util.imageItemHTML("img/icon/actions/crypt.png"));
		}
		
		dataTable.setHTML(row, 1, Util.mimeImageHTML(doc.getMimeType()));
		dataTable.setHTML(row, 2, doc.getName());
		dataTable.setHTML(row, 3, Util.formatSize(doc.getActualVersion().getSize()));
		DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
		dataTable.setHTML(row, 4, dtf.format(doc.getLastModified()));
		dataTable.setHTML(row, 5, doc.getActualVersion().getAuthor());
		Hyperlink hLink = new Hyperlink();
		hLink.setText(doc.getActualVersion().getName());
		hLink.setTitle(doc.getActualVersion().getComment());
		dataTable.setWidget(row, 6, hLink);
		dataTable.setHTML(row, 7, ""+(dataIndexValue++));
		
		// Format
		dataTable.getCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		dataTable.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_LEFT);
		dataTable.getCellFormatter().setHorizontalAlignment(row, 3, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(row, 4, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(row, 5, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(row, 6, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setVisible(row,7,false);
		
		for (int i=0; i<7; i++) {
			dataTable.getCellFormatter().addStyleName(row, i, "okm-DisableSelect");
		}
	}
	
	/**
	 * Sets the mail to the row
	 * 
	 * @param rows The table row
	 * @param doc The document
	 */
	public void addRow(GWTMail mail) {
		int row = dataTable.getRowCount();
		dataTable.insertRow(row);
		
		// Sets document object
		data.put(new Integer(dataIndexValue),mail);
		
		// Mail is never checkout or subscribed ( because can not be changed )
		dataTable.setHTML(row, 0, "&nbsp;");
		
		if (mail.getAttachments().size()>0) {
			dataTable.setHTML(row, 1, Util.imageItemHTML("img/email_attach.gif"));
		} else {
			dataTable.setHTML(row, 1, Util.imageItemHTML("img/email.gif"));
		}

		dataTable.setHTML(row, 2, mail.getSubject());
		dataTable.setHTML(row, 3, Util.formatSize(mail.getSize()));
		DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
		dataTable.setHTML(row, 4, dtf.format(mail.getReceivedDate()));
		dataTable.setHTML(row, 5, mail.getFrom());
		dataTable.setHTML(row, 6, "&nbsp;");
		dataTable.setHTML(row, 7, ""+(dataIndexValue++));
		
		// Format
		dataTable.getCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		dataTable.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(row, 2, HasHorizontalAlignment.ALIGN_LEFT);
		dataTable.getCellFormatter().setHorizontalAlignment(row, 3, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(row, 4, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(row, 5, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(row, 6, HasHorizontalAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setVisible(row,7,false);
		
		for (int i=0; i<7; i++) {
			dataTable.getCellFormatter().addStyleName(row, i, "okm-DisableSelect");
		}
	}
	
	/**
	 * Deselects the selected row
	 */
	public void deselecSelectedRow() {
		if(!dataTable.getSelectedRows().isEmpty()) {
			int selectedRow = ((Integer) dataTable.getSelectedRows().iterator().next()).intValue();
			dataTable.deselectRow(selectedRow);
		}
		selectedRow = -1;
	}
	
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.EventListener#onBrowserEvent(com.google.gwt.user.client.Event)
	 */
	public void onBrowserEvent(Event event) {
		boolean headerFired = false; // Controls when event is fired by header
		
		// Case targe event is header must disable drag & drop
		if (headerTable.getEventTargetCell(event)!=null) {
			dragged=false; 
			headerFired = true;
		}
		boolean enableOpen = true;
		
		// If some action is on course must do speacil actions, this must be made before selected row
		// is changed
		switch(rowAction) {
			case ACTION_RENAMING :
				enableOpen = false;
				break;
		}
		
		// When de button mouse is released
		mouseX = DOM.eventGetClientX(event);
		mouseY = DOM.eventGetClientY(event);
		
		int type = DOM.eventGetType(event);
		
		if (type == Event.ONMOUSEDOWN && DOM.eventGetButton(event)==Event.BUTTON_RIGHT) {
			type = EVENT_ONMOUSEDOWN_RIGHT; // Special case, that must be so much similar to click event
		}
		
		switch (type) {
			case Event.ONCLICK:
			case EVENT_ONMOUSEDOWN_RIGHT:
				
				// Only for right mouuse button
				if (!headerFired && type==EVENT_ONMOUSEDOWN_RIGHT) {
					Main.get().mainPanel.desktop.browser.fileBrowser.showMenu();
					DOM.eventPreventDefault(event); // Prevent to fire event to browser
				}
				
				if (dataTable.getEventTargetCell(event)!=null) {
					// Mark panel as selected and disables tree navigator panel
					if (getSelectedRow()>=0 && !Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
						Main.get().mainPanel.desktop.browser.fileBrowser.setSelectedPanel(true);
					} 
					
					// And row must be other than the selected one
					if (getSelectedRow()>= 0 && getSelectedRow() != selectedRow) {
						// If some action is on course must do speacil actions, this must be made before selected row
						// is changed
						switch (rowAction) {
							case ACTION_RENAMING :
								if (getSelectedRow() != selectedRow) {
									Main.get().mainPanel.desktop.browser.fileBrowser.hideRename(selectedRow);
								}
								break;
						}
						
						selectedRow = getSelectedRow();
						
						if (isFolderSelected()) {				
							Main.get().mainPanel.desktop.browser.tabMultiple.enableTabFolder();
							GWTFolder folder = getFolder();
							Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.setProperties(folder);
							Main.get().mainPanel.topPanel.toolBar.checkToolButtonPermissions(folder,
									Main.get().activeFolderTree.getFolder(),
																							 FILE_BROWSER);
						} else if (isMailSelected()) {				
							Main.get().mainPanel.desktop.browser.tabMultiple.enableTabMail();
							GWTMail mail = getMail();
							Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.setProperties(mail);
							Main.get().mainPanel.topPanel.toolBar.checkToolButtonPermissions(mail,
									 Main.get().activeFolderTree.getFolder());
						} else {
							Main.get().mainPanel.desktop.browser.tabMultiple.enableTabDocument();
							GWTDocument doc = getDocument();
							Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.setProperties(doc);
							Main.get().mainPanel.topPanel.toolBar.checkToolButtonPermissions(doc,Main.get().activeFolderTree.getFolder());
						}
					} 
				}
				break;
		
			case Event.ONDBLCLICK:		
				// On double click not sends event to onCellClicked across super.onBrowserEvent();
				// Disables the event propagation the sequence is:
				// Two time entry onCellClicked before entry on onBrowserEvent and disables the
				// Tree onCellClicked that produces inconsistence error refreshing
				DOM.eventCancelBubble(event, true);
				
				if (!headerFired && getSelectedRow() >= 0) {
					if (isFolderSelected()) {
						Main.get().mainPanel.desktop.browser.tabMultiple.enableTabFolder();
						if (getSelectedRow() != selectedRow) { // Must not refresh properties on double click if row is yet selected
							Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.setProperties(getFolder());
						}
						Main.get().activeFolderTree.setActiveNode(getFolder().getPath(),true);
					} else if (isMailSelected()) {				
						Main.get().mainPanel.desktop.browser.tabMultiple.enableTabMail();
						GWTMail mail = getMail();
						if (getSelectedRow() != selectedRow) { // Must not refresh properties on double click if row is yet selected
							Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.setProperties(mail);
						}
						Main.get().mainPanel.topPanel.toolBar.checkToolButtonPermissions(mail,
								 														 Main.get().activeFolderTree.getFolder());
					} else {
						Main.get().mainPanel.desktop.browser.tabMultiple.enableTabDocument();
						GWTDocument doc = getDocument();
						Main.get().mainPanel.topPanel.toolBar.checkToolButtonPermissions(doc,Main.get().activeFolderTree.getFolder());
						// We come here before executing click ( click is always executed ) 
						if (enableOpen) {
							if (Main.get().workspaceUserProperties.getWorkspace().getAvailableOption().isDownloadOption()) {
								downloadDocument(false);
							}
						} 
					}
				}
				break;
			
			case Event.ONMOUSEMOVE:
				if (isDragged()) {
		            
		            // Implements drag & drop
		            int noAction = FileBrowser.ACTION_NONE;
		            
		            // On trash drag is disabled
					if (isSelectedRow() && Main.get().mainPanel.desktop.browser.fileBrowser.fileBrowserAction==noAction &&
						Main.get().mainPanel.desktop.navigator.getStackIndex()!=UIDesktopConstants.NAVIGATOR_CATEGORIES &&
						Main.get().mainPanel.desktop.navigator.getStackIndex()!=UIDesktopConstants.NAVIGATOR_THESAURUS && 
						Main.get().mainPanel.desktop.navigator.getStackIndex()!=UIDesktopConstants.NAVIGATOR_TRASH){					
						Main.get().dragable.show(dataTable.getHTML(getSelectedRow(),1)+dataTable.getHTML(getSelectedRow(),2), OriginPanel.FILE_BROWSER);
					}
					unsetDraged(); 
				}
				break;
			
			case Event.ONMOUSEOUT:
				unsetDraged(); 
				break;
				
			case Event.ONMOUSEDOWN:
				dragged = true;
				break;
				
			case Event.ONMOUSEUP:
				unsetDraged();
				break;
		}
		
		super.onBrowserEvent(event);
	}
	
	/**
	 * Gets the path of the selected document or folder
	 * 
	 * @return The id
	 */
	public String getSelectedId() {
		String id = "";
		
		if (getSelectedRow() >= 0) {
			if (isFolderSelected()) {
				id = getFolder().getPath();
			} else if (isMailSelected()) {
				id = getMail().getPath();
			} else if (isDocumentSelected()) {
				id = getDocument().getPath();
			} 
		}
		
		return id;
	}
	
	/**
	 * Finds row by id document or folder
	 * 
	 * @param id The id
	 * @return The selected row
	 */
	public int findSelectedRowById(String id) {
		int selected = 0;
		int rowIndex = 0;
		boolean found = false;
		
		// Looking for id on directories
		while (!found && rowIndex < data.size()) {
			if (data.get(rowIndex) instanceof GWTFolder) {
				if (((GWTFolder) data.get(rowIndex)).getPath().equals(id)) {
					selected = rowIndex;
					found = true;
				}
			} else if (data.get(rowIndex) instanceof GWTMail) {
				if (((GWTMail)data.get(rowIndex)).getPath().equals(id)) {
					selected = rowIndex;
					found = true;
				}
			} else if (data.get(rowIndex) instanceof GWTDocument) {
				if (((GWTDocument)data.get(rowIndex)).getPath().equals(id)) {
					selected = rowIndex;
					found = true;
				}
			}
			
			rowIndex++;
		}
		
		if (found) {
				found = false;
				rowIndex = 0;
				int tmpSelected = selected;
				selected = 0;
				while(!found && rowIndex < dataTable.getRowCount()) {
					if(dataTable.getText(rowIndex,7).equals(String.valueOf(tmpSelected))) {
						found=true;
						selected = rowIndex;
					}
					
					rowIndex++;
				}
		} else {
			selected = -1;
		}
		
		return selected;
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
	 * Reset selected rows
	 */
	public void resetSelectedRows() {
		if (!dataTable.getSelectedRows().isEmpty()) {
			dataTable.getSelectedRows().clear();
		}
	}
	
	/**
	 * Restores the selected row value
	 * 
	 * @param selectedRow The selected row
	 */
	public void restoreSelectedRow(int selectedRow){
		Log.debug("ExtendedScrollTable restoreSelectedRow:"+selectedRow);
		this.selectedRow = selectedRow;
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
	 * Gets a actual document object row
	 * 
	 * @return
	 */
	public GWTDocument getDocument() {
		if (isDocumentSelected()) {
			return (GWTDocument) data.get(Integer.parseInt(dataTable.getText(getSelectedRow(),7)));
		} else {
			return null;
		}
	}
	
	/**
	 * Sets the document object to actual row
	 * 
	 * @param doc The document
	 */
	public void setDocument(GWTDocument doc) {
		if (isDocumentSelected()) {
			int row = getSelectedRow();
			data.put(new Integer(dataTable.getText(getSelectedRow(),7)),doc);
			
			if (doc.isCheckedOut()) {
				dataTable.setHTML(row, 0, Util.imageItemHTML("img/icon/edit.gif"));
			} else if (doc.isLocked()) {
				dataTable.setHTML(row, 0, Util.imageItemHTML("img/icon/lock.gif"));
			} else {
				dataTable.setHTML(row, 0, "");
			}
			
			// Subscribed is a special case, must add icon with others
			if (doc.isSubscribed()) {
				dataTable.setHTML(row, 0, dataTable.getHTML(row,0) + Util.imageItemHTML("img/icon/subscribed.gif"));
			}
			// Document has notes
			if (doc.isHasNotes()) {
				dataTable.setHTML(row, 0, dataTable.getHTML(row,0) + Util.imageItemHTML("img/icon/note.gif"));
			}
			// Document encrypted
			if (doc.getCipherName()!=null && !doc.getCipherName().equals("")) {
				dataTable.setHTML(row, 0, dataTable.getHTML(row,0) + Util.imageItemHTML("img/icon/actions/crypt.png"));
			}
			
			dataTable.setHTML(row, 1, Util.mimeImageHTML(doc.getMimeType()));
			dataTable.setHTML(row, 2, doc.getName());
			dataTable.setHTML(row, 3, Util.formatSize(doc.getActualVersion().getSize()));
			DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
			dataTable.setHTML(row, 4, dtf.format(doc.getLastModified()));
			dataTable.setHTML(row, 5, doc.getActualVersion().getAuthor());
			dataTable.setHTML(row, 6, doc.getActualVersion().getName());
		}
	}
	
	/**
	 * Deletes document row
	 */
	public void delete() {
		if (isSelectedRow()) { 
			Log.debug("ExtendedScrollPanel delete:");
			data.remove(Integer.parseInt(dataTable.getText(getSelectedRow(),7)));
			dataTable.removeRow(getSelectedRow());
			selectPrevRow();
		} 
	}
	
	/**
	 * After deletes document or folder selects a row 
	 */
	public void selectPrevRow() {
		Log.debug("ExtendedScrollTable selectPrevRow");
		Log.debug("ExtendedScrollTable selectPrevRow -> dataTable.getRowCount():"+ dataTable.getRowCount());
		Log.debug("ExtendedScrollTable selectPrevRow -> selectedRow:"+selectedRow);
		if (dataTable.getRowCount()>0) {
			if (dataTable.getRowCount()>selectedRow) {
				Log.debug("selectPrevRow:"+selectedRow);
				dataTable.selectRow(selectedRow,true);
				
			} else {
				Log.debug("selectPrevRow-1:"+(selectedRow-1));
				dataTable.selectRow(selectedRow-1,true);
			}
		}
	}
	
	/**
	 * Gets a actual Folder object row
	 * 
	 * @return
	 */
	public GWTFolder getFolder() {
		// Row selected must be on table folder
		if (isFolderSelected()) {
			return (GWTFolder) data.get(Integer.parseInt(dataTable.getText(getSelectedRow(),7)));
		} else {
			return null;
		}
	}
	
	/**
	 * Gets a actual Mail object row
	 * 
	 * @return
	 */
	public GWTMail getMail() {
		// Row selected must be on table mail
		if (isMailSelected()) {
			return (GWTMail) data.get(Integer.parseInt(dataTable.getText(getSelectedRow(),7)));
		} else {
			return null;
		}
	}
	
	/**
	 * Sets the mail to the row
	 * 
	 * @param rows The table row
	 * @param doc The document
	 */
	public void setMail(GWTMail mail) {
		if (isMailSelected()) {
			int row = getSelectedRow();
			data.put(new Integer(dataTable.getText(getSelectedRow(),7)),mail);
			
			if (mail.getAttachments().size()>0) {
				dataTable.setHTML(row, 1, Util.imageItemHTML("img/email_attach.gif"));
			} else {
				dataTable.setHTML(row, 1, Util.imageItemHTML("img/email.gif"));
			}
			
			// Mail is never checkout or subscribed ( because can not be changed )
			dataTable.setHTML(row, 2, mail.getSubject());
			dataTable.setHTML(row, 3, Util.formatSize(mail.getSize()));
			DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
			dataTable.setHTML(row, 4, dtf.format(mail.getReceivedDate()));
			dataTable.setHTML(row, 5, mail.getFrom());
		}
	}
	
	/**
	 * Sets the folder to the selected row
	 * 
	 * @param folder The folder object
	 */
	public void setFolder(GWTFolder folder) {
		// Row selected must be on table folder
		if (isFolderSelected()) {
			int row = getSelectedRow();
			data.put(new Integer(dataTable.getText(row,7)),folder);
			
			// Subscribed is a special case, must add icon with others
			if (folder.isSubscribed()) {
				dataTable.setHTML(row, 0, Util.imageItemHTML("img/icon/subscribed.gif"));
			} else {
				dataTable.setHTML(row, 0, "");
			}
			
			// Looks if must change icon on parent if now has no childs and properties with user security atention
			if ( (folder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE) {
				if (folder.getHasChilds()) {
					dataTable.setHTML(row, 1, Util.imageItemHTML("img/menuitem_childs.gif"));
				} else {
					dataTable.setHTML(row, 1, Util.imageItemHTML("img/menuitem_empty.gif"));
				}
			} else {
				if (folder.getHasChilds()) {
					dataTable.setHTML(row, 1, Util.imageItemHTML("img/menuitem_childs_ro.gif"));
				} else {
					dataTable.setHTML(row, 1, Util.imageItemHTML("img/menuitem_empty_ro.gif"));
				}
			}
			
			dataTable.setHTML(row, 2, folder.getName());		
			dataTable.setHTML(row, 3, "");
			DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
			dataTable.setHTML(row, 4, dtf.format(folder.getCreated()));
			dataTable.setHTML(row, 5, folder.getAuthor());
			dataTable.setHTML(row, 6, "");
		} 
	}
	
	/**
	 * Return true or false if actual selected row is document
	 * 
	 * @return True or False if actual row is document type
	 */
	public boolean isDocumentSelected() {
		if (!dataTable.getSelectedRows().isEmpty()) {
			if (data.get(Integer.parseInt(dataTable.getText(getSelectedRow(),7))) instanceof GWTDocument) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Return true or false if actual selected row is folder
	 * 
	 * @return True or False if actual row is folder type
	 */
	public boolean isFolderSelected() {
		if (!dataTable.getSelectedRows().isEmpty()) {
			Log.debug("ExtendedScrollTable isFolderSelected: key " + dataTable.getText(getSelectedRow(),7));
			Log.debug("ExtendedScrollTable isFolderSelected: data size" + data.size());
			
			if (data.get(Integer.parseInt(dataTable.getText(getSelectedRow(),7))) instanceof GWTFolder) {
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
			Log.debug("ExtendedScrollTable isFolderSelected: key " + dataTable.getText(getSelectedRow(),7));
			Log.debug("ExtendedScrollTable isFolderSelected: data size" + data.size());
			
			if (data.get(Integer.parseInt(dataTable.getText(getSelectedRow(),7))) instanceof GWTMail) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Return true or false if it's a selected row
	 * 
	 * @return True or false selected row
	 */
	public boolean isSelectedRow(){
		Log.debug("isSelectedRow:");
		if (!dataTable.getSelectedRows().isEmpty() ){
			Log.debug("isSelectedRow: true");
			return true;
		} else {
			Log.debug("isSelectedRow: false");
			return false;
		}
	}
	
	/**
	 * Download document
	 */
	public void downloadDocument(boolean checkout) {
		Log.debug("downloadDocument()");
		if (isDocumentSelected()) {
			Log.debug("jump to download");
			Util.downloadFile(getDocument().getPath(), (checkout?"checkout":""));
		}
		Log.debug("downloadDocument: void");
	}

	/**
	 * Download document as PDF
	 */
	public void downloadDocumentPdf() {
		Log.debug("downloadDocumentPdf()");
		if (isDocumentSelected()) {
			Log.debug("jump to download");
			Util.downloadFilePdf(getDocument().getUuid());
		}
		Log.debug("downloadDocumentPdf: void");
	}
	
	/**
	 * Gets the checkout flag
	 * 
	 * @return Checkout state
	 */
	public boolean isCheckout() {
		// Row selected must be on table documents only this can be checkout
		if (isDocumentSelected()) {
			return getDocument().isCheckedOut();
		} else {
			return false;
		}
	}
	
	/**
	 * Gets the locked flag
	 * 
	 * @return locked state
	 */
	public boolean isLocked() {
		// Row selected must be on table documents only this can be checkout
		if (isDocumentSelected()) {
			return getDocument().isLocked();
		} else {
			return false;
		}
	}
	
	/**
	 * Sets the actual action on rows
	 * 
	 * @param action The action
	 */
	public void setAction(int action) {
		rowAction = action;
	}
	
	/**
	 * Resets the row action
	 */
	public void resetAction() {
		rowAction = ACTION_NONE;
	}
    
    /**
	 * isDragged Returns true or false if is dragged
	 * 
	 * @return Return dragged value
	 */
	public boolean isDragged() {
		return dragged;
	}
	
	/**
	 * unsetDraged
	 * 
	 * Sets dragged flag to false;
	 */
	public void unsetDraged() {
		this.dragged = false;
	}
	
	/**
	 * hasRows
	 * 
	 * @return has rows
	 */
	public boolean hasRows() {
		return dataTable.getRowCount()>0;
	}

}
