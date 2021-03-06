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

package com.openkm.frontend.client.widget.categories;

import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.bean.GWTFolder;
import com.openkm.frontend.client.widget.MenuPopup;
import com.openkm.frontend.client.widget.foldertree.FolderSelectPopup;
import com.openkm.frontend.client.widget.foldertree.FolderTree;
import com.openkm.frontend.client.widget.startup.StartUp;

/**
 * CatergoryTree tree
 * 
 * @author jllort
 *
 */
public class CategoriesTree extends FolderTree {
	
	public CategoriesSelectPopup categoriesSelectPopup;
	
	/**
	 * Inits on first load
	 */
	public void init() {
		menuPopup = new MenuPopup(new CategoriesMenu());
		menuPopup.setStyleName("okm-Tree-MenuPopup");
		
		categoriesSelectPopup = new CategoriesSelectPopup();
		categoriesSelectPopup.setWidth("300");
		categoriesSelectPopup.setHeight("240");
		categoriesSelectPopup.setStyleName("okm-Popup");
		
		folderRoot = Main.get().categoriesRootFolder;
		
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
		//changed STARTUP_LOADING_THESAURUS to STARTUP_LOADING_TEMPLATES by vissu on may18 for disable categories & thesaurus
		Main.get().startUp.nextStatus(StartUp.STARTUP_LOADING_TEMPLATES);
	}
	
	/**
	 * Move folder on file browser ( only trash mode )
	 */
	public void move() {
		GWTFolder folderToRestore = (GWTFolder) actualItem.getUserObject();
		folderSelectPopup.setEntryPoint(FolderSelectPopup.ENTRYPOINT_CATEGORIES);
		folderSelectPopup.setToMove(folderToRestore);
		showDirectorySelectPopup();
		hideMenuPopup();
	}
	
	/**
	 * Copy folder on file browser ( only trash mode )
	 */
	public void copy() {
	}	
}