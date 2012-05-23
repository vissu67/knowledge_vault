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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.search.FlagTerm;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.api.OKMDocument;
import com.openkm.api.OKMFolder;
import com.openkm.api.OKMRepository;
import com.openkm.bean.Document;
import com.openkm.bean.Mail;
import com.openkm.bean.Repository;
import com.openkm.core.AccessDeniedException;
import com.openkm.core.Config;
import com.openkm.core.ConversionException;
import com.openkm.core.DatabaseException;
import com.openkm.core.FileSizeExceededException;
import com.openkm.core.ItemExistsException;
import com.openkm.core.JcrSessionManager;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.core.UnsupportedMimeTypeException;
import com.openkm.core.UserQuotaExceededException;
import com.openkm.core.VirusDetectedException;
import com.openkm.dao.bean.MailAccount;
import com.openkm.dao.bean.MailFilter;
import com.openkm.dao.bean.MailFilterRule;
import com.openkm.extension.core.ExtensionException;
import com.openkm.jcr.JCRUtils;
import com.openkm.module.direct.DirectDocumentModule;
import com.openkm.module.direct.DirectMailModule;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.pop3.POP3Folder;

import freemarker.template.Template;
import freemarker.template.TemplateException;

public class MailUtils {
	private static Logger log = LoggerFactory.getLogger(MailUtils.class);
	
	/**
	 * Send mail without FROM addresses.
	 * 
	 * @param toAddress Destination addresses.
	 * @param subject The mail subject.
	 * @param content The mail body.
	 * @throws MessagingException If there is any error.
	 */
	public static void sendMessage(List<String> toAddress, String subject, String content) throws 
			MessagingException {
		try {
			send(null, toAddress, subject, content, null);
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
		} catch (RepositoryException e) {
			log.warn(e.getMessage(), e);
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
		} catch (DatabaseException e) {
			log.warn(e.getMessage(), e);
		}
	}
	
	/**
	 * Send mail without FROM addresses.
	 * 
	 * @param toAddress Destination addresses.
	 * @param subject The mail subject.
	 * @param content The mail body.
	 * @throws MessagingException If there is any error.
	 */
	public static void sendMessage(String toAddress, String subject, String content) throws 
			MessagingException {
		try {
			ArrayList<String> toList = new ArrayList<String>();
			toList.add(toAddress);
			send(null, toList, subject, content, null);
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
		} catch (RepositoryException e) {
			log.warn(e.getMessage(), e);
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
		} catch (DatabaseException e) {
			log.warn(e.getMessage(), e);
		}
	}

	/**
	 * Send mail without FROM addresses.
	 * 
	 * @param toAddress Destination addresses.
	 * @param subject The mail subject.
	 * @param content The mail body.
	 * @throws MessagingException If there is any error.
	 */
	public static void sendMessage(String fromAddress, List<String> toAddress, String subject, String content) throws 
			MessagingException {
		try {
			send(fromAddress, toAddress, subject, content, null);
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
		} catch (RepositoryException e) {
			log.warn(e.getMessage(), e);
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
		} catch (DatabaseException e) {
			log.warn(e.getMessage(), e);
		}
	}
	
	/**
	 * Send mail without FROM addresses.
	 * 
	 * @param toAddress Destination addresses.
	 * @param subject The mail subject.
	 * @param content The mail body.
	 * @throws MessagingException If there is any error.
	 */
	public static void sendMessage(String fromAddress, String toAddress, String subject, String content) throws 
			MessagingException {
		try {
			ArrayList<String> toList = new ArrayList<String>();
			toList.add(toAddress);
			send(fromAddress, toList, subject, content, null);
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
		} catch (RepositoryException e) {
			log.warn(e.getMessage(), e);
		} catch (IOException e) {
			log.warn(e.getMessage(), e);
		} catch (DatabaseException e) {
			log.warn(e.getMessage(), e);
		}
	}
	
	/**
	 * Send document to non-registered OpenKM users
	 * 
	 * @param toAddress Destination addresses.
	 * @param subject The mail subject.
	 * @param text The mail body.
	 * @throws MessagingException If there is any error.
	 */
	public static void sendDocument(String fromAddress, List<String> toAddress, String subject, String text, 
			String docPath) throws MessagingException, PathNotFoundException, RepositoryException,
			IOException, DatabaseException {
		send(fromAddress, toAddress, subject, text, docPath);
	}

	/**
	 * Send mail with FROM addresses.
	 * 
	 * @param fromAddress Origin address.
	 * @param toAddress Destination addresses.
	 * @param subject The mail subject.
	 * @param text The mail body.
	 * @throws MessagingException If there is any error.
	 */
	private static void send(String fromAddress, List<String> toAddress, String subject, String text, 
			String docPath)	throws MessagingException, PathNotFoundException, RepositoryException,
			IOException, DatabaseException {
		log.debug("send({}, {}, {}, {}, {})", new Object[] { fromAddress, toAddress, subject, text, docPath });
		Session mailSession = null;

		try {
			InitialContext initialContext = new InitialContext();
			mailSession = (Session) PortableRemoteObject.narrow(initialContext.lookup("java:/mail/OpenKM"), Session.class);
		} catch (javax.naming.NamingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		MimeMessage m = new MimeMessage(mailSession);

		if (fromAddress != null) {
			InternetAddress from = new InternetAddress(fromAddress);
			m.setFrom(from);
		} else {
			m.setFrom();
		}
		
		InternetAddress[] to = new InternetAddress[toAddress.size()];
		int i = 0;
		
		for (Iterator<String> it = toAddress.iterator(); it.hasNext(); ) {
			to[i++] = new InternetAddress(it.next());
		}
		
		m.addHeader("charset", "UTF-8");
		m.setRecipients(Message.RecipientType.TO, to);
		m.setSubject(subject, "UTF-8");
		m.setSentDate(new Date());
		
		// Build a multiparted mail with HTML and text content for better SPAM behaviour
		MimeMultipart content = new MimeMultipart("alternative");

		// Text part
		MimeBodyPart textPart = new MimeBodyPart();
		textPart.setText(text.replaceAll("<br/?>", "\n").replaceAll("<[^>]*>", ""));
		textPart.setHeader("Content-Type", "text/plain");
		content.addBodyPart(textPart);

		// HTML Part
		MimeBodyPart htmlPart = new MimeBodyPart();
		StringBuilder htmlContent = new StringBuilder();
		htmlContent.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">\n");
		htmlContent.append("<html>\n<head>\n");
		htmlContent.append("<meta content=\"text/html;charset=UTF-8\" http-equiv=\"Content-Type\"/>\n");
		htmlContent.append("</head>\n<body>\n");
		htmlContent.append(text);
		htmlContent.append("\n</body>\n</html>");
		htmlPart.setContent(htmlContent.toString(), "text/html; charset=UTF-8");
		htmlPart.setHeader("Content-Type", "text/html");
		content.addBodyPart(htmlPart);

		if (docPath != null) {
			InputStream is = null;
			FileOutputStream fos = null;
			String docName = JCRUtils.getName(docPath);
				
			try {
				is = OKMDocument.getInstance().getContent(null, docPath, false);
				File tmp = File.createTempFile("okm", ".tmp");
				fos = new FileOutputStream(tmp);
				IOUtils.copy(is, fos);
				fos.flush();
				
				// Document attachment part
				MimeBodyPart docPart = new MimeBodyPart();
				DataSource source = new FileDataSource(tmp.getPath());
				docPart.setDataHandler(new DataHandler(source));
				docPart.setFileName(docName);
				content.addBodyPart(docPart);
			} finally {
				IOUtils.closeQuietly(is);
				IOUtils.closeQuietly(fos);
			}
		}
        
        m.setContent(content);
		Transport.send(m);
		log.debug("send: void");
	}
		
	/**
	 * Import messages
	 * http://www.jguru.com/faq/view.jsp?EID=26898
	 * 
	 * == Using Unique Identifier (UIDL) ==
	 * Mail server assigns an unique identifier for every email in the same account. You can get as UIDL 
	 * for every email by MailInfo.UIDL property. To avoid receiving the same email twice, the best way is
	 * storing the UIDL of email retrieved to a text file or database. Next time before you retrieve email,
	 * compare your local uidl list with remote uidl. If this uidl exists in your local uidl list, don't
	 * receive it; otherwise receive it.
	 * 
	 * == Different property of UIDL in POP3 and IMAP4 ==
	 * UIDL is always unique in IMAP4 and it is always an incremental integer. UIDL in POP3 can be any valid
	 * asc-ii characters, and an UIDL may be reused by POP3 server if email with this UIDL has been deleted
	 * from the server. Hence you are advised to remove the uidl from your local uidl list if that uidl is
	 * no longer exist on the POP3 server.
	 * 
	 * == Remarks ==
	 * You should create different local uidl list for different email account, because the uidl is only
	 * unique for the same account. 
	 */
	public static String importMessages(String uid, MailAccount ma) throws PathNotFoundException,
			ItemExistsException, VirusDetectedException, AccessDeniedException, RepositoryException,
			DatabaseException, UserQuotaExceededException, ExtensionException {
		log.debug("importMessages({}, {})", new Object[] { uid, ma });
		Properties props = System.getProperties();
		Session session = Session.getDefaultInstance(props);
		String exceptionMessage = null;
		
		try {
			// Open connection
			Store store = session.getStore(ma.getMailProtocol());
			store.connect(ma.getMailHost(), ma.getMailUser(), ma.getMailPassword());
			
			Folder folder = store.getFolder(ma.getMailFolder());
			folder.open(Folder.READ_WRITE);
			// Message messages[] = folder.getMessages();
			Message messages[] = folder.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));
			
			for (int i=0; i < messages.length; i++) {
				Message msg = messages[i];
				// log.info(i + ": " + msg.getFrom()[0] + " " + msg.getSubject()+" "+msg.getContentType());
				// log.info("Received: "+msg.getReceivedDate());
				// log.info("Sent: "+msg.getSentDate());
				
				Calendar receivedDate = Calendar.getInstance();
				Calendar sentDate = Calendar.getInstance();
				
				// Can be void
				if (msg.getReceivedDate() != null) {
					receivedDate.setTime(msg.getReceivedDate());
				}
				
				// Can be void
				if (msg.getSentDate() != null) {
					sentDate.setTime(msg.getSentDate());
				}
				
				log.debug("{} -> {} - {}", new Object[] { i ,msg.getSubject(), msg.getReceivedDate() });
				com.openkm.bean.Mail mail = new com.openkm.bean.Mail();
				String body = getText(msg);
				
				// log.info("getText: "+body);
				if (body.charAt(0) == 'H') {
					mail.setMimeType("text/html");
				} else if (body.charAt(0) == 'T') {
					mail.setMimeType("text/plain");
				} else {
					mail.setMimeType("unknown");
				}
				
				mail.setContent(body.substring(1));
				
				if (msg.getFrom().length > 0) {
					mail.setFrom(MimeUtility.decodeText(msg.getFrom()[0].toString()));
				}
				
				mail.setSize(msg.getSize());
				mail.setSubject(msg.getSubject());
				mail.setTo(address2String(msg.getRecipients(Message.RecipientType.TO)));
				mail.setCc(address2String(msg.getRecipients(Message.RecipientType.CC)));
				mail.setBcc(address2String(msg.getRecipients(Message.RecipientType.BCC)));
				mail.setReceivedDate(receivedDate);
				mail.setSentDate(sentDate);
				
				if (ma.getMailFilters().isEmpty()) {
					log.debug("Import in compatibility mode");
					String mailPath = getUserMailPath(uid);
					importMail(mailPath, true, folder, msg, ma, mail);
				} else {
					for (MailFilter mf : ma.getMailFilters()) {
						log.debug("MailFilter: {}", mf);
						
						if (checkRules(mail, mf.getFilterRules())) {
							String mailPath = mf.getPath();
							importMail(mailPath, mf.isGrouping(), folder, msg, ma, mail);		
						}
					}
				}
				
				// Set message as seen
				if (ma.isMailMarkSeen()) {
					msg.setFlag(Flags.Flag.SEEN, true);
				} else {
					msg.setFlag(Flags.Flag.SEEN, false);
				}
				
				// Delete read mail if requested
				if (ma.isMailMarkDeleted()) {
					msg.setFlag(Flags.Flag.DELETED, true);
				}
			}
			
			// Close connection
			log.debug("Expunge: {}", ma.isMailMarkDeleted());
			folder.close(ma.isMailMarkDeleted());
			store.close();
		} catch (NoSuchProviderException e) {
			log.error(e.getMessage(), e);
			exceptionMessage = e.getMessage();
		} catch (MessagingException e) {
			log.error(e.getMessage(), e);
			exceptionMessage = e.getMessage();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			exceptionMessage = e.getMessage();
		}
		
		log.debug("importMessages: {}", exceptionMessage);
		return exceptionMessage;
	}
	
	/**
	 * Import mail into OpenKM repository 
	 */
	private static void importMail(String mailPath, boolean grouping, Folder folder, Message msg, 
			MailAccount ma, com.openkm.bean.Mail mail) throws DatabaseException, RepositoryException,
			AccessDeniedException, ItemExistsException, PathNotFoundException, MessagingException,
			VirusDetectedException, UserQuotaExceededException, IOException, ExtensionException {
		String systemToken = JcrSessionManager.getInstance().getSystemToken();
		OKMRepository okmRepository = OKMRepository.getInstance();
		String path = grouping ? createGroupPath(mailPath, mail.getReceivedDate()) : mailPath;
		
		if (ma.getMailProtocol().equals(MailAccount.PROTOCOL_POP3)) {
			mail.setPath(path + "/" + ((POP3Folder)folder).getUID(msg) + "-" + JCRUtils.escape(msg.getSubject()));
		} else {
			mail.setPath(path + "/" + ((IMAPFolder)folder).getUID(msg) + "-" + JCRUtils.escape(msg.getSubject()));
		}
		
		String newMailPath = JCRUtils.getParent(mail.getPath()) + "/" + JCRUtils.escape(JCRUtils.getName(mail.getPath())); 
		log.debug("newMailPath: {}", newMailPath);
		
		if (!okmRepository.hasNode(systemToken, newMailPath)) {
			new DirectMailModule().create(systemToken, mail, ma.getUser());
			
			try {
				addAttachments(mail, msg, ma.getUser());
			} catch (UnsupportedMimeTypeException e) {
				log.warn(e.getMessage(), e);
			} catch (FileSizeExceededException e) {
				log.warn(e.getMessage(), e);
			} catch (UserQuotaExceededException e) {
				log.warn(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Check mail import rules
	 */
	private static boolean checkRules(com.openkm.bean.Mail mail, Set<MailFilterRule> filterRules) {
		log.info("checkRules({}, {})", mail, filterRules);
		boolean ret = true;
		
		for (MailFilterRule fr : filterRules) {
			log.info("FilterRule: {}", fr);
			
			if (fr.isActive()) {
				if (MailFilterRule.FIELD_FROM.equals(fr.getField())) {
					if (MailFilterRule.OPERATION_CONTAINS.equals(fr.getOperation())) {
						ret &= mail.getFrom().toLowerCase().contains(fr.getValue().toLowerCase());
					} else if (MailFilterRule.OPERATION_EQUALS.equals(fr.getOperation())) {
						ret &= mail.getFrom().equalsIgnoreCase(fr.getValue());
					}
				} else if (MailFilterRule.FIELD_TO.equals(fr.getField())) {
					if (MailFilterRule.OPERATION_CONTAINS.equals(fr.getOperation())) {
						for (int j=0; j<mail.getTo().length; j++) {
							ret &= mail.getTo()[j].toLowerCase().contains(fr.getValue().toLowerCase());
						}
					} else if (MailFilterRule.OPERATION_EQUALS.equals(fr.getOperation())) {
						for (int j=0; j<mail.getTo().length; j++) {
							ret &= mail.getTo()[j].equalsIgnoreCase(fr.getValue());
						}
					}
				} else if (MailFilterRule.FIELD_SUBJECT.equals(fr.getField())) {
					if (MailFilterRule.OPERATION_CONTAINS.equals(fr.getOperation())) {
						ret &= mail.getSubject().toLowerCase().contains(fr.getValue().toLowerCase());
					} else if (MailFilterRule.OPERATION_EQUALS.equals(fr.getOperation())) {
						ret &= mail.getSubject().equalsIgnoreCase(fr.getValue());
					}
				} else if (MailFilterRule.FIELD_CONTENT.equals(fr.getField())) {
					if (MailFilterRule.OPERATION_CONTAINS.equals(fr.getOperation())) {
						ret &= mail.getContent().toLowerCase().contains(fr.getValue().toLowerCase());
					} else if (MailFilterRule.OPERATION_EQUALS.equals(fr.getOperation())) {
						ret &= mail.getContent().equalsIgnoreCase(fr.getValue());
					}
				}
			}
			
			log.info("FilterRule: {}", ret);
		}
		
		log.info("checkRules: {}", ret);
		return ret;
	}
	
	/**
	 * Create mail path
	 */
	private static String createGroupPath(String mailPath, Calendar receivedDate) throws DatabaseException,
			RepositoryException, AccessDeniedException, ItemExistsException, PathNotFoundException,
			ExtensionException {
		log.debug("createGroupPath({}, {})", new Object[] { mailPath, receivedDate });
		String systemToken = JcrSessionManager.getInstance().getSystemToken();
		OKMRepository okmRepository = OKMRepository.getInstance();
		String path = mailPath + "/" + receivedDate.get(Calendar.YEAR);
		OKMFolder okmFolder = OKMFolder.getInstance();
		
		if (!okmRepository.hasNode(systemToken, path)) {
			com.openkm.bean.Folder fld = new com.openkm.bean.Folder();
			fld.setPath(path);
			okmFolder.create(systemToken, fld);
		}
		
		path += "/" + (receivedDate.get(Calendar.MONTH) + 1);
		
		if (!okmRepository.hasNode(systemToken, path)) {
			com.openkm.bean.Folder fld = new com.openkm.bean.Folder();
			fld.setPath(path);
			okmFolder.create(systemToken, fld);
		}
		
		path += "/" + receivedDate.get(Calendar.DAY_OF_MONTH);
		
		if (!okmRepository.hasNode(systemToken, path)) {
			com.openkm.bean.Folder fld = new com.openkm.bean.Folder();
			fld.setPath(path);
			okmFolder.create(systemToken, fld);
		}
		
		log.debug("createGroupPath: {}", path);
		return path;
	}
	
	/**
	 * Get text from message
	 */
	private static String getText(Part p) throws MessagingException, IOException {
		if (p.isMimeType("text/*")) {
			String s = (String)p.getContent();

			if (p.isMimeType("text/html")) {
				return "H"+s;
			} else if (p.isMimeType("text/plain")) {
				return "T"+s;
			} else {
				return "X"+s;
			}
		} else if (p.isMimeType("multipart/alternative")) {
			// prefer plain text over html
			Multipart mp = (Multipart)p.getContent();
			String text = null;
			for (int i = 0; i < mp.getCount(); i++) {
				Part bp = mp.getBodyPart(i);
				if (bp.isMimeType("text/plain")) {
					String s = getText(bp);
					if (s != null)
						return s;
				} else if (bp.isMimeType("text/html")) {
					String s = getText(bp);
					if (s != null)
						return s;
				} else {
					return getText(bp);
				}
			}
			return text;
		} else if (p.isMimeType("multipart/*")) {
			Multipart mp = (Multipart)p.getContent();
			for (int i = 0; i < mp.getCount(); i++) {
				String s = getText(mp.getBodyPart(i));
				if (s != null)
					return s;
			}
		}

		return null;
	}
	
	/**
	 * Add attachments to an imported mail.
	 */
	private static void addAttachments(com.openkm.bean.Mail mail, Part p, String userId) throws MessagingException,
			IOException, UnsupportedMimeTypeException, FileSizeExceededException, UserQuotaExceededException,
			VirusDetectedException, ItemExistsException, PathNotFoundException, AccessDeniedException,
			RepositoryException, DatabaseException, ExtensionException {
		String systemToken = JcrSessionManager.getInstance().getSystemToken();
		
		if (p.isMimeType("multipart/*")) {
			Multipart mp = (Multipart)p.getContent();
			int count = mp.getCount();
			
			for (int i = 1; i < count; i++) {
				BodyPart bp = mp.getBodyPart(i);
				
				if (bp.getFileName() != null) {
					Document attachment = new Document();
					String mimeType = Config.mimeTypes.getContentType(bp.getFileName().toLowerCase());
					attachment.setMimeType(mimeType);
					attachment.setPath(mail.getPath() + "/" + bp.getFileName());
					InputStream is = bp.getInputStream();
					new DirectDocumentModule().create(systemToken, attachment, is, userId);
					is.close();
				}
			}
		}
	}
	
	/**
	 * @param addresses
	 * @return
	 */
	private static String[] address2String(Address[] addresses) {
		ArrayList<String> list = new ArrayList<String>();
		
		if (addresses != null) {
			for (int i=0; i<addresses.length; i++) {
				list.add(addresses[i].toString()); 
			}
		}
		
		return (String[]) list.toArray(new String[list.size()]);
	}
	
	/**
	 * 
	 */
	public static String getUserMailPath(String uid) {
		return "/"+Repository.MAIL+"/"+uid;
	}
	
	/**
	 * User tinyurl service as url shorter
	 * 
	 * Depends on commons-httpclient:commons-httpclient:jar:3.0 
	 * because of org.apache.jackrabbit:jackrabbit-webdav:jar:1.6.4
	 */
	public static String getTinyUrl(String fullUrl) throws HttpException, IOException {
		HttpClient httpclient = new HttpClient();
 
		// Prepare a request object
		HttpMethod method = new GetMethod("http://tinyurl.com/api-create.php");
		method.setQueryString(new NameValuePair[]{ new NameValuePair("url", fullUrl) });
		httpclient.executeMethod(method);
		InputStreamReader isr = new InputStreamReader(method.getResponseBodyAsStream(), "UTF-8");
		StringWriter sw = new StringWriter();
		int c; while ((c = isr.read()) != -1) sw.write(c);
		isr.close();
		method.releaseConnection();
	
		return sw.toString();
	}
	
	/**
	 * Test IMAP connection
	 */
	public static void testConnection(MailAccount ma) throws IOException {
		log.debug("testConnection({})", ma);
		Properties props = System.getProperties();
		Session session = Session.getDefaultInstance(props);
		Store store = null;
		Folder folder = null;
		
		try {
			store = session.getStore(ma.getMailProtocol());
			store.connect(ma.getMailHost(), ma.getMailUser(), ma.getMailPassword());
			folder = store.getFolder(ma.getMailFolder());
			folder.open(Folder.READ_WRITE);
			folder.close(false);
		} catch (NoSuchProviderException e) {
			throw new IOException(e.getMessage());
		} catch (MessagingException e) {
			throw new IOException(e.getMessage());
		} finally {
			// Try to close folder
			if (folder != null && folder.isOpen()) {
				try {
					folder.close(false);
				} catch (MessagingException e) {
					throw new IOException(e.getMessage());
				}
			}
			
			// Try to close store
			if (store != null) {
				try {
					store.close();
				} catch (MessagingException e) {
					throw new IOException(e.getMessage());
				}
			}
		}
		
		log.debug("testConnection: void");
	}
	
	/**
	 * Generate HTML with mail object data and contents
	 */
	public static String mail2html(Mail mail) throws ConversionException {
		HashMap<String, String> hm = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder();
		
		for (int i=0; i < mail.getTo().length - 1; i++) {
			sb.append(mail.getTo()[i]).append(", ");
		}
		
		sb.append(mail.getTo()[mail.getTo().length - 1]);
		hm.put("mailTo", sb.toString());
		hm.put("mailFrom", mail.getFrom());
		hm.put("mailSubject", mail.getSubject());
		hm.put("mailContent", mail.getContent());
		StringWriter sw = new StringWriter();
		InputStreamReader isr = null;
		
		try {
			isr = new InputStreamReader(MailUtils.class.getResourceAsStream("mail.ftl"));
			Template tpl = new Template("mail", isr, TemplateUtils.getConfig());
			tpl.process(hm, sw);
		} catch (IOException e) {
			throw new ConversionException("IOException: " + e.getMessage(), e);
		} catch (TemplateException e) {
			throw new ConversionException("TemplateException: " + e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(sw);
			IOUtils.closeQuietly(isr);
		}
		
		return sw.toString();
	}
}
