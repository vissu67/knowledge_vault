package com.openkm.frontend.client.util;

import java.util.Comparator;

import com.openkm.frontend.client.bean.GWTKeyword;

public class KeywordComparator implements Comparator<GWTKeyword> {
	private static final Comparator<GWTKeyword> INSTANCE  = new KeywordComparator();
	
	public static Comparator<GWTKeyword> getInstance() {
		return INSTANCE;
	}
	
	public int compare(GWTKeyword arg0, GWTKeyword arg1) {
		return arg0.getKeyword().compareTo(arg1.getKeyword());
	}
}