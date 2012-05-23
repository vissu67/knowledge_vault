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

package com.openkm.frontend.client.service.extension;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.extension.GWTContact;

/**
 * OKMContactService
 * 
 * @author jllort
 *
 */
@RemoteServiceRelativePath("../extension/Contact")
public interface OKMContactService extends RemoteService {
	public void create (String uuid, GWTContact contact) throws OKMException;
	public void delete (int id) throws OKMException;
	public void delete (int id, String uuid) throws OKMException;
	public List<GWTContact> findByUuid(String uuid) throws OKMException;
	public List<GWTContact> findAll() throws OKMException;
	public List<GWTContact> getGoogleContacts(String username, String userpass, String groupId, Map<String,String> googleFieldMap) throws OKMException;
	public Boolean loginGoogleContact(String username, String userpass) throws OKMException;
	public Map<String,String> getContactGroups(String username, String userpass) throws OKMException;
	public void syncGoogleContacts(List<GWTContact> contacts)  throws OKMException;
	public List<GWTContact> findAllFiltered(String uuid) throws OKMException;
	public void update(GWTContact contact) throws OKMException;
	public void addContact(int id, String uuid) throws OKMException;
}