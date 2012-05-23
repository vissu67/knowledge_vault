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

package com.openkm.frontend.client.widget.notify;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.contants.service.RPCService;
import com.openkm.frontend.client.service.OKMNotifyService;
import com.openkm.frontend.client.service.OKMNotifyServiceAsync;

/**
 * NotifyPopup
 * 
 * @author jllort
 *
 */
public class NotifyPopup extends DialogBox  {
	
	private static final int NONE 					= -1;
	public static final int NOTIFY_WITH_LINK 		= 0;
	public static final int NOTIFY_WITH_ATTACHMENT = 1;
	
	private final OKMNotifyServiceAsync notifyService = (OKMNotifyServiceAsync) GWT.create(OKMNotifyService.class);
	
	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	private Button closeButton;
	private Button sendButton;
	private TextArea message;
	private ScrollPanel messageScroll;
	private NotifyPanel notifyPanel;
	private HTML commentTXT;
	private HTML errorNotify;
	private String users;
	private String roles;
	private GWTDocument doc;
	private int type = NONE;
	
	public NotifyPopup() {
		// Establishes auto-close when click outside
		super(false,true);
		
		setText(Main.i18n("notify.label"));
		users = "";
		roles = "";
		doc = new GWTDocument();
		
		vPanel = new VerticalPanel();
		hPanel = new HorizontalPanel();
		notifyPanel = new NotifyPanel();
		message = new TextArea();
		
		errorNotify = new HTML(Main.i18n("fileupload.label.must.select.users"));
		errorNotify.setWidth("365");
		errorNotify.setVisible(false);
		errorNotify.setStyleName("fancyfileupload-failed");
		
		commentTXT = new HTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + Main.i18n("fileupload.label.notify.comment"));
		
		closeButton = new Button(Main.i18n("fileupload.button.close"), new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				hide();
				reset(NONE);
			}
		});
		
		sendButton = new Button(Main.i18n("fileupload.send"), new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				// Only sends if there's some user selected
				users = notifyPanel.getUsersToNotify();
				roles = notifyPanel.getRolesToNotify();
				if (!users.equals("") || !roles.equals("")) {
					errorNotify.setVisible(false);
					sendLinkNotification();
					hide();
					reset(NONE);
				} else {
					errorNotify.setVisible(true);
				}
			}
		});
		
		hPanel.add(closeButton);
		HTML space = new HTML("");
		hPanel.add(space);
		hPanel.add(sendButton);
		
		hPanel.setCellWidth(space, "40");
		
		message.setSize("375","60");
		message.setStyleName("okm-TextArea");
		// TODO This is a workaround for a Firefox 2 bug
		// http://code.google.com/p/google-web-toolkit/issues/detail?id=891
		messageScroll = new ScrollPanel(message);
		messageScroll.setAlwaysShowScrollBars(false);	
		
		vPanel.add(new HTML("<br>"));
		vPanel.add(commentTXT);
		vPanel.add(messageScroll);
		vPanel.add(errorNotify);
		vPanel.add(new HTML("<br>"));
		vPanel.add(notifyPanel);
		vPanel.add(new HTML("<br>"));
		vPanel.add(hPanel);
		vPanel.add(new HTML("<br>"));
		
		vPanel.setCellHorizontalAlignment(errorNotify, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(messageScroll, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(notifyPanel, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, VerticalPanel.ALIGN_CENTER);
		
		vPanel.setWidth("100%");
		
		closeButton.setStyleName("okm-Button");
		sendButton.setStyleName("okm-Button");
		
		commentTXT.addStyleName("okm-DisableSelect");
		notifyPanel.addStyleName("okm-DisableSelect");
		
		setWidget(vPanel);
	}
	
	/**
	 * langRefresh 
	 * 
	 * Refreshing lang
	 */
	public void langRefresh(){
		switch(type) {
			case NOTIFY_WITH_LINK:
				setText(Main.i18n("notify.label"));
				break;
			case NOTIFY_WITH_ATTACHMENT:
				setText(Main.i18n("notify.label.attachment"));
				break;
		}
		closeButton.setHTML(Main.i18n("button.close")); 
		sendButton.setHTML(Main.i18n("fileupload.send"));
		commentTXT = new HTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + Main.i18n("fileupload.label.notify.comment"));
		errorNotify.setHTML(Main.i18n("fileupload.label.must.select.users"));
		notifyPanel.langRefresh();
	}
	
	/**
	 * executeSendDocument
	 * 
	 * @param type
	 */
	public void executeSendDocument(int type) {
		if (Main.get().mainPanel.desktop.browser.fileBrowser.isDocumentSelected()) {
			reset(type);
			doc = Main.get().mainPanel.desktop.browser.fileBrowser.getDocument();
			super.center();
		} 
	}
	
	/**
	 * Call back send link notification
	 */
	final AsyncCallback<Object> callbackNotify = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("notify", caught);
		}
	};	

	/**
	 * Sens the link notification
	 */
	private void sendLinkNotification() {
		ServiceDefTarget endPoint = (ServiceDefTarget) notifyService;
		endPoint.setServiceEntryPoint(RPCService.NotifyService);	
		switch(type) {
			case NOTIFY_WITH_LINK:
				notifyService.notify(doc.getPath(), users, roles, message.getText(), false, callbackNotify);
				break;
			case NOTIFY_WITH_ATTACHMENT:
				notifyService.notify(doc.getPath(), users, roles, message.getText(), true, callbackNotify);
				break;
		}
	}
	
	/**
	 * Reste values
	 */
	private void reset(int type) {
		this.type = type;
		switch(type) {
			case NOTIFY_WITH_LINK:
				setText(Main.i18n("notify.label"));
				break;
			case NOTIFY_WITH_ATTACHMENT:
				setText(Main.i18n("notify.label.attachment"));
				break;
		}
		users = "";
		roles = "";
		message.setText("");
		notifyPanel.reset();
		notifyPanel.getAll();
		doc = new GWTDocument();
		errorNotify.setVisible(false);
	}
	
	/**
	 * enableAdvancedFilter
	 */
	public void enableAdvancedFilter() {
		notifyPanel.enableAdvancedFilter();
	}
	
	/**
	 * disableErrorNotify
	 */
	public void disableErrorNotify() {
		errorNotify.setVisible(false);
	}
}
