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

package com.openkm.frontend.client.widget.dashboard.keymap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTQueryResult;
import com.openkm.frontend.client.util.CommonUI;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.dashboard.ImageHover;
import com.openkm.frontend.client.widget.dashboard.Score;
import com.openkm.frontend.client.widget.dashboard.Status;

/**
 * KeyMapTable
 * 
 * @author jllort
 *
 */
public class KeyMapTable extends Composite {
	
	public static final int VISIBLE_SMALL 		= 0;
	public static final int VISIBLE_MEDIUM 		= 1;
	public static final int VISIBLE_BIG 		= 2;
	
	private FlexTable table;
	private List<FlexTable> tableDocumentList;
	private List<HorizontalPanel> hKeyPanelList;
	private int visibleStatus = VISIBLE_MEDIUM;
	private List<String> firtRowList;
	public Status status;
	
	/**
	 * KeyMapTable
	 */
	public KeyMapTable() {
		status = new Status();
		status.setStyleName("okm-StatusPopup");
		
		table = new FlexTable();
		tableDocumentList = new ArrayList<FlexTable>();
		hKeyPanelList = new ArrayList<HorizontalPanel>();
		firtRowList = new ArrayList<String>();
		visibleStatus = VISIBLE_MEDIUM;
		
		table.setWidth("100%");
		
		table.setCellSpacing(0);
		table.setCellPadding(2);
		
		initWidget(table);
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
		Collection<String> selectedKeyList = Main.get().mainPanel.dashboard.keyMapDashboard.getFiltering();
		int rows = table.getRowCount();
		int firstRow = rows;
		
		firtRowList.add(""+firstRow);
		
		GWTDocument doc = new GWTDocument();
		if (gwtQueryResult.getDocument()!=null) {
			doc = gwtQueryResult.getDocument();
		} else if (gwtQueryResult.getAttachment()!=null) {
			doc = gwtQueryResult.getAttachment();
		}
		final String docPath = doc.getPath();
		
		Image gotoDocument = new Image("img/icon/actions/goto_document.gif");
		gotoDocument.addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				CommonUI.openAllFolderPath(docPath.substring(0,docPath.lastIndexOf("/")), docPath);
			}
			
		});
		gotoDocument.setTitle(Main.i18n("dashboard.keyword.goto.document"));
		gotoDocument.setStyleName("okm-KeyMap-ImageHover");
		table.setWidget(rows, 0, gotoDocument);
		
		if(doc.isAttachment())  {
			SimplePanel sp = new SimplePanel(); // Solves some middle alignament problem derived from mimeImageHTML method
			sp.add(new HTML(Util.imageItemHTML("img/email_attach.gif") + Util.mimeImageHTML(doc.getMimeType())));
			table.setWidget(rows, 1, sp);
		} else {
			SimplePanel sp = new SimplePanel(); // Solves some middle alignament problem derived from mimeImageHTML method
			sp.add(new HTML(Util.mimeImageHTML(doc.getMimeType())));
			table.setWidget(rows, 1, sp);
		}
		Hyperlink hLink = new Hyperlink();
		hLink.setHTML(doc.getName());
		// On attachemt case must remove last folder path, because it's internal usage not for visualization
		if (doc.isAttachment()) {
			hLink.setTitle(doc.getParent().substring(0, doc.getParent().lastIndexOf("/")));
		} else {
			hLink.setTitle(doc.getParent());
		}
		
		table.setWidget(rows, 2, hLink);
		
		// Format
		table.getCellFormatter().setHorizontalAlignment(rows, 0, HasAlignment.ALIGN_CENTER);
		table.getCellFormatter().setHorizontalAlignment(rows, 1, HasAlignment.ALIGN_RIGHT);
		table.getCellFormatter().setHorizontalAlignment(rows, 2, HasAlignment.ALIGN_LEFT);
		table.getCellFormatter().setVerticalAlignment(rows, 0, HasAlignment.ALIGN_MIDDLE);
		table.getCellFormatter().setVerticalAlignment(rows, 0, HasAlignment.ALIGN_MIDDLE);
		table.getCellFormatter().setVerticalAlignment(rows, 2, HasAlignment.ALIGN_MIDDLE);
		table.getFlexCellFormatter().setWidth(rows, 0, "24");
		table.getFlexCellFormatter().setWidth(rows, 1, "47");
		
		for (int i=0; i<2; i++) {
			table.getCellFormatter().addStyleName(rows, i, "okm-DisableSelect");
		}
		
		// Writing detail
		rows++; // Next row line
		FlexTable tableDocument = new FlexTable();
		FlexTable tableProperties = new FlexTable();
		FlexTable tableSubscribedUsers = new FlexTable();
		tableDocument.setWidget(0, 0, tableProperties);
		tableDocument.setHTML(0,1, "");
		tableDocument.setWidget(0,2,tableSubscribedUsers);
		tableDocument.getFlexCellFormatter().setVerticalAlignment(0, 0, HasAlignment.ALIGN_TOP);
		tableDocument.getFlexCellFormatter().setVerticalAlignment(0, 2, HasAlignment.ALIGN_TOP);
		tableDocument.getCellFormatter().setWidth(0, 0, "75%");
		tableDocument.getCellFormatter().setWidth(0, 1, "25");
		tableDocument.getCellFormatter().setWidth(0, 2, "25%");
		
		tableDocument.setWidth("100%");
		table.setWidget(rows, 0, tableDocument);
		table.getFlexCellFormatter().setColSpan(rows, 0, 3);
		table.getCellFormatter().setHorizontalAlignment(rows, 0, HasHorizontalAlignment.ALIGN_LEFT);
		
		tableDocument.setStyleName("okm-DisableSelect");
		tableProperties.setStyleName("okm-DisableSelect");
		tableSubscribedUsers.setStyleName("okm-DisableSelect");
		
		tableProperties.setHTML(0, 0, "<b>"+Main.i18n("document.folder")+"</b>");
		tableProperties.setHTML(0, 1, doc.getParentId());
		tableProperties.setHTML(1, 0, "<b>"+Main.i18n("document.size")+"</b>");
		tableProperties.setHTML(1, 1, Util.formatSize(doc.getActualVersion().getSize()));
		tableProperties.setHTML(2, 0, "<b>"+Main.i18n("document.created")+"</b>");
		DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
		tableProperties.setHTML(2, 1, dtf.format(doc.getCreated())+" "+Main.i18n("document.by")+" "+doc.getAuthor());
		tableProperties.setHTML(3, 0, "<b>"+Main.i18n("document.lastmodified")+"</b>");
		tableProperties.setHTML(3, 1, dtf.format(doc.getLastModified())+" "+Main.i18n("document.by")+" "+doc.getActualVersion().getAuthor());
		tableProperties.setHTML(4, 0, "<b>"+Main.i18n("document.mimetype")+"</b>");
		tableProperties.setHTML(5, 1, doc.getMimeType());
		tableProperties.setHTML(5, 0, "<b>"+Main.i18n("document.status")+"</b>");
		tableProperties.setHTML(6, 1, "");
		tableProperties.setHTML(6, 0, "<b>"+Main.i18n("document.subscribed")+"</b>");
		tableProperties.setHTML(6, 1, "");
		
		if (doc.isCheckedOut()) {
			tableProperties.setHTML(5, 1, Main.i18n("document.status.checkout")+" "+doc.getLockInfo().getOwner());
		} else if (doc.isLocked()) {
			tableProperties.setHTML(5, 1, Main.i18n("document.status.locked")+" "+doc.getLockInfo().getOwner());
		} else {
			tableProperties.setHTML(5, 1, Main.i18n("document.status.normal"));
		}
		
		if (doc.isSubscribed()) {
			tableProperties.setHTML(6, 1, Main.i18n("document.subscribed.yes"));
		} else {
			tableProperties.setHTML(6, 1, Main.i18n("document.subscribed.no"));
		}
		
		// Sets wordWrap for al rows
		for (int i=0; i<7; i++) {
			setRowWordWarp(i, 2, false, tableProperties);
		}
		
		// Setting subscribers
		tableSubscribedUsers.setHTML(0,0,"<b>"+Main.i18n("document.subscribed.users")+"<b>");
		setRowWordWarp(0, 1, false, tableSubscribedUsers);
		
		// Sets the folder subscribers
		for (Iterator<String> it= doc.getSubscriptors().iterator(); it.hasNext(); ) {
			tableSubscribedUsers.setHTML(tableSubscribedUsers.getRowCount(), 0, it.next());
			setRowWordWarp(tableSubscribedUsers.getRowCount()-1, 1, false, tableSubscribedUsers);
		}
		
		// Writing keys
		rows++; // Next row line
		HorizontalPanel hKeyPanel = new HorizontalPanel();
		table.setWidget(rows, 0, hKeyPanel);
		table.getFlexCellFormatter().setColSpan(rows, 0, 3);
		table.getCellFormatter().setHorizontalAlignment(rows, 0, HasHorizontalAlignment.ALIGN_RIGHT);
		table.getCellFormatter().addStyleName(rows, 0, "okm-Table-BottomBorder");
		
		for (Iterator<String> it = doc.getKeywords().iterator(); it.hasNext();){
			// First adds only new keywords
			final String keyword = it.next();
			if (!selectedKeyList.contains(keyword)) {
				HorizontalPanel externalPanel = new HorizontalPanel();
				HorizontalPanel hPanel = new HorizontalPanel();
				HTML space = new HTML();
				ImageHover add = new ImageHover("img/icon/actions/add_disabled.gif","img/icon/actions/add.gif");
				add.addClickHandler(new ClickHandler() { 
					@Override
					public void onClick(ClickEvent event) {
						// remove keyword on all keyword panels
						Main.get().mainPanel.dashboard.keyMapDashboard.selectKey(keyword);
					}
				});
				add.setStyleName("okm-KeyMap-ImageHover");
				hPanel.add(new HTML(keyword));
				hPanel.add(space);
				hPanel.add(add);
				hPanel.setCellWidth(space, "6");
				hPanel.setStyleName("okm-KeyMap-Gray");
				HTML space1 = new HTML();
				externalPanel.add(hPanel);
				externalPanel.add(space1);
				externalPanel.setCellWidth(space1, "6");
				hKeyPanel.add(externalPanel);
			}
		}
		
		for (Iterator<String> it = selectedKeyList.iterator(); it.hasNext();) {
			// Last adding selected keywords
			final String keyword = it.next();
			HorizontalPanel externalPanel = new HorizontalPanel();
			HorizontalPanel hPanel = new HorizontalPanel();
			HTML space = new HTML();
			ImageHover add = new ImageHover("img/icon/actions/delete_disabled.gif","img/icon/actions/delete.gif");
			add.addClickHandler(new ClickHandler() { 
				@Override
				public void onClick(ClickEvent event) {
					// remove keyword on all keyword panels
					Main.get().mainPanel.dashboard.keyMapDashboard.removeKey(keyword);
				}
			});
			add.setStyleName("okm-KeyMap-ImageHover");
			hPanel.add(new HTML(keyword));
			hPanel.add(space);
			hPanel.add(add);
			hPanel.setCellWidth(space, "6");
			hPanel.setStyleName("okm-KeyMap-Selected");
			HTML space1 = new HTML();
			externalPanel.add(hPanel);
			externalPanel.add(space1);
			externalPanel.setCellWidth(space1, "6");
			hKeyPanel.add(externalPanel);
		}
		
		// Setting visibility
		switch (visibleStatus) {
			case VISIBLE_SMALL:
				tableDocument.setVisible(false);
				hKeyPanel.setVisible(false);
				table.getCellFormatter().addStyleName(firstRow, 0, "okm-Table-BottomBorder");
				table.getCellFormatter().addStyleName(firstRow, 1, "okm-Table-BottomBorder");
				table.getCellFormatter().addStyleName(firstRow, 2, "okm-Table-BottomBorder");
				break;
			case VISIBLE_MEDIUM:
				tableDocument.setVisible(false);
				hKeyPanel.setVisible(true);
				break;
			case VISIBLE_BIG:
				tableDocument.setVisible(true);
				hKeyPanel.setVisible(true);
				break;
		}
		
		// Saving object for refreshing language and setting visible ( true / false )
		tableDocumentList.add(tableDocument);
		hKeyPanelList.add(hKeyPanel);
	}
	
	/**
	 * Lang refreshing
	 */
	public void langRefresh() {
		for (Iterator<FlexTable> it = tableDocumentList.iterator(); it.hasNext();) {
			FlexTable tableDocument = it.next();
			FlexTable tableProperties = (FlexTable) tableDocument.getWidget(0, 0);
			FlexTable tableSubscribedUsers  = (FlexTable) tableDocument.getWidget(0, 2);
			
			tableProperties.setHTML(1, 0, "<b>"+Main.i18n("document.folder")+"</b>");
			tableProperties.setHTML(2, 0, "<b>"+Main.i18n("document.size")+"</b>");
			tableProperties.setHTML(3, 0, "<b>"+Main.i18n("document.created")+"</b>");
			tableProperties.setHTML(4, 0, "<b>"+Main.i18n("document.lastmodified")+"</b>");
			tableProperties.setHTML(5, 0, "<b>"+Main.i18n("document.mimetype")+"</b>");
			tableProperties.setHTML(6, 0, "<b>"+Main.i18n("document.status")+"</b>");
			tableProperties.setHTML(7, 0, "<b>"+Main.i18n("document.subscribed")+"</b>");
			
			tableSubscribedUsers.setHTML(0,0,"<b>"+Main.i18n("document.subscribed.users")+"<b>");
		}
	}
	
	/**
	 * Changes the visualization detail
	 * 
	 * @param value The new visualization detail
	 */
	public void changeVisibilityDetail(int value) {
		visibleStatus = value;
		refreshingVisibilityDetail();
	}
	
	/**
	 * refreshing the visualizationa panels
	 */
	private void refreshingVisibilityDetail() {
		// Adding or removing style on first column to showing border when other panells are unvisible
		if (visibleStatus==VISIBLE_SMALL){
			for (Iterator<String> it=firtRowList.iterator(); it.hasNext();) {
				int firstRow = Integer.parseInt(it.next());
				table.getCellFormatter().addStyleName(firstRow, 0, "okm-Table-BottomBorder");
				table.getCellFormatter().addStyleName(firstRow, 1, "okm-Table-BottomBorder");
				table.getCellFormatter().addStyleName(firstRow, 2, "okm-Table-BottomBorder");
			}
		} else {
			for (Iterator<String> it=firtRowList.iterator(); it.hasNext();) {
				int firstRow = Integer.parseInt(it.next());
				table.getCellFormatter().removeStyleName(firstRow, 0, "okm-Table-BottomBorder");
				table.getCellFormatter().removeStyleName(firstRow, 1, "okm-Table-BottomBorder");
				table.getCellFormatter().removeStyleName(firstRow, 2, "okm-Table-BottomBorder");
			}
		}
		
		// Setting document properties visibility
		for (Iterator<FlexTable> it = tableDocumentList.iterator(); it.hasNext();) {
			FlexTable tableDocument = it.next();
			// Setting visibility
			switch (visibleStatus) {
				case VISIBLE_SMALL:
					tableDocument.setVisible(false);
					break;
				case VISIBLE_MEDIUM:
					tableDocument.setVisible(false);
					break;
				case VISIBLE_BIG:
					tableDocument.setVisible(true);
					break;
			}
		}
		
		// Setting key panel visibility
		for (Iterator<HorizontalPanel> it = hKeyPanelList.iterator(); it.hasNext();) {
			HorizontalPanel hKeyPanel = it.next();
			switch (visibleStatus) {
				case VISIBLE_SMALL:
					hKeyPanel.setVisible(false);
					break;
				case VISIBLE_MEDIUM:
					hKeyPanel.setVisible(true);
					break;
				case VISIBLE_BIG:
					hKeyPanel.setVisible(true);
					break;
			}
		}
	}
	
	/**
	 * Gets the actual detail
	 * 
	 * @return The actual detail 
	 */
	public int getActualDetail() {
		return visibleStatus;
	}
	
	/**
	 * Adding folder
	 * 
	 * @param gwtQueryResult Query result
	 * @param score The folder score
	 */
	private void addFolderRow(GWTQueryResult gwtQueryResult, Score score) {
		// NOT YET IMPLEMENTED
	}
	
	/**
	 * Adding mail
	 * 
	 * @param gwtQueryResult Query result
	 * @param score The mail score
	 */
	private void addMailRow(GWTQueryResult gwtQueryResult, Score score) {
		// NOT YET IMPLEMENTED
	}
	
	/**
	 * removeAllRows
	 */
	private void removeAllRows() {
		while (table.getRowCount()>0) {
			table.removeRow(0);
		}
	}
	
	/**
	 * Resets the values
	 */
	public void reset() {
		removeAllRows();
		tableDocumentList = new ArrayList<FlexTable>();
		hKeyPanelList = new ArrayList<HorizontalPanel>();
		firtRowList = new ArrayList<String>();
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
	 * Sets the refreshing
	 */
	public void setRefreshing() {
		int left = Main.get().mainPanel.dashboard.keyMapDashboard.scrollTable.getAbsoluteLeft() + 
				   (Main.get().mainPanel.dashboard.keyMapDashboard.scrollTable.getOffsetWidth()/2);
		int top = Main.get().mainPanel.dashboard.keyMapDashboard.scrollTable.getAbsoluteTop() + 
				  (Main.get().mainPanel.dashboard.keyMapDashboard.scrollTable.getOffsetHeight()/2);
		status.setFlag_getDashboard();
		status.refresh(left, top);
	}
	
	/**
	 * Unsets the refreshing
	 */
	public void unsetRefreshing() {
		status.unsetFlag_getDashboard();
	}
}