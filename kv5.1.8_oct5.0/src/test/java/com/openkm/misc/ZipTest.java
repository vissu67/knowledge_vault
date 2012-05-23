package com.openkm.misc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import junit.framework.TestCase;

import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test zip filename encodings
 */
public class ZipTest extends TestCase {
	private static Logger log = LoggerFactory.getLogger(ZipTest.class);

	public ZipTest(String name) {
		super(name);
	}

	public static void main(String[] args) throws Exception {
		ZipTest test = new ZipTest("main");
		test.setUp();
		test.testJava();
		test.testApache();
		test.tearDown();
	}

	@Override
	protected void setUp() throws Exception {
		log.debug("setUp()");
	}

	@Override
	protected void tearDown() throws Exception {
		log.debug("tearDown()");
		//FileUtils.deleteQuietly(zip);
	}

	public void testJava() throws IOException {
		log.debug("testJava()");
		File zip = File.createTempFile("java_", ".zip");
		
		// Create zip
		FileOutputStream fos = new FileOutputStream(zip);
		ZipOutputStream zos = new ZipOutputStream(fos);
		zos.putNextEntry(new ZipEntry("coñeta"));
		zos.closeEntry();
		zos.close();
		
		// Read zip
		FileInputStream fis = new FileInputStream(zip);
		ZipInputStream zis = new ZipInputStream(fis);
		ZipEntry ze = zis.getNextEntry();
		System.out.println(ze.getName());
		assertEquals(ze.getName(), "coñeta");
		zis.close();
	}

	public void testApache() throws IOException, ArchiveException {
		log.debug("testApache()");
		File zip = File.createTempFile("apache_", ".zip");
		
		// Create zip
		FileOutputStream fos = new FileOutputStream(zip);
		ArchiveOutputStream aos = new ArchiveStreamFactory().createArchiveOutputStream("zip", fos);
		aos.putArchiveEntry(new ZipArchiveEntry("coñeta"));
		aos.closeArchiveEntry();
		aos.close();
		
		// Read zip
		FileInputStream fis = new FileInputStream(zip);
		ArchiveInputStream ais = new ArchiveStreamFactory().createArchiveInputStream("zip", fis);
		ZipArchiveEntry zae = (ZipArchiveEntry) ais.getNextEntry();
		assertEquals(zae.getName(), "coñeta");
		ais.close();
	}
}
