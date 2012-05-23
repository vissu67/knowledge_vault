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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.jcr.AccessDeniedException;
import javax.jcr.Item;
import javax.jcr.ItemNotFoundException;
import javax.jcr.NamespaceException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.nodetype.PropertyDefinition;

import org.apache.jackrabbit.JcrConstants;
import org.apache.jackrabbit.server.io.ExportContext;
import org.apache.jackrabbit.server.io.IOHandler;
import org.apache.jackrabbit.server.io.IOManager;
import org.apache.jackrabbit.server.io.IOUtil;
import org.apache.jackrabbit.server.io.ImportContext;
import org.apache.jackrabbit.server.io.PropertyExportContext;
import org.apache.jackrabbit.server.io.PropertyHandler;
import org.apache.jackrabbit.server.io.PropertyImportContext;
import org.apache.jackrabbit.util.ISO9075;
import org.apache.jackrabbit.util.Text;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Document;
import com.openkm.bean.Folder;
import com.openkm.bean.Permission;
import com.openkm.cache.UserItemsManager;
import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.dao.MimeTypeDAO;
import com.openkm.extractor.RegisteredExtractors;
import com.openkm.jcr.JCRUtils;

public class DefaultHandler implements IOHandler, PropertyHandler {
	private static Logger log = LoggerFactory.getLogger(DefaultHandler.class);

    //private String collectionNodetype = JcrConstants.NT_FOLDER;
	private String collectionNodetype = Folder.TYPE;
    //private String defaultNodetype = JcrConstants.NT_FILE;
	private String defaultNodetype = Document.TYPE;
    /* IMPORTANT NOTE: for webDAV compliancy the default nodetype of the content
       node has been changed from nt:resource to nt:unstructured. */
    //private String contentNodetype = JcrConstants.NT_UNSTRUCTURED;
	private String contentNodetype = Document.CONTENT_TYPE;

    private IOManager ioManager;

    /**
     * Creates a new <code>DefaultHandler</code> with default nodetype definitions
     * and without setting the IOManager.
     *
     * @see OKMHandler#setIOManager(IOManager)
     */
    public DefaultHandler() {
    	log.debug("DefaultHandler()");
    }

    /**
     * Creates a new <code>DefaultHandler</code> with default nodetype definitions:<br>
     * <ul>
     * <li>Nodetype for Collection: {@link JcrConstants#NT_FOLDER nt:folder}</li>
     * <li>Nodetype for Non-Collection: {@link JcrConstants#NT_FILE nt:file}</li>
     * <li>Nodetype for Non-Collection content: {@link JcrConstants#NT_RESOURCE nt:resource}</li>
     * </ul>
     *
     * @param ioManager
     */
    public DefaultHandler(IOManager ioManager) {
    	log.debug("DefaultHandler({})", ioManager);
        this.ioManager = ioManager;
    }

    /**
     * Creates a new <code>DefaultHandler</code>. Please note that the specified
     * nodetypes must match the definitions of the defaults.
     *
     * @param ioManager
     * @param collectionNodetype
     * @param defaultNodetype
     * @param contentNodetype
     */
    public DefaultHandler(IOManager ioManager, String collectionNodetype, String defaultNodetype, String contentNodetype) {
    	log.debug("DefaultHandler({}, {}, {}, {})", new Object[] { ioManager, collectionNodetype, defaultNodetype, contentNodetype });
    	this.ioManager = ioManager;
        this.collectionNodetype = collectionNodetype;
        this.defaultNodetype = defaultNodetype;
        this.contentNodetype = contentNodetype;
    }

    /**
     * @see OKMHandler#getIOManager()
     */
    public IOManager getIOManager() {
        return ioManager;
    }

    /**
     * @see OKMHandler#setIOManager(IOManager)
     */
    public void setIOManager(IOManager ioManager) {
        this.ioManager = ioManager;
    }

    /**
     * @see OKMHandler#getName()
     */
    public String getName() {
        return getClass().getName();
    }

    /**
     * @see OKMHandler#canImport(ImportContext, boolean)
     */
    public boolean canImport(ImportContext context, boolean isCollection) {
    	log.debug("canImport({}, {})", context, isCollection);
        if (context == null || context.isCompleted()) {
            return false;
        }
        Item contextItem = context.getImportRoot();
        
        if (!isCollection) {
        	// Check file restrictions (Don't check folders)
        	String mimeType = Config.mimeTypes.getContentType(context.getSystemId().toLowerCase());
        	
        	log.debug("File: {}", context.getSystemId());
        	log.debug("MimeType: {}", mimeType);
        	log.debug("Size: {}", context.getContentLength());
        	
        	// Restrict for MIME
        	try {
        		if (Config.RESTRICT_FILE_MIME && MimeTypeDAO.findByName(mimeType) == null) {
        			return false;
        		}
        	} catch (DatabaseException e) {
        		log.error(e.getMessage(), e);
        		return false;
        	}
        	
       		// Restrict for extension
       		StringTokenizer st = new StringTokenizer(Config.RESTRICT_FILE_EXTENSION, ",");
       		
       		while (st.hasMoreTokens()) {
       			String wc = st.nextToken();
       			String re = wildcard2regexp(wc);
       			
       			if (Pattern.matches(re, context.getSystemId())) {
        			log.debug("Filename BAD -> {} ({})", re, wc);
        			return false;
        		} else {
        			log.debug("Filename GOOD -> {} ({})", re, wc);
        		}
        	}
        		
        	// Restrict for size
        	if (context.getContentLength() > Config.MAX_FILE_SIZE) {
        		return false;
        	}
        }
        
        return contextItem != null && contextItem.isNode() && context.getSystemId() != null;
    }
    
	/**
	 * @param wildcard
	 * @return
	 */
	private String wildcard2regexp(String wildcard) {
		StringBuffer sb = new StringBuffer("^");
		for (int i = 0; i < wildcard.length(); i++) {
			char c = wildcard.charAt(i);
			switch (c) {
				case '.':
					sb.append("\\.");
					break;

				case '*':
					sb.append(".*");
					break;
				
				case '?':
					sb.append(".");
					break;
				
				default:
					sb.append(c);
					break;
			}
		}
		return sb.toString();
	}

    /**
     * @see OKMHandler#canImport(ImportContext, DavResource)
     */
    public boolean canImport(ImportContext context, DavResource resource) {
    	//log.debug("canImport("+context+", "+resource+")");
        if (resource == null) {
            return false;
        }
        return canImport(context, resource.isCollection());
    }

    /**
     * @see OKMHandler#importContent(ImportContext, boolean)
     */
    public boolean importContent(ImportContext context, boolean isCollection) throws IOException {
    	log.debug("importContent({}, {})", context, isCollection);
        if (!canImport(context, isCollection)) {
            throw new IOException(getName() + ": Cannot import " + context.getSystemId());
        }

        boolean success = false;
        try {
            Node contentNode = getContentNode(context, isCollection);
            log.info("contentNode: {}", contentNode.getPath());
            
            if (contentNode.isNodeType("mix:versionable")) {
            	log.debug("CHECKOUT");
            	contentNode.checkout();
            }
            
            success = importProperties(context, isCollection, contentNode);
            
            if (success) {
            	success = importData(context, isCollection, contentNode);
            }
            
            if (contentNode.isNodeType(Document.CONTENT_TYPE)) {
            	contentNode.getParent().getParent().save();
            	
        		// Check document filters
    			//DocumentUtils.checkFilters(session, mainNode, mimeType);
    		}
            
            if (contentNode.isNodeType("mix:versionable")) {
            	log.debug("CHECKIN");
            	// Esta línea vale millones!! Resuelve la incidencia del isCkechedOut.
        		// Por lo visto un nuevo nodo se añade con el isCheckedOut a true :/
            	javax.jcr.version.Version ver = contentNode.checkin();
            	
                if (Config.USER_ITEM_CACHE) {
    				// Update user items
                	String user = contentNode.getSession().getUserID();
    				long size = contentNode.getProperty(Document.SIZE).getLong();
    				UserItemsManager.incSize(user, size);
    				
    				if (ver.getName().equals("1.0")) {
    					UserItemsManager.incDocuments(user, 1);
    				}
    			}
            }
            
            // Remove pdf & preview from cache
            Node documentNode = contentNode.getParent();
            log.info("Delete: {}", Config.CACHE_DXF + File.separator + documentNode.getUUID() + ".dxf");
            new File(Config.CACHE_DXF + File.separator + documentNode.getUUID() + ".dxf").delete();
            log.info("Delete: {}", Config.CACHE_PDF + File.separator + documentNode.getUUID() + ".pdf");
            new File(Config.CACHE_PDF + File.separator + documentNode.getUUID() + ".pdf").delete();
            log.info("Delete: {}", Config.CACHE_SWF + File.separator + documentNode.getUUID() + ".swf");
			new File(Config.CACHE_SWF + File.separator + documentNode.getUUID() + ".swf").delete();
        } catch (RepositoryException e) {
            success = false;
            throw new IOException(e.getMessage());
        } finally {
            // revert any changes made in case the import failed.
            if (!success) {
                try {
                    context.getImportRoot().refresh(false);
                } catch (RepositoryException e) {
                    throw new IOException(e.getMessage());
                }
            }
        }
        return success;
    }

    /**
     * @see OKMHandler#importContent(ImportContext, DavResource)
     */
    public boolean importContent(ImportContext context, DavResource resource) throws IOException {
    	log.debug("importContent({}, {})", context, resource);
        if (!canImport(context, resource)) {
            throw new IOException(getName() + ": Cannot import " + context.getSystemId());
        }
        return importContent(context, resource.isCollection());
    }

    /**
     * Imports the data present on the import context to the specified content
     * node.
     *
     * @param context
     * @param isCollection
     * @param contentNode
     * @return
     * @throws IOException
     */
    protected boolean importData(ImportContext context, boolean isCollection, Node contentNode) throws IOException, RepositoryException {
    	log.debug("importData({}, {}, {})", new Object[] {context, isCollection, contentNode });
        InputStream is = context.getInputStream();
        if (is != null) {
            // NOTE: with the default folder-nodetype (nt:folder) no inputstream
            // is allowed. setting the property would therefore fail.
            if (isCollection) {
                return false;
            }
            
            try {
                contentNode.setProperty(JcrConstants.JCR_DATA, is);
                
                if (Config.EXPERIMENTAL_TEXT_EXTRACTION) {
       				String mimeType = contentNode.getProperty(JcrConstants.JCR_MIMETYPE).getString();
    				RegisteredExtractors.index(contentNode.getParent(), contentNode, mimeType);
        		}
            } finally {
                is.close();
            }
        }
        // success if no data to import.
        return true;
    }

    /**
     * Imports the properties present on the specified context to the content
     * node.
     */
    protected boolean importProperties(ImportContext context, boolean isCollection, Node contentNode) {
    	log.debug("importProperties({}, {}, {})", new Object[] { context, isCollection, contentNode });
    	String mimeType = null;
    	
        try {
            // set mimeType property upon resource creation but don't modify
            // it on a subsequent PUT. In contrast to a PROPPATCH request, which
            // is handled by  #importProperties(PropertyContext, boolean)}
            if (!contentNode.hasProperty(JcrConstants.JCR_MIMETYPE)) {
            	//contentNode.setProperty(JcrConstants.JCR_MIMETYPE, context.getMimeType());
            	mimeType = Config.mimeTypes.getContentType(context.getSystemId().toLowerCase());
        		contentNode.setProperty(JcrConstants.JCR_MIMETYPE, mimeType);
            }
        } catch (RepositoryException e) {
            // ignore: property may not be present on the node
        }
        try {
            // set encoding property upon resource creation but don't modify
            // it on a subsequent PUT. In contrast to a PROPPATCH request, which
            // is handled by  #importProperties(PropertyContext, boolean)}
            if (!contentNode.hasProperty(JcrConstants.JCR_ENCODING)) {
                contentNode.setProperty(JcrConstants.JCR_ENCODING, context.getEncoding());
            }
        } catch (RepositoryException e) {
            // ignore: property may not be present on the node
        }
        setLastModified(contentNode, context.getModificationTime());
        
        // OpenKM
        try {
        	Session session = contentNode.getSession();
        	Node parentNode = null;
        	Node mainNode = null;
        	
        	if (contentNode.isNodeType(Folder.TYPE)) {
        		log.debug("Folder node type");
        		Node folderNode = contentNode;
        		parentNode = folderNode.getParent();
        		
        		// Basic folder properties
        		folderNode.setProperty(Folder.AUTHOR, session.getUserID());
        		folderNode.setProperty(Folder.NAME, folderNode.getName());
        		
        		// Set main node
        		mainNode = folderNode;
        	} else if (contentNode.isNodeType(Document.CONTENT_TYPE)) {
        		log.debug("Document node type");
        		Node documentNode = contentNode.getParent();
        		parentNode = documentNode.getParent();
        		
        		// Basic document properties
        		documentNode.setProperty(com.openkm.bean.Property.KEYWORDS, new String[]{});
        		documentNode.setProperty(com.openkm.bean.Property.CATEGORIES, new String[]{}, PropertyType.REFERENCE);
        		documentNode.setProperty(Document.AUTHOR, session.getUserID());
        		documentNode.setProperty(Document.NAME, documentNode.getName());
        		
        		// Basic content properties
        		contentNode.setProperty(Document.SIZE, context.getContentLength());
        		contentNode.setProperty(Document.AUTHOR, session.getUserID());
        		contentNode.setProperty(Document.VERSION_COMMENT, "Edited using WebDAV");
        		        		
        		// Set main node
        		mainNode = documentNode;
        	}
        	
    		// Get parent node auth info
    		Value[] usersReadParent = parentNode.getProperty(Permission.USERS_READ).getValues();
    		String[] usersRead = JCRUtils.usrValue2String(usersReadParent, session.getUserID()); 
    		Value[] usersWriteParent = parentNode.getProperty(Permission.USERS_WRITE).getValues();
    		String[] usersWrite = JCRUtils.usrValue2String(usersWriteParent, session.getUserID()); 
    		Value[] usersDeleteParent = parentNode.getProperty(Permission.USERS_DELETE).getValues();
    		String[] usersDelete = JCRUtils.usrValue2String(usersDeleteParent, session.getUserID());
    		Value[] usersSecurityParent = parentNode.getProperty(Permission.USERS_SECURITY).getValues();
    		String[] usersSecurity = JCRUtils.usrValue2String(usersSecurityParent, session.getUserID());
    		
    		Value[] rolesReadParent = parentNode.getProperty(Permission.ROLES_READ).getValues();
    		String[] rolesRead = JCRUtils.rolValue2String(rolesReadParent); 
    		Value[] rolesWriteParent = parentNode.getProperty(Permission.ROLES_WRITE).getValues();
    		String[] rolesWrite = JCRUtils.rolValue2String(rolesWriteParent); 
    		Value[] rolesDeleteParent = parentNode.getProperty(Permission.ROLES_DELETE).getValues();
    		String[] rolesDelete = JCRUtils.rolValue2String(rolesDeleteParent);
    		Value[] rolesSecurityParent = parentNode.getProperty(Permission.ROLES_SECURITY).getValues();
    		String[] rolesSecurity = JCRUtils.rolValue2String(rolesSecurityParent);
    		
    		// Set auth info
    		mainNode.setProperty(Permission.USERS_READ, usersRead);
    		mainNode.setProperty(Permission.USERS_WRITE, usersWrite);
    		mainNode.setProperty(Permission.USERS_DELETE, usersDelete);
    		mainNode.setProperty(Permission.USERS_SECURITY, usersSecurity);
    		mainNode.setProperty(Permission.ROLES_READ, rolesRead);
    		mainNode.setProperty(Permission.ROLES_WRITE, rolesWrite);
    		mainNode.setProperty(Permission.ROLES_DELETE, rolesDelete);
    		mainNode.setProperty(Permission.ROLES_SECURITY, rolesSecurity);
        } catch (ItemNotFoundException e) {
			e.printStackTrace();
		} catch (AccessDeniedException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
        
        return true;
    }

    /**
     * Retrieves/creates the node that will be used to import properties and
     * data. In case of a non-collection this includes and additional content node
     * to be created beside the 'file' node.<br>
     * Please note: If the jcr:content node already exists and contains child
     * nodes, those will be removed in order to make sure, that the import
     * really replaces the existing content of the file-node.
     */
    protected Node getContentNode(ImportContext context, boolean isCollection) throws RepositoryException {
        log.debug("getContentNode({}, {})", context, isCollection);
    	Node parentNode = (Node)context.getImportRoot();
        String name = context.getSystemId();

        if (parentNode.hasNode(name)) {
            parentNode = parentNode.getNode(name);
        } else {
            String ntName = (isCollection) ? getCollectionNodeType() : getNodeType();
            parentNode = parentNode.addNode(name, ntName);
        }
        Node contentNode = null;
        if (isCollection) {
            contentNode = parentNode;
        } else {
            if (parentNode.hasNode(Document.CONTENT)) {
                contentNode = parentNode.getNode(Document.CONTENT);
                // check if nodetype is compatible (might be update of an existing file)
                if (contentNode.isNodeType(getContentNodeType())) {
                    // remove all entries in the jcr:content since replacing content
                    // includes properties (DefaultHandler) and nodes (e.g. ZipHandler)
                    if (contentNode.hasNodes()) {
                        NodeIterator it = contentNode.getNodes();
                        while (it.hasNext()) {
                            it.nextNode().remove();
                        }
                    }
                } else {
                    contentNode.remove();
                    contentNode = null;
                }
            }
            if (contentNode == null) {
                contentNode = parentNode.addNode(Document.CONTENT, getContentNodeType());
            }
        }
        return contentNode;
    }

    /**
     * Returns true if the export root is a node and if it contains a child node
     * with name {@link JcrConstants#JCR_CONTENT jcr:content} in case this
     * export is not intended for a collection.
     *
     * @return true if the export root is a node. If the specified boolean paramter
     * is false (not a collection export) the given export root must contain a
     * child node with name {@link JcrConstants#JCR_CONTENT jcr:content}.
     *
     * @see OKMHandler#canExport(ExportContext, boolean)
     */
    @Override
    public boolean canExport(ExportContext context, boolean isCollection) {
    	log.debug("canExport(ExportContext:{}, {})", context, isCollection);
        if (context == null || context.isCompleted()) {
            return false;
        }
        Item exportRoot = context.getExportRoot();
        boolean success = exportRoot != null && exportRoot.isNode();
        if (success && !isCollection) {
            try {
                Node n = ((Node)exportRoot);
                
                log.debug("Path: {}", n.getPath());
                //for (NodeIterator nit = n.getNodes(); nit.hasNext(); ) {
                	//log.debug("### "+nit.nextNode().getPath());
                //}
                
                success = n.hasNode(Document.CONTENT);
            } catch (RepositoryException e) {
                // should never occur.
                success = false;
            }
        }
        return success;
    }

    /**
     * @see OKMHandler#canExport(ExportContext, DavResource)
     */
    @Override
    public boolean canExport(ExportContext context, DavResource resource) {
    	log.debug("canExport(ExportContext:{}, DavResource:{})", context, resource);
        if (resource == null) {
            return false;
        }
        return canExport(context, resource.isCollection());
    }

    /**
     * Retrieves the content node that will be used for exporting properties and
     * data and calls the corresponding methods.
     *
     * @param context
     * @param isCollection
     * @see #exportProperties(ExportContext, boolean, Node)
     * @see #exportData(ExportContext, boolean, Node)
     */
    @Override
    public boolean exportContent(ExportContext context, boolean isCollection) throws IOException {
    	log.debug("exportContent({}, {})", context, isCollection);
        if (!canExport(context, isCollection)) {
            throw new IOException(getName() + ": Cannot export " + context.getExportRoot());
        }
        try {
            Node contentNode = getContentNode(context, isCollection);
            exportProperties(context, isCollection, contentNode);
            if (context.hasStream()) {
                exportData(context, isCollection, contentNode);
            } // else: missing stream. ignore.
            return true;
        } catch (RepositoryException e) {
            // should never occur, since the proper structure of the content
            // node must be asserted in the 'canExport' call.
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Same as (@link IOHandler#exportContent(ExportContext, boolean)} where
     * the boolean values is defined by {@link DavResource#isCollection()}.
     *
     * @see OKMHandler#exportContent(ExportContext, DavResource)
     */
    @Override
    public boolean exportContent(ExportContext context, DavResource resource) throws IOException {
    	log.debug("exportContent({}, {})", context, resource);
        if (!canExport(context, resource)) {
            throw new IOException(getName() + ": Cannot export " + context.getExportRoot());
        }
        return exportContent(context, resource.isCollection());
    }

    /**
     * Checks if the given content node contains a jcr:data property
     * and spools its value to the output stream fo the export context.<br>
     * Please note, that subclasses that define a different structure of the
     * content node should create their own
     * {@link  #exportData(ExportContext, boolean, Node) exportData} method.
     */
    protected void exportData(ExportContext context, boolean isCollection, Node contentNode) throws 
    		IOException, RepositoryException {
    	log.debug("exportData({}, {}, {})", new Object[] { context, isCollection, contentNode });
        if (contentNode.hasProperty(JcrConstants.JCR_DATA)) {
            Property p = contentNode.getProperty(JcrConstants.JCR_DATA);
            IOUtil.spool(p.getStream(), context.getOutputStream());
        } // else: stream undefined -> contentlength was not set
    }

    /**
     * Retrieves mimetype, encoding and modification time from the content node.
     * The content length is determined by the length of the jcr:data property
     * if it is present. The creation time however is retrieved from the parent
     * node (in case of isCollection == false only).
     */
    protected void exportProperties(ExportContext context, boolean isCollection, Node contentNode) throws
    		IOException {
        log.debug("exportProperties({}, {}, {})", new Object[] { context, isCollection, contentNode });
    	try {
            // only non-collections: 'jcr:created' is present on the parent 'fileNode' only
            if (!isCollection && contentNode.getDepth() > 0 && contentNode.getParent().hasProperty(JcrConstants.JCR_CREATED)) {
                long cTime = contentNode.getParent().getProperty(JcrConstants.JCR_CREATED).getValue().getLong();
                context.setCreationTime(cTime);
            }

            long length = IOUtil.UNDEFINED_LENGTH;
            if (contentNode.hasProperty(JcrConstants.JCR_DATA)) {
                Property p = contentNode.getProperty(JcrConstants.JCR_DATA);
                length = p.getLength();
                context.setContentLength(length);
            }

            String mimeType = null;
            String encoding = null;
            if (contentNode.hasProperty(JcrConstants.JCR_MIMETYPE)) {
                mimeType = contentNode.getProperty(JcrConstants.JCR_MIMETYPE).getString();
            }
            if (contentNode.hasProperty(JcrConstants.JCR_ENCODING)) {
                encoding = contentNode.getProperty(JcrConstants.JCR_ENCODING).getString();
                // ignore "" encodings (although this is avoided during import)
                if ("".equals(encoding)) {
                    encoding = null;
                }
            }
            context.setContentType(mimeType, encoding);

            long modTime = IOUtil.UNDEFINED_TIME;
            if (contentNode.hasProperty(JcrConstants.JCR_LASTMODIFIED)) {
                modTime = contentNode.getProperty(JcrConstants.JCR_LASTMODIFIED).getLong();
                context.setModificationTime(modTime);
            } else {
                context.setModificationTime(System.currentTimeMillis());
            }

            if (length > IOUtil.UNDEFINED_LENGTH && modTime > IOUtil.UNDEFINED_TIME) {
                String etag = "\"" + length + "-" + modTime + "\"";
                context.setETag(etag);
            }
        } catch (RepositoryException e) {
            // should never occur
            log.error("Unexpected error {} while exporting properties: {}", e.getClass().getName(), e.getMessage());
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Retrieves the content node that contains the data to be exported. In case
     * isCollection is true, this corresponds to the export root. Otherwise there
     * must be a child node with name {@link JcrConstants#JCR_CONTENT jcr:content}.
     *
     * @param context
     * @param isCollection
     * @return content node used for the export
     * @throws RepositoryException
     */
    protected Node getContentNode(ExportContext context, boolean isCollection) throws RepositoryException {
        log.debug("getContentNode({}, {})", context, isCollection);
    	Node contentNode = (Node)context.getExportRoot();
        // 'file' nodes must have an jcr:content child node (see canExport)
        if (!isCollection) {
            contentNode = contentNode.getNode(Document.CONTENT);
        }
        return contentNode;
    }

    /**
     * Name of the nodetype to be used to create a new collection node (folder)
     *
     * @return nodetype name
     */
    protected String getCollectionNodeType() {
    	//log.debug("getCollectionNodeType: {}", collectionNodetype);
        return collectionNodetype;
    }

    /**
     * Name of the nodetype to be used to create a new non-collection node (file)
     *
     * @return nodetype name
     */
    protected String getNodeType() {
    	//log.debug("getNodeType: {}", defaultNodetype);
        return defaultNodetype;
    }

    /**
     * Name of the nodetype to be used to create the content node below
     * a new non-collection node, whose name is always {@link JcrConstants#JCR_CONTENT
     * jcr:content}.
     *
     * @return nodetype name
     */
    protected String getContentNodeType() {
    	//log.debug("getContentNodeType: {}", contentNodetype);
        return contentNodetype;
    }

    //----------------------------------------------------< PropertyHandler >---
    @Override
    public boolean canExport(PropertyExportContext context, boolean isCollection) {
    	log.debug("canExport(PropertyExportContext:{}, {})", context, isCollection);
        return canExport((ExportContext) context, isCollection);
    }
    
    @Override
    public boolean exportProperties(PropertyExportContext exportContext, boolean isCollection) throws
    		RepositoryException {
    	log.debug("exportProperties({}, {}", exportContext, isCollection);
        if (!canExport(exportContext, isCollection)) {
            throw new RepositoryException("PropertyHandler " + getName() + " failed to export properties.");
        }

        Node cn = getContentNode(exportContext, isCollection);
        try {
            // export the properties common with normal IO handling
            exportProperties(exportContext, isCollection, cn);

            // export all other properties as well
            PropertyIterator it = cn.getProperties();
            while (it.hasNext()) {
                Property p = it.nextProperty();
                String name = p.getName();
                PropertyDefinition def = p.getDefinition();
                if (def.isMultiple() || isDefinedByFilteredNodeType(def)) {
                    log.debug("Skip property '{}': not added to webdav property set.", name);
                    continue;
                }
                if (JcrConstants.JCR_DATA.equals(name)
                    || JcrConstants.JCR_MIMETYPE.equals(name)
                    || JcrConstants.JCR_ENCODING.equals(name)
                    || JcrConstants.JCR_LASTMODIFIED.equals(name)) {
                    continue;
                }

                DavPropertyName davName = getDavName(name, p.getSession());
                exportContext.setProperty(davName, p.getValue().getString());
            }
            return true;
        } catch (IOException e) {
            // should not occur (log output see 'exportProperties')
            return false;
        }
    }
    
    @Override
    public boolean canImport(PropertyImportContext context, boolean isCollection) {
    	log.debug("canImport({}, {})", context, isCollection);
        if (context == null || context.isCompleted()) {
            return false;
        }
        Item contextItem = context.getImportRoot();
        try {
            return contextItem != null && contextItem.isNode() && (isCollection || ((Node)contextItem).hasNode(JcrConstants.JCR_CONTENT));
        } catch (RepositoryException e) {
            log.error("Unexpected error: {}", e.getMessage());
            return false;
        }
    }
    	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
    public Map importProperties(PropertyImportContext importContext, boolean isCollection) throws
    		RepositoryException {
        log.debug("importProperties({}, {})", importContext, isCollection);
    	if (!canImport(importContext, isCollection)) {
            throw new RepositoryException("PropertyHandler " + getName() + " failed import properties");
        }

        // loop over List and remember all properties and propertyNames
        // that failed to be imported (set or remove).
        Map failures = new HashMap();
        List changeList = importContext.getChangeList();

        // for collections the import-root is the target node where properties
        // are altered. in contrast 'non-collections' are with the handler
        // represented by 'file' nodes, that must have a jcr:content child
        // node, which holds all properties except jcr:created.
        // -> see canImport for the corresponding assertions
        Node cn = (Node) importContext.getImportRoot();
        if (!isCollection && cn.hasNode(JcrConstants.JCR_CONTENT)) {
            cn = cn.getNode(JcrConstants.JCR_CONTENT);
        }

        if (changeList != null) {
            Iterator it = changeList.iterator();
            while (it.hasNext()) {
                Object propEntry = it.next();
                try {
                    if (propEntry instanceof DavPropertyName) {
                        // remove
                        DavPropertyName propName = (DavPropertyName) propEntry;
                        removeJcrProperty(propName, cn);
                    } else if (propEntry instanceof DavProperty) {
                        // add or modify property
                        DavProperty prop = (DavProperty)propEntry;
                        setJcrProperty(prop, cn);
                    } else {
                        // ignore any other entry in the change list
                        log.error("unknown object in change list: " + propEntry.getClass().getName());
                    }
                } catch (RepositoryException e) {
                    failures.put(propEntry, e);
                }
            }
        }
        if (failures.isEmpty()) {
            setLastModified(cn, IOUtil.UNDEFINED_LENGTH);
        }
        return failures;
    }

    //------------------------------------------------------------< private >---
    /**
     * Builds a webdav property name from the given jcrName. In case the jcrName
     * contains a namespace prefix that would conflict with any of the predefined
     * webdav namespaces a new prefix is assigned.<br>
     * Please note, that the local part of the jcrName is checked for XML
     * compatibility by calling {@link ISO9075#encode(String)}
     *
     * @param jcrName
     * @param session
     * @return a <code>DavPropertyName</code> for the given jcr name.
     */
    private DavPropertyName getDavName(String jcrName, Session session) throws RepositoryException {
    	//log.debug("getDavName({}, {})", jcrName, session);
        // make sure the local name is xml compliant
        String localName = ISO9075.encode(Text.getLocalName(jcrName));
        String prefix = Text.getNamespacePrefix(jcrName);
        String uri = session.getNamespaceURI(prefix);
        Namespace namespace = Namespace.getNamespace(prefix, uri);
        DavPropertyName name = DavPropertyName.create(localName, namespace);
        //log.debug("getDavName: {}", name);
        return name;
    }

    /**
     * Build jcr property name from dav property name. If the property name
     * defines a namespace uri, that has not been registered yet, an attempt
     * is made to register the uri with the prefix defined. Note, that no
     * extra effort is made to generated a unique prefix.
     *
     * @param propName
     * @return jcr name
     * @throws RepositoryException
     */
    private String getJcrName(DavPropertyName propName, Session session) throws RepositoryException {
        // remove any encoding necessary for xml compliance
        String pName = ISO9075.decode(propName.getName());
        Namespace propNamespace = propName.getNamespace();
        if (!Namespace.EMPTY_NAMESPACE.equals(propNamespace)) {
            String prefix;
            String emptyPrefix = Namespace.EMPTY_NAMESPACE.getPrefix();
            try {
                // lookup 'prefix' in the session-ns-mappings / namespace-registry
                prefix = session.getNamespacePrefix(propNamespace.getURI());
            } catch (NamespaceException e) {
                // namespace uri has not been registered yet
                NamespaceRegistry nsReg = session.getWorkspace().getNamespaceRegistry();
                prefix = propNamespace.getPrefix();
                // avoid trouble with default namespace
                if (emptyPrefix.equals(prefix)) {
                    prefix = "_pre" + nsReg.getPrefixes().length + 1;
                }
                // NOTE: will fail if prefix is already in use in the namespace registry
                nsReg.registerNamespace(prefix, propNamespace.getURI());
            }
            if (prefix != null && !emptyPrefix.equals(prefix)) {
                pName = prefix + ":" + pName;
            }
        }
        return pName;
    }


    /**
     * @param property
     * @throws RepositoryException
     */
    private void setJcrProperty(DavProperty property, Node contentNode) throws RepositoryException {
        // Retrieve the property value. Note, that a 'null' value is replaced
        // by empty string, since setting a jcr property value to 'null'
        // would be equivalent to its removal.
        String value = "";
        if (property.getValue() != null) {
            value = property.getValue().toString();
        }

        DavPropertyName davName = property.getName();
        if (DavPropertyName.GETCONTENTTYPE.equals(davName)) {
            String mimeType = IOUtil.getMimeType(value);
            String encoding = IOUtil.getEncoding(value);
            contentNode.setProperty(JcrConstants.JCR_MIMETYPE, mimeType);
            contentNode.setProperty(JcrConstants.JCR_ENCODING, encoding);
        } else {
            contentNode.setProperty(getJcrName(davName, contentNode.getSession()), value);
        }
    }

    /**
     * @param propertyName
     * @throws RepositoryException
     */
    private void removeJcrProperty(DavPropertyName propertyName, Node contentNode) throws RepositoryException {
        if (DavPropertyName.GETCONTENTTYPE.equals(propertyName)) {
            if (contentNode.hasProperty(JcrConstants.JCR_MIMETYPE)) {
                contentNode.getProperty(JcrConstants.JCR_MIMETYPE).remove();
            }
            if (contentNode.hasProperty(JcrConstants.JCR_ENCODING)) {
                contentNode.getProperty(JcrConstants.JCR_ENCODING).remove();
            }
        } else {
            String jcrName = getJcrName(propertyName, contentNode.getSession());
            if (contentNode.hasProperty(jcrName)) {
                contentNode.getProperty(jcrName).remove();
            }
            // removal of non existing property succeeds
        }
    }

    private void setLastModified(Node contentNode, long hint) {
        try {
            Calendar lastMod = Calendar.getInstance();
            if (hint > IOUtil.UNDEFINED_TIME) {
                lastMod.setTimeInMillis(hint);
            } else {
                lastMod.setTime(new Date());
            }
            contentNode.setProperty(JcrConstants.JCR_LASTMODIFIED, lastMod);
        } catch (RepositoryException e) {
            // ignore: property may not be available on the node.
            // deliberately not rethrowing as IOException.
        }
    }

    private static boolean isDefinedByFilteredNodeType(PropertyDefinition def) {
        String ntName = def.getDeclaringNodeType().getName();
        return ntName.equals(JcrConstants.NT_BASE)
               || ntName.equals(JcrConstants.MIX_REFERENCEABLE)
               || ntName.equals(JcrConstants.MIX_VERSIONABLE)
               || ntName.equals(JcrConstants.MIX_LOCKABLE);
    }
}
