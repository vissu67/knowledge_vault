package com.openkm.api;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthTest extends TestCase {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(AuthTest.class);

	public AuthTest(String name) {
		super(name);
	}

	public static void main(String[] args) throws Exception {
		AuthTest test = new AuthTest("main");
		test.setUp();
		//test.testLogin();
		test.tearDown();
	}

	@Override
	protected void setUp() {
		//Config.load();
		//log.info("Delete repository: " + Config.HOME_DIR + File.separator + Config.REPOSITORY_HOME);
		//FileUtils.deleteQuietly(new File(Config.HOME_DIR + File.separator + Config.REPOSITORY_HOME));
	}

	@Override
	protected void tearDown() {
		//DirectRepositoryModule.shutdown();
		//log.info("Delete repository: " + Config.HOME_DIR + File.separator + Config.REPOSITORY_HOME);
		//FileUtils.deleteQuietly(new File(Config.HOME_DIR + File.separator + Config.REPOSITORY_HOME));
	}

	public void testNone() {
	}
	
	//public void testLogin() throws UserAlreadyLoggerException, AccessDeniedException, RepositoryException {
		//OKMAuth okmAuth = OKMAuth.getInstance();
		//String token = okmAuth.login("okmAdmin", "admin");
		//assertNotNull(token);
		//okmAuth.logout(token);
	//}
}
