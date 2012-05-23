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

import com.google.gwt.event.dom.client.HasMouseMoveHandlers;
import com.google.gwt.event.dom.client.HasMouseUpHandlers;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalSplitPanel;

/**
 * HorizontalSplitPanelExtended
 * 
 * @author jllort
 *
 */
public class HorizontalSplitPanelExtended extends Composite implements HasMouseMoveHandlers, HasMouseUpHandlers {
	
    private HorizontalSplitPanel horizontalSplitPanel;
    
    /**
     * HorizontalSplitPanelExtendend
     * 
     * @param handler
     */
    public HorizontalSplitPanelExtended() {
    	super();
        horizontalSplitPanel = new HorizontalSplitPanel();
        
        horizontalSplitPanel.setStyleName("okm-HorizontalSplitPanel");
        initWidget(horizontalSplitPanel);
    }

    /**
     * getSplitPanel
     * 
     * @return
     */
    public HorizontalSplitPanel getSplitPanel()
    {
    	return horizontalSplitPanel;
    }

	@Override
	public HandlerRegistration addMouseMoveHandler(MouseMoveHandler handler) {
		return addDomHandler(handler, MouseMoveEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
		return addDomHandler(handler, MouseUpEvent.getType());
	}
}