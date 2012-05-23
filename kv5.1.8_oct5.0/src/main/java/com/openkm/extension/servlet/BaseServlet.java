package com.openkm.extension.servlet;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.openkm.core.HttpSessionManager;

public class BaseServlet extends HttpServlet  {
	private static final long serialVersionUID = 1L;

	/**
	 * Dispatch errors 
	 */
	protected void sendErrorRedirect(HttpServletRequest request, HttpServletResponse response,
			Throwable e) throws ServletException, IOException {
		request.setAttribute ("javax.servlet.jsp.jspException", e);
		ServletContext sc = getServletConfig().getServletContext();
		sc.getRequestDispatcher("/error.jsp").forward(request, response);
	}
	
	public void updateSessionManager(HttpServletRequest request) {
		HttpSessionManager.getInstance().update(request.getSession().getId());
	}
}
