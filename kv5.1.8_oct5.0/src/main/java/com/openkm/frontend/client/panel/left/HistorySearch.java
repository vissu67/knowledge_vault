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

package com.openkm.frontend.client.panel.left;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.StackPanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.util.Util;
import com.openkm.frontend.client.widget.searchsaved.SearchSaved;
import com.openkm.frontend.client.widget.searchuser.UserNews;

/**
 * History search panel
 * 
 * @author jllort
 * 
 */
public class HistorySearch extends Composite {

	// Constants defining the current view
	public static final int NUMBER_OF_STACKS = 2;
	public static final int STACK_HEIGHT = 22;

	// Stack
	public StackPanel stackPanel;
	public ScrollPanel scrollSearchSavedPanel;
	public ScrollPanel scrollUserNewsSavedPanel;
	public SearchSaved searchSaved;
	public UserNews userNews;

	/**
	 * HistorySearch
	 */
	public HistorySearch() {
		stackPanel = new StackPanel();
		searchSaved = new SearchSaved();
		userNews = new UserNews();
		scrollSearchSavedPanel = new ScrollPanel();
		scrollUserNewsSavedPanel = new ScrollPanel();

		scrollSearchSavedPanel.addStyleName("okm-PanelSelected");
		scrollUserNewsSavedPanel.addStyleName("okm-PanelSelected");

		scrollSearchSavedPanel.add(searchSaved);
		scrollSearchSavedPanel.setSize("100%", "100%");

		scrollUserNewsSavedPanel.add(userNews);
		scrollUserNewsSavedPanel.setSize("100%", "100%");

		stackPanel.add(scrollSearchSavedPanel, Util.createHeaderHTML("img/icon/stackpanel/find.gif", Main
				.i18n("leftpanel.label.stored.search")), true);
		stackPanel.add(scrollUserNewsSavedPanel, Util.createHeaderHTML("img/icon/news.gif", Main
				.i18n("leftpanel.label.user.search")), true);

		stackPanel.showStack(0);
		stackPanel.setStyleName("okm-StackPanel");
		stackPanel.addStyleName("okm-DisableSelect");
		initWidget(stackPanel);
	}

	// Public methods to access between objects
	/**
	 * Refresh language descriptions
	 */
	public void langRefresh() {
		stackPanel.setStackText(0, Util.createHeaderHTML("img/icon/stackpanel/find.gif", Main
				.i18n("leftpanel.label.stored.search")), true);
		stackPanel.setStackText(1, Util.createHeaderHTML("img/icon/news.gif", Main
				.i18n("leftpanel.label.user.search")), true);
		searchSaved.langRefresh();
	}

	/**
	 * Resizes all objects on the widget the panel and the tree
	 * 
	 * @param width
	 *            The widget width
	 * @param height
	 *            The widget height
	 */
	public void setSize(int width, int height) {
		stackPanel.setSize("" + width, "" + height);
		// Substract 2 pixels for borders on stackPanel
		scrollSearchSavedPanel.setSize("" + (width - 2), ""+ (height - 2 - (NUMBER_OF_STACKS * STACK_HEIGHT)));
	}
}
