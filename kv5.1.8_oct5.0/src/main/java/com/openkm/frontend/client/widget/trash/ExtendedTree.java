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

package com.openkm.frontend.client.widget.trash;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Tree;

/**
 * ExtendedTree captures right button and marks a popup flag
 * 
 * @author jllort
 *
 */
public class ExtendedTree extends Tree {
	
	private boolean flagPopup = false;
	public int mouseX = 0;
	public int mouseY = 0;
	
	/**
	 * @return true or false popup flag
	 */
	public boolean isShowPopUP() {
		return flagPopup;
	}
	
	public void onBrowserEvent(Event event) {
		
		// When de button mouse is released
		if (DOM.eventGetType(event) == Event.ONMOUSEDOWN) {
			// When de button mouse is released
			mouseX = DOM.eventGetClientX(event);
			mouseY = DOM.eventGetClientY(event);
			
			switch (DOM.eventGetButton(event)){
				case Event.BUTTON_RIGHT:
					DOM.eventPreventDefault(event); // Prevent to fire event to browser
					flagPopup = true;
					break;
				default:
					flagPopup = false;
			}
		}
		
		super.onBrowserEvent(event);
	}
}