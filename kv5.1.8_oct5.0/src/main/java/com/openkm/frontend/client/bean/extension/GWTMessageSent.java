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

package com.openkm.frontend.client.bean.extension;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * GWTMessageSent
 * 
 * @author jllort
 *
 */
public class GWTMessageSent implements IsSerializable {
	private static final long serialVersionUID = 1L;
	private Date sentDate;
	private GWTTextMessageSent textMessageSent;
	private GWTProposedQuerySent proposedQuerySent;
	private GWTProposedSubscriptionSent proposedSubscriptionSent;
	
	public GWTProposedSubscriptionSent getProposedSubscriptionSent() {
		return proposedSubscriptionSent;
	}

	public void setProposedSubscriptionSent(GWTProposedSubscriptionSent proposedSubscriptionSent) {
		this.proposedSubscriptionSent = proposedSubscriptionSent;
	}

	public GWTProposedQuerySent getProposedQuerySent() {
		return proposedQuerySent;
	}

	public void setProposedQuerySent(GWTProposedQuerySent proposedQuerySent) {
		this.proposedQuerySent = proposedQuerySent;
	}

	public GWTTextMessageSent getTextMessageSent() {
		return textMessageSent;
	}

	public void setTextMessageSent(GWTTextMessageSent textMessageSent) {
		this.textMessageSent = textMessageSent;
	}

	public Date getSentDate() {
		return sentDate;
	}
	
	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append(", sentDate="); sb.append(sentDate);
		sb.append("}");
		return sb.toString();
	}
}
