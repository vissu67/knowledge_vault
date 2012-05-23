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

package com.openkm.frontend.client.widget.thesaurus;

import java.util.Iterator;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.contants.service.RPCService;
import com.openkm.frontend.client.service.OKMThesaurusService;
import com.openkm.frontend.client.service.OKMThesaurusServiceAsync;

/**
 * ThesaurusSelectPopup
 * 
 * @author jllort
 *
 */
public class ThesaurusSelectPopup extends DialogBox  {	
	
	private final OKMThesaurusServiceAsync thesaurusService = (OKMThesaurusServiceAsync) GWT.create(OKMThesaurusService.class);
	
	public static final int NONE 				= -1;
	public static final int DOCUMENT_PROPERTIES = 0;
	public static final int WIZARD			 	= 1;
	
	private final int TAB_TREE 		= 0;
	private final int TAB_KEYWORDS 	= 1;
	
	private VerticalPanel vPanel;
	private HorizontalPanel hPanel;
	public ScrollPanel scrollDirectoryPanel;
	public ScrollPanel scrollKeywordPanel;
	private VerticalPanel verticalDirectoryPanel;
	private FolderSelectTree folderSelectTree;
	private Button cancelButton;
	private Button actionButton;
	public Status status;
	private TextBox keyword;
	private VerticalPanel vPanelKeyword;
	private FlexTable keywordTable;
	public TabPanel tabPanel;
	private int selectedRow = -1;
	private int selectedTab = TAB_TREE;
	private int selectedFrom = NONE;
	
	/**
	 * ThesaurusSelectPopup
	 */
	public ThesaurusSelectPopup() {
		// Establishes auto-close when click outside
		super(false,true);
		
		status = new Status();
		status.setStyleName("okm-StatusPopup");
		
		tabPanel = new TabPanel();
		tabPanel.setSize("290", "175");
		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				selectedTab = event.getSelectedItem().intValue();
				evaluateEnableAction();
			}
		});
		
		
		vPanel = new VerticalPanel();		
		vPanel.setWidth("300");
		vPanel.setHeight("225");
		hPanel = new HorizontalPanel();
		
		scrollDirectoryPanel = new ScrollPanel();
		scrollDirectoryPanel.setSize("100%", "100%");
		scrollDirectoryPanel.setStyleName("okm-Popup-text");
		scrollKeywordPanel = new ScrollPanel();
		scrollKeywordPanel.setStyleName("okm-Popup-text");
		verticalDirectoryPanel = new VerticalPanel();
		folderSelectTree = new FolderSelectTree();
		folderSelectTree.setSize("100%", "100%");
				
		verticalDirectoryPanel.add(folderSelectTree);
		scrollDirectoryPanel.add(verticalDirectoryPanel);
		
		cancelButton = new Button(Main.i18n("button.close"), new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		actionButton = new Button(Main.i18n("button.add"), new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				if (selectedTab==TAB_TREE) {
					executeAction(folderSelectTree.getActualPath());
				} else {
					executeAction(keywordTable.getText(selectedRow, 0));
				}
			}
		});
		
		keyword = new TextBox();
		keyword.setWidth("292");
		keyword.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (keyword.getText().length()>=3) {
					getKeywords(keyword.getText().toLowerCase());
				} else {
					removeAllRows();
				}
			}
		});
		keywordTable = new FlexTable();
		keywordTable.setWidth("100%");
		keywordTable.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				markSelectedRow(keywordTable.getCellForEvent(event).getRowIndex());
				evaluateEnableAction();
			}
		});
		
		scrollKeywordPanel.add(keywordTable);
		vPanelKeyword = new VerticalPanel();
		vPanelKeyword.add(keyword);
		vPanelKeyword.add(scrollKeywordPanel);
		
		vPanelKeyword.setCellHeight(keyword, "25");
		vPanelKeyword.setCellVerticalAlignment(keyword, HasAlignment.ALIGN_MIDDLE);
		
		tabPanel.add(scrollDirectoryPanel, Main.i18n("thesaurus.tab.tree"));
		tabPanel.add(vPanelKeyword, Main.i18n("thesaurus.tab.keywords"));
		tabPanel.selectTab(TAB_TREE);
		
		scrollDirectoryPanel.setPixelSize(290,175);
		scrollKeywordPanel.setPixelSize(290,150);
		
		vPanel.add(tabPanel);
		vPanel.add(new HTML("<br>"));
		hPanel.add(cancelButton);
		HTML space = new HTML();
		space.setWidth("50");
		hPanel.add(space);
		hPanel.add(actionButton);
		vPanel.add(hPanel);
		vPanel.add(new HTML("<br>"));
		
		vPanel.setCellHorizontalAlignment(tabPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHeight(tabPanel, "150");

		cancelButton.setStyleName("okm-Button");
		actionButton.setStyleName("okm-Button");
		keyword.setStyleName("okm-Input");
		keywordTable.setStyleName("okm-NoWrap");
		keywordTable.addStyleName("okm-Table-Row");

		super.hide();
		setWidget(vPanel);
	}
	
	/**
	 * Executes the action
	 */
	public void executeAction(String actualPath) {
		String keyword = actualPath.substring(actualPath.lastIndexOf("/")+1).replace(" ", "_");
		switch (selectedFrom) {
			case DOCUMENT_PROPERTIES:
				Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.document.addKeywordToPendinList(keyword);
				Main.get().mainPanel.desktop.browser.tabMultiple.tabDocument.document.addPendingKeyWordsList();
				break;
			case WIZARD:
				Main.get().wizardPopup.keywordsWidget.addKeywordToPendinList(keyword);
				Main.get().wizardPopup.keywordsWidget.addPendingKeyWordsList();
				break;
		}
	}
	
	/**
	 * Language refresh
	 */
	public void langRefresh() {
		TabBar tabBar = tabPanel.getTabBar();
		selectedTab = tabBar.getSelectedTab();
		
		while (tabPanel.getWidgetCount() > 0) {
			tabPanel.remove(0);
		}
		tabPanel.add(scrollDirectoryPanel, Main.i18n("thesaurus.tab.tree"));
		tabPanel.add(scrollKeywordPanel, Main.i18n("thesaurus.tab.keywords"));
		tabPanel.selectTab(selectedTab);
		scrollDirectoryPanel.setPixelSize(290,175);
		scrollKeywordPanel.setPixelSize(290,150);
		
		setText(Main.i18n("thesaurus.directory.select.label"));
		cancelButton.setText(Main.i18n("button.close"));
		actionButton.setText(Main.i18n("button.add"));		
	}
	
	/**
	 * Shows the popup 
	 */
	public void show(int selectedFrom){
		this.selectedFrom = selectedFrom;
		initButtons();
		int left = (Window.getClientWidth()-300) / 2;
		int top = (Window.getClientHeight()-225) / 2;
		setPopupPosition(left, top);
		setText(Main.i18n("thesaurus.directory.select.label"));
		
		// Resets to initial tree value
		folderSelectTree.reset();
		removeAllRows();
		keyword.setText("");
		evaluateEnableAction();
		center();
	}
	
	/**
	 * Enables or disables move button
	 * 
	 * @param enable
	 */
	public void enable(boolean enable) {
		actionButton.setEnabled(enable);
	}
	
	/**
	 * Enables all button
	 */
	private void initButtons() {
		cancelButton.setEnabled(true);
		actionButton.setEnabled(false);
	}
	
	/**
	 * Gets asyncronous root node
	 */
	final AsyncCallback<List<String>> callbackGetKeywords = new AsyncCallback<List<String>>() {
		public void onSuccess(List<String> result) {
			removeAllRows();
			for (Iterator<String> it = result.iterator(); it.hasNext();) {
				keywordTable.setHTML(keywordTable.getRowCount(), 0, it.next());
			}
			status.unsetFlagKeywords();
		}

		public void onFailure(Throwable caught) {
			status.unsetFlagKeywords();
			Main.get().showError("getKeywords", caught);
		}
	};
	
	/**
	 * Gets the root
	 */
	public void getKeywords(String filter) {
		ServiceDefTarget endPoint = (ServiceDefTarget) thesaurusService;
		endPoint.setServiceEntryPoint(RPCService.ThesaurusService);	
		status.setFlagKeywords();
		thesaurusService.getKeywords(filter, callbackGetKeywords);
	}
	
	/**
	 * removeAllRows
	 */
	private void removeAllRows() {
		selectedRow = -1;
		evaluateEnableAction();
		while (keywordTable.getRowCount()>0) {
			keywordTable.removeRow(0);
		}
	}
	
	/**
	 * markSelectedRow
	 * 
	 * @param row
	 */
	private void markSelectedRow(int row) {
		// And row must be other than the selected one
		if (row != selectedRow) {
			styleRow(selectedRow, false);
			styleRow(row, true);
			selectedRow = row;
		}
	}
	
	/**
	 * Change the style row selected or unselected
	 * 
	 * @param row The row afected
	 * @param selected Indicates selected unselected row
	 */
	private void styleRow(int row, boolean selected) {
		if (row>=0) {
			if (selected) {
				keywordTable.getRowFormatter().addStyleName(row, "okm-Table-SelectedRow");
		    } else {
		    	keywordTable.getRowFormatter().removeStyleName(row, "okm-Table-SelectedRow");
		    }
		}
	 }
	
	/**
	 * evaluateEnableAction
	 */
	private void evaluateEnableAction() {
		switch (selectedTab) {
			case TAB_TREE:
				enable(folderSelectTree.evaluateEnableActionButton());
				break;
				
			case TAB_KEYWORDS:
				enable(selectedRow>=0);
				break;
		}
	}
}