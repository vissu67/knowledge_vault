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

package com.openkm.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.jcr.Credentials;
import javax.jcr.SimpleCredentials;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.jackrabbit.core.security.AnonymousPrincipal;
import org.apache.jackrabbit.core.security.CredentialsCallback;
import org.apache.jackrabbit.core.security.SecurityConstants;
import org.apache.jackrabbit.core.security.SystemPrincipal;
import org.apache.jackrabbit.core.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pavila
 *
 * JBoss security framewokk (several login modules):
 * http://wiki.jboss.org/wiki/Wiki.jsp?page=JBossSX
 * 
 * JBoss UsersRolesLoginModule.java source code:
 * http://wiki.jboss.org/wiki/Wiki.jsp?page=JBossSX
 */
@SuppressWarnings("deprecation")
public class OKMLoginModule implements LoginModule {
	private static Logger log = LoggerFactory.getLogger(OKMLoginModule.class);
	private String usersRsrcName = "users.properties";
	private String rolesRsrcName = "roles.properties";
	private Properties users;
	private Properties roles;
    private Subject subject;
    private CallbackHandler callbackHandler;
    private final Set<Principal> principals = new HashSet<Principal>();
    private String defaultUserId = null;
	
	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
		log.debug("initialize("+subject+", "+callbackHandler+", "+sharedState+", "+options+")");

		this.subject = subject;
		this.callbackHandler = callbackHandler;
		
		try {
			this.users = loadProperties(usersRsrcName);
			this.roles = loadProperties(rolesRsrcName);
		} catch (Exception e) {
			// Note that although this exception isn't passed on, users or roles will be null
	        // so that any call to login will throw a LoginException.
	        log.error("Failed to load users/passwords/role files", e);
	    }

		log.debug("initialize: void");
	}

	@Override
	public boolean login() throws LoginException {
		log.debug("login()");
		boolean ok;
		
        // prompt for a user name and password
        if (callbackHandler == null) {
            throw new LoginException("no CallbackHandler available");
        }

		if (users == null) throw new LoginException("Missing users.properties file.");
	    if (roles == null) throw new LoginException("Missing roles.properties file.");

		boolean authenticated = false;
		principals.clear();

	    try {
            // Get credentials using a JAAS callback
            CredentialsCallback ccb = new CredentialsCallback();
            callbackHandler.handle(new Callback[] { ccb });
            Credentials creds = ccb.getCredentials();
            
            // Use the credentials to set up principals
            if (creds != null) {
                if (creds instanceof SimpleCredentials) {
                    SimpleCredentials sc = (SimpleCredentials) creds;
                    // authenticate

                    Object attr = sc.getAttribute(SecurityConstants.IMPERSONATOR_ATTRIBUTE);
                    if (attr != null && attr instanceof Subject) {
                        Subject impersonator = (Subject) attr;
                        // @todo check privileges to 'impersonate' the user represented by the supplied credentials
                        
                        log.debug("***** RARO ******");
                        log.debug(impersonator.toString());
                        log.debug("***** RARO ******");
                    } else {
                        // @todo implement simple username/password authentication
                    	
                        log.debug("***********");
                        log.debug(sc.getUserID()+" -> "+new String(sc.getPassword()));
                        log.debug("***********");
                        
                        if (users.getProperty(sc.getUserID()).equals(new String(sc.getPassword()))) {
                        	log.debug("*********** BIEN");
                        	authenticated = true;
                        } else {
                        	log.debug("*********** MAL");
                        	authenticated = false;
                        }
                    }

                    if ("anonymousUserId".equals(sc.getUserID())) {
                        principals.add(new AnonymousPrincipal());
                        authenticated = true;
                    } else {
                        // else assume the user we authenticated is the UserPrincipal
                        principals.add(new UserPrincipal(sc.getUserID()));
                        //java.security.acl.;
                        authenticated = true;
                    }
                }
            } else if (defaultUserId != null) {
                //principals.add(new UserPrincipal(defaultUserId));
                principals.add(new SystemPrincipal());
                authenticated = true;
            } else {
                principals.add(new AnonymousPrincipal());
                authenticated = true;
            }
        } catch (java.io.IOException ioe) {
            throw new LoginException(ioe.toString());
        } catch (UnsupportedCallbackException uce) {
            throw new LoginException(uce.getCallback().toString() + " not available");
        }        
        
        if (authenticated) {
        	ok = !principals.isEmpty();
        } else {
            // authentication failed: clean out state
            principals.clear();
            throw new FailedLoginException();
        }
        
        log.debug("login: "+ok);
        return ok;
	}

	@Override
	public boolean commit() throws LoginException {
		log.debug("commit()");
		boolean ok;
		
		if (principals.isEmpty()) {
            ok = false;
        } else {
            // add a principals (authenticated identities) to the Subject
            subject.getPrincipals().addAll(principals);
            ok = true;
        }
	
		log.debug("commit: "+ok);
		return ok;
	}

	@Override
	public boolean abort() throws LoginException {
		log.debug("abort()");
		boolean ok;
		
		if (principals.isEmpty()) {
            ok = false;
        } else {
            logout();
            ok = true;
        }
        
		log.debug("abort: "+ok);
		return ok;
	}

	@Override
	public boolean logout() throws LoginException {
		log.debug("logout()");
		boolean ok = true;
		
		subject.getPrincipals().removeAll(principals);
        principals.clear();
		
		log.debug("logout: "+ok);
		return ok;
	}
	
	/** 
	 * Utility method which loads the given properties file and returns a
	 * Properties object containing the key,value pairs in that file.
	 * The properties files should be in the class path as this method looks
	 * to the thread context class loader (TCL) to locate the resource. If the
	 * TCL is a URLClassLoader the findResource(String) method is first tried.
	 * If this fails or the TCL is not a URLClassLoader getResource(String) is
	 * tried.
	 * @param propertiesName - the name of the properties file resource
	 * @param log - the logger used for trace level messages
	 * @return the loaded properties file if found
	 * @exception IOException thrown if the properties file cannot be found
	 *    or loaded 
	 */
	@SuppressWarnings("unused")
	static Properties loadProperties(String propertiesName) throws IOException {
		log.debug("loadProperties("+propertiesName+")");
		Properties bundle = null;
	  
	    InputStream is = new FileInputStream(propertiesName);
	    if (is != null) {
	    	bundle = new Properties();
	        bundle.load(is);
	    } else {
	        throw new IOException("Properties file " + propertiesName + " not avilable");
	    }
	    
	    log.debug("loadProperties: "+bundle);
	    return bundle;
	}
}
