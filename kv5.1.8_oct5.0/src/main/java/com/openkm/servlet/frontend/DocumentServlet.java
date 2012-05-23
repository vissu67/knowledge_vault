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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.jooreports.templates.DocumentTemplateException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.DocumentException;
import com.openkm.api.OKMDocument;
import com.openkm.api.OKMFolder;
import com.openkm.api.OKMPropertyGroup;
import com.openkm.api.OKMRepository;
import com.openkm.api.OKMSearch;
import com.openkm.bean.Document;
import com.openkm.bean.PropertyGroup;
import com.openkm.bean.QueryResult;
import com.openkm.bean.Version;
import com.openkm.bean.form.FormElement;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.ConversionException;
import com.openkm.core.DatabaseException;
import com.openkm.core.FileSizeExceededException;
import com.openkm.core.ItemExistsException;
import com.openkm.core.LockException;
import com.openkm.core.NoSuchGroupException;
import com.openkm.core.NoSuchPropertyException;
import com.openkm.core.ParseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.core.UnsupportedMimeTypeException;
import com.openkm.core.UserQuotaExceededException;
import com.openkm.core.VersionException;
import com.openkm.core.VirusDetectedException;
import com.openkm.dao.bean.QueryParams;
import com.openkm.extension.core.ExtensionException;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTVersion;
import com.openkm.frontend.client.bean.form.GWTFormElement;
import com.openkm.frontend.client.contants.service.ErrorCode;
import com.openkm.frontend.client.service.OKMDocumentService;
import com.openkm.frontend.client.util.DocumentComparator;
import com.openkm.jcr.JCRUtils;
import com.openkm.util.DocConverter;
import com.openkm.util.FileUtils;
import com.openkm.util.GWTUtil;
import com.openkm.util.OOUtils;
import com.openkm.util.PDFUtils;
import com.openkm.util.TemplateUtils;

import freemarker.template.TemplateException;

/**
 * Directory tree service
 */
public class DocumentServlet extends OKMRemoteServiceServlet implements OKMDocumentService {
	private static Logger log = LoggerFactory.getLogger(DocumentServlet.class);
	private static final long serialVersionUID = 5746570509074299745L;
	
	@Override
	public List<GWTDocument> getChilds(String fldPath) throws OKMException {
		log.debug("getDocumentChilds({})", fldPath);
		List<GWTDocument> docList = new ArrayList<GWTDocument>();
		updateSessionManager();

		try {
			if (fldPath == null) {
				fldPath = OKMRepository.getInstance().getRootFolder(null).getPath();
			}
			
			// Case thesaurus view must search documents in keywords 
			if (fldPath.startsWith("/okm:thesaurus")){

				QueryParams queryParams = new QueryParams();
				Set<String> keywords = new HashSet<String>();
				keywords.add(fldPath.substring(fldPath.lastIndexOf("/") + 1).replace(" ", "_"));
				queryParams.setKeywords(keywords);
				Collection<QueryResult> results = OKMSearch.getInstance().find(null, queryParams);
				for (Iterator<QueryResult> it = results.iterator(); it.hasNext();) {		
					QueryResult queryResult = it.next();
					if (queryResult.getDocument()!=null) {
						GWTDocument docClient = GWTUtil.copy(queryResult.getDocument());
						docList.add(docClient);
					}
				}
			} else if (fldPath.startsWith("/okm:categories")){

				//TODO: Possible optimization getting folder really could not be needed we've got UUID in GWT UI
				String uuid = OKMFolder.getInstance().getProperties(null, fldPath).getUuid();
				Collection<Document> results = OKMSearch.getInstance().getCategorizedDocuments(null, uuid);
				for (Iterator<Document> it = results.iterator(); it.hasNext();) {		
					GWTDocument docClient = GWTUtil.copy(it.next());
					docList.add(docClient);
				}
			} else {

				log.debug("ParentFolder: {}", fldPath);
				Collection<Document> col = OKMDocument.getInstance().getChilds(null, fldPath);
				
				for (Iterator<Document> it = col.iterator(); it.hasNext();) {		
					Document doc = it.next();
					log.debug("Document: {}", doc);
					GWTDocument docClient = GWTUtil.copy(doc);
					docList.add(docClient);
				}
			}

			Collections.sort(docList, DocumentComparator.getInstance());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_General), e.getMessage());
		}

		log.debug("getDocumentChilds: {}", docList);
		return docList;
	}
	
	@Override
	public List<GWTVersion> getVersionHistory (String docPath) throws OKMException {
		log.debug("getVersionHistory({})", docPath);
		List<GWTVersion> versionList = new ArrayList<GWTVersion>();
		updateSessionManager();
	
		try {
			Collection<Version> col = OKMDocument.getInstance().getVersionHistory(null, docPath);
			
			for (Iterator<Version> it = col.iterator(); it.hasNext();){		
				Version version = it.next();
				log.debug("version: {}", version);
				GWTVersion versionClient = GWTUtil.copy(version);
				versionList.add(versionClient); 
			}
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("getVersionHistory: {}", versionList);
		return versionList;
	}
	
	@Override
	public void delete(String docPath) throws OKMException {
		log.debug("delete({})", docPath);
		updateSessionManager();
		
		try {
			OKMDocument.getInstance().delete(null, docPath);
		} catch (LockException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Lock), e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("delete: void");
	}
	
	@Override
	public void checkout(String docPath) throws OKMException {
		log.debug("checkout({})", docPath);
		updateSessionManager();
		
		try {
			OKMDocument.getInstance().checkout(null, docPath);
		} catch (LockException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Lock), e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("checkout: void");
	}
	
	//added by vissu on feb20 for zohoapi
	public String zoho(String docPath) throws OKMException {
		System.out.println("DocumentServlet.java--zoho");
		log.debug("zoho({})", docPath);
		String url = null;	//added by vissu on feb22
		updateSessionManager();
		
		try {
			url = OKMDocument.getInstance().zoho(null, docPath);	//added by vissu on feb22
		} catch (LockException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Lock), e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("zoho: url");
		return url;
	}
	
	@Override
	public void cancelCheckout(String docPath) throws OKMException {
		log.debug("cancelCheckout({})", docPath);
		updateSessionManager();
		
		try {
			OKMDocument.getInstance().cancelCheckout(null, docPath);
		} catch (LockException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Lock), e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("cancelCheckout: void");
	}
	
	@Override
	public void lock(String docPath) throws OKMException {
		log.debug("lock({})", docPath);
		updateSessionManager();
		
		try {
			OKMDocument.getInstance().lock(null, docPath);
		} catch (LockException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Lock), e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("lock: void");
	}
	
	@Override
	public void unlock(String docPath) throws OKMException {
		log.debug("lock({})", docPath);
		updateSessionManager();
		
		try {
			OKMDocument.getInstance().unlock(null, docPath);
		} catch (LockException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_UnLock), e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("lock: void");
	}
	
	@Override
	public GWTDocument rename(String docPath, String newName) throws OKMException {
		log.debug("rename({}, {})", docPath, newName);
		GWTDocument gWTDocument = new GWTDocument();
		updateSessionManager();
		
		try {
			gWTDocument = GWTUtil.copy(OKMDocument.getInstance().rename(null, docPath, newName));
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (ItemExistsException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_ItemExists), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("rename: {}", gWTDocument);
		return gWTDocument;
	}
	
	@Override
	public void move(String docPath, String destPath) throws OKMException {
		log.debug("move({}, {})", docPath, destPath);
		updateSessionManager();
		
		try {
			OKMDocument.getInstance().move(null, docPath, destPath);
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (ItemExistsException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_ItemExists), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("move: void");
	}
	
	@Override
	public void purge(String docPath) throws OKMException {
		log.debug("purge({})", docPath);
		updateSessionManager();
		
		try {
			OKMDocument.getInstance().purge(null, docPath);
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("purge: void");
	}

	@Override
	public void restoreVersion(String docPath, String versionId) throws OKMException {
		log.debug("restoreVersion({}, {})", docPath, versionId);
		updateSessionManager();
		
		try {
			OKMDocument.getInstance().restoreVersion(null, docPath, versionId);
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("restoreVersion: void");
	}
	
	@Override
	public GWTDocument get(String docPath) throws OKMException {
		log.debug("get({})", docPath);
		GWTDocument gWTDocument = new GWTDocument();
		updateSessionManager();
		
		try {
			gWTDocument = GWTUtil.copy(OKMDocument.getInstance().getProperties(null, docPath));
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("get: {}", gWTDocument);
		return gWTDocument;
	}
	
	@Override
	public void copy(String docPath, String fldPath) throws OKMException {
		log.debug("copy({}, {})", docPath, fldPath);
		updateSessionManager();
		
		try {
			OKMDocument.getInstance().copy(null, docPath, fldPath);
		} catch (ItemExistsException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_ItemExists), e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("copy: void");
	}
	
	@Override
	public Boolean isValid(String docPath) throws OKMException {
		log.debug("isValid({})", docPath);
		updateSessionManager();
		
		try {
			return new Boolean(OKMDocument.getInstance().isValid(null, docPath));
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_General), e.getMessage());
		}
	}
	
	@Override
	public Long getVersionHistorySize(String docPath) throws OKMException {
		log.debug("getVersionHistorySize({})", docPath);
		updateSessionManager();
	
		try {
			return new Long(OKMDocument.getInstance().getVersionHistorySize(null, docPath));			
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_General), e.getMessage());
		}
	}
	
	@Override
	public void purgeVersionHistory(String docPath) throws OKMException {
		log.debug("purgeVersionHistory({})", docPath);
		updateSessionManager();
	
		try {
			OKMDocument.getInstance().purgeVersionHistory(null, docPath);			
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("purgeVersionHistory: void");
	}
	
	@Override
	public void forceUnlock(String docPath) throws OKMException {
		log.debug("forceUnlock({})", docPath);
		updateSessionManager();
	
		try {
			OKMDocument.getInstance().forceUnlock(null, docPath);		
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("forceUnlock: void");
	}
	
	@Override
	public void forceCancelCheckout(String docPath) throws OKMException {
		log.debug("forceCancelCheckout({})", docPath);
		updateSessionManager();
	
		try {
			OKMDocument.getInstance().forceCancelCheckout(null, docPath);		
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("forceCancelCheckout: void");
	}
	
	@Override
	public String createFromTemplate(String tplPath, String destinationPath, List<GWTFormElement> formProperties,
			Map<String, List<Map<String, String>>> tableProperties) throws OKMException {
		log.debug("createFromTemplate({},{})", tplPath, destinationPath);
		updateSessionManager();
		File tmp = null;
		InputStream fis = null;
		
		try {
			Document docTpl = OKMDocument.getInstance().getProperties(null, tplPath);
			tmp = tmpFromTemplate(docTpl, formProperties, tableProperties);
			
			// Change fileName after conversion
			if (docTpl.getMimeType().equals("text/html")) {
				destinationPath = destinationPath.substring(0, destinationPath.lastIndexOf(".")) + ".pdf";
			}
			
	        // Create document
	        fis = new FileInputStream(tmp);
	        Document newDoc = new Document();
			newDoc.setPath(destinationPath);
			newDoc = OKMDocument.getInstance().create(null, newDoc, fis);
			destinationPath = newDoc.getPath();
			
			// Set property groups ( metadata )
			for (PropertyGroup pg : OKMPropertyGroup.getInstance().getGroups(null, tplPath)) {
				OKMPropertyGroup.getInstance().addGroup(null, newDoc.getPath(), pg.getName());
				
				// Get group properties
				List<FormElement> properties = new ArrayList<FormElement>();
				
				for (FormElement fe : OKMPropertyGroup.getInstance().getProperties(null, newDoc.getPath(), pg.getName())) {
					// Iterates all properties because can have more than one group
					for (GWTFormElement fp : formProperties) {
						if (fe.getName().equals(fp.getName())) {
							properties.add(GWTUtil.copy(fp));
						}
					}
				}
				
				OKMPropertyGroup.getInstance().setProperties(null, newDoc.getPath(), pg.getName(), properties); 
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (DocumentException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Document), e.getMessage());
		} catch (UnsupportedMimeTypeException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_UnsupportedMimeType), e.getMessage());
		} catch (FileSizeExceededException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_FileSizeExceeded), e.getMessage());
		} catch (UserQuotaExceededException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_QuotaExceed), e.getMessage());
		} catch (VirusDetectedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Virus), e.getMessage());
		} catch (ItemExistsException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_ItemExists), e.getMessage());
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (DocumentTemplateException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_DocumentTemplate), e.getMessage());
		} catch (ConversionException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Conversion), e.getMessage());
		} catch (TemplateException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Template), e.getMessage());
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Parse), e.getMessage());
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_NoSuchGroup), e.getMessage());
		} catch (LockException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Lock), e.getMessage());
		} catch (NoSuchPropertyException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_NoSuchProperty), e.getMessage());
		} catch (ExtensionException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Extension), e.getMessage());
		} finally {
			FileUtils.deleteQuietly(tmp);
			IOUtils.closeQuietly(fis);
		}
		
		log.debug("createFromTemplate: {}", destinationPath);
		return destinationPath;
	}
	
	@Override
	public String updateFromTemplate(String tplPath, String destinationPath, List<GWTFormElement> formProperties,
			Map<String, List<Map<String, String>>> tableProperties) throws OKMException {
		log.debug("updateFromTemplate({},{})", tplPath, destinationPath);
		updateSessionManager();
		InputStream fis = null;
		File tmp = null;
		
		try {
			Document docTpl = OKMDocument.getInstance().getProperties(null, tplPath);
			tmp = tmpFromTemplate(docTpl, formProperties, tableProperties);
			
	        // Update document
	        fis = new FileInputStream(tmp);
	        OKMDocument.getInstance().checkout(null, destinationPath);
	        OKMDocument.getInstance().setContent(null, destinationPath, fis);
	        OKMDocument.getInstance().checkin(null, destinationPath, "Updated from template");
	        
			// Set property groups ( metadata )
			for (PropertyGroup pg : OKMPropertyGroup.getInstance().getGroups(null, destinationPath)) {
				List<FormElement> properties = new ArrayList<FormElement>();
				
				for (FormElement fe : OKMPropertyGroup.getInstance().getProperties(null, destinationPath, pg.getName())) {
					// Iterates all properties because can have more than one group
					for (GWTFormElement fp : formProperties) {
						if (fe.getName().equals(fp.getName())) {
							properties.add(GWTUtil.copy(fp));
						}
					}
				}
				
				OKMPropertyGroup.getInstance().setProperties(null, destinationPath, pg.getName(), properties); 
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (DocumentException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Document), e.getMessage());
		} catch (FileSizeExceededException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_FileSizeExceeded), e.getMessage());
		} catch (UserQuotaExceededException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_QuotaExceed), e.getMessage());
		} catch (VirusDetectedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Virus), e.getMessage());
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (DocumentTemplateException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_DocumentTemplate), e.getMessage());
		} catch (ConversionException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Conversion), e.getMessage());
		} catch (TemplateException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Template), e.getMessage());
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Parse), e.getMessage());
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_NoSuchGroup), e.getMessage());
		} catch (LockException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Lock), e.getMessage());
		} catch (NoSuchPropertyException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_NoSuchProperty), e.getMessage());
		} catch (VersionException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Version), e.getMessage());
		} finally {
			FileUtils.deleteQuietly(tmp);
			IOUtils.closeQuietly(fis);
		}
		
		log.debug("updateFromTemplate: {}", destinationPath);
		return destinationPath;
	}
	
	/**
	 * Create a document from a template and store it in a temporal file. 
	 */
	private File tmpFromTemplate(Document docTpl, List<GWTFormElement> formProperties,
			Map<String, List<Map<String, String>>> tableProperties) throws PathNotFoundException,
			RepositoryException, IOException, DatabaseException, DocumentException, TemplateException,
			DocumentTemplateException, ConversionException {
		FileOutputStream fos = null;
		InputStream fis = null;
		File tmp = null;
		
		try {
			// Reading original document
			fis = OKMDocument.getInstance().getContent(null, docTpl.getPath(), false);
			
			// Save content to temporary file
			String fileName = JCRUtils.getName(docTpl.getPath());
			tmp = File.createTempFile("okm", "." + FileUtils.getFileExtension(fileName));
			fos = new FileOutputStream(tmp);
			
			// Setting values to document
			Map<String, Object> values = new HashMap<String, Object>();
			
			for (GWTFormElement formElement : formProperties) {
				String key = formElement.getName().replace(".", "_").replace(":", "_");
				Object value = GWTUtil.getFormElementValue(formElement);
				values.put(key, value);
			}
			
			for (String key : tableProperties.keySet()) {
				values.put(key, tableProperties.get(key));
			}
			
			// Fill document by mime type
			if (docTpl.getMimeType().equals("application/pdf")) {
				// Fill form
				PDFUtils.fillForm(fis, values, fos);
			} else if (docTpl.getMimeType().equals("application/vnd.oasis.opendocument.text")) {
				// Fill template
				OOUtils.fillTemplate(fis, values, fos);
			} else if (docTpl.getMimeType().equals("text/html")) {
				//Fill template
				TemplateUtils.replace(fileName, fis, values, fos);
				fis.close();
				fos.close();
				
				// Converting to PDF
				fis = new FileInputStream(tmp);
				File tmp2 = tmp;
				tmp = File.createTempFile("okm", ".pdf");
				DocConverter.getInstance().html2pdf(fis, tmp); // tmp has converted pdf file
				tmp2.delete(); // deleting html tmp file
			}
		} finally {
			IOUtils.closeQuietly(fis);
			IOUtils.closeQuietly(fos);
		}
		
		return tmp;
	}
	
	@Override
	public String convertToPdf(String docPath) throws OKMException {
		log.debug("convertToPdf({})", docPath);
		updateSessionManager();
		String destinationPath = "";
		InputStream is = null;
		
		try {
			String uuid = OKMRepository.getInstance().getNodeUuid(null, docPath);
			
			// Now an document can be located by UUID
			if (!uuid.equals("")) {
				File pdfCache = new File(Config.CACHE_PDF + File.separator + uuid + ".pdf");
				Document doc = OKMDocument.getInstance().getProperties(null, docPath);
				DocConverter converter = DocConverter.getInstance();
				
				// Getting content
				is = OKMDocument.getInstance().getContent(null, docPath, false);
				
				// Convert to PDF
				if (!pdfCache.exists()) {
					try {
						File tmp = FileUtils.createTempFileFromMime(doc.getMimeType());
						FileUtils.copy(is, tmp);
						converter.doc2pdf(tmp, doc.getMimeType(), pdfCache);
						tmp.delete();
					} catch (ConversionException e) {
						pdfCache.delete();
						log.error(e.getMessage(), e);
						throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Conversion), e.getMessage());
					}
				}
				
				is.close();
				is = new FileInputStream(pdfCache);
				
				// create new document
				doc = new Document();
				doc.setPath(JCRUtils.getParent(docPath) + "/" + FileUtils.getFileName(JCRUtils.getName(docPath)) + ".pdf");
				destinationPath = OKMDocument.getInstance().create(null, doc, is).getPath();
				is.close();
				
				// Set property groups ( metadata ) from original document to converted
				for (PropertyGroup pg : OKMPropertyGroup.getInstance().getGroups(null, docPath)) {	
					// Add group
					OKMPropertyGroup.getInstance().addGroup(null, destinationPath, pg.getName());
					
					// Properties to be saved from original document
					List<FormElement> properties = OKMPropertyGroup.getInstance().getProperties(null, docPath, pg.getName()); 
					
					// Set properties
					OKMPropertyGroup.getInstance().setProperties(null, destinationPath, pg.getName(), properties); 
				}
			}
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_IO), e.getMessage());
		} catch (UnsupportedMimeTypeException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_UnsupportedMimeType), e.getMessage());
		} catch (FileSizeExceededException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_FileSizeExceeded), e.getMessage());
		} catch (UserQuotaExceededException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_QuotaExceed), e.getMessage());
		} catch (VirusDetectedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Virus), e.getMessage());
		} catch (ItemExistsException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_ItemExists), e.getMessage());
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_AccessDenied), e.getMessage());
		} catch (ExtensionException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Extension), e.getMessage());
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Parse), e.getMessage());
		} catch (NoSuchPropertyException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_NoSuchProperty), e.getMessage());
		} catch (NoSuchGroupException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_NoSuchGroup), e.getMessage());
		} catch (LockException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMDocumentService, ErrorCode.CAUSE_Lock), e.getMessage());
		} 
		
		log.debug("convertToPdf: {}", destinationPath);
		return destinationPath;
	}
}
