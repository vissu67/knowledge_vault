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

package com.openkm.module.direct;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Document;
import com.openkm.bean.StatsInfo;
import com.openkm.core.DatabaseException;
import com.openkm.core.JcrSessionManager;
import com.openkm.core.RepositoryException;
import com.openkm.jcr.JCRUtils;
import com.openkm.module.StatsModule;

public class DirectStatsModule implements StatsModule {
	private static Logger log = LoggerFactory.getLogger(DirectStatsModule.class);

	private static String TAXONOMY_DOCUMENTS = "/jcr:root/okm:root//element(*,okm:document)";
	private static String TAXONOMY_FOLDERS = "/jcr:root/okm:root//element(*,okm:folder)";
	private static String TEMPLATES_DOCUMENTS = "/jcr:root/okm:templates//element(*,okm:document)";
	private static String TEMPLATES_FOLDERS = "/jcr:root/okm:templates//element(*,okm:folder)";
	private static String PERSONAL_DOCUMENTS = "/jcr:root/okm:personal//element(*,okm:document)";
	private static String PERSONAL_FOLDERS = "/jcr:root/okm:personal//element(*,okm:folder)";
	private static String TRASH_DOCUMENTS = "/jcr:root/okm:trash//element(*,okm:document)";
	private static String TRASH_FOLDERS = "/jcr:root/okm:trash//element(*,okm:folder)";
	
	@Override
	public StatsInfo getDocumentsByContext(String token) throws RepositoryException, DatabaseException {
		log.debug("getDocumentsByContext({})", token);
		StatsInfo si = new StatsInfo();
		double[] percents = new double[4];
		String[] sizes = new String[4];
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Workspace workspace = session.getWorkspace();
			QueryManager queryManager = workspace.getQueryManager();
			long taxonomyDocuments = getCount(queryManager, TAXONOMY_DOCUMENTS);
			long personalDocuments = getCount(queryManager, PERSONAL_DOCUMENTS);
			long templatesDocuments = getCount(queryManager, TEMPLATES_DOCUMENTS);
			long trashDocuments = getCount(queryManager, TRASH_DOCUMENTS); 
			long totalDocuments =  taxonomyDocuments + personalDocuments + templatesDocuments + trashDocuments; 
			
			// Fill sizes
			sizes[0] = Long.toString(taxonomyDocuments);
			sizes[1] = Long.toString(personalDocuments);
			sizes[2] = Long.toString(templatesDocuments);
			sizes[3] = Long.toString(trashDocuments);
			si.setSizes(sizes);
			
			// Compute percents
			percents[0] = (totalDocuments > 0)?((double) taxonomyDocuments / totalDocuments):0;
			percents[1] = (totalDocuments > 0)?((double) personalDocuments / totalDocuments):0;
			percents[2] = (totalDocuments > 0)?((double) templatesDocuments / totalDocuments):0;
			percents[3] = (totalDocuments > 0)?((double) trashDocuments / totalDocuments):0;
			si.setPercents(percents);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}

		log.debug("getDocumentsByContext: {}", si);
		return si;
	}
	
	@Override
	public StatsInfo getFoldersByContext(String token) throws RepositoryException, DatabaseException {
		log.debug("getFoldersByContext({})", token);
		StatsInfo si = new StatsInfo();
		double[] percents = new double[4];
		String[] sizes = new String[4];
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Workspace workspace = session.getWorkspace();
			QueryManager queryManager = workspace.getQueryManager();
			long taxonomyFolders = getCount(queryManager, TAXONOMY_FOLDERS);
			long personalFolders = getCount(queryManager, PERSONAL_FOLDERS);
			long templatesFolders = getCount(queryManager, TEMPLATES_FOLDERS);
			long trashFolders = getCount(queryManager, TRASH_FOLDERS); 
			long totalFolders =  taxonomyFolders + personalFolders + templatesFolders + trashFolders; 
			
			// Fill sizes
			sizes[0] = Long.toString(taxonomyFolders);
			sizes[1] = Long.toString(personalFolders);
			sizes[2] = Long.toString(templatesFolders);
			sizes[3] = Long.toString(trashFolders);
			si.setSizes(sizes);
			
			// Compute percents
			percents[0] = (totalFolders > 0)?((double) taxonomyFolders / totalFolders):0;
			percents[1] = (totalFolders > 0)?((double) personalFolders / totalFolders):0;
			percents[2] = (totalFolders > 0)?((double) templatesFolders / totalFolders):0;
			percents[3] = (totalFolders > 0)?((double) trashFolders / totalFolders):0;
			si.setPercents(percents);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}

		log.debug("getFoldersByContext: {}", si);
		return si;
	}

	/**
	 * Get result node count.
	 */
	private long getCount(QueryManager queryManager, String statement) throws InvalidQueryException,
			javax.jcr.RepositoryException {
		Query query = queryManager.createQuery(statement, Query.XPATH);
		QueryResult result = query.execute();
		return result.getRows().getSize();
	}
	
	@Override
	public StatsInfo getDocumentsSizeByContext(String token) throws RepositoryException, DatabaseException {
		log.debug("getDocumentsSizeByContext({})", token);
		StatsInfo si = new StatsInfo();
		double[] percents = new double[4];
		String[] sizes = new String[4];
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Workspace workspace = session.getWorkspace();
			QueryManager queryManager = workspace.getQueryManager();
			long taxonomyDocumentSize = getSize(queryManager, TAXONOMY_DOCUMENTS);
			long personalDocumentSize = getSize(queryManager, PERSONAL_DOCUMENTS);
			long templatesDocumentSize = getSize(queryManager, TEMPLATES_DOCUMENTS);
			long trashDocumentSize = getSize(queryManager, TRASH_DOCUMENTS); 
			long totalDocumentSize =  taxonomyDocumentSize + personalDocumentSize + templatesDocumentSize + trashDocumentSize; 
			
			// Fill sizes
			sizes[0] = Long.toString(taxonomyDocumentSize);
			sizes[1] = Long.toString(personalDocumentSize);
			sizes[2] = Long.toString(templatesDocumentSize);
			sizes[3] = Long.toString(trashDocumentSize);
			si.setSizes(sizes);
			
			// Compute percents
			percents[0] = (totalDocumentSize > 0)?((double) taxonomyDocumentSize / totalDocumentSize):0;
			percents[1] = (totalDocumentSize > 0)?((double) personalDocumentSize / totalDocumentSize):0;
			percents[2] = (totalDocumentSize > 0)?((double) templatesDocumentSize / totalDocumentSize):0;
			percents[3] = (totalDocumentSize > 0)?((double) trashDocumentSize / totalDocumentSize):0;
			si.setPercents(percents);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}

		log.debug("getDocumentsSizeByContext: {}", si);
		return si;
	}
	
	/**
	 * Get document node size.
	 */
	private long getSize(QueryManager queryManager, String statement) throws InvalidQueryException,
			javax.jcr.RepositoryException {
		Query query = queryManager.createQuery(statement, Query.XPATH);
		QueryResult result = query.execute();
		long size = 0;
		
		for (NodeIterator nit = result.getNodes(); nit.hasNext(); ) {
			Node docNode = nit.nextNode();
			Node docContentNode = docNode.getNode(Document.CONTENT);
			size += docContentNode.getProperty(Document.SIZE).getLong();
		}
		
		return size;
	}
}
