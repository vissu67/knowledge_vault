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

package com.openkm.util;

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.DatabaseException;
import com.openkm.dao.ActivityDAO;
import com.openkm.dao.bean.Activity;

/**
 * 
 * @author pavila
 */
public class UserActivity {
	private static Logger log = LoggerFactory.getLogger(UserActivity.class);
		
	/**
	 * Log activity
	 * 
	 * @param user User id who generated the activity.
	 * @param action Which action is associated with the activity.
	 * @param item Unique node identifier if this activity is node related, or another entity identifier. 
	 * @param params Other activity related parameters.
	 */
	public static void log(String user, String action, String item, String params) {
		try {
			Activity vo = new Activity();
			Calendar cal = Calendar.getInstance();
			cal.setTime(new java.util.Date());
			vo.setDate(cal);
			vo.setUser(user);
			vo.setAction(action);
			vo.setItem(item);
			vo.setParams(params);
			log.debug(vo.toString());
			ActivityDAO.create(vo);
		} catch (DatabaseException e) {
			log.error(e.getMessage());
		}
	}
}
