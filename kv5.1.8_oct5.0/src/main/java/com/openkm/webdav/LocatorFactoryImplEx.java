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

package com.openkm.webdav;

import org.apache.jackrabbit.webdav.AbstractLocatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.Config;

public class LocatorFactoryImplEx extends AbstractLocatorFactory {
    private static Logger log = LoggerFactory.getLogger(LocatorFactoryImplEx.class);

    /**
     * Create a new factory
     *
     * @param pathPrefix Prefix, that needs to be removed in order to retrieve
     *                   the path of the repository item from a given <code>DavResourceLocator</code>.
     */
    public LocatorFactoryImplEx(String pathPrefix) {
        super(pathPrefix);
    }

    /**
     * @see AbstractLocatorFactory#getRepositoryPath(String, String)
     */
    @Override
    protected String getRepositoryPath(String resourcePath, String wspPath) {
    	log.debug("getRepositoryPath({}, {})", resourcePath, wspPath);
        if (resourcePath == null) {
            return resourcePath;
        }
        
        if (resourcePath.equals(wspPath) || startsWithWorkspace(resourcePath, wspPath)) {
            String repositoryPath = resourcePath.substring(wspPath.length());
            
            if (Config.SYSTEM_WEBDAV_FIX) {
            	repositoryPath = repositoryPath.replace("okm_", "okm:");
            }
            
            String ret = (repositoryPath.length() == 0) ? "/" : repositoryPath;
            log.debug("getRepositoryPath: {}", ret);
            return ret;
        } else {
            throw new IllegalArgumentException("Unexpected format of resource path.");
        }
    }

    /**
     * @see AbstractLocatorFactory#getResourcePath(String, String)
     */
    @Override
    protected String getResourcePath(String repositoryPath, String wspPath) {
    	log.debug("getResourcePath({}, {})", repositoryPath, wspPath);
        if (repositoryPath == null) {
            throw new IllegalArgumentException("Cannot build resource path from 'null' repository path");
        }
        
        if (Config.SYSTEM_WEBDAV_FIX) {
        	repositoryPath = repositoryPath.replace("okm:", "okm_");
        }
        
        String ret = (startsWithWorkspace(repositoryPath, wspPath)) ? repositoryPath : wspPath + repositoryPath;
        log.debug("getResourcePath: {}", ret);
        return ret;
    }

    private boolean startsWithWorkspace(String repositoryPath, String wspPath) {
        if (wspPath == null) {
            return true;
        } else {
            return repositoryPath.startsWith(wspPath + "/");
        }
    }
}
