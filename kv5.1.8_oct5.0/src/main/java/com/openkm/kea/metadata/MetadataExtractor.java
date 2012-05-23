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

package com.openkm.kea.metadata;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.ontoware.rdf2go.RDF2Go;
import org.ontoware.rdf2go.model.Model;
import org.ontoware.rdf2go.model.node.Node;
import org.ontoware.rdf2go.model.node.URI;
import org.ontoware.rdf2go.model.node.impl.URIImpl;
import org.semanticdesktop.aperture.extractor.Extractor;
import org.semanticdesktop.aperture.extractor.ExtractorException;
import org.semanticdesktop.aperture.extractor.ExtractorFactory;
import org.semanticdesktop.aperture.extractor.ExtractorRegistry;
import org.semanticdesktop.aperture.extractor.impl.DefaultExtractorRegistry;
import org.semanticdesktop.aperture.mime.identifier.MimeTypeIdentifier;
import org.semanticdesktop.aperture.mime.identifier.magic.MagicMimeTypeIdentifier;
import org.semanticdesktop.aperture.rdf.RDFContainer;
import org.semanticdesktop.aperture.rdf.impl.RDFContainerImpl;
import org.semanticdesktop.aperture.util.IOUtil;
import org.semanticdesktop.aperture.vocabulary.NCO;
import org.semanticdesktop.aperture.vocabulary.NFO;
import org.semanticdesktop.aperture.vocabulary.NIE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.kea.MetadataDTO;

/**
 * MetadataExtractor
 * 
 * @author jllort
 *
 */
public class MetadataExtractor {
	private static Logger log = LoggerFactory.getLogger(MetadataExtractor.class);
    private MetadataDTO mdDTO;
    private File tempFile;
    private RDFContainer rdf;
    private SubjectExtractor subjectExtractor;
    private boolean se = true;

    /**
     * MetadataExtractor
     */
    public MetadataExtractor(boolean se) throws MetadataExtractionException {
        mdDTO = new MetadataDTO();
        this.se = se;
        
        if (se) {
        	subjectExtractor = new SubjectExtractor();
        }
    }

    /**
     * MetadataExtractor
     */
    public MetadataExtractor(int subjectLimit) throws MetadataExtractionException {
        mdDTO = new MetadataDTO();
        subjectExtractor = new SubjectExtractor(subjectLimit);
    }

    /**
     * getTempFile
     */
    public File getTempFile() {
        return tempFile;
    }

    /**
     * getOriginalFileName
     */
    public String getOriginalFileName() {
        return mdDTO.getFileName();
    }

    /**
     * getMdDTO
     */
    public MetadataDTO getMdDTO() {
        return mdDTO;
    }

    public MetadataDTO extract(File tempFile) throws MetadataExtractionException {
        try {
        	this.tempFile = tempFile;
            loadRDF();
            extractMetadataFromRDF();
            
            if (se) {
            	extractSuggestedSubjects();
            }
            
            rdf.dispose();
            return mdDTO;
        } catch (MetadataExtractionException e) {
            log.error("Metadata Extraction error: ");
            log.error(e.getMessage(), e);
            throw e;
        }
    }

    /**
     * loadRDF
     */
    @SuppressWarnings("unchecked")
	private void loadRDF() {
        MimeTypeIdentifier identifier = new MagicMimeTypeIdentifier();
        ExtractorRegistry extractorRegistry = new DefaultExtractorRegistry();
        String mimeType;

        try {
            // establish mimetype
            FileInputStream fis = new FileInputStream(tempFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            byte[] bytes = IOUtil.readBytes(bis, identifier.getMinArrayLength());
            bis.close();
            mimeType = identifier.identify(bytes, tempFile.getPath(), null);
            
            if (mimeType == null) {
                throw new MetadataExtractionException("Unable to extract MimeType for: " + mdDTO.getFileName());
            } else {
                mdDTO.setMimeType(mimeType);
            }

            // create RDF metadata model
            URI uri = new URIImpl(tempFile.toURI().toString());
            Model rdfModel = RDF2Go.getModelFactory().createModel();
            rdfModel.open();
            rdf = new RDFContainerImpl(rdfModel, uri);

            // create extractor
            ExtractorFactory extractorFactory;
            Extractor extractor;
            Set<ExtractorFactory> factories = extractorRegistry.getExtractorFactories(mimeType);
            if (factories == null || factories.isEmpty()) {
                throw new MetadataExtractionException("Unable to find extractor factory for: " + mimeType);
            } else {
                extractorFactory = factories.iterator().next();
                extractor = extractorFactory.get();
            }

            // extract the metadata
            fis = new FileInputStream(tempFile);
            bis = new BufferedInputStream(fis, 8192);
            extractor.extract(uri, bis, null, mimeType, rdf);
        } catch (FileNotFoundException e) {
            log.error("Unable to locate the workspace file for: " + mdDTO.getFileName(), e);
        } catch (IOException e) {
            log.error("Unable to read workspace file for: " + mdDTO.getFileName(), e);
        } catch (MetadataExtractionException e) {
            log.error(e.getMessage(), e);
        } catch (ExtractorException e) {
            log.error("Aperture extraction error: " + e.getMessage(), e);
        }
    }

    /**
     * extractMetadataFromRDF
     */
    @SuppressWarnings("unchecked")
	private void extractMetadataFromRDF() {
        // set up secondary RDF container for creator
        String creator = "";
        Collection<Node> creators = rdf.getAll(NCO.creator);
        
        for (Iterator<Node> iterator = creators.iterator(); iterator.hasNext();) {
            Node node = iterator.next();
            RDFContainer container = new RDFContainerImpl(rdf.getModel(), node.asURI());
            creator = container.getString(NCO.fullname);
            if (creator!=null && !creator.equals("")) break;
        }
        
        // copy values to metadataDTO
        mdDTO.setTitle(rdf.getString(NIE.title));
        mdDTO.setCreator(creator);
        mdDTO.addSubject(rdf.getString(NIE.subject));
        mdDTO.setGenerator(rdf.getString(NIE.generator));
        mdDTO.setContentCreated(rdf.getDate(NIE.contentCreated));
        mdDTO.setContentLastModified(rdf.getDate(NIE.contentLastModified));
        mdDTO.setPageCount(rdf.getInteger(NFO.pageCount));
        mdDTO.setKeyword(rdf.getString(NIE.keyword));
    }

    /**
     * extractSuggestedSubjects
     * 
     * @throws MetadataExtractionException
     */
    private void extractSuggestedSubjects() throws MetadataExtractionException {
        List<String> sugSubjects = subjectExtractor.extractSuggestedSubjects(rdf.getString(NIE.plainTextContent));
        Iterator<String> iter = sugSubjects.iterator();
        while (iter.hasNext()) {
            mdDTO.addSubject(iter.next());
        }
    }
}
