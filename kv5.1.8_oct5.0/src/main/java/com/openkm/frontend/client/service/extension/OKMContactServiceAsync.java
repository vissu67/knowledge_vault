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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.RemoteService;
import com.openkm.frontend.client.bean.extension.GWTContact;

/**
 * OKMContactServiceAsync
 * 
 * @author jllort
 *
 */
public interface OKMContactServiceAsync extends RemoteService {
	public void create (String uuid, GWTContact contact, AsyncCallback<?> callback);
	public void delete (int id, AsyncCallback<?> callback);
	public void delete (int id, String uuid, AsyncCallback<?> callback);
	public void findByUuid(String uuid, AsyncCallback<List<GWTContact>> callback);
	public void findAll(AsyncCallback<List<GWTContact>> callback);
	public void getGoogleContacts(String username, String userpass, String groupId, Map<String,String> googleFieldMap, AsyncCallback<List<GWTContact>> callback);
	public void loginGoogleContact(String username, String userpass, AsyncCallback<Boolean> callback);
	public void getContactGroups(String username, String userpass, AsyncCallback<Map<String,String>> callback);
	public void syncGoogleContacts(List<GWTContact> contacts, AsyncCallback<?> callback);
	public void findAllFiltered(String uuid, AsyncCallback<List<GWTContact>> callback);
	public void update(GWTContact contact, AsyncCallback<?> callback);
	public void addContact(int id, String uuid, AsyncCallback<?> callback); 
}