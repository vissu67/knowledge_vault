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

package com.openkm.frontend.client.widget.chat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.contants.service.RPCService;
import com.openkm.frontend.client.service.OKMChatService;
import com.openkm.frontend.client.service.OKMChatServiceAsync;

/**
 * Online popup
 * 
 * @author jllort
 *
 */
public class OnlineUsersPopup extends DialogBox {
	
	private final OKMChatServiceAsync chatService = (OKMChatServiceAsync) GWT.create(OKMChatService.class);
	
	public static final int ACTION_NONE 			= -1;
	public static final int ACTION_NEW_CHAT 		= 0;
	public static final int ACTION_ADD_USER_TO_ROOM = 1;
	
	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	private Button cancel;
	private Button accept;
	private ExtendedFlexTable table;
	private ScrollPanel scrollPanel;
	private int action = ACTION_NONE;
	private String room = "";
	private List<String> usersInChat;
	
	/**
	 * Online users popup
	 */
	public OnlineUsersPopup() {
		
		// Establishes auto-close when click outside
		super(false,true);
		
		setText(Main.i18n("user.online"));
		usersInChat = new ArrayList<String>();
		
		vPanel = new VerticalPanel();
		cancel = new Button(Main.i18n("button.cancel"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		accept = new Button(Main.i18n("button.accept"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				executeAction();
			}
		});
		
		hPanel = new HorizontalPanel();
		hPanel.add(cancel);
		HTML space = new HTML("");
		hPanel.add(space);
		hPanel.add(accept);
		hPanel.setCellWidth(space, "25");
		
		table = new ExtendedFlexTable();
		table.setBorderWidth(0);
		table.setCellPadding(2);
		table.setCellSpacing(0);
		table.setWidth("100%");
		
		scrollPanel = new ScrollPanel(table);
		scrollPanel.setSize("240", "300");
		
		vPanel.add(scrollPanel);
		vPanel.add(hPanel);
		
		vPanel.setCellHeight(scrollPanel, "300");
		vPanel.setCellHeight(hPanel, "25");
		vPanel.setCellHorizontalAlignment(scrollPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellVerticalAlignment(scrollPanel, HasAlignment.ALIGN_MIDDLE);
		vPanel.setCellVerticalAlignment(hPanel, HasAlignment.ALIGN_MIDDLE);
		
		scrollPanel.setStyleName("okm-PanelSelected");
		scrollPanel.addStyleName("okm-Input");
		table.addStyleName("okm-DisableSelect");
		cancel.setStyleName("okm-Button");
		accept.setStyleName("okm-Button");
		
		vPanel.setWidth("250px");
		vPanel.setHeight("350px");

		super.hide();
		setWidget(vPanel);
	}
	
	/**
	 * refreshOnlineUsers
	 */
	public void refreshOnlineUsers() {
		table.removeAllRows(); // Remove all table values
		enableAcceptButton();
		String actualUser = Main.get().workspaceUserProperties.getUser();
		List<String> userList = Main.get().mainPanel.bottomPanel.userInfo.getConnectedUserList();
		for (Iterator<String> it = userList.iterator(); it.hasNext();) {
			int rows = table.getRowCount();
			String user = it.next();
			// Only we add other user than actual UI user connected ( you can not chat yourself )
			// Evaluate case new chat or existing chat
			if (action == ACTION_NEW_CHAT  && !user.equals(actualUser)) {
				table.setHTML(rows, 0, user);
				table.getRowFormatter().setStyleName(rows, "okm-Table-Row");
				setRowWordWarp(rows, 1, false);
			} else if (!user.equals(actualUser) && !usersInChat.contains(user)) {
				table.setHTML(rows, 0, user);
				table.getRowFormatter().setStyleName(rows, "okm-Table-Row");
				setRowWordWarp(rows, 1, false);
			}
		}
	}
	
	/**
	 * Set the WordWarp for all the row cells
	 * 
	 * @param row The row cell
	 * @param columns Number of row columns
	 * @param warp
	 */
	private void setRowWordWarp(int row, int columns, boolean warp) {
		CellFormatter cellFormatter = table.getCellFormatter();
		for (int i=0; i<columns; i++) {
			cellFormatter.setWordWrap(row, i, warp);
		}
	}
	
	/**
	 * Language refresh
	 */
	public void langRefresh() {
		cancel.setHTML(Main.i18n("button.cancel"));
		accept.setHTML(Main.i18n("button.accept"));
		setText(Main.i18n("user.online"));
	}
	
	/**
	 * enableAcceptButton
	 */
	public void enableAcceptButton() {
		accept.setEnabled((table.getSelectedRow()>=0));
	}
	
	/**
	 * createNewChatRoom
	 */
	public void createNewChatRoom() {
		if (table.getSelectedRow()>=0) {
			final String user = table.getHTML(table.getSelectedRow(), 0);
			ServiceDefTarget endPoint = (ServiceDefTarget) chatService;
			endPoint.setServiceEntryPoint(RPCService.ChatService);
			chatService.createNewChatRoom(user, new AsyncCallback<String>() {
				@Override
				public void onSuccess(String result) {
					ChatRoomPopup chatRoomPopup = new ChatRoomPopup(user, result);
					chatRoomPopup.center();
					chatRoomPopup.getPendingMessage(result);
					Main.get().mainPanel.bottomPanel.userInfo.addChatRoom(chatRoomPopup);
				}
				
				@Override
				public void onFailure(Throwable caught) {
					Main.get().showError("GetCreateNewChatRoom", caught);
				}
			});
		}
	}
	
	/**
	 * addUserToRoom
	 */
	public void addUserToRoom() {
		if (table.getSelectedRow()>=0) {
			String user = table.getHTML(table.getSelectedRow(), 0);
			ServiceDefTarget endPoint = (ServiceDefTarget) chatService;
			endPoint.setServiceEntryPoint(RPCService.ChatService);
			chatService.addUserToChatRoom(room, user, new AsyncCallback<Object>() {
				@Override
				public void onSuccess(Object result) {
				}
				
				@Override
				public void onFailure(Throwable caught) {
					Main.get().showError("AddUserToChatRoom", caught);
				}
			});
		}
	}
	
	/**
	 * setAction
	 * 
	 * @param action
	 */
	public void setAction(int action) {
		setAction(action,"");
	}
	
	/**
	 * setAction
	 * 
	 * @param action
	 */
	public void setAction(int action, String room) {
		this.action = action;
		this.room = room;
	}
	
	/**
	 * executeAction
	 */
	public void executeAction() {
		hide();
		switch (action) {
			case ACTION_NEW_CHAT:
				createNewChatRoom();
				break;
			case ACTION_ADD_USER_TO_ROOM:
				addUserToRoom();
				break;
		}
	}
	
	/**
	 * setUsersInChat
	 * 
	 * @param usersInChat
	 */
	public void setUsersInChat(List<String> usersInChat) {
		this.usersInChat = usersInChat;
	}
}