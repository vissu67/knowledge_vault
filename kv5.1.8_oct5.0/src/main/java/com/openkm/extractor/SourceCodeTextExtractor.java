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
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.apache.jackrabbit.extractor.AbstractTextExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Text extractor for source code.
 */
public class SourceCodeTextExtractor extends AbstractTextExtractor {

	/**
	 * Logger instance.
	 */
	private static final Logger log = LoggerFactory.getLogger(SourceCodeTextExtractor.class);

	/**
	 * Creates a new <code>AudioTextExtractor</code> instance.
	 */
	public SourceCodeTextExtractor() {
		super(new String[] { "text/x-java", "text/css", "text/x-csrc", "text/x-sql",
				"application/x-php", "application/javascript" });
	}

	// -------------------------------------------------------< TextExtractor >

	/**
	 * {@inheritDoc}
	 */
	public Reader extractText(InputStream stream, String type, String encoding) throws IOException {
		try {
			if (encoding != null) {
				return new InputStreamReader(stream, encoding);
			}
		} catch (UnsupportedEncodingException e) {
			log.warn("Unsupported encoding '{}', using default ({}) instead.", encoding, System
					.getProperty("file.encoding"));
		}
		return new InputStreamReader(stream);
	}
}
