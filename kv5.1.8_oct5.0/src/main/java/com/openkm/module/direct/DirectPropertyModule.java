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
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Encryption;
import com.openkm.cache.UserDocumentKeywordsManager;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.JcrSessionManager;
import com.openkm.core.LockException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.core.VersionException;
import com.openkm.jcr.JCRUtils;
import com.openkm.module.PropertyModule;
import com.openkm.module.base.BaseNotificationModule;
import com.openkm.module.base.BasePropertyModule;
import com.openkm.module.base.BaseScriptingModule;
import com.openkm.util.UserActivity;

public class DirectPropertyModule implements PropertyModule {
	private static Logger log = LoggerFactory.getLogger(DirectPropertyModule.class);

	@Override
	public void addCategory(String token, String nodePath, String catId) throws VersionException,
			LockException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("addCategory({}, {}, {})", new Object[] { token, nodePath, catId });
		Node documentNode = null;
		Session session = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}

		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			documentNode = session.getRootNode().getNode(nodePath.substring(1));
			BasePropertyModule.addCategory(session, documentNode, catId);
			
			// Check subscriptions
			BaseNotificationModule.checkSubscriptions(documentNode, session.getUserID(), "ADD_CATEGORY", null);

			// Check scripting
			BaseScriptingModule.checkScripts(session, documentNode, documentNode, "ADD_CATEGORY");

			// Activity log
			UserActivity.log(session.getUserID(), "ADD_CATEGORY", documentNode.getUUID(), catId+", "+nodePath);
		} catch (javax.jcr.PathNotFoundException e) {
			JCRUtils.discardsPendingChanges(documentNode);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.AccessDeniedException e) {
			JCRUtils.discardsPendingChanges(documentNode);
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.version.VersionException e) {
			JCRUtils.discardsPendingChanges(documentNode);
			throw new VersionException(e.getMessage(), e);
		} catch (javax.jcr.lock.LockException e) {
			JCRUtils.discardsPendingChanges(documentNode);
			throw new LockException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			JCRUtils.discardsPendingChanges(documentNode);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}

		log.debug("addCategory: void");
	}

	@Override
	public void removeCategory(String token, String nodePath, String catId) throws VersionException,
			LockException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("removeCategory({}, {}, {})", new Object[] { token, nodePath, catId });
		Node documentNode = null;
		Session session = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}

		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			documentNode = session.getRootNode().getNode(nodePath.substring(1));
			BasePropertyModule.removeCategory(session, documentNode, catId);
			
			// Check subscriptions
			BaseNotificationModule.checkSubscriptions(documentNode, session.getUserID(), "REMOVE_CATEGORY", null);

			// Check scripting
			BaseScriptingModule.checkScripts(session, documentNode, documentNode, "REMOVE_CATEGORY");

			// Activity log
			UserActivity.log(session.getUserID(), "REMOVE_CATEGORY", documentNode.getUUID(), catId+", "+nodePath);
		} catch (javax.jcr.PathNotFoundException e) {
			JCRUtils.discardsPendingChanges(documentNode);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.AccessDeniedException e) {
			JCRUtils.discardsPendingChanges(documentNode);
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.version.VersionException e) {
			JCRUtils.discardsPendingChanges(documentNode);
			throw new VersionException(e.getMessage(), e);
		} catch (javax.jcr.lock.LockException e) {
			JCRUtils.discardsPendingChanges(documentNode);
			throw new LockException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			JCRUtils.discardsPendingChanges(documentNode);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}

		log.debug("removeCategory: void");
	}

	@Override
	public String addKeyword(String token, String nodePath, String keyword) throws VersionException,
			LockException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("addKeyword({}, {}, {})", new Object[] { token, nodePath, keyword });
		Node documentNode = null;
		Session session = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}

		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			documentNode = session.getRootNode().getNode(nodePath.substring(1));
			keyword = BasePropertyModule.addKeyword(session, documentNode, keyword);
			
			// Update cache
			if (Config.USER_KEYWORDS_CACHE) {
				UserDocumentKeywordsManager.add(session.getUserID(), nodePath, keyword);
			}
			
			// Check subscriptions
			BaseNotificationModule.checkSubscriptions(documentNode, session.getUserID(), "ADD_KEYWORD", null);
			
			// Check scripting
			BaseScriptingModule.checkScripts(session, documentNode, documentNode, "ADD_KEYWORD");
			
			// Activity log
			UserActivity.log(session.getUserID(), "ADD_KEYWORD", documentNode.getUUID(), keyword+", "+nodePath);
		} catch (javax.jcr.PathNotFoundException e) {
			JCRUtils.discardsPendingChanges(documentNode);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.AccessDeniedException e) {
			JCRUtils.discardsPendingChanges(documentNode);
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.version.VersionException e) {
			JCRUtils.discardsPendingChanges(documentNode);
			throw new VersionException(e.getMessage(), e);
		} catch (javax.jcr.lock.LockException e) {
			JCRUtils.discardsPendingChanges(documentNode);
			throw new LockException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			JCRUtils.discardsPendingChanges(documentNode);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}

		log.debug("addKeyword: {}", keyword);
		return keyword;
	}

	@Override
	public void removeKeyword(String token, String nodePath, String keyword) throws VersionException,
			LockException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("removeKeyword({}, {}, {})", new Object[] { token, nodePath, keyword });
		Node documentNode = null;
		Session session = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}

		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			documentNode = session.getRootNode().getNode(nodePath.substring(1));
			BasePropertyModule.removeKeyword(session, documentNode, keyword);
			
			// Update cache
			if (Config.USER_KEYWORDS_CACHE) {
				UserDocumentKeywordsManager.remove(session.getUserID(), nodePath, keyword);
			}
			
			// Check subscriptions
			BaseNotificationModule.checkSubscriptions(documentNode, session.getUserID(), "REMOVE_KEYWORD", null);

			// Check scripting
			BaseScriptingModule.checkScripts(session, documentNode, documentNode, "REMOVE_KEYWORD");

			// Activity log
			UserActivity.log(session.getUserID(), "REMOVE_KEYWORD", documentNode.getUUID(), keyword+", "+nodePath);
		} catch (javax.jcr.PathNotFoundException e) {
			JCRUtils.discardsPendingChanges(documentNode);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.AccessDeniedException e) {
			JCRUtils.discardsPendingChanges(documentNode);
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.version.VersionException e) {
			JCRUtils.discardsPendingChanges(documentNode);
			throw new VersionException(e.getMessage(), e);
		} catch (javax.jcr.lock.LockException e) {
			JCRUtils.discardsPendingChanges(documentNode);
			throw new LockException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			JCRUtils.discardsPendingChanges(documentNode);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}

		log.debug("removeKeyword: void");
	}
	
	@Override
	public void setEncryption(String token, String nodePath, String cipherName) throws VersionException,
			LockException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("setEncryption({}, {}, {})", new Object[] { token, nodePath, cipherName });
		Node documentNode = null;
		Session session = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}

		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			documentNode = session.getRootNode().getNode(nodePath.substring(1));
			
			if (!documentNode.isNodeType(Encryption.TYPE) && cipherName != null) {
				documentNode.addMixin(Encryption.TYPE);
				documentNode.setProperty(Encryption.CIPHER_NAME, cipherName);
				documentNode.save();
			}
			
			// Check subscriptions
			BaseNotificationModule.checkSubscriptions(documentNode, session.getUserID(), "SET_ENCRYPTION", null);

			// Check scripting
			BaseScriptingModule.checkScripts(session, documentNode, documentNode, "SET_ENCRYPTION");

			// Activity log
			UserActivity.log(session.getUserID(), "SET_ENCRYPTION", documentNode.getUUID(), cipherName+", "+nodePath);
		} catch (javax.jcr.PathNotFoundException e) {
			JCRUtils.discardsPendingChanges(documentNode);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.AccessDeniedException e) {
			JCRUtils.discardsPendingChanges(documentNode);
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.version.VersionException e) {
			JCRUtils.discardsPendingChanges(documentNode);
			throw new VersionException(e.getMessage(), e);
		} catch (javax.jcr.lock.LockException e) {
			JCRUtils.discardsPendingChanges(documentNode);
			throw new LockException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			JCRUtils.discardsPendingChanges(documentNode);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}

		log.debug("setEncryption: void");
	}
	
	@Override
	public void unsetEncryption(String token, String nodePath) throws VersionException,
			LockException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("unsetEncryption({}, {})", new Object[] { token, nodePath });
		Node documentNode = null;
		Session session = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}

		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			documentNode = session.getRootNode().getNode(nodePath.substring(1));
			
			if (documentNode.isNodeType(Encryption.TYPE)) {
				documentNode.removeMixin(Encryption.TYPE);
				documentNode.save();
			}
			
			// Check subscriptions
			BaseNotificationModule.checkSubscriptions(documentNode, session.getUserID(), "UNSET_ENCRYPTION", null);

			// Check scripting
			BaseScriptingModule.checkScripts(session, documentNode, documentNode, "UNSET_ENCRYPTION");

			// Activity log
			UserActivity.log(session.getUserID(), "UNSET_ENCRYPTION", documentNode.getUUID(), nodePath);
		} catch (javax.jcr.PathNotFoundException e) {
			JCRUtils.discardsPendingChanges(documentNode);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.AccessDeniedException e) {
			JCRUtils.discardsPendingChanges(documentNode);
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.version.VersionException e) {
			JCRUtils.discardsPendingChanges(documentNode);
			throw new VersionException(e.getMessage(), e);
		} catch (javax.jcr.lock.LockException e) {
			JCRUtils.discardsPendingChanges(documentNode);
			throw new LockException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			JCRUtils.discardsPendingChanges(documentNode);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}

		log.debug("unsetEncryption: void");
	}
}
