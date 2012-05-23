package com.openkm.frontend.client.widget;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.contants.service.RPCService;
import com.openkm.frontend.client.contants.ui.UIDockPanelConstants;
import com.openkm.frontend.client.extension.widget.userinfo.UserInfoExtension;
import com.openkm.frontend.client.service.OKMChatService;
import com.openkm.frontend.client.service.OKMChatServiceAsync;
import com.openkm.frontend.client.util.OKMBundleResources;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.chat.ChatRoomDialogBox;
import com.openkm.frontend.client.widget.chat.ChatRoomPopup;
import com.openkm.frontend.client.widget.chat.OnlineUsersPopup;

public class UserInfo extends Composite {
	
	private final OKMChatServiceAsync chatService = (OKMChatServiceAsync) GWT.create(OKMChatService.class);
	
	private static final int USERS_IN_ROOM_REFRESHING_TIME = 1000;
	private static final int NEW_ROOM_REFRESHING_TIME = 200;
	
	private HorizontalPanel panel;
	private Image advertisement;
	private HTML user;
	private String msg = "";
	private Image img;
	private HTML userRepositorySize;
	private Image imgRepositorySize;
	private HTML lockedDocuments;
	private Image imgLockedDocuments;
	private HTML checkoutDocuments;
	private Image imgCheckoutDocuments;
	
	//added by vissu on feb19 for zohoapi
	private HTML zohoDocuments;
	private Image imgZohoDocuments;
	
	private HTML subscriptions;
	private Image imgSubscriptions;
	private HTML newDocuments;
	private Image imgNewsDocuments;
	private HTML newWorkflowTasks;	
	private Image imgWorkflowTasks;
	private HTML newWorkflowPooledTasks;
	private Image imgWorkflowPooledTasks;
	private Image imgChat;
	private Image imgNewChatRoom;
	private Image imgChatSeparator;
	private boolean chatConnected = false;
	private HTML usersConnected;
	private List<String> connectUsersList;
	private List<ChatRoomDialogBox> chatRoomList;
	private Image imgUserQuota;
	private boolean userQuota = false;
	private long quotaLimit = 0;
	private boolean quotaExceeded = false;
	private HTML quotaUsed;
	private int percent = 0;
	private List<UserInfoExtension> widgetExtensionList;
	
	/**
	 * UserInfo
	 */
	public UserInfo() {
		widgetExtensionList = new ArrayList<UserInfoExtension>();
		connectUsersList = new ArrayList<String>();
		chatRoomList = new ArrayList<ChatRoomDialogBox>();
		img = new Image(OKMBundleResources.INSTANCE.openkmConnected());
		panel = new HorizontalPanel();
		panel.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		panel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		user = new HTML("");
		userRepositorySize = new HTML("");
		usersConnected = new HTML("");
		lockedDocuments = new HTML("");
		checkoutDocuments = new HTML("");
		
		zohoDocuments = new HTML("");		//added by vissu on feb19 for zohoapi
		
		subscriptions = new HTML("");
		newDocuments = new HTML("");
		newWorkflowTasks = new HTML("");
		newWorkflowPooledTasks = new HTML("");
		quotaUsed = new HTML("");
		quotaUsed.setVisible(false);
		imgRepositorySize = new Image(OKMBundleResources.INSTANCE.repositorySize());
		imgUserQuota = new Image(OKMBundleResources.INSTANCE.quota1());
		imgChat = new Image(OKMBundleResources.INSTANCE.chatDisconnected());
		imgChatSeparator = new Image(OKMBundleResources.INSTANCE.separator());
		imgNewChatRoom = new Image(OKMBundleResources.INSTANCE.newChatRoom());
		imgLockedDocuments = new Image(OKMBundleResources.INSTANCE.lock());
		imgCheckoutDocuments = new Image(OKMBundleResources.INSTANCE.checkout());
		
		imgZohoDocuments = new Image(OKMBundleResources.INSTANCE.zoho());		//added by vissu on feb19 for zohoapi
		
		imgSubscriptions = new Image(OKMBundleResources.INSTANCE.subscribed());
		imgNewsDocuments = new Image(OKMBundleResources.INSTANCE.news());
		imgWorkflowTasks = new Image(OKMBundleResources.INSTANCE.workflowTasks());
		imgWorkflowPooledTasks = new Image(OKMBundleResources.INSTANCE.workflowPooledTasks());
		imgRepositorySize.setVisible(false);
		imgUserQuota.setVisible(false);
		imgChat.setVisible(false);
		imgChatSeparator.setVisible(false);
		usersConnected.setVisible(false);
		imgNewChatRoom.setVisible(false);
		imgLockedDocuments.setVisible(false);
		imgCheckoutDocuments.setVisible(false);
		
		imgZohoDocuments.setVisible(false);		//added by vissu on feb19 for zohoapi
		
		imgSubscriptions.setVisible(false);
		imgNewsDocuments.setVisible(false);
		imgWorkflowTasks.setVisible(false);
		imgWorkflowPooledTasks.setVisible(false);
		imgChat.setTitle(Main.i18n("user.info.chat.connect"));
		imgUserQuota.setTitle(Main.i18n("user.info.user.quota"));
		imgNewChatRoom.setTitle(Main.i18n("user.info.chat.new.room"));
		imgLockedDocuments.setTitle(Main.i18n("user.info.locked.actual"));
		imgCheckoutDocuments.setTitle(Main.i18n("user.info.checkout.actual"));
		
		imgZohoDocuments.setTitle(Main.i18n("user.info.zoho.actual"));		//added by vissu on feb19 for zohoapi
		
		imgSubscriptions.setTitle(Main.i18n("user.info.subscription.actual"));
		imgNewsDocuments.setTitle(Main.i18n("user.info.news.new"));
		imgWorkflowTasks.setTitle(Main.i18n("user.info.workflow.pending.tasks"));
		imgWorkflowPooledTasks.setTitle(Main.i18n("user.info.workflow.pending.pooled.tasks"));
		
		imgLockedDocuments.addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				Main.get().mainPanel.topPanel.tabWorkspace.changeSelectedTab(UIDockPanelConstants.DASHBOARD);
				Main.get().mainPanel.dashboard.horizontalToolBar.showUserView();
			}
		});
		
		imgCheckoutDocuments.addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				Main.get().mainPanel.topPanel.tabWorkspace.changeSelectedTab(UIDockPanelConstants.DASHBOARD);
				Main.get().mainPanel.dashboard.horizontalToolBar.showUserView();
			}
		});
		
		//added by vissu on feb19 for zohoapi
		imgZohoDocuments.addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				
			}
		});
		
		imgSubscriptions.addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				Main.get().mainPanel.topPanel.tabWorkspace.changeSelectedTab(UIDockPanelConstants.DASHBOARD);
				Main.get().mainPanel.dashboard.horizontalToolBar.showUserView();
			}
		});
		
		imgNewsDocuments.addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				Main.get().mainPanel.topPanel.tabWorkspace.changeSelectedTab(UIDockPanelConstants.DASHBOARD);
				Main.get().mainPanel.dashboard.horizontalToolBar.showNewsView();
			}
		});
		
		imgWorkflowTasks.addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				Main.get().mainPanel.topPanel.tabWorkspace.changeSelectedTab(UIDockPanelConstants.DASHBOARD);
				Main.get().mainPanel.dashboard.horizontalToolBar.showWorkflowView();
			}
		});
		
		imgWorkflowPooledTasks.addClickHandler(new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				Main.get().mainPanel.topPanel.tabWorkspace.changeSelectedTab(UIDockPanelConstants.DASHBOARD);
				Main.get().mainPanel.dashboard.horizontalToolBar.showWorkflowView();
			}
		});
		
		imgChat.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!chatConnected) {
					loginChat();
				} else {
					chatConnected = false; // Trying disable other RPC calls
					logoutChat();
				}
			}
		});
		
		imgNewChatRoom.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Main.get().onlineUsersPopup.setAction(OnlineUsersPopup.ACTION_NEW_CHAT);
				Main.get().onlineUsersPopup.center();
				Main.get().onlineUsersPopup.refreshOnlineUsers();
			}
		});
		
		advertisement = new Image(OKMBundleResources.INSTANCE.warning());
		advertisement.setVisible(false);
		
		advertisement.addClickHandler( new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				Main.get().msgPopup.show("openkm.update.available", msg, 400, 200);
			}
		});
		
		panel.add(advertisement);
		panel.add(new HTML("&nbsp;"));
		panel.add(img);
		panel.add(new HTML("&nbsp;"));
		panel.add(user);
		panel.add(new Image(OKMBundleResources.INSTANCE.separator()));

		//commented below by vissu on nov 20 to hide vault size

		/*panel.add(new HTML("&nbsp;"));
		panel.add(imgRepositorySize);
		panel.add(new HTML("&nbsp;"));
		panel.add(userRepositorySize);
		panel.add(new HTML("&nbsp;"));
		
		panel.add(imgUserQuota);
		panel.add(new HTML("&nbsp;"));
		panel.add(quotaUsed);
		panel.add(new HTML("&nbsp;"));
		panel.add(new Image(OKMBundleResources.INSTANCE.separator()));*/
		
		panel.add(new HTML("&nbsp;"));
		panel.add(imgChat);
		panel.add(new HTML("&nbsp;"));
		panel.add(imgNewChatRoom);
		panel.add(new HTML("&nbsp;"));
		panel.add(usersConnected);
		panel.add(new HTML("&nbsp;"));
		panel.add(imgChatSeparator);
		panel.add(new HTML("&nbsp;"));
		panel.add(imgLockedDocuments);
		panel.add(new HTML("&nbsp;"));
		panel.add(lockedDocuments);
		panel.add(new HTML("&nbsp;"));
		panel.add(imgCheckoutDocuments);
		panel.add(new HTML("&nbsp;"));
		panel.add(checkoutDocuments);
		panel.add(new HTML("&nbsp;"));
		
		//added by vissu on feb19 for zohoapi
		panel.add(imgZohoDocuments);
		panel.add(new HTML("&nbsp;"));
		panel.add(zohoDocuments);
		panel.add(new HTML("&nbsp;"));
		
		panel.add(imgSubscriptions);
		panel.add(new HTML("&nbsp;"));
		panel.add(subscriptions);
		panel.add(new HTML("&nbsp;"));
		panel.add(imgNewsDocuments);
		panel.add(newDocuments);
		panel.add(new HTML("&nbsp;"));
		panel.add(imgWorkflowTasks);
		panel.add(newWorkflowTasks);
		panel.add(new HTML("&nbsp;"));
		panel.add(imgWorkflowPooledTasks);
		panel.add(newWorkflowPooledTasks);
		panel.add(new HTML("&nbsp;"));
		
		imgLockedDocuments.setStyleName("okm-Hyperlink");
		imgCheckoutDocuments.setStyleName("okm-Hyperlink");
		
		imgZohoDocuments.setStyleName("okm-Hyperlink");		//added by vissu on feb19 for zohoapi
		
		imgSubscriptions.setStyleName("okm-Hyperlink");
		imgNewsDocuments.setStyleName("okm-Hyperlink");
		imgWorkflowTasks.setStyleName("okm-Hyperlink");
		imgWorkflowPooledTasks.setStyleName("okm-Hyperlink");
		imgChat.setStyleName("okm-Hyperlink");
		imgNewChatRoom.setStyleName("okm-Hyperlink");
		
		initWidget(panel);
	}
	
	/**
	 * Sets the user value 
	 * 
	 * @param user The user value
	 */
	public void setUser(String user, boolean isAdmin) {
		this.user.setHTML("&nbsp;"+Main.i18n("general.connected")+" "+user+ "&nbsp;");
		//below condition commnted by vissu on 27oct
		//if (isAdmin) {
			this.user.addStyleName("okm-Input-System");
	//	} 
	}
	
	/**
	 * Sets the repository size
	 * 
	 * @param size
	 */
	public void setUserRepositorySize(double size) {
		imgRepositorySize.setVisible(true);
		userRepositorySize.setHTML("&nbsp;"+Util.formatSize(size)+ "&nbsp;");
		if (userQuota) {
			if (size>0) {
				if (size>=quotaLimit) {
					quotaExceeded = true;
					percent=100;
					imgUserQuota.setResource(OKMBundleResources.INSTANCE.quota6());
				} else {
					// Calculating %
					percent = new Double((size*100)/quotaLimit).intValue();
					if (percent==0) {
						percent=1;
					} else if (percent>100) {
						percent=100;
					}
					if (percent<=20) {
						imgUserQuota.setResource(OKMBundleResources.INSTANCE.quota1());
					} else if (percent<=40) {
						imgUserQuota.setResource(OKMBundleResources.INSTANCE.quota2());
					} else if (percent<=60) {
						imgUserQuota.setResource(OKMBundleResources.INSTANCE.quota3());
					} else if (percent<=80) {
						imgUserQuota.setResource(OKMBundleResources.INSTANCE.quota4());
					} else {
						imgUserQuota.setResource(OKMBundleResources.INSTANCE.quota5());
					}
				}
			} else {
				quotaExceeded = false;
				imgUserQuota.setResource(OKMBundleResources.INSTANCE.quota1());
			}
			quotaUsed.setHTML(percent + "%");
		}
	}
	
	/**
	 * Sets the locked documents 
	 * 
	 * @param value
	 */
	public void setLockedDocuments(int value) {
		imgLockedDocuments.setVisible(true);
		lockedDocuments.setHTML("&nbsp;"+value+ "&nbsp;");
	}
	
	/**e
	 * Sets the checkout documents
	 * 
	 * @param value
	 */
	public void setCheckoutDocuments(int value) {
		imgCheckoutDocuments.setVisible(true);
		checkoutDocuments.setHTML("&nbsp;"+value+ "&nbsp;");
	}
	
	//added by vissu on feb19 for zohoapi
	public void setZohoDocuments(int value) {
		imgZohoDocuments.setVisible(true);
		zohoDocuments.setHTML("&nbsp;"+value+ "&nbsp;");
	}
	
	/**
	 * Sets the subscriptions documents and folders 
	 * 
	 * @param value
	 */
	public void setSubscriptions(int value) {
		imgSubscriptions.setVisible(true);
		subscriptions.setHTML("&nbsp;"+value+ "&nbsp;");
	}
	
	/**
	 * Sets the news documents
	 * 
	 * @param value
	 */
	public void setNewsDocuments(int value) {
		//vissu touch to hide news on oct'9th
	/*	imgNewsDocuments.setVisible(true);
		newDocuments.setHTML("&nbsp;"+value+ "&nbsp;");
		if (value>0) {
			imgNewsDocuments.setResource(OKMBundleResources.INSTANCE.newsAlert());
		} else {
			imgNewsDocuments.setResource(OKMBundleResources.INSTANCE.news());
		}	*/
	}
	
	/**
	 * Sets the news workflows
	 * 
	 * @param value
	 */
	public void setNewsWorkflows(int value) {
		//vissu touch to hide wrokflow on oct'9th
	/*	imgWorkflowTasks.setVisible(true);
		newWorkflowTasks.setHTML("&nbsp;" + value + "&nbsp;");
		
		if (value > 0) {
			imgWorkflowTasks.setResource(OKMBundleResources.INSTANCE.workflowTasksAlert());
		} else {
			imgWorkflowTasks.setResource(OKMBundleResources.INSTANCE.workflowTasks());
		}	*/
	}
	
	/**
	 * Sets the pooled task instances
	 * 
	 * @param value
	 */
	public void setPooledTaskInstances(int value) {
		//vissu touch to hide WorkflowPooledTasks on oct'11th
	/*	imgWorkflowPooledTasks.setVisible(true);
		newWorkflowPooledTasks.setHTML("&nbsp;" + value + "&nbsp;");
		
		if (value > 0) {
			imgWorkflowPooledTasks.setResource(OKMBundleResources.INSTANCE.workflowPooledTasksAlert());
		} else {
			imgWorkflowPooledTasks.setResource(OKMBundleResources.INSTANCE.workflowPooledTasks());
		}	*/
	}
	
	/**
	 * Sets the msg value 
	 * 
	 * @param msg The msg value
	 */
	public void setUpdateMessage(String msg) {
		if (msg!=null && !msg.equals("")) {
			advertisement.setVisible(true);
			this.msg = msg;
		}
	}

	/**
	 * langRefresh
	 */
	public void langRefresh() {
		user.setHTML("&nbsp;"+ Main.i18n("general.connected")+" "+Main.get().workspaceUserProperties.getUser() + "&nbsp;");
		if (chatConnected) {
			imgChat.setTitle(Main.i18n("user.info.chat.disconnect"));
			usersConnected.setHTML(connectUsersList.size() + "");
		} else {
			imgChat.setTitle(Main.i18n("user.info.chat.connect"));
			usersConnected.setHTML("");
		}
		imgUserQuota.setTitle(Main.i18n("user.info.user.quota"));
		imgNewChatRoom.setTitle(Main.i18n("user.info.chat.new.room"));
		imgLockedDocuments.setTitle(Main.i18n("user.info.locked.actual"));
		imgCheckoutDocuments.setTitle(Main.i18n("user.info.checkout.actual"));
		
		imgZohoDocuments.setTitle(Main.i18n("user.info.zoho.actual"));		//added by vissu on feb19 for zohoapi
		
		imgSubscriptions.setTitle(Main.i18n("user.info.subscription.actual"));
		imgNewsDocuments.setTitle(Main.i18n("user.info.news.new"));
		imgWorkflowTasks.setTitle(Main.i18n("user.info.workflow.pending.tasks"));
		imgWorkflowPooledTasks.setTitle(Main.i18n("user.info.workflow.pending.pooled.tasks"));
		quotaUsed.setHTML(percent + "%");
		
		// Resfreshing actual chatrooms
		for (Iterator<ChatRoomDialogBox> it = chatRoomList.iterator(); it.hasNext();) {
			it.next().langRefresh();
		}
	}
	
	/**
	 * refreshConnectedUsers
	 */
	private void refreshConnectedUsers() {
		if (chatConnected) {
			ServiceDefTarget endPoint = (ServiceDefTarget) chatService;
			endPoint.setServiceEntryPoint(RPCService.ChatService);
			chatService.getLoggedUsers(new AsyncCallback<List<String>>() {
				@Override
				public void onSuccess(List<String> result) {
					connectUsersList = result;
					usersConnected.setHTML(connectUsersList.size() + "");
					Timer timer = new Timer() {
						@Override
						public void run() {
							refreshConnectedUsers();
						}
					};
					timer.schedule(USERS_IN_ROOM_REFRESHING_TIME); // Each minute seconds refreshing connected users
				}
				
				@Override
				public void onFailure(Throwable caught) {
					Main.get().showError("GetLoggedUsers", caught);
				}
			});
		}
	}
	
	/**
	 * getPendingChatRoomUser
	 */
	private void getPendingChatRoomUser() {
		if (chatConnected) {
			ServiceDefTarget endPoint = (ServiceDefTarget) chatService;
			endPoint.setServiceEntryPoint(RPCService.ChatService);
			chatService.getPendingChatRoomUser(new AsyncCallback<List<String>>() {
				
				@Override
				public void onSuccess(List<String> result) {
					for (Iterator<String> it = result.iterator(); it.hasNext();) {
						String room = it.next();
						ChatRoomPopup chatRoomPopup = new ChatRoomPopup("",room);
						chatRoomPopup.center();
						chatRoomPopup.getPendingMessage(room);
						addChatRoom(chatRoomPopup);
					}
					
					Timer timer = new Timer() {
						@Override
						public void run() {
							getPendingChatRoomUser();
						}
					};
					timer.schedule(NEW_ROOM_REFRESHING_TIME); // Each minute seconds refreshing connected users
				}
				
				@Override
				public void onFailure(Throwable caught) {
					Main.get().showError("GetLoggedUsers", caught);
				}
			});
		}
	}
	
	/**
	 * getConnectedUserList
	 * 
	 * @return
	 */
	public List<String> getConnectedUserList() {
		return connectUsersList;
	}
	
	/**
	 * addChatRoom
	 * 
	 * @param chatRoom
	 */
	public void addChatRoom(ChatRoomDialogBox chatRoom) {
		if (!chatRoomList.contains(chatRoom)) {
			chatRoomList.add(chatRoom);
		}
	}
	
	/**
	 * removeChatRoom
	 * 
	 * @param chatRoom
	 */
	public void removeChatRoom(ChatRoomDialogBox chatRoom) {
		if (chatRoomList.contains(chatRoom)) {
			chatRoomList.remove(chatRoom);
		}
	}
	
	/**
	 * isConnectedToChat
	 * 
	 * @return
	 */
	public boolean isConnectedToChat() {
		return chatConnected;
	}
	
	/**
	 * getChatRoomList
	 * 
	 * @return
	 */
	public List<ChatRoomDialogBox> getChatRoomList() {
		return chatRoomList;
	}
	
	/**
	 * disconnectChat
	 * 
	 * Used before logout ( in logout popup is made disconnection )
	 */
	public void disconnectChat() {
		chatConnected = false;
		usersConnected.setVisible(false);
		imgNewChatRoom.setVisible(false);
		usersConnected.setHTML("");
		imgChat.setResource(OKMBundleResources.INSTANCE.chatDisconnected());
	}
	
	/**
	 * disconnectChat
	 * 
	 * Recursivelly disconnecting chat rooms and chat before login out
	 *
	 */
	private void logoutChat() {
		// Disconnect rooms
		if (getChatRoomList().size()>0) {
			final ChatRoomDialogBox chatRoom = getChatRoomList().get(0);
			chatRoom.setChatRoomActive(false);
			ServiceDefTarget endPoint = (ServiceDefTarget) chatService;
			endPoint.setServiceEntryPoint(RPCService.ChatService);
			chatService.closeRoom(chatRoom.getRoom(),new AsyncCallback<Object>() {
				@Override
				public void onSuccess(Object arg0) {
					removeChatRoom(chatRoom);
					logoutChat(); // Recursive call
				}
				
				@Override
				public void onFailure(Throwable caught) {
					Main.get().showError("CloseRoom", caught);
					// If happens some problem always we try continue disconnecting chat rooms
					removeChatRoom(chatRoom);
					logoutChat(); // Recursive call
				}
			});
		} else {
			// Disconnect chat
			disconnectChat(); // Only used to change view and disabling some RPC
			ServiceDefTarget endPoint = (ServiceDefTarget) chatService;
			endPoint.setServiceEntryPoint(RPCService.ChatService);
			chatService.logout(new AsyncCallback<Object>() {
				@Override
				public void onSuccess(Object result) {
				}
				@Override
				public void onFailure(Throwable caught) {
					Main.get().showError("GetLogoutChat", caught);
				}
			});
		}
	}
	
	/**
	 * enableChat
	 */
	public void enableChat() {
		imgChat.setVisible(true);
		imgChatSeparator.setVisible(true);
	}
	
	/**
	 * enableUserQuota
	 */
	public void enableUserQuota(long quotaLimit) {
		this.quotaLimit = quotaLimit;
		imgUserQuota.setVisible(true);
		quotaUsed.setVisible(true);
		userQuota = true;
	}
	
	/**
	 * loginChat
	 */
	public void loginChat() {
		ServiceDefTarget endPoint = (ServiceDefTarget) chatService;
		endPoint.setServiceEntryPoint(RPCService.ChatService);
		chatService.login(new AsyncCallback<Object>() {
			@Override
			public void onSuccess(Object result) {
				chatConnected = true;
				imgChat.setResource(OKMBundleResources.INSTANCE.chatConnected());
				imgChat.setTitle(Main.i18n("user.info.chat.disconnect"));
				usersConnected.setVisible(true);
				imgNewChatRoom.setVisible(true);
				refreshConnectedUsers();
				getPendingChatRoomUser();
			}
			@Override
			public void onFailure(Throwable caught) {
				Main.get().showError("GetLoginChat", caught);
			}
		});
	}
	
	/**
	 * isQuotaExceed
	 * 
	 * @return
	 */
	public boolean isQuotaExceed() {
		return quotaExceeded;
	}
	
	/**
	 * showExtensions
	 */
	public void showExtensions() {
		if (widgetExtensionList.size()>0) {
			panel.add(new Image(OKMBundleResources.INSTANCE.separator()));
			panel.add(new HTML("&nbsp;"));
			for (UserInfoExtension extension : widgetExtensionList) {
				panel.add(extension);
				panel.add(new HTML("&nbsp;"));
			}
		}
	}
	
	/**
	 * addUserInfoExtension
	 * 
	 * @param extension
	 */
	public void addUserInfoExtension(UserInfoExtension extension) {
		widgetExtensionList.add(extension);
	}
}
