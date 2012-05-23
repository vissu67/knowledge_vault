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

package com.openkm.servlet.admin;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.dao.DatabaseMetadataDAO;
import com.openkm.dao.HibernateUtil;
import com.openkm.dao.LegacyDAO;
import com.openkm.dao.bean.DatabaseMetadataType;
import com.openkm.dao.bean.DatabaseMetadataValue;
import com.openkm.util.DatabaseMetadataUtils;
import com.openkm.util.UserActivity;

/**
 * Database query
 */
public class DatabaseQueryServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(DatabaseQueryServlet.class);
	
	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		String method = request.getMethod();
		
		if (checkMultipleInstancesAccess(request, response)) {
			if (method.equals(METHOD_GET)) {
				doGet(request, response);
			} else if (method.equals(METHOD_POST)) {
				doPost(request, response);
			}
		}
	}
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("doGet({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		updateSessionManager(request);
		ServletContext sc = getServletContext();
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			sc.setAttribute("qs", null);
			//sc.setAttribute("sql", null);
			sc.setAttribute("type", null);
			sc.setAttribute("exception", null);
			sc.setAttribute("globalResults", null);
			sc.setAttribute("tables", listTables(session));
			sc.setAttribute("vtables", listVirtualTables());
			sc.getRequestDispatcher("/admin/database_query.jsp").forward(request, response);
		} catch (Exception e) {
			sendError(sc, request,response, e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("doPost({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		updateSessionManager(request);
		ServletContext sc = getServletContext();
		Session session = null;
		
		try {
			if (ServletFileUpload.isMultipartContent(request)) {
				FileItemFactory factory = new DiskFileItemFactory(); 
				ServletFileUpload upload = new ServletFileUpload(factory);
				List<FileItem> items = upload.parseRequest(request);
				InputStream is = null;
				String type = "";
				String qs = "";
				byte[] data = null;
				
				for (Iterator<FileItem> it = items.iterator(); it.hasNext();) {
					FileItem item = it.next();
					
					if (item.isFormField()) {
						if (item.getFieldName().equals("qs")) {
							qs = item.getString("UTF-8");
						} else if (item.getFieldName().equals("type")) {
							type = item.getString("UTF-8");
						}
					} else {
						is = item.getInputStream();
						data = IOUtils.toByteArray(is);
						is.close();
					}
				}
				
				if (!qs.equals("") && !type.equals("")) {
					session = HibernateUtil.getSessionFactory().openSession();
					sc.setAttribute("qs", qs);
					sc.setAttribute("type", type);
					
					if (type.equals("jdbc")) {
						executeJdbc(session, qs, sc, request, response);
					} else if (type.equals("hibernate")) {
						executeHibernate(session, qs, sc, request, response);
					} else if (type.equals("metadata")) {
						executeMetadata(session, qs, sc, request, response);
					}
					
					// Activity log
					UserActivity.log(request.getRemoteUser(), "ADMIN_DATABASE_QUERY", null, qs);
				} else if (data != null && data.length > 0) {
					sc.setAttribute("exception", null);
					session = HibernateUtil.getSessionFactory().openSession();
					executeUpdate(session, data, sc, request, response);
					
					// Activity log
					UserActivity.log(request.getRemoteUser(), "ADMIN_DATABASE_UPDATE", null, new String(data));
				} else {
					sc.setAttribute("qs", qs);
					sc.setAttribute("type", type);
					sc.setAttribute("exception", null);
					sc.setAttribute("globalResults", new ArrayList<DatabaseQueryServlet.GlobalResult>());
					sc.getRequestDispatcher("/admin/database_query.jsp").forward(request, response);
				}
			}
		} catch (FileUploadException e) {
			sendError(sc, request,response, e);
		} catch (SQLException e) {
			sendError(sc, request,response, e);
		} catch (HibernateException e) {
			sendError(sc, request,response, e);
		} catch (DatabaseException e) {
			sendError(sc, request,response, e);
		} catch (IllegalAccessException e) {
			sendError(sc, request,response, e);
		} catch (InvocationTargetException e) {
			sendError(sc, request,response, e);
		} catch (NoSuchMethodException e) {
			sendError(sc, request,response, e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	/**
	 * Execute metadata query
	 */
	private void executeMetadata(Session session, String qs, ServletContext sc, HttpServletRequest request,
			HttpServletResponse response) throws DatabaseException, ServletException, IOException,
			HibernateException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		StringTokenizer st = new StringTokenizer(qs, "\n\r");
		List<GlobalResult> globalResults = new ArrayList<DatabaseQueryServlet.GlobalResult>();
		
		// For each query line
		while (st.hasMoreTokens()) {
			String mds = st.nextToken();
			String[] parts = mds.split("\\|");
			
			if (parts.length > 1) {
				String hql = null;
				
				if (parts[0].toUpperCase().equals("SENTENCE")) {
					if (parts.length > 2) {
						List<String> tables = Arrays.asList(parts[1].split(","));
						hql = DatabaseMetadataUtils.replaceVirtual(tables, parts[2]);
						log.debug("Metadata SENTENCE: {}", hql);
						globalResults.add(executeHQL(session, hql, tables));
					}
				} else if (parts[0].toUpperCase().equals("SELECT")) {	
					if (parts.length > 2) {
						hql = DatabaseMetadataUtils.buildQuery(parts[1], parts[2]);
					} else {
						// Filter parameter is optional
						hql = DatabaseMetadataUtils.buildQuery(parts[1], null);
					}
					
					log.debug("Metadata SELECT: {}", hql);
					globalResults.add(executeHQL(session, hql, Arrays.asList(parts[1])));
				} else if (parts[0].toUpperCase().equals("UPDATE")) {
					if (parts.length > 3) {
						hql = DatabaseMetadataUtils.buildUpdate(parts[1], parts[2], parts[3]);
					} else if (parts.length > 2) {
						// Filter parameter is optional
						hql = DatabaseMetadataUtils.buildUpdate(parts[1], parts[2], null);
					} else {
						// Filter parameter is optional
						hql = DatabaseMetadataUtils.buildUpdate(parts[1], null, null);
					}
					
					log.debug("Metadata UPDATE: {}", hql);
					globalResults.add(executeHQL(session, hql, Arrays.asList(parts[1])));
				} else if (parts[0].toUpperCase().equals("DELETE")) {
					if (parts.length > 2) {
						hql = DatabaseMetadataUtils.buildDelete(parts[1], parts[2]);
					} else {
						// Filter parameter is optional
						hql = DatabaseMetadataUtils.buildDelete(parts[1], null);
					}
					
					log.debug("Metadata DELETE: {}", hql);
					globalResults.add(executeHQL(session, hql, Arrays.asList(parts[1])));
				} else {
					throw new DatabaseException("Error in metadata action");
				}
			} else {
				throw new DatabaseException("Error in metadata sentence parameters");
			}
		}
		
		//sc.setAttribute("sql", HibernateUtil.toSql(qs));
		sc.setAttribute("exception", null);
		sc.setAttribute("globalResults", globalResults);
		sc.getRequestDispatcher("/admin/database_query.jsp").forward(request, response);
	}

	/**
	 * Execute Hibernate query
	 */
	private void executeHibernate(Session session, String qs, ServletContext sc, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException, HibernateException,
			DatabaseException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		StringTokenizer st = new StringTokenizer(qs, "\n\r");
		List<GlobalResult> globalResults = new ArrayList<DatabaseQueryServlet.GlobalResult>();
		
		// For each query line
		while (st.hasMoreTokens()) {
			String tk = st.nextToken().trim();
			
			if (tk.toUpperCase().startsWith("--") || tk.equals("") || tk.equals("\r")) {
				// Is a comment, so ignore it
			} else {
				globalResults.add(executeHQL(session, tk, null));
			}
		}
		
		//sc.setAttribute("sql", HibernateUtil.toSql(qs));
		sc.setAttribute("exception", null);
		sc.setAttribute("globalResults", globalResults);
		sc.getRequestDispatcher("/admin/database_query.jsp").forward(request, response);
	}
	
	/**
	 * Execute hibernate sentence
	 */
	@SuppressWarnings("unchecked")
	private GlobalResult executeHQL(Session session, String hql, List<String> vtables) throws HibernateException, 
			DatabaseException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if (hql.toUpperCase().startsWith("SELECT") || hql.toUpperCase().startsWith("FROM")) {
			Query q = session.createQuery(hql);
			List<Object> ret = q.list();
			List<String> columns = new ArrayList<String>();
			List<String> vcolumns = new ArrayList<String>();
			List<List<String>> results = new ArrayList<List<String>>();
			Type[] rt = q.getReturnTypes();
			int i = 0;
			
			if (vtables == null) {
				for (i=0; i<rt.length; i++) {
					columns.add(rt[i].getName());
				}
			} else {
				for (String vtable : vtables) {
					String query = "select dmt.virtualColumn, dmt.realColumn from DatabaseMetadataType dmt where dmt.table='"+vtable+"'";
					List<Object> tmp = LegacyDAO.executeQuery(query);
					
					for (Object obj : tmp) {
						Object[] dt = (Object[]) obj;
						vcolumns.add(String.valueOf(dt[0]));
						columns.add(String.valueOf(dt[0]).concat(" (").concat(String.valueOf(dt[1])).concat(")"));
					}
				}
			}
			
			for (Iterator<Object> it = ret.iterator(); it.hasNext() && i++ < Config.MAX_SEARCH_RESULTS; ) {
				List<String> row = new ArrayList<String>();
				Object obj = it.next();
				
				if (vtables == null) {
					if (obj instanceof Object[]) {
						Object[] ao = (Object[]) obj;
						
						for (int j=0; j<ao.length; j++) {
							row.add(String.valueOf(ao[j]));
						}
					} else {
						row.add(String.valueOf(obj));
					}
				} else {
					if (obj instanceof DatabaseMetadataValue) {
						for (String column : vcolumns) {
							row.add(DatabaseMetadataUtils.getString((DatabaseMetadataValue) obj, column));
						}
					} else if (obj instanceof Object[]) {
						for (Object objChild : (Object[]) obj) {
							if (objChild instanceof DatabaseMetadataValue) {
								DatabaseMetadataValue dmvChild = (DatabaseMetadataValue) objChild;
								List<DatabaseMetadataType> types = DatabaseMetadataDAO.findAllTypes(dmvChild.getTable());
								
								for (DatabaseMetadataType emt : types) {
									for (String column : vcolumns) {
										if (emt.getVirtualColumn().equals(column)) {
											row.add(BeanUtils.getProperty(dmvChild, emt.getRealColumn()));
										}
									}
								}
							} else {
								row.add(String.valueOf(objChild));
							}
						}
					} else {
						row.add("Query result should be instance of DatabaseMetadataValue");
					}
				}
				
				results.add(row);
			}
			
			GlobalResult gr = new GlobalResult();
			gr.setColumns(columns);
			gr.setResults(results);
			gr.setRows(null);
			gr.setSql(hql);
			return gr;
		} else {
			GlobalResult gr = new GlobalResult();
			int rows = session.createQuery(hql).executeUpdate();
			gr.setColumns(null);
			gr.setResults(null);
			gr.setRows(rows);
			gr.setSql(hql);
			return gr;
		}
	}
	
	/**
	 * Execute JDBC query
	 */
	private void executeJdbc(Session session, String qs, ServletContext sc, HttpServletRequest request, 
			HttpServletResponse response) throws SQLException, ServletException, IOException {
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		List<GlobalResult> globalResults = new ArrayList<DatabaseQueryServlet.GlobalResult>();
		
		try {
			con = session.connection();
			stmt = con.createStatement();
			StringTokenizer st = new StringTokenizer(qs, "\n\r");
			
			// For each query line
			while (st.hasMoreTokens()) {
				String tk = st.nextToken().trim();
				
				if (tk.toUpperCase().startsWith("--") || tk.equals("") || tk.equals("\r")) {
					// Is a comment, so ignore it
				} else {
					if (tk.endsWith(";")) {
						tk = tk.substring(0, tk.length() - 1);
					}
					
					if (tk.toUpperCase().startsWith("SELECT")) {
						rs = stmt.executeQuery(tk);
						ResultSetMetaData md = rs.getMetaData();
						List<String> columns = new ArrayList<String>();
						List<List<String>> results = new ArrayList<List<String>>();
						
						for (int i=1; i<md.getColumnCount()+1; i++) {
							columns.add(md.getColumnName(i));
						}
						
						for (int i=0; rs.next() && i++ < Config.MAX_SEARCH_RESULTS; ) {
							List<String> row = new ArrayList<String>();
							
							for (int j=1; j<md.getColumnCount()+1; j++) {
								row.add(rs.getString(j));
							}
							
							results.add(row);
						}
						
						GlobalResult gr = new GlobalResult();
						gr.setColumns(columns);
						gr.setResults(results);
						gr.setRows(null);
						gr.setSql(tk);
						globalResults.add(gr);
					} else {
						GlobalResult gr = new GlobalResult();
						int rows = stmt.executeUpdate(tk);
						gr.setColumns(null);
						gr.setResults(null);
						gr.setRows(rows);
						gr.setSql(tk);
						globalResults.add(gr);
					}
				}
			}
		} finally {
			LegacyDAO.close(rs);
			LegacyDAO.close(stmt);
			LegacyDAO.close(con);
		}
		
		//sc.setAttribute("sql", null);
		sc.setAttribute("exception", null);
		sc.setAttribute("globalResults", globalResults);
		sc.getRequestDispatcher("/admin/database_query.jsp").forward(request, response);
	}
	
	/**
	 * Import into database
	 */
	private void executeUpdate(Session session, byte[] data, ServletContext sc, HttpServletRequest request, 
			HttpServletResponse response) throws SQLException, ServletException, IOException {
		log.debug("executeUpdate({}, {}, {})", new Object[] { session, request, response });
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		List<HashMap<String, String>> errors = new ArrayList<HashMap<String, String>>();
		List<GlobalResult> globalResults = new ArrayList<DatabaseQueryServlet.GlobalResult>();
		int rows = 0;
		
		try {
			con = session.connection();
			stmt = con.createStatement();
			InputStreamReader is = new InputStreamReader(new ByteArrayInputStream(data));
			BufferedReader br = new BufferedReader(is);
			String sql;
			int ln = 0;
			
			while ((sql = br.readLine()) != null) {
				String trimmedSql = sql.trim();
				ln++;
				
				if (trimmedSql.length() > 0 && !trimmedSql.startsWith("--")) {
					try {
						if (trimmedSql.endsWith(";")) {
							trimmedSql = trimmedSql.substring(0, trimmedSql.length() - 1);
						}
						
						rows += stmt.executeUpdate(trimmedSql);
					} catch (SQLException e) {
						HashMap<String, String> error = new HashMap<String, String>();
						error.put("ln", Integer.toString(ln));
						error.put("sql", trimmedSql);
						error.put("msg", e.getMessage());
						errors.add(error);
					}
				}
			}
		} finally {
			LegacyDAO.close(rs);
			LegacyDAO.close(stmt);
			LegacyDAO.close(con);
		}
		
		GlobalResult gr = new GlobalResult();
		gr.setColumns(null);
		gr.setResults(null);
		gr.setRows(rows);
		gr.setErrors(errors);
		globalResults.add(gr);
		
		sc.setAttribute("qs", null);
		sc.setAttribute("type", null);
		sc.setAttribute("globalResults", globalResults);
		sc.getRequestDispatcher("/admin/database_query.jsp").forward(request, response);
		
		log.debug("executeUpdate: void");
	}
	
	/**
	 * List tables from database
	 */
	private List<String> listTables(Session session) {
		final List<String> tables = new ArrayList<String>();
		final String[] tableTypes = { "TABLE" };
		final String[] tablePatterns = new String[] {
				"JBPM_%", "OKM_%", "DEFAULT_%", "VERSION_%",
				"jbpm_%", "okm_%", "default_%", "version_%" };
		
		session.doWork(
			new Work() {
				@Override
				public void execute(Connection con) throws SQLException {
					DatabaseMetaData md = con.getMetaData();
					
					for (String table : tablePatterns) {
						ResultSet rs = md.getTables(null, null, table, tableTypes);
						
						while (rs.next()) {
							tables.add(rs.getString(3));
						}
						
						rs.close();
					}
				}
			}
		);
		
		return tables;
	}
	
	/**
	 * List virtual tables from database
	 */
	private List<String> listVirtualTables() throws DatabaseException {
		String query = "select distinct(dmv.table) from DatabaseMetadataType dmv order by dmv.table";
		List<Object> tmp = LegacyDAO.executeQuery(query);
		List<String> tables = new ArrayList<String>();
		
		for (Object obj : tmp) {
			tables.add(String.valueOf(obj));
		}
		
		return tables;
	}
	
	/**
	 * Send error to be displayed inline
	 */
	protected void sendError(ServletContext sc, HttpServletRequest request, HttpServletResponse response, 
			Exception e) throws ServletException, IOException {
		sc.setAttribute("exception", e);
		sc.setAttribute("globalResults", null);
		sc.getRequestDispatcher("/admin/database_query.jsp").forward(request, response);
	}
	
	/**
	 * Container helper class
	 */
	public class GlobalResult {
		private List<HashMap<String, String>> errors = new ArrayList<HashMap<String, String>>();
		private List<List<String>> results = new ArrayList<List<String>>();
		private List<String> columns = new ArrayList<String>();
		private Integer rows = new Integer(0);
		private String sql = new String();
		
		public List<String> getColumns() {
			return columns;
		}
		
		public void setColumns(List<String> columns) {
			this.columns = columns;
		}
		
		public List<List<String>> getResults() {
			return results;
		}
		
		public void setResults(List<List<String>> results) {
			this.results = results;
		}
		
		public Integer getRows() {
			return rows;
		}
		
		public void setRows(Integer rows) {
			this.rows = rows;
		}

		public String getSql() {
			return sql;
		}

		public void setSql(String sql) {
			this.sql = sql;
		}

		public List<HashMap<String, String>> getErrors() {
			return errors;
		}

		public void setErrors(List<HashMap<String, String>> errors) {
			this.errors = errors;
		}
	}
}
