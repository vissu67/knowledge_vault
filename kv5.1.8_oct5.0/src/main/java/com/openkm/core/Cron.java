/**
 *  OpenKM, Open Document Management System (http://www.openkm.com)
 *  Copyright (C) 2006-2011  Paco Avila & Josep Llort
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

import java.text.ParseException;
import java.util.Calendar;
import java.util.TimerTask;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bsh.EvalError;

import com.kenai.crontabparser.CronTabExpression;
import com.openkm.dao.CronTabDAO;
import com.openkm.dao.bean.CronTab;
import com.openkm.util.ExecutionUtils;
import com.openkm.util.MailUtils;
import com.openkm.util.SecureStore;

public class Cron extends TimerTask {
	private static Logger log = LoggerFactory.getLogger(Cron.class);
	public static final String CRON_TASK = "cronTask";
	
	public void run() {
		log.debug("*** Cron activated ***");
		Calendar cal = Calendar.getInstance();
		
		try {
			for (CronTab ct : CronTabDAO.findAll()) {
				if (ct.isActive()) {
					try {
						CronTabExpression cte = CronTabExpression.parse(ct.getExpression());
						
						if (cte.matches(cal)) {
							log.info("Id: {}, Name: {}, Mime: {}", new Object[] {ct.getId(), ct.getName(),
									ct.getFileMime()});
							
							if (CronTab.BSH.equals(ct.getFileMime())) {
								RunnerBsh runner = new RunnerBsh(ct.getId(),ct.getName(), ct.getMail(),  
										new String(SecureStore.b64Decode(ct.getFileContent())));
								new Thread(runner).start();
							} else if (CronTab.JAR.equals(ct.getFileMime())) {
								RunnerJar runner = new RunnerJar(ct.getId(), ct.getName(), ct.getMail(), 
										SecureStore.b64Decode(ct.getFileContent()));
								new Thread(runner).start();
							}
						}
					} catch (ParseException e) {
						log.warn(e.getMessage() + " : " + ct.getExpression());
					}
				}
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Inner helper class
	 */
	public static class RunnerBsh implements Runnable {
		private String script;
		private String name;
		private String mail;
		private int ctId;
		
		public RunnerBsh(int ctId, String name, String mail, String script) {
			this.script = script;
			this.name = name;
			this.mail = mail;
			this.ctId = ctId;
		}
		
	    public void run() {
	    	if (script != null) {
	    		try {
					CronTabDAO.setLastBegin(ctId);
				} catch (DatabaseException e) {
					log.warn("Error setting last begin in crontab {}: {}", ctId, e.getMessage());
				}
	    		
				try {
					Object[] ret = ExecutionUtils.runScript(script);
					
					try {
						StringBuilder msg = new StringBuilder();
						msg.append("Return: ").append(ret[0]);
						msg.append("\n<hr/>\n");
						msg.append("StdOut: ").append(ret[1]);
						msg.append("\n<hr/>\n");
						msg.append("StdErr: ").append(ret[2]);
						
						if (!mail.equals("")) {
							MailUtils.sendMessage(mail, "Cron task '" + name + "' executed - Ok", msg.toString());
						} else {
							log.warn("Crontab task email is empty: {}", msg);
						}
					} catch (MessagingException e) {
						log.warn("Error sending mail: {}", e.getMessage());
					}
				} catch (EvalError e) {
					try {
						String msg = e.toString();
						
						if (mail.equals("")) {
							log.info(msg);
						} else {
							MailUtils.sendMessage(mail, "Cron task '" + name + "' executed - Error", msg);
						}
					} catch (MessagingException e1) {
						log.warn("Error sending mail: {}", e.getMessage());
					}
				}
				
	    		try {
					CronTabDAO.setLastEnd(ctId);
				} catch (DatabaseException e) {
					log.warn("Error setting last end in crontab {}: {}", ctId, e.getMessage());
				}
	    	}
	    }
	}
	
	/**
	 * Inner helper class
	 */
	public static class RunnerJar implements Runnable {
		private byte[] content;
		private String name;
		private String mail;
		private int ctId;
		
		public RunnerJar(int ctId, String name, String mail, byte[] content) {
			this.content = content;
			this.name = name;
			this.mail = mail;
			this.ctId = ctId;
		}
		
	    public void run() {
	    	if (content != null) {
	    		try {
					CronTabDAO.setLastBegin(ctId);
				} catch (DatabaseException e) {
					log.warn("Error setting last begin in crontab {}: {}", ctId, e.getMessage());
				}
				
				try {
					Object ret = ExecutionUtils.runJar(content, CRON_TASK);
					
					try {
						String msg = (ret == null ? "" : ret.toString());
						
						if (!mail.equals("")) {
							MailUtils.sendMessage(mail, "Cron task '" + name + "' executed - Ok", msg);
						} else {
							log.warn("Crontab task email is empty: {}", msg);
						}
					} catch (MessagingException e) {
						log.warn("Error sending mail: {}", e.getMessage());
					}
				} catch (Exception e) {
					try {
						String msg = e.toString();
						
						if (mail.equals("")) {
							log.info(msg);
						} else {
							MailUtils.sendMessage(mail, "Cron task '" + name + "' executed - Error", msg);
						}
					} catch (MessagingException e1) {
						log.warn("Error sending mail: {}", e.getMessage());
					}
				}
				
				try {
					CronTabDAO.setLastEnd(ctId);
				} catch (DatabaseException e) {
					log.warn("Error setting last end in crontab {}: {}", ctId, e.getMessage());
				}
	    	}
	    }
	}
}
