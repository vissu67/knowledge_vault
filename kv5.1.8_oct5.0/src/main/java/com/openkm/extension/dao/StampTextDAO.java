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
import com.openkm.extension.dao.bean.StampText;

public class StampTextDAO {
	private static Logger log = LoggerFactory.getLogger(StampTextDAO.class);

	private StampTextDAO() {}
	
	/**
	 * Create
	 */
	public static int create(StampText st) throws DatabaseException {
		log.debug("create({})", st);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Integer id = (Integer) session.save(st);
			HibernateUtil.commit(tx);
			log.debug("create: {}", id);
			return id.intValue();
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	/**
	 * Update
	 */
	public static void update(StampText st) throws DatabaseException {
		log.debug("update({})", st);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.update(st);
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
	 * Active
	 */
	public static void active(int stId, boolean active) throws DatabaseException {
		log.debug("active({}, {})", stId, active);
		String qs = "update StampText st set st.active=:active where st.id=:id";
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(qs);
			q.setBoolean("active", active);
			q.setInteger("id", stId);
			q.executeUpdate();
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
		
		log.debug("active: void");
	}
	
	/**
	 * Delete
	 */
	public static void delete(int stId) throws DatabaseException {
		log.debug("delete({})", stId);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			StampText st = (StampText) session.load(StampText.class, stId);
			session.delete(st);
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
	 * Find by pk
	 */
	public static StampText findByPk(int stId) throws DatabaseException {
		log.debug("findByPk({})", stId);
		String qs = "from StampText st where st.id=:id";
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setInteger("id", stId);
			StampText ret = (StampText) q.setMaxResults(1).uniqueResult();
			log.debug("findByPk: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	/**
	 * find all mail accounts
	 */
	@SuppressWarnings("unchecked")
	public static List<StampText> findAll() throws DatabaseException {
		log.debug("findAll()");
		String qs = "from StampText st order by st.id";
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			List<StampText> ret = q.list();
			log.debug("findAll: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	/**
	 * Find by user
	 */
	@SuppressWarnings("unchecked")
	public static List<StampText> findByUser(String usrId) throws DatabaseException {
		log.debug("findByUser({})", usrId);
		String qs = "from StampText st where :user in elements(st.users)" +
			"and st.active=:active order by st.id";
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("user", usrId);
			q.setBoolean("active", true);
			List<StampText> ret = q.list();
			log.debug("findByUser: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
}
