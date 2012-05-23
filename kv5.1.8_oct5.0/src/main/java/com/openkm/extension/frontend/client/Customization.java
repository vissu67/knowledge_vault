
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

package com.openkm.extension.frontend.client;

import java.util.ArrayList;
import java.util.List;

/**
 * Customization
 * 
 * @author jllort
 *
 */
public class Customization {
	
	/**
	 * getExtensionWidgets
	 * 
	 * @return
	 */
	public static List<Object> getExtensionWidgets(List<String> uuidList) {
		List<Object> extensions = new ArrayList<Object>();
		
		// add here your widget extensions
		if (uuidList.contains("d9dab640-d098-11df-bd3b-0800200c9a66")) {
			extensions.add(new HelloWorld());
		} 
		if (uuidList.contains("9f84b330-d096-11df-bd3b-0800200c9a66")) {
			//extensions.add(new ToolBarButtonExample().getButton());
		} 
		if (uuidList.contains("d95e01a0-d097-11df-bd3b-0800200c9a66")) {
			extensions.add(new TabFolderExample());
		} 
		if (uuidList.contains("44f94470-d097-11df-bd3b-0800200c9a66")) {
			extensions.add(new TabWorkspaceExample());
		}
		if (uuidList.contains("4d245f30-ef47-11df-98cf-0800200c9a66")) {
			extensions.add(new ToolBarBoxExample().getToolBarBox());
		}

//		extensions.add(new MainMenuExample().getNewMenu());
//		extensions.add(new HandlersTest());		
				
		return extensions;
	}
}
