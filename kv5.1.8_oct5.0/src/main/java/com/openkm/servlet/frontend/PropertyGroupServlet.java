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

import com.openkm.api.OKMPropertyGroup;
import com.openkm.bean.PropertyGroup;
import com.openkm.bean.form.FormElement;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.LockException;
import com.openkm.core.NoSuchGroupException;
import com.openkm.core.NoSuchPropertyException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.GWTPropertyGroup;
import com.openkm.frontend.client.bean.form.GWTFormElement;
import com.openkm.frontend.client.contants.service.ErrorCode;
import com.openkm.frontend.client.service.OKMPropertyGroupService;
import com.openkm.util.GWTUtil;

/**
 * PropertyGroup Servlet Class
 */
public class PropertyGroupServlet extends OKMRemoteServiceServlet implements OKMPropertyGroupService {
	private static Logger log = LoggerFactory.getLogger(PropertyGroupServlet.class);
	private static final long serialVersionUID = 2638205115826644606L;
	
	@Override
	public List<GWTPropertyGroup> getAllGroups() throws OKMException {
		log.debug("getAllGroups()");
		List<GWTPropertyGroup> groupList = new ArrayList<GWTPropertyGroup>();
		updateSessionManager();

		try {
			Collection<PropertyGroup> col = OKMPropertyGroup.getInstance().getAllGroups(null);
			
			for (Iterator<PropertyGroup> it = col.iterator(); it.hasNext();) {	
				PropertyGroup pg = it.next();
				
				if (pg.isVisible()) {
					GWTPropertyGroup group = GWTUtil.copy(pg);
					groupList.add(group);
				}
			}
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("getAllGroups: {}", groupList);
		return groupList;
	}
	
	@Override
	public List<GWTPropertyGroup> getAllGroups(String path) throws OKMException {
		log.debug("getAllGroups({})", path);
		List<GWTPropertyGroup> groupList = new ArrayList<GWTPropertyGroup>();
		updateSessionManager();

		try {
			Collection<PropertyGroup> col = OKMPropertyGroup.getInstance().getAllGroups(null);
			List<GWTPropertyGroup> actualGroupsList = getGroups(path);
			
			for (Iterator<PropertyGroup> it = col.iterator(); it.hasNext();) {	
				PropertyGroup pg = it.next();
				
				if (pg.isVisible()) {
					GWTPropertyGroup group = GWTUtil.copy(pg);
					groupList.add(group);
				}
			}
			
			// Purge from list values that are assigned to document
			if (!actualGroupsList.isEmpty()) {
				for (Iterator<GWTPropertyGroup> it = actualGroupsList.iterator(); it.hasNext();) {	
					GWTPropertyGroup group = it.next();
					
					for (Iterator<GWTPropertyGroup> itGroupList = groupList.iterator(); itGroupList.hasNext();) {
						GWTPropertyGroup groupListElement = itGroupList.next();
						if (groupListElement.getName().equals(group.getName())) {
							groupList.remove(groupListElement);
							break;
						}
					}
				}
			}
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (OKMException e) {
			log.error(e.getMessage(), e);
			throw e;
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("getAllGroups: {}", groupList);
		return groupList;
	}
	
	@Override
	public void addGroup(String path, String grpName) throws OKMException {
		log.debug("addGroup({}, {})", path, grpName);
		updateSessionManager();
		
		try {
			OKMPropertyGroup.getInstance().addGroup(null, path, grpName);
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_NoSuchGroup), e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (LockException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Lock), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("addGroup: void");
	}
	
	@Override
	public List<GWTPropertyGroup> getGroups(String path) throws OKMException {
		log.debug("getGroups({})", path);
		List<GWTPropertyGroup> groupList = new ArrayList<GWTPropertyGroup>();
		updateSessionManager();

		try {
			Collection<PropertyGroup> col = OKMPropertyGroup.getInstance().getGroups(null, path);
			
			for (Iterator<PropertyGroup> it = col.iterator(); it.hasNext();) {	
				PropertyGroup pg = it.next();
				
				if (pg.isVisible()) {
					GWTPropertyGroup group = GWTUtil.copy(pg);
					groupList.add(group);
				}
			}
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("getGroups: {}", groupList);
		return groupList;
	}
	
	@Override
	public List<GWTFormElement> getProperties(String path, String grpName) throws OKMException {
		log.debug("getProperties({}, {})", path, grpName);
		List<GWTFormElement> properties = new ArrayList<GWTFormElement>();
		updateSessionManager();

		try {
			for (Iterator<FormElement> it = OKMPropertyGroup.getInstance().getProperties(null, path, grpName).iterator(); it.hasNext();) {
				properties.add(GWTUtil.copy(it.next()));
			}
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_NoSuchGroup), e.getMessage());
		}  catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("getProperties: {}", properties);
		return properties;
	}
	
	@Override
	public List<GWTFormElement> getPropertyGroupForm(String grpName) throws OKMException {
		log.debug("getPropertyGroupForm({})", grpName);
		List<FormElement> properties = new ArrayList<FormElement>();
		List<GWTFormElement> gwtProperties = new ArrayList<GWTFormElement>();
		updateSessionManager();

		try {
			properties = OKMPropertyGroup.getInstance().getPropertyGroupForm(null, grpName);
			
			for (Iterator<FormElement> it = properties.iterator(); it.hasNext(); ) {
				gwtProperties.add(GWTUtil.copy(it.next()));
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("getPropertyGroupForm: {}", gwtProperties);
		return gwtProperties;
	}
	
	@Override
	public void setProperties(String path, String grpName, List<GWTFormElement> formProperties) throws OKMException {
		log.debug("setProperties({}, {}, {})", new Object[] { path, grpName, formProperties });
		updateSessionManager();
		
		try {
			List<FormElement> properties = new ArrayList<FormElement>();
			for (Iterator<GWTFormElement> it = formProperties.iterator(); it.hasNext(); ) {
				properties.add(GWTUtil.copy(it.next()));
			}
			OKMPropertyGroup.getInstance().setProperties(null, path, grpName, properties);
		} catch (NoSuchPropertyException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_NoSuchProperty), e.getMessage());
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_NoSuchGroup), e.getMessage());
		} catch (LockException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Lock), e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("setProperties: void");
	}
	
	@Override
	public void removeGroup(String path, String grpName) throws OKMException  {
		log.debug("removeGroup({}, {})", path, grpName);
		updateSessionManager();
		
		try {
			OKMPropertyGroup.getInstance().removeGroup(null, path, grpName);
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_NoSuchGroup), e.getMessage());
		}  catch (LockException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Lock), e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMPropertyGroupService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("removeGroup: void");
	}
}
