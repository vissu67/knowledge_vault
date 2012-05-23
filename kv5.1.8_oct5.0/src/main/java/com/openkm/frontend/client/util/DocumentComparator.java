package com.openkm.frontend.client.util;

import java.util.Comparator;

import com.openkm.frontend.client.bean.GWTDocument;

/**
 * @author jllort
 *
 */
public class DocumentComparator implements Comparator<GWTDocument> {
	private static final Comparator<GWTDocument> INSTANCE  = new DocumentComparator();
	
	public static Comparator<GWTDocument> getInstance() {
		return INSTANCE;
	}
	
	public int compare(GWTDocument arg0, GWTDocument arg1) {
		return arg0.getName().compareTo(arg1.getName());
	}
}
