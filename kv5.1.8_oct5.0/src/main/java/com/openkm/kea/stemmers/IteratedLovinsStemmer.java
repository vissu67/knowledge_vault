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

package com.openkm.kea.stemmers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the iterated version of the Lovins stemmer.
 *
 * @author Eibe Frank (eibe@cs.waikato.ac.nz)
 * @version 1.0
 */
public class IteratedLovinsStemmer extends LovinsStemmer {

  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger log = LoggerFactory.getLogger(IteratedLovinsStemmer.class);

/**
   * Iterated stemming of the given word.
   */
  public String stem(String str) {

    if (str.length() <= 2) {
      return str;
    }
    String stemmed = super.stem(str);
    while (!stemmed.equals(str)) {
      str = stemmed;
      stemmed = super.stem(stemmed);
    }
    return stemmed;
  }

  /**
   * Stems text coming into stdin and writes it to stdout.
   */
  public static void main(String[] ops) {

    IteratedLovinsStemmer ls = new IteratedLovinsStemmer();

    try {
      int num;
      StringBuffer wordBuffer = new StringBuffer();
      while ((num = System.in.read()) != -1) {
	char c = (char)num;
	if (((num >= (int)'A') && (num <= (int)'Z')) ||
	    ((num >= (int)'a') && (num <= (int)'z'))) {
	  wordBuffer.append(c);
	} else {
	  if (wordBuffer.length() > 0) {
	    log.info(ls.stem(wordBuffer.toString().
				     toLowerCase()));
	    wordBuffer = new StringBuffer();
	  }
	  log.info(""+c);
	}
      }
    } catch (Exception e) {
      System.err.println(e.getMessage());
    }
  }
}
    

