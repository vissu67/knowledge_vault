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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;

import org.apache.commons.io.IOUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.hql.QueryTranslator;
import org.hibernate.hql.QueryTranslatorFactory;
import org.hibernate.hql.ast.ASTQueryTranslatorFactory;
import org.hibernate.jdbc.Work;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.Config;
import com.openkm.dao.bean.DatabaseMetadataSequence;
import com.openkm.dao.bean.DatabaseMetadataType;
import com.openkm.dao.bean.DatabaseMetadataValue;
import com.openkm.util.DatabaseDialectAdapter;
import com.openkm.util.EnvironmentDetector;

/**
 * Show SQL => Logger.getLogger("org.hibernate.SQL").setThreshold(Level.INFO);
 * JBPM Integration => org.jbpm.db.JbpmSessionFactory
 * 
 * @author pavila
 */
public class HibernateUtil {
	private static Logger log = LoggerFactory.getLogger(HibernateUtil.class);
	private static SessionFactory sessionFactory;
	public static String HBM2DDL_CREATE = "create";
	
	/**
	 * Disable constructor to guaranty a single instance
	 */
	private HibernateUtil() {}
	
	/**
	 * Get instance
	 */
	public static SessionFactory getSessionFactory() {
		return getSessionFactory(Config.HIBERNATE_HBM2DDL);
	}
	
	/**
	 * Get instance
	 */
	public static SessionFactory getSessionFactory(String hbm2ddl) {
		if (sessionFactory == null) {
			try {
				AnnotationConfiguration ac = new AnnotationConfiguration();
				
				// Add annotated beans
				ac.addAnnotatedClass(DatabaseMetadataType.class);
				ac.addAnnotatedClass(DatabaseMetadataValue.class);
				ac.addAnnotatedClass(DatabaseMetadataSequence.class);
				
				// Configure Hibernate
				Configuration cfg = ac.configure();
				cfg.setProperty("hibernate.dialect", Config.HIBERNATE_DIALECT);
				cfg.setProperty("hibernate.connection.datasource", Config.HIBERNATE_DATASOURCE);
				cfg.setProperty("hibernate.hbm2ddl.auto", hbm2ddl);
				cfg.setProperty("hibernate.show_sql", Config.HIBERNATE_SHOW_SQL);
				cfg.setProperty("hibernate.generate_statistics", Config.HIBERNATE_STATISTICS);
				
				// Show configuration
				log.info("Hibernate 'hibernate.dialect' = {}", cfg.getProperty("hibernate.dialect"));
				log.info("Hibernate 'hibernate.connection.datasource' = {}", cfg.getProperty("hibernate.connection.datasource"));
				log.info("Hibernate 'hibernate.hbm2ddl.auto' = {}", cfg.getProperty("hibernate.hbm2ddl.auto"));
				log.info("Hibernate 'hibernate.show_sql' = {}", cfg.getProperty("hibernate.show_sql"));
				log.info("Hibernate 'hibernate.generate_statistics' = {}", cfg.getProperty("hibernate.generate_statistics"));
				
				sessionFactory = cfg.buildSessionFactory();
				
				if (HBM2DDL_CREATE.equals(hbm2ddl)) {
					log.info("Executing specific import for: {}", Config.HIBERNATE_DIALECT);
					InputStream is = Config.getResourceAsStream("default.sql");
					String adapted = DatabaseDialectAdapter.dialectAdapter(is, Config.HIBERNATE_DIALECT);
					executeImport(new StringReader(adapted));
				}
			} catch (HibernateException e) {
				log.error(e.getMessage(), e);
				throw new ExceptionInInitializerError(e);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
				throw new ExceptionInInitializerError(e);
			}
		}
		
		return sessionFactory;
	}
	
	/**
	 * Close factory
	 */
	public static void closeSessionFactory() {
		if (sessionFactory != null) {
			sessionFactory.close();
			sessionFactory = null;
		}
	}
	
	/**
	 * Close session
	 */
	public static void close(Session session) {
		if (session != null && session.isOpen()) {
			session.close();
		}
	}
	
	/**
	 * Commit transaction
	 */
	public static void commit(Transaction tx) {
		if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
			tx.commit();
		}
	}
	
	/**
	 * Rollback transaction
	 */
	public static void rollback(Transaction tx) {
		if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
			tx.rollback();
		}
	}

	/**
	 * Convert from Blob to byte array
	 */
	public static byte[] toByteArray(Blob fromImageBlob) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		try {
			byte buf[] = new byte[4000];
			int dataSize;
			InputStream is = fromImageBlob.getBinaryStream();
			
			try {
				while((dataSize = is.read(buf)) != -1) {
					baos.write(buf, 0, dataSize);
				}
			} finally {
				if(is != null) {
					is.close();
				}
			}
			
			return baos.toByteArray();
		} catch (Exception e) {
		}
		
		return null;
	}
	
	/**
	 * HQL to SQL translator
	 */
	public static String toSql(String hql) {
		if (hql != null && hql.trim().length() > 0) {
			final QueryTranslatorFactory qtf = new ASTQueryTranslatorFactory();
			final SessionFactoryImplementor sfi = (SessionFactoryImplementor) sessionFactory;
			final QueryTranslator translator = qtf.createQueryTranslator(hql, hql, Collections.EMPTY_MAP, sfi);
			translator.compile(Collections.EMPTY_MAP, false);
			return translator.getSQLString(); 
		}
		
		return null;
	}
	
	/**
	 * Load specific database import
	 */
	private static void executeImport(final Reader rd) {
		Session session = null;
		Transaction tx = null;
		
		try {
			session = sessionFactory.openSession();
			tx = session.beginTransaction();
			
			session.doWork(
				new Work() {
					@Override
					public void execute(Connection con) throws SQLException {
						try {
							for (HashMap<String, String> error: LegacyDAO.executeScript(con, rd)) {
								log.error("Error during import script execution at line {}: {} [ {} ]",
										new Object[] { error.get("ln"), error.get("msg"), error.get("sql") });
							}
						} catch (IOException e) {
							log.error(e.getMessage(), e);
						} finally {
							IOUtils.closeQuietly(rd);
						}
					}
				}
			);
			
			commit(tx);
		} catch (Exception e) {
			rollback(tx);
			log.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Generate database schema and initial data for a defined dialect
	 */
	public static void generateDatabase(String dialect) throws IOException {
		AnnotationConfiguration ac = new AnnotationConfiguration();
        
        // Add annotated beans
        ac.addAnnotatedClass(DatabaseMetadataType.class);
        ac.addAnnotatedClass(DatabaseMetadataValue.class);
        ac.addAnnotatedClass(DatabaseMetadataSequence.class);
        
        // Configure Hibernate
        log.info("Exporting Database Schema...");
        String dbSchema = EnvironmentDetector.getUserHome() + "/schema.sql";
        Configuration cfg = ac.configure();
        cfg.setProperty("hibernate.dialect", dialect);
        SchemaExport se = new SchemaExport(cfg);
        se.setOutputFile(dbSchema);
        se.setDelimiter(";");
        se.setFormat(false);
        se.create(false, false);
        log.info("Database Schema exported to {}", dbSchema);
        
        String initialData = new File("").getAbsolutePath() + "/src/main/resources/default.sql";
        log.info("Exporting Initial Data from '{}'...", initialData);
        String initData = EnvironmentDetector.getUserHome() + "/data.sql";
        FileInputStream fis = new FileInputStream(initialData);
        String ret = DatabaseDialectAdapter.dialectAdapter(fis, dialect);
        FileWriter fw = new FileWriter(initData);
        IOUtils.write(ret, fw);
        fw.flush();
        fw.close();
        log.info("Initial Data exported to {}", initData);
	}
}
