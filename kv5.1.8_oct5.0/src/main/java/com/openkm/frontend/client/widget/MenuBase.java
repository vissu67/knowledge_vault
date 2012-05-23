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

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MenuItem;

import com.openkm.frontend.client.bean.GWTAvailableOption;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTMail;


/**
 * Menu base
 * 
 * @author jllort
 *
 */
public abstract class MenuBase extends Composite {
	
	public abstract void langRefresh();
	public abstract void enableAllMenuOptions();
	public abstract void enableRootMenuOptions();	
	public abstract void checkMenuOptionPermissions(GWTFolder folder, GWTFolder folderParent);
	public abstract void evaluateMenuOptions();
	public abstract void disableAllMenuOption();
	public abstract void checkMenuOptionPermissions(GWTDocument doc, GWTFolder folder );
	public abstract void checkMenuOptionPermissions(GWTMail mail, GWTFolder folder );
	public abstract void setAvailableOption(GWTAvailableOption option);
	
	/**
	 * Enables menu item
	 * 
	 * @param menuItem The menu item
	 */
	public void enable(MenuItem menuItem) {
		menuItem.addStyleName("okm-MenuItem");
		menuItem.removeStyleName("okm-MenuItem-strike");
	}
	
	/**
	 * Disable the menu item
	 * 
	 * @param menuItem The menu item
	 */
	public void disable(MenuItem menuItem) {
		menuItem.removeStyleName("okm-MenuItem");
		menuItem.addStyleName("okm-MenuItem-strike");
	}
}