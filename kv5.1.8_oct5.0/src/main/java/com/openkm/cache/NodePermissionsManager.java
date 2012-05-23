package com.openkm.cache;

import java.util.HashMap;
import java.util.Map;

import org.apache.jackrabbit.core.NodeId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.cache.NodePermissions;
import com.openkm.util.Serializer;

public class NodePermissionsManager {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(NodePermissionsManager.class);
	private static final String FILEALIZATION = "NodePermissionsManager";
	private static Map<NodeId, NodePermissions> nodePermissionsMgr;
	
	static {
		deserialize();
	}
	
	/**
	 * 
	 */
	public static NodePermissions get(NodeId id) {
		return nodePermissionsMgr.get(id);
	}
	
	/**
	 * 
	 */
	public static synchronized void put(NodeId id, NodePermissions perm) {
		nodePermissionsMgr.put(id, perm);
		serialize();
	}
	
	/**
	 * 
	 */
	private static synchronized void serialize() {
		Serializer.write(FILEALIZATION, nodePermissionsMgr);
	}
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	private static synchronized void deserialize() {
		nodePermissionsMgr = new HashMap<NodeId, NodePermissions>();
		Object obj = Serializer.read(FILEALIZATION);
		if (obj != null) {
			nodePermissionsMgr = (HashMap<NodeId, NodePermissions>) obj;
		}
	}
}
