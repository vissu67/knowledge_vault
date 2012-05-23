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
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.jcr.JCRUtils;
import com.openkm.util.ArchiveUtils;
import com.openkm.util.FileUtils;

/**
 * Repository backup servlet
 */
public class RepositoryBackupServlet extends BaseServlet {
	private static Logger log = LoggerFactory.getLogger(RepositoryBackupServlet.class);
	private static final long serialVersionUID = 1L;
	
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
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("doGet({}, {})", request, response);
		File backup = null;
		updateSessionManager(request);
		
		try {
			backup = JCRUtils.hotBackup();
			String archive = backup.getName() + ".zip";
			log.info("FileName: {}", archive);
			response.setHeader("Content-disposition", "attachment; filename=\""+archive+"\"");
			response.setContentType("application/zip");
			OutputStream out = response.getOutputStream();
			ArchiveUtils.createZip(backup, "", out);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			FileUtils.deleteQuietly(backup);
		}
	}
}
