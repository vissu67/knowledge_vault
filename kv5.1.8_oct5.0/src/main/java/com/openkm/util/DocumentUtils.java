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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.ZipFile;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.io.IOUtils;
import org.apache.jackrabbit.JcrConstants;
import org.dts.spell.SpellChecker;
import org.dts.spell.dictionary.OpenOfficeSpellDictionary;
import org.jbpm.JbpmContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Document;
import com.openkm.bean.form.FormElement;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.dao.DocumentFilterDAO;
import com.openkm.dao.bean.DocumentFilter;
import com.openkm.dao.bean.DocumentFilterRule;
import com.openkm.jcr.JCRUtils;
import com.openkm.module.base.BasePropertyGroupModule;
import com.openkm.module.base.BasePropertyModule;
import com.openkm.module.base.BaseWorkflowModule;
import com.openkm.util.metadata.MetadataExtractor;
import com.openkm.util.metadata.OfficeMetadata;
import com.openkm.util.metadata.OpenOfficeMetadata;
import com.openkm.util.metadata.PdfMetadata;

public class DocumentUtils {
	private static Logger log = LoggerFactory.getLogger(DocumentUtils.class);
	
	/**
	 * Check for document filters and execute proper actions.
	 */	
	public static void checkFilters(Session session, Node node, String mimeType) throws DatabaseException,
			RepositoryException {
		log.info("checkFilters({}, {})", node.getPath(), mimeType);
		
		for (DocumentFilter df : DocumentFilterDAO.findAll(true)) {
			boolean match = false;
			
			if (df.isActive()) {
				if (df.getType().equals(DocumentFilter.TYPE_PATH) && checkPathFilter(node, df.getValue())) {
					match = true;
				} else if (df.getType().equals(DocumentFilter.TYPE_MIME_TYPE) && df.getValue().equals(mimeType)) {
					match = true;
				}
				
				if (match) {
					for (DocumentFilterRule dfr : df.getFilterRules()) {
						if (dfr.isActive()) {
							if (DocumentFilterRule.ACTION_ASSIGN_PROPERTY_GROUP.equals(dfr.getAction())) {
								try {
									log.info("ACTION_PROPERTY_GROUP");
									BasePropertyGroupModule.addGroup(session, node, dfr.getValue());
								} catch (Exception e) {
									JCRUtils.discardsPendingChanges(node);
								}
							} else if (DocumentFilterRule.ACTION_ASSIGN_WORKFLOW.equals(dfr.getAction())) {
								JbpmContext jbpmContext = null;
								
								try {
									log.info("ACTION_WORKFLOW");
									jbpmContext = JBPMUtils.getConfig().createJbpmContext();
									List<FormElement> vars = new ArrayList<FormElement>();
									int pdId = Integer.parseInt(dfr.getValue());
									BaseWorkflowModule.runProcessDefinition(session, jbpmContext, pdId, node.getUUID(), vars);
								} finally {
									if (jbpmContext != null) { 
										jbpmContext.close();
									}
								}
							} else if (DocumentFilterRule.ACTION_ADD_CATEGORY.equals(dfr.getAction())) {
								try {
									log.info("ACTION_CATEGORY {}", dfr.getValue());
									BasePropertyModule.addCategory(session, node, dfr.getValue());
								} catch (Exception e) {
									JCRUtils.discardsPendingChanges(node);
								}
							} else if (DocumentFilterRule.ACTION_ADD_KEYWORD.equals(dfr.getAction())) {
								try {
									log.info("ACTION_KEYWORD: {}", dfr.getValue());
									BasePropertyModule.addKeyword(session, node, dfr.getValue());
								} catch (Exception e) {
									JCRUtils.discardsPendingChanges(node);
								}
							} else if (DocumentFilterRule.ACTION_EXTRACT_METADATA.equals(dfr.getAction())) {
								InputStream is = null;
								
								try {
									if (Config.MIME_PDF.equals(mimeType)) {
										Node contentNode = node.getNode(Document.CONTENT);
										is = contentNode.getProperty(JcrConstants.JCR_DATA).getStream();
										PdfMetadata md = MetadataExtractor.pdfExtractor(is);
										log.info("{}", md);
									} else if (Config.MIME_MS_WORD.equals(mimeType) ||
											Config.MIME_MS_EXCEL.equals(mimeType) ||
											Config.MIME_MS_POWERPOINT.equals(mimeType)) {
										Node contentNode = node.getNode(Document.CONTENT);
										is = contentNode.getProperty(JcrConstants.JCR_DATA).getStream();
										OfficeMetadata md = MetadataExtractor.officeExtractor(is, mimeType);
										log.info("{}", md);
									} else if (Config.MIME_OO_TEXT.equals(mimeType) ||
												Config.MIME_OO_SPREADSHEET.equals(mimeType) ||
												Config.MIME_OO_PRESENTATION.equals(mimeType)) {
										Node contentNode = node.getNode(Document.CONTENT);
										is = contentNode.getProperty(JcrConstants.JCR_DATA).getStream();
										OpenOfficeMetadata md = new OpenOfficeMetadata();
										log.info("{}", md);
									}
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									IOUtils.closeQuietly(is);
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * Check document path helper
	 */
	private static boolean checkPathFilter(Node node, String path) throws RepositoryException {
		if (node.getPath().equals(path)) {
			return true;
		} else {
			return checkPathFilter(node.getParent(), path);
		}
	}
	
	/**
	 * Text spell checker
	 */
	public static String spellChecker(String text) throws IOException {
		log.debug("spellChecker({})", text);
		StringBuilder sb = new StringBuilder();
		
		if (Config.SYSTEM_OPENOFFICE_DICTIONARY.equals("")) {
			log.warn("OpenOffice dictionary not configured");
			sb.append(text);
		} else {
			log.info("Using OpenOffice dictionary: {}", Config.SYSTEM_OPENOFFICE_DICTIONARY);
			ZipFile zf = new ZipFile(Config.SYSTEM_OPENOFFICE_DICTIONARY);
			OpenOfficeSpellDictionary oosd = new OpenOfficeSpellDictionary(zf);
			SpellChecker sc = new SpellChecker(oosd);
			sc.setCaseSensitive(false);
			StringTokenizer st = new StringTokenizer(text);
			
			while (st.hasMoreTokens()) {
				String w = st.nextToken();
				List<String> s = sc.getDictionary().getSuggestions(w);
				
				if (s.isEmpty()) {
					sb.append(w).append(" ");
				} else {
					sb.append(s.get(0)).append(" ");
				}
			}
			
			zf.close();
		}
		
		log.debug("spellChecker: {}", sb.toString());
		return sb.toString();
	}
}
