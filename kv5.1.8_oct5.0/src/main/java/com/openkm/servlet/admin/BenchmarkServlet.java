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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.InputMismatchException;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.jackrabbit.JcrConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.api.OKMFolder;
import com.openkm.api.OKMRepository;
import com.openkm.bean.ContentInfo;
import com.openkm.bean.Folder;
import com.openkm.bean.Repository;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.FileSizeExceededException;
import com.openkm.core.ItemExistsException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.core.UnsupportedMimeTypeException;
import com.openkm.core.UserQuotaExceededException;
import com.openkm.core.VirusDetectedException;
import com.openkm.extension.core.ExtensionException;
import com.openkm.jcr.JCRUtils;
import com.openkm.module.base.BaseFolderModule;
import com.openkm.util.Benchmark;
import com.openkm.util.FormatUtil;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;
import com.openkm.util.impexp.HTMLInfoDecorator;
import com.openkm.util.impexp.ImpExpStats;
import com.openkm.util.impexp.RepositoryImporter;

/**
 * Benchmark servlet
 */
public class BenchmarkServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(BenchmarkServlet.class);
	private static String BM_FOLDER = "benchmark";
	
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
		String action = request.getParameter("action")!=null?request.getParameter("action"):"";
		updateSessionManager(request);
				
		if (action.equals("okmImport")) {
			okmImport(request, response, BM_FOLDER + "_okm_import");
		} else if (action.equals("okmCopy")) {
			okmCopy(request, response);
		} else if (action.equals("okmApiHighGenerate")) {
			okmApiHighGenerate(request, response, BM_FOLDER + "_okm_api_high");
		} else if (action.equals("okmApiLowGenerate")) {
			okmApiLowGenerate(request, response, BM_FOLDER + "_okm_api_low");
		} else if (action.equals("okmRawGenerate")) {
			okmRawGenerate(request, response, BM_FOLDER + "_okm_raw");
		} else if (action.equals("jcrGenerate")) {
			jcrGenerate(request, response, BM_FOLDER + "_jcr");
		} else {
			ServletContext sc = getServletContext();
			sc.getRequestDispatcher("/admin/benchmark.jsp").forward(request, response);
		}
	}

	/**
	 * Load documents into repository several times
	 */
	private void okmImport(HttpServletRequest request, HttpServletResponse response, 
			String base) throws IOException {
		log.debug("okmImport({}, {}, {})", new Object[] { request, response, base });
		String path = WebUtils.getString(request, "param1");
		int times = WebUtils.getInt(request, "param2");
		PrintWriter out = response.getWriter();
		ImpExpStats tStats = new ImpExpStats();
		long tBegin = 0, tEnd = 0;
		response.setContentType("text/html");
		header(out);
		out.println("<h1>Benchmark: OpenKM import documents</h1>");
		out.flush();
		
		try {
			File dir = new File(path);
			int docs = FileUtils.listFiles(dir, null, true).size();
			out.println("<b>- Path:</b> "+path+"<br/>");
			out.println("<b>- Times:</b> "+times+"<br/>");
			out.println("<b>- Documents:</b> "+docs+"<br/>");
			out.flush();
			
			Folder rootFld = OKMRepository.getInstance().getRootFolder(null);
			Folder fld = new Folder();
			fld.setPath(rootFld.getPath() + "/" + base);
			OKMFolder.getInstance().create(null, fld);
			tBegin = System.currentTimeMillis();
			
			for (int i=0; i<times; i++) {
				out.println("<h2>Iteration "+i+"</h2>");
				out.flush();
				//out.println("<table class=\"results\" width=\"100%\">");
				//out.println("<tr><th>#</th><th>Document</th><th>Size</th></tr>");
				
				long begin = System.currentTimeMillis();
				fld.setPath(rootFld.getPath() + "/" + base + "/" + i);
				OKMFolder.getInstance().create(null, fld);
				ImpExpStats stats = RepositoryImporter.importDocuments(null, dir, fld.getPath(), false, out, 
						new HTMLInfoDecorator(docs));
				long end = System.currentTimeMillis();
				tStats.setSize(tStats.getSize() + stats.getSize());
				tStats.setFolders(tStats.getFolders() + stats.getFolders());
				tStats.setDocuments(tStats.getDocuments() + stats.getDocuments());
				
				//out.println("<table>");
				out.println("<br/>");
				out.println("<b>Size:</b> "+FormatUtil.formatSize(stats.getSize())+"<br/>");
				out.println("<b>Folders:</b> "+stats.getFolders()+"<br/>");
				out.println("<b>Documents:</b> "+stats.getDocuments()+"<br/>");
				out.println("<b>Time:</b> "+FormatUtil.formatSeconds(end - begin)+"<br/>");
				out.flush();
			}
			
			tEnd = System.currentTimeMillis();
		} catch (PathNotFoundException e) {
			out.println("<div class=\"warn\">PathNotFoundException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (ItemExistsException e) {
			out.println("<div class=\"warn\">ItemExistsException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (AccessDeniedException e) {
			out.println("<div class=\"warn\">AccessDeniedException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (RepositoryException e) {
			out.println("<div class=\"warn\">RepositoryException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (DatabaseException e) {
			out.println("<div class=\"warn\">DatabaseException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (ExtensionException e) {
			out.println("<div class=\"warn\">ExtensionException: "+e.getMessage()+"</div>");
			out.flush();
		}
		
		out.println("<hr/>");
		out.println("<b>Total size:</b> "+FormatUtil.formatSize(tStats.getSize())+"<br/>");
		out.println("<b>Total folders:</b> "+tStats.getFolders()+"<br/>");
		out.println("<b>Total documents:</b> "+tStats.getDocuments()+"<br/>");
		out.println("<b>Total time:</b> "+FormatUtil.formatSeconds(tEnd - tBegin)+"<br/>");
		out.print("</body>");
		out.print("</html>");
		out.flush();
		out.close();
		
		// Activity log
		UserActivity.log(request.getRemoteUser(), "ADMIN_BENCHMARK_OKM_IMPORT", null, 
				"Size: " + FormatUtil.formatSize(tStats.getSize()) +
				", Folders: " + tStats.getFolders() +
				", Documents: " + tStats.getDocuments() +
				", Time: " + FormatUtil.formatSeconds(tEnd - tBegin));
		log.debug("okmImport: void");
	}
	
	/**
	 * Copy documents into repository several times
	 */
	private void okmCopy(HttpServletRequest request, HttpServletResponse response) throws IOException {
		log.debug("okmCopy({}, {})", new Object[] { request, response });
		String src = WebUtils.getString(request, "param1");
		String dst = WebUtils.getString(request, "param2");
		int times = WebUtils.getInt(request, "param3");
		PrintWriter out = response.getWriter();
		ContentInfo cInfo = new ContentInfo();
		long tBegin = 0, tEnd = 0;
		response.setContentType("text/html");
		header(out);
		out.println("<h1>Benchmark: OpenKM copy documents</h1>");
		out.flush();
		
		try {
			cInfo = OKMFolder.getInstance().getContentInfo(null, src);
			out.println("<b>- Source:</b> "+src+"<br/>");
			out.println("<b>- Destination:</b> "+dst+"<br/>");
			out.println("<b>- Size:</b> "+FormatUtil.formatSize(cInfo.getSize())+"<br/>");
			out.println("<b>- Mails:</b> "+cInfo.getMails()+"<br/>");
			out.println("<b>- Folders:</b> "+cInfo.getFolders()+"<br/>");
			out.println("<b>- Documents:</b> "+cInfo.getDocuments()+"<br/>");
			out.flush();
			tBegin = System.currentTimeMillis();
			
			for (int i=0; i<times; i++) {
				out.println("<h2>Iteration "+i+"</h2>");
				out.flush();
				long begin = System.currentTimeMillis();
				Folder fld = new Folder();
				fld.setPath(dst+"/"+i);
				OKMFolder.getInstance().create(null, fld);
				OKMFolder.getInstance().copy(null, src, fld.getPath());
				long end = System.currentTimeMillis();
				out.println("<b>Time:</b> "+FormatUtil.formatSeconds(end - begin)+"<br/>");
				out.flush();
			}
			
			tEnd = System.currentTimeMillis();
		} catch (PathNotFoundException e) {
			out.println("<div class=\"warn\">PathNotFoundException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (ItemExistsException e) {
			out.println("<div class=\"warn\">ItemExistsException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (AccessDeniedException e) {
			out.println("<div class=\"warn\">AccessDeniedException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (RepositoryException e) {
			out.println("<div class=\"warn\">RepositoryException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (DatabaseException e) {
			out.println("<div class=\"warn\">DatabaseException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (UserQuotaExceededException e) {
			out.println("<div class=\"warn\">UserQuotaExceededException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (ExtensionException e) {
			out.println("<div class=\"warn\">ExtensionException: "+e.getMessage()+"</div>");
			out.flush();
		}
				
		out.println("<hr/>");
		out.println("<b>Total size:</b> "+FormatUtil.formatSize(cInfo.getSize() * times)+"<br/>");
		out.println("<b>Total mails:</b> "+cInfo.getMails() * times+"<br/>");
		out.println("<b>Total folders:</b> "+cInfo.getFolders() * times+"<br/>");
		out.println("<b>Total documents:</b> "+cInfo.getDocuments() * times+"<br/>");
		out.println("<b>Total time:</b> "+FormatUtil.formatSeconds(tEnd - tBegin)+"<br/>");
		out.print("</body>");
		out.print("</html>");
		out.flush();
		out.close();
		
		// Activity log
		UserActivity.log(request.getRemoteUser(), "ADMIN_BENCHMARK_OKM_COPY", null, 
				"Size: " + FormatUtil.formatSize(cInfo.getSize() * times) +
				", Folders: " + cInfo.getMails() * times +
				", Documents: " + cInfo.getDocuments() * times +
				", Time: " + FormatUtil.formatSeconds(tEnd - tBegin));
		log.debug("okmCopy: void");
	}
	
	/**
	 * Generate documents into repository (OpenKM API)
	 */
	private void okmApiHighGenerate(HttpServletRequest request, HttpServletResponse response, 
			String base) throws IOException {
		log.debug("okmApiHighGenerate({}, {}, {})", new Object[] { request, response });
		int maxDocuments = WebUtils.getInt(request, "param1");
		int maxFolder = WebUtils.getInt(request, "param2");
		int maxDepth = WebUtils.getInt(request, "param3");
		int maxIterations = WebUtils.getInt(request, "param4");
		PrintWriter out = response.getWriter();
		PrintWriter results = new PrintWriter(Config.HOME_DIR + File.separator + base + ".csv");
		long tBegin = 0, tEnd = 0, pBegin = 0, pEnd = 0;
		Benchmark bm = null;
		response.setContentType("text/html");
		header(out);
		out.println("<h1>Benchmark: OpenKM generate documents (API HIGH)</h1>");
		out.flush();
		
		try {
			bm = new Benchmark(maxDocuments, maxFolder, maxDepth);
			out.println("<b>- Documents:</b> "+bm.getMaxDocuments()+"<br/>");
			out.println("<b>- Folders:</b> "+bm.getMaxFolders()+"<br/>");
			out.println("<b>- Depth:</b> "+bm.getMaxDepth()+"<br/>");
			out.println("<b>- Calibration:</b> "+bm.runCalibration()+" ms<br/>");
			out.println("<b>- Calculated foldes:</b> "+bm.calculateFolders()+"<br/>");
			out.println("<b>- Calculated documents:</b> "+bm.calculateDocuments()+"<br/><br/>");
			results.print("\"Date\",");
			results.print("\"Time\",");
			results.print("\"Seconds\",");
			results.print("\"Folders\",");
			results.print("\"Documents\",");
			results.print("\"Size\"\n");
			results.flush();
			tBegin = System.currentTimeMillis();
			
			for (int i=0; i < maxIterations; i++) {
				out.println("<h2>Iteration "+i+"</h2>");
				out.println("<table class=\"results\" width=\"80%\">");
				out.println("<tr><th>Date</th><th>Partial seconds</th><th>Partial miliseconds</th><th>Total folders</th><th>Total documents</th><th>Total size</th></tr>");
				out.flush();
				
				Folder fld = OKMRepository.getInstance().getRootFolder(null);
				fld.setPath(fld.getPath() + "/" + base);
				
				if (!OKMRepository.getInstance().hasNode(null, fld.getPath())) {
					fld = OKMFolder.getInstance().create(null, fld);
				}
				
				PrintWriter pResults = new PrintWriter(Config.HOME_DIR + File.separator + base + "_" + i + ".csv");
				pResults.print("\"Date\",");
				pResults.print("\"Time\",");
				pResults.print("\"Seconds\",");
				pResults.print("\"Folders\",");
				pResults.print("\"Documents\",");
				pResults.print("\"Size\"\n");
				pResults.flush();
				
				pBegin = System.currentTimeMillis();
				bm.okmApiHighPopulate(null, fld, out, pResults);
				pEnd = System.currentTimeMillis();
				pResults.close();
				out.println("</table>");
				
				results.print("\"" + FormatUtil.formatDate(Calendar.getInstance()) + "\",");
				results.print("\"" + FormatUtil.formatSeconds(pEnd - pBegin) + "\",");
				results.print("\"" + (pEnd - pBegin) + "\",");
				results.print("\"" + bm.getTotalFolders() + "\",");
				results.print("\"" + bm.getTotalDocuments() + "\",");
				results.print("\"" + FormatUtil.formatSize(bm.getTotalSize()) + "\"\n");
				results.flush();
				
				out.println("<table class=\"results\" width=\"80%\">");
				out.println("<tr><th>Date</th><th>Partial seconds</th><th>Total folders</th><th>Total documents</th><th>Total size</th></tr>");
				out.println("<td>"+FormatUtil.formatDate(Calendar.getInstance())+"</td>");
				out.println("<td>"+FormatUtil.formatSeconds(pEnd - pBegin)+"</td>");
				out.println("<td>"+bm.getTotalFolders()+"</td>");
				out.println("<td>"+bm.getTotalDocuments()+"</td>");
				out.println("<td>"+FormatUtil.formatSize(bm.getTotalSize())+"</td>");
				out.println("</tr>");
				out.println("</table>");
				out.flush();
			}
			
			tEnd = System.currentTimeMillis();
		} catch (FileNotFoundException e) {
			out.println("<div class=\"warn\">FileNotFoundException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (PathNotFoundException e) {
			out.println("<div class=\"warn\">PathNotFoundException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (ItemExistsException e) {
			out.println("<div class=\"warn\">ItemExistsException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (AccessDeniedException e) {
			out.println("<div class=\"warn\">AccessDeniedException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (RepositoryException e) {
			out.println("<div class=\"warn\">RepositoryException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (DatabaseException e) {
			out.println("<div class=\"warn\">DatabaseException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (UserQuotaExceededException e) {
			out.println("<div class=\"warn\">UserQuotaExceededException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (InputMismatchException e) {
			out.println("<div class=\"warn\">InputMismatchException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (UnsupportedMimeTypeException e) {
			out.println("<div class=\"warn\">UnsupportedMimeTypeException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (FileSizeExceededException e) {
			out.println("<div class=\"warn\">FileSizeExceededException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (VirusDetectedException e) {
			out.println("<div class=\"warn\">VirusDetectedException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (ExtensionException e) {
			out.println("<div class=\"warn\">ExtensionException: "+e.getMessage()+"</div>");
			out.flush();
		}
		
		long elapse = tEnd - tBegin;
		out.println("<hr/>");
		out.println("<b>Total size:</b> "+FormatUtil.formatSize(bm.getTotalSize())+"<br/>");
		out.println("<b>Total folders:</b> "+bm.getTotalFolders()+"<br/>");
		out.println("<b>Total documents:</b> "+bm.getTotalDocuments()+"<br/>");
		out.println("<b>Total time:</b> "+FormatUtil.formatSeconds(elapse)+"<br/>");
		out.println("<b>Documents per second:</b> "+bm.getTotalDocuments()/(elapse/1000)+"<br/>");
		out.print("</body>");
		out.print("</html>");
		out.flush();
		out.close();
		results.close();
		
		// Activity log
		UserActivity.log(request.getRemoteUser(), "ADMIN_BENCHMARK_OKM_API_HIGH", null, 
				"Size: " + FormatUtil.formatSize(bm.getTotalSize()) +
				", Folders: " + bm.getTotalFolders() +
				", Documents: " + bm.getTotalDocuments() +
				", Time: " + FormatUtil.formatSeconds(elapse));
		log.debug("okmApiHighGenerate: void");
	}
	
	/**
	 * Generate documents into repository (OpenKM RAW)
	 */
	private void okmApiLowGenerate(HttpServletRequest request, HttpServletResponse response,
			String base) throws IOException {
		log.debug("okmApiLowGenerate({}, {}, {})", new Object[] { request, response, base });
		int maxDocuments = WebUtils.getInt(request, "param1");
		int maxFolder = WebUtils.getInt(request, "param2");
		int maxDepth = WebUtils.getInt(request, "param3");
		int maxIterations = WebUtils.getInt(request, "param4");
		PrintWriter out = response.getWriter();
		PrintWriter results = new PrintWriter(Config.HOME_DIR + File.separator + base + ".csv");
		long tBegin = 0, tEnd = 0, pBegin = 0, pEnd = 0;
		Benchmark bm = null;
		Session session = null;
		response.setContentType("text/html");
		header(out);
		out.println("<h1>Benchmark: OpenKM generate documents (API LOW)</h1>");
		out.flush();
		
		try {
			session = JCRUtils.getSession();
			bm = new Benchmark(maxDocuments, maxFolder, maxDepth);
			out.println("<b>- Documents:</b> "+bm.getMaxDocuments()+"<br/>");
			out.println("<b>- Folders:</b> "+bm.getMaxFolders()+"<br/>");
			out.println("<b>- Depth:</b> "+bm.getMaxDepth()+"<br/>");
			out.println("<b>- Calibration:</b> "+bm.runCalibration()+" ms<br/>");
			out.println("<b>- Calculated foldes:</b> "+bm.calculateFolders()+"<br/>");
			out.println("<b>- Calculated documents:</b> "+bm.calculateDocuments()+"<br/><br/>");
			results.print("\"Date\",");
			results.print("\"Time\",");
			results.print("\"Seconds\",");
			results.print("\"Folders\",");
			results.print("\"Documents\",");
			results.print("\"Size\"\n");
			results.flush();
			tBegin = System.currentTimeMillis();
			
			for (int i=0; i < maxIterations; i++) {
				out.println("<h2>Iteration "+i+"</h2>");
				out.println("<table class=\"results\" width=\"80%\">");
				out.println("<tr><th>Date</th><th>Partial seconds</th><th>Partial miliseconds</th><th>Total folders</th><th>Total documents</th><th>Total size</th></tr>");
				out.flush();
				
				Node rootNode = session.getRootNode().getNode(Repository.ROOT);
				Node baseNode = null;
				
				if (rootNode.hasNode(base)) {
					baseNode = rootNode.getNode(base);
				} else {
					baseNode = BaseFolderModule.create(session, rootNode, base);
				}
				
				PrintWriter pResults = new PrintWriter(Config.HOME_DIR + File.separator + base + "_" + i + ".csv");
				pResults.print("\"Date\",");
				pResults.print("\"Time\",");
				pResults.print("\"Seconds\",");
				pResults.print("\"Folders\",");
				pResults.print("\"Documents\",");
				pResults.print("\"Size\"\n");
				pResults.flush();
				
				pBegin = System.currentTimeMillis();
				bm.okmApiLowPopulate(session, baseNode, out, pResults);
				pEnd = System.currentTimeMillis();
				pResults.close();
				out.println("</table>");
				
				results.print("\"" + FormatUtil.formatDate(Calendar.getInstance()) + "\",");
				results.print("\"" + FormatUtil.formatSeconds(pEnd - pBegin) + "\",");
				results.print("\"" + (pEnd - pBegin) + "\",");
				results.print("\"" + bm.getTotalFolders() + "\",");
				results.print("\"" + bm.getTotalDocuments() + "\",");
				results.print("\"" + FormatUtil.formatSize(bm.getTotalSize()) + "\"\n");
				results.flush();
				
				out.println("<table class=\"results\" width=\"80%\">");
				out.println("<tr><th>Date</th><th>Partial seconds</th><th>Total folders</th><th>Total documents</th><th>Total size</th></tr>");
				out.println("<td>"+FormatUtil.formatDate(Calendar.getInstance())+"</td>");
				out.println("<td>"+FormatUtil.formatSeconds(pEnd - pBegin)+"</td>");
				out.println("<td>"+bm.getTotalFolders()+"</td>");
				out.println("<td>"+bm.getTotalDocuments()+"</td>");
				out.println("<td>"+FormatUtil.formatSize(bm.getTotalSize())+"</td>");
				out.println("</tr>");
				out.println("</table>");
				out.flush();
			}
			
			tEnd = System.currentTimeMillis();
		} catch (FileNotFoundException e) {
			out.println("<div class=\"warn\">FileNotFoundException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (javax.jcr.PathNotFoundException e) {
			out.println("<div class=\"warn\">PathNotFoundException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (javax.jcr.ItemExistsException e) {
			out.println("<div class=\"warn\">ItemExistsException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (javax.jcr.AccessDeniedException e) {
			out.println("<div class=\"warn\">AccessDeniedException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (javax.jcr.RepositoryException e) {
			out.println("<div class=\"warn\">RepositoryException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (InputMismatchException e) {
			out.println("<div class=\"warn\">InputMismatchException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (UserQuotaExceededException e) {
			out.println("<div class=\"warn\">UserQuotaExceededException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (DatabaseException e) {
			out.println("<div class=\"warn\">DatabaseException: "+e.getMessage()+"</div>");
			out.flush();
		} finally {
			JCRUtils.logout(session);
		}
		
		long elapse = tEnd - tBegin;
		out.println("<hr/>");
		out.println("<b>Total size:</b> "+FormatUtil.formatSize(bm.getTotalSize())+"<br/>");
		out.println("<b>Total folders:</b> "+bm.getTotalFolders()+"<br/>");
		out.println("<b>Total documents:</b> "+bm.getTotalDocuments()+"<br/>");
		out.println("<b>Total time:</b> "+FormatUtil.formatSeconds(elapse)+"<br/>");
		out.println("<b>Documents per second:</b> "+bm.getTotalDocuments()/(elapse/1000)+"<br/>");
		out.print("</body>");
		out.print("</html>");
		out.flush();
		out.close();
		results.close();
		
		// Activity log
		UserActivity.log(request.getRemoteUser(), "ADMIN_BENCHMARK_OKM_API_LOW", null, 
				"Size: " + FormatUtil.formatSize(bm.getTotalSize()) +
				", Folders: " + bm.getTotalFolders() +
				", Documents: " + bm.getTotalDocuments() +
				", Time: " + FormatUtil.formatSeconds(elapse));
		log.debug("okmApiLowGenerate: void");
	}
	
	/**
	 * Generate documents into repository (OpenKM RAW)
	 */
	private void okmRawGenerate(HttpServletRequest request, HttpServletResponse response,
			String base) throws IOException {
		log.debug("okmRawGenerate({}, {}, {})", new Object[] { request, response, base });
		int maxDocuments = WebUtils.getInt(request, "param1");
		int maxFolder = WebUtils.getInt(request, "param2");
		int maxDepth = WebUtils.getInt(request, "param3");
		int maxIterations = WebUtils.getInt(request, "param4");
		PrintWriter out = response.getWriter();
		PrintWriter results = new PrintWriter(Config.HOME_DIR + File.separator + base + ".csv");
		long tBegin = 0, tEnd = 0, pBegin = 0, pEnd = 0;
		Benchmark bm = null;
		Session session = null;
		response.setContentType("text/html");
		header(out);
		out.println("<h1>Benchmark: OpenKM generate documents (RAW)</h1>");
		out.flush();
		
		try {
			session = JCRUtils.getSession();
			bm = new Benchmark(maxDocuments, maxFolder, maxDepth);
			out.println("<b>- Documents:</b> "+bm.getMaxDocuments()+"<br/>");
			out.println("<b>- Folders:</b> "+bm.getMaxFolders()+"<br/>");
			out.println("<b>- Depth:</b> "+bm.getMaxDepth()+"<br/>");
			out.println("<b>- Calibration:</b> "+bm.runCalibration()+" ms<br/>");
			out.println("<b>- Calculated foldes:</b> "+bm.calculateFolders()+"<br/>");
			out.println("<b>- Calculated documents:</b> "+bm.calculateDocuments()+"<br/><br/>");
			results.print("\"Date\",");
			results.print("\"Time\",");
			results.print("\"Seconds\",");
			results.print("\"Folders\",");
			results.print("\"Documents\",");
			results.print("\"Size\"\n");
			results.flush();
			tBegin = System.currentTimeMillis();
			
			for (int i=0; i < maxIterations; i++) {
				out.println("<h2>Iteration "+i+"</h2>");
				out.println("<table class=\"results\" width=\"80%\">");
				out.println("<tr><th>Date</th><th>Partial seconds</th><th>Partial miliseconds</th><th>Total folders</th><th>Total documents</th><th>Total size</th></tr>");
				out.flush();
				
				Node rootNode = session.getRootNode().getNode(Repository.ROOT);
				Node baseNode = null;
				
				if (rootNode.hasNode(base)) {
					baseNode = rootNode.getNode(base);
				} else {
					baseNode = BaseFolderModule.create(session, rootNode, base);
				}
				
				PrintWriter pResults = new PrintWriter(Config.HOME_DIR + File.separator + base + "_" + i + ".csv");
				pResults.print("\"Date\",");
				pResults.print("\"Time\",");
				pResults.print("\"Seconds\",");
				pResults.print("\"Folders\",");
				pResults.print("\"Documents\",");
				pResults.print("\"Size\"\n");
				pResults.flush();
				
				pBegin = System.currentTimeMillis();
				bm.okmRawPopulate(session, baseNode, out, pResults);
				pEnd = System.currentTimeMillis();
				pResults.close();
				out.println("</table>");
				
				results.print("\"" + FormatUtil.formatDate(Calendar.getInstance()) + "\",");
				results.print("\"" + FormatUtil.formatSeconds(pEnd - pBegin) + "\",");
				results.print("\"" + (pEnd - pBegin) + "\",");
				results.print("\"" + bm.getTotalFolders() + "\",");
				results.print("\"" + bm.getTotalDocuments() + "\",");
				results.print("\"" + FormatUtil.formatSize(bm.getTotalSize()) + "\"\n");
				results.flush();
				
				out.println("<table class=\"results\" width=\"80%\">");
				out.println("<tr><th>Date</th><th>Partial seconds</th><th>Total folders</th><th>Total documents</th><th>Total size</th></tr>");
				out.println("<td>"+FormatUtil.formatDate(Calendar.getInstance())+"</td>");
				out.println("<td>"+FormatUtil.formatSeconds(pEnd - pBegin)+"</td>");
				out.println("<td>"+bm.getTotalFolders()+"</td>");
				out.println("<td>"+bm.getTotalDocuments()+"</td>");
				out.println("<td>"+FormatUtil.formatSize(bm.getTotalSize())+"</td>");
				out.println("</tr>");
				out.println("</table>");
				out.flush();
			}
			
			tEnd = System.currentTimeMillis();
		} catch (FileNotFoundException e) {
			out.println("<div class=\"warn\">FileNotFoundException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (javax.jcr.PathNotFoundException e) {
			out.println("<div class=\"warn\">PathNotFoundException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (javax.jcr.ItemExistsException e) {
			out.println("<div class=\"warn\">ItemExistsException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (javax.jcr.AccessDeniedException e) {
			out.println("<div class=\"warn\">AccessDeniedException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (javax.jcr.RepositoryException e) {
			out.println("<div class=\"warn\">RepositoryException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (InputMismatchException e) {
			out.println("<div class=\"warn\">InputMismatchException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (DatabaseException e) {
			out.println("<div class=\"warn\">DatabaseException: "+e.getMessage()+"</div>");
			out.flush();
		} finally {
			JCRUtils.logout(session);
		}
		
		long elapse = tEnd - tBegin;
		out.println("<hr/>");
		out.println("<b>Total size:</b> "+FormatUtil.formatSize(bm.getTotalSize())+"<br/>");
		out.println("<b>Total folders:</b> "+bm.getTotalFolders()+"<br/>");
		out.println("<b>Total documents:</b> "+bm.getTotalDocuments()+"<br/>");
		out.println("<b>Total time:</b> "+FormatUtil.formatSeconds(elapse)+"<br/>");
		out.println("<b>Documents per second:</b> "+bm.getTotalDocuments()/(elapse/1000)+"<br/>");
		out.print("</body>");
		out.print("</html>");
		out.flush();
		out.close();
		results.close();
		
		// Activity log
		UserActivity.log(request.getRemoteUser(), "ADMIN_BENCHMARK_OKM_RAW", null, 
				"Size: " + FormatUtil.formatSize(bm.getTotalSize()) +
				", Folders: " + bm.getTotalFolders() +
				", Documents: " + bm.getTotalDocuments() +
				", Time: " + FormatUtil.formatSeconds(elapse));
		log.debug("okmRawGenerate: void");
	}
	
	/**
	 * Generate documents into repository (Jackrabbit)
	 */
	private void jcrGenerate(HttpServletRequest request, HttpServletResponse response, 
			String base) throws IOException {
		log.debug("jcrGenerate({}, {}, {})", new Object[] { request, response, base });
		int maxDocuments = WebUtils.getInt(request, "param1");
		int maxFolder = WebUtils.getInt(request, "param2");
		int maxDepth = WebUtils.getInt(request, "param3");
		int maxIterations = WebUtils.getInt(request, "param4");
		PrintWriter out = response.getWriter();
		PrintWriter results = new PrintWriter(Config.HOME_DIR + File.separator + base + ".csv");
		long tBegin = 0, tEnd = 0, pBegin = 0, pEnd = 0;
		Benchmark bm = null;
		Session session = null;
		response.setContentType("text/html");
		header(out);
		out.println("<h1>Benchmark: Jackrabbit generate documents</h1>");
		out.flush();
		
		try {
			session = JCRUtils.getSession();
			bm = new Benchmark(maxDocuments, maxFolder, maxDepth);
			out.println("<b>- Documents:</b> "+bm.getMaxDocuments()+"<br/>");
			out.println("<b>- Folders:</b> "+bm.getMaxFolders()+"<br/>");
			out.println("<b>- Depth:</b> "+bm.getMaxDepth()+"<br/>");
			out.println("<b>- Calibration:</b> "+bm.runCalibration()+" ms<br/>");
			out.println("<b>- Calculated foldes:</b> "+bm.calculateFolders()+"<br/>");
			out.println("<b>- Calculated documents:</b> "+bm.calculateDocuments()+"<br/><br/>");
			results.print("\"Date\",");
			results.print("\"Time\",");
			results.print("\"Seconds\",");
			results.print("\"Folders\",");
			results.print("\"Documents\",");
			results.print("\"Size\"\n");
			results.flush();
			tBegin = System.currentTimeMillis();
			
			for (int i=0; i < maxIterations; i++) {
				out.println("<h2>Iteration "+i+"</h2>");
				out.println("<table class=\"results\" width=\"80%\">");
				out.println("<tr><th>Date</th><th>Partial seconds</th><th>Partial miliseconds</th><th>Total folders</th><th>Total documents</th><th>Total size</th></tr>");
				out.flush();
				
				Node rootNode = session.getRootNode().getNode(Repository.ROOT);
				Node baseNode = null;
				
				if (rootNode.hasNode(base)) {
					baseNode = rootNode.getNode(base);
				} else {
					baseNode = rootNode.addNode(base, JcrConstants.NT_FOLDER);
					rootNode.save();
				}
				
				PrintWriter pResults = new PrintWriter(Config.HOME_DIR + File.separator + base + "_" + i + ".csv");
				pResults.print("\"Date\",");
				pResults.print("\"Time\",");
				pResults.print("\"Seconds\",");
				pResults.print("\"Folders\",");
				pResults.print("\"Documents\",");
				pResults.print("\"Size\"\n");
				pResults.flush();
				
				pBegin = System.currentTimeMillis();
				bm.jcrPopulate(session, baseNode, out, pResults);
				pEnd = System.currentTimeMillis();
				pResults.close();
				out.println("</table>");
				
				results.print("\"" + FormatUtil.formatDate(Calendar.getInstance()) + "\",");
				results.print("\"" + FormatUtil.formatSeconds(pEnd - pBegin) + "\",");
				results.print("\"" + (pEnd - pBegin) + "\",");
				results.print("\"" + bm.getTotalFolders() + "\",");
				results.print("\"" + bm.getTotalDocuments() + "\",");
				results.print("\"" + FormatUtil.formatSize(bm.getTotalSize()) + "\"\n");
				results.flush();
				
				out.println("<table class=\"results\" width=\"80%\">");
				out.println("<tr><th>Date</th><th>Partial seconds</th><th>Total folders</th><th>Total documents</th><th>Total size</th></tr>");
				out.println("<td>"+FormatUtil.formatDate(Calendar.getInstance())+"</td>");
				out.println("<td>"+FormatUtil.formatSeconds(pEnd - pBegin)+"</td>");
				out.println("<td>"+bm.getTotalFolders()+"</td>");
				out.println("<td>"+bm.getTotalDocuments()+"</td>");
				out.println("<td>"+FormatUtil.formatSize(bm.getTotalSize())+"</td>");
				out.println("</tr>");
				out.println("</table>");
				out.flush();
			}
			
			tEnd = System.currentTimeMillis();
		} catch (FileNotFoundException e) {
			out.println("<div class=\"warn\">FileNotFoundException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (javax.jcr.PathNotFoundException e) {
			out.println("<div class=\"warn\">PathNotFoundException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (javax.jcr.ItemExistsException e) {
			out.println("<div class=\"warn\">ItemExistsException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (javax.jcr.AccessDeniedException e) {
			out.println("<div class=\"warn\">AccessDeniedException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (javax.jcr.RepositoryException e) {
			out.println("<div class=\"warn\">RepositoryException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (InputMismatchException e) {
			out.println("<div class=\"warn\">InputMismatchException: "+e.getMessage()+"</div>");
			out.flush();
		} catch (DatabaseException e) {
			out.println("<div class=\"warn\">DatabaseException: "+e.getMessage()+"</div>");
			out.flush();
		} finally {
			JCRUtils.logout(session);
		}
		
		long elapse = tEnd - tBegin;
		out.println("<hr/>");
		out.println("<b>Total size:</b> "+FormatUtil.formatSize(bm.getTotalSize())+"<br/>");
		out.println("<b>Total folders:</b> "+bm.getTotalFolders()+"<br/>");
		out.println("<b>Total documents:</b> "+bm.getTotalDocuments()+"<br/>");
		out.println("<b>Total time:</b> "+FormatUtil.formatSeconds(elapse)+"<br/>");
		out.println("<b>Documents per second:</b> "+bm.getTotalDocuments()/(elapse/1000)+"<br/>");
		out.print("</body>");
		out.print("</html>");
		out.flush();
		out.close();
		results.close();
		
		// Activity log
		UserActivity.log(request.getRemoteUser(), "ADMIN_BENCHMARK_JCR", null, 
				"Size: " + FormatUtil.formatSize(bm.getTotalSize()) +
				", Folders: " + bm.getTotalFolders() +
				", Documents: " + bm.getTotalDocuments() +
				", Time: " + FormatUtil.formatSeconds(elapse));
		log.debug("jcrGenerate: void");
	}

	private void header(PrintWriter out) {
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		out.println("<head>");
		out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
		out.println("<link rel=\"Shortcut icon\" href=\"favicon.ico\" />");
		out.println("<link rel=\"stylesheet\" href=\"css/style.css\" type=\"text/css\" />");
		out.println("<script src=\"js/biblioteca.js\" type=\"text/javascript\"></script>");
		out.println("<script type=\"text/javascript\">scrollToBottom();</script>");
		out.println("<title>Benchmark</title>");
		out.println("</head>");
		out.println("<body>");
	}
}
