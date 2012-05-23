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

package com.openkm.frontend.client.util;

import java.util.Comparator;

import com.openkm.frontend.client.bean.GWTBookmark;

/**
 * BookmarkComparator
 * 
 * @author jllort
 *
 */
public class BookmarkComparator implements Comparator<GWTBookmark> {
	private static final Comparator<GWTBookmark> INSTANCE  = new BookmarkComparator();
	
	public static Comparator<GWTBookmark> getInstance() {
		return INSTANCE;
	}

	public int compare(GWTBookmark arg0, GWTBookmark arg1) {		
		// Compare first with type, and second for name
		if (!arg0.getType().equals(arg0.getType())) {
			return arg1.getType().compareTo(arg0.getType()); // inverse comparation
		} else {
			return arg0.getName().toUpperCase().compareTo(arg1.getName().toUpperCase());
		}
	}
}