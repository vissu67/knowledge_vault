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

package com.openkm.servlet.frontend;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTVersion;
import com.openkm.frontend.client.service.OKMTestService;

/**
 * 
 * TestServlet
 * 
 * @author jllort
 *
 */
public class TestServlet extends OKMRemoteServiceServlet implements OKMTestService {
	private static final long serialVersionUID = 5955080308631192466L;
	private static Logger log = LoggerFactory.getLogger(TestServlet.class);

	@Override
	public String StringTest(int size) {
		log.debug("size:"+size);
		String text = "";
		while (text.length()<size) {
			Random randomGenerator = new Random();
			int value = randomGenerator.nextInt(25);
			text += (char) (65+value);
		}
		log.debug(text);
		return text;
	}
	
	@Override
	public List<GWTFolder> folderText(int size) {
		List<GWTFolder> folderList = new ArrayList<GWTFolder>();
		for (int i = 0; i<size; i++) {
			GWTFolder folder = new GWTFolder();
			folder.setPath("some path");
			folder.setParentPath("some parent path");
			folder.setAuthor("author");
			folder.setCreated(new Date());
			folder.setHasChilds(false);
			folder.setName("folder name");
			folder.setPermissions((byte)0);
			folder.setSubscribed(false);
			folder.setUuid("uuid");
			folderList.add(folder);
		}
		
		return folderList;
	}
	
	@Override
	public List<GWTDocument> documentText(int size) {
		List<GWTDocument> documentList = new ArrayList<GWTDocument>();
		for (int i = 0; i<size; i++) {
			GWTDocument doc = new GWTDocument();
			doc.setPath("some path");
			doc.setAuthor("author");
			doc.setActualVersion(new GWTVersion());
			doc.setCreated(new Date());
			doc.setCheckedOut(false);
			doc.setConvertibleToPdf(false);
			doc.setConvertibleToSwf(false);
			doc.setHasNotes(false);
			doc.setLastModified(new Date());
			doc.setLocked(false);
			doc.setMimeType("some mime");
			doc.setName("document name");
			doc.setParentId("some id");
			doc.setPermissions((byte)0);
			doc.setSubscribed(false);
			doc.setUuid("uuid");
			documentList.add(doc);
		}
		
		return documentList;
	}
}