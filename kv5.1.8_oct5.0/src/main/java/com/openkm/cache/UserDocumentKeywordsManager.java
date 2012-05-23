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

package com.openkm.cache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.Workspace;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Property;
import com.openkm.core.DatabaseException;
import com.openkm.core.RepositoryException;
import com.openkm.dao.UserDocumentKeywordsDAO;
import com.openkm.dao.bean.cache.UserDocumentKeywords;

public class UserDocumentKeywordsManager {
	private static Logger log = LoggerFactory.getLogger(UserDocumentKeywordsManager.class);
	private static Map<String, Map<String, UserDocumentKeywords>> userDocumentKeywordsMgr = new HashMap<String, Map<String, UserDocumentKeywords>>();

	/**
	 * 
	 */
	public static Map<String, UserDocumentKeywords> get(String uid) {
		Map<String, UserDocumentKeywords> userDocKeywords = userDocumentKeywordsMgr.get(uid);
		
		if (userDocKeywords == null) {
			userDocKeywords = new HashMap<String, UserDocumentKeywords>();
		}
		
		return userDocKeywords;
	}
	
	/**
	 * Add keyword
	 */
	public static synchronized void add(String user, String nodePath, String keyword) {
		log.info("add({}, {}, {})", new Object[] {user, nodePath, keyword });
		Map<String, UserDocumentKeywords> usrDocs = get(user);
		UserDocumentKeywords udk = usrDocs.get(nodePath);
		
		if (udk == null) {
			udk = new UserDocumentKeywords();
			udk.setUser(user);
			udk.setDocument(nodePath);
			usrDocs.put(nodePath, udk);
			userDocumentKeywordsMgr.put(user, usrDocs);
		}
		
		udk.getKeywords().add(keyword);
	}
	
	/**
	 * Remove keyword
	 */
	public static synchronized void remove(String user, String nodePath, String keyword) {
		Map<String, UserDocumentKeywords> usrDocs = get(user);
		UserDocumentKeywords udk = usrDocs.get(nodePath);
		
		if (udk == null) {
			udk = new UserDocumentKeywords();
			udk.setUser(user);
			udk.setDocument(nodePath);
			usrDocs.put(nodePath, udk);
			userDocumentKeywordsMgr.put(user, usrDocs);
		}

		udk.getKeywords().remove(keyword);
		
		if (udk.getKeywords().isEmpty()) {
			usrDocs.remove(nodePath);
		}
	}
	
	/**
	 * TODO: Not fully implemented
	 * SEE: DirectSearchModule.getKeywordMapLive() 
	 */
	public static synchronized void refreshUserDocKeywords(Session session) throws RepositoryException {
		log.info("refreshUserDocKeywords({})", session);
		String statement = "/jcr:root/okm:root/element(*,okm:document)";
		
		try {
			Workspace workspace = session.getWorkspace();
			QueryManager queryManager = workspace.getQueryManager();
			Query query = queryManager.createQuery(statement, Query.XPATH);
			javax.jcr.query.QueryResult qResult = query.execute();
			Map<String, Set<String>> userDocKeywords = new HashMap<String, Set<String>>();
			
			for (NodeIterator nit = qResult.getNodes(); nit.hasNext(); ) {
				Node docNode = nit.nextNode();
				Value[] keywords = docNode.getProperty(Property.KEYWORDS).getValues();
				Set<String> keywordSet = new HashSet<String>();
				
				for (int i=0; i<keywords.length; i++) {
					keywordSet.add(keywords[i].getString());
				}
				
				userDocKeywords.put(docNode.getUUID(), keywordSet);
			}
			
			//userDocumentKeywordsMgr.put(session.getUserID(), userDocKeywords);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		}
		
		log.info("refreshUserDocKeywords: void");
	}
	
	/**
	 * 
	 */
	public static synchronized void serialize() throws DatabaseException {
		UserDocumentKeywordsDAO.clean();
		
		for (String user : userDocumentKeywordsMgr.keySet()) {
			log.info("User: {}", user);
			
			for (UserDocumentKeywords udk : userDocumentKeywordsMgr.get(user).values()) {
				log.info("Document: {}", udk);
				UserDocumentKeywordsDAO.create(udk);
			}
		}
	}
	
	/**
	 * 
	 */
	public static synchronized void deserialize() throws DatabaseException {
		for (String user : UserDocumentKeywordsDAO.findUsers()) {
			Map<String, UserDocumentKeywords> udkMap = new HashMap<String, UserDocumentKeywords>();
			
			for (UserDocumentKeywords udk: UserDocumentKeywordsDAO.findByUser(user)) {
				udkMap.put(udk.getDocument(), udk);
			}
			
			userDocumentKeywordsMgr.put(user, udkMap);
		}
	}
}
