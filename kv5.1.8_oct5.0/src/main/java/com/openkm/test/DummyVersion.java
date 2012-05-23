package com.openkm.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.LoginException;
import javax.jcr.NamespaceException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.ValueFormatException;
import javax.jcr.Workspace;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.jcr.version.VersionHistory;
import javax.jcr.version.VersionIterator;
import javax.naming.NamingException;

import org.apache.commons.io.FileUtils;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyVersion {
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

		log.debug("*** ADD A DOCUMENT NODE ***");
		Node fileNode = myRoot.addNode("perico", "nt:file");
		Node contentNode = fileNode.addNode("jcr:content", "nt:resource");
		contentNode.addMixin("mix:versionable");
		contentNode.setProperty("jcr:data", new ByteArrayInputStream("Texto de pruebas".getBytes()));
		contentNode.setProperty("jcr:mimeType", "text/plain");
		contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
		myRoot.save();
		
		log.debug("*** ADD A DOCUMENT VERSION ***");
		for (int i=0; i<5; i++) {
			contentNode.checkout();
			contentNode.setProperty("jcr:data", new ByteArrayInputStream(("Texto de pruebas "+i).getBytes()));
			contentNode.save();
			contentNode.checkin();
		}
		
		log.debug("*** DOCUMENT VERSION ***");
		Version baseVersion = contentNode.getBaseVersion();
		log.info(baseVersion.getName());
		
		log.debug("*** VIEW DOCUMENT VERSION HISTORY ***");
		VersionHistory vh = contentNode.getVersionHistory();
		for (VersionIterator vit = vh.getAllVersions(); vit.hasNext();) {
			Version v = vit.nextVersion();
			log.debug(v.getName());
		}

		log.debug("*** DOCUMENT VERSION RESTORE TO 1.2 ***");
		contentNode.restore("1.2", true);
		
		log.debug("*** DOCUMENT VERSION ***");
		baseVersion = contentNode.getBaseVersion();
		log.info(baseVersion.getName());
		
		log.debug("*** ADD A DOCUMENT VERSION ***");
		for (int i=0; i<5; i++) {
			contentNode.checkout();
			contentNode.setProperty("jcr:data", new ByteArrayInputStream(("Texto de pruebas "+i).getBytes()));
			contentNode.save();
			contentNode.checkin();
		}

		log.debug("*** VIEW DOCUMENT VERSION HISTORY ***");
		vh = contentNode.getVersionHistory();
		for (VersionIterator vit = vh.getAllVersions(); vit.hasNext();) {
			Version v = vit.nextVersion();
			log.debug(v.getName());
		}
		
		log.debug("*** SAY BYE ***");
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

	/**
	 * @param session
	 * @param okmRoot
	 * @param fileName
	 * @throws ItemExistsException
	 * @throws PathNotFoundException
	 * @throws NoSuchNodeTypeException
	 * @throws LockException
	 * @throws VersionException
	 * @throws ConstraintViolationException
	 * @throws RepositoryException
	 * @throws ValueFormatException
	 * @throws FileNotFoundException
	 * @throws AccessDeniedException
	 * @throws InvalidItemStateException
	 */
	public static void addDocument(Session session, Node okmRoot,
			String fileName) throws ItemExistsException, PathNotFoundException,
			NoSuchNodeTypeException, LockException, VersionException,
			ConstraintViolationException, RepositoryException,
			ValueFormatException, FileNotFoundException, AccessDeniedException,
			InvalidItemStateException {
		// Add document
		Node fileNode = okmRoot.addNode(new File(fileName).getName(), "nt:file");
		fileNode.addMixin("mix:referenceable");
		fileNode.addMixin("mix:lockable");
		fileNode.addMixin("mix:versionable");
		Node resNode = fileNode.addNode("jcr:content", "nt:resource");
		resNode.setProperty("jcr:mimeType", getMime(fileName));
		resNode.setProperty("jcr:data", new FileInputStream(fileName));
		resNode.setProperty("jcr:lastModified", Calendar.getInstance());
		session.save();
		log.info("File '"+fileName+"' added.");
	}

	/**
	 * @param fileName
	 * @return
	 */
	public static String getMime(String fileName) {
		if (fileName.endsWith(".doc")) {
			return "application/msword";
		} else if (fileName.endsWith(".odt")) {
			return "application/vnd.oasis.opendocument.text";
		} else if (fileName.endsWith(".pdf")) {
			return "application/pdf";
		} else if (fileName.endsWith(".txt")) {
			return "text/plain";
		}

		return "application/octect-stream";
	}

	/**
	 * @param session
	 * @param words
	 * @throws RepositoryException
	 * @throws InvalidQueryException
	 * @throws UnsupportedRepositoryOperationException
	 * @throws ItemNotFoundException
	 * @throws AccessDeniedException
	 */
	public static void search(Session session, String words)
			throws RepositoryException, InvalidQueryException,
			UnsupportedRepositoryOperationException, ItemNotFoundException,
			AccessDeniedException {
		// Search
		String statement = "/jcr:root/my:root//element(*,nt:resource)[jcr:contains(.,'" + words + "')]";
		Workspace workspace = session.getWorkspace();
		QueryManager queryManager = workspace.getQueryManager();
		Query query = queryManager.createQuery(statement, javax.jcr.query.Query.XPATH);
		QueryResult result = query.execute();

		log.info("Search results:");
		for (NodeIterator it = result.getNodes(); it.hasNext();) {
			Node sNode = (Node) it.next();
			log.info(" * "+sNode.getParent().getName());
		}
	}
}
