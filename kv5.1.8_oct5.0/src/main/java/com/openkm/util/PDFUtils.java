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

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bsh.EvalError;
import bsh.Interpreter;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PRAcroForm;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfWriter;

import de.svenjacobs.loremipsum.LoremIpsum;
import freemarker.template.TemplateException;

/**
 * http://itextpdf.sourceforge.net/howtosign.html
 * 
 * @author pavila
 */
public class PDFUtils {
	private static Logger log = LoggerFactory.getLogger(PDFUtils.class);
	public static int LAYER_UNDER_CONTENT = 0;
	public static int LAYER_OVER_CONTENT = 1;
	
	/**
	 * Stamp PDF document with image watermark
	 */
	public static void stampImage(InputStream input, byte[] image, OutputStream output) throws FileNotFoundException,
			DocumentException, EvalError, IOException {
		log.debug("stampImage({}, {}, {})", new Object[] { input, image, output });
		stampImage(input, image, LAYER_UNDER_CONTENT, 0.3f, "PAGE_CENTER - IMAGE_WIDTH / 2", "PAGE_MIDDLE - IMAGE_HEIGHT / 2", output);
		
	}

	/**
	 * Stamp PDF document with image watermark
	 */
	public static void stampImage(InputStream input, byte[] image, int layer, float opacity, String exprX, String exprY,
			OutputStream output) throws FileNotFoundException, DocumentException, EvalError, IOException {
		log.debug("stampImage({}, {}, {}, {}, {}, {}, {})", new Object[] { input, image, layer, opacity, exprX, exprY, output });
		Image img = Image.getInstance(image);
		PdfReader reader = new PdfReader(input);
		PdfStamper stamper = new PdfStamper(reader, output);
		PdfGState gs = new PdfGState();
		gs.setFillOpacity(opacity);
		gs.setStrokeOpacity(opacity);
		int numPages = reader.getNumberOfPages();
		int count = 0;
		
		while (count++ < numPages) {
			Interpreter i = new Interpreter();
			i.set("IMAGE_WIDTH", (int) img.getWidth());
			i.set("IMAGE_HEIGHT", (int) img.getHeight());
			i.set("PAGE_WIDTH", (int) reader.getPageSizeWithRotation(count).getWidth());
			i.set("PAGE_HEIGHT", (int) reader.getPageSizeWithRotation(count).getHeight());
			i.set("PAGE_CENTER", (int) reader.getPageSizeWithRotation(count).getWidth() / 2);
			i.set("PAGE_MIDDLE", (int) reader.getPageSizeWithRotation(count).getHeight() / 2);
			int evalX = (Integer) i.eval(exprX);
			int evalY = (Integer) i.eval(exprY);
			log.debug("evalX: {}", evalX);
			log.debug("evalY: {}", evalY);
			
			img.setAbsolutePosition(evalX, evalY);
			PdfContentByte cb = null;
			
			if (layer == LAYER_UNDER_CONTENT) {
				cb = stamper.getUnderContent(count);
			} else if (layer == LAYER_OVER_CONTENT) {
				cb = stamper.getOverContent(count);
			} else {
				throw new IllegalArgumentException();
			}
			
			cb.saveState();
			cb.setGState(gs);
			cb.addImage(img);
			cb.restoreState();
		}
		
		stamper.close();
	}
	
	/**
	 * Stamp PDF document with text watermark
	 */
	public static void stampText(InputStream input, String text, OutputStream output) throws FileNotFoundException,
			DocumentException, EvalError, IOException {
		log.debug("stampText({}, {}, {})", new Object[] { input, text, output });
		stampText(input, text, LAYER_UNDER_CONTENT, 0.5f, 100, Color.RED, 35, Element.ALIGN_CENTER, "PAGE_CENTER", "PAGE_MIDDLE", output);
	}
	
	/**
	 * Stamp PDF document with text watermark
	 */
	public static void stampText(InputStream input, String text, int layer, float opacity, int size, Color color,
			int rotation, int align, String exprX, String exprY, OutputStream output) throws FileNotFoundException,
			DocumentException, EvalError, IOException  {
		log.debug("stampText({}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {})", new Object[] { input, text, layer, opacity, size, color, rotation, align, exprX, exprY, output });
		PdfReader reader = new PdfReader(input);
		PdfStamper stamper = new PdfStamper(reader, output);
		BaseFont bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
		PdfGState gs = new PdfGState();
		gs.setFillOpacity(opacity);
		gs.setStrokeOpacity(opacity);
		int numPages = reader.getNumberOfPages();
		int count = 0;
		
		while (count++ < numPages) {
			Interpreter i = new Interpreter();
			i.set("PAGE_WIDTH", (int) reader.getPageSizeWithRotation(count).getWidth());
			i.set("PAGE_HEIGHT", (int) reader.getPageSizeWithRotation(count).getHeight());
			i.set("PAGE_CENTER", (int) reader.getPageSizeWithRotation(count).getWidth() / 2);
			i.set("PAGE_MIDDLE", (int) reader.getPageSizeWithRotation(count).getHeight() / 2);
			int evalX = (Integer) i.eval(exprX);
			int evalY = (Integer) i.eval(exprY);
			log.debug("evalX: {}", evalX);
			log.debug("evalY: {}", evalY);
			
			PdfContentByte cb = null;
			
			if (layer == LAYER_UNDER_CONTENT) {
				cb = stamper.getUnderContent(count);
			} else if (layer == LAYER_OVER_CONTENT) {
				cb = stamper.getOverContent(count);
			} else {
				throw new IllegalArgumentException();
			}
						
			cb.saveState();
			cb.setColorFill(color);
			cb.setGState(gs);
			cb.beginText();
			cb.setFontAndSize(bf, size);
									
			cb.showTextAligned(align, text, evalX, evalY, rotation);
			cb.endText();
			cb.restoreState();
		}
		
		stamper.close();
		reader.close();
	}
	
	/**
	 * Fill PDF form
	 * @throws  
	 */
	@SuppressWarnings("rawtypes")
	public static void fillForm(InputStream input, Map<String, Object> values, 
			OutputStream output) throws FileNotFoundException, DocumentException, TemplateException,
			IOException  {
		log.debug("fillForm({}, {}, {})", new Object[] { input, values, output });
		PdfReader reader = new PdfReader(input);
		PdfStamper stamper = new PdfStamper(reader, output);
		AcroFields fields = stamper.getAcroFields();
		PRAcroForm form = reader.getAcroForm();
		boolean formFlattening = false;
		
		if (form != null) {
			for (Iterator it = form.getFields().iterator(); it.hasNext(); ) {
				PRAcroForm.FieldInformation field = (PRAcroForm.FieldInformation) it.next();
				String fieldValue = fields.getField(field.getName());
				log.debug("Field: {}, Value: {}", field.getName(), fieldValue);
				
				if (fieldValue != null && !fieldValue.equals("")) {
					if (values.containsKey(field.getName())) {
						String result = TemplateUtils.replace("PDF_FILL_FORM", fieldValue, values);
						log.debug("Set to '{}'", result);
						fields.setField(field.getName(), result);
						stamper.partialFormFlattening(field.getName());
						formFlattening = true;
					}
				} else {
					Object value = values.get(field.getName());
					
					if (value != null) {
						log.debug("Set to '{}'", value);
						fields.setField(field.getName(), value.toString());
						stamper.partialFormFlattening(field.getName());
						formFlattening = true;
					}
				}
			}
		}
		
		stamper.setFormFlattening(formFlattening);
		stamper.close();
		reader.close();
	}
	
	/**
	 * List form fields
	 */
	@SuppressWarnings("rawtypes")
	public static List<String> listFormFields(String input) throws FileNotFoundException, DocumentException,
			IOException {
		log.debug("listFormFields({})", input);
		List<String> formFields = new ArrayList<String>();
		PdfReader reader = new PdfReader(input);
		PRAcroForm form = reader.getAcroForm();
		
		if (form != null) {
			for (Iterator it = form.getFields().iterator(); it.hasNext(); ) {
				PRAcroForm.FieldInformation field = (PRAcroForm.FieldInformation) it.next();
				formFields.add(field.getName());
			}
		}
		
		reader.close();
		log.debug("listFormFields: {}", formFields);
		return formFields;
	}
	
	/**
	 * Generate sample PDF 
	 */
	public static void generateSample(int paragraphs, OutputStream os) throws DocumentException {
		LoremIpsum li = new LoremIpsum();
		Document doc = new Document(PageSize.A4, 25, 25, 25, 25);
		PdfWriter.getInstance(doc, os);
		doc.open();
		
		for (int i=0; i<paragraphs; i++) {
			doc.add(new Paragraph(li.getParagraphs()));
		}
		
		doc.close();
	}
}
