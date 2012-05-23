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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTTaskInstance;
import com.openkm.frontend.client.bean.form.GWTButton;
import com.openkm.frontend.client.contants.ui.UIDesktopConstants;
import com.openkm.frontend.client.contants.ui.UIDockPanelConstants;
import com.openkm.frontend.client.widget.form.FormManager.ValidationButton;
import com.openkm.frontend.client.widget.form.HasWorkflow;

import eu.maydu.gwt.validation.client.ValidationProcessor;

/**
 * Confirm panel
 * 
 * @author jllort
 *
 */
public class ConfirmPopup extends DialogBox {
	
	public static final int NO_ACTION 								= 0;
	public static final int CONFIRM_DELETE_FOLDER 					= 1;
	public static final int CONFIRM_DELETE_DOCUMENT 				= 2;
	public static final int CONFIRM_EMPTY_TRASH 					= 3;
	public static final int CONFIRM_PURGE_FOLDER 					= 4;
	public static final int CONFIRM_PURGE_DOCUMENT  				= 5;
	public static final int CONFIRM_DELETE_PROPERTY_GROUP			= 6;
	public static final int CONFIRM_PURGE_VERSION_HISTORY_DOCUMENT	= 7;
	public static final int CONFIRM_RESTORE_HISTORY_DOCUMENT		= 8;
	public static final int CONFIRM_SET_DEFAULT_HOME				= 9;
	public static final int CONFIRM_DELETE_SAVED_SEARCH				= 10;
	public static final int CONFIRM_DELETE_USER_NEWS				= 11;
	public static final int CONFIRM_DELETE_MAIL		 				= 12;
	public static final int CONFIRM_PURGE_MAIL  					= 13;
	public static final int CONFIRM_GET_POOLED_WORKFLOW_TASK		= 14;
	public static final int CONFIRM_FORCE_UNLOCK					= 15;
	public static final int CONFIRM_FORCE_CANCEL_CHECKOUT			= 16;
	public static final int CONFIRM_WORKFLOW_ACTION					= 17;
	
	//added by vissu on feb25 for zohoapi
	public static final int CONFIRM_CLOSE_ZOHO						= 18;

	
	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	private HTML text;
	private Button cancelButton;
	private Button acceptButton;
	private int action = 0;
	private Object object;
	
	/**
	 * Confirm popup
	 */
	public ConfirmPopup() {
		// Establishes auto-close when click outside
		super(false,true);
		
		vPanel = new VerticalPanel();
		hPanel = new HorizontalPanel();
		text = new HTML();
		text.setStyleName("okm-NoWrap");
		
		cancelButton = new Button(Main.i18n("button.cancel"), new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		acceptButton = new Button(Main.i18n("button.accept"), new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				execute();
				hide();
			}
		});

		vPanel.setWidth("300px");
		vPanel.setHeight("100px");
		cancelButton.setStyleName("okm-Button");
		acceptButton.setStyleName("okm-Button");

		text.setHTML("");
		
		hPanel.add(cancelButton);
		hPanel.add(new HTML("&nbsp;&nbsp;"));
		hPanel.add(acceptButton);
		
		vPanel.add(new HTML("<br>"));
		vPanel.add(text);
		vPanel.add(new HTML("<br>"));
		vPanel.add(hPanel);
		vPanel.add(new HTML("<br>"));
		
		vPanel.setCellHorizontalAlignment(text, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, VerticalPanel.ALIGN_CENTER);

		super.hide();
		setWidget(vPanel);
	}
	
	/**
	 * Execute the confirmed action
	 */
	private void execute() {
		switch (action) {
		
			case CONFIRM_DELETE_FOLDER :
				if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
					Main.get().mainPanel.desktop.browser.fileBrowser.delete();
				} else if (Main.get().activeFolderTree.isPanelSelected()) {
					Main.get().activeFolderTree.delete();
				}
				break;
				
			case CONFIRM_DELETE_DOCUMENT :
				if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
					Main.get().mainPanel.desktop.browser.fileBrowser.delete();
				}
				break;
				
			//added by vissu on feb25 for zohoapi
			case CONFIRM_CLOSE_ZOHO:
				if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
					//Main.get().mainPanel.desktop.browser.fileBrowser.zoho();
					Main.get().zohoPopup.executeClose();
				}
				break;
				
			case CONFIRM_EMPTY_TRASH :
				// Ensures DESKTOP view is enabled
				if (Main.get().mainPanel.topPanel.tabWorkspace.getSelectedWorkspace()!=UIDockPanelConstants.DESKTOP){
					Main.get().mainPanel.topPanel.tabWorkspace.changeSelectedTab(UIDockPanelConstants.DESKTOP);
				}
				
				//Ensures that trash view is enabled
				if (Main.get().mainPanel.desktop.navigator.getStackIndex() != UIDesktopConstants.NAVIGATOR_TRASH) {
					Main.get().mainPanel.desktop.navigator.stackPanel.showStack(UIDesktopConstants.NAVIGATOR_TRASH, false);
				}
				
				Main.get().activeFolderTree.purgeTrash();
				break;
			
			case CONFIRM_PURGE_FOLDER:
				if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
					Main.get().mainPanel.desktop.browser.fileBrowser.purge();
				} else if (Main.get().activeFolderTree.isPanelSelected()) {
					Main.get().activeFolderTree.purge();
				}
				break;
				
			case CONFIRM_PURGE_DOCUMENT:
				if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
					Main.get().mainPanel.desktop.browser.fileBrowser.purge();
				}
				break;
			
			case CONFIRM_DELETE_PROPERTY_GROUP:
				if (Main.get().mainPanel.topPanel.toolBar.isNodeDocument()) {
					Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.removePropertyGroup();
				} else if (Main.get().mainPanel.topPanel.toolBar.isNodeFolder()) {
					Main.get().mainPanel.desktop.browser.tabMultiple.tabFolder.removePropertyGroup();
				} else if (Main.get().mainPanel.topPanel.toolBar.isNodeMail()) {
					Main.get().mainPanel.desktop.browser.tabMultiple.tabMail.removePropertyGroup();
				}
				// Always if a property group is deleted add property button on tool bar must be enabled, we execute to ensure this
				Main.get().mainPanel.topPanel.toolBar.enableAddPropertyGroup();
				break;
				
			case CONFIRM_PURGE_VERSION_HISTORY_DOCUMENT:
				Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.version.purgeVersionHistory();
				break;
			
			case CONFIRM_RESTORE_HISTORY_DOCUMENT:
				if (object!=null && object instanceof String ) {
					Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.version.restoreVersion((String) object);
				}
				break;
			
			case CONFIRM_SET_DEFAULT_HOME:
				Main.get().mainPanel.topPanel.mainMenu.bookmark.setUserHome();
				break;
				
			case CONFIRM_DELETE_SAVED_SEARCH:
				Main.get().mainPanel.search.historySearch.searchSaved.deleteSearch();
				break;
				
			case CONFIRM_DELETE_USER_NEWS:
				Main.get().mainPanel.search.historySearch.userNews.deleteSearch();
				break;
			
			case CONFIRM_DELETE_MAIL :
				if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
					Main.get().mainPanel.desktop.browser.fileBrowser.delete();
				}
				break;
			
			case CONFIRM_PURGE_MAIL:
				if (Main.get().mainPanel.desktop.browser.fileBrowser.isPanelSelected()) {
					Main.get().mainPanel.desktop.browser.fileBrowser.purge();
				}
				break;
				
			case CONFIRM_GET_POOLED_WORKFLOW_TASK:
				Main.get().mainPanel.dashboard.workflowDashboard.setTaskInstanceActorId();
				break;
				
			case CONFIRM_FORCE_UNLOCK :
				Main.get().mainPanel.desktop.browser.fileBrowser.forceUnlock();
				break;
				
			case CONFIRM_FORCE_CANCEL_CHECKOUT:
				Main.get().mainPanel.desktop.browser.fileBrowser.forceCancelCheckout();
				break;
				
			case CONFIRM_WORKFLOW_ACTION:
				if (object!=null && object instanceof ValidationButton ) {
					ValidationButton validationButton = (ValidationButton) object;
					GWTButton gWTButton = validationButton.getButton();
					ValidationProcessor validationProcessor = validationButton.getValidationProcessor();
					HasWorkflow workflow = validationButton.getWorkflow();
					GWTTaskInstance taskInstance = validationButton.getTaskInstance();
					if (validationProcessor.validate()) {
						if (gWTButton.getTransition().equals("")) {
							workflow.setTaskInstanceValues(taskInstance.getId(), null);
						} else {
							workflow.setTaskInstanceValues(taskInstance.getId(), gWTButton.getTransition());
						}
						validationButton.disableAllButtonList();
					}
				}
				break;
		}
		
		action = NO_ACTION; // Resets action value
	}
	
	/**
	 * Sets the action to be confirmed
	 * 
	 * @param action The action to be confirmed
	 */
	public void setConfirm(int action) {
		this.action = action;
		switch (action) {
		
			case CONFIRM_DELETE_FOLDER :
				text.setHTML(Main.i18n("confirm.delete.folder"));
				break;
				
			case CONFIRM_DELETE_DOCUMENT :
				text.setHTML(Main.i18n("confirm.delete.document"));
				break;
				
			//added by vissu on feb25 for zohoapi
			case CONFIRM_CLOSE_ZOHO :
				text.setHTML(Main.i18n("confirm.close.zoho"));
				break;
				
			
			case CONFIRM_EMPTY_TRASH :
				text.setHTML(Main.i18n("confirm.delete.trash"));
				break;
			
			case CONFIRM_PURGE_FOLDER:
				text.setHTML(Main.i18n("confirm.purge.folder"));
				break;
				
			case CONFIRM_PURGE_DOCUMENT:
				text.setHTML(Main.i18n("confirm.purge.document"));
				break;
			
			case CONFIRM_DELETE_PROPERTY_GROUP:
				text.setHTML(Main.i18n("confirm.delete.propety.group"));
				break;
			
			case CONFIRM_PURGE_VERSION_HISTORY_DOCUMENT:
				text.setHTML(Main.i18n("confirm.purge.version.history.document"));
				break;
			
			case CONFIRM_RESTORE_HISTORY_DOCUMENT:
				text.setHTML(Main.i18n("confirm.purge.restore.document"));
				break;
			
			case CONFIRM_SET_DEFAULT_HOME:
				text.setHTML(Main.i18n("confirm.set.default.home"));
				break;
				
			case CONFIRM_DELETE_SAVED_SEARCH:
				text.setHTML(Main.i18n("confirm.delete.saved.search"));
				break;
				
			case CONFIRM_DELETE_USER_NEWS:
				text.setHTML(Main.i18n("confirm.delete.user.news"));
				break;
			
			case CONFIRM_DELETE_MAIL:
				text.setHTML(Main.i18n("confirm.delete.mail"));
				break;
				
			case CONFIRM_GET_POOLED_WORKFLOW_TASK:
				text.setHTML(Main.i18n("confirm.get.pooled.workflow.task"));
				break;
				
			case CONFIRM_FORCE_UNLOCK :
				text.setHTML(Main.i18n("confirm.force.unlock"));
				break;
				
			case CONFIRM_FORCE_CANCEL_CHECKOUT:
				text.setHTML(Main.i18n("confirm.force.cancel.checkout"));
				break;
				
			case CONFIRM_WORKFLOW_ACTION:
				break;
		}
	}
	
	/**
	 * setConfirmationText
	 * 
	 * @param text
	 */
	public void setConfirmationText(String text) {
		this.text.setHTML(text);
	}
	
	/**
	 * Language refresh
	 */
	public void langRefresh() {
		setText(Main.i18n("confirm.label"));
		cancelButton.setText(Main.i18n("button.cancel"));
		acceptButton.setText(Main.i18n("button.accept"));
	}
	
	/**
	 * Sets the value to object
	 * 
	 * @param object The object to set
	 */
	public void setValue(Object object) {
		this.object = object;
	}
	
	/**
	 * Get the object value
	 * 
	 * @return The object
	 */
	public Object getValue() {
		return this.object;
	}
	
	/**
	 * Shows de popup
	 */
	public void show(){
		setText(Main.i18n("confirm.label"));
		int left = (Window.getClientWidth()-300)/2;
		int top = (Window.getClientHeight()-125)/2;
		setPopupPosition(left,top);
		super.show();
	}
}