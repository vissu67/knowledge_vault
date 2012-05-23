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
 * Wrapper for the Snowball stemmer for Spanish // use stemSB
 * Or translation of the Stemmer implemented in C
 * i found here:
 * http://members.unine.ch/jacques.savoy/clef/index.html
 *
 * @author    Olena Medelyan
 */

public class SpanishStemmer extends Stemmer {
	
	private static Logger log = LoggerFactory.getLogger(SpanishStemmer.class);
	
	private static final long serialVersionUID = 1L;
	
	private SpanishStemmerSB stemmer = new SpanishStemmerSB();		
	
	public String stemSB(String str) {
		stemmer.setCurrent(str);
		stemmer.stem();
		return stemmer.getCurrent();
	}

	
	/*  Spanish stemmer tring to remove inflectional suffixes */	
	public String stem(String word) {
		
		int len = word.length()-1;
		
		if (len > 3) {
			
			word = removeSpanishAccent(word);
			
			if (word.endsWith("eses")) {
				//  corteses -> cort�s  
				word = word.substring(0,len-1);
				return word;
			}
			
			if (word.endsWith("ces")) {
				//  dos veces -> una vez  
				word = word.substring(0,len-2);
				word = word + 'z';
				return word;
			}
			
			if (word.endsWith("os") || word.endsWith("as") || word.endsWith("es")) {
				//  ending with -os, -as  or -es 				
				word = word.substring(0,len-1);
				return word;
				
			}
			if (word.endsWith("o") || word.endsWith("a") || word.endsWith("e")) {  
				//  ending with  -o,  -a, or -e 
				word = word.substring(0,len-1);
				return word;
			}
			
		}  
		return word;         
	}
	
	
	private String removeSpanishAccent (String word)
	{ 
		word = word.replaceAll("à|á|â|ä","a");
		word = word.replaceAll("ò|ó|ô|ö","o");
		word = word.replaceAll("è|é|ê|ë","e");
		word = word.replaceAll("ù|ú|û|ü","a");
		word = word.replaceAll("ì|í|î|ï","a");

		return word;
	}
	
	/**
	 * The main method. // for testing  
	 */
	public static void main(String[] ops) {
	
		SpanishStemmer s = new SpanishStemmer();
		log.info(s.stem("veces"));	
	}
	
	
}