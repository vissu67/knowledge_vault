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
import java.security.NoSuchAlgorithmException;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.DatabaseException;
import com.openkm.dao.TwitterAccountDAO;
import com.openkm.dao.bean.TwitterAccount;
import com.openkm.jcr.JCRUtils;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;

/**
 * User twitter accounts servlet
 */
public class TwitterAccountServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(TwitterAccountServlet.class);
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("doGet({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		Session session = null;
		updateSessionManager(request);
		
		try {
			session = JCRUtils.getSession();
			
			if (action.equals("create")) {
				create(session, request, response);
			} else if (action.equals("edit")) {
				edit(session, request, response);
			} else if (action.equals("delete")) {
				delete(session, request, response);
			}
			
			if (action.equals("") || WebUtils.getBoolean(request, "persist")) {
				list(session, request, response);
			}
		} catch (LoginException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} finally {
			JCRUtils.logout(session);
		}
	}
	
	/**
	 * New twitter account
	 */
	private void create(Session session, HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException, DatabaseException {
		log.info("create({}, {}, {})", new Object[] { session, request, response });
		
		if (WebUtils.getBoolean(request, "persist")) {
			TwitterAccount ta = new TwitterAccount();
			ta.setUser(WebUtils.getString(request, "ta_user"));
			ta.setTwitterUser(WebUtils.getString(request, "ta_tuser"));
			ta.setActive(WebUtils.getBoolean(request, "ta_active"));
			TwitterAccountDAO.create(ta);
			
			// Activity log
			UserActivity.log(session.getUserID(), "ADMIN_TWITTER_ACCOUNT_CREATE", ta.getUser(), ta.toString());
		} else {
			ServletContext sc = getServletContext();
			TwitterAccount ta = new TwitterAccount();
			ta.setUser(WebUtils.getString(request, "ta_user"));
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("ta", ta);
			sc.getRequestDispatcher("/admin/twitter_account_edit.jsp").forward(request, response);
		}
		
		log.debug("create: void");
	}
	
	/**
	 * Edit twitter account
	 */
	private void edit(Session session, HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException, DatabaseException, NoSuchAlgorithmException {
		log.debug("edit({}, {}, {})", new Object[] { session, request, response });
		
		if (WebUtils.getBoolean(request, "persist")) {
			TwitterAccount ta = new TwitterAccount();
			ta.setId(WebUtils.getInt(request, "ta_id"));
			ta.setUser(WebUtils.getString(request, "ta_user"));
			ta.setTwitterUser(WebUtils.getString(request, "ta_tuser"));
			ta.setActive(WebUtils.getBoolean(request, "ta_active"));
			TwitterAccountDAO.update(ta);
			
			// Activity log
			UserActivity.log(session.getUserID(), "ADMIN_TWITTER_ACCOUNT_EDIT", Integer.toString(ta.getId()), ta.toString());
		} else {
			ServletContext sc = getServletContext();
			int taId = WebUtils.getInt(request, "ta_id");
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("ta", TwitterAccountDAO.findByPk(taId));
			sc.getRequestDispatcher("/admin/twitter_account_edit.jsp").forward(request, response);
		}
		
		log.debug("edit: void");
	}
	
	/**
	 * Update twitter account
	 */
	private void delete(Session session, HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException, DatabaseException, NoSuchAlgorithmException {
		log.debug("delete({}, {}, {})", new Object[] { session, request, response });
		
		if (WebUtils.getBoolean(request, "persist")) {
			int taId = WebUtils.getInt(request, "ta_id");
			TwitterAccountDAO.delete(taId);
			
			// Activity log
			UserActivity.log(session.getUserID(), "ADMIN_TWITTER_ACCOUNT_DELETE", Integer.toString(taId), null);
		} else {
			ServletContext sc = getServletContext();
			int taId = WebUtils.getInt(request, "ta_id");
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("ta", TwitterAccountDAO.findByPk(taId));
			sc.getRequestDispatcher("/admin/twitter_account_edit.jsp").forward(request, response);
		}
		
		log.debug("delete: void");
	}

	/**
	 * List twitter accounts
	 */
	private void list(Session session, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException {
		log.debug("list({}, {}, {})", new Object[] { session, request, response });
		ServletContext sc = getServletContext();
		String usrId = WebUtils.getString(request, "ta_user");
		sc.setAttribute("ta_user", usrId);
		sc.setAttribute("twitterAccounts", TwitterAccountDAO.findByUser(usrId, false));
		sc.getRequestDispatcher("/admin/twitter_account_list.jsp").forward(request, response);
		log.debug("list: void");
	}
}
