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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.util.MailUtils;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;

/**
 * CheckEmailServlet servlet
 */
public class CheckEmailServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(LogCatServlet.class);
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws 
			ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		updateSessionManager(request);
		
		if (action.equals("send")) {
			send(request, response);
		} else {
			ServletContext sc = getServletContext();
			sc.getRequestDispatcher("/admin/check_email.jsp").forward(request, response);
		}
	}
	
	/**
	 * Send email
	 */
	private void send(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("send({}, {})", request, response);
		String from = WebUtils.getString(request, "from");
		String to = WebUtils.getString(request, "to");
		String subject = WebUtils.getString(request, "subject");
		String content = WebUtils.getString(request, "content");
		ServletContext sc = getServletContext();
		sc.setAttribute("from", from);
		sc.setAttribute("to", to);
		sc.setAttribute("subject", subject);
		sc.setAttribute("content", content);
		
		try {
			MailUtils.sendMessage(from, to, subject, content);
			sc.setAttribute("error", "Ok");
		} catch (Exception e) {
			sc.setAttribute("error", e.getMessage());
		}
		
		sc.getRequestDispatcher("/admin/check_email.jsp").forward(request, response);
		
		// Activity log
		UserActivity.log(request.getRemoteUser(), "ADMIN_CHECK_EMAIL", null, from+", "+to+", "+subject+", "+content);		
		log.debug("view: void");
	}
}
