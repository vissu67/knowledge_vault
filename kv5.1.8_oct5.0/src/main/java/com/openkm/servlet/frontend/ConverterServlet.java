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
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.api.OKMDocument;
import com.openkm.api.OKMRepository;
import com.openkm.bean.Document;
import com.openkm.core.Config;
import com.openkm.core.ConversionException;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.contants.service.ErrorCode;
import com.openkm.jcr.JCRUtils;
import com.openkm.util.DocConverter;
import com.openkm.util.FileUtils;
import com.openkm.util.WebUtils;

/**
 * Document converter service
 */
public class ConverterServlet extends OKMHttpServlet {
	private static Logger log = LoggerFactory.getLogger(ConverterServlet.class);
	private static final long serialVersionUID = 1L;
	
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
			IOException {
		log.debug("service({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String uuid = WebUtils.getString(request, "uuid");
		boolean inline = WebUtils.getBoolean(request, "inline");
		boolean toPdf = WebUtils.getBoolean(request, "toPdf");
		boolean toSwf = WebUtils.getBoolean(request, "toSwf");
		boolean toDxf = WebUtils.getBoolean(request, "toDxf");
		File tmp = null;
		InputStream is = null;
		updateSessionManager(request);
		
		try {
			// Now an document can be located by UUID
			if (!uuid.equals("")) {
				String path = OKMRepository.getInstance().getNodePath(null, uuid);
				Document doc = OKMDocument.getInstance().getProperties(null, path);
				String fileName = JCRUtils.getName(doc.getPath());
				
				// Save content to temporary file
				tmp = File.createTempFile("okm", "." + FileUtils.getFileExtension(fileName));
				is = OKMDocument.getInstance().getContent(null, path, false);
				FileUtils.copy(is, tmp);
				
				// Prepare conversion
				ConversionData cd = new ConversionData();
				cd.uuid = uuid;
				cd.fileName = fileName;
				cd.mimeType = doc.getMimeType();
				cd.file = tmp;
				
				
			System.out.println("printed by vissu on 30oct");	
			System.out.println("cd.uuid =" +uuid);
			System.out.println("cd.fileName = " +fileName);
			System.out.println("cd.mimeType = " +doc.getMimeType());
			System.out.println("cd.file = "+tmp);	
			
			
			
				if (toDxf && !cd.mimeType.equals(Config.MIME_DXF)) {
					try {
						toDXF(cd);
					} catch (ConversionException e) {
						log.error(e.getMessage(), e);
					}
				} else if (toPdf && !cd.mimeType.equals(Config.MIME_PDF)) {
					try {
						toPDF(cd);
					} catch (ConversionException e) {
						log.error(e.getMessage(), e);
						InputStream tis = ConverterServlet.class.getResourceAsStream("conversion_problem.pdf");
						FileUtils.copy(tis, cd.file);
					}
				} else if (toSwf && !cd.mimeType.equals(Config.MIME_SWF)) {
					try {
						toSWF(cd);
					} catch (ConversionException e) {
						log.error(e.getMessage(), e);
						InputStream tis = ConverterServlet.class.getResourceAsStream("conversion_problem.swf");
						FileUtils.copy(tis, cd.file);
					}
				}
				
				WebUtils.sendFile(request, response, cd.fileName, cd.mimeType, inline, cd.file);
				is.close();
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
			org.apache.commons.io.FileUtils.deleteQuietly(tmp);
		}
		
		log.debug("service: void");
	}
	
	/**
	 * Handles DXF conversion
	 */
	private void toDXF(ConversionData cd) throws ConversionException, DatabaseException, IOException {
		File dxfCache = new File(Config.CACHE_DXF + File.separator + cd.uuid + ".dxf");
		
		if (DocConverter.getInstance().convertibleToDxf(cd.mimeType)) {
			if (!dxfCache.exists()) {
				try {
					if (cd.mimeType.equals(Config.MIME_DWG)) {
						DocConverter.getInstance().dwg2dxf(cd.file, dxfCache);
					} else if (cd.mimeType.equals(Config.MIME_DXF)) {
						// Document already in DXF format
					} else {
						throw new NotImplementedException("Conversion from '" + cd.mimeType + "' to DXF not available");
					}
				} catch (ConversionException e) {
					dxfCache.delete();
					throw e;
				} finally {
					cd.mimeType = Config.MIME_DXF;
					cd.fileName = FileUtils.getFileName(cd.fileName) + ".dxf";
				}
			}
			
			if (dxfCache.exists()) cd.file = dxfCache;
			cd.mimeType = Config.MIME_DXF;
			cd.fileName = FileUtils.getFileName(cd.fileName) + ".dxf";
		} else {
			throw new NotImplementedException("Conversion from '" + cd.mimeType + "' to DXF not available");
		}
	}
	
	/**
	 * Handles PDF conversion
	 */
	private void toPDF(ConversionData cd) throws ConversionException, DatabaseException, IOException {
		File pdfCache = new File(Config.CACHE_PDF + File.separator + cd.uuid + ".pdf");
		
		if (DocConverter.getInstance().convertibleToPdf(cd.mimeType)) {
			if (!pdfCache.exists()) {
				try {
					if (cd.mimeType.equals(Config.MIME_POSTSCRIPT)) {
						DocConverter.getInstance().ps2pdf(cd.file, pdfCache);
					} else if (cd.mimeType.equals(Config.MIME_TIFF)) {
						DocConverter.getInstance().tiff2pdf(cd.file, pdfCache);
					} else if (DocConverter.validImageMagick.contains(cd.mimeType)) {
						DocConverter.getInstance().img2pdf(cd.file, cd.mimeType, pdfCache);
					} else if (DocConverter.validOpenOffice.contains(cd.mimeType)) {
						DocConverter.getInstance().doc2pdf(cd.file, cd.mimeType, pdfCache);
					} else if (DocConverter.validAutoCad.contains(cd.mimeType)) {
						DocConverter.getInstance().cad2pdf(cd.file, cd.mimeType, pdfCache);
					} else if (cd.mimeType.equals(Config.MIME_PDF)) {
						// Document already in PDF format
					} else {
						throw new NotImplementedException("Conversion from '" + cd.mimeType + "' to PDF not available");
					}
				} catch (ConversionException e) {
					pdfCache.delete();
					throw e;
				} finally {
					cd.mimeType = Config.MIME_PDF;
					cd.fileName = FileUtils.getFileName(cd.fileName) + ".pdf";
				}
			}
			
			if (pdfCache.exists()) cd.file = pdfCache;
			cd.mimeType = Config.MIME_PDF;
			cd.fileName = FileUtils.getFileName(cd.fileName) + ".pdf";
		} else {
			throw new NotImplementedException("Conversion from '" + cd.mimeType + "' to PDF not available");
		}
	}
	
	/**
	 * Handles SWF conversion 
	 */
	private void toSWF(ConversionData cd) throws ConversionException, DatabaseException, IOException {
		File swfCache = new File(Config.CACHE_SWF + File.separator + cd.uuid + ".swf");
		
		if (DocConverter.getInstance().convertibleToSwf(cd.mimeType)) {
			if (!swfCache.exists()) {
				try {
					if (cd.mimeType.equals(Config.MIME_PDF)) {
						DocConverter.getInstance().pdf2swf(cd.file, swfCache);
					} else if (DocConverter.getInstance().convertibleToPdf(cd.mimeType)) {
						toPDF(cd);
						DocConverter.getInstance().pdf2swf(cd.file, swfCache);
					} else if (cd.mimeType.equals(Config.MIME_SWF)) {
						// Document already in SWF format
					} else {
						throw new NotImplementedException("Conversion from '" + cd.mimeType + "' to SWF not available");
					}
				} catch (ConversionException e) {
					swfCache.delete();
					throw e;
				} finally {
					cd.mimeType = Config.MIME_SWF;
					cd.fileName = FileUtils.getFileName(cd.fileName) + ".swf";
				}
			}
			
			if (swfCache.exists()) cd.file = swfCache;
			cd.mimeType = Config.MIME_SWF;
			cd.fileName = FileUtils.getFileName(cd.fileName) + ".swf";
		} else {
			throw new NotImplementedException("Conversion from '" + cd.mimeType + "' to SWF not available");
		}
	}
	
	/**
	 * For internal use only.
	 */
	private class ConversionData {
		private String uuid;
		private String fileName;
		private String mimeType;
		private File file;
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("{");
			sb.append("uuid="); sb.append(uuid);
			sb.append(", fileName="); sb.append(fileName);
			sb.append(", mimeType="); sb.append(mimeType);
			sb.append(", file="); sb.append(file);
			sb.append("}");
			return sb.toString();
		}
	}
}
