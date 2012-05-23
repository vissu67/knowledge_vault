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

import com.openkm.api.OKMProperty;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.LockException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.core.VersionException;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.contants.service.ErrorCode;
import com.openkm.frontend.client.service.OKMPropertyService;

/**
 * Servlet Class
 * 
 * @web.servlet              name="PropertyServlet"
 *                           display-name="Directory tree service"
 *                           description="Directory tree service"
 * @web.servlet-mapping      url-pattern="/PropertyServlet"
 * @web.servlet-init-param   name="A parameter"
 *                           value="A value"
 */
public class PropertyServlet extends OKMRemoteServiceServlet implements OKMPropertyService {
	private static Logger log = LoggerFactory.getLogger(PropertyServlet.class);
	private static final long serialVersionUID = 1138063389446959876L;
	
	@Override
	public void addCategory(String nodePath, String category) throws OKMException {
		log.debug("addCategory({}, {})", nodePath, category);
		updateSessionManager();

		try {
			OKMProperty.getInstance().addCategory(null, nodePath, category);
		} catch (VersionException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyService, ErrorCode.CAUSE_Version), e.getMessage());
		} catch (LockException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyService, ErrorCode.CAUSE_Lock), e.getMessage());
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyService, ErrorCode.CAUSE_Database), e.getMessage());
		} 
		log.debug("addCategory: void");
	}
	
	@Override
	public void removeCategory(String nodePath, String category) throws OKMException {
		log.debug("removeCategory({}, {})", nodePath, category);
		updateSessionManager();

		try {
			OKMProperty.getInstance().removeCategory(null, nodePath, category);
		} catch (VersionException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyService, ErrorCode.CAUSE_Version), e.getMessage());
		} catch (LockException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyService, ErrorCode.CAUSE_Lock), e.getMessage());
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyService, ErrorCode.CAUSE_Database), e.getMessage());
		}
		log.debug("removeCategory: void");
	}
	
	@Override
	public void addKeyword(String nodePath, String keyword) throws OKMException {
		log.debug("addKeyword({}, {})", nodePath, keyword);
		updateSessionManager();

		try {
			OKMProperty.getInstance().addKeyword(null, nodePath, keyword);
		} catch (VersionException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyService, ErrorCode.CAUSE_Version), e.getMessage());
		} catch (LockException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyService, ErrorCode.CAUSE_Lock), e.getMessage());
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyService, ErrorCode.CAUSE_Database), e.getMessage());
		}
		log.debug("addKeyword: void");
	}
	
	@Override
	public void removeKeyword(String nodePath, String keyword) throws OKMException {
		log.debug("removeKeyword({}, {})", nodePath, keyword);
		updateSessionManager();

		try {
			OKMProperty.getInstance().removeKeyword(null, nodePath, keyword);
		} catch (VersionException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyService, ErrorCode.CAUSE_Version), e.getMessage());
		} catch (LockException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyService, ErrorCode.CAUSE_Lock), e.getMessage());
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyService, ErrorCode.CAUSE_Database), e.getMessage());
		}
		log.debug("addKeyword: void");
	}
}
