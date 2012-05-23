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

package com.openkm.webdav;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.jackrabbit.webdav.simple.ItemFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultItemFilter implements ItemFilter {
	private static Logger log = LoggerFactory.getLogger(DefaultItemFilter.class);
	
	@Override
	public void setFilteredURIs(String[] uris) {
	}

	@Override
	public void setFilteredPrefixes(String[] prefixes) {
	}

	@Override
	public void setFilteredNodetypes(String[] nodetypeNames) {
	}

	@Override
	public boolean isFilteredItem(Item item) {
		log.debug("isFilteredItem({})", item);
		
		try {
			if (item.isNode() && ((Node) item).isNodeType("okm:notes")) {
				return true;
			} else if ("jcr:system".equals(item.getName())) {
				return true;
			} else if ("okm:config".equals(item.getName())) {
				return true;
			} else if ("okm:thesaurus".equals(item.getName())) {
				return true;
			} else if ("okm:categories".equals(item.getName())) {
				return true;
			} else if ("okm:mail".equals(item.getName())) {
				return true;
			} else if ("okm:trash".equals(item.getName())) {
				return true;
			}
		} catch (RepositoryException e) {
			// Silent
		}
		
		return false;
	}

	@Override
	public boolean isFilteredItem(String name, Session session) {
		//log.debug("isFilteredItem({}, {})", name, session);
		return false;
	}
}
