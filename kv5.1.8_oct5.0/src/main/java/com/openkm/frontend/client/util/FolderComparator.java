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

import com.openkm.frontend.client.bean.GWTFolder;

public class FolderComparator implements Comparator<GWTFolder> {
	private static final Comparator<GWTFolder> INSTANCE  = new FolderComparator();
	
	public static Comparator<GWTFolder> getInstance() {
		return INSTANCE;
	}

	public int compare(GWTFolder arg0, GWTFolder arg1) {
		GWTFolder first = arg0;
		GWTFolder second = arg1;
		return first.getName().compareTo(second.getName());
	}
}
