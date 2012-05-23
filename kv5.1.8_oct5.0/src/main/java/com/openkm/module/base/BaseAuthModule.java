package com.openkm.module.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.Config;
import com.openkm.principal.PrincipalAdapter;
import com.openkm.principal.PrincipalAdapterException;

public class BaseAuthModule {
	private static Logger log = LoggerFactory.getLogger(BaseAuthModule.class);
	private static PrincipalAdapter principalAdapter = null;
	
	/**
	 * Singleton pattern for global Principal Adapter.
	 */
	public static PrincipalAdapter getPrincipalAdapter() throws PrincipalAdapterException {
		if (principalAdapter == null) {
			try {
				log.info("PrincipalAdapter: {}", Config.PRINCIPAL_ADAPTER);
				Object object = Class.forName(Config.PRINCIPAL_ADAPTER).newInstance();
				principalAdapter = (PrincipalAdapter) object;
			} catch (ClassNotFoundException e) {
				log.error(e.getMessage(), e);
				throw new PrincipalAdapterException(e.getMessage(), e);
			} catch (InstantiationException e) {
				log.error(e.getMessage(), e);
				throw new PrincipalAdapterException(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				log.error(e.getMessage(), e);
				throw new PrincipalAdapterException(e.getMessage(), e);
			}
		}

		return principalAdapter;
	}
}
