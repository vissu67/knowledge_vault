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

package com.openkm.frontend.client.widget.test;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.contants.service.RPCService;
import com.openkm.frontend.client.service.OKMTestService;
import com.openkm.frontend.client.service.OKMTestServiceAsync;

/**
 * Test Popup
 * 
 * @author jllort
 *
 */
public class TestPopup extends DialogBox {
	private final OKMTestServiceAsync testService = (OKMTestServiceAsync) GWT.create(OKMTestService.class);
	
	VerticalPanel vPanel;
	HorizontalPanel hPanel;
	ScrollPanel scroll;
	FlexTable table;
	Button clean;
	Button run;
	Button close;
	TextBox sizeTest;
	TextBox cyclesTest;
	TextBox numThreads;
	ListBox type;
	
	String runnningTest = "";
	int selectedTest = 0;
	int actualCycle = 0;
	int maxCycle = 0;
	int textSize = 0;
	
	/**
	 * TestPopup
	 */
	public TestPopup() {
		// Establishes auto-close when click outside
		super(false, true);
		setText("GWT Testing");
		vPanel = new VerticalPanel();
		
		// Controller
		hPanel = new HorizontalPanel();
		hPanel.add(new HTML("&nbsp;Type:"));
		type = new ListBox();
		type.addItem("String", "String");
		type.addItem("GWTFolder", "GWTFolder");
		type.addItem("GWTDocument", "GWTDocument");
		type.setStyleName("okm-Input");
		hPanel.add(type);
		hPanel.add(new HTML("&nbsp;Elements:"));
		sizeTest = new TextBox();
		sizeTest.setSize("60", "20");
		sizeTest.setText("2000");
		sizeTest.setStyleName("okm-Input");
		hPanel.add(sizeTest);
		hPanel.add (new HTML("&nbsp;Cycles:"));
		cyclesTest = new TextBox();
		cyclesTest.setText("100");
		cyclesTest.setSize("60", "20");
		cyclesTest.setStyleName("okm-Input");
		hPanel.add(cyclesTest);
		hPanel.add (new HTML("&nbsp;Threads:"));
		numThreads = new TextBox();
		numThreads.setText("1");
		numThreads.setSize("60", "20");
		numThreads.setStyleName("okm-Input");
		hPanel.add(numThreads);
		hPanel.add(new HTML("&nbsp;"));
		clean = new Button("Clean");
		clean.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				table.removeAllRows();
			}
		});
		clean.setStyleName("okm-Input");
		hPanel.add(clean);
		hPanel.add (new HTML("&nbsp;"));
		run = new Button("run");
		run.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				run();
			}
		});
		run.setStyleName("okm-Input");
		hPanel.add(run);
		hPanel.add (new HTML("&nbsp;"));
		
		// Log
		table = new FlexTable();
		scroll = new ScrollPanel(table);
		scroll.setSize("600", "450");
		table.setWidth("100%");
		table.setCellPadding(5);
		table.setCellSpacing(0);
		
		// Close
		close = new Button("close") ;
		close.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		close.setStyleName("okm-Input");
		
		vPanel.add(hPanel);
		vPanel.add(scroll);
		vPanel.add(close);
		vPanel.setCellHeight(hPanel, "30");
		vPanel.setCellHeight(scroll, "450");
		vPanel.setCellHeight(close, "20");
		vPanel.setCellHorizontalAlignment(close, HasAlignment.ALIGN_CENTER);
		
		super.hide();
		setWidget(vPanel);
	}
	
	/**
	 * run
	 */
	public void run() {
		runnningTest = "Test " + type.getValue(type.getSelectedIndex()) + " > ";
		selectedTest = type.getSelectedIndex();
		actualCycle = 0;
		maxCycle = Integer.parseInt(cyclesTest.getText());
		textSize = Integer.parseInt(sizeTest.getText());
		log(runnningTest, "Starting");
		int thread = Integer.parseInt(numThreads.getText()); 
		int count = 0;
		
		while (count < thread) {
			controller();
			count ++;
		}
	}
	
	/**
	 * controller
	 */
	private void controller() {
		if (actualCycle<maxCycle) {
			switch (selectedTest) {
				case 0:
					stringTest(actualCycle);
					actualCycle++;
					break;
				case 1:
					folderTest(actualCycle);
					actualCycle++;
					break;
				case 2:
					documentTest(actualCycle);
					actualCycle++;
					break;
			}
		} else {
			log(runnningTest, "Finished");
		}
	}
	
	/**
	 * stringTest
	 */
	private void stringTest(final int cycle) {
		ServiceDefTarget endPoint = (ServiceDefTarget) testService;
		endPoint.setServiceEntryPoint(RPCService.TestService);
		log(runnningTest, "Calling RPC: " + cycle);
		testService.StringTest(textSize, new AsyncCallback<String>() {
			@Override
			public void onSuccess(String result) {
				log(runnningTest, "Finished RPC: " + cycle + ", Result length: " + result.length());
				controller();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Main.get().showError("StringTest", caught);
			}
		});
	}
	
	/**
	 * folderTest
	 */
	private void folderTest(final int cycle) {
		ServiceDefTarget endPoint = (ServiceDefTarget) testService;
		endPoint.setServiceEntryPoint(RPCService.TestService);
		log(runnningTest, "Calling RPC: " + cycle);
		testService.folderText(textSize, new AsyncCallback<List<GWTFolder>>() {
			
			@Override
			public void onSuccess(List<GWTFolder> result) {
				log(runnningTest, "Finished RPC: " + cycle + ", Result size: " + result.size());
				controller();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Main.get().showError("folderText", caught);
			}
		});
	}
	
	/**
	 * documentTest
	 */
	private void documentTest(final int cycle) {
		ServiceDefTarget endPoint = (ServiceDefTarget) testService;
		endPoint.setServiceEntryPoint(RPCService.TestService);
		log(runnningTest, "Calling RPC: " + cycle);
		testService.documentText(textSize, new AsyncCallback<List<GWTDocument>>() {
			@Override
			public void onSuccess(List<GWTDocument> result) {
				log(runnningTest, "Finished RPC: " + cycle + ", Result size: " + result.size());
				controller();
			}
			@Override
			public void onFailure(Throwable caught) {
				Main.get().showError("folderText", caught);
			}
		});
	}
	
	/**
	 * @param value
	 * @param value2
	 */
	private void log(String value, String value2) {
		DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.date.pattern"));
		int row = table.getRowCount();
		table.setHTML(row, 0, dtf.format(new Date()) + " " + value);
		table.setHTML(row, 1, value2);
		table.getCellFormatter().setHeight(row, 0, "20");
		table.getCellFormatter().setWidth(row, 0, "250");
	}
}