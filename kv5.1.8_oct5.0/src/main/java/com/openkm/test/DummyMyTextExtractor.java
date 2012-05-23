package com.openkm.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.apache.jackrabbit.extractor.AbstractTextExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DummyMyTextExtractor extends AbstractTextExtractor {

    /**
     * Logger instance.
     */
    private static final Logger log = LoggerFactory.getLogger(DummyMyTextExtractor.class);

    /**
     * Creates a new <code>TextExtractor</code> instance.
     */
    public DummyMyTextExtractor() {
        super(new String[]{"image/jpeg"});
    }
    
    //-------------------------------------------------------< TextExtractor >

    /**
     * {@inheritDoc}
     */ 
    public Reader extractText(InputStream stream, String type, String encoding) throws IOException {
    	log.info("******************* EXTRACT");
        return new StringReader("");
    }
}
