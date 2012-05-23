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

package com.openkm.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;

import javax.activation.MimetypesFileTypeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.StoredFile;
import com.openkm.dao.ConfigDAO;
import com.openkm.dao.MimeTypeDAO;
import com.openkm.dao.bean.MimeType;
import com.openkm.extractor.RegisteredExtractors;
import com.openkm.principal.DatabasePrincipalAdapter;
import com.openkm.util.EnvironmentDetector;

public class Config {
	private static Logger log = LoggerFactory.getLogger(Config.class);
	public static TreeMap<String, String> values = new TreeMap<String, String>();
	
	// Server specific configuration
	public static final String HOME_DIR = EnvironmentDetector.getServerHomeDir();
	public static final String TMP_DIR = EnvironmentDetector.getTempDir();
	public static final String NULL_DEVICE = EnvironmentDetector.getNullDevice();
	public static final String JNDI_BASE = EnvironmentDetector.getServerJndiBase();
	public static final boolean IN_SERVER = EnvironmentDetector.inServer();
	
	// Scripting
	public static final String START_SCRIPT = "start.bsh";
	public static final String STOP_SCRIPT = "stop.bsh";
	public static final String START_JAR = "start.jar";
	public static final String STOP_JAR = "stop.jar";
	
	// Configuration files
	public static final String OPENKM_CONFIG = "OpenKM.cfg";
	public static final String NODE_DEFINITIONS = "CustomNodes.cnd";
	public static String CONTEXT;
	public static String INSTANCE;
	public static String INSTANCE_CHROOT_PATH;
	public static String JBPM_CONFIG;
	public static String PROPERTY_GROUPS_XML;
	public static String PROPERTY_GROUPS_CND;
	
	// Default script
	public static final String PROPERTY_DEFAULT_SCRIPT = "default.script";
	
	// Preview cache
	public static String CACHE_HOME;
	public static String CACHE_DXF;
	public static String CACHE_PDF;
	public static String CACHE_SWF;
	
	// Experimental features
	public static String PROPERTY_EXPERIMENTAL_TEXT_EXTRACTION = "experimental.text.extraction";
	public static String PROPERTY_EXPERIMENTAL_MOBILE_CONTEXT = "experimental.mobile.context";
	public static String PROPERTY_EXPERIMENTAL_PLUGIN_DEBUG = "experimental.plugin.debug";
	
	// Configuration properties
	public static final String PROPERTY_REPOSITORY_CONFIG = "repository.config";
	public static final String PROPERTY_REPOSITORY_HOME = "repository.home";
	public static final String PROPERTY_CACHE_HOME = "cache.home";
	
	public static final String PROPERTY_DEFAULT_USER_ROLE = "default.user.role";
	public static final String PROPERTY_DEFAULT_ADMIN_ROLE = "default.admin.role";
	
	// Text extractors
	public static final String PROPERTY_REGISTERED_TEXT_EXTRACTORS = "registered.text.extractors";
	
	// Workflow
	public static final String PROPERTY_WORKFLOW_START_TASK_AUTO_RUN = "workflow.start.task.auto.run";
	public static final String PROPERTY_WORKFLOW_RUN_CONFIG_FORM = "workflow.run.config.form";
	
	// Principal
	public static final String PROPERTY_PRINCIPAL_ADAPTER = "principal.adapter";
	public static final String PROPERTY_PRINCIPAL_DATABASE_FILTER_INACTIVE_USERS = "principal.database.filter.inactive.users";
	
	// LDAP
	public static final String PROPERTY_PRINCIPAL_LDAP_SERVER = "principal.ldap.server";
	public static final String PROPERTY_PRINCIPAL_LDAP_SECURITY_PRINCIPAL = "principal.ldap.security.principal";
	public static final String PROPERTY_PRINCIPAL_LDAP_SECURITY_CREDENTIALS = "principal.ldap.security.credentials";
	
	public static final String PROPERTY_PRINCIPAL_LDAP_USER_SEARCH_BASE = "principal.ldap.user.search.base";
	public static final String PROPERTY_PRINCIPAL_LDAP_USER_SEARCH_FILTER = "principal.ldap.user.search.filter";
	public static final String PROPERTY_PRINCIPAL_LDAP_USER_ATTRIBUTE = "principal.ldap.user.attribute";
	
	public static final String PROPERTY_PRINCIPAL_LDAP_ROLE_SEARCH_BASE = "principal.ldap.role.search.base";
	public static final String PROPERTY_PRINCIPAL_LDAP_ROLE_SEARCH_FILTER = "principal.ldap.role.search.filter";
	public static final String PROPERTY_PRINCIPAL_LDAP_ROLE_ATTRIBUTE = "principal.ldap.role.attribute";
	
	public static final String PROPERTY_PRINCIPAL_LDAP_MAIL_SEARCH_BASE = "principal.ldap.mail.search.base";
	public static final String PROPERTY_PRINCIPAL_LDAP_MAIL_SEARCH_FILTER = "principal.ldap.mail.search.filter";
	public static final String PROPERTY_PRINCIPAL_LDAP_MAIL_ATTRIBUTE = "principal.ldap.mail.attribute";
	
	public static final String PROPERTY_PRINCIPAL_LDAP_USERS_BY_ROLE_SEARCH_BASE = "principal.ldap.users.by.role.search.base";
	public static final String PROPERTY_PRINCIPAL_LDAP_USERS_BY_ROLE_SEARCH_FILTER = "principal.ldap.users.by.role.search.filter";
	public static final String PROPERTY_PRINCIPAL_LDAP_USERS_BY_ROLE_ATTRIBUTE = "principal.ldap.users.by.role.attribute";
	
	public static final String PROPERTY_PRINCIPAL_LDAP_ROLES_BY_USER_SEARCH_BASE = "principal.ldap.roles.by.user.search.base";
	public static final String PROPERTY_PRINCIPAL_LDAP_ROLES_BY_USER_SEARCH_FILTER = "principal.ldap.roles.by.user.search.filter";
	public static final String PROPERTY_PRINCIPAL_LDAP_ROLES_BY_USER_ATTRIBUTE = "principal.ldap.roles.by.user.attribute";
	
	public static final String PROPERTY_MAX_FILE_SIZE = "max.file.size";
	public static final String PROPERTY_MAX_SEARCH_RESULTS = "max.search.results";
	
	public static final String PROPERTY_RESTRICT_FILE_MIME = "restrict.file.mime";
	public static final String PROPERTY_RESTRICT_FILE_EXTENSION = "restrict.file.extension";
	
	public static final String PROPERTY_NOTIFICATION_MESSAGE_SUBJECT = "notification.message.subject";
	public static final String PROPERTY_NOTIFICATION_MESSAGE_BODY = "notification.message.body";
	
	public static final String PROPERTY_SUBSCRIPTION_MESSAGE_SUBJECT = "subscription.message.subject";
	public static final String PROPERTY_SUBSCRIPTION_MESSAGE_BODY = "subscription.message.body";
	
	public static final String PROPERTY_SUBSCRIPTION_TWITTER_USER = "notify.twitter.user";
	public static final String PROPERTY_SUBSCRIPTION_TWITTER_PASSWORD = "notify.twitter.password";
	public static final String PROPERTY_SUBSCRIPTION_TWITTER_STATUS = "notify.twitter.status";
	
	public static final String PROPERTY_SYSTEM_DEMO = "system.demo";
	public static final String PROPERTY_SYSTEM_MULTIPLE_INSTANCES = "system.multiple.instances";
	public static final String PROPERTY_SYSTEM_APACHE_REQUEST_HEADER_FIX = "system.apache.request.header.fix";
	public static final String PROPERTY_SYSTEM_WEBDAV_SERVER = "system.webdav.server";
	public static final String PROPERTY_SYSTEM_WEBDAV_FIX = "system.webdav.fix";
	public static final String PROPERTY_SYSTEM_READONLY = "system.readonly";
	public static final String PROPERTY_SYSTEM_MAINTENANCE = "system.maintenance";
	public static final String PROPERTY_SYSTEM_OCR = "system.ocr";
	public static final String PROPERTY_SYSTEM_PDF_FORCE_OCR = "system.pdf.force.ocr";
	public static final String PROPERTY_SYSTEM_OPENOFFICE_PATH = "system.openoffice.path";
	public static final String PROPERTY_SYSTEM_OPENOFFICE_TASKS = "system.openoffice.tasks";
	public static final String PROPERTY_SYSTEM_OPENOFFICE_PORT = "system.openoffice.port";
	public static final String PROPERTY_SYSTEM_OPENOFFICE_SERVER = "system.openoffice.server";
	public static final String PROPERTY_SYSTEM_OPENOFFICE_DICTIONARY = "system.openoffice.dictionary";
	public static final String PROPERTY_SYSTEM_IMAGEMAGICK_CONVERT = "system.imagemagick.convert";
	public static final String PROPERTY_SYSTEM_SWFTOOLS_PDF2SWF = "system.swftools.pdf2swf";
	public static final String PROPERTY_SYSTEM_GHOSTSCRIPT_PS2PDF = "system.ghostscript.ps2pdf";
	public static final String PROPERTY_SYSTEM_DWG2DXF = "system.dwg2dxf";
	public static final String PROPERTY_SYSTEM_ANTIVIR = "system.antivir";
	public static final String PROPERTY_SYSTEM_LOGIN_LOWERCASE = "system.login.lowercase";
	public static final String PROPERTY_SYSTEM_PREVIEWER = "system.previewer";
	public static final String PROPERTY_SYSTEM_DOCUMENT_NAME_MISMATCH_CHECK = "system.document.name.mismatch.check";
	public static final String PROPERTY_SYSTEM_KEYWORD_LOWERCASE = "system.keyword.lowercase";
	
	public static final String PROPERTY_UPDATE_INFO = "update.info";
	public static final String PROPERTY_APPLICATION_URL = "application.url";
	public static final String PROPERTY_DEFAULT_LANG = "default.lang";
	public static final String PROPERTY_USER_ASSIGN_DOCUMENT_CREATION = "user.assign.document.creation";
	public static final String PROPERTY_USER_KEYWORDS_CACHE = "user.keywords.cache";
	public static final String PROPERTY_USER_ITEM_CACHE = "user.item.cache";
	
	// Schedule
	public static final String PROPERTY_SCHEDULE_REPOSITORY_INFO = "schedule.repository.info";
	public static final String PROPERTY_SCHEDULE_MAIL_IMPORTER = "schedule.mail.importer";
	public static final String PROPERTY_SCHEDULE_SESSION_KEEPALIVE = "schedule.session.keepalive";
	public static final String PROPERTY_SCHEDULE_DASHBOARD_REFRESH = "schedule.dashboard.refresh";
	
	// KEA
	// Used in generate_thesaurus.jsp
	public static final String PROPERTY_KEA_THESAURUS_SKOS_FILE = "kea.thesaurus.skos.file";
	public static final String PROPERTY_KEA_THESAURUS_OWL_FILE = "kea.thesaurus.owl.file";
	public static final String PROPERTY_KEA_THESAURUS_VOCABULARY_SERQL = "kea.thesaurus.vocabulary.serql";
	public static final String PROPERTY_KEA_THESAURUS_BASE_URL = "kea.thesaurus.base.url";
	public static final String PROPERTY_KEA_THESAURUS_TREE_ROOT = "kea.thesaurus.tree.root";
	public static final String PROPERTY_KEA_THESAURUS_TREE_CHILDS = "kea.thesaurus.tree.childs";
	public static final String PROPERTY_KEA_MODEL_FILE = "kea.model.file";
	public static final String PROPERTY_KEA_AUTOMATIC_KEYWORD_EXTRACTION_NUMBER = "kea.automatic.keyword.extraction.number";
	public static final String PROPERTY_KEA_AUTOMATIC_KEYWORD_EXTRACTION_RESTRICTION = "kea.automatic.keyword.extraction.restriction";
	public static final String PROPERTY_KEA_STOPWORDS_FILE = "kea.stopwords.file";
	
	// Validator
	public static final String PROPERTY_VALIDATOR_PASSWORD = "validator.password";
	
	public static final String PROPERTY_VALIDATOR_PASSWORD_MIN_LENGTH = "validator.password.min.length";
	public static final String PROPERTY_VALIDATOR_PASSWORD_MAX_LENGTH = "validator.password.max.length";
	public static final String PROPERTY_VALIDATOR_PASSWORD_MIN_LOWERCASE = "validator.password.min.lowercase";
	public static final String PROPERTY_VALIDATOR_PASSWORD_MIN_UPPERCASE = "validator.password.min.uppercase";
	public static final String PROPERTY_VALIDATOR_PASSWORD_MIN_DIGITS = "validator.password.min.digits";
	public static final String PROPERTY_VALIDATOR_PASSWORD_MIN_SPECIAL = "validator.password.mini.special";

	// Hibernate
	public static final String PROPERTY_HIBERNATE_DIALECT = "hibernate.dialect";
	public static final String PROPERTY_HIBERNATE_DATASOURCE = "hibernate.datasource";
	public static final String PROPERTY_HIBERNATE_HBM2DDL = "hibernate.hbm2ddl"; // Used in login.jsp
	public static final String PROPERTY_HIBERNATE_SHOW_SQL = "hibernate.show_sql";
	public static final String PROPERTY_HIBERNATE_STATISTICS = "hibernate.statistics";
	
	// Logo icons
	public static final String PROPERTY_LOGO_LOGIN = "logo.login";
	public static final String PROPERTY_LOGO_TEXT = "logo.text";
	public static final String PROPERTY_LOGO_MOBILE = "logo.mobile";
	public static final String PROPERTY_LOGO_REPORT = "logo.report";
	
	// Zoho
	public static final String PROPERTY_ZOHO_USER = "zoho.user";
	public static final String PROPERTY_ZOHO_PASSWORD = "zoho.password";
	public static final String PROPERTY_ZOHO_API_KEY = "zoho.api.key";
	public static final String PROPERTY_ZOHO_SECRET_KEY = "zoho.secret.key";
	
	// Mime types
	public static String MIME_PDF = "application/pdf";
	public static String MIME_POSTSCRIPT = "application/postscript";
	public static String MIME_MS_WORD = "application/msword";
	public static String MIME_MS_EXCEL = "application/vnd.ms-excel";
	public static String MIME_MS_POWERPOINT = "application/vnd.ms-powerpoint";
	public static String MIME_OO_TEXT = "application/vnd.oasis.opendocument.text";
	public static String MIME_OO_SPREADSHEET = "application/vnd.oasis.opendocument.spreadsheet";
	public static String MIME_OO_PRESENTATION = "application/vnd.oasis.opendocument.presentation";
	public static String MIME_SWF = "application/x-shockwave-flash";
	public static String MIME_DXF = "image/vnd.dxf";
	public static String MIME_DWG = "image/vnd.dwg";
	public static String MIME_TIFF = "image/tiff";
	
	/**
	 *  Default values
	 */
	// Experimental features
	public static String EXPERIMENTAL_MOBILE_CONTEXT = "mobile";
	public static boolean EXPERIMENTAL_TEXT_EXTRACTION = false;
	public static boolean EXPERIMENTAL_PLUGIN_DEBUG = false;
	
	public static String REPOSITORY_CONFIG;
	public static String REPOSITORY_HOME;
	public static String DEFAULT_SCRIPT;
	
	public static String SYSTEM_USER = "system";
	public static String ADMIN_USER = "okmAdmin";
	public static String GROUP_ADMIN = "joebloggs";  //added by vissu on oct 31th
	public static String DEFAULT_USER_ROLE;
	public static String DEFAULT_ADMIN_ROLE;
	//added by vissu on oct 10th
	public static String DEFAULT_GROUP_ADMIN_ROLE = "GroupAdminRole";  //added by vissu on oct 31th;
	
	// Text extractors
	public static String REGISTERED_TEXT_EXTRACTORS = 
		"org.apache.jackrabbit.extractor.PlainTextExtractor\n" +
		"org.apache.jackrabbit.extractor.MsWordTextExtractor\n" +
		"org.apache.jackrabbit.extractor.MsExcelTextExtractor\n" +
		"org.apache.jackrabbit.extractor.MsPowerPointTextExtractor\n" +
		"org.apache.jackrabbit.extractor.OpenOfficeTextExtractor\n" +
		"org.apache.jackrabbit.extractor.RTFTextExtractor\n" +
		"org.apache.jackrabbit.extractor.HTMLTextExtractor\n" +
		"org.apache.jackrabbit.extractor.XMLTextExtractor\n" +
		"org.apache.jackrabbit.extractor.PngTextExtractor\n" +
		"org.apache.jackrabbit.extractor.MsOutlookTextExtractor\n" +
		"com.openkm.extractor.PdfTextExtractor\n" +
		"com.openkm.extractor.AudioTextExtractor\n" +
		"com.openkm.extractor.ExifTextExtractor\n" +
		"com.openkm.extractor.CuneiformTextExtractor\n" +
		"com.openkm.extractor.SourceCodeTextExtractor\n" +
		"com.openkm.extractor.MsOffice2007TextExtractor";
	
	// Workflow
	public static String WORKFLOW_RUN_CONFIG_FORM;
	public static boolean WORKFLOW_START_TASK_AUTO_RUN;
	public static String WORKFLOW_PROCESS_INSTANCE_VARIABLE_UUID = "uuid";
	public static String WORKFLOW_PROCESS_INSTANCE_VARIABLE_PATH = "path";
	
	// Principal
	public static String PRINCIPAL_ADAPTER;
	public static boolean PRINCIPAL_DATABASE_FILTER_INACTIVE_USERS;
	
	// LDAP
	public static String PRINCIPAL_LDAP_SERVER; // ldap://phoenix.server:389
	public static String PRINCIPAL_LDAP_SECURITY_PRINCIPAL; //"cn=Administrator,cn=Users,dc=openkm,dc=com"
	public static String PRINCIPAL_LDAP_SECURITY_CREDENTIALS; // "xxxxxx"
	
	public static String PRINCIPAL_LDAP_USER_SEARCH_BASE; // ou=people,dc=openkm,dc=com
	public static String PRINCIPAL_LDAP_USER_SEARCH_FILTER; // (&(objectClass=posixAccount)(!(objectClass=gosaUserTemplate)))
	public static String PRINCIPAL_LDAP_USER_ATTRIBUTE; // uid
	
	public static String PRINCIPAL_LDAP_ROLE_SEARCH_BASE; // ou=groups,dc=openkm,dc=com
	public static String PRINCIPAL_LDAP_ROLE_SEARCH_FILTER; // (&(objectClass=posixGroup)(cn=*)(|(description=*OpenKM*)(cn=users)))
	public static String PRINCIPAL_LDAP_ROLE_ATTRIBUTE; // cn
	
	public static String PRINCIPAL_LDAP_MAIL_SEARCH_BASE; // uid={0},ou=people,dc=openkm,dc=com
	public static String PRINCIPAL_LDAP_MAIL_SEARCH_FILTER; // (&(objectClass=inetOrgPerson)(mail=*))
	public static String PRINCIPAL_LDAP_MAIL_ATTRIBUTE; // mail
	
	public static String PRINCIPAL_LDAP_USERS_BY_ROLE_SEARCH_BASE; 
	public static String PRINCIPAL_LDAP_USERS_BY_ROLE_SEARCH_FILTER; // (&(objectClass=group)(cn={0}))
	public static String PRINCIPAL_LDAP_USERS_BY_ROLE_ATTRIBUTE;
	
	public static String PRINCIPAL_LDAP_ROLES_BY_USER_SEARCH_BASE;
	public static String PRINCIPAL_LDAP_ROLES_BY_USER_SEARCH_FILTER; // (&(objectClass=group)(cn={0}))
	public static String PRINCIPAL_LDAP_ROLES_BY_USER_ATTRIBUTE;
	
	public static long MAX_FILE_SIZE;
	public static int MAX_SEARCH_RESULTS;
	
	public static boolean RESTRICT_FILE_MIME;
	public static String RESTRICT_FILE_EXTENSION;

	public static String NOTIFICATION_MESSAGE_SUBJECT;
	public static String NOTIFICATION_MESSAGE_BODY;

	public static String SUBSCRIPTION_MESSAGE_SUBJECT;
	public static String SUBSCRIPTION_MESSAGE_BODY;
	
	//added by vissu on nov2
	public static String ACCOUNT_MESSAGE_SUBJECT = "Knowledge Vault - New User - ${name}";
	public static String ACCOUNT_MESSAGE_BODY = "Hello ${name},<br><br>Welcome to Knowledge Vault! You have been introduced as a user under the following \"SuperUser\":<br>SuperUsername: joebloggs<br><br>Your new username and password on Knowledge Vault is<br/><b>Username: ${userId}</b><br/><br/><b>Password: ${password}</b><br/><br/>We hope you enjoy using Knowledge Vault.<br><br>Please click the following link if you wish to proceed straight to the Login page.<br><a href='http://demov5.knowledgevault.com.au'>http://demov5.knowledgevault.com.au</a>";

	public static String EMAIL_ADDRESS = "noreply@knowledgevault.com.au";
	public static String SITE_NAME = "Knowledge Vault";
	//addition end
	
	
	public static String SUBSCRIPTION_TWITTER_USER;
	public static String SUBSCRIPTION_TWITTER_PASSWORD;
	public static String SUBSCRIPTION_TWITTER_STATUS;
	
	public static boolean SYSTEM_DEMO;
	public static boolean SYSTEM_MULTIPLE_INSTANCES;
	public static boolean SYSTEM_APACHE_REQUEST_HEADER_FIX;
	public static boolean SYSTEM_WEBDAV_SERVER;
	public static boolean SYSTEM_WEBDAV_FIX;
	public static boolean SYSTEM_MAINTENANCE;
	public static boolean SYSTEM_READONLY;
	public static String SYSTEM_OCR = "";
	public static boolean SYSTEM_PDF_FORCE_OCR;
	public static String SYSTEM_OPENOFFICE_PATH = "";
	public static int SYSTEM_OPENOFFICE_TASKS;
	public static int SYSTEM_OPENOFFICE_PORT;
	public static String SYSTEM_OPENOFFICE_SERVER = "";
	public static String SYSTEM_OPENOFFICE_DICTIONARY = "";
	public static String SYSTEM_IMAGEMAGICK_CONVERT;
	public static String SYSTEM_SWFTOOLS_PDF2SWF;
	public static String SYSTEM_GHOSTSCRIPT_PS2PDF;
	public static String SYSTEM_DWG2DXF;
	public static String SYSTEM_ANTIVIR;
	public static boolean SYSTEM_LOGIN_LOWERCASE;
	public static String SYSTEM_PREVIEWER;
	public static boolean SYSTEM_DOCUMENT_NAME_MISMATCH_CHECK;
	public static boolean SYSTEM_KEYWORD_LOWERCASE;
	
	public static boolean UPDATE_INFO;
	public static String APPLICATION_URL;
	public static String APPLICATION_BASE;
	public static String DEFAULT_LANG;
	public static boolean USER_ASSIGN_DOCUMENT_CREATION;
	public static boolean USER_KEYWORDS_CACHE;
	public static boolean USER_ITEM_CACHE;
	
	// Schedule
	public static long SCHEDULE_REPOSITORY_INFO;
	public static long SCHEDULE_MAIL_IMPORTER;
	public static long SCHEDULE_SESSION_KEEPALIVE;
	public static long SCHEDULE_DASHBOARD_REFRESH;

	// KEA
	public static String KEA_THESAURUS_SKOS_FILE;
	public static String KEA_THESAURUS_OWL_FILE;
	public static String KEA_THESAURUS_VOCABULARY_SERQL;
	public static String KEA_THESAURUS_BASE_URL;
	public static String KEA_THESAURUS_TREE_ROOT;
	public static String KEA_THESAURUS_TREE_CHILDS;
	public static String KEA_MODEL_FILE;
	public static int KEA_AUTOMATIC_KEYWORD_EXTRACTION_NUMBER;
	public static boolean KEA_AUTOMATIC_KEYWORD_EXTRACTION_RESTRICTION;
	public static String KEA_STOPWORDS_FILE;

	// Validator
	public static String VALIDATOR_PASSWORD;
	
	public static int VALIDATOR_PASSWORD_MIN_LENGTH;
	public static int VALIDATOR_PASSWORD_MAX_LENGTH;
	public static int VALIDATOR_PASSWORD_MIN_LOWERCASE;
	public static int VALIDATOR_PASSWORD_MIN_UPPERCASE;
	public static int VALIDATOR_PASSWORD_MIN_DIGITS;
	public static int VALIDATOR_PASSWORD_MIN_SPECIAL;
	
	public static String VALIDATOR_PASSWORD_ERROR_MIN_LENGTH = "Password error: too short";
	public static String VALIDATOR_PASSWORD_ERROR_MAX_LENGTH = "Password error: too long";	
	public static String VALIDATOR_PASSWORD_ERROR_MIN_LOWERCASE = "Password error: too few lowercase characters";
	public static String VALIDATOR_PASSWORD_ERROR_MIN_UPPERCASE = "Password error: too few uppercase characters";
	public static String VALIDATOR_PASSWORD_ERROR_MIN_DIGITS = "Password error: too few digits";
	public static String VALIDATOR_PASSWORD_ERROR_MIN_SPECIAL = "Password error: too few special characters";
	
	// Hibernate
	public static String HIBERNATE_DIALECT = "org.hibernate.dialect.HSQLDialect";
	public static String HIBERNATE_DATASOURCE = "java:/OpenKMDS";
	public static String HIBERNATE_HBM2DDL = "create";
	public static String HIBERNATE_SHOW_SQL = "false";
	public static String HIBERNATE_STATISTICS = "false";
	
	// Logo icons
	public static StoredFile LOGO_LOGIN;
	public static String LOGO_TEXT;
	public static StoredFile LOGO_MOBILE;
	public static StoredFile LOGO_REPORT;
	
	// Zoho
	public static String ZOHO_USER;
	public static String ZOHO_PASSWORD;
	public static String ZOHO_API_KEY;
	public static String ZOHO_SECRET_KEY;
	
	// Misc
	public static int SESSION_EXPIRATION = 1800; // 30 mins (session.getMaxInactiveInterval())
	
	// Registered MIME types
	public static MimetypesFileTypeMap mimeTypes = new MimetypesFileTypeMap();
	
	/**
	 * Get url base
	 */
	private static String getBase(String url) {
		String ret = "";
		
		int idx1 = url.lastIndexOf('/');
		if (idx1 > 0) ret = url.substring(0, idx1);
		int idx2 = ret.lastIndexOf('/');
		if (idx2 > 0) ret = ret.substring(0, idx2);
		
		return ret;
	}
		
	/**
	 * Load OpenKM configuration from OpenKM.cfg 
	 */
	public static void load(String ctx) {
		Properties config = new Properties();
		String configFile = HOME_DIR+"/"+OPENKM_CONFIG;
		CONTEXT = ctx;
		
		// Read config
		try {
			log.info("** Reading config file " + configFile + " **");
			FileInputStream fis = new FileInputStream(configFile);
			config.load(fis);
			
			// Hibernate
			HIBERNATE_DIALECT = config.getProperty(PROPERTY_HIBERNATE_DIALECT, HIBERNATE_DIALECT);
			values.put(PROPERTY_HIBERNATE_DIALECT, HIBERNATE_DIALECT);
			HIBERNATE_DATASOURCE = config.getProperty(PROPERTY_HIBERNATE_DATASOURCE, "java:/" + CONTEXT + "DS");
			values.put(PROPERTY_HIBERNATE_DATASOURCE, HIBERNATE_DATASOURCE);
			HIBERNATE_HBM2DDL = config.getProperty(PROPERTY_HIBERNATE_HBM2DDL, HIBERNATE_HBM2DDL);
			values.put(PROPERTY_HIBERNATE_HBM2DDL, HIBERNATE_HBM2DDL);
			HIBERNATE_SHOW_SQL = config.getProperty(PROPERTY_HIBERNATE_SHOW_SQL, HIBERNATE_SHOW_SQL);
			values.put(PROPERTY_HIBERNATE_SHOW_SQL, HIBERNATE_SHOW_SQL);
			HIBERNATE_STATISTICS = config.getProperty(PROPERTY_HIBERNATE_STATISTICS, HIBERNATE_STATISTICS);
			values.put(PROPERTY_HIBERNATE_STATISTICS, HIBERNATE_STATISTICS);
			
			// Misc
			SYSTEM_MULTIPLE_INSTANCES = "on".equalsIgnoreCase(config.getProperty(PROPERTY_SYSTEM_MULTIPLE_INSTANCES, "off"));
			values.put(PROPERTY_SYSTEM_MULTIPLE_INSTANCES, Boolean.toString(SYSTEM_MULTIPLE_INSTANCES));
			
			fis.close();
			
			if (SYSTEM_MULTIPLE_INSTANCES) {
				INSTANCE = HOME_DIR + File.separator + "instances" + File.separator + CONTEXT;
				values.put("instance", INSTANCE);
				INSTANCE_CHROOT_PATH = INSTANCE + File.separator + "root" + File.separator;
				values.put("instance.chroot.path", INSTANCE_CHROOT_PATH);
			} else {
				INSTANCE = HOME_DIR;
				values.put("instance", INSTANCE);
				INSTANCE_CHROOT_PATH = "";
				values.put("instance.chroot.path", INSTANCE_CHROOT_PATH);
			}
			
			REPOSITORY_CONFIG = INSTANCE + File.separator + "repository.xml";
			values.put(PROPERTY_REPOSITORY_CONFIG, REPOSITORY_CONFIG);
			REPOSITORY_HOME = INSTANCE + File.separator + "repository";
			values.put(PROPERTY_REPOSITORY_HOME, REPOSITORY_HOME);
			
			CACHE_HOME = INSTANCE + File.separator + "cache";
			values.put(PROPERTY_CACHE_HOME, CACHE_HOME);
			
			JBPM_CONFIG = INSTANCE + File.separator + "jbpm.xml";
			values.put("jbpm.config", JBPM_CONFIG);
			
			PROPERTY_GROUPS_XML = INSTANCE + File.separator + "PropertyGroups.xml";
			values.put("property.groups.xml", PROPERTY_GROUPS_XML);
			PROPERTY_GROUPS_CND = INSTANCE + File.separator + "PropertyGroups.cnd";
			values.put("property.groups.cnd", PROPERTY_GROUPS_CND);
			
			// Load or reload database configuration
			reload(CONTEXT, config);
		} catch (FileNotFoundException e) {
			log.warn("** No {} file found, set default config **", OPENKM_CONFIG);
		} catch (IOException e) {
			log.warn("** IOError reading {}, set default config **", OPENKM_CONFIG);
		}
	}
	
	/**
	 * Reload OpenKM configuration from database
	 */
	public static void reload(String ctx, Properties cfg) {
		try {
			// Experimental features
			EXPERIMENTAL_MOBILE_CONTEXT = ConfigDAO.getString(PROPERTY_EXPERIMENTAL_MOBILE_CONTEXT, EXPERIMENTAL_MOBILE_CONTEXT);
			values.put(PROPERTY_EXPERIMENTAL_MOBILE_CONTEXT, EXPERIMENTAL_MOBILE_CONTEXT);
			EXPERIMENTAL_PLUGIN_DEBUG = ConfigDAO.getBoolean(PROPERTY_EXPERIMENTAL_PLUGIN_DEBUG, EXPERIMENTAL_PLUGIN_DEBUG);
			values.put(PROPERTY_EXPERIMENTAL_PLUGIN_DEBUG, Boolean.toString(EXPERIMENTAL_PLUGIN_DEBUG));
			EXPERIMENTAL_TEXT_EXTRACTION = ConfigDAO.getBoolean(PROPERTY_EXPERIMENTAL_TEXT_EXTRACTION, EXPERIMENTAL_TEXT_EXTRACTION);
			values.put(PROPERTY_EXPERIMENTAL_TEXT_EXTRACTION, Boolean.toString(EXPERIMENTAL_TEXT_EXTRACTION));
			
			REPOSITORY_CONFIG = ConfigDAO.getString(PROPERTY_REPOSITORY_CONFIG, REPOSITORY_CONFIG);
			values.put(PROPERTY_REPOSITORY_CONFIG, REPOSITORY_CONFIG);
			REPOSITORY_HOME = ConfigDAO.getString(PROPERTY_REPOSITORY_HOME, REPOSITORY_HOME);
			values.put(PROPERTY_REPOSITORY_HOME, REPOSITORY_HOME);
			
			CACHE_HOME = ConfigDAO.getString(PROPERTY_CACHE_HOME, CACHE_HOME);
			values.put(PROPERTY_CACHE_HOME, CACHE_HOME);
			CACHE_DXF = CACHE_HOME + File.separator + "dxf";
			values.put("cache.dxf", CACHE_DXF);
			CACHE_PDF = CACHE_HOME + File.separator + "pdf";
			values.put("cache.pdf", CACHE_PDF);
			CACHE_SWF = CACHE_HOME + File.separator + "swf";
			values.put("cache.swf", CACHE_SWF);
			
			DEFAULT_USER_ROLE = ConfigDAO.getString(PROPERTY_DEFAULT_USER_ROLE, "UserRole");
			values.put(PROPERTY_DEFAULT_USER_ROLE, DEFAULT_USER_ROLE);
			DEFAULT_ADMIN_ROLE = ConfigDAO.getString(PROPERTY_DEFAULT_ADMIN_ROLE, "AdminRole");
			values.put(PROPERTY_DEFAULT_ADMIN_ROLE, DEFAULT_ADMIN_ROLE);
			
			//added by vissu
			//DEFAULT_GROUP_ADMIN_ROLE = "GroupAdminRole";
			
			DEFAULT_SCRIPT = ConfigDAO.getText(PROPERTY_DEFAULT_SCRIPT, "print(\"UserId: \"+session.getUserID());\n" +
					"print(\"EventType: \"+eventType);\n" +
					"print(\"EventNode: \"+eventNode.getPath());\n" +
					"print(\"ScriptNode: \"+scriptNode.getPath());");
			values.put(PROPERTY_DEFAULT_SCRIPT, DEFAULT_SCRIPT);
			
			// Text extractors
			REGISTERED_TEXT_EXTRACTORS = ConfigDAO.getText(PROPERTY_REGISTERED_TEXT_EXTRACTORS, REGISTERED_TEXT_EXTRACTORS);
			values.put(PROPERTY_REGISTERED_TEXT_EXTRACTORS, REGISTERED_TEXT_EXTRACTORS);
			RegisteredExtractors.init();
			
			// Workflow
			WORKFLOW_RUN_CONFIG_FORM = ConfigDAO.getString(PROPERTY_WORKFLOW_RUN_CONFIG_FORM, "run_config");
			values.put(PROPERTY_WORKFLOW_RUN_CONFIG_FORM, WORKFLOW_RUN_CONFIG_FORM);
			WORKFLOW_START_TASK_AUTO_RUN = ConfigDAO.getBoolean(PROPERTY_WORKFLOW_START_TASK_AUTO_RUN, true);			
			values.put(PROPERTY_WORKFLOW_START_TASK_AUTO_RUN, Boolean.toString(WORKFLOW_START_TASK_AUTO_RUN));
			
			// Principal
			PRINCIPAL_ADAPTER = ConfigDAO.getString(PROPERTY_PRINCIPAL_ADAPTER, DatabasePrincipalAdapter.class.getCanonicalName());
			values.put(PROPERTY_PRINCIPAL_ADAPTER, PRINCIPAL_ADAPTER);
			PRINCIPAL_DATABASE_FILTER_INACTIVE_USERS = ConfigDAO.getBoolean(PROPERTY_PRINCIPAL_DATABASE_FILTER_INACTIVE_USERS, true);
			values.put(PROPERTY_PRINCIPAL_DATABASE_FILTER_INACTIVE_USERS, Boolean.toString(PRINCIPAL_DATABASE_FILTER_INACTIVE_USERS));

			// LDAP
			PRINCIPAL_LDAP_SERVER = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_SERVER, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_SERVER, PRINCIPAL_LDAP_SERVER);
			PRINCIPAL_LDAP_SECURITY_PRINCIPAL = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_SECURITY_PRINCIPAL, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_SECURITY_PRINCIPAL, PRINCIPAL_LDAP_SECURITY_PRINCIPAL);
			PRINCIPAL_LDAP_SECURITY_CREDENTIALS = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_SECURITY_CREDENTIALS, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_SECURITY_CREDENTIALS, PRINCIPAL_LDAP_SECURITY_CREDENTIALS);
			
			PRINCIPAL_LDAP_USER_SEARCH_BASE = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_USER_SEARCH_BASE, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_USER_SEARCH_BASE, PRINCIPAL_LDAP_USER_SEARCH_BASE);
			PRINCIPAL_LDAP_USER_SEARCH_FILTER = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_USER_SEARCH_FILTER, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_USER_SEARCH_FILTER, PRINCIPAL_LDAP_USER_SEARCH_FILTER);
			PRINCIPAL_LDAP_USER_ATTRIBUTE = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_USER_ATTRIBUTE, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_USER_ATTRIBUTE, PRINCIPAL_LDAP_USER_ATTRIBUTE);

			PRINCIPAL_LDAP_ROLE_SEARCH_BASE = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_ROLE_SEARCH_BASE, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_ROLE_SEARCH_BASE, PRINCIPAL_LDAP_ROLE_SEARCH_BASE);
			PRINCIPAL_LDAP_ROLE_SEARCH_FILTER = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_ROLE_SEARCH_FILTER, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_ROLE_SEARCH_FILTER, PRINCIPAL_LDAP_ROLE_SEARCH_FILTER);
			PRINCIPAL_LDAP_ROLE_ATTRIBUTE = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_ROLE_ATTRIBUTE, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_ROLE_ATTRIBUTE, PRINCIPAL_LDAP_ROLE_ATTRIBUTE);
			
			PRINCIPAL_LDAP_MAIL_SEARCH_BASE = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_MAIL_SEARCH_BASE, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_MAIL_SEARCH_BASE, PRINCIPAL_LDAP_MAIL_SEARCH_BASE);
			PRINCIPAL_LDAP_MAIL_SEARCH_FILTER = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_MAIL_SEARCH_FILTER, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_MAIL_SEARCH_FILTER, PRINCIPAL_LDAP_MAIL_SEARCH_FILTER);
			PRINCIPAL_LDAP_MAIL_ATTRIBUTE= ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_MAIL_ATTRIBUTE, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_MAIL_ATTRIBUTE, PRINCIPAL_LDAP_MAIL_ATTRIBUTE);
			
			PRINCIPAL_LDAP_USERS_BY_ROLE_SEARCH_BASE = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_USERS_BY_ROLE_SEARCH_BASE, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_USERS_BY_ROLE_SEARCH_BASE, PRINCIPAL_LDAP_USERS_BY_ROLE_SEARCH_BASE);
			PRINCIPAL_LDAP_USERS_BY_ROLE_SEARCH_FILTER = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_USERS_BY_ROLE_SEARCH_FILTER, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_USERS_BY_ROLE_SEARCH_FILTER, PRINCIPAL_LDAP_USERS_BY_ROLE_SEARCH_FILTER);
			PRINCIPAL_LDAP_USERS_BY_ROLE_ATTRIBUTE = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_USERS_BY_ROLE_ATTRIBUTE, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_USERS_BY_ROLE_ATTRIBUTE, PRINCIPAL_LDAP_USERS_BY_ROLE_ATTRIBUTE);
			
			PRINCIPAL_LDAP_ROLES_BY_USER_SEARCH_BASE = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_ROLES_BY_USER_SEARCH_BASE, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_ROLES_BY_USER_SEARCH_BASE, PRINCIPAL_LDAP_ROLES_BY_USER_SEARCH_BASE);
			PRINCIPAL_LDAP_ROLES_BY_USER_SEARCH_FILTER = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_ROLES_BY_USER_SEARCH_FILTER, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_ROLES_BY_USER_SEARCH_FILTER, PRINCIPAL_LDAP_ROLES_BY_USER_SEARCH_FILTER);
			PRINCIPAL_LDAP_ROLES_BY_USER_ATTRIBUTE = ConfigDAO.getString(PROPERTY_PRINCIPAL_LDAP_ROLES_BY_USER_ATTRIBUTE, "");
			values.put(PROPERTY_PRINCIPAL_LDAP_ROLES_BY_USER_ATTRIBUTE, PRINCIPAL_LDAP_ROLES_BY_USER_ATTRIBUTE);
			
			MAX_FILE_SIZE = ConfigDAO.getLong(PROPERTY_MAX_FILE_SIZE, 64) * 1024 * 1024; // 64 * 1024 * 1024 = 64 MB
			values.put(PROPERTY_MAX_FILE_SIZE, Long.toString(MAX_FILE_SIZE));
			MAX_SEARCH_RESULTS = ConfigDAO.getInteger(PROPERTY_MAX_SEARCH_RESULTS, 261006); // Almost infinite
			values.put(PROPERTY_MAX_SEARCH_RESULTS, Integer.toString(MAX_SEARCH_RESULTS));

			RESTRICT_FILE_MIME = ConfigDAO.getBoolean(PROPERTY_RESTRICT_FILE_MIME, false);
			values.put(PROPERTY_RESTRICT_FILE_MIME, Boolean.toString(RESTRICT_FILE_MIME));
			RESTRICT_FILE_EXTENSION = ConfigDAO.getString(PROPERTY_RESTRICT_FILE_EXTENSION, "*~,*.bak");
			values.put(PROPERTY_RESTRICT_FILE_EXTENSION, RESTRICT_FILE_EXTENSION);
	//vissu Demo on 22nov
			NOTIFICATION_MESSAGE_SUBJECT = ConfigDAO.getText(PROPERTY_NOTIFICATION_MESSAGE_SUBJECT, "Demo - NOTIFICATION - ${documentName}");
			values.put(PROPERTY_NOTIFICATION_MESSAGE_SUBJECT, NOTIFICATION_MESSAGE_SUBJECT);
			NOTIFICATION_MESSAGE_BODY = ConfigDAO.getText(PROPERTY_NOTIFICATION_MESSAGE_BODY, "<b>Document: </b><a href=\"${documentUrl}\">${documentPath}</a><br/><b>User: </b>${userId}<br/><b>Message: </b>${notificationMessage}<br/>");
			values.put(PROPERTY_NOTIFICATION_MESSAGE_BODY, NOTIFICATION_MESSAGE_BODY);
			
			//vissu Demo
			SUBSCRIPTION_MESSAGE_SUBJECT = ConfigDAO.getText(PROPERTY_SUBSCRIPTION_MESSAGE_SUBJECT, "Demo - ${eventType} - ${documentPath}");
			values.put(PROPERTY_SUBSCRIPTION_MESSAGE_SUBJECT, SUBSCRIPTION_MESSAGE_SUBJECT);
			SUBSCRIPTION_MESSAGE_BODY = ConfigDAO.getText(PROPERTY_SUBSCRIPTION_MESSAGE_BODY, "<b>Document: </b><a href=\"${documentUrl}\">${documentPath}</a><br/><b>User: </b>${userId}<br/><b>Event: </b>${eventType}<br/><b>Comment: </b>${subscriptionComment}<br/>");
			values.put(PROPERTY_SUBSCRIPTION_MESSAGE_BODY, SUBSCRIPTION_MESSAGE_BODY);
			
			SUBSCRIPTION_TWITTER_USER = ConfigDAO.getString(PROPERTY_SUBSCRIPTION_TWITTER_USER, "");
			values.put(PROPERTY_SUBSCRIPTION_TWITTER_USER, SUBSCRIPTION_TWITTER_USER);
			SUBSCRIPTION_TWITTER_PASSWORD = ConfigDAO.getString(PROPERTY_SUBSCRIPTION_TWITTER_PASSWORD, "");
			values.put(PROPERTY_SUBSCRIPTION_TWITTER_PASSWORD, SUBSCRIPTION_TWITTER_PASSWORD);
			SUBSCRIPTION_TWITTER_STATUS = ConfigDAO.getText(PROPERTY_SUBSCRIPTION_TWITTER_STATUS, "Demo - ${documentUrl} - ${documentPath} - ${userId} - ${eventType}");
			values.put(PROPERTY_SUBSCRIPTION_TWITTER_STATUS, SUBSCRIPTION_TWITTER_STATUS);
			
			SYSTEM_DEMO = ConfigDAO.getBoolean(PROPERTY_SYSTEM_DEMO, "on".equalsIgnoreCase(cfg.getProperty(PROPERTY_SYSTEM_DEMO, "off")));
			values.put(PROPERTY_SYSTEM_DEMO, Boolean.toString(SYSTEM_DEMO));
			SYSTEM_APACHE_REQUEST_HEADER_FIX = ConfigDAO.getBoolean(PROPERTY_SYSTEM_APACHE_REQUEST_HEADER_FIX, "on".equalsIgnoreCase(cfg.getProperty(PROPERTY_SYSTEM_APACHE_REQUEST_HEADER_FIX, "off")));
			values.put(PROPERTY_SYSTEM_APACHE_REQUEST_HEADER_FIX, Boolean.toString(SYSTEM_APACHE_REQUEST_HEADER_FIX));
			SYSTEM_WEBDAV_SERVER = ConfigDAO.getBoolean(PROPERTY_SYSTEM_WEBDAV_SERVER, "on".equalsIgnoreCase(cfg.getProperty(PROPERTY_SYSTEM_WEBDAV_SERVER, "off")));
			values.put(PROPERTY_SYSTEM_WEBDAV_SERVER, Boolean.toString(SYSTEM_WEBDAV_SERVER));
			SYSTEM_WEBDAV_FIX = ConfigDAO.getBoolean(PROPERTY_SYSTEM_WEBDAV_FIX, "on".equalsIgnoreCase(cfg.getProperty(PROPERTY_SYSTEM_WEBDAV_FIX, "off")));
			values.put(PROPERTY_SYSTEM_WEBDAV_FIX, Boolean.toString(SYSTEM_WEBDAV_FIX));
			SYSTEM_MAINTENANCE = ConfigDAO.getBoolean(PROPERTY_SYSTEM_MAINTENANCE, false);
			values.put(PROPERTY_SYSTEM_MAINTENANCE, Boolean.toString(SYSTEM_MAINTENANCE));
			SYSTEM_READONLY = ConfigDAO.getBoolean(PROPERTY_SYSTEM_READONLY, false);
			values.put(PROPERTY_SYSTEM_READONLY, Boolean.toString(SYSTEM_READONLY));
			
			SYSTEM_OPENOFFICE_PATH = ConfigDAO.getString(PROPERTY_SYSTEM_OPENOFFICE_PATH, cfg.getProperty(PROPERTY_SYSTEM_OPENOFFICE_PATH, ""));
			values.put(PROPERTY_SYSTEM_OPENOFFICE_PATH, SYSTEM_OPENOFFICE_PATH);
			SYSTEM_OPENOFFICE_TASKS = ConfigDAO.getInteger(PROPERTY_SYSTEM_OPENOFFICE_TASKS, 200);
			values.put(PROPERTY_SYSTEM_OPENOFFICE_TASKS, Integer.toString(SYSTEM_OPENOFFICE_TASKS));
			SYSTEM_OPENOFFICE_PORT = ConfigDAO.getInteger(PROPERTY_SYSTEM_OPENOFFICE_PORT, 2002);
			values.put(PROPERTY_SYSTEM_OPENOFFICE_PORT, Integer.toString(SYSTEM_OPENOFFICE_PORT));
			SYSTEM_OPENOFFICE_SERVER = ConfigDAO.getString(PROPERTY_SYSTEM_OPENOFFICE_SERVER, cfg.getProperty(PROPERTY_SYSTEM_OPENOFFICE_SERVER, ""));
			values.put(PROPERTY_SYSTEM_OPENOFFICE_SERVER, SYSTEM_OPENOFFICE_SERVER);
			SYSTEM_OPENOFFICE_DICTIONARY = ConfigDAO.getString(PROPERTY_SYSTEM_OPENOFFICE_DICTIONARY, "");
			values.put(PROPERTY_SYSTEM_OPENOFFICE_DICTIONARY, SYSTEM_OPENOFFICE_DICTIONARY);
			
			SYSTEM_OCR = ConfigDAO.getString(PROPERTY_SYSTEM_OCR, cfg.getProperty(PROPERTY_SYSTEM_OCR, ""));
			values.put(PROPERTY_SYSTEM_OCR, SYSTEM_OCR);
			SYSTEM_PDF_FORCE_OCR = ConfigDAO.getBoolean(PROPERTY_SYSTEM_PDF_FORCE_OCR, "on".equalsIgnoreCase(cfg.getProperty(PROPERTY_SYSTEM_PDF_FORCE_OCR, "off")));
			values.put(PROPERTY_SYSTEM_PDF_FORCE_OCR, Boolean.toString(SYSTEM_PDF_FORCE_OCR));
			SYSTEM_IMAGEMAGICK_CONVERT = ConfigDAO.getString(PROPERTY_SYSTEM_IMAGEMAGICK_CONVERT, cfg.getProperty(PROPERTY_SYSTEM_IMAGEMAGICK_CONVERT, ""));
			values.put(PROPERTY_SYSTEM_IMAGEMAGICK_CONVERT, SYSTEM_IMAGEMAGICK_CONVERT);
			SYSTEM_SWFTOOLS_PDF2SWF = ConfigDAO.getString(PROPERTY_SYSTEM_SWFTOOLS_PDF2SWF, cfg.getProperty(PROPERTY_SYSTEM_SWFTOOLS_PDF2SWF, ""));
			values.put(PROPERTY_SYSTEM_SWFTOOLS_PDF2SWF, SYSTEM_SWFTOOLS_PDF2SWF);
			SYSTEM_GHOSTSCRIPT_PS2PDF = ConfigDAO.getString(PROPERTY_SYSTEM_GHOSTSCRIPT_PS2PDF, cfg.getProperty(PROPERTY_SYSTEM_GHOSTSCRIPT_PS2PDF, ""));
			values.put(PROPERTY_SYSTEM_GHOSTSCRIPT_PS2PDF, SYSTEM_GHOSTSCRIPT_PS2PDF);
			SYSTEM_DWG2DXF = ConfigDAO.getString(PROPERTY_SYSTEM_DWG2DXF, cfg.getProperty(PROPERTY_SYSTEM_DWG2DXF, ""));
			values.put(PROPERTY_SYSTEM_DWG2DXF, SYSTEM_DWG2DXF);
			SYSTEM_ANTIVIR = ConfigDAO.getString(PROPERTY_SYSTEM_ANTIVIR, cfg.getProperty(PROPERTY_SYSTEM_ANTIVIR, ""));
			values.put(PROPERTY_SYSTEM_ANTIVIR, SYSTEM_ANTIVIR);
			SYSTEM_PREVIEWER = ConfigDAO.getString(PROPERTY_SYSTEM_PREVIEWER, "zviewer"); // flexpaper
			values.put(PROPERTY_SYSTEM_PREVIEWER, SYSTEM_PREVIEWER);
			SYSTEM_LOGIN_LOWERCASE = ConfigDAO.getBoolean(PROPERTY_SYSTEM_LOGIN_LOWERCASE, false);
			values.put(PROPERTY_SYSTEM_LOGIN_LOWERCASE, Boolean.toString(SYSTEM_LOGIN_LOWERCASE));
			SYSTEM_DOCUMENT_NAME_MISMATCH_CHECK = ConfigDAO.getBoolean(PROPERTY_SYSTEM_DOCUMENT_NAME_MISMATCH_CHECK, true);
			values.put(PROPERTY_SYSTEM_DOCUMENT_NAME_MISMATCH_CHECK, Boolean.toString(SYSTEM_DOCUMENT_NAME_MISMATCH_CHECK));
			SYSTEM_KEYWORD_LOWERCASE = ConfigDAO.getBoolean(PROPERTY_SYSTEM_KEYWORD_LOWERCASE, false);
			values.put(PROPERTY_SYSTEM_KEYWORD_LOWERCASE, Boolean.toString(SYSTEM_KEYWORD_LOWERCASE));
			
			// Modify default admin user if login lowercase is active
			if (SYSTEM_LOGIN_LOWERCASE) {
				ADMIN_USER = ADMIN_USER.toLowerCase();
			}
			
			UPDATE_INFO = ConfigDAO.getBoolean(PROPERTY_UPDATE_INFO, "on".equalsIgnoreCase(cfg.getProperty(PROPERTY_UPDATE_INFO, "on")));
			values.put(PROPERTY_UPDATE_INFO, Boolean.toString(UPDATE_INFO));
			APPLICATION_URL = ConfigDAO.getString(PROPERTY_APPLICATION_URL, "http://localhost:8080/"+ctx+"/index.jsp");
			APPLICATION_BASE = getBase(APPLICATION_URL); 
			values.put(PROPERTY_APPLICATION_URL, APPLICATION_URL);
			DEFAULT_LANG = ConfigDAO.getString(PROPERTY_DEFAULT_LANG, "");
			values.put(PROPERTY_DEFAULT_LANG, DEFAULT_LANG);
			
			USER_ASSIGN_DOCUMENT_CREATION = ConfigDAO.getBoolean(PROPERTY_USER_ASSIGN_DOCUMENT_CREATION, true);
			values.put(PROPERTY_USER_ASSIGN_DOCUMENT_CREATION, Boolean.toString(USER_ASSIGN_DOCUMENT_CREATION));
			USER_KEYWORDS_CACHE = ConfigDAO.getBoolean(PROPERTY_USER_KEYWORDS_CACHE, false);
			values.put(PROPERTY_USER_KEYWORDS_CACHE, Boolean.toString(USER_KEYWORDS_CACHE));
			USER_ITEM_CACHE = ConfigDAO.getBoolean(PROPERTY_USER_ITEM_CACHE, "on".equalsIgnoreCase(cfg.getProperty(PROPERTY_USER_ITEM_CACHE, "on")));
			values.put(PROPERTY_USER_ITEM_CACHE, Boolean.toString(USER_ITEM_CACHE));
			
			// Schedule
			SCHEDULE_REPOSITORY_INFO = ConfigDAO.getLong(PROPERTY_SCHEDULE_REPOSITORY_INFO, 1440) * 60 * 1000; // 1440 * 60 * 1000 = 24 hours
			values.put(PROPERTY_SCHEDULE_REPOSITORY_INFO, Long.toString(SCHEDULE_REPOSITORY_INFO));
			SCHEDULE_MAIL_IMPORTER = ConfigDAO.getLong(PROPERTY_SCHEDULE_MAIL_IMPORTER, 0) * 60 * 1000; // 0 * 60 * 1000 = disabled
			values.put(PROPERTY_SCHEDULE_MAIL_IMPORTER, Long.toString(SCHEDULE_MAIL_IMPORTER));
			SCHEDULE_SESSION_KEEPALIVE = ConfigDAO.getLong(PROPERTY_SCHEDULE_SESSION_KEEPALIVE, 15) * 60 * 1000; // 15 * 60 * 1000 = 15 min
			values.put(PROPERTY_SCHEDULE_SESSION_KEEPALIVE, Long.toString(SCHEDULE_SESSION_KEEPALIVE));
			SCHEDULE_DASHBOARD_REFRESH = ConfigDAO.getLong(PROPERTY_SCHEDULE_DASHBOARD_REFRESH, 30) * 60 * 1000; // 30 * 60 * 1000 = 30 min
			values.put(PROPERTY_SCHEDULE_DASHBOARD_REFRESH, Long.toString(SCHEDULE_DASHBOARD_REFRESH));
			
			// KEA
			KEA_THESAURUS_SKOS_FILE = ConfigDAO.getString(PROPERTY_KEA_THESAURUS_SKOS_FILE, "");
			values.put(PROPERTY_KEA_THESAURUS_SKOS_FILE, KEA_THESAURUS_SKOS_FILE);
			KEA_THESAURUS_OWL_FILE = ConfigDAO.getString(PROPERTY_KEA_THESAURUS_OWL_FILE, "");
			values.put(PROPERTY_KEA_THESAURUS_OWL_FILE, KEA_THESAURUS_OWL_FILE);
			KEA_THESAURUS_VOCABULARY_SERQL = ConfigDAO.getText(PROPERTY_KEA_THESAURUS_VOCABULARY_SERQL, "");
			values.put(PROPERTY_KEA_THESAURUS_VOCABULARY_SERQL, KEA_THESAURUS_VOCABULARY_SERQL);
			KEA_THESAURUS_BASE_URL = ConfigDAO.getString(PROPERTY_KEA_THESAURUS_BASE_URL, "");
			values.put(PROPERTY_KEA_THESAURUS_BASE_URL, KEA_THESAURUS_BASE_URL);
			KEA_THESAURUS_TREE_ROOT = ConfigDAO.getText(PROPERTY_KEA_THESAURUS_TREE_ROOT, "");
			values.put(PROPERTY_KEA_THESAURUS_TREE_ROOT, KEA_THESAURUS_TREE_ROOT);
			KEA_THESAURUS_TREE_CHILDS = ConfigDAO.getText(PROPERTY_KEA_THESAURUS_TREE_CHILDS, "");
			values.put(PROPERTY_KEA_THESAURUS_TREE_CHILDS, KEA_THESAURUS_TREE_CHILDS);
			KEA_MODEL_FILE = ConfigDAO.getString(PROPERTY_KEA_MODEL_FILE, "");
			values.put(PROPERTY_KEA_MODEL_FILE, KEA_MODEL_FILE);
			KEA_AUTOMATIC_KEYWORD_EXTRACTION_NUMBER = ConfigDAO.getInteger(PROPERTY_KEA_AUTOMATIC_KEYWORD_EXTRACTION_NUMBER, 0);
			values.put(PROPERTY_KEA_AUTOMATIC_KEYWORD_EXTRACTION_NUMBER, Integer.toString(KEA_AUTOMATIC_KEYWORD_EXTRACTION_NUMBER));
			KEA_AUTOMATIC_KEYWORD_EXTRACTION_RESTRICTION = ConfigDAO.getBoolean(PROPERTY_KEA_AUTOMATIC_KEYWORD_EXTRACTION_RESTRICTION, false);			
			values.put(PROPERTY_KEA_AUTOMATIC_KEYWORD_EXTRACTION_RESTRICTION, Boolean.toString(KEA_AUTOMATIC_KEYWORD_EXTRACTION_RESTRICTION));
			KEA_STOPWORDS_FILE = ConfigDAO.getString(PROPERTY_KEA_STOPWORDS_FILE, "");
			values.put(PROPERTY_KEA_STOPWORDS_FILE, KEA_STOPWORDS_FILE);
			
			// Validator
			VALIDATOR_PASSWORD = ConfigDAO.getString(PROPERTY_VALIDATOR_PASSWORD, "com.openkm.validator.password.NoPasswordValidator");
			values.put(PROPERTY_VALIDATOR_PASSWORD, VALIDATOR_PASSWORD);
			
			VALIDATOR_PASSWORD_MIN_LENGTH = ConfigDAO.getInteger(PROPERTY_VALIDATOR_PASSWORD_MIN_LENGTH, 0);
			values.put(PROPERTY_VALIDATOR_PASSWORD_MIN_LENGTH, Integer.toString(VALIDATOR_PASSWORD_MIN_LENGTH));
			VALIDATOR_PASSWORD_MAX_LENGTH = ConfigDAO.getInteger(PROPERTY_VALIDATOR_PASSWORD_MAX_LENGTH, 0);
			values.put(PROPERTY_VALIDATOR_PASSWORD_MAX_LENGTH, Integer.toString(VALIDATOR_PASSWORD_MAX_LENGTH));
			VALIDATOR_PASSWORD_MIN_LOWERCASE = ConfigDAO.getInteger(PROPERTY_VALIDATOR_PASSWORD_MIN_LOWERCASE, 0);
			values.put(PROPERTY_VALIDATOR_PASSWORD_MIN_LOWERCASE, Integer.toString(VALIDATOR_PASSWORD_MIN_LOWERCASE));
			VALIDATOR_PASSWORD_MIN_UPPERCASE = ConfigDAO.getInteger(PROPERTY_VALIDATOR_PASSWORD_MIN_UPPERCASE, 0);
			values.put(PROPERTY_VALIDATOR_PASSWORD_MIN_UPPERCASE, Integer.toString(VALIDATOR_PASSWORD_MIN_UPPERCASE));
			VALIDATOR_PASSWORD_MIN_DIGITS = ConfigDAO.getInteger(PROPERTY_VALIDATOR_PASSWORD_MIN_DIGITS, 0);
			values.put(PROPERTY_VALIDATOR_PASSWORD_MIN_DIGITS, Integer.toString(VALIDATOR_PASSWORD_MIN_DIGITS));
			VALIDATOR_PASSWORD_MIN_SPECIAL = ConfigDAO.getInteger(PROPERTY_VALIDATOR_PASSWORD_MIN_SPECIAL, 0);
			values.put(PROPERTY_VALIDATOR_PASSWORD_MIN_SPECIAL, Integer.toString(VALIDATOR_PASSWORD_MIN_SPECIAL));
			
			// Logo icons
			LOGO_LOGIN = ConfigDAO.getFile(PROPERTY_LOGO_LOGIN, "/img/logo_login.gif");
			values.put(PROPERTY_LOGO_LOGIN, LOGO_LOGIN.getName());
			LOGO_TEXT = ConfigDAO.getString(PROPERTY_LOGO_TEXT, "&nbsp;");
			values.put(PROPERTY_LOGO_TEXT, LOGO_TEXT);
			LOGO_MOBILE = ConfigDAO.getFile(PROPERTY_LOGO_MOBILE, "/img/logo_mobile.gif");
			values.put(PROPERTY_LOGO_MOBILE, LOGO_MOBILE.getName());
			LOGO_REPORT = ConfigDAO.getFile(PROPERTY_LOGO_REPORT, "/img/logo_report.gif");
			values.put(PROPERTY_LOGO_REPORT, LOGO_REPORT.getName());
			
			// Zoho
			ZOHO_USER = ConfigDAO.getString(PROPERTY_ZOHO_USER, cfg.getProperty(PROPERTY_ZOHO_USER, ""));
			values.put(PROPERTY_ZOHO_USER, ZOHO_USER);
			ZOHO_PASSWORD = ConfigDAO.getString(PROPERTY_ZOHO_PASSWORD, cfg.getProperty(PROPERTY_ZOHO_PASSWORD, ""));
			values.put(PROPERTY_ZOHO_PASSWORD, ZOHO_PASSWORD);
			ZOHO_API_KEY = ConfigDAO.getString(PROPERTY_ZOHO_API_KEY, cfg.getProperty(PROPERTY_ZOHO_API_KEY, ""));
			values.put(PROPERTY_ZOHO_API_KEY, ZOHO_API_KEY);
			ZOHO_SECRET_KEY = ConfigDAO.getString(PROPERTY_ZOHO_SECRET_KEY, cfg.getProperty(PROPERTY_ZOHO_SECRET_KEY, ""));
			values.put(PROPERTY_ZOHO_SECRET_KEY, ZOHO_SECRET_KEY);
		} catch (DatabaseException e) {
			log.error("** Error reading configuration table **");
		} catch (IOException e) {
			log.error("** Error reading configuration table **");
		}
	}
	
	/**
	 * load mime types
	 */
	public static void loadMimeTypes() {
		try {
			List<MimeType> mimeTypeList = MimeTypeDAO.findAll("mt.id");
			Config.mimeTypes = new MimetypesFileTypeMap();
			
			for (MimeType mt : mimeTypeList) {
				String entry = mt.getName();
				for (String ext : mt.getExtensions()) {
					entry += " " + ext;
				}
				log.debug("loadMimeTypes => Add Entry: {}", entry);
				Config.mimeTypes.addMimeTypes(entry);
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Resource locator helper
	 */
	public static InputStream getResourceAsStream(String resource) throws IOException {
		String stripped = resource.startsWith("/") ? resource.substring(1) : resource;
		InputStream stream = null;
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		
		if (classLoader!=null) {
			stream = classLoader.getResourceAsStream(stripped);
		}
		
		if ( stream == null ) {
			stream = Config.class.getResourceAsStream(resource);
		}
		
		if ( stream == null ) {
			stream = Config.class.getClassLoader().getResourceAsStream(stripped);
		}
		
		if ( stream == null ) {
			throw new IOException(resource + " not found");
		}
		
		return stream;
	}
}
