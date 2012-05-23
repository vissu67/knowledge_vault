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
import com.openkm.extension.dao.bean.Contact;

/**
 * ContactDAO
 * 
 * @author jllort
 *
 */
public class ContactDAO {
	private static Logger log = LoggerFactory.getLogger(MessageDAO.class);

	private ContactDAO() {}
	
	/**
	 * create
	 *
	 * @param contact
	 * @throws DatabaseException
	 */
	public static int create(Contact contact) throws DatabaseException {
		log.debug("create()");
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Integer id = (Integer) session.save(contact);
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
	 * delete 
	 * 
	 * @param id
	 * @throws DatabaseException
	 */
	public static void delete(int id) throws DatabaseException {
		log.debug("delete({})", id);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Contact contact = (Contact) session.load(Contact.class, id);
			session.delete(contact);
			HibernateUtil.commit(tx);
		} catch(HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
		
		log.debug("delete: void");
	}
	
	/**
	 * Find users who sent an message
	 */
	@SuppressWarnings("unchecked")
	public static List<Contact> findByUuid(String uuid) throws DatabaseException {
		log.debug("findByUuid({})", uuid);
		String qs = "select con from Contact con where :uuid in elements(con.uuids) order by con.name";
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("uuid", uuid);
			List<Contact> ret =  q.list();
			log.debug("findByUuid: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	/**
	 * Find users whom sent an message
	 */
	@SuppressWarnings("unchecked")
	public static List<Contact> findAll() throws DatabaseException {
		log.debug("findAll({})");
		String qs = "from Contact con order by con.name";
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			List<Contact> ret =  q.list();
			log.debug("findAll: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	/**
	 * Find users whom sent an message
	 */
	@SuppressWarnings("unchecked")
	public static List<Contact> findAllFiltered(String uuid) throws DatabaseException {
		log.debug("findAll({})");
		String qs = "from Contact con where :uuid not in elements(con.uuids) order by con.name";
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("uuid", uuid);
			List<Contact> ret =  q.list();
			log.debug("findAll: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	/**
	 * update 
	 * 
	 * @param contact
	 * @throws DatabaseException
	 */
	public static void update(Contact contact) throws DatabaseException {
		log.debug("update({})", contact);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.update(contact);
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
	 * Find by pk
	 */
	public static Contact findByPk(int id) throws DatabaseException {
		log.debug("findByPk({})", id);
		String qs = "from Contact con where con.id=:id";
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setInteger("id", id);
			Contact ret = (Contact) q.setMaxResults(1).uniqueResult();
			log.debug("findByPk: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	/**
	 * findByOrigin
	 * 
	 * @param externalid
	 * @param origin
	 * @return
	 * @throws DatabaseException
	 */
	public static Contact findByOrigin(String externalId, String origin) throws DatabaseException {
		log.debug("findByOrigin({},{})", externalId, origin);
		String qs = "from Contact con where con.externalId=:externalId and con.origin=:origin";
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("externalId", externalId);
			q.setString("origin", origin);
			Contact ret = (Contact) q.setMaxResults(1).uniqueResult();
			log.debug("findByOrigin: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
}