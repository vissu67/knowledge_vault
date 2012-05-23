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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.JcrSessionInfo;
import com.openkm.util.UUIDGenerator;

/**
 * @author pavila
 */
public class JcrSessionManager {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(JcrSessionManager.class);
	private static JcrSessionManager instance = new JcrSessionManager();
	private Map<String, JcrSessionInfo> sessions = new HashMap<String, JcrSessionInfo>();
	private static String systemToken;
	
	/**
	 * Prevents class instantiation
	 */
	private JcrSessionManager() {
	}
	
	/**
	 * Instantiate a SessionManager.
	 */
	public static JcrSessionManager getInstance() {
		return instance;
	}
	
	/**
	 * Get system token
	 */
	public String getSystemToken() {
		return systemToken;
	}
	
	/**
	 * Set system session
	 */
	public void putSystemSession(Session session) {
		systemToken = UUIDGenerator.generate(this);
		add(systemToken, session);
	}
	
	/**
	 * Get system session
	 */
	public Session getSystemSession() {
		return get(getSystemToken());
	}
	
	/**
	 * Add a new session
	 */
	public synchronized void add(String token, Session session) {
		JcrSessionInfo si = new JcrSessionInfo();
		si.setSession(session);
		si.setCreation(Calendar.getInstance());
		si.setLastAccess(Calendar.getInstance());
		sessions.put(token, si);
	}
	
	/**
	 * Return a session
	 */
	public Session get(String token) {
		JcrSessionInfo si = (JcrSessionInfo) sessions.get(token);
		
		if (si != null) {
			si.setLastAccess(Calendar.getInstance());
			return si.getSession();
		}
		
		return null;
	}
	
	/**
	 * Return a session info
	 */
	public JcrSessionInfo getInfo(String token) {
		return sessions.get(token);
	}
	
	/**
	 * Remove a session
	 */
	public synchronized void remove(String token) {
		sessions.remove(token);
	}
		
	/**
	 * Return all active tokens
	 */
	public List<String> getTokens() {
		List<String> list = new ArrayList<String>();
		
		for (String token : sessions.keySet()) {
			if (!systemToken.equals(token)) {
				list.add(token);
			}
		}
		
		return list;
	}
	
	/**
	 * Get active sessions
	 */
	public Map<String, JcrSessionInfo> getSessions() {
		return sessions;
	}
}
