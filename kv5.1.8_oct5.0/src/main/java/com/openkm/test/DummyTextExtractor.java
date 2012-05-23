package com.openkm.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.LoginException;
import javax.jcr.NamespaceException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Workspace;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;
import javax.naming.NamingException;

import org.apache.commons.io.FileUtils;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyTextExtractor {
	private static Logger log = LoggerFactory.getLogger(DummyTextExtractor.class);
	private static String repHomeDir = "repository";
	private static Session systemSession = null;
	private static Repository repository = null;
	
	public static void main(String[] args) throws NamingException,
			RepositoryException, FileNotFoundException, InterruptedException {
		log.info("*** DESTROY REPOSITORY ***");
		removeRepository();
		
		log.info("*** CREATE REPOSITORY ***");
		createRepository();
		
		log.info("*** USER LOGIN ***");
		Session userSession = login("paco", "pepe");
		
		log.info("*** GET MY ROOT NODE ***");
		Node rootNode = userSession.getRootNode();
		Node myRoot = rootNode.getNode("my:root");
				
		log.info("*** ADD A DOCUMENT NODE ***");
		Node fileNode = myRoot.addNode("perico.jpg", "nt:file");
		Node contentNode = fileNode.addNode("jcr:content", "nt:resource");
		contentNode.addMixin("mix:versionable");
		
		log.info("*** ADD DOCUMENT NODE PROPERTIES ***");
		contentNode.setProperty("jcr:data", new ByteArrayInputStream("Texto de pruebas".getBytes()));
		contentNode.setProperty("jcr:mimeType", "image/jpeg");
		contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
		myRoot.save();
		
		log.info("*** CHECKIN ***");
		contentNode.checkin();
		
		log.info("*** CHECKOUT ***");
		contentNode.checkout();
		
		log.info("*** SAY BYE ***");
		userSession.logout();
		getSystemSession().logout();
	}

	/**
	 * 
	 */
	private static void removeRepository() {
		try {
			FileUtils.deleteDirectory(new File(repHomeDir));
		} catch (IOException e) {
			System.err.println("No previous repo");
		}
	}

	/**
	 * @return
	 * @throws NamingException
	 * @throws RepositoryException
	 * @throws LoginException
	 * @throws NoSuchWorkspaceException
	 */
	public static Session login(String user, String pass) throws NamingException,
		RepositoryException, LoginException, NoSuchWorkspaceException {
		Repository repository = getRepository();
		Session session = repository.login(new SimpleCredentials(user, pass.toCharArray()), null);
		log.info("Session: "+session);
		return session;
	}

	/**
	 * @return
	 * @throws RepositoryException
	 */
	public static Repository getRepository() throws RepositoryException {
		if (repository == null) {
			// Repository config
			String repositoryConfig = "repositoryTE.xml";
			String repositoryHome = repHomeDir;
			
			RepositoryConfig config = RepositoryConfig.create(repositoryConfig, repositoryHome);
			repository = RepositoryImpl.create(config);
			log.info("*** System repository created "+repository);
		}
		
		return repository;
	}
	
	/**
	 * @return
	 * @throws LoginException
	 * @throws NoSuchWorkspaceException
	 * @throws RepositoryException
	 */
	public static Session getSystemSession() throws LoginException, NoSuchWorkspaceException, RepositoryException {
		if (systemSession == null) {
			// System User Session
			systemSession = repository.login(new SimpleCredentials("system", "".toCharArray()), null);
			log.info("*** System user created "+systemSession.getUserID());				
		}
		
		return systemSession;
	}
	
	/**
	 * @param session
	 * @return
	 * @throws NamespaceException
	 * @throws UnsupportedRepositoryOperationException
	 * @throws AccessDeniedException
	 * @throws RepositoryException
	 * @throws ItemExistsException
	 * @throws PathNotFoundException
	 * @throws NoSuchNodeTypeException
	 * @throws LockException
	 * @throws VersionException
	 * @throws ConstraintViolationException
	 * @throws InvalidItemStateException
	 */
	public static Node createRepository()
			throws NamespaceException, UnsupportedRepositoryOperationException,
			AccessDeniedException, RepositoryException, ItemExistsException,
			PathNotFoundException, NoSuchNodeTypeException, LockException,
			VersionException, ConstraintViolationException,
			InvalidItemStateException {
		// Initialize repository
		//Repository repository = getRepository();
		Session systemSession = getSystemSession();
		
		// Namespace registration
		Workspace ws = systemSession.getWorkspace();
		ws.getNamespaceRegistry().registerNamespace("my", "http://www.guia-ubuntu.org/1.0");

		// Node creation
		Node root = systemSession.getRootNode();
		Node okmRoot = root.addNode("my:root", "nt:folder");
		okmRoot.addMixin("mix:referenceable");
		systemSession.save();
		log.info("****** Repository created *******");
		return okmRoot;
	}
}
