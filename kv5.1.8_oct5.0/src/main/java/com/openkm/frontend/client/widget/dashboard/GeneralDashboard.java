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
import com.openkm.frontend.client.contants.service.RPCService;
import com.openkm.frontend.client.service.OKMDashboardService;
import com.openkm.frontend.client.service.OKMDashboardServiceAsync;

/**
 * GeneralDashboard
 * 
 * @author jllort
 *
 */
public class GeneralDashboard extends Composite {
	private final OKMDashboardServiceAsync dashboardService = (OKMDashboardServiceAsync) GWT.create(OKMDashboardService.class);
	
	private final int NUMBER_OF_COLUMNS = 2;
	
	private HorizontalPanel hPanel;
	private VerticalPanel vPanelLeft;
	private VerticalPanel vPanelRight;
	
	private DashboardWidget lastWeekTopDownloadedDocuments;
	private DashboardWidget lastMonthTopDownloadedDocuments;
	private DashboardWidget lastMonthTopModifiedDocuments;
	private DashboardWidget lastWeekTopModifiedDocuments;
	private DashboardWidget lastModifiedDocuments;
	private DashboardWidget lastUploadedDocuments;
	
	private boolean firstTime = true;
	
	/**
	 * GeneralDashboard
	 */
	public GeneralDashboard() {
		vPanelLeft = new VerticalPanel();
		vPanelRight = new VerticalPanel();
		hPanel = new HorizontalPanel();
		
		lastWeekTopDownloadedDocuments = new DashboardWidget("LastWeekTopDownloadedDocuments",
				"dashboard.general.last.week.top.downloaded.documents", "img/icon/actions/download.gif",
				true, "lastWeekTopDownloadedDocuments");
		lastMonthTopDownloadedDocuments = new DashboardWidget("LastMonthTopDownloadedDocuments",
				"dashboard.general.last.month.top.downloaded.documents", "img/icon/actions/download.gif",
				false, "lastMonthTopDownloadedDocuments");
		lastWeekTopModifiedDocuments = new DashboardWidget("LastWeekTopModifiedDocuments",
				"dashboard.general.last.week.top.modified.documents", "img/icon/actions/checkin.gif",
				false, "lastWeekTopModifiedDocuments");
		lastMonthTopModifiedDocuments = new DashboardWidget("LastMonthTopModifiedDocuments",
				"dashboard.general.last.month.top.modified.documents", "img/icon/actions/checkin.gif",
				false, "lastMonthTopModifiedDocuments");
		lastModifiedDocuments = new DashboardWidget("LastModifiedDocuments",
				"dashboard.user.last.modified.documents", "img/icon/actions/checkin.gif",
				true, "lastModifiedDocuments");
		lastUploadedDocuments = new DashboardWidget("LastUploadedDocuments",
				"dashboard.general.last.uploaded.documents", "img/icon/actions/add_document.gif",
				false, "lastUploadedDocuments");
		
		vPanelLeft.add(lastWeekTopDownloadedDocuments);
		vPanelLeft.add(lastMonthTopDownloadedDocuments);
		vPanelLeft.add(lastWeekTopModifiedDocuments);
		vPanelLeft.add(lastMonthTopModifiedDocuments);
		vPanelLeft.add(lastUploadedDocuments);
		vPanelRight.add(lastModifiedDocuments);
		
		hPanel.add(vPanelLeft);
		hPanel.add(vPanelRight);
		
		initWidget(hPanel);
		
		// Refreshing all panels
		refreshAll();
		
		firstTime = false;
	}
	
	/**
	 * Refreshing language
	 */
	public void langRefresh() {
		lastWeekTopDownloadedDocuments.langRefresh();
		lastMonthTopDownloadedDocuments.langRefresh();
		lastWeekTopModifiedDocuments.langRefresh();
		lastMonthTopModifiedDocuments.langRefresh();
		lastModifiedDocuments.langRefresh();
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
		lastWeekTopDownloadedDocuments.setWidth(columnWidth);
		lastMonthTopDownloadedDocuments.setWidth(columnWidth);
		lastWeekTopModifiedDocuments.setWidth(columnWidth);
		lastMonthTopModifiedDocuments.setWidth(columnWidth);
		lastModifiedDocuments.setWidth(columnWidth);
		lastUploadedDocuments.setWidth(columnWidth);
	}
	
	/**
	 * Get last week top downloaded documents callback
	 */
	final AsyncCallback<List<GWTDashboardDocumentResult>> callbackGetLastWeekTopDownloadedDocuments = new AsyncCallback<List<GWTDashboardDocumentResult>>() {
		public void onSuccess(List<GWTDashboardDocumentResult> result){
			lastWeekTopDownloadedDocuments.setDocuments(result);
			lastWeekTopDownloadedDocuments.setHeaderResults(result.size());
			lastWeekTopDownloadedDocuments.unsetRefreshing();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("getLastWeekTopDownloadedDocuments", caught);
			lastWeekTopDownloadedDocuments.unsetRefreshing();
		}
	};
	
	/**
	 * Get last month top downloaded documents callback
	 */
	final AsyncCallback<List<GWTDashboardDocumentResult>> callbackGetLastMonthTopDownloadedDocuments = new AsyncCallback<List<GWTDashboardDocumentResult>>() {
		public void onSuccess(List<GWTDashboardDocumentResult> result){
			lastMonthTopDownloadedDocuments.setDocuments(result);
			lastMonthTopDownloadedDocuments.setHeaderResults(result.size());
			lastMonthTopDownloadedDocuments.unsetRefreshing();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("getLastMonthTopDownloadedDocuments", caught);
			lastMonthTopDownloadedDocuments.unsetRefreshing();
		}
	};
	
	/**
	 * Gets last months top modified documents callback
	 */
	final AsyncCallback<List<GWTDashboardDocumentResult>> callbackGetLastMonthTopModifiedDocuments = new AsyncCallback<List<GWTDashboardDocumentResult>>() {
		public void onSuccess(List<GWTDashboardDocumentResult> result){
			lastMonthTopModifiedDocuments.setDocuments(result);
			lastMonthTopModifiedDocuments.setHeaderResults(result.size());
			lastMonthTopModifiedDocuments.unsetRefreshing();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("getLastMonthTopModifiedDocuments", caught);
			lastMonthTopModifiedDocuments.unsetRefreshing();
		}
	};
	
	/**
	 * Gets last week top modified documents callback
	 */
	final AsyncCallback<List<GWTDashboardDocumentResult>> callbackGetLastWeekTopModifiedDocuments = new AsyncCallback<List<GWTDashboardDocumentResult>>() {
		public void onSuccess(List<GWTDashboardDocumentResult> result){
			lastWeekTopModifiedDocuments.setDocuments(result);
			lastWeekTopModifiedDocuments.setHeaderResults(result.size());
			lastWeekTopModifiedDocuments.unsetRefreshing();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("getLastWeekTopModifiedDocuments", caught);
			lastWeekTopModifiedDocuments.unsetRefreshing();
		}
	};
	
	/**
	 * Gets last modified documents callback
	 */
	final AsyncCallback<List<GWTDashboardDocumentResult>> callbackGetLastModifiedDocuments = new AsyncCallback<List<GWTDashboardDocumentResult>>() {
		public void onSuccess(List<GWTDashboardDocumentResult> result){
			lastModifiedDocuments.setDocuments(result);
			lastModifiedDocuments.setHeaderResults(result.size());
			lastModifiedDocuments.unsetRefreshing();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("getLastModifiedDocuments", caught);
			lastModifiedDocuments.unsetRefreshing();
		}
	};
	
	
	/**
	 * get last week top uploaded documents
	 */
	final AsyncCallback<List<GWTDashboardDocumentResult>> callbackGetLastUploadedDocuments = new AsyncCallback<List<GWTDashboardDocumentResult>>() {
		public void onSuccess(List<GWTDashboardDocumentResult> result){
			lastUploadedDocuments.setDocuments(result);
			lastUploadedDocuments.setHeaderResults(result.size());
			lastUploadedDocuments.unsetRefreshing();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("callbackGetLastUploadedDocuments", caught);
			lastUploadedDocuments.unsetRefreshing();
		}
	};

	/**
	 * getLastWeekTopDownloadedDocuments
	 */
	public void getLastWeekTopDownloadedDocuments() {
		if (!firstTime) {
			lastWeekTopDownloadedDocuments.setRefreshing();
		}
		ServiceDefTarget endPoint = (ServiceDefTarget) dashboardService;
		endPoint.setServiceEntryPoint(RPCService.DashboardService);		
		dashboardService.getLastWeekTopDownloadedDocuments(callbackGetLastWeekTopDownloadedDocuments);
	}
	
	/**
	 * getLastMonthTopDownloadedDocuments
	 */
	public void getLastMonthTopDownloadedDocuments() {
		if (!firstTime) {
			lastMonthTopDownloadedDocuments.setRefreshing();
		}
		ServiceDefTarget endPoint = (ServiceDefTarget) dashboardService;
		endPoint.setServiceEntryPoint(RPCService.DashboardService);		
		dashboardService.getLastMonthTopDownloadedDocuments(callbackGetLastMonthTopDownloadedDocuments);
	}
	
	/**
	 * getLastMonthTopModifiedDocuments
	 */
	public void getLastMonthTopModifiedDocuments() {
		if (!firstTime) {
			lastMonthTopModifiedDocuments.setRefreshing();
		}
		ServiceDefTarget endPoint = (ServiceDefTarget) dashboardService;
		endPoint.setServiceEntryPoint(RPCService.DashboardService);		
		dashboardService.getLastMonthTopModifiedDocuments(callbackGetLastMonthTopModifiedDocuments);
	}
	
	/**
	 * getLastWeekTopModifiedDocuments
	 */
	public void getLastWeekTopModifiedDocuments() {
		if (!firstTime) {
			lastWeekTopModifiedDocuments.setRefreshing();
		}
		ServiceDefTarget endPoint = (ServiceDefTarget) dashboardService;
		endPoint.setServiceEntryPoint(RPCService.DashboardService);		
		dashboardService.getLastWeekTopModifiedDocuments(callbackGetLastWeekTopModifiedDocuments);
	}
	
	/**
	 * getLastModifiedDocuments
	 */
	public void getLastModifiedDocuments() {
		if (!firstTime) {
			lastModifiedDocuments.setRefreshing();
		}
		ServiceDefTarget endPoint = (ServiceDefTarget) dashboardService;
		endPoint.setServiceEntryPoint(RPCService.DashboardService);		
		dashboardService.getLastModifiedDocuments(callbackGetLastModifiedDocuments);
	}
	
	/**
	 * getLastUploadedDocuments
	 */
	public void getLastUploadedDocuments() {
		if (!firstTime) {
			lastUploadedDocuments.setRefreshing();
		}
		ServiceDefTarget endPoint = (ServiceDefTarget) dashboardService;
		endPoint.setServiceEntryPoint(RPCService.DashboardService);	
		dashboardService.getLastUploadedDocuments(callbackGetLastUploadedDocuments);
	}
	
	/**
	 * Refresh all panels
	 */
	public void refreshAll() {
		getLastWeekTopDownloadedDocuments();
		getLastMonthTopDownloadedDocuments();
		getLastMonthTopModifiedDocuments();
		getLastWeekTopModifiedDocuments();
		getLastModifiedDocuments();
		getLastUploadedDocuments();
	}
}
