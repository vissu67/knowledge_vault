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

package com.openkm.module;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import com.openkm.bean.DashboardDocumentResult;
import com.openkm.bean.DashboardFolderResult;
import com.openkm.bean.DashboardMailResult;
import com.openkm.core.DatabaseException;
import com.openkm.core.ParseException;
import com.openkm.core.RepositoryException;
import com.openkm.dao.bean.QueryParams;

public interface DashboardModule {

	/**
	 * Get list of locked documents by user
	 */
	public List<DashboardDocumentResult> getUserLockedDocuments(String token) throws RepositoryException,
			DatabaseException;
	
	/**
	 * Get list of checked-out documents by user
	 */
	public List<DashboardDocumentResult> getUserCheckedOutDocuments(String token) throws RepositoryException,
			DatabaseException;
	
	/**
	 * Get user subscribed documents
	 */
	public List<DashboardDocumentResult> getUserSubscribedDocuments(String token) throws RepositoryException,
			DatabaseException;
	
	/**
	 * Get user subscribed folders
	 */
	public List<DashboardFolderResult> getUserSubscribedFolders(String token) throws RepositoryException,
			DatabaseException;
	
	/**
	 * Get user last uploaded documents 
	 */
	public List<DashboardDocumentResult> getUserLastUploadedDocuments(String token) throws RepositoryException,
			DatabaseException;
	
	/**
	 * Get user last modified documents 
	 */
	public List<DashboardDocumentResult> getUserLastModifiedDocuments(String token) throws RepositoryException,
			DatabaseException;
	
	/**
	 * Get user last downloaded documents
	 */
	public List<DashboardDocumentResult> getUserLastDownloadedDocuments(String token) throws RepositoryException,
			DatabaseException;
	
	/**
	 * Get user last imported mails
	 */
	public List<DashboardMailResult> getUserLastImportedMails(String token) throws RepositoryException,
			DatabaseException;
	
	/**
	 * Get user last imported mail attachments
	 */
	public List<DashboardDocumentResult> getUserLastImportedMailAttachments(String token) throws RepositoryException,
			DatabaseException;
	
	/**
	 * Get user documents size
	 */
	public long getUserDocumentsSize(String token) throws RepositoryException, DatabaseException;
	
	/**
	 * Get user searchs
	 */
	public List<QueryParams> getUserSearchs(String token) throws RepositoryException, DatabaseException;
	
	/**
	 * Find
	 */
	public List<DashboardDocumentResult> find(String token, int pqId) throws IOException, ParseException, 
			RepositoryException, DatabaseException;
		
	/**
	 * Get last week top downloaded documents
	 */
	public List<DashboardDocumentResult> getLastWeekTopDownloadedDocuments(String token) throws 
			RepositoryException, DatabaseException;

	/**
	 * Get last month downloaded documents
	 */
	public List<DashboardDocumentResult> getLastMonthTopDownloadedDocuments(String token) throws 
			RepositoryException, DatabaseException;
	
	/**
	 * Get last week top modified documents
	 */
	public List<DashboardDocumentResult> getLastWeekTopModifiedDocuments(String token) throws
			RepositoryException, DatabaseException;

	/**
	 * Get las month top modified documentd
	 */
	public List<DashboardDocumentResult> getLastMonthTopModifiedDocuments(String token) throws
			RepositoryException, DatabaseException;

	/**
	 * Get last modified documents
	 */
	public List<DashboardDocumentResult> getLastModifiedDocuments(String token) throws RepositoryException,
			DatabaseException;
	
	/**
	 * Get last uploaded documents
	 */
	public List<DashboardDocumentResult> getLastUploadedDocuments(String token) throws RepositoryException,
			DatabaseException;

	/**
	 * Visite node
	 */
	public void visiteNode(String token, String source, String node, Calendar date) throws 
			RepositoryException, DatabaseException;
}
