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

import java.io.Serializable;

import org.apache.commons.fileupload.ProgressListener;

/**
 * File upload listener to show uploading status
 * 
 * @author jllort
 *
 */
public class FileUploadListener implements ProgressListener, Serializable {
	private static final long serialVersionUID = -4945332468806137001L;
	private volatile long bytesRead = 0L, contentLength = 0L, item = 0L;   
	private boolean uploadFinish = false;

	/**
	 * FileUploadListener
	 */
	public FileUploadListener() {
		super();
	}

	@Override
	public void update(long aBytesRead, long aContentLength, int anItem) {
		bytesRead = aBytesRead;
		contentLength = aContentLength;
		item = anItem;
	}

	public long getBytesRead() {
		return bytesRead;
	}

	public long getContentLength() {
		return contentLength;
	}

	public long getItem() {
		return item;
	}

	public boolean isUploadFinish() {
		return uploadFinish;
	}

	public void setUploadFinish(boolean uploadFinish) {
		this.uploadFinish = uploadFinish;
	}
}
