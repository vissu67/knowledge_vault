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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTTestImap;
import com.openkm.frontend.client.bean.GWTWorkspace;
import com.openkm.frontend.client.contants.service.RPCService;
import com.openkm.frontend.client.service.OKMGeneralService;
import com.openkm.frontend.client.service.OKMGeneralServiceAsync;
import com.openkm.frontend.client.service.OKMWorkspaceService;
import com.openkm.frontend.client.service.OKMWorkspaceServiceAsync;

/**
 * User popup
 * 
 * @author jllort
 *
 */
public class UserPopup extends DialogBox implements ClickHandler {
	
	private final OKMWorkspaceServiceAsync workspaceService = (OKMWorkspaceServiceAsync) GWT.create(OKMWorkspaceService.class);
	private final OKMGeneralServiceAsync generalService = (OKMGeneralServiceAsync) GWT.create(OKMGeneralService.class);
	
	private VerticalPanel vPanel;
	private FlexTable userFlexTable;
	private FlexTable mailFlexTable;
	private HTML userName;
	private HTML userPassword;
	//vissu touch to to add on oct'9th
	private HTML confirmUserPassword;
	
	private HTML userMail;
	private HTML userRoles;
	private HTML imapHost;
	private HTML imapUser;
	private HTML imapPassword;
	private HTML imapFolder;
	private TextBox hostText;
	private TextBox imapUserText;
	private TextBox imapFolderText;
	private PasswordTextBox userPasswordText;
	private PasswordTextBox userPasswordTextVerify;
	private TextBox userMailText;
	private VerticalPanel rolesPanel;
	private PasswordTextBox imapUserPasswordText;
	private Button update;
	private Button cancel;
	private Button delete;
	private Button test;
	private HorizontalPanel hPanel; 
	private HTML passwordError;
	private HTML passwordValidationError;
	private HTML imapPassordError;
	private HTML imapError;
	private HTML imapTestError;
	private HTML imapTestOK;
	private GroupBoxPanel userGroupBoxPanel;
	private GroupBoxPanel mailGroupBoxPanel;
	
	/**
	 * User popup
	 */
	public UserPopup() {
		
		// Establishes auto-close when click outside
		super(false,true);
		int left = (Window.getClientWidth()-400)/2;
		int top = (Window.getClientHeight()-220)/2;
		
		vPanel = new VerticalPanel();
		userFlexTable = new FlexTable();
		mailFlexTable = new FlexTable();
		
		userGroupBoxPanel = new GroupBoxPanel();
		userGroupBoxPanel.setCaption(Main.i18n("user.preferences.user.data"));
		userGroupBoxPanel.add(userFlexTable);
		
		mailGroupBoxPanel = new GroupBoxPanel();
		mailGroupBoxPanel.setCaption(Main.i18n("user.preferences.mail.data"));
		mailGroupBoxPanel.add(mailFlexTable);
		
		userName = new HTML(Main.i18n("user.preferences.user"));
		userPassword = new HTML(Main.i18n("user.preferences.password"));
		
		//vissu touch on oct'9th
		confirmUserPassword = new HTML(Main.i18n("user.preferences.confirmpassword"));

		userMail = new HTML(Main.i18n("user.preferences.mail"));
		userRoles = new HTML(Main.i18n("user.preferences.roles"));
		imapHost = new HTML(Main.i18n("user.preferences.imap.host"));
		imapUser = new HTML(Main.i18n("user.preferences.imap.user"));
		imapPassword = new HTML(Main.i18n("user.preferences.imap.user.password"));
		imapFolder = new HTML(Main.i18n("user.preferences.imap.folder"));
		userPasswordText = new PasswordTextBox();
		userPasswordTextVerify = new PasswordTextBox();
		userMailText = new TextBox();
		rolesPanel = new VerticalPanel();
		imapUserPasswordText = new PasswordTextBox();
		passwordError = new HTML(Main.i18n("user.preferences.password.error"));
		passwordValidationError = new HTML("");
		imapPassordError = new HTML(Main.i18n("user.preferences.imap.password.error.void"));
		imapError = new HTML(Main.i18n("user.preferences.imap.error"));
		imapTestError = new HTML(Main.i18n("user.preferences.imap.test.error"));
		imapTestOK = new HTML(Main.i18n("user.preferences.imap.test.ok"));
		
		passwordError.setVisible(false);
		passwordValidationError.setVisible(false);
		imapPassordError.setVisible(false);
		imapError.setVisible(false);
		imapTestError.setVisible(false);
		imapTestOK.setVisible(false);
		
		hostText = new TextBox();
		imapUserText = new TextBox();
		imapFolderText = new TextBox();
		
		update = new Button(Main.i18n("button.update"), new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				passwordError.setVisible(false);
				passwordValidationError.setVisible(false);
				imapPassordError.setVisible(false);
				imapError.setVisible(false);
				imapTestError.setVisible(false);
				imapTestOK.setVisible(false);
				// Password always must be equals
				if (!userPasswordText.getText().equals(userPasswordTextVerify.getText())) {
					passwordError.setVisible(true);
				// Case creation
				} else if (Main.get().workspaceUserProperties.getWorkspace().getImapID()<0 && imapUserPasswordText.getText().equals("") && 
						  (imapFolderText.getText().length()>0 || imapUserText.getText().length()>0 || hostText.getText().length()>0) ) {
					imapPassordError.setVisible(true);
				// Case update 
			    } else if( (imapUserPasswordText.getText().length()>0 || imapFolderText.getText().length()>0 || imapUserText.getText().length()>0 ||
							hostText.getText().length()>0) && !(imapFolderText.getText().length()>0 && imapUserText.getText().length()>0 
							&& hostText.getText().length()>0) ) {
					imapError.setVisible(true);
				} else {
					final GWTWorkspace workspace = new GWTWorkspace();
					workspace.setUser(Main.get().workspaceUserProperties.getUser());
					workspace.setEmail(userMailText.getText());
					workspace.setImapFolder(imapFolderText.getText());
					workspace.setImapHost(hostText.getText());
					workspace.setImapUser(imapUserText.getText());
					workspace.setImapPassword(imapUserPasswordText.getText());
					workspace.setPassword(userPasswordText.getText());
					workspace.setPassword(userPasswordTextVerify.getText());	//added by vissu on oct'11th
					workspace.setImapID(Main.get().workspaceUserProperties.getWorkspace().getImapID());
					ServiceDefTarget endPoint = (ServiceDefTarget) workspaceService;
					endPoint.setServiceEntryPoint(RPCService.WorkspaceService);
					// First must validate password
					workspaceService.isValidPassword(userPasswordText.getText(), new AsyncCallback<String>() {
						@Override
						public void onSuccess(String result) {
							if (result.equals("")) {
								workspaceService.updateUserWorkspace(workspace, callbackUpdateUserWorkspace);
							} else {
								passwordValidationError.setHTML(result);
								passwordValidationError.setVisible(true);
							}
						}
						
						@Override
						public void onFailure(Throwable caught) {
							Main.get().showError("callbackIsValidPassword", caught);
						}
					});
				}
			}			
		});
		
		cancel = new Button(Main.i18n("button.cancel"), new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}			
		});
		
		test = new Button(Main.i18n("button.test"), new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				imapTestError.setVisible(false);
				imapTestOK.setVisible(false);
				test.setEnabled(false);
				ServiceDefTarget endPoint = (ServiceDefTarget) generalService;
				endPoint.setServiceEntryPoint(RPCService.GeneralService);
				generalService.testImapConnection(hostText.getText(), imapUserText.getText(), imapUserPasswordText.getText(), imapFolderText.getText(), new AsyncCallback<GWTTestImap>() {					
					@Override
					public void onSuccess(GWTTestImap result) {
						if (!result.isError()) {
							imapTestError.setVisible(false);
							imapTestOK.setVisible(true);
						} else {
							imapTestError.setHTML(Main.i18n("user.preferences.imap.test.error") + "<br>" + result.getErrorMsg());
							imapTestError.setVisible(true);
							imapTestOK.setVisible(false);
						}
						test.setEnabled(true);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						imapTestError.setVisible(false);
						imapTestOK.setVisible(false);
						test.setEnabled(true);
						Main.get().showError("testImapConnection", caught);
					}
				});
			}			
		});
		
		delete = new Button(Main.i18n("button.delete"), new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				int Id = Main.get().workspaceUserProperties.getWorkspace().getImapID();
				if (Id>=0) {
					ServiceDefTarget endPoint = (ServiceDefTarget) workspaceService;
					endPoint.setServiceEntryPoint(RPCService.WorkspaceService);
					workspaceService.deleteMailAccount(Id, callbackDeleteMailAccount);
				}
			}			
		});
		
		hPanel = new HorizontalPanel();
		hPanel.add(update);
		hPanel.add(new HTML("&nbsp;&nbsp;"));
		hPanel.add(cancel);
		
		userFlexTable.setCellPadding(0);
		userFlexTable.setCellSpacing(2);
		userFlexTable.setWidth("455");
		
		//commented by vissu on oct'11th
				/*
				userFlexTable.setWidget(0, 0, userName);
				userFlexTable.setWidget(1, 0, userPassword);
				userFlexTable.setWidget(2, 0, userMail);
				userFlexTable.setWidget(3, 0, userRoles);	*/
		//added by vissu on oct'11th
		userFlexTable.setWidget(0, 0, userName);
		userFlexTable.setWidget(1, 0, userPassword);
		userFlexTable.setWidget(2, 0, confirmUserPassword);	
		userFlexTable.setWidget(3, 0, userMail);
		userFlexTable.setWidget(4, 0, userRoles);
		
		userFlexTable.setWidget(1, 1, userPasswordText);
		//commented by vissu on oct'11th
		//userFlexTable.setWidget(1, 2, userPasswordTextVerify);
		//added by vissu on oct'11th
		userFlexTable.setWidget(2, 1, userPasswordTextVerify);
		userFlexTable.setWidget(3, 1, userMailText);
		userFlexTable.setWidget(4, 1, rolesPanel);
		
		userFlexTable.getFlexCellFormatter().setVerticalAlignment(3, 0, HasAlignment.ALIGN_TOP);
		userFlexTable.getFlexCellFormatter().setColSpan(2, 1, 2);
		userFlexTable.getFlexCellFormatter().setColSpan(3, 1, 2);
		
		mailFlexTable.setCellPadding(0);
		mailFlexTable.setCellSpacing(2);
		mailFlexTable.setWidth("455");
		
		mailFlexTable.setWidget(1, 0, imapHost);
		mailFlexTable.setWidget(2, 0, imapUser);
		mailFlexTable.setWidget(3, 0, imapPassword);
		mailFlexTable.setWidget(4, 0, imapFolder);
		
		mailFlexTable.setWidget(1, 1, hostText);
		mailFlexTable.setWidget(2, 1, imapUserText);
		mailFlexTable.setWidget(3, 1, imapUserPasswordText);
		mailFlexTable.setWidget(4, 1, imapFolderText);
		mailFlexTable.setWidget(5, 0, new HTML("&nbsp;"));
		mailFlexTable.setWidget(5, 1, delete);
		mailFlexTable.setWidget(5, 2, test);
		
		mailFlexTable.getFlexCellFormatter().setColSpan(1, 1, 2);
		mailFlexTable.getFlexCellFormatter().setAlignment(5, 1, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		mailFlexTable.getFlexCellFormatter().setAlignment(5, 2, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
		
		userMailText.setWidth("275");
		hostText.setWidth("275");
		rolesPanel.setWidth("275");
		userGroupBoxPanel.setWidth("460px");
		mailGroupBoxPanel.setWidth("460px");
		
		vPanel.setWidth("470px");
		vPanel.setHeight("195px");
		
		vPanel.add(new HTML("<br>"));
		vPanel.add(userGroupBoxPanel);
		vPanel.add(new HTML("<br>"));
		//vissu touch commented below line to remove mail box on oct'9th
		//vPanel.add(mailGroupBoxPanel);
		vPanel.add(passwordError);
		vPanel.add(passwordValidationError);
		vPanel.add(imapPassordError);
		vPanel.add(imapError);
		vPanel.add(imapTestError);
		vPanel.add(imapTestOK);
		vPanel.add(new HTML("<br>"));
		vPanel.add(hPanel);
		vPanel.add(new HTML("<br>"));
		
		vPanel.setCellHorizontalAlignment(userGroupBoxPanel, HasAlignment.ALIGN_CENTER);
		//vissu touch commented below line to remove mail box on oct'9th
		//vPanel.setCellHorizontalAlignment(mailGroupBoxPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(passwordError, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(passwordValidationError, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(imapPassordError, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(imapError, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(imapTestError, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(imapTestOK, HasAlignment.ALIGN_CENTER);
		
		userName.addStyleName("okm-NoWrap");
		userPassword.addStyleName("okm-NoWrap");
		confirmUserPassword.addStyleName("okm-NoWrap"); //added by vissu for zohoapi
		userMail.addStyleName("okm-NoWrap");
		imapHost.addStyleName("okm-NoWrap");
		imapUser.addStyleName("okm-NoWrap");
		imapPassword.addStyleName("okm-NoWrap");
		imapFolder.addStyleName("okm-NoWrap");
		userPasswordText.setStyleName("okm-Input");
		userPasswordTextVerify.setStyleName("okm-Input");
		userMailText.setStyleName("okm-Input");
		hostText.setStyleName("okm-Input");
		imapUserText.setStyleName("okm-Input");
		imapUserPasswordText.setStyleName("okm-Input");
		imapFolderText.setStyleName("okm-Input");
		passwordError.setStyleName("okm-Input-Error");
		passwordValidationError.setStyleName("okm-Input-Error");
		imapPassordError.setStyleName("okm-Input-Error");
		imapError.setStyleName("okm-Input-Error");
		imapTestError.setStyleName("okm-Input-Error");
		imapTestOK.setStyleName("okm-Input-Ok");
		update.setStyleName("okm-Button");
		cancel.setStyleName("okm-Button");
		delete.setStyleName("okm-Button");
		test.setStyleName("okm-Button");
		
		setPopupPosition(left,top);

		super.hide();
		setWidget(vPanel);
	}
	
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.ClickListener#onClick(com.google.gwt.user.client.ui.Widget)
	 */
	public void onClick(ClickEvent event) {
		super.hide();
	}
	
	/**
	 * Language refresh
	 */
	public void langRefresh() {
		setText(Main.i18n("user.preferences.label"));
		userName.setHTML(Main.i18n("user.preferences.user"));
		userPassword.setHTML(Main.i18n("user.preferences.password"));
		confirmUserPassword.setHTML(Main.i18n("user.preferences.confirmpassword")); //added by vissu on oct'11th
		userMail.setHTML(Main.i18n("user.preferences.mail"));
		imapHost.setHTML(Main.i18n("user.preferences.imap.host"));
		imapUser.setHTML(Main.i18n("user.preferences.imap.user"));
		imapPassword.setHTML(Main.i18n("user.preferences.imap.user.password"));
		imapFolder.setHTML(Main.i18n("user.preferences.imap.folder"));
		passwordError.setHTML(Main.i18n("user.preferences.password.error"));
		passwordValidationError.setHTML("");
		imapPassordError.setHTML(Main.i18n("user.preferences.imap.password.error.void"));
		imapError.setHTML(Main.i18n("user.preferences.imap.error"));
		imapTestError.setHTML(Main.i18n("user.preferences.imap.error"));
		imapTestOK.setHTML(Main.i18n("user.preferences.imap.ok"));
		update.setText(Main.i18n("button.update"));
		cancel.setText(Main.i18n("button.cancel"));
		delete.setText(Main.i18n("button.delete"));
		test.setText(Main.i18n("button.test"));
		userGroupBoxPanel.setCaption(Main.i18n("user.preferences.user.data"));
		mailGroupBoxPanel.setCaption(Main.i18n("user.preferences.mail.data"));
	}
	
	/**
	 * Reset values
	 */
	private void reset() {
		userPasswordText.setText("");
		userPasswordTextVerify.setText("");
		imapUserPasswordText.setText("");
	}
	
	/**
	 * Show the popup user preferences
	 * 
	 */
	public void show() {
		setText(Main.i18n("user.preferences.label"));
		GWTWorkspace workspace = Main.get().workspaceUserProperties.getWorkspace();
		
		reset();
		hostText.setText(workspace.getImapHost());
		imapUserText.setText(workspace.getImapUser());
		imapFolderText.setText(workspace.getImapFolder());
		userFlexTable.setText(0, 1, workspace.getUser());
		userFlexTable.getFlexCellFormatter().setColSpan(0, 1, 2);
		userMailText.setText(workspace.getEmail());
		
		for (Iterator<String> it = workspace.getRoleList().iterator(); it.hasNext();) {
			rolesPanel.add(new HTML(it.next()));
		}
		
		passwordError.setVisible(false);
		passwordValidationError.setVisible(false);
		imapPassordError.setVisible(false);
		imapError.setVisible(false);
		imapTestError.setVisible(false);
		imapTestOK.setVisible(false);
		
		if (workspace.isChangePassword()) {
			userMail.setVisible(true);
		    userMailText.setVisible(true);
		    userPassword.setVisible(true);
		    confirmUserPassword.setVisible(true); //added by vissu
			userPasswordText.setVisible(true);
			userPasswordTextVerify.setVisible(true);
		} else {
			userMail.setVisible(true);
			userMailText.setVisible(false);
			userPassword.setVisible(false);
			confirmUserPassword.setVisible(false);
			userPasswordText.setVisible(false);
			userPasswordTextVerify.setVisible(false);
		}
		
		// Enables delete button only if there's some imap server configured to be removed
		if (workspace.getImapID()>=0) {
			delete.setVisible(true);
		} else {
			delete.setVisible(false);
		}
		
		super.show();
	}
	
	/**
	 * Call back update user workspace data 
	 */
	final AsyncCallback<Object> callbackUpdateUserWorkspace = new AsyncCallback<Object>() {
		public void onSuccess(Object result){
			Main.get().workspaceUserProperties.refreshUserWorkspace(); // Refreshing workspace saved values
			hide();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("callbackUpdateUserWorkspace", caught);
		}
	};
	
	/**
	 * Call back delete mail account
	 */
	final AsyncCallback<Object> callbackDeleteMailAccount = new AsyncCallback<Object>() {
		public void onSuccess(Object result){
			Main.get().workspaceUserProperties.getUserWorkspace(); // Refreshing workspace saved values
			hostText.setText("");
			imapUserText.setText("");
			imapUserPasswordText.setText("");
			imapFolderText.setText("");
			delete.setVisible(false);
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("callbackDeleteMailAccount", caught);
		}
	};
	
}