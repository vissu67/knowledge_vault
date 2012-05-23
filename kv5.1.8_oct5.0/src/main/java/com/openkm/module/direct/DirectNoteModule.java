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

package com.openkm.module.direct;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;

import org.apache.jackrabbit.core.NodeImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Note;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.JcrSessionManager;
import com.openkm.core.LockException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.jcr.JCRUtils;
import com.openkm.module.NoteModule;
import com.openkm.module.base.BaseNoteModule;
import com.openkm.module.base.BaseNotificationModule;
import com.openkm.util.UserActivity;

public class DirectNoteModule implements NoteModule {
	private static Logger log = LoggerFactory.getLogger(DirectNoteModule.class);
	
	@Override
	public Note add(String token, String nodePath, String text) throws LockException, 
			PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("add({}, {}, {})", new Object[] { token, nodePath, text });
		Note newNote = null;
		Session session = null;
		Node node = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			node = session.getRootNode().getNode(nodePath.substring(1));
			newNote = BaseNoteModule.add(session, node, text);
						
			// Check subscriptions
			BaseNotificationModule.checkSubscriptions(node, session.getUserID(), "ADD_NOTE", text);

			// Activity log
			UserActivity.log(session.getUserID(), "ADD_NOTE", node.getUUID(), text+", "+nodePath);
		} catch (javax.jcr.PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(node);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(node);
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.lock.LockException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(node);
			throw new LockException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(node);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}
		
		log.debug("add: {}", newNote);
		return newNote;
	}

	@Override
	public void remove(String token, String notePath) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("remove({}, {})", token, notePath);
		Session session = null;
		Node parentNode = null;
		String nid = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Node noteNode = session.getRootNode().getNode(notePath.substring(1));
			nid = ((NodeImpl) noteNode).getIdentifier();
			parentNode = noteNode.getParent();
			
			if (session.getUserID().equals(noteNode.getProperty(Note.USER).getString())) {
				noteNode.remove();
				parentNode.save();
				
				if (!parentNode.hasNodes()) {
					Node primary = parentNode.getParent();
					log.info("Remove mixin '{}' from {}", Note.MIX_TYPE, primary);
					primary.removeMixin(Note.MIX_TYPE);
					primary.save();
				}
			} else {
				throw new AccessDeniedException("Note can only be removed by its creator");
			}
						
			// Activity log
			UserActivity.log(session.getUserID(), "REMOVE_NOTE", nid, notePath);
		} catch (javax.jcr.PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(parentNode);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(parentNode);
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.lock.LockException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(parentNode);
			throw new LockException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(parentNode);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}
		
		log.debug("remove: void");
	}
	
	@Override
	public Note get(String token, String notePath) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("get({}, {})", token, notePath);
		Session session = null;
		Note note = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Node noteNode = session.getRootNode().getNode(notePath.substring(1));
			note = BaseNoteModule.get(noteNode);

			// Activity log
			UserActivity.log(session.getUserID(), "GET_NOTE", ((NodeImpl) noteNode).getIdentifier(), notePath);
		} catch (javax.jcr.PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.lock.LockException e) {
			log.error(e.getMessage(), e);
			throw new LockException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}
		
		log.debug("get: {}", note);
		return note;
	}

	@Override
	public void set(String token, String notePath, String text) throws LockException, 
			PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("set({}, {}, {})", new Object[] { token, notePath, text });
		Session session = null;
		Node noteNode = null;
		
		if (Config.SYSTEM_READONLY) {
			throw new AccessDeniedException("System is in read-only mode");
		}
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			noteNode = session.getRootNode().getNode(notePath.substring(1));
			
			if (session.getUserID().equals(noteNode.getProperty(Note.USER).getString())) {
				noteNode.setProperty(Note.TEXT, text);
				noteNode.save();
			} else {
				throw new AccessDeniedException("Note can only be modified by its creator");
			}
			
			// Check subscriptions
			BaseNotificationModule.checkSubscriptions(noteNode, session.getUserID(), "SET_NOTE", null);
			
			// Activity log
			UserActivity.log(session.getUserID(), "SET_NOTE", ((NodeImpl) noteNode).getIdentifier(), notePath);
		} catch (javax.jcr.PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(noteNode);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(noteNode);
			throw new AccessDeniedException(e.getMessage(), e);
		} catch (javax.jcr.lock.LockException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(noteNode);
			throw new LockException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			JCRUtils.discardsPendingChanges(noteNode);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}
		
		log.debug("set: void");
	}

	@Override
	public List<Note> list(String token, String nodePath) throws PathNotFoundException,
			RepositoryException, DatabaseException {
		log.debug("list({}, {})", token, nodePath);
		List<Note> notes = new ArrayList<Note>();
		Session session = null;
		
		try {
			if (token == null) {
				session = JCRUtils.getSession();
			} else {
				session = JcrSessionManager.getInstance().get(token);
			}
			
			Node nodeNode = session.getRootNode().getNode(nodePath.substring(1));
			Node notesNode = nodeNode.getNode(Note.LIST);
			
			for (NodeIterator nit = notesNode.getNodes(); nit.hasNext(); ) {
				Node noteNode = nit.nextNode();
				Note note = new Note();
				note.setDate(noteNode.getProperty(Note.DATE).getDate());
				note.setUser(noteNode.getProperty(Note.USER).getString());
				note.setText(noteNode.getProperty(Note.TEXT).getString());
				note.setPath(noteNode.getPath());
				notes.add(note);
			}

			// Activity log
			UserActivity.log(session.getUserID(), "LIST_NOTES", nodeNode.getUUID(), nodePath);
		} catch (javax.jcr.PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new PathNotFoundException(e.getMessage(), e);
		} catch (javax.jcr.RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new RepositoryException(e.getMessage(), e);
		} finally {
			if (token == null) JCRUtils.logout(session);
		}

		log.debug("getChilds: {}", notes);
		return notes;
	}
}
