package com.openkm.servlet.admin;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.HttpSessionManager;
import com.openkm.util.UserActivity;

public class BaseServlet extends HttpServlet  {
	private static final long serialVersionUID = 1L;
	protected static final String METHOD_GET = "GET";
	protected static final String METHOD_POST = "POST";
    
	/**
	 * Dispatch errors 
	 */
	protected void sendErrorRedirect(HttpServletRequest request, HttpServletResponse response,
			Throwable e) throws ServletException, IOException {
		request.setAttribute ("javax.servlet.jsp.jspException", e);
		ServletContext sc = getServletConfig().getServletContext();
		sc.getRequestDispatcher("/error.jsp").forward(request, response);
	}
	
	/**
	 * Update HTTP active sessions
	 */
	public void updateSessionManager(HttpServletRequest request) {
		HttpSessionManager.getInstance().update(request.getSession().getId());
	}
	
	/**
	 * Test if an user can access to administration
	 */
	public static boolean isAdmin(HttpServletRequest request) {
		return request.isUserInRole(Config.DEFAULT_ADMIN_ROLE);
	}

	
	/**
	 * Test if an user can access to Group administration
	 * Added by vissu on oct 24th
	 */
	public static boolean isGroupAdmin(HttpServletRequest request) {
		return request.isUserInRole(Config.DEFAULT_GROUP_ADMIN_ROLE);
	}

	
	/**
	 * Test if an user can access to administration when configured as SaaS: An user can
	 * access if:
	 * 
	 * - Multiple Instances is active AND user id okmAdmin
	 * - Multiple Instances is inactive AND user has AdminRole role
	 */
	public static boolean isMultipleInstancesAdmin(HttpServletRequest request) {
		return Config.SYSTEM_MULTIPLE_INSTANCES && request.getRemoteUser().equals(Config.ADMIN_USER) ||
			!Config.SYSTEM_MULTIPLE_INSTANCES && request.isUserInRole(Config.DEFAULT_ADMIN_ROLE);
	}
	
	/**
	 * Check for forbidden access 
	 */
	public boolean checkMultipleInstancesAccess(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (!isMultipleInstancesAdmin(request)) {
			// Activity log
			UserActivity.log(request.getRemoteUser(), "ADMIN_ACCESS_DENIED", request.getRequestURI(), request.getQueryString());
			
			AccessDeniedException ade = new AccessDeniedException("You should not access this resource");
			sendErrorRedirect(request, response, ade);
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * Print HTML page header
	 */
	public void header(PrintWriter out, String title) {
		out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
		out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
		out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		out.println("<head>");
		out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
		out.println("<link rel=\"Shortcut icon\" href=\"favicon.ico\" />");
		out.println("<link rel=\"stylesheet\" href=\"css/style.css\" type=\"text/css\" />");
		out.println("<script src=\"js/biblioteca.js\" type=\"text/javascript\"></script>");
		out.println("<script type=\"text/javascript\">scrollToBottom();</script>");
		out.println("<title>" + title + "</title>");
		out.println("</head>");
		out.println("<body>");
	}
	
	/**
	 * Print HTML page footer
	 */
	public void footer(PrintWriter out) {
		out.println("</body>");
		out.println("</html>");
	}
}
