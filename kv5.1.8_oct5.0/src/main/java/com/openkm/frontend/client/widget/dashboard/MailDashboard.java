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
import com.openkm.frontend.client.bean.GWTDashboardMailResult;
import com.openkm.frontend.client.contants.service.RPCService;
import com.openkm.frontend.client.service.OKMDashboardService;
import com.openkm.frontend.client.service.OKMDashboardServiceAsync;

/**
 * MailDashboard
 * 
 * @author jllort
 *
 */
public class MailDashboard extends Composite {
	private final OKMDashboardServiceAsync dashboardService = (OKMDashboardServiceAsync) GWT.create(OKMDashboardService.class);
	
	private final int NUMBER_OF_COLUMNS = 2;
	
	private HorizontalPanel hPanel;
	private VerticalPanel vPanelLeft;
	private VerticalPanel vPanelRight;
	
	private DashboardWidget userLastImportedMails;
	private DashboardWidget userLastImportedAttachments;
	
	private boolean firstTime = true;
	
	/**
	 * GeneralDashboard
	 */
	public MailDashboard() {
		vPanelLeft = new VerticalPanel();
		vPanelRight = new VerticalPanel();
		hPanel = new HorizontalPanel();
		
		userLastImportedMails = new DashboardWidget("UserLastImportedMails",
				"dashboard.mail.last.imported.mails", "img/email.gif", true, "userLastImportedMails");
		userLastImportedAttachments = new DashboardWidget("UserLastImportedMailAttachments",
				"dashboard.mail.last.imported.attached.documents", "img/email_attach.gif", true,
				"userLastImportedMailAttachments");
		
		vPanelLeft.add(userLastImportedMails);
		vPanelRight.add(userLastImportedAttachments);
		
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
		userLastImportedMails.langRefresh();
		userLastImportedAttachments.langRefresh();
	}
	
	/**
	 * setWidth
	 * 
	 * @param width
	 */
	public void setWidth(int width) {
		int columnWidth = width/NUMBER_OF_COLUMNS;
		
		// Trying to distribute widgets on columns with max size
		userLastImportedMails.setWidth(columnWidth);
		userLastImportedAttachments.setWidth(columnWidth);
	}
	
	/**
	 * Get last user imported mails callback
	 */
	final AsyncCallback<List<GWTDashboardMailResult>> callbackGetUserLastImportedMails = new AsyncCallback<List<GWTDashboardMailResult>>() {
		public void onSuccess(List<GWTDashboardMailResult> result){
			userLastImportedMails.setMails(result);
			userLastImportedMails.setHeaderResults(result.size());
			userLastImportedMails.unsetRefreshing();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("getUserLastImportedMails", caught);
			userLastImportedMails.unsetRefreshing();
		}
	};
	
	/**
	 * Gets last imported mail attachments documents callback
	 */
	final AsyncCallback<List<GWTDashboardDocumentResult>> callbackGetUserLastImportedMailAttachments = new AsyncCallback<List<GWTDashboardDocumentResult>>() {
		public void onSuccess(List<GWTDashboardDocumentResult> result){
			userLastImportedAttachments.setDocuments(result);
			userLastImportedAttachments.setHeaderResults(result.size());
			userLastImportedAttachments.unsetRefreshing();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("getUserLastImportedMailAttachments", caught);
			userLastImportedAttachments.unsetRefreshing();
		}
	};

	/**
	 * getLastWeekTopDownloadedDocuments
	 */
	public void getUserLastImportedMails() {
		if (!firstTime) {
			userLastImportedMails.setRefreshing();
		}
		ServiceDefTarget endPoint = (ServiceDefTarget) dashboardService;
		endPoint.setServiceEntryPoint(RPCService.DashboardService);		
		dashboardService.getUserLastImportedMails(callbackGetUserLastImportedMails);
	}
	
	/**
	 * getLastModifiedDocuments
	 */
	public void getUserLastImportedMailAttachments() {
		if (!firstTime) {
			userLastImportedAttachments.setRefreshing();
		}
		ServiceDefTarget endPoint = (ServiceDefTarget) dashboardService;
		endPoint.setServiceEntryPoint(RPCService.DashboardService);		
		dashboardService.getUserLastImportedMailAttachments(callbackGetUserLastImportedMailAttachments);
	}
	
	/**
	 * Refresh all panels
	 */
	public void refreshAll() {
		getUserLastImportedMails();
		getUserLastImportedMailAttachments();
	}
}
