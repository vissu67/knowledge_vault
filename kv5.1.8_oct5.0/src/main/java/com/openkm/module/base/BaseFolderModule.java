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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.util.TraversingItemVisitor;
import javax.jcr.version.VersionException;

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.core.NodeImpl;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.core.security.AccessManager;
import org.apache.jackrabbit.spi.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.ContentInfo;
import com.openkm.bean.Document;
import com.openkm.bean.Folder;
import com.openkm.bean.Mail;
import com.openkm.bean.Note;
import com.openkm.bean.Notification;
import com.openkm.bean.Permission;
import com.openkm.cache.UserItemsManager;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.UserQuotaExceededException;
import com.openkm.jcr.JCRUtils;

public class BaseFolderModule {
	private static Logger log = LoggerFactory.getLogger(BaseFolderModule.class);
	
	/**
	 * Create a new folder
	 */
	public static Node create(Session session, Node parentNode, String name) throws 
			javax.jcr.ItemExistsException, javax.jcr.PathNotFoundException, NoSuchNodeTypeException, 
			javax.jcr.lock.LockException, VersionException, ConstraintViolationException, 
			javax.jcr.RepositoryException {
		// Create and add a new folder node
		Node folderNode = parentNode.addNode(name, Folder.TYPE);
		folderNode.setProperty(Folder.AUTHOR, session.getUserID());
		folderNode.setProperty(Folder.NAME, name);
		
		//added by vissu feb 6
		System.out.println("parentpath = "+parentNode.getPath());
		
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
		
		//added by vissu feb 6
		System.out.println("rolesreadparent = "+rolesReadParent.toString());
		
		// Set auth info
		folderNode.setProperty(Permission.USERS_READ, usersRead);
		folderNode.setProperty(Permission.USERS_WRITE, usersWrite);
		folderNode.setProperty(Permission.USERS_DELETE, usersDelete);
		folderNode.setProperty(Permission.USERS_SECURITY, usersSecurity);
		folderNode.setProperty(Permission.ROLES_READ, rolesRead);
		folderNode.setProperty(Permission.ROLES_WRITE, rolesWrite);
		folderNode.setProperty(Permission.ROLES_DELETE, rolesDelete);
		folderNode.setProperty(Permission.ROLES_SECURITY, rolesSecurity);
		
		parentNode.save();
		
		if (Config.USER_ITEM_CACHE) {
			// Update user items size
			UserItemsManager.incFolders(session.getUserID(), 1);
		}
		
		return folderNode;
	}
	
	/**
	 * Get folder properties
	 */
	public static Folder getProperties(Session session, Node fldNode) throws 
			javax.jcr.PathNotFoundException, javax.jcr.RepositoryException  {
		log.debug("getProperties[session]({}, {})", session, fldNode);
		Folder fld = new Folder();
		
		// Properties
		fld.setPath(fldNode.getPath());
		fld.setHasChilds(false);
		fld.setCreated(fldNode.getProperty(JcrConstants.JCR_CREATED).getDate());
		fld.setAuthor(fldNode.getProperty(Folder.AUTHOR).getString());
		fld.setUuid(fldNode.getUUID());
					
		for (NodeIterator nit = fldNode.getNodes(); nit.hasNext(); ) {
			Node node = nit.nextNode();

			if (node.isNodeType(Folder.TYPE)) {
				fld.setHasChilds(true);
				break;
			}
		}
		
		// Get permissions
		if (Config.SYSTEM_READONLY) {
			fld.setPermissions(Permission.NONE);
		} else {
			AccessManager am = ((SessionImpl) session).getAccessManager();
			Path path = ((NodeImpl)fldNode).getPrimaryPath();
			//Path path = ((SessionImpl)session).getHierarchyManager().getPath(((NodeImpl)folderNode).getId());
			if (am.isGranted(path, org.apache.jackrabbit.core.security.authorization.Permission.READ)) {
				fld.setPermissions(Permission.READ);
			}
			
			if (am.isGranted(path, org.apache.jackrabbit.core.security.authorization.Permission.ADD_NODE)) {
				fld.setPermissions((byte) (fld.getPermissions() | Permission.WRITE));
			}
			
			if (am.isGranted(path, org.apache.jackrabbit.core.security.authorization.Permission.REMOVE_NODE)) {
				fld.setPermissions((byte) (fld.getPermissions() | Permission.DELETE));
			}
			
			if (am.isGranted(path, org.apache.jackrabbit.core.security.authorization.Permission.MODIFY_AC)) {
				fld.setPermissions((byte) (fld.getPermissions() | Permission.SECURITY));
			}
		}
		
		// Get user subscription
		Set<String> subscriptorSet = new HashSet<String>();
		
		if (fldNode.isNodeType(Notification.TYPE)) {
			Value[] subscriptors = fldNode.getProperty(Notification.SUBSCRIPTORS).getValues();

			for (int i=0; i<subscriptors.length; i++) {
				subscriptorSet.add(subscriptors[i].getString());
				
				if (session.getUserID().equals(subscriptors[i].getString())) {
					fld.setSubscribed(true);
				}
			}
		}
		
		fld.setSubscriptors(subscriptorSet);
		
		// Get notes
		if (fldNode.isNodeType(Note.MIX_TYPE)) {
			List<Note> notes = new ArrayList<Note>();
			Node notesNode = fldNode.getNode(Note.LIST);
			
			for (NodeIterator nit = notesNode.getNodes(); nit.hasNext(); ) {
				Node noteNode = nit.nextNode();
				Note note = new Note();
				note.setDate(noteNode.getProperty(Note.DATE).getDate());
				note.setUser(noteNode.getProperty(Note.USER).getString());
				note.setText(noteNode.getProperty(Note.TEXT).getString());
				note.setPath(noteNode.getPath());
				notes.add(note);
			}
			
			fld.setNotes(notes);
		}
		
		log.debug("Permisos: {} => {}", fldNode.getPath(), fld.getPermissions());
		log.debug("getProperties[session]: {}", fld);
		return fld;
	}
	
	/**
	 * Performs recursive node copy
	 */
	public static void copy(Session session, Node srcFolderNode, Node dstFolderNode) throws
			NoSuchNodeTypeException, VersionException, ConstraintViolationException,
			javax.jcr.lock.LockException, javax.jcr.RepositoryException, IOException,
			DatabaseException, UserQuotaExceededException {
		log.debug("copy({}, {}, {})", new Object[] { session, srcFolderNode.getPath(), dstFolderNode.getPath() });
		
		for (NodeIterator it = srcFolderNode.getNodes(); it.hasNext(); ) {
			Node child = it.nextNode();
			
			if (child.isNodeType(Document.TYPE)) {
				BaseDocumentModule.copy(session, child, dstFolderNode);
				dstFolderNode.save();
			} else if (child.isNodeType(Mail.TYPE)) {
				BaseMailModule.copy(session, child, dstFolderNode);
				dstFolderNode.save();
			} else if (child.isNodeType(Folder.TYPE)) {
				Node newFolder = BaseFolderModule.create(session, dstFolderNode, child.getName());
				dstFolderNode.save();
				copy(session, child, newFolder);
			}
		}
		
		log.debug("copy: void");
	}
	
	/**
	 * Get content info recursively
	 */
	public static ContentInfo getContentInfo(Node folderNode) throws AccessDeniedException, 
			RepositoryException, PathNotFoundException {
		log.debug("getContentInfo({})", folderNode);
		ContentInfo contentInfo = new ContentInfo();
		
		try {
			for (NodeIterator ni = folderNode.getNodes(); ni.hasNext(); ) {
				Node child = ni.nextNode();
				
				if (child.isNodeType(Folder.TYPE)) {
					ContentInfo ci = getContentInfo(child);
					contentInfo.setFolders(contentInfo.getFolders() + ci.getFolders() + 1);
					contentInfo.setDocuments(contentInfo.getDocuments() + ci.getDocuments());
					contentInfo.setSize(contentInfo.getSize() + ci.getSize());
				} else if (child.isNodeType(Document.TYPE)) {
					Node documentContentNode = child.getNode(Document.CONTENT);
					long size = documentContentNode.getProperty(Document.SIZE).getLong();
					contentInfo.setDocuments(contentInfo.getDocuments() + 1);
					contentInfo.setSize(contentInfo.getSize() + size);
				} else if (child.isNodeType(Mail.TYPE)) {
					long size = child.getProperty(Mail.SIZE).getLong();
					contentInfo.setMails(contentInfo.getMails() + 1);
					contentInfo.setSize(contentInfo.getSize() + size);
				}
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
		}
		
		log.debug("getContentInfo: {}", contentInfo);
		return contentInfo;
	}
	
	/**
	 * Check recursively if the folder contains locked nodes
	 */
	public static boolean hasLockedNodes(Node node) throws javax.jcr.RepositoryException {
		boolean hasLock = false;
		
		for (NodeIterator ni = node.getNodes(); ni.hasNext(); ) {
			Node child = ni.nextNode();
			
			if (child.isNodeType(Document.TYPE)) {
				hasLock |= child.isLocked();
			} else if (child.isNodeType(Folder.TYPE)) {
				hasLock |= hasLockedNodes(child);
			} else if (child.isNodeType(Mail.TYPE)) {
				// Mail nodes can't be locked
				//added by vissu on may19
			} else if (child.isNodeType(Note.LIST)) {
				// Note nodes can't be locked
			} else {
				throw new javax.jcr.RepositoryException("Unknown node type: " + 
						child.getPrimaryNodeType().getName() + "(" + child.getPath() + ")");
			}
		}
		
		return hasLock;
	}
	
	/**
	 * Check if a node has removable childs
	 * TODO: Is this neccessary? The access manager should prevent this an
	 * make the core thown an exception. 
	 */
	public static boolean hasWriteAccess(Node node) throws javax.jcr.RepositoryException {
		log.debug("hasWriteAccess({})", node.getPath());
		final int REMOVE_NODE = org.apache.jackrabbit.core.security.authorization.Permission.REMOVE_NODE;
		boolean canWrite = true;
		AccessManager am = ((SessionImpl) node.getSession()).getAccessManager();
		
		for (NodeIterator ni = node.getNodes(); ni.hasNext(); ) {
			Node child = ni.nextNode();
			Path path = ((NodeImpl)node).getPrimaryPath();
			
			if (child.isNodeType(Document.TYPE)) {
				canWrite &= am.isGranted(path, REMOVE_NODE);
			} else if (child.isNodeType(Folder.TYPE)) {
				canWrite &= am.isGranted(path, REMOVE_NODE);
				canWrite &= hasWriteAccess(child);
			} else if (child.isNodeType(Mail.TYPE)) {
				canWrite &= am.isGranted(path, REMOVE_NODE);
				//added by vissu on may19
			} else if (child.isNodeType(Note.LIST)) {
				// Note nodes has no security
			} else {
				throw new javax.jcr.RepositoryException("Unknown node type: " + 
						child.getPrimaryNodeType().getName() + "(" + child.getPath() + ")");
			}
		}
		
		log.debug("hasWriteAccess: {}", canWrite);
		return canWrite;
	}
	
	/**
	 * Purge folders recursively
	 */
	public static void purge(Session session, Node fldNode) throws VersionException, 
			javax.jcr.lock.LockException, ConstraintViolationException, javax.jcr.RepositoryException {
		for (NodeIterator nit = fldNode.getNodes(); nit.hasNext(); ) {
			Node node = nit.nextNode();
			
			if (node.isNodeType(Document.TYPE)) {
				BaseDocumentModule.purge(session, node.getParent(), node);
			} else if (node.isNodeType(Folder.TYPE)) {
				BaseFolderModule.purge(session, node);
			}
		}
		
		String author = fldNode.getProperty(Folder.AUTHOR).getString();
		fldNode.remove();
		
		if (Config.USER_ITEM_CACHE) {
			UserItemsManager.decFolders(author, 1);
		}
	}
	
	/**
	 * Quicker version of getContentInfo but surpases user permissions
	 * 
	 * Could be a replacement of OKMFolder.getContentInfo() at:
	 * 
	 * @see com.openkm.servlet.admin.BenchmarkServlet
	 * @see com.openkm.servlet.admin.RepositoryCheckerServlet
	 * @see com.openkm.servlet.admin.RepositoryViewServlet
	 * 
	 * And in getCount(QueryManager, String) at :
	 * 
	 * @see com.openkm.module.direct.DirectStatsModule
	 */
	public static void getContentInfoVisitor(Session session, Node node) throws NoSuchNodeTypeException,
			VersionException, ConstraintViolationException, LockException, RepositoryException {
		log.debug("getContentInfoVisitor({}, {})", new Object[] { session, node });
		final ContentInfo ci = new ContentInfo();
		
		TraversingItemVisitor visitor = new TraversingItemVisitor.Default() {
			@Override
			protected void entering(Node node, int level) throws javax.jcr.RepositoryException {
				if (node.isNodeType(Folder.TYPE)) {
					ci.setFolders(ci.getFolders() + 1);
				} else if (node.isNodeType(Document.TYPE)) {
					Node docContent = node.getNode(Document.CONTENT);
					long size = docContent.getProperty(Document.SIZE).getLong();
					ci.setDocuments(ci.getDocuments() + 1);
					ci.setSize(ci.getSize() + size);
				} else if (node.isNodeType(Mail.TYPE)) {
					long size = node.getProperty(Mail.SIZE).getLong();
					ci.setMails(ci.getMails() + 1);
					ci.setSize(ci.getSize() + size);
				}
			}
		};
	
		node.accept(visitor);
	}
}
