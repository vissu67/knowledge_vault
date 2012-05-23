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

package com.openkm.frontend.client.service.extension;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.extension.GWTForum;
import com.openkm.frontend.client.bean.extension.GWTForumPost;
import com.openkm.frontend.client.bean.extension.GWTForumTopic;

/**
 * OKMForumService
 * 
 * @author jllort
 *
 */
@RemoteServiceRelativePath("../extension/Forum")
public interface OKMForumService extends RemoteService {
	public List<GWTForumTopic> getTopicsByForum(int id) throws OKMException;
	public List<GWTForumTopic> getTopicsByUuid(String uuid) throws OKMException;
	public GWTForumTopic createTopic(int id, String uuid, GWTForumTopic topic) throws OKMException;
	public GWTForumTopic findTopicByPK(int id) throws OKMException;
	public void createPost(int forumId, int topicId, GWTForumPost post) throws OKMException;
	public void increaseTopicView(int id) throws OKMException;
	public Boolean deletePost(int forumId, int topicId, int postId) throws OKMException;
	public void updatePost(GWTForumPost post) throws OKMException;
	public List<GWTForum> getAllForum() throws OKMException;
	public GWTForum createForum(GWTForum forum) throws OKMException;
	public void deleteForum(int id) throws OKMException;
	public void updateForum(GWTForum forum) throws OKMException;
	public void updateTopic(int id, GWTForumPost post) throws OKMException;
}