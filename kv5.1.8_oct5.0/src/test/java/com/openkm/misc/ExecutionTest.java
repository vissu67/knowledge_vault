package com.openkm.misc;

import java.io.File;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.Cron;
import com.openkm.util.ExecutionUtils;

public class ExecutionTest extends TestCase {
	private static Logger log = LoggerFactory.getLogger(ExecutionTest.class);
	private static final String BASE_DIR = "src/test/resources/execution";
	private static final String RESULT = "Hola, mundo!";
	private static final String EMPTY = "";
	
	public ExecutionTest(String name) {
		super(name);
	}

	public static void main(String[] args) throws Exception {
		ExecutionTest test = new ExecutionTest("main");
		test.setUp();
		test.testBeanShell();
		test.testJar();
		test.tearDown();
	}

	public void testBeanShell() throws Exception {
		log.debug("testBeanShell()");
		File bsh = new File(BASE_DIR + "/beanShellSample.bsh");
		Object[] result = ExecutionUtils.runScript(bsh);
		assertEquals(RESULT, result[0]);
		assertEquals(EMPTY, result[1]);
		assertEquals(EMPTY, result[2]);
	}
	
	public void testJar() throws Exception {
		log.debug("testJar()");
		File jar = new File(BASE_DIR + "/JarSample.jar");
		Object result = ExecutionUtils.runJar(jar, Cron.CRON_TASK);
		assertEquals(RESULT, result);
	}
}
