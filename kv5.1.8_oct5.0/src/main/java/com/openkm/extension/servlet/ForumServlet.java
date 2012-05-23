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
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

import com.openkm.core.Config;
import com.openkm.core.DatabaseException;
import com.openkm.extension.dao.ForumDAO;
import com.openkm.extension.dao.bean.Forum;
import com.openkm.extension.dao.bean.ForumPost;
import com.openkm.extension.dao.bean.ForumTopic;
import com.openkm.frontend.client.OKMException;
import com.openkm.frontend.client.bean.extension.GWTForum;
import com.openkm.frontend.client.bean.extension.GWTForumPost;
import com.openkm.frontend.client.bean.extension.GWTForumTopic;
import com.openkm.frontend.client.contants.service.ErrorCode;
import com.openkm.frontend.client.service.extension.OKMForumService;
import com.openkm.servlet.frontend.OKMRemoteServiceServlet;
import com.openkm.util.GWTUtil;

/**
 * ForumServlet
 * 
 * @author jllort
 */
public class ForumServlet extends OKMRemoteServiceServlet implements OKMForumService {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(ForumServlet.class);
	
	@Override
	public List<GWTForumTopic> getTopicsByForum(int id) throws OKMException {
		log.debug("getTopicsByForum({})", id);
		updateSessionManager();
		List<GWTForumTopic> topicList = new ArrayList<GWTForumTopic>();
		
		try {
			for (ForumTopic topic : ForumDAO.findByPk(id).getTopics()) {
				topicList.add(GWTUtil.copy(topic));
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMForumService, ErrorCode.CAUSE_Database), e.getMessage());
		}
		
		log.debug("getTopicsByUuid: {}", topicList);
		return topicList;
	}
	
	@Override
	public List<GWTForumTopic> getTopicsByUuid(String uuid) throws OKMException {
		log.debug("getTopicsByUuid({})", uuid);
		updateSessionManager();
		List<GWTForumTopic> topicList = new ArrayList<GWTForumTopic>(); 
		
		try {
			for (ForumTopic topic : ForumDAO.findAllTopicByUuid(uuid)) {
				topicList.add(GWTUtil.copy(topic));
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMForumService, ErrorCode.CAUSE_Database), e.getMessage());
		}
		
		log.debug("getTopicsByUuid: {}", topicList);
		return topicList;
	}
	
	@Override
	public GWTForumTopic createTopic(int id, String uuid, GWTForumTopic topic) throws OKMException {
		log.debug("createTopic({}, {}, {})", new Object[] { id, uuid, topic });
		updateSessionManager();
		
		try {
			Forum forum = ForumDAO.findByPk(id);
			topic.setDate(new Date());
			topic.setLastPostDate(topic.getDate());
			topic.setUuid(uuid);
			topic.setUser(getThreadLocalRequest().getRemoteUser());
			topic.setLastPostUser(topic.getUser());
			topic.setReplies(0);
			topic.setViews(0);
			GWTForumPost post = topic.getPosts().iterator().next();
			post.setDate(topic.getDate());
			post.setUser(topic.getUser());
			forum.getTopics().add(GWTUtil.copy(topic));
			forum.setNumTopics(forum.getNumTopics() + 1);
			forum.setNumPosts(forum.getNumPosts() + 1);
			ForumDAO.update(forum);
			ForumTopic ft = new ForumTopic();
			
			for (Iterator<ForumTopic> it = forum.getTopics().iterator(); it.hasNext();) {
				ft = it.next();
			}
			
			log.debug("getTopicsByUuid: {}", ft);
			return GWTUtil.copy(ft);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMForumService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}
	
	@Override
	public GWTForumTopic findTopicByPK(int id) throws OKMException {
		log.debug("findTopicByPK({})", id);
		updateSessionManager();
		
		try {
			return GWTUtil.copy(ForumDAO.findTopicByPk(id));
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMForumService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}
	
	@Override
	public void createPost(int forumId, int topicId, GWTForumPost post) throws OKMException {
		log.debug("createPost({}, {}, {})", new Object[] { forumId, topicId, post.getSubject() });
		updateSessionManager();
		
		try {
			post.setDate(new Date());
			post.setUser(getThreadLocalRequest().getRemoteUser());
			
			// Update topic
			ForumTopic topic = ForumDAO.findTopicByPk(topicId);
			topic.getPosts().add(GWTUtil.copy(post));
			topic.setReplies(topic.getReplies() + 1);
			Calendar cal = Calendar.getInstance();
			cal.setTime(post.getDate());
			topic.setLastPostDate(cal);
			topic.setLastPostUser(post.getUser());
			ForumDAO.update(topic);
			
			// Update forum
			Forum forum = ForumDAO.findByPk(forumId);
			forum.setNumPosts(forum.getNumPosts() + 1);
			forum.setLastPostDate(cal);
			forum.setLastPostUser(post.getUser());
			ForumDAO.update(forum);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMForumService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}
	
	@Override
	public void increaseTopicView(int id) throws OKMException {
		log.debug("increaseTopicView({})", id);
		updateSessionManager();
		
		try {
			ForumTopic topic = ForumDAO.findTopicByPk(id);
			topic.setViews(topic.getViews() + 1);
			ForumDAO.update(topic);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMForumService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}

	@Override
	public Boolean deletePost(int forumId, int topicId, int postId) throws OKMException {
		log.debug("deletePost({}, {})", topicId, postId);
		updateSessionManager();
		
		try {
			ForumTopic topic = ForumDAO.findTopicByPk(topicId);
			
			for (ForumPost post : topic.getPosts()) {
				if (post.getId() == postId) {
					topic.setReplies(topic.getReplies() - 1);
					topic.getPosts().remove(post);
					break;
				}
			}
			
			if (topic.getPosts().size() > 0) {
				ForumDAO.update(topic); // Deleting post
				Forum forum = ForumDAO.findByPk(forumId);
				forum.setNumPosts(forum.getNumPosts() - 1);
				ForumDAO.update(forum); // Updating forum
				return new Boolean(true);
			} else {
				Forum forum = ForumDAO.findByPk(forumId);
				forum.setNumPosts(forum.getNumPosts() - 1);
				forum.setNumTopics(forum.getNumTopics() - 1);
				
				for (ForumTopic fp : forum.getTopics()) {
					if (fp.getId() == topicId) {
						forum.getTopics().remove(fp);
						break;
					}
				}
				
				ForumDAO.update(forum); // Deleting topic
				return new Boolean(false);
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMForumService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}

	@Override
	public void updatePost(GWTForumPost post) throws OKMException {
		log.debug("updatePost({})", post.getId());
		updateSessionManager();
		
		try {
			ForumPost fp = ForumDAO.findPostByPk(post.getId());
			fp.setSubject(post.getSubject());
			fp.setMessage(post.getMessage());
			ForumDAO.update(fp);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMForumService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}

	@Override
	public List<GWTForum> getAllForum() throws OKMException {
		log.debug("getAllForum()");
		List<GWTForum> forumList = new ArrayList<GWTForum>();
		updateSessionManager();
		
		try {
			for (Forum forum : ForumDAO.findAll()) {
				// Only administrators can see first forum ( all document discussions )
				if (!getThreadLocalRequest().isUserInRole(Config.DEFAULT_ADMIN_ROLE)) {
					if (forum.getId()!=1) {
						forumList.add(GWTUtil.copy(forum));
					}
				} else {
					forumList.add(GWTUtil.copy(forum));
				}
			}
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMForumService, ErrorCode.CAUSE_Database), e.getMessage());
		}
		
		return forumList;
	}

	@Override
	public GWTForum createForum(GWTForum forum) throws OKMException {
		log.debug("createForum()");
		updateSessionManager();
		
		try {
			forum.setDate(new Date());
			forum.setLastPostDate(new Date());
			forum.setLastPostUser(getThreadLocalRequest().getRemoteUser());
			forum.setNumPosts(0);
			forum.setNumTopics(0);
			Forum f = GWTUtil.copy(forum);
			ForumDAO.create(f);
			return GWTUtil.copy(f);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMForumService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}

	@Override
	public void deleteForum(int id) throws OKMException {
		log.debug("deleteForum()");
		updateSessionManager();
		
		try {
			ForumDAO.delete(id);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMForumService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}

	@Override
	public void updateForum(GWTForum forum) throws OKMException {
		log.debug("updateForum()");
		updateSessionManager();
		
		try {
			Forum f = ForumDAO.findByPk(forum.getId());
			f.setName(forum.getName());
			f.setDescription(forum.getDescription());
			ForumDAO.update(f);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMForumService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}

	@Override
	public void updateTopic(int id, GWTForumPost post) throws OKMException {
		log.debug("updateTopic({}, {})", id,post.getId());
		updateSessionManager();
		
		try {
			// Update post
			ForumPost fp = ForumDAO.findPostByPk(post.getId());
			fp.setSubject(post.getSubject());
			fp.setMessage(post.getMessage());
			ForumDAO.update(fp);
			
			// Update topic
			ForumTopic ft = ForumDAO.findTopicByPk(id);
			ft.setTitle(post.getSubject()); // Updating the title
			ForumDAO.update(ft);
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new OKMException(ErrorCode.get(ErrorCode.ORIGIN_OKMForumService, ErrorCode.CAUSE_Database), e.getMessage());
		}
	}
}