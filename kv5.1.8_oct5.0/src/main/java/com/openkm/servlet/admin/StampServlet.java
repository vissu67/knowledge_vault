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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.jcr.LoginException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bsh.EvalError;

import com.lowagie.text.DocumentException;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.dao.AuthDAO;
import com.openkm.extension.dao.StampImageDAO;
import com.openkm.extension.dao.StampTextDAO;
import com.openkm.extension.dao.bean.StampImage;
import com.openkm.extension.dao.bean.StampText;
import com.openkm.jcr.JCRUtils;
import com.openkm.principal.PrincipalAdapterException;
import com.openkm.util.PDFUtils;
import com.openkm.util.SecureStore;
import com.openkm.util.UserActivity;
import com.openkm.util.WebUtils;

/**
 * Stamp servlet
 */
public class StampServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(StampServlet.class);
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("doGet({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		Session session = null;
		updateSessionManager(request);
		
		try {
			session = JCRUtils.getSession();
			
			if (action.equals("textCreate")) {
				textCreate(session, request, response);
			} else if (action.equals("imageCreate")) {
				imageCreate(session, request, response);
			} else if (action.equals("textEdit")) {
				textEdit(session, request, response);
			} else if (action.equals("imageEdit")) {
				imageEdit(session, request, response);
			} else if (action.equals("textDelete")) {
				textDelete(session, request, response);
			} else if (action.equals("textColor")) {
				textColor(session, request, response);
			} else if (action.equals("textTest")) {
				textTest(session, request, response);
			} else if (action.equals("imageDelete")) {
				imageDelete(session, request, response);
			} else if (action.equals("textActive")) {
				textActive(session, request, response);
			} else if (action.equals("imageActive")) {
				imageActive(session, request, response);
			} else if (action.equals("imageView")) {
				imageView(session, request, response);
			} else if (action.equals("imageTest")) {
				imageTest(session, request, response);
			}
			
			if (action.equals("") || action.equals("textList") || action.equals("textActive") ||
					(action.startsWith("text") && WebUtils.getBoolean(request, "persist"))) {
				textList(session, request, response);
			} else if (action.equals("imageList") || action.equals("imageActive")) {
				imageList(session, request, response);
			}
		} catch (LoginException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (NoSuchAlgorithmException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (PrincipalAdapterException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (DocumentException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (EvalError e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} finally {
			JCRUtils.logout(session);
		}
	}

	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException,
			ServletException {
		log.debug("doPost({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String action = WebUtils.getString(request, "action");
		Session session = null;
		updateSessionManager(request);
		
		try {
			if (ServletFileUpload.isMultipartContent(request)) {
				session = JCRUtils.getSession();
				InputStream is = null;
				FileItemFactory factory = new DiskFileItemFactory(); 
				ServletFileUpload upload = new ServletFileUpload(factory);
				List<FileItem> items = upload.parseRequest(request);
				StampImage si = new StampImage();
				
				for (Iterator<FileItem> it = items.iterator(); it.hasNext();) {
					FileItem item = it.next();
					
					if (item.isFormField()) {
						if (item.getFieldName().equals("action")) {
							action = item.getString("UTF-8");
						} else if (item.getFieldName().equals("si_id")) {
							si.setId(Integer.parseInt(item.getString("UTF-8")));
						} else if (item.getFieldName().equals("si_name")) {
							si.setName(item.getString("UTF-8"));
						} else if (item.getFieldName().equals("si_description")) {
							si.setDescription(item.getString("UTF-8"));
						} else if (item.getFieldName().equals("si_layer")) {
							si.setLayer(Integer.parseInt(item.getString("UTF-8")));
						} else if (item.getFieldName().equals("si_opacity")) {
							si.setOpacity(Float.parseFloat(item.getString("UTF-8")));
						} else if (item.getFieldName().equals("si_expr_x")) {
							si.setExprX(item.getString("UTF-8"));
						} else if (item.getFieldName().equals("si_expr_y")) {
							si.setExprY(item.getString("UTF-8"));
						} else if (item.getFieldName().equals("si_active")) {
							si.setActive(true);
						} else if (item.getFieldName().equals("si_users")) {
							si.getUsers().add(item.getString("UTF-8"));
						}
					} else {
						is = item.getInputStream();
						si.setImageMime(Config.mimeTypes.getContentType(item.getName()));
						si.setImageContent(SecureStore.b64Encode(IOUtils.toByteArray(is)));
						is.close();
					}
				}
			
				if (action.equals("imageCreate")) {
					int id = StampImageDAO.create(si);
					
					// Activity log
					UserActivity.log(session.getUserID(), "ADMIN_STAMP_IMAGE_CREATE", Integer.toString(id), si.toString());
					imageList(session, request, response);
				} else if (action.equals("imageEdit")) {
					StampImageDAO.update(si);
					
					// Activity log
					UserActivity.log(session.getUserID(), "ADMIN_STAMP_IMAGE_EDIT", Integer.toString(si.getId()), si.toString());
					imageList(session, request, response);
				} else if (action.equals("imageDelete")) {
					StampImageDAO.delete(si.getId());
					
					// Activity log
					UserActivity.log(session.getUserID(), "ADMIN_STAMP_IMAGE_DELETE", Integer.toString(si.getId()), null);
					imageList(session, request, response);
				}
			}
		} catch (LoginException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} catch (FileUploadException e) {
			log.error(e.getMessage(), e);
			sendErrorRedirect(request,response, e);
		} finally {
			JCRUtils.logout(session);
		}
	}
	
	/**
	 * New text stamp
	 */
	private void textCreate(Session session, HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException, DatabaseException {
		log.debug("textCreate({}, {}, {})", new Object[] { session, request, response });
		
		if (WebUtils.getBoolean(request, "persist")) {
			StampText st = new StampText();
			st.setName(WebUtils.getString(request, "st_name"));
			st.setDescription(WebUtils.getString(request, "st_description"));
			st.setText(WebUtils.getString(request, "st_text"));
			st.setLayer(WebUtils.getInt(request, "st_layer"));
			st.setOpacity(WebUtils.getFloat(request, "st_opacity"));
			st.setSize(WebUtils.getInt(request, "st_size"));
			st.setColor(WebUtils.getString(request, "st_color"));
			st.setAlign(WebUtils.getInt(request, "st_align"));
			st.setRotation(WebUtils.getInt(request, "st_rotation"));
			st.setExprX(WebUtils.getString(request, "st_expr_x"));
			st.setExprY(WebUtils.getString(request, "st_expr_y"));
			st.setActive(WebUtils.getBoolean(request, "st_active"));
			st.setUsers(new HashSet<String>(WebUtils.getStringList(request, "st_users")));
			
			int id = StampTextDAO.create(st);
			
			// Activity log
			UserActivity.log(session.getUserID(), "ADMIN_STAMP_TEXT_CREATE", Integer.toString(id), st.toString());
		} else {
			ServletContext sc = getServletContext();
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("users", AuthDAO.findAllUsers(true));
			sc.setAttribute("stamp", new StampText());
			sc.getRequestDispatcher("/admin/stamp_text_edit.jsp").forward(request, response);
		}
		
		log.debug("textCreate: void");
	}
	
	/**
	 * Edit text stamp
	 */
	private void textEdit(Session session, HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException, DatabaseException, NoSuchAlgorithmException {
		log.debug("textEdit({}, {}, {})", new Object[] { session, request, response });
		
		if (WebUtils.getBoolean(request, "persist")) {
			StampText st = new StampText();
			st.setId(WebUtils.getInt(request, "st_id"));
			st.setName(WebUtils.getString(request, "st_name"));
			st.setDescription(WebUtils.getString(request, "st_description"));
			st.setText(WebUtils.getString(request, "st_text"));
			st.setLayer(WebUtils.getInt(request, "st_layer"));
			st.setOpacity(WebUtils.getFloat(request, "st_opacity"));
			st.setSize(WebUtils.getInt(request, "st_size"));
			st.setColor(WebUtils.getString(request, "st_color"));
			st.setAlign(WebUtils.getInt(request, "st_align"));
			st.setRotation(WebUtils.getInt(request, "st_rotation"));
			st.setExprX(WebUtils.getString(request, "st_expr_x"));
			st.setExprY(WebUtils.getString(request, "st_expr_y"));
			st.setActive(WebUtils.getBoolean(request, "st_active"));
			st.setUsers(new HashSet<String>(WebUtils.getStringList(request, "st_users")));
			
			StampTextDAO.update(st);
			
			// Activity log
			UserActivity.log(session.getUserID(), "ADMIN_STAMP_TEXT_EDIT", Integer.toString(st.getId()), st.toString());
		} else {
			ServletContext sc = getServletContext();
			int stId = WebUtils.getInt(request, "st_id");
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("users", AuthDAO.findAllUsers(true));
			sc.setAttribute("stamp", StampTextDAO.findByPk(stId));
			sc.getRequestDispatcher("/admin/stamp_text_edit.jsp").forward(request, response);
		}
		
		log.debug("textEdit: void");
	}
	
	/**
	 * Delete text stamp
	 */
	private void textDelete(Session session, HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException, DatabaseException, NoSuchAlgorithmException {
		log.debug("textDelete({}, {}, {})", new Object[] { session, request, response });
		
		if (WebUtils.getBoolean(request, "persist")) {
			int stId = WebUtils.getInt(request, "st_id");
			StampTextDAO.delete(stId);
			
			// Activity log
			UserActivity.log(session.getUserID(), "ADMIN_STAMP_TEXT_DELETE", Integer.toString(stId), null);
		} else {
			ServletContext sc = getServletContext();
			int stId = WebUtils.getInt(request, "st_id");
			sc.setAttribute("action", WebUtils.getString(request, "action"));
			sc.setAttribute("persist", true);
			sc.setAttribute("users", AuthDAO.findAllUsers(true));
			sc.setAttribute("stamp", StampTextDAO.findByPk(stId));
			sc.getRequestDispatcher("/admin/stamp_text_edit.jsp").forward(request, response);
		}
		
		log.debug("textDelete: void");
	}
	
	/**
	 * Active text stamp
	 */
	private void textActive(Session session, HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException, DatabaseException, NoSuchAlgorithmException {
		log.debug("textActive({}, {}, {})", new Object[] { session, request, response });
		int stId = WebUtils.getInt(request, "st_id");
		boolean active = WebUtils.getBoolean(request, "st_active");
		StampTextDAO.active(stId, active);
		
		// Activity log
		UserActivity.log(session.getUserID(), "ADMIN_STAMP_TEXT_ACTIVE", Integer.toString(stId), Boolean.toString(active));
		log.debug("textActive: void");
	}

	/**
	 * List text stamp
	 */
	private void textList(Session session, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException, PrincipalAdapterException {
		log.debug("textList({}, {}, {})", new Object[] { session, request, response });
		ServletContext sc = getServletContext();
		sc.setAttribute("stamps", StampTextDAO.findAll());
		sc.getRequestDispatcher("/admin/stamp_text_list.jsp").forward(request, response);
		log.debug("textList: void");
	}
	
	/**
	 * View text color
	 */
	private void textColor(Session session, HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException, DatabaseException {
		log.debug("textColor({}, {}, {})", new Object[] { session, request, response });
		int stId = WebUtils.getInt(request, "st_id");
		StampText st = StampTextDAO.findByPk(stId);
		BufferedImage bi = new BufferedImage(16, 16, BufferedImage.TYPE_INT_RGB);
		Graphics g = bi.getGraphics();
		g.setColor(Color.decode(st.getColor()));
		g.fillRect(0, 0, 16, 16);
		response.setContentType("image/jpeg");
		ImageIO.write(bi, "jpg", response.getOutputStream());
		log.debug("textColor: void");
	}
	
	/**
	 * Test text stamp
	 */
	private void textTest(Session session, HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException, DatabaseException, DocumentException, EvalError {
		log.debug("textTest({}, {}, {})", new Object[] { session, request, response });
		int stId = WebUtils.getInt(request, "st_id");
		StampText st = StampTextDAO.findByPk(stId);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PDFUtils.generateSample(5, baos);
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		baos = new ByteArrayOutputStream();
		PDFUtils.stampText(bais, st.getText(), st.getLayer(), st.getOpacity(), st.getSize(),
				Color.decode(st.getColor()), st.getRotation(), st.getAlign(), st.getExprX(), st.getExprY(),
				baos);
		bais = new ByteArrayInputStream(baos.toByteArray());
		WebUtils.sendFile(request, response, "sample.pdf", Config.MIME_PDF, true, bais);
		log.debug("textTest: void");
	}
	
	/**
	 * New image stamp
	 */
	private void imageCreate(Session session, HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException, DatabaseException {
		log.debug("imageCreate({}, {}, {})", new Object[] { session, request, response });
		ServletContext sc = getServletContext();
		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("persist", true);
		sc.setAttribute("users", AuthDAO.findAllUsers(true));
		sc.setAttribute("stamp", new StampImage());
		sc.getRequestDispatcher("/admin/stamp_image_edit.jsp").forward(request, response);
		log.debug("imageCreate: void");
	}
	
	/**
	 * Edit image stamp
	 */
	private void imageEdit(Session session, HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException, DatabaseException, NoSuchAlgorithmException {
		log.debug("imageEdit({}, {}, {})", new Object[] { session, request, response });
		ServletContext sc = getServletContext();
		int siId = WebUtils.getInt(request, "si_id");
		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("persist", true);
		sc.setAttribute("users", AuthDAO.findAllUsers(true));
		sc.setAttribute("stamp", StampImageDAO.findByPk(siId));
		sc.getRequestDispatcher("/admin/stamp_image_edit.jsp").forward(request, response);
		log.debug("imageEdit: void");
	}
	
	/**
	 * Delete image stamp
	 */
	private void imageDelete(Session session, HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException, DatabaseException, NoSuchAlgorithmException {
		log.debug("imageDelete({}, {}, {})", new Object[] { session, request, response });
		ServletContext sc = getServletContext();
		int siId = WebUtils.getInt(request, "si_id");
		sc.setAttribute("action", WebUtils.getString(request, "action"));
		sc.setAttribute("persist", true);
		sc.setAttribute("users", AuthDAO.findAllUsers(true));
		sc.setAttribute("stamp", StampImageDAO.findByPk(siId));
		sc.getRequestDispatcher("/admin/stamp_image_edit.jsp").forward(request, response);
		log.debug("imageDelete: void");
	}
	
	/**
	 * Active image stamp
	 */
	private void imageActive(Session session, HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException, DatabaseException {
		log.debug("imageActive({}, {}, {})", new Object[] { session, request, response });
		int siId = WebUtils.getInt(request, "si_id");
		boolean active = WebUtils.getBoolean(request, "si_active");
		StampImageDAO.active(siId, active);
		
		// Activity log
		UserActivity.log(session.getUserID(), "ADMIN_STAMP_IMAGE_ACTIVE", Integer.toString(siId), Boolean.toString(active));
		log.debug("imageActive: void");
	}

	/**
	 * List image stamp
	 */
	private void imageList(Session session, HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, DatabaseException {
		log.debug("imageList({}, {}, {})", new Object[] { session, request, response });
		ServletContext sc = getServletContext();
		sc.setAttribute("stamps", StampImageDAO.findAll());
		sc.getRequestDispatcher("/admin/stamp_image_list.jsp").forward(request, response);
		log.debug("imageList: void");
	}
	
	/**
	 * View image stamp
	 */
	private void imageView(Session session, HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException, DatabaseException {
		log.debug("imageView({}, {}, {})", new Object[] { session, request, response });
		int siId = WebUtils.getInt(request, "si_id");
		StampImage si = StampImageDAO.findByPk(siId);
		response.setContentType(si.getImageMime());
		ServletOutputStream sos = response.getOutputStream();
		sos.write(SecureStore.b64Decode(si.getImageContent()));
		sos.flush();
		sos.close();
		log.debug("imageView: void");
	}
	
	/**
	 * Test image stamp
	 */
	private void imageTest(Session session, HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException, DatabaseException, DocumentException, EvalError {
		log.debug("imageTest({}, {}, {})", new Object[] { session, request, response });
		int siId = WebUtils.getInt(request, "si_id");
		StampImage si = StampImageDAO.findByPk(siId);
		byte[] image = SecureStore.b64Decode(si.getImageContent());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PDFUtils.generateSample(5, baos);
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		baos = new ByteArrayOutputStream();
		PDFUtils.stampImage(bais, image, si.getLayer(), si.getOpacity(), si.getExprX(), si.getExprY(), baos);
		bais = new ByteArrayInputStream(baos.toByteArray());
		WebUtils.sendFile(request, response, "sample.pdf", Config.MIME_PDF, true, bais);
		log.debug("imageTest: void");
	}
}
