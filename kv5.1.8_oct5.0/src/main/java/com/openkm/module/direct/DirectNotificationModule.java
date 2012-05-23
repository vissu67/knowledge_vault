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

import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Notification;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.JcrSessionManager;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.jcr.JCRUtils;
import com.openkm.module.NotificationModule;
import com.openkm.util.MailUtils;
import com.openkm.util.TemplateUtils;
import com.openkm.util.UserActivity;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class DirectNotificationModule implements NotificationModule {
	private static Logger log = LoggerFactory.getLogger(DirectNotificationModule.class);
	
	@Override
	public synchronized void subscribe(String token, String nodePath) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("subscribe({}, {})", token, nodePath);
		Node node = null;
		Node sNode = null;
		Session session = null;
		Session systemSession = null;
		String lt = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			systemSession = DirectRepositoryModule.getSystemSession();
			node = session.getRootNode().getNode(nodePath.substring(1));
			sNode = systemSession.getNodeByUUID(node.getUUID());
			lt = JCRUtils.getLockToken(node.getUUID());
			systemSession.addLockToken(lt);
			
			// Perform subscription
			if (node.isNodeType(Notification.TYPE)) {
				Value[] actualUsers = node.getProperty(Notification.SUBSCRIPTORS).getValues();
				String[] newUsers = new String[actualUsers.length+1];
				boolean alreadyAdded = false;

				for (int i=0; i<actualUsers.length; i++) {
					newUsers[i] = actualUsers[i].getString();
					
					// Don't add a user twice
					if (actualUsers[i].getString().equals(session.getUserID())) {
						alreadyAdded = true;
					}
				}
				
				if (!alreadyAdded) {
					newUsers[newUsers.length-1] = session.getUserID();
					sNode.setProperty(Notification.SUBSCRIPTORS, newUsers);
				}
			} else {
				sNode.addMixin(Notification.TYPE);
				sNode.setProperty(Notification.SUBSCRIPTORS, new String[] { session.getUserID() });
			}
			
			sNode.save();
			
			// Activity log
			UserActivity.log(session.getUserID(), "SUBSCRIBE_USER", node.getUUID(), nodePath);
		} catch (javax.jcr.AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(sNode);
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(sNode);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(sNode);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (lt != null) systemSession.removeLockToken(lt);
			if (token == null) JCRUtils.logout(session);
		}
		
		log.debug("subscribe: void");
	}
	
	@Override
	public synchronized void unsubscribe(String token, String nodePath) throws PathNotFoundException, 
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("unsubscribe({}, {})", token, nodePath);
		Node node = null;
		Node sNode = null;
		Session session = null;
		Session systemSession = null;
		String lt = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}

		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			systemSession = DirectRepositoryModule.getSystemSession();
			node = session.getRootNode().getNode(nodePath.substring(1));
			sNode = systemSession.getNodeByUUID(node.getUUID());
			lt = JCRUtils.getLockToken(node.getUUID());
			systemSession.addLockToken(lt);

			// Perform unsubscription
			if (node.isNodeType(Notification.TYPE)) {
				Value[] actualUsers = node.getProperty(Notification.SUBSCRIPTORS).getValues();
				ArrayList<String> newUsers = new ArrayList<String>();
				
				for (int i=0; i<actualUsers.length; i++) {
					if (!actualUsers[i].getString().equals(session.getUserID())) {
						newUsers.add(actualUsers[i].getString());
					}
				}
				
				if (newUsers.isEmpty()) {
					sNode.removeMixin(Notification.TYPE);
				} else {
					sNode.setProperty(Notification.SUBSCRIPTORS, (String[])newUsers.toArray(new String[newUsers.size()]));
				}			
			}

			sNode.save();

			// Activity log
			UserActivity.log(session.getUserID(), "UNSUBSCRIBE_USER", node.getUUID(), nodePath);
		} catch (javax.jcr.AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(sNode);
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(sNode);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(sNode);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (lt != null) systemSession.removeLockToken(lt);
			if (token == null) JCRUtils.logout(session);
		}

		log.debug("unsubscribe: void");
	}
	
	@Override
	public List<String> getSubscriptors(String token, String nodePath) throws PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("getSusbcriptions({}, {})", token, nodePath);
		List<String> users = new ArrayList<String>();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Node node = session.getRootNode().getNode(nodePath.substring(1));
			
			if (node.isNodeType(Notification.TYPE)) {
				Value[] notifyUsers = node.getProperty(Notification.SUBSCRIPTORS).getValues();
			
				for (int i=0; i<notifyUsers.length; i++) {
					users.add(notifyUsers[i].getString());
				}
			}
		} catch (javax.jcr.PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}

		log.debug("getSusbcriptions: {}", users);
		return users;
	}
	
	@Override
	public void notify(String token, String nodePath, List<String> users, String message, boolean attachment)
			throws PathNotFoundException, AccessDeniedException, RepositoryException {
		log.debug("notify({}, {}, {}, {})", new Object[] { token, nodePath, users, message });
		List<String> to = new ArrayList<String>();
		Session session = null;
		
		if (!users.isEmpty()) {
			try {
				log.debug("Nodo: {}, Message: {}", nodePath, message);
				
				if (token == null) {
					session = JCRUtils.getSession();
				} else {
					session = JcrSessionManager.getInstance().get(token);
				}
				
				for (String user : users) {
					String mail = new DirectAuthModule().getMail(token, user);
					
					if (mail != null) {
						to.add(mail);
					}
				}
				
				// Get session user email address
				String from = new DirectAuthModule().getMail(token, session.getUserID());
				
				if (!to.isEmpty() && from != null && !from.isEmpty()) {
					StringWriter swSubject = new StringWriter();
					StringWriter swBody = new StringWriter();
					Configuration cfg = TemplateUtils.getConfig();
					
					Map<String, String> model = new HashMap<String, String>();
					model.put("documentUrl", Config.APPLICATION_URL+"?docPath=" + URLEncoder.encode(nodePath, "UTF-8"));
					
					
					//vissu touch on oct'9th
					String kvPath = nodePath;
					kvPath = kvPath.replaceAll("okm:root","Knowledge Vault");
					//model.put("documentPath", nodePath);
					model.put("documentPath", kvPath);
					
					model.put("documentName", JCRUtils.getName(nodePath));
					model.put("userId", session.getUserID());
					model.put("notificationMessage", message);
					
					if (TemplateUtils.templateExists(Config.NOTIFICATION_MESSAGE_SUBJECT)) {
						Template tpl = cfg.getTemplate(Config.NOTIFICATION_MESSAGE_SUBJECT);
						tpl.process(model, swSubject);
					} else {
						StringReader sr = new StringReader(Config.NOTIFICATION_MESSAGE_SUBJECT);
						Template tpl = new Template("NotificationMessageSubject", sr, cfg);
						tpl.process(model, swSubject);
						sr.close();
					}
					
					if (TemplateUtils.templateExists(Config.NOTIFICATION_MESSAGE_BODY)) {
						Template tpl = cfg.getTemplate(Config.NOTIFICATION_MESSAGE_BODY);
						tpl.process(model, swBody);
					} else {
						StringReader sr = new StringReader(Config.NOTIFICATION_MESSAGE_BODY);
						Template tpl = new Template("NotificationMessageBody", sr, cfg);
						tpl.process(model, swBody);
						sr.close();
					}
					
					if (attachment) {
						MailUtils.sendDocument((String) from, to, swSubject.toString(), swBody.toString(), nodePath);
					} else {
						MailUtils.sendMessage((String) from, to, swSubject.toString(), swBody.toString());
					}
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (token == null) JCRUtils.logout(session);
			}
		}

		log.debug("notify: void");
	}
}
