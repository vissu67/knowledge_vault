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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTProcessDefinition;
import com.openkm.frontend.client.service.OKMWorkflowService;
import com.openkm.frontend.client.service.OKMWorkflowServiceAsync;
import com.openkm.frontend.client.widget.wizard.WorkflowWidget;
import com.openkm.frontend.client.widget.wizard.WorkflowWidgetToFire;

/**
 * WorkflowPopup popup
 * s
 * @author jllort
 *
 */
public class WorkflowPopup extends DialogBox implements WorkflowWidgetToFire {
	
	private final OKMWorkflowServiceAsync workflowService = (OKMWorkflowServiceAsync) GWT.create(OKMWorkflowService.class);
	
	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	private Button button;
	private Button addButton;
	private ListBox listBox;
	private SimplePanel sp;
	private WorkflowWidget workflowWidget= null;
	private String uuid = "";
	
	/**
	 * WorkflowPopup popup
	 */
	public WorkflowPopup() {
		// Establishes auto-close when click outside
		super(false,true);

		vPanel = new VerticalPanel();
		hPanel = new HorizontalPanel();
		sp = new SimplePanel();
		
		button = new Button(Main.i18n("button.close"), new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		addButton = new Button(Main.i18n("button.start"), new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				addButton.setEnabled(false);
				runProcessDefinition();
			}
		});

		listBox = new ListBox();
		listBox.addChangeHandler(new ChangeHandler(){
			@Override
			public void onChange(ChangeEvent event) {
				if (listBox.getSelectedIndex()>0) {
					addButton.setEnabled(true);
				} else {
					addButton.setEnabled(false);
				}
				sp.setVisible(false);
				sp.clear();
			}
		});
		listBox.setStyleName("okm-Select");
		
		vPanel.setWidth("300px");
		vPanel.setHeight("50px");
		button.setStyleName("okm-Button");
		addButton.setStyleName("okm-Button");
		addButton.setEnabled(false);
		
		hPanel.add(button);
		hPanel.add(new HTML("&nbsp;&nbsp;"));
		hPanel.add(addButton);
		
		hPanel.setCellHorizontalAlignment(button,VerticalPanel.ALIGN_CENTER);
		hPanel.setCellHorizontalAlignment(addButton,VerticalPanel.ALIGN_CENTER);
		
		vPanel.add(new HTML("<br>"));
		vPanel.add(listBox);
		vPanel.add(new HTML("<br>"));
		vPanel.add(sp);
		vPanel.add(new HTML("<br>"));
		vPanel.add(hPanel);
		vPanel.add(new HTML("<br>"));
		
		vPanel.setCellHorizontalAlignment(listBox, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, VerticalPanel.ALIGN_CENTER);
		
		super.hide();
		setWidget(vPanel);
	}
	
	/**
	 * Gets asynchronous to get all process definitions
	 */
	final AsyncCallback<List<GWTProcessDefinition>> callbackFindLatestProcessDefinitions = new AsyncCallback<List<GWTProcessDefinition>>() {
		public void onSuccess(List<GWTProcessDefinition> result){
			listBox.clear();
			listBox.addItem("",""); // Adds empty value
			for (Iterator<GWTProcessDefinition> it = result.iterator(); it.hasNext();) {
				GWTProcessDefinition processDefinition = it.next();
				listBox.addItem(processDefinition.getName(),""+processDefinition.getId());
			}
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("callbackFindLatestProcessDefinitions", caught);
		}
	};
	
	/**
	 * Gets asynchronous to run process definition
	 */
	final AsyncCallback<Object> callbackRunProcessDefinition = new AsyncCallback<Object>() {
		public void onSuccess(Object result){
			Main.get().mainPanel.dashboard.workflowDashboard.findUserTaskInstances();
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("callbackRunProcessDefinition", caught);
		}
	};
	
	/**
	 * Enables close button
	 */
	public void enableClose() {
		button.setEnabled(true);
		Main.get().mainPanel.setVisible(true); // Shows main panel when all widgets are loaded
	}
	
	/**
	 * Language refresh
	 */
	public void langRefresh() {
		setText(Main.i18n("workflow.label"));
		button.setText(Main.i18n("button.close"));
		addButton.setText(Main.i18n("button.start"));
	}
	
	/**
	 * Show the popup error
	 * 
	 * @param msg Error message
	 */
	public void show() {
		setText(Main.i18n("workflow.label"));
		findLatestProcessDefinitions(); // Gets all groups
		listBox.setVisible(true);
		addButton.setEnabled(false);
		workflowWidget = null;
		sp.setVisible(false);
		sp.clear();
		int left = (Window.getClientWidth()-300)/2;
		int top = (Window.getClientHeight()-100)/2;
		setPopupPosition(left,top);
		super.show();
	}
	
	/**
	 * Gets all process definitions
	 */
	private void findLatestProcessDefinitions() {
		workflowService.findLatestProcessDefinitions(callbackFindLatestProcessDefinitions);
	}	
	
	/**
	 * Run process definition
	 */
	private void runProcessDefinition() {
		if (workflowWidget!=null) {
			workflowWidget.runProcessDefinition(); // Here has some forms to be filled
		} else if (listBox.getSelectedIndex()>0) {
			if (Main.get().activeFolderTree.isPanelSelected()) {
				uuid = Main.get().activeFolderTree.getFolder().getUuid();
			} else {
				if (Main.get().mainPanel.desktop.browser.fileBrowser.isDocumentSelected()) {
					uuid = Main.get().mainPanel.desktop.browser.fileBrowser.getDocument().getUuid();
				} else if(Main.get().mainPanel.desktop.browser.fileBrowser.isFolderSelected()) {
					uuid = Main.get().mainPanel.desktop.browser.fileBrowser.getFolder().getUuid();
				} else if(Main.get().mainPanel.desktop.browser.fileBrowser.isMailSelected()) {
					uuid = Main.get().mainPanel.desktop.browser.fileBrowser.getMail().getUuid();
				}
			}
			workflowWidget = new WorkflowWidget(new Double(listBox.getValue(listBox.getSelectedIndex())).doubleValue(), uuid, this, new HashMap<String, Object>());
			sp.add(workflowWidget);
			workflowWidget.runProcessDefinition();
		}
	}

	@Override
	public void finishedRunProcessDefinition() {
		workflowWidget = null;
		hide();
	}

	@Override
	public void hasPendingProcessDefinitionForms() {
		sp.setVisible(true);
		addButton.setEnabled(true);
	}
}
