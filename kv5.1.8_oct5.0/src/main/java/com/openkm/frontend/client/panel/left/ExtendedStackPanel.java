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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.user.client.ui.StackPanel;
import com.openkm.frontend.client.Main;
import com.openkm.frontend.client.contants.ui.UIDesktopConstants;
import com.openkm.frontend.client.contants.ui.UIDockPanelConstants;
import com.openkm.frontend.client.extension.event.HasNavigatorEvent;
import com.openkm.frontend.client.extension.event.handler.NavigatorHandlerExtension;
import com.openkm.frontend.client.extension.event.hashandler.HasNavigatorHandlerExtension;

public class ExtendedStackPanel extends StackPanel implements HasNavigatorEvent, HasNavigatorHandlerExtension {
	
	private boolean startupFinished		= false; // to indicate process starting up has finished
	private boolean taxonomyVisible		= false;
	private boolean categoriesVisible	= false;
	private boolean thesaurusVisible 	= false;
	private boolean templatesVisible 	= false;
	private boolean personalVisible 	= false;
	private boolean mailVisible			= false;
	private boolean trashVisible		= false;
	private int hiddenStacks = 7;
	private int stackIndex = 0;
	private List<NavigatorHandlerExtension> navHandlerExtensionList;
	
	public ExtendedStackPanel() {
		super();
		navHandlerExtensionList = new ArrayList<NavigatorHandlerExtension>();
	}
	
	/* (non-Javadoc)
	 * @see com.google.gwt.user.client.ui.StackPanel#showStack(int)
	 */
	public void showStack( int index ) {
		stackIndex = index;
		
		if (startupFinished) {		
			changeView(index,true);
		}
		
        super.showStack(index);
	} 
	
	/**
	 * setStartUpFinished
	 */
	public void setStartUpFinished() {
		startupFinished = true;
	}
	
	/**
	 * Gets the stack index value
	 * 
	 * @return The stack index value
	 */
	public int getStackIndex() {
		return indexCorrectedChangeViewIndex(stackIndex);
	}
	
	/**
	 * Change the stack panel view
	 * 
	 * @param index The new stack index selected
	 * @param refresh Enables or disables refreshing
	 */
	private void changeView(int index, boolean refresh) {
		if (startupFinished) {
			// If there's folder creating or renaming must cancel it before changing view
			if (Main.get().activeFolderTree.isFolderCreating()) {
				Main.get().activeFolderTree.removeTmpFolderCreate();
			} else if (Main.get().activeFolderTree.isFolderRenaming()) {
				Main.get().activeFolderTree.cancelRename();
			}
			
			switch (indexCorrectedChangeViewIndex(index)) {
				case UIDesktopConstants.NAVIGATOR_TAXONOMY:
					Main.get().activeFolderTree = Main.get().mainPanel.desktop.navigator.taxonomyTree;
					Main.get().mainPanel.desktop.browser.fileBrowser.changeView(UIDesktopConstants.NAVIGATOR_TAXONOMY);
					Main.get().mainPanel.topPanel.toolBar.changeView(UIDesktopConstants.NAVIGATOR_TAXONOMY,UIDockPanelConstants.DESKTOP);
					if (refresh) {
						Main.get().activeFolderTree.forceSetSelectedPanel();
						Main.get().activeFolderTree.refresh(true); // When opening a path document must not refreshing
					}
					Main.get().mainPanel.desktop.browser.tabMultiple.setVisibleButtons(true);
					break;
					
				case UIDesktopConstants.NAVIGATOR_TRASH:
					Main.get().activeFolderTree = Main.get().mainPanel.desktop.navigator.trashTree;
					Main.get().mainPanel.desktop.browser.fileBrowser.changeView(UIDesktopConstants.NAVIGATOR_TRASH);
					Main.get().mainPanel.topPanel.toolBar.changeView(UIDesktopConstants.NAVIGATOR_TRASH,UIDockPanelConstants.DESKTOP);
					if (refresh) {
						Main.get().activeFolderTree.forceSetSelectedPanel();
						Main.get().activeFolderTree.refresh(true); // When opening a path document must not refreshing
					}
					Main.get().mainPanel.desktop.browser.tabMultiple.setVisibleButtons(false);
					break;
					
				case UIDesktopConstants.NAVIGATOR_CATEGORIES:
					Main.get().activeFolderTree = Main.get().mainPanel.desktop.navigator.categoriesTree;
					Main.get().mainPanel.desktop.browser.fileBrowser.changeView(UIDesktopConstants.NAVIGATOR_CATEGORIES);
					Main.get().mainPanel.topPanel.toolBar.changeView(UIDesktopConstants.NAVIGATOR_CATEGORIES,UIDockPanelConstants.DESKTOP);
					if (refresh) {
						Main.get().activeFolderTree.forceSetSelectedPanel();
						Main.get().activeFolderTree.refresh(true); // When opening a path document must not refreshing
					}
					Main.get().mainPanel.desktop.browser.tabMultiple.setVisibleButtons(true);
					break;
					
				case UIDesktopConstants.NAVIGATOR_THESAURUS:
					Main.get().activeFolderTree = Main.get().mainPanel.desktop.navigator.thesaurusTree;
					Main.get().mainPanel.desktop.browser.fileBrowser.changeView(UIDesktopConstants.NAVIGATOR_THESAURUS);
					Main.get().mainPanel.topPanel.toolBar.changeView(UIDesktopConstants.NAVIGATOR_THESAURUS,UIDockPanelConstants.DESKTOP);
					if (refresh) {
						Main.get().activeFolderTree.forceSetSelectedPanel();
						Main.get().activeFolderTree.refresh(true); // When opening a path document must not refreshing
					}
					Main.get().mainPanel.desktop.browser.tabMultiple.setVisibleButtonsOnlyDocuments();
					break;
				
				case UIDesktopConstants.NAVIGATOR_TEMPLATES:
					Main.get().activeFolderTree = Main.get().mainPanel.desktop.navigator.templateTree;
					Main.get().mainPanel.desktop.browser.fileBrowser.changeView(UIDesktopConstants.NAVIGATOR_TEMPLATES);
					Main.get().mainPanel.topPanel.toolBar.changeView(UIDesktopConstants.NAVIGATOR_TEMPLATES,UIDockPanelConstants.DESKTOP);
					if (refresh) {
						Main.get().activeFolderTree.forceSetSelectedPanel();
						Main.get().activeFolderTree.refresh(true); // When opening a path document must not refreshing
					}
					Main.get().mainPanel.desktop.browser.tabMultiple.setVisibleButtons(true);
					break;
				
				case UIDesktopConstants.NAVIGATOR_PERSONAL:
					Main.get().activeFolderTree = Main.get().mainPanel.desktop.navigator.personalTree;
					Main.get().mainPanel.desktop.browser.fileBrowser.changeView(UIDesktopConstants.NAVIGATOR_PERSONAL);
					Main.get().mainPanel.topPanel.toolBar.changeView(UIDesktopConstants.NAVIGATOR_PERSONAL,UIDockPanelConstants.DESKTOP);
					if (refresh) {
						Main.get().activeFolderTree.forceSetSelectedPanel();
						Main.get().activeFolderTree.refresh(true); // When opening a path document must not refreshing
					}
					Main.get().mainPanel.desktop.browser.tabMultiple.setVisibleButtons(true);
					break;
				
			/*	case UIDesktopConstants.NAVIGATOR_MAIL:
					Main.get().activeFolderTree = Main.get().mainPanel.desktop.navigator.mailTree;
					Main.get().mainPanel.desktop.browser.fileBrowser.changeView(UIDesktopConstants.NAVIGATOR_MAIL);
					Main.get().mainPanel.topPanel.toolBar.changeView(UIDesktopConstants.NAVIGATOR_MAIL,UIDockPanelConstants.DESKTOP);
					if (refresh) {
						Main.get().activeFolderTree.forceSetSelectedPanel();
						Main.get().activeFolderTree.refresh(true); // When opening a path document must not refreshing
					}
					Main.get().mainPanel.desktop.browser.tabMultiple.setVisibleButtons(true);
					break;*/
			}
			fireEvent(HasNavigatorEvent.STACK_CHANGED);
		}
	}
	
	/**
	 * Show a stack
	 * 
	 * @param index The new stack index selected
	 * @param refresh Enables or disables refreshing
	 */
	public void showStack( int index, boolean refresh ) {
		stackIndex = correctedStackIndex(index);
		changeView(stackIndex,refresh);
        super.showStack(stackIndex);
	} 
	
	/**
	 * isTaxonomyVisible
	 * 
	 * @return
	 */
	public boolean isTaxonomyVisible() {
		return taxonomyVisible;
	}
	
	/**
	 * isCategoriesVisible
	 * 
	 * @return
	 */
	public boolean isCategoriesVisible() {
		return categoriesVisible;
	}
	
	/**
	 * isThesaurusVisible
	 * 
	 * @return
	 */
	public boolean isThesaurusVisible() {
		return thesaurusVisible;
	}
	
	/**
	 * isTemplatesVisible
	 * 
	 * @return
	 */
	public boolean isTemplatesVisible() {
		return templatesVisible;
	}
	
	/**
	 * isPersonalVisible
	 * 
	 * @return
	 */
	public boolean isPersonalVisible() {
		return personalVisible;
	}
	
	/**
	 * isMailVisible
	 * 
	 * @return
	 */
	public boolean isMailVisible() {
		//return mailVisible;
		return false;
	}
	
	/**
	 * isTrashVisible
	 * 
	 * @return
	 */
	public boolean isTrashVisible() {
		return trashVisible;
	}
	
	/**
	 * showTaxonomy
	 */
	public void showTaxonomy() {
		hiddenStacks--;
		taxonomyVisible = true;
	}

	/**
	 * showCategories
	 * 
	 * @param 
	 */
	public void showCategories() {
		hiddenStacks--;
		categoriesVisible = true;
	}
	
	/**
	 * showThesaurus
	 * 
	 */
	public void showThesaurus() {
		hiddenStacks--;
		thesaurusVisible = true;
	}
	
	/**
	 * showTemplates
	 */
	public void showTemplates() {
		hiddenStacks--;
		templatesVisible = true;
	}
	
	/**
	 * showPersonal
	 * 
	 * @param 
	 */
	public void showPersonal() {
		hiddenStacks--;
		personalVisible = true;
	}
	
	/**
	 * showMail
	 * 
	 * @param 
	 */
	public void showMail() {
		hiddenStacks--;
		mailVisible = true;
	}
	
	/**
	 * showTrash
	 */
	public void showTrash() {
		hiddenStacks--;
		trashVisible = true;
	}
	
	/**
	 * indexCorrectedChangeViewIndex
	 * 
	 * Return index correction made depending visible panels
	 * 
	 * @param index
	 * @return
	 */
	public int indexCorrectedChangeViewIndex(int index) {
		int corrected = index;
		if (!taxonomyVisible && corrected>=UIDesktopConstants.NAVIGATOR_TAXONOMY) {
			corrected++;
		}
		if (!categoriesVisible && corrected>=UIDesktopConstants.NAVIGATOR_CATEGORIES) {
			corrected++;
		}
		if (!thesaurusVisible && corrected>=UIDesktopConstants.NAVIGATOR_THESAURUS) {
			corrected++;
		}
		if (!templatesVisible && corrected>=UIDesktopConstants.NAVIGATOR_TEMPLATES) {
			corrected++;
		}
		if (!personalVisible && corrected>=UIDesktopConstants.NAVIGATOR_PERSONAL) {
			corrected++;
		}
		if (!mailVisible && corrected>=UIDesktopConstants.NAVIGATOR_MAIL) {
			corrected++;
		}
		if (!trashVisible && corrected>=UIDesktopConstants.NAVIGATOR_TRASH) {
			corrected++;
		}
		return corrected;
	}
	
	/**
	 * correctedStackIndex
	 * 
	 * Return index correction, made depending visible panels
	 * 
	 * @param index
	 * @return
	 */
	private int correctedStackIndex(int index) {
		int corrected = index;
		if (!trashVisible && corrected>=UIDesktopConstants.NAVIGATOR_TRASH) {
			corrected--;
		}
		if (!mailVisible && corrected>=UIDesktopConstants.NAVIGATOR_MAIL) {
			corrected--;
		}
		if (!personalVisible && corrected>=UIDesktopConstants.NAVIGATOR_PERSONAL) {
			corrected--;
		}
		if (!thesaurusVisible && corrected>=UIDesktopConstants.NAVIGATOR_THESAURUS) {
			corrected--;
		}
		if (!templatesVisible && corrected>=UIDesktopConstants.NAVIGATOR_TEMPLATES) {
			corrected--;
		}
		if (!categoriesVisible && corrected>=UIDesktopConstants.NAVIGATOR_CATEGORIES) {
			corrected--;
		}
		if (!taxonomyVisible && corrected>=UIDesktopConstants.NAVIGATOR_TAXONOMY) {
			corrected--;
		}
		return corrected;
	}
	
	/**
	 * getHiddenStacks
	 * 
	 * @return
	 */
	public int getHiddenStacks() {
		return hiddenStacks;
	}
	
	@Override
	public void addNavigatorHandlerExtension(NavigatorHandlerExtension handlerExtension) {
		navHandlerExtensionList.add(handlerExtension);
	}

	@Override
	public void fireEvent(NavigatorEventConstant event) {
		for (Iterator<NavigatorHandlerExtension> it = navHandlerExtensionList.iterator(); it.hasNext();) {
			it.next().onChange(event);
		}
	}
}