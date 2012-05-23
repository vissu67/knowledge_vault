/**
 *  OpenKM, Open Document Management System (http://www.openkm.com)
 *  Copyright (c) 2006-2011  Paco Avila & Josep Llort
 *
 *  No bytes were intentionally harmed during the development of this application.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package com.openkm.module.direct;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

import javax.jcr.ItemExistsException;
import javax.jcr.LoginException;
import javax.jcr.NamespaceException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.jcr.Workspace;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;
import javax.naming.NamingException;

import org.apache.commons.io.FileUtils;
import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.ConfigurationException;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.apache.jackrabbit.core.config.WorkspaceConfig;
import org.apache.jackrabbit.core.nodetype.InvalidNodeTypeDefException;
import org.apache.jackrabbit.core.nodetype.NodeTypeDef;
import org.apache.jackrabbit.core.nodetype.NodeTypeManagerImpl;
import org.apache.jackrabbit.core.nodetype.NodeTypeRegistry;
import org.apache.jackrabbit.core.nodetype.compact.CompactNodeTypeDefReader;
import org.apache.jackrabbit.core.nodetype.compact.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Document;
import com.openkm.bean.Folder;
import com.openkm.bean.Permission;
import com.openkm.bean.PropertyGroup;
import com.openkm.bean.Repository;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.JcrSessionManager;
import com.openkm.core.OKMSystemSession;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.jcr.JCRUtils;
import com.openkm.module.RepositoryModule;
import com.openkm.module.base.BaseDocumentModule;
import com.openkm.module.base.BaseFolderModule;
import com.openkm.util.MailUtils;
import com.openkm.util.UUIDGenerator;
import com.openkm.util.UserActivity;
import com.openkm.util.WarUtils;

public class DirectRepositoryModule implements RepositoryModule {
	private static Logger log = LoggerFactory.getLogger(DirectRepositoryModule.class);
	private static javax.jcr.Repository repository = null;
	private static Session systemSession = null;

	/**
	 * Cache the repository information
	 * 
	 * @return The actual repository.
	 * @throws NamingException
	 * @throws javax.jcr.RepositoryException
	 */
	public synchronized static javax.jcr.Repository getRepository() throws javax.jcr.RepositoryException {
		log.debug("getRepository()");
		String repConfig = Config.REPOSITORY_CONFIG;
		String repHome = null;
		WorkspaceConfig wc = null;
		
		if (repository == null) {
			// Allow absolute repository path
			if ((new File(Config.REPOSITORY_HOME)).isAbsolute()) {
				repHome = Config.REPOSITORY_HOME;
			} else {
				repHome = Config.HOME_DIR + File.separator + Config.REPOSITORY_HOME;
			}
			
			// Repository configuration
			try {
				RepositoryConfig config = RepositoryConfig.create(repConfig, repHome);
				wc = config.getWorkspaceConfig(config.getDefaultWorkspaceName());
				repository = RepositoryImpl.create(config);
			} catch (ConfigurationException e) {
				log.error(e.getMessage(), e);
				throw e;
			} catch (javax.jcr.RepositoryException e) {
				log.error(e.getMessage(), e);
				throw e;
			}
		}
		
		// Creation of a top access level SYSTEM. Needed by the AccessManager.
		if (systemSession == null) {
			// System User Session
			try {
				systemSession = OKMSystemSession.create((RepositoryImpl)repository, wc);
			} catch (LoginException e) {
				log.error(e.getMessage(), e);
				throw e;
			} catch (NoSuchWorkspaceException e) {
				log.error(e.getMessage(), e);
				throw e;
			} catch (javax.jcr.RepositoryException e) {
				log.error(e.getMessage(), e);
				throw e;
			}
		}

		log.debug("getRepository: " + repository);
		return repository;
	}
	
	/**
	 * Close repository and free the lock 
	 */
	public synchronized static void shutdown() {
		log.debug("shutdownRepository()");
		
		if (systemSession != null && systemSession.isLive()) {
			systemSession.logout();
		}
		
		systemSession = null;
        ((RepositoryImpl)repository).shutdown();
        repository = null;
        log.debug("shutdownRepository: void");
    }
	
	/**
	 * Get the System User Session to perform unsecured operations.
	 * 
	 * @return The System User Session.
	 */
	public static Session getSystemSession() {
		log.debug("getSystemSession()");
				
		if (systemSession != null) {
			log.debug("systemSession.isLive() = "+systemSession.isLive());
			log.debug("systemSession.getUserID() = "+systemSession.getUserID());
			
			try {
				log.debug("systemSession.hasPendingChanges() = "+systemSession.hasPendingChanges());
			} catch (javax.jcr.RepositoryException e) {
				log.error("# MKK-1 # MKK-1 # MKK-1 # MKK-1 # MKK-1 # MKK-1 # MKK-1 #");
				log.error(e.getMessage(), e);
				log.error("# MKK-1 # MKK-1 # MKK-1 # MKK-1 # MKK-1 # MKK-1 # MKK-1 #");
			}
		} else {
			log.error("# MKK-2 # MKK-2 # MKK-2 # MKK-2 # MKK-2 # MKK-2 # MKK-2 #");
			log.error("systemSession is NULL");
			log.error("# MKK-2 # MKK-2 # MKK-2 # MKK-2 # MKK-2 # MKK-2 # MKK-2 #");
		}
		
		log.debug("getSystemSession: {}", systemSession);
		return systemSession;
	}

	/**
	 * Initialize the repository.
	 * 
	 * @return The root path of the initialized repository.
	 * @throws AccessDeniedException If there is any security problem: you can't access the parent
	 * document folder because of lack of permissions.
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public synchronized static String initialize() throws javax.jcr.RepositoryException, 
			FileNotFoundException, InvalidNodeTypeDefException, ParseException, DatabaseException {
		log.debug("initialize()");
				
		// Initializes Repository and SystemSession
		getRepository();
		Session systemSession = getSystemSession();
		String okmRootPath = create(systemSession);
		
		// Store system session token 
		DirectAuthModule.loadUserData(systemSession);
		JcrSessionManager.getInstance().putSystemSession(systemSession);
		log.debug("*** System user created "+systemSession.getUserID());				
				
		log.debug("initialize: {}", okmRootPath);
		return okmRootPath;
	}
	
	/**
	 * Create OpenKM repository structure
	 */
	public synchronized static String create(Session session) throws javax.jcr.RepositoryException,
			FileNotFoundException, InvalidNodeTypeDefException, ParseException {
		String okmRootPath = null;
		Node rootNode = null;
		
		try {
			rootNode = session.getRootNode().getNode(Repository.ROOT);
		} catch (javax.jcr.PathNotFoundException e) {
			log.info("No okm:root node found");
		} catch (javax.jcr.RepositoryException e) {
			log.info("No okm:root node found");
		}
			
		try {
			if (rootNode == null) {
				log.info("Repository creation");

				// Register namespaces
				log.info("Register namespace");
				Workspace ws = session.getWorkspace();
				NamespaceRegistry nsr = ws.getNamespaceRegistry(); 
				nsr.registerNamespace(Repository.OKM, Repository.OKM_URI);
				nsr.registerNamespace(PropertyGroup.GROUP, PropertyGroup.GROUP_URI);
				nsr.registerNamespace(PropertyGroup.GROUP_PROPERTY, PropertyGroup.GROUP_PROPERTY_URI);

				// Register custom node types from resources
				log.info("Register custom node types");
				InputStream is = DirectRepositoryModule.class.getResourceAsStream(Config.NODE_DEFINITIONS);
				
				if (is != null) {
					registerCustomNodeTypes(session, is);
				} else {
					String msg = "Configuration error: "+Config.NODE_DEFINITIONS+" not found";
					log.debug(msg);
					throw new javax.jcr.RepositoryException(msg);
				}
				
				Node root = session.getRootNode();
				
				// Create okm:root
				log.info("Create okm:root");
				Node okmRoot = createBase(session, root, Repository.ROOT);
				okmRootPath = okmRoot.getPath();

				// Create okm:thesaurus
				log.info("Create okm:thesaurus");
				createBase(session, root, Repository.THESAURUS);

				// Create okm:categories
				log.info("Create okm:categories");
				createBase(session, root, Repository.CATEGORIES);

				// Create okm:templates
				log.info("Create okm:templates");
				createBase(session, root, Repository.TEMPLATES);

				// Create okm:personal
				log.info("Create okm:personal");
				createBase(session, root, Repository.PERSONAL);
				
				// Create okm:mail
				log.info("Create okm:mail");
				createBase(session, root, Repository.MAIL);

				// Create okm:trash
				log.info("Create okm:trash");
				createBase(session, root, Repository.TRASH);
				
				// Create okm:config
				log.info("Create okm:config");
				Node okmConfig = root.addNode(Repository.SYS_CONFIG, Repository.SYS_CONFIG_TYPE);

				// Generate installation UUID
				String uuid = UUIDGenerator.generate(okmConfig);
				okmConfig.setProperty(Repository.SYS_CONFIG_UUID, uuid);
				Repository.setUuid(uuid);
				
				// Set repository version
				okmConfig.setProperty(Repository.SYS_CONFIG_VERSION, WarUtils.getAppVersion().getMajor());
				
				root.save();
			} else {
				log.info("Repository already created");
				Node root = session.getRootNode();
				Node okmConfig = root.getNode(Repository.SYS_CONFIG);
				
				// Get installation UUID
				String uuid = okmConfig.getProperty(Repository.SYS_CONFIG_UUID).getString();
				Repository.setUuid(uuid);
				
				// Test repository version
				String repoVer = okmConfig.getProperty(Repository.SYS_CONFIG_VERSION).getString();
				
				if (!WarUtils.getAppVersion().getMajor().equals(repoVer)) {
					log.warn("### Actual repository version ("+repoVer+") differs from application repository version ("+WarUtils.getAppVersion().getMajor()+") ###");
					log.warn("### You should upgrade the repository ###");
				}
			}
		} catch (NamespaceException e) {
			log.error(e.getMessage(), e);
			throw e;
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw e;
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
			throw e;
		} catch (InvalidNodeTypeDefException e) {
			log.error(e.getMessage(), e);
			throw e;
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			throw e;
		}
		
		return okmRootPath;
	}
	
	/**
	 * Create base node
	 */
	private static Node createBase(Session session, Node root, String name) throws ItemExistsException,
			javax.jcr.PathNotFoundException, NoSuchNodeTypeException, LockException, VersionException,
			ConstraintViolationException, javax.jcr.RepositoryException {
		Node base = root.addNode(name, Folder.TYPE);

		// Add basic properties
		base.setProperty(Folder.AUTHOR, session.getUserID());
		base.setProperty(Folder.NAME, name);

		// Auth info
		base.setProperty(Permission.USERS_READ, new String[] { session.getUserID() });
		base.setProperty(Permission.USERS_WRITE, new String[] { session.getUserID() });
		base.setProperty(Permission.USERS_DELETE, new String[] { session.getUserID() });
		base.setProperty(Permission.USERS_SECURITY, new String[] { session.getUserID() });
		base.setProperty(Permission.ROLES_READ, new String[] { Config.DEFAULT_USER_ROLE });
		base.setProperty(Permission.ROLES_WRITE, new String[] { Config.DEFAULT_USER_ROLE });
		base.setProperty(Permission.ROLES_DELETE, new String[] { Config.DEFAULT_USER_ROLE });
		base.setProperty(Permission.ROLES_SECURITY, new String[] { Config.DEFAULT_USER_ROLE });
		
		return base;
	}

	/**
	 * Remove a repository from disk.
	 * 
	 * @throws AccessDeniedException If there is any security problem: you can't access the parent
	 * document folder because of lack of permissions. 
	 * @throws RepositoryException If there is any general repository problem.
	 */
	public synchronized void remove() throws RepositoryException {
		log.debug("remove()");
		String repHome = null;
		
		// Allow absolute repository path
		if ((new File(Config.REPOSITORY_HOME)).isAbsolute()) {
			repHome = Config.REPOSITORY_HOME;
		} else {
			repHome = Config.HOME_DIR+File.separator+Config.REPOSITORY_HOME;
		}
		
		try {
			FileUtils.deleteDirectory(new File(repHome));
		} catch (IOException e) {
			System.err.println("No previous repository found");
		}
		
		log.debug("create: void");
	}
	
	@Override
	public Folder getRootFolder(String token) throws PathNotFoundException, RepositoryException, 
			DatabaseException {
		log.debug("getRootFolder({})", token);
		Folder rootFolder = new Folder();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Node rootNode = session.getRootNode().getNode(Repository.ROOT);
			rootFolder = BaseFolderModule.getProperties(session, rootNode);
			
			// Activity log
			UserActivity.log(session.getUserID(), "GET_ROOT_FOLDER", rootNode.getUUID(), rootFolder.getPath());
		} catch (javax.jcr.PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}
		
		log.debug("getRootFolder: {}", rootFolder);
		return rootFolder;
	}
	
	@Override
	public Folder getTrashFolder(String token) throws PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("getTrashFolder({})", token);
		Folder trashFolder = new Folder();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Node trashNode = session.getRootNode().getNode(Repository.TRASH+"/"+session.getUserID());
			trashFolder = BaseFolderModule.getProperties(session, trashNode);
			
			// Activity log
			UserActivity.log(session.getUserID(), "GET_TRASH_FOLDER", trashNode.getUUID(), trashFolder.getPath());
		} catch (javax.jcr.PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}
		
		log.debug("getTrashFolder: {}", trashFolder);
		return trashFolder;
	}
	
	@Override
	public Folder getTrashFolderBase(String token) throws PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("getTrashFolderBase({})", token);
		Folder trashFolder = new Folder();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Node trashNode = session.getRootNode().getNode(Repository.TRASH);
			trashFolder = BaseFolderModule.getProperties(session, trashNode);
			
			// Activity log
			UserActivity.log(session.getUserID(), "GET_TRASH_FOLDER_BASE", trashNode.getUUID(), trashFolder.getPath());
		} catch (javax.jcr.PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}
		
		log.debug("getTrashFolderBase: {}", trashFolder);
		return trashFolder;
	}
	
	@Override
	public Folder getTemplatesFolder(String token) throws PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("getTemplatesFolder({})", token);
		Folder templatesFolder = new Folder();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Node templatesNode = session.getRootNode().getNode(Repository.TEMPLATES);
			templatesFolder = BaseFolderModule.getProperties(session, templatesNode);
			
			// Activity log
			UserActivity.log(session.getUserID(), "GET_TEMPLATES_FOLDER", templatesNode.getUUID(), templatesFolder.getPath());
		} catch (javax.jcr.PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}
		
		log.debug("getTemplatesFolder: {}", templatesFolder);
		return templatesFolder;
	}
	
	@Override
	public Folder getPersonalFolder(String token) throws PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("getPersonalFolder({})", token);
		Folder personalFolder = new Folder();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Node personalNode = session.getRootNode().getNode(Repository.PERSONAL+"/"+session.getUserID());
			personalFolder = BaseFolderModule.getProperties(session, personalNode);
			
			// Activity log
			UserActivity.log(session.getUserID(), "GET_PERSONAL_FOLDER", personalNode.getUUID(), personalFolder.getPath());
		} catch (javax.jcr.PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}
		
		log.debug("getPersonalFolder: {}", personalFolder);
		return personalFolder;
	}
	
	@Override
	public Folder getPersonalFolderBase(String token) throws PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("getPersonalFolderBase({})", token);
		Folder personalFolder = new Folder();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Node personalNode = session.getRootNode().getNode(Repository.PERSONAL);
			personalFolder = BaseFolderModule.getProperties(session, personalNode);
			
			// Activity log
			UserActivity.log(session.getUserID(), "GET_PERSONAL_FOLDER_BASE", personalNode.getUUID(), personalFolder.getPath());
		} catch (javax.jcr.PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}
		
		log.debug("getPersonalFolderBase: {}", personalFolder);
		return personalFolder;
	}
	
	@Override
	public Folder getMailFolder(String token) throws PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("getMailFolder({})", token);
		Folder mailFolder = new Folder();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			String mailPath = MailUtils.getUserMailPath(session.getUserID());
			Node mailNode = session.getRootNode().getNode(mailPath.substring(1));
			mailFolder = BaseFolderModule.getProperties(session, mailNode);
			
			// Activity log
			UserActivity.log(session.getUserID(), "GET_MAIL_FOLDER", mailNode.getUUID(), mailFolder.getPath());
		} catch (javax.jcr.PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}
		
		log.debug("getMailFolder: {}", mailFolder);
		return mailFolder;
	}
	
	@Override
	public Folder getMailFolderBase(String token) throws PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("getMailFolderBase({})", token);
		Folder mailFolder = new Folder();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Node mailNode = session.getRootNode().getNode(Repository.MAIL);
			mailFolder = BaseFolderModule.getProperties(session, mailNode);
			
			// Activity log
			UserActivity.log(session.getUserID(), "GET_MAIL_FOLDER_BASE", mailNode.getUUID(), mailFolder.getPath());
		} catch (javax.jcr.PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}
		
		log.debug("getMailFolderBase: {}", mailFolder);
		return mailFolder;
	}
	
	@Override
	public Folder getThesaurusFolder(String token) throws PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("getThesaurusFolder({})", token);
		Folder thesaurusFolder = new Folder();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Node thesaurusNode = session.getRootNode().getNode(Repository.THESAURUS);
			thesaurusFolder = BaseFolderModule.getProperties(session, thesaurusNode);
			
			// Activity log
			UserActivity.log(session.getUserID(), "GET_THESAURUS_FOLDER", thesaurusNode.getUUID(), thesaurusFolder.getPath());
		} catch (javax.jcr.PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}
		
		log.debug("getThesaurusFolder: {}", thesaurusFolder);
		return thesaurusFolder;
	}
	
	@Override
	public Folder getCategoriesFolder(String token) throws PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("getCategoriesFolder({})", token);
		Folder categoriesFolder = new Folder();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Node categoriesNode = session.getRootNode().getNode(Repository.CATEGORIES);
			categoriesFolder = BaseFolderModule.getProperties(session, categoriesNode);
			
			// Activity log
			UserActivity.log(session.getUserID(), "GET_CATEGORIES_FOLDER", categoriesNode.getUUID(), categoriesFolder.getPath());
		} catch (javax.jcr.PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}
		
		log.debug("getCategoriesFolder: {}", categoriesFolder);
		return categoriesFolder;
	}
	
	/**
	 * Register custom node definition from file.
	 *
	 * TODO For Jackrabbit 2.0 should be done as:
	 *   InputStream is = getClass().getClassLoader().getResourceAsStream("test.cnd");
	 *   Reader cnd = new InputStreamReader(is);
	 *   NodeType[] nodeTypes = CndImporter.registerNodeTypes(cnd, session);
	 * 
	 * The key method is:
	 *   CndImporter.registerNodeTypes("cndfile", session);
	 */
	@SuppressWarnings("unchecked")
	public synchronized static void registerCustomNodeTypes(Session session, InputStream cndFile)
			throws FileNotFoundException, ParseException,
			javax.jcr.RepositoryException, InvalidNodeTypeDefException {
		log.debug("registerCustomNodeTypes({}, {})", session, cndFile);

		// Read in the CND file
		InputStreamReader fileReader = new InputStreamReader(cndFile);
		
		// Create a CompactNodeTypeDefReader
		CompactNodeTypeDefReader cndReader = new CompactNodeTypeDefReader(
				fileReader, Config.NODE_DEFINITIONS);

		// Get the List of NodeTypeDef objects
		List<NodeTypeDef> ntdList = cndReader.getNodeTypeDefs();

		// Get the NodeTypeManager from the Workspace.
		// Note that it must be cast from the generic JCR NodeTypeManager to the
		// Jackrabbit-specific implementation.
		Workspace ws = session.getWorkspace();
		NodeTypeManagerImpl ntmgr = (NodeTypeManagerImpl) ws.getNodeTypeManager();

		// Acquire the NodeTypeRegistry
		NodeTypeRegistry ntreg = ntmgr.getNodeTypeRegistry();

		// Loop through the prepared NodeTypeDefs
		for (Iterator<NodeTypeDef> i = ntdList.iterator(); i.hasNext();) {
			// Get the NodeTypeDef...
			NodeTypeDef ntd = i.next();

			// ...and register or reregister it
			if (!ntreg.isRegistered(ntd.getName())) {
				log.info("Register type " + ntd.getName().toString());
				ntreg.registerNodeType(ntd);
			} else {
				log.info("Reregister type " + ntd.getName().toString());
				ntreg.reregisterNodeType(ntd);
			}
		}

		log.debug("registerCustomNodeTypes: void");
	}

	@Override
	public void purgeTrash(String token) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("purgeTrash({})", token);
		Node userTrash = null;
		Session session = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			userTrash = session.getRootNode().getNode(Repository.TRASH+"/"+session.getUserID());
			
			for (NodeIterator it = userTrash.getNodes(); it.hasNext(); ) {
				Node child = it.nextNode();
				
				if (child.isNodeType(Document.TYPE)) {
					BaseDocumentModule.purge(session, child.getParent(), child);
				} else if (child.isNodeType(Folder.TYPE)) {
					BaseFolderModule.purge(session, child);
				}
			}
			
			userTrash.save();
			
			// Activity log
			UserActivity.log(session.getUserID(), "PURGE_TRASH", null, null);
		} catch (javax.jcr.AccessDeniedException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(userTrash);
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(userTrash);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}
		
		log.debug("purgeTrash: void");
	}
	
	@Override
	public String getUpdateMessage(String token) throws RepositoryException {
		return Repository.getUpdateMsg();
	}

	@Override
	public String getRepositoryUuid(String token) throws RepositoryException {
		return Repository.getUuid();
	}
	
	@Override
	public boolean hasNode(String token, String path) throws RepositoryException, DatabaseException {
		log.debug("hasNode({}, {})", token, path);
		boolean ret = false;
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			ret = session.getRootNode().hasNode(path.substring(1));
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}

		log.debug("hasNode: {}", ret);
		return ret;
	}

	@Override
	public String getNodePath(String token, String uuid) throws PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("getNodePath({}, {})", token, uuid);
		String ret;
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			ret = session.getNodeByUUID(uuid).getPath();
		} catch (javax.jcr.ItemNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}

		log.debug("getNodePath: {}", ret);
		return ret;
	}
	
	@Override
	public String getNodeUuid(String token, String path) throws PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("getNodeUuid({}, {})", token, path);
		String ret;
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			ret = session.getRootNode().getNode(path.substring(1)).getUUID();
		} catch (javax.jcr.ItemNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}

		log.debug("getNodeUuid: {}", ret);
		return ret;
	}
}
