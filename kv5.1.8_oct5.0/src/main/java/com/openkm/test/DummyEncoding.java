package com.openkm.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
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
import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyEncoding {
	private static Logger log = LoggerFactory.getLogger(DummyEncoding.class);
	private static String repHomeDir = "repotest2";
	private static Session systemSession = null;
	private static Repository repository = null;
	private static String nodoPruebaName = "nodoPrueba γλώσσα ñañeñó";

	public static void main(String[] args) throws NamingException,
			RepositoryException, IOException {
		write();
		read();
	}

	/**
	 * Write repository data
	 */
	private static void write() throws NamespaceException,
			UnsupportedRepositoryOperationException, AccessDeniedException,
			ItemExistsException, PathNotFoundException,
			NoSuchNodeTypeException, LockException, VersionException,
			ConstraintViolationException, InvalidItemStateException,
			RepositoryException, NamingException, UnsupportedEncodingException {
		log.info("*** DESTROY REPOSITORY ***");
		removeRepository();

		log.info("*** CREATE REPOSITORY ***");
		createRepository();

		log.info("*** USER LOGIN ***");
		Session userSession = login("paco", "pepe");

		log.info("*** GET MY ROOT NODE ***");
		Node rootNode = userSession.getRootNode();
		Node myRoot = rootNode.getNode("my:root");

		log.info("*** ADD A TEST NODE ***");
		Node nodoPrueba = myRoot.addNode(nodoPruebaName);
		nodoPrueba.setProperty("nombre", nodoPruebaName);
		myRoot.save();
		
		log.info("*** ADD A DOCUMENT NODE ***");
		Node fileNode = myRoot.addNode("ñañó", "nt:file");
		Node contentNode = fileNode.addNode("jcr:content", "nt:resource");
		contentNode.setProperty("jcr:data", new ByteArrayInputStream(nodoPruebaName.getBytes("UTF-8")));
		contentNode.setProperty("jcr:mimeType", "text/plain");
		contentNode.setProperty("jcr:encoding", "UTF-8");
		contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
		myRoot.save();

		log.info("*** SAY BYE ***");
		userSession.logout();
		getSystemSession().logout();
	}

	/**
	 * Read repository data
	 */
	private static void read() throws LoginException, NoSuchWorkspaceException,
			NamingException, RepositoryException, IOException {
		log.info("*** USER LOGIN ***");
		Session userSession = login("paco", "pepe");

		log.info("*** GET MY ROOT NODE ***");
		Node rootNode = userSession.getRootNode();
		Node myRoot = rootNode.getNode("my:root");

		log.info("*** VIEW TEST NODE PROPERTIES ***");
		Node nodoPrueba = myRoot.getNode(nodoPruebaName);
		log.info("** NOMBRE: "+nodoPrueba.getProperty("nombre").getString());
		
		log.info("*** VIEW DOCUMENT NODE PROPERTIES ***");
		Node fileNode = myRoot.getNode("ñañó");
		Node contentNode = fileNode.getNode("jcr:content");
		InputStream is = contentNode.getProperty("jcr:data").getStream();
		InputStreamReader isr = new InputStreamReader(is, "UTF-8");
		log.info("** DATA: "+new String(IOUtils.toByteArray(isr)));
		isr.close();
		is.close();
		
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
	public static Session login(String user, String pass)
			throws NamingException, RepositoryException, LoginException,
			NoSuchWorkspaceException {
		Repository repository = getRepository();
		Session session = repository.login(new SimpleCredentials(user, pass
				.toCharArray()), null);
		log.debug("Session: " + session);
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

			RepositoryConfig config = RepositoryConfig.create(repositoryConfig,
					repositoryHome);
			repository = RepositoryImpl.create(config);
			log.debug("*** System repository created " + repository);
		}

		return repository;
	}

	/**
	 * @return
	 * @throws LoginException
	 * @throws NoSuchWorkspaceException
	 * @throws RepositoryException
	 */
	public static Session getSystemSession() throws LoginException,
			NoSuchWorkspaceException, RepositoryException {
		if (systemSession == null) {
			// System User Session
			systemSession = repository.login(new SimpleCredentials("system", ""
					.toCharArray()), null);
			log.debug("*** System user created " + systemSession.getUserID());
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
	public static Node createRepository() throws NamespaceException,
			UnsupportedRepositoryOperationException, AccessDeniedException,
			RepositoryException, ItemExistsException, PathNotFoundException,
			NoSuchNodeTypeException, LockException, VersionException,
			ConstraintViolationException, InvalidItemStateException {
		// Initialize repository
		getRepository();
		Session systemSession = getSystemSession();

		// Namespace registration
		Workspace ws = systemSession.getWorkspace();
		ws.getNamespaceRegistry().registerNamespace("my", "http://www.guia-ubuntu.org/1.0");

		// Node creation
		Node root = systemSession.getRootNode();
		Node okmRoot = root.addNode("my:root");
		okmRoot.addMixin("mix:referenceable");
		systemSession.save();
		log.info("****** Repository created *******");
		return okmRoot;
	}
}
