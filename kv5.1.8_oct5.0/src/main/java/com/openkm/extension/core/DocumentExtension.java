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

package com.openkm.extension.core;

import java.io.File;
import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.Session;

import com.openkm.bean.Document;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.FileSizeExceededException;
import com.openkm.core.ItemExistsException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.Ref;
import com.openkm.core.RepositoryException;
import com.openkm.core.UnsupportedMimeTypeException;
import com.openkm.core.UserQuotaExceededException;
import com.openkm.core.VirusDetectedException;

public interface DocumentExtension extends Extension {
	/**
	 * Executed BEFORE document CREATE.
	 */
	public void preCreate(Session session, Ref<Node> parentNode, Ref<File> content, Ref<Document> doc)
		throws UnsupportedMimeTypeException, FileSizeExceededException, UserQuotaExceededException,
		VirusDetectedException, ItemExistsException, PathNotFoundException, AccessDeniedException, 
		RepositoryException, IOException, DatabaseException, ExtensionException;
	
	/**
	 * Executed AFTER document CREATE.
	 */
	public void postCreate(Session session, Ref<Node> parentNode, Ref<Node> docNode)
		throws UnsupportedMimeTypeException, FileSizeExceededException, UserQuotaExceededException,
		VirusDetectedException, ItemExistsException, PathNotFoundException, AccessDeniedException, 
		RepositoryException, IOException, DatabaseException, ExtensionException;
	
	/**
	 * Executed BEFORE document MOVE.
	 */
	public void preMove(Session session, Ref<Node> srcDocNode, Ref<Node> dstFldNode)
		throws PathNotFoundException, ItemExistsException, AccessDeniedException, RepositoryException,
		DatabaseException, ExtensionException;
	
	/**
	 * Executed AFTER document MOVE.
	 */
	public void postMove(Session session, Ref<Node> srcFldNode, Ref<Node> dstDocNode)
		throws PathNotFoundException, ItemExistsException, AccessDeniedException, RepositoryException,
		DatabaseException, ExtensionException;
}
