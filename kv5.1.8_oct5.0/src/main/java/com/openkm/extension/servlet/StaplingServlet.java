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

package com.openkm.extension.servlet;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.DatabaseException;
import com.openkm.core.PathNotFoundException;
import com.openkm.core.RepositoryException;
import com.openkm.extension.dao.StapleGroupDAO;
import com.openkm.extension.dao.bean.Staple;
import com.openkm.extension.dao.bean.StapleGroup;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.extension.GWTStapleGroup;
import com.openkm.frontend.client.contants.service.ErrorCode;
import com.openkm.frontend.client.service.extension.OKMStaplingService;
import com.openkm.servlet.frontend.OKMRemoteServiceServlet;
import com.openkm.util.GWTUtil;

/**
 * Servlet Class
 */
public class StaplingServlet extends OKMRemoteServiceServlet implements OKMStaplingService {
	private static Logger log = LoggerFactory.getLogger(StaplingServlet.class);
	private static final long serialVersionUID = 395857404418870245L;
	
	@Override
	public String create(String user, String uuid, String type, String uuid2, String type2) throws OKMException {
		StapleGroup stapleGroup = new StapleGroup();
		stapleGroup.setUser(user);
		
		try {
			// Creating stapling group
			int id = StapleGroupDAO.create(stapleGroup);
			
			// Adding stapling elements
			stapleGroup = StapleGroupDAO.findByPk(id);
			Staple staple = new Staple();
			staple.setUuid(uuid);
			staple.setType(type);
			stapleGroup.getStaples().add(staple); // Added first
			staple = new Staple();
			staple.setUuid(uuid2);
			staple.setType(type2);
			stapleGroup.getStaples().add(staple); // Added second
			StapleGroupDAO.update(stapleGroup); 	// Updating
			return String.valueOf(id);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMStaplingService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}
	
	@Override
	public void add(String id, String uuid, String type) throws OKMException {
		try {
			StapleGroup stapleGroup = StapleGroupDAO.findByPk(Integer.valueOf(id));
			boolean found = false;
			
			for (Staple st : stapleGroup.getStaples()) {
				if (st.getUuid().equals(uuid)) {
					found = true;
					break;
				}
			}
			
			// Only we add if document not exists
			if (!found) {
				Staple staple = new Staple();
				staple.setUuid(uuid);
				staple.setType(type);
				stapleGroup.getStaples().add(staple); // Added first
				StapleGroupDAO.update(stapleGroup); // Updating
			}
		} catch (NumberFormatException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMStaplingService, ErrorCode.CAUSE_NumberFormat), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMStaplingService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}
	
	@Override
	public List<GWTStapleGroup> getAll(String uuid) throws OKMException {
		List<GWTStapleGroup> stapList = new ArrayList<GWTStapleGroup>();
		
		try {
			for (StapleGroup sg : StapleGroupDAO.findAll(uuid)) {
				stapList.add(GWTUtil.copy(sg));
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMStaplingService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMStaplingService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (PathNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMStaplingService, ErrorCode.CAUSE_PathNotFound), e.getMessage());
		}
		
		return stapList;
	}
	
	@Override
	public void removeAllStapleByUuid(String uuid) throws OKMException {
		try {
			List<String> idToDelete = new ArrayList<String>();
			
			for (StapleGroup sg : StapleGroupDAO.findAll(uuid)) {
				for (Staple staple : sg.getStaples()) {
					if (staple.getUuid().equals(uuid)) {
						idToDelete.add(String.valueOf(staple.getId()));
					}
				}
			}
			
			for (String id : idToDelete) {
				StapleGroupDAO.deleteStaple(Integer.parseInt(id));
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMStaplingService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMStaplingService, ErrorCode.CAUSE_Repository), e.getMessage());
		} 
	}
	
	@Override
	public void remove(String id) throws OKMException {
		try {
			StapleGroupDAO.delete(Integer.parseInt(id));
		} catch (NumberFormatException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMStaplingService, ErrorCode.CAUSE_NumberFormat), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMStaplingService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}
	
	@Override
	public void removeStaple(String id) throws OKMException {
		try {
			StapleGroupDAO.deleteStaple(Integer.parseInt(id));
		} catch (NumberFormatException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMStaplingService, ErrorCode.CAUSE_NumberFormat), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMStaplingService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}
}
