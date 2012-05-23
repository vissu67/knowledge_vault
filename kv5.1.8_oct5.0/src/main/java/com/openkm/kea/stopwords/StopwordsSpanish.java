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

package com.openkm.kea.stopwords;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;

import com.openkm.kea.metadata.WorkspaceHelper;

/**
 * Class that can test whether a given string is a stop word.
 * Lowercases all words before the test.
 *
 * @author Eibe Frank (eibe@cs.waikato.ac.nz)
 * @version 1.0
 */
public class StopwordsSpanish extends Stopwords {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/** The hashtable containing the list of stopwords */
	private static Hashtable<String,Double> m_Stopwords = null;
	
	public StopwordsSpanish() {
		
		if (m_Stopwords == null) {
			m_Stopwords = new Hashtable<String,Double>();
			Double dummy = new Double(0);
			File txt = new File(WorkspaceHelper.KEA_STOPWORDS_PATH);	
			InputStreamReader is;
			String sw = null;
			try {
				is = new InputStreamReader(new FileInputStream(txt), "UTF-8");
				BufferedReader br = new BufferedReader(is);				
				while ((sw=br.readLine()) != null)  {
					m_Stopwords.put(sw, dummy);   
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/** 
	 * Returns true if the given string is a stop word.
	 */
	public boolean isStopword(String str) {
		return m_Stopwords.containsKey(str.toLowerCase());
	}
}
		
		
