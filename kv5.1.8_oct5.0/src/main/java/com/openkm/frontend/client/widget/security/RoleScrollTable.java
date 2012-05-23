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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.gen2.table.client.FixedWidthFlexTable;
import com.google.gwt.gen2.table.client.FixedWidthGrid;
import com.google.gwt.gen2.table.client.ScrollTable;
import com.google.gwt.gen2.table.client.SelectionGrid;
import com.google.gwt.gen2.table.client.AbstractScrollTable.ResizePolicy;
import com.google.gwt.gen2.table.client.AbstractScrollTable.ScrollPolicy;
import com.google.gwt.gen2.table.client.AbstractScrollTable.ScrollTableImages;

import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTPermission;
import com.openkm.frontend.client.contants.service.RPCService;
import com.openkm.frontend.client.service.OKMAuthService;
import com.openkm.frontend.client.service.OKMAuthServiceAsync;

/**
 * RoleScrollTable
 * 
 * @author jllort
 *
 */
public class RoleScrollTable extends Composite {
	
	private final OKMAuthServiceAsync authService = (OKMAuthServiceAsync) GWT.create(OKMAuthService.class);
	
	private final int PROPERTY_READ 	= 0;
	private final int PROPERTY_WRITE 	= 1;
	private final int PROPERTY_DELETE 	= 2;
	private final int PROPERTY_SECURITY	= 3;
	
	private ScrollTable table;
	private FixedWidthFlexTable headerTable;
	private FixedWidthGrid dataTable;
	private boolean isAssigned = false;  // Determines if is assigned users table or not
	private int rowIndex = -1;
	private String path;
	private int flag_property;
	
	/**
	 * RoleScrollTable
	 * 
	 * @param isAssigned
	 */
	public RoleScrollTable(boolean isAssigned) {
		this.isAssigned = isAssigned;
		
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
		
		// Level 1 headers
		if (isAssigned) {
			table.setSize("405","365");
			headerTable.setHTML(0, 0, Main.i18n("security.group.name"));
			headerTable.setHTML(0, 1, Main.i18n("security.group.permission.read"));
			headerTable.setHTML(0, 2, Main.i18n("security.group.permission.write"));
			headerTable.setHTML(0, 3, Main.i18n("security.group.permission.delete"));
			headerTable.setHTML(0, 4, Main.i18n("security.group.permission.security"));
			table.setColumnWidth(0,175);
			table.setColumnWidth(1,55);
			table.setColumnWidth(2,55);
			table.setColumnWidth(3,55);
			table.setColumnWidth(4,55);
		} else {
			table.setSize("185","365");
			headerTable.setHTML(0, 0, Main.i18n("security.group.name"));
			table.setColumnWidth(0,167);
		}
		
		// Table data
	    dataTable.setSelectionPolicy(SelectionGrid.SelectionPolicy.ONE_ROW);
	    table.setResizePolicy(ResizePolicy.UNCONSTRAINED);
	    table.setScrollPolicy(ScrollPolicy.BOTH);
	    
	    initWidget(table);
	}
		
	/**
	 * Lang refresh
	 */
	public void langRefresh() {
		if (isAssigned) {
			headerTable.setHTML(0, 0, Main.i18n("security.group.name"));
			headerTable.setHTML(0, 1, Main.i18n("security.group.permission.read"));
			headerTable.setHTML(0, 2, Main.i18n("security.group.permission.write"));
			headerTable.setHTML(0, 3, Main.i18n("security.group.permission.delete"));
			headerTable.setHTML(0, 4, Main.i18n("security.group.permission.security"));
		} else {
			headerTable.setHTML(0, 0, Main.i18n("security.group.name"));
		}
	}
	
	/**
	 * Adds new username permission row
	 * 
	 * @param userName The role name value
	 * @param permission The permission value
	 */
	public void addRow(String roleName, Byte permission) {
		final int rows = dataTable.getRowCount();
		dataTable.insertRow(rows);
		dataTable.setHTML(rows, 0, roleName);
		
		CheckBox checkReadPermission = new CheckBox();
		CheckBox checkWritePermission = new CheckBox();
		CheckBox checkDeletePermission = new CheckBox();
		CheckBox checkSecurityPermission = new CheckBox();
		
		ClickHandler checkBoxReadListener = new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				flag_property = PROPERTY_READ;
				Widget sender = (Widget) event.getSource();
				
				// Actions are inverse to check value because before user perform check on checkbox
				// it has inverse value
				if (((CheckBox) sender).getValue()) {
					grant(dataTable.getText(rows, 0), GWTPermission.READ, Main.get().securityPopup.recursive.getValue());
				} else {
					revoke(dataTable.getText(rows, 0), GWTPermission.READ, Main.get().securityPopup.recursive.getValue());
				}
			}
		};
		
		ClickHandler checkBoxWriteListener = new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				flag_property = PROPERTY_WRITE;
				Widget sender = (Widget) event.getSource();
				
				// Actions are inverse to check value because before user perform check on checkbox
				// it has inverse value
				if (((CheckBox) sender).getValue()) {
					grant(dataTable.getText(rows, 0), GWTPermission.WRITE, Main.get().securityPopup.recursive.getValue());
				} else {
					revoke(dataTable.getText(rows, 0), GWTPermission.WRITE, Main.get().securityPopup.recursive.getValue());
				}
			}
		};
		
		ClickHandler checkBoxDeleteListener = new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				flag_property = PROPERTY_DELETE;
				Widget sender = (Widget) event.getSource();
				
				// Actions are inverse to check value because before user perform check on checkbox
				// it has inverse value
				if (((CheckBox) sender).getValue()) {
					grant(dataTable.getText(rows, 0), GWTPermission.DELETE, Main.get().securityPopup.recursive.getValue());
				} else {
					revoke(dataTable.getText(rows, 0), GWTPermission.DELETE, Main.get().securityPopup.recursive.getValue());
				}
			}
		};
		
		ClickHandler checkBoxSecurityListener = new ClickHandler() { 
			@Override
			public void onClick(ClickEvent event) {
				flag_property = PROPERTY_SECURITY;
				Widget sender = (Widget) event.getSource();
				
				// Actions are inverse to check value because before user perform check on checkbox
				// it has inverse value
				if (((CheckBox) sender).getValue()) {
					grant(dataTable.getText(rows, 0), GWTPermission.SECURITY, Main.get().securityPopup.recursive.getValue());
				} else {
					revoke(dataTable.getText(rows, 0), GWTPermission.SECURITY, Main.get().securityPopup.recursive.getValue());
				}
			}
		};
		
		checkReadPermission.addClickHandler(checkBoxReadListener);
		
		if ((permission.byteValue() & GWTPermission.READ) == GWTPermission.READ) {
			checkReadPermission.setValue(true);
			dataTable.setWidget(rows, 1, checkReadPermission);
			dataTable.getCellFormatter().setHorizontalAlignment(rows,1,HasAlignment.ALIGN_CENTER);
		} else {
			checkReadPermission.setValue(false);
			dataTable.setWidget(rows,1, checkReadPermission);
			dataTable.getCellFormatter().setHorizontalAlignment(rows,1,HasAlignment.ALIGN_CENTER);
		}
		
		checkWritePermission.addClickHandler(checkBoxWriteListener);
		
		if ((permission.byteValue() & GWTPermission.WRITE) == GWTPermission.WRITE) {
			checkWritePermission.setValue(true);
			dataTable.setWidget(rows, 2, checkWritePermission);
			dataTable.getCellFormatter().setHorizontalAlignment(rows,2,HasAlignment.ALIGN_CENTER);
		} else {
			checkWritePermission.setValue(false);
			dataTable.setWidget(rows, 2, checkWritePermission);
			dataTable.getCellFormatter().setHorizontalAlignment(rows,2,HasAlignment.ALIGN_CENTER);
		}
		
		checkDeletePermission.addClickHandler(checkBoxDeleteListener);
		
		if ((permission.byteValue() & GWTPermission.DELETE) == GWTPermission.DELETE) {
			checkDeletePermission.setValue(true);
			dataTable.setWidget(rows, 3, checkDeletePermission);
			dataTable.getCellFormatter().setHorizontalAlignment(rows,3,HasAlignment.ALIGN_CENTER);
		} else {
			checkDeletePermission.setValue(false);
			dataTable.setWidget(rows, 3, checkDeletePermission);
			dataTable.getCellFormatter().setHorizontalAlignment(rows,3,HasAlignment.ALIGN_CENTER);
		}
		
		checkSecurityPermission.addClickHandler(checkBoxSecurityListener);
		
		if ((permission.byteValue() & GWTPermission.SECURITY) == GWTPermission.SECURITY) {
			checkSecurityPermission.setValue(true);
			dataTable.setWidget(rows, 4, checkSecurityPermission);
			dataTable.getCellFormatter().setHorizontalAlignment(rows,4,HasAlignment.ALIGN_CENTER);
		} else {
			checkSecurityPermission.setValue(false);
			dataTable.setWidget(rows, 4, checkSecurityPermission);
			dataTable.getCellFormatter().setHorizontalAlignment(rows,4,HasAlignment.ALIGN_CENTER);
		}
	}
	
	/**
	 * Adds new roleName name row
	 * 
	 * @param roleName The user name value
	 */
	public void addRow(String roleName) {
		int rows = dataTable.getRowCount();
		dataTable.insertRow(rows);
		dataTable.setHTML(rows, 0, roleName);
	}
	
	/**
	 * Selects the las row
	 */
	public void selectLastRow(){
		if (dataTable.getRowCount()>0) {
			dataTable.selectRow(dataTable.getRowCount()-1,true);
		}
	}
	
	/**
	 * Removes all rows except the first
	 */
	public void removeAllRows() {
		// Purge all rows 
		while (dataTable.getRowCount() > 0) {
			dataTable.removeRow(0);
		}
	}
	
	/**
	 * Reset table values
	 */
	public void reset() {
		removeAllRows();
	}
	
	/**
	 * Gets the role
	 * 
	 * @return The role
	 */
	public String getRole() {
		String role = null;
		
		if (!dataTable.getSelectedRows().isEmpty()) {
			int selectedRow = ((Integer) dataTable.getSelectedRows().iterator().next()).intValue();
			if (dataTable.isRowSelected(selectedRow)) {
				role = dataTable.getHTML(((Integer) dataTable.getSelectedRows().iterator().next()).intValue(),0);
			}
		}
	
		return role;
	}
	
	/**
	 * Removes the selected row
	 */
	public void removeSelectedRow() {
		if(!dataTable.getSelectedRows().isEmpty()) {
			int selectedRow = ((Integer) dataTable.getSelectedRows().iterator().next()).intValue();
			dataTable.removeRow(selectedRow);
			if (dataTable.getRowCount()>0) {
				if (dataTable.getRowCount()>selectedRow) {
					dataTable.selectRow(selectedRow,true);
				} else {
					dataTable.selectRow(selectedRow-1,true);
				}
			}
		}
	}
	
	/**
	 * Call back add new role grant
	 */
	final AsyncCallback<Object> callbackGrantRole = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {
			Main.get().securityPopup.status.unsetFlag_update();
		}

		public void onFailure(Throwable caught) {
			switch (flag_property) {
				case PROPERTY_READ:
					((CheckBox) dataTable.getWidget(rowIndex, 1)).setValue(false);
					break;
				case PROPERTY_WRITE:
					((CheckBox) dataTable.getWidget(rowIndex, 2)).setValue(false);
					break;
				case PROPERTY_DELETE:
					((CheckBox) dataTable.getWidget(rowIndex, 3)).setValue(false);
					break;
				case PROPERTY_SECURITY:
					((CheckBox) dataTable.getWidget(rowIndex, 4)).setValue(false);
					break;
			}
			Main.get().securityPopup.status.unsetFlag_update();
			Main.get().showError("GrantRole", caught);
		}
	};
	
	/**
	 * Call back revoke role grant
	 */
	final AsyncCallback<Object> callbackRevokeRole = new AsyncCallback<Object>() {
		public void onSuccess(Object result) {
			// If user has no grants must be deleted
			if (!dataTable.getSelectedRows().isEmpty()) {
				int selectedRow = ((Integer) dataTable.getSelectedRows().iterator().next()).intValue();
				if (!((CheckBox) dataTable.getWidget(selectedRow, 1)).getValue() && 
					!((CheckBox) dataTable.getWidget(selectedRow, 2)).getValue() && 
					!((CheckBox) dataTable.getWidget(selectedRow, 3)).getValue() &&
					!((CheckBox) dataTable.getWidget(selectedRow, 4)).getValue()) {
					Main.get().securityPopup.securityRole.unassignedRole.addRow(dataTable.getText(selectedRow, 0));
					removeSelectedRow();
				}
			}
			Main.get().securityPopup.status.unsetFlag_update();
		}

		public void onFailure(Throwable caught) {
			switch (flag_property) {
				case PROPERTY_READ:
					((CheckBox) dataTable.getWidget(rowIndex, 1)).setValue(true);
					break;
				case PROPERTY_WRITE:
					((CheckBox) dataTable.getWidget(rowIndex, 2)).setValue(true);
					break;
				case PROPERTY_DELETE:
					((CheckBox) dataTable.getWidget(rowIndex, 3)).setValue(true);
					break;
				case PROPERTY_SECURITY:
					((CheckBox) dataTable.getWidget(rowIndex, 4)).setValue(true);
					break;
			}
			Main.get().securityPopup.status.unsetFlag_update();
			Main.get().showError("RevokeRole", caught);
		}
	};
	
	/**
	 * Grant the role
	 * 
	 * @param user The granted role
	 * @param permissions The permissions value
	 */
	public void grant(String role, int permissions, boolean recursive) {
		if (path != null) {
			Main.get().securityPopup.status.setFlag_update();
			ServiceDefTarget endPoint = (ServiceDefTarget) authService;
			endPoint.setServiceEntryPoint(RPCService.AuthService);
			authService.grantRole(path, role, permissions, recursive, callbackGrantRole);
		}
	}
	
	/**
	 * Revoke the role grant
	 * 
	 * @param user The role
	 * @param permissions The permissions value
	 */
	public void revoke(String role, int permissions, boolean recursive) {
		if (path != null) {
			Main.get().securityPopup.status.setFlag_update();
			ServiceDefTarget endPoint = (ServiceDefTarget) authService;
			endPoint.setServiceEntryPoint(RPCService.AuthService);	
			authService.revokeRole(path, role, permissions, recursive, callbackRevokeRole);
		}
	}
	
	/**
	 * Sets the path
	 * 
	 * @param path The path
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	/**
	 * fillWidth
	 */
	public void fillWidth(){
		table.fillWidth();
	}
	
	/**
	 * getDataTable
	 * 
	 * @return FixedWidthGrid
	 */
	public FixedWidthGrid getDataTable(){
		return table.getDataTable();
	}
}