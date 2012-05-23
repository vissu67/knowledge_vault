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

package com.openkm.extension.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.DatabaseException;
import com.openkm.dao.HibernateUtil;
import com.openkm.extension.dao.bean.Forum;
import com.openkm.extension.dao.bean.ForumPost;
import com.openkm.extension.dao.bean.ForumTopic;

/**
 * ExtensionDAO
 * 
 * @author pavila
 */
public class ForumDAO {
	private static Logger log = LoggerFactory.getLogger(ForumDAO.class);

	private ForumDAO() {}
	
	/**
	 * Create
	 */
	public static int create(Forum frm) throws DatabaseException {
		log.debug("create({})", frm);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Integer id = (Integer) session.save(frm);
			HibernateUtil.commit(tx);
			log.debug("create: {}" + id);
			return id.intValue();
		} catch(HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	/**
	 * Update
	 */
	public static void update(Forum frm) throws DatabaseException {
		log.debug("update({})", frm);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.update(frm);
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
	 * Update
	 */
	public static void update(ForumTopic topic) throws DatabaseException {
		log.debug("update({})", topic);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.update(topic);
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
	 * Update
	 */
	public static void update(ForumPost post) throws DatabaseException {
		log.debug("update({})", post);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.update(post);
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
	public static void delete(int frmId) throws DatabaseException {
		log.debug("delete({})", frmId);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Forum frm = (Forum) session.load(Forum.class, frmId);
			session.delete(frm);
			HibernateUtil.commit(tx);
		} catch(HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
		
		log.debug("deleteSent: void");
	}
	
	/**
	 * Find by pk
	 */
	public static Forum findByPk(int id) throws DatabaseException {
		log.debug("findByPk({})");
		String qs = "from Forum frm where frm.id=:id";		
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setInteger("id", id);
			Forum ret = (Forum) q.setMaxResults(1).uniqueResult();
			log.debug("findByPk: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	/**
	 * Find by pk
	 */
	public static ForumTopic findTopicByPk(int id) throws DatabaseException {
		log.debug("findTopicByPk({})");
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			ForumTopic ret = (ForumTopic) session.get(ForumTopic.class, id);
			log.debug("findTopicByPk: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	/**
	 * Find by pk
	 */
	public static ForumPost findPostByPk(int id) throws DatabaseException {
		log.debug("findPostByPk({})");	
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			ForumPost ret = (ForumPost) session.get(ForumPost.class, id);
			log.debug("findPostByPk: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	/**
	 * Find all forums
	 */
	@SuppressWarnings("unchecked")
	public static List<Forum> findAll() throws DatabaseException {
		log.debug("findAll({})");
		String qs = "from Forum frm order by frm.date asc";		
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			List<Forum> ret = q.list();

			log.debug("findAll: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	/**
	 * Find all topics by uuid
	 */
	@SuppressWarnings("unchecked")
	public static List<ForumTopic> findAllTopicByUuid(String uuid) throws DatabaseException {
		log.debug("findAllTopicByUuid({})");
		String qs = "from ForumTopic ft where ft.uuid=:uuid order by ft.lastPostDate desc";		
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("uuid", uuid);
			List<ForumTopic> ret = q.list();
			log.debug("findAllTopicByUuid: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
}