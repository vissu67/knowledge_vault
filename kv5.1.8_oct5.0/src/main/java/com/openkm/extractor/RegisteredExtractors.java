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

package com.openkm.extractor;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFormatException;

import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.core.query.lucene.JackrabbitTextExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Document;
import com.openkm.core.Config;
import com.openkm.util.ReaderInputStream;
import com.openkm.util.UserActivity;

public class RegisteredExtractors {
	private static Logger log = LoggerFactory.getLogger(RegisteredExtractors.class);
	private static JackrabbitTextExtractor jte = new JackrabbitTextExtractor("");
	private static final int MIN_EXTRACTION = 16;
	
	/**
	 * Initialize text extractors from REGISTERED_TEXT_EXTRACTORS
	 */
	public static synchronized void init() {
		log.info("Initializing text extractors");
		jte = new JackrabbitTextExtractor(Config.REGISTERED_TEXT_EXTRACTORS);
	}
	
	/**
	 * Return registered content types
	 */
	public static String[] getContentTypes() {
		return jte.getContentTypes();
	}
	
	/**
	 * Extract text to be indexed
	 */
	public static InputStream getText(Node node, String mimeType, String encoding, InputStream is)
			throws ValueFormatException, PathNotFoundException, RepositoryException, IOException {
		log.info("getText({}, {}, {})", new Object[] { mimeType, encoding, is });
		InputStream ret = null;
		boolean failure = false;
		String failureMessage = "Unknown error";
		
		try {
			Reader rd = jte.extractText(is, mimeType, encoding);
			
			// Check for minimum text extraction size
			if (rd.markSupported()) {
				rd.mark(0);
				long sk = rd.skip(MIN_EXTRACTION);
				if (sk < MIN_EXTRACTION) failure = true;
				rd.reset();
			} else {
				log.warn("Mark not supported in {}", rd.getClass().getCanonicalName());
			}
			
			// Convert reader to input stream
			ret = new ReaderInputStream(rd);
		} catch (Exception e) {
			log.warn("Text extraction failure: {}", e.getMessage());
			failureMessage = e.getMessage();
			failure = true;
		}
		
		if (failure || ret == null) {
			if (node != null) {
				log.warn("There was a problem extracting text from '{}'", node.getPath());
				UserActivity.log(node.getSession().getUserID(), "MISC_TEXT_EXTRACTION_FAILURE",
						node.getUUID(), node.getPath() + ", FailureMessage: " + failureMessage);
			}
		}
		
		log.info("getText: {}", ret);
		return ret;
	}
	
	/** 
	 * EXPERIMENTAL  
	 */
	public static void index(Node docNode, Node contNode, String mimeType) throws ValueFormatException,
			PathNotFoundException, RepositoryException, IOException {
		InputStream in = null;
		InputStream out = null;
		
		try {
			in = contNode.getProperty(JcrConstants.JCR_DATA).getStream();
			out = RegisteredExtractors.getText(docNode, mimeType, "UTF-8", in);
			contNode.setProperty(Document.TEXT, out);
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(in);
		}
	}
}
