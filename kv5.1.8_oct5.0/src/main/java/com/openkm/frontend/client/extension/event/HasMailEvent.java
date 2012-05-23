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

package com.openkm.frontend.client.extension.event;

/**
 * HasDocumentEvent
 * 
 * 
 * @author jllort
 *
 */

public interface HasMailEvent {
	
	/**
	 * MailEventConstant
	 * 
	 * @author jllort
	 *
	 */
	public static class MailEventConstant {
		
		static final int EVENT_MAIL_CHANGED			= 1;
		static final int EVENT_PANEL_RESIZED   		= 2;
		static final int EVENT_TAB_CHANGED    		= 3;
		static final int EVENT_SECURITY_CHANGED 	= 4;
		static final int EVENT_SET_VISIBLE_BUTTONS	= 5;
		static final int EVENT_MAIL_DELETED			= 6;
		
		private int type = 0;
		
		/**
		 * DocumentEventConstant
		 * 
		 * @param type
		 */
		private MailEventConstant(int type) {
			this.type = type;
		}
		
		public int getType(){
			return type;
		}
	}
	
	MailEventConstant MAIL_CHANGED = new MailEventConstant(MailEventConstant.EVENT_MAIL_CHANGED);
	MailEventConstant PANEL_RESIZED = new MailEventConstant(MailEventConstant.EVENT_PANEL_RESIZED);
	MailEventConstant TAB_CHANGED = new MailEventConstant(MailEventConstant.EVENT_TAB_CHANGED);
	MailEventConstant SECURITY_CHANGED = new MailEventConstant(MailEventConstant.EVENT_SECURITY_CHANGED);
	MailEventConstant SET_VISIBLE_BUTTONS = new MailEventConstant(MailEventConstant.EVENT_SET_VISIBLE_BUTTONS);
	MailEventConstant MAIL_DELETED = new MailEventConstant(MailEventConstant.EVENT_MAIL_DELETED);
	
	/**
	 * @param event
	 */
	void fireEvent(MailEventConstant event);
}