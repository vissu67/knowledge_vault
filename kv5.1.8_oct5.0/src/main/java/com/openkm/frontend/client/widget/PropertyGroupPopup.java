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

package com.openkm.frontend.client.widget;

import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.bean.GWTPropertyGroup;
import com.openkm.frontend.client.service.OKMPropertyGroupService;
import com.openkm.frontend.client.service.OKMPropertyGroupServiceAsync;

/**
 * Group popup
 * 
 * @author jllort
 *
 */
public class PropertyGroupPopup extends DialogBox {
	
	private final OKMPropertyGroupServiceAsync propertyGroupService = (OKMPropertyGroupServiceAsync) GWT.create(OKMPropertyGroupService.class);
	
	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	private Button button;
	private Button addButton;
	private ListBox listBox;
	private String path;
	
	/**
	 * About popup
	 */
	public PropertyGroupPopup() {
		// Establishes auto-close when click outside
		super(false,true);

		vPanel = new VerticalPanel();
		hPanel = new HorizontalPanel();
		
		button = new Button(Main.i18n("button.close"), new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		addButton = new Button(Main.i18n("button.add"), new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				addGroup();
				hide();
			}
		});

		listBox = new ListBox();
		listBox.addChangeHandler(new ChangeHandler(){
			@Override
			public void onChange(ChangeEvent arg0) {
				if (listBox.getSelectedIndex()>0) {
					addButton.setEnabled(true);
				} else {
					addButton.setEnabled(false);
				}
			}
		});
		
		listBox.setStyleName("okm-Select");
		
		vPanel.setWidth("300px");
		vPanel.setHeight("50px");
		button.setStyleName("okm-Button");
		addButton.setStyleName("okm-Button");
		addButton.setEnabled(false);
		
		hPanel.add(button);
		hPanel.add(new HTML("&nbsp;&nbsp;"));
		hPanel.add(addButton);
		
		hPanel.setCellHorizontalAlignment(button,VerticalPanel.ALIGN_CENTER);
		hPanel.setCellHorizontalAlignment(addButton,VerticalPanel.ALIGN_CENTER);

		vPanel.add(new HTML("<br>"));
		vPanel.add(listBox);
		vPanel.add(new HTML("<br>"));
		vPanel.add(hPanel);
		vPanel.add(new HTML("<br>"));
		
		vPanel.setCellHorizontalAlignment(listBox, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, VerticalPanel.ALIGN_CENTER);

		super.hide();
		setWidget(vPanel);
	}
	
	/**
	 * Gets asyncronous to get all groups
	 */
	final AsyncCallback<List<GWTPropertyGroup>> callbackGetAllGroups = new AsyncCallback<List<GWTPropertyGroup>>() {
		public void onSuccess(List<GWTPropertyGroup> result){
			listBox.clear();
			listBox.addItem("",""); // Adds empty value
			
			for (Iterator<GWTPropertyGroup> it = result.iterator(); it.hasNext();) {
				GWTPropertyGroup group = it.next();
				listBox.addItem(group.getLabel(), group.getName());
			}
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("GetAllGroups", caught);
		}
	};
	
	/**
	 * Gets asyncronous to add a group
	 */
	final AsyncCallback<Object> callbackAddGroup = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {
			Object node = Main.get().mainPanel.topPanel.toolBar.getActualNode();
			if (node!=null) {
				if (Main.get().mainPanel.topPanel.toolBar.isNodeDocument()){
					GWTDocument doc = (GWTDocument) Main.get().mainPanel.topPanel.toolBar.getActualNode();
					Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.setProperties(doc);
				} else if (Main.get().mainPanel.topPanel.toolBar.isNodeFolder()){
					GWTFolder folder = (GWTFolder) Main.get().mainPanel.topPanel.toolBar.getActualNode();
					Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.setProperties(folder);
				} else if (Main.get().mainPanel.topPanel.toolBar.isNodeMail()){
					GWTMail mail = (GWTMail) Main.get().mainPanel.topPanel.toolBar.getActualNode();
					Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.setProperties(mail);
				}
				// Case there's only two items (white and other) and this is added, then
				// there's no item to be added and must disable addPropertyGroup
				if (listBox.getItemCount()==2) {
					Main.get().mainPanel.topPanel.toolBar.disableAddPropertyGroup();
				}
			}
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("AddGroup", caught);
		}
	};
	
	/**
	 * Enables close button
	 */
	public void enableClose() {
		button.setEnabled(true);
		Main.get().mainPanel.setVisible(true); // Shows main panel when all widgets are loaded
	}
	
	/**
	 * Language refresh
	 */
	public void langRefresh() {
		setText(Main.i18n("group.label"));
		button.setText(Main.i18n("button.close"));
		addButton.setText(Main.i18n("button.add"));
	}
	
	/**
	 * Show the popup error
	 * 
	 * @param msg Error message
	 */
	public void show() {
		setText(Main.i18n("group.label"));
		getAllGroups(); // Gets all groups
		addButton.setEnabled(false);
		int left = (Window.getClientWidth()-300)/2;
		int top = (Window.getClientHeight()-100)/2;
		setPopupPosition(left,top);
		super.show();
	}
	
	/**
	 * Gets all property groups
	 */
	private void getAllGroups() {
		path = Main.get().mainPanel.topPanel.toolBar.getActualNodePath();
		if (!path.equals("")) {
			propertyGroupService.getAllGroups(path, callbackGetAllGroups);
		}
	}
	
	/**
	 * Add a group to a document
	 */
	private void addGroup() {
		if (listBox.getSelectedIndex()>0) {
			String grpName = listBox.getValue(listBox.getSelectedIndex());
			if (path!=null && !path.equals("")) {
				propertyGroupService.addGroup(path, grpName, callbackAddGroup);
			}
		}
	}
}
