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

package com.openkm.servlet.frontend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.api.OKMRepository;
import com.openkm.bean.Folder;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.contants.service.ErrorCode;
import com.openkm.frontend.client.service.OKMRepositoryService;
import com.openkm.util.GWTUtil;

/**
 * Servlet Class
 * 
 * @web.servlet              name="RepositoryServlet"
 *                           display-name="Directory tree service"
 *                           description="Directory tree service"
 * @web.servlet-mapping      url-pattern="/RepositoryServlet"
 * @web.servlet-init-param   name="A parameter"
 *                           value="A value"
 */
public class RepositoryServlet extends OKMRemoteServiceServlet implements OKMRepositoryService {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(RepositoryServlet.class);

	@Override
	public String getUpdateMessage() throws OKMException {
		log.debug("getUpdateMessage()");
		String msg = "";
		updateSessionManager();

		try {
			msg = OKMRepository.getInstance().getUpdateMessage(null);
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("getUpdateMessage: {}", msg);
		return msg;
	}
	
	@Override
	public GWTFolder getPersonalFolder() throws OKMException {
		log.debug("getPersonalFolder()");
		GWTFolder gWTFolder = new GWTFolder();
		Folder folder;
		updateSessionManager();
		
		try {
			// Administrators user can see all user homes
			if (getThreadLocalRequest().isUserInRole(Config.DEFAULT_ADMIN_ROLE)) {
				folder = OKMRepository.getInstance().getPersonalFolderBase(null);
			} else {
				folder = OKMRepository.getInstance().getPersonalFolder(null);
			}
			gWTFolder = GWTUtil.copy(folder);
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("getPersonalFolder: {}", gWTFolder);
		return gWTFolder;
	}
	
	@Override
	public GWTFolder getTemplatesFolder() throws OKMException {
		log.debug("getTemplateFolder()");
		GWTFolder gWTFolder = new GWTFolder();
		Folder folder;
		updateSessionManager();
		
		try {
			folder =  OKMRepository.getInstance().getTemplatesFolder(null);
			gWTFolder = GWTUtil.copy(folder);
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("getTemplatesFolder: {}", gWTFolder);
		return gWTFolder;
	}
	
	@Override
	public void purgeTrash() throws OKMException {
		log.debug("purgeTrash()");
		updateSessionManager();
		
		try {
			OKMRepository.getInstance().purgeTrash(null);
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e ) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("purgeTrash: void");
	}
	
	@Override
	public GWTFolder getTrashFolder() throws OKMException {
		log.debug("getTrashFolder()");
		GWTFolder gWTFolder = new GWTFolder();
		Folder folder;
		updateSessionManager();
		
		try {
			// Administrators user can see all user homes
			if (getThreadLocalRequest().isUserInRole(Config.DEFAULT_ADMIN_ROLE)) {
				folder = OKMRepository.getInstance().getTrashFolderBase(null);
			} else {
				folder = OKMRepository.getInstance().getTrashFolder(null);
			}
			gWTFolder = GWTUtil.copy(folder);
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("getTrashFolder: {}", gWTFolder);
		return gWTFolder;
	}
	
	@Override
	public GWTFolder getRootFolder() throws OKMException {
		log.debug("getRootFolder()");
		GWTFolder gWTFolder = new GWTFolder();
		Folder folder;
		updateSessionManager();
		
		try {
			folder = OKMRepository.getInstance().getRootFolder(null);
			gWTFolder = GWTUtil.copy(folder);
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("getRootFolder: {}", gWTFolder);
		return gWTFolder;
	}
		
	@Override
	public GWTFolder getMailFolder() throws OKMException {
		log.debug("getMailFolder()");
		GWTFolder gWTFolder = new GWTFolder();
		Folder folder;
		updateSessionManager();
		
		try {
			// Administrators user can see all user homes
			if (getThreadLocalRequest().isUserInRole(Config.DEFAULT_ADMIN_ROLE)) {
				folder = OKMRepository.getInstance().getMailFolderBase(null);
			} else {
				folder = OKMRepository.getInstance().getMailFolder(null);
			}
			gWTFolder = GWTUtil.copy(folder);
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("getMailFolder: {}", gWTFolder);
		return gWTFolder;
	}
	
	@Override
	public GWTFolder getThesaurusFolder() throws OKMException {
		log.debug("getThesaurusFolder()");
		GWTFolder gWTFolder = new GWTFolder();
		Folder folder;
		updateSessionManager();
		
		try {
			folder = OKMRepository.getInstance().getThesaurusFolder(null);
			gWTFolder = GWTUtil.copy(folder);
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("getThesaurusFolder: {}", gWTFolder);
		return gWTFolder;
	}
	
	@Override
	public GWTFolder getCategoriesFolder() throws OKMException {
		log.debug("getCategoriesFolder()");
		GWTFolder gWTFolder = new GWTFolder();
		Folder folder;
		updateSessionManager();
		
		try {
			folder = OKMRepository.getInstance().getCategoriesFolder(null);
			gWTFolder = GWTUtil.copy(folder);
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("getCategoriesFolder: {}", gWTFolder);
		return gWTFolder;
	}
	
	@Override
	public String getPathByUUID(String uuid) throws OKMException {
		log.debug("getPathByUUID()");
		String path = "";
		updateSessionManager();
		
		try {
			path = OKMRepository.getInstance().getNodePath(null, uuid);
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_Database), e.getMessage());
		}
		
		return path;
	}
	
	@Override
	public String getUUIDByPath(String path) throws OKMException {
		log.debug("getUUIDByPath()");
		String uuid = "";
		updateSessionManager();
		
		try {
			uuid = OKMRepository.getInstance().getNodeUuid(null, path);
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_Database), e.getMessage());
		}
		
		return uuid;
	}
	
	@Override
	public Boolean hasNode(String path) throws OKMException {
		log.debug("hasNode({})"+path);
		updateSessionManager();
		try {
			return OKMRepository.getInstance().hasNode(null, path);
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMRepositoryService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}
}
