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

package com.openkm.frontend.client.widget.wizard;

import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTPropertyGroup;
import com.openkm.frontend.client.contants.service.RPCService;
import com.openkm.frontend.client.service.OKMDocumentService;
import com.openkm.frontend.client.service.OKMDocumentServiceAsync;
import com.openkm.frontend.client.service.OKMPropertyGroupService;
import com.openkm.frontend.client.service.OKMPropertyGroupServiceAsync;
import com.openkm.frontend.client.widget.propertygroup.PropertyGroupWidget;
import com.openkm.frontend.client.widget.propertygroup.PropertyGroupWidgetToFire;

/**
 * WizardPopup
 * 
 * @author jllort
 *
 */
public class WizardPopup extends DialogBox {
	
	private final OKMPropertyGroupServiceAsync propertyGroupService = (OKMPropertyGroupServiceAsync) GWT.create(OKMPropertyGroupService.class);
	private final OKMDocumentServiceAsync documentService = (OKMDocumentServiceAsync) GWT.create(OKMDocumentService.class);
	
	private static final int STATUS_NONE 				= -1;
	private static final int STATUS_ADD_PROPERTY_GROUPS = 0;
	private static final int STATUS_PROPERTY_GROUPS 	= 1;
	private static final int STATUS_WORKFLOWS 			= 2;
	private static final int STATUS_CATEGORIES 			= 3;
	private static final int STATUS_KEYWORDS 			= 4;
	private static final int STATUS_FINISH 				= 5;
	
	private FiredVerticalPanel vPanelFired;
	private String docPath = "";
	private List<GWTPropertyGroup> groupsList = null;
	private List<Double> workflowsList = null;
	private int groupIndex = 0;
	private int workflowIndex = 0;
	private PropertyGroupWidget propertyGroupWidget = null;
	private WorkflowWidget workflowWidget= null;
	private int status = STATUS_NONE;
	public Button actualButton;
	public KeywordsWidget keywordsWidget;
	public CategoriesWidget categoriesWidget;
	public GWTDocument docToSign = null;
	public String uuid = "";
	
	/**
	 * WizardPopup
	 */
	public WizardPopup() {
		// Establishes auto-close when click outside
		super(false,true);
		
		actualButton = new Button("");
		vPanelFired = new FiredVerticalPanel();
		vPanelFired.setSize("100%", "20");
		setText(Main.i18n("wizard.document.uploading"));
		
		actualButton.setStyleName("okm-Button");
		
		super.hide();
		setWidget(vPanelFired);
	}
	
	/**
	 * Starting wizard
	 * 
	 * @param docPath
	 */
	public void start(String docPath) {
		vPanelFired.clear();
		actualButton = new Button("");
		actualButton.setEnabled(false);
		this.docPath = docPath;
		docToSign = null;
		status = STATUS_ADD_PROPERTY_GROUPS;
		
		// Wizard
		groupIndex = 0;
		groupsList = Main.get().workspaceUserProperties.getWorkspace().getWizardPropertyGroupsList();
		
		// workflow 
		workflowIndex = 0;
		workflowsList = Main.get().workspaceUserProperties.getWorkspace().getWizardWorkflowsList();
		
		// getting uuid
		documentService.get(docPath, new AsyncCallback<GWTDocument>() {
			@Override
			public void onSuccess(GWTDocument result) {
				uuid = result.getUuid();
				addPropertyGroups(); // Continue adding property groups
			}
			@Override
			public void onFailure(Throwable caught) {
				Main.get().showError("get", caught);
			}
		});
	}
	
	/**
	 * Gets asyncronous to add a group
	 */
	final AsyncCallback<Object> callbackAddGroup = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {
			groupIndex++;
			if (groupsList.size()>groupIndex) {
				addPropertyGroups();
			} else {
				groupIndex = 0; // restarting property group index to setting
				showNextWizard();
			}
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("AddGroup", caught);
		}
	};
	
	/**
	 * Add property groups to a document
	 */
	private void addPropertyGroups() {
		if (groupsList!=null && groupsList.size()>groupIndex) {
			status = STATUS_PROPERTY_GROUPS;
			ServiceDefTarget endPoint = (ServiceDefTarget) propertyGroupService;
			endPoint.setServiceEntryPoint(RPCService.PropertyGroupService);	
			propertyGroupService.addGroup(docPath, groupsList.get(groupIndex).getName(), callbackAddGroup);
			
		} else if(groupsList==null || (groupsList!=null && groupsList.isEmpty() )) {
			status = STATUS_WORKFLOWS;
			showNextWizard();
			
		} else if(groupsList.size()==0) {
			status = STATUS_WORKFLOWS;
			showNextWizard();
		}
	}
	
	/**
	 * getProperties()
	 */
	private void getProperties() {
		HorizontalPanel hPanel = new HorizontalPanel();
		HTML space = new HTML("");
		hPanel.add(actualButton);
		hPanel.add(space);
		hPanel.setCellWidth(space, "3");
		propertyGroupWidget = new PropertyGroupWidget(docPath, groupsList.get(groupIndex), 
				   									  new HTML(groupsList.get(groupIndex).getLabel()), vPanelFired );
		vPanelFired.clear();
		vPanelFired.add(propertyGroupWidget);
		vPanelFired.add(hPanel);
		HTML space2 = new HTML("");
		vPanelFired.add(space2);
		vPanelFired.setCellVerticalAlignment(propertyGroupWidget, HasAlignment.ALIGN_TOP);
		vPanelFired.setCellHorizontalAlignment(hPanel, HasAlignment.ALIGN_RIGHT);
		vPanelFired.setCellHeight(space2, "5");
		propertyGroupWidget.getProperties();
	}
	
	/**
	 * getWorkflows
	 */
	public void getWorkflows() {
		HorizontalPanel hPanel = new HorizontalPanel();
		HTML space = new HTML("");
		hPanel.add(actualButton);
		hPanel.add(space);
		hPanel.setCellWidth(space, "3");
		workflowWidget = new WorkflowWidget(workflowsList.get(workflowIndex).doubleValue(), uuid, vPanelFired, new HashMap<String, Object>());
		vPanelFired.clear();
		vPanelFired.add(workflowWidget);
		vPanelFired.add(hPanel);
		HTML space2 = new HTML("");
		vPanelFired.add(space2);
		vPanelFired.setCellVerticalAlignment(workflowWidget, HasAlignment.ALIGN_TOP);
		vPanelFired.setCellHorizontalAlignment(hPanel, HasAlignment.ALIGN_RIGHT);
		vPanelFired.setCellHeight(space2, "5");
		workflowWidget.runProcessDefinition();
	}
	
	/**
	 * showNextWizard
	 */
	public void showNextWizard() {
		switch (status) {
			case STATUS_PROPERTY_GROUPS:
				if (groupsList!=null && groupsList.size()>groupIndex) {
					if (groupsList.size()==groupIndex+1) {
						// Case last property group to be added
						if (!Main.get().workspaceUserProperties.getWorkspace().isWizardWorkflows() &&
							!Main.get().workspaceUserProperties.getWorkspace().isWizardCategories() && 
							!Main.get().workspaceUserProperties.getWorkspace().isWizardKeywords()) {
							actualButton = acceptButton();
						} else {
							actualButton = nextButton();
						}
					} else {
						actualButton = nextButton();
					}
					getProperties();
					groupIndex++;
				} else {
					// Forward to next status
					status = STATUS_WORKFLOWS;
					showNextWizard();
				}
				break;
			
			case STATUS_WORKFLOWS:
				if (workflowsList!=null && workflowsList.size()>workflowIndex) {
					if (workflowsList.size()==workflowIndex+1) {
						// Case last property group to be added
						if (!Main.get().workspaceUserProperties.getWorkspace().isWizardCategories() && 
							!Main.get().workspaceUserProperties.getWorkspace().isWizardKeywords()) {
							actualButton = acceptButton();
						} else {
							actualButton = nextButton();
						}
					} else {
						actualButton = nextButton();
					}
					getWorkflows();
					workflowIndex++;
				} else {
					// Forward to next status
					status = STATUS_CATEGORIES;
					showNextWizard();
				}
				break;
				
			case STATUS_CATEGORIES:
				if (Main.get().workspaceUserProperties.getWorkspace().isWizardCategories()) {
					if (!Main.get().workspaceUserProperties.getWorkspace().isWizardKeywords()) {
						actualButton = acceptButton();
					} else {
						actualButton = nextButton();
					}
					setCategories();
				} else {
					status = STATUS_KEYWORDS;
					showNextWizard();
				}
				break;
				
			case STATUS_KEYWORDS:
				if (Main.get().workspaceUserProperties.getWorkspace().isWizardKeywords()) {
					actualButton = acceptButton();
					setKeywords();
				} else {
					status = STATUS_FINISH;
					showNextWizard();
				}
				break;
			
			case STATUS_FINISH:
				hide();
				Main.get().fileUpload.resetAfterWizardFinished(); // Restoring wizard
				break;
		}
	}
	
	/**
	 * Accept button 
	 * 
	 * @return
	 */
	private Button acceptButton() {
		Button button = new Button(Main.i18n("button.accept"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				actualButton.setEnabled(false);
				executeActionButton();
			}
		});
		button.setStyleName("okm-Button");
		button.setEnabled(false);
		return button;
	}
	
	/**
	 * Next button 
	 * 
	 * @return
	 */
	private Button nextButton() {
		Button button = new Button(Main.i18n("button.next"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				actualButton.setEnabled(false);
				executeActionButton();
			}
		});
		button.setStyleName("okm-Button");
		button.setEnabled(false);
		return button;
	}
	
	private void executeActionButton() {
		switch (status) {
			case STATUS_PROPERTY_GROUPS:
				if (propertyGroupWidget!=null) {
					propertyGroupWidget.setProperties();
				}
				break;
				
			case STATUS_WORKFLOWS:
				if (workflowWidget!=null) {
					workflowWidget.runProcessDefinition();
				}
				break;
				
			case STATUS_CATEGORIES:
				status = STATUS_KEYWORDS;
				showNextWizard();
				break;
				
			case STATUS_KEYWORDS:
				status = STATUS_FINISH;
				showNextWizard();
				break;
		}
	}
	
	/**
	 * setCategories
	 */
	private void setCategories() {
		categoriesWidget = new CategoriesWidget(docPath, new HTML(Main.i18n("document.categories")));
		
		HorizontalPanel hPanel = new HorizontalPanel();
		HTML space = new HTML("");
		hPanel.add(actualButton);
		hPanel.add(space);
		hPanel.setCellWidth(space, "3");

		vPanelFired.clear();
		vPanelFired.add(categoriesWidget);
		vPanelFired.add(hPanel);
		HTML space2 = new HTML("");
		vPanelFired.add(space2);
		vPanelFired.setCellVerticalAlignment(categoriesWidget, HasAlignment.ALIGN_TOP);
		vPanelFired.setCellHorizontalAlignment(hPanel, HasAlignment.ALIGN_RIGHT);
		vPanelFired.setCellHeight(space2, "5");
		actualButton.setEnabled(true);
		changeView(); 
	}
	
	/**
	 * setKeywords
	 */
	private void setKeywords() {
		// To be implemented
		keywordsWidget = new KeywordsWidget(docPath, new HTML(Main.i18n("document.keywords")));
		
		HorizontalPanel hPanel = new HorizontalPanel();
		HTML space = new HTML("");
		hPanel.add(actualButton);
		hPanel.add(space);
		hPanel.setCellWidth(space, "3");

		vPanelFired.clear();
		vPanelFired.add(keywordsWidget);
		vPanelFired.add(hPanel);
		HTML space2 = new HTML("");
		vPanelFired.add(space2);
		vPanelFired.setCellVerticalAlignment(keywordsWidget, HasAlignment.ALIGN_TOP);
		vPanelFired.setCellHorizontalAlignment(hPanel, HasAlignment.ALIGN_RIGHT);
		vPanelFired.setCellHeight(space2, "5");
		actualButton.setEnabled(true);
		changeView();
	}
	
	/**
	 * changeView
	 * 
	 * Ensures fileupload is hiden and panel is centered
	 */
	public void changeView() {
		Main.get().fileUpload.hide();
		center();
	}
	
	/**
	 * FiredVerticalPanel
	 * 
	 * @author jllort
	 *
	 */
	private class FiredVerticalPanel extends Composite implements PropertyGroupWidgetToFire, WorkflowWidgetToFire {
		private VerticalPanel vPanel;
		
		/**
		 * FiredVerticalPanel
		 */
		public FiredVerticalPanel() {
			vPanel = new VerticalPanel();
			initWidget(vPanel);
			
		}
		
		@Override
		public void finishedGetProperties() {
			if (propertyGroupWidget!=null) {
				propertyGroupWidget.edit();
				actualButton.setEnabled(true);
			}
			changeView();
		}
		
		@Override
		public void finishedSetProperties() {
			showNextWizard();
		}
		
		@Override
		public void finishedRunProcessDefinition() {
			showNextWizard();
		}
		
		@Override
		public void hasPendingProcessDefinitionForms() {
			changeView();
			actualButton.setEnabled(true);
		}
		
		/**
		 * setCellHorizontalAlignment
		 * 
		 * @param w
		 * @param align
		 */
		public void setCellHorizontalAlignment(Widget w, HorizontalAlignmentConstant align) {
			vPanel.setCellHorizontalAlignment(w, align);
		}
		
		/**
		 * setCellHeight
		 * 
		 * @param w
		 * @param height
		 */
		public void setCellHeight(Widget w, String height) {
			vPanel.setCellHeight(w, height);
		}
		
		/**
		 * setCellVerticalAlignment
		 * 
		 * @param w
		 * @param align
		 */
		public void setCellVerticalAlignment(Widget w, VerticalAlignmentConstant align) {
			vPanel.setCellVerticalAlignment(w, align);
		}
		
		/**
		 * clear
		 */
		public void clear() {
			vPanel.clear();
		}
		
		/**
		 * add
		 * 
		 * @param widget
		 */
		public void add(Widget widget) {
			vPanel.add(widget);
		}
	}
	
	/**
	 * langRefresh
	 */
	public void langRefresh() {
		setText(Main.i18n("wizard.document.uploading"));
	}
	
	/**
	 * getDocumentToSign
	 * 
	 * @return
	 */
	public GWTDocument getDocumentToSign() {
		return docToSign;
	}
}