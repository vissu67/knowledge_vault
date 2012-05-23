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

package com.openkm.servlet;

import java.io.File;
import java.util.Calendar;
import java.util.Timer;

import javax.servlet.GenericServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.SessionImpl;
import org.jbpm.JbpmContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.openkm.cache.UserDocumentKeywordsManager;
import com.openkm.cache.UserItemsManager;
import com.openkm.core.Config;
import com.openkm.core.Cron;
import com.openkm.core.DataStoreGarbageCollector;
import com.openkm.core.DatabaseException;
import com.openkm.core.RepositoryInfo;
import com.openkm.core.UpdateInfo;
import com.openkm.core.UserMailImporter;
import com.openkm.core.Watchdog;
import com.openkm.dao.HibernateUtil;
import com.openkm.extension.core.ExtensionManager;
import com.openkm.kea.RDFREpository;
import com.openkm.module.direct.DirectRepositoryModule;
import com.openkm.util.DocConverter;
import com.openkm.util.ExecutionUtils;
import com.openkm.util.JBPMUtils;
import com.openkm.util.UserActivity;
import com.openkm.util.WarUtils;

/**
 * Servlet Startup Class
 */
public class RepositoryStartupServlet extends HttpServlet {
	private static Logger log = LoggerFactory.getLogger(RepositoryStartupServlet.class);
	private static final long serialVersionUID = 1L;
	private static Timer dsgcTimer;
	private static Timer wdTimer;
	private static Timer riTimer;
	private static Timer uiTimer;
	private static Timer umiTimer;
	private static Timer cronTimer;
	private static Watchdog wd;
	private static Cron cron;
	private static UpdateInfo ui;
	private static RepositoryInfo ri;
	private static UserMailImporter umi;
	private static DataStoreGarbageCollector dsgc;
	private static boolean hasConfiguredDataStore;
	private static boolean running = false;
	
    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext sc = getServletContext();
        
        // Read config file
        Config.load(sc.getContextPath().substring(1));
        
        // Call only once during initialization time of your application
        // @see http://issues.openkm.com/view.php?id=1577
        SLF4JBridgeHandler.install();
        
        // Get OpenKM version
        WarUtils.readAppVersion(sc);
        log.info("*** Application version: "+WarUtils.getAppVersion()+" ***");
        
        // Initialize DXF cache folder
        File dxfCacheFolder = new File(Config.CACHE_DXF);
        if (!dxfCacheFolder.exists()) dxfCacheFolder.mkdirs();
        
        // Initialize PDF cache folder
        File pdfCacheFolder = new File(Config.CACHE_PDF);
        if (!pdfCacheFolder.exists()) pdfCacheFolder.mkdirs();
        
        // Initialize SWF cache folder
        File previewCacheFolder = new File(Config.CACHE_SWF);
        if (!previewCacheFolder.exists()) previewCacheFolder.mkdirs();
        
        // Initialize chroot folder
        if (Config.SYSTEM_MULTIPLE_INSTANCES) {
        	File chrootFolder = new File(Config.INSTANCE_CHROOT_PATH);
        	if (!chrootFolder.exists()) chrootFolder.mkdirs();
        }
        
        // Invoke start
        start();
        
        // Activity log
		UserActivity.log(Config.SYSTEM_USER, "MISC_OPENKM_START", null, null);
    }
    
	@Override
    public void destroy() {
        super.destroy();
        
        // Invoke stop
        stop(this);
        
        // Activity log
		UserActivity.log(Config.SYSTEM_USER, "MISC_OPENKM_STOP", null, null);
    }
	
	/**
	 * Start OpenKM and possible repository and database initialization
	 */
	public static synchronized void start() throws ServletException {
		if (running) {
			throw new IllegalStateException("OpenKM already started");
		}
		
        try {
        	log.info("*** Repository initializing... ***");
        	DirectRepositoryModule.initialize();
        	log.info("*** Repository initialized ***");
        } catch (Exception e) {
        	throw new ServletException(e.getMessage(), e);
        }
        
        if (Config.USER_ITEM_CACHE) {
        	// Deserialize
        	try {
        		log.info("*** Cache deserialization ***");
        		UserItemsManager.deserialize();
        		UserDocumentKeywordsManager.deserialize();
        	} catch (DatabaseException e) {
        		log.warn(e.getMessage(), e);
        	}
        }
        
        log.info("*** User database initialized ***");
        
        // Test for datastore
		SessionImpl si = (SessionImpl) DirectRepositoryModule.getSystemSession();
        
		if (((RepositoryImpl)si.getRepository()).getDataStore() == null) {
        	hasConfiguredDataStore = false;
        } else {
        	hasConfiguredDataStore = true;
        }
        
        // Create timers
		uiTimer = new Timer();
		wdTimer = new Timer();
		cronTimer = new Timer();
		riTimer = new Timer();
		umiTimer = new Timer();
		dsgcTimer = new Timer();
        
        // Workflow
        log.info("*** Initializing workflow engine... ***");
        JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
        jbpmContext.setSessionFactory(HibernateUtil.getSessionFactory());
        jbpmContext.getGraphSession();
        jbpmContext.getJbpmConfiguration().getJobExecutor().start(); // startJobExecutor();
        jbpmContext.close();
        
        // Mime types
        log.info("*** Initializing MIME types... ***");
        Config.loadMimeTypes();
                
        if (Config.UPDATE_INFO) {
        	 log.info("*** Activating update info ***");
        	 ui = new UpdateInfo();
        	 uiTimer.schedule(ui, 1000, 24*60*60*1000); // First in 1 seg, next each 24 hours
        }
		
        log.info("*** Activating watchdog ***");
        wd = new Watchdog();
        wdTimer.schedule(wd, 60*1000, 5*60*1000); // First in 1 min, next each 5 mins
        
        log.info("*** Activating cron ***");
        cron = new Cron();
        Calendar calCron = Calendar.getInstance();
        calCron.add(Calendar.MINUTE, 1);
        calCron.set(Calendar.SECOND, 0);
        calCron.set(Calendar.MILLISECOND, 0);
        // Round begin to next minute, 0 seconds, 0 miliseconds
        cronTimer.scheduleAtFixedRate(cron, calCron.getTime(), 60*1000); // First in 1 min, next each 1 min
        
        log.info("*** Activating repository info ***");
        ri = new RepositoryInfo();
        riTimer.schedule(ri, 60*1000, Config.SCHEDULE_REPOSITORY_INFO); // First in 1 min, next each X minutes
        
        if (Config.SCHEDULE_MAIL_IMPORTER > 0) {
        	log.info("*** Activating user mail importer ***");
        	umi = new UserMailImporter();
        	umiTimer.schedule(umi, 5*60*1000, Config.SCHEDULE_MAIL_IMPORTER); // First in 5 mins, next each X minutes
        } else {
        	log.info("*** User mail importer disabled ***");
        }
        
        if (hasConfiguredDataStore) {
        	log.info("*** Activating datastore garbage collection ***");
        	dsgc = new DataStoreGarbageCollector();
        	Calendar calGc = Calendar.getInstance();
        	calGc.add(Calendar.DAY_OF_YEAR, 1);
        	calGc.set(Calendar.HOUR_OF_DAY, 0);
        	calGc.set(Calendar.MINUTE, 0);
        	calGc.set(Calendar.SECOND, 0);
        	calGc.set(Calendar.MILLISECOND, 0);
        	dsgcTimer.scheduleAtFixedRate(dsgc, calGc.getTime(), 24*60*60*1000); // First tomorrow at 00:00, next each 24 hours
        }
        
        try {
        	log.info("*** Activating thesaurus repository ***");
        	RDFREpository.getInstance();
        } catch (Exception e) {
        	log.warn(e.getMessage(), e);
        }
        
        try {
        	if (!Config.SYSTEM_OPENOFFICE_PATH.equals("")) {
        		log.info("*** Start OpenOffice manager ***");
        		DocConverter.getInstance().start();
        	} else if (!Config.SYSTEM_OPENOFFICE_SERVER.equals("")) {
        		log.info("*** Using OpenOffice conversion server ***");
        	} else {
        		log.warn("*** No OpenOffice manager nor server configured ***");
        	}
        } catch (Throwable e) {
        	log.warn(e.getMessage(), e);
        }
        
        // Initialize plugin framework
        ExtensionManager.getInstance();
        
        try {
        	log.info("*** Ejecute start script ***");
        	File script = new File(Config.HOME_DIR + File.separatorChar + Config.START_SCRIPT);
        	ExecutionUtils.runScript(script);
        	File jar = new File(Config.HOME_DIR + File.separatorChar + Config.START_JAR);
        	ExecutionUtils.runJar(jar);
        } catch (Throwable e) {
        	log.warn(e.getMessage(), e);
        }
        
        // OpenKM is started
        running = true;
	}
	
	/**
	 * Close OpenKM and free resources
	 */
	public static synchronized void stop(GenericServlet gs) {
		if (!running) {
			throw new IllegalStateException("OpenKM not started");
		}
		
		// Shutdown plugin framework
		ExtensionManager.getInstance().shutdown();
		
		try {
        	if (!Config.SYSTEM_OPENOFFICE_PATH.equals("")) {
        		if (log == null && gs != null) gs.log("*** Shutting down OpenOffice manager ***");
        		else log.info("*** Shutting down OpenOffice manager ***");
        		DocConverter.getInstance().stop();
        	}
        } catch (Throwable e) {
        	log.warn(e.getMessage(), e);
        }
        
        if (hasConfiguredDataStore) {
        	if (log == null && gs != null) gs.log("*** Shutting down datastore garbage collection... ***");
        	else log.info("*** Shutting down datastore garbage collection... ***");
        	dsgc.cancel();
        }
        
        if (Config.SCHEDULE_MAIL_IMPORTER > 0) {
        	if (log == null && gs != null) gs.log("*** Shutting down user mail importer ***");
        	else log.info("*** Shutting down user mail importer ***");
        	umi.cancel();
        }
        
        if (log == null && gs != null) gs.log("*** Shutting down repository info... ***");
        else log.info("*** Shutting down repository info... ***");
        ri.cancel();
        
        if (log == null && gs != null) gs.log("*** Shutting down cron... ***");
        else log.info("*** Shutting down cron... ***");
        cron.cancel();
        
        if (log == null && gs != null) gs.log("*** Shutting down watchdog... ***");
        else log.info("*** Shutting down watchdog... ***");
        wd.cancel();
        
        if (Config.UPDATE_INFO) {
        	if (log == null && gs != null) gs.log("*** Shutting down update info... ***");
        	else log.info("*** Shutting down update info... ***");
        	ui.cancel();
        }
        
        // Cancel timers
        dsgcTimer.cancel();
        umiTimer.cancel();
        riTimer.cancel();
        cronTimer.cancel();
        wdTimer.cancel();
        uiTimer.cancel();
		
        if (log == null && gs != null) gs.log("*** Shutting down workflow engine... ***");
        else log.info("*** Shutting down workflow engine... ***");
        JbpmContext jbpmContext = JBPMUtils.getConfig().createJbpmContext();
        jbpmContext.getJbpmConfiguration().getJobExecutor().stop();
        jbpmContext.close();
        
        if (log == null && gs != null) gs.log("*** Shutting down repository... ***");
        else log.info("*** Shutting down repository...");
        
        if (Config.USER_ITEM_CACHE) {
        	// Serialize
        	try {
        		log.info("*** Cache serialization ***");
        		UserItemsManager.serialize();
        		UserDocumentKeywordsManager.serialize();
        	} catch (DatabaseException e) {
        		log.warn(e.getMessage(), e);
        	}
        }
        
        try {
        	// Preserve system user config
        	DirectRepositoryModule.shutdown();
        } catch (Exception e) {
        	log.error(e.getMessage(), e);
        }
        
        if (log == null && gs != null) gs.log("*** Repository shutted down ***");
        else log.info("*** Repository shutted down ***");
        
        try {
        	if (log == null && gs != null) gs.log("*** Ejecute stop script ***");
        	else log.info("*** Ejecute stop script ***");
        	File script = new File(Config.HOME_DIR + File.separatorChar + Config.STOP_SCRIPT);
        	ExecutionUtils.runScript(script);
        	File jar = new File(Config.HOME_DIR + File.separatorChar + Config.STOP_JAR);
        	ExecutionUtils.runJar(jar);
        } catch (Throwable e) {
        	log.warn(e.getMessage(), e);
        }
        
        // OpenKM is stopped
        running = false;
	}
}
