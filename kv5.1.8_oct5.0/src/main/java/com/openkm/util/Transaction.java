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

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import org.apache.jackrabbit.api.XASession;

public class Transaction {
	XAResource xares = null;
	Xid xid = null;
	
	public Transaction(XASession session) {
		xares = session.getXAResource();
		xid = XidFactory.createXid();
	}
	
	/**
	 * Start transaction
	 */
	public void start() {
		try {
			xares.start(xid, XAResource.TMNOFLAGS);
		} catch (XAException e) {
			e.printStackTrace();
		}
	}

	/**
	 * End transaction
	 */
	public void end() {
		try {
			xares.end(xid, XAResource.TMSUCCESS);
		} catch (XAException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Commit transaction
	 */
	public void commit() {
		try {
			if (xares.prepare(xid) == XAResource.XA_OK) {
				xares.commit(xid, false);
			}
		} catch (XAException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Commit transaction
	 */
	public void rollback() {
		try {
			if (xares.prepare(xid) == XAResource.XA_OK) {
				xares.rollback(xid);
			}
		} catch (XAException e1) {
			e1.printStackTrace();
		}
	}
}
