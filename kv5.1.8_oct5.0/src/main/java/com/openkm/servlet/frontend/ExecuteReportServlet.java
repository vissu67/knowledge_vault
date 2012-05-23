package com.openkm.servlet.frontend;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bsh.EvalError;

import com.openkm.bean.form.FormElement;
import com.openkm.core.DatabaseException;
import com.openkm.core.ParseException;
import com.openkm.dao.ReportDAO;
import com.openkm.dao.UserConfigDAO;
import com.openkm.dao.bean.Profile;
import com.openkm.dao.bean.Report;
import com.openkm.dao.bean.UserConfig;
import com.openkm.jcr.JCRUtils;
import com.openkm.util.ReportUtils;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;

/**
 * Execute report for users
 * 
 * @pavila
 */
public class ExecuteReportServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(ExecuteReportServlet.class);
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("doGet({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		int id = WebUtils.getInt(request, "id");
		int format = WebUtils.getInt(request, "format", ReportUtils.OUTPUT_PDF);
		ByteArrayOutputStream baos = null;
		ByteArrayInputStream bais = null;
		Session session = null;
		
		try {
			session = JCRUtils.getSession();
			UserConfig uc = UserConfigDAO.findByPk(session, request.getRemoteUser());
			Profile up = uc.getProfile();
			
			if (up.getMisc().getReports().contains(id)) {
				Report rp = ReportDAO.findByPk(id);
				
				// Set file name
				String fileName = rp.getFileName().substring(0, rp.getFileName().indexOf('.')) + ReportUtils.FILE_EXTENSION[format];
				
				// Set default report parameters
				Map<String, String> params = new HashMap<String, String>();
				String host = com.openkm.core.Config.APPLICATION_URL;
				params.put("host", host.substring(0, host.lastIndexOf("/") + 1));
				
				for (FormElement fe : ReportUtils.getReportParameters(id)) {
					params.put(fe.getName(), WebUtils.getString(request, fe.getName()));
				}
				
				baos = ReportUtils.execute(rp, params, format);
				bais = new ByteArrayInputStream(baos.toByteArray());
				WebUtils.sendFile(request, response, fileName, ReportUtils.FILE_MIME[format], false, bais);
				
				// Activity log
				UserActivity.log(session.getUserID(), "EXECUTE_REPORT", Integer.toString(id), "OK");
			} else {
				// Activity log
				UserActivity.log(session.getUserID(), "EXECUTE_REPORT", Integer.toString(id), "FAILURE");
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} catch (JRException e) {
			log.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} catch (EvalError e) {
			log.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} catch (LoginException e) {
			log.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} finally {
			JCRUtils.logout(session);
			IOUtils.closeQuietly(bais);
			IOUtils.closeQuietly(baos);
		}
	}
}
