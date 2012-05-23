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

package com.openkm.api;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Document;
import com.openkm.bean.QueryResult;
import com.openkm.bean.ResultSet;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.DatabaseException;
import com.openkm.core.ParseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.dao.bean.QueryParams;
import com.openkm.module.ModuleManager;
import com.openkm.module.SearchModule;

/**
 * @author pavila
 *
 */
public class OKMSearch implements SearchModule {
	private static Logger log = LoggerFactory.getLogger(OKMSearch.class);
	private static OKMSearch instance = new OKMSearch();

	private OKMSearch() {}
	
	public static OKMSearch getInstance() {
		return instance;
	}

	@Override
	public List<QueryResult> findByContent(String token, String words) throws IOException, ParseException,
			RepositoryException, DatabaseException {
		log.debug("findByContent({}, {})", token, words);
		SearchModule sm = ModuleManager.getSearchModule();
		List<QueryResult> col = sm.findByContent(token, words);
		log.debug("findByContent: {}", col);
		return col;
	}

	@Override
	public List<QueryResult> findByName(String token, String words) throws IOException, ParseException, 
			RepositoryException, DatabaseException {
		log.debug("findByName({}, {})", token, words);
		SearchModule sm = ModuleManager.getSearchModule();
		List<QueryResult> col = sm.findByName(token, words);
		log.debug("findByName: {}", col);
		return col;
	}

	@Override
	public List<QueryResult> findByKeywords(String token, Set<String> words) throws IOException,
			ParseException, RepositoryException, DatabaseException {
		log.debug("findByKeywords({}, {})", token, words);
		SearchModule sm = ModuleManager.getSearchModule();
		List<QueryResult> col = sm.findByKeywords(token, words);
		log.debug("findByKeywords: {}", col);
		return col;
	}

	@Override
	public List<QueryResult> find(String token, QueryParams params) throws IOException, ParseException, 
			RepositoryException, DatabaseException {
		log.debug("find({}, {})", token, params);
		SearchModule sm = ModuleManager.getSearchModule();
		List<QueryResult> col = sm.find(token, params);
		log.debug("find: {}", col);
		return col;
	}

	@Override
	public ResultSet findPaginated(String token, QueryParams params, int offset, int limit) throws
			IOException, ParseException, RepositoryException, DatabaseException {
		log.debug("findPaginated({}, {}, {}, {})", new Object[] { token, params, offset, limit });
		SearchModule sm = ModuleManager.getSearchModule();
		ResultSet rs = sm.findPaginated(token, params, offset, limit);
		log.debug("findPaginated: {}", rs);
		return rs;
	}

	@Override
	public List<QueryResult> findByStatement(String token, String statement, String type) throws
			RepositoryException, DatabaseException {
		log.debug("findByStatement({}, {}, {})", new Object[] { token, statement, type });
		SearchModule sm = ModuleManager.getSearchModule();
		List<QueryResult> col = sm.findByStatement(token, statement, type);
		log.debug("findByKeywords: {}", col);
		return col;
	}

	@Override
	public ResultSet findByStatementPaginated(String token, String statement, String type, int offset,
			int limit) throws RepositoryException, DatabaseException {
		log.debug("findByStatement({}, {}, {}, {}, {})", new Object[] { token, statement, type, offset, limit });
		SearchModule sm = ModuleManager.getSearchModule();
		ResultSet rs = sm.findByStatementPaginated(token, statement, type, offset, limit);
		log.debug("findByKeywords: {}", rs);
		return rs;
	}

	@Override
	public int saveSearch(String token, QueryParams params) throws AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("saveSearch({}, {})", token, params);
		SearchModule sm = ModuleManager.getSearchModule();
		int id = sm.saveSearch(token, params);
		log.debug("saveSearch: {}", id);
		return id;
	}
	
	@Override
	public void updateSearch(String token, QueryParams params) throws AccessDeniedException,
			RepositoryException, DatabaseException {
		log.debug("updateSearch({}, {})", token, params);
		SearchModule sm = ModuleManager.getSearchModule();
		sm.saveSearch(token, params);
		log.debug("updateSearch: void");
	}

	@Override
	public QueryParams getSearch(String token, int qpId) throws PathNotFoundException, RepositoryException,
			DatabaseException {
		log.debug("getSearch({}, {})", token, qpId);
		SearchModule sm = ModuleManager.getSearchModule();
		QueryParams qp = sm.getSearch(token, qpId);
		log.debug("getSearch: {}", qp);
		return qp;
	}

	@Override
	public List<QueryParams> getAllSearchs(String token) throws RepositoryException, DatabaseException {
		log.debug("getAllSearchs({})", token);
		SearchModule sm = ModuleManager.getSearchModule();
		List<QueryParams> col = sm.getAllSearchs(token);
		log.debug("getAllSearchs: {}", col);
		return col;
	}

	@Override
	public void deleteSearch(String token, int qpId) throws AccessDeniedException, RepositoryException,
			DatabaseException {
		log.debug("deleteSearch({}, {})", token, qpId);
		SearchModule sm = ModuleManager.getSearchModule();
		sm.deleteSearch(token, qpId);
		log.debug("deleteSearch: void");
	}

	@Override
	public Map<String, Integer> getKeywordMap(String token, List<String> filter) throws RepositoryException,
			DatabaseException {
		log.debug("getKeywordMap({})", token);
		SearchModule sm = ModuleManager.getSearchModule();
		Map<String, Integer> kmap = sm.getKeywordMap(token, filter);
		log.debug("getKeywordMap: {}", kmap);
		return kmap;
	}

	@Override
	public List<Document> getCategorizedDocuments(String token, String categoryId) throws
			RepositoryException, DatabaseException {
		log.debug("getCategorizedDocuments({})", token);
		SearchModule sm = ModuleManager.getSearchModule();
		List<Document> col = sm.getCategorizedDocuments(token, categoryId);
		log.debug("getCategorizedDocuments: {}", col);
		return col;
	}
}
