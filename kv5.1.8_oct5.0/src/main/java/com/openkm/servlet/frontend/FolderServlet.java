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

import com.openkm.api.OKMFolder;
import com.openkm.bean.Folder;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.ItemExistsException;
import com.openkm.core.LockException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.contants.service.ErrorCode;
import com.openkm.frontend.client.service.OKMFolderService;
import com.openkm.frontend.client.util.FolderComparator;
import com.openkm.util.GWTUtil;

/**
 * Servlet Class
 * 
 * @web.servlet              name="FolderServlet"
 *                           display-name="Directory tree service"
 *                           description="Directory tree service"
 * @web.servlet-mapping      url-pattern="/FolderServlet"
 * @web.servlet-init-param   name="A parameter"
 *                           value="A value"
 */
public class FolderServlet extends OKMRemoteServiceServlet implements OKMFolderService {
	private static Logger log = LoggerFactory.getLogger(FolderServlet.class);
	private static final long serialVersionUID = -4436438730167948558L;
	
	@Override
	public GWTFolder create(String fldPath, String fldPathParent) throws OKMException {
		log.debug("create({}, {})", fldPath, fldPathParent);
		GWTFolder gWTFolder = new GWTFolder();
		Folder folder = new Folder();
		folder.setPath(fldPathParent+"/"+fldPath);
		updateSessionManager();
		
		try {
			gWTFolder = GWTUtil.copy(OKMFolder.getInstance().create(null, folder));
		} catch (ItemExistsException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_ItemExists), e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e){
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e ){
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e ){
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("create: {}", gWTFolder);
		return gWTFolder;
	}
	
	@Override
	public void delete(String fldPath) throws OKMException {
		log.debug("delete({})", fldPath);
		updateSessionManager();
		
		try {
			OKMFolder.getInstance().delete(null, fldPath);
		} catch (LockException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Lock), e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e){
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e ){
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e ){
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("delete: void");
	}

	@Override
	public List<GWTFolder> getChilds(String fldPath) throws OKMException {
		log.debug("getFolderChilds({})", fldPath);
		List<GWTFolder> folderList = new ArrayList<GWTFolder>();
		updateSessionManager();
		
		try {
			log.debug("ParentFolder: {}", fldPath);
			Collection<Folder> col = OKMFolder.getInstance().getChilds(null, fldPath);
			for (Iterator<Folder> it = col.iterator(); it.hasNext();){				
				Folder folder = it.next();
				GWTFolder gWTFolder = GWTUtil.copy(folder);
				folderList.add(gWTFolder);
			}
			
			Collections.sort(folderList, FolderComparator.getInstance());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (DatabaseException e ){
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("getFolderChilds: {}", folderList);
		return folderList;
	}
	
	@Override
	public GWTFolder rename(String fldId, String newName)  throws OKMException  {
		log.debug("rename({}, {})", fldId, newName);
		GWTFolder gWTFolder = new GWTFolder();
		updateSessionManager();
		
		try {
			gWTFolder = GWTUtil.copy(OKMFolder.getInstance().rename(null, fldId, newName));
		} catch (ItemExistsException e){
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_ItemExists), e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e ){
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("rename: {}", gWTFolder);
		return gWTFolder;
	}
	
	@Override
	public void move(String fldPath, String dstPath) throws OKMException {
		log.debug("move({}, {})", fldPath, dstPath);
		updateSessionManager();
		
		try {
			OKMFolder.getInstance().move(null, fldPath, dstPath);
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e){
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (ItemExistsException e ){
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_ItemExists), e.getMessage());
		} catch (RepositoryException e ){
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e ){
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("move: void");
	}
	
	@Override
	public void purge(String fldPath) throws OKMException {
		log.debug("purge({})", fldPath);
		updateSessionManager();
		
		try {
			OKMFolder.getInstance().purge(null, fldPath);
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e){
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e ){
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e ){
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("purge: void");
	}
	
	@Override
	public GWTFolder getProperties(String fldPath) throws OKMException {
		log.debug("getProperties({})", fldPath);
		GWTFolder gWTFolder = new GWTFolder();
		updateSessionManager();
		
		try {
			gWTFolder = GWTUtil.copy(OKMFolder.getInstance().getProperties(null, fldPath));
		} catch (PathNotFoundException e ){
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (RepositoryException e ){
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e ){
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e ){
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("getProperties: {}", gWTFolder);
		return gWTFolder;
	}
	
	@Override
	public void copy(String fldPath, String dstPath) throws OKMException {
		log.debug("copy({}, {})", fldPath, dstPath);
		updateSessionManager();
	
		try {
			OKMFolder.getInstance().copy(null, fldPath, dstPath);
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e){
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (ItemExistsException e ){
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_ItemExists), e.getMessage());
		} catch (RepositoryException e ){
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e ){
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("copy: void");
	}
	
	@Override
	public Boolean isValid(String fldPath) throws OKMException {
		log.debug("isValid({})", fldPath);
		updateSessionManager();
	
		try {
			return Boolean.valueOf(OKMFolder.getInstance().isValid(null, fldPath));
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e){
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e ){
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e ){
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMFolderService, ErrorCode.CAUSE_General), e.getMessage());
		}
	}
}
