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

package com.openkm.dao;

import java.util.List;

import javax.jcr.Node;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Folder;
import com.openkm.bean.Repository;
import com.openkm.core.DatabaseException;
import com.openkm.core.RepositoryException;
import com.openkm.dao.bean.Bookmark;
import com.openkm.jcr.JCRUtils;

public class BookmarkDAO {
	private static Logger log = LoggerFactory.getLogger(BookmarkDAO.class);

	private BookmarkDAO() {}
	
	/**
	 * Create
	 */
	public static void create(Bookmark bm) throws DatabaseException {
		log.debug("create({})", bm);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.save(bm);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
		
		log.debug("create: void");
	}
	
	/**
	 * Update
	 */
	public static void update(Bookmark bm) throws DatabaseException {
		log.debug("update({})", bm);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.update(bm);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
		
		log.debug("update: void");
	}
	
	/**
	 * Delete
	 */
	public static void delete(int bmId) throws DatabaseException {
		log.debug("delete({})", bmId);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Bookmark bm = (Bookmark) session.load(Bookmark.class, bmId);
			session.delete(bm);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
		
		log.debug("delete: void");
	}
	
	/**
	 * Finde by user
	 */
	@SuppressWarnings("unchecked")
	public static List<Bookmark> findByUser(javax.jcr.Session jcrSession, String usrId) throws DatabaseException,
			RepositoryException {
		log.debug("findByUser({}, {})", jcrSession, usrId);
		String qs = "from Bookmark bm where bm.user=:user order by bm.id";
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(qs);
			q.setString("user", usrId);
			List<Bookmark> ret = q.list();
			
			for (Bookmark bm : ret) {
				try {
					Node node = jcrSession.getNodeByUUID(bm.getUuid());
					String nType = JCRUtils.getNodeType(node);
					bm.setPath(node.getPath());
					
					// TODO Se supone que el tipo no cambia
					if (!nType.equals(bm.getType())) {
						bm.setType(JCRUtils.getNodeType(node));
						session.update(ret);
					}
				} catch (javax.jcr.ItemNotFoundException e) {
					// If user bookmark is missing, set a default
					Node okmRoot = jcrSession.getRootNode().getNode(Repository.ROOT);
					bm.setPath(okmRoot.getPath());
					bm.setUuid(okmRoot.getUUID());
					bm.setType(Folder.TYPE);
					session.save(bm);
				}
			}
			
			HibernateUtil.commit(tx);
			log.debug("findByUser: {}", ret);
			return ret;
		} catch (javax.jcr.RepositoryException e) {
			HibernateUtil.rollback(tx);
			throw new RepositoryException(e.getMessage(), e);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}


	/**
	 * Find by pk
	 */
	public static Bookmark findByPk(javax.jcr.Session jcrSession, int bmId) throws DatabaseException,
			RepositoryException {
		log.debug("findByPk({}, {})", jcrSession, bmId);
		String qs = "from Bookmark bm where bm.id=:id";
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(qs);
			q.setInteger("id", bmId);
			Bookmark ret = (Bookmark) q.setMaxResults(1).uniqueResult();
			
			try {
				Node node = jcrSession.getNodeByUUID(ret.getUuid());
				String nType = JCRUtils.getNodeType(node);
				ret.setPath(node.getPath());
				
				// TODO Se supone que el tipo no cambia
				if (!nType.equals(ret.getType())) {
					ret.setType(JCRUtils.getNodeType(node));
					session.update(ret);
				}
			} catch (javax.jcr.ItemNotFoundException e) {
				// If user bookmark is missing, set a default
				Node okmRoot = jcrSession.getRootNode().getNode(Repository.ROOT);
				ret.setPath(okmRoot.getPath());
				ret.setUuid(okmRoot.getUUID());
				ret.setType(Folder.TYPE);
				session.save(ret);
			}
			
			HibernateUtil.commit(tx);
			log.debug("findByPk: {}", ret);
			return ret;
		} catch (javax.jcr.RepositoryException e) {
			HibernateUtil.rollback(tx);
			throw new RepositoryException(e.getMessage(), e);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
}
