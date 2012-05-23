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

package com.openkm.frontend.client.panel.center;

import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.openkm.frontend.client.panel.left.Navigator;
import com.openkm.frontend.client.util.Util;

/**
 * Administration
 * 
 * @author jllort
 *
 */
public class Desktop extends Composite {
	
	private final static int PANEL_LEFT_WIDTH 	= 325;		//width changed from 225 to 325 by vissu
	public final static int SPLITTER_WIDTH 	= 10;
	
	private HorizontalSplitPanelExtended horizontalSplitPanel;
	public Navigator navigator;
	public Browser browser;
	private boolean isResizeInProgress = false;
	private int width = 0;
	private int height = 0; 
	private int left = 0;
	private int right = 0;
	
	/**
	 * Desktop
	 */
	public Desktop() {
		horizontalSplitPanel = new HorizontalSplitPanelExtended();
		navigator = new Navigator();
		browser = new Browser();
		
		horizontalSplitPanel.getSplitPanel().setLeftWidget(navigator);
		horizontalSplitPanel.getSplitPanel().setRightWidget(browser);
		horizontalSplitPanel.getSplitPanel().setSplitPosition(""+PANEL_LEFT_WIDTH);
		
		horizontalSplitPanel.addMouseMoveHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if (horizontalSplitPanel.getSplitPanel().isResizing()) {
					if (!isResizeInProgress) {
						isResizeInProgress = true;
						onSplitResize();
					}
				} 
			}
		});
		
		horizontalSplitPanel.addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				if (isResizeInProgress) {
					isResizeInProgress = false;
				}
			}
		});
		
		initWidget(horizontalSplitPanel);
	}
	
	/**
	 * Sets the size on initialization
	 * 
	 * @param width The max width of the widget
	 * @param height The max height of the widget
	 */
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		left = PANEL_LEFT_WIDTH;
		right = width-(PANEL_LEFT_WIDTH+SPLITTER_WIDTH);
		horizontalSplitPanel.setPixelSize(width, height);
		navigator.setSize(left, height);
		browser.setSize(right, height);
	}
	
	/**
	 * onSplitResize
	 */
	public void onSplitResize() {
		final int resizeUpdatePeriod = 20; // ms ( Internally splitter is refreshing each 20 ms )
		if (isResizeInProgress) {
			new Timer() {
				@Override
				public void run() {
					resizePanels(); // Always making resize
					if (isResizeInProgress) {
						onSplitResize();
					} else {
						// On finishing in good idea to fill width column tables
						browser.fileBrowser.table.fillWidth();
						// Solve some problems with chrome
						if (Util.getUserAgent().equals("chrome")) {
							new Timer() {
								@Override
								public void run() {
									resizePanels();
								}
								
							}.schedule(250);
						}
					}
				}
			}.schedule(resizeUpdatePeriod);
		}
	}
	
	/**
	 * Sets the panel width on resizing
	 * 
	 * @param left
	 * @param right
	 */
	private void resizePanels() {
		int total = 0;
		String value = DOM.getStyleAttribute (horizontalSplitPanel.getSplitPanel().getElement(), "width");
		if (value.contains("px")) { value = value.substring(0,value.indexOf("px")); }
		total = Integer.parseInt(value);
		value = DOM.getStyleAttribute (DOM.getChild(DOM.getChild(horizontalSplitPanel.getSplitPanel().getElement(),0), 0), "width");
		if (value.contains("px")) { value = value.substring(0,value.indexOf("px")); }
		left = Integer.parseInt(value);
		value = DOM.getStyleAttribute (DOM.getChild(DOM.getChild(horizontalSplitPanel.getSplitPanel().getElement(),0), 2), "left");
		if (value.contains("px")) { value = value.substring(0,value.indexOf("px")); }
		right = total - Integer.parseInt(value);
		
		// Solve some problems with chrome
		if (Util.getUserAgent().equals("chrome")) {
			if (left-15>0 && height-15>0 && right-15>0) {
				navigator.setSize(left-15, height-15);
				browser.setWidth(right-15);
			}
		} 
		
		navigator.setSize(left, height);
		browser.setWidth(right);
	}
	
	/**
	 * refreshSpliterAfterAdded
	 */
	public void refreshSpliterAfterAdded() {
		horizontalSplitPanel.getSplitPanel().setSplitPosition(""+left);
		browser.refreshSpliterAfterAdded();
		
		// Solve some problems with chrome
		if (Util.getUserAgent().equals("chrome")) {
			new Timer() {
				@Override
				public void run() {
					resizePanels();
				}
				
			}.schedule(250);
		}
	}
	
	/**
	 * getWidth
	 * 
	 * @return
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * getHeight
	 * 
	 * @return
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * getLeft
	 * 
	 * @return
	 */
	public int getLeft() {
		return left;
	}

	/**
	 * getRight
	 * 
	 * @return
	 */
	public int getRight() {
		return right;
	}
}