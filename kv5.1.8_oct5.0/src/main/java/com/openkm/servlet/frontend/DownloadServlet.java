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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.api.OKMDocument;
import com.openkm.api.OKMRepository;
import com.openkm.bean.Document;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.contants.service.ErrorCode;
import com.openkm.jcr.JCRUtils;
import com.openkm.util.ArchiveUtils;
import com.openkm.util.FileUtils;
import com.openkm.util.WebUtils;
import com.openkm.util.impexp.RepositoryExporter;
import com.openkm.util.impexp.TextInfoDecorator;

/**
 * Documento download servlet
 */
public class DownloadServlet extends OKMHttpServlet {
	private static Logger log = LoggerFactory.getLogger(DownloadServlet.class);
	private static final long serialVersionUID = 1L;
	private static final boolean exportZip = true;
	private static final boolean exportJar = false;
	
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		log.debug("service({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String id = request.getParameter("id");
		String path = id != null?new String(id.getBytes("ISO-8859-1"), "UTF-8"):null;
		String uuid = request.getParameter("uuid");
		String checkout = request.getParameter("checkout");
		String ver = request.getParameter("ver");
		boolean export = request.getParameter("export") != null;
		boolean inline = request.getParameter("inline") != null;
		File tmp = File.createTempFile("okm", ".tmp");
		Document doc = null;
		InputStream is = null;
		updateSessionManager(request);
		
		try {
			// Now an document can be located by UUID
			if (uuid != null && !uuid.equals("")) {
				path = OKMRepository.getInstance().getNodePath(null, uuid);
			}
						
			if (export) {
				if (exportZip) {
					// Get document
					FileOutputStream os = new FileOutputStream(tmp);
					exportZip(path, os);
					os.flush();
					os.close();
					is = new FileInputStream(tmp);
					
					// Send document
					String fileName = JCRUtils.getName(path) + ".zip";
					WebUtils.sendFile(request, response, fileName, "application/zip", inline, is);
				} else if (exportJar) {
					// Get document
					FileOutputStream os = new FileOutputStream(tmp);
					exportJar(path, os);
					os.flush();
					os.close();
					is = new FileInputStream(tmp);
					
					// Send document
					String fileName = JCRUtils.getName(path) + ".jar";
					WebUtils.sendFile(request, response, fileName, "application/x-java-archive", inline, is);

				}
			} else {
				// Get document
				doc = OKMDocument.getInstance().getProperties(null, path);
				
				if (ver != null && !ver.equals("")) {
					is = OKMDocument.getInstance().getContentByVersion(null, path, ver);
				} else {
					is = OKMDocument.getInstance().getContent(null, path, checkout != null);
				}
				
				// Send document
				String fileName = JCRUtils.getName(doc.getPath());
				WebUtils.sendFile(request, response, fileName, doc.getMimeType(), inline, is);
			}
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new ServletException(new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDownloadService, ErrorCode.CAUSE_PathNotFound), e.getMessage()));
		} catch (RepositoryException e) {
			log.warn(e.getMessage(), e);
			throw new ServletException(new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDownloadService, ErrorCode.CAUSE_Repository), e.getMessage()));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new ServletException(new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDownloadService, ErrorCode.CAUSE_IO), e.getMessage()));
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new ServletException(new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDownloadService, ErrorCode.CAUSE_Database), e.getMessage()));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ServletException(new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDownloadService, ErrorCode.CAUSE_General), e.getMessage()));
		} finally {
			IOUtils.closeQuietly(is);
			FileUtils.deleteQuietly(tmp);
		}
		
		log.debug("service: void");
	}
	
	/**
	 * Generate a zip file from a repository folder path   
	 */
	private void exportZip(String path, OutputStream os) throws PathNotFoundException, AccessDeniedException,
			RepositoryException, ArchiveException, IOException, DatabaseException  {
		log.debug("exportZip({}, {})", path, os);
		File tmp = null;
		
		try {
			tmp = FileUtils.createTempDir();
			
			// Export files
			StringWriter out = new StringWriter();
			RepositoryExporter.exportDocuments(null, path, tmp, false, out, new TextInfoDecorator(path));
			out.close();
			
			// Zip files
			ArchiveUtils.createZip(tmp, JCRUtils.getName(path), os);
		} catch (IOException e) {
			log.error("Error exporting zip", e);
			throw e;
		} finally {
			if (tmp != null) {
				try {
					org.apache.commons.io.FileUtils.deleteDirectory(tmp);
				} catch (IOException e) {
					log.error("Error deleting temporal directory", e);
					throw e;
				}
			}
		}
		
		log.debug("exportZip: void");
	}
	
	/**
	 * Generate a jar file from a repository folder path   
	 */
	private void exportJar(String path, OutputStream os) throws PathNotFoundException, AccessDeniedException,
			RepositoryException, ArchiveException, IOException, DatabaseException  {
		log.debug("exportJar({}, {})", path, os);
		File tmp = null;
		
		try {
			tmp = FileUtils.createTempDir();
			
			// Export files
			StringWriter out = new StringWriter();
			RepositoryExporter.exportDocuments(null, path, tmp, false, out, new TextInfoDecorator(path));
			out.close();
			
			// Jar files
			ArchiveUtils.createJar(tmp, JCRUtils.getName(path), os);
		} catch (IOException e) {
			log.error("Error exporting jar", e);
			throw e;
		} finally {
			if (tmp != null) {
				try {
					org.apache.commons.io.FileUtils.deleteDirectory(tmp);
				} catch (IOException e) {
					log.error("Error deleting temporal directory", e);
					throw e;
				}
			}
		}
		
		log.debug("exportJar: void");
	}
}
