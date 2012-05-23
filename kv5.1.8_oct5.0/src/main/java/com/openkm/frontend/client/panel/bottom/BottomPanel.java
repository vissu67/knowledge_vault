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

package com.openkm.frontend.client.panel.bottom;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.widget.UserInfo;

/**
 * Bottom panel
 * 
 * @author jllort
 *
 */
public class BottomPanel extends Composite {
	
	public static final int PANEL_HEIGHT = 60;
	
	private HorizontalPanel panel;
	private Label statusMsg;
	private InlineHTML footerLogo;
	private SimplePanel spLeft;
	private SimplePanel spRight;
	public UserInfo userInfo;
	private String key = "";
	private Timer removeStatus;
	private String aditionalErrorMsg = "";
	
	/**
	 * BottomPanel
	 */
	public BottomPanel() {
		userInfo = new UserInfo();
		panel = new HorizontalPanel();
		spLeft = new SimplePanel();
		spRight = new SimplePanel();
		
		statusMsg = new Label("");
		statusMsg.setStyleName("okm-Input");
		statusMsg.setSize("340","15");
		statusMsg.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		
		//added by vissu 2nov
		footerLogo = new InlineHTML("<img onclick=\"javascript:window.open('http://knowledgevault.com.au/')\" src='img/footer_logo.png' height='48' width='421' style='border:0px;'></a>");
		footerLogo.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
		statusMsg.setSize("421","60");
		
		spLeft.setWidth("10");
		spRight.setWidth("10");

		
		panel.add(spLeft);
		panel.add(userInfo);
		
		//vissu touch to hide status msg on oct'9th
		//panel.add(statusMsg);
		//added by vissu 2nov
		panel.add(footerLogo);
		
		panel.add(spRight);
		panel.setCellWidth(spLeft,"10");
		panel.setCellWidth(spRight,"10");
		panel.setCellHorizontalAlignment(userInfo, HasAlignment.ALIGN_LEFT);
		//vissu touch to hide status msg on oct'9th
		//panel.setCellHorizontalAlignment(statusMsg, HasAlignment.ALIGN_RIGHT);
		panel.setCellHorizontalAlignment(footerLogo, HasAlignment.ALIGN_RIGHT);
		
		
		panel.setCellHorizontalAlignment(spRight, HasAlignment.ALIGN_RIGHT);
		panel.setCellVerticalAlignment(userInfo, HasAlignment.ALIGN_MIDDLE);
		//vissu touch to hide status msg on oct'9th
		//panel.setCellVerticalAlignment(statusMsg, HasAlignment.ALIGN_MIDDLE);
		panel.setCellVerticalAlignment(footerLogo, HasAlignment.ALIGN_MIDDLE);

		
		panel.setStyleName("okm-bottomPanel");
		panel.addStyleName("okm-DisableSelect");
		panel.setHorizontalAlignment(VerticalPanel.ALIGN_LEFT);
		panel.setSize("100%", "100%");
		initWidget(panel);
	}
	
	/**
	 * Sets the size
	 * 
	 * @param width the width size
	 * @param height the height size
	 */
	public void setSize(int width, int height) {
		panel.setSize(""+width, ""+height);
	}
	
	/**
	 * Sets the status
	 * 
	 * @param key The status value
	 * @param error Is error or not
	 */
	public void setStatus(String msg) {
		aditionalErrorMsg = "";
		
		// Always we ensure remove status here might be disabled to prevent removing new status data
		if (removeStatus!=null) {
			removeStatus.cancel();
			removeStatus = null;
		}
		
		statusMsg.removeStyleName("okm-Input-Error");
		statusMsg.setText(msg);
	}
	
	/**
	 * Sets the status
	 * 
	 * @param key The status value
	 * @param error Is error or not
	 */
	public void setStatus(String key, boolean error) {
		this.key = key;
		aditionalErrorMsg = "";
		
		// Always we ensure remove status here might be disabled to prevent removing new status data
		if (removeStatus!=null) {
			removeStatus.cancel();
			removeStatus = null;
		}
		
		if (error) {
			statusMsg.addStyleName("okm-Input-Error");
			DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.hour.pattern"));
			aditionalErrorMsg = " - " + dtf.format(new Date());
			// On error case we reset status at 2 minutes
			removeStatus = new Timer() {
				public void run() {
					resetStatus();
				}
			};
			removeStatus.schedule(120*1000); // 2 min
			
		} else {
			statusMsg.removeStyleName("okm-Input-Error");
		}
		statusMsg.setText(" "+Main.i18n(key) + aditionalErrorMsg );
	}
	
	/**
	 * Sets the status code
	 * 
	 * @param key The status code key
	 * @param error Is error or not
	 * @param errorCode The code error
	 */
	public void setStatus(String key, boolean error, int errorCode) {
		this.key = key;
		aditionalErrorMsg = "";
		
		// Always we ensure remove status here might be disabled to prevent removing new status data
		if (removeStatus!=null) {
			removeStatus.cancel();
			removeStatus = null;
		}
		
		if (error) {
			statusMsg.addStyleName("okm-Input-Error");
			DateTimeFormat dtf = DateTimeFormat.getFormat(Main.i18n("general.hour.pattern"));
			aditionalErrorMsg = " (" + errorCode +") - " + dtf.format(new Date());
			// On error case we reset status at 2 minutes
			removeStatus = new Timer() {
				public void run() {
					resetStatus();
				}
			};
			removeStatus.schedule(120*1000); // 2 min
		} else {
			statusMsg.removeStyleName("okm-Input-Error");
		}
		statusMsg.setText(" "+Main.i18n(key) + aditionalErrorMsg);
	}
	
	/**
	 * Resets the status value
	 */
	public void resetStatus(){
		statusMsg.setText("");
		statusMsg.removeStyleName("okm-Input-Error");
	}
	
	/**
	 * Lang refresh
	 */
	public void langRefresh() {
		statusMsg.setText(" "+Main.i18n(key) + aditionalErrorMsg);
		userInfo.langRefresh();
	}
}