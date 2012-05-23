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

package com.openkm.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JRQuery;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bsh.EvalError;
import bsh.Interpreter;

import com.openkm.bean.form.FormElement;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.ParseException;
import com.openkm.dao.HibernateUtil;
import com.openkm.dao.LegacyDAO;
import com.openkm.dao.ReportDAO;
import com.openkm.dao.bean.Report;

/**
 * Utilidades para jasper reports
 * 
 * http://www.javalobby.org/articles/hibernatequery103/
 */
public class ReportUtils {
	private static Logger log = LoggerFactory.getLogger(ReportUtils.class);
	public static Map<String, JasperReport> JasperCharged = new HashMap<String, JasperReport>();
	
	public static final int OUTPUT_TEXT = 0;
	public static final int OUTPUT_HTML = 1;
	public static final int OUTPUT_PDF = 2;
	public static final int OUTPUT_RTF = 3;
	public static final int OUTPUT_CSV = 4;
	public static final int OUTPUT_ODT = 5;
	public static final int OUTPUT_DOCX = 6;
	
	public static final String MIME_JASPER = "application/x-jasper";
	public static final String MIME_JRXML = "application/x-jrxml";
	public static final String MIME_REPORT = "application/x-report";
	
	public static final String MIME_TEXT = "text/plain"; 
	public static final String MIME_HTML = "text/html";
	public static final String MIME_PDF = "application/pdf";
	public static final String MIME_RTF = "application/rtf";
	public static final String MIME_CSV = "text/csv";
	public static final String MIME_ODT = "application/vnd.oasis.opendocument.text";
	public static final String MIME_DOCX = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
	
	public static final String[] FILE_MIME = { MIME_TEXT, MIME_HTML, MIME_PDF, MIME_RTF, MIME_CSV, MIME_ODT, MIME_DOCX };
	public static final String[] FILE_EXTENSION = { ".txt", ".html", ".pdf", ".rtf", ".csv", ".odt", ".docx" };

	/**
	 * Generates a report based on a map collection (from file)
	 */
	public static OutputStream generateReport(OutputStream out, String fileReport, 
			Map<String, String> parameters, int outputType, Collection<Map<String, String>> list)
			throws Exception {
		if (!JasperCharged.containsKey(fileReport)) {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			try {
				JasperReport jasperReport = JasperCompileManager.compileReport(cl.getResourceAsStream(fileReport));
				JasperCharged.put(fileReport, jasperReport);
			} catch (Exception e) {
				throw new Exception("Compiling error: " + e.getMessage());
			}
		}

		try {
			JasperReport jasperReport = (JasperReport) JasperCharged.get(fileReport);
			JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, 
					new JRMapCollectionDataSource(list));
			export(out, outputType, print);
		} catch (JRException je) {
			throw new JRException("Error generating report", je);
		}

		return out;
	}
	
	/**
	 * Generates a report based on a map collection (from stream)
	 */
	public static OutputStream generateReport(OutputStream out, InputStream report, 
			Map<String, String> parameters, int outputType, Collection<Map<String, String>> list)
			throws JRException {
		JasperReport jasperReport = JasperCompileManager.compileReport(report);
		JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, 
				new JRMapCollectionDataSource(list));
		export(out, outputType, print);
		return out;
	}
	
	/**
	 * Generates a report based on a map collection (from stream)
	 */
	public static OutputStream generateReport(OutputStream out, JasperReport jr, 
			Map<String, String> parameters, int outputType) throws JRException, EvalError {
		JRQuery query = jr.getQuery();
		
		if (query != null) {
			Interpreter bsh = new Interpreter(null, System.out, System.err, false);
			
			// Set parameters
			for (Map.Entry<String, String> entry : parameters.entrySet()) {
				bsh.set(entry.getKey(), entry.getValue());
			}
			
			@SuppressWarnings("rawtypes")
			Collection list = (Collection) bsh.eval(query.getText());
			JasperPrint print = JasperFillManager.fillReport(jr, parameters, 
					new JRMapCollectionDataSource(list));
			export(out, outputType, print);
		} else {
			throw new JRException("Null report query string");
		}
		
		return out;
	}


	/**
	 * Generates a report based on a JDBC connection (from file)
	 */
	public static OutputStream generateReport(OutputStream out, String fileReport, 
			Map<String, String> parameters, int outputType,	Connection con) throws Exception {
		if (!JasperCharged.containsKey(fileReport)) {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			try {
				JasperReport jasperReport = JasperCompileManager.compileReport(cl.getResourceAsStream(fileReport));
				JasperCharged.put(fileReport, jasperReport);
			} catch (Exception e) {
				throw new Exception("Compiling error: " + e.getMessage());
			}
		}

		try {
			JasperReport jasperReport = (JasperReport) JasperCharged.get(fileReport);
			JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, con);
			export(out, outputType, print);
		} catch (JRException je) {
			throw new JRException("Error generating report", je);
		}

		return out;
	}

	/**
	 * Generates a report based on a JDBC connection (from stream)
	 */
	public static OutputStream generateReport(OutputStream out, JasperReport jr, 
			Map<String, String> parameters, int outputType,	Connection con) throws JRException {
		JasperPrint print = JasperFillManager.fillReport(jr, parameters, con);
		export(out, outputType, print);
		return out;
	}
	
	/**
	 * Export report 
	 */
	private static void export(OutputStream out, int outputType, JasperPrint print) throws JRException {
		switch (outputType) {
		case OUTPUT_TEXT:
			JRTextExporter textExp = new JRTextExporter();
			textExp.setParameter(JRTextExporterParameter.PAGE_HEIGHT, 300);
			textExp.setParameter(JRTextExporterParameter.PAGE_WIDTH, 100);
			textExp.setParameter(JRTextExporterParameter.CHARACTER_WIDTH, 80);
			textExp.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");			
			textExp.setParameter(JRExporterParameter.JASPER_PRINT, print);
			textExp.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
			textExp.exportReport();
			break;
			
		case OUTPUT_PDF:
			JRPdfExporter pdfExp = new JRPdfExporter();
			pdfExp.setParameter(JRExporterParameter.JASPER_PRINT, print);
			pdfExp.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
			pdfExp.exportReport();
			break;
			
		case OUTPUT_HTML:
			JRHtmlExporter htmlExp = new JRHtmlExporter();
			htmlExp.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
			htmlExp.setParameter(JRExporterParameter.JASPER_PRINT, print);
			htmlExp.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
			htmlExp.setParameter(JRHtmlExporterParameter.IS_USING_IMAGES_TO_ALIGN,	Boolean.FALSE);
			htmlExp.exportReport();
			break;
			
		case OUTPUT_RTF:
			JRRtfExporter rtfExp = new JRRtfExporter();
			rtfExp.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
			rtfExp.setParameter(JRExporterParameter.JASPER_PRINT, print);
			rtfExp.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
			rtfExp.exportReport();
			break;
			
		case OUTPUT_CSV:
			JRCsvExporter csvExp = new JRCsvExporter();
			csvExp.setParameter(JRExporterParameter.CHARACTER_ENCODING, "UTF-8");
			csvExp.setParameter(JRExporterParameter.JASPER_PRINT, print);
			csvExp.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
			csvExp.exportReport();
			break;
			
		case OUTPUT_ODT:
			JROdtExporter odtExp = new JROdtExporter();
			odtExp.setParameter(JRExporterParameter.JASPER_PRINT, print);
			odtExp.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
			odtExp.exportReport();
			break;
			
		case OUTPUT_DOCX:
			JRDocxExporter docxExp = new JRDocxExporter();
			docxExp.setParameter(JRExporterParameter.JASPER_PRINT, print);
			docxExp.setParameter(JRExporterParameter.OUTPUT_STREAM, out);
			docxExp.exportReport();
			break;
		}
	}
	
	/**
	 * Get report parameters
	 */
	public static List<FormElement> getReportParameters(int rpId) throws ParseException, DatabaseException,
			IOException {
		log.debug("getReportParameters({})", rpId);
		//long begin = Calendar.getInstance().getTimeInMillis();
		List<FormElement> params = null;
		ByteArrayInputStream bais = null;
		ZipInputStream zis = null;
		
		try {
			Report rp = ReportDAO.findByPk(rpId);
			bais = new ByteArrayInputStream(SecureStore.b64Decode(rp.getFileContent()));
			zis = new ZipInputStream(bais);
			ZipEntry zi = null;
			
			while ((zi = zis.getNextEntry()) != null) {
				if ("params.xml".equals(zi.getName())) {
					params = FormUtils.parseReportParameters(zis);
					break;
				}
			}
			
			if (params == null) {
				params = new ArrayList<FormElement>();
				log.warn("Report '{}' has no params.xml file", rpId);
			}
			
			// Activity log
			// UserActivity.log(session.getUserID(), "GET_REPORT_PARAMETERS", rpId+"", null);
		} catch (IOException e) {
			throw e;
		} finally {
			IOUtils.closeQuietly(zis);
			IOUtils.closeQuietly(bais);
		}
		
		log.debug("getReportParameters: {}", params);
		//log.info("Time: "+(Calendar.getInstance().getTimeInMillis()-begin)+" ms");
		return params;
	}
	
	/**
	 * Execute report
	 */
	public static ByteArrayOutputStream execute(Report rp, Map<String, String> params, int format) throws
			JRException, IOException, EvalError {
		ByteArrayOutputStream baos = null;
		ByteArrayInputStream bais = null;
		ZipInputStream zis = null;
		org.hibernate.Session dbSession = null;
		Connection con = null;
		
		try {
			baos = new ByteArrayOutputStream();
			bais = new ByteArrayInputStream(SecureStore.b64Decode(rp.getFileContent()));
			JasperReport jr = null;
			
			// Obtain or compile report
			if (ReportUtils.MIME_JRXML.equals(rp.getFileMime())) {
				log.debug("Report type: JRXML");
				jr = JasperCompileManager.compileReport(bais);
			} else if (ReportUtils.MIME_JASPER.equals(rp.getFileMime())) {
				log.debug("Report type: JASPER");
				jr = (JasperReport) JRLoader.loadObject(bais);
			} else if (ReportUtils.MIME_REPORT.equals(rp.getFileMime())) {
				zis = new ZipInputStream(bais);
				ZipEntry zi = null;
				
				while ((zi = zis.getNextEntry()) != null) {
					if (!zi.isDirectory()) {
						String mimeType = Config.mimeTypes.getContentType(zi.getName());
						
						if (ReportUtils.MIME_JRXML.equals(mimeType)) {
							log.debug("Report type: JRXML inside ARCHIVE");
							jr = JasperCompileManager.compileReport(zis);
							break;
						} else if (ReportUtils.MIME_JASPER.equals(mimeType)) {
							log.debug("Report type: JASPER inside ARCHIVE");
							jr = (JasperReport) JRLoader.loadObject(zis);
							break;
						}
					}
				}
			}
			
			// Guess if SQL or BSH
			if (jr != null) {
				JRQuery query = jr.getQuery();
				String tq = query.getText().trim();
					
				if (tq.toUpperCase().startsWith("SELECT")) {
					log.debug("Report type: DATABASE");
					dbSession = HibernateUtil.getSessionFactory().openSession();
					con = dbSession.connection();
					ReportUtils.generateReport(baos, jr, params, format, con);
				} else {
					log.debug("Report type: SCRIPTING");
					ReportUtils.generateReport(baos, jr, params, format);
				}
			}
			
			return baos;
		} finally {
			LegacyDAO.close(con);
			HibernateUtil.close(dbSession);
			IOUtils.closeQuietly(bais);
			IOUtils.closeQuietly(baos);
			IOUtils.closeQuietly(zis);
		}
	}
}
