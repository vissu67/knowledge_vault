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

package com.openkm.frontend.client.widget.categories;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTFolder;

public class CategoriesSelectPopup extends DialogBox  {	
	
	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	public ScrollPanel scrollDirectoryPanel;
	private VerticalPanel verticalDirectoryPanel;
	private FolderSelectTree folderSelectTree;
	private Button cancelButton;
	private Button actionButton;
	public Status status;
	
	public CategoriesSelectPopup() {
		// Establishes auto-close when click outside
		super(false,true);
		
		status = new Status();
		status.setStyleName("okm-StatusPopup");
		
		vPanel = new VerticalPanel();		
		vPanel.setWidth("300");
		vPanel.setHeight("200");
		hPanel = new HorizontalPanel();
		
		scrollDirectoryPanel = new ScrollPanel();
		scrollDirectoryPanel.setSize("290", "150");
		scrollDirectoryPanel.setStyleName("okm-Popup-text");
		verticalDirectoryPanel = new VerticalPanel();
		verticalDirectoryPanel.setSize("100%", "100%");
		folderSelectTree = new FolderSelectTree();
		folderSelectTree.setSize("100%", "100%");
				
		verticalDirectoryPanel.add(folderSelectTree);
		scrollDirectoryPanel.add(verticalDirectoryPanel);
		
		cancelButton = new Button(Main.i18n("button.close"), new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		actionButton = new Button(Main.i18n("button.add"), new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				executeAction(folderSelectTree.getCategory());
			}
		});
		
		vPanel.add(new HTML("<br>"));
		vPanel.add(scrollDirectoryPanel);
		vPanel.add(new HTML("<br>"));
		hPanel.add(cancelButton);
		HTML space = new HTML();
		space.setWidth("50");
		hPanel.add(space);
		hPanel.add(actionButton);
		vPanel.add(hPanel);
		vPanel.add(new HTML("<br>"));
		
		vPanel.setCellHorizontalAlignment(scrollDirectoryPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHeight(scrollDirectoryPanel, "150");

		cancelButton.setStyleName("okm-Button");
		actionButton.setStyleName("okm-Button");

		super.hide();
		setWidget(vPanel);
	}
	
	/**
	 * Executes the action
	 */
	public void executeAction(GWTFolder category) {
		Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.document.addCategory(category);
	}
	
	/**
	 * Language refresh
	 */
	public void langRefresh() {
		setText(Main.i18n("categories.folder.select.label"));
		cancelButton.setText(Main.i18n("button.close"));
		actionButton.setText(Main.i18n("button.add"));		
	}
	
	/**
	 * Shows the popup 
	 */
	public void show(){
		initButtons();
		int left = (Window.getClientWidth()-300) / 2;
		int top = (Window.getClientHeight()-200) / 2;
		setPopupPosition(left, top);
		setText(Main.i18n("categories.folder.select.label"));
		
		// Resets to initial tree value
		folderSelectTree.reset();
		super.show();
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
	 * Enables all button
	 */
	private void initButtons() {
		cancelButton.setEnabled(true);
		actionButton.setEnabled(false);
	}
}