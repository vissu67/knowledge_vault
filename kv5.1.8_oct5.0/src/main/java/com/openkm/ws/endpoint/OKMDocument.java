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

package com.openkm.ws.endpoint;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.apache.commons.io.IOUtils;
import org.jboss.annotation.security.SecurityDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Document;
import com.openkm.bean.Version;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.FileSizeExceededException;
import com.openkm.core.ItemExistsException;
import com.openkm.core.LockException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.core.UnsupportedMimeTypeException;
import com.openkm.core.UserQuotaExceededException;
import com.openkm.core.VersionException;
import com.openkm.core.VirusDetectedException;
import com.openkm.extension.core.ExtensionException;
import com.openkm.module.DocumentModule;
import com.openkm.module.ModuleManager;
import com.openkm.principal.PrincipalAdapterException;

/**
 * Servlet Class
 * 
 * @web.servlet name="OKMDocument" 
 * @web.servlet-mapping url-pattern="/OKMDocument"
 */

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
@SecurityDomain("OpenKM")
public class OKMDocument {
	private static Logger log = LoggerFactory.getLogger(OKMDocument.class);

	@WebMethod
	public Document create(@WebParam(name = "token") String token,
			@WebParam(name = "doc") Document doc,
			@WebParam(name = "content") byte[] content) throws IOException, UnsupportedMimeTypeException,
			FileSizeExceededException, UserQuotaExceededException, VirusDetectedException,
			ItemExistsException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException, ExtensionException {
		log.debug("create({})", doc);
		DocumentModule dm = ModuleManager.getDocumentModule();
		ByteArrayInputStream bais = new ByteArrayInputStream(content);
		Document newDocument = dm.create(token, doc, bais);
		bais.close();
		log.debug("create: {}", newDocument);
		return newDocument;
	}
	
	@WebMethod
	public Document createSimple(@WebParam(name = "token") String token,
			@WebParam(name = "docPath") String docPath,
			@WebParam(name = "content") byte[] content) throws IOException, UnsupportedMimeTypeException,
			FileSizeExceededException, UserQuotaExceededException, VirusDetectedException, 
			ItemExistsException, PathNotFoundException, AccessDeniedException, RepositoryException,
			DatabaseException, ExtensionException {
		log.debug("createSimple({})", docPath);
		DocumentModule dm = ModuleManager.getDocumentModule();
		ByteArrayInputStream bais = new ByteArrayInputStream(content);
		Document doc = new Document();
		doc.setPath(docPath);
		Document newDocument = dm.create(token, doc, bais);
		bais.close();
		log.debug("createSimple: {}", newDocument);
		return newDocument;
	}

	@WebMethod
	public void delete(@WebParam(name = "token") String token,
			@WebParam(name = "docPath") String docPath) throws AccessDeniedException, RepositoryException,
			PathNotFoundException, LockException, DatabaseException {
		log.debug("delete({}, {})", token, docPath);
		DocumentModule dm = ModuleManager.getDocumentModule();
		dm.delete(token, docPath);
		log.debug("delete: void");
	}
	
	@WebMethod
	public Document getProperties(@WebParam(name = "token") String token,
			@WebParam(name = "docPath") String docPath) throws RepositoryException, PathNotFoundException,
			DatabaseException {
		log.debug("getProperties({}, {})", token, docPath);
		DocumentModule dm = ModuleManager.getDocumentModule();
		Document doc = dm.getProperties(token, docPath);
		log.debug("getProperties: {}", doc);
		return doc;
	}

	@WebMethod
	public byte[] getContent(@WebParam(name = "token") String token,
			@WebParam(name = "docPath") String docPath,
			@WebParam(name = "checkout") boolean checkout) throws RepositoryException, IOException,
			PathNotFoundException, DatabaseException {
		log.debug("getContent({}, {}, {})", new Object[] { token, docPath, checkout });
		DocumentModule dm = ModuleManager.getDocumentModule();
		InputStream is = dm.getContent(token, docPath, checkout);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy(is, baos);
		byte[] data = baos.toByteArray();
		log.debug("getContent: {}", data);
		return data;
	}

	@WebMethod
	public byte[] getContentByVersion(@WebParam(name = "token") String token,
			@WebParam(name = "docPath") String docPath,
			@WebParam(name = "versionId") String versionId) throws RepositoryException, IOException,
			PathNotFoundException, DatabaseException {
		log.debug("getContentByVersion({}, {}, {})", new Object[] { token,  docPath, versionId });
		DocumentModule dm = ModuleManager.getDocumentModule();
		InputStream is = dm.getContentByVersion(token, docPath, versionId); 
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		IOUtils.copy(is, baos);
		byte[] data = baos.toByteArray();
		log.debug("getContentByVersion: {}", data);
		return data;
	}

	@WebMethod
	public Document[] getChilds(@WebParam(name = "token") String token,
			@WebParam(name = "fldPath") String fldPath) throws RepositoryException, PathNotFoundException,
			DatabaseException {
		log.debug("getChilds({}, {})", token, fldPath);
		DocumentModule dm = ModuleManager.getDocumentModule();
		List<Document> col = dm.getChilds(token, fldPath);
		Document[] result = (Document[]) col.toArray(new Document[col.size()]);
		log.debug("getChilds: {}", result);
		return result;
	}
	
	@WebMethod
	public Document rename(@WebParam(name = "token") String token,
			@WebParam(name = "docPath") String docPath,
			@WebParam(name = "newName") String newName) throws AccessDeniedException, RepositoryException,
			PathNotFoundException, ItemExistsException, DatabaseException {
		log.debug("rename({}, {}, {})", new Object[] { token, docPath, newName });
		DocumentModule dm = ModuleManager.getDocumentModule();
		Document renamedDocument = dm.rename(token, docPath, newName);
		log.debug("rename: {}", renamedDocument);
		return renamedDocument;
	}
	
	@WebMethod
	public void setProperties(@WebParam(name = "token") String token,
			@WebParam(name = "doc") Document doc) throws AccessDeniedException,
			RepositoryException, PathNotFoundException, VersionException, LockException, DatabaseException {
		log.debug("setProperties({}, {})", token, doc);
		DocumentModule dm = ModuleManager.getDocumentModule();
		dm.setProperties(token, doc);
		log.debug("setProperties: void");
	}

	@WebMethod
	public void checkout(@WebParam(name = "token") String token,
			@WebParam(name = "docPath") String docPath) throws AccessDeniedException,
			RepositoryException, PathNotFoundException, LockException, DatabaseException {
		log.debug("checkout({}, {})", token, docPath);
		DocumentModule dm = ModuleManager.getDocumentModule();
		dm.checkout(token, docPath);
		log.debug("checkout: void");
	}

	@WebMethod
	public void cancelCheckout(@WebParam(name = "token") String token,
			@WebParam(name = "docPath") String docPath) throws AccessDeniedException,
			RepositoryException, PathNotFoundException, LockException, DatabaseException {
		log.debug("cancelCheckout({}, {})", token, docPath);
		DocumentModule dm = ModuleManager.getDocumentModule();
		dm.cancelCheckout(token, docPath);
		log.debug("cancelCheckout: void");
	}
	
	@WebMethod
	public void forceCancelCheckout(@WebParam(name = "token") String token,
			@WebParam(name = "docPath") String docPath) throws AccessDeniedException, RepositoryException,
			PathNotFoundException, LockException, DatabaseException, PrincipalAdapterException {
		log.debug("forceCancelCheckout({}, {})", token, docPath);
		DocumentModule dm = ModuleManager.getDocumentModule();
		dm.forceCancelCheckout(token, docPath);
		log.debug("forceCancelCheckout: void");
	}
	
	@WebMethod
	public Version checkin(@WebParam(name = "token") String token,
			@WebParam(name = "docPath") String docPath,
			@WebParam(name = "comment") String comment) throws LockException, VersionException,
			PathNotFoundException, AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("checkin({}, {} ,{})", new Object[] { token, docPath, comment });
		DocumentModule dm = ModuleManager.getDocumentModule();
		Version version = dm.checkin(token, docPath, comment);
		log.debug("checkin: {}", version);
		return version;
	}

	@WebMethod
	public Version[] getVersionHistory(@WebParam(name = "token") String token,
			@WebParam(name = "docPath") String docPath) throws PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("getVersionHistory({}, {})", token, docPath);
		DocumentModule dm = ModuleManager.getDocumentModule();
		List<Version> col = dm.getVersionHistory(token, docPath);
		Version[] result = (Version[]) col.toArray(new Version[col.size()]);
		log.debug("getVersionHistory: {}", result);
		return result;
	}

	@WebMethod
	public void setContent(@WebParam(name = "token") String token,
			@WebParam(name = "docPath") String docPath,
			@WebParam(name = "content") byte[] content) throws FileSizeExceededException, 
			UserQuotaExceededException, VirusDetectedException, VersionException, LockException,
			PathNotFoundException, AccessDeniedException, RepositoryException, IOException, 
			DatabaseException {
		log.debug("setContent({}, {}, {})", new Object[] { token, docPath, content });
		DocumentModule dm = ModuleManager.getDocumentModule();
		ByteArrayInputStream bais = new ByteArrayInputStream(content);
		dm.setContent(token, docPath, bais);
		bais.close();
		log.debug("setContent: void");
	}

	@WebMethod
	public void lock(@WebParam(name = "token") String token,
			@WebParam(name = "docPath") String docPath) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("lock({}, {})", token, docPath);
		DocumentModule dm = ModuleManager.getDocumentModule();
		dm.lock(token, docPath);
		log.debug("lock: void");
	}

	@WebMethod
	public void unlock(@WebParam(name = "token") String token,
			@WebParam(name = "docPath") String docPath) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException {
		log.debug("unlock({}, {})", token, docPath);
		DocumentModule dm = ModuleManager.getDocumentModule();
		dm.unlock(token, docPath);
		log.debug("unlock: void");
	}
	
	@WebMethod
	public void forceUnlock(@WebParam(name = "token") String token,
			@WebParam(name = "docPath") String docPath) throws LockException, PathNotFoundException,
			AccessDeniedException, RepositoryException, DatabaseException, PrincipalAdapterException {
		log.debug("forceUnlock({}, {})", token, docPath);
		DocumentModule dm = ModuleManager.getDocumentModule();
		dm.forceUnlock(token, docPath);
		log.debug("forceUnlock: void");
	}

	@WebMethod
	public void purge(@WebParam(name = "token") String token,
			@WebParam(name = "docPath") String docPath) throws AccessDeniedException, RepositoryException,
			PathNotFoundException, DatabaseException {
		log.debug("purge({}, {})", token, docPath);
		DocumentModule dm = ModuleManager.getDocumentModule();
		dm.purge(token, docPath);
		log.debug("purge: void");
	}

	@WebMethod
	public void move(@WebParam(name = "token") String token,
			@WebParam(name = "docPath") String docPath, 
			@WebParam(name = "fldPath") String fldPath) throws LockException, PathNotFoundException,
			ItemExistsException, AccessDeniedException, RepositoryException, DatabaseException,
			ExtensionException {
		log.debug("move({}, {}, {})", new Object[] { token, docPath, fldPath });
		DocumentModule dm = ModuleManager.getDocumentModule();
		dm.move(token, docPath, fldPath);
		log.debug("move: void");
	}
	
	@WebMethod
	public void restoreVersion(@WebParam(name = "token") String token,
			@WebParam(name = "docPath") String docPath,
			@WebParam(name = "versionId") String versionId) throws AccessDeniedException, 
			RepositoryException, PathNotFoundException, DatabaseException {
		log.debug("restoreVersion({}, {}, {})", new Object[] { token, docPath, versionId });
		DocumentModule dm = ModuleManager.getDocumentModule();
		dm.restoreVersion(token, docPath, versionId);
		log.debug("restoreVersion: void");
	}
	
	@WebMethod
	public void purgeVersionHistory(@WebParam(name = "token") String token,
			@WebParam(name = "docPath") String docPath) throws AccessDeniedException, RepositoryException,
			PathNotFoundException, DatabaseException {
		log.debug("purgeVersionHistory({}, {})", token, docPath);
		DocumentModule dm = ModuleManager.getDocumentModule();
		dm.purgeVersionHistory(token, docPath);
		log.debug("purgeVersionHistory: void");
	}
	
	@WebMethod
	public long getVersionHistorySize(@WebParam(name = "token") String token,
			@WebParam(name = "docPath") String docPath) throws RepositoryException, PathNotFoundException,
			DatabaseException {
		log.debug("getVersionHistorySize({}, {})", token, docPath);
		DocumentModule dm = ModuleManager.getDocumentModule();
		long size = dm.getVersionHistorySize(token, docPath);
		log.debug("getVersionHistorySize: {}", size);
		return size;
	}
	
	@WebMethod
	public boolean isValid(@WebParam(name = "token") String token,
			@WebParam(name = "docPath") String docPath) throws PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("isValid({}, {})", token, docPath);
		DocumentModule dm = ModuleManager.getDocumentModule();
		boolean valid = dm.isValid(token, docPath);
		log.debug("isValid: {}", valid);
		return valid;
	}
	
	@WebMethod
	public String getPath(@WebParam(name = "token") String token,
			@WebParam(name = "uuid") String uuid) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("getPath({}, {})", token, uuid);
		DocumentModule dm = ModuleManager.getDocumentModule();
		String path = dm.getPath(token, uuid);
		log.debug("getPath: {}", path);
		return path;
	}
}
