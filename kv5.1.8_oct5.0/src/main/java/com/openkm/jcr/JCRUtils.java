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

package com.openkm.jcr;

import java.io.File;
import java.io.IOException;
import java.security.PrivilegedAction;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.Workspace;
import javax.jcr.lock.Lock;
import javax.jcr.lock.LockException;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.security.auth.Subject;

import org.apache.commons.io.FileUtils;
import org.apache.jackrabbit.api.jsr283.security.AccessControlList;
import org.apache.jackrabbit.api.jsr283.security.AccessControlManager;
import org.apache.jackrabbit.api.jsr283.security.AccessControlPolicy;
import org.apache.jackrabbit.api.jsr283.security.AccessControlPolicyIterator;
import org.apache.jackrabbit.api.jsr283.security.Privilege;
import org.apache.jackrabbit.core.NodeImpl;
import org.apache.jackrabbit.core.RepositoryCopier;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.SessionImpl;
import org.apache.jackrabbit.core.lock.LockManager;
import org.apache.jackrabbit.core.lock.LockManagerImpl;
import org.apache.jackrabbit.core.security.principal.PrincipalImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Document;
import com.openkm.bean.Folder;
import com.openkm.bean.Mail;
import com.openkm.bean.StatsInfo;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.RepositoryException;
import com.openkm.dao.LockTokenDAO;
import com.openkm.dao.bean.LockToken;
import com.openkm.module.direct.DirectAuthModule;
import com.openkm.module.direct.DirectRepositoryModule;
import com.openkm.util.StackTraceUtils;

public class JCRUtils {
	private static Logger log = LoggerFactory.getLogger(JCRUtils.class);
	private static long activeSessions = 0;
	private static long sessionCreationCount = 0;
	private static long sessionDestroyCount = 0;
	
/*	//addd by vissu on 7nov
	private static String TAXONOMY_DOCUMENTS = "/jcr:root/okm:root//element(*,okm:document)";
	private static String TEMPLATES_DOCUMENTS = "/jcr:root/okm:templates//element(*,okm:document)";
	private static String PERSONAL_DOCUMENTS = "/jcr:root/okm:personal//element(*,okm:document)";
	private static String TRASH_DOCUMENTS = "/jcr:root/okm:trash//element(*,okm:document)";
*/
	
	/**
	 * Get parent node.
	 */
	public static String getParent(String path) {
		log.debug("getParent({})", path);
		int lastSlash = path.lastIndexOf('/');
		String ret = (lastSlash > 0)?path.substring(0, lastSlash):"";
		log.debug("getParent: {}", ret);
		return ret;	
	}

	/**
	 * Get node name.
	 */
	public static String getName(String path) {
		log.debug("getName({})", path);
		String ret = path.substring(path.lastIndexOf('/') + 1);
		log.debug("getName: {}", ret);
		return ret;
	}
	
	/**
	 * Eliminate dangerous chars in node name.
	 * TODO Keep on sync with uploader:com.openkm.applet.Util.escape(String)
	 */
	public static String escape(String name) {
		log.debug("escape({})", name);
		String ret = name.replace('/', ' ');
		ret = ret.replace(':', ' ');
		ret = ret.replace('[', ' ');
		ret = ret.replace(']', ' ');
		ret = ret.replace('*', ' ');
		ret = ret.replace('\'', ' ');
		ret = ret.replace('"', ' ');
		ret = ret.replace('|', ' ');
		ret = ret.trim();
		log.debug("escape: {}", ret);
		return ret;
	}
	
	/**
	 * Convert a Value array to String array and add a user id.
	 */
	public static String[] usrValue2String(Value[] values, String usrId) throws ValueFormatException, IllegalStateException, javax.jcr.RepositoryException {
		ArrayList<String> list = new ArrayList<String>();
		
		for (int i=0; i<values.length; i++) {
			// Admin and System user is not propagated across the child nodes
			if (!values[i].getString().equals(Config.SYSTEM_USER) && 
					!values[i].getString().equals(Config.ADMIN_USER)) {
				list.add(values[i].getString());
			}
		}
		
		if (Config.USER_ASSIGN_DOCUMENT_CREATION) {
			// No add an user twice
			if (!list.contains(usrId)) {
				list.add(usrId);
			}
		}
		
		return (String[]) list.toArray(new String[list.size()]);
	}
	
	/**
	 * Convert a Value array to String array.
	 */
	public static String[] rolValue2String(Value[] values) throws ValueFormatException, IllegalStateException, javax.jcr.RepositoryException {
		ArrayList<String> list = new ArrayList<String>();
		
		for (int i=0; i<values.length; i++) {
			// Do not propagate private OpenKM roles
			if (!values[i].getString().equals(Config.DEFAULT_ADMIN_ROLE)) {
				list.add(values[i].getString()); 
			}
		}
		
		return (String[]) list.toArray(new String[list.size()]);
	}

	/**
	 * 
	 */
	public static String[] value2String(Value[] values) throws ValueFormatException, IllegalStateException, javax.jcr.RepositoryException {
		ArrayList<String> list = new ArrayList<String>();
		
		for (int i=0; i<values.length; i++) {
			list.add(values[i].getString()); 
		}
		
		return (String[]) list.toArray(new String[list.size()]);
	}

	/**
	 * This method discards all pending changes currently recorded in this
	 * Session that apply to this Node or any of its descendants.
	 *  
	 * @param node The node to cancel.
	 */
	public static void discardsPendingChanges(Node node) {
		try {
			// JSR-170: page 173
			// http://www.day.com/maven/jsr170/javadocs/jcr-1.0/javax/jcr/Item.html#refresh(boolean)
			if (node != null) {
				node.refresh(false);
			} else {
				log.warn("node == NULL");
			}
		} catch (javax.jcr.RepositoryException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * This method discards all pending changes currently recorded in this
	 * Session that apply to this Session.
	 *  
	 * @param node The node to cancel.
	 */
	public static void discardsPendingChanges(Session session) {
		try {
			// http://www.day.com/maven/jsr170/javadocs/jcr-1.0/javax/jcr/Session.html#refresh(boolean)
			if (session != null) {
				session.refresh(false);
			} else {
				log.warn("session == NULL");
			}
		} catch (javax.jcr.RepositoryException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Make a silent logout
	 * See http://jackrabbit.510166.n4.nabble.com/Lock-token-not-being-added-to-session-td2018601.html
	 */
	public static void logout(Session session) {
		if (session != null && session.isLive()) {
			for (String lt: session.getLockTokens()) {
				log.debug("Remove LockToken: {}", lt);
				session.removeLockToken(lt);
			}
			session.logout();
			log.debug("#{} - {} Destroy session {} from {}", new Object[] { ++sessionDestroyCount, --activeSessions, session, StackTraceUtils.whoCalledMe() });
		}
	}
	
	/**
	 * Load lock tokens from database
	 */
	public static void loadLockTokens(Session session) throws DatabaseException,
			javax.jcr.RepositoryException {
		List<LockToken> ltList = LockTokenDAO.findByUser(session.getUserID());
		
		for (Iterator<LockToken> it = ltList.iterator(); it.hasNext(); ) {
			LockToken lt = it.next();
			session.addLockToken(lt.getToken());
		}
	}
	
	/**
	 * Add lock token to user data
	 */
	public static void addLockToken(Session session, Node node) throws DatabaseException,
			javax.jcr.RepositoryException {
		log.debug("addLockToken({}, {})", session, node);
		LockToken lt = new LockToken();
		lt.setUser(session.getUserID());
		lt.setToken(getLockToken(node.getUUID()));
		LockTokenDAO.add(lt);
		log.debug("addLockToken: void");
	}

	/**
	 * Remove lock token from user data
	 */
	public static void removeLockToken(Session session, Node node) throws DatabaseException, 
			javax.jcr.RepositoryException {
		log.debug("removeLockToken({}, {})", session, node);
		LockTokenDAO.remove(session.getUserID(), getLockToken(node.getUUID()));
		log.debug("removeLockToken: void");
	}
	
	/**
	 * Obtain lock token from node
	 */
	public static String getLockToken(Session session, Node node) throws LockException, 
			javax.jcr.RepositoryException {
		LockManager lm = ((SessionImpl)session).getLockManager();
		Lock lock = ((LockManagerImpl) lm).getLock((NodeImpl)node);
		
		if (lock != null) {
			return lock.getLockToken();
		} else {
			return null;
		}
	}
	
	/**
	 * Obtain lock token from node id
	 */
	public static String getLockToken(String id) {
		StringBuffer buf = new StringBuffer();
		buf.append(id.toString());
		buf.append('-');
		buf.append(getCheckDigit(id.toString()));
		return buf.toString();
	}
	
	/**
	 * Calculate check digit for lock token
	 * 
	 * @see org.apache.jackrabbit.core.lock.LockToken.getCheckDigit(String uuid)
	 */
	private static char getCheckDigit(String uuid) {
        int result = 0;

        int multiplier = 36;
        for (int i = 0; i < uuid.length(); i++) {
            char c = uuid.charAt(i);
            if (c >= '0' && c <= '9') {
                int num = c - '0';
                result += multiplier * num;
                multiplier--;
            } else if (c >= 'A' && c <= 'F') {
                int num = c - 'A' + 10;
                result += multiplier * num;
                multiplier--;
            } else if (c >= 'a' && c <= 'f') {
                int num = c - 'a' + 10;
                result += multiplier * num;
                multiplier--;
            }
        }

        int rem = result % 37;
        if (rem != 0) {
            rem = 37 - rem;
        }
        if (rem >= 0 && rem <= 9) {
            return (char) ('0' + rem);
        } else if (rem >= 10 && rem <= 35) {
            return (char) ('A' + rem - 10);
        } else {
            return '+';
        }
    }
	
	/**
	 * 
	 */
	public static void grant(Session session, String path, String principal, String privilege) throws javax.jcr.RepositoryException {
		AccessControlManager acm = ((SessionImpl) session).getAccessControlManager();
		AccessControlPolicyIterator acpi = acm.getApplicablePolicies(path);
		AccessControlPolicy acp = acpi.nextAccessControlPolicy();
		Privilege[] privileges = new Privilege[] { acm.privilegeFromName(Privilege.JCR_ALL) };
		((AccessControlList) acp).addAccessControlEntry(new PrincipalImpl(principal), privileges);
		session.save();
	}
	
	/**
	 * Repository Hot-Backup 
	 */
	public static File hotBackup() throws RepositoryException, IOException {
		log.debug("hotBackup()");
		String date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String backDirName = Config.CONTEXT + "_" + date; 
		File backDir = new File(System.getProperty("java.io.tmpdir") + File.separator + backDirName);
		FileUtils.deleteQuietly(backDir);
		backDir.mkdir();
		boolean oldSystemReadonly = Config.SYSTEM_READONLY; 

		try {
			Config.SYSTEM_READONLY = true;
			RepositoryCopier.copy((RepositoryImpl) DirectRepositoryModule.getRepository(), backDir);
		} catch (javax.jcr.RepositoryException e) {
			FileUtils.deleteQuietly(backDir);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			Config.SYSTEM_READONLY = oldSystemReadonly;
		}
		
		log.debug("hotBackup: {}", backDir);
		return backDir;
	}
	
	/**
	 * Get JCR Session
	 */
	public static Session getSession() throws javax.jcr.LoginException, javax.jcr.RepositoryException,
			DatabaseException {
		Object obj = null;
		
		try {
			InitialContext ctx = new InitialContext();
			Subject subject = (Subject) ctx.lookup("java:comp/env/security/subject");
			obj = Subject.doAs(subject, new PrivilegedAction<Object>() {
				public Object run() {
					Session s = null;

					try {
						s = DirectRepositoryModule.getRepository().login();
					} catch (javax.jcr.LoginException e) {
						return e;
					} catch (javax.jcr.RepositoryException e) {
						return e;
					}

					return s;
				}
			});
		} catch (NamingException e) {
			throw new javax.jcr.LoginException(e.getMessage());
		}
		
		if (obj instanceof javax.jcr.LoginException) {
			throw (javax.jcr.LoginException) obj;
		} else if (obj instanceof javax.jcr.RepositoryException) {
			throw (javax.jcr.LoginException) obj;
		} else if (obj instanceof javax.jcr.Session) {
			Session session = (javax.jcr.Session) obj;
			log.debug("#{} - {} Create session {} from {}", new Object[] { ++sessionCreationCount, ++activeSessions, session, StackTraceUtils.whoCalledMe() });
			DirectAuthModule.loadUserData(session);
			return session;
		} else {
			return null;
		}
	}
	
	/**
	 * Get node type
	 */
	public static String getNodeType(Node node) throws javax.jcr.RepositoryException  {
		String ret = "unknown";

		if (node.isNodeType(Document.TYPE)) {
			ret = Document.TYPE;
		} else if (node.isNodeType(Folder.TYPE)) {
			ret = Folder.TYPE;
		} else if (node.isNodeType(Mail.TYPE)) {
			ret = Mail.TYPE;
		}

		return ret;
	}
	
	/**
	 * Get node uuid from path
	 */
	public static String getUUID(Session session, String path) throws javax.jcr.RepositoryException {
		Node rootNode = session.getRootNode();
		Node node = rootNode.getNode(path.substring(1));
		return node.getUUID();
	}
	
	/**
	 * Get node path from uuid
	 */
	public static String getPath(Session session, String uuid) throws javax.jcr.RepositoryException {
		Node node = session.getNodeByUUID(uuid);
		return node.getPath();
	}
	
	/**
	 * Calculate user quota
	 */
	public static long calculateQuota(Session session) throws javax.jcr.RepositoryException,DatabaseException {
		// "/jcr:root/okm:root//element(*, okm:document)[okm:content/@okm:author='"+session.getUserID()+"']";
		//commented below by vissu to get the whole repository size on nov6
		//String qs = "/jcr:root//element(*, okm:document)[okm:content/@okm:author='"+session.getUserID()+"']";
		Session session1 = null;
		session1 = JCRUtils.getSession();

		String qs = "/jcr:root/okm:root//element(*,okm:document)";
		
		Workspace workspace = session1.getWorkspace();
		QueryManager queryManager = workspace.getQueryManager();
		Query query = queryManager.createQuery(qs, Query.XPATH);
		QueryResult result = query.execute();
		long size = 0;
		
		for (NodeIterator nit = result.getNodes(); nit.hasNext(); ) {
			Node node = nit.nextNode();
			Node contentNode = node.getNode(Document.CONTENT);
			size += contentNode.getProperty(Document.SIZE).getLong();
		}
		
		return size;
	}
}
