package com.openkm.frontend.client.util;

import java.util.Comparator;

import com.openkm.frontend.client.bean.extension.GWTContact;

/**
 * @author jllort
 *
 */
public class ContactComparator implements Comparator<GWTContact> {
	private static final Comparator<GWTContact> INSTANCE  = new ContactComparator();
	
	public static Comparator<GWTContact> getInstance() {
		return INSTANCE;
	}
	
	public int compare(GWTContact arg0, GWTContact arg1) {
		return arg0.getName().toLowerCase().compareTo(arg1.getName().toLowerCase());
	}
}