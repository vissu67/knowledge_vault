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

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.Config;
import com.openkm.util.FormatUtil;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;

/**
 * LogCatServlet servlet
 */
public class LogCatServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(LogCatServlet.class);
	private static File logFolder = new File(Config.HOME_DIR + "/server/default/log");
	
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		String method = request.getMethod();
		
		if (checkMultipleInstancesAccess(request, response)) {
			if (method.equals(METHOD_GET)) {
				doGet(request, response);
			} else if (method.equals(METHOD_POST)) {
				doPost(request, response);
			}
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws 
			ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		updateSessionManager(request);
		
		if (action.equals("view")) {
			view(request, response);
		} else if (action.equals("list") || action.equals("")) {
			list(request, response);
		} else {
			ServletContext sc = getServletContext();
			sc.getRequestDispatcher("/admin/logcat.jsp").forward(request, response);
		}
	}

	/**
	 * List logs
	 */
	private void list(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.info("list({}, {})", request, response);
		ServletContext sc = getServletContext();
		sc.setAttribute("files", FileUtils.listFiles(logFolder, null, false));
		sc.getRequestDispatcher("/admin/logcat.jsp").forward(request, response);
		
		// Activity log
		UserActivity.log(request.getRemoteUser(), "ADMIN_LOGCAT_LIST", null, logFolder.getPath());
		
		log.debug("list: void");
	}
	
	/**
	 * View log
	 */
	private void view(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("view({}, {})", request, response);
		int begin = WebUtils.getInt(request, "begin");
		int end = WebUtils.getInt(request, "end");
		String str = WebUtils.getString(request, "str");
		String file = WebUtils.getString(request, "file");
		ServletContext sc = getServletContext();
		File lf = new File(logFolder, file);
		sc.setAttribute("file", file);
		sc.setAttribute("begin", begin);
		sc.setAttribute("end", end);
		sc.setAttribute("str", str);
		sc.setAttribute("messages", FormatUtil.parseLog(lf, begin, end, str));
		sc.getRequestDispatcher("/admin/logcat_view.jsp").forward(request, response);
		
		// Activity log
		UserActivity.log(request.getRemoteUser(), "ADMIN_LOGCAT_VIEW", file, str);
		
		log.debug("view: void");
	}
}
