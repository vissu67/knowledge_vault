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

package com.openkm.servlet.admin;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.HttpSessionInfo;
import com.openkm.core.HttpSessionManager;
import com.openkm.util.UserActivity;

/**
 * Logged users servlet
 */
public class LoggedUsersServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(LoggedUsersServlet.class);
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("doGet({}, {})", request, response);
		ServletContext sc = getServletContext();
		request.setCharacterEncoding("UTF-8");
		updateSessionManager(request);
		
		List<HttpSessionInfo> sessions = HttpSessionManager.getInstance().getSessions();
		sc.setAttribute("sessions", sessions);
		sc.getRequestDispatcher("/admin/logged_users.jsp").forward(request, response);
		
		// Activity log
		UserActivity.log(request.getRemoteUser(), "ADMIN_LOGGED_USERS", null, null);
	}
}
