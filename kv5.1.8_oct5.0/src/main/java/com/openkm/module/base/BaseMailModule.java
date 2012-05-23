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

package com.openkm.module.base;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.apache.jackrabbit.core.NodeImpl;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.core.security.AccessManager;
import org.apache.jackrabbit.spi.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Document;
import com.openkm.bean.Mail;
import com.openkm.bean.Permission;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.UserQuotaExceededException;
import com.openkm.jcr.JCRUtils;

public class BaseMailModule {
	private static Logger log = LoggerFactory.getLogger(BaseMailModule.class);
	
	/**
	 * Create a new mail
	 */
	public static Node create(Session session, Node parentNode, String name, long size, String from, 
			String[] reply, String[] to, String[] cc, String[] bcc, Calendar sentDate, 
			Calendar receivedDate, String subject, String content, String mimeType) throws
			javax.jcr.ItemExistsException, javax.jcr.PathNotFoundException, NoSuchNodeTypeException, 
			javax.jcr.lock.LockException, VersionException, ConstraintViolationException,
			javax.jcr.RepositoryException, IOException, DatabaseException, UserQuotaExceededException {
		// Create and add a new mail node
		Node mailNode = parentNode.addNode(name, Mail.TYPE);
		mailNode.setProperty(Mail.SIZE, size);
		mailNode.setProperty(Mail.FROM, from);
		mailNode.setProperty(Mail.REPLY, reply);
		mailNode.setProperty(Mail.TO, to);
		mailNode.setProperty(Mail.CC, cc);
		mailNode.setProperty(Mail.BCC, bcc);
		mailNode.setProperty(Mail.SENT_DATE, sentDate);
		mailNode.setProperty(Mail.RECEIVED_DATE, receivedDate);
		mailNode.setProperty(Mail.SUBJECT, subject);
		mailNode.setProperty(Mail.CONTENT, content);
		mailNode.setProperty(Mail.MIME_TYPE, mimeType);
		
		// Get parent node auth info
		Value[] usersReadParent = parentNode.getProperty(Permission.USERS_READ).getValues();
		String[] usersRead = JCRUtils.usrValue2String(usersReadParent, session.getUserID()); 
		Value[] usersWriteParent = parentNode.getProperty(Permission.USERS_WRITE).getValues();
		String[] usersWrite = JCRUtils.usrValue2String(usersWriteParent, session.getUserID()); 
		Value[] usersDeleteParent = parentNode.getProperty(Permission.USERS_DELETE).getValues();
		String[] usersDelete = JCRUtils.usrValue2String(usersDeleteParent, session.getUserID());
		Value[] usersSecurityParent = parentNode.getProperty(Permission.USERS_SECURITY).getValues();
		String[] usersSecurity = JCRUtils.usrValue2String(usersSecurityParent, session.getUserID());
		
		Value[] rolesReadParent = parentNode.getProperty(Permission.ROLES_READ).getValues();
		String[] rolesRead = JCRUtils.rolValue2String(rolesReadParent); 
		Value[] rolesWriteParent = parentNode.getProperty(Permission.ROLES_WRITE).getValues();
		String[] rolesWrite = JCRUtils.rolValue2String(rolesWriteParent); 
		Value[] rolesDeleteParent = parentNode.getProperty(Permission.ROLES_DELETE).getValues();
		String[] rolesDelete = JCRUtils.rolValue2String(rolesDeleteParent);
		Value[] rolesSecurityParent = parentNode.getProperty(Permission.ROLES_SECURITY).getValues();
		String[] rolesSecurity = JCRUtils.rolValue2String(rolesSecurityParent);
		
		// Set auth info
		mailNode.setProperty(Permission.USERS_READ, usersRead);
		mailNode.setProperty(Permission.USERS_WRITE, usersWrite);
		mailNode.setProperty(Permission.USERS_DELETE, usersDelete);
		mailNode.setProperty(Permission.USERS_SECURITY, usersSecurity);
		mailNode.setProperty(Permission.ROLES_READ, rolesRead);
		mailNode.setProperty(Permission.ROLES_WRITE, rolesWrite);
		mailNode.setProperty(Permission.ROLES_DELETE, rolesDelete);
		mailNode.setProperty(Permission.ROLES_SECURITY, rolesSecurity);
		
		parentNode.save();
		
		return mailNode;
	}
	
	/**
	 * Get mail properties
	 */
	public static Mail getProperties(Session session, Node mailNode) throws 
			javax.jcr.PathNotFoundException, javax.jcr.RepositoryException  {
		log.debug("getProperties({}, {})", session, mailNode);
		Mail mail = new Mail();
		
		// Properties
		Value[] replyValues = mailNode.getProperty(Mail.REPLY).getValues();
		String[] reply = JCRUtils.value2String(replyValues);
		Value[] toValues = mailNode.getProperty(Mail.TO).getValues();
		String[] to = JCRUtils.value2String(toValues);
		Value[] ccValues = mailNode.getProperty(Mail.CC).getValues();
		String[] cc = JCRUtils.value2String(ccValues);
		Value[] bccValues = mailNode.getProperty(Mail.BCC).getValues();
		String[] bcc = JCRUtils.value2String(bccValues);
		
		mail.setPath(mailNode.getPath());
		mail.setUuid(mailNode.getUUID());
		mail.setReply(reply);
		mail.setTo(to);
		mail.setCc(cc);
		mail.setBcc(bcc);
		mail.setFrom(mailNode.getProperty(Mail.FROM).getString());
		mail.setSize(mailNode.getProperty(Mail.SIZE).getLong());
		mail.setSentDate(mailNode.getProperty(Mail.SENT_DATE).getDate());
		mail.setReceivedDate(mailNode.getProperty(Mail.RECEIVED_DATE).getDate());
		mail.setSubject(mailNode.getProperty(Mail.SUBJECT).getString());
		mail.setContent(mailNode.getProperty(Mail.CONTENT).getString());
		mail.setMimeType(mailNode.getProperty(Mail.MIME_TYPE).getString());
		
		// Get attachments
		ArrayList<Document> attachments = new ArrayList<Document>();
		
		for (NodeIterator nit = mailNode.getNodes(); nit.hasNext(); ) {
			Node node = nit.nextNode();

			if (node.isNodeType(Document.TYPE)) {
				Document attachment = BaseDocumentModule.getProperties(session, node);
				attachments.add(attachment);
			}
		}
		
		mail.setAttachments(attachments);
		
		// Get permissions
		if (Config.SYSTEM_READONLY) {
			mail.setPermissions(Permission.NONE);
		} else {
			AccessManager am = ((SessionImpl) session).getAccessManager();
			Path path = ((NodeImpl)mailNode).getPrimaryPath();
			//Path path = ((SessionImpl)session).getHierarchyManager().getPath(((NodeImpl)folderNode).getId());
			if (am.isGranted(path, org.apache.jackrabbit.core.security.authorization.Permission.READ)) {
				mail.setPermissions(Permission.READ);
			}
			
			if (am.isGranted(path, org.apache.jackrabbit.core.security.authorization.Permission.ADD_NODE)) {
				mail.setPermissions((byte) (mail.getPermissions() | Permission.WRITE));
			}
			
			if (am.isGranted(path, org.apache.jackrabbit.core.security.authorization.Permission.REMOVE_NODE)) {
				mail.setPermissions((byte) (mail.getPermissions() | Permission.DELETE));
			}
			
			if (am.isGranted(path, org.apache.jackrabbit.core.security.authorization.Permission.MODIFY_AC)) {
				mail.setPermissions((byte) (mail.getPermissions() | Permission.SECURITY));
			}
		}
		
		log.debug("Permisos: {} => {}", mailNode.getPath(), mail.getPermissions());
		log.debug("getProperties[session]: {}", mail);
		return mail;
	}
	
	/**
	 * Copy recursively
	 */
	public static void copy(Session session, Node srcMailNode, Node dstFolderNode) throws ValueFormatException, 
			javax.jcr.PathNotFoundException, javax.jcr.RepositoryException, IOException, DatabaseException,
			UserQuotaExceededException {
		log.debug("copy({}, {}, {})", new Object[] { session, srcMailNode, dstFolderNode });
		
		String name = srcMailNode.getName();
		long size = srcMailNode.getProperty(Mail.SIZE).getLong();
		String from = srcMailNode.getProperty(Mail.FROM).getString();
		String[] reply = JCRUtils.value2String(srcMailNode.getProperty(Mail.REPLY).getValues());
		String[] to = JCRUtils.value2String(srcMailNode.getProperty(Mail.TO).getValues());
		String[] cc = JCRUtils.value2String(srcMailNode.getProperty(Mail.CC).getValues());
		String[] bcc = JCRUtils.value2String(srcMailNode.getProperty(Mail.BCC).getValues());
		Calendar sentDate = srcMailNode.getProperty(Mail.SENT_DATE).getDate(); 
		Calendar receivedDate = srcMailNode.getProperty(Mail.RECEIVED_DATE).getDate();
		String subject = srcMailNode.getProperty(Mail.SUBJECT).getString();
		String content = srcMailNode.getProperty(Mail.CONTENT).getString();
		String mimeType = srcMailNode.getProperty(Mail.MIME_TYPE).getString();
		
		Node mNode = BaseMailModule.create(session, dstFolderNode, name, size, from, reply, to, 
				cc, bcc, sentDate, receivedDate, subject, content, mimeType);
		
		// Get attachments
		for (NodeIterator nit = srcMailNode.getNodes(); nit.hasNext(); ) {
			Node node = nit.nextNode();

			if (node.isNodeType(Document.TYPE)) {
				BaseDocumentModule.copy(session, node, mNode);
			}
		}
		
		log.debug("copy: void");
	}
}
