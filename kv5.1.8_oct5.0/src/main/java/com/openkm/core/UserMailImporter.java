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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.api.OKMAuth;
import com.openkm.dao.MailAccountDAO;
import com.openkm.dao.bean.MailAccount;
import com.openkm.extension.core.ExtensionException;
import com.openkm.principal.PrincipalAdapterException;
import com.openkm.util.MailUtils;

public class UserMailImporter extends TimerTask {
	private static Logger log = LoggerFactory.getLogger(UserMailImporter.class);
	private static volatile boolean running = false;
	private List<String> exceptionMessages = new ArrayList<String>();
	
	public boolean isRunning() {
		return running;
	}
	
	public List<String> getExceptionMessages() {
		return exceptionMessages;
	}
	
	@Override
	public void run() {
		if (running) {
			log.warn("*** User mail importer already running ***");
		} else {
			running = true;
			exceptionMessages = new ArrayList<String>();
			log.info("*** User mail importer activated ***");
			
			try {
				if (Config.SYSTEM_READONLY) {
					exceptionMessages.add("Warning: System in read-only mode");
					log.warn("*** System in read-only mode ***");
				} else {
					String systemToken = JcrSessionManager.getInstance().getSystemToken();
					Collection<String> users = OKMAuth.getInstance().getUsers(systemToken);
					
					for (Iterator<String> usrIt = users.iterator(); usrIt.hasNext(); ) {
						String uid = usrIt.next();
						List<MailAccount> mailAccounts = MailAccountDAO.findByUser(uid, true);
						
						for (Iterator<MailAccount> maIt = mailAccounts.iterator(); maIt.hasNext(); ) {
							MailAccount ma = maIt.next();
							
							if (Config.SYSTEM_READONLY) {
								exceptionMessages.add("Warning: System in read-only mode");
								log.warn("*** System in read-only mode ***");
							} else {
								String exceptionMessage = MailUtils.importMessages(uid, ma);
								
								if (exceptionMessage != null) {
									exceptionMessages.add("Id: " + ma.getId() + ", User: " + ma.getUser() + 
										", Error: " + exceptionMessage);
								}
							}
						}
					}
				}
			} catch (RepositoryException e) {
				log.error(e.getMessage(), e);
				exceptionMessages.add(e.getMessage());
			} catch (DatabaseException e) {
				log.error(e.getMessage(), e);
				exceptionMessages.add(e.getMessage());
			} catch (PathNotFoundException e) {
				log.error(e.getMessage(), e);
				exceptionMessages.add(e.getMessage());
			} catch (ItemExistsException e) {
				log.error(e.getMessage(), e);
				exceptionMessages.add(e.getMessage());
			} catch (VirusDetectedException e) {
				log.error(e.getMessage(), e);
				exceptionMessages.add(e.getMessage());
			} catch (AccessDeniedException e) {
				log.error(e.getMessage(), e);
				exceptionMessages.add(e.getMessage());
			} catch (PrincipalAdapterException e) {
				log.error(e.getMessage(), e);
				exceptionMessages.add(e.getMessage());
			} catch (UserQuotaExceededException e) {
				log.error(e.getMessage(), e);
				exceptionMessages.add(e.getMessage());
			} catch (ExtensionException e) {
				log.error(e.getMessage(), e);
				exceptionMessages.add(e.getMessage());
			} finally {
				running = false;
			}
		}
	}
}
