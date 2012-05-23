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

package com.openkm.util.impexp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Document;
import com.openkm.bean.Folder;
import com.openkm.bean.Mail;
import com.openkm.bean.Version;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.JcrSessionManager;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.jcr.JCRUtils;
import com.openkm.module.DocumentModule;
import com.openkm.module.ModuleManager;

public class RepositoryChecker {
	private static Logger log = LoggerFactory.getLogger(RepositoryChecker.class);
	private RepositoryChecker() {}

	/**
	 * Performs a recursive repository document check
	 */
	public static ImpExpStats checkDocuments(String token, String fldPath, boolean versions, Writer out, 
			InfoDecorator deco) throws PathNotFoundException, AccessDeniedException, RepositoryException,
			IOException, DatabaseException {
		log.debug("checkDocuments({}, {}, {}, {}, {})", new Object[] { token, fldPath, versions, out, deco });
		Session session = null;
		ImpExpStats stats = new ImpExpStats();
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Node baseNode = session.getRootNode().getNode(fldPath.substring(1));
			stats = checkDocumentsHelper(token, baseNode, versions, out, deco);
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			stats.setOk(false);
			throw e;
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			stats.setOk(false);
			throw e;
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
			stats.setOk(false);
			throw e;
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			stats.setOk(false);
			throw e;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			stats.setOk(false);
			throw e;
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			stats.setOk(false);
			throw e;
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			stats.setOk(false);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}

		log.debug("checkDocuments: {}", stats);
		return stats;
	}

	/**
	 * Performs a recursive repository document check
	 */
	private static ImpExpStats checkDocumentsHelper(String token, Node baseNode,
			boolean versions, Writer out, InfoDecorator deco) throws FileNotFoundException,
			PathNotFoundException, AccessDeniedException, RepositoryException, IOException,
			DatabaseException, javax.jcr.PathNotFoundException, javax.jcr.RepositoryException {
		log.debug("checkDocumentsHelper({}, {}, {}, {}, {})", new Object[] { token, baseNode, versions, out, deco });
		ImpExpStats stats = new ImpExpStats();
		
		for (NodeIterator ni = baseNode.getNodes(); ni.hasNext(); ) {
			Node child = ni.nextNode();
			
			if (child.isNodeType(Document.TYPE)) {
				ImpExpStats tmp = readDocument(token, child.getPath(), versions, out, deco);
				stats.setDocuments(stats.getDocuments() + tmp.getDocuments());
				stats.setFolders(stats.getFolders() + tmp.getFolders());
				stats.setSize(stats.getSize() + tmp.getSize());
				stats.setOk(stats.isOk() && tmp.isOk());
			} else if (child.isNodeType(Folder.TYPE)) {
				ImpExpStats tmp = readFolder(token, child, versions, out, deco);
				stats.setDocuments(stats.getDocuments() + tmp.getDocuments());
				stats.setFolders(stats.getFolders() + tmp.getFolders());
				stats.setSize(stats.getSize() + tmp.getSize());
				stats.setOk(stats.isOk() && tmp.isOk());
			} else if (child.isNodeType(Mail.TYPE)) {
				ImpExpStats tmp = readFolder(token, child, versions, out, deco);
				stats.setDocuments(stats.getDocuments() + tmp.getDocuments());
				stats.setFolders(stats.getFolders() + tmp.getFolders());
				stats.setSize(stats.getSize() + tmp.getSize());
				stats.setOk(stats.isOk() && tmp.isOk());
			} else {
				log.error("Unknown node type: {} ({})", child.getPrimaryNodeType().getName(), child.getPath());
				stats.setOk(false);
				out.write(deco.print(child.getPath(), 0, "Unknown node type: " + child.getPrimaryNodeType().getName()));
				out.flush();
			}
		}
				
		log.debug("checkDocumentsHelper: {}", stats);
		return stats;
	}
	
	/**
	 * Read document contents.
	 */
	private static ImpExpStats readDocument(String token, String docPath, boolean versions,
			Writer out, InfoDecorator deco) throws PathNotFoundException, RepositoryException,
			DatabaseException, IOException {
		log.debug("readDocument({})", docPath);
		DocumentModule dm = ModuleManager.getDocumentModule();
		File fsPath = new File(Config.NULL_DEVICE);
		ImpExpStats stats = new ImpExpStats();
		Document doc = dm.getProperties(token, docPath);
		
		try {
			FileOutputStream fos = new FileOutputStream(fsPath);
			InputStream is = dm.getContent(token, docPath, false);
			IOUtils.copy(is, fos);
			is.close();
			
			if (versions) { // Check version history
				for (Version ver : dm.getVersionHistory(token, docPath)) {
					is = dm.getContentByVersion(token, docPath, ver.getName());
					IOUtils.copy(is, fos);
					is.close();
				}
			}
			
			fos.close();
			out.write(deco.print(docPath, doc.getActualVersion().getSize(), null));
			out.flush();
			
			// Stats
			stats.setSize(stats.getSize() + doc.getActualVersion().getSize());
			stats.setDocuments(stats.getDocuments() + 1);
		} catch (RepositoryException e) {
			log.error(e.getMessage());
			stats.setOk(false);
			out.write(deco.print(docPath, doc.getActualVersion().getSize(), e.getMessage()));
			out.flush();
		}
		
		return stats;
	}
	
	/**
	 * Read folder contents. 
	 */
	private static ImpExpStats readFolder(String token, Node baseNode, boolean versions,
			Writer out, InfoDecorator deco) throws FileNotFoundException, PathNotFoundException,
			AccessDeniedException, RepositoryException, IOException, DatabaseException,
			javax.jcr.PathNotFoundException, javax.jcr.RepositoryException {
		log.debug("readFolder({})", baseNode.getPath());
		ImpExpStats stats = checkDocumentsHelper(token, baseNode, versions, out, deco);
		
		// Stats
		stats.setFolders(stats.getFolders() + 1);
		
		return stats;
	}
}
