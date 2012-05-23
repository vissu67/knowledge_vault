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

package com.openkm.frontend.client.widget.dashboard;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasAllMouseHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * ToolBarBox
 * 
 * @author jllort
 *
 */
public class ToolBarBox extends HorizontalPanel implements HasClickHandlers, HasAllMouseHandlers {
	
	private VerticalPanel vPanel;
	private Image image;
	private HTML html;
	
	/**
	 * ToolBarBox
	 * 
	 * @param url
	 * @param text
	 */
	public ToolBarBox(Image img, String text) {
		super();
		sinkEvents(Event.ONCLICK | Event.MOUSEEVENTS);
		
		vPanel = new VerticalPanel();
		HTML space1 = new HTML("&nbsp;");
		HTML space2 = new HTML("&nbsp;");
		image = img;
		html = new HTML(text);
		html.setText(text);
		html.setTitle(text);
		image.setTitle(text);
		
		vPanel.add(image);
		vPanel.add(html);
		
		add(space1);
		add(vPanel);
		add(space2);
		
		vPanel.setCellHorizontalAlignment(html, HasAlignment.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(image, HasAlignment.ALIGN_CENTER);
		
		setCellVerticalAlignment(vPanel, HasAlignment.ALIGN_MIDDLE);
		setCellHorizontalAlignment(vPanel, HasAlignment.ALIGN_CENTER);
		
		setCellWidth(space1, "15");
		setCellWidth(space2, "15");
		
		html.setStyleName("okm-noWrap");
		
		setHeight("59");
		setWidth("100%");
	}
	
	/**
	 * setLabelText
	 * 
	 * @param text
	 */
	public void setLabelText(String text) {
		html.setText(text);
		html.setTitle(text);
		image.setTitle(text);
	}
	
	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.HasClickHandlers#addClickHandler(com.google.gwt.event.dom.client.ClickHandler)
	 */
	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addHandler(handler, ClickEvent.getType());
	}
	
	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.HasMouseDownHandlers#addMouseDownHandler(com.google.gwt.event.dom.client.MouseDownHandler)
	 */
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
	    return addDomHandler(handler, MouseDownEvent.getType());
	}
	  
	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.HasMouseMoveHandlers#addMouseMoveHandler(com.google.gwt.event.dom.client.MouseMoveHandler)
	 */
	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
		return addDomHandler(handler, MouseMoveEvent.getType());
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.HasMouseOutHandlers#addMouseOutHandler(com.google.gwt.event.dom.client.MouseOutHandler)
	 */
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
	    return addDomHandler(handler, MouseOutEvent.getType());
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.HasMouseOverHandlers#addMouseOverHandler(com.google.gwt.event.dom.client.MouseOverHandler)
	 */
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
	    return addDomHandler(handler, MouseOverEvent.getType());
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.HasMouseUpHandlers#addMouseUpHandler(com.google.gwt.event.dom.client.MouseUpHandler)
	 */
	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
	    return addDomHandler(handler, MouseUpEvent.getType());
	}

	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.HasMouseWheelHandlers#addMouseWheelHandler(com.google.gwt.event.dom.client.MouseWheelHandler)
	 */
	public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
	    return addDomHandler(handler, MouseWheelEvent.getType());
	}
}