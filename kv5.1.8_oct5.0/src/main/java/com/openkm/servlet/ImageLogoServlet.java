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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.StoredFile;
import com.openkm.core.Config;
import com.openkm.util.SecureStore;
import com.openkm.util.WebUtils;

/**
 * Image Logo Servlet
 */
public class ImageLogoServlet extends HttpServlet {
	private static Logger log = LoggerFactory.getLogger(ImageLogoServlet.class);
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		String img = request.getPathInfo();
		
		try {
			if (img != null && img.length() > 1) {
				StoredFile logo = getImage(img.substring(1));
				
				if (logo != null) {
					byte[] content = SecureStore.b64Decode(logo.getContent());
					ByteArrayInputStream bais = new ByteArrayInputStream(content);
					WebUtils.sendFile(request, response, logo.getName(), logo.getMime(), true, bais);
				} else {
					sendError(request, response);
				}
			}
		} catch (MalformedURLException e) {
			sendError(request, response);
			log.warn(e.getMessage(), e);
		} catch (IOException e) {
			sendError(request, response);
			log.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Send error image
	 */
	private void sendError(HttpServletRequest request, HttpServletResponse response) throws IOException {
		InputStream is = getServletContext().getResource("/img/error.png").openStream();
		WebUtils.sendFile(request, response, "error.png", "image/png", true, is);
		is.close();
	}

	/**
	 * Get requested image input stream.
	 */
	private StoredFile getImage(String img) throws MalformedURLException, IOException {
		log.debug("getImage({})", img);
		StoredFile stFile = null;
		
		if ("login".equals(img)) {
			stFile = Config.LOGO_LOGIN;
		} else if ("mobile".equals(img)) {
			stFile = Config.LOGO_MOBILE;
		} else if ("report".equals(img)) {
			stFile = Config.LOGO_REPORT;
		}
		
		log.debug("getImage: {}", stFile);
		return stFile;
	}
}
