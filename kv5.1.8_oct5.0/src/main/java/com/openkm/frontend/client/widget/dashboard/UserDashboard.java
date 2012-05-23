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

package com.openkm.frontend.client.widget.dashboard;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDashboardDocumentResult;
import com.openkm.frontend.client.bean.GWTDashboardFolderResult;
import com.openkm.frontend.client.contants.service.RPCService;
import com.openkm.frontend.client.service.OKMDashboardService;
import com.openkm.frontend.client.service.OKMDashboardServiceAsync;

/**
 * UserDashboard
 * 
 * @author jllort
 *
 */
public class UserDashboard extends Composite {
	
	private final OKMDashboardServiceAsync dashboardService = (OKMDashboardServiceAsync) GWT.create(OKMDashboardService.class);
	
	private final int NUMBER_OF_COLUMNS = 2;
	
	private HorizontalPanel hPanel;
	private VerticalPanel vPanelLeft;
	private VerticalPanel vPanelRight;
	
	private DashboardWidget lockedDocuments;
	private DashboardWidget chechoutDocuments;
	private DashboardWidget lastModifiedDocuments;
	private DashboardWidget subscribedDocuments;
	private DashboardWidget subscribedFolder;
	private DashboardWidget lastDownloadedDocuments;
	private DashboardWidget lastUploadedDocuments;
	
	private boolean firstTime = true; 
	private int tmpSubscriptions = 0;
	private boolean checkoutDocumentFlag = false;
	
	/**
	 * UserDashboard
	 */
	public UserDashboard() {
		vPanelLeft = new VerticalPanel();
		vPanelRight = new VerticalPanel();
		hPanel = new HorizontalPanel();
		
		hPanel.add(vPanelLeft);
		hPanel.add(vPanelRight);
		
		lockedDocuments = new DashboardWidget("UserLockedDocuments", "dashboard.user.locked.documents",
				"img/icon/lock.gif", true, "userLockedDocuments");
		chechoutDocuments = new DashboardWidget("UserCheckedOutDocuments", "dashboard.user.checkout.documents",
				"img/icon/actions/checkout.gif", true, "userCheckedOutDocuments");
		lastModifiedDocuments = new DashboardWidget("UserLastModifiedDocuments",
				"dashboard.user.last.modified.documents","img/icon/actions/checkin.gif", true,
				"userLastModifiedDocuments");
		lastDownloadedDocuments = new DashboardWidget("UserLastDownloadedDocuments",
				"dashboard.user.last.downloaded.documents", "img/icon/actions/download.gif", false,
				"userLastDownloadedDocuments");
		subscribedDocuments = new DashboardWidget("UserSubscribedDocuments", "dashboard.user.subscribed.documents",
				"img/icon/subscribed.gif", false, "userSubscribedDocuments");
		subscribedFolder = new DashboardWidget("UserSubscribedFolders", "dashboard.user.subscribed.folders",
				"img/icon/subscribed.gif", false, "userSubscribedFolders");
		lastUploadedDocuments = new DashboardWidget("UserLastUploadedDocuments",
				"dashboard.user.last.uploaded.documents", "img/icon/actions/add_document.gif", true,
				"userLastUploadedDocuments");
		
		vPanelLeft.add(lockedDocuments);
		vPanelLeft.add(chechoutDocuments);
		vPanelLeft.add(lastDownloadedDocuments);
		vPanelRight.add(lastModifiedDocuments);
		vPanelRight.add(lastUploadedDocuments);
		vPanelLeft.add(subscribedDocuments);
		vPanelLeft.add(subscribedFolder);
		
		initWidget(hPanel);
		
		// Refreshing data panels;
		refreshAll();
		
		firstTime=false; // Must not show status first time loading OpenKM ( on startup )
	}
	
	/**
	 * Refreshing language
	 */
	public void langRefresh() {
		lockedDocuments.langRefresh();
		chechoutDocuments.langRefresh();
		lastModifiedDocuments.langRefresh();
		lastDownloadedDocuments.langRefresh();
		subscribedDocuments.langRefresh();
		subscribedFolder.langRefresh();
		lastUploadedDocuments.langRefresh();
	}
	
	/**
	 * setWidth
	 * 
	 * @param width
	 */
	public void setWidth(int width) {
		int columnWidth = width/NUMBER_OF_COLUMNS;
		
		// Trying to distribute widgets on columns with max size
		lockedDocuments.setWidth(columnWidth);
		chechoutDocuments.setWidth(columnWidth);
		lastModifiedDocuments.setWidth(columnWidth);
		lastDownloadedDocuments.setWidth(columnWidth);
		subscribedDocuments.setWidth(columnWidth);
		subscribedFolder.setWidth(columnWidth);
		lastUploadedDocuments.setWidth(columnWidth);
	}
	
	/**
	 * Gets the locked documents callback
	 */
	final AsyncCallback<List<GWTDashboardDocumentResult>> callbackGetUserLockedDocuments = new AsyncCallback<List<GWTDashboardDocumentResult>>() {
		public void onSuccess(List<GWTDashboardDocumentResult> result){
			lockedDocuments.setDocuments(result);
			lockedDocuments.setHeaderResults(result.size());
			Main.get().mainPanel.bottomPanel.userInfo.setLockedDocuments(result.size());
			lockedDocuments.unsetRefreshing();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("getUserLockedDocuments", caught);
			lockedDocuments.unsetRefreshing();
		}
	};
	
	/**
	 * Gets the checkout documents callback
	 */
	final AsyncCallback<List<GWTDashboardDocumentResult>> callbackGetUserCheckOutDocuments = new AsyncCallback<List<GWTDashboardDocumentResult>>() {
		public void onSuccess(List<GWTDashboardDocumentResult> result){
			chechoutDocuments.setDocuments(result);
			chechoutDocuments.setHeaderResults(result.size());
			Main.get().mainPanel.bottomPanel.userInfo.setCheckoutDocuments(result.size());
			chechoutDocuments.unsetRefreshing();
			checkoutDocumentFlag = false; // Marks rpc calls are finished and can checkout document
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("getUserCheckedOutDocuments", caught);
			chechoutDocuments.unsetRefreshing();
		}
	};
	
	/**
	 * Gets last modified documents callback
	 */
	final AsyncCallback<List<GWTDashboardDocumentResult>> callbackGetUserLastModifiedDocuments = new AsyncCallback<List<GWTDashboardDocumentResult>>() {
		public void onSuccess(List<GWTDashboardDocumentResult> result){
			lastModifiedDocuments.setDocuments(result);
			lastModifiedDocuments.setHeaderResults(result.size());
			lastModifiedDocuments.unsetRefreshing();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("getUserLastModifiedDocuments", caught);
			lastModifiedDocuments.unsetRefreshing();
		}
	};
	
	/**
	 * Get subscribed documents callback
	 */
	final AsyncCallback<List<GWTDashboardDocumentResult>> callbackGetUserSubscribedDocuments = new AsyncCallback<List<GWTDashboardDocumentResult>>() {
		public void onSuccess(List<GWTDashboardDocumentResult> result){
			subscribedDocuments.setDocuments(result);
			subscribedDocuments.setHeaderResults(result.size());
			tmpSubscriptions = result.size();
			getUserSubscribedFolders();
			subscribedDocuments.unsetRefreshing();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("getUserSubscribedDocuments", caught);
			subscribedDocuments.unsetRefreshing();
		}
	};
	
	/**
	 * Gets the subscribed folders
	 */
	final AsyncCallback<List<GWTDashboardFolderResult>> callbackGetUserSubscribedFolders = new AsyncCallback<List<GWTDashboardFolderResult>>() {
		public void onSuccess(List<GWTDashboardFolderResult> result){
			subscribedFolder.setFolders(result);
			subscribedFolder.setHeaderResults(result.size());
			tmpSubscriptions += result.size();
			Main.get().mainPanel.bottomPanel.userInfo.setSubscriptions(tmpSubscriptions);
			subscribedFolder.unsetRefreshing();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("getUserSubscribedFolders", caught);
			subscribedFolder.unsetRefreshing();
		}
	};
	
	/**
	 * Gets the downloaded documents
	 */
	final AsyncCallback<List<GWTDashboardDocumentResult>> callbackGetUserLastDownloadedDocuments = new AsyncCallback<List<GWTDashboardDocumentResult>>() {
		public void onSuccess(List<GWTDashboardDocumentResult> result){
			lastDownloadedDocuments.setDocuments(result);
			lastDownloadedDocuments.setHeaderResults(result.size());
			lastDownloadedDocuments.unsetRefreshing();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("getUserLastDownloadedDocuments", caught);
			lastDownloadedDocuments.unsetRefreshing();
		}
	};
	
	/**
	 * Gets the last uploaded documents
	 */
	final AsyncCallback<List<GWTDashboardDocumentResult>> callbackGetUserLastUploadedDocuments = new AsyncCallback<List<GWTDashboardDocumentResult>>() {
		public void onSuccess(List<GWTDashboardDocumentResult> result){
			lastUploadedDocuments.setDocuments(result);
			lastUploadedDocuments.setHeaderResults(result.size());
			lastUploadedDocuments.unsetRefreshing();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("callbackGetUserLastUploadedDocuments", caught);
			lastUploadedDocuments.unsetRefreshing();
		}
	};
	
	/**
	 * getUserLockedDocuments
	 */
	public void getUserLockedDocuments() {
		if (!firstTime) {
			lockedDocuments.setRefreshing();
		}
		ServiceDefTarget endPoint = (ServiceDefTarget) dashboardService;
		endPoint.setServiceEntryPoint(RPCService.DashboardService);		
		dashboardService.getUserLockedDocuments(callbackGetUserLockedDocuments);
	}
	
	/**
	 * setPendingCheckoutDocumentFlag
	 * 
	 * Flag to ensure RPC calls are finished
	 */
	public void setPendingCheckoutDocumentFlag() {
		checkoutDocumentFlag = true;
	}
	
	public boolean isPendingCheckoutDocumentFlag() {
		return checkoutDocumentFlag;
	}
	
	/**
	 * getUserCheckedOutDocuments
	 */
	public void getUserCheckedOutDocuments() {
		if (!firstTime) {
			chechoutDocuments.setRefreshing();
		}
		ServiceDefTarget endPoint = (ServiceDefTarget) dashboardService;
		endPoint.setServiceEntryPoint(RPCService.DashboardService);	
		dashboardService.getUserCheckedOutDocuments(callbackGetUserCheckOutDocuments);
	}
	
	/**
	 * getUserLastModifiedDocuments
	 */
	public void getUserLastModifiedDocuments() {
		if (!firstTime) {
			lastModifiedDocuments.setRefreshing();
		}
		ServiceDefTarget endPoint = (ServiceDefTarget) dashboardService;
		endPoint.setServiceEntryPoint(RPCService.DashboardService);		
		dashboardService.getUserLastModifiedDocuments(callbackGetUserLastModifiedDocuments);
	}
	
	/**
	 * getUserSubscribedDocuments
	 */
	public void getUserSubscribedDocuments() {
		if (!firstTime) {
			subscribedDocuments.setRefreshing();
		}
		ServiceDefTarget endPoint = (ServiceDefTarget) dashboardService;
		endPoint.setServiceEntryPoint(RPCService.DashboardService);		
		dashboardService.getUserSubscribedDocuments(callbackGetUserSubscribedDocuments);
	}
	
	/**
	 * getUserSubscribedFolders
	 */
	public void getUserSubscribedFolders() {
		if (!firstTime) {
			subscribedFolder.setRefreshing();
		}
		ServiceDefTarget endPoint = (ServiceDefTarget) dashboardService;
		endPoint.setServiceEntryPoint(RPCService.DashboardService);		
		dashboardService.getUserSubscribedFolders(callbackGetUserSubscribedFolders);
	}
	
	/**
	 * getUserLastDownloadedDocuments
	 */
	public void getUserLastDownloadedDocuments() {
		if (!firstTime) {
			lastDownloadedDocuments.setRefreshing();
		}
		ServiceDefTarget endPoint = (ServiceDefTarget) dashboardService;
		endPoint.setServiceEntryPoint(RPCService.DashboardService);		
		dashboardService.getUserLastDownloadedDocuments(callbackGetUserLastDownloadedDocuments);
	}
	
	/**
	 * getUserLastUploadedDocuments
	 */
	public void getUserLastUploadedDocuments() {
		if (!firstTime) {
			lastUploadedDocuments.setRefreshing();
		}
		ServiceDefTarget endPoint = (ServiceDefTarget) dashboardService;
		endPoint.setServiceEntryPoint(RPCService.DashboardService);
		dashboardService.getUserLastUploadedDocuments(callbackGetUserLastUploadedDocuments);	
	}
	
	/**
	 * Refresh all panlels
	 */
	public void refreshAll() {
		getUserLockedDocuments();
		getUserCheckedOutDocuments();
		getUserLastModifiedDocuments();
		getUserSubscribedDocuments();
		getUserLastDownloadedDocuments();
		getUserLastUploadedDocuments();
	}
}
