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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.openkm.frontend.client.service.OKMChatService;
import com.openkm.util.UUIDGenerator;

/**
 * Servlet Class
 * 
 * @web.servlet              name="ChatServlet"
 *                           display-name="Directory tree service"
 *                           description="Directory tree service"
 * @web.servlet-mapping      url-pattern="/ChatServlet"
 * @web.servlet-init-param   name="A parameter"
 *                           value="A value"
 */
public class ChatServlet extends OKMRemoteServiceServlet implements OKMChatService {
	private static final long serialVersionUID = 3780857624687394918L;
	private static final int DELAY = 100; // mseg
	
	private static final int ACTION_LOGIN 					 		= 0;
	private static final int ACTION_LOGOUT 					 		= 1;
	private static final int ACTION_ADD_ROOM_TO_USER 		 		= 2;
	private static final int ACTION_REMOVE_USER_ROOM 		 		= 3;
	private static final int ACTION_ADD_PENDING_ROOM_TO_USER 		= 4;
	private static final int ACTION_GET_PENDING_USER_ROOM 	 		= 5;
	private static final int ACTION_GET_PENDING_USER_ROOM_MESSAGE 	= 6;
	private static final int ACTION_ADD_USER_MESSAGE_TO_ROOM	 	= 7;
	private static final int ACTION_CREATE_MESSAGE_ROOM	 			= 8;
	private static final int ACTION_CREATE_MESSAGE_USER_ROOM	 	= 9;
	private static final int ACTION_REMOVE_USER_MESSAGE_ROOM	 	= 10;
	private static final int ACTION_DELETE_EMPTY_MESSAGE_ROOM	 	= 11;
	private static final int ACTION_GET_USERS_IN_MESSAGE_ROOM	 	= 12;
	
	private static List<String> usersLogged = new ArrayList<String>();
	private static Map<String, List<String>> usersRooms= new HashMap<String, List<String>>(); // user is the key
	private static Map<String, List<String>> pendingUsersRooms= new HashMap<String, List<String>>(); // user is the key
	private static Map<String, HashMap<String,List<String>>> msgUsersRooms= new HashMap<String, HashMap<String,List<String>>>(); // room is the key
															// user is the subkey, messages are copied to each user
	
    @Override
    public void init(final ServletConfig config) throws ServletException {
    	super.init(config);
    }
    
    @Override
    public void login() {
    	usersLoggedAction(ACTION_LOGIN);
    }
    
    @Override
    public void logout() {
    	usersLoggedAction(ACTION_LOGOUT);
    }
    
    @Override
    public List<String> getLoggedUsers() {
    	return usersLogged;
    }
    
    @Override
    public String createNewChatRoom(String user) {
    	updateSessionManager();
    	String room = UUIDGenerator.generate(""); // Used to unique identifying room
    	String actualUser = getThreadLocalRequest().getRemoteUser();
    	// Add users to rooms
    	usersRoomAction(room, user, ACTION_ADD_ROOM_TO_USER);
    	pendingRoomAction(room, user, ACTION_ADD_PENDING_ROOM_TO_USER);
    	usersRoomAction(room, actualUser, ACTION_ADD_ROOM_TO_USER);
    	messageUserRoomAction(room,"","",ACTION_CREATE_MESSAGE_ROOM);
    	messageUserRoomAction(room, user, "", ACTION_CREATE_MESSAGE_USER_ROOM);
    	messageUserRoomAction(room, actualUser, "", ACTION_CREATE_MESSAGE_USER_ROOM);
    	return room;
    }
    
    @Override
    public List<String> getPendingMessage(String room) {
    	String user = getThreadLocalRequest().getRemoteUser();
		List<String> pendingMessages = new ArrayList<String>();
		int countCycle = 0;
		updateSessionManager();
		
		// 10 * Delay = 1000 = 1 second ( we want a 10 seconds waiting mantaining RPC comunication) that's 10*10=100 cycles
    	// With it mechanism
    	do  {
    		pendingMessages = messageUserRoomAction(room, user, "", ACTION_GET_PENDING_USER_ROOM_MESSAGE); 
			countCycle++;
    		try {
				Thread.sleep(DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	} while (pendingMessages.isEmpty() && (countCycle<100) && usersLogged.contains(user));
    	
    	return pendingMessages;
    }
    
    @Override
    public List<String> getPendingChatRoomUser() {
    	String user = getThreadLocalRequest().getRemoteUser();
    	List<String> pendingRooms = new ArrayList<String>();
    	int countCycle = 0;
    	updateSessionManager();
    	
    	// 10 * Delay = 1000 = 1 second ( we want a 10 seconds waiting mantaining RPC comunication) that's 10*10=100 cycles
    	// With it mechanism
    	do  {
    		pendingRooms = pendingRoomAction("", user, ACTION_GET_PENDING_USER_ROOM); 
			countCycle++;
    		try {
				Thread.sleep(DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} 
    	} while (pendingRooms.isEmpty() && (countCycle<100) && usersLogged.contains(user));
    	
    	return pendingRooms;
    }
    
    @Override
    public void addMessageToRoom(String room, String msg) {
    	updateSessionManager();
    	String user = getThreadLocalRequest().getRemoteUser();
    	messageUserRoomAction(room, user, msg, ACTION_ADD_USER_MESSAGE_TO_ROOM);
    }
    
    @Override
    public void closeRoom(String room) {
    	updateSessionManager();
    	String user = getThreadLocalRequest().getRemoteUser();
    	usersRoomAction(room, user, ACTION_REMOVE_USER_ROOM);
    	messageUserRoomAction(room, user, "", ACTION_REMOVE_USER_MESSAGE_ROOM);
    	messageUserRoomAction(room, "", "", ACTION_DELETE_EMPTY_MESSAGE_ROOM); // Evaluates if message room must be deleted
    }
    
    @Override
    public void addUserToChatRoom(String room, String user) {
    	updateSessionManager();
    	usersRoomAction(room, user, ACTION_ADD_ROOM_TO_USER);
    	pendingRoomAction(room, user, ACTION_ADD_PENDING_ROOM_TO_USER);
    	messageUserRoomAction(room, user, "", ACTION_CREATE_MESSAGE_USER_ROOM);
    }
    
    @Override
    public String usersInRoom(String room) {
    	updateSessionManager();
    	return String.valueOf(messageUserRoomAction(room, "", "", ACTION_GET_USERS_IN_MESSAGE_ROOM).size());
    }
    
    @Override
    public List<String> getUsersInRoom(String room) {
    	updateSessionManager();
    	return messageUserRoomAction(room, "", "", ACTION_GET_USERS_IN_MESSAGE_ROOM);
    }

    /**
     * Synchronized users logged actions
     */
    private synchronized void usersLoggedAction(int action) {
    	String user = getThreadLocalRequest().getRemoteUser();
    	updateSessionManager();
    	
    	switch (action) {
    		case ACTION_LOGIN:
    	    	if (!usersLogged.contains(user)) {
    	    		usersLogged.add(user);
    	    	}
    	    	Collections.sort(usersLogged); // Always we sort logged users
    			break;
    			
    		case ACTION_LOGOUT:
    			if (usersLogged.contains(user)) {
    	    		usersLogged.remove(user);
    	    	}
    	    	if (pendingUsersRooms.containsKey(user)) {
    	    		pendingUsersRooms.remove(user);
    	    	}
    	    	if (usersRooms.containsKey(user)) {
    	    		List<String> rooms = usersRooms.get(user);
    	    		for (Iterator<String> it = rooms.iterator(); it.hasNext();) {
    	    			String room = it.next();
    	    			if (msgUsersRooms.containsKey(room)) {
    	    				Map<String, List<String>> roomMessages = msgUsersRooms.get(room);
    	    				if (roomMessages.containsKey(user)) {
    	    					roomMessages.remove(user);
    	    				}
    	    			}
    	    		}
    	    	}
    			break;
    	}
    }
    
    /**
     * Synchronized users room actions
     */
    private synchronized void usersRoomAction(String room, String user, int action) {
    	updateSessionManager();
    	
    	switch (action) {
    		case ACTION_ADD_ROOM_TO_USER:
    			if (!usersRooms.keySet().contains(user)) {
    	    		List<String> userRoomList = new ArrayList<String>();
    	    		userRoomList.add(room);
    	    		usersRooms.put(user, userRoomList);
    	    	} else {
    	    		List<String> userRoomList = usersRooms.get(user);
    	    		if (!userRoomList.contains(room)) {
    	    			userRoomList.add(room);
    	    		}
    	    	}
    			break;
    		
    		case ACTION_REMOVE_USER_ROOM:
    	    	if (usersRooms.keySet().contains(user)) {
    	    		List<String> userRoomList = usersRooms.get(user);
    	    		if (userRoomList.contains(room)) {
    	    			userRoomList.remove(room);
    	    		}
    	    	}
    			break;
    	}
    }
    
    /**
     * Synchronized pending room actions
     */
    private synchronized List<String> pendingRoomAction(String room, String user, int action) {
    	updateSessionManager();
    	
    	switch(action) {
    		case ACTION_ADD_PENDING_ROOM_TO_USER:
    			if (!pendingUsersRooms.keySet().contains(user)) {
    	    		List<String> userPendingRoomList = new ArrayList<String>();
    	    		userPendingRoomList.add(room);
    	    		pendingUsersRooms.put(user, userPendingRoomList);
    	    	} else {
    	    		List<String> userPendingRoomList = pendingUsersRooms.get(user);
    	    		if (!userPendingRoomList.contains(room)) {
    	    			userPendingRoomList.add(room);
    	    		}
    	    	}
    			return new ArrayList<String>();
    		
    		case ACTION_GET_PENDING_USER_ROOM:
    	    	if (pendingUsersRooms.keySet().contains(user)) {
    	    		List<String> userRooms = pendingUsersRooms.get(user);
    	    		pendingUsersRooms.remove(user);
    	    		return userRooms;
    	    	} else {
    	    		return new ArrayList<String>();
    	    	}
    			
    		default:
    			return new ArrayList<String>();
    	}
    }
    
    /**
     * Synchronized message user room actions
     */
    private synchronized List<String> messageUserRoomAction(String room, String user, String msg, int action) {
    	updateSessionManager();
    	
    	switch(action) {
    		case ACTION_GET_PENDING_USER_ROOM_MESSAGE:
    	    	if (msgUsersRooms.containsKey(room) && msgUsersRooms.get(room).containsKey(user)) {
    	    		List<String> messages = msgUsersRooms.get(room).get(user);
    	    		msgUsersRooms.get(room).put(user, new ArrayList<String>()); // Empty messages
    	    		return messages;
    	    	} else {
    	    		return new ArrayList<String>();
    	    	}
    	   
    		case ACTION_ADD_USER_MESSAGE_TO_ROOM:
    			String message =  user + ": " + msg;
    	    	if (msgUsersRooms.containsKey(room)) {
    	    		Map<String, List<String>> roomMap = msgUsersRooms.get(room);
    	    		for (Iterator<String> it = roomMap.keySet().iterator(); it.hasNext();) {
    	    			String roomUser = it.next();
    	    			// Pending message is not added to himself ( that's done by UI )
    	    			if (!roomUser.equals(user)) {
    	    				roomMap.get(roomUser).add(message); // Adding message for each user available
    	    			}
    	    		}
    	    	}
    	    	return new ArrayList<String>();
    	    	
    		case ACTION_CREATE_MESSAGE_ROOM:
    			if (!msgUsersRooms.containsKey(room)) {
    	    		msgUsersRooms.put(room, new HashMap<String, List<String>>());
    	    	}
    			return new ArrayList<String>();
    			
    		case ACTION_CREATE_MESSAGE_USER_ROOM:
    			if (msgUsersRooms.containsKey(room)) {
    	    		if (!msgUsersRooms.get(room).containsKey(user)) {
    	    			msgUsersRooms.get(room).put(user, new ArrayList<String>());
    	    		}
    	    	}
    			return new ArrayList<String>();
    			
    		case ACTION_REMOVE_USER_MESSAGE_ROOM:
    			if (msgUsersRooms.containsKey(room)) {
    	    		if (msgUsersRooms.get(room).containsKey(user)) {
    	    			msgUsersRooms.get(room).remove(user);
    	    		}
    	    	}
    			return new ArrayList<String>();
    			
    		case ACTION_DELETE_EMPTY_MESSAGE_ROOM:
    			// Room message without users must be deleted
    	    	if (msgUsersRooms.containsKey(room)) {
    	    		if (msgUsersRooms.get(room).keySet().size()==0) {
    	    			msgUsersRooms.remove(room);
    	    		}
    	    	}
    			return new ArrayList<String>();
    			
    		case ACTION_GET_USERS_IN_MESSAGE_ROOM:
    			if (msgUsersRooms.containsKey(room)) {
    				Collection<String> userList = msgUsersRooms.get(room).keySet();
    	    		return new ArrayList<String>(userList);
    	    	} else {
    	    		return new ArrayList<String>();
    	    	}
    			
    		default:
    			return new ArrayList<String>();
    	}
    }
}
