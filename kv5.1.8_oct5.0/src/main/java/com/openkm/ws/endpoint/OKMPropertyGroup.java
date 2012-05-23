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

package com.openkm.ws.endpoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.jboss.annotation.security.SecurityDomain;
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
import com.openkm.ws.util.FormElementComplex;

/**
 * Servlet Class
 * 
 * @web.servlet name="OKMPropertyGroup"
 * @web.servlet-mapping url-pattern="/OKMPropertyGroup"
 */

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
@SecurityDomain("OpenKM")
public class OKMPropertyGroup {
	private static Logger log = LoggerFactory.getLogger(OKMPropertyGroup.class);

	@WebMethod
	public void addGroup(@WebParam(name = "token") String token,
			@WebParam(name = "nodePath") String nodePath,
			@WebParam(name = "grpName") String grpName) throws NoSuchGroupException, LockException,
			PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("addGroup({}, {}, {})", new Object[] { token, nodePath, grpName });
		PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
		cm.addGroup(token, nodePath, grpName);
		log.debug("addGroup: void");
	}

	@WebMethod
	public void removeGroup(@WebParam(name = "token") String token,
			@WebParam(name = "nodePath") String nodePath,
			@WebParam(name = "grpName") String grpName) throws AccessDeniedException, 
			NoSuchGroupException, LockException, PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("removeGroup({}, {}, {})", new Object[] { token, nodePath, grpName });
		PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
		cm.removeGroup(token, nodePath, grpName);
		log.debug("removeGroup: void");
	}

	@WebMethod
	public PropertyGroup[] getGroups(@WebParam(name = "token") String token,
			@WebParam(name = "nodePath") String nodePath) throws IOException,
			ParseException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getGroups({}, {})", token, nodePath);
		PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
		List<PropertyGroup> col = cm.getGroups(token, nodePath);
		PropertyGroup[] result = (PropertyGroup[]) col.toArray(new PropertyGroup[col.size()]);
		log.debug("getGroups: {}", result);
		return result;
	}

	@WebMethod
	public PropertyGroup[] getAllGroups(@WebParam(name = "token") String token) throws IOException,
			ParseException, RepositoryException, DatabaseException {
		log.debug("getAllGroups({})", token);
		PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
		List<PropertyGroup> col = cm.getAllGroups(token);
		PropertyGroup[] result = (PropertyGroup[]) col.toArray(new PropertyGroup[col.size()]);
		log.debug("getAllGroups: {} ", result);
		return result;
	}

	@WebMethod
	public FormElementComplex[] getProperties(@WebParam(name = "token") String token,
			@WebParam(name = "nodePath") String nodePath,
			@WebParam(name = "grpName") String grpName) throws IOException, ParseException,
			NoSuchGroupException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("getProperties({}, {}, {})", new Object[] { token, nodePath, grpName });
		PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
		List<FormElement> col = cm.getProperties(token, nodePath, grpName);
		FormElementComplex[] result = new FormElementComplex[col.size()];
		
		for (int i=0; i<col.size(); i++) {
			result[i] = FormElementComplex.toFormElementComplex(col.get(i));
		}
		
		log.debug("getProperties: {}", result);
		return result;
	}
	
	@WebMethod
	public void setProperties(@WebParam(name = "token") String token,
			@WebParam(name = "nodePath") String nodePath,
			@WebParam(name = "grpName") String grpName,
			@WebParam(name = "properties") FormElementComplex[] properties) throws IOException, ParseException,
			NoSuchPropertyException, NoSuchGroupException, LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("setProperties({}, {}, {}, {})", new Object[] { token, nodePath, grpName, properties });
		PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
		List<FormElement> al = new ArrayList<FormElement>();
		
		for (int i=0; i<properties.length; i++) {
			al.add(FormElementComplex.toFormElement(properties[i]));
		}
		
		cm.setProperties(token, nodePath, grpName, al);
		log.debug("setProperties: void");
	}
	
	@WebMethod
	public boolean hasGroup(@WebParam(name = "token") String token,
			@WebParam(name = "nodePath") String nodePath,
			@WebParam(name = "grpName") String grpName) throws IOException,
			ParseException, PathNotFoundException, RepositoryException, DatabaseException {
		log.debug("hasGroup({}, {}, {})", new Object[] { token, nodePath, grpName });
		PropertyGroupModule cm = ModuleManager.getPropertyGroupModule();
		boolean ret = cm.hasGroup(token, nodePath, grpName);
		log.debug("hasGroup: {}", ret);
		return ret;
	}
}
