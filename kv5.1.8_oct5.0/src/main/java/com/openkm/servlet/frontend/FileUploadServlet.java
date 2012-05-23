package com.openkm.servlet.frontend;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.api.OKMAuth;
import com.openkm.api.OKMDocument;
import com.openkm.api.OKMFolder;
import com.openkm.api.OKMNotification;
import com.openkm.api.OKMProperty;
import com.openkm.bean.Document;
import com.openkm.bean.Folder;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.core.FileSizeExceededException;
import com.openkm.core.ItemExistsException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.core.UnsupportedMimeTypeException;
import com.openkm.core.VersionException;
import com.openkm.core.VirusDetectedException;
import com.openkm.extension.core.ExtensionException;
import com.openkm.frontend.client.contants.service.ErrorCode;
import com.openkm.frontend.client.contants.ui.UIFileUploadConstants;
import com.openkm.jcr.JCRUtils;
import com.openkm.util.FileUtils;
import com.openkm.util.SecureStore;
import com.openkm.util.impexp.ImpExpStats;
import com.openkm.util.impexp.RepositoryImporter;
import com.openkm.util.impexp.TextInfoDecorator;

import de.schlichtherle.io.File;
import de.schlichtherle.io.FileOutputStream;

/**
 * FileUploadServlet
 * 
 * @author pavila
 */
public class FileUploadServlet extends OKMHttpServlet {
	private static Logger log = LoggerFactory.getLogger(FileUploadServlet.class);
	private static final long serialVersionUID = 1L;
	public static final int INSERT = 0;
	public static final int UPDATE = 1;
	private final String returnOKMessage = "OKM_OK";
	public static final String FILE_UPLOAD_STATUS = "file_upload_status";
	
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws 
			ServletException, IOException {
		log.debug("doPost({}, {})", request, response);
		String fileName = null;
		InputStream is = null;
		String path = null;
		int action = 0;
		boolean notify = false;
		boolean importZip = false;
		String users = null;
		String roles = null;
		String message = null;
		String comment = null;
		String folder = null;
		String cipherName = null;
		String rename = null;
		PrintWriter out = null;
		String uploadedDocPath = null;
		java.io.File tmp = null;
		updateSessionManager(request);
		
		try {
			boolean isMultipart = ServletFileUpload.isMultipartContent(request);
			response.setContentType("text/plain");
			out = response.getWriter();	
			log.debug("isMultipart: {}", isMultipart);
			
			// Create a factory for disk-based file items
			if (isMultipart) {
				FileItemFactory factory = new DiskFileItemFactory(); 
				ServletFileUpload upload = new ServletFileUpload(factory);
				FileUploadListener listener = new FileUploadListener();
				
				// Saving listener to session
				request.getSession().setAttribute(FILE_UPLOAD_STATUS, listener);
				upload.setHeaderEncoding("UTF-8");
				
				// upload servlet allows to set upload listener
		        upload.setProgressListener(listener);
				List<FileItem> items = upload.parseRequest(request);

				// Parse the request and get all parameters and the uploaded file
				for (Iterator<FileItem> it = items.iterator(); it.hasNext();) {
					FileItem item = it.next();
									
					if (item.isFormField()) {
						if (item.getFieldName().equals("path")) { path = item.getString("UTF-8"); }
						if (item.getFieldName().equals("action")) { action = Integer.parseInt(item.getString("UTF-8")); }
						if (item.getFieldName().equals("users")) { users = item.getString("UTF-8"); }
						if (item.getFieldName().equals("roles")) { roles = item.getString("UTF-8"); }
						if (item.getFieldName().equals("notify")) { notify = true; } 
						if (item.getFieldName().equals("importZip")) { importZip = true; }
						if (item.getFieldName().equals("message")) { message = item.getString("UTF-8"); }
						if (item.getFieldName().equals("comment")) { comment = item.getString("UTF-8"); }
						if (item.getFieldName().equals("folder")) { folder = item.getString("UTF-8"); }
						if (item.getFieldName().equals("cipherName")) { cipherName = item.getString("UTF-8"); }
						if (item.getFieldName().equals("rename")) { rename = item.getString("UTF-8"); }
					} else {
						fileName = item.getName();
						is = item.getInputStream();
					}
				}
				
				// Save document with different name than uploading
				if (rename!=null && !rename.equals("")) {
					if (rename.contains(".")) {
						fileName = rename;
					} else {
						// here rename not contains .
						if (fileName.contains(".")) {
							fileName = fileName.substring(fileName.indexOf("."));
							fileName = rename + fileName;
						}
					}
				}

				// Now, we have read all parameters and the uploaded file
				if (action == UIFileUploadConstants.ACTION_INSERT) {
					if (fileName != null && !fileName.equals("")) {
						if (importZip && FilenameUtils.getExtension(fileName).equalsIgnoreCase("zip")) {
							log.info("Import zip file '{}' into '{}'", fileName, path);
							String erroMsg = importZip(path, is);
							
							if (erroMsg == null) {
								out.print(returnOKMessage);
							} else {
								log.warn("erroMsg: {}", erroMsg);
								out.print(erroMsg);
							}
						} else if (importZip && FilenameUtils.getExtension(fileName).equalsIgnoreCase("jar")) {
							log.info("Import jar file '{}' into '{}'", fileName, path);
							String erroMsg = importJar(path, is);
							
							if (erroMsg == null) {
								out.print(returnOKMessage);
							} else {
								log.warn("erroMsg: {}", erroMsg);
								out.print(erroMsg);
							}
						} else {
							fileName = FilenameUtils.getName(fileName);
							log.info("Upload file '{}' into '{}'", fileName, path);
							Document doc = new Document();
							doc.setPath(path + "/" + fileName);
							uploadedDocPath = OKMDocument.getInstance().create(null, doc, is).getPath();
							
							// Case is uploaded a encrypted document
							if (cipherName!=null && !cipherName.equals("")) {
								OKMProperty.getInstance().setEncryption(null, doc.getPath(), cipherName);
							}
							
							// Return the path of the inserted document in response
							out.print(returnOKMessage + " path["+URLEncoder.encode(uploadedDocPath,"UTF-8")+"]path");
						}
					}
				} else if (action == UIFileUploadConstants.ACTION_UPDATE) {
					log.info("File updated: {}", path);
					
					// http://en.wikipedia.org/wiki/Truth_table#Applications => ¬p ∨ q
					if (!Config.SYSTEM_DOCUMENT_NAME_MISMATCH_CHECK || JCRUtils.getName(path).equals(fileName)) {
						OKMDocument document = OKMDocument.getInstance();
						
						//added by vissu feb13
						System.out.println("path= "+path);
						
						Document doc = document.getProperties(null, path);
						document.setContent(null, path, is);
						document.checkin(null, path, comment);
						uploadedDocPath = path;
						
						// Case is uploaded a encrypted document
						if (cipherName != null && !cipherName.equals("")) {
							// Case updated document was not encripted yet
							if (doc.getCipherName() == null) {
								OKMProperty.getInstance().setEncryption(null, path, cipherName);
								// In that case is mandatory compact the history
								document.purgeVersionHistory(null, path);
							}
						} else {
							// Case us uploaded a decrypt document
							if (doc.getCipherName() != null && !doc.getCipherName().equals("")) {
								OKMProperty.getInstance().unsetEncryption(null, path);
								// In that case is mandatory compact the history too
								document.purgeVersionHistory(null, path);
							} 
						}
						
						// Return the path of the inserted document in response
						out.print(returnOKMessage + " path["+URLEncoder.encode(uploadedDocPath,"UTF-8")+"]path");
					} else {
						out.print(ErrorCode.get(ErrorCode.ORIGIN_OKMUploadService, ErrorCode.CAUSE_DocumentNameMismatch));
					}
				} else if (action == UIFileUploadConstants.ACTION_FOLDER) {
					log.info("Folder create: {}", path);
					Folder fld = new Folder();
					fld.setPath(path + "/" + folder);
					OKMFolder.getInstance().create(null, fld);
					out.print(returnOKMessage);
				}
				
				listener.setUploadFinish(true); // Mark uploading operation has finished

				// If the document have been added to the repository, perform user notification
				if ((action == UIFileUploadConstants.ACTION_INSERT || action == UIFileUploadConstants.ACTION_UPDATE) & notify) {
					List<String> userNames = new ArrayList<String>(Arrays.asList(users.split(",")));
					List<String> roleNames = Arrays.asList(roles.split(","));
					
					for (String role : roleNames) {
						List<String> usersInRole = OKMAuth.getInstance().getUsersByRole(null, role);
						
						for (String user : usersInRole) {
							if (!userNames.contains(user)) {
								userNames.add(user);
							}
						}
					}
					
					OKMNotification.getInstance().notify(null, uploadedDocPath, userNames, message, false);
				}
			} else {
				// Used only when document is digital signed ( form in that case is not multiplart it's a normal post )
				action = (request.getParameter("action")!=null?Integer.parseInt(request.getParameter("action")):-1);
				if (action == UIFileUploadConstants.ACTION_DIGITAL_SIGNATURE_INSERT || 
					action == UIFileUploadConstants.ACTION_DIGITAL_SIGNATURE_UPDATE) {
					path = request.getParameter("path");
					String data = request.getParameter("data");
					tmp = java.io.File.createTempFile("okm", ".tmp");
					FileOutputStream fos = new FileOutputStream(tmp);
					BufferedOutputStream bos = new BufferedOutputStream(fos);
					bos.write(SecureStore.b64Decode(data));
					bos.flush();
					bos.close();
					fos.flush();
					fos.close();
					FileInputStream fis = new FileInputStream(tmp);
					switch (action) {
						case UIFileUploadConstants.ACTION_DIGITAL_SIGNATURE_INSERT:
							Document newDoc = new Document();
							path = path.substring(0,path.lastIndexOf(".")+1) + "pdf";
							newDoc.setPath(path);
							OKMDocument.getInstance().create(null, newDoc, fis);
							break;
						
						case UIFileUploadConstants.ACTION_DIGITAL_SIGNATURE_UPDATE:
							OKMDocument.getInstance().checkout(null, path);
							OKMDocument.getInstance().setContent(null, path, fis);
							OKMDocument.getInstance().checkin(null, path, "Signed");
							break;
					}
				}
			}
		} catch (AccessDeniedException e) {
			log.warn(e.getMessage(), e);
			out.print(ErrorCode.get(ErrorCode.ORIGIN_OKMUploadService, ErrorCode.CAUSE_AccessDenied));
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			out.print(ErrorCode.get(ErrorCode.ORIGIN_OKMUploadService, ErrorCode.CAUSE_PathNotFound));
		} catch (ItemExistsException e) {
			log.warn(e.getMessage(), e);
			out.print(ErrorCode.get(ErrorCode.ORIGIN_OKMUploadService, ErrorCode.CAUSE_ItemExists));
		} catch (UnsupportedMimeTypeException e) {
			log.warn(e.getMessage(), e);
			out.print(ErrorCode.get(ErrorCode.ORIGIN_OKMUploadService, ErrorCode.CAUSE_UnsupportedMimeType));
		} catch (FileSizeExceededException e) {
			log.warn(e.getMessage(), e);
			out.print(ErrorCode.get(ErrorCode.ORIGIN_OKMUploadService, ErrorCode.CAUSE_FileSizeExceeded));
		} catch (VirusDetectedException e) {
			log.warn(e.getMessage(), e);
			out.print(VirusDetectedException.class.getSimpleName() + " : "+ e.getMessage());
		} catch (VersionException e) {
			log.error(e.getMessage(), e);
			out.print(ErrorCode.get(ErrorCode.ORIGIN_OKMUploadService, ErrorCode.CAUSE_Version));
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			out.print(ErrorCode.get(ErrorCode.ORIGIN_OKMUploadService, ErrorCode.CAUSE_Repository));
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			out.print(ErrorCode.get(ErrorCode.ORIGIN_OKMUploadService, ErrorCode.CAUSE_Database));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			out.print(ErrorCode.get(ErrorCode.ORIGIN_OKMUploadService, ErrorCode.CAUSE_IO));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			out.print(e.toString());
		} finally {
			if (tmp != null) {
				tmp.delete();
			}
			IOUtils.closeQuietly(is);
			out.flush();
			IOUtils.closeQuietly(out);
			System.gc();
		}
	}
	
	/**
	 * Import zipped documents
	 * 
	 * @param path Where import into the repository.
	 * @param is The zip file to import.
	 */
	private synchronized String importZip(String path, InputStream is) throws PathNotFoundException,
			ItemExistsException, AccessDeniedException, RepositoryException, IOException,
			DatabaseException, ExtensionException {
		log.debug("importZip({}, {})", path, is);
        java.io.File tmpIn = null;
        java.io.File tmpOut = null;
        String errorMsg = null;
       	
		try {
			// Create temporal
			tmpIn = File.createTempFile("okm", ".zip");
	        tmpOut = FileUtils.createTempDir();
	        FileOutputStream fos = new FileOutputStream(tmpIn);
			IOUtils.copy(is, fos);
			fos.close();
			
			// Unzip files
			File fileTmpIn = new File(tmpIn);
			fileTmpIn.archiveCopyAllTo(tmpOut);
			File.umount();
			
			// Import files
			StringWriter out = new StringWriter();
			ImpExpStats stats = RepositoryImporter.importDocuments(null, tmpOut, path, false, out, new TextInfoDecorator(tmpOut));
			if (!stats.isOk()) {
				errorMsg = out.toString();
			}
			out.close();
		} catch (IOException e) {
			log.error("Error importing zip", e);
			throw e;
		} finally {
			if (tmpIn != null) {
				org.apache.commons.io.FileUtils.deleteQuietly(tmpIn);
			}

			if (tmpOut != null) {
				org.apache.commons.io.FileUtils.deleteQuietly(tmpOut);
			}

			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					log.error("Error closing zip input stream", e);
					throw e;
				}
			}
		}
		
		log.debug("importZip: {}", errorMsg);
		return errorMsg;
	}
	
	/**
	 * Import jarred documents
	 * 
	 * @param path Where import into the repository.
	 * @param is The jar file to import.
	 */
	private String importJar(String path, InputStream is) throws PathNotFoundException,
			ItemExistsException, AccessDeniedException, RepositoryException, IOException,
			DatabaseException, ExtensionException {
		log.debug("importJar({}, {})", path, is);
        java.io.File tmpIn = null;
        java.io.File tmpOut = null;
        String errorMsg = null;
       	
		try {
			// Create temporal
			tmpIn = File.createTempFile("okm", ".jar");
	        tmpOut = FileUtils.createTempDir();
	        FileOutputStream fos = new FileOutputStream(tmpIn);
			IOUtils.copy(is, fos);
			fos.close();
			
			// Unzip files
			File fileTmpIn = new File(tmpIn);
			fileTmpIn.archiveCopyAllTo(tmpOut);
			
			// Import files
			StringWriter out = new StringWriter();
			ImpExpStats stats = RepositoryImporter.importDocuments(null, tmpOut, path, false, out, new TextInfoDecorator(tmpOut));
			if (!stats.isOk()) {
				errorMsg = out.toString();
			}
			out.close();
		} catch (IOException e) {
			log.error("Error importing jar", e);
			throw e;
		} finally {
			if (tmpIn != null) {
				File.umount();
				org.apache.commons.io.FileUtils.deleteQuietly(tmpIn);
			}

			if (tmpOut != null) {
				org.apache.commons.io.FileUtils.deleteQuietly(tmpOut);
			}

			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					log.error("Error closing zip input stream", e);
					throw e;
				}
			}
		}
		
		log.debug("importJar: {}", errorMsg);
		return errorMsg;
	}
}
