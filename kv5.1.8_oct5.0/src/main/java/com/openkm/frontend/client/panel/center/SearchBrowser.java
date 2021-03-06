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
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.searchin.SearchIn;
import com.openkm.frontend.client.widget.searchresult.SearchResult;

/**
 * Search panel
 * 
 * @author jllort
 *
 */
public class SearchBrowser extends Composite {
	
	private final static int PANEL_TOP_HEIGHT 	= 210;
	public final static int SPLITTER_HEIGHT 	= 10;
	
	private VerticalSplitPanelExtended verticalSplitPanel;
	
	public SearchIn searchIn;
	public SearchResult searchResult;
	
	private boolean isResizeInProgress = false;
	public int width = 0;
	public int height = 0;
	public int topHeight = 0;
	public int bottomHeight = 0;
	
	/**
	 * SearchBrowser
	 */
	@SuppressWarnings("deprecation")
	public SearchBrowser() {
		verticalSplitPanel = new VerticalSplitPanelExtended();
		searchIn = new SearchIn();
		searchResult = new SearchResult();
		verticalSplitPanel.getSplitPanel().setTopWidget(searchIn);
		verticalSplitPanel.getSplitPanel().setBottomWidget(searchResult);
		
		verticalSplitPanel.addMouseMoveHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if (verticalSplitPanel.getSplitPanel().isResizing()) {
					if (!isResizeInProgress) {
						isResizeInProgress = true;
						onSplitResize();
					}
				} 
			}
		});
		
		verticalSplitPanel.addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				if (isResizeInProgress) {
					isResizeInProgress = false;
				}
			}
		});
		
		searchIn.setStyleName("okm-Input");
		initWidget(verticalSplitPanel);
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
					} else if (Util.getUserAgent().equals("chrome")) {
						new Timer() {
							@Override
							public void run() {
								resizePanels();
							}
							
						}.schedule(250);
					}
				}
			}.schedule(resizeUpdatePeriod);
		}
	}
	
	/**
	 * Refresh language values
	 */
	public void langRefresh() {
		searchIn.langRefresh();	
		searchResult.langRefresh();
	}
	
	/**
	 * Sets the size on initialization
	 * 
	 * @param width The max width of the widget
	 * @param height The max height of the widget
	 */
	@SuppressWarnings("deprecation")
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
		topHeight = PANEL_TOP_HEIGHT;
		bottomHeight = height - (topHeight + SPLITTER_HEIGHT);
		verticalSplitPanel.setSize(""+width, ""+height);
		verticalSplitPanel.getSplitPanel().setSplitPosition(""+topHeight);
		resize();
	}
	
	private void resize() {
		verticalSplitPanel.setWidth(""+width);
		// We substract 2 pixels for width and heigh generated by border line
		searchIn.setSize(width, topHeight);
		// Resize the scroll panel on tab properties 
		// We substract 2 pixels for width and heigh generated by border line
		searchResult.setPixelSize(width-2, bottomHeight-2);
	}
	
	
	/**
	 * Sets the panel width on resizing
	 * 
	 * @param left
	 * @param right
	 */
	private void resizePanels() {
		int total = verticalSplitPanel.getOffsetHeight();
		String value = DOM.getStyleAttribute (DOM.getChild(DOM.getChild(verticalSplitPanel.getSplitPanel().getElement(),0), 0), "height");
		if (value.contains("px")) { value = value.substring(0,value.indexOf("px")); }
		topHeight = Integer.parseInt(value);
		value = DOM.getStyleAttribute (DOM.getChild(DOM.getChild(verticalSplitPanel.getSplitPanel().getElement(),0), 2), "top");
		if (value.contains("px")) { value = value.substring(0,value.indexOf("px")); }
		bottomHeight = total - Integer.parseInt(value);		
		
		// To solve some problems with chrome
		if (Util.getUserAgent().equals("chrome")) {
			if (topHeight-15>0 && bottomHeight-15>0 && this.width-15>0) {
				topHeight -= 15;
				bottomHeight -= 15;
				this.width -= 15;
				resize();
				topHeight += 15;
				bottomHeight += 15;
				this.width += 15;
			}
		}
		
		resize();
	}
	
	/**
	 * setWidth
	 * 
	 * @param width
	 */
	public void setWidth(int width) {
		this.width = width;
		
		// To solve some problems with chrome
		if (Util.getUserAgent().equals("chrome")) {
			if (topHeight-15>0 && bottomHeight-15>0 && this.width-15>0) {
				topHeight -= 15;
				bottomHeight -= 15;
				this.width -= 15;
				resize();
				topHeight += 15;
				bottomHeight += 15;
				this.width += 15;
			}
		}
		
		resize();
	}
	
	/**
	 * refreshSpliterAfterAdded
	 * 
	 */
	@SuppressWarnings("deprecation")
	public void refreshSpliterAfterAdded() {
		verticalSplitPanel.getSplitPanel().setSplitPosition(""+topHeight);
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