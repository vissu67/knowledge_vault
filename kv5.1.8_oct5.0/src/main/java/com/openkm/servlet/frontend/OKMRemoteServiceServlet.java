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

package com.openkm.servlet.frontend;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.user.server.rpc.SerializationPolicy;
import com.openkm.core.Config;
import com.openkm.core.HttpSessionManager;

/**
 * Extends the RemoteServiceServlet to obtain token auth on development and production
 * environments. Config.GWTDS determines the environment development and production values.
 * 
 * @author jllort
 *
 */
public class OKMRemoteServiceServlet extends RemoteServiceServlet {
	private static Logger log = LoggerFactory.getLogger(OKMRemoteServiceServlet.class);
	private static final long serialVersionUID = 1L;
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
	}
	
	@Override
	protected SerializationPolicy doGetSerializationPolicy(HttpServletRequest request, String moduleBaseURL, String strongName) {
	    if (Config.SYSTEM_APACHE_REQUEST_HEADER_FIX) {
	    	// Get base url from the header instead of the body. This way 
	    	// Apache reverse proxy with rewrite on header can work.
	    	// Suggested at http://stackoverflow.com/questions/1517290/problem-with-gwt-behind-a-reverse-proxy-either-nginx-or-apache
	    	// ProxyPass /app/ ajp://localhost:8009/OpenKM/
	    	// RequestHeader edit X-GWT-Module-Base ^(.*)/app/(.*)$ $1/OpenKM/$2
	    	String moduleBaseURLHdr = request.getHeader("X-GWT-Module-Base");
	    	log.debug("X-GWT-Module-Base: {}", moduleBaseURLHdr);
	    	
	    	if	(moduleBaseURLHdr != null) {
	    		moduleBaseURL = moduleBaseURLHdr;
	    	}
	    }

	    return super.doGetSerializationPolicy(request, moduleBaseURL, strongName);
	}
	
	public void updateSessionManager() {
		HttpSessionManager.getInstance().update(getThreadLocalRequest().getSession().getId());
	}
}
