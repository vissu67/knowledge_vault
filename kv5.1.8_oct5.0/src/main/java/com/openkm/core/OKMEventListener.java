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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.observation.Event;
import javax.jcr.observation.EventIterator;
import javax.jcr.observation.EventListener;
import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Notification;
import com.openkm.module.direct.DirectRepositoryModule;
import com.openkm.util.MailUtils;

public class OKMEventListener implements EventListener {
	private static Logger log = LoggerFactory.getLogger(OKMEventListener.class);
	
	public void onEvent(EventIterator arg0) {
		Session system = DirectRepositoryModule.getSystemSession();
		
		while (arg0.hasNext()) {
			Event evn = arg0.nextEvent();
			String eventType = "";
			
			try {
				if (!evn.getPath().endsWith("okm:content") &&
					!evn.getPath().endsWith("okm:size") &&
					!evn.getPath().endsWith("okm:author") &&
					!evn.getPath().endsWith("jcr:lastModified") &&
					!evn.getPath().endsWith("jcr:lockOwner") &&
					!evn.getPath().endsWith("jcr:lockIsDeep") &&
					!evn.getPath().endsWith("jcr:isCheckedOut") &&
					!evn.getPath().endsWith("jcr:baseVersion") &&
					!evn.getPath().endsWith("jcr:predecessors")) {
						switch (evn.getType()) {
							case Event.NODE_ADDED:
								eventType = "NODE_ADDED";
								break;

							case Event.NODE_REMOVED:
								eventType = "NODE_REMOVED";
								break;
								
							case Event.PROPERTY_ADDED:
								eventType = "PROPERTY_ADDED";
								break;
								
							case Event.PROPERTY_REMOVED:
								eventType = "PROPERTY_REMOVED";
								break;
								
							case Event.PROPERTY_CHANGED:
								if (evn.getPath().endsWith("jcr:data")) {
									eventType = "NODE_CHANGED";
								} else {
									eventType = "PROPERTY_CHANGED";
								}
								break;
						}
						
						log.info("***** Type: "+eventType+" - "+evn.getPath());
						
						if (evn.getType() != Event.NODE_REMOVED && evn.getType() != Event.PROPERTY_REMOVED) {
							Item item = system.getItem(evn.getPath());
							for (; !item.isNode(); item = item.getParent()) {
								log.info("[1][1] "+item.getPath());
							}
							
							Node node = (Node) item;
							for (; !node.isNodeType(Notification.TYPE); node = node.getParent()) {
								log.info("[2][2] "+node.getPath());
							}
													
							Value[] subscriptors = node.getProperty(Notification.SUBSCRIPTORS).getValues();
							for (int i=0; i<subscriptors.length; i++) {
								log.info("[3][3] "+subscriptors[i].getString());
							}
						}
						
						List<String> emails = new ArrayList<String>();
						String body = 
							"URL: " + Config.APPLICATION_URL+"?nodePath=" + URLEncoder.encode(evn.getPath(), "UTF-8") + "\n" +
							"File: " + evn.getPath() + "\n" +
							"User: " + evn.getUserID() + "\n" +
							"Action: " + eventType;
						
						MailUtils.sendMessage("monkiki@gmail.com", emails, "OpenKM notification", body);
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (MessagingException e) {
				e.printStackTrace();
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
	}
}
