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

package com.openkm.extension.servlet;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gdata.client.Query;
import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.contacts.ContactGroupEntry;
import com.google.gdata.data.contacts.ContactGroupFeed;
import com.google.gdata.data.contacts.UserDefinedField;
import com.google.gdata.data.extensions.Name;
import com.google.gdata.data.extensions.PhoneNumber;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import com.openkm.core.DatabaseException;
import com.openkm.extension.dao.ContactDAO;
import com.openkm.extension.dao.bean.Contact;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.extension.GWTContact;
import com.openkm.frontend.client.contants.service.ErrorCode;
import com.openkm.frontend.client.service.extension.OKMContactService;
import com.openkm.frontend.client.util.ContactComparator;
import com.openkm.servlet.frontend.OKMRemoteServiceServlet;
import com.openkm.util.GWTUtil;

/**
 * ContactServlet
 */
public class ContactServlet extends OKMRemoteServiceServlet implements OKMContactService {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(ContactServlet.class);
	
	@Override
	public void create(String uuid, GWTContact contact) throws OKMException {
		try {
			int id = ContactDAO.create( GWTUtil.copy(contact)); // Create
			if (uuid!=null) {
				Contact newContact = ContactDAO.findByPk(id);		// Find by pk
				newContact.getUuids().add(uuid);
				ContactDAO.update(newContact);						// Update with document uuid
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMContactService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}
	
	@Override
	public void delete(int id) throws OKMException {
		try {
			ContactDAO.delete(id);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMContactService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}
	
	@Override
	public void delete(int id, String uuid) throws OKMException {
		try {
			Contact contact = ContactDAO.findByPk(id);	// Find by pk
			contact.getUuids().remove(uuid);
			ContactDAO.update(contact);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMContactService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}
	
	@Override
	public List<GWTContact> findByUuid(String uuid) throws OKMException {
		List<GWTContact> contacts = new ArrayList<GWTContact>();
		try {
			for (Contact contact : ContactDAO.findByUuid(uuid)) {
				contacts.add(GWTUtil.copy(contact, uuid));
			}
			Collections.sort(contacts, ContactComparator.getInstance());
			return contacts;
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMContactService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}
	
	@Override
	public List<GWTContact> findAll() throws OKMException {
		List<GWTContact> contacts = new ArrayList<GWTContact>();
		try {
			for (Contact contact : ContactDAO.findAll()) {
				contacts.add(GWTUtil.copy(contact, null));
			}
			Collections.sort(contacts, ContactComparator.getInstance());
			return contacts;
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMContactService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}
	
	@Override
	public List<GWTContact> getGoogleContacts(String username, String userpass, String groupId, Map<String,String> googleFieldMap) throws OKMException {
		try {
			List<GWTContact> contactList = new ArrayList<GWTContact>();
			ContactsService googleService = new ContactsService("OpenKM");
			googleService.setUserCredentials(username, userpass);
			
			// Request the feed
			URL feedUrl = new URL("https://www.google.com/m8/feeds/contacts/default/full");
			ContactFeed resultFeed = googleService.getFeed(feedUrl, ContactFeed.class);
			Query contactQuery = new Query(feedUrl);
			contactQuery.setMaxResults(resultFeed.getTotalResults());
			contactQuery.setStringCustomParameter("group", groupId);
			resultFeed = googleService.query(contactQuery, ContactFeed.class);
			for (ContactEntry entry : resultFeed.getEntries()) {
				// Only contacts with name will be added
				if (entry.hasName()) {
					GWTContact contact = new GWTContact();
					contact.setExternalId(entry.getId());
					contact.setOrigin("google");
					Name name = entry.getName();
					if (name.hasFullName()) {
						contact.setName(name.getFullName().getValue());
					} 
					if (entry.hasPostalAddresses()) {
						contact.setAddress(entry.getPostalAddresses().get(0).getValue());
					}
					if (entry.hasEmailAddresses()) {
						contact.setMail(entry.getEmailAddresses().get(0).getAddress());
					}
					
					if (entry.hasPhoneNumbers()) {
						boolean mobile = false;
						boolean work = false;
						boolean home = false;
						boolean principal = false;
						boolean fax = false;
						boolean homeFax = false;
						String homePhone = "";
						String principalPhone = "";
						String homeFaxPhone = "";
						
						// Capturing all telephone numbers types
						for (PhoneNumber phone : entry.getPhoneNumbers()) {
							String rel = phone.getRel();
							if (!mobile && rel.equals("http://schemas.google.com/g/2005#mobile")) {
								mobile = true;
								contact.setMobile(phone.getPhoneNumber());
							} else if (!work && rel.equals("http://schemas.google.com/g/2005#work")) {
								work = true;
								contact.setPhone(phone.getPhoneNumber());
							} else if (!home && rel.equals("http://schemas.google.com/g/2005#home")) {
								home = true;
								homePhone = phone.getPhoneNumber();
							} else if (!principal && rel.equals("http://schemas.google.com/g/2005#main")) {
								principal = true;
								principalPhone = phone.getPhoneNumber();
							} else if (!fax && rel.equals("http://schemas.google.com/g/2005#work_fax")) {
								fax = true;
								contact.setFax(phone.getPhoneNumber());
							} else if (!homeFax && rel.equals("http://schemas.google.com/g/2005#home_fax")) {
								homeFax = true;
								homeFaxPhone = phone.getPhoneNumber();
							}
							
							if (mobile && work && fax) {
								break;
							}
						}
						// Trying setting some phone
						if (!work && (principal || home)) {
							if (!principalPhone.equals("")) {
								contact.setPhone(principalPhone);
							} else if (!homePhone.equals("")) {
								contact.setPhone(homePhone);
							} 
						}
						// Trying setting some fax
						if (!fax && homeFax) {
							if (!homeFaxPhone.equals("")) {
								contact.setFax(homeFaxPhone);
							}
						}
						
						// Getting user defined fields by mapping
						if (!googleFieldMap.isEmpty()) {
							for (UserDefinedField userfield : entry.getUserDefinedFields()) {
								if (googleFieldMap.keySet().contains(userfield.getKey())) {
									String key = googleFieldMap.get(userfield.getKey());
									if (key.equals("name")) {
										contact.setName(userfield.getValue());
									} else if (key.equals("mail")) {
										contact.setMail(userfield.getValue());
									} else if (key.equals("phone")) {
										contact.setPhone(userfield.getValue());
									} else if (key.equals("mobile")) {
										contact.setMobile(userfield.getValue());
									} else if (key.equals("fax")) {
										contact.setFax(userfield.getValue());
									} else if (key.equals("address")) {
										contact.setAddress(userfield.getValue());
									} else if (key.equals("city")) {
										contact.setCity(userfield.getValue());
									} else if (key.equals("postalCode")) {
										contact.setPostalCode(userfield.getValue());
									} else if (key.equals("province")) {
										contact.setProvince(userfield.getValue());
									} else if (key.equals("country")) {
										contact.setCountry(userfield.getValue());
									} else if (key.equals("web")) {
										contact.setWeb(userfield.getValue());
									} else if (key.equals("notes")) {
										contact.setNotes(userfield.getValue());
									} 
								}
							}
						}
					}
					if (entry.hasWebsites()) {
						contact.setWeb(entry.getWebsites().get(0).getHref());
					}
					contactList.add(contact);
				}
			}
			Collections.sort(contactList, ContactComparator.getInstance());
			return contactList;
		} catch (AuthenticationException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMContactService, ErrorCode.CAUSE_Authentication), e.getMessage());
		} catch (ServiceException e) {
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMContactService, ErrorCode.CAUSE_Google), e.getMessage());
		} catch (IOException e) {
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMContactService, ErrorCode.CAUSE_IO), e.getMessage());
		}
	}
	
	@Override
	public Boolean loginGoogleContact(String username, String userpass) throws OKMException {
		Boolean logged = new Boolean(true);
		try {
			ContactsService googleService = new ContactsService("OpenKM");
			googleService.setUserCredentials(username, userpass);
		} catch (AuthenticationException e) {
			logged = new Boolean(false);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMContactService, ErrorCode.CAUSE_General), e.getMessage());
		}
		return logged;
	}
	
	@Override
	public Map<String,String> getContactGroups(String username, String userpass) throws OKMException {
		Map<String,String> contactGroups = new HashMap<String, String>();
		try {
			ContactsService googleService = new ContactsService("OpenKM");
			googleService.setUserCredentials(username, userpass);
			
			URL feedUrl = new URL("https://www.google.com/m8/feeds/groups/default/full");
			ContactGroupFeed resultFeed = googleService.getFeed(feedUrl, ContactGroupFeed.class);
			Query groupQuery = new Query(feedUrl);
			groupQuery.setMaxResults(resultFeed.getTotalResults());
			resultFeed = googleService.query(groupQuery, ContactGroupFeed.class); 
			for (ContactGroupEntry entry : resultFeed.getEntries()) {
				contactGroups.put(entry.getTitle().getPlainText(), entry.getId());
			}

		} catch (AuthenticationException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMContactService, ErrorCode.CAUSE_Authentication), e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMContactService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (ServiceException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMContactService, ErrorCode.CAUSE_Google), e.getMessage());
		}
		return contactGroups;
	}
	
	@Override
	public void syncGoogleContacts(List<GWTContact> contacts) throws OKMException {
		try {
			for (GWTContact contact : contacts) {
				Contact tmp = ContactDAO.findByOrigin(contact.getExternalId(), contact.getOrigin()); 
				if (tmp!=null) {
					contact.setId(tmp.getId()); 
					ContactDAO.update(GWTUtil.copy(contact));
				} else {
					ContactDAO.create(GWTUtil.copy(contact)); 
				} 
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMContactService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}
	
	@Override
	public List<GWTContact> findAllFiltered(String uuid) throws OKMException {
		List<GWTContact> contacts = new ArrayList<GWTContact>();
		try {
			for (Contact contact : ContactDAO.findAllFiltered(uuid)) {
				contacts.add(GWTUtil.copy(contact, null));
			}
			Collections.sort(contacts, ContactComparator.getInstance());
			return contacts;
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMContactService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}
	
	@Override
	public void update(GWTContact contact) throws OKMException {
		try {
			Contact contactToUpdate = GWTUtil.copy(contact);
			contactToUpdate.setUuids(ContactDAO.findByPk(contact.getId()).getUuids()); // Loading uuids	
			ContactDAO.update(contactToUpdate);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMContactService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}
	
	@Override
	public void addContact(int id, String uuid) throws OKMException {
		try {
			Contact contact = ContactDAO.findByPk(id);	// Find by pk
			// Only add new uuid not existing ones
			if (!contact.getUuids().contains(uuid)) {
				contact.getUuids().add(uuid);
				ContactDAO.update(contact);					// Update with document uuid
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMContactService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}
}