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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.api.OKMMail;
import com.openkm.api.OKMRepository;
import com.openkm.bean.Mail;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.ItemExistsException;
import com.openkm.core.LockException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.contants.service.ErrorCode;
import com.openkm.frontend.client.service.OKMMailService;
import com.openkm.util.GWTUtil;

/**
 * Servlet Class
 * 
 * @web.servlet              name="MailServlet"
 *                           display-name="Directory mail service"
 *                           description="Directory mail service"
 * @web.servlet-mapping      url-pattern="/MailServlet"
 * @web.servlet-init-param   name="A parameter"
 *                           value="A value"
 */
public class MailServlet extends OKMRemoteServiceServlet implements OKMMailService {
	private static Logger log = LoggerFactory.getLogger(MailServlet.class);
	private static final long serialVersionUID = 6444705787188086209L;
	
	@Override
	public List<GWTMail> getChilds(String fldPath) throws OKMException {
		log.debug("getChilds({})", fldPath);
		List<GWTMail> mailList = new ArrayList<GWTMail>();
		updateSessionManager();
		
		try {
			if (fldPath == null) {
				fldPath = OKMRepository.getInstance().getMailFolder(null).getPath();
			} 
			
			log.debug("ParentFolder: {}", fldPath);
			Collection<Mail> col = OKMMail.getInstance().getChilds(null, fldPath);
			
			for (Iterator<Mail> it = col.iterator(); it.hasNext();) {		
				Mail mail = it.next();
				log.debug("Mail: {}", mail);
				GWTMail mailClient = GWTUtil.copy(mail);
				mailList.add(mailClient);
			}
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("getChilds: {}", mailList);
		return mailList;
	}
	
	@Override
	public void delete(String mailPath) throws OKMException {
		log.debug("delete({})", mailPath);
		updateSessionManager();
		
		try {
			OKMMail.getInstance().delete(null, mailPath);
		} catch (LockException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Lock), e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("delete: void");
	}
	
	@Override
	public void move(String mailPath, String destPath) throws OKMException {
		log.debug("move({}, {})", mailPath, destPath);
		updateSessionManager();
		
		try {
			OKMMail.getInstance().move(null, mailPath, destPath);
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (ItemExistsException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_ItemExists), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("move: void");
	}
	
	@Override
	public void purge(String mailPath) throws OKMException {
		log.debug("purge({})", mailPath);
		updateSessionManager();
		
		try {
			OKMMail.getInstance().purge(null, mailPath);
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("purge: void");
	}
	
	@Override
	public void copy(String mailPath, String fldPath) throws OKMException {
		log.debug("copy({}, {})", mailPath, fldPath);
		updateSessionManager();
		
		try {
			OKMMail.getInstance().copy(null, mailPath, fldPath);
		} catch (ItemExistsException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_ItemExists), e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("copy: void");
	}
	
	@Override
	public GWTMail getProperties(String mailPath) throws OKMException {
		log.debug("getProperties({})", mailPath);
		GWTMail mailClient = new GWTMail();
		updateSessionManager();
		
		try {
			mailClient = GWTUtil.copy(OKMMail.getInstance().getProperties(null, mailPath));
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("copy: getProperties");
		return mailClient;
	}
	
	@Override
	public GWTMail rename(String mailId, String newName) throws OKMException  {
		log.debug("rename({}, {})", mailId, newName);
		GWTMail gWTMail = new GWTMail();
		updateSessionManager();
		
		try {
			gWTMail = GWTUtil.copy(OKMMail.getInstance().rename(null, mailId, newName));
		} catch (ItemExistsException e){
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_ItemExists), e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e ){
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMMailService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("rename: {}", gWTMail);
		return gWTMail;
	}
}
