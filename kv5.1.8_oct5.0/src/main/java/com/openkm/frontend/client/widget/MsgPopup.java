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

package com.openkm.frontend.client.widget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.openkm.frontend.client.Main;

/**
 * Message popup
 * 
 * @author jllort
 *
 */
public class MsgPopup extends DialogBox implements ClickHandler {
	//private PopupPanel panel;
	private VerticalPanel vPanel;
	private HTML text;
	private Button button;
	private ScrollPanel sPanel;
	private String property = "";
	
	/**
	 * MsgPopup
	 */
	public MsgPopup() {
		// Establishes auto-close when click outside
		super(false,true);
		
		vPanel = new VerticalPanel();
		text= new HTML();
		sPanel = new ScrollPanel();
		
		button = new Button(Main.i18n("button.close"), this);
		
		vPanel.setWidth("300px");
		vPanel.setHeight("200px");
		sPanel.setWidth("280px");
		sPanel.setHeight("160px");
		sPanel.setStyleName("okm-Popup-text");
		
		vPanel.add(new HTML("<br>"));
		sPanel.add(text);
		vPanel.add(sPanel);
		vPanel.add(new HTML("<br>"));
		vPanel.add(button);
		vPanel.add(new HTML("<br>"));
		
		text.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
		vPanel.setCellHorizontalAlignment(sPanel, VerticalPanel.ALIGN_CENTER);
		vPanel.setCellHorizontalAlignment(button, VerticalPanel.ALIGN_CENTER);
		
		center();
		button.setStyleName("okm-Button");

		hide();
		setWidget(vPanel);
	}
	
	/* (non-Javadoc)
	 * @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent)
	 */
	public void onClick(ClickEvent event) {
		hide();
		// Removes all previous text for next errors messages, varios errors can be added simultanealy
		// on show(String msg )
		text.setText("");
	}
	
	/**
	 * Language refresh
	 */
	public void langRefresh() {
		button.setText(Main.i18n("button.close"));
		if (!property.equals("")) {
			setText(Main.i18n(property));
		}
	}
	
	/**
	 * Show the popup error
	 * 
	 * @param property The property value to translate
	 * @param msg Error message
	 */
	public void show(String property, String msg, int width, int height) {
		//TODO: aqui pueden haber problemas de concurrencia al ser llamado simultaneamente este método
		// cabe la posibilidad de perder algun mensaje de error.
		this.property = property;
		setText(Main.i18n(property));
		vPanel.setWidth(""+width);
		vPanel.setHeight(""+height);
		sPanel.setWidth(""+(width-20));
		sPanel.setHeight(""+(height-40));
		if (!text.getHTML().equals("")) {
			text.setHTML(text.getHTML() + "<br><br>" + msg);
		} else {
			text.setHTML(msg);
		}
		center();
		super.show();
	}
}