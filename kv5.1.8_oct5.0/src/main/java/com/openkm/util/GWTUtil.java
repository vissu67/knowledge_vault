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

package com.openkm.util;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jackrabbit.util.ISO8601;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.api.OKMDocument;
import com.openkm.api.OKMFolder;
import com.openkm.api.OKMMail;
import com.openkm.api.OKMPropertyGroup;
import com.openkm.api.OKMRepository;
import com.openkm.bean.DashboardDocumentResult;
import com.openkm.bean.DashboardFolderResult;
import com.openkm.bean.DashboardMailResult;
import com.openkm.bean.Document;
import com.openkm.bean.Folder;
import com.openkm.bean.Lock;
import com.openkm.bean.Mail;
import com.openkm.bean.Note;
import com.openkm.bean.PropertyGroup;
import com.openkm.bean.QueryResult;
import com.openkm.bean.Version;
import com.openkm.bean.form.Button;
import com.openkm.bean.form.CheckBox;
import com.openkm.bean.form.Download;
import com.openkm.bean.form.FormElement;
import com.openkm.bean.form.Input;
import com.openkm.bean.form.Node;
import com.openkm.bean.form.Option;
import com.openkm.bean.form.Print;
import com.openkm.bean.form.Select;
import com.openkm.bean.form.Separator;
import com.openkm.bean.form.SuggestBox;
import com.openkm.bean.form.Text;
import com.openkm.bean.form.TextArea;
import com.openkm.bean.form.Upload;
import com.openkm.bean.form.Validator;
import com.openkm.bean.workflow.Comment;
import com.openkm.bean.workflow.ProcessDefinition;
import com.openkm.bean.workflow.ProcessInstance;
import com.openkm.bean.workflow.TaskInstance;
import com.openkm.bean.workflow.Token;
import com.openkm.bean.workflow.Transition;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.ParseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.dao.KeyValueDAO;
import com.openkm.dao.bean.Activity;
import com.openkm.dao.bean.Bookmark;
import com.openkm.dao.bean.KeyValue;
import com.openkm.dao.bean.Language;
import com.openkm.dao.bean.QueryParams;
import com.openkm.dao.bean.Report;
import com.openkm.dao.bean.UserConfig;
import com.openkm.extension.dao.bean.Contact;
import com.openkm.extension.dao.bean.Forum;
import com.openkm.extension.dao.bean.ForumPost;
import com.openkm.extension.dao.bean.ForumTopic;
import com.openkm.extension.dao.bean.MessageReceived;
import com.openkm.extension.dao.bean.MessageSent;
import com.openkm.extension.dao.bean.ProposedQueryReceived;
import com.openkm.extension.dao.bean.ProposedQuerySent;
import com.openkm.extension.dao.bean.ProposedSubscriptionReceived;
import com.openkm.extension.dao.bean.ProposedSubscriptionSent;
import com.openkm.extension.dao.bean.StampImage;
import com.openkm.extension.dao.bean.StampText;
import com.openkm.extension.dao.bean.Staple;
import com.openkm.extension.dao.bean.StapleGroup;
import com.openkm.frontend.client.bean.GWTBookmark;
import com.openkm.frontend.client.bean.GWTComment;
import com.openkm.frontend.client.bean.GWTDashboardDocumentResult;
import com.openkm.frontend.client.bean.GWTDashboardFolderResult;
import com.openkm.frontend.client.bean.GWTDashboardMailResult;
import com.openkm.frontend.client.bean.GWTDocument;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.bean.GWTKeyValue;
import com.openkm.frontend.client.bean.GWTLanguage;
import com.openkm.frontend.client.bean.GWTLock;
import com.openkm.frontend.client.bean.GWTMail;
import com.openkm.frontend.client.bean.GWTNote;
import com.openkm.frontend.client.bean.GWTProcessDefinition;
import com.openkm.frontend.client.bean.GWTProcessInstance;
import com.openkm.frontend.client.bean.GWTPropertyGroup;
import com.openkm.frontend.client.bean.GWTPropertyParams;
import com.openkm.frontend.client.bean.GWTQueryParams;
import com.openkm.frontend.client.bean.GWTQueryResult;
import com.openkm.frontend.client.bean.GWTReport;
import com.openkm.frontend.client.bean.GWTTaskInstance;
import com.openkm.frontend.client.bean.GWTToken;
import com.openkm.frontend.client.bean.GWTTransition;
import com.openkm.frontend.client.bean.GWTUserConfig;
import com.openkm.frontend.client.bean.GWTVersion;
import com.openkm.frontend.client.bean.GWTWorkflowComment;
import com.openkm.frontend.client.bean.extension.GWTActivity;
import com.openkm.frontend.client.bean.extension.GWTContact;
import com.openkm.frontend.client.bean.extension.GWTForum;
import com.openkm.frontend.client.bean.extension.GWTForumPost;
import com.openkm.frontend.client.bean.extension.GWTForumTopic;
import com.openkm.frontend.client.bean.extension.GWTMessageReceived;
import com.openkm.frontend.client.bean.extension.GWTProposedQueryReceived;
import com.openkm.frontend.client.bean.extension.GWTProposedQuerySent;
import com.openkm.frontend.client.bean.extension.GWTProposedSubscriptionReceived;
import com.openkm.frontend.client.bean.extension.GWTProposedSubscriptionSent;
import com.openkm.frontend.client.bean.extension.GWTStamp;
import com.openkm.frontend.client.bean.extension.GWTStaple;
import com.openkm.frontend.client.bean.extension.GWTStapleGroup;
import com.openkm.frontend.client.bean.extension.GWTTextMessageSent;
import com.openkm.frontend.client.bean.form.GWTButton;
import com.openkm.frontend.client.bean.form.GWTCheckBox;
import com.openkm.frontend.client.bean.form.GWTDownload;
import com.openkm.frontend.client.bean.form.GWTFormElement;
import com.openkm.frontend.client.bean.form.GWTInput;
import com.openkm.frontend.client.bean.form.GWTNode;
import com.openkm.frontend.client.bean.form.GWTOption;
import com.openkm.frontend.client.bean.form.GWTPrint;
import com.openkm.frontend.client.bean.form.GWTSelect;
import com.openkm.frontend.client.bean.form.GWTSeparator;
import com.openkm.frontend.client.bean.form.GWTSuggestBox;
import com.openkm.frontend.client.bean.form.GWTText;
import com.openkm.frontend.client.bean.form.GWTTextArea;
import com.openkm.frontend.client.bean.form.GWTUpload;
import com.openkm.frontend.client.bean.form.GWTValidator;

public class GWTUtil {
	private static Logger log = LoggerFactory.getLogger(GWTUtil.class);
	
	/**
	 * Copy the Document data to GWTDocument data.
	 * 
	 * @param doc The original Document object.
	 * @return A GWTDocument object with the data from 
	 * the original Document.
	 */
	public static GWTDocument copy(Document doc) {
		log.debug("copy({})", doc);
		GWTDocument gWTDoc = new GWTDocument();
		
		gWTDoc.setKeywords(doc.getKeywords());
		gWTDoc.setMimeType(doc.getMimeType());
		gWTDoc.setName(GWTUtil.getName(doc.getPath()));
		gWTDoc.setParent(GWTUtil.getParent(doc.getPath()));
		gWTDoc.setParentId(GWTUtil.getParent(doc.getPath()));
		gWTDoc.setPath(doc.getPath());
		gWTDoc.setAuthor(doc.getAuthor());
		gWTDoc.setCreated(doc.getCreated().getTime());
		gWTDoc.setLastModified(doc.getLastModified().getTime());
		gWTDoc.setCheckedOut(doc.isCheckedOut());
		gWTDoc.setLocked(doc.isLocked());
		gWTDoc.setSubscribed(doc.isSubscribed());
		gWTDoc.setActualVersion(copy(doc.getActualVersion()));
		gWTDoc.setPermissions(doc.getPermissions());
		gWTDoc.setLockInfo(copy(doc.getLockInfo()));
		gWTDoc.setConvertibleToPdf(doc.isConvertibleToPdf());
		gWTDoc.setConvertibleToSwf(doc.isConvertibleToSwf());
		gWTDoc.setConvertibleToDxf(doc.isConvertibleToDxf());
		gWTDoc.setCipherName(doc.getCipherName());
		gWTDoc.setSubscriptors(doc.getSubscriptors());
		gWTDoc.setUuid(doc.getUuid());
		gWTDoc.setNotes(copy(doc.getNotes()));
		
		for (Iterator<Note> it = doc.getNotes().iterator(); it.hasNext() && !gWTDoc.isHasNotes();) {
			Note note = it.next();
			if (!note.getUser().equals(Config.SYSTEM_USER)) {
				gWTDoc.setHasNotes(true);
			}
		}
		
		Set<GWTFolder> categories = new HashSet<GWTFolder>();
		
		for (Iterator<Folder> it = doc.getCategories().iterator(); it.hasNext();) {
			categories.add(copy(it.next()));
		}
		
		gWTDoc.setCategories(categories);
		log.debug("copy: {}", gWTDoc);
		return gWTDoc;
	}
	
	/**
	 * Copy the GWTDocument data to Document data.
	 * 
	 * @param gWTDoc The original GWTDocument object.
	 * @return A Document object with the data form de original GWTDocument
	 */
	public static Document copy(GWTDocument gWTDoc) {
		log.debug("copy({})", gWTDoc);
		Document doc = new Document();
		Calendar cal = Calendar.getInstance();
		
		doc.setKeywords(gWTDoc.getKeywords());
		doc.setMimeType(gWTDoc.getMimeType());
		doc.setPath(gWTDoc.getPath());
		doc.setAuthor(gWTDoc.getAuthor());
		cal.setTime(gWTDoc.getCreated());
		doc.setCreated(cal);
		cal.setTime(gWTDoc.getLastModified());
		doc.setLastModified(cal);
		doc.setCheckedOut(gWTDoc.isCheckedOut());
		doc.setLocked(gWTDoc.isLocked());
		doc.setActualVersion(GWTUtil.copy(gWTDoc.getActualVersion()));
		doc.setPermissions(gWTDoc.getPermissions());
		doc.setSubscribed(gWTDoc.isSubscribed());
		doc.setSubscriptors(gWTDoc.getSubscriptors());
		Set <Folder> categories = new HashSet<Folder>();
		
		for (Iterator<GWTFolder> it = gWTDoc.getCategories().iterator(); it.hasNext();){
			categories.add(copy(it.next()));
		}
		
		doc.setCategories(categories);
		gWTDoc.setActualVersion(copy(doc.getActualVersion()));
		
		log.debug("copy: {}", gWTDoc);
		return doc;
	}

	/**
	 * Copy the Folder data to GWTFolder data.
	 * 
	 * @param doc The original Folder object.
	 * @return A GWTFolder object with the data from 
	 * the original Document.
	 */
	public static GWTFolder copy(Folder fld) {
		log.debug("copy({})", fld);
		GWTFolder gWTFolder = new GWTFolder();
		
		gWTFolder.setUuid(fld.getUuid());
		gWTFolder.setPath(fld.getPath());
		gWTFolder.setParentPath(GWTUtil.getParent(fld.getPath()));
		gWTFolder.setName(GWTUtil.getName(fld.getPath()));
		gWTFolder.setHasChilds(fld.getHasChilds());
		gWTFolder.setCreated(fld.getCreated().getTime());
		gWTFolder.setPermissions(fld.getPermissions());
		gWTFolder.setAuthor(fld.getAuthor());
		gWTFolder.setSubscribed(fld.isSubscribed());
		gWTFolder.setSubscriptors(fld.getSubscriptors());
		gWTFolder.setNotes(copy(fld.getNotes()));
		
		for (Iterator<Note> it = fld.getNotes().iterator(); it.hasNext() && !gWTFolder.isHasNotes();) {
			Note note = it.next();
			if (!note.getUser().equals(Config.SYSTEM_USER)) {
				gWTFolder.setHasNotes(true);
			}
		}
		
		log.debug("copy: {}", gWTFolder);
		return gWTFolder;
	}	
	
	/**
	 * Copy the GWTFolder data to Folder data.
	 * 
	 * @param doc The original GWTFolder object.
	 * @return A Folder object with the data from 
	 * the original Document.
	 */
	public static Folder copy(GWTFolder fld) {
		log.debug("copy({})", fld);
		Folder folder = new Folder();
		
		folder.setUuid(fld.getUuid());
		folder.setPath(fld.getPath());
		folder.setHasChilds(fld.getHasChilds());
		Calendar created = Calendar.getInstance();
		created.setTimeInMillis(fld.getCreated().getTime());
		folder.setCreated(created);
		folder.setPermissions(fld.getPermissions());
		folder.setAuthor(fld.getAuthor());
		folder.setSubscribed(fld.isSubscribed());
		folder.setSubscriptors(fld.getSubscriptors());
		
		log.debug("copy: {}", folder);
		return folder;
	}
	
	/**
	 * Copy the Version data to GWTVersion data.
	 * 
	 * @param doc The original Version object.
	 * @return A GWTVersion object with the data from 
	 * the original Document.
	 */
	public static GWTVersion copy(Version version) {
		log.debug("copy({})", version);
		GWTVersion gWTVersion = new GWTVersion();
		
		gWTVersion.setCreated(version.getCreated().getTime());
		gWTVersion.setName(version.getName());
		gWTVersion.setSize(version.getSize());
		gWTVersion.setAuthor(version.getAuthor());
		gWTVersion.setActual(version.isActual());
		gWTVersion.setComment(version.getComment());
		
		log.debug("copy: {}", gWTVersion);
		return gWTVersion;
	}
	
	/**
	 * Copy the GWTVersion data to Version data object
	 * 
	 * @param gWTVersion The original GWTVersion
	 * @return A Version object with the data from the original GWTVersion
	 */
	public static Version copy(GWTVersion gWTVersion) {
		log.debug("copy({})", gWTVersion);
		Version version = new Version();
		Calendar cal = Calendar.getInstance();
		
		version.setName(gWTVersion.getName());
		version.setSize(gWTVersion.getSize());
		version.setAuthor(gWTVersion.getAuthor());
		version.setActual(gWTVersion.isActual());
		cal.setTime(gWTVersion.getCreated());
		version.setCreated(cal);
		version.setComment(gWTVersion.getComment());
		
		log.debug("copy: {}", version);
		return version;
	}
	
	/**
	 * Copy the Lock data to GWTLock data.
	 * 
	 * @param doc The original Version object.
	 * @return A GWTLock object with the data from 
	 * the original Lock.
	 */
	public static GWTLock copy(Lock lock) {
		log.debug("copy({})", lock);
		GWTLock gWTLock = new GWTLock();
		
		if (lock != null) {
			gWTLock.setNodePath(lock.getNodePath());
			gWTLock.setOwner(lock.getOwner());
			gWTLock.setToken(lock.getToken());
		}
		
		log.debug("copy: {}", gWTLock);
		return gWTLock;
	}
	
	/**
	 * Copy the Bookmark data to GWTBookmark data.
	 * 
	 * @param bookmark The original Version object.
	 * @return A GWTBookmark object with the data from 
	 * the original Bookmark.
	 */
	public static GWTBookmark copy(Bookmark bookmark) {
		log.debug("copy({})", bookmark);
		GWTBookmark gWTBookmark = new GWTBookmark();
		
		if (bookmark != null) {
			gWTBookmark.setId(bookmark.getId());
			gWTBookmark.setName(bookmark.getName());
			gWTBookmark.setPath(bookmark.getPath());
			gWTBookmark.setUuid(bookmark.getUuid());
			gWTBookmark.setType(bookmark.getType());
		}
		
		log.debug("copy: {}", gWTBookmark);
		return gWTBookmark;
	}
	
	/**
	 * Get parent item path from path.
	 * 
	 * @param path The complete item path.
	 * @return The parent item path.
	 */
	public static String getParent(String path) {
		log.debug("getParent({})", path);
		int lastSlash = path.lastIndexOf('/');
		String ret = (lastSlash > 0)?path.substring(0, lastSlash):"";
		log.debug("getParent: {}", ret);
		return ret;	
	}

	/**
	 * Get item name from path.
	 * 
	 * @param path The complete item path.
	 * @return The name of the item.
	 */
	public static String getName(String path) {
		log.debug("getName({})", path);
		String ret = path.substring(path.lastIndexOf('/')+1);
		log.debug("getName: {}", ret);
		return ret;
	}
	
	/**
	 * Copy the gWTparams data to GWTQueryParams data object
	 * 
	 * @param gWTParams The original GWTQueryParams
	 * @return The QueryParams object with the data from de original GWTQueryParams
	 */
	public static QueryParams copy(GWTQueryParams gWTParams) {
		QueryParams params = new QueryParams();
		
		params.setId(gWTParams.getId());
		params.setQueryName(gWTParams.getQueryName());
		params.setContent(gWTParams.getContent());
		String keywords = gWTParams.getKeywords().trim();
		Set<String> tmpKwd = new HashSet<String>();
		
		if (!keywords.equals("")) {
			String kw[] = keywords.split(" ");
			for (int i=0; i<kw.length; i++) {
				tmpKwd.add(kw[i]);
			}
		}
		
		params.setKeywords(tmpKwd);
		params.setMimeType(gWTParams.getMimeType());
		params.setName(gWTParams.getName());
		Map<String, String> properties = new HashMap<String, String>();
		
		for (Iterator<String> it = gWTParams.getProperties().keySet().iterator(); it.hasNext();) {
			String key = it.next();
			properties.put(key, gWTParams.getProperties().get(key).getValue());
		}
		
		params.setProperties(properties);
		params.setPath(gWTParams.getPath());
		String categories = gWTParams.getCategoryUuid().trim();
		Set<String> tmpCat = new HashSet<String>();
		
		if (!categories.equals("")) {
			tmpCat.add(categories);
		}
		
		params.setCategories(tmpCat);
		params.setAuthor(gWTParams.getAuthor());
		Calendar lastModifiedFrom = Calendar.getInstance();
		Calendar lastModifiedTo = Calendar.getInstance();
		
		if (gWTParams.getLastModifiedFrom() != null && gWTParams.getLastModifiedTo() != null) {
			lastModifiedFrom.setTime(gWTParams.getLastModifiedFrom());
			lastModifiedTo.setTime(gWTParams.getLastModifiedTo());
		} else {
			lastModifiedFrom = null;
			lastModifiedTo = null;
		}
		
		params.setLastModifiedFrom(lastModifiedFrom);
		params.setLastModifiedTo(lastModifiedTo);
		params.setDashboard(gWTParams.isDashboard());
		params.setDomain(gWTParams.getDomain());
		params.setMailSubject(gWTParams.getMailSubject());
		params.setMailTo(gWTParams.getMailTo());
		params.setMailFrom(gWTParams.getMailFrom());
		params.setOperator(gWTParams.getOperator());
		
		return params;
	}
	
	/**
	 * Copy the QueryResult data to GWTQueryResult
	 * 
	 * @param queryResult The original QueryResult
	 * @return The GWTQueryResult object with data values from de origina QueryResult
	 */
	public static GWTQueryResult copy(QueryResult queryResult) {
		GWTQueryResult gwtQueryResult = new GWTQueryResult();
		
		if (queryResult.getDocument()!=null) {
			gwtQueryResult.setDocument(copy(queryResult.getDocument())); 
			gwtQueryResult.getDocument().setAttachment(false);
		} else if (queryResult.getFolder()!=null) {
			gwtQueryResult.setFolder(copy(queryResult.getFolder()));
		} else if (queryResult.getMail()!=null) {
			gwtQueryResult.setMail(copy(queryResult.getMail()));
		} else if (queryResult.getAttachment()!=null) {
			gwtQueryResult.setAttachment(copy(queryResult.getAttachment()));
			gwtQueryResult.getAttachment().setAttachment(true);
		}
		
		gwtQueryResult.setScore(queryResult.getScore());
		
		return gwtQueryResult;
	}
	
	/**
	 * Copy the QueryParams data to GWTQueryParams data object
	 * 
	 * @param GWTQueryParams The original QueryParams
	 * @return The GWTQueryParams object with the data from de original QueryParams 
	 */
	public static GWTQueryParams copy(QueryParams params) throws RepositoryException, 
			IOException, PathNotFoundException, ParseException, DatabaseException {
		GWTQueryParams gWTParams = new GWTQueryParams();
		
		gWTParams.setId(params.getId());
		gWTParams.setQueryName(params.getQueryName());
		gWTParams.setContent(params.getContent());
		String tmp = "";
		
		for (Iterator<String> itKwd = params.getKeywords().iterator(); itKwd.hasNext(); ) {
			tmp += itKwd.next() + " "; 
		}
		
		gWTParams.setKeywords(tmp);
		gWTParams.setMimeType(params.getMimeType());
		gWTParams.setName(params.getName());
		gWTParams.setPath(params.getPath());
		gWTParams.setAuthor(params.getAuthor());
		gWTParams.setDashboard(params.isDashboard());
		gWTParams.setDomain(params.getDomain());
		gWTParams.setMailSubject(params.getMailSubject());
		gWTParams.setMailFrom(params.getMailFrom());
		gWTParams.setMailTo(params.getMailTo());
		gWTParams.setOperator(params.getOperator());
		Iterator<String> itCat = params.getCategories().iterator();
		
		if (itCat.hasNext()) {
			gWTParams.setCategoryUuid(itCat.next());
		}
		
		if (params.getCategories() != null && !params.getCategories().isEmpty()) {
			itCat = params.getCategories().iterator();
			if (itCat.hasNext()) {
				gWTParams.setCategoryPath(OKMRepository.getInstance().getNodePath(null, itCat.next()));
			}
		}
		
		if (params.getLastModifiedFrom() != null && params.getLastModifiedTo() != null) {
			gWTParams.setLastModifiedFrom(params.getLastModifiedFrom().getTime());
			gWTParams.setLastModifiedTo(params.getLastModifiedTo().getTime());
		} 
		
		// Sets group name for each property param
		Map<String, GWTPropertyParams> finalProperties = new HashMap<String, GWTPropertyParams> ();
		Map<String, String> properties = params.getProperties();
		Collection<String> colKeys = properties.keySet();
		
		for (Iterator<String> itKeys = colKeys.iterator(); itKeys.hasNext(); ){
			String key = itKeys.next();
			boolean found = false;
			
			// Obtain all group names
			Collection<PropertyGroup> colGroups = OKMPropertyGroup.getInstance().getAllGroups(null);
			Iterator<PropertyGroup> itGroup = colGroups.iterator();
			while (itGroup.hasNext() && !found) {
				PropertyGroup group = itGroup.next();
				
				// Obtain all metadata values
				Collection<FormElement> metaData = OKMPropertyGroup.getInstance().getPropertyGroupForm(null, group.getName());
				for (Iterator<FormElement> it = metaData.iterator(); it.hasNext();) {
					FormElement formElement = it.next();
					if (formElement.getName().equals(key)) {
						found = true;
						GWTPropertyParams gWTPropertyParams = new GWTPropertyParams();
						gWTPropertyParams.setGrpName(group.getName());
						gWTPropertyParams.setGrpLabel(group.getLabel());
						gWTPropertyParams.setFormElement(GWTUtil.copy(formElement));
						gWTPropertyParams.setValue(properties.get(key));
						finalProperties.put(key,gWTPropertyParams);
						break;
					}
				}
			}
		}
		
		gWTParams.setProperties(finalProperties);
		return gWTParams;
	}

	/**
	 * Copy the DashboardDocumentResult data to GWTDashboardDocumentResult
	 * 
	 * @param dsDocumentResult The original DashboardDocumentResult
	 * @return The GWTDashboardDocumentResult object with data values from the original
	 * DashboardDocumentResult
	 */
	public static GWTDashboardDocumentResult copy(DashboardDocumentResult dsDocumentResult) {
		GWTDashboardDocumentResult gwtDashboardDocumentResult = new GWTDashboardDocumentResult();
		
		gwtDashboardDocumentResult.setDocument(copy(dsDocumentResult.getDocument()));
		gwtDashboardDocumentResult.setVisited(dsDocumentResult.isVisited());
		gwtDashboardDocumentResult.setDate(dsDocumentResult.getDate().getTime());
		
		return gwtDashboardDocumentResult;
	}
	
	/**
	 * Copy the DashboardFolderResult data to GWTDashboardFolderResult
	 * 
	 * @param dsFolderResult The original DashboardFolderResult
	 * @return The GWTDashboardFolderResult object with data values from the original
	 * DashboardFolderResult
	 */
	public static GWTDashboardFolderResult copy(DashboardFolderResult dsFolderResult) {
		GWTDashboardFolderResult gwtDashboardFolderResult = new GWTDashboardFolderResult();
		
		gwtDashboardFolderResult.setFolder(copy(dsFolderResult.getFolder()));
		gwtDashboardFolderResult.setVisited(dsFolderResult.isVisited());
		gwtDashboardFolderResult.setDate(dsFolderResult.getDate().getTime());
		
		return gwtDashboardFolderResult;
	}
	
	
	/**
	 * Copy the DashboardMailResult data to GWTDashboardMailResult
	 * 
	 * @param dsMailResult The original DashboardMailResult
	 * @return The GWTDashboardMailResult object with data values from the original
	 * DashboardMailResult
	 */
	public static GWTDashboardMailResult copy(DashboardMailResult dsmailResult) {
		GWTDashboardMailResult gwtDashboardMailResult = new GWTDashboardMailResult();
		
		gwtDashboardMailResult.setMail(copy(dsmailResult.getMail()));
		gwtDashboardMailResult.setVisited(dsmailResult.isVisited());
		gwtDashboardMailResult.setDate(dsmailResult.getDate().getTime());
		
		return gwtDashboardMailResult;
	}
	
	
	/**
	 * Copy to ProcessDefinition data to  GWTProcessDefinition
	 * 
	 * @param ProcessDefinition the original data
	 * @return The GWTProcessDefinition object with data values from original ProcessDefinition
	 */
	public static GWTProcessDefinition copy(ProcessDefinition processDefinition) {
		GWTProcessDefinition gWTProcessDefinition = new GWTProcessDefinition();
		
		gWTProcessDefinition.setId(processDefinition.getId());
		gWTProcessDefinition.setName(processDefinition.getName());
		gWTProcessDefinition.setVersion(processDefinition.getVersion());
		gWTProcessDefinition.setDescription(processDefinition.getDescription());
		
		return gWTProcessDefinition;
	}
	
	/**
	 * Copy to TaskInstance data to  GWTTaskInstance
	 * 
	 * @param TaskInstance the original data
	 * @return The GWTTaskInstance object with data values from original TaskInstance
	 */
	public static GWTTaskInstance copy(TaskInstance taskInstance) throws PathNotFoundException,
			RepositoryException, DatabaseException {
		GWTTaskInstance gWTTaskInstance = new GWTTaskInstance();
		
		gWTTaskInstance.setActorId(taskInstance.getActorId());
		gWTTaskInstance.setCreate(taskInstance.getCreate().getTime());
		gWTTaskInstance.setId(taskInstance.getId());
		gWTTaskInstance.setName(taskInstance.getName());
		gWTTaskInstance.setProcessInstance(copy(taskInstance.getProcessInstance()));
		gWTTaskInstance.setDescription(taskInstance.getDescription());
		
		if (taskInstance.getDueDate() != null) {
			gWTTaskInstance.setDueDate(taskInstance.getDueDate().getTime());
		}
		
		if (taskInstance.getStart() != null) {
			gWTTaskInstance.setStart(taskInstance.getStart().getTime());
		}
		
		gWTTaskInstance.setComments(copyComments(taskInstance.getComments()));
		return gWTTaskInstance;
	}
	
	/**
	 * Copy to ProcessInstance data to  GWTProcessInstance
	 * 
	 * @param ProcessInstance the original data
	 * @return The GWTProcessInstance object with data values from original ProcessInstance
	 */
	public static GWTProcessInstance copy(ProcessInstance processInstance) throws PathNotFoundException,
			RepositoryException, DatabaseException {
		GWTProcessInstance gWTProcessInstance = new GWTProcessInstance();
		
		gWTProcessInstance.setEnded(processInstance.isEnded());
		gWTProcessInstance.setId(processInstance.getId());
		gWTProcessInstance.setProcessDefinition(copy(processInstance.getProcessDefinition()));
		gWTProcessInstance.setSuspended(processInstance.isSuspended());
		Map<String, Object> variables = new HashMap<String, Object>();
		
		for (Iterator<String> it = processInstance.getVariables().keySet().iterator(); it.hasNext();) {
			String key = it.next();
			Object obj = processInstance.getVariables().get(key);
			
			if (obj instanceof FormElement ) {
				variables.put(key, copy((FormElement) obj));
			} else {
				variables.put(key, obj);
			}
		}
		
		gWTProcessInstance.setVariables(variables);
		gWTProcessInstance.setVersion(processInstance.getVersion());
		gWTProcessInstance.setStart(processInstance.getStart().getTime());
		gWTProcessInstance.setKey(processInstance.getKey());
		gWTProcessInstance.setRootToken(copy(processInstance.getRootToken()));
		processInstance.getRootToken();
		
		return gWTProcessInstance;
	}
	
	/**
	 * Copy to Token data to GWTToken
	 * 
	 * @param FormElement the original data
	 * @return The GWTToken object with data values from original Token
	 */
	public static GWTToken copy(Token token) throws PathNotFoundException, RepositoryException, DatabaseException {
		GWTToken gWTToken = new GWTToken();
		Collection<GWTTransition> availableTransitions = new ArrayList<GWTTransition>();
		
		for (Iterator<Transition> it = token.getAvailableTransitions().iterator(); it.hasNext();) {
			availableTransitions.add(copy(it.next()));
		}
		
		gWTToken.setAvailableTransitions(availableTransitions);
		Collection<GWTWorkflowComment> comments = new ArrayList<GWTWorkflowComment>();
		
		for (Iterator<Comment> it = token.getComments().iterator(); it.hasNext();) {
			comments.add(copy(it.next()));
		}
		
		gWTToken.setComments(comments);
		
		if (token.getEnd() != null) {
			gWTToken.setEnd(token.getEnd().getTime());
		}
		
		gWTToken.setId(token.getId());
		gWTToken.setName(token.getName());
		gWTToken.setNode(token.getNode());
		
		if (token.getParent() != null) {
			gWTToken.setParent(copy(token.getParent()));
		} 
		
		if (token.getProcessInstance() != null) {
			gWTToken.setProcessInstance(copy(token.getProcessInstance()));
		}
		
		gWTToken.setStart(token.getStart().getTime());
		gWTToken.setSuspended(token.isSuspended());
		
		return gWTToken;
	}
	
	/**
	 * Copy to Token data to GWTTransition
	 * 
	 * @param Transition the original data
	 * @return The GWTTransition object with data values from original Transition
	 */
	public static GWTTransition copy(Transition transition) {
		GWTTransition gWTTransition = new GWTTransition();
		gWTTransition.setFrom(transition.getFrom());
		gWTTransition.setId(transition.getId());
		gWTTransition.setName(transition.getName());
		gWTTransition.setTo(transition.getTo());
		
		return gWTTransition;
	}
	
	/**
	 * Copy to Comment data to GWTWorkFlowComment
	 * @param Transition the original data
	 * @return The GWTWorkFlowComment object with data values from original Comment
	 */
	public static GWTWorkflowComment copy(Comment comment) {
		GWTWorkflowComment gWTComment = new GWTWorkflowComment();
		gWTComment.setActorId(comment.getActorId());
		gWTComment.setMessage(comment.getMessage());
		gWTComment.setTime(comment.getTime().getTime());
		
		return gWTComment;
	}
	
	/**
	 * Copy to Validator data to GWTValidator
	 * 
	 * @param Validator the original data
	 * @return The GWTValidator object with data values from original Validator
	 */
	public static List<GWTValidator> copyValidators(List<Validator> validators) {
		List<GWTValidator> gwtValidatorsList = new ArrayList<GWTValidator>();
		for (Validator validator : validators) {
			gwtValidatorsList.add(copy(validator));
		}
		return gwtValidatorsList;
	}
	
	/**
	 * copyNodes
	 */
	public static List<GWTNode> copyNodes(List<Node> nodes) {
		List<GWTNode> gwtNodesList = new ArrayList<GWTNode>();
		for (Node node : nodes) {
			gwtNodesList.add(copy(node));
		}
		return gwtNodesList;
	}
	
	/**
	 * Copy to FormElement data to GWTFormElemen
	 * 
	 * @param FormElement the original data
	 * @return The GWTFormElement object with data values from original FormElement
	 */
	public static GWTFormElement copy(FormElement formElement) throws PathNotFoundException,
			RepositoryException, DatabaseException {
		if (formElement instanceof Button) {
			GWTButton gWTButton = new GWTButton();
			gWTButton.setName(formElement.getName());
			gWTButton.setLabel(formElement.getLabel());
			gWTButton.setWidth(formElement.getWidth());
			gWTButton.setHeight(formElement.getHeight());
			Button button = (Button) formElement;
			gWTButton.setTransition(button.getTransition());
			gWTButton.setConfirmation(button.getConfirmation());
			return gWTButton;
		} else if (formElement instanceof Input) {
			GWTInput gWTInput = new GWTInput();
			gWTInput.setName(formElement.getName());
			gWTInput.setLabel(formElement.getLabel());
			gWTInput.setWidth(formElement.getWidth());
			gWTInput.setHeight(formElement.getHeight());
			Input input = (Input) formElement;
			gWTInput.setReadonly(input.isReadonly());
			gWTInput.setValue(input.getValue());
			
			if (input.getType().equals(Input.TYPE_DATE)) {
				if (!input.getValue().equals("")) {
					Calendar date = ISO8601.parse(input.getValue());
					
					if (date != null) {
						gWTInput.setDate(date.getTime());
					} else {
						log.warn("Input '{}' value should be in ISO8601 format: {}", input.getName(), input.getValue());
					}
				}
			}
			
			if (input.getType().equals(Input.TYPE_FOLDER) && !gWTInput.getValue().equals("")) {
				gWTInput.setFolder(copy(OKMFolder.getInstance().getProperties(null, ((Input) formElement).getValue())));
			}
			
			gWTInput.setType(((Input) formElement).getType());
			gWTInput.setValidators(copyValidators(input.getValidators()));
			gWTInput.setData(input.getData());
			return gWTInput;
		} else if (formElement instanceof SuggestBox) {
			GWTSuggestBox gWTsuggestBox = new GWTSuggestBox();
			gWTsuggestBox.setName(formElement.getName());
			gWTsuggestBox.setLabel(formElement.getLabel());
			gWTsuggestBox.setWidth(formElement.getWidth());
			gWTsuggestBox.setHeight(formElement.getHeight());
			SuggestBox suggestBox = (SuggestBox) formElement;
			gWTsuggestBox.setReadonly(suggestBox.isReadonly());
			gWTsuggestBox.setValidators(copyValidators(suggestBox.getValidators()));
			gWTsuggestBox.setValue(suggestBox.getValue());
			gWTsuggestBox.setDialogTitle(suggestBox.getDialogTitle());
			gWTsuggestBox.setTable(suggestBox.getTable());
			gWTsuggestBox.setFilterQuery(suggestBox.getFilterQuery());
			gWTsuggestBox.setValueQuery(suggestBox.getValueQuery());
			gWTsuggestBox.setFilterMinLen(suggestBox.getFilterMinLen());
			gWTsuggestBox.setData(suggestBox.getData());
			return gWTsuggestBox;
		} else if (formElement instanceof CheckBox) {
			GWTCheckBox gWTCheckbox = new GWTCheckBox();
			gWTCheckbox.setName(formElement.getName());
			gWTCheckbox.setLabel(formElement.getLabel());
			CheckBox checkbox = (CheckBox) formElement;
			gWTCheckbox.setValue(checkbox.getValue());
			gWTCheckbox.setReadonly(checkbox.isReadonly());
			gWTCheckbox.setValidators(copyValidators(checkbox.getValidators()));
			gWTCheckbox.setData(checkbox.getData());
			return gWTCheckbox;
		} else if (formElement instanceof Select) {
			GWTSelect gWTselect = new GWTSelect();
			gWTselect.setName(formElement.getName());
			gWTselect.setLabel(formElement.getLabel());
			gWTselect.setWidth(formElement.getWidth());
			gWTselect.setHeight(formElement.getHeight());
			Select select = (Select) formElement;
			gWTselect.setType(select.getType());
			gWTselect.setReadonly(select.isReadonly());
			List<GWTOption> options = new ArrayList<GWTOption>();
			
			for (Iterator<Option> it = select.getOptions().iterator(); it.hasNext();) {
				options.add(copy(it.next()));
			}
			
			gWTselect.setOptions(options);
			gWTselect.setValidators(copyValidators(select.getValidators()));
			gWTselect.setData(select.getData());
			return gWTselect;
		} else if (formElement instanceof TextArea) {
			GWTTextArea gWTTextArea= new GWTTextArea();
			gWTTextArea.setName(formElement.getName());
			gWTTextArea.setLabel(formElement.getLabel());
			gWTTextArea.setWidth(formElement.getWidth());
			gWTTextArea.setHeight(formElement.getHeight());
			TextArea textArea = (TextArea) formElement;
			gWTTextArea.setValue(textArea.getValue());
			gWTTextArea.setReadonly(textArea.isReadonly());
			gWTTextArea.setValidators(copyValidators(textArea.getValidators()));
			gWTTextArea.setData(textArea.getData());
			return gWTTextArea;
		} else if (formElement instanceof Upload) {
			GWTUpload gWTUpload= new GWTUpload();
			gWTUpload.setName(formElement.getName());
			gWTUpload.setLabel(formElement.getLabel());
			gWTUpload.setWidth(formElement.getWidth());
			gWTUpload.setHeight(formElement.getHeight());
			Upload upload = (Upload) formElement;
			gWTUpload.setFolderPath(upload.getFolderPath());
			gWTUpload.setFolderUuid(upload.getFolderUuid());
			gWTUpload.setDocumentName(upload.getDocumentName());
			gWTUpload.setDocumentUuid(upload.getDocumentUuid());
			gWTUpload.setType(upload.getType());
			gWTUpload.setData(upload.getData());
			gWTUpload.setValidators(copyValidators(upload.getValidators()));
			return gWTUpload;
		} else if (formElement instanceof Text) {
			GWTText gWTtext = new GWTText();
			gWTtext.setName(formElement.getName());
			gWTtext.setLabel(formElement.getLabel());
			gWTtext.setHeight(formElement.getHeight());
			gWTtext.setWidth(formElement.getWidth());
			Text text = (Text) formElement;
			gWTtext.setData(text.getData());
			return gWTtext;
		} else if (formElement instanceof Separator) {
			GWTSeparator separator = new GWTSeparator();
			separator.setName(formElement.getName());
			separator.setLabel(formElement.getLabel());
			separator.setHeight(formElement.getHeight());
			separator.setWidth(formElement.getWidth());
			return separator;
		} else if (formElement instanceof Download) {
			GWTDownload gWTdownload = new GWTDownload();
			gWTdownload.setName(formElement.getName());
			gWTdownload.setLabel(formElement.getLabel());
			gWTdownload.setHeight(formElement.getHeight());
			gWTdownload.setWidth(formElement.getWidth());
			Download download = (Download) formElement;
			gWTdownload.setData(download.getData());
			gWTdownload.setValidators(copyValidators(download.getValidators()));
			gWTdownload.setNodes(copyNodes(download.getNodes()));
			return gWTdownload;
		} else if (formElement instanceof Print) {
			GWTPrint gWTprint = new GWTPrint();
			gWTprint.setName(formElement.getName());
			gWTprint.setLabel(formElement.getLabel());
			gWTprint.setHeight(formElement.getHeight());
			gWTprint.setWidth(formElement.getWidth());
			Print download = (Print) formElement;
			gWTprint.setData(download.getData());
			gWTprint.setValidators(copyValidators(download.getValidators()));
			gWTprint.setNodes(copyNodes(download.getNodes()));
			return gWTprint;
		} else {
			return new GWTFormElement();
		}
	}
	
	/**
	 * Copy to GWTFormElement data to FormElement
	 * 
	 * @param GWTFormElement the original data
	 * @return The FormElement object with data values from original GWTFormElement
	 */
	public static FormElement copy(GWTFormElement formElement) {
		if (formElement instanceof GWTButton) {
			Button button = new Button();
			button.setName(formElement.getName());
			GWTButton gWTButton = ((GWTButton) formElement);
			button.setTransition(gWTButton.getTransition());
			button.setConfirmation(gWTButton.getConfirmation());
			return button;
		} else if (formElement instanceof GWTInput) {
			Input input = new Input();
			input.setName(formElement.getName());
			GWTInput gWTInput = ((GWTInput) formElement);
			input.setReadonly(gWTInput.isReadonly());
			
			if (gWTInput.getType().equals(GWTInput.TYPE_TEXT) || 
				gWTInput.getType().equals(GWTInput.TYPE_LINK) ||
				gWTInput.getType().equals(GWTInput.TYPE_FOLDER) ) {
				input.setValue(gWTInput.getValue());
			} else if (gWTInput.getType().equals(GWTInput.TYPE_DATE)) {
				if (gWTInput.getDate() != null) {
					Calendar cal = Calendar.getInstance();
					cal.setTime(((GWTInput) formElement).getDate());
					input.setValue(ISO8601.format(cal));
				}
			}
			
			input.setType(gWTInput.getType());
			input.setData(gWTInput.getData());
			return input;
		} else if(formElement instanceof GWTSuggestBox) {
			SuggestBox suggestBox = new SuggestBox();
			suggestBox.setName(formElement.getName());
			GWTSuggestBox gWTSuggestBox = ((GWTSuggestBox)formElement);
			suggestBox.setReadonly(gWTSuggestBox.isReadonly());
			suggestBox.setValue(gWTSuggestBox.getValue());
			suggestBox.setFilterQuery(gWTSuggestBox.getFilterQuery());
			suggestBox.setValueQuery(gWTSuggestBox.getValueQuery());
			suggestBox.setFilterMinLen(gWTSuggestBox.getFilterMinLen());
			suggestBox.setData(gWTSuggestBox.getData());
			return suggestBox;
		} else if (formElement instanceof GWTCheckBox) {
			CheckBox checkbox = new CheckBox();
			checkbox.setLabel(formElement.getLabel());
			checkbox.setName(formElement.getName());
			GWTCheckBox gWTCheckBox = ((GWTCheckBox) formElement);
			checkbox.setValue(gWTCheckBox.getValue());
			checkbox.setReadonly(gWTCheckBox.isReadonly());
			checkbox.setData(gWTCheckBox.getData());
			return checkbox;
		} else if (formElement instanceof GWTSelect) {
			Select select = new Select();
			select.setName(formElement.getName());
			GWTSelect gWTSelect = ((GWTSelect) formElement);
			select.setType(gWTSelect.getType());
			select.setReadonly(gWTSelect.isReadonly());
			List<Option> options = new ArrayList<Option>();
			
			for (GWTOption option : gWTSelect.getOptions()) {
				options.add(copy(option));
				
				if (option.isSelected()) {
					if (Select.TYPE_SIMPLE.equals(select.getType())) {
						select.setValue(option.getValue());
					} else {
						if ("".equals(select.getValue())) {
							select.setValue(option.getValue());
						} else {
							select.setValue(select.getValue().concat(",").concat(option.getValue()));
						}
					}
				}
			}
			
			select.setOptions(options);
			select.setData(gWTSelect.getData());
			return select;
		} else if (formElement instanceof GWTTextArea) {
			TextArea textArea= new TextArea();
			textArea.setName(formElement.getName());
			GWTTextArea gWTTextArea = ((GWTTextArea) formElement);
			textArea.setValue(gWTTextArea.getValue());
			textArea.setReadonly(gWTTextArea.isReadonly());
			textArea.setData(gWTTextArea.getData());
			return textArea;
		} else if (formElement instanceof GWTUpload) {
			Upload upload= new Upload();
			upload.setName(formElement.getName());
			GWTUpload gWTUpload = ((GWTUpload) formElement);
			upload.setDocumentName(gWTUpload.getDocumentName());
			upload.setDocumentUuid(gWTUpload.getDocumentUuid());
			upload.setFolderPath(gWTUpload.getFolderPath());
			upload.setFolderUuid(gWTUpload.getFolderUuid());
			upload.setType(gWTUpload.getType());
			upload.setData(gWTUpload.getData());
			return upload;
		} else if (formElement instanceof GWTText) {
			Text text = new Text();
			GWTText gWTText = (GWTText) formElement;
			text.setName(gWTText.getName());
			text.setLabel(gWTText.getLabel());
			text.setHeight(gWTText.getHeight());
			text.setWidth(gWTText.getWidth());
			text.setData(gWTText.getData());
			return text;
		} else if (formElement instanceof GWTSeparator) {
			Separator separator = new Separator();
			GWTSeparator gWTSeparator = (GWTSeparator) formElement;
			separator.setName(gWTSeparator.getName());
			separator.setLabel(gWTSeparator.getLabel());
			separator.setHeight(gWTSeparator.getHeight());
			separator.setWidth(gWTSeparator.getWidth());
			return separator;
		} else if (formElement instanceof GWTDownload) {
			Download download = new Download();
			GWTDownload gWTDownload = (GWTDownload) formElement;
			download.setName(gWTDownload.getName());
			download.setLabel(gWTDownload.getLabel());
			download.setHeight(gWTDownload.getHeight());
			download.setWidth(gWTDownload.getWidth());
			download.setData(gWTDownload.getData());
			List<Node> nodes = new ArrayList<Node>();
			
			for (GWTNode gWTNode : gWTDownload.getNodes()) {
				nodes.add(copy(gWTNode));
			}
			
			return download;
		} else if (formElement instanceof GWTPrint) {
			Print print = new Print();
			GWTPrint gWTprint = (GWTPrint) formElement;
			print.setName(gWTprint.getName());
			print.setLabel(gWTprint.getLabel());
			print.setHeight(gWTprint.getHeight());
			print.setWidth(gWTprint.getWidth());
			print.setData(gWTprint.getData());
			List<Node> nodes = new ArrayList<Node>();
			
			for (GWTNode gWTNode : gWTprint.getNodes()) {
				nodes.add(copy(gWTNode));
			}
			
			return print;
		} else {
			return new FormElement();
		}
	}
	
	/**
	 * getFormElementValue
	 */
	public static Object getFormElementValue(GWTFormElement formElement) throws DatabaseException {
		if (formElement instanceof GWTButton) {
			return ((GWTButton) formElement).getLabel();
		} else if (formElement instanceof GWTInput) {
			GWTInput input = (GWTInput) formElement;
			
			if (GWTInput.TYPE_DATE.equals(input.getType())) {
				return input.getDate();
			} else {
				return input.getValue();
			}
		} else if (formElement instanceof GWTSuggestBox) {
			GWTSuggestBox suggestBox = (GWTSuggestBox) formElement;
			
			// The ' character must be replaced to \" to be correctly parsed
			// and after it must change all " characters to '
			String formatedQuery =  MessageFormat.format(suggestBox.getValueQuery().replaceAll("'", "\\\""), suggestBox.getValue()).replaceAll("\"", "'");
			List<KeyValue> keyValues = KeyValueDAO.getKeyValues(Arrays.asList(suggestBox.getTable()), formatedQuery);
			
			if (!keyValues.isEmpty()) {
				return keyValues.get(0).getValue();
			} else {
				return "";
			}
		} else if (formElement instanceof GWTCheckBox) {
			return ((GWTCheckBox) formElement).getValue()?"true":"false";
		} else if (formElement instanceof GWTSelect) {
			String value = "";
			
			for (Iterator<GWTOption> it = ((GWTSelect) formElement).getOptions().iterator(); it.hasNext();) {
				GWTOption option = it.next();
				
				if (option.isSelected()) {
					value += option.getLabel() + " ";
				}
			}
			
			return value;
		} else if (formElement instanceof GWTTextArea) {
			return ((GWTTextArea) formElement).getValue();
		} else if (formElement instanceof GWTUpload) {
			return ((GWTButton) formElement).getLabel();
		} else if (formElement instanceof GWTText) {
			return ((GWTText) formElement).getLabel();
		} else if (formElement instanceof GWTSeparator) {
			return ((GWTSeparator) formElement).getLabel();
		} else if (formElement instanceof GWTDownload) {
			GWTDownload download = ((GWTDownload) formElement);
			String value = "";
			
			for (GWTNode node : download.getNodes()) {
				if (!value.equals("")) {
					value += ",";
				}
				
				if (!node.getUuid().equals("")) {
					value += node.getUuid();
				} else {
					value += node.getPath();
				}
			}
			
			return value;
		} else if (formElement instanceof GWTPrint) {
			GWTPrint print = ((GWTPrint) formElement);
			String value = "";
			
			for (GWTNode node : print.getNodes()) {
				if (!value.equals("")) {
					value += ",";
				}
				
				if (!node.getUuid().equals("")) {
					value += node.getUuid();
				} else {
					value += node.getPath();
				}
			}
			
			return value;
		}
		
		return "";
	}
	
	/**
	 * Copy to Validator data to  GWTValidator
	 * 
	 * @param Validator the original data
	 * @return The GWTValidator object with data values from original Validator
	 */
	public static GWTValidator copy(Validator validator) {
		GWTValidator gWTValidator = new GWTValidator();
		gWTValidator.setParameter(validator.getParameter());
		gWTValidator.setType(validator.getType());
		return gWTValidator;
	}
	
	/**
	 * Copy to Node data to  GWTNode
	 * 
	 * @param Node the original data
	 * @return The GWTNode object with data values from original Node
	 */
	public static GWTNode copy(Node node) {
		GWTNode gWTNode = new GWTNode();
		gWTNode.setLabel(node.getLabel());
		gWTNode.setPath(node.getPath());
		gWTNode.setUuid(node.getUuid());
		return gWTNode;
	}
	
	/**
	 * Copy to GWTNode data to Node
	 * 
	 * @param GWTNode the original data
	 * @return The Node object with data values from original GWTNode
	 */
	public static Node copy(GWTNode gWTNode) {
		Node node = new Node();
		node.setLabel(gWTNode.getLabel());
		node.setPath(gWTNode.getPath());
		node.setUuid(gWTNode.getUuid());
		return node;
	}
	
	/**
	 * Copy to GWTOption data to  Option
	 * 
	 * @param GWTOption the original data
	 * @return The Option object with data values from original GWTOption
	 */
	public static Option copy(GWTOption gWTOption) {
		Option option = new Option();
		option.setLabel(gWTOption.getLabel());
		option.setValue(gWTOption.getValue());
		option.setSelected(gWTOption.isSelected());
		return option;
	}
	
	/**
	 * Copy to Option data to  GWTOption
	 * 
	 * @param Option the original data
	 * @return The GWTOption object with data values from original Option
	 */
	public static GWTOption copy(Option option) {
		GWTOption gWTOption = new GWTOption();
		gWTOption.setLabel(option.getLabel());
		gWTOption.setValue(option.getValue());
		gWTOption.setSelected(option.isSelected());
		return gWTOption;
	}
	
	/**
	 * Copy to Comment data to  GWTComment
	 * 
	 * @param Comment the original data
	 * @return The GWTTaskInstanceComment object with data values from original TaskInstanceComment
	 */
	public static List<GWTComment> copyComments(List<Comment> list) {
		List<GWTComment> al = new ArrayList<GWTComment>();
		GWTComment gWTComment;
		
		for (Iterator<Comment> it = list.iterator(); it.hasNext();) {
			Comment comment = it.next();
			gWTComment = new GWTComment();
		
			gWTComment.setActorId(comment.getActorId());
			gWTComment.setMessage(comment.getMessage());
			gWTComment.setTime(comment.getTime().getTime());
			al.add(gWTComment);
		}
		
		return al;
	}
	
	/**
	 * Copy Note data to GWTNote
	 * 
	 * @param Note the original data
	 * @return The GWTNote object with data values from original Note
	 */
	public static List<GWTNote> copy(List<Note> commentList) {
		List<GWTNote> gWTCommentList = new ArrayList<GWTNote>();

		for (Iterator<Note> it = commentList.iterator(); it.hasNext();) {
			gWTCommentList.add(copy(it.next()));
		}
		
		return gWTCommentList;
	}
	
	/**
	 * Copy Note data to GWTNote
	 * 
	 * @param Note the original data
	 * @return The GWTNote object with data values from original Note
	 */
	public static GWTNote copy(Note note) {
		GWTNote gWTNote = new GWTNote();
		
		gWTNote.setDate(note.getDate().getTime());
		gWTNote.setText(note.getText());
		gWTNote.setUser(note.getUser());
		gWTNote.setPath(note.getPath());
		return gWTNote;
	}
	
	/**
	 * Copy the Mail data to GWTMail data.
	 * 
	 * @param mail The original Mail object.
	 * @return A GWTMail object with the data from 
	 * the original Mail.
	 */
	public static GWTMail copy(Mail mail) {
		log.debug("copy({})", mail);
		GWTMail gWTMail = new GWTMail();
		List<GWTDocument> attachList = new ArrayList<GWTDocument>();
		
		for (Iterator<Document> it = mail.getAttachments().iterator(); it.hasNext();) {
			attachList.add(copy(it.next()));
		}
		
		gWTMail.setAttachments(attachList);
		gWTMail.setBcc(mail.getBcc());
		gWTMail.setCc(mail.getCc());
		gWTMail.setContent(mail.getContent());
		gWTMail.setFrom(mail.getFrom());
		gWTMail.setPath(mail.getPath());
		gWTMail.setPermissions(mail.getPermissions());
		gWTMail.setReceivedDate(mail.getReceivedDate().getTime());
		gWTMail.setReply(mail.getReply());
		gWTMail.setSentDate(mail.getSentDate().getTime());
		gWTMail.setSize(mail.getSize());
		gWTMail.setSubject(mail.getSubject());
		gWTMail.setTo(mail.getTo());
		gWTMail.setUuid(mail.getUuid());
		gWTMail.setParent(GWTUtil.getParent(mail.getPath()));
		gWTMail.setMimeType(mail.getMimeType());
		
		log.debug("copy: {}", gWTMail);
		return gWTMail;
	}
	
	/**
	 * Copy the PropertyGroup data to GWTPropertyGroup data.
	 * 
	 * @param doc The original PropertyGroup object.
	 * @return A GWTPropertyGroup object with the data from 
	 * the original PropertyGroup.
	 */
	public static GWTPropertyGroup copy(PropertyGroup property) {
		GWTPropertyGroup gWTPropertyGroup = new GWTPropertyGroup();
		
		gWTPropertyGroup.setLabel(property.getLabel());
		gWTPropertyGroup.setName(property.getName());
		gWTPropertyGroup.setVisible(property.isVisible());
		gWTPropertyGroup.setReadonly(property.isReadonly());
		
		return gWTPropertyGroup;
	}
	
	/**
	 * Copy the UserConfig data to GWTUserConfig data.
	 * 
	 * @param doc The original UserConfig object.
	 * @return A GWTUserConfig object with the data from 
	 * the original UserConfig.
	 */
	public static GWTUserConfig copy(UserConfig userCopy) {
		GWTUserConfig gWTUserConfig = new GWTUserConfig();
		
		gWTUserConfig.setHomePath(userCopy.getHomePath());
		gWTUserConfig.setHomeType(userCopy.getHomeType());
		gWTUserConfig.setHomeUuid(userCopy.getHomeUuid());
		gWTUserConfig.setUser(userCopy.getUser());
		
		return gWTUserConfig;
	}
	
	/**
	 * Copy the StaplingGroup data to GWTStapleGroup data.
	 * 
	 * @param doc The original StaplingGroup object.
	 * @return A GWTStaplingGroup object with the data from 
	 * the original StaplingGroup.
	 */
	public static GWTStapleGroup copy(StapleGroup sg) throws RepositoryException, DatabaseException, 
			PathNotFoundException {
		GWTStapleGroup gsg = new GWTStapleGroup();
		gsg.setId(sg.getId());
		gsg.setUser(sg.getUser());
		
		for (Staple st: sg.getStaples()) {
			GWTStaple gst = new GWTStaple();
			gst.setId(st.getId());
			gst.setType(st.getType());
			
			try {
				// Getting document / folder / mail properties 
				if (st.getType().equals(Staple.STAPLE_DOCUMENT)) {
					String path = OKMDocument.getInstance().getPath(null, st.getUuid());
					gst.setDoc(copy(OKMDocument.getInstance().getProperties(null, path)));
				} else if (st.getType().equals(Staple.STAPLE_FOLDER)) {
					String path = OKMFolder.getInstance().getPath(null, st.getUuid());
					gst.setFolder(copy(OKMFolder.getInstance().getProperties(null, path)));
				} else if (st.getType().equals(Staple.STAPLE_MAIL)) {
					String path = OKMMail.getInstance().getPath(null, st.getUuid());
					gst.setMail(GWTUtil.copy(OKMMail.getInstance().getProperties(null, path)));
				}
				gsg.getStaples().add(gst);
			} catch (AccessDeniedException e){
				// Silent exception, some users have not accessing to some documents, folders or mails
			}
		}
		
		return gsg;
	}
	
	/**
	 * Copy Language to GWTLanguage
	 */
	public static GWTLanguage copy(Language language) {
		GWTLanguage gWTlang = new GWTLanguage();
		gWTlang.setId(language.getId());
		gWTlang.setName(language.getName());
		return gWTlang;
	}
	
	/**
	 * Copy GWTProposedSubscriptionReceived to GWTProposedSubscription
	 */
	public static GWTProposedSubscriptionReceived copy(ProposedSubscriptionReceived ps) {
		GWTProposedSubscriptionReceived gWTProposedSubscription = new GWTProposedSubscriptionReceived();
		gWTProposedSubscription.setId(ps.getId());
		gWTProposedSubscription.setFrom(ps.getFrom());
		gWTProposedSubscription.setTo(ps.getTo());
		gWTProposedSubscription.setUuid(ps.getUuid());
		gWTProposedSubscription.setPath(ps.getPath());
		gWTProposedSubscription.setType(ps.getType());
		gWTProposedSubscription.setComment(ps.getComment());
		gWTProposedSubscription.setAccepted(ps.isAccepted());
		gWTProposedSubscription.setSentDate(ps.getSentDate().getTime());
		
		if (ps.getSeenDate()!=null) {
			gWTProposedSubscription.setSeenDate(ps.getSeenDate().getTime());
		}
		
		return gWTProposedSubscription;
	}
	
	/**
	 * Copy ProposedSubscriptionSent to GWTProposedSubscriptionSent
	 */
	public static GWTProposedSubscriptionSent copy(ProposedSubscriptionSent ps) {
		GWTProposedSubscriptionSent gWTProposedSubscription = new GWTProposedSubscriptionSent();
		gWTProposedSubscription.setId(ps.getId());
		gWTProposedSubscription.setFrom(ps.getFrom());
		gWTProposedSubscription.setTo(ps.getTo());
		gWTProposedSubscription.setUuid(ps.getUuid());
		gWTProposedSubscription.setPath(ps.getPath());
		gWTProposedSubscription.setType(ps.getType());
		gWTProposedSubscription.setComment(ps.getComment());
		gWTProposedSubscription.setSentDate(ps.getSentDate().getTime());
		
		return gWTProposedSubscription;
	}
	
	/**
	 * Copy ProposedQuery to GWTProposedQuery
	 */
	public static GWTProposedQueryReceived copy(ProposedQueryReceived pq, QueryParams params) 
			throws RepositoryException, IOException, PathNotFoundException, ParseException,
			DatabaseException {
		GWTProposedQueryReceived gWTProposedQuery = new GWTProposedQueryReceived();
		gWTProposedQuery.setAccepted(pq.isAccepted());
		gWTProposedQuery.setComment(pq.getComment());
		gWTProposedQuery.setFrom(pq.getFrom());
		gWTProposedQuery.setTo(pq.getTo());
		gWTProposedQuery.setUser(pq.getUser());
		gWTProposedQuery.setId(pq.getId());
		gWTProposedQuery.setSentDate(pq.getSentDate().getTime());
		
		if (pq.getSeenDate() != null) {
			gWTProposedQuery.setSeenDate(pq.getSeenDate().getTime());
		}
		
		gWTProposedQuery.setParams(copy(params));
		return gWTProposedQuery;
	}
	
	/**
	 * Copy ProposedQuery to GWTProposedQuery
	 */
	public static GWTProposedQuerySent copy(ProposedQuerySent pq, QueryParams params) 
			throws RepositoryException, IOException, PathNotFoundException, ParseException,
			DatabaseException {
		GWTProposedQuerySent gWTProposedQuery = new GWTProposedQuerySent();
		gWTProposedQuery.setComment(pq.getComment());
		gWTProposedQuery.setFrom(pq.getFrom());
		gWTProposedQuery.setTo(pq.getTo());
		gWTProposedQuery.setUser(pq.getUser());
		gWTProposedQuery.setId(pq.getId());
		gWTProposedQuery.setSentDate(pq.getSentDate().getTime());
		gWTProposedQuery.setParams(copy(params));
		return gWTProposedQuery;
	}
	
	/**
	 * Copy MessageSent to GWTMessageSent
	 */
	public static GWTTextMessageSent copy(MessageSent ms) {
		GWTTextMessageSent gWTMessageSent  = new GWTTextMessageSent();
		gWTMessageSent.setContent(ms.getContent());
		gWTMessageSent.setFrom(ms.getFrom());
		gWTMessageSent.setUser(ms.getUser());
		gWTMessageSent.setId(ms.getId());
		gWTMessageSent.setSentDate(ms.getSentDate().getTime());
		gWTMessageSent.setSubject(ms.getSubject());
		gWTMessageSent.setTo(ms.getTo());
		
		return gWTMessageSent;
	}
	
	/**
	 * Copy MessageReceived to GWTMessageReceived
	 */
	public static GWTMessageReceived copy(MessageReceived mr) {
		GWTMessageReceived gWTMessageReceived = new GWTMessageReceived();
		gWTMessageReceived.setContent(mr.getContent());
		gWTMessageReceived.setFrom(mr.getFrom());
		gWTMessageReceived.setId(mr.getId());
		
		if (mr.getSeenDate() != null) {
			gWTMessageReceived.setSeenDate(mr.getSeenDate().getTime());
		}
		
		gWTMessageReceived.setSentDate(mr.getSentDate().getTime());
		gWTMessageReceived.setSubject(mr.getSubject());
		gWTMessageReceived.setTo(mr.getTo());
		gWTMessageReceived.setUser(mr.getUser());
		
		return gWTMessageReceived;
	}
	
	/**
	 * Copy StampText to GWTStamp
	 */
	public static GWTStamp copy(StampText st) {
		GWTStamp gst = new GWTStamp();
		
		gst.setId(st.getId());
		gst.setName(st.getName());
		gst.setType(GWTStamp.STAMP_TEXT);
		
		return gst;
	}
	
	/**
	 * Copy StampText to StampImage
	 */
	public static GWTStamp copy(StampImage st) {
		GWTStamp gst = new GWTStamp();
		
		gst.setId(st.getId());
		gst.setName(st.getName());
		gst.setType(GWTStamp.STAMP_IMAGE);
		
		return gst;
	}
	
	/**
	 * Copy GWTContact to  Contact
	 */
	public static Contact copy(GWTContact gWTContact) {
		Contact contact = new Contact();
		contact.setAddress(gWTContact.getAddress());
		contact.setCity(gWTContact.getCity());
		contact.setCountry(gWTContact.getCountry());
		contact.setFax(gWTContact.getFax());
		contact.setId(gWTContact.getId());
		contact.setMail(gWTContact.getMail());
		contact.setMobile(gWTContact.getMobile());
		contact.setName(gWTContact.getName());
		contact.setNotes(gWTContact.getNotes());
		contact.setPostalCode(gWTContact.getPostalCode());
		contact.setPhone(gWTContact.getPhone());
		contact.setProvince(gWTContact.getProvince());
		contact.setWeb(gWTContact.getWeb());
		contact.setExternalId(gWTContact.getExternalId());
		contact.setOrigin(gWTContact.getOrigin());
		
		return contact;
	}
	
	/**
	 * Copy Contact to GWTContact
	 */
	public static GWTContact copy(Contact contact, String uuid) {
		GWTContact gwtContact = new GWTContact();
		gwtContact.setAddress(contact.getAddress());
		gwtContact.setCity(contact.getCity());
		gwtContact.setCountry(contact.getCountry());
		gwtContact.setFax(contact.getFax());
		gwtContact.setId(contact.getId());
		gwtContact.setMail(contact.getMail());
		gwtContact.setMobile(contact.getMobile());
		gwtContact.setName(contact.getName());
		gwtContact.setNotes(contact.getNotes());
		gwtContact.setPostalCode(contact.getPostalCode());
		gwtContact.setPhone(contact.getPhone());
		gwtContact.setProvince(contact.getProvince());
		gwtContact.setWeb(contact.getWeb());
		
		return gwtContact;
	}
	
	/**
	 * Copy Activity to GWTActivity
	 */
	public static GWTActivity copy(Activity activity) {
		GWTActivity gWTActivity = new GWTActivity();
		gWTActivity.setAction(activity.getAction());
		gWTActivity.setDate(activity.getDate().getTime());
		gWTActivity.setId(activity.getId());
		gWTActivity.setItem(activity.getItem());
		gWTActivity.setParams(activity.getParams());
		gWTActivity.setUser(activity.getUser());
		
		return gWTActivity;
	}
	
	/**
	 * Copy Forum to GWTForum
	 */
	public static GWTForum copy(Forum forum) {
		GWTForum f = new GWTForum();
		f.setActive(forum.isActive());
		f.setDate(forum.getDate().getTime());
		f.setDescription(forum.getDescription());
		f.setId(forum.getId());
		f.setLastPostDate(forum.getLastPostDate().getTime());
		f.setLastPostUser(forum.getLastPostUser());
		f.setName(forum.getName());
		f.setNumPosts(forum.getNumPosts());
		f.setNumTopics(forum.getNumTopics());
		
		for (ForumTopic topic : forum.getTopics()) {
			f.getTopics().add(copy(topic));
		}
		
		return f;
	}
	
	/**
	 * Copy ForumTopic to GWTForumTopic
	 */
	public static GWTForumTopic copy(ForumTopic topic) {
		GWTForumTopic ft = new GWTForumTopic();
		ft.setDate(topic.getDate().getTime());
		ft.setId(topic.getId());
		ft.setReplies(topic.getReplies());
		ft.setLastPostDate(topic.getLastPostDate().getTime());
		ft.setLastPostUser(topic.getLastPostUser());
		ft.setTitle(topic.getTitle());
		ft.setUser(topic.getUser());
		ft.setUuid(topic.getUuid());
		ft.setViews(topic.getViews());
		
		for (Iterator<ForumPost> it = topic.getPosts().iterator(); it.hasNext();) {
			ft.getPosts().add(copy(it.next()));
		}
		
		return ft;
	}
	
	/**
	 * Copy ForumPost to GWTForumPost
	 */
	public static GWTForumPost copy(ForumPost post) {
		GWTForumPost fp = new GWTForumPost();
		fp.setDate(post.getDate().getTime());
		fp.setId(post.getId());
		fp.setMessage(post.getMessage());
		fp.setSubject(post.getSubject());
		fp.setUser(post.getUser());
		return fp;
	}
	
	/**
	 * Copy GWTForum to Forum
	 */
	public static Forum copy(GWTForum forum) {
		Forum f = new Forum();
		f.setActive(forum.isActive());
		Calendar cal = Calendar.getInstance();
		cal.setTime(forum.getDate());
		f.setDate(cal);
		f.setDescription(forum.getDescription());
		f.setId(forum.getId());
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(forum.getLastPostDate());
		f.setLastPostDate(cal2);
		f.setLastPostUser(forum.getLastPostUser());
		f.setName(forum.getName());
		f.setNumPosts(forum.getNumPosts());
		f.setNumTopics(forum.getNumTopics());
		
		for (GWTForumTopic topic : forum.getTopics()) {
			f.getTopics().add(copy(topic));
		}
		
		return f;
	}
	
	/**
	 * Copy GWTForumTopic to ForumTopic
	 */
	public static ForumTopic copy(GWTForumTopic topic) {
		ForumTopic ft = new ForumTopic();
		Calendar date = Calendar.getInstance();
		date.setTime(topic.getDate());
		ft.setDate(date);
		ft.setId(topic.getId());
		ft.setReplies(topic.getReplies());
		Calendar lastDate = Calendar.getInstance();
		lastDate.setTime(topic.getLastPostDate());
		ft.setLastPostDate(lastDate);
		ft.setLastPostUser(topic.getLastPostUser());
		ft.setTitle(topic.getTitle());
		ft.setUser(topic.getUser());
		ft.setUuid(topic.getUuid());
		ft.setViews(topic.getViews());
		
		for (Iterator<GWTForumPost> it = topic.getPosts().iterator(); it.hasNext();) {
			ft.getPosts().add(copy(it.next()));
		}
		
		return ft;
	}
	
	/**
	 * Copy ForumPost to GWTForumPost
	 */
	public static ForumPost copy(GWTForumPost post) {
		ForumPost fp = new ForumPost();
		Calendar date = Calendar.getInstance();
		date.setTime(post.getDate());
		fp.setDate(date);
		fp.setId(post.getId());
		fp.setMessage(post.getMessage());
		fp.setSubject(post.getSubject());
		fp.setUser(post.getUser());
		return fp;
	}
	
	/**
	 * Copy KeyValue to GWTKeyValue
	 */
	public static GWTKeyValue copy(KeyValue keyValue) {
		GWTKeyValue gWTKeyValue = new GWTKeyValue();
		gWTKeyValue.setKey(keyValue.getKey());
		gWTKeyValue.setValue(keyValue.getValue());
		return gWTKeyValue;
	}
	
	/**
	 * Copy Report to GWTReport
	 */
	public static GWTReport copy(Report report, List<FormElement> formElements) throws PathNotFoundException,
			RepositoryException, DatabaseException {
		GWTReport gWTReport = new GWTReport();
		gWTReport.setActive(report.isActive());
		gWTReport.setFileContent(report.getFileContent());
		gWTReport.setFileMime(report.getFileMime());
		gWTReport.setFileName(report.getFileName());
		gWTReport.setId(report.getId());
		gWTReport.setName(report.getName());
		List<GWTFormElement> gWTFormElemets = new ArrayList<GWTFormElement>();
		
		for (FormElement formElement : formElements) {
			gWTFormElemets.add(copy(formElement));
		}
		
		gWTReport.setFormElements(gWTFormElemets);
		return gWTReport;
	}
}
