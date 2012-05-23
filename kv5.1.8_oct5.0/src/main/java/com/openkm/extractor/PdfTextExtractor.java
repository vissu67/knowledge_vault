package com.openkm.extractor;

import java.io.BufferedInputStream;
import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jackrabbit.extractor.AbstractTextExtractor;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;
import org.apache.pdfbox.util.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.Config;
import com.openkm.util.FileUtils;

/**
 * Text extractor for Portable Document Format (PDF).
 */
public class PdfTextExtractor extends AbstractTextExtractor {

    /**
     * Logger instance.
     */
	private static final Logger log = LoggerFactory.getLogger(PdfTextExtractor.class);

    /**
     * Force loading of dependent class.
     */
    static {
        PDFParser.class.getName();
    }

    /**
     * Creates a new <code>PdfTextExtractor</code> instance.
     */
    public PdfTextExtractor() {
        super(new String[]{ "application/pdf" });
    }

    //-------------------------------------------------------< TextExtractor >

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
	public Reader extractText(InputStream stream, String type, String encoding) throws IOException {
        try {
            PDFParser parser = new PDFParser(new BufferedInputStream(stream));
            
            try {
                parser.parse();
                PDDocument document = parser.getPDDocument();
                CharArrayWriter writer = new CharArrayWriter();
                
                PDFTextStripper stripper = new PDFTextStripper();
                stripper.setLineSeparator("\n");
                stripper.writeText(document, writer);
                String st = writer.toString().trim();
                log.debug("TextStripped: '{}'", st);
                
                if (Config.SYSTEM_PDF_FORCE_OCR || st.length() <= 1) {
                	log.warn("PDF does not contains text layer");
                	
                	// Extract images from PDF
					List pages = document.getDocumentCatalog().getAllPages();
                	StringBuilder sb = new StringBuilder();
                	
                	for (Iterator itPg = pages.iterator(); itPg.hasNext(); ) {
                		PDPage page = (PDPage) itPg.next();
                        PDResources resources = page.getResources();
                        Map images = resources.getImages();
                        
                        if (images != null) {
                        	for (Iterator itImg = images.keySet().iterator(); itImg.hasNext(); ) {
                        		 String key = (String) itImg.next();
                                 PDXObjectImage image = (PDXObjectImage) images.get(key);
                                 File pdfImg = File.createTempFile(key, "." + image.getSuffix());
                                 log.debug("Writing image: {}", pdfImg.getPath());
                                 image.write2file(pdfImg);
                                 String txt = new CuneiformTextExtractor().doOcr(pdfImg);
                                 sb.append(txt).append(" ");
                                 log.debug("OCR Extracted: {}", txt);
                                 FileUtils.deleteQuietly(pdfImg);
                        	}
                        }
                	}
                	
                	return new StringReader(sb.toString());
                } else {
                	return new CharArrayReader(writer.toCharArray());
                }
            } finally {
                try {
                    PDDocument doc = parser.getPDDocument();
                    if (doc != null) {
                        doc.close();
                    }
                } catch (IOException e) {
                    // ignore
                }
            }
        } catch (Exception e) {
            // it may happen that PDFParser throws a runtime
            // exception when parsing certain pdf documents
        	log.warn("Failed to extract PDF text content", e);
            return new StringReader("");
        } finally {
            stream.close();
        }
    }
}

