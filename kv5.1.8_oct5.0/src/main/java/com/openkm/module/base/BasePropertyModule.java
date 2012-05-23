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

import javax.jcr.Node;
import javax.jcr.PropertyType;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Property;
import com.openkm.core.Config;

public class BasePropertyModule {
	private static Logger log = LoggerFactory.getLogger(BasePropertyModule.class);

	/**
	 * Add category 
	 */
	public static void addCategory(Session session, Node node, String catId) throws ValueFormatException,
			javax.jcr.PathNotFoundException, javax.jcr.RepositoryException  {
		log.debug("addCategory({}, {}, {})", new Object[] { session, node, catId });
		
		synchronized (node) {
			Value[] property = node.getProperty(Property.CATEGORIES).getValues();
			Value[] newProperty = new Value[property.length+1];
			boolean alreadyAdded = false;
			
			for (int i=0; i<property.length; i++) {
				newProperty[i] = property[i];
				
				if (property[i].getString().equals(catId)) {
					alreadyAdded = true;
				}
			}
			
			if (!alreadyAdded) {
				Node reference = session.getNodeByUUID(catId);
				newProperty[newProperty.length-1] = session.getValueFactory().createValue(reference);
				node.setProperty(Property.CATEGORIES, newProperty, PropertyType.REFERENCE);
				node.save();
			}
		}
		
		log.debug("addCategory: void");
	}

	/**
	 * Remove category
	 */
	public static void removeCategory(Session session, Node node, String catId) throws ValueFormatException,
			javax.jcr.PathNotFoundException, javax.jcr.RepositoryException {
		log.debug("removeCategory({}, {}, {})", new Object[] { session, node, catId });
		boolean removed = false;
		
		synchronized (node) {
			Value[] property = node.getProperty(Property.CATEGORIES).getValues();
			ArrayList<Value> newProperty = new ArrayList<Value>();
				
			for (int i=0; i<property.length; i++) {
				if (!property[i].getString().equals(catId)) {
					newProperty.add(property[i]);
				} else {
					removed = true;
				}
			}
			
			if (removed) {
				node.setProperty(Property.CATEGORIES, (Value[])newProperty.toArray(new Value[newProperty.size()]), PropertyType.REFERENCE);
				node.save();
			}
		}

		log.debug("removeCategory: void");
	}

	/**
	 * Add keyword
	 */
	public static String addKeyword(Session session, Node node, String keyword) throws ValueFormatException,
			javax.jcr.PathNotFoundException, javax.jcr.RepositoryException  {
		log.debug("addKeyword({}, {}, {})", new Object[] { session, node, keyword });
		
		synchronized (node) {
			Value[] property = node.getProperty(Property.KEYWORDS).getValues();
			Value[] newProperty = new Value[property.length+1];
			boolean alreadyAdded = false;
			
			if (Config.SYSTEM_KEYWORD_LOWERCASE) {
				keyword = keyword.toLowerCase();
			}
			
			for (int i=0; i<property.length; i++) {
				newProperty[i] = property[i];
				
				if (property[i].equals(keyword)) {
					alreadyAdded = true;
				}
			}
			
			if (!alreadyAdded) {
				newProperty[newProperty.length-1] = session.getValueFactory().createValue(keyword);
				node.setProperty(Property.KEYWORDS, newProperty);
				node.save();
			}
		}
		
		log.debug("addKeyword: {}", keyword);
		return keyword;
	}
	
	/**
	 * Remove keyword
	 */
	public static void removeKeyword(Session session, Node node, String keyword) throws ValueFormatException,
			javax.jcr.PathNotFoundException, javax.jcr.RepositoryException {
		log.debug("removeKeyword({}, {}, {})", new Object[] { session, node, keyword });
		boolean removed = false;
		
		synchronized (node) {
			Value[] property = node.getProperty(Property.KEYWORDS).getValues();
			ArrayList<Value> newProperty = new ArrayList<Value>();
			
			for (int i=0; i<property.length; i++) {
				if (!property[i].getString().equals(keyword)) {
					newProperty.add(property[i]);
				} else {
					removed = true;
				}
			}
			
			if (removed) {
				node.setProperty(Property.KEYWORDS, (Value[])newProperty.toArray(new Value[newProperty.size()]));
				node.save();
			}
		}
		
		log.debug("removeKeyword: void");
	}
}
