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

package com.openkm.util;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.openkm.bean.LogMessage;

/**
 * @author pavila
 *
 */
public class FormatUtil {
	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory.getLogger(FormatUtil.class);
	
	/**
	 * Detect if the current browser is a mobile one
	 */
	public static final boolean isMobile(HttpServletRequest request) {
		String userAgent = request.getHeader("user-agent").toLowerCase();
		return userAgent.contains("android") || userAgent.contains("iphone") || userAgent.contains("blackberry");
	}
	
	/**
	 * Format the document size for human readers
	 */
	public static String formatSize(long size) {
		DecimalFormat df = new DecimalFormat("#0.0");
		String str;
		
		if (size / 1024 < 1) {
			str = size + " B";
		} else if (size / 1048576 < 1) {
			str = df.format(size / 1024.0) + " KB";
		} else if (size / 1073741824 < 1) {
			str = df.format(size / 1048576.0) + " MB";
		} else if (size /  1099511627776L < 1) {
			str = df.format(size / 1073741824.0) + " GB";
		} else if (size /  1125899906842624L < 1) {
			str = df.format(size / 1099511627776.0) + " TB";
		} else {
			str = "BIG";
		}
		
		return str;
	}

	/**
	 * Format time for human readers
	 */
	public static String formatTime(long time) {
		DateFormat df = new SimpleDateFormat("HH:mm:ss.SSS"); 
		String str = df.format(time);
		return str;
	}
	
	/**
	 * Format time interval for humans 
	 */
	public static String formatSeconds(long time) {
		long hours, minutes, seconds;
		time = time / 1000;
		hours = time / 3600;
		time = time - (hours * 3600);
		minutes = time / 60;
		time = time - (minutes * 60);
		seconds = time;
		return (hours<10?"0"+hours:hours)+":"+
			(minutes<10?"0"+minutes:minutes)+":"+
			(seconds<10?"0"+seconds:seconds);
	}
	
	/**
	 * Format time interval for humans 
	 */
	public static String formatMiliSeconds(long time) {
		long hours, minutes, seconds, mseconds;
		mseconds = time % 1000;
		time = time / 1000;
		hours = time / 3600;
		time = time - (hours * 3600);
		minutes = time / 60;
		time = time - (minutes * 60);
		seconds = time;
		return (hours<10?"0"+hours:hours)+":"+
			(minutes<10?"0"+minutes:minutes)+":"+
			(seconds<10?"0"+seconds:seconds)+"."+
			(mseconds<10?"00"+mseconds:(mseconds<100?"0"+mseconds:mseconds));
	}
	
	/**
	 * Format calendar date
	 */
	public static String formatDate(Calendar cal) {
		return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(cal.getTime());
	}
	
	/**
	 * Format string array
	 */
	public static String formatArray(String[] values) {
		if (values != null) {
			if (values.length == 1) {
				return values[0];
			} else {
				return ArrayUtils.toString(values);
			}
		} else {
			return "NULL";
		}
	}
	
	/**
	 * Format object
	 */
	public static String formatObject(Object value) {
		if (value != null) {
			if (value instanceof Object[]) {
				return ArrayUtils.toString(value);
			} else {
				return value.toString();
			}
		} else {
			return "NULL";
		}
	}
	
	/**
	 * Escape html tags 
	 */
	public static String escapeHtml(String str) {
		return str.replace("<", "&lt;").replace(">", "&gt;");
	}
	
	/**
	 * Parser log file
	 */
	public static Collection<LogMessage> parseLog(File flog, int begin, int end, String str) throws IOException {
		//log.debug("parseLog({}, {}, {}, {})", new Object[] { flog, begin, end, str });
		ArrayList<LogMessage> al = new ArrayList<LogMessage>();
		int i = 0;
		
		if (begin < 0 || end < 0) {
			int maxLines = 0;
			
			for (LineIterator lit = FileUtils.lineIterator(flog); lit.hasNext(); ) {
				lit.nextLine();
				maxLines++;
			}
			
			if (begin < 0) {
				begin += maxLines;
			}
			
			if (end < 0) {
				end += maxLines + 1;
			}
		}
		
		for (LineIterator lit = FileUtils.lineIterator(flog); lit.hasNext(); ) {
			String line = lit.nextLine();
			int idx = line.indexOf(str);
			i++;
			
			if ((str == null || idx > -1) && i >= begin && i <= end) {
				if (idx > -1) {
					StringBuilder sb = new StringBuilder();
					sb.append(line.substring(0, idx));
					sb.append("<span class=\"highlight\">");
					sb.append(line.substring(idx, idx + str.length()));
					sb.append("</span>");
					sb.append(line.substring(idx + str.length()));
					line = sb.toString();
				}
				
				LogMessage lm = new LogMessage();
				lm.setLine(i);
				lm.setMessage(line);
				al.add(lm);
			}
		}
		
		//log.debug("parseLog: {}", al);
		return al;
	}
}
