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

package com.openkm.api;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.PropertyGroup;
import com.openkm.bean.form.FormElement;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.LockException;
import com.openkm.core.NoSuchGroupException;
import com.openkm.core.NoSuchPropertyException;
import com.openkm.core.ParseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.module.ModuleManager;
import com.openkm.module.PropertyGroupModule;

/**
 * @author pavila
 * 
 */
public class OKMPropertyGroup implements PropertyGroupModule {
	private static Logger log = LoggerFactory.getLogger(OKMPropertyGroup.class);
	private static OKMPropertyGroup instance = new OKMPropertyGroup();
	
	private OKMPropertyGroup() {}
	
	public static OKMPropertyGroup getInstance() {
		return instance;
	}
	
	@Override
	public void addGroup(String token, String nodePath, String grpName) throws NoSuchGroupException,
			LockException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("addGroup({}, {}, {})", new Object[] { token, nodePath, grpName });
		PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
		cm.addGroup(token, nodePath, grpName);
		log.debug("addGroup: void");
	}

	@Override
	public void removeGroup(String token, String nodePath, String grpName) throws AccessDeniedException, 
			NoSuchGroupException, LockException, PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("removeGroup({}, {}, {})", new Object[] { token, nodePath, grpName });
		PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
		cm.removeGroup(token, nodePath, grpName);
		log.debug("removeGroup: void");
	}

	@Override
	public List<PropertyGroup> getGroups(String token, String nodePath) throws IOException, ParseException, 
			PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getGroups({}, {})", token, nodePath);
		PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
		List<PropertyGroup> ret = cm.getGroups(token, nodePath);
		log.debug("getGroups: {}", ret);
		return ret;
	}

	@Override
	public List<PropertyGroup> getAllGroups(String token) throws IOException, ParseException,
			RepositoryException, DatabaseException {
		log.debug("getAllGroups({})", token);
		PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
		List<PropertyGroup> ret = cm.getAllGroups(token);
		log.debug("getAllGroups: {}", ret);
		return ret;
	}

	@Override
	public List<FormElement> getProperties(String token, String nodePath, String grpName) throws IOException,
			ParseException, NoSuchGroupException, PathNotFoundException, RepositoryException, 
			DatabaseException {
		log.debug("getProperties({}, {}, {})", new Object[] { token, nodePath, grpName });
		PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
		List<FormElement> ret = cm.getProperties(token, nodePath, grpName);
		log.debug("getProperties: {}", ret);
		return ret;
	}

	@Override
	public void setProperties(String token, String nodePath, String grpName, List<FormElement> properties)
			throws IOException, ParseException, NoSuchPropertyException, NoSuchGroupException, LockException,
			PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("setProperties({}, {}, {}, {})", new Object[] { token, nodePath, grpName, properties });
		PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
		cm.setProperties(token, nodePath, grpName, properties);
		log.debug("setProperties: void");
	}

	@Override
	public List<FormElement> getPropertyGroupForm(String token, String grpName) throws ParseException,
			IOException, RepositoryException, DatabaseException {
		log.debug("getPropertyGroupForm({}, {})", token, grpName);
		PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
		List<FormElement> ret = cm.getPropertyGroupForm(token, grpName);
		log.debug("getPropertyGroupForm: {}", ret);
		return ret;
	}
	
	@Override
	public boolean hasGroup(String token, String nodePath, String grpName) throws IOException,
			ParseException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("hasGroup({}, {}, {})", new Object[] { token, nodePath, grpName });
		PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
		boolean ret = cm.hasGroup(token, nodePath, grpName);
		log.debug("hasGroup: {}", ret);
		return ret;
	}
}
