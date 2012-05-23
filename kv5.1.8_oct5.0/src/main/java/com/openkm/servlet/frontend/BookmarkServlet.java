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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.api.OKMBookmark;
import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.dao.bean.Bookmark;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.GWTBookmark;
import com.openkm.frontend.client.contants.service.ErrorCode;
import com.openkm.frontend.client.service.OKMBookmarkService;
import com.openkm.frontend.client.util.BookmarkComparator;
import com.openkm.util.GWTUtil;

/**
 * Servlet Class
 * 
 * @web.servlet              name="BookmarkServlet"
 *                           display-name="Directory tree service"
 *                           description="Directory tree service"
 * @web.servlet-mapping      url-pattern="/BookmarkServlet"
 * @web.servlet-init-param   name="A parameter"
 *                           value="A value"
 */
public class BookmarkServlet extends OKMRemoteServiceServlet implements OKMBookmarkService {
	private static Logger log = LoggerFactory.getLogger(BookmarkServlet.class);
	private static final long serialVersionUID = 1L;

	@Override
	public List<GWTBookmark> getAll() throws OKMException {
		log.debug("getAll()");
		List<GWTBookmark> bookmarkList = new ArrayList<GWTBookmark>();
		updateSessionManager();
		
		try {
			Collection<Bookmark> col = OKMBookmark.getInstance().getAll(null);
			
			for (Iterator<Bookmark> it = col.iterator(); it.hasNext();) {		
				Bookmark bookmark = it.next();
				log.debug("Bookmark: {}", bookmark);
				GWTBookmark bookmarkClient = GWTUtil.copy(bookmark);
				bookmarkList.add(bookmarkClient);
			}
			
			Collections.sort(bookmarkList, BookmarkComparator.getInstance());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("getAll: {}", bookmarkList);
		return bookmarkList;
	}
	
	@Override
	public GWTBookmark add(String nodePath, String name) throws OKMException {
		log.debug("add({}, {})", nodePath, name);
		updateSessionManager();
		
		try {
			return GWTUtil.copy(OKMBookmark.getInstance().add(null, nodePath, name));
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_General), e.getMessage());
		}
	}
	
	@Override
	public void remove(int bmId) throws OKMException {
		log.debug("remove({})", bmId);
		updateSessionManager();
		
		try {
			OKMBookmark.getInstance().remove(null, bmId);
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("remove: void");
	}
	
	@Override
	public GWTBookmark rename(int bmId, String newName) throws OKMException {
		log.debug("rename({}, {})", bmId, newName);
		updateSessionManager();
		
		try {
			return GWTUtil.copy(OKMBookmark.getInstance().rename(null, bmId, newName));
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_General), e.getMessage());
		}
	}
	
	@Override
	public GWTBookmark get(int bmId) throws OKMException {
		log.debug("get({})", bmId);
		updateSessionManager();
		try {
			return GWTUtil.copy(OKMBookmark.getInstance().get(null, bmId));
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMBookmarkService, ErrorCode.CAUSE_General), e.getMessage());
		}
	}
}
