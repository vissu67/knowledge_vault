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

package com.openkm.bean.form;

import java.util.ArrayList;
import java.util.List;

public class Select extends FormElement {
	private static final long serialVersionUID = 1L;
	public static final String TYPE_SIMPLE = "simple";
	public static final String TYPE_MULTIPLE = "multiple";
	private List<Validator> validators = new ArrayList<Validator>();
	private List<Option> options = new ArrayList<Option>();
	private String type = TYPE_SIMPLE;
	private String value = "";
	private String data = "";
	private boolean readonly = false;
	
	public Select() {
		super.width = "150px";
	}

	public List<Option> getOptions() {
		return options;
	}

	public void setOptions(List<Option> options) {
		this.options = options;
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
	
	public List<Validator> getValidators() {
		return validators;
	}

	public void setValidators(List<Validator> validators) {
		this.validators = validators;
	}
	
	public boolean isReadonly() {
		return readonly;
	}

	public void setReadonly(boolean readonly) {
		this.readonly = readonly;
	}
	
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("label="); sb.append(label);
		sb.append(", name="); sb.append(name);
		sb.append(", width="); sb.append(width);
		sb.append(", height="); sb.append(height);
		sb.append(", readonly="); sb.append(readonly);
		sb.append(", type="); sb.append(type);
		sb.append(", value="); sb.append(value);
		sb.append(", data="); sb.append(data);
		sb.append(", options="); sb.append(options);
		sb.append(", validators="); sb.append(validators);
		sb.append("}");
		return sb.toString();
	}
}
