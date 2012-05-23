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

package com.openkm.dao;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.core.DatabaseException;
import com.openkm.dao.bean.QueryParams;
import com.openkm.dao.bean.Role;
import com.openkm.dao.bean.User;
import com.openkm.util.SecureStore;

//by vissu touch 24oct
import com.openkm.bean.Folder;
import com.openkm.module.direct.DirectFolderModule;
import com.openkm.core.LockException;
import com.openkm.api.OKMAuth;
import com.openkm.core.Config;


//by vissu touch 24oct
import com.openkm.jcr.JCRUtils;
import com.openkm.core.JcrSessionManager;



public class AuthDAO {
	private static Logger log = LoggerFactory.getLogger(AuthDAO.class);

	private AuthDAO() {}
			
	/**
	 * Create user in database
	 */
	public static void createUser(User user) throws DatabaseException {
		log.debug("createUser({})", user);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			user.setPassword(SecureStore.md5Encode(user.getPassword().getBytes()));
			session.save(user);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
		
		log.debug("createUser: void");
	}

	/**
	 * Update user in database
	 */
	public static void updateUser(User user) throws DatabaseException {
		log.debug("updateUser({})", user);
		String qs = "select u.password from User u where u.id=:id";
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(qs);
			q.setParameter("id", user.getId());
			String password = (String) q.setMaxResults(1).uniqueResult();
			user.setPassword(password);
			session.update(user);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
		
		log.debug("updateUser: void");
	}
	
	/**
	 * Active user in database
	 */
	public static void activeUser(String usrId, boolean active) throws DatabaseException {
		log.debug("activeUser({}, {})", usrId, active);
		String qs = "update User u set u.active=:active where u.id=:id";
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(qs);
			q.setBoolean("active", active);
			q.setString("id", usrId);
			q.executeUpdate();
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
		
		log.debug("activeUser: void");
	}
	
	/**
	 * Update user password in database 
	 */
	public static void updateUserPassword(String usrId, String usrPassword) throws DatabaseException {
		log.debug("updateUserPassword({}, {})", usrId, usrPassword);
		String qs = "update User u set u.password=:password where u.id=:id";
		Session session = null;
		Transaction tx = null;
		
		try {
			if (usrPassword != null && usrPassword.trim().length() > 0) {
				session = HibernateUtil.getSessionFactory().openSession();
				tx = session.beginTransaction();
				Query q = session.createQuery(qs);
				q.setString("password", SecureStore.md5Encode(usrPassword.getBytes()));
				q.setString("id", usrId);
				q.executeUpdate();
				HibernateUtil.commit(tx);
			}
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
		
		log.debug("updateUserPassword: void");
	}
	
	/**
	 * Update user email in database
	 */
	public static void updateUserEmail(String usrId, String usrEmail) throws DatabaseException {
		log.debug("updateUserEmail({}, {})", usrId, usrEmail);
		String qs = "update User u set u.email=:email where u.id=:id";
		Session session = null;
		Transaction tx = null;
		
		try {
			if (usrEmail != null && usrEmail.trim().length() > 0) {
				session = HibernateUtil.getSessionFactory().openSession();
				tx = session.beginTransaction();
				Query q = session.createQuery(qs);
				q.setString("email", usrEmail);
				q.setString("id", usrId);
				q.executeUpdate();
				HibernateUtil.commit(tx);
			}
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
		
		log.debug("updateUserEmail: void");
	}
	
	/**
	 * Delete user from database
	 */
	@SuppressWarnings("unchecked")
	public static void deleteUser(String usrId) throws DatabaseException {
		log.debug("deleteUser({})", usrId);
		String qsMail = "delete from MailAccount ma where ma.user=:user";
		String qsTwitter = "delete from TwitterAccount ta where ta.user=:user";
		String qsBookmark = "delete from Bookmark bm where bm.user=:user";
		String qsConfig = "delete from UserConfig uc where uc.user=:user";
		String qsItems = "delete from UserItems ui where ui.user=:user";
		String qsSharedQuery = "from QueryParams qp where :user in elements(qp.shared)";
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			User user = (User) session.load(User.class, usrId);
			session.delete(user);
						
			Query qMail = session.createQuery(qsMail);
			qMail.setString("user", usrId);
			qMail.executeUpdate();
			
			Query qTwitter = session.createQuery(qsTwitter);
			qTwitter.setString("user", usrId);
			qTwitter.executeUpdate();
			
			Query qBookmark = session.createQuery(qsBookmark);
			qBookmark.setString("user", usrId);
			qBookmark.executeUpdate();
			
			Query qConfig = session.createQuery(qsConfig);
			qConfig.setString("user", usrId);
			qConfig.executeUpdate();
			
			Query qItems = session.createQuery(qsItems);
			qItems.setString("user", usrId);
			qItems.executeUpdate();
			
			Query qSharedQuery = session.createQuery(qsSharedQuery);
			qSharedQuery.setString("user", usrId);
			for (QueryParams qp : (List<QueryParams>) qSharedQuery.list()) {
				qp.getShared().remove(usrId);
				session.update(qp);
			}
			
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
		
		log.debug("deleteUser: void");
	}
	
	/**
	 * Get all users in database
	 */
	@SuppressWarnings("unchecked")
	public static List<User> findAllUsers(boolean filterByActive) throws DatabaseException {
		log.debug("findAllUsers({})", filterByActive);
		String qs = "from User u "+(filterByActive?"where u.active=:active":"")+" order by u.id";
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			
			if (filterByActive) {
				q.setBoolean("active", true);
			}
			
			List<User> ret = q.list();
			log.debug("findAllUsers: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Get all users within a role
	 */
	@SuppressWarnings("unchecked")
	public static List<User> findUsersByRole(String rolId, boolean filterByActive) throws DatabaseException {
		log.debug("findUsersByRole({}, {})", rolId, filterByActive);
		String qs = "select u from User u, Role r where r.id=:rolId and r in elements(u.roles) " + 
			(filterByActive?"and u.active=:active":"")+" order by u.id";
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("rolId", rolId);
			
			if (filterByActive) {
				q.setBoolean("active", true);
			}
			
			List<User> ret = q.list();
			log.debug("findUsersByRole: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	/**
	 * Get all users within a role
	 */
	@SuppressWarnings("unchecked")
	public static List<Role> findRolesByUser(String usrId, boolean filterByActive) throws DatabaseException {
		log.debug("findRolesByUser({}, {})", usrId, filterByActive);
		String qs = "select r from User u, Role r where u.id=:usrId and r in elements(u.roles) " + 
			(filterByActive?"and r.active=:active":"")+" order by r.id";
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("usrId", usrId);
			
			if (filterByActive) {
				q.setBoolean("active", true);
			}
			
			List<Role> ret = q.list();
			log.debug("findRolesByUser: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	/**
	 * Get user from database
	 */
	public static User findUserByPk(String usrId) throws DatabaseException {
		log.debug("findUserByPk({})", usrId);
		String qs = "from User u where u.id=:id";
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("id", usrId);
			User ret = (User) q.setMaxResults(1).uniqueResult();
			log.debug("findUserByPk: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Create role in database
	 */
	public static void createRole(Role vo) throws DatabaseException {
		log.debug("createRole({})", vo);
		Session session = null;
		Transaction tx = null;

		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.save(vo);
			HibernateUtil.commit(tx);
			
			
			//create folder ******************************************* by vissu
			String role = null;

			role = vo.getId();
			
			if (Config.DEFAULT_GROUP_ADMIN_ROLE.equals(role)) {
			
			//by vissu
			Folder roleFolder = null;
			DirectFolderModule dfm =null;
			//by vissu
		//vissu create folder
		/*roleFolder = new Folder();
		roleFolder.setHasChilds(false);
		roleFolder.setAuthor("okmAdmin");
		roleFolder.setPath("/okm:root/");
		roleFolder.setPermissions((byte) 3);
		roleFolder.setSubscribed(false);
		roleFolder.setUuid(null);*/
			
		dfm = new DirectFolderModule();
        JcrSessionManager sm = JcrSessionManager.getInstance();
		
		/****************PROJECT MANAGER FOLDER*****************/
		roleFolder = new Folder();
		roleFolder.setHasChilds(false);
		roleFolder.setAuthor("okmAdmin");
		roleFolder.setPath("/okm:root/Project Manager");
		roleFolder.setPermissions((byte) 3);
		roleFolder.setSubscribed(false);
		roleFolder.setUuid(null);
		dfm.create(sm.getSystemToken(), roleFolder);
		
		String[] projecmanagement={"Project Management","Business Files","Control & Automation","Building","Services","Sundry Activities","Area 1","Area 2","Area 3","Area 4","Area 5"};
		    for(int i=0;i<11;i++){
		  
				roleFolder = new Folder();
				roleFolder.setHasChilds(false);
				roleFolder.setAuthor("okmAdmin");
				roleFolder.setPath("/okm:root/Project Manager/"+projecmanagement[i]);
				roleFolder.setPermissions((byte) 3);
				roleFolder.setSubscribed(false);
				roleFolder.setUuid(null);
				dfm.create(sm.getSystemToken(), roleFolder);
				
		    }
		/****************PROJECT MANAGER FOLDER*****************/

			/****************FACTORY MANAGER FOLDER*****************/
			roleFolder = new Folder();
			roleFolder.setHasChilds(false);
			roleFolder.setAuthor("okmAdmin");
			roleFolder.setPath("/okm:root/Factory Manager");
			roleFolder.setPermissions((byte) 3);
			roleFolder.setSubscribed(false);
			roleFolder.setUuid(null);
			dfm.create(sm.getSystemToken(), roleFolder);
			
			String[] factorymanagement={"HACCP","OH&S","Quality","Key Performance Indicators","Human Resource","Training","Reliability or Maintenance","Continuous Improvement","Equipment Information- Line A","Equipment Information- Line B","Equipment Information- Line C","Equipment Information- Line D","Equipment Information- Line E"};
			    for(int i=0;i<13;i++){
			  
					roleFolder = new Folder();
					roleFolder.setHasChilds(false);
					roleFolder.setAuthor("okmAdmin");
					roleFolder.setPath("/okm:root/Factory Manager/"+factorymanagement[i]);
					roleFolder.setPermissions((byte) 3);
					roleFolder.setSubscribed(false);
					roleFolder.setUuid(null);
					dfm.create(sm.getSystemToken(), roleFolder);
					
			    }
			/****************FACTORY MANAGER FOLDER*****************/

				/****************MAINTENANCE MANAGER FOLDER*****************/
				roleFolder = new Folder();
				roleFolder.setHasChilds(false);
				roleFolder.setAuthor("okmAdmin");
				roleFolder.setPath("/okm:root/Maintenance Manager");
				roleFolder.setPermissions((byte) 3);
				roleFolder.setSubscribed(false);
				roleFolder.setUuid(null);
				dfm.create(sm.getSystemToken(), roleFolder);
				
				String[] maintenancemanagement={"OH&S","Quality","Key Performance Indicators","Human Resource","Training","Spare Parts Information","Equipment Information- Line A","Equipment Information- Line B","Equipment Information- Line C","Equipment Information- Line D","Equipment Information- Line E"};
				    for(int i=0;i<11;i++){
				  
						roleFolder = new Folder();
						roleFolder.setHasChilds(false);
						roleFolder.setAuthor("okmAdmin");
						roleFolder.setPath("/okm:root/Maintenance Manager/"+maintenancemanagement[i]);
						roleFolder.setPermissions((byte) 3);
						roleFolder.setSubscribed(false);
						roleFolder.setUuid(null);
						dfm.create(sm.getSystemToken(), roleFolder);
						
				    }
				/****************MAINTENANCE MANAGER FOLDER*****************/					    
		
		//revoke UserRole recursively from root
	    OKMAuth.getInstance().revokeRole(sm.getSystemToken(), "/okm:root", "UserRole" , 8, true);
	    OKMAuth.getInstance().revokeRole(sm.getSystemToken(), "/okm:root", "UserRole" , 4, true);
	    OKMAuth.getInstance().revokeRole(sm.getSystemToken(), "/okm:root", "UserRole" , 2, true);
	    OKMAuth.getInstance().revokeRole(sm.getSystemToken(), "/okm:root", "UserRole" , 1, true);

	    OKMAuth.getInstance().grantRole(sm.getSystemToken(), "/okm:root", "UserRole" , 1, false);
	    
	    OKMAuth.getInstance().grantRole(sm.getSystemToken(), "/okm:root/Project Manager", role , 1, true);
	    OKMAuth.getInstance().grantRole(sm.getSystemToken(), "/okm:root/Project Manager", role , 2, true);
	    OKMAuth.getInstance().grantRole(sm.getSystemToken(), "/okm:root/Project Manager", role , 4, true);
	    OKMAuth.getInstance().grantRole(sm.getSystemToken(), "/okm:root/Project Manager", role , 8, true);

	    OKMAuth.getInstance().grantRole(sm.getSystemToken(), "/okm:root/Factory Manager", role , 1, true);
	    OKMAuth.getInstance().grantRole(sm.getSystemToken(), "/okm:root/Factory Manager", role , 2, true);
	    OKMAuth.getInstance().grantRole(sm.getSystemToken(), "/okm:root/Factory Manager", role , 4, true);
	    OKMAuth.getInstance().grantRole(sm.getSystemToken(), "/okm:root/Factory Manager", role , 8, true);

	    OKMAuth.getInstance().grantRole(sm.getSystemToken(), "/okm:root/Maintenance Manager", role , 1, true);
	    OKMAuth.getInstance().grantRole(sm.getSystemToken(), "/okm:root/Maintenance Manager", role , 2, true);
	    OKMAuth.getInstance().grantRole(sm.getSystemToken(), "/okm:root/Maintenance Manager", role , 4, true);
	    OKMAuth.getInstance().grantRole(sm.getSystemToken(), "/okm:root/Maintenance Manager", role , 8, true);
	    
			}
			//end of modification by vissu on 24oct
			
		}catch (Exception e) {
            e.printStackTrace();
		} finally {
			HibernateUtil.close(session);
		}
		
		log.debug("createRole: void");
	}
	
	/**
	 * Update role in database
	 */
	public static void updateRole(Role role) throws DatabaseException {
		log.debug("updateRole({})", role);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			session.update(role);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
		
		log.debug("updateRole: void");
	}
	
	/**
	 * Active role in database
	 */
	public static void activeRole(String rolId, boolean active) throws DatabaseException {
		log.debug("activeRole({}, {})", rolId, active);
		String qs = "update Role r set r.active=:active where r.id=:id";
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Query q = session.createQuery(qs);
			q.setBoolean("active", active);
			q.setString("id", rolId);
			q.executeUpdate();
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
		
		log.debug("activeRole: void");
	}

	/**
	 * Delete role from database
	 */
	public static void deleteRole(String rolId) throws DatabaseException {
		log.debug("deleteRole({})", rolId);
		String qs = "delete from OKM_USER_ROLE where UR_ROLE=:rolId";
		Session session = null;
		Transaction tx = null;
		
		try {			
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			Role role = (Role) session.load(Role.class, rolId);
			session.delete(role);
			
			// TODO: Make Hibernate handle this relation.
			SQLQuery q = session.createSQLQuery(qs);
			q.setString("rolId", rolId);
			q.executeUpdate();
			
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
		
		log.debug("deleteRole: void");
	}

	/**
	 * Get all roles in database
	 */
	@SuppressWarnings("unchecked")
	public static List<Role> findAllRoles() throws DatabaseException {
		log.debug("findAllRoles()");
		String qs = "from Role r order by r.id";
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			List<Role> ret = q.list();
			log.debug("findAllRoles: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}
	
	/**
	 * Find role by pk
	 */
	public static Role findRoleByPk(String rolId) throws DatabaseException {
		log.debug("findRoleByPk({})", rolId);
		String qs = "from Role r where r.id=:id";
		Session session = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			Query q = session.createQuery(qs);
			q.setString("id", rolId);
			Role ret = (Role) q.setMaxResults(1).uniqueResult();
			log.debug("findRoleByPk: {}", ret);
			return ret;
		} catch (HibernateException e) {
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
	}

	/**
	 * Grant role to user
	 */
	public static void grantRole(String usrId, String rolId) throws DatabaseException {
		log.debug("grantRole({}, {})", usrId, rolId);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			User user = (User) session.load(User.class, usrId);
			Role role = (Role) session.load(Role.class, rolId);
			user.getRoles().add(role);
			session.update(user);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
		
		log.debug("grantRole: void");
	}

	/**
	 * Revoke role from user
	 */
	public void revokeRole(String usrId, String rolId) throws DatabaseException {
		log.debug("revokeRole({}, {})", usrId, rolId);
		Session session = null;
		Transaction tx = null;
		
		try {
			session = HibernateUtil.getSessionFactory().openSession();
			tx = session.beginTransaction();
			User user = (User) session.load(User.class, usrId);
			Role role = (Role) session.load(Role.class, rolId);
			user.getRoles().remove(role);
			session.update(user);
			HibernateUtil.commit(tx);
		} catch (HibernateException e) {
			HibernateUtil.rollback(tx);
			throw new DatabaseException(e.getMessage(), e);
		} finally {
			HibernateUtil.close(session);
		}
		
		log.debug("revokeRole: void");
	}
}
