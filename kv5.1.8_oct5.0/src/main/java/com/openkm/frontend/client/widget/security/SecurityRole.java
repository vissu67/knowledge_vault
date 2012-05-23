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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTPermission;
import com.openkm.frontend.client.contants.service.RPCService;
import com.openkm.frontend.client.service.OKMAuthService;
import com.openkm.frontend.client.service.OKMAuthServiceAsync;
import com.openkm.frontend.client.util.RoleComparator;
import com.openkm.frontend.client.util.Util;

/**
 * Security Group
 * 
 * @author jllort
 *
 */
public class SecurityRole extends Composite implements HasWidgets {
	
	private final OKMAuthServiceAsync authService = (OKMAuthServiceAsync) GWT.create(OKMAuthService.class);
	
	// Number of columns
	public static final int ASSIGNED_ROLES_NUMBER_OF_COLUMNS	= 5;
	public static final int UNASSIGNED_ROLES_NUMBER_OF_COLUMNS	= 1;
	
	public RoleScrollTable assignedRole;
	public RoleScrollTable unassignedRole;
	private HorizontalPanel panel;
	private VerticalPanel buttonPanel;
	private SimplePanel spLeft;
	private SimplePanel spRight;
	private SimplePanel spHeight;
	private HTML addButton;
	private HTML removeButton;
	private String path = "";
	private String tmpRole = "";
	
	/**
	 * Security group
	 */
	public SecurityRole() {
		panel = new HorizontalPanel();
		buttonPanel = new VerticalPanel();
		assignedRole = new RoleScrollTable(true);
		unassignedRole = new RoleScrollTable(false);
		spLeft = new SimplePanel();
		spRight = new SimplePanel();
		spHeight = new SimplePanel();
		spLeft.setWidth("4");
		spRight.setWidth("1");
		spHeight.setHeight("30");
		addButton = new HTML(Util.imageHTML("img/icon/security/add.gif"));
		removeButton = new HTML(Util.imageHTML("img/icon/security/remove.gif"));
		
		buttonPanel.add(addButton);
		buttonPanel.add(spHeight); // separator
		buttonPanel.add(removeButton);
		
		addButton.addClickHandler(addButtonListener);
		removeButton.addClickHandler(removeButtonListener);
		
		panel.add(spLeft);
		panel.add(assignedRole);
		panel.add(buttonPanel);
		panel.add(unassignedRole);

		panel.setCellWidth(buttonPanel, "20");
		panel.setCellWidth(spLeft, "4");
		panel.setCellVerticalAlignment(buttonPanel,HasAlignment.ALIGN_MIDDLE);
		panel.setCellHorizontalAlignment(buttonPanel,HasAlignment.ALIGN_CENTER);
		
		panel.setSize("593", "365");
		
		initWidget(panel);
	}
	
	/**
	 * Add button listener
	 */
	ClickHandler addButtonListener = new ClickHandler() { 
		@Override
		public void onClick(ClickEvent event) {
			if (unassignedRole.getRole() != null) {
				tmpRole = unassignedRole.getRole();
				addRole(tmpRole, GWTPermission.READ, Main.get().securityPopup.recursive.getValue());
			}
		}
	};
	
	/**
	 * Remove button listener
	 */
	ClickHandler removeButtonListener = new ClickHandler() { 
		@Override
		public void onClick(ClickEvent event) {
			if (assignedRole.getRole() != null) {
				tmpRole = assignedRole.getRole();
				revokeRole(tmpRole, Main.get().securityPopup.recursive.getValue());
			}
		}
	};
	
	/**
	 * Language refresh
	 */
	public void langRefresh() {
		assignedRole.langRefresh();
		unassignedRole.langRefresh();
	}
	
	/**
	 * Resets the values
	 */
	public void reset() {
		assignedRole.reset();
		unassignedRole.reset();
		assignedRole.getDataTable().resize(0, ASSIGNED_ROLES_NUMBER_OF_COLUMNS);
		unassignedRole.getDataTable().resize(0, UNASSIGNED_ROLES_NUMBER_OF_COLUMNS);
	}
	
	/**
	 * resetUnassigned
	 */
	public void resetUnassigned() {
		unassignedRole.reset();
		unassignedRole.getDataTable().resize(0, UNASSIGNED_ROLES_NUMBER_OF_COLUMNS);
	}
	
	/**
	 * Call back get granted roles
	 */
	final AsyncCallback<Map<String, Byte>> callbackGetGrantedRoles = new AsyncCallback<Map<String, Byte>>() {
		public void onSuccess(Map<String, Byte> result) {
			List<String> rolesList = new ArrayList<String>();
			
			// Ordering grant roles to list
			for (Iterator<String> it = result.keySet().iterator(); it.hasNext(); ) {
				rolesList.add(it.next());
			}
			Collections.sort(rolesList, RoleComparator.getInstance());
			
			for (Iterator<String> it = rolesList.iterator(); it.hasNext(); ) {
				String groupName = it.next();
				Byte permission = (Byte) result.get(groupName);
				assignedRole.addRow(groupName, permission);
			}
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("GetGrantedRoles", caught);
		}
	};
	
	/**
	 * Call back get ungranted roles
	 */
	final AsyncCallback<List<String>> callbackGetUngrantedRoles = new AsyncCallback<List<String>>() {
		public void onSuccess(List<String> result) {
			for (Iterator<String> it = result.iterator(); it.hasNext(); ) {
				String groupName = (String) it.next();
				unassignedRole.addRow(groupName);
			}
		}

		public void onFailure(Throwable caught) {
			Main.get().showError("GetUngrantedRoles", caught);
		}
	};
	
	/**
	 * Call back add new granted role
	 */
	final AsyncCallback<Object> callbackAddRole = new AsyncCallback<Object>() {
		public void onSuccess(Object result){
			assignedRole.addRow(tmpRole, new Byte(GWTPermission.READ));
			unassignedRole.removeSelectedRow();
			tmpRole = "";
			Main.get().securityPopup.status.unsetFlag_update();
		}

		public void onFailure(Throwable caught) {
			Main.get().securityPopup.status.unsetFlag_update();
			Main.get().showError("AddRole", caught);
		}
	};
	
	/**
	 * Call back revoke granted role
	 */
	final AsyncCallback<Object> callbackRevokeRole = new AsyncCallback<Object>() {
		public void onSuccess(Object result){
			unassignedRole.addRow(tmpRole);
			unassignedRole.selectLastRow();
			assignedRole.removeSelectedRow();
			tmpRole = "";
			Main.get().securityPopup.status.unsetFlag_update();
		}

		public void onFailure(Throwable caught) {
			Main.get().securityPopup.status.unsetFlag_update();
			Main.get().showError("RevokeRole", caught);
		}
	};
	
	/**
	 * Gets the granted roles
	 */
	public void getGrantedRoles() {
		if (path != null) {
			ServiceDefTarget endPoint = (ServiceDefTarget) authService;
			endPoint.setServiceEntryPoint(RPCService.AuthService);	
			authService.getGrantedRoles(path, callbackGetGrantedRoles);
		}
	}
	
	/**
	 * Gets the granted roles
	 */
	public void getUngrantedRoles() {
		if (path != null) {
			ServiceDefTarget endPoint = (ServiceDefTarget) authService;
			endPoint.setServiceEntryPoint(RPCService.AuthService);	
			authService.getUngrantedRoles(path, callbackGetUngrantedRoles);
		}
	}
	
	/**
	 * Gets the granted roles by filter
	 */
	public void getFilteredUngrantedRoles(String filter) {
		if (path != null) {
			resetUnassigned();
			ServiceDefTarget endPoint = (ServiceDefTarget) authService;
			endPoint.setServiceEntryPoint(RPCService.AuthService);	
			authService.getFilteredUngrantedRoles(path, filter, callbackGetUngrantedRoles);
		}
	}
	
	/**
	 * Grant the role
	 * 
	 * @param role The granted role
	 * @param permissions The permissions value
	 */
	public void addRole(String role, int permissions, boolean recursive) {
		if (path != null) {
			Main.get().securityPopup.status.setFlag_update();
			ServiceDefTarget endPoint = (ServiceDefTarget) authService;
			endPoint.setServiceEntryPoint(RPCService.AuthService);	
			authService.grantRole(path, role, permissions, recursive, callbackAddRole);
		}
	}
	
	/**
	 * Revokes all role permissions
	 * 
	 * @param user The role
	 */
	public void revokeRole(String role, boolean recursive) {
		if (path != null) {
			Main.get().securityPopup.status.setFlag_update();
			ServiceDefTarget endPoint = (ServiceDefTarget) authService;
			endPoint.setServiceEntryPoint(RPCService.AuthService);	
			authService.revokeRole(path, role, recursive, callbackRevokeRole);
		}
	}
	
	/**
	 * Sets the path
	 * 
	 * @param path The path
	 */
	public void setPath(String path) {
		assignedRole.setPath(path);
		this.path = path;
	}
	
	/**
	 * fillWidth
	 */
	public void fillWidth() {
		assignedRole.fillWidth();
		unassignedRole.fillWidth();
	}
	
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.HasWidgets#add(com.google.gwt.user.client.ui.Widget)
	 */
	public void add(Widget w) {
	}
	
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.HasWidgets#clear()
	 */
	public void clear() {
	}
	
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.HasWidgets#iterator()
	 */
	public Iterator<Widget> iterator() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.HasWidgets#remove(com.google.gwt.user.client.ui.Widget)
	 */
	public boolean remove(Widget w) {
		return true;
	}
}