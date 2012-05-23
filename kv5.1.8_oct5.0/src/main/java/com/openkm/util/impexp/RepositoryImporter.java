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

package com.openkm.util.impexp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Document;
import com.openkm.bean.Folder;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.FileSizeExceededException;
import com.openkm.core.ItemExistsException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.core.UnsupportedMimeTypeException;
import com.openkm.core.UserQuotaExceededException;
import com.openkm.core.VirusDetectedException;
import com.openkm.extension.core.ExtensionException;
import com.openkm.module.DocumentModule;
import com.openkm.module.ModuleManager;
import com.openkm.util.FileLogger;

public class RepositoryImporter {
	private static Logger log = LoggerFactory.getLogger(RepositoryImporter.class);
	private static final String baseName = RepositoryImporter.class.getSimpleName();
	private RepositoryImporter() {}

	/**
	 * Import documents from filesystem into document repository.
	 */
	public static ImpExpStats importDocuments(String token, File fs, String fldPath, boolean metadata,
			Writer out, InfoDecorator deco) throws PathNotFoundException, ItemExistsException,
			AccessDeniedException, RepositoryException, FileNotFoundException, IOException,
			DatabaseException, ExtensionException {
		log.debug("importDocuments({}, {}, {}, {}, {}, {})", new Object[] { token, fs, fldPath, metadata, out, deco });
		ImpExpStats stats;
		
		try {
			FileLogger.info(baseName, "Start repository import from ''{0}'' to ''{1}''", fs.getPath(), fldPath);
			
			if (fs.exists()) {
				stats = importDocumentsHelper(token, fs, fldPath, metadata, out, deco);
			} else  {
				throw new FileNotFoundException(fs.getPath());
			}
			
			FileLogger.info(baseName, "Repository import finalized");
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			FileLogger.error(baseName, "PathNotFoundException ''{0}''", e.getMessage());
			throw e;
		} catch (AccessDeniedException e) {
			log.error(e.getMessage(), e);
			FileLogger.error(baseName, "AccessDeniedException ''{0}''", e.getMessage());
			throw e;
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
			FileLogger.error(baseName, "FileNotFoundException ''{0}''", e.getMessage());
			throw e;
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			FileLogger.error(baseName, "RepositoryException ''{0}''", e.getMessage());
			throw e;
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			FileLogger.error(baseName, "IOException ''{0}''", e.getMessage());
			throw e;
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			FileLogger.error(baseName, "DatabaseException ''{0}''", e.getMessage());
			throw e;
		}
				
		log.debug("importDocuments: {}", stats);
		return stats;
	}
	
	/**
	 * Import documents from filesystem into document repository (recursive).
	 */
	private static ImpExpStats importDocumentsHelper(String token, File fs, String fldPath, boolean metadata,
			Writer out, InfoDecorator deco) throws FileNotFoundException, PathNotFoundException,
			AccessDeniedException, RepositoryException, IOException, DatabaseException, ExtensionException {
		log.debug("importDocumentsHelper({}, {}, {}, {}, {}, {})", new Object[] { token, fs, fldPath, metadata, out, deco });
		File[] files = fs.listFiles();
		ImpExpStats stats = new ImpExpStats();
		
		for (int i=0; i<files.length; i++) {
			if (files[i].isDirectory()) {
				Folder fld = new Folder();
				fld.setPath(fldPath + "/" + files[i].getName());
				
				try {
					ModuleManager.getFolderModule().create(token, fld);
					FileLogger.info(baseName, "Created folder ''{0}''", fld.getPath());
				} catch (ItemExistsException e) {
					log.warn("ItemExistsException: {}", e.getMessage());
					out.write(deco.print(files[i].getPath(), files[i].length(), "ItemExists"));
					out.flush();
					stats.setOk(false);
					FileLogger.error(baseName, "ItemExistsException ''{0}''", fld.getPath());
				}
				
				ImpExpStats tmp = importDocumentsHelper(token, files[i], fld.getPath(), metadata, out, deco);
				
				// Stats
				stats.setOk(stats.isOk() && tmp.isOk());
				stats.setSize(stats.getSize() + tmp.getSize());
				stats.setDocuments(stats.getDocuments() + tmp.getDocuments());
				stats.setFolders(stats.getFolders() + tmp.getFolders() + 1);
			} else {
				Document doc = new Document();
				doc.setPath(fldPath + "/" + files[i].getName());
				FileInputStream fisContent = new FileInputStream(files[i]);
				int size = fisContent.available();
				boolean docOk = true;
				
				try {
					DocumentModule dm = ModuleManager.getDocumentModule();
					dm.create(token, doc, fisContent);
					FileLogger.info(baseName, "Created document ''{0}''", doc.getPath());
					
					// Metadata
					//Gson gson = new Gson();
					File jsFile = new File(files[i].getPath() + ".json");
					
					if (jsFile.exists() && jsFile.canRead()) {
						FileReader fr = new FileReader(jsFile);
						//Document jsDoc = gson.fromJson(fr, Document.class);
						
						fr.close();
					}
					
					// Stats
					stats.setSize(stats.getSize() + size);
					stats.setDocuments(stats.getDocuments() + 1);
				} catch (UnsupportedMimeTypeException e) {
					log.warn("UnsupportedMimeTypeException: {}", e.getMessage());
					out.write(deco.print(files[i].getPath(), files[i].length(), "UnsupportedMimeType"));
					out.flush();
					stats.setOk(docOk = false);
					FileLogger.error(baseName, "UnsupportedMimeTypeException ''{0}''", doc.getPath());
				} catch (FileSizeExceededException e) {
					log.warn("FileSizeExceededException: {}", e.getMessage());
					out.write(deco.print(files[i].getPath(), files[i].length(), "FileSizeExceeded"));
					out.flush();
					stats.setOk(docOk = false);
					FileLogger.error(baseName, "FileSizeExceededException ''{0}''", doc.getPath());
				} catch (UserQuotaExceededException e) {
					log.warn("UserQuotaExceededException: {}", e.getMessage());
					out.write(deco.print(files[i].getPath(), files[i].length(), "UserQuotaExceeded"));
					out.flush();
					stats.setOk(docOk = false);
					FileLogger.error(baseName, "UserQuotaExceededException ''{0}''", doc.getPath());
				} catch (VirusDetectedException e) {
					log.warn("VirusWarningException: {}", e.getMessage());
					out.write(deco.print(files[i].getPath(), files[i].length(), "VirusWarningException"));
					out.flush();
					stats.setOk(docOk = false);
					FileLogger.error(baseName, "VirusWarningException ''{0}''", doc.getPath());
				} catch (ItemExistsException e) {
					log.warn("ItemExistsException: {}", e.getMessage());
					out.write(deco.print(files[i].getPath(), files[i].length(), "ItemExists"));
					out.flush();
					stats.setOk(docOk = false);
					FileLogger.error(baseName, "ItemExistsException ''{0}''", doc.getPath());
				} finally {
					fisContent.close();
				}

				if (docOk) {
					out.write(deco.print(files[i].getPath(), files[i].length(), null));
					out.flush();
				}
			}
		}
		
		log.debug("importDocumentsHelper: {}", stats);
		return stats;
	}
}
