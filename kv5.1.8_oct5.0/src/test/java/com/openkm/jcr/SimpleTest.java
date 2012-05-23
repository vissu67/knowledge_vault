package com.openkm.jcr;

import java.io.File;
import java.io.IOException;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.TransientRepository;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleTest extends TestCase {
	private static Logger log = LoggerFactory.getLogger(SimpleTest.class);

	public SimpleTest(String name) {
		super(name);
	}

	public static void main(String[] args) throws Exception {
		SimpleTest test = new SimpleTest("main");
		test.setUp();
		test.testBasic();
		test.testSimple();
		test.tearDown();
	}

	@Override
	protected void setUp() {
		log.info("setUp()");
		log.info("Delete repository: {}", Config.REPOSITORY_HOME);
		FileUtils.deleteQuietly(new File(Config.REPOSITORY_HOME));
	}

	@Override
	protected void tearDown() {
		log.info("tearDown()");
		log.info("Delete repository: {}", Config.REPOSITORY_HOME);
		FileUtils.deleteQuietly(new File(Config.REPOSITORY_HOME));
	}

	public void testBasic() throws IOException, LoginException, RepositoryException {
		log.info("testBasic()");
		Repository repository = new TransientRepository(Config.REPOSITORY_CONFIG, Config.REPOSITORY_HOME);
		Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
		Node rootNode = session.getRootNode();
		Node newNode = rootNode.addNode("new node");
		log.info("Restricted node: " + newNode.getPath());
		assertEquals(newNode.getPath(), "/new node");
		rootNode.save();
		session.logout();
	}
	
	public void testSimple() throws IOException, LoginException, RepositoryException {
		log.info("testSimple()");
		RepositoryConfig config = RepositoryConfig.create(Config.REPOSITORY_CONFIG, Config.REPOSITORY_HOME);
		Repository repository = RepositoryImpl.create(config);
		Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray())); 
		Node rootNode = session.getRootNode();
		Node newNode = rootNode.addNode("new node");
		log.info("Restricted node: " + newNode.getPath());
		assertEquals(newNode.getPath(), "/new node");
		rootNode.save();
		session.logout();
		((RepositoryImpl)repository).shutdown();
	}	
}
