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

package com.openkm.frontend.client.widget.security;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabBar;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.openkm.frontend.client.Main;

/**
 * Security popup
 * 
 * @author jllort
 *
 */
public class SecurityPopup extends DialogBox implements ClickHandler {
	
	private static final int TAB_USERS 	= 0;
	private static final int TAB_GROUPS = 1;
	
	public Status status;
	private VerticalPanel vPanel;
	private TabPanel tabPanel;
	public SecurityUser securityUser;
	public SecurityRole securityRole;
	public CheckBox recursive;
	private Button button;
	private SimplePanel sp;
	private boolean filterView = false;
	private CheckBox checkBoxFilter;
	private TextBox filter;
	private HorizontalPanel filterPanel;
	private HTML filterText;
	private String usersFilter = "";
	private String groupsFilter = "";
	//vissu touch on oct'9th
		private boolean enableAdminitration = false;
	
	/**
	 * Security popup
	 */
	public SecurityPopup() {
		// Establishes auto-close when click outside
		super(false,true);
		
		status = new Status();
		vPanel = new VerticalPanel();
		securityUser = new SecurityUser();
		securityRole = new SecurityRole();
		tabPanel = new TabPanel();
		sp = new SimplePanel();
		recursive = new CheckBox(Main.i18n("security.recursive"));
		button = new Button(Main.i18n("button.close"), this);
		
		vPanel.setWidth("345");		//vissu touch to reduce width from 600 to 345
		vPanel.setHeight("330");	//vissu touch to reduce Height from 400 to 330
		sp.setHeight("4");
				
		tabPanel.add(securityUser, Main.i18n("security.users"));
		//vissu touch commenting below line to hide groups on oct'9th
		//tabPanel.add(securityRole, Main.i18n("security.groups"));
		tabPanel.selectTab(TAB_USERS);
		tabPanel.setWidth("100%");
		
		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				switch (event.getSelectedItem().intValue()) {
					case TAB_USERS:
						groupsFilter = filter.getText();
						filter.setText(usersFilter);
						filterText.setHTML(Main.i18n("security.filter.by.users"));
						securityUser.fillWidth();
						break;
					case TAB_GROUPS:
						usersFilter = filter.getText();
						filter.setText(groupsFilter);
						filterText.setHTML(Main.i18n("security.filter.by.groups"));
						securityRole.fillWidth();
						break;
				}
			}
		});
		
		filterPanel = new HorizontalPanel();
		filterPanel.setVisible(false);
		checkBoxFilter = new CheckBox();
		checkBoxFilter.setValue(false);
		checkBoxFilter.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				securityUser.resetUnassigned();
				securityRole.resetUnassigned();
				Widget sender = (Widget) event.getSource();
				if (((CheckBox) sender).getValue()) {
					filter.setText("");
					filter.setEnabled(true);
				} else {
					filter.setText("");
					filter.setEnabled(false);
					usersFilter = "";
					groupsFilter = "";
					refreshUnassigned();
				}
			}
		});
		filter = new TextBox();
		filterText = new HTML(Main.i18n("security.filter.by.users"));
		filterPanel.add(checkBoxFilter);
		filterPanel.add(new HTML("&nbsp;"));
		filterPanel.add(filterText);
		filterPanel.add(new HTML("&nbsp;"));
		filterPanel.add(filter);
		filterPanel.add(new HTML("&nbsp;"));
		
		filterPanel.setCellVerticalAlignment(checkBoxFilter, HasAlignment.ALIGN_MIDDLE);
		filterPanel.setCellVerticalAlignment(filterText, HasAlignment.ALIGN_MIDDLE);
		filterPanel.setCellVerticalAlignment(filter, HasAlignment.ALIGN_MIDDLE);
		
		filter.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (filter.getText().length()>=3) {
					TabBar tabBar = tabPanel.getTabBar();
					int selected = tabBar.getSelectedTab();
					switch(selected) {
						case TAB_USERS:
							securityUser.getFilteredUngrantedUsers(filter.getText());
							break;
							
						case TAB_GROUPS:
							securityRole.getFilteredUngrantedRoles(filter.getText());
							break;
					}
				} else {
					securityUser.resetUnassigned();
					securityRole.resetUnassigned();
				}
			}
		});
		
		vPanel.add(sp);
		vPanel.add(filterPanel);
		vPanel.add(tabPanel);
		vPanel.add(recursive);
		vPanel.add(button);
		
		vPanel.setCellHeight(sp, "4");
		vPanel.setCellHeight(tabPanel, "365");
		vPanel.setCellHeight(recursive, "25");
		vPanel.setCellHeight(button, "25");
		vPanel.setCellHorizontalAlignment(filterPanel, VerticalPanel.ALIGN_RIGHT);
		vPanel.setCellHorizontalAlignment(tabPanel, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellVerticalAlignment(tabPanel, VerticalPanel.ALIGN_TOP);
		vPanel.setCellHorizontalAlignment(button, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellVerticalAlignment(button, VerticalPanel.ALIGN_MIDDLE);
		
		button.setStyleName("okm-Button");
		filter.setStyleName("okm-Input");
		status.setStyleName("okm-StatusPopup");

		super.hide();
		setWidget(vPanel);
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
	 */
	public void onClick(ClickEvent event) {
		Main.get().mainPanel.desktop.browser.tabMultiple.securityRefresh();
		super.hide();
	}
	
	/**
	 * Language refresh
	 */
	public void langRefresh() {
		setText(Main.i18n("security.label"));
		recursive.setText(Main.i18n("security.recursive"));
		button.setText(Main.i18n("button.close"));

		TabBar tabBar = tabPanel.getTabBar();
		int selected = tabBar.getSelectedTab();
		
		while (tabPanel.getWidgetCount() > 0) {
			tabPanel.remove(0);
		}
		
		tabPanel.add(securityUser, Main.i18n("security.users"));
		//vissu touch added below line to enable group only for admin
		if(enableAdminitration)
			
		tabPanel.add(securityRole, Main.i18n("security.groups"));
		tabPanel.selectTab(selected);
		
		switch (selected) {
			case TAB_USERS:
				filterText.setHTML(Main.i18n("security.filter.by.users"));
				break;
			case TAB_GROUPS:
				filterText.setHTML(Main.i18n("security.filter.by.groups"));
				break;
		}
		
		securityUser.langRefresh();
		securityRole.langRefresh();
	}
	
	/**
	 * Show the security popup
	 */
	public void show(String path) {
		int left = (Window.getClientWidth()-325) / 2;	//vissu touch to set 600 as 325
		int top = (Window.getClientHeight()-330) / 2;
		setPopupPosition(left, top);
		setText(Main.i18n("security.label"));
		Main.get().securityPopup.securityUser.setPath(path);
		Main.get().securityPopup.securityRole.setPath(path);
		securityUser.reset();
		securityRole.reset();
		filter.setText("");
		usersFilter = "";
		groupsFilter = "";
		securityUser.getGrantedUsers();
		//commented by vissu on may18 for disable grantedroles in auth
		//securityRole.getGrantedRoles();
		if (!filterView) {
			securityUser.getUngrantedUsers();
			securityRole.getUngrantedRoles();
		} 
		super.show();
		
		// Fill width must be done on visible widgets
		securityUser.fillWidth();
		securityRole.fillWidth();
	}
	
	/**
	 * refreshUnassigned
	 */
	public void refreshUnassigned() {
		securityUser.getUngrantedUsers();
		securityRole.getUngrantedRoles();
	}
	
	//vissu touch the below function on oct'9th
		public void setVisible() {
			enableAdminitration = true;
			langRefresh();
		}	
		
	/**
	 * enableAdvancedFilter
	 */
	public void enableAdvancedFilter() {
		filterView = true;
		filterPanel.setVisible(true);
		checkBoxFilter.setValue(true);
	}
}