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

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.openkm.bean.StoredFile;
import com.openkm.core.DatabaseException;
import com.openkm.dao.bean.Config;
import com.openkm.jcr.JCRUtils;
import com.openkm.util.SecureStore;

public class ConfigDAO  {
	private static Logger log = LoggerFactory.getLogger(ConfigDAO.class);
	private ConfigDAO() {}
	
	/**
	 * Create activity
	 */
	public static void create(Config cfg) throws DatabaseException {
	    Session session = null;
	    Transaction tx = null;
	    
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
	    	session.save(cfg);
	    	HibernateUtil.commit(tx);
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
	public static void update(Config cfg) throws DatabaseException {
		log.debug("update({})", cfg);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.update(cfg);
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
	public static void delete(String key) throws DatabaseException {
		log.debug("delete({})", key);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Config mt = (Config) session.load(Config.class, key);
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
	 * Find by pk
	 */
	public static Config findByPk(String key) throws DatabaseException {
		log.debug("findByPk({})", key);
		String qs = "from Config cfg where cfg.key=:key";
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(qs);
			q.setString("key", key);
			Config ret = (Config) q.setMaxResults(1).uniqueResult();
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
	 * Find by pk with a default value
	 */
	private static String getProperty(String key, String value, String type) throws DatabaseException {
		log.debug("getProperty({}, {}, {})", new Object[] { key, value, type });
		String qs = "from Config cfg where cfg.key=:key";
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(qs);
			q.setString("key", key);
			Config ret = (Config) q.setMaxResults(1).uniqueResult();
			
			if (ret == null) {
				ret = new Config();
				ret.setKey(key);
				ret.setType(type);
				ret.setValue(value);
				session.save(ret);
			} else if (ret.getValue() == null) {
				// For Oracle '' are like NULL
				ret.setValue("");
			}
			
			HibernateUtil.commit(tx);
			log.debug("getProperty: {}", ret);
			return ret.getValue();
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	/**
	 * Find by pk with a default value
	 */
	public static String getString(String key, String value) throws DatabaseException {
		return getProperty(key, value, Config.STRING);
	}
	
	/**
	 * Find by pk with a default value
	 */
	public static String getText(String key, String value) throws DatabaseException {
		return getProperty(key, value, Config.TEXT);
	}
	
	/**
	 * Find by pk with a default value
	 */
	public static boolean getBoolean(String key, boolean value) throws DatabaseException {
		return "true".equalsIgnoreCase(getProperty(key, Boolean.toString(value), Config.BOOLEAN));
	}
	
	/**
	 * Find by pk with a default value
	 */
	public static int getInteger(String key, int value) throws DatabaseException {
		return Integer.parseInt(getProperty(key, Integer.toString(value), Config.INTEGER));
	}
	
	/**
	 * Find by pk with a default value
	 */
	public static int getLong(String key, long value) throws DatabaseException {
		return Integer.parseInt(getProperty(key, Long.toString(value), Config.LONG));
	}
	
	/**
	 * Find by pk with a default value
	 */
	public static StoredFile getFile(String key, String path) throws DatabaseException, IOException {
		InputStream is = null;
		
		try {
			is = Config.class.getResourceAsStream(path);
			StoredFile stFile = new StoredFile();
			stFile.setContent(SecureStore.b64Encode(IOUtils.toByteArray(is)));
			stFile.setName(JCRUtils.getName(path));
			stFile.setMime(com.openkm.core.Config.mimeTypes.getContentType(stFile.getName()));
			String value = getProperty(key, new Gson().toJson(stFile), Config.FILE);
			return new Gson().fromJson(value, StoredFile.class);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}
	
	/**
	 * Find by pk
	 */
	@SuppressWarnings("unchecked")
	public static List<Config> findAll() throws DatabaseException {
		log.debug("findAll()");
		String qs = "from Config cfg order by cfg.key";
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(qs);
			List<Config> ret = q.list();
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
}
