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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.ServletContext;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.AppVersion;

public class WarUtils {
	private static Logger log = LoggerFactory.getLogger(WarUtils.class);
	private static AppVersion appVersion = new AppVersion();
	
	/**
	 * 
	 */
	public static AppVersion getAppVersion() {
		return appVersion;
	}
	
	/**
	 * 
	 */
	public static synchronized void setAppVersion(AppVersion newAppVersion) {
		appVersion = newAppVersion;
	}
	
	/**
	 * 
	 */
	public static synchronized void readAppVersion(ServletContext sc) {
		String appServerHome = sc.getRealPath("/");
		File manifestFile = new File(appServerHome, "META-INF/MANIFEST.MF");
		FileInputStream fis = null;
		
		try {
			fis = new FileInputStream(manifestFile);
			Manifest mf = new Manifest();
			mf.read(fis);
			Attributes atts = mf.getMainAttributes();
			String impVersion = atts.getValue("Implementation-Version");
			String impBuild = atts.getValue("Implementation-Build");
			log.info("Implementation-Version: "+impVersion);
			log.info("Implementation-Build: "+impBuild);
			
			if (impVersion != null && impBuild != null && impVersion.indexOf('.') > 0) {
				String[] version = impVersion.split("\\.");
				appVersion.setMajor(version[0]);
				appVersion.setMinor(version[1]);
				
				if (version.length > 2 && version[2] != null && !version[2].equals("")) {
					appVersion.setMaintenance(version[2]);
				}
				
				appVersion.setBuild(impBuild);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(fis);
		}
	}
}
