package com.openkm.jcr;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import javax.jcr.ItemExistsException;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.core.TransientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CleanUnusedTest extends TestCase {
	private static Logger log = LoggerFactory.getLogger(CleanUnusedTest.class);

	public CleanUnusedTest(String name) {
		super(name);
	}

	public static void main(String[] args) throws Exception {
		CleanUnusedTest test = new CleanUnusedTest("main");
		test.setUp();
		test.testVersionHistory();
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

	public void testVersionHistory() throws IOException, LoginException, RepositoryException {
		log.info("testVersionHistory()");
		Repository repository = new TransientRepository(Config.REPOSITORY_CONFIG, Config.REPOSITORY_HOME);
		Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
		Node rootNode = session.getRootNode();
		Node fileNode = createFile(rootNode, "new file", "Alegoría a la chirigota del Yuyu");
		fileNode.addMixin(JcrConstants.MIX_VERSIONABLE);
		rootNode.save();
				
		Version version = fileNode.checkin();
		log.info("Created version: {}", version.getName());
		
		VersionHistory vh = fileNode.getVersionHistory();
		String vhPath = vh.getPath();
		log.info("VersionHistory path: {}", vhPath);
		assertTrue(rootNode.hasNode(vh.getPath().substring(1)));
		
		for (VersionIterator vit = vh.getAllVersions(); vit.hasNext(); ) {
			log.info("Version: {}", vit.nextVersion().getPath());
		}

		// First remove the node
		fileNode.remove();
		rootNode.save();
		
		// And now remove the versions
		for (VersionIterator vit = vh.getAllVersions(); vit.hasNext(); ) {
			Version ver = vit.nextVersion();
			
			// The rootVersion is not a "real" version node.
			if (!ver.getName().equals(JcrConstants.JCR_ROOTVERSION)) {
				log.info("Removing version: {}", ver.getName());
				vh.removeVersion(ver.getName());
			}
		}
		
		assertFalse(rootNode.hasNode(vhPath.substring(1)));
		session.logout();
	}

	protected Node createFile(Node parent, String name, String content) throws ItemExistsException,
			PathNotFoundException, NoSuchNodeTypeException, LockException, VersionException,
			ConstraintViolationException, RepositoryException {
		Node fileNode = parent.addNode(name, JcrConstants.NT_FILE);
		Node resNode = fileNode.addNode(JcrConstants.JCR_CONTENT, JcrConstants.NT_RESOURCE);
		resNode.setProperty(JcrConstants.JCR_MIMETYPE, "text/plain");
		resNode.setProperty(JcrConstants.JCR_ENCODING, "UTF-8");
		resNode.setProperty(JcrConstants.JCR_DATA, new ByteArrayInputStream(content.getBytes()));
		resNode.setProperty(JcrConstants.JCR_LASTMODIFIED, Calendar.getInstance());
		return fileNode;
	}
}
