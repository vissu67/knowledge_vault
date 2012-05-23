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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;

import org.apache.jackrabbit.server.io.ExportContext;
import org.apache.jackrabbit.server.io.IOHandler;
import org.apache.jackrabbit.server.io.IOManager;
import org.apache.jackrabbit.server.io.ImportContext;
import org.apache.jackrabbit.server.io.PropertyExportContext;
import org.apache.jackrabbit.server.io.PropertyHandler;
import org.apache.jackrabbit.server.io.PropertyImportContext;
import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <code>DirListingExportHandler</code> represents a simple export for
 * collections: a human-readable view listing the members.<br>
 * Note: If {@link #exportContent(ExportContext, boolean)} is called the view
 * list child nodes only, without respecting their representation as
 * <code>DavResource</code>s.
 */
public class DirListingExportHandler implements IOHandler, PropertyHandler {
	private static Logger log = LoggerFactory.getLogger(DirListingExportHandler.class);
	private IOManager ioManager;

	public DirListingExportHandler() {
	}

	public DirListingExportHandler(IOManager ioManager) {
		this.ioManager = ioManager;
	}

	@Override
	public boolean canImport(ImportContext context, boolean isFolder) {
		return false;
	}

	@Override
	public boolean canImport(ImportContext context, DavResource resource) {
		return false;
	}

	@Override
	public boolean importContent(ImportContext context, boolean isCollection) throws IOException {
		// can only handle export
		return false;
	}

	@Override
	public boolean importContent(ImportContext context, DavResource resource) throws IOException {
		return false;
	}

	/**
	 * @return true if the specified context is still valid and provides a
	 *         export root and if 'isCollection' is true. False otherwise
	 */
	@Override
	public boolean canExport(ExportContext context, boolean isCollection) {
		log.info("canExport({}, {})", context, isCollection);
		if (context == null || context.isCompleted()) {
			return false;
		}
		return isCollection && context.getExportRoot() != null;
	}

	/**
	 * @return true if the specified context is still valid and provides a
	 *         export root and if the specified resource is a collection. False
	 *         otherwise.
	 */
	@Override
	public boolean canExport(ExportContext context, DavResource resource) {
		log.info("canExport({}, {})", context, resource);
		if (resource == null) {
			return false;
		}
		return canExport(context, resource.isCollection());
	}

	@Override
	public boolean exportContent(ExportContext context, boolean isCollection) throws IOException {
		log.info("exportContent({}, {})", context, isCollection);
		if (!canExport(context, isCollection)) {
			throw new IOException(getName() + ": Cannot export " + context.getExportRoot());
		}

		// properties (content length undefined)
		context.setModificationTime(new Date().getTime());
		context.setContentType("text/html", "UTF-8");
		context.setETag("");

		// data
		if (context.hasStream()) {
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(context.getOutputStream(), "utf8"));
			try {
				Item item = context.getExportRoot();
				Repository rep = item.getSession().getRepository();
				String repName = rep.getDescriptor(Repository.REP_NAME_DESC);
				String repURL = rep.getDescriptor(Repository.REP_VENDOR_URL_DESC);
				String repVersion = rep.getDescriptor(Repository.REP_VERSION_DESC);
				writer.print("<html><head><title>");
				writer.print(repName);
				writer.print(" ");
				writer.print(repVersion);
				writer.print(" ");
				writer.print(item.getPath());
				writer.print("</title></head>");
				writer.print("<body><h2>");
				writer.print(item.getPath());
				writer.print("</h2><ul>");
				writer.print("<li><a href=\"..\">..</a></li>");
				if (item.isNode()) {
					NodeIterator iter = ((Node) item).getNodes();
					while (iter.hasNext()) {
						Node child = iter.nextNode();
						String label = Text.getName(child.getPath());
						writer.print("<li><a href=\"");
						writer.print(Text.escape(label));
						if (child.isNode()) {
							writer.print("/");
						}
						writer.print("\">");
						writer.print(label);
						writer.print("</a></li>");
					}
				}
				writer.print("</ul><hr size=\"1\"><em>Powered by <a href=\"");
				writer.print(repURL);
				writer.print("\">");
				writer.print(repName);
				writer.print("</a> version ");
				writer.print(repVersion);
				writer.print("</em></body></html>");
			} catch (RepositoryException e) {
				// should not occur
				log.debug(e.getMessage());
			}
			writer.close();
		}
		return true;
	}

	@Override
	public boolean exportContent(ExportContext context, DavResource resource) throws IOException {
		log.info("exportContent({}, {})", context, resource);
		if (!canExport(context, resource)) {
			throw new IOException(getName() + ": Cannot export " + context.getExportRoot());
		}

		// properties (content length undefined)
		context.setModificationTime(new Date().getTime());
		context.setContentType("text/html", "UTF-8");
		context.setETag("");

		// data
		if (context.hasStream()) {
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(context.getOutputStream(), "utf8"));
			try {
				Item item = context.getExportRoot();
				Repository rep = item.getSession().getRepository();
				String repName = rep.getDescriptor(Repository.REP_NAME_DESC);
				String repURL = rep.getDescriptor(Repository.REP_VENDOR_URL_DESC);
				String repVersion = rep.getDescriptor(Repository.REP_VERSION_DESC);
				writer.print("<html><head><title>");
				writer.print(repName);
				writer.print(" ");
				writer.print(repVersion);
				writer.print(" ");
				writer.print(resource.getResourcePath());
				writer.print("</title></head>");
				writer.print("<body><h2>");
				writer.print(resource.getResourcePath());
				writer.print("</h2><ul>");
				writer.print("<li><a href=\"..\">..</a></li>");
				DavResourceIterator iter = resource.getMembers();
				while (iter.hasNext()) {
					DavResource child = iter.nextResource();
					String label = Text.getName(child.getResourcePath());
					writer.print("<li><a href=\"");
					writer.print(child.getHref());
					writer.print("\">");
					writer.print(label);
					writer.print("</a></li>");
				}
				writer.print("</ul><hr size=\"1\"><em>Powered by <a href=\"");
				writer.print(repURL);
				writer.print("\">");
				writer.print(repName);
				writer.print("</a> version ");
				writer.print(repVersion);
				writer.print("</em></body></html>");
			} catch (RepositoryException e) {
				// should not occur
				log.debug(e.getMessage());
			}
			writer.close();
		}
		return true;
	}

	@Override
	public IOManager getIOManager() {
		return ioManager;
	}

	@Override
	public void setIOManager(IOManager ioManager) {
		this.ioManager = ioManager;
	}

	@Override
	public String getName() {
		return "DirListing Export";
	}

	@Override
	public boolean canExport(PropertyExportContext exportContext, boolean isCollection) {
		return false;
	}

	@Override
	public boolean exportProperties(PropertyExportContext exportContext, boolean isCollection) throws 
			RepositoryException {
		// export-content facility only... no responsible for propfind.
		throw new RepositoryException(getName() + ": Cannot export properties for context " + exportContext); 
	}

	@Override
	public boolean canImport(PropertyImportContext importContext, boolean isCollection) {
		return false;
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public Map importProperties(PropertyImportContext importContext, boolean isCollection) throws
			RepositoryException {
		// export facilities only -> throw
		throw new RepositoryException(getName() + ": Cannot import properties."); 	
	}
}
