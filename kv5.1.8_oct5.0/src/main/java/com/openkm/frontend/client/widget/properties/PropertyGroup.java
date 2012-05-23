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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.bean.GWTPermission;
import com.openkm.frontend.client.bean.GWTPropertyGroup;
import com.openkm.frontend.client.extension.event.handler.PropertyGroupHandlerExtension;
import com.openkm.frontend.client.extension.event.hashandler.HasPropertyGroupHandlerExtension;
import com.openkm.frontend.client.widget.ConfirmPopup;
import com.openkm.frontend.client.widget.propertygroup.PropertyGroupWidget;
import com.openkm.frontend.client.widget.propertygroup.PropertyGroupWidgetToFire;

/**
 * PropertyGroup
 * 
 * @author jllort
 *
 */
public class PropertyGroup extends Composite implements HasPropertyGroupHandlerExtension {
	
	private ScrollPanel scrollPanel;
	private PropertyGroupWidget propertyGroupWidget;
	private Button changeButton;
	private Button removeButton;
	private Button cancelButton;
	private boolean editValues = false;
	private FiredHorizontalPanel hPanelFired;
	private GWTPropertyGroup propertyGroup;
	private boolean visible;
	
	/**
	 * PropertyGroup
	 */
	public PropertyGroup(GWTPropertyGroup propertyGroup, Object node, GWTFolder parentFolder, boolean visible) {
		this.visible = visible;
		String path = "";
		byte permissions = 0;
		if (node instanceof GWTDocument) {
			path = ((GWTDocument) node).getPath();
			permissions = ((GWTDocument) node).getPermissions();
		} else if (node instanceof GWTFolder) {
			path = ((GWTFolder) node).getPath();
			permissions = ((GWTFolder) node).getPermissions();
		} else if (node instanceof GWTMail) {
			path = ((GWTMail) node).getPath();
			permissions = ((GWTMail) node).getPermissions();
		}
		hPanelFired = new FiredHorizontalPanel();
		propertyGroupWidget = new PropertyGroupWidget(path, propertyGroup, hPanelFired, hPanelFired);
		scrollPanel = new ScrollPanel(propertyGroupWidget);
		this.propertyGroup = propertyGroup;
		
		changeButton = new Button(Main.i18n("button.change"), new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				if (!editValues) {
					Main.get().mainPanel.disableKeyShorcuts(); // Disables key shortcuts while updating
					changeButton.setHTML(Main.i18n("button.memory"));
					cancelButton.setVisible(true);
					edit();
					editValues = true;
					removeButton.setVisible(false);
				} else {
					if (propertyGroupWidget.getValidationProcessor().validate()) {
						Main.get().mainPanel.enableKeyShorcuts(); // Enables general keys applications
						changeButton.setHTML(Main.i18n("button.change"));
						setProperties();
						editValues = false;
						removeButton.setVisible(true);
						cancelButton.setVisible(false);
					} 
				}
			}
		});
		
		removeButton = new Button(Main.i18n("button.delete"), new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				if (!editValues) {
					Main.get().confirmPopup.setConfirm(ConfirmPopup.CONFIRM_DELETE_PROPERTY_GROUP);
					Main.get().confirmPopup.show();
				} 
			}
		});
		
		cancelButton = new Button(Main.i18n("button.cancel"), new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				if (editValues) {
					Main.get().mainPanel.enableKeyShorcuts(); // Enables general keys applications
					changeButton.setHTML(Main.i18n("button.change"));
					editValues = false;
					cancelEdit();
					removeButton.setVisible(true);
					cancelButton.setVisible(false);
				} 
			}
		});
		
		// Checking button permisions
		if (!visible) {
			changeButton.setVisible(visible);
			removeButton.setVisible(visible);
			
		} else if ( ((permissions & GWTPermission.WRITE) == GWTPermission.WRITE) &&
			     ((parentFolder.getPermissions() & GWTPermission.WRITE) == GWTPermission.WRITE))  {
	    	 		changeButton.setVisible(true);
	    	 		removeButton.setVisible(true);
	    	 		
		} else {
			changeButton.setVisible(false);
			removeButton.setVisible(false);
		}
		
		hPanelFired.add(changeButton);
		hPanelFired.add(new HTML("&nbsp;&nbsp;"));
		hPanelFired.add(cancelButton);
		hPanelFired.add(new HTML("&nbsp;&nbsp;"));
		hPanelFired.add(removeButton);
		
		cancelButton.setVisible(false);  // Not shows cancel button
		
		// Button format
		changeButton.setStyleName("okm-Button");
		removeButton.setStyleName("okm-Button");
		cancelButton.setStyleName("okm-Button");

		getProperties();
		
		initWidget(scrollPanel);
	}
	
	/**
	 * isButtonsVisible
	 * 
	 * @return
	 */
	public boolean isButtonsVisible() {
		return visible;
	}
	
	/**
	 * Lang refresh
	 */
	public void langRefresh() {
		changeButton.setHTML(Main.i18n("button.change"));
		removeButton.setHTML(Main.i18n("button.delete"));		
	}	
	
	/**
	 * Gets all group properties 
	 */
	private void getProperties() {
		propertyGroupWidget.getProperties();
	}

	/**
	 * Remove the document property group
	 */
	public void removeGroup() {
		propertyGroupWidget.removeGroup();
	}
	
	/**
	 * set document property group
	 */
	public void setProperties() {
		Main.get().mainPanel.desktop.browser.tabMultiple.status.setGroupProperties();
		propertyGroupWidget.setProperties();
	}
	
	/**
	 * edit document property group
	 */
	public void edit() {
		propertyGroupWidget.edit();
	}
	
	/**
	 * edit document property group
	 */
	public void cancelEdit() {
		propertyGroupWidget.cancelEdit();
	}
	
	/**
	 * Gets the group name
	 * 
	 * @return The group name
	 */
	public String getGrpName(){
		return propertyGroup.getName();
	}
	
	/**
	 * Gets the group label
	 * 
	 * @return The group label
	 */
	public String getGrpLabel(){
		return propertyGroup.getLabel();
	}
	
	/**
	 * FiredHorizontalPanel
	 * 
	 * @author jllort
	 *
	 */
	private class FiredHorizontalPanel extends Composite implements PropertyGroupWidgetToFire {
		private HorizontalPanel hPanel;
		
		public FiredHorizontalPanel() {
			hPanel = new HorizontalPanel();
			initWidget(hPanel);
		}
		
		@Override
		public void finishedGetProperties() {
		}
		
		@Override
		public void finishedSetProperties() {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetGroupProperties();
		}
		
		/**
		 * add
		 * 
		 * @param widget
		 */
		public void add(Widget widget) {
			hPanel.add(widget);
		}
	}

	@Override
	public void addPropertyGroupHandlerExtension(PropertyGroupHandlerExtension handlerExtension) {
		propertyGroupWidget.addPropertyGroupHandlerExtension(handlerExtension);
	}
}
