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

package com.openkm.frontend.client.widget.thesaurus;

import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.widget.MenuPopup;
import com.openkm.frontend.client.widget.foldertree.FolderTree;
import com.openkm.frontend.client.widget.startup.StartUp;

/**
 * Thesaurus tree
 * 
 * @author jllort
 *
 */
public class ThesaurusTree extends FolderTree {
	
	public ThesaurusSelectPopup thesaurusSelectPopup;
	
	/**
	 * Inits on first load
	 */
	public void init() {
		menuPopup = new MenuPopup(new ThesaurusMenu());
		menuPopup.setStyleName("okm-Tree-MenuPopup");
		
		thesaurusSelectPopup = new ThesaurusSelectPopup();
		thesaurusSelectPopup.setWidth("300");
		thesaurusSelectPopup.setHeight("240");
		thesaurusSelectPopup.setStyleName("okm-Popup");
		
		folderRoot = Main.get().thesaurusRootFolder;
		
		// Sets the context on list context search values
		Main.get().repositoryContext.setContextPersonal(folderRoot.getPath());
		//Main.get().mainPanel.search.searchBrowser.searchIn.setContextValue(folderRoot.getPath(),PanelDefinition.NAVIGATOR_THESAURUS);
		
		actualItem.setUserObject(folderRoot);
		evaluesFolderIcon(actualItem);			
		actualItem.setState(true);
		rootItem = actualItem;  // Preserves actualItem value
		getOnlyChilds(folderRoot.getPath());  	// Special load for firsTime loading ( not refresh file browser )
												// needed to solve that if this stack panel is not showed by user
												// before making a search, can't jump to document folder.
		Main.get().startUp.nextStatus(StartUp.STARTUP_LOADING_TEMPLATES);
	}
	
	/**
	 * Move folder on file browser ( only trash mode )
	 */
	public void move() {
	}
	
	/**
	 * Copy folder on file browser ( only trash mode )
	 */
	public void copy() {
	}	
}