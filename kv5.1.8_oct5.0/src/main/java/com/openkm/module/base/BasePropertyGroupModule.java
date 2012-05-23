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

package com.openkm.module.base;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.nodetype.PropertyDefinition;
import javax.jcr.version.VersionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.form.CheckBox;
import com.openkm.bean.form.FormElement;
import com.openkm.bean.form.Input;
import com.openkm.bean.form.Option;
import com.openkm.bean.form.Select;
import com.openkm.bean.form.SuggestBox;
import com.openkm.bean.form.TextArea;
import com.openkm.core.ParseException;

public class BasePropertyGroupModule {
	private static Logger log = LoggerFactory.getLogger(BasePropertyGroupModule.class);

	public static void addGroup(Session session, Node node, String grpName) throws NoSuchNodeTypeException,
			VersionException, ConstraintViolationException, LockException, RepositoryException {
		log.debug("addGroup({}, {}, {})", new Object[] { session, node, grpName });
		synchronized (node) {
			node.addMixin(grpName);
			node.save();
		}
	}
	
	/**
	 * Set node property value
	 */
	public static void setPropertyValue(Node node, PropertyDefinition pd, FormElement fe) throws
			javax.jcr.PathNotFoundException, javax.jcr.RepositoryException, ParseException {
		Property prop = node.getProperty(pd.getName());
			
		if (fe instanceof Select && ((Select) fe).getType().equals(Select.TYPE_MULTIPLE) 
				&& pd.isMultiple()) {
			List<String> tmp = new ArrayList<String>();
			
			for (Option opt : ((Select) fe).getOptions()) {
				if (opt.isSelected()) {
					tmp.add(opt.getValue());
				}
			}
			
			prop.setValue(tmp.toArray(new String[tmp.size()]));
		} else if (!pd.isMultiple()) {
			if (fe instanceof Input) {
				prop.setValue(((Input) fe).getValue());
			} else if (fe instanceof SuggestBox) {
				prop.setValue(((SuggestBox) fe).getValue());
			} else if (fe instanceof CheckBox) {
				prop.setValue(Boolean.toString(((CheckBox) fe).getValue()));
			} else if (fe instanceof TextArea) {
				prop.setValue(((TextArea) fe).getValue());
			} else if (fe instanceof Select) {
				for (Option opt : ((Select) fe).getOptions()) {
					if (opt.isSelected()) {
						prop.setValue(opt.getValue());
					}
				}
			} else {
				throw new ParseException("Unknown property definition: " + pd.getName());
			}
		} else {
			throw new ParseException("Inconsistent property definition: " + pd.getName());
		}
	}
}
