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

package com.openkm.frontend.client.bean;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * GWTAvailableOption
 * 
 * @author jllort
 *
 */
public class GWTAvailableOption implements IsSerializable {
	public boolean createFolderOption = true;
	public boolean findFolderOption = true;
	public boolean downloadOption = true;
	public boolean downloadPdfOption = true;
	public boolean lockOption = true;
	public boolean unLockOption = true;
	public boolean addDocumentOption = true;
	public boolean checkoutOption = true;
	public boolean checkinOption = true;
	public boolean cancelCheckoutOption = true;
	public boolean deleteOption = true;
	public boolean addPropertyGroupOption = true;
	public boolean removePropertyGroupOption = true;
	public boolean addSubscription = true;
	public boolean removeSubscription = true;
	public boolean homeOption = true;
	public boolean refreshOption = true;
	public boolean workflowOption = true;
	public boolean scannerOption = true;
	public boolean uploaderOption = true;
	public boolean renameOption = true;
	
	//added by vissu on feb19 for zohoapi
	public boolean zohoOption = true;
	
	public boolean copyOption = true;
	public boolean moveOption = true;
	public boolean addBookmarkOption = true;
	public boolean setHomeOption = true;
	public boolean exportOption = true;
	public boolean mediaPlayerOption = true;
	public boolean imageViewerOption = true;
	public boolean gotoFolderOption = true;
	public boolean createFromTemplateOption = true;
	public boolean purgeOption = true;
	public boolean restoreOption = true;
	public boolean purgeTrashOption = true;
	public boolean sendDocumentLinkOption = true;
	public boolean sendDocumentAttachmentOption = true;
	public boolean skinOption = true;
	public boolean debugOption = true;
	public boolean administrationOption = true;
	public boolean manageBookmarkOption = true;
	public boolean helpOption = true;
	public boolean documentationOption = true;
	public boolean bugReportOption = true;
	public boolean supportRequestOption = true;
	public boolean publicForumOption = true;
	public boolean versionChangesOption = true;
	public boolean projectWebOption = true;
	public boolean aboutOption = true;
	public boolean languagesOption = true;
	public boolean preferencesOption = true;

	public GWTAvailableOption() {
	}

	public boolean isCreateFolderOption() {
		return createFolderOption;
	}

	public void setCreateFolderOption(boolean createFolderOption) {
		this.createFolderOption = createFolderOption;
	}

	public boolean isFindFolderOption() {
		return findFolderOption;
	}

	public void setFindFolderOption(boolean findFolderOption) {
		this.findFolderOption = findFolderOption;
	}

	public boolean isDownloadOption() {
		return downloadOption;
	}

	public void setDownloadOption(boolean downloadOption) {
		this.downloadOption = downloadOption;
	}

	public boolean isDownloadPdfOption() {
		return downloadPdfOption;
	}

	public void setDownloadPdfOption(boolean downloadPdfOption) {
		this.downloadPdfOption = downloadPdfOption;
	}

	public boolean isLockOption() {
		return lockOption;
	}

	public void setLockOption(boolean lockOption) {
		this.lockOption = lockOption;
	}

	public boolean isUnLockOption() {
		return unLockOption;
	}

	public void setUnLockOption(boolean unLockOption) {
		this.unLockOption = unLockOption;
	}

	public boolean isAddDocumentOption() {
		return addDocumentOption;
	}

	public void setAddDocumentOption(boolean addDocumentOption) {
		this.addDocumentOption = addDocumentOption;
	}

	public boolean isCheckoutOption() {
		return checkoutOption;
	}

	public void setCheckoutOption(boolean checkoutOption) {
		this.checkoutOption = checkoutOption;
	}

	public boolean isCheckinOption() {
		return checkinOption;
	}

	public void setCheckinOption(boolean checkinOption) {
		this.checkinOption = checkinOption;
	}

	public boolean isCancelCheckoutOption() {
		return cancelCheckoutOption;
	}

	public void setCancelCheckoutOption(boolean cancelCheckoutOption) {
		this.cancelCheckoutOption = cancelCheckoutOption;
	}

	public boolean isDeleteOption() {
		return deleteOption;
	}

	public void setDeleteOption(boolean deleteOption) {
		this.deleteOption = deleteOption;
	}

	public boolean isAddPropertyGroupOption() {
		return addPropertyGroupOption;
	}

	public void setAddPropertyGroupOption(boolean addPropertyGroupOption) {
		this.addPropertyGroupOption = addPropertyGroupOption;
	}

	public boolean isRemovePropertyGroupOption() {
		return removePropertyGroupOption;
	}

	public void setRemovePropertyGroupOption(boolean removePropertyGroupOption) {
		this.removePropertyGroupOption = removePropertyGroupOption;
	}

	public boolean isAddSubscription() {
		return addSubscription;
	}

	public void setAddSubscription(boolean addSubscription) {
		this.addSubscription = addSubscription;
	}

	public boolean isRemoveSubscription() {
		return removeSubscription;
	}

	public void setRemoveSubscription(boolean removeSubscription) {
		this.removeSubscription = removeSubscription;
	}

	public boolean isHomeOption() {
		return homeOption;
	}

	public void setHomeOption(boolean homeOption) {
		this.homeOption = homeOption;
	}

	public boolean isRefreshOption() {
		return refreshOption;
	}

	public void setRefreshOption(boolean refreshOption) {
		this.refreshOption = refreshOption;
	}

	public boolean isWorkflowOption() {
		return workflowOption;
	}

	public void setWorkflowOption(boolean workflowOption) {
		this.workflowOption = workflowOption;
	}

	public boolean isScannerOption() {
		return scannerOption;
	}

	public void setScannerOption(boolean scannerOption) {
		this.scannerOption = scannerOption;
	}

	public boolean isUploaderOption() {
		return uploaderOption;
	}

	public void setUploaderOption(boolean uploaderOption) {
		this.uploaderOption = uploaderOption;
	}
	
	public boolean isRenameOption() {
		return renameOption;
	}
	
	public void setRenameOption(boolean renameOption) {
		this.renameOption = renameOption;
	}
	
	//added by vissu on feb19 for zohoapi
	public boolean isZohoOption() {
		return zohoOption;
	}
	
	public void setZohoOption(boolean zohoOption) {
		this.zohoOption = zohoOption;
	}
	
	public boolean isMoveOption() {
		return moveOption;
	}

	public void setMoveOption(boolean moveOption) {
		this.moveOption = moveOption;
	}

	public boolean isCopyOption() {
		return copyOption;
	}

	public void setCopyOption(boolean copyOption) {
		this.copyOption = copyOption;
	}
	
	public boolean isAddBookmarkOption() {
		return addBookmarkOption;
	}

	public void setAddBookmarkOption(boolean addBookmarkOption) {
		this.addBookmarkOption = addBookmarkOption;
	}
	
	public boolean isSetHomeOption() {
		return setHomeOption;
	}

	public void setSetHomeOption(boolean setHomeOption) {
		this.setHomeOption = setHomeOption;
	}
	
	public boolean isExportOption() {
		return exportOption;
	}

	public void setExportOption(boolean exportOption) {
		this.exportOption = exportOption;
	}
	
	public boolean isMediaPlayerOption() {
		return mediaPlayerOption;
	}

	public void setMediaPlayerOption(boolean mediaPlayerOption) {
		this.mediaPlayerOption = mediaPlayerOption;
	}
	
	public boolean isImageViewerOption() {
		return imageViewerOption;
	}

	public void setImageViewerOption(boolean imageViewerOption) {
		this.imageViewerOption = imageViewerOption;
	}
	
	public boolean isGotoFolderOption() {
		return gotoFolderOption;
	}

	public void setGotoFolderOption(boolean gotoFolderOption) {
		this.gotoFolderOption = gotoFolderOption;
	}
	
	public boolean isCreateFromTemplateOption() {
		return createFromTemplateOption;
	}

	public void setCreateFromTemplateOption(boolean createFromTemplateOption) {
		this.createFromTemplateOption = createFromTemplateOption;
	}
	
	public boolean isPurgeOption() {
		return purgeOption;
	}

	public void setPurgeOption(boolean purgeOption) {
		this.purgeOption = purgeOption;
	}

	public boolean isRestoreOption() {
		return restoreOption;
	}

	public void setRestoreOption(boolean restoreOption) {
		this.restoreOption = restoreOption;
	}
	
	public boolean isPurgeTrashOption() {
		return purgeTrashOption;
	}

	public void setPurgeTrashOption(boolean purgeTrashOption) {
		this.purgeTrashOption = purgeTrashOption;
	}
	
	public boolean isSendDocumentLinkOption() {
		return sendDocumentLinkOption;
	}

	public void setSendDocumentLinkOption(boolean sendDocumentLinkOption) {
		this.sendDocumentLinkOption = sendDocumentLinkOption;
	}
	
	public boolean isSendDocumentAttachmentOption() {
		return sendDocumentAttachmentOption;
	}

	public void setSendDocumentAttachmentOption(boolean sendDocumentAttachmentOption) {
		this.sendDocumentAttachmentOption = sendDocumentAttachmentOption;
	}
	
	public boolean isSkinOption() {
		return skinOption;
	}

	public void setSkinOption(boolean skinOption) {
		this.skinOption = skinOption;
	}

	public boolean isDebugOption() {
		return debugOption;
	}

	public void setDebugOption(boolean debugOption) {
		this.debugOption = debugOption;
	}

	public boolean isAdministrationOption() {
		return administrationOption;
	}

	public void setAdministrationOption(boolean administrationOption) {
		this.administrationOption = administrationOption;
	}
	
	public boolean isManageBookmarkOption() {
		return manageBookmarkOption;
	}

	public void setManageBookmarkOption(boolean manageBookmarkOption) {
		this.manageBookmarkOption = manageBookmarkOption;
	}
	
	public boolean isHelpOption() {
		return helpOption;
	}

	public void setHelpOption(boolean helpOption) {
		this.helpOption = helpOption;
	}

	public boolean isDocumentationOption() {
		return documentationOption;
	}

	public void setDocumentationOption(boolean documentationOption) {
		this.documentationOption = documentationOption;
	}

	public boolean isBugReportOption() {
		return bugReportOption;
	}

	public void setBugReportOption(boolean bugReportOption) {
		this.bugReportOption = bugReportOption;
	}

	public boolean isSupportRequestOption() {
		return supportRequestOption;
	}

	public void setSupportRequestOption(boolean supportRequestOption) {
		this.supportRequestOption = supportRequestOption;
	}

	public boolean isPublicForumOption() {
		return publicForumOption;
	}

	public void setPublicForumOption(boolean publicForumOption) {
		this.publicForumOption = publicForumOption;
	}

	public boolean isVersionChangesOption() {
		return versionChangesOption;
	}

	public void setVersionChangesOption(boolean versionChangesOption) {
		this.versionChangesOption = versionChangesOption;
	}

	public boolean isProjectWebOption() {
		return projectWebOption;
	}

	public void setProjectWebOption(boolean projectWebOption) {
		this.projectWebOption = projectWebOption;
	}

	public boolean isAboutOption() {
		return aboutOption;
	}

	public void setAboutOption(boolean aboutOption) {
		this.aboutOption = aboutOption;
	}

	public boolean isLanguagesOption() {
		return languagesOption;
	}

	public void setLanguagesOption(boolean languagesOption) {
		this.languagesOption = languagesOption;
	}

	public boolean isPreferencesOption() {
		return preferencesOption;
	}

	public void setPreferencesOption(boolean preferencesOption) {
		this.preferencesOption = preferencesOption;
	}
}
