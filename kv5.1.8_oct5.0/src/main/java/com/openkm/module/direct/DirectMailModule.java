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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;

import org.apache.jackrabbit.api.XASession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Mail;
import com.openkm.bean.Repository;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.ItemExistsException;
import com.openkm.core.JcrSessionManager;
import com.openkm.core.LockException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.core.UserQuotaExceededException;
import com.openkm.core.VirusDetectedException;
import com.openkm.jcr.JCRUtils;
import com.openkm.module.MailModule;
import com.openkm.module.base.BaseMailModule;
import com.openkm.module.base.BaseNotificationModule;
import com.openkm.module.base.BaseScriptingModule;
import com.openkm.util.Transaction;
import com.openkm.util.UserActivity;

public class DirectMailModule implements MailModule {
	private static Logger log = LoggerFactory.getLogger(DirectMailModule.class);
	
	@Override
	public Mail create(String token, Mail mail) throws AccessDeniedException, RepositoryException,
			PathNotFoundException, ItemExistsException, VirusDetectedException, DatabaseException,
			UserQuotaExceededException {
		log.debug("create({}, {})", token, mail);
		return create(token, mail, null);
	}
	
	/**
	 * Used when importing mail from scheduler
	 */
	public Mail create(String token, Mail mail, String userId) throws AccessDeniedException, RepositoryException,
			PathNotFoundException, ItemExistsException, VirusDetectedException, DatabaseException,
			UserQuotaExceededException {
		log.debug("create({}, {}, {})", new Object[] { token, mail, userId });
		Mail newMail = null;
		Transaction t = null;
		XASession session = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}
		
		try {
			if (token == null) {
				session = (XASession) JCRUtils.getSession();
			} else {
				session = (XASession) JcrSessionManager.getInstance().get(token);
			}
			
			String parent = JCRUtils.getParent(mail.getPath());
			String name = JCRUtils.getName(mail.getPath());
			Node parentNode = session.getRootNode().getNode(parent.substring(1));
			
			// Escape dangerous chars in name
			name = JCRUtils.escape(name);
			mail.setPath(parent + "/" + name);
			
			t = new Transaction(session);
			t.start();
			
			// Create node
			Node mailNode = BaseMailModule.create(session, parentNode, name, mail.getSize(), 
					mail.getFrom(), mail.getReply(), mail.getTo(), mail.getCc(), mail.getBcc(),
					mail.getSentDate(), mail.getReceivedDate(), mail.getSubject(), mail.getContent(),
					mail.getMimeType());
						
			// Set returned mail properties
			newMail = BaseMailModule.getProperties(session, mailNode);
			
			t.end();
			t.commit();
			
			if (userId == null) {
				userId = session.getUserID();
			}
			
			// Check subscriptions
			BaseNotificationModule.checkSubscriptions(mailNode, userId, "CREATE_MAIL", null);
			
			// Check scripting
			BaseScriptingModule.checkScripts(session, parentNode, mailNode, "CREATE_MAIL");
			
			// Activity log
			UserActivity.log(userId, "CREATE_MAIL", mailNode.getUUID(), mail.getPath());
		} catch (javax.jcr.PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.ItemExistsException e) {
			log.warn(e.getMessage(), e);
			t.rollback();
			throw new ItemExistsException(e.getMessage(), e);
		} catch (javax.jcr.AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			t.rollback();
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			t.rollback();
			throw new RepositoryException(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			t.rollback();
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}

		log.debug("create: {}", newMail);
		return newMail;
	}

	@Override
	public Mail getProperties(String token, String mailPath) throws PathNotFoundException, 
			RepositoryException, DatabaseException {
		log.debug("getProperties({}, {})", token, mailPath);
		Mail mail = null;
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Node mailNode = session.getRootNode().getNode(mailPath.substring(1));
			mail = BaseMailModule.getProperties(session, mailNode);
			
			// Activity log
			UserActivity.log(session.getUserID(), "GET_MAIL_PROPERTIES", mailNode.getUUID(), mailPath);
		} catch (javax.jcr.PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}

		log.debug("get: {}", mail);
		return mail;
	}

	@Override
	public void delete(String token, String mailPath) throws AccessDeniedException, RepositoryException,
			PathNotFoundException, LockException, DatabaseException {
		log.debug("delete({}, {})", token, mailPath);
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
			
			String name = JCRUtils.getName(mailPath);
			Node mailNode = session.getRootNode().getNode(mailPath.substring(1));
			Node parentNode = mailNode.getParent();
			Node userTrash = session.getRootNode().getNode(Repository.TRASH+"/"+session.getUserID());
			
			// Test if already exists a mail with the same name in the trash
			String destPath = userTrash.getPath()+"/";
			String testName = name;
			
			for (int i=1; session.itemExists(destPath+testName); i++) {
				testName = name+" ("+i+")";
			}
			
			session.move(mailNode.getPath(), destPath+testName);
			session.getRootNode().save();
			
			// Check scripting
			BaseScriptingModule.checkScripts(session, parentNode, mailNode, "DELETE_MAIL");
			
			// Activity log
			UserActivity.log(session.getUserID(), "DELETE_MAIL", mailNode.getUUID(), mailPath);
		} catch (javax.jcr.PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(session);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(session);
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(session);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}
				
		log.debug("delete: void");
	}	
	
	@Override
	public void purge(String token, String mailPath) throws AccessDeniedException, RepositoryException,
			PathNotFoundException, DatabaseException {
		log.debug("purge({}, {})", token, mailPath);
		Node parentNode = null;
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
			
			Node mailNode = session.getRootNode().getNode(mailPath.substring(1));
			String mailUuid = mailNode.getUUID();
			parentNode = mailNode.getParent();
			mailNode.remove();
			parentNode.save();
						
			// Check scripting
			BaseScriptingModule.checkScripts(session, parentNode, mailNode, "PURGE_MAIL");

			// Activity log
			UserActivity.log(session.getUserID(), "PURGE_MAIL", mailUuid, mailPath);
		} catch (javax.jcr.PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(parentNode);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(parentNode);
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(parentNode);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}
		
		log.debug("purge: void");
	}

	@Override
	public Mail rename(String token, String mailPath, String newName) throws AccessDeniedException,
			RepositoryException, PathNotFoundException, ItemExistsException, DatabaseException {
		log.debug("rename({}, {}, {})", new Object[] { token, mailPath, newName });
		Mail renamedMail = null;
		Session session = null;
		Node mailNode = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			String parent = JCRUtils.getParent(mailPath);
			String name = JCRUtils.getName(mailPath);
							
			// Escape dangerous chars in name
			newName = JCRUtils.escape(newName);
			
			if (newName != null && !newName.equals("") && !newName.equals(name)) {
				String newPath = parent+"/"+newName;
				session.move(mailPath, newPath);
				
				// Set new name
				mailNode = session.getRootNode().getNode(newPath.substring(1));
				mailNode.setProperty(Mail.SUBJECT, newName);
			
				// Publish changes
				session.save();	
			
				// Set returned document properties
				renamedMail = BaseMailModule.getProperties(session, mailNode);
			} else {
				// Don't change anything
				mailNode = session.getRootNode().getNode(mailPath.substring(1));
				renamedMail = BaseMailModule.getProperties(session, mailNode);
			}
			
			// Activity log
			UserActivity.log(session.getUserID(), "RENAME_MAIL", mailNode.getUUID(), newName+", "+mailPath);
		} catch (javax.jcr.PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(session);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.ItemExistsException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(session);
			throw new ItemExistsException(e.getMessage(), e);
		} catch (javax.jcr.AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(session);
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(session);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}
		
		log.debug("rename: {}", renamedMail);
		return renamedMail;
	}
	
	@Override
	public void move(String token, String mailPath, String dstPath) throws AccessDeniedException,
			RepositoryException, PathNotFoundException, ItemExistsException, DatabaseException {
		log.debug("move({}, {}, {})", new Object[] { token, mailPath, dstPath });
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
			
			//Node mailNode = session.getRootNode().getNode(mailPath.substring(1));
			String name = JCRUtils.getName(mailPath);
			String dstNodePath = dstPath + "/" + name;
			session.move(mailPath, dstPath + "/" + name);
			session.save();
			Node dstMailNode = session.getRootNode().getNode(dstNodePath.substring(1));
			
			// Check scripting
			BaseScriptingModule.checkScripts(session, dstMailNode.getParent(), dstMailNode, "MOVE_MAIL");
			
			// Activity log
			UserActivity.log(session.getUserID(), "MOVE_MAIL", dstMailNode.getUUID(), dstPath+", "+mailPath);
		} catch (javax.jcr.PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(session);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.ItemExistsException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(session);
			throw new ItemExistsException(e.getMessage(), e);
		} catch (javax.jcr.AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(session);
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(session);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}
		
		log.debug("move: void");
	}

	@Override
	public void copy(String token, String mailPath, String dstPath) throws AccessDeniedException,
			RepositoryException, PathNotFoundException, ItemExistsException, IOException, DatabaseException,
			UserQuotaExceededException {
		log.debug("copy({}, {}, {})", new Object[] { token, mailPath, dstPath });
		Transaction t = null;
		XASession session = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}
		
		try {
			if (token == null) {
				session = (XASession) JCRUtils.getSession();
			} else {
				session = (XASession) JcrSessionManager.getInstance().get(token);
			}
			
			t = new Transaction(session);
			t.start();
			
			// Make some work
			Node srcMailNode = session.getRootNode().getNode(mailPath.substring(1)); 
			Node dstFolderNode = session.getRootNode().getNode(dstPath.substring(1));
			BaseMailModule.copy(session, srcMailNode, dstFolderNode);

			t.end();
			t.commit();
			
			// Check subscriptions
			BaseNotificationModule.checkSubscriptions(dstFolderNode, session.getUserID(), "COPY", null);
			
			// Activity log
			UserActivity.log(session.getUserID(), "COPY_MAIL", srcMailNode.getUUID(), dstPath+", "+mailPath);
		} catch (javax.jcr.PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			t.rollback();
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.ItemExistsException e) {
			log.warn(e.getMessage(), e);
			t.rollback();
			throw new ItemExistsException(e.getMessage(), e);
		} catch (javax.jcr.AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			t.rollback();
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			t.rollback();
			throw new RepositoryException(e.getMessage(), e);
		} catch (java.io.IOException e) {
			log.error(e.getMessage(), e);
			t.rollback();
			throw e;
		} finally {
			if (token == null) JCRUtils.logout(session);
		}
		
		log.debug("copy: void");
	}
	
	@Override
	public List<Mail> getChilds(String token, String fldPath) throws PathNotFoundException,
			RepositoryException, DatabaseException {
		log.debug("findChilds({}, {})", token, fldPath);
		List<Mail> childs = new ArrayList<Mail>();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Node folderNode = session.getRootNode().getNode(fldPath.substring(1));

			for (NodeIterator ni = folderNode.getNodes(); ni.hasNext(); ) {
				Node child = ni.nextNode();
				
				if (child.isNodeType(Mail.TYPE)) {
					childs.add(BaseMailModule.getProperties(session, child));
				}
			}

			// Activity log
			UserActivity.log(session.getUserID(), "GET_CHILD_MAILS", folderNode.getUUID(), fldPath);
		} catch (javax.jcr.PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}
				
		log.debug("findChilds: {}", childs);
		return childs;
	}

	@Override
	public boolean isValid(String token, String mailPath) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("isValid({}, {})", token, mailPath);
		boolean valid = false;
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Node node = session.getRootNode().getNode(mailPath.substring(1));
			
			if (node.isNodeType(Mail.TYPE)) {
				valid = true;
			}
		} catch (javax.jcr.PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}

		log.debug("isValid: {}", valid);
		return valid;
	}
	
	@Override
	public String getPath(String token, String uuid) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getPath({}, {})", token, uuid);
		String path = null;
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Node node = session.getNodeByUUID(uuid);

			if (node.isNodeType(Mail.TYPE)) {
				path = node.getPath();
			}
		} catch (javax.jcr.AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}

		log.debug("getPath: {}", path);
		return path;
	}
}
