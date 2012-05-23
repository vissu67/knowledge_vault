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

package com.openkm.principal;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.Config;

/**
 * http://forums.sun.com/thread.jspa?threadID=581444
 * http://java.sun.com/docs/books/tutorial/jndi/ops/filter.html
 * http://www.openkm.com/Configuration/903-Rejavac-cannot-find-symbol-PrincipalAdapter.html
 */
public class LdapPrincipalAdapter implements PrincipalAdapter {
	private static Logger log = LoggerFactory.getLogger(LdapPrincipalAdapter.class);

	@Override
	public List<String> getUsers() throws PrincipalAdapterException {
		log.debug("getUsers()");
		List<String> list = new ArrayList<String>();
		List<String> ldap = ldapSearch(
				Config.PRINCIPAL_LDAP_SERVER,
				Config.PRINCIPAL_LDAP_SECURITY_PRINCIPAL,
				Config.PRINCIPAL_LDAP_SECURITY_CREDENTIALS,
				Config.PRINCIPAL_LDAP_USER_SEARCH_BASE,
				Config.PRINCIPAL_LDAP_USER_SEARCH_FILTER,
				Config.PRINCIPAL_LDAP_USER_ATTRIBUTE);
		
		for (Iterator<String> it = ldap.iterator(); it.hasNext(); ) {
			String user = it.next();
			
			if (!Config.SYSTEM_USER.equals(user)) {
				if (Config.SYSTEM_LOGIN_LOWERCASE) {
					user = user.toLowerCase();
				}
				
				list.add(user);
			}
		}

		log.debug("getUsers: {}", list);
		return list;
	}

	@Override
	public List<String> getRoles() throws PrincipalAdapterException {
		log.debug("getRoles()");
		List<String> list = new ArrayList<String>();
		List<String> ldap = ldapSearch(
				Config.PRINCIPAL_LDAP_SERVER,
				Config.PRINCIPAL_LDAP_SECURITY_PRINCIPAL,
				Config.PRINCIPAL_LDAP_SECURITY_CREDENTIALS,
				Config.PRINCIPAL_LDAP_ROLE_SEARCH_BASE, 
				Config.PRINCIPAL_LDAP_ROLE_SEARCH_FILTER,
				Config.PRINCIPAL_LDAP_ROLE_ATTRIBUTE);
		
		for (Iterator<String> it = ldap.iterator(); it.hasNext(); ) {
			String role = it.next();
			list.add(role);
		}

		log.debug("getRoles: {}", list);
		return list;
	}
	
	@Override
	public String getMail(String user) throws PrincipalAdapterException {
		log.debug("getMail({})", user);
		String mail = null;
		
		List<String> ldap = ldapSearch(
				Config.PRINCIPAL_LDAP_SERVER,
				Config.PRINCIPAL_LDAP_SECURITY_PRINCIPAL,
				Config.PRINCIPAL_LDAP_SECURITY_CREDENTIALS,
				MessageFormat.format(Config.PRINCIPAL_LDAP_MAIL_SEARCH_BASE, user), 
				MessageFormat.format(Config.PRINCIPAL_LDAP_MAIL_SEARCH_FILTER, user), 
				Config.PRINCIPAL_LDAP_MAIL_ATTRIBUTE);
		if (!ldap.isEmpty()) {
			mail = ldap.get(0);
		}
		
		log.debug("getMail: {}", mail);
		return mail;
	}
	
	@Override
	public String getName(String user) throws PrincipalAdapterException {
		return LdapPrincipalAdapter.class.getCanonicalName();
	}
	
	@Override
	public List<String> getUsersByRole(String role) throws PrincipalAdapterException {
		log.debug("getUsersByRole({})", role);
		List<String> list = new ArrayList<String>();
		List<String> ldap = ldapSearch(
				Config.PRINCIPAL_LDAP_SERVER,
				Config.PRINCIPAL_LDAP_SECURITY_PRINCIPAL,
				Config.PRINCIPAL_LDAP_SECURITY_CREDENTIALS,
				MessageFormat.format(Config.PRINCIPAL_LDAP_USERS_BY_ROLE_SEARCH_BASE, role), 
				MessageFormat.format(Config.PRINCIPAL_LDAP_USERS_BY_ROLE_SEARCH_FILTER, role),
				Config.PRINCIPAL_LDAP_USERS_BY_ROLE_ATTRIBUTE);
		
		for (Iterator<String> it = ldap.iterator(); it.hasNext(); ) {
			String user = it.next();
			
			if (!Config.SYSTEM_USER.equals(user)) {
				if (Config.SYSTEM_LOGIN_LOWERCASE) {
					user = user.toLowerCase();
				}
				
				list.add(user);
			}
		}

		log.debug("getUsersByRole: {}", list);
		return list;
	}
	
	@Override
	public List<String> getRolesByUser(String user) throws PrincipalAdapterException {
		log.debug("getRolesByUser({})", user);
		List<String> list = new ArrayList<String>();
		List<String> ldap = ldapSearch(
				Config.PRINCIPAL_LDAP_SERVER,
				Config.PRINCIPAL_LDAP_SECURITY_PRINCIPAL,
				Config.PRINCIPAL_LDAP_SECURITY_CREDENTIALS,
				MessageFormat.format(Config.PRINCIPAL_LDAP_ROLES_BY_USER_SEARCH_BASE, user),
				MessageFormat.format(Config.PRINCIPAL_LDAP_ROLES_BY_USER_SEARCH_FILTER, user),
				Config.PRINCIPAL_LDAP_ROLES_BY_USER_ATTRIBUTE);
		
		for (Iterator<String> it = ldap.iterator(); it.hasNext(); ) {
			String role = it.next();
			list.add(role);
		}

		log.debug("getRolesByUser: {}", list);
		return list;
	}
	
	/**
	 * LDAP Search
	 */
	public List<String> ldapSearch(String url, String principal, String credentials, 
			String searchBase, String searchFilter, String attribute) {
		log.debug("ldapSearch({}, {}, {}, {}, {}, {})", new Object[] {
				url, principal, credentials, searchBase, searchFilter, attribute } );
		List<String> al = new ArrayList<String>();
		Hashtable<String, String> env = new Hashtable<String, String>();

		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.PROVIDER_URL, url);
		
		// Optional is some cases (Max OS/X)
		if (!principal.equals(""))
			env.put(Context.SECURITY_PRINCIPAL, principal);
		if (!credentials.equals(""))
			env.put(Context.SECURITY_CREDENTIALS, credentials);			
		
		try {
			DirContext ctx = new InitialDirContext(env);
			SearchControls searchCtls = new SearchControls();
			searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration<SearchResult> results = ctx.search(searchBase, searchFilter, searchCtls);
			
			while (results.hasMore()) {
				SearchResult searchResult = (SearchResult) results.next();
				Attributes attributes = searchResult.getAttributes();
				
				if (attribute.equals("")) {
					StringBuilder sb = new StringBuilder();
					
					for (NamingEnumeration<?> ne = attributes.getAll(); ne.hasMore(); ) {
						Attribute attr = (Attribute) ne.nextElement();
						sb.append(attr.toString());
						sb.append("\n");
					}
					
					al.add(sb.toString());
				} else {
					Attribute attrib = attributes.get(attribute);
					
					if (attrib != null) {
						// Handle multi-value attributes
						for (NamingEnumeration<?> ne = attrib.getAll(); ne.hasMore(); ) {
							String value = (String) ne.nextElement();
							
							// If FQDN get only main part
							if (value.startsWith("CN=") || value.startsWith("cn=")) {
								String cn = value.substring(3, value.indexOf(','));
								log.debug("FQDN: {}, CN: {}", value, cn);
								al.add(cn);
							} else {
								al.add(value);
							}
						}
					}
				}
			}

			ctx.close();
		} catch (NamingException e) {
			e.printStackTrace();
		}
		
		log.debug("ldapSearch: {}", al);
		return al;
	}
}
