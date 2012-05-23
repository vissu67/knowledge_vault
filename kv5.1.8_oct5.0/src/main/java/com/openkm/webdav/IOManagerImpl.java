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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.jackrabbit.server.io.DefaultIOListener;
import org.apache.jackrabbit.server.io.ExportContext;
import org.apache.jackrabbit.server.io.IOHandler;
import org.apache.jackrabbit.server.io.IOListener;
import org.apache.jackrabbit.server.io.IOManager;
import org.apache.jackrabbit.server.io.ImportContext;
import org.apache.jackrabbit.webdav.DavResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <code>IOManagerImpl</code> represents the most simple <code>IOManager</code>
 * implementation that provides a default constructor and does define any
 * <code>IOHandler</code>s.
 */
public class IOManagerImpl implements IOManager {
    private static Logger log = LoggerFactory.getLogger(IOManagerImpl.class);
    private final List<IOHandler> ioHandlers = new ArrayList<IOHandler>();

    /**
     * Create a new <code>IOManager</code>.
     * Note, that this manager does not define any <code>IOHandler</code>s by
     * default. Use {@link #addIOHandler(IOHandler)} in order to populate the
     * internal list of handlers that are called for <code>importContent</code> and
     * <code>exportContent</code>.
     */
    public IOManagerImpl() {
    }

    /**
     * @see IOManager#addIOHandler(IOHandler)
     */
    @Override
    public void addIOHandler(IOHandler ioHandler) {
        if (ioHandler == null) {
            throw new IllegalArgumentException("'null' is not a valid IOHandler.");
        }
        ioHandler.setIOManager(this);
        ioHandlers.add(ioHandler);
    }

    /**
     * @see IOManager#getIOHandlers()
     */
    @Override
    public IOHandler[] getIOHandlers() {
        return (IOHandler[]) ioHandlers.toArray(new IOHandler[ioHandlers.size()]);
    }

    /**
     * @see IOManager#importContent(ImportContext, boolean)
     */
    @Override
    public boolean importContent(ImportContext context, boolean isCollection) throws IOException {
        boolean success = false;
        if (context != null) {
            IOListener ioListener = context.getIOListener();
            if (ioListener == null) {
                ioListener = new DefaultIOListener(log);
            }
            IOHandler[] ioHandlers = getIOHandlers();
            for (int i = 0; i < ioHandlers.length && !success; i++) {
                IOHandler ioh = ioHandlers[i];
                if (ioh.canImport(context, isCollection)) {
                    ioListener.onBegin(ioh, context);
                    success = ioh.importContent(context, isCollection);
                    ioListener.onEnd(ioh, context, success);
                }
            }
            context.informCompleted(success);
        }
        return success;
    }

    /**
     * @see IOManager#importContent(ImportContext, DavResource)
     */
    @Override
    public boolean importContent(ImportContext context, DavResource resource) throws IOException {
        boolean success = false;
        if (context != null && resource != null) {
            IOListener ioListener = context.getIOListener();
            if (ioListener == null) {
                ioListener = new DefaultIOListener(log);
            }
            IOHandler[] ioHandlers = getIOHandlers();
            for (int i = 0; i < ioHandlers.length && !success; i++) {
                IOHandler ioh = ioHandlers[i];
                if (ioh.canImport(context, resource)) {
                    ioListener.onBegin(ioh, context);
                    success = ioh.importContent(context, resource);
                    ioListener.onEnd(ioh, context, success);
                }
            }
            context.informCompleted(success);
        }
        return success;
    }

    /**
     * @see IOManager#exportContent(ExportContext, boolean)
     */
    @Override
    public boolean exportContent(ExportContext context, boolean isCollection) throws IOException {
    	log.debug("exportContent({}, {})", context, isCollection);
        boolean success = false;
        if (context != null) {
            IOListener ioListener = context.getIOListener();
            if (ioListener == null) {
                ioListener = new DefaultIOListener(log);
            }
            IOHandler[] ioHandlers = getIOHandlers();
            for (int i = 0; i < ioHandlers.length && !success; i++) {
                IOHandler ioh = ioHandlers[i];
                if (ioh.canExport(context, isCollection)) {
                    ioListener.onBegin(ioh, context);
                    success = ioh.exportContent(context, isCollection);
                    ioListener.onEnd(ioh, context, success);
                }
            }
            context.informCompleted(success);
        }
        log.debug("exportContent: {}", success);
        return success;
    }

    /**
     * @see IOManager#exportContent(ExportContext, DavResource)
     */
    @Override
    public boolean exportContent(ExportContext context, DavResource resource) throws IOException {
    	log.debug("exportContent({}, {})", context, resource);
        boolean success = false;
        if (context != null && resource != null) {
            IOListener ioListener = context.getIOListener();
            if (ioListener == null) {
                ioListener = new DefaultIOListener(log);
            }
            IOHandler[] ioHandlers = getIOHandlers();
            for (int i = 0; i < ioHandlers.length && !success; i++) {
                IOHandler ioh = ioHandlers[i];
                if (ioh.canExport(context, resource)) {
                    ioListener.onBegin(ioh, context);
                    success = ioh.exportContent(context, resource);
                    ioListener.onEnd(ioh, context, success);
                }
                log.debug("-> {} --> {}", ioh.getName(), success);
            }
            context.informCompleted(success);
        }
        log.debug("exportContent: {}", success);
        return success;
    }
}
