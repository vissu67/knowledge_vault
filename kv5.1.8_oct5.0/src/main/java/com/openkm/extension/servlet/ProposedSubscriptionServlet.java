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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.LoginException;
import javax.jcr.Session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.api.OKMAuth;
import com.openkm.core.DatabaseException;
import com.openkm.core.RepositoryException;
import com.openkm.extension.dao.ProposedSubscriptionDAO;
import com.openkm.extension.dao.bean.ProposedSubscriptionReceived;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.extension.GWTProposedSubscriptionReceived;
import com.openkm.frontend.client.contants.service.ErrorCode;
import com.openkm.frontend.client.service.extension.OKMProposedSubscriptionService;
import com.openkm.jcr.JCRUtils;
import com.openkm.principal.PrincipalAdapterException;
import com.openkm.servlet.frontend.OKMRemoteServiceServlet;
import com.openkm.util.GWTUtil;

/**
 * ProposedSubscriptionServlet
 */
public class ProposedSubscriptionServlet extends OKMRemoteServiceServlet implements OKMProposedSubscriptionService {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(ProposedSubscriptionServlet.class);
	
	@Override
	public void send(String uuid, String users, String roles, String comment) throws OKMException {
		Object obj[] = {(Object)uuid,(Object)users, (Object)roles, (Object)comment};
		log.debug("create({}, {}, {}, {})", obj);
		
		try {
			String remoteUser = getThreadLocalRequest().getRemoteUser();
			String to = "";
			if (!users.equals("") && !roles.equals("")) {
				to = users + "," + roles;
			} else {
				to = users + roles;
			}
			List<String> userNames = new ArrayList<String>(Arrays.asList(users.split(",")));
			List<String> roleNames = Arrays.asList(roles.split(","));
			
			for (String role : roleNames) {
				List<String> usersInRole = OKMAuth.getInstance().getUsersByRole(null, role);
				
				for (String user : usersInRole) {
					if (!userNames.contains(user)) {
						userNames.add(user);
					}
				}
			}
			
			// You might not sending messages to youself
			if (userNames.contains(remoteUser)) {
				userNames.remove(remoteUser);
			}
			
			for (String user : userNames) {
				ProposedSubscriptionDAO.send(remoteUser, to, user, uuid, comment);
			}
			
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedSubscriptionService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedSubscriptionService, ErrorCode.CAUSE_PrincipalAdapter), e.getMessage());
		}
		
	}

	@Override
	public Map<String,Long> findProposedSubscriptionsUsersFrom() throws OKMException {
		log.debug("findProposedSubscriptionsUsersFrom()");
		Map<String,Long> received = new HashMap<String, Long>();
		updateSessionManager();
		try {		
			String user = getThreadLocalRequest().getRemoteUser();
			Map<String, Long> unreadMap = ProposedSubscriptionDAO.findProposedSubscriptionsUsersFromUnread(user);
			for (String sender : ProposedSubscriptionDAO.findProposedSubscriptionsUsersFrom(user)) {
				if (unreadMap.containsKey(sender)) {
					received.put(sender, unreadMap.get(sender));
				} else {
					received.put(sender, new Long(0));
				}
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedSubscriptionService, ErrorCode.CAUSE_Database), e.getMessage());
		} 
		log.debug("findProposedSubscriptionsUsersFrom: Map"+received);
		return received;
	}
	
	@Override
	public List<GWTProposedSubscriptionReceived> findProposedSubscriptionByMeFromUser(String user) throws OKMException {
		log.debug("findProposedSubscriptionByMeFromUser()");
		updateSessionManager();
		List<GWTProposedSubscriptionReceived> proposedQuerySubscriptionList = new ArrayList<GWTProposedSubscriptionReceived>();
		Session session = null;
		try {
			session = JCRUtils.getSession();		
			for (ProposedSubscriptionReceived proposedSubscriptionReceived : ProposedSubscriptionDAO.findProposedSubscriptionByMeFromUser(session, getThreadLocalRequest().getRemoteUser(), user)) {
				proposedQuerySubscriptionList.add(GWTUtil.copy(proposedSubscriptionReceived));
			}
			return proposedQuerySubscriptionList;
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedQueryService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedQueryService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (LoginException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedSubscriptionService, ErrorCode.CAUSE_Login), e.getMessage());
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedSubscriptionService, ErrorCode.CAUSE_Repository), e.getMessage());
		} finally {
			JCRUtils.logout(session);
		}
	}

	@Override
	public void markSeen(int msgId) throws OKMException {
		log.debug("markSeen({})", msgId);
		updateSessionManager();
		try {
			ProposedSubscriptionDAO.markSeen(msgId);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedSubscriptionService, ErrorCode.CAUSE_Database), e.getMessage());
		}
		log.debug("markSeen() : void");
	}

	@Override
	public void markAccepted(int msgId) throws OKMException {
		log.debug("markAccepted({})", msgId);
		updateSessionManager();
		try {
			ProposedSubscriptionDAO.markAccepted(msgId);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedSubscriptionService, ErrorCode.CAUSE_Database), e.getMessage());
		}
		log.debug("markAccepted() : void");
	}

	@Override
	public void deleteReceived(int msgId) throws OKMException {
		log.debug("deleteReceived({})", msgId);
		updateSessionManager();
		try {
			ProposedSubscriptionDAO.deleteReceived(msgId);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedSubscriptionService, ErrorCode.CAUSE_Database), e.getMessage());
		}
		log.debug("deleteReceived() : void");
	}
	
	@Override
	public void deleteSent(int msgId) throws OKMException {
		log.debug("deleteSent({})", msgId);
		updateSessionManager();
		try {
			ProposedSubscriptionDAO.deleteSent(msgId);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedSubscriptionService, ErrorCode.CAUSE_Database), e.getMessage());
		}
		log.debug("deleteSent() : void");
	}

	@Override
	public void deleteProposedSubscriptionByMeFromUser(String sender) throws OKMException {
		log.debug("deleteProposedSubscriptionByMeFromUser()");
		updateSessionManager();
		List<String> IdToDelete = new ArrayList<String>();
		Session session = null;
		try {
			session = JCRUtils.getSession();			
			for (ProposedSubscriptionReceived ps : ProposedSubscriptionDAO.findProposedSubscriptionByMeFromUser(session, getThreadLocalRequest().getRemoteUser(), sender)) {
				if (ps.getFrom().equals(sender)) {
					IdToDelete.add(String.valueOf(ps.getId()));
				}
			}
			for (String id : IdToDelete) {
				ProposedSubscriptionDAO.deleteReceived(Integer.valueOf(id));
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedSubscriptionService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedSubscriptionService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (LoginException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedSubscriptionService, ErrorCode.CAUSE_Login), e.getMessage());
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMProposedSubscriptionService, ErrorCode.CAUSE_Repository), e.getMessage());
		} finally {
			JCRUtils.logout(session);
		}
		log.debug("deleteProposedSubscriptionByMeFromUser: void");
	}
}
