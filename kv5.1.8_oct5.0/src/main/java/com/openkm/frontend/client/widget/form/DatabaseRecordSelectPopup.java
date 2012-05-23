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

package com.openkm.frontend.client.widget.form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTKeyValue;
import com.openkm.frontend.client.service.OKMKeyValueService;
import com.openkm.frontend.client.service.OKMKeyValueServiceAsync;
import com.openkm.frontend.client.util.MessageFormat;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.searchin.HasSearch;

/**
 * DatabaseRecordSelectPopup
 * 
 * @author jllort
 *
 */
public class DatabaseRecordSelectPopup extends DialogBox {
	private final OKMKeyValueServiceAsync keyValueService = (OKMKeyValueServiceAsync) GWT.create(OKMKeyValueService.class);
	
	private VerticalPanel vPanel;
	private ScrollPanel scrollDatabaseRecordPanel;
	private TextBox record;
	private FlexTable recordTabla;
	private Button cancelBoton;
	private Button acceptBoton;
	private int selectedRow = -1;
	private Map<Integer, GWTKeyValue> rowKeyValueMap;
	List<String> tables;
	private String query;
	private Status status;
	private int filterMinLength = 0;
	
	/**
	 * DatabaseRecordSelectPopup
	 */
	public DatabaseRecordSelectPopup(String title, List<String> tables, String query, final HasDatabaseRecord databaseRecord, 
									 final HasSearch search, final int filterMinLength) {
		// Establishes auto-close when click outside
		super(false,true);
		
		this.tables = tables;
		this.query = query;
		this.filterMinLength = filterMinLength;
		setText(title);

		vPanel = new VerticalPanel();		
		vPanel.setWidth("300");
		vPanel.setHeight("200");
		
		record = new TextBox();
		record.setWidth("292");
		record.addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (record.getText().length()>=filterMinLength) {
					findFilteredDatabaseRecords();
				} else {
					removeAllRows();
				}
			}
		});
		record.setStyleName("okm-Input");
		
		recordTabla = new FlexTable();
		recordTabla.setWidth("100%");
		recordTabla.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				markSelectedRow(recordTabla.getCellForEvent(event).getRowIndex());
				acceptBoton.setEnabled(true);
			}
		});
		
		scrollDatabaseRecordPanel = new ScrollPanel(recordTabla);
		scrollDatabaseRecordPanel.setPixelSize(290,150);
		scrollDatabaseRecordPanel.setStyleName("okm-Popup-text");
		
		cancelBoton = new Button(Main.i18n("button.cancel"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		cancelBoton.setStyleName("okm-Button");
		
		acceptBoton = new Button(Main.i18n("button.accept"), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (selectedRow>=0) {
					databaseRecord.setKeyValue(rowKeyValueMap.get(selectedRow));
					if (search!=null) {
						search.metadataValueChanged();
					}
				}
				hide();
			}
		});
		acceptBoton.setEnabled(false);
		acceptBoton.setStyleName("okm-Button");
		
		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.add(cancelBoton);
		hPanel.add(new HTML("&nbsp;"));
		hPanel.add(acceptBoton);
		
		if (filterMinLength>0) {
			HorizontalPanel hInfoPanel = new HorizontalPanel();
			HTML filterInfo = new HTML(MessageFormat.format(Main.i18n("form.manager.suggestbox.min.filter"), filterMinLength));
			HTML space = Util.hSpace("5");
			hInfoPanel.add(filterInfo);
			hInfoPanel.add(space);
			vPanel.add(hInfoPanel);
			vPanel.setCellHorizontalAlignment(hInfoPanel, HasAlignment.ALIGN_RIGHT);
		}
		
		vPanel.add(record);
		vPanel.add(scrollDatabaseRecordPanel);
		vPanel.add(hPanel);
		
		vPanel.setCellHeight(record, "25");
		vPanel.setCellHeight(hPanel, "25");
		vPanel.setCellVerticalAlignment(record, HasAlignment.ALIGN_MIDDLE);
		vPanel.setCellVerticalAlignment(hPanel, HasAlignment.ALIGN_MIDDLE);
		vPanel.setCellHorizontalAlignment(record, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(scrollDatabaseRecordPanel, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(hPanel, HasAlignment.ALIGN_CENTER);
		
		status = new Status(this);
		status.setStyleName("okm-StatusPopup");
		
		super.hide();
		setWidget(vPanel);
	}
	
	/**
	 * findFilteredDatabaseRecords
	 */
	private void findFilteredDatabaseRecords() {
		removeAllRows();
		selectedRow = -1;
		record.setReadOnly(true);
		acceptBoton.setEnabled(false);
		status.setGetDatabaseRecords();
		keyValueService.getKeyValues(tables, MessageFormat.format(query, record.getText()), new AsyncCallback<List<GWTKeyValue>>() {
			@Override
			public void onSuccess(List<GWTKeyValue> result) {
				rowKeyValueMap = new HashMap<Integer, GWTKeyValue>();
				for (GWTKeyValue keyValue : result) {
					int row = recordTabla.getRowCount();
					rowKeyValueMap.put(row, keyValue);
					recordTabla.setHTML(row, 0, keyValue.getValue());
				}
				record.setReadOnly(false);
				status.unsetGetDatabaseRecords();
			}
			@Override
			public void onFailure(Throwable caught) {
				status.unsetGetDatabaseRecords();
				Main.get().showError("getKeyValues", caught);
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.DialogBox#show()
	 */
	public void show() {
		removeAllRows();
		record.setText("");
		record.setReadOnly(false);
		acceptBoton.setEnabled(false);
		rowKeyValueMap = new HashMap<Integer, GWTKeyValue>();
		super.show();
		record.setFocus(true);
		// Case must show by default all values
		if (filterMinLength==0) {
			findFilteredDatabaseRecords();
		}
	}
	
	/**
	 * removeAllRows
	 */
	private void removeAllRows() {
		recordTabla.removeAllRows();
		selectedRow = -1;
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
				recordTabla.getRowFormatter().addStyleName(row, "okm-Table-SelectedRow");
		    } else {
		    	recordTabla.getRowFormatter().removeStyleName(row, "okm-Table-SelectedRow");
		    }
		}
	 }
	
	/**
	 * langRefresh
	 */
	public void langRefresh() {
		cancelBoton.setHTML(Main.i18n("button.cancel"));
		acceptBoton.setHTML(Main.i18n("button.accept"));
	}
}