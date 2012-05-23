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

package com.openkm.ws.endpoint;

import java.util.Arrays;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.jboss.annotation.security.SecurityDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.Note;

/**
 * Servlet Class
 * 
 * @web.servlet name="OKMTest"
 * @web.servlet-mapping url-pattern="/OKMTest"
 */

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
@SecurityDomain("OpenKM")
public class OKMTest {
	private static Logger log = LoggerFactory.getLogger(OKMTest.class);
	
	@WebMethod
	public void simple(@WebParam(name = "param1") String param1) {
		log.info("simple({})", param1);
	}
	
	@WebMethod
	public void complex(@WebParam(name = "note") Note note) {
		log.info("complex({})", note);
	}
	
	@WebMethod
	public String[] sort1(@WebParam(name = "a") String[] a) {
		log.info("sort1({})", a);
		if (a != null) log.info("sort1: a.length={}", a.length);
		Arrays.sort(a);
		return a;
	}
	
	@WebMethod
	public String[] sort2(@WebParam(name = "a") String[] a) {
		log.info("sort2({})", a);
		if (a != null) {
			log.info("sort2: a.value={}", a);
			if (a != null) log.info("sort2: a.length={}", a.length);
		}
		Arrays.sort(a);
		return a;
	}
	
	@WebMethod
	public String greetings(@WebParam(name = "name") String name) {
		log.info("greetings({})", name);
		return "Hello, "+name+"!";
	}
}
