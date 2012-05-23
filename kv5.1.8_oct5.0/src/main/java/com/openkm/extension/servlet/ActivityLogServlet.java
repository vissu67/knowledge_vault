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

package com.openkm.extension.servlet;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.DatabaseException;
import com.openkm.dao.ActivityDAO;
import com.openkm.dao.bean.Activity;
import com.openkm.dao.bean.ActivityFilter;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.extension.GWTActivity;
import com.openkm.frontend.client.contants.service.ErrorCode;
import com.openkm.frontend.client.service.extension.OKMActivityLogService;
import com.openkm.servlet.frontend.OKMRemoteServiceServlet;
import com.openkm.util.GWTUtil;

/**
 * ActivityLogServlet
 * 
 * @author jllort
 *
 */
public class ActivityLogServlet extends OKMRemoteServiceServlet implements OKMActivityLogService {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(ActivityLogServlet.class);
	
	@Override
	public List<GWTActivity> findByFilterByItem(String item, String action, boolean getChilds) throws OKMException {
		List<GWTActivity> activityList = new ArrayList<GWTActivity>();
		ActivityFilter filter = new ActivityFilter();
		filter.setItem(item);
		try {
			if (!action.equals("")) {
				if (!action.equals("ALL_ACTIONS")) {
					filter.setAction(action);
				}
				
				for (Activity activity : ActivityDAO.findByFilterByItem(filter)) {
					if (getChilds) {
						activityList.add(GWTUtil.copy(activity));
					} else {
						// Root folders are added to limit filtering too 
						if (!activity.getAction().equals("GET_CHILD_DOCUMENTS") && !activity.getAction().equals("GET_CHILD_FOLDERS") && 
							!activity.getAction().equals("GET_CHILD_MAILS") && !activity.getAction().equals("GET_ROOT_FOLDER") &&
							!activity.getAction().equals("GET_CATEGORIES_FOLDER") && !activity.getAction().equals("GET_THESAURUS_FOLDER") &&
							!activity.getAction().equals("GET_TEMPLATES_FOLDER") && !activity.getAction().equals("GET_PERSONAL_FOLDER_BASE") && 
							!activity.getAction().equals("GET_MAIL_FOLDER_BASE") && !activity.getAction().equals("GET_TRASH_FOLDER_BASE")) {
							activityList.add(GWTUtil.copy(activity));
						}
					}
				}
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMActivityLogService, ErrorCode.CAUSE_Database), e.getMessage());
		}
		
		return activityList;
	}
}