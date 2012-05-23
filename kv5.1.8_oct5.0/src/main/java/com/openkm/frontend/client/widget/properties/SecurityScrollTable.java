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

package com.openkm.frontend.client.widget.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.gen2.table.client.ScrollTable;
import com.google.gwt.gen2.table.client.SelectionGrid;
import com.google.gwt.gen2.table.client.AbstractScrollTable.ResizePolicy;
import com.google.gwt.gen2.table.client.AbstractScrollTable.ScrollPolicy;
import com.google.gwt.gen2.table.client.AbstractScrollTable.ScrollTableImages;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Image;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTPermission;
import com.openkm.frontend.client.contants.service.RPCService;
import com.openkm.frontend.client.service.OKMAuthService;
import com.openkm.frontend.client.service.OKMAuthServiceAsync;
import com.openkm.frontend.client.util.RoleComparator;
import com.openkm.frontend.client.util.UserComparator;
import com.openkm.frontend.client.util.Util;

/**
 * SecurityScrollTable
 * 
 * @author jllort
 *
 */
public class SecurityScrollTable extends Composite implements ClickHandler  {
	
	private final OKMAuthServiceAsync authService = (OKMAuthServiceAsync) GWT.create(OKMAuthService.class);
	
	// Number of columns
	public static final int NUMBER_OF_COLUMNS = 11;
	
	private String path;	
	private ScrollTable table;
	private FixedWidthFlexTable headerTable;
	private FixedWidthGrid dataTable;
	private Button button;
	private String withPermission = "img/icon/security/yes.gif";
	private String withoutPermission = "img/icon/security/no.gif";
	private int userRow = 0;
	private int rolRow 	= 0;
	
	/**
	 * SecurityScrollTable
	 */
	public SecurityScrollTable() {
		ScrollTableImages scrollTableImages = new ScrollTableImages(){
			public AbstractImagePrototype scrollTableAscending() {
				return new AbstractImagePrototype() {
					public void applyTo(Image image) {
						image.setUrl("img/sort_asc.gif");
					}
					public Image createImage() {
						return  new Image("img/sort_asc.gif");
					}
					public String getHTML(){
						return "<img border=\"0\" src=\"img/sort_asc.gif\"/>";
					}
				};
			}
			
			public AbstractImagePrototype scrollTableDescending() {
				return new AbstractImagePrototype() {
					public void applyTo(Image image) {
						image.setUrl("img/sort_desc.gif");
					}
					public Image createImage() {
						return  new Image("img/sort_desc.gif");
					}
					public String getHTML(){
						return "<img border=\"0\" src=\"img/sort_desc.gif\"/>";
					}
				};
			}

			public AbstractImagePrototype scrollTableFillWidth() {
				return new AbstractImagePrototype() {
					public void applyTo(Image image) {
						image.setUrl("img/fill_width.gif");
					}
					public Image createImage() {
						return  new Image("img/fill_width.gif");
					}
					public String getHTML(){
						return "<img border=\"0\" src=\"img/fill_width.gif\"/>";
					}
				};
			}
		};
		
		headerTable = new FixedWidthFlexTable();
		dataTable = new FixedWidthGrid();
		
		table = new ScrollTable(dataTable,headerTable,scrollTableImages);
		table.setCellSpacing(0);
		table.setCellPadding(2);
		table.setSize("540","140");
		table.setColumnWidth(0,110);
	    table.setColumnWidth(1,90);
	    table.setColumnWidth(2,90);
	    table.setColumnWidth(3,90);
	    table.setColumnWidth(4,90);
	    table.setColumnWidth(5,80);
	    table.setColumnWidth(6,110);
	    table.setColumnWidth(7,90);
	    table.setColumnWidth(8,90);
	    table.setColumnWidth(9,90);
	    table.setColumnWidth(10,90);
	    
	    table.setPreferredColumnWidth(0, 110);
		table.setPreferredColumnWidth(1, 90);
		table.setPreferredColumnWidth(6, 110);
		
		table.setColumnSortable(5, false);
	    
		button = new Button(Main.i18n("button.update"), this);
		button.setStyleName("okm-Button");
		
		// Level 1 headers		
	
		//vissu touch to hide groups on oct'9th
	   /* headerTable.setHTML(0, 0, Main.i18n("security.group.name"));
	    headerTable.setHTML(0, 1, Main.i18n("security.group.permission.read"));
	    headerTable.setHTML(0, 2, Main.i18n("security.group.permission.write"));
	    headerTable.setHTML(0, 3, Main.i18n("security.group.permission.delete"));
	    headerTable.setHTML(0, 4, Main.i18n("security.group.permission.security"));	*/
		
	    headerTable.setWidget(0, 5, button);
	    headerTable.setHTML(0, 6, Main.i18n("security.user.name"));
	    headerTable.setHTML(0, 7, Main.i18n("security.user.permission.read"));
	    headerTable.setHTML(0, 8, Main.i18n("security.user.permission.write"));
	    headerTable.setHTML(0, 9, Main.i18n("security.user.permission.delete"));
	    headerTable.setHTML(0, 10, Main.i18n("security.user.permission.security"));
	    
	    headerTable.getCellFormatter().setHorizontalAlignment(0,5,HasAlignment.ALIGN_CENTER);
	    headerTable.getCellFormatter().setVerticalAlignment(0,5,HasAlignment.ALIGN_MIDDLE);

	    // Table data
	    dataTable.setSelectionPolicy(SelectionGrid.SelectionPolicy.ONE_ROW);
	    table.setResizePolicy(ResizePolicy.UNCONSTRAINED);
	    table.setScrollPolicy(ScrollPolicy.BOTH);
	    
	    headerTable.addStyleName("okm-DisableSelect");
	    dataTable.addStyleName("okm-DisableSelect");
		
		initWidget(table);
	}
	
	/**
	 * Sets the document or folder ID
	 * 
	 * @param path The document or folder ID
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	/**
	 * Lang refresh
	 */
	public void langRefresh() {
		headerTable.setHTML(0, 0, Main.i18n("security.group.name"));
		headerTable.setHTML(0, 1, Main.i18n("security.group.permission.read"));
		headerTable.setHTML(0, 2, Main.i18n("security.group.permission.write"));
		headerTable.setHTML(0, 3, Main.i18n("security.group.permission.delete"));
		headerTable.setHTML(0, 4, Main.i18n("security.group.permission.security"));
		button.setText(Main.i18n("button.update"));
		headerTable.setHTML(0, 6, Main.i18n("security.user.name"));
		headerTable.setHTML(0, 7, Main.i18n("security.user.permission.read"));
		headerTable.setHTML(0, 8, Main.i18n("security.user.permission.write"));
		headerTable.setHTML(0, 9, Main.i18n("security.user.permission.delete"));
		headerTable.setHTML(0, 10, Main.i18n("security.user.permission.security"));
	}
	
	/**
	 * Removes all rows except the first
	 */
	private void removeAllRows() {
		userRow = 0;
		rolRow = 0;
		// Purge all rows except first
		while (dataTable.getRowCount() > 0) {
			dataTable.removeRow(0);
		}
		dataTable.resize(0, NUMBER_OF_COLUMNS);
	}
	
	/**
	 * Adds a new user row
	 * 
	 * @param userName The user name value
	 * @param permission The permission value
	 */
	private void addUserRow(String userName, Byte permission) {
		//vissu touch if condition to filter displaying system and okmAdmin on oct'9th
		if(userName!="system" && userName!="okmAdmin" ){
		int rows = userRow++;
		if (dataTable.getRowCount()<=rows) {
			dataTable.insertRow(rows);
		}
		
		dataTable.setHTML(rows, 6, userName);
		
		if ((permission.byteValue() & GWTPermission.READ) == GWTPermission.READ) {
			dataTable.setHTML(rows, 7, Util.imageItemHTML(withPermission,""));
		} else {
			dataTable.setHTML(rows, 7, Util.imageItemHTML(withoutPermission,""));
		}
		
		if ((permission.byteValue() & GWTPermission.WRITE) == GWTPermission.WRITE) {
			dataTable.setHTML(rows, 8, Util.imageItemHTML(withPermission,""));
		} else {
			dataTable.setHTML(rows, 8, Util.imageItemHTML(withoutPermission,""));
		}
		
		if ((permission.byteValue() & GWTPermission.DELETE) == GWTPermission.DELETE) {
			dataTable.setHTML(rows, 9, Util.imageItemHTML(withPermission,""));
		} else {
			dataTable.setHTML(rows, 9, Util.imageItemHTML(withoutPermission,""));
		}
		
		if ((permission.byteValue() & GWTPermission.SECURITY) == GWTPermission.SECURITY) {
			dataTable.setHTML(rows, 10, Util.imageItemHTML(withPermission,""));
		} else {
			dataTable.setHTML(rows, 10, Util.imageItemHTML(withoutPermission,""));
		}
		
		dataTable.getCellFormatter().setHorizontalAlignment(rows,7,HasAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows,8,HasAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows,9,HasAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows,10,HasAlignment.ALIGN_CENTER);
		}
	}
		
	
	/**
	 * Adds a new group row
	 * 
	 * @param groupName The group value name
	 * @param permission The permission value
	 */
	private void addRolRow(String groupName, Byte permission) {
		//vissu touch commented out the function to not show group permissions on oct'9th
	/*	int rows = rolRow++; 
		if (dataTable.getRowCount()<=rows) {
			dataTable.insertRow(rows);
		}
		
		dataTable.setHTML(rows, 0, groupName);
		
		if ((permission.byteValue() & GWTPermission.READ) == GWTPermission.READ) {
			dataTable.setHTML(rows, 1, Util.imageItemHTML(withPermission,""));
		} else {
			dataTable.setHTML(rows, 1, "O");
			dataTable.setHTML(rows, 1, Util.imageItemHTML(withoutPermission,""));
		}
		
		if ((permission.byteValue() & GWTPermission.WRITE) == GWTPermission.WRITE) {
			dataTable.setHTML(rows, 2, Util.imageItemHTML(withPermission,""));
		} else {
			dataTable.setHTML(rows, 2, Util.imageItemHTML(withoutPermission,""));
		}
		
		if ((permission.byteValue() & GWTPermission.DELETE) == GWTPermission.DELETE) {
			dataTable.setHTML(rows, 3, Util.imageItemHTML(withPermission,""));
		} else {
			dataTable.setHTML(rows, 3, Util.imageItemHTML(withoutPermission,""));
		}
		
		if ((permission.byteValue() & GWTPermission.SECURITY) == GWTPermission.SECURITY) {
			dataTable.setHTML(rows, 4, Util.imageItemHTML(withPermission,""));
		} else {
			dataTable.setHTML(rows, 4, Util.imageItemHTML(withoutPermission,""));
		}
		
		dataTable.getCellFormatter().setHorizontalAlignment(rows,1,HasAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows,2,HasAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows,3,HasAlignment.ALIGN_CENTER);
		dataTable.getCellFormatter().setHorizontalAlignment(rows,4,HasAlignment.ALIGN_CENTER);	*/
	}
	
	/**
	 * Call back get granted users
	 */
	final AsyncCallback<Map<String,Byte>> callbackGetGrantedUsers = new AsyncCallback<Map<String,Byte>>() {
		public void onSuccess(Map<String,Byte> result){
			List<String> usersList = new ArrayList<String>();
			
			// Ordering grant roles to list
			for (Iterator<String> it = result.keySet().iterator(); it.hasNext(); ) {
				usersList.add(it.next());
			}
			Collections.sort(usersList, UserComparator.getInstance());
			
			for (Iterator<String> it = usersList.iterator(); it.hasNext(); ) {
				String userName = it.next();
				Byte permission = (Byte) result.get(userName);
				addUserRow(userName, permission);
			}
			
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetUserSecurity();
		}

		public void onFailure(Throwable caught){
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetUserSecurity();
			Main.get().showError("GetGrantedUsers", caught);
		}
	};
	
	/**
	 * Call back get granted roles
	 */
	final AsyncCallback<Map<String,Byte>> callbackGetGrantedRoles = new AsyncCallback<Map<String,Byte>>() {
		public void onSuccess(Map<String,Byte> result){
			List<String> rolesList = new ArrayList<String>();
			
			// Ordering grant roles to list
			for (Iterator<String> it = result.keySet().iterator(); it.hasNext(); ) {
				rolesList.add(it.next());
			}
			Collections.sort(rolesList, RoleComparator.getInstance());
			
			for (Iterator<String> it = rolesList.iterator(); it.hasNext(); ) {
				String groupName = it.next();
				Byte permission = (Byte) result.get(groupName);
				addRolRow(groupName, permission);
			}
			
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetRoleSecurity();
		}

		public void onFailure(Throwable caught) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.unsetRoleSecurity();
			Main.get().showError("GetGrantedRoles", caught);
		}
	};
	
	/**
	 * Gets the granted users
	 */
	private void getGrantedUsers() {
		if (path != null) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.setUserSecurity();
			ServiceDefTarget endPoint = (ServiceDefTarget) authService;
			endPoint.setServiceEntryPoint(RPCService.AuthService);	
			authService.getGrantedUsers(path, callbackGetGrantedUsers);
		}
	}
	
	/**
	 * Gets the granted roles
	 */
	private void getGrantedRoles() {
		removeAllRows();
		if (path != null) {
			Main.get().mainPanel.desktop.browser.tabMultiple.status.setRoleSecurity();
			ServiceDefTarget endPoint = (ServiceDefTarget) authService;
			endPoint.setServiceEntryPoint(RPCService.AuthService);	
			authService.getGrantedRoles(path, callbackGetGrantedRoles);
		}
	}
	
	/**
	 * Sets visibility to buttons ( true / false )
	 * 
	 * @param visible The visible value
	 */
	public void setVisibleButtons(boolean visible){
		button.setVisible(visible);
	}
	
	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
	 */
	public void onClick(ClickEvent event) {
		Main.get().securityPopup.show(path);
	}
	
	/**
	 * Sets the change permission
	 * 
	 * @param permission The permission value
	 */
	public void setChangePermision(boolean permission) {
		button.setEnabled(permission);
	}
	
	/**
	 * Get grants
	 */
	public void GetGrants(){
		removeAllRows();
		getGrantedUsers();
		//getGrantedRoles();	//commented by vissu on may18 for disable grantedroles in auth
	}
	
	/**
	 * fillWidth
	 */
	public void fillWidth() {
		table.fillWidth();
	}
}