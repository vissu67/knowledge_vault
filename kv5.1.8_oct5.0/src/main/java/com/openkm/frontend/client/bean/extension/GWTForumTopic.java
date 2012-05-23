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
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * GWTForumTopic
 * 
 * @author jllort
 *
 */
public class GWTForumTopic implements IsSerializable {
	private static final long serialVersionUID = 1L;
	private int id;
	private String title;
	private Date date;
	private String user;
	private String uuid;
	private int replies;
	private int views;
	private String lastPostUser;
	private Date lastPostDate;
	private Set<GWTForumPost> posts = new LinkedHashSet<GWTForumPost>();
	
	public String getLastPostUser() {
		return lastPostUser;
	}

	public void setLastPostUser(String lastPostUser) {
		this.lastPostUser = lastPostUser;
	}

	public Date getLastPostDate() {
		return lastPostDate;
	}

	public void setLastPostDate(Date lastPostDate) {
		this.lastPostDate = lastPostDate;
	}

	public int getReplies() {
		return replies;
	}

	public void setReplies(int replies) {
		this.replies = replies;
	}

	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public Set<GWTForumPost> getPosts() {
		return posts;
	}
	
	public void setPosts(Set<GWTForumPost> posts) {
		this.posts = posts;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
}
