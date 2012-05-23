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

package com.openkm.kea.metadata;

import java.io.File;

import com.openkm.core.Config;

/**
 * WorkspaceHelper
 * 
 * @author jllort
 * 
 */
public class WorkspaceHelper {

	// The files path
	public static final String RDF_SKOS_VOVABULARY_PATH = Config.HOME_DIR + File.separator
			+ Config.KEA_THESAURUS_SKOS_FILE;

	public static final String RDF_OWL_VOVABULARY_PATH = Config.HOME_DIR + File.separator
			+ Config.KEA_THESAURUS_OWL_FILE;

	public static final String KEA_MODEL_PATH = Config.HOME_DIR + File.separator + Config.KEA_MODEL_FILE;

	// It's not final because model builder must change this path ( to solve
	// problem with stopwords file on stopwordX class )
	public static String KEA_STOPWORDS_PATH = Config.HOME_DIR + File.separator + Config.KEA_STOPWORDS_FILE;

	// Language
	public static final String KEA_LANGUAGE = getLanguage();

	// Stop words class name
	public static final String KEA_STOPWORDS_CLASSNAME = getStopWordsClassName();

	/**
	 * getLanguage
	 * 
	 * @return The language
	 */
	public static String getLanguage() {
		String lang = "";

		if (!Config.KEA_STOPWORDS_FILE.equals("")) {
			lang = Config.KEA_STOPWORDS_FILE.substring(Config.KEA_STOPWORDS_FILE.indexOf("_") + 1,
					Config.KEA_STOPWORDS_FILE.indexOf("."));
		}

		return lang;
	}

	/**
	 * getStopWordsClassName
	 * 
	 * @return The class name
	 */
	public static String getStopWordsClassName() {
		String className = null;

		if (KEA_LANGUAGE.equals("en")) {
			className = "com.openkm.kea.stopwords.StopwordsEnglish";
		} else if (KEA_LANGUAGE.equals("es")) {
			className = "com.openkm.kea.stopwords.StopwordsSpanish";
		} else if (KEA_LANGUAGE.equals("de")) {
			className = "com.openkm.kea.stopwords.StopwordsGerman";
		} else if (KEA_LANGUAGE.equals("fr")) {
			className = "com.openkm.kea.stopwords.StopwordsFrench";
		} else {
			className = "com.openkm.kea.stopwords.Stopwords";
		}

		return className;
	}
}
