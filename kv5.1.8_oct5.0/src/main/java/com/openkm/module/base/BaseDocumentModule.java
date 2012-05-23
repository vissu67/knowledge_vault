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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.core.NodeImpl;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.core.security.AccessManager;
import org.apache.jackrabbit.spi.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Document;
import com.openkm.bean.Encryption;
import com.openkm.bean.Folder;
import com.openkm.bean.Lock;
import com.openkm.bean.Note;
import com.openkm.bean.Notification;
import com.openkm.bean.Permission;
import com.openkm.bean.Property;
import com.openkm.bean.Version;
import com.openkm.cache.UserItemsManager;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.UserQuotaExceededException;
import com.openkm.dao.UserConfigDAO;
import com.openkm.dao.bean.ProfileMisc;
import com.openkm.dao.bean.UserConfig;
import com.openkm.dao.bean.cache.UserItems;
import com.openkm.extractor.RegisteredExtractors;
import com.openkm.jcr.JCRUtils;
import com.openkm.util.DocConverter;
import com.openkm.util.UserActivity;

public class BaseDocumentModule {
	private static Logger log = LoggerFactory.getLogger(BaseDocumentModule.class);
	
	/**
	 * Create a new document
	 * 
	 * TODO Parameter title to be used in OpenKM 6
	 */
	public static Node create(Session session, Node parentNode, String name, String title, String mimeType,
			String[] keywords, InputStream is) throws javax.jcr.ItemExistsException,
			javax.jcr.PathNotFoundException, javax.jcr.AccessDeniedException, javax.jcr.RepositoryException,
			IOException, DatabaseException, UserQuotaExceededException {
		log.debug("create({}, {}, {}, {}, {}, {}, {})", new Object[] { session, parentNode, name, title,
				mimeType, keywords, is });

		// Create and add a new file node
		Node documentNode = parentNode.addNode(name, Document.TYPE);
		documentNode.setProperty(Property.KEYWORDS, keywords);
		documentNode.setProperty(Property.CATEGORIES, new String[]{}, PropertyType.REFERENCE);
		documentNode.setProperty(Document.AUTHOR, session.getUserID());
		documentNode.setProperty(Document.NAME, name);
		// documentNode.setProperty(Document.TITLE, title == null ? "" : title);
		long size = is.available();
		
		// Check user quota
		UserConfig uc = UserConfigDAO.findByPk(session, session.getUserID());
		ProfileMisc pm = uc.getProfile().getMisc();
		
		// System user don't care quotas
		if (!Config.SYSTEM_USER.equals(session.getUserID()) && pm.getUserQuota() > 0) {
			long currentQuota = 0;
			
			if (Config.USER_ITEM_CACHE) {
				UserItems ui = UserItemsManager.get(session.getUserID());
				currentQuota = ui.getSize();
			} else {
				currentQuota = JCRUtils.calculateQuota(session);
			}
			
			if (currentQuota + size > pm.getUserQuota()) {
				throw new UserQuotaExceededException(Long.toString(currentQuota + size));
			}
		}
		
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
		documentNode.setProperty(Permission.USERS_READ, usersRead);
		documentNode.setProperty(Permission.USERS_WRITE, usersWrite);
		documentNode.setProperty(Permission.USERS_DELETE, usersDelete);
		documentNode.setProperty(Permission.USERS_SECURITY, usersSecurity);
		documentNode.setProperty(Permission.ROLES_READ, rolesRead);
		documentNode.setProperty(Permission.ROLES_WRITE, rolesWrite);
		documentNode.setProperty(Permission.ROLES_DELETE, rolesDelete);
		documentNode.setProperty(Permission.ROLES_SECURITY, rolesSecurity);

		Node contentNode = documentNode.addNode(Document.CONTENT, Document.CONTENT_TYPE);
		contentNode.setProperty(Document.SIZE, size);
		contentNode.setProperty(Document.AUTHOR, session.getUserID());
		contentNode.setProperty(Document.VERSION_COMMENT, "");
		contentNode.setProperty(JcrConstants.JCR_MIMETYPE, mimeType);
		contentNode.setProperty(JcrConstants.JCR_DATA, is);
		
		// jcr:encoding only have sense for text/* MIME
		if (mimeType.startsWith("text/")) {
			contentNode.setProperty(JcrConstants.JCR_ENCODING, "UTF-8");
		}
		
		if (Config.EXPERIMENTAL_TEXT_EXTRACTION) {
			RegisteredExtractors.index(documentNode, contentNode, mimeType);
		}
		
		contentNode.setProperty(JcrConstants.JCR_LASTMODIFIED, Calendar.getInstance());
		parentNode.save();

		// Esta línea vale millones!! Resuelve la incidencia del isCkechedOut.
		// Por lo visto un nuevo nodo se añade con el isCheckedOut a true :/
		contentNode.checkin();
		
		// Update user items size
		if (Config.USER_ITEM_CACHE) {
			UserItemsManager.incSize(session.getUserID(), size);
			UserItemsManager.incDocuments(session.getUserID(), 1);
		}
		
		return documentNode;
	}
	
	/**
	 * Get document properties using a given Session.
	 */
	public static Document getProperties(Session session, Node docNode) throws javax.jcr.PathNotFoundException,
			javax.jcr.RepositoryException {
		log.debug("getProperties({}, {})", session, docNode);
		Document doc = new Document();
		Node contentNode = docNode.getNode(Document.CONTENT);

		// Properties
		doc.setAuthor(docNode.getProperty(Document.AUTHOR).getString());
		
		// TODO Remove this check in OpenKM 6
		// if (documentNode.hasProperty(Document.TITLE)) {
		// 	doc.setTitle(documentNode.getProperty(Document.TITLE).getPath());		
		// }
		
		doc.setPath(docNode.getPath());
		doc.setLocked(docNode.isLocked());
		doc.setUuid(docNode.getUUID());
		
		if (doc.isLocked()) {
			doc.setLockInfo(getLock(session, docNode.getPath()));
		} else {
			doc.setLockInfo(null);
		}

		doc.setCheckedOut(contentNode.isCheckedOut());
		doc.setMimeType(contentNode.getProperty(JcrConstants.JCR_MIMETYPE).getString());
		doc.setLastModified(contentNode.getProperty(JcrConstants.JCR_LASTMODIFIED).getDate());

		// Get actual version
		if (docNode.isNodeType(Document.TYPE)) {
			javax.jcr.version.Version ver = contentNode.getBaseVersion();
			Version version = new Version();
			version.setAuthor(contentNode.getProperty(Document.AUTHOR).getString());
			version.setSize(contentNode.getProperty(Document.SIZE).getLong());
			version.setComment(contentNode.getProperty(Document.VERSION_COMMENT).getString());
			version.setName(ver.getName());
			version.setCreated(ver.getCreated());
			version.setActual(true);
			doc.setActualVersion(version);
		}

		// If this is a frozen node, we must get create property from
		// the original referenced node.
		if (docNode.isNodeType(JcrConstants.NT_FROZENNODE)) {
			Node node = docNode.getProperty(JcrConstants.JCR_FROZENUUID).getNode();
			doc.setCreated(node.getProperty(JcrConstants.JCR_CREATED).getDate());
		} else {
			doc.setCreated(docNode.getProperty(JcrConstants.JCR_CREATED).getDate());
		}

		// Get permissions
		if (Config.SYSTEM_READONLY) {
			doc.setPermissions(Permission.NONE);
		} else {
			AccessManager am = ((SessionImpl) session).getAccessManager();
			Path path = ((NodeImpl)docNode).getPrimaryPath();
			//Path path = ((SessionImpl)session).getHierarchyManager().getPath(((NodeImpl)folderNode).getId());
			if (am.isGranted(path, org.apache.jackrabbit.core.security.authorization.Permission.READ)) {
				doc.setPermissions(Permission.READ);
			}
			
			if (am.isGranted(path, org.apache.jackrabbit.core.security.authorization.Permission.ADD_NODE)) {
				doc.setPermissions((byte) (doc.getPermissions() | Permission.WRITE));
			}
			
			if (am.isGranted(path, org.apache.jackrabbit.core.security.authorization.Permission.REMOVE_NODE)) {
				doc.setPermissions((byte) (doc.getPermissions() | Permission.DELETE));
			}
			
			if (am.isGranted(path, org.apache.jackrabbit.core.security.authorization.Permission.MODIFY_AC)) {
				doc.setPermissions((byte) (doc.getPermissions() | Permission.SECURITY));
			}
		}
		
		// Get user subscription
		Set<String> subscriptorSet = new HashSet<String>();

		if (docNode.isNodeType(Notification.TYPE)) {
			Value[] subscriptors = docNode.getProperty(Notification.SUBSCRIPTORS).getValues();

			for (int i=0; i<subscriptors.length; i++) {
				subscriptorSet.add(subscriptors[i].getString());

				if (session.getUserID().equals(subscriptors[i].getString())) {
					doc.setSubscribed(true);
				}
			}
		}

		doc.setSubscriptors(subscriptorSet);
		
		// Get document keywords
		Set<String> keywordsSet = new HashSet<String>();
		Value[] keywords = docNode.getProperty(Property.KEYWORDS).getValues();

		for (int i=0; i<keywords.length; i++) {
			keywordsSet.add(keywords[i].getString());
		}

		doc.setKeywords(keywordsSet);
		
		// Get document categories
		Set<Folder> categoriesSet = new HashSet<Folder>();
		Value[] categories = docNode.getProperty(Property.CATEGORIES).getValues();

		for (int i=0; i<categories.length; i++) {
			Node node = session.getNodeByUUID(categories[i].getString());
			categoriesSet.add(BaseFolderModule.getProperties(session, node));
		}

		doc.setCategories(categoriesSet);
		
		DocConverter convert = DocConverter.getInstance();
		doc.setConvertibleToPdf(convert.convertibleToPdf(doc.getMimeType()));
		doc.setConvertibleToSwf(convert.convertibleToSwf(doc.getMimeType()));
		doc.setConvertibleToDxf(convert.convertibleToDxf(doc.getMimeType()));
		
		// Get notes
		if (docNode.isNodeType(Note.MIX_TYPE)) {
			List<Note> notes = new ArrayList<Note>();
			Node notesNode = docNode.getNode(Note.LIST);
			
			for (NodeIterator nit = notesNode.getNodes(); nit.hasNext(); ) {
				Node noteNode = nit.nextNode();
				Note note = new Note();
				note.setDate(noteNode.getProperty(Note.DATE).getDate());
				note.setUser(noteNode.getProperty(Note.USER).getString());
				note.setText(noteNode.getProperty(Note.TEXT).getString());
				note.setPath(noteNode.getPath());
				notes.add(note);
			}
			
			doc.setNotes(notes);
		}
		
		// Get crypto
		if (docNode.isNodeType(Encryption.TYPE)) {
			String cipherName = docNode.getProperty(Encryption.CIPHER_NAME).getString();
			doc.setCipherName(cipherName);
		}
		
		log.debug("Permisos: {} => {}", docNode.getPath(), doc.getPermissions());
		log.debug("getProperties[session]: {}", doc);
		return doc;
	}
	
	/**
	 * Retrieve lock info from a document path
	 */
	public static Lock getLock(Session session, String docPath) throws UnsupportedRepositoryOperationException,
			javax.jcr.lock.LockException, javax.jcr.AccessDeniedException, javax.jcr.RepositoryException {
		log.debug("getLock({}, {})", session, docPath);
		Lock lock = new Lock();
		Node documentNode = session.getRootNode().getNode(docPath.substring(1));
		javax.jcr.lock.Lock lck = documentNode.getLock();
		lock.setOwner(lck.getLockOwner());
		lock.setNodePath(lck.getNode().getPath());
		lock.setToken(lck.getLockToken());
		log.debug("getLock: {}", lock);
		return lock;
	}
	
	/**
	 * Set node content
	 */
	public static void setContent(Session session, Node docNode, InputStream is) throws
			PathNotFoundException, RepositoryException, IOException {
		long size = is.available();
		Node contentNode = docNode.getNode(Document.CONTENT);
		contentNode.setProperty(Document.SIZE, size);
		contentNode.setProperty(JcrConstants.JCR_DATA, is);
		
		if (Config.EXPERIMENTAL_TEXT_EXTRACTION) {
			String mimeType = contentNode.getProperty(JcrConstants.JCR_MIMETYPE).getString();
			RegisteredExtractors.index(docNode, contentNode, mimeType);
		}
		
		contentNode.setProperty(JcrConstants.JCR_LASTMODIFIED, Calendar.getInstance());
		contentNode.save();
	}
	
	/**
	 * Retrieve the content input stream from a document path
	 */
	public static InputStream getContent(Session session, String docPath, boolean checkout) throws 
			javax.jcr.PathNotFoundException, javax.jcr.RepositoryException, IOException {
		Node documentNode = session.getRootNode().getNode(docPath.substring(1));
		InputStream is = getContent(session, documentNode);

		// Activity log
		UserActivity.log(session.getUserID(), (checkout?"GET_DOCUMENT_CONTENT_CHECKOUT":"GET_DOCUMENT_CONTENT"), documentNode.getUUID(), is.available()+", "+docPath);
		
		return is;
	}
	
	/**
	 * Retrieve the content InputStream from a given Node. 
	 */
	public static InputStream getContent(Session session, Node docNode) throws javax.jcr.PathNotFoundException,
			javax.jcr.RepositoryException, IOException {
		log.debug("getContent({}, {})", session, docNode);
		
		Node contentNode = docNode.getNode(Document.CONTENT);
		InputStream is = contentNode.getProperty(JcrConstants.JCR_DATA).getStream();
		
		log.debug("getContent: {}", is);
		return is;
	}
	
	/**
	 * Remove version history, compute free space and remove obsolete files from
	 * PDF and previsualization cache.
	 */
	public static void purge(Session session, Node parentNode, Node docNode) 
			throws javax.jcr.PathNotFoundException, javax.jcr.RepositoryException {
		Node contentNode = docNode.getNode(Document.CONTENT);
		long size = contentNode.getProperty(Document.SIZE).getLong();
		String author = contentNode.getProperty(Document.AUTHOR).getString();
		VersionHistory vh = contentNode.getVersionHistory();
		log.debug("VersionHistory UUID: {}", vh.getUUID());

		// Remove pdf & preview from cache
		new File(Config.CACHE_DXF + File.separator + docNode.getUUID() + ".dxf").delete();
		new File(Config.CACHE_PDF + File.separator + docNode.getUUID() + ".pdf").delete();
		new File(Config.CACHE_SWF + File.separator + docNode.getUUID() + ".swf").delete();
		
		// Remove node itself
		docNode.remove();
		parentNode.save();

		// Unreferenced VersionHistory should be deleted automatically
		// after removal of the last Version
		// https://issues.apache.org/jira/browse/JCR-134
		// http://markmail.org/message/7aildokt74yeoar5
		// http://markmail.org/message/nhbwe7o3c7pd4sga
		//
		// ********** THIS IS ACCORDING WITH JCR-134
		for (VersionIterator vi = vh.getAllVersions(); vi.hasNext(); ) {
			javax.jcr.version.Version ver = vi.nextVersion();
			String versionName = ver.getName();
			log.debug("Version: {}", versionName);
			
			// The rootVersion is not a "real" version node.
			if (!versionName.equals(JcrConstants.JCR_ROOTVERSION)) {
				//Node frozenNode = ver.getNode(JcrConstants.JCR_FROZENNODE);
				//size = frozenNode.getProperty(Document.SIZE).getLong();
				//author = frozenNode.getProperty(Document.AUTHOR).getString();
				log.debug("vh.removeVersion({})", versionName);
				vh.removeVersion(versionName);
			}
		}
		
		if (Config.USER_ITEM_CACHE) {
			UserItemsManager.decSize(author, size);
			UserItemsManager.decDocuments(author, 1);
		}
	}
	
	/**
	 * Is invoked from DirectDocumentNode and DirectFolderNode.
	 */
	public static Node copy(Session session, Node srcDocumentNode, Node dstFolderNode) throws
			ValueFormatException, javax.jcr.PathNotFoundException, javax.jcr.RepositoryException,
			IOException, DatabaseException, UserQuotaExceededException {
		log.debug("copy({}, {}, {})", new Object[] { session, srcDocumentNode, dstFolderNode });
		
		Node srcDocumentContentNode = srcDocumentNode.getNode(Document.CONTENT);
		String mimeType = srcDocumentContentNode.getProperty("jcr:mimeType").getString();
		// String title = srcDocumentContentNode.getProperty(Document.TITLE).getString();
		InputStream is = srcDocumentContentNode.getProperty("jcr:data").getStream();
		Node newDocument = BaseDocumentModule.create(session, dstFolderNode, srcDocumentNode.getName(),
				null /* title */, mimeType, new String[]{}, is);
		is.close();
		
		log.debug("copy: {}", newDocument);
		return newDocument;
	}
	
	/**
	 * Clean preview cache for this document
	 */
	public static void cleanPreviewCache(String uuid) {
		new File(Config.CACHE_DXF + File.separator + uuid + ".dxf").delete();
		new File(Config.CACHE_PDF + File.separator + uuid + ".pdf").delete();
		new File(Config.CACHE_SWF + File.separator + uuid + ".swf").delete();
	}
}
