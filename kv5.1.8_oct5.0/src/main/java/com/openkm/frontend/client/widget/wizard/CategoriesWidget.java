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

package com.openkm.frontend.client.widget.wizard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.contants.service.RPCService;
import com.openkm.frontend.client.service.OKMPropertyService;
import com.openkm.frontend.client.service.OKMPropertyServiceAsync;
import com.openkm.frontend.client.util.CommonUI;
import com.openkm.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.util.Util;

/**
 * CategoriesWidget
 * 
 * @author jllort
 *
 */
public class CategoriesWidget extends Composite {
	private final OKMPropertyServiceAsync propertyService = (OKMPropertyServiceAsync) GWT.create(OKMPropertyService.class);
	
	private FlexTable table;
	private String docPath;
	private CellFormatter cellFormatter;	
	private VerticalPanel vPanel; 
	private ScrollPanel scrollDirectoryPanel;
	private VerticalPanel verticalDirectoryPanel;
	private FolderSelectTree folderSelectTree;
	private Button actionButton;
	private FlexTable tableSubscribedCategories;
	private Collection<GWTFolder> docCategories;
	private boolean remove = true;
	
	public CategoriesWidget(String docPath, Widget widget) {
		table = new FlexTable();
		tableSubscribedCategories = new FlexTable();
		docCategories = new ArrayList<GWTFolder>();
		this.docPath = docPath;
		
		cellFormatter = table.getCellFormatter(); // Gets the cell formatter
		table.setWidth("100%");
		table.setCellPadding(0);
		table.setCellSpacing(2);

		table.setWidget(0, 0, widget);
		table.getFlexCellFormatter().setColSpan(0,0,2);
		cellFormatter.addStyleName(0,0,"okm-Security-Title-RightBorder"); // Border and margins
		
		RowFormatter rowFormatter = table.getRowFormatter();
		rowFormatter.setStyleName(0, "okm-Security-Title");
			
		// Widget format
		cellFormatter.setHorizontalAlignment(0,0,HasAlignment.ALIGN_CENTER);
		cellFormatter.setVerticalAlignment(0,0,HasAlignment.ALIGN_MIDDLE);
		
		// Categories
		vPanel = new VerticalPanel();		
		vPanel.setWidth("390");
		vPanel.setHeight("175");
		
		scrollDirectoryPanel = new ScrollPanel();
		scrollDirectoryPanel.setSize("380", "150");
		scrollDirectoryPanel.setStyleName("okm-Popup-text");
		verticalDirectoryPanel = new VerticalPanel();
		verticalDirectoryPanel.setSize("100%", "100%");
		folderSelectTree = new FolderSelectTree();
		folderSelectTree.setSize("100%", "100%");
				
		verticalDirectoryPanel.add(folderSelectTree);
		scrollDirectoryPanel.add(verticalDirectoryPanel);
		
		actionButton = new Button(Main.i18n("button.add"), new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				Main.get().wizardPopup.categoriesWidget.addCategory(folderSelectTree.getCategory());
			}
		});
		actionButton.setEnabled(false);
		vPanel.add(scrollDirectoryPanel);
		vPanel.add(actionButton);
		
		vPanel.setCellHorizontalAlignment(scrollDirectoryPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(actionButton, HasAlignment.ALIGN_CENTER);
		vPanel.setCellVerticalAlignment(actionButton, HasAlignment.ALIGN_MIDDLE);
		vPanel.setCellHeight(scrollDirectoryPanel, "150");
		vPanel.setCellHeight(actionButton, "25");
		
		table.setWidget(1, 0, vPanel);
		table.getFlexCellFormatter().setColSpan(1,0,2);
		cellFormatter.setHorizontalAlignment(1,0,HasAlignment.ALIGN_CENTER);
		
		table.setHTML(2, 0, "&nbsp;<b>"+Main.i18n("document.categories")+"</b>");
		table.getFlexCellFormatter().setColSpan(2,0,2);
		cellFormatter.setHorizontalAlignment(2,0,HasAlignment.ALIGN_LEFT);
		
		table.setWidget(3, 0, tableSubscribedCategories);
		table.getFlexCellFormatter().setColSpan(3,0,2);
		cellFormatter.setHorizontalAlignment(3,0,HasAlignment.ALIGN_LEFT);
		
		setRowWordWarp(0, 0,true, tableSubscribedCategories);

		table.setStyleName("okm-DisableSelect");
		actionButton.setStyleName("okm-Button");
		tableSubscribedCategories.setStyleName("okm-DisableSelect");
		
		// Resets to initial tree value
		folderSelectTree.reset();
		
		initWidget(table);
	}
	
	/**
	 * Enables or disables move button
	 * 
	 * @param enable
	 */
	public void enable(boolean enable) {
		actionButton.setEnabled(enable);
	}
	
	/**
	 * Callback addCategory document
	 */
	final AsyncCallback<Object> callbackAddCategory = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {	
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("AddCategory", caught);
		}
	};
	
	/**
	 * Callback removeCategory document
	 */
	final AsyncCallback<Object> callbackRemoveCategory = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {	
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("RemoveCategory", caught);
		}
	};
	
	/**
	 * addCategory document
	 */
	public void addCategory(GWTFolder category) {
		if (!existCategory(category.getUuid())) {
			docCategories.add(category);
			drawCategory(category,remove);
			ServiceDefTarget endPoint = (ServiceDefTarget) propertyService;
			endPoint.setServiceEntryPoint(RPCService.PropertyService);
			propertyService.addCategory(docPath, category.getUuid(), callbackAddCategory);
		}
	}
	
	/**
	 * removeCategory document
	 */
	public void removeCategory(String UUID) {
		ServiceDefTarget endPoint = (ServiceDefTarget) propertyService;
		endPoint.setServiceEntryPoint(RPCService.PropertyService);
		propertyService.removeCategory(docPath, UUID, callbackRemoveCategory);
	}
	
	/**
	 * existCategory
	 * 
	 * @param Uuid
	 * @return
	 */
	private boolean existCategory(String Uuid) {
		boolean found = false;
		for (Iterator<GWTFolder> it = docCategories.iterator(); it.hasNext();) {
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
}