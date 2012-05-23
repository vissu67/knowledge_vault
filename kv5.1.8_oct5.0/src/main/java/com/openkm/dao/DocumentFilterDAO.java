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
import com.openkm.dao.bean.DocumentFilter;
import com.openkm.dao.bean.DocumentFilterRule;

public class DocumentFilterDAO {
	private static Logger log = LoggerFactory.getLogger(DocumentFilterDAO.class);

	private DocumentFilterDAO() {}
	
	/**
	 * Create
	 */
	public static void create(DocumentFilter df) throws DatabaseException {
		log.debug("create({})", df);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.save(df);
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
	public static void update(DocumentFilter df) throws DatabaseException {
		log.debug("update({})", df);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.update(df);
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
	public static void delete(int dfId) throws DatabaseException {
		log.debug("delete({})", dfId);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			DocumentFilter df = (DocumentFilter) session.load(DocumentFilter.class, dfId);
			session.delete(df);
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
	 * find all document filter
	 */
	@SuppressWarnings("unchecked")
	public static List<DocumentFilter> findAll(boolean filterByActive) throws DatabaseException {
		log.debug("findAll({})", filterByActive);
		String qs = "from DocumentFilter df " + (filterByActive?"where df.active=:active":"") +
			" order by df.id";
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			
			if (filterByActive) {
				q.setBoolean("active", true);
			}
			
			List<DocumentFilter> ret = q.list();
			log.debug("findAll: {}", ret);
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
	public static DocumentFilter findByPk(int dfId) throws DatabaseException {
		log.debug("findByPk({})", dfId);
		String qs = "from DocumentFilter df where df.id=:id";
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setInteger("id", dfId);
			DocumentFilter ret = (DocumentFilter) q.setMaxResults(1).uniqueResult();
			log.debug("findByPk: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	/**
	 * Update
	 */
	public static void updateRule(DocumentFilterRule dfr) throws DatabaseException {
		log.debug("updateRule({})", dfr);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.update(dfr);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
		
		log.debug("updateRule: void");
	}
	
	/**
	 * Delete
	 */
	public static void deleteRule(int dfrId) throws DatabaseException {
		log.debug("deleteRule({})", dfrId);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			DocumentFilterRule dfr = (DocumentFilterRule) session.load(DocumentFilterRule.class, dfrId);
			session.delete(dfr);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
		
		log.debug("deleteRule: void");
	}
	
	/**
	 * Find by pk
	 */
	public static DocumentFilterRule findRuleByPk(int dfrId) throws DatabaseException {
		log.debug("findRuleByPk({})", dfrId);
		Session session = null;
				
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			DocumentFilterRule ret = (DocumentFilterRule) session.load(DocumentFilterRule.class, dfrId);
			
			log.debug("findRuleByPk: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
}
