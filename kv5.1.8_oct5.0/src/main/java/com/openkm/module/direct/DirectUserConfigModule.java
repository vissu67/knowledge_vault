package com.openkm.module.direct;

import javax.jcr.Node;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.JcrSessionManager;
import com.openkm.core.RepositoryException;
import com.openkm.dao.UserConfigDAO;
import com.openkm.dao.bean.UserConfig;
import com.openkm.jcr.JCRUtils;
import com.openkm.module.UserConfigModule;
import com.openkm.util.UserActivity;

public class DirectUserConfigModule implements UserConfigModule {
	private static Logger log = LoggerFactory.getLogger(DirectUserConfigModule.class);
	
	@Override
	public void setHome(String token, String nodePath) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("setHome({}, {})", token, nodePath);
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
			
			Node rootNode = session.getRootNode();
			Node node = rootNode.getNode(nodePath.substring(1));
			UserConfig uc = new UserConfig();
			uc.setHomePath(nodePath);
			uc.setHomeUuid(node.getUUID());
			uc.setHomeType(JCRUtils.getNodeType(node));
			uc.setUser(session.getUserID());
			UserConfigDAO.setHome(uc);
			
			// Activity log
			UserActivity.log(session.getUserID(), "USER_CONFIG_SET_HOME", node.getUUID(), nodePath);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} catch (DatabaseException e) {
			throw e;
		} finally {
			if (token == null) JCRUtils.logout(session);
		}

		log.debug("setHome: void");
	}
	
	@Override
	public UserConfig getConfig(String token) throws RepositoryException, DatabaseException {
		log.debug("getConfig({})", token);
		UserConfig ret = new UserConfig();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			ret = UserConfigDAO.findByPk(session, session.getUserID());
			
			// Activity log
			UserActivity.log(session.getUserID(), "USER_CONFIG_GET_CONFIG", null, null);
		} catch (javax.jcr.RepositoryException e) {
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}

		log.debug("getConfig: {}", ret);
		return ret;
	}
}
