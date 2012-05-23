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

public class DummyLockToken {
	private static Logger log = LoggerFactory.getLogger(Dummy.class);
	private static String repHomeDir = "repotest2";
	private static Session systemSession = null;
	private static Repository repository = null;
	
	public static void main(String[] args) throws NamingException,
			RepositoryException, FileNotFoundException {
		log.debug("*** DESTROY REPOSITORY ***");
		removeRepository();
		
		log.debug("*** CREATE REPOSITORY ***");
		createRepository();
		
		log.debug("*** USER LOGIN ***");
		Session userSession = login("paco", "pepe");
		
		log.debug("*** GET MY ROOT NODE ***");
		Node rootNode = userSession.getRootNode();
		Node myRoot = rootNode.getNode("my:root");
				
		log.debug("*** BEGIN: ADD A DOCUMENT NODE ***");
		Node fileNode = myRoot.addNode("perico", "nt:file");
		fileNode.addMixin("mix:lockable");
		Node contentNode = fileNode.addNode("jcr:content", "nt:resource");
		contentNode.setProperty("jcr:data", new ByteArrayInputStream("Texto de pruebas".getBytes()));
		contentNode.setProperty("jcr:mimeType", "text/plain");
		contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
		myRoot.save();
		log.debug("*** END: ADD A DOCUMENT NODE ***");
		

		log.debug("*** LOCK NODE ***");
		fileNode.lock(true, false);
		String[] tokens = userSession.getLockTokens();
		for (int i=0; i<tokens.length; i++) {
			log.debug("Token: "+tokens[i]);
		}
		
		log.debug("*** UNLOCK NODE ***");
		fileNode.unlock();
		tokens = userSession.getLockTokens();
		for (int i=0; i<tokens.length; i++) {
			log.debug("Token: "+tokens[i]);
		}

		log.debug("*** SAY BYE ***");
		userSession.logout();
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
		log.debug("Session: "+session);
		return session;
	}

	/**
	 * @return
	 * @throws RepositoryException
	 */
	public static Repository getRepository() throws RepositoryException {
		if (repository == null) {
			// Repository config
			String repositoryConfig = "repository2.xml";
			String repositoryHome = "repotest2";
			
			RepositoryConfig config = RepositoryConfig.create(repositoryConfig, repositoryHome);
			repository = RepositoryImpl.create(config);
			log.debug("*** System repository created "+repository);
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
			log.debug("*** System user created "+systemSession.getUserID());				
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
