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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.extractor.AbstractTextExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.ConversionException;
import com.openkm.util.DocConverter;

/**
 * Text extractor for JPEG image documents.
 * Use java metadata extraction library from 
 * http://www.drewnoakes.com/code/exif/index.html
 */
public class OOTextExtractor extends AbstractTextExtractor {

    /**
     * Logger instance.
     */
    private static final Logger log = LoggerFactory.getLogger(OOTextExtractor.class);
    //private static OpenOfficeConnection connection = null;

    /**
     * Creates a new <code>JpegTextExtractor</code> instance.
     */
    public OOTextExtractor() {
        super(new String[]{
        		// MsExcel
        		"application/vnd.ms-excel", "application/msexcel", "application/excel",
        		
        		// MsPowerPoint
        		"application/vnd.ms-powerpoint", "application/mspowerpoint", "application/powerpoint",
        		
        		// MsWord
        		"application/vnd.ms-word", "application/msword", "application/word",
        		
        		// MsOffice2007
        		"application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.template",
                "application/vnd.openxmlformats-officedocument.presentationml.template",
                "application/vnd.openxmlformats-officedocument.presentationml.slideshow",
                "application/vnd.openxmlformats-officedocument.presentationml.presentation",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.template"
        		});
    }
    
    //-------------------------------------------------------< TextExtractor >

    /**
     * {@inheritDoc}
     */ 
	public Reader extractText(InputStream stream, String type, String encoding) throws IOException {
		String ret = "";
		File fIn = File.createTempFile("okm", ".doc");
		File fOut = File.createTempFile("okm", ".txt");
		
    	try {
    		FileOutputStream fos = new FileOutputStream(fIn);
    		IOUtils.copy(stream, fos);
			fos.flush();
			fos.close();
			
    		// Convert to text
			DocConverter.getInstance().convert(fIn, type, fOut);
			ret = FileUtils.readFileToString(fOut);
    		log.debug("TEXT: "+ret);
            return new StringReader(ret);
		} catch (ConversionException e) {
			log.warn("Failed to extract text", e);
			return new StringReader("");
		} finally {
			stream.close();
			fIn.delete();
			fOut.delete();
        }
    }
}
