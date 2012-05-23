package com.openkm.module.base;

import javax.jcr.Node;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bsh.EvalError;
import bsh.Interpreter;

import com.openkm.bean.Document;
import com.openkm.bean.Folder;
import com.openkm.bean.Scripting;

public class BaseScriptingModule {
	private static Logger log = LoggerFactory.getLogger(BaseScriptingModule.class);
	
	/**
	 * Check for scripts and evaluate
	 * 
	 * @param node Node modified (Document or Folder)
	 * @param user User who generated the modification event
	 * @param eventType Type of modification event
	 */
	public static void checkScripts(Session session, Node scriptNode, Node eventNode, String eventType) {
		log.debug("checkScripts({}, {}, {}, {})", new Object[] { session, scriptNode, eventNode, eventType });

		try {
			checkScriptsHelper(session, scriptNode, eventNode, eventType);
		} catch (ValueFormatException e) {
			e.printStackTrace();
		} catch (javax.jcr.PathNotFoundException e) {
			e.printStackTrace();
		} catch (javax.jcr.RepositoryException e) {
			e.printStackTrace();
		}

		log.debug("checkScripts: void");
	}

	/**
	 * Check script helper method for recursion.
	 */
	private static void checkScriptsHelper(Session session, Node scriptNode, Node eventNode,
			String eventType) throws javax.jcr.RepositoryException {
		log.debug("checkScriptsHelper({}, {}, {}, {})", new Object[] { session, scriptNode, eventNode,
				eventType });

		if (scriptNode.isNodeType(Folder.TYPE) || scriptNode.isNodeType(Document.TYPE)) {
			if (scriptNode.isNodeType(Scripting.TYPE)) {
				String code = scriptNode.getProperty(Scripting.SCRIPT_CODE).getString();

				// Evaluate script
				Interpreter i = new Interpreter();
				try {
					i.set("session", session);
					i.set("scriptNode", scriptNode);
					i.set("eventNode", eventNode);
					i.set("eventType", eventType);
					i.eval(code);
				} catch (EvalError e) {
					log.warn(e.getMessage(), e);
				}
			}

			// Check for script in parent node
			checkScriptsHelper(session, scriptNode.getParent(), eventNode, eventType);
		}

		log.debug("checkScriptsHelper: void");
	}
}
