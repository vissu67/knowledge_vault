package com.openkm.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.dao.HibernateUtil;

public class Test {
	private static Logger log = LoggerFactory.getLogger(Test.class);
	
	/**
	 * Only for testing purposes
	 */
	public static void main(String[] args) throws Exception {
		log.info("Generate database schema & initial data");
		HibernateUtil.generateDatabase("org.hibernate.dialect.Oracle10gDialect");
	}
}
