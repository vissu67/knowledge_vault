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

import com.openkm.kea.filter.KEAFilter;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import weka.core.Instances;
import weka.core.FastVector;
import weka.core.Attribute;
import weka.core.Instance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SubjectExtractor
 * 
 * @author jllort
 *
 */
public class SubjectExtractor {

	private static Logger log = LoggerFactory.getLogger(SubjectExtractor.class);

    private String modelName = "model";
    private String vocabulary;
    private String vocabularyFormat = "skos";
    private String language = "en";
    private String encoding = "UTF-8";
    private boolean debug = true;
    private int subjectNumLimit = 12;
    private double subjectRelLimit = 1.2;
    private boolean additionalInfo = false;
    private KEAFilter filter = null;

    /**
     * SubjectExtractor
     * 
     * @throws MetadataExtractionException
     */
    public SubjectExtractor() throws MetadataExtractionException {
        filter = KEAFilterBank.getFilter();
    }

    /**
     * SubjectExtractor
     * 
     * @param limit
     * @throws MetadataExtractionException
     */
    public SubjectExtractor(int limit) throws MetadataExtractionException {
        subjectNumLimit = limit;
        filter = KEAFilterBank.getFilter();
    }


    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getVocabulary() {
        return vocabulary;
    }

    public void setVocabulary(String vocabulary) {
        this.vocabulary = vocabulary;
    }

    public String getVocabularyFormat() {
        return vocabularyFormat;
    }

    public void setVocabularyFormat(String vocabularyFormat) {
        this.vocabularyFormat = vocabularyFormat;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public int getSubjectNumLimit() {
        return subjectNumLimit;
    }

    public void setSubjectNumLimit(int subjectNumLimit) {
        this.subjectNumLimit = subjectNumLimit;
    }

    public double getSubjectRelLimit() {
        return subjectRelLimit;
    }

    public void setSubjectRelLimit(double subjectRelLimit) {
        this.subjectRelLimit = subjectRelLimit;
    }

    public boolean isAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(boolean additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    /**
     * extractSuggestedSubjects
     * 
     * @param documentText
     * @return
     */
	public List<String> extractSuggestedSubjects(String documentText) {

        Date start,stop;

        start = new Date();
        List<String> subjects = new ArrayList<String>();
        // no idea what this is ....
        FastVector atts = new FastVector(3);
        atts.addElement(new Attribute("doc", (FastVector) null));
        atts.addElement(new Attribute("keyphrases", (FastVector) null));
        atts.addElement(new Attribute("filename", (String) null));
        Instances unknownDataStructure = new Instances("keyphrase_training_data", atts, 0);

        try {
            // this is the exrtraction process part - not too well understood yet
            // "unkowndatastructure" is called instances in original KEA code
            double[] unknownStructure = new double[2];
            unknownStructure[0] = (double) unknownDataStructure.attribute(0).addStringValue(documentText);
            unknownStructure[1] = Instance.missingValue(); // this part used for existing subjects - we have none
            unknownDataStructure.add(new Instance(1.0, unknownStructure));
            filter.input(unknownDataStructure.instance(0));
            unknownDataStructure.stringFreeStructure(); //??**&%%!!!??

            // this is getting the results out - better understood
            Instance[] rankedSubjects = new Instance[this.subjectNumLimit];
            Instance subject;
            while ((subject = filter.output()) != null) {
                int index = (int) subject.value(filter.getRankIndex()) -1;
                if (index < subjectNumLimit) {
                    rankedSubjects[index] = subject;
                }
            }
            for (int i = 0; i < subjectNumLimit; i++) {
                if (rankedSubjects[i] != null) {
                    subjects.add(rankedSubjects[i].stringValue(filter.getUnstemmedPhraseIndex()));
                }
            }

        } catch (Exception e) {
            log.error("problem in subject extraction: ",e);
        } finally {
            stop = new Date();
            long time = (stop.getTime() - start.getTime());
            log.info("Subject extraction completed in " + time + "ms");
        }
        
        return subjects;
    }


}
