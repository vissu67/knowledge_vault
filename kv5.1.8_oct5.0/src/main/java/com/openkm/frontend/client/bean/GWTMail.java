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

package com.openkm.frontend.client.bean;

import java.util.Collection;
import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * GWTMail
 * 
 * @author jllort
 *
 */
public class GWTMail implements IsSerializable {
		
	private String path;
	private String uuid;
	private byte permissions;
	private String from;
	private String[] reply = new String[]{};
	private String[] to;
	private String[] cc;
	private String[] bcc;
	private Date sentDate;
	private Date receivedDate;
	private String subject;
	private String content;
	private long size;
	private Collection<GWTDocument> attachments;
	private String parent;
	private String mimeType;

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public byte getPermissions() {
		return permissions;
	}

	public void setPermissions(byte permissions) {
		this.permissions = permissions;
	}
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String[] getReply() {
		return reply;
	}

	public void setReply(String[] reply) {
		this.reply = reply;
	}

	public String[] getTo() {
		return to;
	}

	public void setTo(String[] to) {
		this.to = to;
	}

	public String[] getCc() {
		return cc;
	}

	public void setCc(String[] cc) {
		this.cc = cc;
	}

	public String[] getBcc() {
		return bcc;
	}

	public void setBcc(String[] bcc) {
		this.bcc = bcc;
	}

	public Date getSentDate() {
		return sentDate;
	}

	public void setSentDate(Date sentDate) {
		this.sentDate = sentDate;
	}

	public Date getReceivedDate() {
		return receivedDate;
	}

	public void setReceivedDate(Date receivedDate) {
		this.receivedDate = receivedDate;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
	
	public Collection<GWTDocument> getAttachments() {
		return attachments;
	}

	public void setAttachments(Collection<GWTDocument> attachments) {
		this.attachments = attachments;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		sb.append("path="); sb.append(path);
		sb.append(", uuid="); sb.append(uuid);
		sb.append(", permissions="); sb.append(permissions);
		sb.append(", size="); sb.append(size);
		sb.append(", from="); sb.append(from);
		sb.append(", reply="); sb.append(reply);
		sb.append(", to="); sb.append(to);
		sb.append(", sentDate="); sb.append(sentDate==null?null:sentDate.getTime());
		sb.append(", receivedDate="); sb.append(receivedDate==null?null:receivedDate.getTime());
		sb.append(", subject="); sb.append(subject);
		sb.append(", content="); sb.append(content);
		sb.append(", attachments="); sb.append(attachments);
		sb.append("]");
		return sb.toString();
	}
}