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

package com.openkm.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jcr.Repository;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.jackrabbit.webdav.simple.SimpleWebdavServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.Config;
import com.openkm.module.direct.DirectRepositoryModule;
import com.openkm.webdav.LocatorFactoryImplEx;

/**
 * Servlet Class
 * 
 * @param name="x-missing-auth-mapping" value="pavila:quickly5"
 * @param name="authenticate-header" value="Basic realm=\"OpenKM Webdav Server\""
 * @param name="resource-path-prefix" value="/repository"
 * @param name="resource-config" value="/WEB-INF/config.xml"
 */
public class WebdavServlet extends SimpleWebdavServlet {
	private static Logger log = LoggerFactory.getLogger(WebdavServlet.class);
	private static final long serialVersionUID = 1L;

	public void init() throws ServletException {
		log.info("*** Webdav initializing... ***");
		super.init();
		setLocatorFactory(new LocatorFactoryImplEx(getPathPrefix()));
		log.info("*** Webdav initialized ***");
	}
	
	@Override
	public Repository getRepository() {
		log.debug("getRepository()");
		Repository repository = null;
		
		try {
			repository = DirectRepositoryModule.getRepository();
		} catch (javax.jcr.RepositoryException e) {
			e.printStackTrace();
		}
		
		return repository;
	}
	
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response)
    		throws ServletException, IOException {
		if (Config.SYSTEM_WEBDAV_SERVER) {
			super.service(request, response);
		} else {
			PrintWriter out = response.getWriter();
			out.println("WebDAV is disabled. Contact with your administrator.");
			out.flush();
			out.close();
		}
	}
}
