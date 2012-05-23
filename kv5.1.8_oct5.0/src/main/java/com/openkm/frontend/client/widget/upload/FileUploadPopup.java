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

package com.openkm.frontend.client.widget.upload;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.FileToUpload;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.contants.service.ErrorCode;
import com.openkm.frontend.client.contants.ui.UIFileUploadConstants;

/**
 * File Upload
 * 
 * @author jllort
 *
 */
public class FileUploadPopup extends DialogBox {
	
	private Button closeButton;
	private Button addButton;
	private Button sendButton;
	private VerticalPanel vPanel;
	private HorizontalPanel vButtonPanel;
	private FancyFileUpload ffUpload;
	private int popupWidth = 415;
	private int popupHeight = 125;
	private boolean enableAddButton = false;
	private boolean enableImport = true;
	private boolean enableNotifyButton = true;
	
	/**
	 * File upload
	 */
	public FileUploadPopup() {
		super(false,true);
		ffUpload = new FancyFileUpload();
		vPanel = new VerticalPanel();
		vButtonPanel = new HorizontalPanel();
		
		closeButton = new Button(Main.i18n("fileupload.button.close"), new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
					executeCancel();
				}
			}
		);
		
		addButton = new Button(Main.i18n("fileupload.button.add.other.file"), new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
					ffUpload.reset(enableImport, enableNotifyButton);
					addButton.setVisible(false); // Add new file button must be unvisible after clicking
					sendButton.setVisible(true);
					FileToUpload fileToUpload = new FileToUpload();
					fileToUpload.setFileUpload(new FileUpload());
					fileToUpload.setPath((String) Main.get().activeFolderTree.getActualPath());
					fileToUpload.setAction(UIFileUploadConstants.ACTION_INSERT);
					enqueueFileToUpload(new ArrayList<FileToUpload>(Arrays.asList(fileToUpload)));
				}
			}
		);
		addButton.setVisible(false);
		
		sendButton = new Button();
		sendButton.setText(Main.i18n("fileupload.send"));
		sendButton.setStyleName("okm-Button");
		// Set up a click listener on the proceed check box
		sendButton.addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				executeSend();
			}
		});
		
		vPanel.setWidth("415px");
		vPanel.setHeight("100px");
		
		vPanel.add(new HTML("<br>"));
		vPanel.add(ffUpload);
		        
		ffUpload.addChangeHandler(new ChangeHandler(){
			@Override
			public void onChange(ChangeEvent event) {
            	if (ffUpload.getUploadState() == FancyFileUpload.PENDING_STATE ||
            		ffUpload.getUploadState() == FancyFileUpload.UPLOADING_STATE) {
            		closeButton.setEnabled(false);
            		addButton.setVisible(false);
            		sendButton.setVisible(false);
            	   
            	} else if (ffUpload.getUploadState() == FancyFileUpload.EMPTY_STATE ||
            			   ffUpload.getUploadState() == FancyFileUpload.FAILED_STATE ||
            		       ffUpload.getUploadState() == FancyFileUpload.UPLOADED_STATE) {
            		closeButton.setEnabled(true);
            		if (ffUpload.getUploadState() != FancyFileUpload.EMPTY_STATE && enableAddButton) {
            			if (ffUpload.getUploadState() == FancyFileUpload.UPLOADED_STATE) {
            				boolean visible = !ffUpload.isWizard();
            				closeButton.setVisible(visible);
            				sendButton.setVisible(false);
           					addButton.setVisible(visible);
            			} else {
            				addButton.setVisible(true);
            				sendButton.setVisible(false);
            			}
            		} else {
            			// on failed or empty state
            			if (ffUpload.getAction() == UIFileUploadConstants.ACTION_UPDATE && 
            				ffUpload.getUploadState() != FancyFileUpload.EMPTY_STATE) {
            				sendButton.setVisible(false); // checkin case
            			} else {
            				sendButton.setVisible(true);
            			}
            		}
               }
            }
	    }); 
		
		vButtonPanel.add(closeButton);
		vButtonPanel.add(new HTML("&nbsp;&nbsp;"));
		vButtonPanel.add(addButton);
		vButtonPanel.add(new HTML("&nbsp;&nbsp;"));
		vButtonPanel.add(sendButton);
		
		vPanel.add(vButtonPanel);
		vPanel.add(new HTML("<br>"));
		
		vPanel.setCellHorizontalAlignment(ffUpload, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(vButtonPanel, VerticalPanel.ALIGN_CENTER);
		
		closeButton.setStyleName("okm-Button");
		addButton.setStyleName("okm-Button");
		
		setWidget(vPanel);
	}
	
	/**
	 * executeCancel
	 */
	protected void executeCancel() {
		hide();
		addButton.setVisible(false);
		ffUpload.cancel();
	}
	
	/**
	 * executeSend
	 */
	protected void executeSend() {
		if (Main.get().mainPanel.bottomPanel.userInfo.isQuotaExceed()) {
			Main.get().showError("UserQuotaExceed", 
		             			 new OKMException("OKM-"+ErrorCode.ORIGIN_OKMBrowser + ErrorCode.CAUSE_QuotaExceed, ""));
		} else {
			ffUpload.users.setText(ffUpload.notifyPanel.getUsersToNotify());
			ffUpload.roles.setText(ffUpload.notifyPanel.getRolesToNotify());
			if (ffUpload.notifyToUser.getValue() && ffUpload.users.getText().equals("") && ffUpload.roles.getText().equals("")) {
				ffUpload.errorNotify.setVisible(true);
			} else if (ffUpload.getFilename() != null && !ffUpload.getFilename().equals("")) {
				ffUpload.pendingUpload();
			}
		}
	}
	
	
	/**
	 * Language refresh
	 */
	public void langRefresh() {
		closeButton.setHTML(Main.i18n("button.close")); 
		addButton.setHTML(Main.i18n("fileupload.button.add.other.file"));
		sendButton.setText(Main.i18n("fileupload.send"));	
		
		if (ffUpload.getAction() == UIFileUploadConstants.ACTION_INSERT) {
			setText(Main.i18n("fileupload.label.insert"));
		} else {
			setText(Main.i18n("fileupload.label.update"));
		}
		
		ffUpload.langRefresh();
	}
	
	/**
	 * Show file upload popup
	 */
	protected void showPopup(boolean enableAddButton, boolean enableImport, boolean enableNotifyButton) {
		this.enableNotifyButton = enableNotifyButton;
		this.enableAddButton = enableAddButton;
		this.enableImport = enableImport;
		setWidth(""+popupWidth);
		setHeight(""+popupHeight);
		ffUpload.init(); // Inits to correct center position
		center();

		// Allways must initilize htmlForm for tree path initialization
		langRefresh();
		ffUpload.reset(enableImport, enableNotifyButton);
	}
	
	/**
	 * Hide file upload 
	 */
	public void hide() {
		if (ffUpload.getAction() == UIFileUploadConstants.ACTION_UPDATE) {
			if (Main.get().mainPanel.desktop.browser.fileBrowser.table.isDocumentSelected()) {
				GWTDocument doc = Main.get().mainPanel.desktop.browser.fileBrowser.table.getDocument();
				Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.setProperties(doc);
			}
		}
		super.hide();
	}
	
	/**
	 * resetAfterWizardFinished
	 */
	public void resetAfterWizardFinished() {
		ffUpload.refresh();
		closeButton.setVisible(true);
		addButton.setVisible(true);
		super.show();
	}
	
	/**
	 * Sets the path ( document if it's update or directory if it's insert )
	 * 
	 * @param path The document or directory path
	 */
	public void setPath(String path) {
		ffUpload.setPath(path);
	}

	/**
	 * disableErrorNotify
	 */
	public void disableErrorNotify() {
		ffUpload.disableErrorNotify();
	}
	
	/**
	 * enableAdvancedFilter
	 */
	public void enableAdvancedFilter() {
		ffUpload.enableAdvancedFilter();
	}
	
	/**
	 * showDigitalSignature
	 */
	public void showDigitalSignature() {
		ffUpload.showDigitalSignature();
	}
	
	/**
	 * isDigitalSignature
	 * 
	 * @return
	 */
	public boolean isDigitalSignature() {
		return ffUpload.isDigitalSignature();
	}
	
	/**
	 * @param filesToUpload
	 */
	public void enqueueFileToUpload(Collection<FileToUpload> filesToUpload) {
		ffUpload.enqueueFileToUpload(filesToUpload);
	}
}