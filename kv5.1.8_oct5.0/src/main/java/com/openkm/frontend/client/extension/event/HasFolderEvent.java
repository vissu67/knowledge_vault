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
 * HasFolderEvent
 * 
 * 
 * @author jllort
 *
 */

public interface HasFolderEvent {
	
	/**
	 * DocumentEventConstant
	 * 
	 * @author jllort
	 *
	 */
	public static class FolderEventConstant {
		
		static final int EVENT_FOLDER_CHANGED 		= 1;
		static final int EVENT_PANEL_RESIZED   		= 2;
		static final int EVENT_TAB_CHANGED    		= 3;
		static final int EVENT_SECURITY_CHANGED 	= 4;
		static final int EVENT_SET_VISIBLE_BUTTON 	= 5;
		static final int EVENT_NOTE_ADDED 			= 6;
		static final int EVENT_FOLDER_DELETED 		= 7;
		
		private int type = 0;
		
		/**
		 * DocumentEventConstant
		 * 
		 * @param type
		 */
		private FolderEventConstant(int type) {
			this.type = type;
		}
		
		public int getType(){
			return type;
		}
	}
	
	FolderEventConstant FOLDER_CHANGED = new FolderEventConstant(FolderEventConstant.EVENT_FOLDER_CHANGED);
	FolderEventConstant PANEL_RESIZED = new FolderEventConstant(FolderEventConstant.EVENT_PANEL_RESIZED);
	FolderEventConstant TAB_CHANGED = new FolderEventConstant(FolderEventConstant.EVENT_TAB_CHANGED);
	FolderEventConstant SECURITY_CHANGED = new FolderEventConstant(FolderEventConstant.EVENT_SECURITY_CHANGED);
	FolderEventConstant SET_VISIBLE_BUTTON = new FolderEventConstant(FolderEventConstant.EVENT_SET_VISIBLE_BUTTON);
	FolderEventConstant NOTE_ADDED = new FolderEventConstant(FolderEventConstant.EVENT_NOTE_ADDED);
	FolderEventConstant FOLDER_DELETED = new FolderEventConstant(FolderEventConstant.EVENT_FOLDER_DELETED);
	
	/**
	 * @param event
	 */
	void fireEvent(FolderEventConstant event);
}