package com.openkm.servlet;

import javax.jcr.Credentials;
import javax.jcr.LoginException;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.jackrabbit.server.BasicCredentialsProvider;
import org.apache.jackrabbit.server.CredentialsProvider;

import com.openkm.module.direct.DirectRepositoryModule;

public class BasicSecuredServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private CredentialsProvider cp = new BasicCredentialsProvider(null);
	
	/**
	 * Get JCR session
	 */
	public synchronized Session getSession(HttpServletRequest request) throws LoginException,
			javax.jcr.RepositoryException, ServletException {
		Credentials creds = cp.getCredentials(request);
		Repository rep = DirectRepositoryModule.getRepository();

		if (creds == null) {
			return rep.login();
		} else {
			return rep.login(creds);
		}
	}
	
	/**
	 * Get JCR session
	 */
	public synchronized Session getSession(String user, String password) throws LoginException,
			javax.jcr.RepositoryException, ServletException {
		Credentials creds = new SimpleCredentials(user, password.toCharArray());
		Repository rep = DirectRepositoryModule.getRepository();
		return rep.login(creds);
	}
}
