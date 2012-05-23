package com.openkm.module.base;

import java.util.Calendar;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Note;

public class BaseNoteModule {
	private static Logger log = LoggerFactory.getLogger(BaseNoteModule.class);
	
	/**
	 * Add a note to a node.
	 */
	public static Note add(Session session, Node node, String text) throws NoSuchNodeTypeException,
			VersionException, ConstraintViolationException, LockException, AccessDeniedException,
			ItemExistsException, InvalidItemStateException, ReferentialIntegrityException,
			RepositoryException {
		if (!node.isNodeType(Note.MIX_TYPE)) {
			log.debug("Adding mixing '{}' to {}", Note.MIX_TYPE, node.getPath());
			node.addMixin(Note.MIX_TYPE);
			node.save();
		}
		
		Node notesNode = node.getNode(Note.LIST);
		Calendar cal = Calendar.getInstance();
		Node noteNode = notesNode.addNode(cal.getTimeInMillis()+"", Note.TYPE);
		noteNode.setProperty(Note.DATE, cal);
		noteNode.setProperty(Note.USER, session.getUserID());
		noteNode.setProperty(Note.TEXT, text);
		notesNode.save();
		
		// Retrieve stored values
		Note newNote = get(noteNode);
		
		return newNote;
	}
	
	/**
	 * Read note values
	 */
	public static Note get(Node noteNode) throws javax.jcr.PathNotFoundException, javax.jcr.RepositoryException {
		Note note = new Note();
		
		note.setDate(noteNode.getProperty(Note.DATE).getDate());
		note.setUser(noteNode.getProperty(Note.USER).getString());
		note.setText(noteNode.getProperty(Note.TEXT).getString());
		note.setPath(noteNode.getPath());
		
		return note;
	}
}
