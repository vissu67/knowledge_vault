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

package com.openkm.frontend.client.widget.properties;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;

import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.contants.service.RPCService;
import com.openkm.frontend.client.extension.widget.preview.PreviewExtension;
import com.openkm.frontend.client.util.Util;

/**
 * Notes
 * 
 * @author jllort
 *
 */
public class Preview extends Composite {

	private HorizontalPanel hPanel;
	private HTML text;
	private HTML space;
	private HTML video;
	private int width = 0;
	private int height = 0;
	private boolean previewAvailable = false;
	String mediaUrl = "";
	private String mediaProvider = "";
	
	/**
	 * Preview
	 */
	public Preview() {
		hPanel = new HorizontalPanel();
		text= new HTML("<div id=\"pdfviewercontainer\"></div>\n");
		space = new HTML("");
		video = new HTML("<div id=\"mediaplayercontainer\"></div>\n");
		hPanel.add(text);
		hPanel.add(space);
		hPanel.add(video);
		hPanel.setCellHorizontalAlignment(text, HasAlignment.ALIGN_CENTER);
		hPanel.setCellVerticalAlignment(text, HasAlignment.ALIGN_MIDDLE);
		hPanel.setCellHorizontalAlignment(video, HasAlignment.ALIGN_CENTER);
		hPanel.setCellVerticalAlignment(video, HasAlignment.ALIGN_MIDDLE);
		hPanel.setCellHeight(space, "10");
		initWidget(hPanel);
	}
	
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.UIObject#setPixelSize(int, int)
	 */
	public void setPixelSize(int width, int height) {
		super.setPixelSize(width, height);
		this.width = width;
		this.height = height;
	}
	
	public void showEmbedSWF(String Uuid) {
		hideWidgetExtension();
		text.setVisible(true);
		space.setVisible(false);
		video.setVisible(false);
		if (previewAvailable) {
			String url = RPCService.ConverterServlet +"?inline=true&toSwf=true&uuid=" + URL.encodeComponent(Uuid);
			text.setHTML("<div id=\"pdfviewercontainer\"></div>\n"); // needed for rewriting purpose
			if (Main.get().workspaceUserProperties.getWorkspace().getPreviewer().equals("flexpaper")) {
				if (Main.get().workspaceUserProperties.getWorkspace().isPrintPreview()) {
					Util.createPDFViewerFlexPaper(url, ""+width, ""+height, "true");
				} else {
					Util.createPDFViewerFlexPaper(url, ""+width, ""+height, "false");
				}
			} else {
				Util.createPDFViewerZviewer(url, ""+width, ""+height);
			}
		} else {
			text.setHTML("<div id=\"pdfviewercontainer\" align=\"center\"><br><br>" + Main.i18n("preview.unavailable") + "</div>\n"); // needed for rewriting purpose
		}
	}
	
	/**
	 * Set the media file to reproduce
	 * 
	 * @param mediaUrl The media file url
	 */
	public void showMediaFile(String mediaUrl, String mimeType) {
		hideWidgetExtension();
		text.setVisible(false);
		space.setVisible(true);
		video.setVisible(true);
		this.mediaUrl = mediaUrl;
		Util.removeMediaPlayer();
		video.setHTML("<div id=\"mediaplayercontainer\"></div>\n");
				
		if (mimeType.equals("audio/mpeg")) {
			mediaProvider = "sound";
		} else if (mimeType.equals("video/x-flv") || mimeType.equals("video/mp4")) {
			mediaProvider = "video";
		} else if (mimeType.equals("application/x-shockwave-flash")) {
			mediaProvider = "";
		}
		
		// Size ratio
		int width= 400;
		int height = 280;
		
		// Controls size square ( adapts )
		if (this.width>1.4*this.height) {
			height = this.height;
			width = new Double(1.4*this.height).intValue();
		} else {
			width = this.width;
			height = new Double(this.width/1.4).intValue();
		}
		
		Util.createMediaPlayer(mediaUrl, mediaProvider, ""+(width-10), ""+(height-10));
	}
	
	/**
	 * setPreviewExtension
	 * 
	 * @param preview
	 * @param url
	 */
	public void showPreviewExtension(PreviewExtension preview, String url) {
		hideWidgetExtension();
		text.setVisible(false);
		space.setVisible(false);
		video.setVisible(false);
		if (previewAvailable) {
			if (hPanel.getWidgetIndex(preview.getWidget())<0) {
				hPanel.add(preview.getWidget());
			} else {
				preview.setVisible(true);
			}
			preview.createViewer(url, width, height);
		}
	}
	
	/**
	 * hideWidgetExtension
	 */
	private void hideWidgetExtension() {
		if (hPanel.getWidgetCount()>3) {
			for (int i=2; i<hPanel.getWidgetCount(); i++){
				hPanel.getWidget(i).setVisible(false);
			}
		}
	}
	
	/**
	 * Sets the boolean value if previewing document is available
	 * 
	 * @param previewAvailable
	 */
	public void setPreviewAvailable(boolean previewAvailable) {
		this.previewAvailable = previewAvailable;
	}
	
	/**
	 * langRefresh
	 */
	public void langRefresh() {
		if (!previewAvailable) {
			text.setHTML("<div id=\"pdfviewercontainer\" align=\"center\"><br><br>" + Main.i18n("preview.unavailable") + "</div>\n"); // needed for rewriting purpose
		}
	}
}
