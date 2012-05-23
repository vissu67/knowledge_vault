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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.openkm.bean.PropertyGroup;
import com.openkm.bean.form.Button;
import com.openkm.bean.form.CheckBox;
import com.openkm.bean.form.Download;
import com.openkm.bean.form.FormElement;
import com.openkm.bean.form.Input;
import com.openkm.bean.form.Option;
import com.openkm.bean.form.Print;
import com.openkm.bean.form.Select;
import com.openkm.bean.form.Separator;
import com.openkm.bean.form.SuggestBox;
import com.openkm.bean.form.Text;
import com.openkm.bean.form.TextArea;
import com.openkm.bean.form.Upload;
import com.openkm.bean.form.Validator;
import com.openkm.core.ParseException;

public class FormUtils {
	private static Logger log = LoggerFactory.getLogger(FormUtils.class);
	private static Map<PropertyGroup, List<FormElement>> pGroups = null;
	
	/**
	 * Parse form.xml definitions
	 * 
	 * @return A Map with all the forms and its form elements.
	 */
	public static Map<String, List<FormElement>> parseWorkflowForms(InputStream is) throws ParseException {
		log.debug("parseWorkflowForms({})", is);
		// long begin = Calendar.getInstance().getTimeInMillis();
		Map<String, List<FormElement>> forms = new HashMap<String, List<FormElement>>();

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			dbf.setValidating(true);
			ErrorHandler handler = new ErrorHandler();
			DocumentBuilder db = dbf.newDocumentBuilder();
			db.setErrorHandler(handler);

			if (is != null) {
				Document doc = db.parse(is);
				doc.getDocumentElement().normalize();
				NodeList nlForm = doc.getElementsByTagName("workflow-form");

				for (int i = 0; i < nlForm.getLength(); i++) {
					Node nForm = nlForm.item(i);

					if (nForm.getNodeType() == Node.ELEMENT_NODE) {
						String taskName = nForm.getAttributes().getNamedItem("task").getNodeValue();
						NodeList nlField = nForm.getChildNodes();
						List<FormElement> fe = parseField(nlField);
						forms.put(taskName, fe);
					}
				}
			}
		} catch (ParserConfigurationException e) {
			throw new ParseException(e.getMessage(), e);
		} catch (SAXException e) {
			throw new ParseException(e.getMessage(), e);
		} catch (IOException e) {
			throw new ParseException(e.getMessage(), e);
		}

		log.debug("parseWorkflowForms: {}", forms);
		// log.info("Time: "+(Calendar.getInstance().getTimeInMillis()-begin)+" ms");
		return forms;
	}
	
	/**
	 * Parse params.xml definitions
	 * 
	 * @return A List parameter elements.
	 */
	public static List<FormElement> parseReportParameters(InputStream is) throws ParseException {
		log.debug("parseReportParameters({})", is);
		// long begin = Calendar.getInstance().getTimeInMillis();
		List<FormElement> params = new ArrayList<FormElement>();

		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			dbf.setValidating(true);
			ErrorHandler handler = new ErrorHandler();
			DocumentBuilder db = dbf.newDocumentBuilder();
			db.setErrorHandler(handler);

			if (is != null) {
				Document doc = db.parse(is);
				doc.getDocumentElement().normalize();
				NodeList nlForm = doc.getElementsByTagName("report-parameters");
				
				for (int i = 0; i < nlForm.getLength(); i++) {
					Node nForm = nlForm.item(i);
					
					if (nForm.getNodeType() == Node.ELEMENT_NODE) {
						NodeList nlField = nForm.getChildNodes();
						params = parseField(nlField);
					}
				}
			}
		} catch (ParserConfigurationException e) {
			throw new ParseException(e.getMessage(), e);
		} catch (SAXException e) {
			throw new ParseException(e.getMessage(), e);
		} catch (IOException e) {
			throw new ParseException(e.getMessage(), e);
		}

		log.debug("parseReportParameters: {}", params);
		// log.info("Time: "+(Calendar.getInstance().getTimeInMillis()-begin)+" ms");
		return params;
	}

	/**
	 * Parse PropertyGroups.xml definitions
	 * 
	 * @return A Map with all the forms and its form elements.
	 */
	public static synchronized Map<PropertyGroup, List<FormElement>> parsePropertyGroupsForms(String pgFile) 
			throws IOException,	ParseException {
		log.debug("parseMetadataForms()");
		// long begin = Calendar.getInstance().getTimeInMillis();
		if (pGroups == null) {
			log.debug("PropertyGroupForms: {}", pgFile);
			pGroups = new HashMap<PropertyGroup, List<FormElement>>();
			FileInputStream fis = null;
			
			try {
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				dbf.setNamespaceAware(true);
				dbf.setValidating(true);
				ErrorHandler handler = new ErrorHandler();
				DocumentBuilder db = dbf.newDocumentBuilder();
				db.setErrorHandler(handler);
				fis = new FileInputStream(pgFile);
				
				if (fis != null) {
					Document doc = db.parse(fis);
					doc.getDocumentElement().normalize();
					NodeList nlForm = doc.getElementsByTagName("property-group");
					
					for (int i = 0; i < nlForm.getLength(); i++) {
						Node nForm = nlForm.item(i);
						
						if (nForm.getNodeType() == Node.ELEMENT_NODE) {
							PropertyGroup pg = new PropertyGroup();
							
							Node item = nForm.getAttributes().getNamedItem("label");
							if (item != null) pg.setLabel(item.getNodeValue());
							item = nForm.getAttributes().getNamedItem("name");
							if (item != null) pg.setName(item.getNodeValue());
							item = nForm.getAttributes().getNamedItem("visible");
							if (item != null) pg.setVisible(Boolean.valueOf(item.getNodeValue()));
							item = nForm.getAttributes().getNamedItem("readonly");
							if (item != null) pg.setReadonly(Boolean.valueOf(item.getNodeValue()));
							
							NodeList nlField = nForm.getChildNodes();
							List<FormElement> fe = parseField(nlField);
							pGroups.put(pg, fe);
						}
					}
				}
			} catch (ParserConfigurationException e) {
				throw new ParseException(e.getMessage());
			} catch (SAXException e) {
				throw new ParseException(e.getMessage());
			} catch (IOException e) {
				throw e;
			} finally {
				IOUtils.closeQuietly(fis);
			}
		}
		
		log.debug("parseMetadataForms: {}", pGroups);
		// log.info("Time: "+(Calendar.getInstance().getTimeInMillis()-begin)+" ms");
		return clonedPropertyGroups();
	}
	
	/**
	 * Clone to be modified
	 */
	@SuppressWarnings("unchecked")
	private static Map<PropertyGroup, List<FormElement>> clonedPropertyGroups() throws IOException {
		try {
			return (Map<PropertyGroup, List<FormElement>>) Serializer.read(Serializer.write(pGroups));
		} catch (ClassNotFoundException e) {
			throw new IOException("ClassNotFoundException", e);
		}
	}

	/**
	 * Force PropertyGroups.xml re-read in the next petition.
	 */
	public static synchronized void resetPropertyGroupsForms() {
		pGroups = null;
	}

	/**
	 * Retrieve the form elements from a PropertyGroup definition. 
	 */
	public static List<FormElement> getPropertyGroupForms(Map<PropertyGroup, List<FormElement>> formsElements, String groupName) {
		for (Iterator<Entry<PropertyGroup, List<FormElement>>> it1 = formsElements.entrySet().iterator(); it1.hasNext(); ) {
			Entry<PropertyGroup, List<FormElement>> entry = it1.next();
			if (entry.getKey().getName().equals(groupName)) {
				return entry.getValue();
			}
		}
		
		return null;
	}
	
	/**
	 * Retrieve the form element from a PropertyGroups definition. 
	 */
	public static FormElement getFormElement(Map<PropertyGroup, List<FormElement>> formsElements, String propertyName) {
		for (Iterator<Entry<PropertyGroup, List<FormElement>>> it1 = formsElements.entrySet().iterator(); it1.hasNext(); ) {
			Entry<PropertyGroup, List<FormElement>> entry = it1.next();
			for (Iterator<FormElement> it2 = entry.getValue().iterator(); it2.hasNext(); ) {
				FormElement fe = it2.next();
				if (fe.getName().equals(propertyName)) {
					return fe;
				}
			}
		}
		
		return null;
	}

	/**
	 * Parse individual form fields 
	 */
	private static List<FormElement> parseField(NodeList nlField) {
		List<FormElement> fe = new ArrayList<FormElement>();
		
		for (int j = 0; j < nlField.getLength(); j++) {
			Node nField = nlField.item(j);
		
			if (nField.getNodeType() == Node.ELEMENT_NODE) {
				String fieldComponent = nField.getNodeName();

				if (fieldComponent.equals("input")) {
					Input input = new Input();
					Node item = nField.getAttributes().getNamedItem("label");
					if (item != null) input.setLabel(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("name");
					if (item != null) input.setName(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("type");
					if (item != null) input.setType(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("value");
					if (item != null) input.setValue(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("data");
					if (item != null) input.setData(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("width");
					if (item != null) input.setWidth(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("height");
					if (item != null) input.setHeight(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("readonly");
					if (item != null) input.setReadonly(Boolean.parseBoolean(item.getNodeValue()));
					input.setValidators(parseValidators(nField));
					fe.add(input);
				} else if (fieldComponent.equals("suggestbox")) {
					SuggestBox sbox = new SuggestBox();
					Node item = nField.getAttributes().getNamedItem("label");
					if (item != null) sbox.setLabel(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("name");
					if (item != null) sbox.setName(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("value");
					if (item != null) sbox.setValue(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("data");
					if (item != null) sbox.setData(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("width");
					if (item != null) sbox.setWidth(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("height");
					if (item != null) sbox.setHeight(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("table");
					if (item != null) sbox.setTable(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("filterQuery");
					if (item != null) sbox.setFilterQuery(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("valueQuery");
					if (item != null) sbox.setValueQuery(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("dialogTitle");
					if (item != null) sbox.setDialogTitle(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("readonly");
					if (item != null) sbox.setReadonly(Boolean.parseBoolean(item.getNodeValue()));
					item = nField.getAttributes().getNamedItem("filterMinLen");
					if (item != null) sbox.setFilterMinLen(Integer.parseInt(item.getNodeValue()));
					sbox.setValidators(parseValidators(nField));
					fe.add(sbox);
				} else if (fieldComponent.equals("upload")) {
					Upload up = new Upload();
					Node item = nField.getAttributes().getNamedItem("label");
					if (item != null) up.setLabel(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("name");
					if (item != null) up.setName(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("width");
					if (item != null) up.setWidth(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("height");
					if (item != null) up.setHeight(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("folderPath");
					if (item != null) up.setFolderPath(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("folderUuid");
					if (item != null) up.setFolderUuid(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("documentName");
					if (item != null) up.setDocumentName(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("documentUuid");
					if (item != null) up.setDocumentUuid(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("type");
					if (item != null) up.setType(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("data");
					if (item != null) up.setData(item.getNodeValue());
					up.setValidators(parseValidators(nField));
					fe.add(up);
				} else if (fieldComponent.equals("download")) {
					Download down = new Download();
					Node item = nField.getAttributes().getNamedItem("label");
					if (item != null) down.setLabel(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("name");
					if (item != null) down.setName(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("width");
					if (item != null) down.setWidth(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("height");
					if (item != null) down.setHeight(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("data");
					if (item != null) down.setData(item.getNodeValue());
					down.setNodes(parseNodes(nField));
					down.setValidators(parseValidators(nField));
					fe.add(down);
				} else if (fieldComponent.equals("print")) {
					Print print = new Print();
					Node item = nField.getAttributes().getNamedItem("label");
					if (item != null) print.setLabel(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("name");
					if (item != null) print.setName(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("width");
					if (item != null) print.setWidth(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("height");
					if (item != null) print.setHeight(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("data");
					if (item != null) print.setData(item.getNodeValue());
					print.setNodes(parseNodes(nField));
					print.setValidators(parseValidators(nField));
					fe.add(print);
				} else if (fieldComponent.equals("checkbox")) {
					CheckBox checkBox = new CheckBox();
					Node item = nField.getAttributes().getNamedItem("label");
					if (item != null) checkBox.setLabel(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("name");
					if (item != null) checkBox.setName(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("value");
					if (item != null) checkBox.setValue(Boolean.parseBoolean(item.getNodeValue()));
					item = nField.getAttributes().getNamedItem("data");
					if (item != null) checkBox.setData(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("width");
					if (item != null) checkBox.setWidth(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("height");
					if (item != null) checkBox.setHeight(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("readonly");
					if (item != null) checkBox.setReadonly(Boolean.parseBoolean(item.getNodeValue()));
					checkBox.setValidators(parseValidators(nField));
					fe.add(checkBox);
				} else if (fieldComponent.equals("textarea")) {
					TextArea textArea = new TextArea();
					Node item = nField.getAttributes().getNamedItem("label");
					if (item != null) textArea.setLabel(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("name");
					if (item != null) textArea.setName(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("value");
					if (item != null) textArea.setValue(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("data");
					if (item != null) textArea.setData(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("width");
					if (item != null) textArea.setWidth(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("height");
					if (item != null) textArea.setHeight(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("readonly");
					if (item != null) textArea.setReadonly(Boolean.parseBoolean(item.getNodeValue()));
					textArea.setValidators(parseValidators(nField));
					fe.add(textArea);
				} else if (fieldComponent.equals("button")) {
					Button button = new Button();
					Node item = nField.getAttributes().getNamedItem("label");
					if (item != null) button.setLabel(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("name");
					if (item != null) button.setName(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("transition");
					if (item != null) button.setTransition(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("confirmation");
					if (item != null) button.setConfirmation(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("width");
					if (item != null) button.setWidth(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("height");
					if (item != null) button.setHeight(item.getNodeValue());
					fe.add(button);
				} else if (fieldComponent.equals("text")) {
					Text txt = new Text();
					Node item = nField.getAttributes().getNamedItem("label");
					if (item != null) txt.setLabel(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("name");
					if (item != null) txt.setName(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("data");
					if (item != null) txt.setData(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("width");
					if (item != null) txt.setWidth(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("height");
					if (item != null) txt.setHeight(item.getNodeValue());
					fe.add(txt);
				} else if (fieldComponent.equals("separator")) {
					Separator sep = new Separator();
					Node item = nField.getAttributes().getNamedItem("label");
					if (item != null) sep.setLabel(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("name");
					if (item != null) sep.setName(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("width");
					if (item != null) sep.setWidth(item.getNodeValue());
					fe.add(sep);
				} else if (fieldComponent.equals("select")) {
					Select select = new Select();
					ArrayList<Option> options = new ArrayList<Option>();
					Node item = nField.getAttributes().getNamedItem("label");
					if (item != null) select.setLabel(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("name");
					if (item != null) select.setName(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("type");
					if (item != null) select.setType(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("data");
					if (item != null) select.setData(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("width");
					if (item != null) select.setWidth(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("height");
					if (item != null) select.setHeight(item.getNodeValue());
					item = nField.getAttributes().getNamedItem("readonly");
					if (item != null) select.setReadonly(Boolean.parseBoolean(item.getNodeValue()));
					
					NodeList nlOptions = nField.getChildNodes();
					for (int k = 0; k < nlOptions.getLength(); k++) {
						Node nOption = nlOptions.item(k);
						
						if (nOption.getNodeType() == Node.ELEMENT_NODE) {
							if (nOption.getNodeName().equals("option")) {
								Option option = new Option();
								item = nOption.getAttributes().getNamedItem("label");
								if (item != null) option.setLabel(item.getNodeValue());
								item = nOption.getAttributes().getNamedItem("value");
								if (item != null) option.setValue(item.getNodeValue());
								item = nOption.getAttributes().getNamedItem("selected");
								if (item != null) option.setSelected(Boolean.parseBoolean(item.getNodeValue()));
								options.add(option);
							}
						}
					}
					
					select.setOptions(options);
					select.setValidators(parseValidators(nField));
					fe.add(select);
				}
			}
		}
		
		return fe;
	}
	
	/**
	 * Parse form elements nodes
	 */
	private static List<com.openkm.bean.form.Node> parseNodes(Node nField) {
		List<com.openkm.bean.form.Node> nodes = new ArrayList<com.openkm.bean.form.Node>();
		NodeList nlNodes = nField.getChildNodes();
		
		for (int k = 0; k < nlNodes.getLength(); k++) {
			Node nNode = nlNodes.item(k);
			
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				if (nNode.getNodeName().equals("node")) {
					com.openkm.bean.form.Node node = new com.openkm.bean.form.Node();
					Node item = nNode.getAttributes().getNamedItem("label");
					if (item != null) node.setLabel(item.getNodeValue());
					item = nNode.getAttributes().getNamedItem("path");
					if (item != null) node.setPath(item.getNodeValue());
					item = nNode.getAttributes().getNamedItem("uuid");
					if (item != null) node.setUuid(item.getNodeValue());
					nodes.add(node);
				}
			}
		}
		
		return nodes;
	}
	
	/**
	 * Parse form elements validators
	 */
	private static List<Validator> parseValidators(Node nField) {
		List<Validator> validators = new ArrayList<Validator>();
		NodeList nlValidators = nField.getChildNodes();
		
		for (int k = 0; k < nlValidators.getLength(); k++) {
			Node nValidator = nlValidators.item(k);
			
			if (nValidator.getNodeType() == Node.ELEMENT_NODE) {
				if (nValidator.getNodeName().equals("validator")) {
					Validator validator = new Validator();
					Node item = nValidator.getAttributes().getNamedItem("type");
					if (item != null) validator.setType(item.getNodeValue());
					item = nValidator.getAttributes().getNamedItem("parameter");
					if (item != null) validator.setParameter(item.getNodeValue());
					validators.add(validator);
				}
			}
		}
		
		return validators;
	}
	
	/**
	 * 
	 */
	private static final class ErrorHandler extends DefaultHandler {
		public void error(SAXParseException exception) throws SAXException {
			log.error(exception.getMessage());
			throw exception;
		}

		public void fatalError(SAXParseException exception) throws SAXException {
			log.error(exception.getMessage());
			throw exception;
		}

		public void warning(SAXParseException exception) throws SAXException {
			log.warn(exception.getMessage());
			throw exception;
		}
	}
	
	/**
	 * Get form element type
	 */
	public static Map<String, String> toString(FormElement fe) {
		Map<String, String> ret = new HashMap<String, String>();
		ret.put("label", fe.getLabel());
		ret.put("name", fe.getName());
		ret.put("width", fe.getWidth());
		ret.put("height", fe.getHeight());
		
		if (fe instanceof Input) {
			Input input = (Input) fe;
			ret.put("field", "Input");
			StringBuilder sb = new StringBuilder();
			sb.append("<i>Readonly:</i> ");
			sb.append(input.isReadonly());
			sb.append("<br/>");
			sb.append("<i>Data:</i> ");
			sb.append(input.getData());
			sb.append("<br/>");
			sb.append("<i>Type:</i> ");
			sb.append(input.getType());
			drawValidators(sb, input.getValidators());
			ret.put("others", sb.toString());
		} else if (fe instanceof SuggestBox) {
			SuggestBox suggestBox = (SuggestBox) fe;
			ret.put("field", "SuggestBox");
			StringBuilder sb = new StringBuilder();
			sb.append("<i>Readonly:</i> ");
			sb.append(suggestBox.isReadonly());
			sb.append("<br/>");
			sb.append("<i>Data:</i> ");
			sb.append(suggestBox.getData());
			sb.append("<br/>");
			sb.append("<i>DialogTitle:</i> ");
			sb.append(suggestBox.getDialogTitle());
			sb.append("<br/>");
			sb.append("<i>Table:</i> ");
			sb.append(suggestBox.getTable());
			sb.append("<br/>");
			sb.append("<i>FilterMinLen:</i> ");
			sb.append(suggestBox.getFilterMinLen());
			sb.append("<br/>");
			sb.append("<i>FilterQuery:</i> ");
			sb.append(suggestBox.getFilterQuery());
			sb.append("<br/>");
			sb.append("<i>ValueQuery:</i> ");
			sb.append(suggestBox.getValueQuery());
			drawValidators(sb, suggestBox.getValidators());
			ret.put("others", sb.toString());
		} else if (fe instanceof CheckBox) {
			CheckBox checkBox = new CheckBox();
			ret.put("field", "CheckBox");
			StringBuilder sb = new StringBuilder();
			sb.append("<i>Readonly:</i> ");
			sb.append(checkBox.isReadonly());
			sb.append("<br/>");
			sb.append("<i>Data:</i> ");
			sb.append(checkBox.getData());
			drawValidators(sb, checkBox.getValidators());
			ret.put("others", sb.toString());
		} else if (fe instanceof TextArea) {
			TextArea textArea = (TextArea) fe;
			ret.put("field", "TextArea");
			StringBuilder sb = new StringBuilder();
			sb.append("<i>Readonly:</i> ");
			sb.append(textArea.isReadonly());
			sb.append("<br/>");
			sb.append("<i>Data:</i> ");
			sb.append(textArea.getData());
			drawValidators(sb, textArea.getValidators());
			ret.put("others", sb.toString());
		} else if (fe instanceof Select) {
			Select select = (Select) fe;
			ret.put("field", "Select");
			StringBuilder sb = new StringBuilder();
			sb.append("<i>Readonly:</i> ");
			sb.append(select.isReadonly());
			sb.append("<br/>");
			sb.append("<i>Data:</i> ");
			sb.append(select.getData());
			sb.append("<br/>");
			sb.append("<i>Type:</i> ");
			sb.append(select.getType());
			sb.append("<br/>");
			sb.append("<i>Options:</i><ul>");
			
			for (Iterator<Option> itOpt = select.getOptions().iterator(); itOpt.hasNext(); ) {
				Option opt = itOpt.next();
				sb.append("<li><i>Label:</i> ");
				sb.append(opt.getLabel());
				sb.append(", <i>Value:</i> ");
				sb.append(opt.getValue());
				sb.append("</li>");
			}
			
			sb.append("</ul>");
			drawValidators(sb, select.getValidators());
			ret.put("others", sb.toString());
		} else if (fe instanceof Button) {
			Button button = (Button) fe;
			ret.put("field", "Button");
			StringBuilder sb = new StringBuilder();
			sb.append("<i>Transition:</i> ");
			sb.append(button.getTransition());
			ret.put("others", sb.toString());
		} else if (fe instanceof Upload) {
			Upload up = (Upload) fe;
			ret.put("field", "Upload");
			StringBuilder sb = new StringBuilder();
			sb.append("<i>Type:</i> ");
			sb.append(up.getType());
			sb.append("<br/>");
			sb.append("<i>FolderPath:</i> ");
			sb.append(up.getFolderPath());
			sb.append("<br/>");
			sb.append("<i>FolderUuid:</i> ");
			sb.append(up.getFolderUuid());
			sb.append("<br/>");
			sb.append("<i>DocumentName:</i> ");
			sb.append(up.getDocumentName());
			sb.append("<br/>");
			sb.append("<i>DocumentUuid:</i> ");
			sb.append(up.getDocumentUuid());
			drawValidators(sb, up.getValidators());
			ret.put("others", sb.toString());
		} else if (fe instanceof Separator) {
			ret.put("field", "Separator");
			ret.put("others", "");
		} else if (fe instanceof Text) {
			ret.put("field", "Text");
			ret.put("others", "");
		}
		
		return ret;
	}
	
	/**
	 * Draw validation configuration
	 */
	private static void drawValidators(StringBuilder sb, List<Validator> validators) {
		if (!validators.isEmpty()) {
			sb.append("<br/><i>Validators:</i><ul>");
			for (Iterator<Validator> it = validators.iterator(); it.hasNext(); ) {
				Validator v = it.next();
				sb.append("<li><i>Type:</i> ");
				sb.append(v.getType());
				sb.append(", <i>Parameter:</i> ");
				sb.append(v.getParameter());
				sb.append("</li>");
			}
			sb.append("</ul>");
		}
	}
}
