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

package com.openkm.servlet.frontend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.api.OKMUserConfig;
import com.openkm.core.DatabaseException;
import com.openkm.core.RepositoryException;
import com.openkm.dao.bean.UserConfig;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.GWTUserConfig;
import com.openkm.frontend.client.contants.service.ErrorCode;
import com.openkm.frontend.client.service.OKMUserConfigService;
import com.openkm.util.GWTUtil;

/**
 * Servlet Class
 * 
 * @web.servlet              name="UserConfigServlet"
 *                           display-name="Directory tree service"
 *                           description="Directory tree service"
 * @web.servlet-mapping      url-pattern="/UserConfigServlet"
 * @web.servlet-init-param   name="A parameter"
 *                           value="A value"
 */
public class UserConfigServlet extends OKMRemoteServiceServlet implements OKMUserConfigService {
	private static Logger log = LoggerFactory.getLogger(UserConfigServlet.class);
	private static final long serialVersionUID = 1L;
	
	@Override
	public void setUserHome(String path) throws OKMException {
		log.debug("setUserHome({})", path);
		updateSessionManager();
		
		try {
			OKMUserConfig.getInstance().setHome(null, path);
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMUserCopyService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMUserCopyService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMUserCopyService, ErrorCode.CAUSE_General), e.getMessage());
		}
		
		log.debug("setUserHome: void");
	}
	
	@Override
	public GWTUserConfig getUserHome() throws OKMException {
		log.debug("getUserHome()");
		updateSessionManager();
		
		try {
			UserConfig config = OKMUserConfig.getInstance().getConfig(null);
			// Any home that will not have okm:root parent needs reseting ( normally because node is deleted to /okm:trash )
			if (!config.getHomePath().startsWith("/okm:root")) {
				OKMUserConfig.getInstance().setHome(null, "/okm:root");
				config = OKMUserConfig.getInstance().getConfig(null);
			}
			return GWTUtil.copy(config);
		} catch (RepositoryException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMUserCopyService, ErrorCode.CAUSE_Repository), e.getMessage());
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMUserCopyService, ErrorCode.CAUSE_Database), e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMUserCopyService, ErrorCode.CAUSE_General), e.getMessage());
		}
	}
}
