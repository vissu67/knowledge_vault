package com.openkm.servlet.mobile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.api.OKMAuth;
import com.openkm.api.OKMDocument;
import com.openkm.api.OKMFolder;
import com.openkm.api.OKMSearch;
import com.openkm.bean.Document;
import com.openkm.bean.Folder;
import com.openkm.bean.Repository;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.ParseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.util.WebUtils;

/**
 * Servlet implementation class HandlerServlet
 */
public class HandlerServlet extends HttpServlet {
	private static Logger log = LoggerFactory.getLogger(HandlerServlet.class);
	private static final long serialVersionUID = 1L;
    
    public HandlerServlet() {
        super();
    }

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response) throws
			ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		log.debug("action: {}", action);
		
		try {
			if (action.equals("") || action.equals("browse")) {
				browse(request, response);
			} else if (action.equals("fldprop")) {
				fldProperties(request, response);
			} else if (action.equals("docprop")) {
				docProperties(request, response);
			} else if (action.equals("search")) {
				search(request, response);
			} else if (action.equals("logout")) {
				logout(request, response);
			}
		} catch (AccessDeniedException e) {
			sendErrorRedirect(request,response, e);
		} catch (PathNotFoundException e) {
			sendErrorRedirect(request,response, e);
		} catch (ParseException e) {
			sendErrorRedirect(request,response, e);
		} catch (RepositoryException e) {
			sendErrorRedirect(request,response, e);
		} catch (DatabaseException e) {
			sendErrorRedirect(request,response, e);
		}
	}
	
	/**
	 * Dispatch errors 
	 */
	private void sendErrorRedirect(HttpServletRequest request, HttpServletResponse response,
			Throwable e) throws ServletException, IOException {
		request.setAttribute ("javax.servlet.jsp.jspException", e);
		ServletContext sc = getServletConfig().getServletContext();
		sc.getRequestDispatcher("/error.jsp").forward(request, response);
	}
	
	/**
	 * List contents
	 */
	private void browse(HttpServletRequest request, HttpServletResponse response) throws 
			PathNotFoundException, RepositoryException, IOException, ServletException, DatabaseException {
		log.debug("browse({}, {})", request, response);
		ServletContext sc = getServletContext();
		String userId = request.getRemoteUser();
		String path = WebUtils.getString(request, "path");
		//added by vissu to show mobile path
		String pathDisplay = null;
		//String parentPathDisplay = null;

		
		if (path == null || path.equals("")) {
			path = "/okm:root";
		}
		
		List<Folder> fldList = OKMFolder.getInstance().getChilds(null, path);
		Collections.sort(fldList, FolderComparator.getInstance());
		List<Document> docList = OKMDocument.getInstance().getChilds(null, path);
		Collections.sort(docList, DocumentComparator.getInstance());
		sc.setAttribute("folderChilds", fldList);
		sc.setAttribute("documentChilds", docList);
		sc.setAttribute("userId", userId);
		sc.setAttribute("path", path);
		
		pathDisplay = path;
		pathDisplay = pathDisplay.replaceAll("okm:root","Knowledge Vault");
		sc.setAttribute("pathDisplay", pathDisplay);
		
	/*	//added by vissu on 1dec
		parentPathDisplay = pathDisplay;
		parentPathDisplay.substring(0,parentPathDisplay.lastIndexOf('/'));
	*/	
		

		sc.getRequestDispatcher("/" + Config.EXPERIMENTAL_MOBILE_CONTEXT + "/browse.jsp").forward(request, response);
	}
	
	/**
	 * Folder properties
	 * @throws AccessDeniedException 
	 */
	private void fldProperties(HttpServletRequest request, HttpServletResponse response) throws
			PathNotFoundException, AccessDeniedException, RepositoryException, IOException,
			ServletException, DatabaseException {
		log.debug("fldProperties({}, {})", request, response);
		ServletContext sc = getServletContext();
		String uuid = WebUtils.getString(request, "uuid", null);
		String path = WebUtils.getString(request, "path");
		//added by vissu 1dec
		String pathDisplay = null;
		String parentPathDisplay = null;
		
		
		if (uuid != null) {
			path = OKMFolder.getInstance().getPath(null, uuid);
		}
		
		if (path == null || path.equals("")) {
			path = "/okm:root";
		}
		
		sc.setAttribute("fld", OKMFolder.getInstance().getProperties(null, path));
		sc.setAttribute("path", path);
		
		pathDisplay = path;
		pathDisplay = pathDisplay.replaceAll("okm:root","Knowledge Vault");
		sc.setAttribute("pathDisplay", pathDisplay);	
		
		//added by vissu on 1dec
		parentPathDisplay = pathDisplay;
		parentPathDisplay.substring(0,parentPathDisplay.lastIndexOf('/'));
		sc.setAttribute("parentPathDisplay", parentPathDisplay);
		
		sc.getRequestDispatcher("/" + Config.EXPERIMENTAL_MOBILE_CONTEXT + "/fld-properties.jsp").forward(request, response);
	}
	
	/**
	 * Document properties
	 * @throws AccessDeniedException 
	 */
	private void docProperties(HttpServletRequest request, HttpServletResponse response) throws
			PathNotFoundException, AccessDeniedException, RepositoryException, IOException,
			ServletException, DatabaseException {
		log.debug("docProperties({}, {})", request, response);
		ServletContext sc = getServletContext();
		String uuid = WebUtils.getString(request, "uuid", null);
		String path = WebUtils.getString(request, "path");
		//added by vissu on 1dec
		String pathDisplay = null;
		String parentPathDisplay = null;
		
		if (uuid != null) {
			path = OKMDocument.getInstance().getPath(null, uuid);
		}
		
		if (path == null || path.equals("")) {
			path = "/okm:root";
		}
		
		Document doc = OKMDocument.getInstance().getProperties(null, path);
		List<String> categories = new ArrayList<String>();
		
		for (Folder cat : doc.getCategories()) {
			categories.add(cat.getPath().substring(Repository.CATEGORIES.length() + 2)); 
		}
		
		sc.setAttribute("doc", doc);
		sc.setAttribute("path", path);
		sc.setAttribute("categories", categories);
		
		pathDisplay = path;
		pathDisplay = pathDisplay.replaceAll("okm:root","Knowledge Vault");
		sc.setAttribute("pathDisplay", pathDisplay);	
		
		//added by vissu on 1dec
				parentPathDisplay = pathDisplay;
				parentPathDisplay.substring(0,parentPathDisplay.lastIndexOf('/'));
				sc.setAttribute("parentPathDisplay", parentPathDisplay);
				
		sc.getRequestDispatcher("/" + Config.EXPERIMENTAL_MOBILE_CONTEXT + "/doc-properties.jsp").forward(request, response);
	}
	
	
	/**
	 * Search documents
	 */
	private void search(HttpServletRequest request, HttpServletResponse response) throws
			PathNotFoundException, ParseException, RepositoryException, IOException, 
			ServletException, DatabaseException {
		log.debug("search({}, {})", request, response);
		ServletContext sc = getServletContext();
		String query = WebUtils.getString(request, "query");
		
		if (query != null && !query.equals("")) {
			sc.setAttribute("queryResult", OKMSearch.getInstance().findByContent(null, query));	
		}
		
		sc.getRequestDispatcher("/" + Config.EXPERIMENTAL_MOBILE_CONTEXT + "/search.jsp").forward(request, response);
	}
	
	/**
	 * Logout
	 */
	private void logout(HttpServletRequest request, HttpServletResponse response) throws 
			AccessDeniedException, RepositoryException, IOException, DatabaseException {
		log.debug("logout({}, {})", request, response);
		OKMAuth.getInstance().logout(null);
		request.getSession().invalidate();
		response.sendRedirect("index.jsp");
	}
}
