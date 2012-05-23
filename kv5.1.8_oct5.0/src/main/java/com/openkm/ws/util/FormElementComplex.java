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

package com.openkm.ws.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.form.Button;
import com.openkm.bean.form.CheckBox;
import com.openkm.bean.form.FormElement;
import com.openkm.bean.form.Input;
import com.openkm.bean.form.Option;
import com.openkm.bean.form.Select;
import com.openkm.bean.form.SuggestBox;
import com.openkm.bean.form.TextArea;
import com.openkm.bean.form.Validator;

public class FormElementComplex implements Serializable {
	private static Logger log = LoggerFactory.getLogger(FormElementComplex.class);
	private static final long serialVersionUID = 1L;
	private String objClass;
	private String label;
	private String name;
	private String width;
	private String height;
	private String type;
	private String value;
	private String transition;
	private boolean readonly;
	private List<Option> options = new ArrayList<Option>();
	private List<Validator> validators = new ArrayList<Validator>();

	public FormElementComplex() {
	}

	public String getObjClass() {
		return objClass;
	}

	public void setObjClass(String objClass) {
		this.objClass = objClass;
	}
	
	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	public String getTransition() {
		return transition;
	}

	public void setTransition(String transition) {
		this.transition = transition;
	}

	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
	}

	public List<Validator> getValidators() {
		return validators;
	}

	public void setValidators(List<Validator> validators) {
		this.validators = validators;
	}
	
	/**
	 * Conversion from FormElement to FormElementComplex.
	 */
	public static FormElementComplex toFormElementComplex(FormElement fe) {
		log.debug("toFormElementComplex({})", fe);
		FormElementComplex fec = new FormElementComplex();
		
		fec.setHeight(fe.getHeight());
		fec.setWidth(fe.getWidth());
		fec.setLabel(fe.getLabel());
		fec.setName(fe.getName());
		
		if (fe instanceof Input) {
			Input i = (Input) fe;
			fec.setType(i.getType());
			fec.setValue(i.getValue());
			fec.setReadonly(i.isReadonly());
			fec.setObjClass(i.getClass().getName());
		} else if (fe instanceof SuggestBox) {
			SuggestBox sb = (SuggestBox) fe;
			fec.setValue(sb.getValue());
			fec.setReadonly(sb.isReadonly());
			fec.setObjClass(sb.getClass().getName());
		} else if (fe instanceof TextArea) {
			TextArea ta = (TextArea) fe;
			fec.setValue(ta.getValue());
			fec.setReadonly(ta.isReadonly());
			fec.setObjClass(ta.getClass().getName());
		} else if (fe instanceof CheckBox) {
			CheckBox cb = (CheckBox) fe;
			fec.setValue(Boolean.toString(cb.getValue()));
			fec.setReadonly(cb.isReadonly());
			fec.setObjClass(cb.getClass().getName());
		} else if (fe instanceof Select) {
			Select s = (Select) fe;
			fec.setType(s.getType());
			fec.setOptions(s.getOptions());
			fec.setReadonly(s.isReadonly());
			fec.setObjClass(s.getClass().getName());
		} else if (fe instanceof Button) {
			Button b = (Button) fe;
			fec.setTransition(b.getTransition());
			fec.setObjClass(b.getClass().getName());
		}
		
		log.debug("toFormElementComplex: ", fec);
		return fec;
	}
	
	/**
	 * Conversion from FormElementComplex to FormElement.
	 */
	public static FormElement toFormElement(FormElementComplex fec) {
		log.debug("toFormElement({})", fec);
		FormElement fe = new FormElement();
				
		if (Input.class.getName().equals(fec.getObjClass())) {
			fe = new Input();
			((Input) fe).setType(fec.getType());
			((Input) fe).setValue(fec.getValue());
			((Input) fe).setReadonly(fec.isReadonly());
		} else if (SuggestBox.class.getName().equals(fec.getObjClass())) {
			fe = new SuggestBox();
			((SuggestBox) fe).setValue(fec.getValue());
			((SuggestBox) fe).setReadonly(fec.isReadonly());
		} else if (TextArea.class.getName().equals(fec.getObjClass())) {
			fe = new TextArea();
			((TextArea) fe).setValue(fec.getValue());
			((TextArea) fe).setReadonly(fec.isReadonly());
		} else if (CheckBox.class.getName().equals(fec.getObjClass())) {
			fe = new CheckBox();
			((CheckBox) fe).setValue(Boolean.valueOf(fec.getValue()));
			((CheckBox) fe).setReadonly(fec.isReadonly());
		} else if (Select.class.getName().equals(fec.getObjClass())) {
			fe = new Select();
			((Select) fe).setType(fec.getType());
			((Select) fe).setOptions(fec.getOptions());
			((Select) fe).setReadonly(fec.isReadonly());
		} else if (Button.class.getName().equals(fec.getObjClass())) {
			fe = new Button();
			((Button) fe).setTransition(fec.getTransition());
		}
		
		fe.setHeight(fec.getHeight());
		fe.setWidth(fec.getWidth());
		fe.setLabel(fec.getLabel());
		fe.setName(fec.getName());
		
		log.debug("toFormElement: {}", fe);
		return fe;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("label="); sb.append(label);
		sb.append(", name="); sb.append(name);
		sb.append(", width="); sb.append(width);
		sb.append(", height="); sb.append(height);
		sb.append(", type="); sb.append(type);
		sb.append(", value="); sb.append(value);
		sb.append(", readonly="); sb.append(readonly);
		sb.append(", options="); sb.append(options);
		sb.append(", validators="); sb.append(validators);
		sb.append("}");
		return sb.toString();
	}
}
