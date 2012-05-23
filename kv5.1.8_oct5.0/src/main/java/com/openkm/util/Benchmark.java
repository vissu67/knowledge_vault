package com.openkm.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.InputMismatchException;

import javax.jcr.Node;
import javax.jcr.PropertyType;
import javax.jcr.Session;

import org.apache.jackrabbit.JcrConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Document;
import com.openkm.bean.Folder;
import com.openkm.bean.Permission;
import com.openkm.bean.Property;
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
import com.openkm.module.base.BaseDocumentModule;
import com.openkm.module.base.BaseFolderModule;
import com.openkm.module.direct.DirectDocumentModule;
import com.openkm.module.direct.DirectFolderModule;
import com.openkm.util.markov.Generator;

/**
 * Default values generate text files with about 39 pages.
 * 
 * @author pavila
 */
public class Benchmark {
	private static Logger log = LoggerFactory.getLogger(Benchmark.class);
	private static final String SEED = Config.HOME_DIR + File.separator + "benchmark.txt";
	private static final int PARAGRAPH = 250;
	private static final int LINE_WIDTH = 80;
	private static final int TOTAL_CHARS = 500;
	private static final int MAX_DOCUMENTS = 12;
	private static final int MAX_FOLDERS = 4;
	private static final int MAX_DEPTH = 3;
	private Generator gen = null;
	private int maxDocuments = 0;
	private int maxFolders = 0;
	private int maxDepth = 0;
	private int totalFolders = 0;
	private int totalDocuments = 0;
	private long totalSize = 0;
	private int row = 0;
	
	/**
	 * Main method for testing purposes
	 */
	public static void main(String[] args) {
	}
	
	public Benchmark() throws IOException {
		this.maxDocuments = MAX_DOCUMENTS;
		this.maxFolders = MAX_FOLDERS;
		this.maxDepth = MAX_DEPTH;
		FileInputStream fis = new FileInputStream(SEED);
		gen = new Generator(fis);
		fis.close();
	}
	
	public Benchmark(int maxDocuments, int maxFolders, int maxDepth) throws IOException {
		this.maxDocuments = maxDocuments;
		this.maxFolders = maxFolders;
		this.maxDepth = maxDepth;
		FileInputStream fis = new FileInputStream(SEED);
		gen = new Generator(fis);
		fis.close();
	}
	
	public Benchmark(int maxDocuments, int maxFolders, int maxDepth, InputStream is) throws IOException {
		this.maxDocuments = maxDocuments;
		this.maxFolders = maxFolders;
		this.maxDepth = maxDepth;
		gen = new Generator(is);
	}
	
	public int getMaxDocuments() {
		return maxDocuments;
	}
	
	public int getMaxFolders() {
		return maxFolders;
	}
	
	public int getMaxDepth() {
		return maxDepth;
	}
	
	public int getTotalFolders() {
		return totalFolders;
	}
	
	public int getTotalDocuments() {
		return totalDocuments;
	}
	
	public long getTotalSize() {
		return totalSize;
	}
	
	/**
	 * Calculates the number of folder created
	 */
	public int calculateFolders() {
		int nodesAtLevel = 1;
		int total = 0;
		
		for (int i=1; i<=maxDepth; i++) {
			nodesAtLevel = nodesAtLevel * maxFolders;
			total += nodesAtLevel;
		}
		
		return total;
	}
	
	/**
	 * Calculates the number of document created
	 */
	public int calculateDocuments() {
		int nodesAtLevel = 1;
		int total = 0;
		
		for (int i=1; i<=maxDepth; i++) {
			nodesAtLevel = nodesAtLevel * maxFolders;
			total += nodesAtLevel;
		}
		
		return total * maxDocuments;
	}
	
	/**
	 * Run system calibration
	 * @throws IOException 
	 * @throws InputMismatchException 
	 */
	public long runCalibration() throws InputMismatchException, IOException {
		final int ITERATIONS = 10;
		long total = 0;
		
		for (int i=0; i<ITERATIONS; i++) {
			long calBegin = System.currentTimeMillis();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			gen.generateText(PARAGRAPH, LINE_WIDTH, TOTAL_CHARS, baos);
			baos.close();
			long calEnd = System.currentTimeMillis();
			total = calEnd - calBegin;
		}
		
		log.debug("Calibration: {} ms", total / ITERATIONS);
		return total / ITERATIONS;
	}
	
	/**
	 * Run OpenKM text document insertions (API)
	 */
	public void okmApiHighPopulate(String token, Folder root, PrintWriter out, PrintWriter res) throws IOException,
			InputMismatchException, ItemExistsException, PathNotFoundException, UserQuotaExceededException, 
			AccessDeniedException, UnsupportedMimeTypeException, FileSizeExceededException,
			VirusDetectedException, RepositoryException, DatabaseException, ExtensionException {
		long begin = System.currentTimeMillis();
		okmApiHighPopulateHelper(token, root, out, res, gen, 0);
		long end = System.currentTimeMillis();
		String elapse = FormatUtil.formatSeconds(end - begin);
		log.debug("Total Time: {} - Folders: {}, Documents: {}", new Object[] { elapse, totalFolders, totalDocuments });
	}
	
	/**
	 * Helper
	 */
	private void okmApiHighPopulateHelper(String token, Folder root, PrintWriter out, PrintWriter res,
			Generator gen, int depth) throws 
			InputMismatchException, IOException, ItemExistsException, PathNotFoundException,
			UserQuotaExceededException,	AccessDeniedException, UnsupportedMimeTypeException, 
			FileSizeExceededException, VirusDetectedException, RepositoryException, DatabaseException,
			ExtensionException {
		log.debug("okmApiHighPopulateHelper({}, {}, {}, {})", new Object[] { token, root, gen, depth });
		
		if (depth < maxDepth) {
			for (int i=0; i<maxFolders; i++) {
				long begin = System.currentTimeMillis();
				Folder fld = new Folder();
				fld.setPath(root.getPath() + "/" + System.currentTimeMillis());
				fld = new DirectFolderModule().create(token, fld);
				totalFolders++;
				log.debug("At depth {}, created folder {}", depth, fld.getPath());
				
				for (int j=0; j<maxDocuments; j++) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					gen.generateText(PARAGRAPH, LINE_WIDTH, TOTAL_CHARS, baos);
					baos.close();
					totalSize += baos.size();
					
					// Repository insertion
					ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
					Document doc = new Document();
					doc.setMimeType("text/plain");
					doc.setPath(fld.getPath() + "/" + System.currentTimeMillis() + ".txt");
					new DirectDocumentModule().create(token, doc, bais);
					totalDocuments++;
				}
				
				long end = System.currentTimeMillis();
				String elapse = FormatUtil.formatSeconds(end - begin);
				log.debug("Partial Time: {} - Folders: {}, Documents: {}", new Object[] { elapse, totalFolders, totalDocuments });
				out.print("<tr class=\""+(row++%2==0?"even":"odd")+"\">");
				out.print("<td>"+FormatUtil.formatDate(Calendar.getInstance())+"</td>");
				out.print("<td>"+elapse+"</td>");
				out.print("<td>"+(end - begin)+"</td>");
				out.print("<td>"+totalFolders+"</td>");
				out.print("<td>"+totalDocuments+"</td>");
				out.print("<td>"+FormatUtil.formatSize(totalSize)+"</td>");
				out.println("</tr>");
				out.flush();
				
				res.print("\"" + FormatUtil.formatDate(Calendar.getInstance()) + "\",");
				res.print("\"" + elapse + "\",");
				res.print("\"" + (end - begin) + "\",");
				res.print("\"" + totalFolders + "\",");
				res.print("\"" + totalDocuments + "\",");
				res.print("\"" + FormatUtil.formatSize(totalSize) + "\"\n");
				res.flush();
				
				// Go depth
				okmApiHighPopulateHelper(token, fld, out, res, gen, depth+1);
			}
		} else {
			log.debug("Max depth reached: {}", depth);
		}
	}
	
	/**
	 * Run OpenKM text document insertions (API)
	 */
	public void okmApiLowPopulate(Session session, Node root, PrintWriter out, PrintWriter res) throws
			javax.jcr.ItemExistsException, javax.jcr.PathNotFoundException, 
			javax.jcr.nodetype.NoSuchNodeTypeException, javax.jcr.lock.LockException,
			javax.jcr.version.VersionException, javax.jcr.nodetype.ConstraintViolationException, 
			javax.jcr.RepositoryException, InputMismatchException, IOException, DatabaseException,
			UserQuotaExceededException {
		long begin = System.currentTimeMillis();
		okmApiLowPopulateHelper(session, root, out, res, gen, 0);
		long end = System.currentTimeMillis();
		String elapse = FormatUtil.formatSeconds(end - begin);
		log.debug("Total Time: {} - Folders: {}, Documents: {}", new Object[] { elapse, totalFolders, totalDocuments });
	}
	
	/**
	 * Helper
	 */
	private void okmApiLowPopulateHelper(Session session, Node root, PrintWriter out, PrintWriter res,
			Generator gen, int depth) throws 
			javax.jcr.ItemExistsException, javax.jcr.PathNotFoundException, 
			javax.jcr.nodetype.NoSuchNodeTypeException, javax.jcr.lock.LockException,
			javax.jcr.version.VersionException, javax.jcr.nodetype.ConstraintViolationException, 
			javax.jcr.RepositoryException, InputMismatchException, IOException, DatabaseException, 
			UserQuotaExceededException {
		log.debug("okmApiLowPopulateHelper({}, {}, {}, {})", new Object[] { session, root, gen, depth });
		
		if (depth < maxDepth) {
			for (int i=0; i<maxFolders; i++) {
				long begin = System.currentTimeMillis();
				Node fld = BaseFolderModule.create(session, root, Long.toString(System.currentTimeMillis()));
				totalFolders++;
				log.debug("At depth {}, created folder {}", depth, fld.getPath());
				
				for (int j=0; j<maxDocuments; j++) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					gen.generateText(PARAGRAPH, LINE_WIDTH, TOTAL_CHARS, baos);
					baos.close();
					totalSize += baos.size();
					
					// Repository insertion
					ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
					BaseDocumentModule.create(session, fld, System.currentTimeMillis() + ".txt", 
							null, "text/plain", new String[]{}, bais);
					totalDocuments++;
				}
				
				long end = System.currentTimeMillis();
				String elapse = FormatUtil.formatSeconds(end - begin);
				log.debug("Partial Time: {} - Folders: {}, Documents: {}", new Object[] { elapse, totalFolders, totalDocuments });
				out.print("<tr class=\""+(row++%2==0?"even":"odd")+"\">");
				out.print("<td>"+FormatUtil.formatDate(Calendar.getInstance())+"</td>");
				out.print("<td>"+elapse+"</td>");
				out.print("<td>"+(end - begin)+"</td>");
				out.print("<td>"+totalFolders+"</td>");
				out.print("<td>"+totalDocuments+"</td>");
				out.print("<td>"+FormatUtil.formatSize(totalSize)+"</td>");
				out.println("</tr>");
				out.flush();
				
				res.print("\"" + FormatUtil.formatDate(Calendar.getInstance()) + "\",");
				res.print("\"" + elapse + "\",");
				res.print("\"" + (end - begin) + "\",");
				res.print("\"" + totalFolders + "\",");
				res.print("\"" + totalDocuments + "\",");
				res.print("\"" + FormatUtil.formatSize(totalSize) + "\"\n");
				res.flush();
				
				// Go depth
				okmApiLowPopulateHelper(session, fld, out, res, gen, depth+1);
			}
		} else {
			log.debug("Max depth reached: {}", depth);
		}
	}
	
	/**
	 * Run OpenKM text document insertions (RAW)
	 */
	public void okmRawPopulate(Session session, Node root, PrintWriter out, PrintWriter res) throws
			javax.jcr.ItemExistsException, javax.jcr.PathNotFoundException, 
			javax.jcr.nodetype.NoSuchNodeTypeException, javax.jcr.lock.LockException,
			javax.jcr.version.VersionException, javax.jcr.nodetype.ConstraintViolationException, 
			javax.jcr.RepositoryException, InputMismatchException, IOException {
		long begin = System.currentTimeMillis();
		okmRawPopulateHelper(session, root, out, res, gen, 0);
		long end = System.currentTimeMillis();
		String elapse = FormatUtil.formatSeconds(end - begin);
		log.debug("Total Time: {} - Folders: {}, Documents: {}", new Object[] { elapse, totalFolders, totalDocuments });
	}
	
	/**
	 * Helper
	 */
	private void okmRawPopulateHelper(Session session, Node root, PrintWriter out, PrintWriter res,
			Generator gen, int depth)
			throws javax.jcr.ItemExistsException, javax.jcr.PathNotFoundException, 
			javax.jcr.nodetype.NoSuchNodeTypeException, javax.jcr.lock.LockException,
			javax.jcr.version.VersionException, javax.jcr.nodetype.ConstraintViolationException, 
			javax.jcr.RepositoryException, InputMismatchException, IOException {
		log.debug("okmRawPopulateHelper({}, {}, {}, {})", new Object[] { session, root, gen, depth });
		
		if (depth < maxDepth) {
			for (int i=0; i<maxFolders; i++) {
				long begin = System.currentTimeMillis();
				String fldName = Long.toString(System.currentTimeMillis());
				Node fldNode = root.addNode(fldName, Folder.TYPE);
				fldNode.setProperty(Folder.AUTHOR, session.getUserID());
				fldNode.setProperty(Folder.NAME, fldName);
				
				// Set auth info
				fldNode.setProperty(Permission.USERS_READ, new String[] { session.getUserID() });
				fldNode.setProperty(Permission.USERS_WRITE, new String[] { session.getUserID() });
				fldNode.setProperty(Permission.USERS_DELETE, new String[] { session.getUserID() });
				fldNode.setProperty(Permission.USERS_SECURITY, new String[] { session.getUserID() });
				fldNode.setProperty(Permission.ROLES_READ, new String[] {});
				fldNode.setProperty(Permission.ROLES_WRITE, new String[] {});
				fldNode.setProperty(Permission.ROLES_DELETE, new String[] {});
				fldNode.setProperty(Permission.ROLES_SECURITY, new String[] {});
				
				root.save();
				totalFolders++;
				log.debug("At depth {}, created folder {}", depth, fldNode.getPath());
				
				for (int j=0; j<maxDocuments; j++) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					gen.generateText(PARAGRAPH, LINE_WIDTH, TOTAL_CHARS, baos);
					baos.close();
					totalSize += baos.size();
					
					// Repository insertion
					ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
					String docName = System.currentTimeMillis() + ".txt";
					Node docNode = fldNode.addNode(docName, Document.TYPE);
					docNode.setProperty(Property.KEYWORDS, new String[] {});
					docNode.setProperty(Property.CATEGORIES, new String[]{}, PropertyType.REFERENCE);
					docNode.setProperty(Document.AUTHOR, session.getUserID());
					docNode.setProperty(Document.NAME, docName);
					
					// Set auth info
					docNode.setProperty(Permission.USERS_READ, new String[] { session.getUserID() });
					docNode.setProperty(Permission.USERS_WRITE, new String[] { session.getUserID() });
					docNode.setProperty(Permission.USERS_DELETE, new String[] { session.getUserID() });
					docNode.setProperty(Permission.USERS_SECURITY, new String[] { session.getUserID() });
					docNode.setProperty(Permission.ROLES_READ, new String[] { session.getUserID() });
					docNode.setProperty(Permission.ROLES_WRITE, new String[] { session.getUserID() });
					docNode.setProperty(Permission.ROLES_DELETE, new String[] { session.getUserID() });
					docNode.setProperty(Permission.ROLES_SECURITY, new String[] { session.getUserID() });
					
					Node contNode = docNode.addNode(Document.CONTENT, Document.CONTENT_TYPE);
					contNode.setProperty(Document.SIZE, bais.available());
					contNode.setProperty(Document.AUTHOR, session.getUserID());
					contNode.setProperty(Document.VERSION_COMMENT, "");
					contNode.setProperty(JcrConstants.JCR_MIMETYPE, "text/plain");
					contNode.setProperty(JcrConstants.JCR_ENCODING, "UTF-8");
					contNode.setProperty(JcrConstants.JCR_DATA, bais);
					contNode.setProperty(JcrConstants.JCR_LASTMODIFIED, Calendar.getInstance());
					fldNode.save();
					
					contNode.checkin();
					totalDocuments++;
				}
				
				long end = System.currentTimeMillis();
				String elapse = FormatUtil.formatSeconds(end - begin);
				log.debug("Partial Time: {} - Folders: {}, Documents: {}", new Object[] { elapse, totalFolders, totalDocuments });
				out.print("<tr class=\""+(row++%2==0?"even":"odd")+"\">");
				out.print("<td>"+FormatUtil.formatDate(Calendar.getInstance())+"</td>");
				out.print("<td>"+elapse+"</td>");
				out.print("<td>"+(end - begin)+"</td>");
				out.print("<td>"+totalFolders+"</td>");
				out.print("<td>"+totalDocuments+"</td>");
				out.print("<td>"+FormatUtil.formatSize(totalSize)+"</td>");
				out.println("</tr>");
				out.flush();
				
				res.print("\"" + FormatUtil.formatDate(Calendar.getInstance()) + "\",");
				res.print("\"" + elapse + "\",");
				res.print("\"" + (end - begin) + "\",");
				res.print("\"" + totalFolders + "\",");
				res.print("\"" + totalDocuments + "\",");
				res.print("\"" + FormatUtil.formatSize(totalSize) + "\"\n");
				res.flush();
				
				// Go depth
				okmRawPopulateHelper(session, fldNode, out, res, gen, depth+1);
			}
		} else {
			log.debug("Max depth reached: {}", depth);
		}
	}
	
	/**
	 * Run JCR text document insertions
	 */
	public void jcrPopulate(Session session, Node root, PrintWriter out, PrintWriter res) throws IOException,
			javax.jcr.ItemExistsException, javax.jcr.PathNotFoundException, 
			javax.jcr.nodetype.NoSuchNodeTypeException, javax.jcr.lock.LockException,
			javax.jcr.version.VersionException, javax.jcr.nodetype.ConstraintViolationException, 
			javax.jcr.RepositoryException, InputMismatchException, IOException {
		long begin = System.currentTimeMillis();
		jcrPopulateHelper(session, root, out, res, gen, 0);
		long end = System.currentTimeMillis();
		String elapse = FormatUtil.formatSeconds(end - begin);
		log.debug("Total Time: {} - Folders: {}, Documents: {}", new Object[] { elapse, totalFolders, totalDocuments });
	}
	
	/**
	 * Helper
	 */
	private void jcrPopulateHelper(Session session, Node root, PrintWriter out, PrintWriter res, 
			Generator gen, int depth) throws 
			javax.jcr.ItemExistsException, javax.jcr.PathNotFoundException, 
			javax.jcr.nodetype.NoSuchNodeTypeException, javax.jcr.lock.LockException,
			javax.jcr.version.VersionException, javax.jcr.nodetype.ConstraintViolationException, 
			javax.jcr.RepositoryException, InputMismatchException, IOException {
		log.debug("jcrPopulateHelper({}, {}, {}, {})", new Object[] { session, root, gen, depth });
		
		if (depth < maxDepth) {
			for (int i=0; i<maxFolders; i++) {
				long begin = System.currentTimeMillis();
				Node fldNode = root.addNode(Long.toString(System.currentTimeMillis()), JcrConstants.NT_FOLDER);
				fldNode.addMixin(JcrConstants.MIX_REFERENCEABLE);
				root.save();
				totalFolders++;
				log.debug("At depth {}, created folder {}", depth, fldNode.getPath());
				
				for (int j=0; j<maxDocuments; j++) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					gen.generateText(PARAGRAPH, LINE_WIDTH, TOTAL_CHARS, baos);
					baos.close();
					totalSize += baos.size();
					
					// Repository insertion
					ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
					Node docNode = fldNode.addNode(System.currentTimeMillis() + ".txt", JcrConstants.NT_FILE);
					docNode.addMixin(JcrConstants.MIX_REFERENCEABLE);
					Node resNode = docNode.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE);
					resNode.setProperty(JcrConstants.JCR_MIMETYPE, "text/plain");
					resNode.setProperty(JcrConstants.JCR_ENCODING, "UTF-8");
					resNode.setProperty(JcrConstants.JCR_DATA, bais);
					resNode.setProperty(JcrConstants.JCR_LASTMODIFIED, Calendar.getInstance());
					fldNode.save();
					totalDocuments++;
				}
				
				long end = System.currentTimeMillis();
				String elapse = FormatUtil.formatSeconds(end - begin);
				log.debug("Partial Time: {} - Folders: {}, Documents: {}", new Object[] { elapse, totalFolders, totalDocuments });
				out.print("<tr class=\""+(row++%2==0?"even":"odd")+"\">");
				out.print("<td>"+FormatUtil.formatDate(Calendar.getInstance())+"</td>");
				out.print("<td>"+elapse+"</td>");
				out.print("<td>"+(end - begin)+"</td>");
				out.print("<td>"+totalFolders+"</td>");
				out.print("<td>"+totalDocuments+"</td>");
				out.print("<td>"+FormatUtil.formatSize(totalSize)+"</td>");
				out.println("</tr>");
				out.flush();
				
				res.print("\"" + FormatUtil.formatDate(Calendar.getInstance()) + "\",");
				res.print("\"" + elapse + "\",");
				res.print("\"" + (end - begin) + "\",");
				res.print("\"" + totalFolders + "\",");
				res.print("\"" + totalDocuments + "\",");
				res.print("\"" + FormatUtil.formatSize(totalSize) + "\"\n");
				res.flush();
				
				// Go depth
				jcrPopulateHelper(session, fldNode, out, res, gen, depth+1);
			}
		} else {
			log.debug("Max depth reached: {}", depth);
		}
	}
}
