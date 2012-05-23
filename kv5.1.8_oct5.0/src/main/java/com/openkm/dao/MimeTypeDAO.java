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

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.DatabaseException;
import com.openkm.dao.bean.MimeType;

public class MimeTypeDAO {
	private static Logger log = LoggerFactory.getLogger(MimeTypeDAO.class);

	private MimeTypeDAO() {}
	
	/**
	 * Create
	 */
	public static int create(MimeType mt) throws DatabaseException {
		log.debug("create({})", mt);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Integer id = (Integer) session.save(mt);
			MimeType mtTmp = (MimeType) session.load(MimeType.class, id);

			for (String extensions : mt.getExtensions()) {
				mtTmp.getExtensions().add(extensions);	
			}

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
	public static void update(MimeType mt) throws DatabaseException {
		log.debug("update({})", mt);
		String qs = "select mt.imageContent, mt.imageMime from MimeType mt where mt.id=:id";
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();

			if (mt.getImageContent() == null || mt.getImageContent().length() == 0) {
				Query q = session.createQuery(qs);
				q.setParameter("id", mt.getId());
				Object[] data = (Object[]) q.setMaxResults(1).uniqueResult();
				mt.setImageContent((String) data[0]);
				mt.setImageMime((String) data[1]);
			}
			
			session.update(mt);
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
	public static void delete(int mtId) throws DatabaseException {
		log.debug("delete({})", mtId);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			MimeType mt = (MimeType) session.load(MimeType.class, mtId);
			session.delete(mt);
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
	 * Delete all
	 */
	@SuppressWarnings("unchecked")
	public static void deleteAll() throws DatabaseException {
		log.debug("deleteAll()");
		String qs = "from MimeType";
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			List<MimeType> ret = session.createQuery(qs).list();
			
			for (MimeType mt : ret) {
				session.delete(mt);
			}
			
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
		
		log.debug("deleteAll: void");
	}
	
	/**
	 * Find by pk
	 */
	public static MimeType findByPk(int mtId) throws DatabaseException {
		log.debug("findByPk({})", mtId);
		String qs = "from MimeType mt where mt.id=:id";
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(qs);
			q.setInteger("id", mtId);
			MimeType ret = (MimeType) q.setMaxResults(1).uniqueResult();
			HibernateUtil.commit(tx);
			log.debug("findByPk: {}", ret);
			return ret;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	/**
	 * Find by pk
	 * 
	 * @param sort Can be "mt.id" or "mt.name".
	 */
	@SuppressWarnings("unchecked")
	public static List<MimeType> findAll(String sort) throws DatabaseException {
		log.debug("findAll()");
		String qs = "from MimeType mt order by "+sort;
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(qs);
			List<MimeType> ret = q.list();
			HibernateUtil.commit(tx);
			log.debug("findAll: {}", ret);
			return ret;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Find by name
	 */
	public static MimeType findByName(String name) throws DatabaseException {
		log.debug("findByName({})", name);
		String qs = "from MimeType mt where mt.name=:name";
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(qs);
			q.setString("name", name);
			MimeType ret = (MimeType) q.setMaxResults(1).uniqueResult();
			HibernateUtil.commit(tx);
			log.debug("findByName: {}", ret);
			return ret;
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
}
